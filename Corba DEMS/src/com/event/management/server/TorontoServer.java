/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.event.management.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Jenny
 */
public class TorontoServer {

	public static void main(String args[]) throws Exception {

		TorontoServer torontoServer = new TorontoServer();

		System.out.println("Toronto Server started");

		Runnable task = () -> {
			torontoServer.receive();
		};
		Thread thread = new Thread(task);
		thread.start();

		Runnable task1 = () -> {
			torontoServer.receiveMulticastRequest();
		};

		Runnable task2 = () -> {
			receiveFailedResponse();
		};
		Thread thread1 = new Thread(task1);
		Thread thread2 = new Thread(task2);

		thread1.start();
		thread2.start();

		System.out.println("Toronto Server Exiting ...");
	}

	void receive() {

	}

	private void receiveMulticastRequest() {
		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(9990);
			aSocket.joinGroup(InetAddress.getByName("224.0.0.252"));

			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				String message = new String(request.getData(), 0, request.getLength());
				// message += counter;
				System.out.println("Toront o" + message);
			}
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendRequestToFrontEnd(String message) {

	}

	private static void receiveFailedResponse() {

	}
}