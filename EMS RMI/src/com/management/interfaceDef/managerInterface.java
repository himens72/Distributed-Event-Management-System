/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.interfaceDef;

import java.io.IOException;
import java.rmi.Remote;
/**
 *
 * @author Himen Sidhpura
 */
public interface managerInterface extends Remote {

	public String addEvent(String managerId, String eventId, String eventType, String eventCapacity) throws IOException;

	public String removeEvent(String managerId, String eventId, String eventType) throws IOException;

	public String listEventAvailability(String managerId, String eventType) throws IOException, InterruptedException;

	public String eventBooking(String customerId, String eventId, String eventType) throws IOException;

	public String cancelBooking(String customerId, String eventId, String eventType) throws IOException;

	public String getBookingSchedule(String customerId) throws IOException;

	public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) throws IOException;

}
