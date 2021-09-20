package com.ibm.ws.svt.quicksec.EJB;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity

public class MessengerEnt implements Serializable {
		
		
		private static final long serialVersionUID = 1L;
		
	    @Id
	    private String userId;
	    private int userCount;
	    private Boolean isManager;
	    
	    	    
	    public void setuserId(String userId){
	    	this.userId=userId;
	    }
	    public String getuserId(){
	    	return this.userId;
	    }
	    
	    public void setuserCount(int userCount){
	    	this.userCount=userCount;
	    }
	    public int getuserCount(){
	    	return this.userCount;
	    }
	    
	    public void setisManager(Boolean isManager){
	    	this.isManager=isManager;
	    }
	    public Boolean getisManager() {
	    	return this.isManager;
	    }
	    
}


