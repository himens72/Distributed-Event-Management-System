/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.event.management.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.event.management.constants.Constants;

/**
 *
 * @author Jenny
 */
public class MontrealServer {
	private static Logger logger;
	String response = "";

	public static void main(String args[]) throws Exception {

		MontrealServer montrealServer = new MontrealServer();
		montrealServer.setLogger("logs/MTL.txt", "MTL");
		logger.info("Montreal Server Started");
		Runnable montrealTask = () -> {
			montrealServer.receive();
		};
		Thread thread = new Thread(montrealTask);
		thread.start();

		Runnable montrealRequestTask = () -> {
			montrealServer.receiveMulticastRequest();
		};

		Runnable montrealResponseTask = () -> {
			receiveFailedResponse();
		};
		Thread thread1 = new Thread(montrealRequestTask);
		Thread thread2 = new Thread(montrealRequestTask);

		thread1.start();
		thread2.start();

	}

	void receive() {

	}

	private void receiveMulticastRequest() {}

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