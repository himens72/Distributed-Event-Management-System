package com.event.management.server;

import javax.xml.ws.Endpoint;

import com.event.management.implementation.EventManagerClient;

public class server {

	public static void main(String args[]) {
		EventManagerClient toronto = new EventManagerClient("TOR");
		EventManagerClient montreal = new EventManagerClient("MTL");
		EventManagerClient ottawa = new EventManagerClient("OTW");

		Endpoint torontoEndPoint = Endpoint.publish("http://localhost:8080/EMS/TOR", toronto);
		Endpoint montrealEndPoint = Endpoint.publish("http://localhost:8080/EMS/MTL", montreal);
		Endpoint ottawaEndPoint = Endpoint.publish("http://localhost:8080/EMS/OTW", ottawa);

		System.out.println("Toronto service published: " + torontoEndPoint.isPublished());
		System.out.println("Montreal service published: " + montrealEndPoint.isPublished());
		System.out.println("Ottawa service published: " + ottawaEndPoint.isPublished());
		System.out.println("Exiting Servers!!!");

	}
}