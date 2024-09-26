package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;

import com.orderon.dao.AccessManager.Schedule;

public interface ISchedules{

	public ArrayList<Schedule> getSchedules(String systemId, String outletId);
	
	public boolean addSchedule(String corporateID, String restaurantId, String systemId, String outletId, 
			String name, String days, String timeSlots);
	
	public Schedule getScheduleById(String systemId, int scheduleId);
	
	public boolean deleteSchedule(String systemId, int scheduleId);
	
	public JSONArray getSchedules(String systemId, String outletId, JSONArray scheduleIds);
}
