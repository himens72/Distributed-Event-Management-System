package com.event.management.sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.event.management.constants.Constants;

import EventManagement.managerInterface;

public class Sequencer {
	static long counter = 1;
	static managerInterface managerObj;
	static Logger logger;

	public static void main(String args[]) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(Constants.SEQUENCER_PORT);
			byte[] buffer = new byte[Constants.BYTE_LENGTH];
			setLogger("logs/Sequencer.txt", "Sequencer");

			logger.info("Sequencer Started");
			while (true) {
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String message = new String(request.getData(), 0, request.getLength());
				// message += counter;
				Object obj = new JSONParser().parse(message);
				JSONObject jsonObject = (JSONObject) obj;
				jsonObject.put("Sequence", counter);
				logger.info("Sequencer Data : " + jsonObject.toString());
				counter++;
				InetAddress aHost = InetAddress.getByName(Constants.MULTICAST_IP);
				byte[] msg = jsonObject.toString().getBytes();
				if (jsonObject.get(Constants.ID).toString().subSequence(0, 3).equals("TOR")) {
					DatagramPacket packet = new DatagramPacket(msg, msg.length, aHost, Constants.RM_TORONTO_PORT);
					aSocket.send(packet);
				} else if (jsonObject.get(Constants.ID).toString().subSequence(0, 3).equals("MTL")) {
					DatagramPacket packet = new DatagramPacket(msg, msg.length, aHost, Constants.RM_MONTREAL_PORT);
					aSocket.send(packet);
				} else if (jsonObject.get(Constants.ID).toString().subSequence(0, 3).equals("OTW")) {
					DatagramPacket packet = new DatagramPacket(msg, msg.length, aHost, Constants.RM_OTTAWA_PORT);
					aSocket.send(packet);
				}
			}
		} catch (SocketException e) {
			logger.info(e.getMessage());
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			logger.info(e.getMessage());
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setLogger(String location, String id) {
		try {
			logger = Logger.getLogger(id);
			FileHandler fileTxt = new FileHandler(location, true);
			SimpleFormatter formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			logger.addHandler(fileTxt);
		} catch (Exception err) {
			logger.info("Couldn't Initiate Logger. Please check file permission");
		}
	}
}
