/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.management.implementation.managerImplementation;

/**
 *
 * @author Himen Sidhpura
 */
public class Toronto {

	managerImplementation managerObj = null;

	public Toronto(managerImplementation aThis) {

		this.managerObj = aThis;
	}

	public void xyz(int port) throws SocketException, IOException {

		System.out.println("Toronto Server");

		DatagramSocket conSocket = null;
		conSocket = new DatagramSocket(port);
	}
}
