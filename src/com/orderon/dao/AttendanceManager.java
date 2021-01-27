package com.orderon.dao;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.orderon.interfaces.IAttendance;

public class AttendanceManager extends AccessManager implements IAttendance{

	public AttendanceManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Boolean hasAuthorizedAttendance(String hotelId, String serviceDate) {
		String sql = "SELECT * FROM Attendance WHERE isPresent = 1 AND authorisation != "+AUTHORIZE+" AND checkInDate = '"+serviceDate+"';";
		return db.hasRecords(sql, hotelId);
	}

	@Override
	public Boolean isPresent(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Attendance WHERE checkOutTime is NULL AND isPresent = 1 AND employeeId = '"
				+ employeeId + "';";
		return db.hasRecords(sql, hotelId);
	}

	@Override
	public Boolean hasCheckedInForFirstShift(String hotelId, String employeeId, String serviceDate) {
		String sql = "SELECT * FROM Attendance WHERE checkInDate = '" + serviceDate
				+ "' AND employeeId = '" + employeeId + "' AND hotelId = '" + hotelId + "' AND shift = 1;";
		return db.hasRecords(sql, hotelId);
	}

	@Override
	public Boolean hasCheckedIn(String hotelId, String employeeId, int shift, String serviceDate) {
		String sql = "SELECT * FROM Attendance WHERE checkInDate = '" + serviceDate
				+ "' AND employeeId = '" + employeeId + "' AND hotelId = '" + hotelId + "' AND shift = "+shift+";";
		return db.hasRecords(sql, hotelId);
	}

	@Override
	public Boolean removeAttendance(String hotelId, String employeeId, int shift, String serviceDate) {
		String sql = "DELETE FROM Attendance WHERE checkInDate = '" + serviceDate
				+ "' AND employeeId = '" + employeeId + "' AND hotelId = '" + hotelId + "' AND shift = "+shift+";";
		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public Boolean hasSecondShift(String hotelId, String startDate, String endDate) {
		String sql = "SELECT * FROM Attendance WHERE shift = 2 AND authorisation = 1 AND checkInDate BETWEEN '"
				+ startDate + "' AND '" + endDate + "';";
		return db.hasRecords(sql, hotelId);
	}

	@Override
	public ArrayList<Attendance> getAllAttendance(String hotelId, int shift, String serviceDate) {

		String sql = "SELECT Employee.employeeId, firstName, surName, Attendance.Id, Attendance.checkInTime , Attendance.checkOutTime, " +
				"Attendance.authorisation, Attendance.reason, Attendance.checkOutDate, Attendance.isPresent , Attendance.shift " +
				"FROM Employee " + 
				"LEFT OUTER JOIN Attendance ON Employee.employeeId == Attendance.employeeId AND Attendance.shift == "+shift+ 
				" AND Attendance.checkInDate == '"+serviceDate+"' WHERE Employee.hotelId = '" + hotelId + "' AND (designation != 'ADMINISTRATOR' || designation != 'OWNER');";
		return db.getRecords(sql, Attendance.class, hotelId);
	}

	@Override
	public boolean markExcused(String hotelId, String employeeId, String reason, int shift, String serviceDate) {

		String sql = "INSERT INTO ATTENDANCE (hotelId, employeeId, checkInTime, checkInDate, authorisation, isPresent, shift, reason)"
				+ " VALUES ('" + escapeString(hotelId) + "', '" + escapeString(employeeId) + "', '"
				+ LocalDateTime.now() + "', '" + escapeString(serviceDate) + "', " + AUTHORIZE + ", "
				+ EXCUSED + ", " + shift + ", '" + reason + "');";

		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public boolean markAbsent(String hotelId, String employeeId, int shift, String serviceDate) {

		String sql = "INSERT INTO ATTENDANCE (hotelId, employeeId, checkInTime, checkInDate, authorisation, isPresent, shift)"
				+ " VALUES ('" + escapeString(hotelId) + "', '" + escapeString(employeeId) + "', '"
				+ LocalDateTime.now() + "', '" + escapeString(serviceDate) + "', " + AUTHORIZE + ", "
				+ ABSENT + ", " + shift + ");";

		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public String checkOutAll(String hotelId) {

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String checkOutDate = now.format(formatter);
		formatter = DateTimeFormatter.ofPattern("HH:mm");

		String sql = "UPDATE ATTENDANCE SET checkOutTime = '" + now + "', checkOutDate = '" + checkOutDate
				+ "' WHERE hotelId = '" + hotelId + "' AND isPresent = 1 AND checkOutDate is null;";
		
		System.out.println(sql);
		db.executeUpdate(sql, hotelId, true);

		return checkOutDate;
	}

	@Override
	public String checkOut(String hotelId, int attendanceId) {

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String checkOutDate = now.format(formatter);
		formatter = DateTimeFormatter.ofPattern("HH:mm");

		String sql = "UPDATE ATTENDANCE SET checkOutTime = '" + now + "', checkOutDate = '" + checkOutDate
				+ "' WHERE hotelId = '" + hotelId + "' AND Id = '" + attendanceId + "';";

		db.executeUpdate(sql, hotelId, true);

		return checkOutDate;
	}

	@Override
	public String checkInEmployee(String hotelId, String employeeId, int shift, String serviceDate) {

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		String checkInTime = now.format(formatter);

		String sql = "INSERT INTO ATTENDANCE (hotelId, employeeId, checkInTime, checkInDate, authorisation, isPresent, shift)"
				+ " VALUES ('" + escapeString(hotelId) + "', '" + escapeString(employeeId) + "', '" + now + "', '"
				+ serviceDate + "', " + UNAUTHORIZE + ", " + PRESENT + ", " + shift
				+ ");";

		db.executeUpdate(sql, hotelId, true);

		return checkInTime;
	}

	@Override
	public boolean authorizeEmployee(String hotelId, int attendanceId) {

		String sql = "UPDATE ATTENDANCE SET authorisation = " + AUTHORIZE + " WHERE hotelId = '" + hotelId
				+ "' AND Id = '" + attendanceId + "';";

		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public int getLastAttendanceId(String hotelId) {
		String sql = "SELECT MAX (Id) AS entityId FROM Attendance;";

		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);
		return entity.getId();
	}
}
