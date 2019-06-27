package EventManagement;


/**
* EventManagement/managerInterfaceOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from EventManagement.idl
* Thursday, June 27, 2019 3:13:50 PM EDT
*/

public interface managerInterfaceOperations 
{
  String addEvent (String managerId, String eventId, String eventType, String eventCapacity);
  String removeEvent (String managerId, String eventId, String eventType);
  String listEventAvailability (String managerId, String eventType);
  String eventBooking (String customerId, String eventId, String eventType);
  String cancelBooking (String customerId, String eventId, String eventType);
  String getBookingSchedule (String customerId);
  String swapEvent (String customerId, String newEventId, String newEventType, String oldEventId, String oldEventType);
} // interface managerInterfaceOperations
