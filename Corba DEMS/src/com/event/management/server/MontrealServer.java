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
import com.event.management.implementation.EventManagerMontreal;

/**
 *
 * @author Jenny
 */
public class MontrealServer {
	private static Logger logger;
	String response = "";
	static EventManagerMontreal mtlObject = new EventManagerMontreal();
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
		Thread thread2 = new Thread(montrealResponseTask);

		thread1.start();
		thread2.start();

	}

	void receive() {

	}

	private void receiveMulticastRequest() {


		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(9991);
			aSocket.joinGroup(InetAddress.getByName("230.1.1.5"));

			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);				
				String requestMessage = new String(request.getData());
				Object obj = new JSONParser().parse(requestMessage.trim());
				JSONObject jsonObject = (JSONObject) obj;
				logger.info("Request:" + jsonObject);
				switch (jsonObject.get(Constants.OPERATION).toString()) {
					case "addEventOperation": {
						String managerId = jsonObject.get(Constants.ID).toString();
						String eventId = jsonObject.get(Constants.EVENT_ID).toString();
						String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();
						String eventCapacity = jsonObject.get(Constants.EVENT_CAPACITY).toString();

						response = mtlObject.addEvent(managerId, eventId, eventType, eventCapacity);

						break;
					}
					case "removeEventOperation": {
						String managerId = jsonObject.get(Constants.ID).toString();
						String eventId = jsonObject.get(Constants.EVENT_ID).toString();
						String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();

						response = mtlObject.removeEvent(managerId, eventId, eventType);
						break;
					}
					case "listEventOperation": {
						String managerId = jsonObject.get(Constants.ID).toString();
						String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();

						response = mtlObject.listEventAvailability(managerId,eventType);
						break;
					}
					case "eventBookingOperation": {
						String customerId = jsonObject.get(Constants.ID).toString();
						String eventId = jsonObject.get(Constants.EVENT_ID).toString();
						String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();

						response = mtlObject.eventBooking(customerId, eventId, eventType);
						break;
					}

					case "cancelBookingOperation": {
						String customerId = jsonObject.get(Constants.ID).toString();
						String eventId = jsonObject.get(Constants.EVENT_ID).toString();
						String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();

						response = mtlObject.cancelBooking(customerId, eventId, eventType);
						break;
					}

					case "bookingScheduleOperation": {
						String customerId = jsonObject.get(Constants.ID).toString();

						response = mtlObject.getBookingSchedule(customerId);
						break;
					}
					case "swapEventOperation": {
						String customerId = jsonObject.get(Constants.ID).toString();
						String newEventId = jsonObject.get(Constants.EVENT_ID).toString(); 
						String newEventType = jsonObject.get(Constants.EVENT_TYPE).toString();
						String oldEventId = jsonObject.get(Constants.OLD_EVENT_ID).toString();
						String oldEventType = jsonObject.get(Constants.OLD_EVENT_TYPE).toString();
						
						response = mtlObject.swapEvent(customerId, newEventId, newEventType,oldEventId,oldEventType);
						break;
					}					
					}

					System.out.println("MTL Response: " + response);
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (aSocket != null)
				aSocket.close();
		}	
	}

	private void sendRequestToFrontEnd(String message) {
		DatagramSocket aSocket = null;
		byte[] buffer = new byte[1000];
		try {
			System.out.println("Request from MTL Server sent to Front End!");
			aSocket = new DatagramSocket();
			byte[] m = message.getBytes();
			InetAddress aHost = InetAddress.getByName("192.168.0.107");

			System.out.println("Msg in Bytes: " + m);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, 0110);
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