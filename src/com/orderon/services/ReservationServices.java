package com.orderon.services;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import com.orderon.dao.AccessManager;
import com.orderon.dao.AccessManager.Reservation;
import com.orderon.dao.AccessManager.ServiceLog;
import com.orderon.dao.AccessManager.Settings;
import com.orderon.dao.OrderManager;
import com.orderon.dao.OutletManager;
import com.orderon.dao.ReservationManager;
import com.orderon.dao.ServiceManager;
import com.orderon.interfaces.IOrder;
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.IReservation;
import com.orderon.interfaces.IService;

@Path("/VendorServices")
public class ReservationServices {

	@POST
	@Path("/v1/addNewReservation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addNewReservation(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IReservation dao = new ReservationManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
				dao.createNewReservation(inObj.getString("hotelId"), inObj.getInt("maleCount")
						, inObj.getInt("femaleCount"), inObj.getInt("childrenCount"), inObj.getString("bookedTime"),
						inObj.getString("firstName"), inObj.getString("surName"), inObj.getString("mobileNo"), inObj.getBoolean("isPriorityCust"),
						inObj.getString("emailId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/addNewWaitList")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addNewWaitList(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IReservation dao = new ReservationManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
				dao.createNewWaitList(inObj.getString("hotelId"), inObj.getInt("maleCount")
						, inObj.getInt("femaleCount"), inObj.getInt("childrenCount"),inObj.getString("firstName"), inObj.getString("surName"), 
						inObj.getString("mobileNo"), inObj.getBoolean("isPriorityCust"), inObj.getString("emailId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/getReservations")
	@Produces(MediaType.APPLICATION_JSON)
	public String getReservations(@QueryParam("hotelId") String hotelId, @QueryParam("bookingDate") String bookingDate) {
		JSONObject reservationObj = null;
		JSONArray reservationArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		IReservation dao = new ReservationManager(false);
		try {
			ArrayList<Reservation> reservations = dao.getReservations(hotelId, bookingDate);
			
			for(int j=0; j<2; j++) {
				for (int i = 0; i < reservations.size(); i++) {
					reservationObj = new JSONObject();
					reservationObj.put("reservationId", reservations.get(i).getReservationId());
					reservationObj.put("customerId", reservations.get(i).getCustomerId());
					reservationObj.put("customerName", reservations.get(i).getCustomerName());
					reservationObj.put("mobileNumber", reservations.get(i).getMobileNumber());
					reservationObj.put("isPriorityCust", reservations.get(i).getIsPriorityCust());
					reservationObj.put("maleCount", reservations.get(i).getMaleCount());
					reservationObj.put("femaleCount", reservations.get(i).getFemaleCount());
					reservationObj.put("childrenCount", reservations.get(i).getChildrenCount());
					reservationObj.put("bookingDate", reservations.get(i).getBookingDate());
					reservationObj.put("bookedTime", reservations.get(i).getBookingTime());
					reservationObj.put("state", reservations.get(i).getState());
					reservationObj.put("timeStamp", reservations.get(i).getTimeStamp());
					reservationArr.put(reservationObj);
				}
				if(j==0) {
					outObj.put("reservations", reservationArr);
					reservations = dao.getWaitList(hotelId, bookingDate);
				}else {
					outObj.put("waitlist", reservationArr);
				}
				reservationArr = new JSONArray();
			}
			reservationObj = new JSONObject();
			reservationObj.put("booked", AccessManager.RESERVATION_STATE_BOOKED);
			reservationObj.put("cancelled", AccessManager.RESERVATION_STATE_CANCELLED);
			reservationObj.put("delayed", AccessManager.RESERVATION_STATE_DELAYED);
			reservationObj.put("waiting", AccessManager.RESERVATION_STATE_WAITING);
			reservationObj.put("seated", AccessManager.RESERVATION_STATE_SEATED);
			reservationArr.put(reservationObj);
			outObj.put("states", reservationArr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/editReservation")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String editReservation(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IReservation dao = new ReservationManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
					dao.editReservation(inObj.getString("hotelId"), inObj.getInt("reservationId"), inObj.getInt("maleCount")
							, inObj.getInt("femaleCount"), inObj.getInt("childrenCount"), inObj.getString("bookedTime")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateReservationState")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateReservationState(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IReservation dao = new ReservationManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
					dao.updateReservationState(inObj.getString("hotelId"), inObj.getInt("reservationId"), inObj.getInt("state")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/transferReservationToTable")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String transferReservationToTable(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IReservation dao = new ReservationManager(true);
		IOrder orderDao = new OrderManager(false);
		IOutlet outletDao = new OutletManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			outObj.put("status", -1);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			
			JSONArray tablesArr = inObj.getJSONArray("tableIds");
			String[] tableIds = new String[tablesArr.length()];
			for (int i = 0; i < tableIds.length; i++) {
				tableIds[i] = tablesArr.getString(i);
			}
			dao.beginTransaction(inObj.getString("systemId"));
			Reservation reservation = dao.getReservation(inObj.getString("hotelId"), inObj.getInt("reservationId"));
			ServiceLog service = serviceDao.getCurrentService(inObj.getString("hotelId"));
			
			Settings setting = outletDao.getSettings(inObj.getString("hotelId"));
			//outObj = orderDao.newOrder(inObj.getString("systemId"), inObj.getString("outletId"), setting.getHotelType(), inObj.getString("userId"), tableIds,
			//		reservation.getCovers(), reservation.getCustomerName(), reservation.getMobileNumber(), reservation.getCustomerAddress(),
			//		inObj.has("section")?inObj.getString("section"):"", "", service);
			
			if(outObj.getBoolean("status")) {
				dao.commitTransaction(inObj.getString("hotelId"));
			}else {
				dao.rollbackTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
}
