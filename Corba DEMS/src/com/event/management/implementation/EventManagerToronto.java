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

import com.event.management.model.TorontoData;

public class EventManagerToronto {
//	public String location;
	public String response;
	public TorontoData torontoData;
	private static Logger logger;

	public EventManagerToronto() {
		super();
		torontoData = new TorontoData();
		setLogger("logs/TOR.txt", "TOR");		
	}
	
	public String addEvent(String managerId, String eventId, String eventType, String eventCapacity) {
		logger.info("Add Event Operation :  " + managerId + " has started creating event with id " + eventId
				+ " of type " + eventType + " with capacity " + eventCapacity);
		if (eventType.equals("Seminars") || eventType.equals("Conferences") || eventType.equals("Trade Shows")) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				String output = "";
//				if (eventId.substring(0, 3).trim().equals("TOR"))
					output = torontoData.addEvent(eventId, eventType, eventCapacity);
//				else if (eventId.substring(0, 3).trim().equals("MTL"))
//					output = montrealData.addEvent(eventId, eventType, eventCapacity);
//				else if (eventId.substring(0, 3).trim().equals("OTW"))
//					output = ottawaData.addEvent(eventId, eventType, eventCapacity);
				logger.info("Add Event Operation Output : " + output);
				return output;
			} else {
				logger.info("Please Enter Proper Event Id");
				return "Please Enter Proper Event Id";
			}
		} else {
			logger.info("Please Enter proper event type");
			return "Please Enter proper event type";
		}
	}

	public String removeEvent(String managerId, String eventId, String eventType) {
		logger.info("Remove Event Operation :  " + managerId + " has delete event with id " + eventId + " of type "
				+ eventType);
		if (eventType.equals("Seminars") || eventType.equals("Conferences") || eventType.equals("Trade Shows")) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				String output = "";
//				if (eventId.substring(0, 3).trim().equals("TOR"))
					output = torontoData.removeEvent(eventId, eventType);
//				else if (eventId.substring(0, 3).trim().equals("MTL"))
//					output = montrealData.removeEvent(eventId, eventType);
//				else if (eventId.substring(0, 3).trim().equals("OTW"))
//					output = ottawaData.removeEvent(eventId, eventType);
				logger.info("Add Remove Operation Output : " + output);
				return output.trim();
			} else {
				logger.info("Please Enter proper Event Id");
				return "Please Enter Proper Event Id";
			}
		} else {
			logger.info("Please Enter Proper Event Type");
			return "Please Enter proper event type";
		}
	}

	public String listEventAvailability(String managerId, String eventType) {
		logger.info("List  Event Operation :  " + managerId + " want to see All available list of type " + eventType);
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			String temp = "";
//			if (managerId.substring(0, 3).trim().equals("TOR")) {
				temp = torontoData.retrieveEvent(eventType).trim();
				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9991, "listOperation")
						.trim();
				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9992,
						"listOperation").trim();
				logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
				return temp.trim().equals("") ? "No Events Available" : temp.trim();			 
//			} else if (managerId.substring(0, 3).trim().equals("MTL")) {
//				temp = montrealData.retrieveEvent(eventType).trim();
//				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9990, "listOperation")
//						.trim();
//				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9992,
//						"listOperation").trim();
//				logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
//				return temp.trim().equals("") ? "No Events Available" : temp.trim();
//			} else if (managerId.substring(0, 3).trim().equals("OTW")) {
//				temp = ottawaData.retrieveEvent(eventType).trim();
//				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9990, "listOperation")
//						.trim();
//				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9991,
//						"listOperation").trim();
//				logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
//				return temp.trim().equals("") ? "No Events Available" : temp.trim();
//			}
//			logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
//			return temp.trim().equals("") ? "No Events Available" : temp.trim();

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
//				if (customerId.trim().substring(0, 3).equals("TOR")) {
					count.append(
							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")
							+ ",");
					count.append(
							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")
							+ ",");
//				} else if (customerId.trim().substring(0, 3).equals("MTL")) {
//					count.append(
//							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")
//							+ ",");
//					count.append(
//							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")
//							+ ",");
//				} else if (customerId.trim().substring(0, 3).equals("OTW")) {
//					count.append(
//							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")
//							+ ",");
//					count.append(
//							requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")
//							+ ",");
//				}
				String[] split = count.toString().trim().split(",");
				int totalEve = 0;
				for (int i = 0; i < split.length; i++) {
					totalEve += Integer.parseInt(split[i].trim());
				}
				if (totalEve >= 3) {
					return "you have already reached maximum limit of Current Month Outside city registration ";
				}
			}

			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				String temp = "";
