package com.event.management.implementation;

import java.util.logging.Logger;

import com.event.management.model.TorontoData;

import EventManagement.managerInterfacePOA;

public class EventManagerMontreal extends managerInterfacePOA {
	public String location;
	public String response;
	public TorontoData torontoData;
	private static Logger logger;

	public EventManagerMontreal() {
		// TODO Auto-generated constructor stub
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

}
