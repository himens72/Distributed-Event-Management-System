/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.event.management.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.event.management.implementation.EventManagerClient;

/**
 *
 * @author Jenny
 */
public class Ottawa {

	EventManagerClient managerObj = null;
	private static Logger logger;

	public Ottawa(EventManagerClient aThis) {

		this.managerObj = aThis;
	}

	public void serverConnection(int port) {
		setLogger("logs/OTW.txt", "OTW");
		logger.info("Ottawa Server Started");
		DatagramSocket datagramSocket = null;

		while (true) {
			try {
				datagramSocket = new DatagramSocket(port);
				byte[] receive = new byte[65535];
				DatagramPacket packetReceive = new DatagramPacket(receive, receive.length);
				datagramSocket.receive(packetReceive);
				byte[] data = packetReceive.getData();
				// String[] receiveData = new String(data).split(",");
				JSONParser parser = new JSONParser();
				String temp11 = new String(data);
				JSONObject json = (JSONObject) parser.parse(temp11.trim());
				String id = (String) json.get("id");
				String eventId = (String) json.get("eventId");
				String eventType = (String) json.get("eventType");
				String eventCapacity = (String) json.get("eventCapacity");
				String operation = (String) json.get("operation");
				// managerId + "," + eventId + "," + eventType + "," + eventCapacity + "," +
				// operation
				logger.info("Receive Data : " + new String(data));
				logger.info("Operation Performed " + operation.trim());
				if (operation.trim().equals("listOperation")) {
					String temp = managerObj.ottawaData.retrieveEvent(eventType.trim());
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (operation.trim().equals("addOperation")) {
					String temp = managerObj.ottawaData.addEvent(eventId.trim(), eventType.trim(),
							eventCapacity.trim());
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (operation.trim().equals("bookOperation")) {
					String temp = managerObj.ottawaData.bookEvent(id.trim(), eventId.trim(), eventType.trim());
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (operation.trim().equals("cancelOperation")) {
					String temp = managerObj.ottawaData.removeEvent(id.trim(), eventId.trim(), eventType.trim());
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (operation.trim().equals("scheduleOperation")) {
					String temp = managerObj.ottawaData.getBookingSchedule(id.trim());
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (operation.trim().equals("countOperation")) {
					String temp = managerObj.ottawaData.getBookingCount(id.trim(), eventId.trim());
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (operation.trim().equals("existanceOperation")) {
					boolean temp = managerObj.ottawaData.getEvent(id.trim(), eventId.trim(), eventType.trim());
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
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				datagramSocket.close();
			}

		}
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

	public String processData(byte[] receiveData) {
		return null;
	}
}