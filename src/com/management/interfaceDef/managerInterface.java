/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.management.interfaceDef;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 *
 * @author Himen Sidhpura
 */
public interface managerInterface extends Remote {

	public void sendMessage(String msg) throws RemoteException;

}
