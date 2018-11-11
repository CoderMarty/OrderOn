package com.orderon.JUnitTestSuite;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import com.orderon.AccessManager;
import com.orderon.AccessManager.Employee;
import com.orderon.AccessManager.MenuCollection;
import com.orderon.AccessManager.MenuItem;
import com.orderon.AccessManager.Order;
import com.orderon.AccessManager.OrderItem;
import com.orderon.AccessManager.ServiceLog;
import com.orderon.AccessManager.TableUsage;
import com.orderon.AccessManager.User;
import com.orderon.UserType;

import junit.framework.TestCase;

public class ServicesTest extends TestCase{
	
	public AccessManager dao = new AccessManager(false);
	public String hotelId = "h0002";
	public String userId = "Admin";
	public String password = "1234";
	public String user2 = "tempUser";

	/**
	 * Tests if User is valid or not
	 */
	@Test
	public void testValidateUser(){
		
		//Valid User
		User user = dao.validUser(hotelId, "Admin", password);
		assertNotNull(user);

		//Invalid Password
		user = dao.validUser(hotelId, "Admin", "2234");
		assertNull(user);

		//Invalid hotelId
		user = dao.validUser("bc01", "Admin", password);
		assertNull(user);

		//Invalid userName
		user = dao.validUser(hotelId, "Admin1", password);
		assertNull(user);
	}
	
	/**
	 * Tests if method returns null when user is invalid 
	 */
	@Test
	public void testValidMPUser(){

		//Valid User
		String output = dao.validMPUser(hotelId, "Admin", password);
		assertNotNull(output);

		//Invalid Password
		output = dao.validMPUser(hotelId, "Admin", "2234");
		assertNull(output);

		//Invalid userName
		output = dao.validMPUser(hotelId, "Admin1", password);
		assertNull(output);
	}
	
	/**
	 * Tests if KDS users are allowed access
	 * i.e. Administrator or Chef.
	 */
	@Test
	public void testValidKDSUser(){

		//Administrator
		String output = dao.validKDSUser(hotelId, "Admin", password);
		assertNotNull(output);

		//Manager
		output = dao.validKDSUser(hotelId, "Orne", "9876");
		assertNull(output);

		//Chef
		output = dao.validKDSUser(hotelId, "Sonu", password);
		//assertNotNull(output);

		//Waiter
		output = dao.validKDSUser(hotelId, "S.singh", password);
		assertNull(output);
	}
	
	/**
	 * Tests if the user logged in has a valid auth Token.
	 */
	@Test
	public void testUserAuthentication(){
		//Valid User
		String output = dao.validMPUser(hotelId, userId, password);
		//test auth token
		assertNotNull(output);
		
		//Check if auth token is valid
		UserType userType = dao.validateToken(hotelId, userId, output);
		//Check the type of user.
		assertEquals(UserType.ADMINISTRATOR, userType);
		
		//log out user
		boolean output2 = dao.removeToken(hotelId, userId);
		//check if token is null
		assertTrue(output2);

		//Check if user can login after token is removed
		userType = dao.validateToken(hotelId, userId, output);
		//Check the type of user.
		assertEquals(UserType.UNAUTHORIZED, userType);
	}
	
