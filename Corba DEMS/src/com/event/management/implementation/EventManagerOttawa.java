package com.event.management.implementation;

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
import com.event.management.model.OttawaData;

public class EventManagerOttawa {
//	public String location;
	public String response;
	public OttawaData ottawaData;
	private static Logger logger;

	public EventManagerOttawa() {
		super();
		ottawaData = new OttawaData();
		setLogger("logs/OTW.txt", "OTW");		
	}
	
	public String addEvent(String managerId, String eventId, String eventType, String eventCapacity) {
		logger.info("Add Event Operation :  " + managerId + " has started creating event with id " + eventId
				+ " of type " + eventType + " with capacity " + eventCapacity);
		if (eventType.equals("Seminars") || eventType.equals("Conferences") || eventType.equals("Trade Shows")) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				String output = "";
				if (eventId.substring(0, 3).trim().equals("OTW"))
					output = generateJSONObject(managerId, eventId, eventType, eventCapacity, "None", "None", Constants.ADD_OPERATION, ottawaData.addEvent(eventId, eventType, eventCapacity));
				else
					output = generateJSONObject(managerId, eventId, eventType, eventCapacity, "None", "None", Constants.ADD_OPERATION, false);
					return output;
			} else {
				logger.info("Please Enter Proper Event Id");
				return generateJSONObject(managerId, eventId, eventType, eventCapacity, "None", "None", Constants.ADD_OPERATION, false);
			}
		} else {
			logger.info("Please Enter proper event type");
			return generateJSONObject(managerId, eventId, eventType, eventCapacity, "None", "None", Constants.ADD_OPERATION, false);
		}
	}

	public String removeEvent(String managerId, String eventId, String eventType) {
		logger.info("Remove Event Operation :  " + managerId + " has delete event with id " + eventId + " of type "
				+ eventType);
		if (eventType.equals("Seminars") || eventType.equals("Conferences") || eventType.equals("Trade Shows")) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				String output = "";
				if (eventId.substring(0, 3).trim().equals("OTW"))
					output = generateJSONObject(managerId, eventId, eventType, "None", "None", "None", Constants.REMOVE_OPERATION, ottawaData.removeEvent(eventId, eventType));
				else 
					output = generateJSONObject(managerId, eventId, eventType, "None", "None", "None", Constants.REMOVE_OPERATION, false);
				logger.info("Add Remove Operation Output : " + output);
				return output.trim();
			} else {
				logger.info("Please Enter proper Event Id");
				return generateJSONObject(managerId, eventId, eventType, "None", "None", "None", Constants.REMOVE_OPERATION, false);
			}
		} else {
			logger.info("Please Enter Proper Event Type");
			return generateJSONObject(managerId, eventId, eventType, "None", "None", "None", Constants.REMOVE_OPERATION, false);
		}
	}
	
	public String listEventAvailability(String managerId, String eventType) {
		logger.info("List  Event Operation :  " + managerId + " want to see All available list of type " + eventType);
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			String temp = "";
			if (managerId.substring(0, 3).trim().equals("OTW")) {
				temp = ottawaData.retrieveEvent(eventType).trim();
				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 8990, Constants.LIST_OPERATION)
						.trim();
				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 8991,
						Constants.LIST_OPERATION).trim();
				logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
				return temp.trim().equals("") || temp.trim().equals("No Events Found") ? "No Events Found" : temp.trim();
			} 
			logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
			return temp.trim().equals("") || temp.trim().equals("No Events Found") ? "No Events Found" : temp.trim();
		} else {
			logger.info("Please enter Event type properly");
			return "Please enter Event type properly";
		}
	}
	
	public String requestOnOtherServer(String managerId, String eventId, String eventType, String eventCapacity,
			int port, String operation) {
		DatagramSocket datagramSocket = null;
		try {
			String requestData = managerId + "," + eventId + "," + eventType + "," + eventCapacity + "," + operation;
			datagramSocket = new DatagramSocket();
			DatagramPacket packetSend = new DatagramPacket(requestData.getBytes(), requestData.getBytes().length,
					InetAddress.getByName("localhost"), port);
			datagramSocket.send(packetSend);
			byte[] buffer = new byte[65535];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			datagramSocket.receive(reply);
			String response = new String(reply.getData());
			return response.trim();
		} catch (UnknownHostException e) {
			logger.info(e.getMessage());
		} catch (SocketException e) {
			logger.info(e.getMessage());
		} catch (IOException e) {
			logger.info(e.getMessage());
		} finally {
			datagramSocket.close();
		}
		return "";		
	}

	public String eventBooking(String customerId, String eventId, String eventType) {
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			StringBuilder count = new StringBuilder();
			if (!customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 8990, "countOperation")
								+ ",");
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 8991, "countOperation")
								+ ",");
				String[] split = count.toString().trim().split(",");
				int totalEve = 0;
				for (int i = 0; i < split.length; i++) {
					totalEve += Integer.parseInt(split[i].trim());
				}
				if (totalEve >= 3) {
					return generateJSONObject(customerId, eventId, eventType, "None", "None", "None",
							Constants.BOOK_OPERATION, false);
				}
			}
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.BOOK_OPERATION, ottawaData.bookEvent(customerId, eventId, eventType));
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8990,
						Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty()? temp.trim() : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.BOOK_OPERATION, false);// + " -- > " + count;
			} 
			else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8991,
						Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty()? temp.trim() : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.BOOK_OPERATION, false);// + " -- > " + count;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8992,
						Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty()? temp.trim() : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.BOOK_OPERATION, false);// + " -- > " + count;
			}
			else {
				return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.BOOK_OPERATION, false);
			}
		} else {
			return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.BOOK_OPERATION, false);
		}
	}

	public String cancelBooking(String customerId, String eventId, String eventType) {
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.CANCEL_OPERATION, ottawaData.removeEvent(customerId, eventId, eventType));
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8990,
						Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.CANCEL_OPERATION, false);
			} 
			else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8991,
						Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.CANCEL_OPERATION, false);
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8992,
						Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.CANCEL_OPERATION, false);
			}
			else {
				return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.CANCEL_OPERATION, false);
			}
		} else {
			return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.CANCEL_OPERATION, false);
		}
}

	public String getBookingSchedule(String customerId) {
		logger.info("Booking Schedule Operation :  " + customerId);

		StringBuilder temp = new StringBuilder();
		temp.append(ottawaData.getBookingSchedule(customerId.trim()));
		temp.append(
				requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 8990, "scheduleOperation")
				.trim());
		temp.append(
				requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 8991, "scheduleOperation")
				.trim());
		logger.info("Booking Schedule for " + customerId + " : " + temp);
		return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
	}

	public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {
		boolean existanceFlag = checkEventExistance(customerID, oldEventID, oldEventType);
		System.out.println("Existance Flag : " + existanceFlag);
		if(existanceFlag == false)
			return generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
		if (customerID.trim().substring(0, 3).equals(newEventID.trim().substring(0, 3))
		/*
		 * && customerID.trim().substring(0, 3).equals(oldEventID.trim().substring(0,
		 * 3))
		 */) {
			boolean bookFlag = unpackJSON(swapEventBooking(customerID, newEventID, newEventType));
			if (bookFlag) {
				boolean cancelFlag = unpackJSON(swapCancelBooking(customerID, oldEventID, oldEventType));
				return cancelFlag ? generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, true)
						: generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
			} else {
				return generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
			}
		} /*
			 * else if
			 * (customerID.trim().substring(0,3).equals(newEventID.trim().substring(0,3)) &&
			 * !customerID.trim().substring(0,3).equals(oldEventID.trim().substring(0,3))) {
			 * boolean bookFlag = swapEventBooking(customerID, newEventID, newEventType);
			 * if(bookFlag) { boolean cancelFlag = swapCancelBooking(customerID, oldEventID,
			 * oldEventType); return cancelFlag ? customerID+
			 * " : Swap Event Operation Successful. " : customerID+
			 * " : Swap Event Operation Failure."; } else { return
			 * "Swap Operation : Unable to Book New Event ID"; } }
			 */ else if (!customerID.trim().substring(0, 3).equals(newEventID.trim().substring(0, 3))
				&& customerID.trim().substring(0, 3).equals(oldEventID.trim().substring(0, 3))) {
			boolean flag = checkMaximumLimt(customerID, newEventID);
			if (flag)
				return generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
			boolean bookFlag = unpackJSON(swapEventBooking(customerID, newEventID, newEventType));
			if (bookFlag) {
				boolean cancelFlag = unpackJSON(swapCancelBooking(customerID, oldEventID, oldEventType));
				return cancelFlag ? generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, true)
						: generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
			} else {
				return generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
			}
		} else if (!customerID.trim().substring(0, 3).equals(newEventID.trim().substring(0, 3))
				&& !customerID.trim().substring(0, 3).equals(oldEventID.trim().substring(0, 3))) {
			if (newEventID.trim().substring(6, newEventID.length()).equals(oldEventID.trim().substring(6, oldEventID.trim().length()))) {
				boolean bookFlag = unpackJSON(swapEventBooking(customerID, newEventID, newEventType));
				if (bookFlag) {
					boolean cancelFlag = unpackJSON(swapCancelBooking(customerID, oldEventID, oldEventType));
					return cancelFlag ? generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, true)
							: generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
				} else {
					return generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
				}
			} else {
				boolean flag = checkMaximumLimt(customerID, newEventID);
				if (flag) {
					return generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
				}
				else {
					boolean bookFlag = unpackJSON(swapEventBooking(customerID, newEventID, newEventType));
					if (bookFlag) {
						boolean cancelFlag = unpackJSON(swapCancelBooking(customerID, oldEventID, oldEventType));
						return cancelFlag ? generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, true)
								: generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
					} else {
						return generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
					}	
				}
			}
		}
		return generateJSONObject(customerID, newEventID, newEventType, "None", oldEventID, oldEventType, Constants.SWAP_OPERATION, false);
	}

	public boolean checkMaximumLimt(String customerId, String eventId) {
		StringBuilder count = new StringBuilder();
		if (!customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
			if (customerId.trim().substring(0, 3).equals("TOR")) {
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 8991, "countOperation")
								+ ",");
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 8992, "countOperation")
								+ ",");
			} else if (customerId.trim().substring(0, 3).equals("MTL")) {
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 8990, "countOperation")
								+ ",");
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 8992, "countOperation")
								+ ",");
			} else if (customerId.trim().substring(0, 3).equals("OTW")) {
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 8990, "countOperation")
								+ ",");
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 8991, "countOperation")
								+ ",");
			}
			String[] split = count.toString().trim().split(",");
			int totalEve = 0;
			for (int i = 0; i < split.length; i++) {
				totalEve += Integer.parseInt(split[i].trim());
			}
			if (totalEve >= 3) {
				return true;
			}
		}
		return false;
	}
	public boolean checkEventExistance(String customerID, String eventId, String eventType) {
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			if (customerID.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				boolean temp = false;
				temp = ottawaData.getEvent(customerID, eventId, eventType);
				return temp == false ? temp : true;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerID, eventId, eventType, "No Capacity", 8990,
						"existanceOperation");
				return temp.trim().equals("Denies") ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerID, eventId, eventType, "No Capacity", 8991,
						"existanceOperation");
				return temp.trim().equals("Denies") ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerID, eventId, eventType, "No Capacity", 8992,
						"existanceOperation");
				System.out.println(temp);
				return temp.trim().equals("Denies") ? false : true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	public String swapEventBooking(String customerId, String eventId, String eventType) {
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, ottawaData.bookEvent(customerId, eventId, eventType));
				//return !temp.trim().isEmpty() && temp.contains("has book event") ? true : false;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8990,
						Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty() ? temp.trim() : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
			}
			else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8991,
						Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty() ? temp.trim() : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8992,
						Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty() ? temp.trim() : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
			} 
			else {
				return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
			}
		} else {
			return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
		}
	}

	public String swapCancelBooking(String customerId, String eventId, String eventType){

		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				return generateJSONObject(customerId, eventId, eventType, "None","None", "None", Constants.SWAP_OPERATION, ottawaData.removeEvent(customerId, eventId, eventType));
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8990,
						Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
			} 
			else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8991,
						Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 8992,
						Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp : generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
			} 
			else {
				return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
			}
		} else {
			return generateJSONObject(customerId, eventId, eventType, "None", "None", "None", Constants.SWAP_OPERATION, false);
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
		obj.put("status", status);
		return obj.toString();
	}
	
	static boolean unpackJSON(String jsonString) {
		Object obj = null;
		try {
			obj = new JSONParser().parse(jsonString.trim());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonObject = (JSONObject) obj;
		return Boolean.parseBoolean(jsonObject.get("status").toString().trim());
	}
}
