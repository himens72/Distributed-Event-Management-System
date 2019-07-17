package com.event.management.implementation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;

import com.event.management.locks.ServerLocks;
import com.event.management.model.TorontoData;

import EventManagement.managerInterfacePOA;

public class EventManagerToronto extends managerInterfacePOA {
	public String location;
	public String response;
	public TorontoData torontoData;
	private static Logger logger;
	private ORB orb;

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	public ORB getOrb() {
		return orb;
	}

	public EventManagerToronto() {
		super();
		torontoData = new TorontoData();
		Runnable task = () -> {
			receive();
		};
		Thread thread = new Thread(task);
		thread.start();

		Runnable task1 = () -> {
			receiveMulticastRequest();
		};
		Runnable task2 = () -> {
			receiveFailedResponse();
		};
		Thread thread1 = new Thread(task1);
		Thread thread2 = new Thread(task2);
		thread1.start();
		thread2.start();
	}

	private void receiveMulticastRequest() {
		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(1313);
			aSocket.joinGroup(InetAddress.getByName("230.1.2.5"));
			BufferedWriter output = null;
			FileWriter fr = null;

			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String text = new String(request.getData()).trim();
				System.out.println("receiveMulticastRequest:" + new String(request.getData()));
				System.out.println("Response" + response);
				sendRequestToFrontEnd(response);
			}
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (aSocket != null)
				aSocket.close();
		}
	}

	private void sendRequestToFrontEnd(String message) {
		DatagramSocket aSocket = null;
		byte[] buffer = new byte[1000];
		try {
			System.out.println("send request to front end called");
			aSocket = new DatagramSocket();
			byte[] m = message.getBytes();
			InetAddress aHost = InetAddress.getByName("132.205.4.21");

			System.out.println("bytes: " + m);
			DatagramPacket request = new DatagramPacket(m, m.length, aHost, 1113);
			aSocket.send(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void receive() {
		try {
			DatagramSocket aSocket = null;
			try {
				aSocket = new DatagramSocket(2233);

				byte[] buffer = new byte[1000];
				while (true) {
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					aSocket.receive(request);
					String message = new String(request.getData(), 0, request.getLength());
					message = message.replaceAll("[^a-zA-Z0-9]", "");
					System.out.println(message);
					if (message.substring((message.length()) - 2).equalsIgnoreCase("BI")) {
						String a = "";
						byte[] msg = a.getBytes();

						DatagramPacket reply = new DatagramPacket(msg, msg.length, request.getAddress(),
								request.getPort());
						aSocket.send(reply);
					} else if (message.substring((message.length()) - 2).equalsIgnoreCase("RI")) {
						String a = "";
						byte[] msg = a.getBytes();
						DatagramPacket reply = new DatagramPacket(msg, msg.length, request.getAddress(),
								request.getPort());
						aSocket.send(reply);
					} else if (message.substring((message.length()) - 6).equalsIgnoreCase("REMOVE")) {
						ServerLocks.lockTorontoServerData.readLock().lock();
						try {
							String temp = "";
						} finally {
							ServerLocks.lockTorontoServerData.readLock().unlock();
						}
					} else if (message.substring((message.length()) - 4).equalsIgnoreCase("FIND")) {
						ServerLocks.lockTorontoServer.readLock().lock();
						try {
							String temp = "";
						} finally {
							ServerLocks.lockTorontoServer.readLock().unlock();
						}
												String a = "";
						byte[] msg = a.getBytes();

						DatagramPacket reply = new DatagramPacket(msg, msg.length, request.getAddress(),
								request.getPort());
						aSocket.send(reply);
					} else if (message.substring((message.length() - 2)).equalsIgnoreCase("WL")) {
													String a = "0";
							byte[] msg = a.getBytes();

							DatagramPacket reply = new DatagramPacket(msg, msg.length, request.getAddress(),
									request.getPort());
							aSocket.send(reply);
					} else if (message.substring((message.length() - 8)).equalsIgnoreCase("EXCHANGE")) {
						ServerLocks.lockTorontoServer.readLock().lock();
						try {
							String x = "";
						} finally {
							ServerLocks.lockTorontoServer.readLock().unlock();
						}
							String y = "0";
							byte[] msg = y.getBytes();

							DatagramPacket reply = new DatagramPacket(msg, msg.length, request.getAddress(),
									request.getPort());
							aSocket.send(reply);
					} else if (message.substring((message.length()) - 5).equalsIgnoreCase("CHECK")) {
						ServerLocks.lockMontrealServerData.readLock().lock();
							try {
								String a = "";
							} finally {
								ServerLocks.lockMontrealServerData.readLock().unlock();
							}
								String y = "0";
								byte[] msg = y.getBytes();

								DatagramPacket reply = new DatagramPacket(msg, msg.length, request.getAddress(),
										request.getPort());
								aSocket.send(reply);
						} else {
							ArrayList<String> a = new ArrayList<String>();
							ServerLocks.lockOttwawaServer.readLock().lock();
							try {
								String ac = "";
							} finally {
								ServerLocks.lockOttwawaServer.readLock().unlock();
							}
								String y = "0";
								byte[] msg = y.getBytes();

								DatagramPacket reply = new DatagramPacket(msg, msg.length, request.getAddress(),
										request.getPort());
								aSocket.send(reply);
							
						
					}
				}
			} catch (SocketException e) {
				System.out.println(e.getMessage());
			}
		}

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
	}

	private static void receiveFailedResponse() {

		MulticastSocket aSocket = null;
		try {
			aSocket = new MulticastSocket(6467);
			aSocket.joinGroup(InetAddress.getByName("230.2.2.5"));

			while (true) {
				byte[] buffer = new byte[1000];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);

				System.out.println("FrontEnd Response: " + new String(request.getData()));

				// TODO handle failure or wrong response
			}
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
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
