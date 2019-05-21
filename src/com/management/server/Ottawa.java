/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.server;

import java.net.DatagramSocket;
import java.net.SocketException;

import com.management.implementation.managerImplementation;
import com.management.model.eventData;

/**
 *
 * @author Himen Sidhpura
 */
public class Ottawa {

	managerImplementation managerObj = null;
	eventData serverData;
	public Ottawa(managerImplementation aThis) {

		this.managerObj = aThis;
	}

	public void xyz(int port) throws SocketException {

		System.out.println("Ottawa Server");
		serverData = new eventData();
		serverData.setServerName("Ottawa Server Data Model Crearted");
        System.out.println(serverData.getServerName());
		DatagramSocket conSocket = null;
		conSocket = new DatagramSocket(port);
	}
}
