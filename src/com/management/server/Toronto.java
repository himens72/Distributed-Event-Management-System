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

import com.management.implementation.managerImplementation;
import com.management.model.eventData;

/**
 *
 * @author Himen Sidhpura
 */
public class Toronto {

	managerImplementation managerObj = null;
	eventData serverData;

	public Toronto(managerImplementation aThis) {

		this.managerObj = aThis;
	}

	public void xyz(int port) throws SocketException, IOException {

		System.out.println("Toronto Server");
		serverData = new eventData();
		serverData.setServerName("Toronto Server Data Model Crearted");
		System.out.println(serverData.getServerName());
		DatagramPacket packet = null;
		byte[] receiveData = new byte[65535];
		DatagramSocket datagramSocket = datagramSocket = new DatagramSocket(port);
			packet = new DatagramPacket(receiveData, receiveData.length);
			datagramSocket.receive(packet);
			System.out.println("Client:-" + processData(receiveData));
			receiveData = new byte[65535];
	}

	private String processData(byte[] receiveData) {
		// TODO Auto-generated method stub
		System.out.println("Data Started Processing");
		return "Event Created";
	}
}
