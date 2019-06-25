package com.event.management.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import com.event.management.implementation.EventManagerClient;

import EventManagement.managerInterface;
import EventManagement.managerInterfaceHelper;

class CustomerClient{
	
	static BufferedReader br;
	      
public static void main(String[] args) {
       System.out.println("Welcome!!");
       String output ="";
       br = new BufferedReader(new InputStreamReader(System.in));
       EventManagerClient managerObj = null;
       
	try {
		ORB orb = ORB.init(args, null);
		// -ORBInitialPort 1050 -ORBInitialHost localhost
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        managerInterface mInterface = null;
	    
        System.out.println("Enter msg");
        String msg = br.readLine().trim();
        System.out.println("Check " + msg);
        System.out.println("Check " + msg.trim().equals("Toronto"));
		
		
        if(msg.trim().equals("Toronto")){ 
        	System.out.println("ithhhhhhhhe");
        	mInterface =  managerInterfaceHelper.narrow(ncRef.resolve_str("TOR"));
        	output = mInterface.passMsg(msg);
        	System.out.println("Wlcome Message: " + output);
        	
        }
        
        else if(msg == "Montreal"){      
        	mInterface =  managerInterfaceHelper.narrow(ncRef.resolve_str("MTL"));
        	output = mInterface.passMsg(msg);
        	System.out.println("Wlcome Message: " + output);
        } 
        
        else if(msg == "Ottawa"){      
        	mInterface =  managerInterfaceHelper.narrow(ncRef.resolve_str("OTW")); 
        	output = mInterface.passMsg(msg);
        	System.out.println("Wlcome Message: " + output);
      }       
      
   
}
                   
	catch (Exception e) {
		System.out.println("Hello Client exception: " + e);
		e.printStackTrace();
	}
}
}