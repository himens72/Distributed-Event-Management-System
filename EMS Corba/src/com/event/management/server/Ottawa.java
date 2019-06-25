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

import com.event.management.implementation.EventManagerClient;

/**
 *
 * @author Himen Sidhpura
 */
public class Ottawa {
	
	EventManagerClient managerObj = null;
	
	public Ottawa(EventManagerClient aThis) {

		this.managerObj = aThis;
	}

	public void serverConnection(int port) {
		System.out.println("Ottawa Server started");
		DatagramSocket datagramSocket = null;

		while (true) {
			try {
				datagramSocket = new DatagramSocket(port);
				byte[] receive = new byte[65535];
				DatagramPacket packetReceive = new DatagramPacket(receive, receive.length);
				datagramSocket.receive(packetReceive);
				byte[] data = packetReceive.getData();
				String[] receiveData = new String(data).split(",");
				
				String temp = "Hello" + receiveData;
				DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
				packetReceive.getAddress(), packetReceive.getPort());
				datagramSocket.send(reply);
				
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

}