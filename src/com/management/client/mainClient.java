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
import com.management.interfaceDef.managerInterface;

/**
 *
 * @author Himen Sidhpura
 */
public class mainClient {

	static Registry reg;
	static managerInterface managerObj;
	public static void main(String[] args) throws NotBoundException, IOException {

		reg = LocateRegistry.getRegistry(8080);
		BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Your ID");
		String id = br.readLine().trim();

		managerObj = null;

		if (id.charAt(3) == 'M') {
			createManagerObject(id.substring(0,3));
			System.out.println("1. Add Event ");
			System.out.println("2. Remove Event");
			System.out.println("3. List all Available Event");
			System.out.println("Select Any above option");
			String option = br.readLine().trim();
			managerObj.addEvent("Event is going to created on Toronto Server");
		} else if(id.charAt(3) == 'C') {
			createManagerObject(id.substring(0,3));
			System.out.println("1. Book Event ");
			System.out.println("2. List all event schedule");
			System.out.println("3. Cancel Event");
			System.out.println("Select Any above option");
			String option = br.readLine().trim();
		}
	}
	
	public static void createManagerObject(String serverName) throws AccessException, RemoteException, NotBoundException {
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
}
