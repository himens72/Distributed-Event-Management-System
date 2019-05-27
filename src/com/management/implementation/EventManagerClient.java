/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.implementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.management.interfaceDef.managerInterface;
import com.management.model.eventData;
import com.management.server.Montreal;
import com.management.server.Ottawa;
import com.management.server.Toronto;

/**
 *
 * @author Himen Sidhpura
 */
public class EventManagerClient extends UnicastRemoteObject implements managerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4262636697845867549L;
	public String location;
	public String response;
	public eventData serverData;
	private static Logger logger;

	public eventData getServerData() {
		return serverData;
	}

	public void setServerData(eventData serverData) {
		this.serverData = serverData;
	}

	public EventManagerClient(String location) throws RemoteException {
		super();
		serverData = new eventData();
		this.location = location;
		setLogger("logs/" + location + ".txt", location);

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
	public String addEvent(String managerId, String eventId, String eventtype, String eventCapacity)
			throws IOException {
		if (eventtype.equals("Seminars") || eventtype.equals("Conferences") || eventtype.equals("Trade Shows")) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				String output = serverData.addEvent(eventId, eventtype, eventCapacity);
				
				return output;
			} else {
				return "Please Enter Proper Event Id";
			}
		} else {
			return "Please Enter proper event type";
		}
	}

	@Override
	public String removeEvent(String managerId, String eventId, String eventtype) throws IOException {

		if (eventtype.equals("Seminars") || eventtype.equals("Conferences") || eventtype.equals("Trade Shows")) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				System.out.println("Before Removed " + serverData.getServerData());
				String output = serverData.removeEvent(eventId, eventtype);
				System.out.println("After Removed " + serverData.getServerData());
				return output.trim();
			} else {
				return "Please Enter Proper Event Id";
			}
		} else {
			return "Please Enter proper event type";
		}

	}

	@Override
	public String listEventAvailability(String managerId, String eventType) throws IOException, InterruptedException {

		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			String temp = serverData.retrieveEvent(eventType).trim();
			if (managerId.substring(0, 3).trim().equals("TOR")) {
				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9991, "listOperation")
						.trim();
				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9992,
						"listOperation").trim();
				return temp.trim() == "" ? "No Events Available" : temp.trim();
			} else if (managerId.substring(0, 3).trim().equals("MTL")) {
				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9990, "listOperation")
						.trim();
				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9992,
						"listOperation").trim();
				return temp.trim() == "" ? "No Events Available" : temp.trim();
			} else if (managerId.substring(0, 3).trim().equals("OTW")) {
				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9990, "listOperation")
						.trim();
				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9991,
						"listOperation").trim();
				return temp.trim() == "" ? "No Events Available" : temp.trim();
			}
			return temp.trim() == "" ? "No Events Available" : temp.trim();

		} else {
			return "Please enter Event type properly";
		}
	}

	public String requestOnOtherServer(String managerId, String eventId, String eventType, String eventCapacity,
			int port, String operation) {

		DatagramSocket datagramSocket = null;
		try {
			String requestData = managerId + "," + eventId + "," + eventType + "," + eventCapacity + "," + operation;
			datagramSocket = new DatagramSocket();
			DatagramPacket packetSend = new DatagramPacket(requestData.getBytes(), requestData.getBytes().length,
					InetAddress.getByName("localhost"), port);
			datagramSocket.send(packetSend);
			byte[] buffer = new byte[65535];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			datagramSocket.receive(reply);
			String response = new String(reply.getData());
			return response.trim();
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
		} catch (SocketException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		} finally {
			datagramSocket.close();
		}
		return "";
	}

	@Override
	public String eventBooking(String customerId, String eventId, String eventType) throws IOException {
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			StringBuilder count = new StringBuilder();
			if (!customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				if (customerId.trim().substring(0, 3).equals("TOR")) {
					count.append(
							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")
							+ ",");
					count.append(
							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")
							+ ",");
				} else if (customerId.trim().substring(0, 3).equals("MTL")) {
					count.append(
							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")
							+ ",");
					count.append(
							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")
							+ ",");
				} else if (customerId.trim().substring(0, 3).equals("OTW")) {
					count.append(
							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")
							+ ",");
					count.append(
							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")
							+ ",");
				}
				String[] split = count.toString().trim().split(",");
				int totalEve = 0;
				for (int i = 0; i < split.length; i++) {
					totalEve += Integer.parseInt(split[i].trim());
				}
				if (totalEve >= 3) {
					return "you have already reached maximum limit of Current Month Outside city registration ";
				}
			}

			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				String temp = serverData.bookEvent(customerId, eventId, eventType);
				return temp == "" ? "Unable to Book  Event" : temp.trim();
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp.trim();// + " -- > " + count;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
						"bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp.trim();// + " -- > " + count;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
						"bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp.trim();// + " -- > " + count;

			} else {
				return "Please Enter Event ID Properly";
			}

		} else {
			return "Please enter Event type properly";
		}
	}

	@Override
	public String cancelBooking(String customerId, String eventId, String eventType) throws IOException {

		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				String temp = serverData.removeEvent(customerId, eventId, eventType);
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"cancelOperation");
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
						"cancelOperation");
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
						"cancelOperation");
				return temp == "" ? "Unable to Cancel  Event" : temp;

			} else {
				return "Please Enter Event ID Properly";
			}

		} else {
			return "Please enter Event type properly";
		}
	}

	@Override
	public String getBookingSchedule(String customerId) throws IOException {
		// TODO Auto-generated method stub

		StringBuilder temp = new StringBuilder();
		temp.append(serverData.getBookingSchedule(customerId.trim()));
		if (customerId.substring(0, 3).trim().equals("TOR")) {
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9991,
					"scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9992,
					"scheduleOperation").trim());
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
		} else if (customerId.substring(0, 3).trim().equals("MTL")) {
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9990,
					"scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9992,
					"scheduleOperation").trim());
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
		} else if (customerId.substring(0, 3).trim().equals("OTW")) {
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9990,
					"scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9991,
					"scheduleOperation").trim());
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
		}
		return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
	}

	static void setLogger(String location, String id) {
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