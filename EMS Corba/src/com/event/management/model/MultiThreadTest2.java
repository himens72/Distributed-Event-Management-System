package com.event.management.model;

import java.util.ArrayList;

import com.event.management.implementation.EventManagerClient;

class Multithreading extends Thread {
	TorontoData data;
	String id;
	String eventId;
	String eventType;
	public Multithreading(TorontoData data,String id, String eventId, String eventType) {
		// TODO Auto-generated constructor stub
		this.data = data;
		this.id= id;
		this.eventId = eventId;
		this.eventType = eventType;
		
	}
	public void run() {
		try {
			// Displaying the thread that is running
			System.out.println("Thread " + Thread.currentThread().getId() + " is running");
			EventManagementTestCases mc = new EventManagementTestCases();
			mc.torontoData = data;
			System.out.println(id +" ---> " + mc.swapEvent(id, eventId, eventType, "TORA070619", "Seminars"));
			data = mc.torontoData;
		} catch (Exception e) {
			// Throwing an exception
			System.out.println("Exception is caught");
		}
	}
}

// Main Class 
public class MultiThreadTest2 {
	private static TorontoData data;

	public static void main(String[] args) {
		data = new TorontoData();
		data.addEvent("TORM080621", "Trade Shows", "1");
		data.addEvent("TORA070619", "Seminars", "2");
		System.out.println(data.serverData);
		int n = 3; // Number of threads
		ArrayList<String> list = new ArrayList<String>();
		list.add("OTWC1234");
		list.add("OTWC1235");
		data.bookEvent("OTWC1234", "TORA070619", "Seminars");
		data.bookEvent("OTWC1235", "TORA070619", "Seminars");
		for (int i = 0; i < 2; i++) {
			Multithreading object = new Multithreading(data,list.get(i),"TORM080621", "Trade Shows");
			object.start();
			data = object.data;
		}

	}
}


class EventManagerTest {
	
}