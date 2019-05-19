/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

	public static void main(String[] args) throws NotBoundException, IOException {

		Registry reg = LocateRegistry.getRegistry(8080);
		BufferedReader br =new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter Your ID");
		String id = br.readLine();

		managerInterface managerObj = null;

		if (id.charAt(3) == 'M') {

			if (id.startsWith("TOR")) {
				managerObj = (managerInterface) reg.lookup("Toronto M");
				managerObj.sendMessage("Request on Toronto Server ");
			} else if (id.startsWith("MTL")) {
				managerObj = (managerInterface) reg.lookup("Montreal M");
				managerObj.sendMessage("Request on Montreal Server ");

			} else if (id.startsWith("OTW")) {
				managerObj = (managerInterface) reg.lookup("Ottawa M");
				managerObj.sendMessage("Request on Ottawa Server ");

			}
		}
	}
}
