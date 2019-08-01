package com.event.management.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class JavaMulticastSocketgetInterfaceExample1 {
	public static void main(String[] args) throws IOException {
		try {
			int MULTICAST_LISTENING_PORT = 9990;
			MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_LISTENING_PORT);
			String MULTICAST_ADDRESS = null;
			InetAddress multicastGroup = InetAddress.getByName(MULTICAST_ADDRESS);
			System.out.println("leaving group using leaveGroup method....");
			multicastSocket.leaveGroup(multicastGroup);
		} catch (IOException ioException) {
		}
	}

}
