package com.event.management.server;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.event.management.implementation.EventManagerClient;
import EventManagement.managerInterfaceHelper;
import EventManagement.managerInterface;

public class server {

	public static void main(String args[]) {
		try {
			// create and initialize the ORB //// get reference to rootpoa &amp; activate
			// the POAManager
			ORB orb = ORB.init(args, null);
			// -ORBInitialPort 1050 -ORBInitialHost localhost
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant for Toronto and register it with the ORB
			EventManagerClient toronto = new EventManagerClient("TOR");
			toronto.setORB(orb);// get object reference from the servant
			org.omg.CORBA.Object refTor = rootpoa.servant_to_reference(toronto);
			managerInterface hrefTor = managerInterfaceHelper.narrow(refTor);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRefTor = NamingContextExtHelper.narrow(objRef);
			NameComponent pathTor[] = ncRefTor.to_name("TOR");
			ncRefTor.rebind(pathTor, hrefTor);

			// create servant for Montreal and register it with the ORB
			EventManagerClient montreal = new EventManagerClient("MTL");
			montreal.setORB(orb);
			org.omg.CORBA.Object refMtl = rootpoa.servant_to_reference(montreal);
			managerInterface hrefMtl = managerInterfaceHelper.narrow(refMtl);
			// objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRefMtl = NamingContextExtHelper.narrow(objRef);
			NameComponent pathMtl[] = ncRefMtl.to_name("MTL");
			ncRefMtl.rebind(pathMtl, hrefMtl);

			// create servant for Ottawa and register it with the ORB
			EventManagerClient ottawa = new EventManagerClient("OTW");
			ottawa.setORB(orb);
			org.omg.CORBA.Object refOtw = rootpoa.servant_to_reference(ottawa);
			managerInterface hrefOtw = managerInterfaceHelper.narrow(refOtw);
			// objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRefOtw = NamingContextExtHelper.narrow(objRef);
			NameComponent pathOtw[] = ncRefOtw.to_name("OTW");
			ncRefOtw.rebind(pathOtw, hrefOtw);

			// get object reference from the servant

			System.out.println("All the Servers Started");

			// wait for invocations from clients
			for (;;) {
				orb.run();
			}
		}

		catch (InvalidName | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound
				| AdapterInactive | ServantNotActive | WrongPolicy e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("Exiting Servers!!!");

	}
}