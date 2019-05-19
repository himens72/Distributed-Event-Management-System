/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.server;

import java.net.DatagramSocket;
import java.net.SocketException;

import com.management.implementation.managerImplementation;

/**
 *
 * @author Himen Sidhpura
 */
public class Montreal {

	managerImplementation managerObj = null;

	public Montreal(managerImplementation aThis) {

		this.managerObj = aThis;
	}

	public void xyz(int port) throws SocketException {

		System.out.println("Montreal Server");

		DatagramSocket conSocket = null;
		conSocket = new DatagramSocket(port);

	}
}