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
	public ArrayList<Schedule> getSchedules(String systemId, String outletId) {
		String sql = "SELECT * FROM Schedules  WHERE outletId='" + outletId + "';";
		return db.getRecords(sql, Schedule.class, systemId);
	}

	@Override
	public boolean addSchedule(String corporateID, String restaurantId, String systemId, 
			String outletId, String name, String days, String timeSlots) {

		String sql = "INSERT INTO Schedules (corporateID, restaurantId, systemId, outletId, "
				+ "name, days, timeSlots) VALUES('" + escapeString(corporateID) + "', '" + escapeString(restaurantId) 
				+ "', '" + escapeString(systemId) + "', '" + escapeString(outletId) + "', '"
				+ escapeString(name) + "', '" + days + "', '" + timeSlots + "');";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public Schedule getScheduleById(String systemId, int scheduleId) {
		String sql = "SELECT * FROM Schedules WHERE id=" + scheduleId + ";";
		return db.getOneRecord(sql, Schedule.class, systemId);
	}

	@Override
	public boolean deleteSchedule(String systemId, int scheduleId) {
		String sql = "DELETE FROM Schedules WHERE id = " + scheduleId + ";";
		return db.executeUpdate(sql, systemId, true);
	}

	@Override
	public JSONArray getSchedules(String systemId, String outletId, JSONArray scheduleIds){
		String sql = "";
		JSONArray schedules = new JSONArray();
		for(int i=0; i<scheduleIds.length(); i++) {
			try {
				sql = "SELECT * FROM Schedules WHERE id=" + scheduleIds.get(i) + " AND outletId='" + escapeString(outletId) + "';";
				schedules.put(new JSONObject(db.getOneRecord(sql, Schedule.class, systemId)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return schedules;
	}

}
