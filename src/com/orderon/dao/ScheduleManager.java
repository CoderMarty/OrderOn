package com.orderon.dao;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.interfaces.ISchedules;

public class ScheduleManager extends AccessManager implements ISchedules {

	public ScheduleManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ArrayList<Schedule> getSchedules(String hotelId) {
		String sql = "SELECT * FROM Schedules  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, Schedule.class, hotelId);
	}

	@Override
	public boolean addSchedule(String hotelId, String name, String days, String timeSlots) {

		String sql = "INSERT INTO Schedules (hotelId, name, days, timeSlots) VALUES('" + escapeString(hotelId) + "', '"
				+ escapeString(name) + "', '" + days + "', '" + timeSlots + "');";
		return db.executeUpdate(sql, true);
	}

	@Override
	public Schedule getScheduleById(String hotelId, int scheduleId) {
		String sql = "SELECT * FROM Schedules WHERE id=" + scheduleId + " AND hotelId='" + escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Schedule.class, hotelId);
	}

	@Override
	public boolean deleteSchedule(String hotelId, int scheduleId) {
		String sql = "DELETE FROM Schedules WHERE id = " + scheduleId + " AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public JSONArray getSchedules(String hotelId, JSONArray scheduleIds){
		String sql = "";
		JSONArray schedules = new JSONArray();
		for(int i=0; i<scheduleIds.length(); i++) {
			try {
				sql = "SELECT * FROM Schedules WHERE id=" + scheduleIds.get(i) + " AND hotelId='" + escapeString(hotelId) + "';";
				schedules.put(new JSONObject(db.getOneRecord(sql, Schedule.class, hotelId)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return schedules;
	}

}