	/**
	 * Test CRUD Operations for New User
	 */
	@Test
	public void testNewUserOperations(){

		//Add new user
		boolean output = dao.addUser(hotelId, user2, "HW017", 0, password);
		assertTrue(output);

		//Check if user exists
		output = dao.userExists(hotelId, user2);
		assertTrue(output);
		
		User user = dao.validUser(hotelId, user2, password);
		assertTrue(output);
		
		JSONObject obj = dao.updateUser(hotelId, user2, password, "4321", 3);
		
		//Check if user exists
		try {
			output = obj.getBoolean("status");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(output);
		
		user = dao.validUser(hotelId, user2, "4321");
		assertEquals("HW017", user.getEmployeeId());
		assertEquals("Swiggy Delivery", user.getName());
		assertEquals(user2, user.getUserId());
		
		//Delete user
		output = dao.deleteUser(hotelId, user2);
		assertTrue(output);
		
		//Check if user exists
		output = dao.userExists(hotelId, user2);
		assertFalse(output);
	}
	
	/**
	 * Test CRUD Operations for New Item
	 */
	@Test
	public void testMenuItemOperations(){

		String category = "testCollection";
		String item = "testItem";
		
		boolean output = dao.addCollection(hotelId, category, "No Image");
		assertTrue(output);
		
		output = dao.collectionExists(hotelId, category);
		assertTrue(output);
		
		MenuCollection menuCollection = dao.getCollectionByName(hotelId, category);
		assertEquals(category, menuCollection.getCollection());
		
		String menuId = dao.addMenuItem(hotelId, item, "test Description", category, "testStation", "", 10, 100.0, 100.0, 100.0, 0, "No image", 0
				, 0 , 0, "TD");
		assertNotNull(menuId);
		
		MenuItem menuItem = dao.itemExists(hotelId, item);
		assertNotNull(menuItem);
		
		assertEquals(menuId, menuItem.getMenuId());
		assertEquals(item, menuItem.getTitle());
		assertEquals("test Description", menuItem.getDescription());
		assertEquals(category, menuItem.getCategory());
		assertEquals("testStation", menuItem.getStation());
		assertEquals("", menuItem.getFlags());
		assertEquals(10, menuItem.getPreparationTime());
		assertEquals(100.0, menuItem.getRate());
		assertEquals(100.0, menuItem.getInhouseRate());
		assertEquals(100.0, menuItem.getCostPrice());
		assertEquals(0, menuItem.getVegType());
		assertEquals(0, menuItem.getHasIncentive());
		assertEquals(0, menuItem.getIncentive());
		assertEquals("TD", menuItem.getShortForm());
		
		output = dao.updateMenuItem(hotelId, menuId, item, "des 2", category, "testStation", "cs;", 12, 100.0, 100.0, 50.0, 1, "No image", 0,
				1, 50, "DD");
		assertTrue(output);
		
		output = dao.updateMenuItemState(hotelId, menuId, 1);
		assertTrue(output);
		
		menuItem = dao.getMenuItemByTitle(hotelId, item);
		assertEquals("des 2", menuItem.getDescription());
		assertEquals("cs;", menuItem.getFlags());
		assertEquals(12, menuItem.getPreparationTime());
		assertEquals(50.0, menuItem.getCostPrice());
		assertEquals(1, menuItem.getState());
		assertEquals(1, menuItem.getVegType());
		assertEquals(1, menuItem.getHasIncentive());
		assertEquals(50, menuItem.getIncentive());
		assertEquals("DD", menuItem.getShortForm());
		
		output = dao.deleteItem(hotelId, menuId);
		assertTrue(output);

		output = dao.deleteCollection(hotelId, category);
		assertTrue(output);
	}
	
	/**
	 * Test CRUD Operations for New Employee
	 */
	@Test
	public void testEmployeeOperations(){

		String employeeId = dao.addEmployee(hotelId, "testFN", "testMN", "testLN","test address", "0000000000",
				"01/01/2017", "Male", "01/01/2017", "MANAGER", "FLOOR", 0, 0, "No image", "test@test.com");
		assertNotNull(employeeId);
	
		Employee employee = dao.getEmployeeById(hotelId, employeeId);
		assertEquals(employeeId, employee.getEmployeeId());
		assertEquals("testFN", employee.getFirstName());
		assertEquals("testMN", employee.getMiddleName());
		assertEquals("testLN", employee.getSurName());
		assertEquals("test address", employee.getAddress());
		assertEquals("0000000000", employee.getContactNumber());
		assertEquals("01/01/2017", employee.getDob());
		assertEquals("Male", employee.getSex());
		assertEquals("01/01/2017", employee.getHiringDate());
		assertEquals("MANAGER", employee.getDesignation());
		assertEquals("FLOOR", employee.getDepartment());
		assertEquals(0, employee.getSalary());
		assertEquals(0, employee.getBonus());
		assertEquals("test@test.com", employee.getEmail());
		
		boolean output = dao.updateEmployee(hotelId, employeeId, "FN", "testMN", "testLN","test address", "1000000000",
				"02/01/2017", "Male", "01/01/2017", "WAITER", "FLOOR", 0, 0, "No image", "test1@test.com");
		assertTrue(output);
		
		employee = dao.getEmployeeById(hotelId, employeeId);
		assertEquals(employeeId, employee.getEmployeeId());
		assertEquals("FN", employee.getFirstName());
		assertEquals("testMN", employee.getMiddleName());
		assertEquals("testLN", employee.getSurName());
		assertEquals("test address", employee.getAddress());
		assertEquals("1000000000", employee.getContactNumber());
		assertEquals("02/01/2017", employee.getDob());
		assertEquals("Male", employee.getSex());
		assertEquals("01/01/2017", employee.getHiringDate());
		assertEquals("WAITER", employee.getDesignation());
		assertEquals("FLOOR", employee.getDepartment());
		assertEquals(0, employee.getSalary());
		assertEquals(0, employee.getBonus());
		assertEquals("test1@test.com", employee.getEmail());
		
		output = dao.deleteEmployee(hotelId, employeeId);
		assertTrue(output);
	}
	
	/**
	 * Tests the Ordering Operation
	 */
	@Test	
	public void testOrderingOperations(){
		
		String[] orderTables = {"4"};
		String orderId = "";
		String subOrderId = "";
		
		ArrayList<TableUsage> tables = dao.getTableUsage(hotelId, userId);
		assertNotNull(tables);
		assertEquals(19, tables.size());
		
		/*LocalDateTime now = LocalDateTime.now();
		String date = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		
		boolean output = dao.addService("h0001", "Lunch", date, 100);
		assertNotNull(output);
		
		ServiceLog service = dao.getCurrentService("h0001");
		assertEquals("Lunch", service.getServiceType());
		assertEquals(date, service.getServiceDate());
		assertEquals(100, service.getCashInHand());
		assertEquals(0, service.getIsCurrent());
		*/
		JSONObject orderObj = dao.newOrder(hotelId, user2, orderTables, 2, "Angelina", "9123456789", "C/103", "No Nuts", "");
		assertNotNull(orderObj);
		try {
			assertEquals(0, orderObj.getInt("status"));
			orderId = orderObj.getString("orderId");
			assertNotNull(orderId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		subOrderId = dao.getNextSubOrderId(hotelId, orderId);
		assertNotNull(subOrderId);
		
		JSONObject subOrderObj = dao.newSubOrder(hotelId, orderId, "1", 2, "No Onion", subOrderId, "waiter", 0);
		assertNotNull(subOrderObj);
		try {
			assertEquals(0, subOrderObj.get("status"));
			subOrderId = (String) subOrderObj.get("subOrderId");
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Order order = dao.getOrderById(hotelId, orderId);	
		assertEquals("Angelina", order.getCustomerName());
		assertEquals("9123456789", order.getCustomerNumber());
		assertEquals("waiter", order.getWaiterId());
		
		ArrayList<OrderItem> orderItems = dao.getOrderedItems(hotelId, orderId);
		
		boolean output = dao.cancelOrder(hotelId, orderId);
		
		//output = dao.endService("h0001", date, "Lunch");
		assertTrue(output);
	}
}
