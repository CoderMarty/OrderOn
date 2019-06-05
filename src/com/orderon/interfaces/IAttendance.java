package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.Attendance;

public interface IAttendance extends IAccess{
	
	public Boolean hasAuthorizedAttendance(String hotelId, String serviceDate);

	public Boolean isPresent(String hotelId, String employeeId);

	public Boolean hasCheckedInForFirstShift(String hotelId, String employeeId, String serviceDate);
	
	public Boolean hasCheckedIn(String hotelId, String employeeId, int shift, String serviceDate);

	public Boolean removeAttendance(String hotelId, String employeeId, int shift, String serviceDate);

	public Boolean hasSecondShift(String hotelId, String startDate, String endDate);

	public ArrayList<Attendance> getAllAttendance(String hotelId, int shift, String serviceDate);

	public boolean markExcused(String hotelId, String employeeId, String reason, int shift, String serviceDate);

	public boolean markAbsent(String hotelId, String employeeId, int shift, String serviceDate);

	public String checkOutAll(String hotelId);

	public String checkOut(String hotelId, int attendanceId);

	public String checkInEmployee(String hotelId, String employeeId, int shift, String serviceDate);

	public boolean authorizeEmployee(String hotelId, int attendanceId);

	public int getLastAttendanceId(String hotelId);
}
