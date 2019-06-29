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

import com.event.management.implementation.EventManagerClient;

/**
 *
 * @author Jenny
 */
public class Toronto {
	EventManagerClient managerObj = null;
	private static Logger logger;

	public Toronto(EventManagerClient aThis) {

		this.managerObj = aThis;
	}

	public void serverConnection(int port) {
		setLogger("logs/TOR.txt", "TOR");
		logger.info("Toronto Server Started");
		DatagramSocket datagramSocket = null;

		while (true) {
			try {
				datagramSocket = new DatagramSocket(port);
				byte[] receive = new byte[65535];
				DatagramPacket packetReceive = new DatagramPacket(receive, receive.length);
				datagramSocket.receive(packetReceive);
				byte[] data = packetReceive.getData();
				String[] receiveData = new String(data).split(",");
				logger.info("Receive Data : " + new String(data));
				logger.info("Operation Performed " + receiveData[receiveData.length - 1].trim());
				if (receiveData[receiveData.length - 1].trim().equals("listOperation")) {
					String temp = managerObj.torontoData.retrieveEvent(receiveData[2]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("addOperation")) {
					String temp = managerObj.torontoData.addEvent(receiveData[1], receiveData[2], receiveData[3]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("bookOperation")) {
					String temp = managerObj.torontoData.bookEvent(receiveData[0], receiveData[1], receiveData[2]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("cancelOperation")) {
					String temp = managerObj.torontoData.removeEvent(receiveData[0], receiveData[1], receiveData[2]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("scheduleOperation")) {
					String temp = managerObj.torontoData.getBookingSchedule(receiveData[0]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("countOperation")) {
					String temp = managerObj.torontoData.getBookingCount(receiveData[0], receiveData[1]);
					logger.info("Reply send to customer : " + temp);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
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