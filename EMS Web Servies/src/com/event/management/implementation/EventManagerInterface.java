package com.event.management.implementation;

import java.io.IOException;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface EventManagerInterface 
{
	public String addEvent(String managerId, String eventId, String eventType, String eventCapacity) throws IOException;

	public String removeEvent(String managerId, String eventId, String eventType) throws IOException;

	public String listEventAvailability(String managerId, String eventType) throws IOException, InterruptedException;

	public String eventBooking(String customerId, String eventId, String eventType) throws IOException;

	public String cancelBooking(String customerId, String eventId, String eventType) throws IOException;

	public String getBookingSchedule(String customerId) throws IOException;

	public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) throws IOException;
	
}