/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import com.management.implementation.EventManagerClient;

/**
 *
 * @author Himen Sidhpura
 */
public class Ottawa {

	EventManagerClient managerObj = null;

	public Ottawa(EventManagerClient aThis) {

		this.managerObj = aThis;
	}

	public void xyz(int port) {

		System.out.println("Ottawa Server");
		DatagramSocket datagramSocket = null;
		while (true) {
			try {
				datagramSocket = new DatagramSocket(port);
				byte[] receive = new byte[65535];
				DatagramPacket packetReceive = new DatagramPacket(receive, receive.length);
				datagramSocket.receive(packetReceive);
				byte[] data = packetReceive.getData();
				String[] receiveData = new String(data).split(",");
				System.out.println("receive Data : " + new String(data));
				System.out.println(receiveData[receiveData.length - 1].trim());
				if (receiveData[receiveData.length - 1].trim().equals("listOperation")) {
					System.out.println("Montreal Server XXX");
					String temp = managerObj.serverData.retrieveEvent(receiveData[2]);
					System.out.println("Main Response : " + managerObj.serverData.getServerData());
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("addOperation")) {
					System.out.println("Ottawa Server XXX");
					String temp = managerObj.serverData.addEvent(receiveData[1], receiveData[2], receiveData[3]);
					System.out.println("Main Response : " + managerObj.serverData.getServerData());
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("bookOperation")) {
					String temp = managerObj.serverData.bookEvent(receiveData[0], receiveData[1], receiveData[2]);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("cancelOperation")) {
					String temp = managerObj.serverData.removeEvent(receiveData[0], receiveData[1], receiveData[2]);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("scheduleOperation")) {
					System.out.println("Montreal Server XXX");
					String temp = managerObj.serverData.getBookingSchedule(receiveData[0]);
					System.out.println("Main Response : " + managerObj.serverData.getBookingSchedule(receiveData[0]));
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("countOperation")) {
					System.out.println("Montreal Server XXX");
					String temp = managerObj.serverData.getBookingCount(receiveData[0], receiveData[1]);
					System.out.println("Ottawa Main Response : " + managerObj.serverData.getBookingCount(receiveData[0], receiveData[1]));
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					datagramSocket.send(reply);
				} else {
					System.out.println("ERROR");
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

	public String processData(byte[] receiveData) {
		return null;
	}
}