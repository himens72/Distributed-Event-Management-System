package com.event.management.toronto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.event.management.constants.Constants;

public class TORManager {
	public String response;
	public TORDB torontoData;
	private static Logger logger;

	public TORManager() {
		super();
		torontoData = new TORDB();
		setLogger("logs/TOR.txt", "TOR");
	}

	public String addEvent(String managerId, String eventId, String eventType, String eventCapacity) {
		if (eventType.equals(Constants.SEMINARS) || eventType.equals(Constants.CONFERENCES) || eventType.equals(Constants.TRADE_SHOWS)) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				String output = "";
				if (eventId.substring(0, 3).trim().equals("TOR"))
					output = generateJSONObject(managerId, eventId, eventType, eventCapacity, Constants.NONE,
							Constants.NONE, Constants.ADD_OPERATION,
							torontoData.addEvent(eventId, eventType, eventCapacity));
				else
					output = generateJSONObject(managerId, eventId, eventType, eventCapacity, Constants.NONE,
							Constants.NONE, Constants.ADD_OPERATION, false);
				return output;
			} else {
				return generateJSONObject(managerId, eventId, eventType, eventCapacity, Constants.NONE, Constants.NONE,
						Constants.ADD_OPERATION, false);
			}
		} else {
			return generateJSONObject(managerId, eventId, eventType, eventCapacity, Constants.NONE, Constants.NONE,
					Constants.ADD_OPERATION, false);
		}
	}

	public String removeEvent(String managerId, String eventId, String eventType) {
		if (eventType.equals(Constants.SEMINARS) || eventType.equals(Constants.CONFERENCES) || eventType.equals(Constants.TRADE_SHOWS)) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				String output = "";
				if (eventId.substring(0, 3).trim().equals("TOR"))
					output = generateJSONObject(managerId, eventId, eventType, Constants.NONE, Constants.NONE,
							Constants.NONE, Constants.REMOVE_OPERATION, torontoData.removeEvent(eventId, eventType));
				else
					output = generateJSONObject(managerId, eventId, eventType, Constants.NONE, Constants.NONE,
							Constants.NONE, Constants.REMOVE_OPERATION, false);
				return output.trim();
			} else {
				return generateJSONObject(managerId, eventId, eventType, Constants.NONE, Constants.NONE, Constants.NONE,
						Constants.REMOVE_OPERATION, false);
			}
		} else {
			return generateJSONObject(managerId, eventId, eventType, Constants.NONE, Constants.NONE, Constants.NONE,
					Constants.REMOVE_OPERATION, false);
		}
	}

	public String listEventAvailability(String managerId, String eventType) {
		if (eventType.trim().equals(Constants.SEMINARS) || eventType.trim().equals(Constants.CONFERENCES)
				|| eventType.trim().equals(Constants.TRADE_SHOWS)) {
			String temp = "";
			temp = torontoData.retrieveEvent(eventType).trim();
			temp += requestOnOtherServer(managerId, Constants.NONE, eventType, Constants.NONE,
					Constants.LOCAL_MONTREAL_PORT, Constants.LIST_OPERATION).trim();
			temp = temp + requestOnOtherServer(managerId, Constants.NONE, eventType, Constants.NONE,
					Constants.LOCAL_OTTAWA_PORT, Constants.LIST_OPERATION).trim();
			boolean status = temp.trim().isEmpty() ? false : true;
			return eventAvailableJSONObject(managerId, eventType, temp, Constants.LIST_OPERATION, status);
		} else {
			return eventAvailableJSONObject(managerId, eventType, "", Constants.LIST_OPERATION, false);
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
		if (eventType.trim().equals(Constants.SEMINARS) || eventType.trim().equals(Constants.CONFERENCES)
				|| eventType.trim().equals(Constants.TRADE_SHOWS)) {
			StringBuilder count = new StringBuilder();
			if (!customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				if (customerId.trim().substring(0, 3).equals("TOR")) {
					count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
							Constants.LOCAL_MONTREAL_PORT, "countOperation") + ",");
					count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
							Constants.LOCAL_OTTAWA_PORT, "countOperation") + ",");
				} else if (customerId.trim().substring(0, 3).equals("MTL")) {
					count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
							Constants.LOCAL_TORONTO_PORT, "countOperation") + ",");
					count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
							Constants.LOCAL_OTTAWA_PORT, "countOperation") + ",");
				} else if (customerId.trim().substring(0, 3).equals("OTW")) {
					count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
							Constants.LOCAL_TORONTO_PORT, "countOperation") + ",");
					count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
							Constants.LOCAL_MONTREAL_PORT, "countOperation") + ",");
				}
				String[] split = count.toString().trim().split(",");
				int totalEve = 0;
				for (int i = 0; i < split.length; i++) {
					totalEve += Integer.parseInt(split[i].trim());
				}
				if (totalEve >= 3) {
					return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
							Constants.NONE, Constants.BOOK_OPERATION, false);
				}
			}

			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
						Constants.NONE, Constants.BOOK_OPERATION,
						torontoData.bookEvent(customerId, eventId, eventType));
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_TORONTO_PORT, Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty() ? temp.trim()
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.BOOK_OPERATION, false);// + " -- > " + count;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_MONTREAL_PORT, Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty() ? temp.trim()
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.BOOK_OPERATION, false);// + " -- > " + count;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_OTTAWA_PORT, Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty() ? temp.trim()
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.BOOK_OPERATION, false);// + " -- > " + count;
			} else {
				return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
						Constants.NONE, Constants.BOOK_OPERATION, false);
			}
		} else {
			return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE, Constants.NONE,
					Constants.BOOK_OPERATION, false);
		}
	}

	public String cancelBooking(String customerId, String eventId, String eventType) {
		if (eventType.trim().equals(Constants.SEMINARS) || eventType.trim().equals(Constants.CONFERENCES)
				|| eventType.trim().equals(Constants.TRADE_SHOWS)) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
						Constants.NONE, Constants.CANCEL_OPERATION,
						torontoData.removeEvent(customerId, eventId, eventType));
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_TORONTO_PORT, Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.CANCEL_OPERATION, false);
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_MONTREAL_PORT, Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.CANCEL_OPERATION, false);
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_OTTAWA_PORT, Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.CANCEL_OPERATION, false);
			} else {
				return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
						Constants.NONE, Constants.CANCEL_OPERATION, false);
			}
		} else {
			return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE, Constants.NONE,
					Constants.CANCEL_OPERATION, false);
		}
	}

	public String getBookingSchedule(String customerId) {
		StringBuilder temp = new StringBuilder();
		temp.append(torontoData.getBookingSchedule(customerId.trim()));
		temp.append(requestOnOtherServer(customerId, Constants.NONE, Constants.NONE, Constants.NONE,
				Constants.LOCAL_MONTREAL_PORT, Constants.SCHEDULE_OPERATION).trim());
		temp.append(requestOnOtherServer(customerId, Constants.NONE, Constants.NONE, Constants.NONE,
				Constants.LOCAL_OTTAWA_PORT, Constants.SCHEDULE_OPERATION).trim());
		return temp.toString().trim().length() == 0
				? eventScheduleJSONObject(customerId, "", Constants.SCHEDULE_OPERATION, false)
						: eventScheduleJSONObject(customerId, temp.toString().trim(), Constants.SCHEDULE_OPERATION, true);
	}

	public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {
		boolean existanceFlag = checkEventExistance(customerID, oldEventID, oldEventType);
		if (existanceFlag == false)
			return generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID, oldEventType,
					Constants.SWAP_OPERATION, false);
		if (customerID.trim().substring(0, 3).equals(newEventID.trim().substring(0, 3))) {
			boolean bookFlag = unpackJSON(swapEventBooking(customerID, newEventID, newEventType));
			if (bookFlag) {
				boolean cancelFlag = unpackJSON(swapCancelBooking(customerID, oldEventID, oldEventType));
				return cancelFlag
						? generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
								oldEventType, Constants.SWAP_OPERATION, true)
								: generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
										oldEventType, Constants.SWAP_OPERATION, false);
			} else {
				return generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
						oldEventType, Constants.SWAP_OPERATION, false);
			}
		} else if (!customerID.trim().substring(0, 3).equals(newEventID.trim().substring(0, 3))
				&& customerID.trim().substring(0, 3).equals(oldEventID.trim().substring(0, 3))) {
			boolean flag = checkMaximumLimt(customerID, newEventID);
			if (flag)
				return generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
						oldEventType, Constants.SWAP_OPERATION, false);
			boolean bookFlag = unpackJSON(swapEventBooking(customerID, newEventID, newEventType));
			if (bookFlag) {
				boolean cancelFlag = unpackJSON(swapCancelBooking(customerID, oldEventID, oldEventType));
				return cancelFlag
						? generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
								oldEventType, Constants.SWAP_OPERATION, true)
								: generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
										oldEventType, Constants.SWAP_OPERATION, false);
			} else {
				return generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
						oldEventType, Constants.SWAP_OPERATION, false);
			}
		} else if (!customerID.trim().substring(0, 3).equals(newEventID.trim().substring(0, 3))
				&& !customerID.trim().substring(0, 3).equals(oldEventID.trim().substring(0, 3))) {
			if (newEventID.trim().substring(6, newEventID.length())
					.equals(oldEventID.trim().substring(6, oldEventID.trim().length()))) {
				boolean bookFlag = unpackJSON(swapEventBooking(customerID, newEventID, newEventType));
				if (bookFlag) {
					boolean cancelFlag = unpackJSON(swapCancelBooking(customerID, oldEventID, oldEventType));
					return cancelFlag
							? generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
									oldEventType, Constants.SWAP_OPERATION, true)
									: generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
											oldEventType, Constants.SWAP_OPERATION, false);
				} else {
					return generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
							oldEventType, Constants.SWAP_OPERATION, false);
				}
			} else {
				boolean flag = checkMaximumLimt(customerID, newEventID);
				if (flag) {
					return generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
							oldEventType, Constants.SWAP_OPERATION, false);
				} else {
					boolean bookFlag = unpackJSON(swapEventBooking(customerID, newEventID, newEventType));
					if (bookFlag) {
						boolean cancelFlag = unpackJSON(swapCancelBooking(customerID, oldEventID, oldEventType));
						return cancelFlag
								? generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
										oldEventType, Constants.SWAP_OPERATION, true)
										: generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
												oldEventType, Constants.SWAP_OPERATION, false);
					} else {
						return generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID,
								oldEventType, Constants.SWAP_OPERATION, false);
					}
				}
			}
		}
		return generateJSONObject(customerID, newEventID, newEventType, Constants.NONE, oldEventID, oldEventType,
				Constants.SWAP_OPERATION, false);
	}

	public boolean checkMaximumLimt(String customerId, String eventId) {
		StringBuilder count = new StringBuilder();
		if (!customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
			if (customerId.trim().substring(0, 3).equals("TOR")) {
				count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
						Constants.LOCAL_MONTREAL_PORT, "countOperation") + ",");
				count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
						Constants.LOCAL_OTTAWA_PORT, "countOperation") + ",");
			} else if (customerId.trim().substring(0, 3).equals("MTL")) {
				count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
						Constants.LOCAL_TORONTO_PORT, "countOperation") + ",");
				count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
						Constants.LOCAL_OTTAWA_PORT, "countOperation") + ",");
			} else if (customerId.trim().substring(0, 3).equals("OTW")) {
				count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
						Constants.LOCAL_TORONTO_PORT, "countOperation") + ",");
				count.append(requestOnOtherServer(customerId, eventId, Constants.NONE, Constants.NONE,
						Constants.LOCAL_MONTREAL_PORT, "countOperation") + ",");
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
		if (eventType.trim().equals(Constants.SEMINARS) || eventType.trim().equals(Constants.CONFERENCES)
				|| eventType.trim().equals(Constants.TRADE_SHOWS)) {
			if (customerID.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				boolean temp = false;
				temp = torontoData.getEvent(customerID, eventId, eventType);
				return temp == false ? temp : true;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerID, eventId, eventType, Constants.NONE,
						Constants.LOCAL_TORONTO_PORT, "existanceOperation");
				return temp.trim().equals("Denies") ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerID, eventId, eventType, Constants.NONE,
						Constants.LOCAL_MONTREAL_PORT, "existanceOperation");
				return temp.trim().equals("Denies") ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerID, eventId, eventType, Constants.NONE,
						Constants.LOCAL_OTTAWA_PORT, "existanceOperation");
				// System.out.println(temp);
				return temp.trim().equals("Denies") ? false : true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public String swapEventBooking(String customerId, String eventId, String eventType) {
		if (eventType.trim().equals(Constants.SEMINARS) || eventType.trim().equals(Constants.CONFERENCES)
				|| eventType.trim().equals(Constants.TRADE_SHOWS)) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
						Constants.NONE, Constants.SWAP_OPERATION,
						torontoData.bookEvent(customerId, eventId, eventType));
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_TORONTO_PORT, Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty() ? temp.trim()
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.SWAP_OPERATION, false);
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_MONTREAL_PORT, Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty() ? temp.trim()
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.SWAP_OPERATION, false);
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_OTTAWA_PORT, Constants.BOOK_OPERATION);
				return !temp.trim().isEmpty() ? temp.trim()
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.SWAP_OPERATION, false);
			} else {
				return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
						Constants.NONE, Constants.SWAP_OPERATION, false);
			}
		} else {
			return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE, Constants.NONE,
					Constants.SWAP_OPERATION, false);
		}
	}

	public String swapCancelBooking(String customerId, String eventId, String eventType) {

		if (eventType.trim().equals(Constants.SEMINARS) || eventType.trim().equals(Constants.CONFERENCES)
				|| eventType.trim().equals(Constants.TRADE_SHOWS)) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
						Constants.NONE, Constants.SWAP_OPERATION,
						torontoData.removeEvent(customerId, eventId, eventType));
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_TORONTO_PORT, Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.SWAP_OPERATION, false);
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_MONTREAL_PORT, Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.SWAP_OPERATION, false);
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, Constants.NONE,
						Constants.LOCAL_OTTAWA_PORT, Constants.CANCEL_OPERATION);
				return !temp.trim().isEmpty() ? temp
						: generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
								Constants.NONE, Constants.SWAP_OPERATION, false);
			} else {
				return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE,
						Constants.NONE, Constants.SWAP_OPERATION, false);
			}
		} else {
			return generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE, Constants.NONE,
					Constants.SWAP_OPERATION, false);
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
		obj.put(Constants.OPERATION_STATUS, status);
		return obj.toString();
	}

	static String eventAvailableJSONObject(String id, String eventType, String events, String operation,
			boolean status) {
		JSONObject obj = new JSONObject();
		obj.put(Constants.ID, id.trim());
		obj.put(Constants.EVENT_TYPE, eventType.trim());
		obj.put(Constants.LIST_EVENT_AVAILABLE, events.trim());
		obj.put(Constants.OPERATION, operation.trim());
		obj.put(Constants.OPERATION_STATUS, status);
		return obj.toString();
	}

	static String eventScheduleJSONObject(String id, String events, String operation, boolean status) {
		JSONObject obj = new JSONObject();
		obj.put(Constants.ID, id.trim());
		String[] splitEvents = events.trim().split(",");
		ArrayList<String> temp = new ArrayList<String>();
		for (int i = 0; i < splitEvents.length; i++) {
			temp.add(splitEvents[i].replaceAll("\\s+", "").trim());
		}
		Collections.sort(temp);
		obj.put(Constants.LIST_EVENT_SCHEDULE, temp.toString().trim());
		obj.put(Constants.OPERATION, operation.trim());
		obj.put(Constants.OPERATION_STATUS, status);
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
		return Boolean.parseBoolean(jsonObject.get(Constants.OPERATION_STATUS).toString().trim());
	}
}
