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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import com.management.interfaceDef.managerInterface;
import com.management.server.Montreal;
import com.management.server.Ottawa;
import com.management.server.Toronto;

/**
 *
 * @author Himen Sidhpura
 */
public class managerImplementation extends UnicastRemoteObject implements managerInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4262636697845867549L;
	public String location;
	public String response;

	public managerImplementation(String location) throws RemoteException {
		super();

		this.location = location;
		if (location.equals("TOR")) {

			Toronto toronto = new Toronto(this);

			Runnable task1 = () -> {
				try {
					toronto.xyz(9990);
				} catch (SocketException ex) {
				} catch (IOException ex) {
				}

			};
			Thread t1 = new Thread(task1);
			t1.start();
		} else if (location.equals("MTL")) {
			Runnable task2 = () -> {
				Montreal montreal = new Montreal(this);
				try {
					montreal.xyz(9991);
				} catch (SocketException ex) {
				} catch (IOException ex) {
				}
			};
			Thread t2 = new Thread(task2);
			t2.start();
		} else if (location.equals("OTW")) {
			Ottawa ottawa = new Ottawa(this);
			Runnable task3 = () -> {
				try {
					ottawa.xyz(9992);
				} catch (SocketException ex) {
				} catch (IOException ex) {
				}
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
	public void addEvent(String msg) throws IOException {
		// TODO Auto-generated method stub
		DatagramSocket datagramSocket = new DatagramSocket();

		InetAddress inetAddress = InetAddress.getLocalHost();
		byte buffer[] = null;
			buffer = msg.getBytes();
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, 9990);
			datagramSocket.send(datagramPacket);
	}
}
