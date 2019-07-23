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
	}

	public void receive() {
		DatagramSocket datagramSocket = null;
		while (true) {
			try {
				datagramSocket = new DatagramSocket(8990);
				byte[] receive = new byte[65535];
				DatagramPacket packetReceive = new DatagramPacket(receive, receive.length);
				datagramSocket.receive(packetReceive);
				byte[] data = packetReceive.getData();
				String[] receiveData = new String(data).split(",");
				logger.info("Receive Data : " + new String(data));
				logger.info("Operation Performed " + receiveData[receiveData.length - 1].trim());
				if (receiveData[receiveData.length - 1].trim().equals(Constants.LIST_OPERATION)) {
					String temp = torObject.torontoData.retrieveEvent(receiveData[2]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("addOperation")) {
					boolean temp = torObject.torontoData.addEvent(receiveData[1], receiveData[2], receiveData[3]);
					String newTemp = temp == false ? "Denies" : "Approves";
					logger.info("Reply send to customer : " + newTemp);
					DatagramPacket reply = new DatagramPacket(newTemp.getBytes(), newTemp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("bookOperation")) {
					String temp = torObject.torontoData.bookEvent(receiveData[0], receiveData[1], receiveData[2]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("cancelOperation")) {
					String temp = torObject.torontoData.removeEvent(receiveData[0], receiveData[1], receiveData[2]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("scheduleOperation")) {
					String temp = torObject.torontoData.getBookingSchedule(receiveData[0]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("countOperation")) {
					String temp = torObject.torontoData.getBookingCount(receiveData[0], receiveData[1]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("existanceOperation")) {
					boolean temp = torObject.torontoData.getEvent(receiveData[0], receiveData[1], receiveData[2]);
					logger.info("Reply send to customer : " + temp);
					String newTemp = temp == false ? "Denies" : "Approves";
					DatagramPacket reply = new DatagramPacket(newTemp.getBytes(), newTemp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else {
					logger.info("Some problem in Server");
				}

				receive = new byte[65535];
				data = new byte[65535];

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				datagramSocket.close();
			}

		}
	}

	private void receiveMulticastRequest() {

		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(9990);
			aSocket.joinGroup(InetAddress.getByName("230.0.0.0"));

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

					response = torObject.addEvent(managerId, eventId, eventType, eventCapacity);

					break;
				}
				case "removeEventOperation": {
					String managerId = jsonObject.get(Constants.ID).toString();
					String eventId = jsonObject.get(Constants.EVENT_ID).toString();
					String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();

					response = torObject.removeEvent(managerId, eventId, eventType);
					break;
				}
				case "listEventOperation": {
					String managerId = jsonObject.get(Constants.ID).toString();
					String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();

					response = torObject.listEventAvailability(managerId, eventType);
					break;
				}
				case "eventBookingOperation": {
					String customerId = jsonObject.get(Constants.ID).toString();
					String eventId = jsonObject.get(Constants.EVENT_ID).toString();
					String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();

					response = torObject.eventBooking(customerId, eventId, eventType);
					break;
				}

				case "cancelBookingOperation": {
					String customerId = jsonObject.get(Constants.ID).toString();
					String eventId = jsonObject.get(Constants.EVENT_ID).toString();
					String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();

					response = torObject.cancelBooking(customerId, eventId, eventType);
					break;
				}

				case "bookingScheduleOperation": {
					String customerId = jsonObject.get(Constants.ID).toString();

					response = torObject.getBookingSchedule(customerId);
					break;
				}
				case "swapEventOperation": {
					String customerId = jsonObject.get(Constants.ID).toString();
					String newEventId = jsonObject.get(Constants.EVENT_ID).toString();
					String newEventType = jsonObject.get(Constants.EVENT_TYPE).toString();
					String oldEventId = jsonObject.get(Constants.OLD_EVENT_ID).toString();
					String oldEventType = jsonObject.get(Constants.OLD_EVENT_TYPE).toString();

					response = torObject.swapEvent(customerId, newEventId, newEventType, oldEventId, oldEventType);
					break;
				}
				}

				System.out.println("TOR Response: " + response);
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
			System.out.println("Request from TOR Server sent to Front End!");
			aSocket = new DatagramSocket();
			byte[] m = message.getBytes();
			InetAddress aHost = InetAddress.getByName("192.168.0.156");

			System.out.println("Msg in Bytes: " + m);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, 1110);
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