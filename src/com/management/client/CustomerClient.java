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
				System.out.println("1. Add Event ");
				System.out.println("2. Remove Event");
				System.out.println("3. List all Available Event");
				System.out.println("Select Any above option");
				String option = br.readLine().trim();
				if (option.equals("1")) {
					addEventOption(id);
				} else if (option.equals("3")) {
					listEventAvailabilityOption(id);
				} else if (option.equals("2")) {
					removeEventOption(id);
				}
			} else if (id.charAt(3) == 'C') {
				createManagerObject(id.substring(0, 3));
				System.out.println("1. Book Event ");
				System.out.println("2. List all event schedule");
				System.out.println("3. Cancel Event");
				System.out.println("Select Any above option");
				String option = br.readLine().trim();
				if (option.equals("1")) {
					bookEventOption(id);
				} else if (option.equals("2")) {
					System.out.println(managerObj.getBookingSchedule(id));
				} else if (option.equals("3")) {
					cancelEventOption(id);
				}
			}
			if (id.equals("quit")) {
				break;
			}
		}

	}

	public static void createManagerObject(String serverName)
			throws AccessException, RemoteException, NotBoundException {
		if (serverName.startsWith("TOR")) {
			managerObj = (managerInterface) reg.lookup("Toronto M");
			managerObj.sendMessage("Request on Toronto Server ");
		} else if (serverName.startsWith("MTL")) {
			managerObj = (managerInterface) reg.lookup("Montreal M");
			managerObj.sendMessage("Request on Montreal Server ");

		} else if (serverName.startsWith("OTW")) {
			managerObj = (managerInterface) reg.lookup("Ottawa M");
			managerObj.sendMessage("Request on Ottawa Server ");
		}
	}

	public static void bookEventOption(String customerId) throws IOException {
		System.out.println("1. Event ID ");
		String eventId = br.readLine().trim();
		System.out.println("2. Event Type");
		String eventType = br.readLine().trim();
		logger.info(managerObj.eventBooking(customerId, eventId, eventType));
	}

	public static void listScheduleEventOption(String customerId) {

	}

	public static void cancelEventOption(String customerId) throws IOException {
		System.out.println("1. Event ID ");
		String eventId = br.readLine().trim();
		System.out.println("2. Event Type");
		String eventType = br.readLine().trim();
		System.out.println(managerObj.cancelBooking(customerId, eventId, eventType));
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
		System.out.println(managerObj.listEventAvailability(managerId, eventType));
	}

	public static void removeEventOption(String managerId) throws IOException {
		System.out.println("1. Event ID ");
		String eventId = br.readLine().trim();
		System.out.println("2. Event Type");
		String eventType = br.readLine().trim();
		System.out.println(managerObj.removeEvent(managerId, eventId, eventType));
	}

	static void setLogger(String location, String id) {
		try {
			logger = Logger.getLogger(id);
			FileHandler fileTxt = new FileHandler(location, true);
			SimpleFormatter formatterTxt = new SimpleFormatter();
			fileTxt.setFormatter(formatterTxt);
			logger.addHandler(fileTxt);
		} catch (Exception err) {
			System.out.println("Couldn't Initiate Logger. Please check file permission");
		}

	}
}
