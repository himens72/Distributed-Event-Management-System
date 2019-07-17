package com.event.management.sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import com.event.management.constants.Constants;

import EventManagement.managerInterface;
import EventManagement.managerInterfaceHelper;

public class Sequencer {
	static long counter = 1;
	static managerInterface managerObj;

	public static void main(String args[]) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(5555);
			byte[] buffer = new byte[1000];
			System.out.println("Sequencer Started");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String message = new String(request.getData(), 0, request.getLength());
				// message += counter;
				Object obj = new JSONParser().parse(message);
				JSONObject jsonObject = (JSONObject) obj;
				jsonObject.put("Sequence", counter);
				System.out.println("Sequencer Data : " + jsonObject.toString());
				counter++;

				InetAddress aHost = InetAddress.getByName("230.1.1.5");
				System.out.println("Inet Addess " + aHost.getHostName() + " " +aHost.getHostAddress());
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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
