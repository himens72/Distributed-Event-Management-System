package com.event.management.implementation;

import org.omg.CORBA.ORB;

import com.event.management.server.Montreal;
import com.event.management.server.Ottawa;
import com.event.management.server.Toronto;

import EventManagement.managerInterfacePOA;

public class EventManagerClient extends managerInterfacePOA {
	public String location;
    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public EventManagerClient(String location) {
		super();
		//serverData = new eventData();
		this.location = location;
		//setLogger("logs/" + location + ".txt", location);

		if (location.equals("TOR")) {

			Toronto toronto = new Toronto(this);

			Runnable task1 = () -> {
				toronto.serverConnection(9990);

			};
			Thread thread1 = new Thread(task1);
			thread1.start();
		} else if (location.equals("MTL")) {
			Runnable task2 = () -> {
				Montreal montreal = new Montreal(this);
				montreal.serverConnection(9991);
			};
			Thread thread2 = new Thread(task2);
			thread2.start();
		} else if (location.equals("OTW")) {
			Ottawa ottawa = new Ottawa(this);
			Runnable task3 = () -> {
				ottawa.serverConnection(9992);
			};
			Thread thread3 = new Thread(task3);
			thread3.start();
		} else {

			System.out.println("Server not started");

		}

	}    
   
    
	@Override
	public String addEvent(String managerId, String eventId, String eventType, String eventCapacity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String removeEvent(String managerId, String eventId, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String listEventAvailability(String managerId, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String eventBooking(String customerId, String eventId, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cancelBooking(String customerId, String eventId, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBookingSchedule(String customerId) {
		// TODO Auto-generated method stub
		return null;
	}
	 @Override
		public String passMsg(String msg) {
	    	String output= "";
	    	System.out.println("herrrrrrre");
			if(msg.trim().equals("Toronto")) {
				output ="Hello" + msg;
			}
			else if(msg.trim().equals("Montreal")) {
				output ="Bonjour" + msg;
			}
			else if(msg.trim().equals("Ottawa")) {
				output ="Hiiiiii" + msg;
			}		
			return output;
		}
}
