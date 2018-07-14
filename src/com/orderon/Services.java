package com.orderon;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.AccessManager.AddOn;
import com.orderon.AccessManager.Attendance;
import com.orderon.AccessManager.Bank;
import com.orderon.AccessManager.CollectionWiseReportA;
import com.orderon.AccessManager.CollectionWiseReportB;
import com.orderon.AccessManager.Customer;
import com.orderon.AccessManager.CustomerReport;
import com.orderon.AccessManager.DailyDiscountReport;
import com.orderon.AccessManager.DailyOperationReport;
import com.orderon.AccessManager.Discount;
import com.orderon.AccessManager.Employee;
import com.orderon.AccessManager.EntityString;
import com.orderon.AccessManager.Expense;
import com.orderon.AccessManager.GrossSaleReport;
import com.orderon.AccessManager.Hotel;
import com.orderon.AccessManager.IncentiveReport;
import com.orderon.AccessManager.KitchenDisplayOrders;
import com.orderon.AccessManager.KitchenStation;
import com.orderon.AccessManager.LoyaltyOffer;
import com.orderon.AccessManager.LoyaltySetting;
import com.orderon.AccessManager.LunchDinnerSalesReport;
import com.orderon.AccessManager.MPNotification;
import com.orderon.AccessManager.MenuItem;
import com.orderon.AccessManager.MonthReport;
import com.orderon.AccessManager.Notification;
import com.orderon.AccessManager.Order;
import com.orderon.AccessManager.OrderAddOn;
import com.orderon.AccessManager.OrderItem;
import com.orderon.AccessManager.OrderSpecification;
import com.orderon.AccessManager.Report;
import com.orderon.AccessManager.ServerLog;
import com.orderon.AccessManager.ServiceLog;
import com.orderon.AccessManager.Specifications;
import com.orderon.AccessManager.Stock;
import com.orderon.AccessManager.Table;
import com.orderon.AccessManager.TableUsage;
import com.orderon.AccessManager.User;
import com.orderon.AccessManager.YearlyReport;
import com.orderon.AccessManager.itemWiseReport;

@Path("/Services")
public class Services {

	// static Logger logger = Logger.getLogger(Services.class);
	private static final String api_version = "1.01A rev.10023";

