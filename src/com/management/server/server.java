/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.server;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.management.implementation.managerImplementation;

/**
 *
 * @author Himen Sidhpura
 */
public class server {

	public static void main(String args[]) throws RemoteException, AlreadyBoundException {

	
		Registry reg = LocateRegistry.createRegistry(8080);
		managerImplementation toronto = new managerImplementation("TOR");
		managerImplementation montreal = new managerImplementation("MTL");
		managerImplementation ottawa = new managerImplementation("OTW");

		reg.bind("Toronto M", toronto);
		reg.bind("Montreal M", montreal);
		reg.bind("Ottawa M", ottawa);

		System.out.println("All Server  Started");
	}
}