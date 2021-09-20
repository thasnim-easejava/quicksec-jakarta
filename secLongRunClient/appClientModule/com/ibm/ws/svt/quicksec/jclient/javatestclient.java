package com.ibm.ws.svt.quicksec.jclient;


import java.io.FileInputStream;

import jakarta.ejb.EJB;

import com.ibm.ws.svt.quicksec.EJB.Delegate;
import com.ibm.ws.svt.quicksec.jclient.*;

public class javatestclient {

   
   private static String shost, mhost = null;
   private static String loginmodule=null;
   private static int threads = 0;
   private static String hits    = null;
   private static String realm   = null;
   private static String lookup  = null;
   private static int maxUsers  = 0;
   private static int startUserNumber = 1;
   private static String uidSuffix = "user";
   private static String pwdSuffix = "puser";
   //private static String OneLineOut = "no";
   private static int sleeptime  = 0;
   private static boolean  debug = false; 
   

   private static int numThreads, inActiveClients = 0;

   //private static long numberOfSOAPCallsCompleted = 0;
   //private static long lastNumberOfSOAPCallsCompleted = 0;

   private static String uid = null;
   private static String pwd = null;
   
   public static long succCounts = 0;
  
    /**
   * The main routine called from the command-line.
     fo args, see usage in main
   */
   public static void main ( String args[] ) {

     System.out.println( "**** WAS SVT security test case: QuickSec java client stress run ****" );
     if (args.length != 11)
     {
        System.err.println("Incorrect number of parameters are passed. Correct usage: client run client1 -- <Host:port of sessionEJB> <Host:port of messengerEJB> <Login Module: WSLogin> <noOfThreads> <Hits/Thread> <duration in Hrs> <Start User Number> <maxUsers> <UserID Suffix> <Password Suffix> <sleeptime>");
        System.exit(1);
     }
    
    shost = args[0];
    mhost = args[1];
    loginmodule=args[2];
    threads = Integer.parseInt(args[3]);
    hits = args[4];
//	Durtion is [5] but no variable defined as we can read it only once 
	startUserNumber = Integer.parseInt(args[6]);
    maxUsers = Integer.parseInt(args[7]);
    uidSuffix = args[8];
    pwdSuffix = args[9];
	sleeptime = Integer.parseInt(args[10]);
    
    //OneLineOut = args[11];
	realm = "none";
    lookup = "no";
    
    System.out.println("Session Host        = "+args[0]);
    System.out.println("Messenger Host      = "+args[1]);
    System.out.println("Login Module        = "+args[2]);
    System.out.println("Number of Threads   = "+args[3]);
    System.out.println("Hits/Thread         = "+args[4]);
    System.out.println("Duration            = "+args[5]);
    System.out.println("Start User Number   = "+args[6]);
	System.out.println("maxUsers            = "+args[7]);
    System.out.println("Uid Suffix          = "+args[8]);
    System.out.println("Password Suffix     = "+args[9]);
	System.out.println("sleep time  		= "+args[10]);
    
    //System.out.println("One Line Out      = "+args[11]);



    //int tempInc = 1;
             
    long startTime = System.currentTimeMillis();
    long timeRan=0;
    double duration = Double.parseDouble(args[5])*60*60*1000;
    
    //ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
   // int IniNumOfThreads = currentGroup.activeCount();
    //System.out.println("Number Of Active Threads at begining: " + IniNumOfThreads);

    
    
     //ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
     //System.out.println(currentGroup.activeCount());
   int attempts = 0;
   try {
         
    while (duration > timeRan) {
        //System.out.println("OK");
        long endTime = System.currentTimeMillis();
        timeRan=(endTime-startTime);

        ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
        ClientThread clnt[] = new ClientThread[threads];
        numThreads = currentGroup.activeCount();
       
        //inActiveClients= (threads + IniNumOfThreads) - numThreads;
        System.out.println("Total Curent threads: "+ numThreads + "    Starting App Req Threads: " + threads);

  		int startNumberHolder = startUserNumber;
        for (int i=0;i<threads;i++) {
	    uid = uidSuffix + startNumberHolder;
        pwd = pwdSuffix + startNumberHolder;
	    System.out.println("uid :" + uid + "........pwd :" + pwd);
        clnt[i] = new ClientThread(uid, pwd, shost, mhost, loginmodule, realm, lookup, hits, i, sleeptime );
        clnt[i].setName(uid);

        if (startNumberHolder > maxUsers+1) {
			startNumberHolder = startUserNumber;
            
        }else {
			startNumberHolder = startNumberHolder+1;
                }
        } //end for loop

        for (int i=0;i<threads;i++) {
            clnt[i].start();
            attempts++;
			try {
				Thread.sleep(sleeptime);
				}catch(InterruptedException interruptedexception){
				interruptedexception.printStackTrace();
				}
        }//end for loop




        Thread.sleep(3*60*1000);

        
        //System.out.println("DONE=== inactiveClients: "+inActiveClients);

    }//end while loop
    

     }catch(InterruptedException interruptedexception)
        {
         interruptedexception.printStackTrace();
     }
                   
     long tmp = attempts*Integer.parseInt(hits);
     System.out.println("Total Attemps: "+ tmp + "    Total succeed: " + succCounts);
    }
   
   synchronized public static void addSuccCount(){
	   {
		   succCounts++ ;
	   }
 
     }
}
