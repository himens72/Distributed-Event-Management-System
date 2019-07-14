package com.event.management.sequencer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Sequencer {
	static long counter = 1;

	public static void main(String args[]) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(5555);
			byte[] buffer = new byte[1000];

			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String message = new String(request.getData(), 0, request.getLength());
				message += counter;
				counter++;
				InetAddress aHost = InetAddress.getByName("230.1.1.5");
				byte[] msg = message.getBytes();
				DatagramPacket packet = new DatagramPacket(msg, msg.length, aHost, 1313);
				aSocket.send(packet);
			}
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
