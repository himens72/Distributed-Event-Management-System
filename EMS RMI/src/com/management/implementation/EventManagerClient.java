/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.implementation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.management.interfaceDef.managerInterface;
import com.management.model.MontrealData;
import com.management.model.OttawaData;
import com.management.model.TorontoData;
import com.management.server.Montreal;
import com.management.server.Ottawa;
import com.management.server.Toronto;

/**
 *
 * @author Himen Sidhpura
 */
public class EventManagerClient extends UnicastRemoteObject implements managerInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4262636697845867549L;
	public String location;
	public String response;
	public MontrealData montrealData;
	public TorontoData torontoData;
	public OttawaData ottawaData;
	private static Logger logger;

	public EventManagerClient(String location) throws RemoteException {
		super();
		montrealData = new MontrealData();
		torontoData = new TorontoData();
		ottawaData = new OttawaData();
		this.location = location;
		setLogger("logs/" + location + ".txt", location);

		if (location.equals("TOR")) {
			Toronto toronto = new Toronto(this);
			Runnable task1 = () -> {
				toronto.serverConnection(9990);
			};
			Thread thread1 = new Thread(task1);
			thread1.start();
		} else if (location.equals("MTL")) {
			Runnable task2 = () -> {
				Montreal montreal = new Montreal(this);
				montreal.serverConnection(9991);
			};
			Thread thread2 = new Thread(task2);
			thread2.start();
		} else if (location.equals("OTW")) {
			Ottawa ottawa = new Ottawa(this);
			Runnable task3 = () -> {
				ottawa.serverConnection(9992);
			};
			Thread thread3 = new Thread(task3);
			thread3.start();
		} else {

			System.out.println("Server not started");
		}
	}

	@Override
	public String addEvent(String managerId, String eventId, String eventtype, String eventCapacity)
			throws IOException {
		logger.info("Add Event Operation :  " + managerId + " has started creating event with id " + eventId
				+ " of type " + eventtype + " with capacity " + eventCapacity);
		if (eventtype.equals("Seminars") || eventtype.equals("Conferences") || eventtype.equals("Trade Shows")) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				String output = "";
				if (eventId.substring(0, 3).trim().equals("TOR"))
					output = torontoData.addEvent(eventId, eventtype, eventCapacity);
				else if (eventId.substring(0, 3).trim().equals("MTL"))
					output = montrealData.addEvent(eventId, eventtype, eventCapacity);
				else if (eventId.substring(0, 3).trim().equals("OTW"))
					output = ottawaData.addEvent(eventId, eventtype, eventCapacity);
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

	@Override
	public String removeEvent(String managerId, String eventId, String eventtype) throws IOException {
		logger.info("Remove Event Operation :  " + managerId + " has delete event with id " + eventId + " of type "
				+ eventtype);
		if (eventtype.equals("Seminars") || eventtype.equals("Conferences") || eventtype.equals("Trade Shows")) {
			if (eventId.substring(0, 3).trim().equals(managerId.substring(0, 3).trim())) {
				String output = "";
				if (eventId.substring(0, 3).trim().equals("TOR"))
					output = torontoData.removeEvent(eventId, eventtype);
				else if (eventId.substring(0, 3).trim().equals("MTL"))
					output = montrealData.removeEvent(eventId, eventtype);
				else if (eventId.substring(0, 3).trim().equals("OTW"))
					output = ottawaData.removeEvent(eventId, eventtype);
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

	@Override
	public String listEventAvailability(String managerId, String eventType) throws IOException, InterruptedException {
		logger.info("List  Event Operation :  " + managerId + " want to see All available list of type " + eventType);
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
			String temp = "";
			if (managerId.substring(0, 3).trim().equals("TOR")) {
				temp = torontoData.retrieveEvent(eventType).trim();
				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9991, "listOperation")
						.trim();
				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9992,
						"listOperation").trim();
				logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
				return temp.trim().equals("") ? "No Events Available" : temp.trim();
			} else if (managerId.substring(0, 3).trim().equals("MTL")) {
				temp = montrealData.retrieveEvent(eventType).trim();
				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9990, "listOperation")
						.trim();
				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9992,
						"listOperation").trim();
				logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
				return temp.trim().equals("") ? "No Events Available" : temp.trim();
			} else if (managerId.substring(0, 3).trim().equals("OTW")) {
				temp = ottawaData.retrieveEvent(eventType).trim();
				temp += requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9990, "listOperation")
						.trim();
				temp = temp + requestOnOtherServer(managerId, "No Event Id", eventType, "No Capacity", 9991,
						"listOperation").trim();
				logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
				return temp.trim().equals("") ? "No Events Available" : temp.trim();
			}
			logger.info(temp.trim().equals("") ? "No Events Available" : temp.trim());
			return temp.trim().equals("") ? "No Events Available" : temp.trim();

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

	@Override
	public String eventBooking(String customerId, String eventId, String eventType) throws IOException {
		if (eventType.trim().equals("Seminars") || eventType.trim().equals("Conferences")
				|| eventType.trim().equals("Trade Shows")) {
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
					return "you have already reached maximum limit of Current Month Outside city registration ";
				}
			}
			if (customerId.substring(0, 3).trim().equals(eventId.substring(0, 3).trim())) {
				String temp = "";
				if (eventId.trim().substring(0, 3).equals("TOR")) {
					temp = torontoData.bookEvent(customerId, eventId, eventType);
				} else if (eventId.trim().substring(0, 3).equals("MTL")) {
					temp = montrealData.bookEvent(customerId, eventId, eventType);
				} else if (eventId.trim().substring(0, 3).equals("OTW")) {
					temp = ottawaData.bookEvent(customerId, eventId, eventType);
				}
				return temp == "" ? "Unable to Book  Event" : temp.trim();
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp.trim();// + " -- > " + count;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
						"bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp.trim();// + " -- > " + count;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
						"bookOperation");
				return temp == "" ? "Unable to Book  Event" : temp.trim();// + " -- > " + count;

			} else {
				return "Please Enter Event ID Properly";
			}

		} else {
			return "Please enter Event type properly";
		}
	}

	@Override
	public String cancelBooking(String customerId, String eventId, String eventType) throws IOException {

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
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"cancelOperation");
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
						"cancelOperation");
				return temp == "" ? "Unable to Cancel  Event" : temp;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
						"cancelOperation");
				return temp == "" ? "Unable to Cancel  Event" : temp;

			} else {
				return "Please Enter Event ID Properly";
			}

		} else {
			return "Please enter Event type properly";
		}
	}

	@Override
	public String getBookingSchedule(String customerId) throws IOException {
		// TODO Auto-generated method stub
		logger.info("Booking Schedule Operation :  " + customerId);

		StringBuilder temp = new StringBuilder();
		if (customerId.substring(0, 3).trim().equals("TOR")) {
			temp.append(torontoData.getBookingSchedule(customerId.trim()));
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9991,
					"scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9992,
					"scheduleOperation").trim());
			logger.info("Booking Schedule for " + customerId + " : " + temp);
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
		} else if (customerId.substring(0, 3).trim().equals("MTL")) {
			temp.append(montrealData.getBookingSchedule(customerId.trim()));
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9990,
					"scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9992,
					"scheduleOperation").trim());
			logger.info("Booking Schedule for " + customerId + " : " + temp);
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
		} else if (customerId.substring(0, 3).trim().equals("OTW")) {
			temp.append(ottawaData.getBookingSchedule(customerId.trim()));
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9990,
					"scheduleOperation").trim());
			temp.append(requestOnOtherServer(customerId, "No Event Id", "No Types", "No Capacity", 9991,
					"scheduleOperation").trim());
			logger.info("Booking Schedule for " + customerId + " : " + temp);
			return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
		}
		logger.info("Booking Schedule for " + customerId + " : " + temp);
		return temp.toString().length() == 0 ? "No Events Schedule" : temp.toString().trim();
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

	@Override
	public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID,
			String oldEventType) throws IOException {
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
				return temp.trim().isEmpty() ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("TOR")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"bookOperation");
				return temp.trim().isEmpty() ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
						"bookOperation");
				return temp.trim().isEmpty() ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
						"bookOperation");
				return temp.trim().isEmpty() ? false : true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean swapCancelBooking(String customerId, String eventId, String eventType) throws IOException {

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
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9990,
						"cancelOperation");
				return temp.trim().isEmpty() ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("MTL")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9991,
						"cancelOperation");
				return temp.trim().isEmpty() ? false : true;
			} else if (eventId.trim().substring(0, 3).equals("OTW")) {
				String temp = requestOnOtherServer(customerId, eventId, eventType, "No Capacity", 9992,
						"cancelOperation");
				return temp.trim().isEmpty() ? false : true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}
}
