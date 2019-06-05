package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.Employee;
import com.orderon.dao.AccessManager.Outlet;

public interface IEmployee {

	public String addEmployee(Outlet outlet, String firstName, String middleName, String surName, String address,
			String contactNumber, String dob, String sex, String hiringDate, String designation,
			int salary, int bonus, String image, String email);

	public Boolean updateEmployee(String hotelId, String employeeId, String firstName, String middleName,
			String surName, String address, String contactNumber, String dob, String sex, String hiringDate,
			String designation, int salary, int bonus, String image, String email);

	public Employee getEmployeeById(String hotelId, String employeeId);
	
	public Employee getEmployeeByName(String hotelId, String name);

	public ArrayList<Employee> getEmployeesByDesignation(String hotelId, String designation);
	
	public ArrayList<Employee> getEmployeesForNC(String hotelId);

	public ArrayList<Employee> getAllEmployee(String hotelId);

	public ArrayList<Employee> getAllDeliveryEmployee(String hotelId);

	public String getNextEmployeeId(String hotelId, String outletCode);

	public boolean deleteEmployee(String hotelId, String employeeId);
}
