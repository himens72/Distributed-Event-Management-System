package com.event.management.frontend;

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
import org.omg.CORBA.ORB;

import com.event.management.constants.Constants;

import EventManagement.managerInterfacePOA;

public class FrontEndImpl extends managerInterfacePOA {

	private static Logger logger;
	private ORB orb;
	String replicaOneResponse = "";
	String replicaTwoResponse = "";
	String replicaThreeResponse = "";
	String replicaOneFailureResponse = "";
	String replicaTwoFailureResponse = "";
	String replicaThreeFailureResponse = "";

	public FrontEndImpl() {
		// TODO Auto-generated constructor stub
	}

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	public ORB getOrb() {
		return orb;
	}

	@Override
	public String addEvent(String managerId, String eventId, String eventType, String eventCapacity) {
		String requestMessage = generateJSONObject(managerId, eventId, eventType, eventCapacity, Constants.NONE,
				Constants.NONE, Constants.ADD_OPERATION);
		udpRequest(requestMessage);
		System.out.println("waiting for response...");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String replyMessage = udpReply();
		return replyMessage;
	}

	@Override
	public String removeEvent(String managerId, String eventId, String eventType) {
		String requestMessage = generateJSONObject(managerId, eventId, eventType, Constants.NONE, Constants.NONE,
				Constants.NONE, Constants.REMOVE_OPERATION);
		udpRequest(requestMessage);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String replyMessage = udpReply();
		return replyMessage;
	}

	@Override
	public String listEventAvailability(String managerId, String eventType) {
		String requestMessage = generateJSONObject(managerId, Constants.NONE, eventType, Constants.NONE, Constants.NONE,
				Constants.NONE, Constants.LIST_OPERATION);
		udpRequest(requestMessage);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String replyMessage = udpReply();
		return replyMessage;
	}

