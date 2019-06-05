package com.orderon.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;

import com.orderon.interfaces.ICustomer;
import com.orderon.interfaces.IReservation;

public class ReservationManager extends AccessManager implements IReservation {

	public ReservationManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Boolean createNewReservation(String hotelId, int maleCount, int femaleCount, int childrenCount,
			String bookingTime, String firstName, String surName, String mobileNumber, Boolean isPriorityCust,
			String emailId) {

		ICustomer dao = new CustomerManager(false);
		Customer customer = dao.getCustomerDetails(hotelId, mobileNumber);
		if(customer == null) {
			dao.addCustomer(hotelId, firstName, surName, mobileNumber, "", "", "", "", false, isPriorityCust, emailId, "");
			customer = dao.getCustomerDetails(hotelId, mobileNumber);
		}else if(isPriorityCust) {
			String sql = "UPDATE Customer SET isPriority = '" +true+ "' WHERE id = "+customer.getId()+";";
			db.executeUpdate(sql, true);
		}
		
		String sql = "INSERT INTO Reservations(hotelId, customerId, maleCount, femaleCount, childrenCount, bookingTime, timeStamp, state, type)"
				+ "VALUES('" + hotelId + "', " + customer.getId() + ", " + maleCount
				+ ", " + femaleCount + ", " + childrenCount + ", '" + bookingTime + "', '" + LocalDateTime.now()
				+ "', " + RESERVATION_STATE_BOOKED + ", " + TYPE_RESERVATION + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean createNewWaitList(String hotelId, int maleCount, int femaleCount, int childrenCount,
			String firstName, String surName, String mobileNumber, Boolean isPriorityCust, String emailId) {

		ICustomer dao = new CustomerManager(false);
		Customer customer = dao.getCustomerDetails(hotelId, mobileNumber);
		if(customer == null) {
			dao.addCustomer(hotelId, firstName, surName, mobileNumber, "", "", "", "", false, isPriorityCust, emailId, "");
			customer = dao.getCustomerDetails(hotelId, mobileNumber);
		}else if(isPriorityCust) {
			String sql = "UPDATE Customer SET isPriority = '" +true+ "' WHERE id = "+customer.getId()+";";
			db.executeUpdate(sql, true);
		}
		
		String sql = "INSERT INTO Reservations(hotelId, customerId, maleCount, femaleCount, childrenCount, bookingTime, bookingDate, timeStamp, state, type)"
				+ "VALUES('" + hotelId + "', " + customer.getId() + ", " + maleCount
				+ ", " + femaleCount + ", " + childrenCount + ", '" + parseTime("HH:mm") + "', '" + LocalDateTime.now()
				+ "', " + RESERVATION_STATE_WAITING + ", " + TYPE_WAITLIST + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean editReservation(String hotelId, int reservationId, int maleCount, int femaleCount,
			int childrenCount, String bookingTime) {

		String sql = "UPDATE Reservations SET maleCount = " + maleCount + ", femaleCount = "
				+ femaleCount + ", childrenCount = " + childrenCount + ", bookingTime = '" + bookingTime
				+ "' WHERE  hotelId='" + hotelId + "' AND reservationId = " + reservationId + ";";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean updateReservationState(String hotelId, int reservationId, int state) {

		String sql = "UPDATE Reservations SET state = " + state + " WHERE  hotelId='" + hotelId + "' AND reservationId = " + reservationId + ";";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean assignOrderToReservation(String hotelId, int reservationId, int state, String orderId) {

		String sql = "UPDATE Reservations SET state = " + state 
				+ ", orderId = '"+orderId+"' WHERE  hotelId='" + hotelId + "' AND reservationId = " + reservationId + ";";
		return db.executeUpdate(sql, true);
	}

	@Override
	public ArrayList<Reservation> getReservations(String hotelId, String bookingDate){
		
		String sql = "SELECT Reservations.*, Customers.mobileNo, Customers.customer, Customers.isPriority FROM Reservations, "
				+ "Customers WHERE Customers.Id == Reservations.customerId AND hotelId='" + hotelId + "' AND bookingDate = " + bookingDate 
				+ " AND type = "+TYPE_RESERVATION+";";
		return db.getRecords(sql, Reservation.class, hotelId);
	}

	@Override
	public ArrayList<Reservation> getWaitList(String hotelId, String bookingDate){
		
		String sql = "SELECT Reservations.*, Customers.mobileNo, Customers.customer, Customers.isPriority FROM Reservations, "
				+ "Customers WHERE Customers.Id == Reservations.customerId AND hotelId='" + hotelId + "' AND bookingDate = " + bookingDate 
				+ " AND type = "+TYPE_WAITLIST+";";
		return db.getRecords(sql, Reservation.class, hotelId);
	}
	
	@Override
	public Reservation getReservation(String hotelId, int reservationId){
		
		String sql = "SELECT Reservations.*, Customers.mobileNo, Customers.customer, Customers.isPriority FROM Reservations, "
				+ "Customers WHERE Reservations.id == "+reservationId+" AND hotelId='" + hotelId +"';";
		return db.getOneRecord(sql, Reservation.class, hotelId);
	}
}
