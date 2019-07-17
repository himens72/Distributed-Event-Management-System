/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.event.management.server;

/**
 *
 * @author Jenny
 */
public class MontrealServer {

	public static void main(String args[]) throws Exception {

		MontrealServer montrealServer = new MontrealServer();

		System.out.println("Montreal Server started");

		Runnable task = () -> {
			montrealServer.receive();
		};
		Thread thread = new Thread(task);
		thread.start();

		Runnable task1 = () -> {
			montrealServer.receiveMulticastRequest();
		};

		Runnable task2 = () -> {
			receiveFailedResponse();
		};
		Thread thread1 = new Thread(task1);
		Thread thread2 = new Thread(task2);

		thread1.start();
		thread2.start();

		System.out.println("Montreal Server Exiting ...");
	}

	void receive() {

	}

	private void receiveMulticastRequest() {

	}

	private void sendRequestToFrontEnd(String message) {

	}

	private static void receiveFailedResponse() {

	}
}