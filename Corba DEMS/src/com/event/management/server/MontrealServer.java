/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.event.management.server;

import java.io.FileWriter;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

	public void receive() {
		DatagramSocket datagramSocket = null;
		while (true) {
			try {
				datagramSocket = new DatagramSocket(Constants.LOCAL_MONTREAL_PORT);
				byte[] receive = new byte[Constants.BYTE_LENGTH];
				DatagramPacket packetReceive = new DatagramPacket(receive, receive.length);
				datagramSocket.receive(packetReceive);
				byte[] data = packetReceive.getData();
				String[] receiveData = new String(data).split(",");
				logger.info("Receive Data : " + new String(data));
				logger.info("Operation Performed " + receiveData[receiveData.length - 1].trim());
				if (receiveData[receiveData.length - 1].trim().equals(Constants.LIST_OPERATION)) {
					String temp = mtlObject.montrealData.retrieveEvent(receiveData[2]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("addOperation")) {
					boolean temp = mtlObject.montrealData.addEvent(receiveData[1], receiveData[2], receiveData[3]);
					String newTemp = temp == false ? "Denies" : "Approves";
					logger.info("Reply send to customer : " + newTemp);
					DatagramPacket reply = new DatagramPacket(newTemp.getBytes(), newTemp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals(Constants.BOOK_OPERATION)) {
					String temp = generateJSONObject(receiveData[0], receiveData[1], receiveData[2], "None", "None",
							"None", Constants.BOOK_OPERATION,
							mtlObject.montrealData.bookEvent(receiveData[0], receiveData[1], receiveData[2]));
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals(Constants.CANCEL_OPERATION)) {
					String temp = generateJSONObject(receiveData[0], receiveData[1], receiveData[2], "None", "None",
							"None", Constants.CANCEL_OPERATION,
							mtlObject.montrealData.removeEvent(receiveData[0], receiveData[1], receiveData[2]));
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals(Constants.SCHEDULE_OPERATION)) {
					String temp = mtlObject.montrealData.getBookingSchedule(receiveData[0]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("countOperation")) {
					String temp = mtlObject.montrealData.getBookingCount(receiveData[0], receiveData[1]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("existanceOperation")) {
					boolean temp = mtlObject.montrealData.getEvent(receiveData[0], receiveData[1], receiveData[2]);
					logger.info("Reply send to customer : " + temp);
					String newTemp = temp == false ? "Denies" : "Approves";
					DatagramPacket reply = new DatagramPacket(newTemp.getBytes(), newTemp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else {
					logger.info("Some problem in Server");
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				datagramSocket.close();
			}
		}
	}
	private void receiveMulticastRequest() {

		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(Constants.RM_MONTREAL_PORT);
			aSocket.joinGroup(InetAddress.getByName(Constants.MULTICAST_IP));
			while (true) {
				byte[] buffer = new byte[Constants.BYTE_LENGTH];
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
					response = mtlObject.listEventAvailability(managerId, eventType);
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

				case Constants.SCHEDULE_OPERATION: {
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
					response = mtlObject.swapEvent(customerId, newEventId, newEventType, oldEventId, oldEventType);
					break;
				}
				}
				updateJSONFile();
				System.out.println("MTL Response: " + response);
				sendRequestToFrontEnd(response);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

	private void sendRequestToFrontEnd(String message) {
		DatagramSocket aSocket = null;
		try {
			System.out.println("Request from MTL Server sent to Front End!");
			aSocket = new DatagramSocket();
			byte[] m = message.getBytes();
			InetAddress aHost = InetAddress.getByName(Constants.FRONTEND_IP);
			System.out.println("Msg in Bytes: " + m);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, Constants.RM_FRONTEND_PORT);
			aSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void receiveFailedResponse() {
		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(Constants.FAULT_PORT);
			aSocket.joinGroup(InetAddress.getByName(Constants.FAULT_MULTICAST_IP));
			while (true) {
				byte[] buffer = new byte[Constants.BYTE_LENGTH];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				System.out.println("FrontEnd Response: " + new String(request.getData()));
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

	static String generateJSONObject(String id, String eventId, String eventType, String eventCapacity,
			String oldEventId, String oldEventType, String operation, boolean status) {
		JSONObject obj = new JSONObject();
		obj.put(Constants.ID, id.trim());
		obj.put(Constants.EVENT_ID, eventId.trim());
		obj.put(Constants.EVENT_TYPE, eventType.trim());
		obj.put(Constants.EVENT_CAPACITY, eventCapacity.trim());
		obj.put(Constants.OLD_EVENT_ID, oldEventId.trim());
		obj.put(Constants.OLD_EVENT_TYPE, oldEventType.trim());
		obj.put(Constants.OPERATION, operation.trim());
		obj.put(Constants.OPERATION_STATUS, status);
		return obj.toString();
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
	public static void updateJSONFile(){
		JSONObject game = new JSONObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String playerString = gson.toJson(mtlObject.montrealData);

		game.put("player", playerString);
		String gameString = gson.toJson(game);

		try {
			FileWriter fileWriter = new FileWriter("montreal.json");
			fileWriter.write(gameString);
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}