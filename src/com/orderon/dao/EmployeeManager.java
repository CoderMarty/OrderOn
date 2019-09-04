package com.orderon.dao;

import java.util.ArrayList;

import com.orderon.interfaces.IEmployee;

public class EmployeeManager extends AccessManager implements IEmployee{

	public EmployeeManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String addEmployee(String systemId, String systemCode, String firstName, String middleName, String surName, String address,
			String contactNumber, String dob, String sex, String hiringDate, String designation,
			int salary, int bonus, String image, String email) {

		String employeeId = getNextEmployeeId(systemId, systemCode);
		boolean sendOperationalEmail = false;
		boolean sendEODEmail = false;
		if(designation.equals(com.orderon.commons.Designation.OWNER.toString())) {
			sendEODEmail = true;
			sendOperationalEmail = true;
		}else if(designation.equals(com.orderon.commons.Designation.MANAGER.toString())){
			sendOperationalEmail = true;
		}
		String sql = "INSERT INTO Employee "
				+ "(hotelId, employeeId, firstName, surName, address, contactNumber, dob, sex, hiringDate, designation"
				+ ", salary, bonus, image, middleName, email, sendOperationalEmail, sendEODEmail) VALUES('" + escapeString(systemId) + "', '"
				+ escapeString(employeeId) + "', '" + escapeString(firstName) + "', '" + escapeString(surName) + "', '"
				+ escapeString(address) + "', '" + escapeString(contactNumber) + "', '" + escapeString(dob) + "', '"
				+ escapeString(sex) + "', '" + escapeString(hiringDate) + "', '" + escapeString(designation) 
				+ "', " + Integer.toString(salary) + ", " + Integer.toString(bonus) + ", '"
				+ (image.equals("No image") ? "" : "1") + "', '" + escapeString(middleName) + "', '"
				+ escapeString(email) + "', '"+sendOperationalEmail+"', '"+sendEODEmail+"');";

		if (db.executeUpdate(sql, systemId, true)) {
			return employeeId;
		} else
			return "";
	}

	@Override
	public Boolean updateEmployee(String hotelId, String employeeId, String firstName, String middleName,
			String surName, String address, String contactNumber, String dob, String sex, String hiringDate,
			String designation, int salary, int bonus, String image, String email) {

		String sql = "UPDATE Employee SET firstName = '" + escapeString(firstName) + "', middleName = '"
				+ escapeString(middleName) + "', surName = '" + escapeString(surName) + "', address = '"
				+ escapeString(address) + "', contactNumber = '" + escapeString(contactNumber) + "', email = '"
				+ escapeString(email) + "', dob = '" + escapeString(dob) + "', sex = '" + escapeString(sex)
				+ "', hiringDate = '" + escapeString(hiringDate) + "', designation = '" + escapeString(designation)
				+ "', salary = " + Integer.toString(salary)
				+ ", bonus = " + Integer.toString(bonus) + ", image  ='" + (image.equals("No image") ? "" : "1")
				+ "' WHERE hotelId = '" + escapeString(hotelId) + "' AND employeeId = '" + escapeString(employeeId)
				+ "';";

		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public Employee getEmployeeById(String hotelId, String employeeId) {
		String sql = "SELECT * FROM Employee WHERE employeeId = '" + escapeString(employeeId) + "' AND hotelId = '"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Employee.class, hotelId);
	}

	@Override
	public Employee getEmployeeByName(String hotelId, String name) {
		String sql = "SELECT * FROM Employee WHERE firstName = '" + escapeString(name) + "' COLLATE NOCASE AND hotelId = '"
				+ escapeString(hotelId) + "';";
		return db.getOneRecord(sql, Employee.class, hotelId);
	}

	@Override
	public ArrayList<Employee> getEmployeesByDesignation(String hotelId, String designation) {
		String sql = "SELECT * FROM Employee WHERE designation = '" + escapeString(designation.toString())
				+ "' AND hotelId = '" + escapeString(hotelId) + "';";
		return db.getRecords(sql, Employee.class, hotelId);
	}

	@Override
	public ArrayList<Employee> getEmployeesForNC(String hotelId) {
		String sql = "SELECT * FROM Employee WHERE designation = '"+ 
				AccessManager.DESIGNATION_OWNER+"' OR designation = '"+AccessManager.DESIGNATION_MANAGER+""
				+ "' AND hotelId = '" + escapeString(hotelId) + "';";
		return db.getRecords(sql, Employee.class, hotelId);
	}

	@Override
	public ArrayList<Employee> getAllEmployee(String hotelId) {
		String sql = "SELECT * FROM Employee WHERE hotelId='" + escapeString(hotelId) + "';";
		return db.getRecords(sql, Employee.class, hotelId);
	}

	@Override
	public ArrayList<Employee> getAllDeliveryEmployee(String hotelId) {
		String sql = "SELECT * FROM Employee WHERE hotelId='" + escapeString(hotelId)
				+ "' AND designation = 'DELIVERYBOY';";
		return db.getRecords(sql, Employee.class, hotelId);
	}

	@Override
	public String getNextEmployeeId(String hotelId, String outletCode) {

		String sql = "SELECT MAX(CAST(SUBSTR(employeeId,"+(outletCode.length()+1)+") AS integer)) AS entityId FROM Employee WHERE hotelId='"
				+ hotelId + "'";
		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		String employeeId = outletCode;

		if (entity != null) {
			return employeeId + (String.format("%03d", entity.getId() + 1));
		}
		return employeeId + "000";
	}

	@Override
	public boolean deleteEmployee(String hotelId, String employeeId) {
		String sql = "DELETE FROM Employee WHERE employeeId = '" + employeeId + "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, hotelId, true);
	}
}
