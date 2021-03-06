package EventManagement;


/**
* EventManagement/managerInterfaceHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from EventManagement.idl
* Sunday, July 7, 2019 1:17:15 PM EDT
*/

abstract public class managerInterfaceHelper
{
  private static String  _id = "IDL:EventManagement/managerInterface:1.0";

  public static void insert (org.omg.CORBA.Any a, EventManagement.managerInterface that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static EventManagement.managerInterface extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (EventManagement.managerInterfaceHelper.id (), "managerInterface");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static EventManagement.managerInterface read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_managerInterfaceStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, EventManagement.managerInterface value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static EventManagement.managerInterface narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof EventManagement.managerInterface)
      return (EventManagement.managerInterface)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      EventManagement._managerInterfaceStub stub = new EventManagement._managerInterfaceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static EventManagement.managerInterface unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof EventManagement.managerInterface)
      return (EventManagement.managerInterface)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      EventManagement._managerInterfaceStub stub = new EventManagement._managerInterfaceStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
