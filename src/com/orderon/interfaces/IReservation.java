package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.Reservation;

public interface IReservation extends IAccess{
	
	public Boolean createNewReservation(String hotelId, int maleCount, int femaleCount,
			int childrenCount, String bookingTime, String firstName, String surName, String mobileNumber, Boolean isPriorityCust,
			String emailId);

	public Boolean createNewWaitList(String hotelId, int maleCount, int femaleCount,
			int childrenCount, String firstName, String surName, String mobileNumber, Boolean isPriorityCust,
			String emailId);

	public Boolean editReservation(String hotelId, int reservationId, int maleCount, int femaleCount,
			int childrenCount, String bookingTime);

	public Boolean updateReservationState(String hotelId, int reservationId, int state);

	public Boolean assignOrderToReservation(String hotelId, int reservationId, int state, String orderId);
	
	public ArrayList<Reservation> getReservations(String hotelId, String bookingDate);
	
	public ArrayList<Reservation> getWaitList(String hotelId, String bookingDate);
	
	public Reservation getReservation(String hotelId, int reservationId);
}
