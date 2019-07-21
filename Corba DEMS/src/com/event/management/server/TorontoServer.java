/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.event.management.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.event.management.constants.Constants;
import com.event.management.implementation.EventManagerToronto;

/**
 *
 * @author Jenny
 */
public class TorontoServer {
	private static Logger logger;
	String response = "";
	static EventManagerToronto torObject = new EventManagerToronto();

	public static void main(String args[]) throws Exception {

		TorontoServer torontoServer = new TorontoServer();
		torontoServer.setLogger("logs/TOR.txt", "TOR");
		logger.info("Toronto Server Started");
		System.out.println("Toronto Server started");

		Runnable torontoTask = () -> {
			torontoServer.receive();
		};
		Thread thread = new Thread(torontoTask);
		thread.start();

		Runnable torontoRequestTask = () -> {
			torontoServer.receiveMulticastRequest();
		};

		Runnable torontoResponseTask = () -> {
			receiveFailedResponse();
		};
		Thread thread1 = new Thread(torontoRequestTask);
		Thread thread2 = new Thread(torontoResponseTask);

		thread1.start();
		thread2.start();

		System.out.println("Toronto Server Exiting ...");
	}

	void receive() {

	}

	private void receiveMulticastRequest() {

		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(1313);
			aSocket.joinGroup(InetAddress.getByName("230.1.2.5"));

			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				logger.info("Request:" + new String(request.getData()));
				String[] requestString = new String(request.getData()).trim().split("\\|+");

				switch (requestString[0]) {
					case Constants.ADD_OPERATION: {
						String managerId = requestString[1];
						String eventId = requestString[2];
						String eventType = requestString[3];
						String eventCapacity = requestString[4];

						response = torObject.addEvent(managerId, eventId, eventType, eventCapacity);

						break;
					}
					case Constants.REMOVE_OPERATION: {
						String managerId = requestString[1];
						String eventId = requestString[2];
						String eventType = requestString[3];

						response = torObject.removeEvent(managerId, eventId, eventType);
						break;
					}
					case Constants.LIST_OPERATION: {
						String managerId = requestString[1];
						String eventType = requestString[2];

						response = torObject.listEventAvailability(managerId,eventType);
						break;
					}
					case Constants.BOOK_OPERATION: {
						String customerId = requestString[1];
						String eventId = requestString[2];
						String eventType = requestString[3];

						response = torObject.eventBooking(customerId, eventId, eventType);
						break;
					}

					case Constants.CANCEL_OPERATION: {
						String customerId = requestString[1];
						String eventId = requestString[2];
						String eventType = requestString[3];

						response = torObject.cancelBooking(customerId, eventId, eventType);
						break;
					}

					case Constants.SCHEDULE_OPERATION: {
						String customerId = requestString[1];

						response = torObject.getBookingSchedule(customerId);
						break;
					}
					case Constants.SWAP_OPERATION: {
						String customerId = requestString[1];
						String newEventId = requestString[2];
						String newEventType = requestString[3];
						String oldEventId = requestString[4];
						String oldEventType = requestString[5];
						response = torObject.swapEvent(customerId, newEventId, newEventType,oldEventId,oldEventType);
						break;
					}					
					}

					System.out.println("Response: " + response);
					sendRequestToFrontEnd(response);
				
				/*
				 * DatagramPacket reply = new DatagramPacket(request.getData(),
				 * request.getLength(), request.getAddress(), request.getPort());
				 * aSocket.send(reply);
				 */
			}

		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	
	}

	private void sendRequestToFrontEnd(String message) {
		DatagramSocket aSocket = null;
		byte[] buffer = new byte[1000];
		try {
			System.out.println("Request sent to Front End!");
			aSocket = new DatagramSocket();
			byte[] m = message.getBytes();
			InetAddress aHost = InetAddress.getByName("192.168.0.107");

			System.out.println("Msg in Bytes: " + m);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, 1113);
			aSocket.send(request);

			/*
			 * DatagramPacket response = new DatagramPacket(buffer, buffer.length);
			 * aSocket.receive(response);
			 */

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void receiveFailedResponse() {
		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(6467);
			aSocket.joinGroup(InetAddress.getByName("230.2.2.5"));

			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				System.out.println("FrontEnd Response: " + new String(request.getData()));

				// TODO handle failure or wrong response
			}
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
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