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
public class Ottawa {

	managerImplementation managerObj = null;
	public Ottawa(managerImplementation aThis) {

		this.managerObj = aThis;
	}

	public void xyz(int port) throws SocketException {

		System.out.println("Ottawa Server");

		DatagramSocket conSocket = null;
		conSocket = new DatagramSocket(port);
	}
}
