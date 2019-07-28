package com.event.management.frontend;

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

import EventManagement.managerInterfaceHelper;
import EventManagement.managerInterface;

public class FrontEnd {

	public static void main(String args[]) {
		try {
			// create and initialize the ORB //// get reference to rootpoa &amp; activate
			// the POAManager
			ORB orb = ORB.init(args, null);
			// -ORBInitialPort 1050 -ORBInitialHost localhost
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant for Toronto and register it with the ORB
			FrontEndImpl frontEndImpl = new FrontEndImpl();
			frontEndImpl.setORB(orb);// get object reference from the servant
			org.omg.CORBA.Object refTor = rootpoa.servant_to_reference(frontEndImpl);
			managerInterface hrefTor = managerInterfaceHelper.narrow(refTor);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRefTor = NamingContextExtHelper.narrow(objRef);
			NameComponent pathTor[] = ncRefTor.to_name("FrontEnd");
			ncRefTor.rebind(pathTor, hrefTor);


			System.out.println("FrontEnd Started");
			Runnable task1 = frontEndImpl::ReplicaOneReply;
            Runnable task2 = frontEndImpl::ReplicaTwoReply;
            Runnable task3 = frontEndImpl::ReplicaThreeReply;

            Thread thread = new Thread(task1);
            Thread thread2 = new Thread(task2);
            Thread thread3 = new Thread(task3);

            thread.start();
            thread2.start();
            thread3.start();

			// wait for invocations from clients
				orb.run();		}

		catch (InvalidName | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound
				| AdapterInactive | ServantNotActive | WrongPolicy e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("Exiting Servers!!!");

	}
}