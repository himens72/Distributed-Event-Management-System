/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.event.management.server;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.event.management.constants.Constants;
import com.event.management.implementation.EventManagerMontreal;
import com.event.management.model.MontrealData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Jenny
 */
public class MontrealServer {
	private static Logger logger;
	String response = "";
	static EventManagerMontreal mtlObject = new EventManagerMontreal();

	public static void main(String args[]) throws Exception {
		MontrealServer montrealServer = new MontrealServer();
		montrealServer.setLogger("logs/MTL.txt", "MTL");
		logger.info("Montreal Server Started");
		Runnable montrealTask = () -> {
			montrealServer.receive();
		};
		Thread thread = new Thread(montrealTask);
		thread.start();

		Runnable montrealRequestTask = () -> {
			montrealServer.receiveMulticastRequest();
		};

		Runnable montrealResponseTask = () -> {
			receiveFailedResponse();
		};
		Runnable RMResponseTask = () -> {
			updateServerData();
		};
		Thread thread1 = new Thread(montrealRequestTask);
		Thread thread2 = new Thread(montrealResponseTask);
		Thread thread3 = new Thread(RMResponseTask);
		thread1.start();
		thread2.start();
		thread3.start();
	}

	public void receive() {
		DatagramSocket datagramSocket = null;
		while (true) {
			try {
				datagramSocket = new DatagramSocket(Constants.LOCAL_MONTREAL_PORT);
				byte[] receive = new byte[Constants.BYTE_LENGTH];
				DatagramPacket packetReceive = new DatagramPacket(receive, receive.length);
				datagramSocket.receive(packetReceive);
				byte[] data = packetReceive.getData();
				String[] receiveData = new String(data).split(",");
				if (receiveData[receiveData.length - 1].trim().equals(Constants.LIST_OPERATION)) {
					String temp = mtlObject.montrealData.retrieveEvent(receiveData[2]);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("addOperation")) {
					boolean temp = mtlObject.montrealData.addEvent(receiveData[1], receiveData[2], receiveData[3]);
					String newTemp = temp == false ? "Denies" : "Approves";
					DatagramPacket reply = new DatagramPacket(newTemp.getBytes(), newTemp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals(Constants.BOOK_OPERATION)) {
					String temp = generateJSONObject(receiveData[0], receiveData[1], receiveData[2], "None", "None",
							"None", Constants.BOOK_OPERATION,
							mtlObject.montrealData.bookEvent(receiveData[0], receiveData[1], receiveData[2]));
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals(Constants.CANCEL_OPERATION)) {
					String temp = generateJSONObject(receiveData[0], receiveData[1], receiveData[2], "None", "None",
							"None", Constants.CANCEL_OPERATION,
							mtlObject.montrealData.removeEvent(receiveData[0], receiveData[1], receiveData[2]));
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals(Constants.SCHEDULE_OPERATION)) {
					String temp = mtlObject.montrealData.getBookingSchedule(receiveData[0]);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("countOperation")) {
					String temp = mtlObject.montrealData.getBookingCount(receiveData[0], receiveData[1]);
					DatagramPacket reply = new DatagramPacket(temp.getBytes(), temp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else if (receiveData[receiveData.length - 1].trim().equals("existanceOperation")) {
					boolean temp = mtlObject.montrealData.getEvent(receiveData[0], receiveData[1], receiveData[2]);
					String newTemp = temp == false ? "Denies" : "Approves";
					DatagramPacket reply = new DatagramPacket(newTemp.getBytes(), newTemp.length(),
							packetReceive.getAddress(), packetReceive.getPort());
					updateJSONFile();
					datagramSocket.send(reply);
				} else {
					logger.info("Some problem in Server");
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				datagramSocket.close();
			}
		}
	}

	private void receiveMulticastRequest() {

		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(Constants.RM_MONTREAL_PORT);
			aSocket.joinGroup(InetAddress.getByName(Constants.MULTICAST_IP));
			while (true) {
				byte[] buffer = new byte[Constants.BYTE_LENGTH];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String requestMessage = new String(request.getData());
				Object obj = new JSONParser().parse(requestMessage.trim());
				JSONObject jsonObject = (JSONObject) obj;
				switch (jsonObject.get(Constants.OPERATION).toString()) {
				case "addEventOperation": {
					String managerId = jsonObject.get(Constants.ID).toString();
					String eventId = jsonObject.get(Constants.EVENT_ID).toString();
					String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();
					String eventCapacity = jsonObject.get(Constants.EVENT_CAPACITY).toString();
					response = mtlObject.addEvent(managerId, eventId, eventType, eventCapacity);
					break;
				}
				case "removeEventOperation": {
					String managerId = jsonObject.get(Constants.ID).toString();
					String eventId = jsonObject.get(Constants.EVENT_ID).toString();
					String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();
					response = mtlObject.removeEvent(managerId, eventId, eventType);
					break;
				}
				case "listEventOperation": {
					String managerId = jsonObject.get(Constants.ID).toString();
					String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();
					response = mtlObject.listEventAvailability(managerId, eventType);
					break;
				}
				case "eventBookingOperation": {
					String customerId = jsonObject.get(Constants.ID).toString();
					String eventId = jsonObject.get(Constants.EVENT_ID).toString();
					String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();
					response = mtlObject.eventBooking(customerId, eventId, eventType);
					break;
				}
				case "cancelBookingOperation": {
					String customerId = jsonObject.get(Constants.ID).toString();
					String eventId = jsonObject.get(Constants.EVENT_ID).toString();
					String eventType = jsonObject.get(Constants.EVENT_TYPE).toString();
					response = mtlObject.cancelBooking(customerId, eventId, eventType);
					break;
				}

				case Constants.SCHEDULE_OPERATION: {
					String customerId = jsonObject.get(Constants.ID).toString();
					response = mtlObject.getBookingSchedule(customerId);
					break;
				}
				case "swapEventOperation": {
					String customerId = jsonObject.get(Constants.ID).toString();
					String newEventId = jsonObject.get(Constants.EVENT_ID).toString();
					String newEventType = jsonObject.get(Constants.EVENT_TYPE).toString();
					String oldEventId = jsonObject.get(Constants.OLD_EVENT_ID).toString();
					String oldEventType = jsonObject.get(Constants.OLD_EVENT_TYPE).toString();
					response = mtlObject.swapEvent(customerId, newEventId, newEventType, oldEventId, oldEventType);
					break;
				}
				}
				updateJSONFile();
				sendRequestToFrontEnd(response);
			}
		} catch (SocketException e) {
			logger.info("Socket: " + e.getMessage());
		} catch (IOException e) {
			logger.info("IO: " + e.getMessage());
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

	private void sendRequestToFrontEnd(String message) {
		DatagramSocket aSocket = null;
		try {
			logger.info("Data sent to Front End : " + message);
			aSocket = new DatagramSocket();
			byte[] m = message.getBytes();
			InetAddress aHost = InetAddress.getByName(Constants.FRONTEND_IP);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, Constants.RM_FRONTEND_PORT);
			aSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void receiveFailedResponse() {
		MulticastSocket aSocket = null;
		DatagramSocket datagramSocket = null;
		try {
			aSocket = new MulticastSocket(Constants.FAULT_PORT);
			aSocket.joinGroup(InetAddress.getByName(Constants.FAULT_MULTICAST_IP));
			while (true) {
				byte[] buffer = new byte[Constants.BYTE_LENGTH];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String[] temp = new String(request.getData()).trim().split(",");
				logger.info(temp[1].trim() + " is sending data to " + temp[2].trim());
				if (temp[1].trim().equals(Constants.RM1_ID)) {
					if (temp[2].trim().equals(Constants.RM1_ID)) {
						datagramSocket = new DatagramSocket();
						JSONParser parser = new JSONParser();
						Object obj = parser.parse(new FileReader("toronto.json"));
						JSONObject jsonObject = (JSONObject) obj;
						String sendTORData = jsonObject.toString();
						obj = parser.parse(new FileReader("montreal.json"));
						jsonObject = (JSONObject) obj;
						String sendMTLData = jsonObject.toString();
						obj = parser.parse(new FileReader("ottawa.json"));
						jsonObject = (JSONObject) obj;
						String sendOTWData = jsonObject.toString();
						byte[] torByte = sendTORData.getBytes();
						byte[] mtlByte = sendMTLData.getBytes();
						byte[] otwByte = sendOTWData.getBytes();
						InetAddress torontoHost = InetAddress.getByName(Constants.FAIL_RM1_IP);
						DatagramPacket torontoRequest = new DatagramPacket(torByte, torByte.length, torontoHost,
								Constants.FAIL_TORONTO_PORT);
						datagramSocket.send(torontoRequest);
						InetAddress montrealHost = InetAddress.getByName(Constants.FAIL_RM1_IP);
						DatagramPacket montrealRequest = new DatagramPacket(mtlByte, mtlByte.length, montrealHost,
								Constants.FAIL_MONTREAL_PORT);
						datagramSocket.send(montrealRequest);
						InetAddress ottawaHost = InetAddress.getByName(Constants.FAIL_RM1_IP);
						DatagramPacket ottawaRequest = new DatagramPacket(otwByte, otwByte.length, ottawaHost,
								Constants.FAIL_OTTAWA_PORT);
						datagramSocket.send(ottawaRequest);
					} else if (temp[2].trim().equals(Constants.RM2_ID)) {
						datagramSocket = new DatagramSocket();
						JSONParser parser = new JSONParser();
						Object obj = parser.parse(new FileReader("toronto.json"));
						JSONObject jsonObject = (JSONObject) obj;
						String sendTORData = jsonObject.toString();
						obj = parser.parse(new FileReader("montreal.json"));
						jsonObject = (JSONObject) obj;
						String sendMTLData = jsonObject.toString();
						obj = parser.parse(new FileReader("ottawa.json"));
						jsonObject = (JSONObject) obj;
						String sendOTWData = jsonObject.toString();
						byte[] torByte = sendTORData.getBytes();
						byte[] mtlByte = sendMTLData.getBytes();
						byte[] otwByte = sendOTWData.getBytes();
						InetAddress torontoHost = InetAddress.getByName(Constants.FAIL_RM2_IP);
						DatagramPacket torontoRequest = new DatagramPacket(torByte, torByte.length, torontoHost,
								Constants.FAIL_TORONTO_PORT);
						datagramSocket.send(torontoRequest);
						InetAddress montrealHost = InetAddress.getByName(Constants.FAIL_RM2_IP);
						DatagramPacket montrealRequest = new DatagramPacket(mtlByte, mtlByte.length, montrealHost,
								Constants.FAIL_MONTREAL_PORT);
						datagramSocket.send(montrealRequest);
						InetAddress ottawaHost = InetAddress.getByName(Constants.FAIL_RM2_IP);
						DatagramPacket ottawaRequest = new DatagramPacket(otwByte, otwByte.length, ottawaHost,
								Constants.FAIL_OTTAWA_PORT);
						datagramSocket.send(ottawaRequest);
					} else if (temp[2].trim().equals(Constants.RM3_ID)) {
						datagramSocket = new DatagramSocket();
						JSONParser parser = new JSONParser();
						Object obj = parser.parse(new FileReader("toronto.json"));
						JSONObject jsonObject = (JSONObject) obj;
						String sendTORData = jsonObject.toString();
						obj = parser.parse(new FileReader("montreal.json"));
						jsonObject = (JSONObject) obj;
						String sendMTLData = jsonObject.toString();
						obj = parser.parse(new FileReader("ottawa.json"));
						jsonObject = (JSONObject) obj;
						String sendOTWData = jsonObject.toString();
						byte[] torByte = sendTORData.getBytes();
						byte[] mtlByte = sendMTLData.getBytes();
						byte[] otwByte = sendOTWData.getBytes();
						InetAddress torontoHost = InetAddress.getByName(Constants.FAIL_RM3_IP);
						DatagramPacket torontoRequest = new DatagramPacket(torByte, torByte.length, torontoHost,
								Constants.FAIL_TORONTO_PORT);
						datagramSocket.send(torontoRequest);
						InetAddress montrealHost = InetAddress.getByName(Constants.FAIL_RM3_IP);
						DatagramPacket montrealRequest = new DatagramPacket(mtlByte, mtlByte.length, montrealHost,
								Constants.FAIL_MONTREAL_PORT);
						datagramSocket.send(montrealRequest);
						InetAddress ottawaHost = InetAddress.getByName(Constants.FAIL_RM3_IP);
						DatagramPacket ottawaRequest = new DatagramPacket(otwByte, otwByte.length, ottawaHost,
								Constants.FAIL_OTTAWA_PORT);
						datagramSocket.send(ottawaRequest);
					}
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void updateServerData() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(Constants.FAIL_MONTREAL_PORT);
			while (true) {
				byte[] buffer = new byte[Constants.BYTE_LENGTH];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String data = new String(request.getData()).trim();
				JSONParser parser = new JSONParser();
				Gson gson = new Gson();
				Object obj = parser.parse(data.trim());
				JSONObject jsonObject = (JSONObject) obj;
				mtlObject.montrealData = gson.fromJson(String.valueOf(jsonObject.get("player")), MontrealData.class);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static String generateJSONObject(String id, String eventId, String eventType, String eventCapacity,
			String oldEventId, String oldEventType, String operation, boolean status) {
		JSONObject obj = new JSONObject();
		obj.put(Constants.ID, id.trim());
		obj.put(Constants.EVENT_ID, eventId.trim());
		obj.put(Constants.EVENT_TYPE, eventType.trim());
		obj.put(Constants.EVENT_CAPACITY, eventCapacity.trim());
		obj.put(Constants.OLD_EVENT_ID, oldEventId.trim());
		obj.put(Constants.OLD_EVENT_TYPE, oldEventType.trim());
		obj.put(Constants.OPERATION, operation.trim());
		obj.put(Constants.OPERATION_STATUS, status);
		return obj.toString();
	}

	private void setLogger(String location, String id) {
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

	public static void updateJSONFile() {
		JSONObject game = new JSONObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String playerString = gson.toJson(mtlObject.montrealData);

		game.put("player", playerString);
		String gameString = gson.toJson(game);

		try {
			FileWriter fileWriter = new FileWriter("montreal.json");
			fileWriter.write(gameString);
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}