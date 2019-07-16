package com.event.management.model;

import java.net.DatagramPacket;

public class EventManagementTestCases {
	TorontoData torontoData;
	MontrealData montrealData;
	OttawaData ottawaData;
	public EventManagementTestCases() {
		// TODO Auto-generated constructor stub
		torontoData = new TorontoData();
				montrealData = new MontrealData();
		ottawaData = new OttawaData();
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
						requestOnMontrealServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")
								+ ",");
				count.append(
						requestOnOttawaServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")
								+ ",");
			} else if (customerId.trim().substring(0, 3).equals("MTL")) {
				count.append(
						requestOnTorontoServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")
								+ ",");
				count.append(
						requestOnOttawaServer(customerId, eventId, "No Types", "No Capacity", 9992, "countOperation")
								+ ",");
			} else if (customerId.trim().substring(0, 3).equals("OTW")) {
				count.append(
						requestOnTorontoServer(customerId, eventId, "No Types", "No Capacity", 9990, "countOperation")
								+ ",");
				count.append(
						requestOnMontrealServer(customerId, eventId, "No Types", "No Capacity", 9991, "countOperation")
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
				if (eventId.trim().substring(0, 3).equals("TOR")) {
					temp = torontoData.getEvent(customerID, eventId, eventType);
				} else if (eventId.trim().substring(0, 3).equals("MTL")) {
					temp = montrealData.getEvent(customerID, eventId, eventType);
				} else if (eventId.trim().substring(0, 3).equals("OTW")) {
					temp = ottawaData.getEvent(customerID, eventId, eventType);
				}
				return temp == false ? temp : true;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnTorontoServer(customerID, eventId, eventType, "No Capacity", 9990,
						"existanceOperation");
				return temp.trim().equals("Denies") ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnMontrealServer(customerID, eventId, eventType, "No Capacity", 9991,
						"existanceOperation");
				return temp.trim().equals("Denies") ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOttawaServer(customerID, eventId, eventType, "No Capacity", 9992,
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
				if (eventId.trim().substring(0, 3).equals("TOR")) {
					temp = torontoData.bookEvent(customerId, eventId, eventType);
				} else if (eventId.trim().substring(0, 3).equals("MTL")) {
					temp = montrealData.bookEvent(customerId, eventId, eventType);
				} else if (eventId.trim().substring(0, 3).equals("OTW")) {
					temp = ottawaData.bookEvent(customerId, eventId, eventType);
				}
				return !temp.trim().isEmpty() && temp.contains("has book event") ? true : false;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnTorontoServer(customerId, eventId, eventType, "No Capacity", 9990,
						"bookOperation");
				return !temp.trim().isEmpty() && temp.contains("has book event") ? true : false;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnMontrealServer(customerId, eventId, eventType, "No Capacity", 9991,
						"bookOperation");
				return !temp.trim().isEmpty() && temp.contains("has book event") ? true : false;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOttawaServer(customerId, eventId, eventType, "No Capacity", 9992,
						"bookOperation");
				return !temp.trim().isEmpty() && temp.contains("has book event") ? true : false;
			} else {
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
				if (eventId.trim().substring(0, 3).equals("TOR")) {
					temp = torontoData.removeEvent(customerId, eventId, eventType);
				} else if (eventId.trim().substring(0, 3).equals("MTL")) {
					temp = montrealData.removeEvent(customerId, eventId, eventType);
				} else if (eventId.trim().substring(0, 3).equals("OTW")) {
					temp = ottawaData.removeEvent(customerId, eventId, eventType);
				}
				return temp.trim().isEmpty() ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnTorontoServer(customerId, eventId, eventType, "No Capacity", 9990,
						"cancelOperation");
				return temp.trim().isEmpty() ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnMontrealServer(customerId, eventId, eventType, "No Capacity", 9991,
						"cancelOperation");
				return temp.trim().isEmpty() ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOttawaServer(customerId, eventId, eventType, "No Capacity", 9992,
						"cancelOperation");
				return temp.trim().isEmpty() ? false : true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}
	private String requestOnMontrealServer(String customerId, String eventId, String eventType, String capacity, int port,
			String operation) {
		// TODO Auto-generated method stub
		String temp = "";
		if (operation.trim().equals("listOperation")) {
			temp = montrealData.retrieveEvent(eventType);
		} else if (operation.trim().equals("addOperation")) {
			 temp = montrealData.addEvent(eventId, eventType, capacity);
		} else if (operation.trim().equals("bookOperation")) {
			 temp = montrealData.bookEvent(customerId, eventId, eventType);
		} else if (operation.trim().equals("cancelOperation")) {
			 temp = montrealData.removeEvent(customerId, eventId, eventType);
		} else if (operation.trim().equals("scheduleOperation")) {
			 temp = montrealData.getBookingSchedule(customerId);
		} else if (operation.trim().equals("countOperation")) {
			 temp = montrealData.getBookingCount(customerId, eventId);
		} else if (operation.trim().equals("existanceOperation")) {
			boolean newTemp = montrealData.getEvent(customerId, eventId, eventType);
			temp = newTemp == false ? "Denies" : "Approves";
		}
		return temp;
	}
	private String requestOnOttawaServer(String customerId, String eventId, String eventType, String capacity, int port,
			String operation) {
		// TODO Auto-generated method stub
		String temp = "";
		if (operation.trim().equals("listOperation")) {
			temp = ottawaData.retrieveEvent(eventType);
		} else if (operation.trim().equals("addOperation")) {
			 temp = ottawaData.addEvent(eventId, eventType, capacity);
		} else if (operation.trim().equals("bookOperation")) {
			 temp = ottawaData.bookEvent(customerId, eventId, eventType);
		} else if (operation.trim().equals("cancelOperation")) {
			 temp = ottawaData.removeEvent(customerId, eventId, eventType);
		} else if (operation.trim().equals("scheduleOperation")) {
			 temp = ottawaData.getBookingSchedule(customerId);
		} else if (operation.trim().equals("countOperation")) {
			 temp = ottawaData.getBookingCount(customerId, eventId);
		} else if (operation.trim().equals("existanceOperation")) {
			boolean newTemp = montrealData.getEvent(customerId, eventId, eventType);
			temp = newTemp == false ? "Denies" : "Approves";
		}
		return temp;
	}
	private String requestOnTorontoServer(String customerId, String eventId, String eventType, String capacity, int port,
			String operation) {
		// TODO Auto-generated method stub
		String temp = "";
		if (operation.trim().equals("listOperation")) {
			temp = torontoData.retrieveEvent(eventType);
		} else if (operation.trim().equals("addOperation")) {
			 temp = torontoData.addEvent(eventId, eventType, capacity);
		} else if (operation.trim().equals("bookOperation")) {
			 temp = torontoData.bookEvent(customerId, eventId, eventType);
		} else if (operation.trim().equals("cancelOperation")) {
			 temp = torontoData.removeEvent(customerId, eventId, eventType);
		} else if (operation.trim().equals("scheduleOperation")) {
			 temp = torontoData.getBookingSchedule(customerId);
		} else if (operation.trim().equals("countOperation")) {
			 temp = torontoData.getBookingCount(customerId, eventId);
		} else if (operation.trim().equals("existanceOperation")) {
			boolean newTemp = torontoData.getEvent(customerId, eventId, eventType);
			temp = newTemp == false ? "Denies" : "Approves";
		}
		return temp;
	}
}