	@GET
	@Path("/v1/heartbeat")
	@Produces(MediaType.APPLICATION_JSON)
	public String hearbeat() {
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", true);
			outObj.put("hotelId", Configurator.getHotelId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/validateUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String validateUser(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
					dao.validUser(inObj.getString("hotelId"), inObj.getString("userId"), inObj.getString("passwd")));
			if (outObj.getBoolean("status")) {
				AccessManager.User user = dao.getUserById(inObj.getString("hotelId"), inObj.getString("userId"));
				outObj.put("fullName",
						dao.getEmployeeById(inObj.getString("hotelId"), user.getEmployeeId()).getFullName());
				outObj.put("type", user.getUserType());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/validateMPUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String validateMPUser(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			dao.updateServerStatus(inObj.getString("hotelId"), false);
			String authToken = dao.validMPUser(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("passwd"));

			if (authToken == null) {
				outObj.put("status", false);
			} else {
				outObj.put("status", true);
				User user = dao.getUserById(inObj.getString("hotelId"), inObj.getString("userId"));
				outObj.put("fullName",
						dao.getEmployeeById(inObj.getString("hotelId"), user.getEmployeeId()).getFullName());
				outObj.put("type", user.getUserType());
				outObj.put("authToken", user.getAuthToken());
				Hotel hotel = dao.getHotelById(inObj.getString("hotelId"));
				outObj.put("hotelType", hotel.getHotelType());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/validateManager")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String validateManager(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.validateAccess1(inObj.getString("hotelId"), inObj.getString("userId"), inObj.getString("passwd")))
				outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/validateKDSUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String validateKDSUser(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String authToken = dao.validKDSUser(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("passwd"));

			if (authToken != null)
				outObj.put("status", true);

			if (outObj.getBoolean("status")) {
				User user = dao.getUserById(inObj.getString("hotelId"), inObj.getString("userId"));
				outObj.put("fullName",
						dao.getEmployeeById(inObj.getString("hotelId"), user.getEmployeeId()).getFullName());
				outObj.put("type", user.getUserType());
				outObj.put("authToken", user.getAuthToken());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/validateToken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String validateToken(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			UserType userType = dao.validateToken(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("authToken"));
			if (userType != UserType.UNAUTHORIZED)
				outObj.put("status", true);

			outObj.put("userType", userType.toString());
		} catch (NullPointerException e) {
			System.out.print(e);
		} catch (JSONException e) {
			System.out.print(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/removeToken")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String removeToken(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			if (dao.removeToken(inObj.getString("hotelId"), inObj.getString("userId")))
				outObj.put("status", "true");

		} catch (NullPointerException e) {
			System.out.print(e);
		} catch (JSONException e) {
			System.out.print(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/isExistingCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String isExistingCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.collectionExists(inObj.getString("hotelId"), inObj.getString("name")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addUser(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			int userType = UserType.valueOf(inObj.getString("userType")).getValue();

			outObj.put("status", dao.addUser(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("employeeId"), userType, inObj.getString("userPasswd")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getUser(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONObject userDetails = new JSONObject();
		try {
			userDetails.put("status", false);
			User user = dao.getUserById(hotelId, userId);
			if (user != null) {
				userDetails.put("status", true);
				userDetails.put("fullName", dao.getEmployeeById(hotelId, user.getEmployeeId()).getFullName());
				userDetails.put("userType", UserType.getType(user.getUserType()).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userDetails.toString();
	}

	@POST
	@Path("/v1/addMenuItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addMenuItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.itemExists(inObj.getString("hotelId"), inObj.getString("title"))) {
				outObj.put("error", "Item Exists");
			} else {
				String menuId = dao.addMenuItem(inObj.getString("hotelId"), inObj.getString("title"),
						inObj.getString("description"), inObj.getString("category"), inObj.getString("station"),
						inObj.getString("flags"), inObj.getInt("preparationTime"), inObj.getInt("rate"),
						inObj.getInt("inhouseRate"), inObj.getInt("costPrice"), inObj.getInt("vegType"),
						inObj.getString("image"), inObj.getInt("isTaxable"), inObj.getInt("incentiveType"),
						inObj.getInt("incentive"));

				if (!menuId.equals("")) {
					outObj.put("status", true);
					outObj.put("menuId", menuId);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addEmployee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addEmployee(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			String employeeId = dao.addEmployee(inObj.getString("hotelId"), inObj.getString("firstName"),
					inObj.getString("middleName"), inObj.getString("surName"), inObj.getString("address"),
					inObj.getString("contactNumber"), inObj.getString("dob"), inObj.getString("sex"),
					inObj.getString("hiringDate"), inObj.getString("designation"), inObj.getString("department"),
					inObj.getInt("salary"), inObj.getInt("bonus"), inObj.getString("image"), inObj.getString("email"));

			if (!employeeId.equals("")) {
				outObj.put("status", true);
				outObj.put("employeeId", employeeId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateEmployee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateEmployee(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (inObj.getString("image") != "No image") {
				deleteFile(inObj.getString("hotelId"), inObj.getString("employeeId") + ".jpg", "Employees");
			}

			outObj.put("status",
					dao.updateEmployee(inObj.getString("hotelId"), inObj.getString("employeeId"),
							inObj.getString("firstName"), inObj.getString("middleName"), inObj.getString("surName"),
							inObj.getString("address"), inObj.getString("contactNumber"), inObj.getString("dob"),
							inObj.getString("sex"), inObj.getString("hiringDate"), inObj.getString("designation"),
							inObj.getString("department"), inObj.getInt("salary"), inObj.getInt("bonus"),
							inObj.getString("image"), inObj.getString("email")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/deleteEmployee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deteleEmployee(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			User user = dao.getUserByEmpId(inObj.getString("hotelId"), inObj.getString("employeeId"));
			if (user != null)
				dao.deleteUser(inObj.getString("hotelId"), user.getUserId());
			outObj.put("status", dao.deleteEmployee(inObj.getString("hotelId"), inObj.getString("employeeId")));
			deleteFile(inObj.getString("hotelId"), inObj.getString("employeeId") + ".jpg", "Employees");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateMenuItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMenuItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (inObj.getString("image") != "No image") {
				deleteFile(inObj.getString("hotelId"), inObj.getString("menuId") + ".jpg", "MenuItems");
			}

			outObj.put("status",
					dao.updateMenuItem(inObj.getString("hotelId"), inObj.getString("menuId"), inObj.getString("title"),
							inObj.getString("description"), inObj.getString("category"), inObj.getString("station"),
							inObj.getString("flags"), inObj.getInt("preparationTime"), inObj.getInt("rate"),
							inObj.getInt("inhouseRate"), inObj.getInt("costPrice"), inObj.getInt("vegType"),
							inObj.getString("image"), inObj.getInt("isTaxable"), inObj.getInt("incentiveType"),
							inObj.getInt("incentive")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/changeState")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String changeState(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.changeMenuItemState(inObj.getString("hotelId"), inObj.getString("menuId"),
					inObj.getInt("state")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateMenuItemState")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMenuItemState(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.updateMenuItemState(inObj.getString("hotelId"), inObj.getString("menuId"),
					inObj.getInt("state")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/transferTable")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String transferTable(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.transferTable(inObj.getString("hotelId"), inObj.getString("oldTableId"),
					inObj.getString("newTableId"), inObj.getString("orderId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateUser(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			int userType = UserType.valueOf(inObj.getString("userType")).getValue();
			boolean isValid = dao.checkPassword(inObj.getString("userId"), inObj.getString("oldPassword"),
					inObj.getString("hotelId"));
			if (isValid) // Jason , checking correctness of old password
			{
				outObj.put("status", dao.updateUser(inObj.getString("hotelId"), inObj.getString("userId"),
						inObj.getString("userPass"), userType));
			} else { // old password is incorrect
				outObj.put("status", "incorrect"); // Jason
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAllUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllUsers(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			outObj.put("users", outArr);
			ArrayList<User> users = dao.getAllUsers(hotelId);
			for (int i = 0; i < users.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("userId", users.get(i).getUserId());
				Employee user = dao.getEmployeeById(hotelId, users.get(i).getEmployeeId());
				if (user == null)
					continue;
				obj.put("userName", user.getFullName());
				obj.put("userType", UserType.getType(users.get(i).getUserType()).toString());
				outArr.put(obj);
			}
			outObj.put("users", outArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAllEmployees")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllEmployees(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			outObj.put("users", outArr);
			String imgLocation;
			ArrayList<Employee> employees = dao.getAllEmployee(hotelId);
			for (int i = 0; i < employees.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("employeeId", employees.get(i).getEmployeeId());
				obj.put("firstName", employees.get(i).getFirstName());
				obj.put("surName", employees.get(i).getSurName());
				obj.put("address", employees.get(i).getAddress());
				obj.put("contactNumber", employees.get(i).getContactNumber());
				obj.put("dob", employees.get(i).getDob());
				obj.put("sex", employees.get(i).getSex());
				obj.put("hiringDate", employees.get(i).getHiringDate());
				obj.put("designation", employees.get(i).getDesignation());
				obj.put("department", employees.get(i).getDepartment());
				obj.put("salary", employees.get(i).getSalary());
				obj.put("bonus", employees.get(i).getBonus());

				imgLocation = "/hotels/" + hotelId + "/Employees/" + employees.get(i).getEmployeeId() + ".jpg";

				obj.put("image", imgLocation);
				outArr.put(obj);
			}
			outObj.put("employees", outArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getEmployee")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEmployee(@QueryParam("hotelId") String hotelId, @QueryParam("employeeId") String employeeId) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			Employee employee = dao.getEmployeeById(hotelId, employeeId);
			outObj.put("employeeId", employeeId);
			outObj.put("firstName", employee.getFirstName());
			outObj.put("middleName", employee.getMiddleName());
			outObj.put("surName", employee.getSurName());
			outObj.put("address", employee.getAddress());
			outObj.put("contactNumber", employee.getContactNumber());
			outObj.put("dob", employee.getDob());
			outObj.put("sex", employee.getSex());
			outObj.put("hiringDate", employee.getHiringDate());
			outObj.put("designation", employee.getDesignation());
			outObj.put("department", employee.getDepartment());
			outObj.put("salary", employee.getSalary());
			outObj.put("bonus", employee.getBonus());
			outObj.put("image", "/hotels/" + hotelId + "/Employees/" + employeeId + ".jpg");
			outObj.put("email", employee.getEmail());
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getEmployeeByDesignation")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEmployeeByDesignation(@QueryParam("hotelId") String hotelId,
			@QueryParam("designation") String deisgnation) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject employeeObj = null;
		try {
			ArrayList<Employee> employees = dao.getEmployeesByDesignation(hotelId, Designation.valueOf(deisgnation));
			for (int i = 0; i < employees.size(); i++) {
				employeeObj = new JSONObject();
				employeeObj.put("name", employees.get(i).getFirstName());
				outArr.put(employeeObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outArr.toString();
	}

	@GET
	@Path("/v1/getAllAttendance")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllAttendance(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = null;
		JSONArray shiftArr = new JSONArray();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);

			for (int x = 1; x <= 2; x++) {
				ArrayList<Attendance> attendance = dao.getAllAttendance(hotelId, x);
				outArr = new JSONArray();
				for (int i = 0; i < attendance.size(); i++) {
					JSONObject obj = new JSONObject();
					obj.put("employeeId", attendance.get(i).getEmployeeId());
					obj.put("id", attendance.get(i).getId());
					obj.put("firstName", attendance.get(i).getFirstName());
					obj.put("surName", attendance.get(i).getSurName());
					obj.put("checkInTime", attendance.get(i).getCheckInTimeHHMM() == null ? ""
							: attendance.get(i).getCheckInTimeHHMM());
					obj.put("checkOutTime", attendance.get(i).getCheckOutTimeHHMM() == null ? ""
							: attendance.get(i).getCheckOutTimeHHMM());
					obj.put("checkInDate", attendance.get(i).getCheckInDate());
					obj.put("checkOutDate", attendance.get(i).getCheckOutDate());
					obj.put("authorisation", attendance.get(i).getAuthorisation());
					obj.put("shift", attendance.get(i).getShift());
					obj.put("reason", attendance.get(i).getReason());
					obj.put("isPresent", attendance.get(i).getIsPresent());
					outArr.put(obj);
				}
				shiftArr.put(outArr);
			}
			outObj.put("employees", shiftArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/checkIn")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String checkInEmployee(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			String checkInTime = dao.checkInEmployee(inObj.getString("hotelId"), inObj.getString("employeeId"),
					inObj.getInt("shift"));
			outObj.put("checkInTime", checkInTime);
			outObj.put("attendanceId", dao.getLastAttendanceId(inObj.getString("hotelId")));
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/authorizeAttendance")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String authorizeAttendance(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj.put("status", dao.authorizeEmployee(inObj.getString("hotelId"), inObj.getInt("attendanceId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/markAbsent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String markAbsent(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj.put("status",
					dao.markAbsent(inObj.getString("hotelId"), inObj.getString("employeeId"), inObj.getInt("shift")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/markExcused")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String markExcused(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj.put("status", dao.markExcused(inObj.getString("hotelId"), inObj.getString("employeeId"),
					inObj.getString("reason"), inObj.getInt("shift")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/checkOut")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String checkOut(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj.put("checkOutTime", dao.checkOut(inObj.getString("hotelId"), inObj.getInt("attendanceId")));
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getHotelName")
	@Produces(MediaType.TEXT_PLAIN)
	public String getHotel(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		AccessManager.Hotel hotel = dao.getHotelById(hotelId);
		if (hotel != null) {
			return hotel.getHotelName();
		}
		return "";
	}

	@GET
	@Path("/v1/getHotelDetails")
	@Produces(MediaType.TEXT_PLAIN)
	public String getHotelDetails(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			Hotel hotel = dao.getHotelById(hotelId);
			outObj.put("hotelName", hotel.getHotelName());
			outObj.put("hotelId", hotel.getHotelId());
			outObj.put("hotelAddress", hotel.getHotelAddress());
			outObj.put("hotelContact", hotel.getHotelContact());
			outObj.put("vatNumber", hotel.getVatNumber());
			outObj.put("gstNumber", hotel.getGstNumber());
			outObj.put("hotelType", hotel.getHotelType());
			outObj.put("kdsType", hotel.getKDSType());
			outObj.put("eodType", hotel.getEODType());
			outObj.put("kotCount", hotel.getKOTCount());
			outObj.put("hotelDescription", hotel.getDescription());
			outObj.put("hotelWebsite", hotel.getWebsite());
			outObj.put("hasSms", hotel.getIsSmsEnabled());
			if (hotel.getIsServerEnabled() == 0) {
				outObj.put("hasServer", hotel.getIsServerEnabled());
				outObj.put("updateStatus", dao.getLastServerLog(hotelId).getStatus());
				outObj.put("lastUpdateTime", dao.getLastServerLog(hotelId).getUpdateTime());
			}
			outObj.put("hasCashDrawer", hotel.getHasCashDrawer());
			outObj.put("imgLocation", Configurator.getImagesLocation());
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getHotelDetailsJSON")
	@Produces(MediaType.APPLICATION_JSON)
	public String getHotelDetail(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			Hotel hotel = dao.getHotelById(hotelId);
			outObj.put("hotelName", hotel.getHotelName());
			outObj.put("hotelAddress", hotel.getHotelAddress());
			outObj.put("hotelContact", hotel.getHotelContact());
			outObj.put("vatNumber", hotel.getVatNumber());
			outObj.put("gstNumber", hotel.getGstNumber());
			outObj.put("hotelType", hotel.getHotelType());
			outObj.put("kdsType", hotel.getKDSType());
			outObj.put("kotCount", hotel.getKOTCount());
			outObj.put("hotelDescription", hotel.getDescription());
			outObj.put("hotelWebsite", hotel.getWebsite());
			outObj.put("hasSms", hotel.getIsSmsEnabled());
			outObj.put("hasCashDrawer", hotel.getHasCashDrawer());
			outObj.put("hasServer", hotel.getIsServerEnabled());
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getUserStatistics")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserStatistics(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		Calendar c = Calendar.getInstance();
		JSONArray weeklyOrders = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject dayStats = null;
		int dow = c.get(Calendar.DAY_OF_WEEK);
		int dayCount = (dow == 1 ? 6 : (dow - Calendar.MONDAY));
		c.add(Calendar.DAY_OF_MONTH, -dayCount);
		try {
			for (int i = 0; i <= dayCount; i++) {
				dayStats = getDayStats(hotelId, userId, c.getTime());
				weeklyOrders.put(dayStats);
				c.add(Calendar.DAY_OF_MONTH, 1);
			}
			outObj.put("orders", weeklyOrders);
			outObj.put("targetOrders", getTargetOrders(weeklyOrders));
			outObj.put("rating", getRating(weeklyOrders));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getUserTables")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserTables(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray tablesArr = new JSONArray();
		JSONObject tableDetails = null;
		ArrayList<TableUsage> tables = dao.getTableUsage(hotelId, userId);
		try {
			for (int i = 0; i < tables.size(); i++) {
				tableDetails = new JSONObject();
				tableDetails.put("tableId", tables.get(i).getTableId());
				tableDetails.put("orderId", tables.get(i).getOrderId());
				tableDetails.put("userId", tables.get(i).getUserId());
				tableDetails.put("state", tables.get(i).getState());
				tablesArr.put(tableDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tablesArr.toString();
	}

	@GET
	@Path("/v1/getTables")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTables(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray tablesArr = new JSONArray();
		JSONObject tableDetails = null;
		ArrayList<TableUsage> tables = dao.getTables(hotelId);
		try {
			for (int i = 0; i < tables.size(); i++) {
				tableDetails = new JSONObject();
				tableDetails.put("tableId", tables.get(i).getTableId());
				tableDetails.put("waiter", tables.get(i).getWaiterId());
				tablesArr.put(tableDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tablesArr.toString();
	}

	@POST
	@Path("/v1/addCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addCollection(inObj.getString("hotelId"), inObj.getString("name"), inObj.getString("image"))) {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCollections")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCollections(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray collectionArr = new JSONArray();
		ArrayList<AccessManager.MenuCollection> collections = dao.getCollections(hotelId);
		for (int i = 0; i < collections.size(); i++) {
			collectionArr.put(collections.get(i).getCollection());
		}
		return collectionArr.toString();
	}

	@GET
	@Path("/v1/getUnit")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUnit() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (Unit unit : Unit.values()) {
				JSONObject obj = new JSONObject();

				if (unit == Unit.TABLESPOONGM)
					obj.put("unit", "TABLESPOON (GM)");
				else if (unit == Unit.TABLESPOONML)
					obj.put("unit", "TABLESPOON (ML)");
				else if (unit == Unit.TEASPOONGM)
					obj.put("unit", "TEASPOON (GM)");
				else if (unit == Unit.TEASPOONML)
					obj.put("unit", "TEASPOON (ML)");
				else
					obj.put("unit", unit);

				obj.put("association", unit.getAssociation());
				obj.put("value", unit.getConversion());
				outArr.put(obj);
			}
			outObj.put("units", outArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	private void printKOT(String hotelId, String orderId) {

		AccessManager dao = new AccessManager(false);
		if (!dao.getHotelById(hotelId).getKDSType().equals("NONKDS"))
			return;
		ArrayList<OrderItem> items = dao.getOrderedItemsForKOT(hotelId, orderId);

		ArrayList<OrderItem> beverageItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> kitchenItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> barItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> outDoorItems = new ArrayList<OrderItem>();

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getStation().equals("Beverage")) {
				beverageItems.add(items.get(i));
			} else if (items.get(i).getStation().equals("Kitchen")) {
				kitchenItems.add(items.get(i));
			} else if (items.get(i).getStation().equals("Bar")) {
				barItems.add(items.get(i));
			} else if (items.get(i).getStation().equals("Outdoor")) {
				outDoorItems.add(items.get(i));
				kitchenItems.add(items.get(i));
			}
		}

		Order order = dao.getOrderById(hotelId, orderId);
		int copies = 1;
		if (order.getInHouse() != 1)
			copies = 2;

		if (beverageItems.size() > 0) {
			defineKOT(beverageItems, dao, "BEVERAGE ORDER TAKING", Configurator.getBeveragePrinter(), hotelId, order,
					copies, false);
		}
		if (kitchenItems.size() > 0) {
			defineKOT(kitchenItems, dao, "KITCHEN ORDER TAKING", Configurator.getKitchenPrinter(), hotelId, order,
					copies, false);
		}
		if (barItems.size() > 0) {
			if (!hotelId.equals("sa0001"))
				defineKOT(barItems, dao, "BAR ORDER TAKING", Configurator.getBarPrinter(), hotelId, order, copies,
						false);
		}
		if (outDoorItems.size() > 0) {
			defineKOT(outDoorItems, dao, "OUTDOOR", Configurator.getOutdoorPrinter(), hotelId, order, 1, false);
		}

		if (hotelId.equals("sa0001")) {
			if (order.getInHouse() == 1)
				defineKOT(items, dao, "SUMMARY", "Cashier", hotelId, order, 1, false);
		} else {
			defineKOT(items, dao, "SUMMARY", "Cashier", hotelId, order, 1, false);
		}

		dao.updateKOTStatus(hotelId, orderId);
	}

	private void defineKOT(ArrayList<OrderItem> items, AccessManager dao, String title, String printerStation,
			String hotelId, Order order, int copies, boolean isCanceled) {

		String out = "<h3 class='centered' style='font-size:14px; margin-top:0px; margin-bottom:5px;'>";

		if (title.equals("SUMMARY"))
			out += "ORDER TAKING SUMMARY";
		else if (isCanceled) {
			out += "CANCELED KOT";
		} else {
			if (order.getInHouse() == 1)
				out += "TABLE ORDER";
			else if (order.getInHouse() == 0)
				out += "HOME DELIVERY";
			else
				out += "PARCEL";

			if (title.equals("OUTDOOR"))
				out += " SANNIDHI KOT";
		}
		Table table = dao.getTableById(hotelId, order.getTableId().split(",")[0]);

		out += "</h3><table style='width: 90%; padding: 0px; margin-left:25px;'><thead>"
				+ "<tr><th>Date</th><th>Time</th><th>Table</th><th>Waiter</th><th>Bill No.</th></tr>"
				+ "</thead><tbody>";

		out += "<tr><td>" + items.get(0).getSubOrderDate().substring(8, 10)
				+ items.get(0).getSubOrderDate().substring(4, 7) + "/" + items.get(0).getSubOrderDate().substring(0, 4)
				+ "</td>" + "<td>" + items.get(0).getSubOrderDate().substring(11, 16) + "</td>";
		String name = "";
		if (table != null)
			name = table.getWaiterId();

		out += "<td>" + order.getTableId() + "</td><td>" + name + "</td><td>" + order.getBillNo() + "</td></tr>"
				+ "</tbody></table>"
				+ "<p class ='ml25 mb0 mt0'>------------------------------------------------------------------------------------------</p>"
				+ "<table class='ml2 table-condensed' style='width: 90%; padding: 0px'>";

		if (isCanceled)
			out += "<thead style='font-size:12px;'><tr><th><u>QTY</u></th><th class='pull-left' style='width:75%'><u>DESCRIPTION</u></th></tr>"
					+ "</thead><tbody>";
		else
			out += "<thead style='font-size:12px;'><tr><th><u>QTY</u></th><th class='pull-left' style='width:50%'><u>DESCRIPTION</u></th><th class='pull-right'><u>SPECS</u></th></tr>"
					+ "</thead><tbody>";

		ArrayList<OrderSpecification> specifications = new ArrayList<OrderSpecification>();
		ArrayList<OrderAddOn> addOns = new ArrayList<OrderAddOn>();
		int quantity = 1;
		int currentQuantity = 0;
		boolean isComplete = true;
		String fontWeight = "";

		for (int i = 0; i < items.size(); i++) {
			String spec = "";
			quantity = items.get(i).getQty();
			isComplete = true;
			currentQuantity = 0;
			for (int j = 1; j <= quantity; j++) {
				specifications = dao.getOrderedSpecification(hotelId, items.get(i).getOrderId(),
						items.get(i).getMenuId(), items.get(i).getSubOrderId(), j);
				if (isCanceled)
					addOns = dao.getCanceledOrderedAddOns(hotelId, items.get(i).getOrderId(),
							items.get(i).getSubOrderId(), items.get(i).getMenuId(), j);
				else
					addOns = dao.getOrderedAddOns(hotelId, items.get(i).getOrderId(), items.get(i).getSubOrderId(),
							items.get(i).getMenuId(), j);

				for (int k = 0; k < specifications.size(); k++) {
					spec += specifications.get(k).getSpecification() + ", ";
				}
				spec = spec.length() > 0 ? spec.substring(0, spec.length() - 2) : spec;

				if (specifications.size() == 0 && addOns.size() == 0) {
					currentQuantity++;
					isComplete = false;
					if (j < quantity)
						continue;
				} else if (!isComplete) {
					fontWeight = currentQuantity == 5 ? "style='font-weight:regular'" : "''";
					if (hotelId.equals("ka0001")) {
						out += "<tr class='marg-td-small'><td class='pull-left' " + fontWeight + ">" + currentQuantity
								+ "</td>" + "<td class='pull-left'>" + formatTitle(items.get(i).getTitle(), true)
								+ "</td><td></td></tr>";
					} else {
						out += "<tr class='marg-td'><td class='pull-left' " + fontWeight + ">" + currentQuantity
								+ "</td>" + "<td class='pull-left'>" + formatTitle(items.get(i).getTitle(), false)
								+ "</td><td class='pull-right'>" + items.get(i).getSpecifications() + "</td></tr>";
					}
					isComplete = true;
					currentQuantity = 1;
				} else
					currentQuantity++;

				fontWeight = currentQuantity == 5 ? "style='font-weight:regular'" : "''";
				if (hotelId.equals("ka0001")) {
					out += "<tr class='marg-td-small'><td class='pull-left' " + fontWeight + ">" + currentQuantity
							+ "</td>" + "<td class='pull-left'>" + formatTitle(items.get(i).getTitle(), true);
				} else {
					out += "<tr class='marg-td'><td class='pull-left' " + fontWeight + ">" + currentQuantity + "</td>"
							+ "<td class='pull-left'>" + formatTitle(items.get(i).getTitle(), false);
				}
				if (!isCanceled) {
					spec = items.get(i).getSpecifications() + spec;
					out += "</td><td class='pull-right'>" + spec + "</td></tr>";
				}

				for (int k = 0; k < addOns.size(); k++) {
					fontWeight = addOns.get(k).getQty() == 5 ? "style='font-weight:regular'" : "''";
					if (hotelId.equals("ka0001"))
						out += "<tr class='marg-td-small'><td class='pull-left addOn-td' " + fontWeight + ">"
								+ addOns.get(k).getQty() + "</td><"
								+ "<td class='addOn-td pull-left' style='padding-left:50px !important'>+ "
								+ formatTitle(addOns.get(k).getName(), true);
					else
						out += "<tr class='marg-td'><td class='pull-left addOn-td' " + fontWeight + ">"
								+ addOns.get(k).getQty() + "</td><"
								+ "<td class='addOn-td pull-left' style='padding-left:50px !important'>+ "
								+ formatTitle(addOns.get(k).getName(), false);
					if (!isCanceled)
						out += "</td><td class='addOn-td'>" + "</td><td></td>/tr>";
				}
				spec = "";
				currentQuantity = 0;
			}
		}

		out += "</tbody></table>"
				+ "<p class ='ml25 mb0 mt0'>------------------------------------------------------------------------------------------</p>";

		//String html = "<html><body>" + out + "</body></html>";
		out = "<html><head><style>.table-condensed>thead>tr>th, .table-condensed>tbody>tr>th,"
				+ ".table-condensed>tfoot>tr>th, .table-condensed>thead>tr>td,"
				+ ".table-condensed>tbody>tr>td, .table-condensed>tfoot>tr>td {"
				+ "padding: 1px;} .mt0 {margin-top: 0px;} .mb0 {margin-bottom: 0px;} .ml25 {margin-left: 25px;} .ml2 {margin-left: 20px;} "
				+ ".mt-20 {margin-top: 20px;} .centered{text-align: center} .pull-right{text-align: right} .pull-left{text-align: left}"
				+ ".addOn-td{border-top: none !important; padding-top: 0px !important; line-height: 1 !important; font-size: 12px !important;}"
				+ ".table {width: 100%;max-width: 100%;margin-bottom: 20px;}"
				+ ".table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td,.table>tbody>tr>td, "
				+ ".table>tfoot>tr>td {padding: 8px;line-height: 1.42857143;border-top: 1px solid #ddd;}"
				+ ".table>thead>tr>th {vertical-align: bottom;border-bottom: 2px solid #ddd;} th {text-align: left;}"
				+ " h3, .h3 {font-size: 18px;} .marg-td{ margin-top:0px; margin-bottom:0px; font-size:12px; font-weight: bold;}"
				+ " .marg-td-small{ margin-top:0px; margin-bottom:0px; font-size:11px; font-weight: bold;}"
				+ "</style></head><body style='width: 377px; font-family:" + Configurator.getKOTFont() + ";'>" + out
				+ "</body></html>";

		print(out, printerStation, copies);
		// run(html);
	}

	@GET
	@Path("/v1/getOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOrder(@QueryParam("hotelId") String hotelId, @QueryParam("orderId") String orderId,
			@QueryParam("showConsolidated") Boolean showConsolidated,
			@QueryParam("showComplimentary") Boolean showComplimentary) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONArray addOnArr = new JSONArray();
		JSONArray specArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject orderObj = new JSONObject();
		JSONArray tablesArr = new JSONArray();
		JSONObject tableDetails = null;
		JSONObject itemDetails = null;
		JSONObject addOnDetails = null;
		JSONObject specDetails = null;
		ArrayList<OrderItem> orderItems = null;
		ArrayList<OrderAddOn> addOns = dao.getAllOrderedAddOns(hotelId, orderId);
		ArrayList<OrderSpecification> specifications = new ArrayList<OrderSpecification>();
		Order order = dao.getOrderById(hotelId, orderId);
		if (showConsolidated) {
			orderItems = dao.getOrderedItemForBill(hotelId, orderId);
			orderItems.addAll(dao.getOrderedItemForBillCI(hotelId, orderId));
		} else
			orderItems = dao.getOrderedItems(hotelId, orderId);

		if (showComplimentary)
			orderItems.addAll(dao.getComplimentaryOrderedItemForBill(hotelId, orderId));

		String menuId = "";
		int itemId = 1;
		int complimentaryTotal = 0;
		int rate = 0;
		try {
			String tableId = order.getTableId();
			if (order.getState() == AccessManager.ORDER_STATE_OFFKDS
					|| order.getState() == AccessManager.ORDER_STATE_SERVICE) {
				if (!dao.isBarOrder(hotelId, orderId)) {
					if (!dao.isTakeAwayOrder(hotelId, orderId)) {
						if (!dao.isHomeDeliveryOrder(hotelId, orderId)) {
							if (!dao.isTableOrder(hotelId, orderId)) {
								outObj.put("status", -1);
								outObj.put("message", "Order not available for this table");
								return outObj.toString();
							}
						}
					}
				}
			}

			for (int i = 0; i < orderItems.size(); i++) {
				if (menuId.equals(orderItems.get(i).getMenuId()))
					itemId++;
				else
					itemId = 1;

				itemDetails = new JSONObject();
				addOnArr = new JSONArray();
				specArr = new JSONArray();
				rate = orderItems.get(i).getRate();
				if (orderItems.get(i).getState() == 50) {
					rate = 0;
					complimentaryTotal += orderItems.get(i).getRate();
				}
				itemDetails.put("subOrderId", orderItems.get(i).getSubOrderId());
				itemDetails.put("id", orderItems.get(i).getId());
				itemDetails.put("subOrderDate", orderItems.get(i).getSubOrderDate());
				itemDetails.put("menuId", orderItems.get(i).getMenuId());
				itemDetails.put("vegType", orderItems.get(i).getVegType());
				itemDetails.put("title", orderItems.get(i).getTitle());
				itemDetails.put("collection", orderItems.get(i).getCategory());
				itemDetails.put("qty", orderItems.get(i).getQty());
				itemDetails.put("rate", rate);
				itemDetails.put("state", orderItems.get(i).getState());
				itemDetails.put("station", orderItems.get(i).getStation());
				itemDetails.put("isTaxable", orderItems.get(i).getIsTaxable());
				String specs = "";
				for (int k = 0; k < orderItems.get(i).getQty(); k++) {
					for (int j = 0; j < addOns.size(); j++) {
						if (addOns.get(j).getMenuId().equals(orderItems.get(i).getMenuId())
								&& addOns.get(j).getSubOrderId().equals(orderItems.get(i).getSubOrderId())
								&& addOns.get(j).getItemId() == itemId) {
							addOnDetails = new JSONObject();
							rate = addOns.get(j).getRate();
							if (addOns.get(j).getState() == 50) {
								rate = 0;
								complimentaryTotal += addOns.get(j).getRate();
							}
							addOnDetails.put("addOnId", addOns.get(j).getAddOnId());
							addOnDetails.put("name", addOns.get(j).getName());
							addOnDetails.put("itemId", addOns.get(j).getItemId());
							addOnDetails.put("quantity", addOns.get(j).getQty());
							addOnDetails.put("state", addOns.get(j).getState());
							addOnDetails.put("rate", rate);
							addOnArr.put(addOnDetails);
						}
					}
					specifications = dao.getOrderedSpecification(hotelId, orderId, orderItems.get(i).getMenuId(),
							orderItems.get(i).getSubOrderId(), itemId);
					for (int y = 0; y < specifications.size(); y++) {
						specDetails = new JSONObject();
						specs += specifications.get(y).getSpecification() + ", ";
						specDetails.put("spec", specifications.get(y).getSpecification());
						specDetails.put("itemId", specifications.get(y).getItemId());
						specArr.put(specDetails);
					}
					if (k < orderItems.get(i).getQty() - 1)
						itemId++;
				}
				specs = specs.length() == 0 ? orderItems.get(i).getSpecifications()
						: specs + ", " + orderItems.get(i).getSpecifications();
				itemDetails.put("specs", specs.replace(", , ", ", "));
				itemDetails.put("allSpecs", orderItems.get(i).getSpecifications());
				itemDetails.put("addOns", addOnArr);
				itemDetails.put("specArr", specArr);
				itemsArr.put(itemDetails);
				menuId = orderItems.get(i).getMenuId();
			}
			if (!tableId.equals(null)) {
				String[] joinedTables = tableId.split(",");
				for (int i = 0; i < joinedTables.length; i++) {
					tableDetails = new JSONObject();
					tableDetails.put("tableId", joinedTables[i]);
					tablesArr.put(tableDetails);
				}
			}
			orderObj.put("items", itemsArr);
			orderObj.put("tableId", tablesArr);
			Customer customer = dao.getCustomerDetails(hotelId, order.getCustomerNumber());
			if (customer != null) {
				orderObj.put("hasCustomer", true);
				orderObj.put("allergyInfo", customer.getAllergyInfo());
				orderObj.put("birthDate", customer.getBirthdate());
				orderObj.put("anniversary", customer.getAnniversary());
			} else
				orderObj.put("hasCustomer", false);
			orderObj.put("noOfGuests", order.getNumberOfGuests());
			orderObj.put("orderNumber", order.getOrderNumber());
			orderObj.put("waiterId", order.getWaiterId());
			orderObj.put("customerName", order.getCustomerName());
			orderObj.put("customerAddress", order.getCustomerAddress());
			orderObj.put("customerNumber", order.getCustomerNumber());
			orderObj.put("customerGst", order.getCustomerGst());
			orderObj.put("ambianceRating", order.getAmbianceRating());
			orderObj.put("hygieneRating", order.getHygieneRating());
			orderObj.put("serviceRating", order.getServiceRating());
			orderObj.put("foodRating", order.getQoFRating());
			orderObj.put("reviewSuggestions", order.getReviewSuggestions());
			orderObj.put("inhouse", order.getInHouse());
			orderObj.put("orderId", order.getOrderId());
			orderObj.put("billNo", order.getBillNo());
			orderObj.put("state", order.getState());
			orderObj.put("reason", order.getReason());
			orderObj.put("authId", order.getAuthId());
			orderObj.put("discountCode", order.getDiscountCode());
			orderObj.put("tableNumber",
					order.getTableId().length() == 3 || order.getTableId().length() == 2
							? order.getTableId().replace(",", "")
							: order.getTableId());
			orderObj.put("printCount", order.getPrintCount());
			orderObj.put("compTotal", complimentaryTotal);
			outObj.put("order", orderObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getReturnedItems")
	@Produces(MediaType.APPLICATION_JSON)
	public String getReturnedItems(@QueryParam("hotelId") String hotelId, @QueryParam("orderId") String orderId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONArray addOnArr = new JSONArray();
		JSONObject addOnDetails = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<OrderItem> orderItems = dao.getReturnedItems(hotelId, orderId);
		ArrayList<OrderAddOn> addOns = null;
		try {
			for (int i = 0; i < orderItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("subOrderId", orderItems.get(i).getSubOrderId());
				itemDetails.put("menuId", orderItems.get(i).getMenuId());
				itemDetails.put("title", orderItems.get(i).getTitle());
				itemDetails.put("qty", orderItems.get(i).getQty());
				itemDetails.put("rate", orderItems.get(i).getRate());
				itemDetails.put("state", orderItems.get(i).getState());
				itemDetails.put("reason", orderItems.get(i).getReason());
				if (orderItems.get(i).getItemId() == 0) {
					for (int j = 1; j <= orderItems.get(i).getQty(); j++) {
						addOns = dao.getReturnedAddOns(hotelId, orderId, orderItems.get(i).getSubOrderId(),
								orderItems.get(i).getMenuId(), j);
						for (int k = 0; k < addOns.size(); k++) {
							addOnDetails = new JSONObject();
							addOnDetails.put("name", addOns.get(k).getName());
							addOnDetails.put("itemId", addOns.get(k).getItemId());
							addOnDetails.put("quantity", addOns.get(k).getQty());
							addOnDetails.put("rate", addOns.get(k).getRate());
							addOnArr.put(addOnDetails);
						}
					}
				} else {
					addOns = dao.getReturnedAddOns(hotelId, orderId, orderItems.get(i).getSubOrderId(),
							orderItems.get(i).getMenuId(), orderItems.get(i).getItemId());
					for (int k = 0; k < addOns.size(); k++) {
						addOnDetails = new JSONObject();
						addOnDetails.put("name", addOns.get(k).getName());
						addOnDetails.put("itemId", addOns.get(k).getItemId());
						addOnDetails.put("quantity", addOns.get(k).getQty());
						addOnDetails.put("rate", addOns.get(k).getRate());
						addOnArr.put(addOnDetails);
					}
				}
				itemDetails.put("addOns", addOnArr);
				addOnArr = new JSONArray();
				itemsArr.put(itemDetails);
			}
			outObj.put("items", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAllOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllOrders(@QueryParam("hotelId") String hotelId, @QueryParam("dateFilter") String dateFilter,
			@QueryParam("query") String query) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;
		int orderState = 0;
		ArrayList<Order> order;
		if (dateFilter.length() > 0) {
			dateFilter = dateFilter.substring(6, 10) + "/" + dateFilter.substring(3, 6) + dateFilter.substring(0, 2);
			order = dao.getAllOrders(hotelId, dateFilter, query);
		} else {
			order = dao.getAllOrders(hotelId, dateFilter, query);
		}
		try {
			for (int i = 0; i < order.size(); i++) {

				orderState = order.get(i).getState();

				itemDetails = new JSONObject();
				itemDetails.put("orderId", order.get(i).getOrderId());
				itemDetails.put("hasServiceCharge", dao.checkServiceCharge(hotelId, order.get(i).getOrderId()));
				itemDetails.put("tableId",
						order.get(i).getTableId().length() == 3 || order.get(i).getTableId().length() == 2
								? order.get(i).getTableId().replace(",", "")
								: order.get(i).getTableId());
				itemDetails.put("orderNumber", order.get(i).getOrderNumber());
				itemDetails.put("customerName", order.get(i).getCustomerName());
				itemDetails.put("customerAddress", order.get(i).getCustomerAddress());
				itemDetails.put("orderDate", order.get(i).getOrderDate().toString().substring(0, 10));
				itemDetails.put("amountRecieved", dao.getTotalPaidAmount(hotelId, order.get(i).getOrderId()));
				itemDetails.put("inhouse", order.get(i).getInHouse());
				itemDetails.put("state", orderState);
				itemDetails.put("foodBill", order.get(i).getFoodBill());
				itemDetails.put("barBill", order.get(i).getBarBill());
				itemDetails.put("waiterId", order.get(i).getWaiterId());
				itemDetails.put("discount",
						orderState == 99 ? 0 : dao.getAppliedDiscount(hotelId, order.get(i).getOrderId()));
				itemDetails.put("billNo", order.get(i).getBillNo());
				itemsArr.put(itemDetails);
			}
			orderObj.put("orders", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderObj.toString();
	}

	@POST
	@Path("/v1/deleteOrderedItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deteleOrderedItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			JSONArray orderItems = inObj.getJSONArray("items");
			String orderId = inObj.getString("orderId");
			String hotelId = inObj.getString("hotelId");
			Order order = dao.getOrderById(hotelId, orderId);
			int quantity = 0;
			int returnQuantity = 0;
			ArrayList<OrderItem> beverageItems = new ArrayList<OrderItem>();
			ArrayList<OrderItem> kitchenItems = new ArrayList<OrderItem>();
			ArrayList<OrderItem> barItems = new ArrayList<OrderItem>();
			ArrayList<OrderItem> outDoorItems = new ArrayList<OrderItem>();
			OrderItem item = new OrderItem();
			boolean status = false;

			for (int i = 0; i < orderItems.length(); i++) {

				String type = orderItems.getJSONObject(i).getString("type");
				quantity = orderItems.getJSONObject(i).getInt("qty");
				returnQuantity = orderItems.getJSONObject(i).getInt("returnQty");

				if (type.equals("return")) {
					quantity = quantity - returnQuantity;
					status = dao.updateOrderItemLog(hotelId, orderId,
							orderItems.getJSONObject(i).getString("subOrderId"),
							orderItems.getJSONObject(i).getString("menuId"),
							orderItems.getJSONObject(i).getString("reason"), type, returnQuantity,
							orderItems.getJSONObject(i).getInt("rate"), orderItems.getJSONObject(i).getInt("qty"));

				} else if (type.equals("cancel")) {
					quantity = quantity - returnQuantity;
					status = dao.updateOrderItemLog(hotelId, orderId,
							orderItems.getJSONObject(i).getString("subOrderId"),
							orderItems.getJSONObject(i).getString("menuId"),
							orderItems.getJSONObject(i).getString("reason"), type,
							orderItems.getJSONObject(i).getInt("qty"), orderItems.getJSONObject(i).getInt("rate"),
							orderItems.getJSONObject(i).getInt("qty"));
				} else {
					quantity = 0;
					status = dao.updateOrderItemLog(hotelId, orderId,
							orderItems.getJSONObject(i).getString("subOrderId"),
							orderItems.getJSONObject(i).getString("menuId"),
							orderItems.getJSONObject(i).getString("reason"), type,
							orderItems.getJSONObject(i).getInt("qty"), orderItems.getJSONObject(i).getInt("rate"),
							orderItems.getJSONObject(i).getInt("qty"));
				}
				if (!status) {
					outObj.put("message", "OrderItemLog could not be updated. Please contact OrderOn support.");
					dao.rollbackTransaction();
					return outObj.toString();
				}

				item = dao.getOrderedItem(hotelId, orderId, orderItems.getJSONObject(i).getString("subOrderId"),
						orderItems.getJSONObject(i).getString("menuId"));
				item.setQuantity(returnQuantity);
				if (item.getStation().equals("Beverage")) {
					beverageItems.add(item);
				} else if (item.getStation().equals("Kitchen")) {
					kitchenItems.add(item);
				} else if (item.getStation().equals("Bar")) {
					barItems.add(item);
				} else if (item.getStation().equals("Outdoor")) {
					outDoorItems.add(item);
				}

				boolean isRemoved = dao.removeSubOrder(hotelId, orderId,
						orderItems.getJSONObject(i).getString("subOrderId"),
						orderItems.getJSONObject(i).getString("menuId"), quantity);

				if (isRemoved) {
					dao.updateFoodBill(hotelId, orderId, orderItems.getJSONObject(i).getString("menuId"), quantity,
							true);
					ArrayList<OrderAddOn> addOns = dao.getOrderedAddOns(hotelId, orderId,
							orderItems.getJSONObject(i).getString("subOrderId"),
							orderItems.getJSONObject(i).getString("menuId"), orderItems.getJSONObject(i).getInt("qty"));
					for (OrderAddOn orderAddOn : addOns) {
						dao.updateOrderAddOnLog(hotelId, orderId, orderItems.getJSONObject(i).getString("subOrderId"),
								orderItems.getJSONObject(i).getString("menuId"), orderAddOn.getItemId(), "void",
								orderAddOn.getQty(), orderAddOn.getRate(), orderAddOn.getAddOnId());
						dao.removeAddOns(hotelId, orderId, orderItems.getJSONObject(i).getString("subOrderId"),
								orderItems.getJSONObject(i).getString("menuId"), quantity);
						dao.updateFoodBillAddOnReturn(hotelId, orderId,
								orderItems.getJSONObject(i).getString("subOrderId"),
								orderItems.getJSONObject(i).getString("menuId"), orderAddOn);
					}
					outObj.put("isTaxable",
							dao.isTaxableMenuItem(hotelId, orderItems.getJSONObject(i).getString("menuId")));
				} else {
					outObj.put("message", "Item could not be deleted. Please contact OrderOn support.");
					dao.rollbackTransaction();
					return outObj.toString();
				}
				if (type.equals("return")) {
					String subject = "Item Returned from Bill No. " + orderItems.getJSONObject(i).getString("billNo");
					String text = "<div style='width:350px; ' class='alert alert-warning'><h3>An Item has been returned from Order. Bill No. "
							+ orderItems.getJSONObject(i).getString("billNo") + ".</h3><p> Details as follows:</p>"
							+ "<div>Item Name: " + orderItems.getJSONObject(i).getString("title").replace("$", " ")
							+ "</div><div>Price : " + orderItems.getJSONObject(i).getInt("rate")
							+ "</div><div>Refund Amount: " + orderItems.getJSONObject(i).getInt("refund")
							+ "</div><div>Time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)
							+ "</div><div>Order punched by: " + orderId.split(":")[0] + "</div><div>Item Returned by : "
							+ inObj.getString("userId") + "</div><div>Reason: "
							+ orderItems.getJSONObject(i).getString("reason") + "</div></div>";
					status = SendVoidReturnEmail(hotelId, subject, text, null, orderId);
					if (!status) {
						outObj.put("message", "Email could not be sent. Please check your internet connection");
					}
				}
			}
			if (beverageItems.size() > 0) {
				defineKOT(beverageItems, dao, "BEVERAGE ORDER TAKING", Configurator.getBeveragePrinter(), hotelId,
						order, 1, true);
			}
			if (kitchenItems.size() > 0) {
				defineKOT(kitchenItems, dao, "KITCHEN ORDER TAKING", Configurator.getKitchenPrinter(), hotelId, order,
						1, true);
			}
			if (barItems.size() > 0) {
				defineKOT(barItems, dao, "BAR ORDER TAKING", Configurator.getBarPrinter(), hotelId, order, 1, true);
			}
			if (outDoorItems.size() > 0) {
				defineKOT(outDoorItems, dao, "OUTDOOR", Configurator.getOutdoorPrinter(), hotelId, order, 1, true);
			}
			outObj.put("status", true);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updatePrintCount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updatePrintCount(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);

			outObj.put("status", dao.updateOrderPrintCount(inObj.getString("hotelId"), inObj.getString("orderId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/voidOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String voidOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			inObj = new JSONObject(jsonObject);
			String orderId = inObj.getString("orderId");
			String billNo = inObj.getString("billNo");
			outObj = dao.voidOrder(inObj.getString("hotelId"), orderId, inObj.getString("reason"),
			inObj.getString("authId"));
			if (outObj.getBoolean("status")) {
				String subject = "Bill No. " + billNo + " marked void!";
				String text = "<div style='width:300px; '><div class='alert alert-warning'><b>Bill No. " + billNo
						+ " </b>has been marked void.</h3><p> Details as follows:</p>" + "<div>Time: "
						+ LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + "</div><div>Order punched by: "
						+ orderId.split(":")[0] + "</div><div>Authorizer: " + inObj.getString("authId")
						+ "</div><div>Reason: " + inObj.getString("reason") + "</div></div>";
				SendVoidReturnEmail(inObj.getString("hotelId"), subject, text, null, inObj.getString("orderId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/complimentaryOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String complimentaryOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String orderId = inObj.getString("orderId");
			String billNo = inObj.getString("billNo");

			if (dao.complimentaryOrder(inObj.getString("hotelId"), orderId, inObj.getString("authId"))) {
				outObj.put("status", true);
				String subject = "Bill No. " + billNo + " marked complimentary!";
				String text = "<div style='width:300px; '><div class='alert alert-warning'><b>Bill No. " + billNo
						+ " </b>has been marked complimentary.</h3><p> Details as follows:</p>" + "<div>Time: "
						+ LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + "</div><div>Order punched by: "
						+ orderId.split(":")[0] + "</div><div>Authorizer: " + inObj.getString("authId")
						+ "</div></div>";
				SendVoidReturnEmail(inObj.getString("hotelId"), subject, text, null, inObj.getString("orderId"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/complimentaryItems")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String complimentaryItems(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		boolean status = false;
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String orderId = inObj.getString("orderId");
			JSONArray compItems = inObj.getJSONArray("compItems");

			for (int i = 0; i < compItems.length(); i++) {
				if (compItems.getJSONObject(i).getBoolean("status")) {
					if(!dao.complimentaryItem(inObj.getString("hotelId"), orderId,
							compItems.getJSONObject(i).getString("menuId"), inObj.getString("authId"),
							compItems.getJSONObject(i).getString("subOrderId"),
							compItems.getJSONObject(i).getInt("rate"), compItems.getJSONObject(i).getInt("qty"), "Complimentary")) {
						outObj.put("message",
								"Item could not be marked complimentary. Please try again, if problem persists contact OrderOn support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}
				}
				JSONArray compAddons = compItems.getJSONObject(i).getJSONArray("compAddOns");

				for (int j = 0; j < compAddons.length(); j++) {
					status = dao.complimentaryAddOn(inObj.getString("hotelId"), orderId,
							compAddons.getJSONObject(j).getInt("addOnId"), inObj.getString("authId"),
							compItems.getJSONObject(i).getString("menuId"),
							compItems.getJSONObject(i).getString("subOrderId"),
							compAddons.getJSONObject(j).getInt("itemId"), compAddons.getJSONObject(j).getInt("rate"),
							compAddons.getJSONObject(j).getInt("qty"));
					if (!status) {
						outObj.put("message",
								"Item could not be marked complimentary. Please try again, if problem persists contact OrderOn support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}
				}
			}
			outObj.put("status", true);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCompletedOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCompletedOrders(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Order> orderItems = dao.getAllOrders(hotelId, "", "");
		int state = 0;
		try {
			for (int i = 0; i < orderItems.size(); i++) {
				state = orderItems.get(i).getState();
				if (state == 1) {
					itemDetails = new JSONObject();
					itemDetails.put("orderId", orderItems.get(i).getOrderId());
					itemDetails.put("tableId",
							orderItems.get(i).getTableId().length() == 3 || orderItems.get(i).getTableId().length() == 2
									? orderItems.get(i).getTableId().replace(",", "")
									: orderItems.get(i).getTableId());
					itemDetails.put("customerName", orderItems.get(i).getCustomerName());
					itemDetails.put("customerAddress", orderItems.get(i).getCustomerAddress());
					itemDetails.put("customerNumber", orderItems.get(i).getCustomerNumber());
					itemDetails.put("orderNumber", orderItems.get(i).getOrderNumber());
					itemDetails.put("pax", orderItems.get(i).getNumberOfGuests());
					itemDetails.put("inhouse", orderItems.get(i).getInHouse());
					itemDetails.put("foodBill", orderItems.get(i).getFoodBill());
					itemDetails.put("barBill", orderItems.get(i).getBarBill());
					itemDetails.put("billNo", orderItems.get(i).getBillNo());
					itemDetails.put("orderNo", orderItems.get(i).getOrderNumber());
					itemsArr.put(itemDetails);
				}
			}
			orderObj.put("orders", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderObj.toString();
	}

	private String getTablesForOrder(AccessManager dao, String hotelId, String orderId) {
		ArrayList<Table> joinedTables = dao.getJoinedTables(hotelId, orderId);
		if (joinedTables.size() == 1) {
			return joinedTables.get(0).getTableId();
		} else {
			String tableIds = "[";
			for (int i = 0; i < joinedTables.size(); i++) {
				if (!tableIds.equals("[")) {
					tableIds += "," + joinedTables.get(i).getTableId();
				} else {
					tableIds += joinedTables.get(i).getTableId();
				}
			}
			tableIds += "]";
			return tableIds;
		}
	}

	@GET
	@Path("/v1/getKDSOrdersListView")
	@Produces(MediaType.APPLICATION_JSON)
	public String getKDSOrdersListView(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONArray addOnArr = new JSONArray();
		JSONArray specArr = new JSONArray();
		JSONArray allSpecArr = new JSONArray();
		JSONObject addOnDetails = new JSONObject();
		JSONObject specDetails = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<KitchenDisplayOrders> orderItems = dao.getKDSOrdersListView(hotelId);
		ArrayList<OrderAddOn> addOns = null;
		ArrayList<OrderSpecification> specifications = null;
		String orderId = "";
		int quantity = 0;
		int currentQuantity = 0;
		boolean hasAddedLast = false;
		try {
			for (int i = 0; i < orderItems.size(); i++) {
				orderId = orderItems.get(i).getOrderId();
				quantity = orderItems.get(i).getQty();
				currentQuantity = 0;
				hasAddedLast = false;
				for (int x = 1; x <= quantity; x++) {
					addOns = dao.getOrderedAddOns(hotelId, orderId, orderItems.get(i).getSubOrderId(),
							orderItems.get(i).getMenuId(), x);
					for (int y = 0; y < addOns.size(); y++) {
						addOnDetails = new JSONObject();
						addOnDetails.put("name", addOns.get(y).getName());
						addOnDetails.put("itemId", addOns.get(y).getItemId());
						addOnDetails.put("quantity", addOns.get(y).getQty());
						addOnDetails.put("rate", addOns.get(y).getRate());
						addOnArr.put(addOnDetails);
					}
					specifications = dao.getOrderedSpecification(hotelId, orderId, orderItems.get(i).getMenuId(),
							orderItems.get(i).getSubOrderId(), x);
					for (int y = 0; y < specifications.size(); y++) {
						specDetails = new JSONObject();
						specDetails.put("spec", specifications.get(y).getSpecification());
						specDetails.put("itemId", specifications.get(y).getItemId());
						specArr.put(specDetails);
					}

					if (specifications.size() == 0 && addOns.size() == 0) {
						currentQuantity++;
						hasAddedLast = false;
					} else {
						specDetails = new JSONObject();
						specDetails.put("spec", orderItems.get(i).getSpecs());
						specDetails.put("itemId", 101);
						allSpecArr.put(specDetails);
						if (!hasAddedLast && currentQuantity > 0) {
							itemDetails = new JSONObject();
							itemDetails.put("menuId", orderItems.get(i).getMenuId());
							itemDetails.put("orderId", orderItems.get(i).getOrderId());
							itemDetails.put("tableId", getTablesForOrder(dao, hotelId, orderId));
							itemDetails.put("subOrderId", orderItems.get(i).getSubOrderId());
							itemDetails.put("title", orderItems.get(i).getTitle());
							itemDetails.put("qty", currentQuantity);
							itemDetails.put("specs", orderItems.get(i).getSpecs());
							itemDetails.put("state", orderItems.get(i).getOrderState());
							itemDetails.put("customerAddress", orderItems.get(i).getCustomerAddress());
							itemDetails.put("customerName", orderItems.get(i).getCustomerName());
							itemDetails.put("inhouse", orderItems.get(i).getInhouse());
							itemDetails.put("billNo", orderItems.get(i).getBillNo());
							itemDetails.put("vegType", orderItems.get(i).getVegType());
							itemDetails.put("addOns", addOnArr);
							itemDetails.put("specifications", allSpecArr);
							itemsArr.put(itemDetails);
							hasAddedLast = true;
						}
						specArr.put(specDetails);
						itemDetails = new JSONObject();
						itemDetails.put("menuId", orderItems.get(i).getMenuId());
						itemDetails.put("orderId", orderItems.get(i).getOrderId());
						itemDetails.put("tableId", getTablesForOrder(dao, hotelId, orderId));
						itemDetails.put("subOrderId", orderItems.get(i).getSubOrderId());
						itemDetails.put("title", orderItems.get(i).getTitle());
						itemDetails.put("qty", 1);
						itemDetails.put("specs", orderItems.get(i).getSpecs());
						itemDetails.put("state", orderItems.get(i).getOrderState());
						itemDetails.put("customerAddress", orderItems.get(i).getCustomerAddress());
						itemDetails.put("customerName", orderItems.get(i).getCustomerName());
						itemDetails.put("inhouse", orderItems.get(i).getInhouse());
						itemDetails.put("billNo", orderItems.get(i).getBillNo());
						itemDetails.put("vegType", orderItems.get(i).getVegType());
						itemDetails.put("addOns", addOnArr);
						itemDetails.put("specifications", specArr);
						itemsArr.put(itemDetails);
						hasAddedLast = true;
					}
					addOnArr = new JSONArray();
					specArr = new JSONArray();
					allSpecArr = new JSONArray();
				}
				if (!hasAddedLast) {
					specDetails = new JSONObject();
					specDetails.put("spec", orderItems.get(i).getSpecs());
					specDetails.put("itemId", 101);
					specArr.put(specDetails);
					itemDetails = new JSONObject();
					itemDetails.put("menuId", orderItems.get(i).getMenuId());
					itemDetails.put("orderId", orderItems.get(i).getOrderId());
					itemDetails.put("tableId", getTablesForOrder(dao, hotelId, orderId));
					itemDetails.put("subOrderId", orderItems.get(i).getSubOrderId());
					itemDetails.put("title", orderItems.get(i).getTitle());
					itemDetails.put("qty", currentQuantity);
					itemDetails.put("specs", orderItems.get(i).getSpecs());
					itemDetails.put("state", orderItems.get(i).getOrderState());
					itemDetails.put("customerAddress", orderItems.get(i).getCustomerAddress());
					itemDetails.put("customerName", orderItems.get(i).getCustomerName());
					itemDetails.put("inhouse", orderItems.get(i).getInhouse());
					itemDetails.put("billNo", orderItems.get(i).getBillNo());
					itemDetails.put("vegType", orderItems.get(i).getVegType());
					itemDetails.put("addOns", addOnArr);
					itemDetails.put("specifications", specArr);
					itemsArr.put(itemDetails);
					hasAddedLast = true;
				}
				addOnArr = new JSONArray();
				specArr = new JSONArray();
			}

			outObj.put("items", itemsArr);
			outObj.put("status", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getMenu")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenu(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONArray addOnArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject addOnDetails = null;
		ArrayList<MenuItem> orderItems = dao.getMenu(hotelId);
		AddOn addOn = null;
		ArrayList<Integer> addOnIds = null;
		try {
			for (int i = 0; i < orderItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("menuId", orderItems.get(i).getMenuId());
				itemDetails.put("category", orderItems.get(i).getCategory());
				itemDetails.put("title", orderItems.get(i).getTitle());
				itemDetails.put("description", orderItems.get(i).getDescription());
				itemDetails.put("flags", orderItems.get(i).getFlags());
				itemDetails.put("vegType", orderItems.get(i).getVegType());
				itemDetails.put("rate", orderItems.get(i).getRate());
				itemDetails.put("inHouseRate", orderItems.get(i).getInhouseRate());
				itemDetails.put("image", orderItems.get(i).getImage());
				itemDetails.put("costPrice", orderItems.get(i).getCostPrice());
				itemDetails.put("state", orderItems.get(i).getState());
				itemDetails.put("isTaxable", orderItems.get(i).getIsTaxable());
				itemDetails.put("shortForm", orderItems.get(i).getShortForm());
				if (!orderItems.get(i).getAddOnString().equals("")) {
					addOnIds = orderItems.get(i).getAddOnIds();
					addOnArr = new JSONArray();
					for (int addOnId : addOnIds) {
						addOnDetails = new JSONObject();
						addOn = dao.getAddOnById(addOnId, hotelId);
						addOnDetails.put("id", addOn.getId());
						addOnDetails.put("name", addOn.getName());
						addOnDetails.put("inHouseRate", addOn.getInHouseRate());
						addOnDetails.put("deliveryRate", addOn.getDeliveryRate());
						addOnArr.put(addOnDetails);
					}
				} else
					addOnArr = new JSONArray();
				itemDetails.put("addOns", addOnArr);
				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getMenuItems")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuItems(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<EntityString> menuItems = dao.getMenuItems(hotelId);
		try {
			for (int i = 0; i < menuItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("title", menuItems.get(i).getEntity());
				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getMenuMP")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuMP(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<MenuItem> orderItems = dao.getMenuMP(hotelId);
		try {
			for (int i = 0; i < orderItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("menuId", orderItems.get(i).getMenuId());
				itemDetails.put("category", orderItems.get(i).getCategory());
				itemDetails.put("title", orderItems.get(i).getTitle());
				itemDetails.put("description", orderItems.get(i).getDescription());
				itemDetails.put("flags", orderItems.get(i).getFlags());
				itemDetails.put("vegType", orderItems.get(i).getVegType());
				itemDetails.put("rate", orderItems.get(i).getRate());
				itemDetails.put("inHouseRate", orderItems.get(i).getInhouseRate());
				itemDetails.put("image", orderItems.get(i).getImage());
				itemDetails.put("costPrice", orderItems.get(i).getCostPrice());
				itemDetails.put("state", orderItems.get(i).getState());
				itemDetails.put("isTaxable", orderItems.get(i).getIsTaxable());

				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getSpecifications")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getSpecifications(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONObject arrObj = null;
		JSONArray arr = new JSONArray();
		ArrayList<Specifications> specs = dao.getSpecifications(hotelId);
		try {
			for (int i = 0; i < specs.size(); i++) {
				arrObj = new JSONObject();
				arrObj.put("specification", specs.get(i).getSpecification());
				arr.put(arrObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr.toString();
	}

	@POST
	@Path("/v1/addSpecification")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addSpecification(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj.put("status", dao.addSpecification(inObj.getString("specification")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDeliveryPersons")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDeliveryPersons(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray personsArr = new JSONArray();
		JSONObject personDetails = null;
		ArrayList<Employee> persons = dao.getAllDeliveryEmployee(hotelId);
		try {
			for (int i = 0; i < persons.size(); i++) {
				personDetails = new JSONObject();
				personDetails.put("deliveryPersonId", persons.get(i).getEmployeeId());
				personDetails.put("name", persons.get(i).getFirstName());

				personsArr.put(personDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return personsArr.toString();
	}

	@GET
	@Path("/v1/getMenuItem")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuItem(@QueryParam("hotelId") String hotelId, @QueryParam("menuId") String menuId) {
		AccessManager dao = new AccessManager(false);
		JSONObject itemDetails = new JSONObject();
		JSONArray addOnArr = new JSONArray();
		JSONObject addOnDetails = null;
		AddOn addOn = null;
		ArrayList<Integer> addOnIds = null;
		try {
			MenuItem orderItems = dao.getMenuById(hotelId, menuId);
			itemDetails.put("menuId", orderItems.getMenuId());
			itemDetails.put("category", orderItems.getCategory());
			itemDetails.put("title", orderItems.getTitle());
			itemDetails.put("description", orderItems.getDescription());
			itemDetails.put("flags", orderItems.getFlags());
			itemDetails.put("vegType", orderItems.getVegType());
			itemDetails.put("rate", orderItems.getRate());
			itemDetails.put("inhouseRate", orderItems.getInhouseRate());
			itemDetails.put("image", orderItems.getImage());
			itemDetails.put("preparationTime", orderItems.getPreparationTime());
			itemDetails.put("station", orderItems.getStation());
			itemDetails.put("costPrice", orderItems.getCostPrice());
			itemDetails.put("state", orderItems.getState());
			itemDetails.put("shortForm", orderItems.getShortForm());
			itemDetails.put("isTaxable", orderItems.getIsTaxable());
			itemDetails.put("incentiveType", orderItems.getHasIncentive());
			itemDetails.put("incentive", orderItems.getIncentive());
			if (!orderItems.getAddOnString().equals("")) {
				addOnIds = orderItems.getAddOnIds();
				addOnArr = new JSONArray();
				for (int addOnId : addOnIds) {
					addOnDetails = new JSONObject();
					addOn = dao.getAddOnById(addOnId, hotelId);
					addOnDetails.put("id", addOn.getId());
					addOnDetails.put("name", addOn.getName());
					addOnDetails.put("inHouseRate", addOn.getInHouseRate());
					addOnDetails.put("deliveryRate", addOn.getDeliveryRate());
					addOnArr.put(addOnDetails);
				}
			} else
				addOnArr = new JSONArray();
			itemDetails.put("addOns", addOnArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDetails.toString();
	}

	@GET
	@Path("/v1/getMenuItemBySearch")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuItemBySearch(@QueryParam("hotelId") String hotelId, @QueryParam("query") String query) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<MenuItem> orderItems = dao.getMenuItemBySearch(hotelId, query);
		try {
			for (int i = 0; i < orderItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("menuId", orderItems.get(i).getMenuId());
				itemDetails.put("category", orderItems.get(i).getCategory());
				itemDetails.put("title", orderItems.get(i).getTitle());
				itemDetails.put("description", orderItems.get(i).getDescription());
				itemDetails.put("flags", orderItems.get(i).getFlags());
				itemDetails.put("vegType", orderItems.get(i).getVegType());
				itemDetails.put("rate", orderItems.get(i).getRate());
				itemDetails.put("inHouseRate", orderItems.get(i).getInhouseRate());
				itemDetails.put("image", orderItems.get(i).getImage());
				itemDetails.put("costPrice", orderItems.get(i).getCostPrice());
				itemDetails.put("state", orderItems.get(i).getState());
				itemDetails.put("isTaxable", orderItems.get(i).getIsTaxable());

				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@POST
	@Path("/v2/newOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String newOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			JSONArray tablesArr = inObj.getJSONArray("tableIds");
			String[] tableIds = new String[tablesArr.length()];
			for (int i = 0; i < tableIds.length; i++) {
				tableIds[i] = tablesArr.getString(i);
			}
			outObj = dao.newOrder(inObj.getString("hotelId"), inObj.getString("userId"), tableIds,
					inObj.getInt("peopleCount"), inObj.getString("customer"), inObj.getString("contactNumber"),
					inObj.getString("allergyInfo"));
			dao.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/newQsrOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String newQsrOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newQsrOrder(inObj.getString("hotelId"), inObj.getString("userId"), inObj.getString("customer"),
					inObj.getString("contactNumber"), inObj.getString("allergyInfo"), inObj.getInt("orderType"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/editOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String editOrder3(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		String hotelId = "";
		String orderId = "";
		String menuId = "";
		boolean printKOT = false;
		try {
			dao.beginTransaction();
			outObj.put("status", -1);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			hotelId = inObj.getString("hotelId");
			orderId = inObj.getString("orderId");
			JSONArray newItems = inObj.getJSONArray("newItems");
			String subOrderId = dao.getNextSubOrderId(hotelId, orderId);
			for (int i = 0; i < newItems.length(); i++) {
				JSONObject subOrder = null;
				menuId = newItems.getJSONObject(i).getString("menuId");
				subOrder = dao.newSubOrder(hotelId, orderId, menuId, newItems.getJSONObject(i).getInt("qty"), "",
						subOrderId, inObj.has("userId") ? inObj.getString("userId") : "",
						newItems.getJSONObject(i).has("rate") ? newItems.getJSONObject(i).getInt("rate") : 0);
				if (subOrder == null || subOrder.getInt("status") == -1) {
					dao.rollbackTransaction();
					if (subOrder != null) {
						outObj.put("message", subOrder.get("message"));
					}
					printKOT = false;
					return outObj.toString();
				} else
					printKOT = true;

				JSONArray addOns = newItems.getJSONObject(i).getJSONArray("addOnArr");
				for (int j = 0; j < addOns.length(); j++) {
					Boolean addedAddOn = dao.addOrderAddon(hotelId, orderId, menuId,
							addOns.getJSONObject(j).getInt("qty"), addOns.getJSONObject(j).getInt("id"), subOrderId,
							addOns.getJSONObject(j).getInt("itemId"));

					if (!addedAddOn) {
						dao.rollbackTransaction();
						outObj.put("status", -1);
						outObj.put("message", "Failed to add Addon");
						return outObj.toString();
					}
				}
				String commonSpecs = "";
				Boolean addedSpec = false;
				JSONArray specifications = newItems.getJSONObject(i).getJSONArray("specArr");
				for (int j = 0; j < specifications.length(); j++) {
					if (specifications.getJSONObject(j).getInt("itemId") == 101) {
						commonSpecs += specifications.getJSONObject(j).getString("spec") + ", ";
					} else {
						addedSpec = dao.addOrderSpecification(hotelId, orderId, subOrderId, menuId,
								specifications.getJSONObject(j).getInt("itemId"),
								specifications.getJSONObject(j).getString("spec"));
						if (!addedSpec) {
							dao.rollbackTransaction();
							outObj.put("status", -1);
							outObj.put("message", "Failed to add Specification");
							return outObj.toString();
						}
					}
				}
				if (commonSpecs.length() > 0)
					dao.updateSpecifications(hotelId, orderId, subOrderId, menuId, commonSpecs);
			}
			dao.updateBillNoInOrders(hotelId, orderId);
			JSONArray changeStateItems = inObj.getJSONArray("changeStateItems");
			for (int i = 0; i < changeStateItems.length(); i++) {
				if (!dao.changeOrderStatus(hotelId, orderId, changeStateItems.getJSONObject(i).getString("subOrderId"),
						changeStateItems.getJSONObject(i).getString("menuId"))) {
					dao.rollbackTransaction();
					outObj.put("message", "Failed to close sub order");
					return outObj.toString();
				}
			}
			JSONArray changeQtyItems = inObj.getJSONArray("changeQtyItems");
			for (int i = 0; i < changeQtyItems.length(); i++) {
				if (!dao.editSubOrder(hotelId, orderId, changeQtyItems.getJSONObject(i).getString("subOrderId"),
						changeQtyItems.getJSONObject(i).getString("menuId"),
						changeQtyItems.getJSONObject(i).getInt("qty"))) {
					dao.rollbackTransaction();
					outObj.put("message", "Failed to edit sub order");
					return outObj.toString();
				} else
					dao.updateFoodBill(hotelId, orderId, changeQtyItems.getJSONObject(i).getString("menuId"), 1, true);
			}
			JSONObject customerDetails = inObj.getJSONObject("editCustomerDetails");
			if (customerDetails.toString().trim().length() > 2)
				dao.editCustomerDetails(hotelId, orderId, customerDetails.getString("customer"),
						customerDetails.getString("contactNumber"), customerDetails.getString("address"),
						customerDetails.getInt("peopleCount"), customerDetails.getString("allergyInfo"));

			outObj.put("status", 0);
			outObj.put("message", "Edited order successfully!");
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		if (printKOT)
			this.printKOT(hotelId, orderId);
		return outObj.toString();
	}

	@POST
	@Path("/v1/editQsrOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String editQsrOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		String hotelId = "";
		String orderId = "";
		Hotel hotel = null;
		try {
			dao.beginTransaction();
			outObj.put("status", -1);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			hotelId = inObj.getString("hotelId");
			orderId = inObj.getString("orderId");
			JSONArray newItems = inObj.getJSONArray("newItems");
			String subOrderId = dao.getNextSubOrderId(inObj.getString("hotelId"), inObj.getString("orderId"));
			for (int i = 0; i < newItems.length(); i++) {
				JSONObject subOrder = null;
				subOrder = dao.newSubOrder(inObj.getString("hotelId"), inObj.getString("orderId"),
						newItems.getJSONObject(i).getString("menuId"), newItems.getJSONObject(i).getInt("qty"),
						newItems.getJSONObject(i).getString("spec"), subOrderId,
						inObj.has("userId") ? inObj.getString("userId") : "", 0);
				if (subOrder == null || subOrder.getInt("status") == -1) {
					dao.rollbackTransaction();
					if (subOrder != null) {
						outObj.put("message", subOrder.get("message"));
					}
					return outObj.toString();
				}
				JSONArray addOns = newItems.getJSONObject(i).getJSONArray("addOnArr");
				for (int j = 0; j < addOns.length(); j++) {
					Boolean addedAddOn = dao.addOrderAddon(inObj.getString("hotelId"), inObj.getString("orderId"),
							newItems.getJSONObject(i).getString("menuId"), addOns.getJSONObject(j).getInt("qty"),
							addOns.getJSONObject(j).getInt("id"), subOrderId, addOns.getJSONObject(j).getInt("itemId"));

					if (!addedAddOn) {
						dao.rollbackTransaction();
						outObj.put("status", -1);
						outObj.put("message", "Failed to add Addon");
						return outObj.toString();
					}
				}
				JSONArray specifications = newItems.getJSONObject(i).getJSONArray("specArr");
				for (int j = 0; j < specifications.length(); j++) {
					Boolean addedSpec = dao.addOrderSpecification(inObj.getString("hotelId"),
							inObj.getString("orderId"), subOrderId, newItems.getJSONObject(i).getString("menuId"),
							specifications.getJSONObject(j).getInt("itemId"),
							specifications.getJSONObject(j).getString("spec"));

					if (!addedSpec) {
						dao.rollbackTransaction();
						outObj.put("status", false);
						outObj.put("message", "Failed to add Specifications");
						return outObj.toString();
					}
				}
			}
			dao.addPayment(inObj.getString("hotelId"), inObj.getString("orderId"),
					dao.getServiceDate(inObj.getString("hotelId")), inObj.getDouble("foodBill"),
					inObj.getDouble("barBill"), inObj.getDouble("discount"), inObj.getDouble("total"),
					inObj.getDouble("sc"), inObj.getDouble("gst"), inObj.getDouble("tip"),
					inObj.getDouble("cashPayment"), inObj.getDouble("cardPayment"), inObj.getString("discountName"),
					inObj.getString("cardType"), inObj.getDouble("complimentary"));
			outObj.put("status", 0);
			outObj.put("message", "Edited order successfully!");
			hotel = dao.getHotelById(hotelId);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		if (hotel.getKDSType().equals("NONKDS"))
			this.printKOT(hotelId, orderId);
		return outObj.toString();
	}

	@POST
	@Path("/v1/editStatus")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String editStatus(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		dao.beginTransaction();
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			if (!dao.changeOrderStatus(inObj.getString("hotelId"), inObj.getString("orderId"),
					inObj.optString("subOrderId"), inObj.optString("menuId"))) {
				dao.rollbackTransaction();
				outObj.put("message", "Failed to change status");
				return outObj.toString();
			}
			outObj.put("status", 0);
			outObj.put("message", "Edited order successfully!");
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/changeOrderStatus")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String changeOrderStatus(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		dao.beginTransaction();
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			if (!dao.changeOrderStatus(inObj.getString("hotelId"), inObj.getString("orderId"))) {
				dao.rollbackTransaction();
				outObj.put("message", "Failed to change status");
				return outObj.toString();
			}
			outObj.put("status", 0);
			outObj.put("message", "Edited order successfully!");
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/checkoutOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String checkoutOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			if (!dao.checkOutOrder(inObj.getString("hotelId"), inObj.getString("orderId"))) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to checkout order");
			} else {
				outObj.put("status", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/assignDeliveryAndCheckoutOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String assignAndCheckoutOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			if (!dao.checkOutOrder(inObj.getString("hotelId"), inObj.getString("orderId"),
					inObj.getString("deliveryPersonId"))) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to checkout order");
			} else {
				outObj.put("status", 0);
				outObj.put("message", "checkout order successful");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/submitRatings")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String submitRatings(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			if (!dao.submitRatings(inObj.getString("hotelId"), inObj.getString("orderId"),
					inObj.getString("customerName"), inObj.getString("customerNumber"),
					inObj.getString("customerBirthdate"), inObj.getString("customerAnniversary"),
					inObj.getString("reviewSuggestions"), inObj.getJSONObject("ratings"),
					inObj.has("wantsPromotion") ? inObj.getBoolean("wantsPromotion") : Boolean.FALSE)) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to submit ratings");
			} else {
				outObj.put("status", 0);
				outObj.put("message", "Ratings submitted successful");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getActiveHomeDeliveries")
	@Produces(MediaType.APPLICATION_JSON)
	public String getActiveHomeDeliveries(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject outObj = null;
		ArrayList<AccessManager.HomeDelivery> homeDeliveries = dao.getActiveHomeDeliveries(hotelId, userId);
		try {
			for (int i = 0; i < homeDeliveries.size(); i++) {
				outObj = new JSONObject();
				outObj.put("customer", homeDeliveries.get(i).getCustomer());
				outObj.put("phone", homeDeliveries.get(i).getMobileNo());
				outObj.put("address", homeDeliveries.get(i).getAddress());
				outObj.put("billNo", homeDeliveries.get(i).getBillNo());
				outObj.put("orderId", homeDeliveries.get(i).getOrderId());
				outObj.put("state", dao.getHomeDeliveryOrderState(hotelId, homeDeliveries.get(i).getOrderId()));
				outObj.put("total", dao.getOrderTotal(hotelId, homeDeliveries.get(i).getOrderId()));
				outArr.put(outObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outArr.toString();
	}

	@GET
	@Path("/v1/getActiveTakeAway")
	@Produces(MediaType.APPLICATION_JSON)
	public String getActiveTakeAway(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject outObj = null;
		ArrayList<AccessManager.HomeDelivery> takeAways = dao.getActiveTakeAway(hotelId, userId);
		try {
			for (int i = 0; i < takeAways.size(); i++) {
				outObj = new JSONObject();
				outObj.put("customer", takeAways.get(i).getCustomer());
				outObj.put("phone", takeAways.get(i).getMobileNo());
				outObj.put("orderId", takeAways.get(i).getOrderId());
				outObj.put("billNo", takeAways.get(i).getBillNo());
				outObj.put("state", dao.getHomeDeliveryOrderState(hotelId, takeAways.get(i).getOrderId()));
				outObj.put("total", dao.getOrderTotal(hotelId, takeAways.get(i).getOrderId()));
				outArr.put(outObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outArr.toString();
	}

	@GET
	@Path("/v1/getActiveBarOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public String getActiveBarOrders(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject outObj = null;
		ArrayList<AccessManager.HomeDelivery> barOrders = dao.getActiveBarOrders(hotelId, userId);
		try {
			for (int i = 0; i < barOrders.size(); i++) {
				outObj = new JSONObject();
				outObj.put("customer", barOrders.get(i).getCustomer());
				outObj.put("phone", barOrders.get(i).getMobileNo());
				outObj.put("address", barOrders.get(i).getAddress());
				outObj.put("orderId", barOrders.get(i).getOrderId());
				outObj.put("billNo", barOrders.get(i).getBillNo());
				outObj.put("state", dao.getHomeDeliveryOrderState(hotelId, barOrders.get(i).getOrderId()));
				outObj.put("total", dao.getOrderTotal(hotelId, barOrders.get(i).getOrderId()));
				outArr.put(outObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outArr.toString();
	}

	@GET
	@Path("/v1/getCustomerDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomerDetails(@QueryParam("hotelId") String hotelId, @QueryParam("mobileNo") String mobileNo) {
		AccessManager dao = new AccessManager(false);
		JSONObject out = new JSONObject();
		AccessManager.Customer customer = dao.getCustomerDetails(hotelId, mobileNo);
		try {
			out.put("customer", "");
			out.put("address", "");
			if (customer != null) {
				out.put("customer", customer.getCustomer());
				out.put("address", customer.getAddress());
				out.put("mobileNo", customer.getMobileNo());
				out.put("birthdate", customer.getBirthdate());
				out.put("anniversary", customer.getAnniversary());
				out.put("isPriviledged", customer.getUserType());
				out.put("remarks", customer.getRemarks());
				out.put("allergyInfo", customer.getAllergyInfo());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	@POST
	@Path("/v1/getCustomersForSMS")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getCustomersForSMS(String jsonObject) {
		AccessManager dao = new AccessManager(false);
		JSONObject out = new JSONObject();
		JSONObject inObj = null;
		SendSMS sms = new SendSMS();
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		try {
			inObj = new JSONObject(jsonObject);
			ArrayList<Customer> customers = dao.getCustomersForSMS(inObj.getString("hotelId"));
			out.put("status", false);
			if (customers == null) {
				return out.toString();
			}
			Hotel hotel = dao.getHotelById(inObj.getString("hotelId"));
			Date curtime = sdf.parse(now.format(dateFormat));
			String message = "";
			for (Customer customer : customers) {
				Date d1 = sdf.parse(customer.getCompleteTimestamp());
				long elapsed = curtime.getTime() - d1.getTime();
				int minutes = (int) (elapsed / 1000) / 60;
				if (minutes > 15 && customer.getMobileNo().length() == 10) {
					message = "Dear " + customer.getCustomer() + "! Greetings from " + hotel.getHotelName()
							+ ". Thank you for dining with us. It was a pleasure to have you over. Have a great day!";
					if (dao.updateOrderSMSStatusDone(inObj.getString("hotelId"), customer.getOrderId()))
						sms.sendSms(message, customer.getMobileNo());
				} else if (customer.getMobileNo().length() != 10) {
					dao.updateOrderSMSStatusDone(inObj.getString("hotelId"), customer.getOrderId());
				}
			}
			out.put("status", true);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	@GET
	@Path("/v1/getAllCustomerDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCustomerDetails(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONArray cusArray = new JSONArray();
		ArrayList<Customer> customers = dao.getAllCustomerDetails(hotelId);
		String mobileNo = "";
		try {
			if (customers != null) {
				for (int i = 0; i < customers.size(); i++) {
					JSONObject out = new JSONObject();
					mobileNo = customers.get(i).getMobileNo();
					out.put("customerName", customers.get(i).getCustomer());
					out.put("address", customers.get(i).getAddress());
					out.put("birthdate", customers.get(i).getBirthdate());
					out.put("anniversary", customers.get(i).getAnniversary());
					out.put("mobileNo", mobileNo);
					out.put("isPriviledged", customers.get(i).getUserType());
					out.put("remarks", customers.get(i).getRemarks());
					out.put("ratingFood", dao.getAverageFood(hotelId, mobileNo));
					out.put("ratingAmbiance", dao.getAverageAmbiance(hotelId, mobileNo));
					out.put("ratingService", dao.getAverageService(hotelId, mobileNo));
					out.put("ratingHygiene", dao.getAverageHygiene(hotelId, mobileNo));
					cusArray.put(out);
				}
			}
			outObj.put("customers", cusArray);
			outObj.put("avgRatingFood", dao.getOverallAvgFood(hotelId));
			outObj.put("avgRatingAmbiance", dao.getOverallAvgAmbiance(hotelId));
			outObj.put("avgRatingService", dao.getOverallAvgService(hotelId));
			outObj.put("avgRatingHygiene", dao.getOverallAvgHygiene(hotelId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON) // JASON
	public String updateCustomer(String jsonObject) {
		boolean result = false;
		try {
			JSONObject inObj = new JSONObject(jsonObject);
			AccessManager dao = new AccessManager(false);
			result = dao.updateCustomer(inObj.getString("name"), inObj.getString("address"),
					inObj.getString("mobnumber"), inObj.getString("birthday"), inObj.getString("anni"),
					inObj.getString("remarks"), inObj.getString("allergyInfo"), inObj.getString("hotelId"));
		} catch (JSONException je) {
		}
		return result ? "true" : "false";
	}

	@POST
	@Path("/v1/modifyCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON) // JASON
	public String modifyCustomer(String jsonObject) {
		JSONObject outObj = new JSONObject();
		try {
			JSONObject inObj = new JSONObject(jsonObject);
			AccessManager dao = new AccessManager(false);
			outObj.put("status",
					dao.modifyCustomer(inObj.getString("hotelId"), inObj.getString("name"), inObj.getString("phone"),
							inObj.getString("birthdate"), inObj.getString("anniversary"),
							inObj.getString("allergyInfo"), "", inObj.getBoolean("wantsPromotion")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/newHomeDeliveryOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String newHomeDeliveryOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newHomeDeliveryOrder(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("customer"), inObj.getString("mobile"), inObj.getString("address"),
					inObj.getString("allergyInfo"));
			dao.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/newTakeAwayOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String newTakeAwayOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newTakeAwayOrder(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("customer"), inObj.getString("mobile"), inObj.getString("allergyInfo"));
			dao.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/newBarOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String newBarOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newBarOrder(inObj.getString("hotelId"), inObj.getString("userId"), inObj.getString("customer"),
					inObj.getString("mobile"), inObj.getString("address"), inObj.getString("allergyInfo"));
			dao.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/cancelOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String cancelOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			dao.beginTransaction();
			if (!dao.cancelOrder(inObj.getString("hotelId"), inObj.getString("orderId"))) {
				dao.rollbackTransaction();
				outObj.put("message", "Failed to cancel the order");
				return outObj.toString();
			}
			dao.commitTransaction();
			outObj.put("status", true);
			outObj.put("message", "Success");
			return outObj.toString();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	private int getTargetOrders(JSONArray weeklyOrders) {
		int max = Configurator.getMinOrders();
		try {
			for (int i = 0; i < weeklyOrders.length(); i++) {
				max = Math.max(max, weeklyOrders.getJSONObject(i).getInt("count"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return max;
	}

	private JSONObject getDayStats(String hotelId, String userId, Date dt) {
		AccessManager dao = new AccessManager(false);
		JSONObject dayStats = new JSONObject();
		try {
			dayStats.put("count", dao.getOrderCount(hotelId, userId, dt));
			dayStats.put("points_ambiance", dao.getAmbiancePoints(hotelId, userId, dt));
			dayStats.put("points_qof", dao.getQoFPoints(hotelId, userId, dt));
			dayStats.put("points_service", dao.getServicePoints(hotelId, userId, dt));
			dayStats.put("points_hygiene", dao.getHygienePoints(hotelId, userId, dt));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dayStats;
	}

	private JSONObject getRating(JSONArray weeklyOrders) {
		JSONObject outObj = new JSONObject();
		int ambiance = 0, qof = 0, service = 0, hygiene = 0, count = 0;

		try {
			for (int i = 0; i < weeklyOrders.length(); i++) {
				count += weeklyOrders.getJSONObject(i).getInt("count");
				ambiance += weeklyOrders.getJSONObject(i).getInt("points_ambiance");
				qof += weeklyOrders.getJSONObject(i).getInt("points_qof");
				service += weeklyOrders.getJSONObject(i).getInt("points_service");
				hygiene += weeklyOrders.getJSONObject(i).getInt("points_hygiene");
			}
			outObj.put("ambianceRating", (count != 0) ? Math.round(ambiance / count) : 0);
			outObj.put("qualityOfFoodRating", (count != 0) ? Math.round(qof / count) : 0);
			outObj.put("serviceRating", (count != 0) ? Math.round(service / count) : 0);
			outObj.put("hygiene", (count != 0) ? Math.round(hygiene / count) : 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj;
	}

	@Path("/version")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnVersion() {
		return "<p>Version: " + api_version + "</p>";
	}

	@POST
	@Path("/v1/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public String uploadFile(@FormDataParam("hotelId") String hotelId, @FormDataParam("directory") String directory,
			@FormDataParam("image_file") InputStream uploadedInputStream,
			@FormDataParam("image_file") FormDataContentDisposition fileDetail, @FormDataParam("itemId") String itemId,
			@FormDataParam("category") String category) throws Exception {
		OutputStream os = null;
		String originalName = fileDetail.getFileName();
		try {
			String[] ext = originalName.split("\\.");

			String fileName = itemId + "." + ext[ext.length - 1];

			File fileToUpload = new File(
					Configurator.getImagesLocation() + "/hotels/" + hotelId + "/" + directory + "/" + fileName);

			os = new FileOutputStream(fileToUpload);
			byte[] b = new byte[2048];
			int length;
			while ((length = uploadedInputStream.read(b)) != -1) {
				os.write(b, 0, length);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return originalName;
	}

	public boolean deleteFile(String hotelId, String originalName, String folder) {
		File fileToDelete = new File(Configurator.getImagesLocation() + "/hotels/" + hotelId + folder + originalName);
		return fileToDelete.delete();
	}

	@GET
	@Path("/v1/getStations")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStations(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<KitchenStation> stations = dao.getKitchenStations(hotelId);
		try {
			for (int i = 0; i < stations.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("station", stations.get(i).getStation());
				itemsArr.put(itemDetails);
			}
			orderObj.put("st", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderObj.toString();
	}

	@POST
	@Path("/v1/deleteItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deteleItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteItem(inObj.getString("hotelId"), inObj.getString("menuId")));
			deleteFile(inObj.getString("hotelId"), inObj.getString("menuId") + ".jpg", "MenuItems");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/deleteCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteCollection(inObj.getString("hotelId"), inObj.getString("collectionName")));
			deleteFile(inObj.getString("hotelId"), inObj.getString("collectionName") + ".jpg", "Collections");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/deleteUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deteleUser(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteUser(inObj.getString("hotelId"), inObj.getString("userId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/isExistingDiscount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String isExistingDiscount(@QueryParam("hotelId") String hotelId, @QueryParam("name") String name) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			if (dao.discountExists(hotelId, name)) {
				outObj.put("status", true);
				outObj.put("usageLimit", dao.getDiscountUsageLimit(hotelId, name));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/hasCheckedOut")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String hasCheckedOut(@QueryParam("hotelId") String hotelId, @QueryParam("serviceDate") String serviceDate) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			if (dao.hasCheckedOut(hotelId)) {
				outObj.put("status", false);
				outObj.put("message", "Kindly authorize/check out all your employees");
			} else if (dao.hasCheckedOutOrders(hotelId, serviceDate)) {
				outObj.put("status", false);
				outObj.put("message", "Kindly check out all your orders");
			} else {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/hasCheckedIn")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String hasCheckedIn(@QueryParam("hotelId") String hotelId, @QueryParam("employeeId") String employeeId) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			outObj.put("remark", "");
			if (dao.hasCheckedIn(hotelId, employeeId)) {
				if (dao.isPresent(hotelId, employeeId))
					outObj.put("remark", "Please Checkout from Shift 1 before checking In.");
				else
					outObj.put("status", true);
			} else
				outObj.put("remark", "Please Check In in shift 1.");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addDiscount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addDiscount(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			if (dao.discountExists(inObj.getString("hotelId"), inObj.getString("name"))) {
				outObj.put("status", false);
				outObj.put("error", "Item Exists");
			} else {
				outObj.put("status",
						dao.addDiscount(inObj.getString("hotelId"), inObj.getString("name"),
								inObj.getString("description"), inObj.getInt("type"), inObj.getInt("value"),
								inObj.getString("startDate"), inObj.getString("expiryDate"),
								inObj.getString("usageLimit"), inObj.getJSONArray("validCollections")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/editDiscount")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String editDiscount(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
					dao.editDiscount(inObj.getString("hotelId"), inObj.getString("name"),
							inObj.getString("description"), inObj.getInt("type"), inObj.getInt("value"),
							inObj.getString("startDate"), inObj.getString("expiryDate"), inObj.getString("usageLimit"),
							inObj.getJSONArray("validCollections")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAllDiscounts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllDiscounts(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray discountArr = new JSONArray();
		JSONArray collectionArr = null;
		JSONObject collObj = null;
		JSONObject outObj = new JSONObject();
		JSONObject discountDetails = null;
		String[] collections = {};
		ArrayList<Discount> discountItems = dao.getAllDiscounts(hotelId);
		try {
			for (int i = 0; i < discountItems.size(); i++) {
				discountDetails = new JSONObject();
				collectionArr = new JSONArray();
				discountDetails.put("name", discountItems.get(i).getName());
				discountDetails.put("description", discountItems.get(i).getDescription());
				discountDetails.put("type", discountItems.get(i).getType());
				discountDetails.put("value", discountItems.get(i).getValue());
				discountDetails.put("startDate", discountItems.get(i).getStartDate());
				discountDetails.put("expiryDate", discountItems.get(i).getExpiryDate());
				discountDetails.put("usageLimit", discountItems.get(i).getUsageLimit());
				collections = discountItems.get(i).getValidCollections();
				for (int j = 0; j < collections.length; j++) {
					collObj = new JSONObject();
					collObj.put("collection", collections[j]);
					collectionArr.put(collObj);
				}
				discountDetails.put("validCollections", collectionArr);
				discountArr.put(discountDetails);
			}
			outObj.put("discounts", discountArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDiscount")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDiscount(@QueryParam("hotelId") String hotelId, @QueryParam("name") String name) {
		AccessManager dao = new AccessManager(false);
		JSONObject discountDetails = new JSONObject();
		JSONArray collectionArr = new JSONArray();
		JSONObject collObj = null;
		String[] collections = {};
		try {
			Discount discount = dao.getDiscountByName(hotelId, name);
			discountDetails = new JSONObject();
			if (discount == null)
				discountDetails.put("status", false);
			else {
				discountDetails.put("status", true);
				discountDetails.put("name", discount.getName());
				discountDetails.put("description", discount.getDescription());
				discountDetails.put("type", discount.getType());
				discountDetails.put("value", discount.getValue());
				discountDetails.put("startDate", discount.getStartDate());
				discountDetails.put("expiryDate", discount.getExpiryDate());
				discountDetails.put("usageLimit", discount.getUsageLimit());
				collections = discount.getValidCollections();
				for (int j = 0; j < collections.length; j++) {
					if (collections[j].equals(""))
						continue;
					collObj = new JSONObject();
					collObj.put("collection", collections[j]);
					collectionArr.put(collObj);

				}
				discountDetails.put("collectionsArr", collectionArr);
				discountDetails.put("hasCollections", discount.getHasCollections());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return discountDetails.toString();
	}

	@POST
	@Path("/v1/deleteDiscount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteDiscount(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteDiscount(inObj.getString("hotelId"), inObj.getString("name")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	private String getPreviousDateString(int day) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, day * (-1));
		return dateFormat.format(cal.getTime());
	}

	@GET
	@Path("/v1/getStatistics")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatistics(@QueryParam("hotelId") String hotelId, @QueryParam("duration") String duration,
			@QueryParam("serviceDate") String serviceDate) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			MonthReport report = dao.getTotalOrdersForCurMonth(hotelId, duration);
			outObj.put("totalOrders", report.getTotalOrders());
			report = dao.getBestWaiter(hotelId, duration);
			if (report == null) {
				outObj.put("waiterName", "None Yet");
				outObj.put("waitersOrders", "0");
			} else {
				outObj.put("waiterName", dao.getEmployeeById(hotelId, report.getBestWaiter()).getFullName());
				outObj.put("waitersOrders", report.getTotalOrderByWaiter());
				outObj.put("waitersImage", "/hotels/" + hotelId + "/Employees/" + report.getBestWaiter() + ".jpg");
				outObj.put("waiterHireDate", dao.getEmployeeById(hotelId, report.getBestWaiter()).getHiringDate());
			}

			report = dao.getMaxOrderedItem(hotelId, duration);
			if (report == null) {
				outObj.put("title", "None Yet");
				outObj.put("orderCount", "0");
			} else {
				outObj.put("title", report.getBestItem());
				outObj.put("menuId", report.getItemId());
				outObj.put("orderCount", report.getItemOrderCount());
			}

			outObj.put("cashBalance", dao.getCashBalance(hotelId));
			// String imageLocation =
			// Configurator.getImagesLocation()+"/hotels/"+hotelId+"/MenuItems/"+report.getItemId();
			// if(report.getItemImage() == "1")
			// {

			// }

			// outObj.put("itemImage", report.getItemImage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getSales")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSales(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		JSONObject tempObj = null;
		JSONArray revenueArr = new JSONArray();
		ArrayList<MonthReport> weeklyRevenue = dao.getWeeklyRevenue(hotelId);
		try {
			outObj.put("status", false);
			for (int i = 0; i < weeklyRevenue.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("daySale", weeklyRevenue.get(i).getTotalSales());
				revenueArr.put(tempObj);
			}
			outObj.put("revenue", revenueArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getYearlySales")
	@Produces(MediaType.APPLICATION_JSON)
	public String getYearlySales(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		JSONObject tempObj = null;
		JSONArray revenueArr = new JSONArray();
		ArrayList<YearlyReport> yearReport = dao.getYearlyOrders(hotelId);
		try {
			outObj.put("status", false);
			for (int i = 0; i < yearReport.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("orders", yearReport.get(i).getTotalOrders());
				tempObj.put("month", yearReport.get(i).getMonth());
				tempObj.put("monthName", yearReport.get(i).getMonthName());
				revenueArr.put(tempObj);
			}
			outObj.put("report", revenueArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@Path("/v1/getNextNotification")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getNextNotification(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			Notification notif = dao.getNextNotification(hotelId, userId);
			if (notif != null) {
				outObj.put("status", true);
				JSONArray tableArr = new JSONArray();
				ArrayList<Table> tables = dao.getJoinedTables(notif.getHotelId(), notif.getOrderId());
				for (int i = 0; i < tables.size(); i++) {
					tableArr.put(tables.get(i).getTableId());
				}
				outObj.put("tables", tableArr);
				outObj.put("msg", notif.getMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDesignation")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDesignation() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (Designation des : Designation.values()) {
				JSONObject obj = new JSONObject();
				if (des == Designation.UNAUTHORIZED)
					continue;
				obj.put("designation", des);
				outArr.put(obj);
			}
			outObj.put("designations", outArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCardType")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCardType() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (CardType des : CardType.values()) {
				JSONObject obj = new JSONObject();
				obj.put("cardType", des);
				outArr.put(obj);
			}
			outObj.put("cards", outArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDepartment")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDepartment() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (Department des : Department.values()) {
				JSONObject obj = new JSONObject();
				if (des == Department.UNKNOWN)
					continue;
				obj.put("department", des);
				outArr.put(obj);
			}
			outObj.put("departments", outArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getUserType")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserType() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (UserType des : UserType.values()) {
				JSONObject obj = new JSONObject();
				if (des == UserType.UNAUTHORIZED)
					continue;
				obj.put("userType", des);
				outArr.put(obj);
			}
			outObj.put("userTypes", outArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAllTaxes")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllTaxes(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			Hotel hotel = dao.getHotelById(hotelId);
			String[] taxes = hotel.getFlags().split(";");
			for (TaxTypes tax : TaxTypes.values()) {
				JSONObject obj = new JSONObject();
				if (tax == TaxTypes.INVALID)
					continue;
				obj.put("name", tax.getName());
				obj.put("percent", tax.getTaxPercent());
				obj.put("abbr", tax.toString());
				obj.put("value", tax.getValue());
				int checker = 0;
				for (String flag : taxes) {
					if (flag.equals(tax.toString()))
						checker = 1;
				}
				obj.put("exists", checker);
				outArr.put(obj);
			}
			outObj.put("taxes", outArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/setTaxForHotel")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String setTaxForHotel(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj.put("status", dao.updateHotelFlags(inObj.getString("hotelId"), inObj.getString("flags")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addPayment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addPayment(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			Hotel hotel = dao.getHotelById(inObj.getString("hotelId"));

			boolean status = dao.addPayment(hotelId, orderId, inObj.getString("orderDate"), inObj.getDouble("foodBill"),
					inObj.getDouble("barBill"), inObj.getDouble("discount"), inObj.getDouble("total"),
					inObj.getDouble("sc"), inObj.getDouble("gst"), inObj.getDouble("tip"),
					inObj.getDouble("cashPayment"), inObj.getDouble("cardPayment"), inObj.getString("discountName"),
					inObj.getString("cardType"), inObj.getDouble("complimentary"));

			if (status) {
				String mobileNo = dao.getMobileNoFromOrderId(hotelId, orderId).getEntity();
				if (dao.getHotelById(hotelId).getHasLoyalty() == 1 && !mobileNo.equals("")) {
					status = dao.addLoyaltyPoints(hotelId, inObj.getDouble("total"), mobileNo);
					if (!status) {
						outObj.put("message", "Loyalty Points could not be added. Please contact OrderOn support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}
				}

				if (hotel.getHotelType().equals("PREPAID") && hotel.getKDSType().equals("KDS")) {
					status = dao.changeOrderStatusToService(hotelId, orderId);
					if (!status) {
						outObj.put("message",
								"Order Status could not be updated. Please contact OrderOn support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}
				} else {
					status = dao.markPaymentComplete(hotelId, orderId);
					if (!status) {
						outObj.put("message", "Failed to mark order complete. Please try again or contact support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}
				}
				if (inObj.getInt("cashPayment") > 0) {
					status = dao.updateCashBalance(hotelId, dao.getCashBalance(hotelId) + inObj.getInt("cashPayment"));
					if (!status) {
						outObj.put("message", "Cash balance could not be updated. Please contact OrderOn support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}
				}
				if (dao.hasCashDrawer(hotelId))
					cashdrawerOpen(hotelId, dao);

				dao.updateOrderSMSStatus(hotelId, orderId);
			} else {
				outObj.put("message", "Payment could not be added. Please contact OrderOn support.");
				dao.rollbackTransaction();
				return outObj.toString();
			}

			if (!inObj.getString("discountName").equals("")) {
				Discount discount = dao.getDiscountByName(hotelId, inObj.getString("discountName"));
				if (!discount.getUsageLimit().equals("Unlimited")) {
					final int usageLimit = Integer.parseInt(discount.getUsageLimit()) - 1;
					if(!dao.updateUsageLimit(hotelId, discount.getName(), usageLimit)){
						outObj.put("message", "Discount usage limit could not be updated. Please contact OrderOn support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}
				}
			}

			outObj.put("status", true);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updatePaymentForReturn")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updatePaymentForReturn(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");

			boolean status = dao.updatePaymentForReturn(hotelId, inObj.getString("orderId"),
					inObj.getDouble("foodBill"), inObj.getDouble("barBill"), inObj.getDouble("discount"),
					inObj.getDouble("total"), inObj.getDouble("serviceCharge"), inObj.getDouble("gst"),
					inObj.getDouble("VATBAR"), inObj.getDouble("cashPayment"), inObj.getDouble("cardPayment"));

			if (status) {
				if (inObj.getInt("cashPayment") > 0) {
					dao.updateCashBalance(hotelId, dao.getCashBalance(hotelId) - inObj.getInt("cashPayment"));
				}
			}

			outObj.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/editPayment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String editPayment(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");

			boolean status = dao.editPayment(hotelId, inObj.getString("orderId"), inObj.getDouble("cashPayment"),
					inObj.getDouble("cardPayment"), inObj.getString("cardType"));

			Report payment = dao.getPayment(hotelId, inObj.getString("orderId"));
			double cash = payment.getCashPayment() - inObj.getInt("cashPayment");
			if (status) {
				if (cash > 0) {
					dao.updateCashBalance(hotelId, dao.getCashBalance(hotelId) - inObj.getInt("cashPayment"));
				}
			}

			outObj.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	/** Stock */

	private double convertUnit(double number, Unit unit, boolean toMultiply) {

		for (Unit unitType : Unit.values()) {
			if (unit != unitType) {
				continue;
			}
			if (toMultiply) {
				if (unitType.getConversion() == 0.001) {
					number = number / 1000;
				} else {
					number = number * unitType.getConversion();
				}
			} else {
				if (unit == unitType) {
					number = number / unitType.getConversion();
				}
			}
			break;
		}
		return number;
	}

	private String filterUnitToDisplay(String unit) {

		if (unit.equals("TABLESPOONGM"))
			return "TABLESPOON (GM)";
		else if (unit.equals("TABLESPOONML"))
			return "TABLESPOON (ML)";
		else if (unit.equals("TEASPOONGM"))
			return "TEASPOON (GM)";
		else if (unit.equals("TEASPOONML"))
			return "TEASPOON (ML)";
		else
			return unit;
	}

	private String filterUnitToStore(String unit) {

		if (unit.equals("TABLESPOON (GM)"))
			unit = "TABLESPOONGM";
		else if (unit.equals("TABLESPOON (ML)"))
			unit = "TABLESPOONML";
		else if (unit.equals("TEASPOON (GM)"))
			unit = "TEASPOONGM";
		else if (unit.equals("TEASPOON (ML)"))
			unit = "TEASPOONML";

		return unit;
	}

	@GET
	@Path("/v1/getStock")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStock(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<Stock> stockItems = dao.getStock(hotelId);
		double ratePerUnit = 0.0;
		Unit unit = Unit.GRAM;

		try {
			for (int i = 0; i < stockItems.size(); i++) {
				unit = Unit.valueOf(stockItems.get(i).getDisplayableUnit());
				ratePerUnit = stockItems.get(i).getRatePerUnit();
				unit.setValue(ratePerUnit);

				itemDetails = new JSONObject();
				itemDetails.put("name", stockItems.get(i).getName());
				itemDetails.put("unit", filterUnitToDisplay(unit.toString()));
				itemDetails.put("ratePerUnit", convertUnit(ratePerUnit, unit, false));
				itemDetails.put("wastage", stockItems.get(i).getWastage());
				itemDetails.put("sku", stockItems.get(i).getSku());
				itemDetails.put("quantity", convertUnit(stockItems.get(i).getQuantity(), unit, true));

				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getMaterials")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaterial(@QueryParam("hotelId") String hotelId, @QueryParam("type") int type) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<Stock> stockItems = dao.getMaterial(hotelId, type);
		Unit unit = Unit.GRAM;

		try {
			for (int i = 0; i < stockItems.size(); i++) {
				unit = Unit.valueOf(stockItems.get(i).getDisplayableUnit());
				itemDetails = new JSONObject();
				itemDetails.put("sku", stockItems.get(i).getSku());
				itemDetails.put("name", stockItems.get(i).getName());
				itemDetails.put("unit", unit.toString());
				itemDetails.put("doe", stockItems.get(i).getDOE());
				itemDetails.put("displayableUnit", filterUnitToDisplay(stockItems.get(i).getDisplayableUnit()));
				itemDetails.put("ratePerUnit", convertUnit(stockItems.get(i).getRatePerUnit(), unit, false));
				itemDetails.put("minQuantity", convertUnit(stockItems.get(i).getMinQuantity(), unit, true));
				itemDetails.put("quantity", convertUnit(stockItems.get(i).getQuantity(), unit, true));

				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getMaterialByName")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaterialByName(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<Stock> stockItems = dao.getMaterialByName(hotelId);
		Unit unit = Unit.GRAM;

		try {
			for (int i = 0; i < stockItems.size(); i++) {
				unit = Unit.valueOf(stockItems.get(i).getDisplayableUnit());
				itemDetails = new JSONObject();
				itemDetails.put("sku", stockItems.get(i).getSku());
				itemDetails.put("name", stockItems.get(i).getName());
				itemDetails.put("unit", unit.toString());
				itemDetails.put("doe", stockItems.get(i).getDOE());
				itemDetails.put("displayableUnit", filterUnitToDisplay(stockItems.get(i).getDisplayableUnit()));
				itemDetails.put("ratePerUnit", convertUnit(stockItems.get(i).getRatePerUnit(), unit, false));
				itemDetails.put("minQuantity", convertUnit(stockItems.get(i).getMinQuantity(), unit, true));
				itemDetails.put("quantity", convertUnit(stockItems.get(i).getQuantity(), unit, true));

				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getExpiringStock")
	@Produces(MediaType.APPLICATION_JSON)
	public String getExpiringStock(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<Stock> stockItems = dao.getExpiringStock(hotelId);
		Unit unit = Unit.GRAM;

		try {
			for (int i = 0; i < stockItems.size(); i++) {
				unit = Unit.valueOf(stockItems.get(i).getDisplayableUnit());
				itemDetails = new JSONObject();
				itemDetails.put("name", stockItems.get(i).getName());
				itemDetails.put("unit", filterUnitToDisplay(stockItems.get(i).getDisplayableUnit()));
				itemDetails.put("quantity", convertUnit(stockItems.get(i).getQuantity(), unit, true));
				itemDetails.put("doc", stockItems.get(i).getDOC());
				itemDetails.put("doe", stockItems.get(i).getDOE());
				itemDetails.put("sku", stockItems.get(i).getSku());

				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getStockRunningOut")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStockRunningOut(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<Stock> stockItems = dao.getStockRunningOut(hotelId);
		Unit unit = Unit.GRAM;

		try {
			for (int i = 0; i < stockItems.size(); i++) {
				unit = Unit.valueOf(stockItems.get(i).getDisplayableUnit());
				itemDetails = new JSONObject();
				itemDetails.put("sku", stockItems.get(i).getSku());
				itemDetails.put("name", stockItems.get(i).getName());
				itemDetails.put("unit", filterUnitToDisplay(stockItems.get(i).getDisplayableUnit()));
				itemDetails.put("quantity", convertUnit(stockItems.get(i).getQuantity(), unit, true));
				itemDetails.put("minQuantity", convertUnit(stockItems.get(i).getMinQuantity(), unit, true));

				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@POST
	@Path("/v1/addMaterial")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addMaterial(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		String doe = "";
		Date date;
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			Unit unit = Unit.valueOf(filterUnitToStore(inObj.getString("unit")));
			double ratePerUnit = inObj.getDouble("ratePerUnit");
			double minQuantity = inObj.getDouble("minQuantity");
			double quantity = inObj.getDouble("quantity");
			String hotelId = inObj.getString("hotelId");
			String name = inObj.getString("name");
			unit.setValue(ratePerUnit);
			for (Unit unitType : Unit.values()) {
				if (unit == unitType) {
					ratePerUnit = ratePerUnit * unitType.getConversion();
					minQuantity = minQuantity / unitType.getConversion();
					quantity = quantity / unitType.getConversion();
					break;
				}
			}

			doe = inObj.getString("doe");
			if (!doe.equals("")) {
				DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
				date = dateFormat.parse(doe);
				dateFormat = new SimpleDateFormat("yyyy/mm/dd");
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				doe = dateFormat.format(cal.getTime());
			}
			if (dao.materialExists(hotelId, name)) {
				outObj.put("status", false);
				outObj.put("error", "Item Exists");
			} else {
				outObj.put("status",
						dao.addMaterial(inObj.getString("hotelId"), inObj.getString("name"), ratePerUnit, minQuantity,
								quantity, doe, inObj.getInt("wastage"), Unit.valueOf(unit.getAssociation()).toString(),
								unit.toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getStockItemBySearch")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStockItemBySearch(@QueryParam("hotelId") String hotelId, @QueryParam("query") String query,
			@QueryParam("type") int type) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<Stock> stockItems = dao.getMaterialBySearch(hotelId, query, type);
		double ratePerUnit = 0.0;
		Unit unit = Unit.GRAM;

		try {
			for (int i = 0; i < stockItems.size(); i++) {
				unit = Unit.valueOf(stockItems.get(i).getDisplayableUnit());
				ratePerUnit = stockItems.get(i).getRatePerUnit();
				unit.setValue(ratePerUnit);

				itemDetails = new JSONObject();
				itemDetails.put("sku", stockItems.get(i).getSku());
				itemDetails.put("name", stockItems.get(i).getName());
				itemDetails.put("unit", filterUnitToStore(unit.toString()));
				itemDetails.put("ratePerUnit", convertUnit(ratePerUnit, unit, false));
				itemDetails.put("wastage", stockItems.get(i).getWastage());
				itemDetails.put("quantity", convertUnit(stockItems.get(i).getQuantity(), unit, true));

				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getMaterialBySearch")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMaterialBySearch(@QueryParam("hotelId") String hotelId, @QueryParam("query") String query,
			@QueryParam("type") int type) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		ArrayList<Stock> stockItems = dao.getMaterialBySearch(hotelId, query, type);
		Unit unit = Unit.GRAM;
		double ratePerUnit = 0.0;

		try {
			for (int i = 0; i < stockItems.size(); i++) {
				unit = Unit.valueOf(stockItems.get(i).getDisplayableUnit());
				ratePerUnit = stockItems.get(i).getRatePerUnit();
				unit.setValue(ratePerUnit);
				itemDetails = new JSONObject();
				itemDetails.put("sku", stockItems.get(i).getSku());
				itemDetails.put("name", stockItems.get(i).getName());
				itemDetails.put("unit", unit.toString());
				itemDetails.put("displayableUnit", filterUnitToDisplay(stockItems.get(i).getDisplayableUnit()));
				itemDetails.put("ratePerUnit", convertUnit(ratePerUnit, unit, false));
				itemDetails.put("minQuantity", convertUnit(stockItems.get(i).getMinQuantity(), unit, true));
				itemDetails.put("quantity", convertUnit(stockItems.get(i).getQuantity(), unit, true));

				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v1/getOneMaterial")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOneMaterial(@QueryParam("hotelId") String hotelId, @QueryParam("sku") String query) {
		AccessManager dao = new AccessManager(false);
		JSONObject itemDetails = null;
		Stock stockItems = dao.getOneMaterial(hotelId, query);
		Unit unit = Unit.GRAM;
		Date date;
		String doe = "";

		try {
			unit = Unit.valueOf(stockItems.getDisplayableUnit());

			doe = stockItems.getDOE();
			if (!doe.equals("")) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
				date = dateFormat.parse(doe);
				dateFormat = new SimpleDateFormat("dd/mm/yyyy");
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				doe = dateFormat.format(cal.getTime());
			}

			itemDetails = new JSONObject();
			itemDetails.put("sku", stockItems.getSku());
			itemDetails.put("name", stockItems.getName());
			itemDetails.put("unit", stockItems.getUnit());
			itemDetails.put("displayableUnit", filterUnitToDisplay(stockItems.getDisplayableUnit()));
			itemDetails.put("ratePerUnit", convertUnit(stockItems.getRatePerUnit(), unit, false));
			itemDetails.put("minQuantity", convertUnit(stockItems.getMinQuantity(), unit, true));
			itemDetails.put("quantity", convertUnit(stockItems.getQuantity(), unit, true));
			itemDetails.put("doe", doe);
			itemDetails.put("wastage", stockItems.getWastage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDetails.toString();
	}

	@POST
	@Path("/v1/updateStock")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateStock(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		double quantity = 0;
		double curQuantity = 0;
		Unit unit = Unit.PIECE;
		String doe = "";
		Double newQuantity = 0.0;

		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			unit = Unit.valueOf(filterUnitToStore(inObj.getString("unit")));
			quantity = inObj.getDouble("quantity");
			curQuantity = inObj.getDouble("currentQuantity");

			for (Unit unitType : Unit.values()) {
				if (unit == unitType) {
					quantity = quantity / unitType.getConversion();
					curQuantity = curQuantity / unitType.getConversion();
					break;
				}
			}
			if (inObj.getInt("type") == 0)
				newQuantity = quantity + curQuantity;
			else
				newQuantity = curQuantity - quantity;
			outObj.put("status", dao.updateStock(inObj.getString("hotelId"), inObj.getString("sku"), newQuantity,
					inObj.getDouble("quantity"), inObj.getDouble("ratePerUnit"), doe));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();

	}

	@POST
	@Path("/v1/updateMaterial")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMaterial(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		Unit unit = Unit.PIECE;
		String doe = "";
		Date date;

		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			unit = Unit.valueOf(filterUnitToStore(inObj.getString("displayableUnit")));

			doe = inObj.getString("doe");
			if (!doe.equals("")) {
				DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
				date = dateFormat.parse(doe);
				dateFormat = new SimpleDateFormat("yyyy/mm/dd");
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				doe = dateFormat.format(cal.getTime());
			}
			outObj.put("status",
					dao.updateMaterial(inObj.getString("hotelId"), inObj.getString("materialName"),
							convertUnit(inObj.getDouble("ratePerUnit"), unit, true),
							convertUnit(inObj.getDouble("minQuantity"), unit, false),
							convertUnit(inObj.getDouble("quantity"), unit, false), doe, inObj.getInt("wastage"),
							unit.toString(), inObj.getString("sku")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();

	}

	@POST
	@Path("/v1/deleteStockItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteStockItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteStockItem(inObj.getString("hotelId"), inObj.getString("sku")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getRecipe")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRecipe(@QueryParam("hotelId") String hotelId, @QueryParam("menuId") String menuId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Stock> recipeItems = dao.getRecipe(hotelId, menuId);
		Stock method = dao.getMethod(hotelId, menuId);
		Unit unit = Unit.GRAM;

		try {
			for (int i = 0; i < recipeItems.size(); i++) {
				unit = Unit.valueOf(recipeItems.get(i).getDisplayableUnit());
				itemDetails = new JSONObject();
				itemDetails.put("sku", recipeItems.get(i).getSku());
				itemDetails.put("name", recipeItems.get(i).getName());
				itemDetails.put("unit", unit.toString());
				itemDetails.put("displayableUnit", filterUnitToDisplay(recipeItems.get(i).getDisplayableUnit()));
				itemDetails.put("quantity", convertUnit(recipeItems.get(i).getQuantity(), unit, true));

				itemsArr.put(itemDetails);
			}
			outObj.put("items", itemsArr);
			outObj.put("method", method.getMethod());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addRecipe")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addRecipe(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			Unit unit = Unit.valueOf(filterUnitToStore(inObj.getString("unit")));
			String hotelId = inObj.getString("hotelId");

			if (dao.recipeItemExists(hotelId, inObj.getString("sku"), inObj.getString("menuId"))) {
				outObj.put("status",
						dao.updateRecipe(inObj.getString("hotelId"),
								convertUnit(inObj.getDouble("quantity"), unit, false), inObj.getString("menuId"),
								inObj.getString("sku"), inObj.getString("unit")));
			} else {
				outObj.put("status",
						dao.addRecipe(inObj.getString("hotelId"), convertUnit(inObj.getDouble("quantity"), unit, false),
								inObj.getString("menuId"), inObj.getString("sku"), inObj.getString("unit")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateMethod")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMethod(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			;

			outObj.put("status",
					dao.updateMethod(inObj.getString("hotelId"), inObj.getString("menuId"), inObj.getString("method")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/deleteRecipeItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteRecipeItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteRecipeItem(inObj.getString("hotelId"), inObj.getString("sku"),
					inObj.getString("menuId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/reduceQuantity")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String reduceQuantity(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);

		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj.put("status", dao.reduceQuantity(inObj.getString("hotelId"), inObj.getString("sku"),
					inObj.getDouble("curQuantity") - inObj.getDouble("quantity"), inObj.getDouble("quantity")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	// Reports
	@GET
	@Path("/v1/getExpenseReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getExpenseReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Expense> reportItems = dao.getExpenseReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("Date", reportItems.get(i).getDate());
				itemDetails.put("Amount", reportItems.get(i).getAmount());
				itemDetails.put("Type", reportItems.get(i).getType());
				itemDetails.put("Payee", reportItems.get(i).getPayee());
				itemDetails.put("Details", reportItems.get(i).getMemo());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
		} catch (JSONException e) {
		}
		return outObj.toString();
	}

	// Reports
	@GET
	@Path("/v1/getCustomerReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomerReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<CustomerReport> reportItems = dao.getCustomerReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("totalPax", reportItems.get(i).getTotalGuests());
				itemDetails.put("totalWalkins", reportItems.get(i).getTotalWalkins());
				itemDetails.put("customerName", reportItems.get(i).getCustomerName());
				itemDetails.put("mobileNumber", reportItems.get(i).getMobileNumber());
				itemDetails.put("spentPerPax", reportItems.get(i).getSpentPerPax());
				itemDetails.put("spentPerWalkin", reportItems.get(i).getSpentPerWalkin());
				itemDetails.put("totalSpent", reportItems.get(i).getTotalSpent());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
		} catch (JSONException e) {
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDiscountReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDiscountReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		ArrayList<AccessManager.DiscountReport> reportItems = dao.getDiscountReport(hotelId, startDate, endDate);
		try {
			for (AccessManager.DiscountReport report : reportItems) {
				JSONObject record = new JSONObject();
				record.put("customerName", report.getCustomerName());
				record.put("discountName", report.getDiscountName());
				record.put("orderDate", report.getOrderDate());
				record.put("total", report.getTotal());
				record.put("discount", report.getDiscount());
				record.put("discountedTotal", report.getDiscountedTotal());
				itemsArr.put(record);
			}
		} catch (JSONException e) {
		}
		return itemsArr.toString();
	}

	// LunchDinnerSalesReport-ap
	@GET
	@Path("/v1/getLunchDinnerSalesReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getLunchDinnerSalesReport(@QueryParam("hotelId") String hotelId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails1 = null;
		JSONObject itemDetails2 = null;
		JSONObject itemDetails3 = null;
		try {
			ArrayList<LunchDinnerSalesReport> reportItems0 = dao.getLunchDinnerSalesReport(hotelId, startDate, endDate,
					0);
			ArrayList<LunchDinnerSalesReport> reportItems1 = dao.getLunchDinnerSalesReport(hotelId, startDate, endDate,
					1);
			ArrayList<LunchDinnerSalesReport> reportItems2 = dao.getLunchDinnerSalesReport(hotelId, startDate, endDate,
					2);
			itemDetails1 = new JSONObject();
			itemDetails2 = new JSONObject();
			itemDetails3 = new JSONObject();
			{
				itemDetails1.put("foodBill", reportItems0.get(0).getFoodBill());
				itemDetails1.put("barBill", reportItems0.get(0).getBarBill());
				itemDetails1.put("pax", reportItems0.get(0).getPax());
				itemDetails1.put("foodPPax",
						Math.round(reportItems0.get(0).getFoodBill() / reportItems0.get(0).getPax()));
				itemDetails1.put("barPPax",
						Math.round(reportItems0.get(0).getBarBill() / reportItems0.get(0).getPax()));
				itemDetails1.put("cash", reportItems0.get(0).getCash());
				itemDetails1.put("card", reportItems0.get(0).getCard());
				itemDetails1.put("VISA", reportItems0.get(0).getVISA());
				itemDetails1.put("MASTERCARD", reportItems0.get(0).getMASTERCARD());
				itemDetails1.put("MAESTRO", reportItems0.get(0).getMAESTRO());
				itemDetails1.put("AMEX", reportItems0.get(0).getAMEX());
				itemDetails1.put("RUPAY", reportItems0.get(0).getRUPAY());
				itemDetails1.put("MSWIPE", reportItems0.get(0).getMSWIPE());
				itemDetails1.put("ZOMATO", reportItems0.get(0).getZOMATO());
				itemDetails1.put("PAYTM", reportItems0.get(0).getPAYTM());
				itemDetails1.put("SWIGGY", reportItems0.get(0).getSWIGGY());
				itemDetails1.put("MAGIC_PIN", reportItems0.get(0).getMAGIC_PIN());
				itemDetails1.put("OTHERS", reportItems0.get(0).getOTHERS());

				itemDetails2.put("foodBill", reportItems1.get(0).getFoodBill());
				itemDetails2.put("barBill", reportItems1.get(0).getBarBill());
				itemDetails2.put("pax", reportItems1.get(0).getPax());
				itemDetails2.put("foodPPax",
						Math.round(reportItems1.get(0).getFoodBill() / reportItems0.get(0).getPax()));
				itemDetails2.put("barPPax",
						Math.round(reportItems1.get(0).getBarBill() / reportItems0.get(0).getPax()));
				itemDetails2.put("cash", reportItems1.get(0).getCash());
				itemDetails2.put("card", reportItems1.get(0).getCard());
				itemDetails2.put("VISA", reportItems1.get(0).getVISA());
				itemDetails2.put("MASTERCARD", reportItems1.get(0).getMASTERCARD());
				itemDetails2.put("MAESTRO", reportItems1.get(0).getMAESTRO());
				itemDetails2.put("AMEX", reportItems1.get(0).getAMEX());
				itemDetails2.put("RUPAY", reportItems1.get(0).getRUPAY());
				itemDetails2.put("MSWIPE", reportItems1.get(0).getMSWIPE());
				itemDetails2.put("ZOMATO", reportItems1.get(0).getZOMATO());
				itemDetails2.put("PAYTM", reportItems1.get(0).getPAYTM());
				itemDetails2.put("SWIGGY", reportItems1.get(0).getSWIGGY());
				itemDetails2.put("MAGIC_PIN", reportItems1.get(0).getMAGIC_PIN());
				itemDetails2.put("OTHERS", reportItems1.get(0).getOTHERS());

				itemDetails3.put("foodBill", reportItems2.get(0).getFoodBill());
				itemDetails3.put("barBill", reportItems2.get(0).getBarBill());
				itemDetails3.put("pax", reportItems2.get(0).getPax());
				itemDetails3.put("foodPPax",
						Math.round(reportItems2.get(0).getFoodBill() / reportItems0.get(0).getPax()));
				itemDetails3.put("barPPax",
						Math.round(reportItems2.get(0).getBarBill() / reportItems0.get(0).getPax()));
				itemDetails3.put("cash", reportItems2.get(0).getCash());
				itemDetails3.put("card", reportItems2.get(0).getCard());
				itemDetails3.put("VISA", reportItems2.get(0).getVISA());
				itemDetails3.put("MASTERCARD", reportItems2.get(0).getMASTERCARD());
				itemDetails3.put("MAESTRO", reportItems2.get(0).getMAESTRO());
				itemDetails3.put("AMEX", reportItems2.get(0).getAMEX());
				itemDetails3.put("RUPAY", reportItems2.get(0).getRUPAY());
				itemDetails3.put("MSWIPE", reportItems2.get(0).getMSWIPE());
				itemDetails3.put("ZOMATO", reportItems2.get(0).getZOMATO());
				itemDetails3.put("PAYTM", reportItems2.get(0).getPAYTM());
				itemDetails3.put("SWIGGY", reportItems2.get(0).getSWIGGY());
				itemDetails3.put("MAGIC_PIN", reportItems2.get(0).getMAGIC_PIN());
				itemDetails3.put("OTHERS", reportItems2.get(0).getOTHERS());
				itemsArr.put(itemDetails2);// 1 table order
				itemsArr.put(itemDetails1);// 0 takeaway
				itemsArr.put(itemDetails3);// 2 home delivery
				// order edited to adjust report, simple workaround ;)
			}
			outObj.put("report", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getTotalSalesForService")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTotalSalesForService(@QueryParam("hotelId") String hotelId,
			@QueryParam("serviceDate") String serviceDate, @QueryParam("serviceType") String serviceType) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONObject arrObj;
		Report reportItems;
		ArrayList<Expense> expenses = dao.getCashExpenses(hotelId, serviceDate, serviceType);
		cashdrawerOpen(hotelId, dao);

		try {
			reportItems = dao.getTotalSalesForService(hotelId, serviceDate, serviceType);
			outObj.put("foodBill", reportItems.getFoodBill());
			outObj.put("barBill", reportItems.getBarBill());
			outObj.put("inhouseSales", reportItems.getInhouseSales());
			outObj.put("hdSales", reportItems.getHomeDeliverySales());
			outObj.put("taSales", reportItems.getTakeAwaySales());
			outObj.put("orderCount", reportItems.getOrderCount());
			outObj.put("printCount", reportItems.getPrintCount());
			outObj.put("reprints", reportItems.getReprints());
			outObj.put("complimentary", reportItems.getComplimentary());
			double temp = Math.round(reportItems.getTotal() * 100);
			outObj.put("total", temp / 100);
			double zomatoCash = dao.getCardPaymentByType(hotelId, serviceDate, serviceType, "ZOMATO_CASH");
			outObj.put("zomatoCash", zomatoCash);
			double swiggyCash = dao.getCardPaymentByType(hotelId, serviceDate, serviceType, "SWIGGY_CASH");
			outObj.put("swiggyCash", swiggyCash);
			temp = Math.round((reportItems.getCashPayment()) * 100);
			outObj.put("cash", temp / 100);
			temp = Math.round((reportItems.getCashPayment() + zomatoCash + swiggyCash) * 100);
			outObj.put("totalCash", temp / 100);
			temp = Math.round(dao.getTotalCardPayment(hotelId, serviceDate, serviceType) * 100);
			outObj.put("card", temp / 100);
			temp = Math.round(dao.getTotalAppPayment(hotelId, serviceDate, serviceType) * 100);
			outObj.put("app", temp / 100);
			temp = Math.round(reportItems.getDiscount() * 100);
			outObj.put("discount", temp / 100);
			temp = Math.round(reportItems.getTotalTax() * 100);
			outObj.put("totalTax", temp / 100);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.VISA.toString());
			arrObj.put("amount", dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.VISA.toString()));
			outObj.put(CardType.VISA.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.VISA.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.MASTERCARD.toString());
			arrObj.put("amount",
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.MASTERCARD.toString()));
			outObj.put(CardType.MASTERCARD.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.MASTERCARD.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.MAESTRO.toString());
			arrObj.put("amount",
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.MAESTRO.toString()));
			outObj.put(CardType.MAESTRO.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.MAESTRO.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.AMEX.toString());
			arrObj.put("amount", dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.AMEX.toString()));
			outObj.put(CardType.AMEX.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.AMEX.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.RUPAY.toString());
			arrObj.put("amount",
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.RUPAY.toString()));
			outObj.put(CardType.RUPAY.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.RUPAY.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.MSWIPE.toString());
			arrObj.put("amount",
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.MSWIPE.toString()));
			outObj.put(CardType.MSWIPE.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.MSWIPE.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.OTHERS.toString());
			arrObj.put("amount",
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.OTHERS.toString()));
			outObj.put(CardType.OTHERS.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.OTHERS.toString()));
			arr.put(arrObj);
			outObj.put("cards", arr);

			arr = new JSONArray();
			arrObj = new JSONObject();
			arrObj.put("name", CardType.ZOMATO.toString());
			arrObj.put("amount",
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.ZOMATO.toString()));
			outObj.put(CardType.ZOMATO.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.ZOMATO.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.SWIGGY.toString());
			arrObj.put("amount",
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.SWIGGY.toString()));
			outObj.put(CardType.SWIGGY.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.SWIGGY.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.MAGIC_PIN.toString());
			arrObj.put("amount",
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.MAGIC_PIN.toString()));
			outObj.put(CardType.MAGIC_PIN.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.MAGIC_PIN.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", CardType.PAYTM.toString());
			arrObj.put("amount",
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.PAYTM.toString()));
			outObj.put(CardType.PAYTM.toString(),
					dao.getCardPaymentByType(hotelId, serviceDate, serviceType, CardType.PAYTM.toString()));
			arr.put(arrObj);
			outObj.put("apps", arr);

			outObj.put("voidTrans", dao.getVoidTransactions(hotelId, serviceDate, serviceType));
			outObj.put("cashInHand", dao.getCashInHand(hotelId));
			int totalExpense = 0;
			int cashLift = 0;

			arr = new JSONArray();
			for (Expense expense : expenses) {
				arrObj = new JSONObject();
				arrObj.put("amount", expense.getAmount());
				arrObj.put("payee", expense.getPayee());
				arrObj.put("type", expense.getType());
				arrObj.accumulate("memo", expense.getMemo());
				if (expense.getType().equals(ExpenseType.CASH_LIFT.toString()))
					cashLift += expense.getAmount();
				else
					totalExpense += expense.getAmount();
				arr.put(arrObj);
			}

			outObj.put("expenses", arr);
			outObj.put("totalExpense", totalExpense);
			outObj.put("cashLift", cashLift);
			outObj.put("eodType", dao.getHotelById(hotelId).getEODType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getSalesReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSaleSummaryReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Report> reportItems = dao.getSaleSummaryReport(hotelId, startDate, endDate);

		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();

				itemDetails.put("foodBill", reportItems.get(i).getFoodBill());
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("barBill", reportItems.get(i).getBarBill());
				itemDetails.put("discount", reportItems.get(i).getDiscount());
				itemDetails.put("sc", reportItems.get(i).getServiceCharge());
				itemDetails.put("tip", reportItems.get(i).getTip());
				itemDetails.put("pax", reportItems.get(i).getPax());
				itemDetails.put("inhouse", reportItems.get(i).getInhouse());
				itemDetails.put("card", reportItems.get(i).getCardPayment());
				itemDetails.put("cash", reportItems.get(i).getCashPayment());
				itemDetails.put("trType", reportItems.get(i).getCardType());
				itemDetails.put("total", reportItems.get(i).getTotal());
				itemDetails.put("tableId", reportItems.get(i).getTableId());
				itemDetails.put("orderDate", reportItems.get(i).getOrderDate());
				itemDetails.put("cgst", reportItems.get(i).getGST() / 2);
				itemDetails.put("sgst", reportItems.get(i).getGST() / 2);
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDailyDiscountReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDailyDiscountReport(@QueryParam("hotelId") String hotelId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<DailyDiscountReport> reportItems = dao.getDailyDiscountReport(hotelId, startDate, endDate);

		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("discountName", reportItems.get(i).getName());
				itemDetails.put("value", reportItems.get(i).getValue());
				itemDetails.put("description", reportItems.get(i).getDescription());
				itemDetails.put("discountPer", reportItems.get(i).getDiscountPer());
				itemDetails.put("sumTotal", reportItems.get(i).getTotal());
				itemDetails.put("avgTotal", reportItems.get(i).getAvgTotal());
				itemDetails.put("sumDiscount", reportItems.get(i).getDiscount());
				itemDetails.put("avgDiscount", reportItems.get(i).getAvgDiscount());
				itemDetails.put("discountedTotal", reportItems.get(i).getSumDiscountedTotal());
				itemDetails.put("ordersAffected", reportItems.get(i).getOrdersAffected());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCollectionWiseReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCollectionWiseReport(@QueryParam("hotelId") String hotelId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<CollectionWiseReportA> reportItemsA = dao.getCollectionWiseReportA(hotelId, startDate, endDate);
		ArrayList<CollectionWiseReportB> reportItemsB = dao.getCollectionWiseReportB(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItemsA.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("collection", reportItemsA.get(i).getCollection());
				itemDetails.put("grossTotal", reportItemsA.get(i).getGrossTotal());
				itemDetails.put("averagePrice", reportItemsA.get(i).getAveragePrice());
				itemDetails.put("noOrdersAffected", reportItemsA.get(i).getNoOrdersAffected());
				itemDetails.put("noOrdersAffectedPer", reportItemsA.get(i).getNoOrdersAffectedPer());
				itemDetails.put("totalQuantityOrdered", reportItemsA.get(i).getTotalQuantityOrdered());
				itemDetails.put("totalQuantityOrderedPer", reportItemsA.get(i).getTotalQuantityOrderedPer());
				itemDetails.put("topItemTitle", reportItemsB.get(i).getHotItem());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAttendanceReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAttendanceReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Attendance> reportItemsA = dao.getAttendanceReport(hotelId, startDate, endDate);
		ArrayList<Attendance> reportItemsB = dao.getAttendanceReportB(hotelId, startDate, endDate);

		String startTime = "";
		String endTime = "";
		try {
			for (int i = 0; i < reportItemsA.size(); i++) {
				startTime = reportItemsA.get(i).getCheckInTime();
				endTime = reportItemsA.get(i).getCheckOutTime();

				if (Pattern.matches("[0-9]{2}:[0-9]{2}", startTime))
					startTime = reportItemsA.get(i).getCheckInDate() + " " + startTime;

				if (Pattern.matches("[0-9]{2}:[0-9]{2}", endTime))
					endTime = reportItemsA.get(i).getCheckOutDate() + " " + endTime;

				itemDetails = new JSONObject();
				itemDetails.put("name", reportItemsA.get(i).getFirstName() + " " + reportItemsA.get(i).getSurName());
				itemDetails.put("checkInDate", reportItemsA.get(i).getCheckInDate());
				itemDetails.put("employeeId", reportItemsA.get(i).getEmployeeId());
				itemDetails.put("checkInTime", reportItemsA.get(i).getCheckInTimeHHMM());
				itemDetails.put("checkOutTime", reportItemsA.get(i).getCheckOutTimeHHMM());
				itemDetails.put("attendanceStr", reportItemsA.get(i).getAttendanceStr());
				itemDetails.put("reason", reportItemsA.get(i).getReason());
				itemDetails.put("timeDiff", timeDecorator(this.calculateTimeDiff("HH:mm", startTime, endTime)));
				itemDetails.put("presentCount", reportItemsA.get(i).getPresentCount());
				itemDetails.put("absentCount", reportItemsA.get(i).getAbsentCount());
				itemDetails.put("excusedCount", reportItemsA.get(i).getExcusedCount());
				itemDetails.put("salary", reportItemsA.get(i).getSalary());
				itemDetails.put("shift", reportItemsA.get(i).getShift());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);

			itemsArr = new JSONArray();
			for (int i = 0; i < reportItemsB.size(); i++) {
				itemDetails = new JSONObject();
				String date = reportItemsB.get(i).getCheckInDate();
				itemDetails.put("checkInDate", date);
				itemsArr.put(itemDetails);
			}
			outObj.put("reportB", itemsArr);
			outObj.put("hasSecondShift", dao.hasSecondShift(hotelId, startDate, endDate));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	private String calculateTimeDiff(String format, String startTime, String endTime) {

		if (startTime.equals("") || endTime.equals(""))
			return "";
		SimpleDateFormat stf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		SimpleDateFormat etf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		if (startTime.length() == 16)
			stf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		if (endTime.length() == 16)
			etf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date sTime = null;
		Date eTime = null;
		try {
			sTime = stf.parse(startTime);
			eTime = etf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long difference = eTime.getTime() - sTime.getTime();

		return DurationFormatUtils.formatDuration(difference, format);
	}

	private String timeDecorator(String time) {
		String[] timeArr = time.split(":");
		StringBuilder builder = new StringBuilder();
		int offSet = 0;
		for (String item : timeArr) {
			if (offSet == 0) {
				builder.append(item);
				if (item.equals("01"))
					builder.append("hr ");
				else
					builder.append("hrs ");
			}
			if (offSet == 1) {
				builder.append(item);
				if (item.equals("01"))
					builder.append("min ");
				else
					builder.append("mins ");
			}
			if (offSet == 2) {
				builder.append(item);
				if (item.equals("01"))
					builder.append("sec ");
				else
					builder.append("secs ");
			}
			offSet++;
		}
		return builder.toString();
	}

	@GET
	@Path("/v1/getGrossSalesReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGrossSalesReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<GrossSaleReport> reportItems = dao.getGrossSalesReport(hotelId, startDate, endDate);

		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("grossTotal", reportItems.get(i).getGrossTotal());
				itemDetails.put("grossDiscount", reportItems.get(i).getGrossDiscount());
				itemDetails.put("grossTaxes", reportItems.get(i).getGrossTaxes());
				itemDetails.put("grossServiceCharge", reportItems.get(i).getGrossServiceCharge());
				itemDetails.put("NetSales", reportItems.get(i).getNetSales());
				itemDetails.put("grossExpenses", reportItems.get(i).getGrossExpenses());
				itemDetails.put("Total", reportItems.get(i).getGrandTotal());
				itemDetails.put("sumVoids", reportItems.get(i).getSumVoids());
				itemDetails.put("sumReturns", reportItems.get(i).getSumReturns());
				itemDetails.put("countVoids", reportItems.get(i).getCountVoids());
				itemDetails.put("countReturns", reportItems.get(i).getCountReturns());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDailyOperationReport1")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDailyOperationReport1(@QueryParam("hotelId") String hotelId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		// total operating cost
		ArrayList<DailyOperationReport> reportItems1 = dao.getDailyOperationReport2(hotelId, startDate, endDate);
		// total revenue
		ArrayList<DailyOperationReport> reportItems2 = dao.getDailyOperationReport1(hotelId, startDate, endDate);
		// Total Operating Margin
		ArrayList<DailyOperationReport> reportItems3 = dao.getDailyOperationReport3(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems1.size(); i++) {
				itemDetails = new JSONObject();
				// total Revenue
				itemDetails.put("totalRevenue", reportItems1.get(i).getTotalRevenue());
				itemDetails.put("grossTotal", reportItems1.get(i).getGrossTotal());
				itemDetails.put("grossDiscount", reportItems1.get(i).getGrossDiscount());
				itemDetails.put("grossTaxes", reportItems1.get(i).getGrossTaxes());
				itemDetails.put("grossServiceCharge", reportItems1.get(i).getGrossServiceCharge());
				itemDetails.put("NetSales", reportItems1.get(i).getNetSales());
				// total operating cost
				itemDetails.put("totalOperatingCost", reportItems2.get(i).gettotalOperatingCost());
				itemDetails.put("INVENTORY", reportItems2.get(i).getINVENTORY());
				itemDetails.put("LABOUR", reportItems2.get(i).getLABOUR());
				itemDetails.put("RENT", reportItems2.get(i).getRENT());
				itemDetails.put("ELECTRICITY_BILL", reportItems2.get(i).getELECTRICITY_BILL());
				itemDetails.put("GAS_BILL", reportItems2.get(i).getGAS_BILL());
				itemDetails.put("PETROL", reportItems2.get(i).getPETROL());
				itemDetails.put("TELEPHONE_BILL", reportItems2.get(i).getTELEPHONE_BILL());
				itemDetails.put("MOBILE_RECHARGE", reportItems2.get(i).getMOBILE_RECHARGE());
				itemDetails.put("INTERNET", reportItems2.get(i).getINTERNET());
				itemDetails.put("SOFTWARE", reportItems2.get(i).getSOFTWARE());
				itemDetails.put("COMPUTER_HARDWARE", reportItems2.get(i).getCOMPUTER_HARDWARE());
				itemDetails.put("REPAIRS", reportItems2.get(i).getREPAIRS());
				itemDetails.put("OTHERS", reportItems2.get(i).getOTHERS());
				itemDetails.put("CASH_LIFT", reportItems2.get(i).getCASH_LIFT());
				// Total Operating Margin
				itemDetails.put("totalOperatingMargin", reportItems3.get(i).getTotalOperatingMargin());
				itemDetails.put("paidIn", reportItems3.get(i).getPaidIn());
				itemDetails.put("paidOut", reportItems3.get(i).getPaidOut());
				itemDetails.put("s", "     ");
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDailyOperationReport2")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDailyOperationReport2(@QueryParam("hotelId") String hotelId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		// Operating Metrics
		// main 3
		ArrayList<DailyOperationReport> reportItems4 = dao.getDailyOperationReport4(hotelId, startDate, endDate);
		// tables turned
		ArrayList<DailyOperationReport> reportItems5 = dao.getDailyOperationReport5(hotelId, startDate, endDate);
		// voids
		// ArrayList<DailyOperationReport> reportItems6 =
		// dao.getDailyOperationReport6(hotelId,startDate, endDate);
		// returns
		// ArrayList<DailyOperationReport> reportItems7 =
		// dao.getDailyOperationReport7(hotelId,startDate, endDate);
		try {
			for (int i = 0; i < reportItems4.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("s", "   ");
				// Operating Metrics
				// main
				itemDetails.put("serviceType", reportItems4.get(i).getServiceType());
				itemDetails.put("AvgAmountPerGuest", reportItems4.get(i).getAvgAmountPerGuest());
				itemDetails.put("AvgAmountPerCheck", reportItems4.get(i).getAvgAmountPerCheck());
				itemDetails.put("Total", reportItems4.get(i).getTotal());
				itemDetails.put("noOfGuests", reportItems4.get(i).getNoOfGuests());
				itemDetails.put("noOfBills", reportItems4.get(i).getNoOfBills());
				// tables turned
				if (hotelId.equals("Be0001")) {
					itemDetails.put("AvgAmountPerTableTurned", "noInhouse!");
				} else {
					itemDetails.put("AvgAmountPerTableTurned", reportItems5.get(i).getAvgAmountPerTableTurned());
				}
				// voids
				// itemDetails.put("voids", reportItems6.get(i).getVoids());
				// returns
				// itemDetails.put("returns", reportItems7.get(i).getReturns());
				itemDetails.put("s", "    ");
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	// testing
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String args[]) throws ParseException {

	}

	private void generateShortForm() {

		String title = "Chi Manch Soup";
		String[] titleSplit = title.split(" ");
		title = "";
		for (String titleBuilder : titleSplit) {
			title += titleBuilder.substring(0, 1).toUpperCase() + titleBuilder.substring(1, titleBuilder.length())
					+ " ";
		}
		System.out.println(title);
	}

	@GET
	@Path("/v1/getPaymentType")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPaymentType() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (PaymentType pay : PaymentType.values()) {
				JSONObject obj = new JSONObject();
				obj.put("payment", pay);
				outArr.put(obj);
			}
			outObj.put("paymentTypes", outArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getExpenseType")
	@Produces(MediaType.APPLICATION_JSON)
	public String getExpenseType() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (ExpenseType exp : ExpenseType.values()) {
				JSONObject obj = new JSONObject();
				obj.put("expense", exp);
				outArr.put(obj);
			}
			outObj.put("expenseTypes", outArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getBankAccounts")
	@Produces(MediaType.APPLICATION_JSON)
	public String getBankAccounts(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Bank> bank = dao.getBankAccounts(hotelId);
		try {
			for (int i = 0; i < bank.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("account", bank.get(i).getAccountName());
				itemsArr.put(itemDetails);
			}
			orderObj.put("accounts", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderObj.toString();
	}

	@POST
	@Path("/v1/addExpense")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addExpense(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			String hotelId = inObj.getString("hotelId");
			String expenseType = inObj.getString("expenseType");

			if (dao.addExpense(hotelId, inObj.getInt("expenseAmount") + inObj.getInt("bonus"),
					inObj.getString("details"), inObj.getString("payeeName"), inObj.getInt("cheque"),
					inObj.getString("paymentType"), expenseType, inObj.getString("bankAccount"),
					inObj.getString("userId"))) {
				outObj.put("status", true);

				if (expenseType.equals(ExpenseType.LABOUR.toString())) {
					dao.updateLabourLog(inObj.getString("hotelId"), inObj.getDouble("expenseAmount"),
							inObj.getString("employeeId"), inObj.getDouble("bonus"));
				}
				if (inObj.getString("bankAccount").equals("CASH_DRAWER")) {
					dao.updateCashBalance(hotelId, dao.getCashBalance(hotelId) - inObj.getInt("expenseAmount"));
					cashdrawerOpen(hotelId, dao);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDailyAnalysisReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDailyAnalysisReport(@QueryParam("hotelId") String hotelId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		Report reportItems;

		try {
			for (int i = 0; i < 2; i++) {
				reportItems = dao.getDailyIncome(hotelId, startDate, endDate, i);
				itemDetails = new JSONObject();
				itemDetails.put("totalRevenue", reportItems.getTotal());
				itemDetails.put("grossSale", reportItems.getTotal());
				itemDetails.put("discounts", reportItems.getDiscount());
				itemDetails.put("sc", reportItems.getServiceCharge());
				itemDetails.put("taxes",
						reportItems.getServiceTax() + reportItems.getSbCess() + reportItems.getKkCess());
				itemDetails.put("VAT", reportItems.getVATBar() + reportItems.getVATFood());
				itemDetails.put("averagePerPax", reportItems.getTotal() / reportItems.getPax());
				itemDetails.put("averagePerCheck", reportItems.getTotal() / reportItems.getChecks());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("barBill", reportItems.getBarBill());

				itemsArr.put(itemDetails);
			}
			outObj.put("income", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addService")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addService(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		String serviceDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addService(inObj.getString("hotelId"), inObj.getString("serviceType"), serviceDate,
					inObj.getInt("cashInHand"))) {
				outObj.put("status", true);
				outObj.put("serviceDate", serviceDate);
				dao.updateCashBalance(inObj.getString("hotelId"), inObj.getInt("cashInHand"));
				dao.initDatabase(inObj.getString("hotelId"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/endService")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String endService(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			if (dao.getHotelById(hotelId).getEODType().equals("NOEOD")) {
				JSONObject report = inObj.getJSONObject("report");
				try {
					if (!dao.addRevenue(inObj.getString("hotelId"), inObj.getString("serviceDate"),
							inObj.getString("serviceType"), report.getDouble("cash"), report.getDouble("card"),
							report.getDouble("app"), report.getDouble("total"), report.getDouble("VISA"),
							report.getDouble("MASTERCARD"), report.getDouble("MAESTRO"), report.getDouble("AMEX"),
							report.getDouble("OTHERS"), report.getDouble("MSWIPE"), report.getDouble("RUPAY"),
							report.getDouble("ZOMATO"), report.getDouble("SWIGGY"), report.getDouble("MAGIC_PIN"),
							report.getDouble("PAYTM"), report.getDouble("complimentary"), 0.0, "", "")) {

						outObj.put("message", "Service cannot be ended right now. Please contact support!");
						return outObj.toString();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			if (dao.endService(inObj.getString("hotelId"), inObj.getString("serviceDate"),
					inObj.getString("serviceType"))) {
				outObj.put("status", true);
				dao.updateCashBalance(inObj.getString("hotelId"), 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getServiceLog")
	@Produces(MediaType.APPLICATION_JSON)
	public String getServiceLog(@QueryParam("hotelId") String hotelId, @QueryParam("serviceDate") String serviceDate) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONObject serviceDetails = null;
		ServiceLog serviceLog = dao.getServiceLog(hotelId, serviceDate);

		try {
			serviceDetails = new JSONObject();
			serviceDetails.put("startTimeStamp", serviceLog.getStartTimeStamp());
			serviceDetails.put("endTimeStamp", serviceLog.getEndTimeStamp());
			serviceDetails.put("isCurrent", serviceLog.getIsCurrent());
			serviceDetails.put("serviceType", serviceLog.getServiceType());

			outObj.put("serviceLog", serviceLog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCurrentService")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCurrentService(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONObject serviceDetails = null;
		ServiceLog serviceLog = dao.getCurrentService(hotelId);

		try {
			outObj.put("status", false);
			serviceDetails = new JSONObject();
			if (serviceLog != null) {
				serviceDetails.put("startTimeStamp", serviceLog.getStartTimeStamp());
				serviceDetails.put("endTimeStamp", serviceLog.getEndTimeStamp());
				serviceDetails.put("serviceDate", serviceLog.getServiceDate());
				serviceDetails.put("serviceType", serviceLog.getServiceType());
				outObj.put("status", true);
			}

			outObj.put("serviceLog", serviceDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getPayment")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPayment(@QueryParam("hotelId") String hotelId, @QueryParam("orderId") String orderId) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONObject paymentDetails = null;
		Report payment = dao.getPayment(hotelId, orderId);

		try {
			outObj.put("status", false);
			paymentDetails = new JSONObject();
			if (payment != null) {
				paymentDetails.put("cash", payment.getCashPayment());
				paymentDetails.put("card", payment.getCardPayment());
				paymentDetails.put("cardType", payment.getCardType());
				paymentDetails.put("sc", payment.getServiceCharge());
				paymentDetails.put("st", payment.getServiceTax());
				paymentDetails.put("gst", payment.getGST());
				paymentDetails.put("sbCess", payment.getSbCess());
				paymentDetails.put("kkCess", payment.getKkCess());
				paymentDetails.put("vatFood", payment.getVATFood());
				paymentDetails.put("vatBar", payment.getVATBar());
				paymentDetails.put("total", payment.getTotal());
				paymentDetails.put("foodBill", payment.getFoodBill());
				paymentDetails.put("barBill", payment.getBarBill());
				paymentDetails.put("taxableFoodBill", dao.getTaxableFoodBill(hotelId, orderId));
				paymentDetails.put("taxableBarBill", dao.getTaxableBarBill(hotelId, orderId));
				paymentDetails.put("discount", payment.getDiscount());
				paymentDetails.put("discountName", payment.getDiscountName());
				outObj.put("status", true);
			}

			outObj.put("payment", paymentDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/addRevenue")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addRevenue(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			boolean status = dao.addRevenue(inObj.getString("hotelId"), inObj.getString("serviceType"),
					inObj.getString("serviceDate"), inObj.getDouble("cash"), inObj.getDouble("card"),
					inObj.getDouble("app"), inObj.getDouble("total"), inObj.getDouble("visa"),
					inObj.getDouble("mastercard"), inObj.getDouble("maestro"), inObj.getDouble("amex"),
					inObj.getDouble("others"), inObj.getDouble("mswipe"), inObj.getDouble("rupay"),
					inObj.getDouble("zomato"), inObj.getDouble("swiggy"), inObj.getDouble("magicPin"),
					inObj.getDouble("payTm"), inObj.getDouble("complimentary"), inObj.getDouble("difference"),
					inObj.getString("reason"), inObj.getString("clearance"));

			outObj.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getMPNotification")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMPNotification(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		MPNotification notification = dao.getMPNotification(hotelId);

		try {
			outObj.put("status", false);
			outObj.put("checkOutOrders", notification.getCheckoutOrders());
			outObj.put("outOfStock", notification.getOutOfStock());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getOrderIdFromTableId")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOrderIdFromTableId(@QueryParam("hotelId") String hotelId, @QueryParam("tableId") String tableId) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		String orderId = dao.getOrderIdFromTables(hotelId, tableId);
		try {
			outObj.put("orderId", orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getOrdersOfOneCustomer")
	@Produces(MediaType.APPLICATION_JSON) // Jason
	public String getOrdersOfOneCustomer(@QueryParam("hotelId") String hotelId,
			@QueryParam("mobileNo") String mobileNo) { // Jason
		AccessManager dao = new AccessManager(false);

		ArrayList<Order> orderList = dao.getOrdersOfOneCustomer(hotelId, mobileNo);
		JSONArray outObj = new JSONArray(orderList);

		try {
			// outObj.put(orderList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getItemwiseReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getItemwiseReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		int totalQuantity = 0;
		ArrayList<itemWiseReport> reportItems = dao.getItemwiseReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("title", reportItems.get(i).getTitle());
				itemDetails.put("category", reportItems.get(i).getCategory());
				itemDetails.put("qty", reportItems.get(i).getQty());
				itemDetails.put("menuId", reportItems.get(i).getMenuId());
				itemsArr.put(itemDetails);
				totalQuantity += reportItems.get(i).getQty();
			}
			outObj.put("report", itemsArr);
			outObj.put("totalQuantity", totalQuantity);
			outObj.put("name", "ITEMWISE REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getLiquorReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getLiquorReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<itemWiseReport> reportItems = dao.getLiquorReport(hotelId, startDate, endDate);
		int totalQuantity = 0;
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("title", reportItems.get(i).getTitle());
				itemDetails.put("category", reportItems.get(i).getCategory());
				itemDetails.put("qty", reportItems.get(i).getQty());
				itemDetails.put("menuId", reportItems.get(i).getMenuId());
				itemsArr.put(itemDetails);
				totalQuantity += reportItems.get(i).getQty();
			}
			outObj.put("report", itemsArr);
			outObj.put("totalQuantity", totalQuantity);
			outObj.put("name", "ITEMWISE LIQUOR REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getIncentiveReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getIncentiveReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<EntityString> captains = dao.getCaptainOrderService(hotelId, startDate, endDate);
		IncentiveReport reportItemsFood;
		IncentiveReport reportItemsBar;
		int totalBar = 0;
		int totalFood = 0;
		int totalBarSale = 0;
		int totalFoodSale = 0;
		try {
			for (int i = 0; i < captains.size(); i++) {
				itemDetails = new JSONObject();
				reportItemsFood = dao.getIncentiveForEmployee(hotelId, captains.get(i).getEntity(), false, startDate,
						endDate);
				itemDetails.put("user", captains.get(i).getEntity());
				itemDetails.put("foodSale", reportItemsFood.getSale());
				itemDetails.put("foodIncentive", reportItemsFood.getIncentive());
				reportItemsBar = dao.getIncentiveForEmployee(hotelId, captains.get(i).getEntity(), true, startDate,
						endDate);
				itemDetails.put("barSale", reportItemsBar.getSale());
				itemDetails.put("barIncentive", reportItemsBar.getIncentive());
				itemDetails.put("totalIncentive", reportItemsBar.getIncentive() + reportItemsFood.getIncentive());
				itemDetails.put("totalSale", reportItemsBar.getSale() + reportItemsFood.getSale());
				itemsArr.put(itemDetails);
				totalBar += reportItemsBar.getIncentive();
				totalBarSale += reportItemsBar.getSale();
				totalFood += reportItemsFood.getIncentive();
				totalFoodSale += reportItemsFood.getSale();
			}
			outObj.put("report", itemsArr);
			outObj.put("totalBarIncentive", totalBar);
			outObj.put("totalBarSale", totalBarSale);
			outObj.put("totalFoodIncentive", totalFood);
			outObj.put("totalFoodSale", totalFoodSale);
			outObj.put("totalIncentive", totalFood + totalBar);
			outObj.put("name", "INCENTIVE REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getItemwiseIncentiveReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getItemwiseIncentiveReport(@QueryParam("hotelId") String hotelId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONArray captainsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject captainDetails = null;
		JSONObject itemDetails = null;
		ArrayList<EntityString> captains = dao.getCaptainOrderService(hotelId, startDate, endDate);
		ArrayList<IncentiveReport> reportItems;
		double totalIncentive = 0.0;
		try {
			captainsArr = new JSONArray();
			for (int i = 0; i < captains.size(); i++) {
				captainDetails = new JSONObject();
				itemsArr = new JSONArray();
				boolean isBar = true;
				for(int x = 0; x<2; x++){
					reportItems = dao.getItemwiseIncentiveReport(hotelId, captains.get(i).getEntity(), startDate, endDate, isBar);
					captainDetails.put("userId", captains.get(i).getEntity());
					for (int j = 0; j < reportItems.size(); j++) {
						itemDetails = new JSONObject();
						itemDetails.put("incentive", reportItems.get(j).getIncentive());
						itemDetails.put("quantity", reportItems.get(j).getQuantity());
						itemDetails.put("title", reportItems.get(j).getTitle());
						itemsArr.put(itemDetails);
						totalIncentive += reportItems.get(j).getIncentive();
					}
					isBar = false;
				}
				captainDetails.put("items", itemsArr);
				captainDetails.put("totalIncentive", totalIncentive);
				totalIncentive = 0;
				captainsArr.put(captainDetails);
			}
			outObj.put("report", captainsArr);
			itemsArr = new JSONArray();
			outObj.put("name", "ITEMWISE INCENTIVE REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCashBalance")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getCashBalance(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		int cashBal = dao.getCashBalance(hotelId);
		try {
			outObj.put("cashBalance", cashBal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/applyDiscount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String applyDiscount(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			boolean status = dao.applyDiscount(inObj.getString("hotelId"), inObj.getString("orderId"),
					inObj.getString("discountCode"));

			outObj.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/applyCustomerGST")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String applyCustomerGST(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			boolean status = dao.applyCustomerGST(inObj.getString("hotelId"), inObj.getString("orderId"),
					inObj.getString("gst"));

			outObj.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/switchTable")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String switchTable(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			boolean status = dao.switchTable(inObj.getString("hotelId"), inObj.getString("oldTableNumber"),
					inObj.getJSONArray("newTableNumbers"));

			outObj.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/moveItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String moveItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj = dao.moveItem(inObj.getString("hotelId"), inObj.getInt("oldTableNumber"),
					inObj.getInt("newTableNumber"), inObj.getJSONArray("orderItemIds"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/switchFromBarToTable")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String switchFromBarToTable(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			boolean status = dao.switchFromBarToTable(inObj.getString("hotelId"), inObj.getString("orderId"),
					inObj.getJSONArray("newTableNumbers"));
			outObj.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/syncOnSever")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String syncOnServer(String jsonObject) {
		ArrayList<String> logs = new ArrayList<String>();
		AccessManager dao = new AccessManager(true);
		JSONObject outObj = new JSONObject();
		JSONObject inObj = null;
		try {
			inObj = new JSONObject(jsonObject);
			String[] logArray = inObj.getString("content").split("\\$");

			for (String log : logArray) {
				if (!log.equals("")) {
					System.out.println(log);
					logs.add(log);
				}
			}
			boolean status = dao.syncOnServer(inObj.getString("hotelId"), logs);
			outObj.put("status", status);
			outObj.put("count", 0);
			if (status) {
				outObj.put("count", logArray.length - 1);
			}
		} catch (JSONException e) {

			e.printStackTrace();
		}

		return outObj.toString();
	}

	private void clearTransactionLog(int count) {
		Configurator.writeToServerFile("");
		System.out.println("Server Updated. " + count + " transactions occured. Cleared Transaction Log!");
	}

	@GET
	@Path("/v1/getTransactionLog")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getTransactionLog() {
		JSONObject outObj = new JSONObject();
		String transactionLog = Configurator.getTransactionLog();
		String[] logArray = transactionLog.split("\\$");
		try {
			outObj.put("transactionLog", transactionLog);
			outObj.put("count", logArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getServerState")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getServerState(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		ServerLog log = dao.getLastServerLog(hotelId);
		try {
			outObj.put("status", log.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/serverSetStatusToBusy")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getServerLog(String jsonObject) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		try {
			JSONObject inObj = new JSONObject(jsonObject);
			ServerLog log = dao.getLastServerLog(inObj.getString("hotelId"));
			if (log == null)
				dao.createServerLog(inObj.getString("hotelId"));
			else
				dao.updateServerLog(inObj.getString("hotelId"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateServerStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateServerStatus(String jsonObject) {
		// AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONObject inObj = null;
		try {
			inObj = new JSONObject(jsonObject);
			// dao.updateServerStatus(inObj.getString("hotelId"),
			// inObj.getBoolean("clearLogs"));
			if (inObj.getBoolean("clearLogs"))
				clearTransactionLog(inObj.getInt("count"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateKOTStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateKOTStatus(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			String orderId = inObj.getString("orderId");
			boolean status = dao.updateKOTStatus(inObj.getString("hotelId"), orderId);

			outObj.put("status", status);
			outObj.put("orderId", orderId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	public boolean SendVoidReturnEmail(String hotelId, String subject, String text, String[] filePaths,
			String orderId) {

		ArrayList<String> recipents = new ArrayList<String>();
		SendEmail email = new SendEmail();
		AccessManager dao = new AccessManager(false);
		ArrayList<Employee> employees = dao.getEmployeesByDesignation(hotelId, Designation.MANAGER);
		employees.addAll(dao.getEmployeesByDesignation(hotelId, Designation.OWNER));
		for (Employee employee : employees) {
			if (!employee.getEmail().trim().equals("") || !employee.equals(null)) {
				recipents.add(employee.getEmail());
			}
		}
		String headerTemplete = "<html>"
				+ "<head><style>.alert {padding: 15px;margin-bottom: 20px;border: 1px solid transparent;border-radius: 4px;}"
				+ ".alert-warning {color: #8a6d3b;background-color: #fcf8e3;border-color: #faebcc;}</style></head>"
				+ "<body style='color:#797979; background:#f2f2f2;'><p>Dear Sir/Madam, </p>";
		String footerTemplete = "<p>Thank you, </p><p>Kind Regards, </p><p> OrderOn Support. </p>"
				+ "<p>If you have any other questions, please feel free to reach out to us at +91-98-67334779 or write to us at support@orderon.co.in. Happy ordering!</p></body></html>";
		text = headerTemplete + text + footerTemplete;
		email.sendEmail(recipents, subject, text, null);

		return false;
	}

	private static final byte[] kaffineDrawerCode = { 27, 112, 0, 100, (byte) 250 };

	public void cashdrawerOpen(String hotelId, AccessManager dao) {

		byte[] open = { 27, 112, 0, 25, (byte) 251 };
		if (hotelId.equals("ka0001")) {
			open = kaffineDrawerCode;
		} else
			return;
		// visit http://keyhut.com/popopen4.htm
		PrintService pservice = PrintServiceLookup.lookupDefaultPrintService();
		DocPrintJob job = pservice.createPrintJob();
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(open, flavor, null);
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		try {
			job.print(doc, aset);
		} catch (PrintException ex) {
			System.out.println(ex.getMessage());
		}
	}

	// -----------------------------------------------------------------------Loyalty

	@POST
	@Path("/v1/addLoyaltyOffer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addLoyalty(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj = dao.addLoyaltyOffer(inObj.getString("name"), inObj.getInt("offerType"), inObj.getInt("points"), inObj.getString("offerValue"),
					inObj.getString("hasUsageLimit"), inObj.getInt("usageLimit"), inObj.getInt("minBill"), inObj.getString("userType"), 
					inObj.getString("validCollections"), inObj.getString("status"), inObj.getString("startDate"), inObj.getString("expiryDate"), 
					inObj.getString("hotelId"), inObj.getString("chainId"), inObj.getInt("offerQuantity"));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/editLoyaltyOffer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String editLoyaltyOfferStatus(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj = dao.editLoyaltyOffer(inObj.getInt("id"), inObj.getInt("offerType"), inObj.getInt("points"), inObj.getString("offerValue"),
					inObj.getString("hasUsageLimit"), inObj.getInt("usageLimit"), inObj.getInt("minBill"), inObj.getString("userType"), 
					inObj.getString("validCollections"), inObj.getString("status"), inObj.getString("startDate"), inObj.getString("expiryDate"), 
					inObj.getString("hotelId"), inObj.getString("chainId"), inObj.getInt("offerQuantity"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAllLoyaltyOffers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllLoyaltyOffers(@QueryParam("hotelId") String hotelId, @QueryParam("chainId") String chainId,
			 @QueryParam("customerPoints") int customerPoints) {
		AccessManager dao = new AccessManager(false);
		JSONArray loyaltyOfferArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject loyaltyDetails = null;
		ArrayList<LoyaltyOffer> loyaltyOffers = null;
		try {
			outObj.put("status", false);
			if(!hotelId.equals("")) {
				if(customerPoints>0) 
					loyaltyOffers = dao.getAllLoyaltyOffersForCustomer(hotelId, customerPoints);
				else
					loyaltyOffers = dao.getAllLoyaltyOffers(hotelId);
			}
			else if(!chainId.equals(""))
				loyaltyOffers = dao.getAllLoyaltyOffersByChain(chainId);
			else {
				outObj.put("message", "hotelId or chainId is required.");
			}
				
			for (int i = 0; i < loyaltyOffers.size(); i++) {
				loyaltyDetails = new JSONObject();
				loyaltyDetails.put("id", loyaltyOffers.get(i).getId());
				loyaltyDetails.put("name", loyaltyOffers.get(i).getName());
				loyaltyDetails.put("offerType", loyaltyOffers.get(i).getOfferType());
				loyaltyDetails.put("offerTypeView", loyaltyOffers.get(i).getOfferTypeView());
				loyaltyDetails.put("offerValue", loyaltyOffers.get(i).getOfferValue());
				loyaltyDetails.put("offerQuantity", loyaltyOffers.get(i).getOfferQuantity());
				loyaltyDetails.put("points", loyaltyOffers.get(i).getPoints());
				loyaltyDetails.put("startDate", loyaltyOffers.get(i).getStartDate());
				loyaltyDetails.put("expiryDate", loyaltyOffers.get(i).getExpiryDate());
				loyaltyDetails.put("userType", loyaltyOffers.get(i).getUserType());
				loyaltyDetails.put("usageLimit", loyaltyOffers.get(i).getUsageLimit());
				loyaltyDetails.put("minBill", loyaltyOffers.get(i).getMinBill());
				loyaltyDetails.put("status", loyaltyOffers.get(i).getStatus());
				loyaltyDetails.put("chainId", loyaltyOffers.get(i).getChainId());
				loyaltyDetails.put("validCollections", loyaltyOffers.get(i).getValidCollections());
				loyaltyOfferArr.put(loyaltyDetails);
			}
			outObj.put("loyaltyOffers", loyaltyOfferArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getLoyaltyOfferById")
	@Produces(MediaType.APPLICATION_JSON)
	public String getLoyaltyOffer(@QueryParam("hotelId") String hotelId, @QueryParam("id") int id) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONObject collObj = new JSONObject();
		JSONArray collArr = new JSONArray();
		String[] collections = {};
		try {
			LoyaltyOffer loyalty = dao.getLoyaltyOfferById(hotelId, id);
			outObj = new JSONObject();
			outObj.put("status", false);
			if (loyalty != null) {
				outObj.put("status", true);
				outObj.put("id", loyalty.getId());
				outObj.put("name", loyalty.getName());
				outObj.put("offerType", loyalty.getOfferType());
				outObj.put("offerTypeView", loyalty.getOfferTypeView());
				outObj.put("offerValue", loyalty.getOfferValue());
				outObj.put("offerQuantity", loyalty.getOfferQuantity());
				outObj.put("points", loyalty.getPoints());
				outObj.put("usageLimit", loyalty.getUsageLimit());
				outObj.put("hasUsageLimit", loyalty.gethasUsageLimit());
				outObj.put("minBill", loyalty.getMinBill());
				outObj.put("startDate", loyalty.getStartDate());
				outObj.put("expiryDate", loyalty.getExpiryDate());
				outObj.put("userType", loyalty.getUserType());
				outObj.put("status", loyalty.getStatus());
				outObj.put("chainId", loyalty.getChainId());
				collections = loyalty.getValidCollections();
				for (int j = 0; j < collections.length; j++) {
					if (collections[j].equals(""))
						continue;
					collObj = new JSONObject();
					collObj.put("collection", collections[j]);
					collArr.put(collObj);
				}
				outObj.put("collectionsArr", collArr);
				outObj.put("hasCollections", loyalty.getHasCollections());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/redeemLoyalty")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String redeemLoyalty(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj = dao.redeemLoyaltyOffer(inObj.getString("hotelId"), inObj.getString("orderId"), 
						inObj.getInt("loyaltyId"), inObj.getString("mobileNo"), inObj.getDouble("redeemablePoints"), 
						inObj.getDouble("billAmount"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	//--------------------------------Loyalty Settings

	@GET
	@Path("/v1/getAllLoyaltySettings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllLoyaltySettings(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray loyaltyArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject loyaltyDetails = null;
		ArrayList<LoyaltySetting> loyaltySettings = null;
		try {
			outObj.put("status", false);
			if(hotelId.equals("")) {
				outObj.put("message", "hotelId or chainId is required.");
				return outObj.toString();
			}
			loyaltySettings = dao.getLoyaltySettings(hotelId);
			for (int i = 0; i < loyaltySettings.size(); i++) {
				loyaltyDetails = new JSONObject();
				loyaltyDetails.put("id", loyaltySettings.get(i).getId());
				loyaltyDetails.put("userType", loyaltySettings.get(i).getUserType());
				loyaltyDetails.put("requiredPoints", loyaltySettings.get(i).getRequiredPoints());
				loyaltyDetails.put("rupeeToPoint", loyaltySettings.get(i).getPointToRupee());
				loyaltyArr.put(loyaltyDetails);
			}
			outObj.put("loyaltySettings", loyaltyArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	//--------------------------------------------
	
	

	@POST
	@Path("/v1/assignWaitersToTable")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String assignWaitersToTable(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		JSONObject waiterObj = new JSONObject();
		JSONArray inArr = new JSONArray();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			inArr = inObj.getJSONArray("waiterToTable");
			for (int i = 0; i < inArr.length(); i++) {
				waiterObj = inArr.getJSONObject(i);
				dao.assignWaiterToTable(inObj.getString("hotelId"),
						waiterObj.has("waiter") ? waiterObj.getString("waiter") : "", waiterObj.getInt("tableId"));
			}
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	public static class Printer implements Printable {
		final Component comp;

		public Printer(Component comp) {
			this.comp = comp;
		}

		@Override
		public int print(Graphics g, PageFormat format, int page_index) throws PrinterException {
			if (page_index > 0) {
				return Printable.NO_SUCH_PAGE;
			}

			// get the bounds of the component
			Dimension dim = comp.getSize();
			double cHeight = dim.getHeight();
			double cWidth = dim.getWidth();

			// get the bounds of the printable area
			double pHeight = format.getImageableHeight();
			double pWidth = format.getImageableWidth();

			double pXStart = format.getImageableX();
			double pYStart = format.getImageableY();

			double xRatio = pWidth / cWidth;
			double yRatio = pHeight / cHeight;

			Graphics2D g2 = (Graphics2D) g;
			g2.translate(pXStart, pYStart);
			g2.scale(xRatio, yRatio);
			comp.paint(g2);

			return Printable.PAGE_EXISTS;
		}
	}

	public String formatTitle(String title, boolean isSmall) {

		String[] titleSplit = title.split(" ");
		title = "";
		if (isSmall) {
			for (String titleBuilder : titleSplit) {
				title += titleBuilder.substring(0, 1).toUpperCase() + titleBuilder.substring(1, titleBuilder.length())
						+ " ";
			}
		} else {
			for (String titleBuilder : titleSplit) {
				title += titleBuilder.toUpperCase() + "  ";
			}
		}

		return title;
	}

	public void run(String html) {
		// create jeditorpane
		JEditorPane jEditorPane = new JEditorPane();

		// add an html editor kit
		HTMLEditorKit kit = new HTMLEditorKit();
		jEditorPane.setEditorKit(kit);

		// add some styles to the html
		StyleSheet styleSheet = kit.getStyleSheet();
		styleSheet.addRule("body {width: 377px; font-family:" + Configurator.getKOTFont() + ";}");
		styleSheet.addRule(
				".table-condensed>thead>tr>th, .table-condensed>tbody>tr>th, .table-condensed>tfoot>tr>th, .table-condensed>thead>tr>td, .table-condensed>tbody>tr>td, .table-condensed>tfoot>tr>td {padding: 1px;}");
		styleSheet.addRule(".mt0 {margin-top: 0px;}");
		styleSheet.addRule(".mt-20 {margin-top: 20px;}");
		styleSheet.addRule(".centered{text-align: center}");
		styleSheet.addRule(".mb0 {margin-bottom: 0px;}");
		styleSheet.addRule(".ml25 {margin-left: 25px;}");
		styleSheet.addRule(".ml2 {margin-left: 20px;} ");
		styleSheet.addRule(".pull-right {text-align: right}");
		styleSheet.addRule(".pull-left {text-align: left}");
		styleSheet.addRule(
				".addOn-td {border-top: none !important; padding-top: 0px !important; line-height: 1 !important; font-size: 12px !important;}");
		styleSheet.addRule(".table {width: 100%;max-width: 100%;margin-bottom: 20px;}");
		styleSheet.addRule(
				"table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td,.table>tbody>tr>td, .table>tfoot>tr>td {padding: 2px;line-height: 1.0;border-top: 1px solid #ddd;}");
		styleSheet.addRule(".table>thead>tr>th {vertical-align: bottom;border-bottom: 2px solid #ddd;}");
		styleSheet.addRule(" th {text-align: left;}");
		styleSheet.addRule("h3, .h3 {font-size: 18px;}");
		styleSheet.addRule(".marg-td{ margin-top:0px; margin-bottom:0px; font-size:12px; font-weight: bold;}");
		styleSheet.addRule(".marg-td-small{ margin-top:0px; margin-bottom:0px; font-size:11px; font-weight: bold;}");

		// create a document, set it on the jeditorpane, then add the html
		Document doc = kit.createDefaultDocument();
		jEditorPane.setDocument(doc);
		jEditorPane.setText(html);

		// now add it all to a frame
		JFrame j = new JFrame("HtmlEditorKit Test");
		j.getContentPane().add(jEditorPane, BorderLayout.CENTER);

		// display the frame
		j.setSize(new Dimension(Configurator.getKOTWidth(), Configurator.getKOTHeight()));

		// center the jframe, then make it visible
		j.setVisible(true);
		PrinterJob pjob = PrinterJob.getPrinterJob();
		PageFormat preformat = pjob.defaultPage();
		preformat.setOrientation(PageFormat.PORTRAIT);
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		Paper paper = new Paper();
		double height = j.getSize().getHeight();
		double width = j.getSize().getWidth();
		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width, height);
		// Orientation
		preformat.setPaper(paper);
		PageFormat postFormat = pjob.validatePage(preformat);
		for (PrintService printService : printServices) {
			try {
				if (printService.getName().equals("Cashier")) {
					pjob.setPrintService(printService);
					pjob.setPrintable(new Printer(j), postFormat);
					for (int x = 0; x < 1; x++)
						pjob.print();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Some problem has occured" + e.getMessage());
			}
		}
		j.dispatchEvent(new WindowEvent(j, WindowEvent.WINDOW_CLOSING));
	}

	private void print(String html, String printerName, int copies) {
		JEditorPane ed1 = new JEditorPane("text/html", html);
		PrinterJob pjob = PrinterJob.getPrinterJob();
		JFrame frame = new JFrame("JFrame Example");
		JPanel panel = new JPanel();
		Border padding = BorderFactory.createEmptyBorder();
		panel.setBorder(padding);
		panel.setLayout(new FlowLayout());
		frame.add(ed1);
		frame.setVisible(true);
		frame.setSize(Configurator.getKOTWidth(), Configurator.getKOTHeight());
		PageFormat preformat = pjob.defaultPage();
		preformat.setOrientation(PageFormat.PORTRAIT);
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		Paper paper = new Paper();
		double height = frame.getSize().getHeight() - 100;
		double width = frame.getSize().getWidth() / Configurator.getDivisor();
		paper.setSize(width, height);
		paper.setImageableArea(0, 0, width - 100, height);
		// Orientation
		preformat.setPaper(paper);
		PageFormat postFormat = pjob.validatePage(preformat);
		for (PrintService printService : printServices) {
			try {
				if (printService.getName().equals(printerName)) {
					pjob.setPrintService(printService);
					pjob.setPrintable(new Printer(frame), postFormat);
					for (int x = 0; x < copies; x++)
						pjob.print();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Some problem has occured" + e.getMessage());
			}
		}
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
}