//				if (eventId.trim().substring(0, 3).equals("TOR")) {
					temp = torontoData.bookEvent(customerId, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("MTL")) {
//					temp = montrealData.bookEvent(customerId, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("OTW")) {
//					temp = ottawaData.bookEvent(customerId, eventId, eventType);
//				}
				return temp == "" ? "Unable to Book  Event" : temp.trim();
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp.trim();// + " -- > " + count;
			} 
//			else if (eventId.trim().substring(0, 3).equals("MTL")) {
//				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
//						"bookOperation");
//				return temp == "" ? "Unable to Book  Event" : temp.trim();// + " -- > " + count;
//			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
//				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
//						"bookOperation");
//				return temp == "" ? "Unable to Book  Event" : temp.trim();// + " -- > " + count;
//			}
			else {
				return "Please Enter Event ID Properly";
			}

		} else {
			return "Please enter Event type properly";
		}
	}

	public String cancelBooking(String customerId, String eventId, String eventType) {
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				String temp = "";
//				if (eventId.trim().substring(0, 3).equals("TOR")) {
					temp = torontoData.removeEvent(customerId, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("MTL")) {
//					temp = montrealData.removeEvent(customerId, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("OTW")) {
//					temp = ottawaData.removeEvent(customerId, eventId, eventType);
//				}
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"cancelOperation");
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} 
//			else if (eventId.trim().substring(0, 3).equals("MTL")) {
//				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
//						"cancelOperation");
//				return temp == "" ? "Unable to Cancel  Event" : temp;
//			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
//				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
//						"cancelOperation");
//				return temp == "" ? "Unable to Cancel  Event" : temp;
//
//			}
			else {
				return "Please Enter Event ID Properly";
			}

		} else {
			return "Please enter Event type properly";
		}
}

	public String getBookingSchedule(String customerId) {
		logger.info("Booking Schedule Operation :  " + customerId);

		StringBuilder temp = new StringBuilder();
//		if (customerId.substring(0, 3).trim().equals("TOR")) {
			temp.append(torontoData.getBookingSchedule(customerId.trim()));
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9991,
					"scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9992,
					"scheduleOperation").trim());
			logger.info("Booking Schedule for " + customerId + " : " + temp);
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
//		} else if (customerId.substring(0, 3).trim().equals("MTL")) {
//			temp.append(montrealData.getBookingSchedule(customerId.trim()));
//			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9990,
//					"scheduleOperation").trim());
//			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9992,
//					"scheduleOperation").trim());
//			logger.info("Booking Schedule for " + customerId + " : " + temp);
//			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
//		} else if (customerId.substring(0, 3).trim().equals("OTW")) {
//			temp.append(ottawaData.getBookingSchedule(customerId.trim()));
//			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9990,
//					"scheduleOperation").trim());
//			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9991,
//					"scheduleOperation").trim());
//			logger.info("Booking Schedule for " + customerId + " : " + temp);
//			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
//		}
//		logger.info("Booking Schedule for " + customerId + " : " + temp);
//		return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
	}

	public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) {
		boolean existanceFlag = checkEventExistance(customerID, oldEventID, oldEventType);
		System.out.println("Existance Flag : " + existanceFlag);
		if(existanceFlag == false)
			return "Some error might occur. Please check Data and Try again";
		if (customerID.trim().substring(0, 3).equals(newEventID.trim().substring(0, 3))
		/*
		 * && customerID.trim().substring(0, 3).equals(oldEventID.trim().substring(0,
		 * 3))
		 */) {
			boolean bookFlag = swapEventBooking(customerID, newEventID, newEventType);
			if (bookFlag) {
				boolean cancelFlag = swapCancelBooking(customerID, oldEventID, oldEventType);
				return cancelFlag ? customerID + " : Swap Event Operation Successful. "
						: customerID + " : Swap Event Operation Failure.";
			} else {
				return "Swap Operation : Unable to Book New Event ID";
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
				return "you have already reached maximum limit of Current Month Outside city registration ";
			boolean bookFlag = swapEventBooking(customerID, newEventID, newEventType);
			if (bookFlag) {
				boolean cancelFlag = swapCancelBooking(customerID, oldEventID, oldEventType);
				return cancelFlag ? customerID + " : Swap Event Operation Successful. "
						: customerID + " : Swap Event Operation Failure.";
			} else {
				return "Swap Operation : Unable to Book New Event ID";
			}
		} else if (!customerID.trim().substring(0, 3).equals(newEventID.trim().substring(0, 3))
				&& !customerID.trim().substring(0, 3).equals(oldEventID.trim().substring(0, 3))) {
			if (newEventID.trim().substring(6, newEventID.length()).equals(oldEventID.trim().substring(6, oldEventID.trim().length()))) {
				boolean bookFlag = swapEventBooking(customerID, newEventID, newEventType);
				if (bookFlag) {
					boolean cancelFlag = swapCancelBooking(customerID, oldEventID, oldEventType);
					return cancelFlag ? customerID + " : Swap Event Operation Successful. "
							: customerID + " : Swap Event Operation Failure.";
				} else {
					return "Swap Operation : Unable to Book New Event ID";
				}
			} else {
				boolean flag = checkMaximumLimt(customerID, newEventID);
				if (flag) {
					return "you have already reached maximum limit of Current Month Outside city registration ";
				}
				else {
					boolean bookFlag = swapEventBooking(customerID, newEventID, newEventType);
					if (bookFlag) {
						boolean cancelFlag = swapCancelBooking(customerID, oldEventID, oldEventType);
						return cancelFlag ? customerID + " : Swap Event Operation Successful. "
								: customerID + " : Swap Event Operation Failure.";
					} else {
						return "Swap Operation : Unable to Book New Event ID";
					}	
				}
			}
		}
		return "Some error might occur. Please check Data and Try again";
	}

	public boolean checkMaximumLimt(String customerId, String eventId) {
		StringBuilder count = new StringBuilder();
		if (!customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
			if (customerId.trim().substring(0, 3).equals("TOR")) {
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")
								+ ",");
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")
								+ ",");
			} else if (customerId.trim().substring(0, 3).equals("MTL")) {
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")
								+ ",");
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")
								+ ",");
			} else if (customerId.trim().substring(0, 3).equals("OTW")) {
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")
								+ ",");
				count.append(
						requestOnOtherServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")
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
//				if (eventId.trim().substring(0, 3).equals("TOR")) {
					temp = torontoData.getEvent(customerID, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("MTL")) {
//					temp = montrealData.getEvent(customerID, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("OTW")) {
//					temp = ottawaData.getEvent(customerID, eventId, eventType);
//				}
				return temp == false ? temp : true;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerID, eventId, eventType, "No Capacity", 9990,
						"existanceOperation");
				return temp.trim().equals("Denies") ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerID, eventId, eventType, "No Capacity", 9991,
						"existanceOperation");
				return temp.trim().equals("Denies") ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerID, eventId, eventType, "No Capacity", 9992,
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
	public boolean swapEventBooking(String customerId, String eventId, String eventType) {
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				String temp = "";
//				if (eventId.trim().substring(0, 3).equals("TOR")) {
					temp = torontoData.bookEvent(customerId, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("MTL")) {
//					temp = montrealData.bookEvent(customerId, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("OTW")) {
//					temp = ottawaData.bookEvent(customerId, eventId, eventType);
//				}
				return !temp.trim().isEmpty() && temp.contains("has book event") ? true : false;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"bookOperation");
				return !temp.trim().isEmpty() && temp.contains("has book event") ? true : false;
			}
