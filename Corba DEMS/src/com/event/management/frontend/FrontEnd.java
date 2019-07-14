package com.event.management.frontend;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.json.simple.JSONObject;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import com.event.management.constants.*; 
import FrontEnd.FrontEndInterface;
import FrontEnd.FrontEndInterfaceHelper;
import FrontEnd.FrontEndInterfacePOA;

public class FrontEnd extends FrontEndInterfacePOA {
	private static Logger logger;
	private ORB orb;
	public static void main(String args[]) throws Exception {

		try {
			// create and initialize the ORB //// get reference to rootpoa &amp; activate
			// the POAManager
			ORB orb = ORB.init(args, null);
			// -ORBInitialPort 1050 -ORBInitialHost localhost
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			FrontEnd object = new FrontEnd();
			object.setORB(orb);

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(object);
			FrontEndInterface href = FrontEndInterfaceHelper.narrow(ref);

			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			NameComponent path[] = ncRef.to_name("frontend");
			ncRef.rebind(path, href);

			orb.run();
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
	}

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	public ORB getOrb() {
		return orb;
	}

	@Override
	public String addEvent(String managerId, String eventId, String eventType, String eventCapacity) {
		String requestMessage = generateJSONObject(managerId, eventId, eventType, eventCapacity, Constants.NONE, Constants.NONE, Constants.ADD_OPERATION);
		udpRequest(requestMessage);
		String replyMessage = udpReply();
		return replyMessage;
	}

	@Override
	public String removeEvent(String managerId, String eventId, String eventType) {
		String requestMessage = generateJSONObject(managerId, eventId, eventType, Constants.NONE, Constants.NONE, Constants.NONE, Constants.REMOVE_OPERATION);
		udpRequest(requestMessage);
		String replyMessage = udpReply();
		return replyMessage;	}

	@Override
	public String listEventAvailability(String managerId, String eventType) {
		String requestMessage = generateJSONObject(managerId, Constants.NONE, eventType, Constants.NONE, Constants.NONE, Constants.NONE, Constants.LIST_OPERATION);
		udpRequest(requestMessage);
		String replyMessage = udpReply();
		return replyMessage;	}

	@Override
	public String eventBooking(String customerId, String eventId, String eventType) {
		String requestMessage = generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE, Constants.NONE, Constants.BOOK_OPERATION);
		udpRequest(requestMessage);
		String replyMessage = udpReply();
		return replyMessage;	}

	@Override
	public String cancelBooking(String customerId, String eventId, String eventType) {
		String requestMessage = generateJSONObject(customerId, eventId, eventType, Constants.NONE, Constants.NONE, Constants.NONE, Constants.CANCEL_OPERATION);
		udpRequest(requestMessage);
		String replyMessage = udpReply();
		return replyMessage;
	}

	@Override
	public String getBookingSchedule(String customerId) {
		String requestMessage = generateJSONObject(customerId, Constants.NONE, Constants.NONE, Constants.NONE, Constants.NONE, Constants.NONE, Constants.SCHEDULE_OPERATION);
		udpRequest(requestMessage);
		String replyMessage = udpReply();
		return replyMessage;	}

	@Override
	public String swapEvent(String customerId, String newEventId, String newEventType, String oldEventId,
			String oldEventType) {
		String requestMessage = generateJSONObject(customerId, newEventId, newEventType,Constants.NONE, oldEventId, oldEventType, Constants.SWAP_OPERATION);
		udpRequest(requestMessage);
		String replyMessage = udpReply();
		return replyMessage;	}
	
	static String generateJSONObject(String id, String eventId, String eventType, String eventCapacity, String oldEventId, String oldEventType,
			String operation) {
		JSONObject obj = new JSONObject();
		obj.put(Constants.ID, id.trim());
		obj.put(Constants.EVENT_ID, eventId.trim());
		obj.put(Constants.EVENT_TYPE, eventType.trim());
		obj.put(Constants.EVENT_CAPACITY, eventCapacity.trim());
		obj.put(Constants.OLD_EVENT_ID, oldEventId.trim());
		obj.put(Constants.OLD_EVENT_TYPE, oldEventType.trim());
		obj.put(Constants.OPERATION, operation.trim());
		return obj.toString();
	}
	public void udpRequest(String message) {
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket();
			byte[] msg = message.getBytes();
			InetAddress aHost = InetAddress.getByName(Constants.LOCALHOST);
			int serverPort = 5555;
			DatagramPacket request = new DatagramPacket(msg, msg.length, aHost, serverPort);
			datagramSocket.send(request);
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

	public String udpReply() {
		DatagramSocket datagramSocket = null;
		try {
			datagramSocket = new DatagramSocket();
			byte[] buffer = new byte[65535];
			while (true) {
				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
				datagramSocket.receive(datagramPacket);
				String response = new String(datagramPacket.getData());
				return response;
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
