package com.orderon.interfaces;

import java.util.ArrayList;

import org.json.JSONArray;

import com.orderon.dao.AccessManager.Schedule;

public interface ISchedules{

	public ArrayList<Schedule> getSchedules(String hotelId);
	
	public boolean addSchedule(String hotelId, String name, String days, String timeSlots);
	
	public Schedule getScheduleById(String hotelId, int scheduleId);
	
	public boolean deleteSchedule(String hotelId, int scheduleId);
	
	public JSONArray getSchedules(String hotelId, JSONArray scheduleIds);
}
