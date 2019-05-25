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
		if (location.equals("TOR")) {

			Toronto toronto = new Toronto(this);

			Runnable task1 = () -> {
				toronto.xyz(9990);

			};
			Thread t1 = new Thread(task1);
			t1.start();
		} else if (location.equals("MTL")) {
			Runnable task2 = () -> {
				Montreal montreal = new Montreal(this);
				montreal.xyz(9991);
			};
			Thread t2 = new Thread(task2);
			t2.start();
		} else if (location.equals("OTW")) {
			Ottawa ottawa = new Ottawa(this);
			Runnable task3 = () -> {
				ottawa.xyz(9992);
			};
			Thread t3 = new Thread(task3);
			t3.start();
		} else {

			System.out.println("Server not started");

		}

	}

	@Override
	public void sendMessage(String msg) {
		// TODO Auto-generated method stub
		System.out.println("Current Msg " + msg);

	}

	@Override
	public String addEvent(String managerId, String eventId, String eventtype, String eventCapacity)
			throws IOException {
		if (eventtype.equals("Seminars") || eventtype.equals("Conferences") || eventtype.equals("Trade Shows")) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				System.out.println("Before Added-----> " + serverData.getServerData());
				String output = serverData.addEvent(eventId, eventtype, eventCapacity);
				System.out.println("After Added-----> " + serverData.getServerData());
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
		// TODO Auto-generated method stub

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
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences") || eventType.trim().equals("Trade Shows")) {
			String temp = serverData.retrieveEvent(eventType).trim();
			System.out.println("Current Server List " + temp);
			if (managerId.substring(0, 3).trim().equals("TOR")) {
				System.out.println("HERE tor +++++++++");
				temp += requestOnOtherServer(managerId, "xxx", eventType, "xxx", 9991, "listOperation").trim();
				temp = temp + requestOnOtherServer(managerId, "xxx", eventType, "xxx", 9992, "listOperation").trim();
				return temp.trim() == "" ? "No Events Available" : temp.trim();
			} else if (managerId.substring(0, 3).trim().equals("MTL")) {
				System.out.println("HERE +++++++++");
				temp += requestOnOtherServer(managerId, "xxx", eventType, "xxx", 9990, "listOperation").trim();
				temp = temp + requestOnOtherServer(managerId, "xxx", eventType, "xxx", 9992, "listOperation").trim();
				return temp.trim() == "" ? "No Events Available" : temp.trim();
			} else if (managerId.substring(0, 3).trim().equals("OTW")) {
				System.out.println("HERE +++++++++");
				temp += requestOnOtherServer(managerId, "xxx", eventType, "xxx", 9990, "listOperation").trim();
				temp = temp + requestOnOtherServer(managerId, "xxx", eventType, "xxx", 9991, "listOperation").trim();
				return temp.trim() == "" ? "No Events Available" : temp.trim();
			}
			System.out.println("List Availability : " + temp);
			return temp.trim() == "" ? "No Events Available" : temp.trim();

		} else {
			return "Please enter Event type properly";
		}
	}

	public String requestOnOtherServer(String managerId, String eventId, String eventType, String eventCapacity,
			int port, String operation) {
		DatagramSocket datagramSocket = null;
		try {
			String requestData = managerId+","+eventId + "," + eventType + "," + eventCapacity + "," + operation;
			System.out.println("requesrData : " + requestData + " " + port);
			datagramSocket = new DatagramSocket();
			DatagramPacket packetSend = new DatagramPacket(requestData.getBytes(), requestData.getBytes().length,
					InetAddress.getByName("localhost"), port);
			datagramSocket.send(packetSend);
			byte[] buffer = new byte[65535];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			datagramSocket.receive(reply);
			String response = new String(reply.getData());
			System.out.println("response : " + response);
			return response.trim();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			datagramSocket.close();
		}
		return "";
	}

	@Override
	public String eventBooking(String customerId, String eventId, String eventType) throws IOException {
		// TODO Auto-generated method stub
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences") || eventType.trim().equals("Trade Shows")) {
			StringBuilder count = new StringBuilder();
			if(!customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				if (customerId.trim().substring(0, 3).equals("TOR")) {
					count.append(requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")+",");
					count.append(requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")+",");
				} else if (customerId.trim().substring(0, 3).equals("MTL")) {
					count.append(requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")+",");
					count.append(requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")+",");
				} else if (customerId.trim().substring(0, 3).equals("OTW")) {
					count.append(requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")+",");
					count.append(requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")+",");
				}
				String[] split = count.toString().trim().split(",");
				int totalEve = 0;
				for(int i = 0; i < split.length;i++) {
					totalEve += Integer.parseInt(split[i].trim());
				}
				if(totalEve >= 3) {
					return "you have already reached maximum limit of Current Month Outside city registration ";
				}
			} 
			
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				String temp= serverData.bookEvent(customerId, eventId, eventType);
				return temp == "" ? "Unable to Book  Event" : temp + " -- > "+ count;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990, "bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp+ " -- > "+ count;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991, "bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp+ " -- > "+ count;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992, "bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp+ " -- > "+ count;

			} else {
				return "Please Enter Event ID Properly";
			}

		} else {
			return "Please enter Event type properly";
		}
	}

	@Override
	public String cancelBooking(String customerId, String eventId, String eventType) throws IOException {
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences") || eventType.trim().equals("Trade Shows")) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				String temp= serverData.removeEvent(customerId, eventId, eventType);
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990, "cancelOperation");
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991, "cancelOperation");
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992, "cancelOperation");
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
			System.out.println("HERE tor +++++++++");
			temp.append(requestOnOtherServer(customerId, "xxx", "No Types", "xxx", 9991, "scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "xxx", "No Types", "xxx", 9992, "scheduleOperation").trim());
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
		} else if (customerId.substring(0, 3).trim().equals("MTL")) {
			System.out.println("HERE +++++++++");
			temp.append(requestOnOtherServer(customerId, "xxx", "No Types", "xxx", 9990, "scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "xxx", "No Types", "xxx", 9992, "scheduleOperation").trim());
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
		} else if (customerId.substring(0, 3).trim().equals("OTW")) {
			System.out.println("HERE +++++++++");
			temp.append(requestOnOtherServer(customerId, "xxx", "No Types", "xxx", 9990, "scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "xxx", "No Types", "xxx", 9991, "scheduleOperation").trim());
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
		}
		System.out.println("List Schedule : " + temp);
		return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
	}

}