/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.event.management.server;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Jenny
 */
public class MontrealServer {
	private static Logger logger;

	public static void main(String args[]) throws Exception {

		MontrealServer montrealServer = new MontrealServer();
		montrealServer.setLogger("logs/MTL.txt", "MTL");
		logger.info("Montreal Server Started");
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

	}

	void receive() {

	}

	private void receiveMulticastRequest() {

	}

	private void sendRequestToFrontEnd(String message) {

	}

	private static void receiveFailedResponse() {

	}

	private void setLogger(String location, String id) {
		try {
			logger = Logger.getLogger(id);
			FileHandler fileTxt = new FileHandler(location, true);
			SimpleFormatter formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			logger.addHandler(fileTxt);
		} catch (Exception err) {
			logger.info("Couldn't Initiate Logger. Please check file permission");
		}
	}
}