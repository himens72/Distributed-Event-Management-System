/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.management.interfaceDef.managerInterface;

/**
 *
 * @author Himen Sidhpura
 */
public class CustomerClient {

	static Registry reg;
	static managerInterface managerObj;
	static BufferedReader br;
	private static Logger logger;
	private static FileHandler fileTxt;

	public static void main(String[] args) throws NotBoundException, IOException, InterruptedException {

		reg = LocateRegistry.getRegistry(8080);
		br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("Enter Your ID");
			String id = br.readLine().trim();
			managerObj = null;
			setLogger("logs/" + id + ".txt", id);
			if (id.charAt(3) == 'M') {
				createManagerObject(id.substring(0, 3));
				managerOption(id);
			} else if (id.charAt(3) == 'C') {
				createManagerObject(id.substring(0, 3));
				customerOption(id);
			}
			if(id.equals("exit"))
				break;
		}

	}
	
	public static  void managerOption(String id) throws IOException, InterruptedException{
		System.out.println("1. Add Event ");
		System.out.println("2. Remove Event");
		System.out.println("3. List all Available Event");
		System.out.println("4. Book Event ");
		System.out.println("5. List all event schedule");
		System.out.println("6. Cancel Event");
		System.out.println("Select Any above option");
		String option = br.readLine().trim();
		if (option.equals("1")) {
			logger.info(id + " started peforming add event operation");
			addEventOption(id);
		} else if (option.equals("3")) {
			logger.info(id + " started peforming list all available  event operation");
			listEventAvailabilityOption(id);
		} else if (option.equals("2")) {
			logger.info(id + " started peforming remove event operation");
			removeEventOption(id);
		}else if (option.equals("4")) {
			System.out.println("Enter Customer ID");
			String customerId = br.readLine();
			logger.info(id + " started peforming book event operation for  " + customerId );
			bookEventOption(customerId);
		} else if (option.trim().equals("5")) {
			System.out.println("Enter Customer ID");
			String customerId = br.readLine();
			logger.info(id + " started peforming schedule event operation for " + customerId);
			logger.info(managerObj.getBookingSchedule(customerId));
		} else if (option.trim().equals("6")) {
			System.out.println("Enter Customer ID");
			String customerId = br.readLine().trim();
			logger.info(id + " started peforming cancel event operation for " + customerId);
			cancelEventOption(customerId);
		} 
	}
	public static void customerOption(String id) throws IOException {
		System.out.println("1. Book Event ");
		System.out.println("2. List all event schedule");
		System.out.println("3. Cancel Event");
		System.out.println("4. Swap Event");
		System.out.println("Select Any above option");
		String option = br.readLine().trim();
		if (option.equals("1")) {
			logger.info(id + " started peforming book event operation");
			bookEventOption(id);
		} else if (option.equals("2")) {
			logger.info(id + " started peforming schedule event operation");
			logger.info(managerObj.getBookingSchedule(id));
		} else if (option.equals("3")) {
			logger.info(id + " started peforming cancel event operation");
			cancelEventOption(id);
		} else if (option.equals("4")) {
			logger.info(id + " started peforming swap event operation");
			swapEventOption(id);
		}
	}
	private static void swapEventOption(String customerID) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("1. New Event ID ");
		String newEventId = br.readLine().trim();
		System.out.println("2. New Event Type");
		String newEventType = br.readLine().trim();
		System.out.println("3. Old Event ID ");
		String oldEventId = br.readLine().trim();
		System.out.println("4. Old Event Type");
		String oldEventType = br.readLine().trim();
		logger.info(managerObj.swapEvent(customerID, newEventId, newEventType, oldEventId, oldEventType));
	}

	public static void createManagerObject(String serverName)
			throws AccessException, RemoteException, NotBoundException {
		if (serverName.startsWith("TOR")) {
			managerObj = (managerInterface) reg.lookup("Toronto M");
		} else if (serverName.startsWith("MTL")) {
			managerObj = (managerInterface) reg.lookup("Montreal M");
		} else if (serverName.startsWith("OTW")) {
			managerObj = (managerInterface) reg.lookup("Ottawa M");
		}
	}

	public static void bookEventOption(String customerId) throws IOException {
		System.out.println("1. Event ID ");
		String eventId = br.readLine().trim();
		System.out.println("2. Event Type");
		String eventType = br.readLine().trim();
		logger.info(managerObj.eventBooking(customerId, eventId, eventType));
	}

	public static void cancelEventOption(String customerId) throws IOException {
		System.out.println("1. Event ID ");
		String eventId = br.readLine().trim();
		System.out.println("2. Event Type");
		String eventType = br.readLine().trim();
		logger.info(managerObj.cancelBooking(customerId, eventId, eventType));
	}

	public static void addEventOption(String managerId) throws IOException {
		System.out.println("1. Event ID ");
		String eventId = br.readLine().trim();
		System.out.println("2. Event Type");
		String eventType = br.readLine().trim();
		System.out.println("3. Booking Capacity");
		String eventCapacity = br.readLine().trim();
		logger.info(managerObj.addEvent(managerId, eventId, eventType, eventCapacity));
	}

	public static void listEventAvailabilityOption(String managerId) throws IOException, InterruptedException {
		System.out.println("1. Event Type");
		String eventType = br.readLine().trim();
		logger.info(managerObj.listEventAvailability(managerId, eventType));
	}

	public static void removeEventOption(String managerId) throws IOException {
		System.out.println("1. Event ID ");
		String eventId = br.readLine().trim();
		System.out.println("2. Event Type");
		String eventType = br.readLine().trim();
		logger.info(managerObj.removeEvent(managerId, eventId, eventType));
	}

	static void setLogger(String location, String id) {
		try {
			logger = Logger.getLogger(id);
			fileTxt = new FileHandler(location, true);
			SimpleFormatter formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			logger.addHandler(fileTxt);
		} catch (Exception err) {
			logger.info("Couldn't Initiate Logger. Please check file permission");
		}
	}
}
