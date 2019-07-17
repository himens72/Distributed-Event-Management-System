package com.event.management.model;

import java.util.HashMap;
import java.util.Map.Entry;

public class MontrealData {
	HashMap<String, HashMap<String, HashMap<String, String>>> serverData;
	String serverName;

	public HashMap<String, HashMap<String, HashMap<String, String>>> getServerData() {
		return serverData;
	}

	public void setServerData(HashMap<String, HashMap<String, HashMap<String, String>>> serverData) {
		this.serverData = serverData;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public MontrealData() {
		serverData = new HashMap<>();
		serverData.put("Conferences", new HashMap<>());
		serverData.put("Seminars", new HashMap<>());
		serverData.put("Trade Shows", new HashMap<>());
	}

	synchronized public String addEvent(String eventId, String eventtype, String eventCapacity) {
		if (!serverData.containsKey(eventtype)) {
			return "Event Type Doesn't Exist. Please Enter proper Event Type";
		}
		HashMap<String, HashMap<String, String>> newValue = serverData.get(eventtype);
		if (newValue.containsKey(eventId)) {
			HashMap<String, String> newList = newValue.get(eventId);
			newList.replace("capacity", newList.get("capacity"), eventCapacity);
			newList.replace("totalBooking", newList.get("totalBooking"), newList.get("totalBooking"));
			newList.replace("customerId", newList.get("customerId"), newList.get("customerId"));
			newValue.replace(eventId, serverData.get(eventtype).get(eventId), newList);
			serverData.replace(eventtype, serverData.get(eventtype), newValue);
			return eventId + "event Capacity is updated to " + eventCapacity + " of type " + eventtype;
		} else {
			HashMap<String, String> temp = new HashMap<>();
			temp.put("capacity", eventCapacity);
			temp.put("totalBooking", "0");
			temp.put("customerId", "");
			newValue.put(eventId, temp);
			serverData.replace(eventtype, serverData.get(eventtype), newValue);
			return eventId + " is created with capacity " + eventCapacity + " of type " + eventtype;
		}
	}

	public synchronized String removeEvent(String eventId, String eventType) {
		if (!serverData.containsKey(eventType)) {
			return "Event Type Doesn't Exist. Please Enter proper Event Type";
		}
		HashMap<String, HashMap<String, String>> newValue = serverData.get(eventType);
		if (newValue.containsKey(eventId)) {
			serverData.get(eventType).remove(eventId);
			return eventId + " is removed for event type" + eventType;
		} else {
			return eventId + " is doesn't exist for event type" + eventType;
		}
	}

	public synchronized String retrieveEvent(String eventType) {
		System.out.println("Event Type : " + eventType);
		if (serverData.containsKey(eventType)) {
			HashMap<String, HashMap<String, String>> temp = serverData.get(eventType);
			if (temp.size() == 0) {
				System.out.println("No Events Found");
				return "";
			} else {
				StringBuilder str = new StringBuilder();
				for (Entry<String, HashMap<String, String>> entry : temp.entrySet()) {
					if (Integer.parseInt(entry.getValue().get("capacity")) >= Integer
							.parseInt(entry.getValue().get("totalBooking")))
						str.append(entry.getKey() + " " + (Integer.parseInt(entry.getValue().get("capacity"))
								- Integer.parseInt(entry.getValue().get("totalBooking"))) + ",");
				}
				return str.toString().trim();
			}
		} else {
			System.out.println("No Event Type Found");
			return "No Event Type Found";
		}
	}

	public synchronized String bookEvent(String customerID, String eventId, String eventType) {
		if (serverData.containsKey(eventType)) {
			HashMap<String, HashMap<String, String>> typeData = serverData.get(eventType);
			if (typeData.size() == 0) {
				System.out.println("No Events Found");
				return "";
			} else {
				if (typeData.containsKey(eventId)) {
					HashMap<String, String> currentEvent = typeData.get(eventId);
					if (Integer.parseInt(currentEvent.get("capacity")) == Integer
							.parseInt(currentEvent.get("totalBooking"))) {
						return eventId + " is not Available for booking of type " + eventType;
					} else {
						StringBuilder customers = new StringBuilder(currentEvent.get("customerId"));
						if (currentEvent.get("customerId").contains(customerID)) {
							return customerID + " has already book event " + eventId + "of event type " + eventType;
						}
						customers.append(customerID.trim());
						currentEvent.replace("customerId", customers.toString().trim() + ",");
						currentEvent.replace("capacity", currentEvent.get("capacity"),
								Integer.toString(Integer.parseInt(currentEvent.get("capacity"))));
						currentEvent.replace("totalBooking", currentEvent.get("totalBooking"),
								Integer.toString(Integer.parseInt(currentEvent.get("totalBooking")) + 1));
						typeData.replace(eventId, typeData.get(eventId), currentEvent);
						serverData.replace(eventType, serverData.get(eventType), typeData);
						return customerID + " has book event " + eventId + " of event  type " + eventType;
					}
				} else {
					return "No Event Found;";
				}
			}
		} else {
			System.out.println("No Event Type Found");
			return "No Event Type Found";
		}
	}

	public synchronized String removeEvent(String customerID, String eventId, String eventType) {
		if (serverData.containsKey(eventType)) {
			HashMap<String, HashMap<String, String>> typeData = serverData.get(eventType);
			if (typeData.size() == 0) {
				System.out.println("No Events Found");
				return "No Events Found";
			} else {
				if (typeData.containsKey(eventId)) {
					HashMap<String, String> currentEvent = typeData.get(eventId);
					if (Integer.parseInt(currentEvent.get("totalBooking")) == 0) {
						return eventId + " is not booked by anyone";
					} else {
						StringBuilder customers = new StringBuilder(currentEvent.get("customerId"));
						if (!currentEvent.get("customerId").contains(customerID)) {

							return customerID + " has not book event " + eventId + "of event type " + eventType;
						}
						currentEvent.replace("customerId", customers.toString().replace(customerID.trim() + ",", ""));
						currentEvent.replace("capacity", currentEvent.get("capacity"),
								Integer.toString(Integer.parseInt(currentEvent.get("capacity"))));
						currentEvent.replace("totalBooking", currentEvent.get("totalBooking"),
								Integer.toString(Integer.parseInt(currentEvent.get("totalBooking")) - 1));
						typeData.replace(eventId, typeData.get(eventId), currentEvent);
						serverData.replace(eventType, serverData.get(eventType), typeData);
						return customerID + " has cancel book event " + eventId + " of event  type " + eventType;
					}
				} else {
					return "No Event Found;";
				}
			}
		} else {
			System.out.println("No Event Type Found");
			return "No Event Type Found";
		}
	}

	public synchronized String getBookingSchedule(String customerId) {
		StringBuilder customers = new StringBuilder();
		for (Entry<String, HashMap<String, HashMap<String, String>>> types : serverData.entrySet()) {
			for (Entry<String, HashMap<String, String>> events : types.getValue().entrySet()) {
				if (events.getValue().get("customerId").contains(customerId.trim())) {
					customers.append(types.getKey() + " = " + events.getKey() + ",");
				}
			}
		}
		return customers.length() == 0 ? "" : customers.toString();
	}

	public synchronized String getBookingCount(String customerId, String eventType) {
		int count = 0;
		String month = eventType.trim().substring(6, eventType.trim().length());
		for (Entry<String, HashMap<String, HashMap<String, String>>> types : serverData.entrySet()) {
			for (Entry<String, HashMap<String, String>> events : types.getValue().entrySet()) {
				if (events.getValue().get("customerId").trim().contains(customerId.trim())
						&& events.getKey().substring(6, events.getKey().trim().length()).trim().equals(month.trim())) {
					count++;
				}
			}
		}
		return Integer.toString(count);
	}
	
	public synchronized boolean getEvent(String customerId, String eventId, String eventType) {
		if (serverData.containsKey(eventType)) {
			HashMap<String, HashMap<String, String>> typeData = serverData.get(eventType);
			if (typeData.size() == 0) {
				System.out.println("No Events Found");
				return false;
			} else {
				if (typeData.containsKey(eventId)) {
					HashMap<String, String> currentEvent = typeData.get(eventId);
					return currentEvent.get("customerId").contains(customerId.trim());
				} else {
					return false;
				}
			}
		} else {
			System.out.println("No Event Type Found");
			return false;
		}
	}
}