//			else if (eventId.trim().substring(0, 3).equals("MTL")) {
//				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
//						"bookOperation");
//				return !temp.trim().isEmpty() && temp.contains("has book event") ? true : false;
//			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
//				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
//						"bookOperation");
//				return !temp.trim().isEmpty() && temp.contains("has book event") ? true : false;
//			} 
			else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean swapCancelBooking(String customerId, String eventId, String eventType){

		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				String temp = "";
//				if (eventId.trim().substring(0, 3).equals("TOR")) {
					temp = torontoData.removeEvent(customerId, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("MTL")) {
//					temp = montrealData.removeEvent(customerId, eventId, eventType);
//				} else if (eventId.trim().substring(0, 3).equals("OTW")) {
//					temp = ottawaData.removeEvent(customerId, eventId, eventType);
//				}
				return temp.trim().isEmpty() ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"cancelOperation");
				return temp.trim().isEmpty() ? false : true;
			} 
//			else if (eventId.trim().substring(0, 3).equals("MTL")) {
//				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
//						"cancelOperation");
//				return temp.trim().isEmpty() ? false : true;
//			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
//				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
//						"cancelOperation");
//				return temp.trim().isEmpty() ? false : true;
//			} 
			else {
				return false;
			}

		} else {
			return false;
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
}
