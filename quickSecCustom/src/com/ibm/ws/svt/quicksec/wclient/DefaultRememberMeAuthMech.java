

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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.authentication.mechanism.http.RememberMe;

@ApplicationScoped
//JREMEMBERID cookie was not getting created when usiing http
//@RememberMe(cookieSecureOnly=false)
@RememberMe(cookieMaxAgeSeconds=-1)
public class DefaultRememberMeAuthMech extends BaseAuthMech {

    public DefaultRememberMeAuthMech() {
    		System.out.println("Inside the DefaultRememberMeAuthMech ");
        sourceClass = DefaultRememberMeAuthMech.class.getName();
    }

}
