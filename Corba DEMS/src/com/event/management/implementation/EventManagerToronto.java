package com.event.management.implementation;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.event.management.model.TorontoData;

import EventManagement.managerInterfacePOA;

public class EventManagerToronto extends managerInterfacePOA {
	public String location;
	public String response;
	public TorontoData torontoData;
	private static Logger logger;

	public EventManagerToronto() {
		// TODO Auto-generated constructor stub
		setLogger("logs/TOR.txt", "TOR");
	}
	
	@Override
	public String addEvent(String managerId, String eventId, String eventType, String eventCapacity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String removeEvent(String managerId, String eventId, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String listEventAvailability(String managerId, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String eventBooking(String customerId, String eventId, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cancelBooking(String customerId, String eventId, String eventType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBookingSchedule(String customerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String swapEvent(String customerId, String newEventId, String newEventType, String oldEventId,
			String oldEventType) {
		// TODO Auto-generated method stub
		return null;
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
