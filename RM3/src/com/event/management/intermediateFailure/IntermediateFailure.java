package com.event.management.intermediateFailure;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.event.management.constants.Constants;

public class IntermediateFailure {
	private static DatagramSocket aSocket;

	public static void main(String[] args) {
		aSocket = null;
		try {
			String message = new String("Failure");
			byte[] msg = message.getBytes();
			InetAddress aHost = InetAddress.getByName(Constants.LOCALHOST);
			DatagramPacket packet1 = new DatagramPacket(msg, msg.length, aHost, 5000);
			aSocket.send(packet1);
			DatagramPacket packet2 = new DatagramPacket(msg, msg.length, aHost, 5001);
			aSocket.send(packet2);
			DatagramPacket packet3 = new DatagramPacket(msg, msg.length, aHost, 5002);
			aSocket.send(packet3);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