	@Override
	public String eventBooking(String customerId, String eventId, String eventType) {
		String requestMessage = generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
				Constants.NONE, Constants.BOOK_OPERATION);
		udpRequest(requestMessage);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String replyMessage = udpReply();
		return replyMessage;
	}

	@Override
	public String cancelBooking(String customerId, String eventId, String eventType) {
		String requestMessage = generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
				Constants.NONE, Constants.CANCEL_OPERATION);
		udpRequest(requestMessage);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String replyMessage = udpReply();
		return replyMessage;
	}

	@Override
	public String getBookingSchedule(String customerId) {
		String requestMessage = generateJSONObject(customerId, Constants.NONE, Constants.NONE, Constants.NONE,
				Constants.NONE, Constants.NONE, Constants.SCHEDULE_OPERATION);
		udpRequest(requestMessage);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String replyMessage = udpReply();
		return replyMessage;
	}

	@Override
	public String swapEvent(String customerId, String newEventId, String newEventType, String oldEventId,
			String oldEventType) {
		String requestMessage = generateJSONObject(customerId, newEventId, newEventType, Constants.NONE, oldEventId,
				oldEventType, Constants.SWAP_OPERATION);
		udpRequest(requestMessage);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String replyMessage = udpReply();
		return replyMessage;
	}

	static String generateJSONObject(String id, String eventId, String eventType, String eventCapacity,
			String oldEventId, String oldEventType, String operation) {
		JSONObject obj = new JSONObject();
		obj.put(Constants.ID, id.trim());
		obj.put(Constants.EVENT_ID, eventId.trim());
		obj.put(Constants.EVENT_TYPE, eventType.trim());
		obj.put(Constants.EVENT_CAPACITY, eventCapacity.trim());
		obj.put(Constants.OLD_EVENT_ID, oldEventId.trim());
		obj.put(Constants.OLD_EVENT_TYPE, oldEventType.trim());
		obj.put(Constants.OPERATION, operation.trim());
		return obj.toString();
	}

	public void udpRequest(String message) {
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket();
			byte[] msg = message.getBytes();
			InetAddress aHost = InetAddress.getByName(Constants.LOCALHOST);
			DatagramPacket request = new DatagramPacket(msg, msg.length, aHost, Constants.SEQUENCER_PORT);
			datagramSocket.send(request);
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

	public void ReplicaOneReply() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(Constants.RM1_FRONTEND_PORT);

			while (true) {
				byte[] buffer = new byte[Constants.BYTE_LENGTH];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				replicaOneResponse = unpackJSON(new String(request.getData(), 0, request.getLength()));
				System.out.println("replicaOneResponse " + replicaOneResponse);
				if (!replicaOneResponse.isEmpty()) {
					System.out.println("RM 1 : " + replicaOneResponse);
				}
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

	public void ReplicaTwoReply() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(Constants.RM2_FRONTEND_PORT);

			while (true) {
				byte[] buffer = new byte[Constants.BYTE_LENGTH];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				replicaTwoResponse = unpackJSON(new String(request.getData(), 0, request.getLength()));
				System.out.println("replicaTwoResponse " + replicaTwoResponse);
				if (!replicaTwoResponse.isEmpty()) {
					System.out.println("RM 2 : " + replicaTwoResponse);
				}
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

	public void ReplicaThreeReply() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(Constants.RM3_FRONTEND_PORT);
			while (true) {
				byte[] buffer = new byte[Constants.BYTE_LENGTH];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				replicaThreeResponse = unpackJSON(new String(request.getData(), 0, request.getLength()));
				System.out.println("replicaThreeResponse " + replicaThreeResponse);
				if (!replicaThreeResponse.isEmpty()) {
					System.out.println("RM 3 : " + replicaThreeResponse);
				}
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

	public String udpReply() {
		if (replicaOneResponse.trim().equals(replicaTwoResponse.trim())
				&& replicaTwoResponse.trim().equals(replicaThreeResponse.trim())) {
			return replicaOneResponse;
		} else if (replicaOneResponse.trim().equals(replicaTwoResponse.trim())) {
			if (replicaThreeResponse.trim().isEmpty()) {
				multicastFailResponse("Server Crash", Constants.RM1_ID, Constants.RM3_ID);
			} else {
				multicastFailResponse("Server Bug", Constants.RM1_ID,Constants.RM3_ID);
			}
			return replicaOneResponse;
		} else if (replicaOneResponse.trim().equals(replicaThreeResponse.trim())) {
			if (replicaTwoResponse.trim().isEmpty()) {
				multicastFailResponse("Server Crash",Constants.RM1_ID, Constants.RM2_ID);
			} else {
				multicastFailResponse("Server Bug", Constants.RM1_ID,Constants.RM2_ID);
			}
			return replicaOneResponse;
		} else if (replicaTwoResponse.trim().equals(replicaThreeResponse.trim())) {
			if (replicaOneResponse.trim().isEmpty()) {
				multicastFailResponse("Server Crash",Constants.RM2_ID, Constants.RM1_ID);
			} else {
				multicastFailResponse("Server Bug", Constants.RM2_ID,Constants.RM1_ID);
			}
			return replicaTwoResponse;
		}
		return replicaOneResponse;
	}

	private void multicastFailResponse(String message, String serverID1, String serverID2) {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte[] data = message.getBytes();
			InetAddress aHost = InetAddress.getByName(Constants.MULTICAST_IP);
			DatagramPacket request = new DatagramPacket(data, data.length, aHost, Constants.FAULT_PORT);
			aSocket.send(request);
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

	static void setLogger(String location, String id) {
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

	static String unpackJSON(String jsonString) {
		Object obj = null;
		try {
			obj = new JSONParser().parse(jsonString.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonObject = (JSONObject) obj;
		boolean operationFlag = Boolean.parseBoolean(jsonObject.get(Constants.OPERATION_STATUS).toString().trim());
		String id = jsonObject.get(Constants.ID).toString().trim();
		String operation = jsonObject.get(Constants.ID).toString().trim();
		if (operation.equals(Constants.ADD_OPERATION)) {
			String eventId = jsonObject.get(Constants.EVENT_ID).toString().trim();
			return operationFlag ? operationWiseJSONString(jsonString.trim())
					: id + " Unable to perform Add Event Operation for  " + eventId;
		} else if (operation.equals(Constants.REMOVE_OPERATION)) {
			String eventId = jsonObject.get(Constants.EVENT_ID).toString().trim();
			return operationFlag ? operationWiseJSONString(jsonString.trim())
					: id + " Unable to perform Remove Event Operation for  " + eventId;
		} else if (operation.equals(Constants.LIST_OPERATION)) {
			String eventType = jsonObject.get(Constants.EVENT_TYPE).toString().trim();
			return operationFlag ? operationWiseJSONString(jsonString.trim())
					: id + " : No Data Found or Might be data issue. " + eventType;
		} else if (operation.equals(Constants.BOOK_OPERATION)) {
			String eventId = jsonObject.get(Constants.EVENT_ID).toString().trim();
			return operationFlag ? operationWiseJSONString(jsonString.trim())
					: id + " Unable to book  event " + eventId;
		} else if (operation.equals(Constants.CANCEL_OPERATION)) {
			String eventId = jsonObject.get(Constants.EVENT_ID).toString().trim();
			return operationFlag ? operationWiseJSONString(jsonString.trim())
					: id + " Unable to cancel  event " + eventId;

		} else if (operation.equals(Constants.SCHEDULE_OPERATION)) {
			return operationFlag ? operationWiseJSONString(jsonString.trim())
					: id + " No Data Found or Might be data issue.";
		} else if (operation.equals(Constants.SWAP_OPERATION)) {
			return operationFlag ? operationWiseJSONString(jsonString.trim())
					: id + " No Data Found or Might be data issue.";
		}
		return operationFlag ? operationWiseJSONString(jsonString.trim())
				: "Might be Data Issue or Please Check Data Again. Please try again";
	}

	static String operationWiseJSONString(String jsonString) {
		Object obj = null;
		try {
			obj = new JSONParser().parse(jsonString.trim());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = (JSONObject) obj;
		String operation = jsonObject.get(Constants.OPERATION).toString().trim();
		String flag = "";
		String id = jsonObject.get(Constants.ID).toString().trim();
		if (operation.equals(Constants.ADD_OPERATION)) {
			String eventId = jsonObject.get(Constants.EVENT_ID).toString().trim();
			String eventType = jsonObject.get(Constants.EVENT_TYPE).toString().trim();
			String eventCapacity = jsonObject.get(Constants.EVENT_CAPACITY).toString().trim();
			flag = id + " has create event " + eventId + " of type " + eventType + " with capacity " + eventCapacity;
		} else if (operation.equals(Constants.REMOVE_OPERATION)) {
			String eventId = jsonObject.get(Constants.EVENT_ID).toString().trim();
			String eventType = jsonObject.get(Constants.EVENT_TYPE).toString().trim();
			flag = id + " has remove event " + eventId + " of type " + eventType;
		} else if (operation.equals(Constants.LIST_OPERATION)) {
			String eventType = jsonObject.get(Constants.EVENT_TYPE).toString().trim();
			String listEvents = jsonObject.get(Constants.LIST_EVENT_AVAILABLE).toString().trim();
			flag = listEvents.trim().isEmpty() ? id + " : No data found for " + eventType
					: id + "  :  " + eventType + " = " + listEvents;
		} else if (operation.equals(Constants.BOOK_OPERATION)) {
			String eventId = jsonObject.get(Constants.EVENT_ID).toString().trim();
			String eventType = jsonObject.get(Constants.EVENT_TYPE).toString().trim();
			flag = id + " has book event " + eventId + " of type " + eventType;
		} else if (operation.equals(Constants.CANCEL_OPERATION)) {
			String eventId = jsonObject.get(Constants.EVENT_ID).toString().trim();
			String eventType = jsonObject.get(Constants.EVENT_TYPE).toString().trim();
			flag = id + " has cancel event " + eventId + " of type " + eventType;
		} else if (operation.equals(Constants.SCHEDULE_OPERATION)) {
			String listEvents = jsonObject.get(Constants.LIST_EVENT_SCHEDULE).toString().trim();
			flag = listEvents.trim().isEmpty() ? id + " : No data found." : id + "  :  " + listEvents;
		} else if (operation.equals(Constants.SWAP_OPERATION)) {
			String newEventId = jsonObject.get(Constants.EVENT_ID).toString().trim();
			String newEventType = jsonObject.get(Constants.EVENT_TYPE).toString().trim();
			String oldEventId = jsonObject.get(Constants.OLD_EVENT_ID).toString().trim();
			String oldEventType = jsonObject.get(Constants.OLD_EVENT_TYPE).toString().trim();
			flag = id + " has swap event " + oldEventId + " of type " + oldEventType + " with " + newEventId
					+ " of type " + newEventType;
		}
		return flag.trim().equals("") ? id + " : Might be Data Issue or Please Check Data Again. Please try again"
				: flag;
	}
}
