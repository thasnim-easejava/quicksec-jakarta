

/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.svt.quicksec.wclient;

import java.util.Base64;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import javax.security.auth.Subject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.BasicAuthenticationCredential;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BaseAuthMech implements HttpAuthenticationMechanism {

    protected static String sourceClass = BaseAuthMech.class.getName();
    private final Logger logger = Logger.getLogger(sourceClass);

    private final String realmName = "Servlet10Realm";

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    private final boolean rememberMe = true;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
                                                HttpServletResponse response,
                                                HttpMessageContext httpMessageContext) throws AuthenticationException {
        logger.entering(sourceClass, "validateRequest", new Object[] { request, response, httpMessageContext });
        AuthenticationStatus status = AuthenticationStatus.SEND_FAILURE;

        Subject clientSubject = httpMessageContext.getClientSubject();
        String authHeader = httpMessageContext.getRequest().getHeader("Authorization");

        if (httpMessageContext.isAuthenticationRequest()) {
            AuthenticationParameters authParams = httpMessageContext.getAuthParameters();

            if (authParams != null) {
                Credential credential = authParams.getCredential();
                int rspStatus = HttpServletResponse.SC_FORBIDDEN;
                status = validateWithIdentityStore(clientSubject, credential, identityStoreHandler, httpMessageContext);
                if (status == AuthenticationStatus.SUCCESS) {
                    httpMessageContext.getMessageInfo().getMap().put("javax.servlet.http.authType", "SERVLET10_AUTH_MECH");
                    rspStatus = HttpServletResponse.SC_OK;
                }
                httpMessageContext.getResponse().setStatus(rspStatus);
            } else {
                if (authHeader == null) {
                    status = setChallengeAuthorizationHeader(httpMessageContext.getResponse());
                } else {
                    status = handleAuthorizationHeader(authHeader, clientSubject, httpMessageContext);
                }
            }
        } else {
            if (authHeader == null) {
                if (httpMessageContext.isProtected() == false) {
                    status = AuthenticationStatus.NOT_DONE;
                } else {
                    status = setChallengeAuthorizationHeader(httpMessageContext.getResponse());
                }
            } else {
                status = handleAuthorizationHeader(authHeader, clientSubject, httpMessageContext);
            }
        }
        System.out.println("I am in validateRequest"+ status);
        logger.exiting(sourceClass, "validateRequest", status);
        return status;
    }

    private AuthenticationStatus setChallengeAuthorizationHeader(HttpServletResponse rsp) {
        rsp.setHeader("WWW-Authenticate", "Basic realm=\"" + realmName + "\"");
        rsp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        System.out.println("I am in setChallengeAuthorizationHeader");
        return AuthenticationStatus.SEND_CONTINUE;
    }

    @SuppressWarnings("unchecked")
    private AuthenticationStatus handleAuthorizationHeader(String authHeader, Subject clientSubject, HttpMessageContext httpMessageContext) throws AuthenticationException {
        AuthenticationStatus status = AuthenticationStatus.SEND_FAILURE;
        int rspStatus = HttpServletResponse.SC_FORBIDDEN;
        if (authHeader.startsWith("Basic ")) {
            String encodedHeader = authHeader.substring(6);
            String basicAuthHeader = decodeCookieString(encodedHeader);

            if (isAuthorizationHeaderValid(basicAuthHeader)) { // BasicAuthenticationCredential.isValid does not work
                BasicAuthenticationCredential basicAuthCredential = new BasicAuthenticationCredential(encodedHeader);
                status = validateWithIdentityStore(clientSubject, basicAuthCredential, identityStoreHandler, httpMessageContext);
                if (status == AuthenticationStatus.SUCCESS) {
                    httpMessageContext.getMessageInfo().getMap().put("javax.servlet.http.authType", "SERVLET10_AUTH_MECH");
                    rspStatus = HttpServletResponse.SC_OK;
                }
            } else {
                logger.info("Basic Auth header is not valid.");
            }
        }
        httpMessageContext.getResponse().setStatus(rspStatus);
        return status;
    }

    private String decodeCookieString(String cookieString) {
        try {
            return new String(Base64.getDecoder().decode(cookieString));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isAuthorizationHeaderValid(String basicAuthHeader) {
        int index = -1;
        boolean isNotValid = basicAuthHeader == null || basicAuthHeader.isEmpty() || (index = basicAuthHeader.indexOf(':')) <= 0 || index == basicAuthHeader.length() - 1;
        return !isNotValid;
    }

//    private AuthenticationStatus authenticate(Subject clientSubject, Credential credential, HttpMessageContext httpMessageContext) {
//        AuthenticationStatus status = validateWithIdentityStore(clientSubject, basicAuthCredential, identityStoreHandler, httpMessageContext);
//        if (status == AuthenticationStatus.SUCCESS) {
//            httpMessageContext.getMessageInfo().getMap().put("javax.servlet.http.authType", "SERVLET10_AUTH_MECH");
//            rspStatus = HttpServletResponse.SC_OK;
//        }
//    }

    private AuthenticationStatus validateWithIdentityStore(Subject clientSubject, Credential credential, IdentityStoreHandler identityStoreHandler,
                                                           HttpMessageContext httpMessageContext) {
        logger.entering(sourceClass, "validateWithIdentityStore", new Object[] { clientSubject, credential, httpMessageContext });
        AuthenticationStatus status = AuthenticationStatus.SEND_FAILURE;

        CredentialValidationResult result = identityStoreHandler.validate(credential);
        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            status = httpMessageContext.notifyContainerAboutLogin(result);
        } else if (result.getStatus() == CredentialValidationResult.Status.NOT_VALIDATED) {
            status = AuthenticationStatus.NOT_DONE;
        }

        logger.exiting(sourceClass, "validateWithIdentityStore", status);
        return status;
    }

    public boolean isRememberMe() {
        logger.entering(sourceClass, "isRememberMe");
        logger.exiting(sourceClass, "isRememberMe", rememberMe);
        return rememberMe;
    }

}
