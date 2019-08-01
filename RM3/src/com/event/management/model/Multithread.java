package com.event.management.model;

import java.util.ArrayList;

class MultithreadingDemo extends Thread {
	MontrealData data;
	String id;
	public MultithreadingDemo(MontrealData data,String id) {
		// TODO Auto-generated constructor stub
		this.data = data;
		this.id= id;
		
	}
	public void run() {
		try {
			// Displaying the thread that is running
			System.out.println("Thread " + Thread.currentThread().getId() + " is running");
			System.out.println(id + " "+data.bookEvent(id, "MTLM123456", "Seminars"));
		} catch (Exception e) {
			// Throwing an exception
			System.out.println("Exception is caught");
		}
	}
}

// Main Class 
public class Multithread {
	private static MontrealData data;

	public static void main(String[] args) {
		data = new MontrealData();
		data.addEvent("MTLM123456", "Seminars", "2");
		System.out.println(data.serverData);
		int n = 3; // Number of threads
		ArrayList<String> list = new ArrayList<String>();
		list.add("OTWC1234");
		list.add("OTWC1235");
		list.add("OTWC1236");
		
		for (int i = 0; i < 3; i++) {
			MultithreadingDemo object = new MultithreadingDemo(data,list.get(i));
			object.start();
			data = object.data;
		}

	}
}