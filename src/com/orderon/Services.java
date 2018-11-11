package com.orderon;

import java.awt.Component;
import java.awt.Dimension;
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
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.orderon.AccessManager.AddOn;
import com.orderon.AccessManager.Attendance;
import com.orderon.AccessManager.Bank;
import com.orderon.AccessManager.Charge;
import com.orderon.AccessManager.Collection;
import com.orderon.AccessManager.CollectionWiseReportA;
import com.orderon.AccessManager.CollectionWiseReportB;
import com.orderon.AccessManager.ConsumptionReport;
import com.orderon.AccessManager.Customer;
import com.orderon.AccessManager.CustomerReport;
import com.orderon.AccessManager.DailyDiscountReport;
import com.orderon.AccessManager.DailyOperationReport;
import com.orderon.AccessManager.DeliveryReport;
import com.orderon.AccessManager.Discount;
import com.orderon.AccessManager.Employee;
import com.orderon.AccessManager.EntityString;
import com.orderon.AccessManager.Expense;
import com.orderon.AccessManager.GrossSaleReport;
import com.orderon.AccessManager.HomeDelivery;
import com.orderon.AccessManager.Hotel;
import com.orderon.AccessManager.IncentiveReport;
import com.orderon.AccessManager.ItemWiseReport;
import com.orderon.AccessManager.KitchenDisplayOrders;
import com.orderon.AccessManager.KitchenStation;
import com.orderon.AccessManager.LoyaltyOffer;
import com.orderon.AccessManager.LoyaltySetting;
import com.orderon.AccessManager.MPNotification;
import com.orderon.AccessManager.MenuItem;
import com.orderon.AccessManager.MonthReport;
import com.orderon.AccessManager.Notification;
import com.orderon.AccessManager.OnlineOrder;
import com.orderon.AccessManager.Order;
import com.orderon.AccessManager.OrderAddOn;
import com.orderon.AccessManager.OrderItem;
import com.orderon.AccessManager.OrderSpecification;
import com.orderon.AccessManager.PaymentWiseSalesReport;
import com.orderon.AccessManager.Report;
import com.orderon.AccessManager.Reservation;
import com.orderon.AccessManager.ReturnedItemsReport;
import com.orderon.AccessManager.Schedule;
import com.orderon.AccessManager.ServerLog;
import com.orderon.AccessManager.ServiceLog;
import com.orderon.AccessManager.Specifications;
import com.orderon.AccessManager.Stock;
import com.orderon.AccessManager.SubCollection;
import com.orderon.AccessManager.Table;
import com.orderon.AccessManager.TableUsage;
import com.orderon.AccessManager.Tax;
import com.orderon.AccessManager.Tier;
import com.orderon.AccessManager.User;
import com.orderon.AccessManager.YearlyReport;
import com.orderon.Configurator.OrderPrinter;

@Path("/Services")
public class Services {

	// static Logger logger = Logger.getLogger(Services.class);
	private static final String api_version = "3.1";
	private static final String billStyle = "<html style='max-width:377px;'><head><style>p{margin: 0 0 10px;} .table-condensed>thead>tr>th, .table-condensed>tbody>tr>th, .table-condensed>tfoot>tr>th, .table-condensed>thead>tr>td,"
			+ " h1, h2, h3, h4, h5, h6, .h1, .h2, .h3, .h4, .h5, .h6 {font-family: inherit;font-weight: 500;line-height: 1.1;color: inherit;}"
			+ " .table-condensed>tbody>tr>td, .table-condensed>tfoot>tr>td {padding: 1px;} .centered{text-align: center;} .text-right{text-align: right;} .mt0{margin-top: 0px;} .mt5{margin-top: 5px;} .mt-20{margin-top: 20px;}"
			+ " .table {width: 100%;max-width: 100%;margin-bottom: 20px;}"
			+ " .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td,.table>tbody>tr>td, "
			+ " .table>tfoot>tr>td {border-top: 1px solid #ddd;}"
			+ " .table>thead>tr>th {vertical-align: bottom;border-bottom: 2px solid #ddd;} th {text-align: left;}"
			+ " h3, .h3 {font-size: 18px;} h6, .h6 {font-size: 12px;} .mb0{margin-bottom: 0px;} .mb10 {margin-bottom: 10px;} .mt-10 {margin-top: 10px;}"
			+ " .ml{margin-left: 5px;} .ml2{margin-left: 20px;} .ml25{margin-left: 5px;} .mr{margin-right: 20px;} .mr1{margin-right: 10px;}"
			+ " .table-xs>thead>tr>td, .table-xs>tbody>tr>td, .table-xs>tfoot>tr>td {padding-top: 1px; padding-bottom: 1px; padding-left: 5px; padding-right: 5px;}"
			+ " .table-condensed>thead>tr>th, .table-condensed>tbody>tr>th,.table-condensed>tfoot>tr>th, .table-condensed>thead>tr>td,"
			+ " .table-condensed>tbody>tr>td, .table-condensed>tfoot>tr>td,.table-xs>tfoot>tr>th, .table-xs>thead>tr>th, .table-xs>tbody>tr>th{padding: 5px;}" 
			+ " .pull-right {float: right !important;}"
			+ " .w1{width:50%} .w2{width:10%} .w3{width:20%}"
			+ " .billHeader {text-align: center; margin-top: 0px; margin-bottom: 0px;} .billHeader2 {text-align: center; margin-top: 5px; margin-bottom: 0px;}"
			+ " .addOn-td{border-top: none !important;padding-top: 5px !important;padding-bottom: 5px !important;line-height: 1 !important;font-size: 12px !important}"
			+ "</style></head><body style='max-width:377px; margin: 0px; font-family:Arial;'>";

	@GET
	@Path("/v1/heartbeat")
	@Produces(MediaType.APPLICATION_JSON)
	public String hearbeat() {
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("hotelId", Configurator.getHotelId());
			outObj.put("ip", Configurator.getIp());
			outObj.put("status", true);
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
			User user = dao.validUser(inObj.getString("hotelId"), inObj.getString("userId"), inObj.getString("passwd"));
			if (user != null) {
				outObj.put("status", true);
				outObj.put("fullName", user.getName());
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
	public String validateMPUser(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			byte[] auth = authString.getBytes();
			String[] decoded = Base64.decodeAsString(auth).split(":");
			String hotelId = inObj.getString("hotelId");
			Hotel hotel = dao.getHotelById(hotelId);
			//dao.initDatabase(hotelId);
			//dao.restaurantSetup(hotelId);
			
			//dao.updateServerStatus(inObj.getString("hotelId"), false);
			String authToken = dao.validMPUser(inObj.getString("hotelId"), decoded[0], decoded[1]);
			if (authToken == null) {
				outObj.put("status", false);
			} else {
				outObj.put("status", true);
				User user = dao.getUserById(hotelId, decoded[0]);
				outObj.put("fullName", user.getName());
				outObj.put("type", user.getUserType());
				outObj.put("authToken", user.getAuthToken());
				outObj.put("hotelType", hotel.getHotelType());
				while(!hotel.getVersion().equals(api_version)) {
					System.out.println("Updating database.");
					dao.updateDatabase(hotelId, hotel.getVersion(), api_version);
					hotel = dao.getHotelById(hotelId);
				}
			}
		} catch (Exception e) {
			dao.rollbackTransaction();
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
				outObj.put("fullName", user.getName());
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
			String auth = Base64.decodeAsString(inObj.getString("userPasswd").getBytes());
			
			outObj.put("status", dao.addUser(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("employeeId"), userType, auth));
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
				userDetails.put("fullName", user.getName());
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

			if (dao.itemExists(inObj.getString("hotelId"), inObj.getString("title")) != null) {
				outObj.put("error", "Item Exists");
			} else {
				String menuId = dao.addMenuItem(inObj.getString("hotelId"), inObj.getString("title"),
						inObj.getString("description"), inObj.getString("category"), inObj.getString("station"),
						inObj.getString("flags"), inObj.getInt("preparationTime"), new BigDecimal(Double.toString(inObj.getDouble("rate"))),
						new BigDecimal(Double.toString(inObj.getDouble("inhouseRate"))),new BigDecimal(Double.toString(inObj.getDouble("onlineRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("costPrice"))), 
						inObj.getInt("vegType"), inObj.getString("image"), inObj.getInt("isTaxable"), 
						inObj.getInt("incentiveType"), inObj.getInt("incentive"), inObj.getString("shortForm"));

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
							inObj.getString("flags"), inObj.getInt("preparationTime"), new BigDecimal(Double.toString(inObj.getDouble("rate"))),
							new BigDecimal(Double.toString(inObj.getDouble("inhouseRate"))), new BigDecimal(Double.toString(inObj.getDouble("onlineRate"))), 
							new BigDecimal(Double.toString(inObj.getDouble("costPrice"))), 
							inObj.getInt("vegType"), inObj.getString("image"), inObj.getInt("isTaxable"), 
							inObj.getInt("incentiveType"), inObj.getInt("incentive"), inObj.getString("shortForm")));
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
			inObj = new JSONObject(jsonObject);
			int userType = UserType.valueOf(inObj.getString("userType")).getValue();
			outObj = dao.updateUser(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("oldPassword"), inObj.getString("password"), userType);
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
				if(users.get(i).getUserType() == UserType.SECRET.getValue())
					continue;
				JSONObject obj = new JSONObject();
				obj.put("userId", users.get(i).getUserId());
				obj.put("userName", users.get(i).getName());
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
	@Path("/v1/getEmployeeForNC")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEmployeeForNC(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject employeeObj = null;
		try {
			ArrayList<Employee> employees = dao.getEmployeesForNC(hotelId);
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
	@Path("/v1/getHotelDetailsJSON")
	@Produces(MediaType.APPLICATION_JSON)
	public String getHotelDetail(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject tempObj = new JSONObject();
		try {
			Hotel hotel = dao.getHotelById(hotelId);
			outObj.put("hotelName", hotel.getHotelName());
			outObj.put("hotelId", hotel.getHotelId());
			outObj.put("hotelAddress", hotel.getHotelAddress());
			outObj.put("hotelContact", hotel.getHotelContact());
			outObj.put("gstNumber", hotel.getGstNumber());
			outObj.put("hotelType", hotel.getHotelType());
			outObj.put("hasKds", hotel.getHasKds());
			outObj.put("hasEod", hotel.getHasEod());
			outObj.put("hasDirectCheckout", hotel.getHasDirectCheckout());
			outObj.put("hasNC", hotel.getHasNC());
			outObj.put("hasBar", hotel.getHasBar());
			outObj.put("hasKot", hotel.getHasKot());
			outObj.put("hasLoyalty", hotel.getHasLoyalty());
			outObj.put("hotelDescription", hotel.getDescription());
			outObj.put("hotelWebsite", hotel.getWebsite());
			outObj.put("printMethod", hotel.getPrintMethod());
			outObj.put("isServer", Configurator.getIsServer());
			outObj.put("getCustomerDb", hotel.getLoadCustomerDb());
			outObj.put("allowCancelationOnPhone", hotel.getAllowItemCancellationOnPhone());
			
			for(int i=0; i<hotel.getIntegrations().length; i++) {
				tempObj = new JSONObject();
				tempObj.put("platform", hotel.getIntegrations()[i]);
				outArr.put(tempObj);
			}
			outObj.put("integrations", outArr);
			
			outArr = new JSONArray();
			for(int i=0; i<hotel.getOnlinePlatforms().length; i++) {
				tempObj = new JSONObject();
				tempObj.put("platform", hotel.getOnlinePlatforms()[i]);
				outArr.put(tempObj);
			}
			outObj.put("onlineOrderPlatforms", outArr);
			
			outArr = new JSONArray();
			for(int i=0; i<hotel.getSections().length; i++) {
				tempObj = new JSONObject();
				tempObj.put("section", hotel.getSections()[i]);
				outArr.put(tempObj);
			}
			outObj.put("sections", outArr);
			
			outObj.put("hasSms", hotel.getIsSmsEnabled());
			if (hotel.getIsServerEnabled() == 1) {
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
				tableDetails.put("tableIndex", i+1);
				tableDetails.put("orderId", tables.get(i).getOrderId());
				tableDetails.put("section", tables.get(i).getSection());
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
				tableDetails.put("section", tables.get(i).getSection());
				tablesArr.put(tableDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tablesArr.toString();
	}
	
	//-----------------------------Collection Apis

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
		ArrayList<Collection> collections = dao.getCollections(hotelId);
		for (int i = 0; i < collections.size(); i++) {
			collectionArr.put(collections.get(i).getName());
		}
		return collectionArr.toString();
	}

	@GET
	@Path("/v3/getCollections")
	@Produces(MediaType.APPLICATION_JSON)
	public String getActiveCollections(@QueryParam("hotelId") String hotelId, @QueryParam("getActiveOnly") Boolean getActiveOnly) {
		AccessManager dao = new AccessManager(false);
		JSONArray collectionArr = new JSONArray();
		JSONObject collectionObj = null;
		ArrayList<Collection> collections = null;
		if(getActiveOnly)
			collections = dao.getActiveCollections(hotelId);
		else
			collections = dao.getCollections(hotelId);
		try {
			for (int i = 0; i < collections.size(); i++) {
				collectionObj = new JSONObject();
				collectionObj.put("collectionId", collections.get(i).getId());
				collectionObj.put("collectionName", collections.get(i).getName());
				collectionObj.put("collectionOrder", collections.get(i).getCollectionOrder());
				collectionObj.put("hasSubCollection", collections.get(i).getHasSubCollection());
				collectionObj.put("schedule", dao.getSchedulesForCollection(hotelId, collections.get(i).getScheduleId()));
			}
			collectionArr.put(collectionObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return collectionArr.toString();
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

	//-----------------------------Schedule Apis
	
	@POST
	@Path("/v3/addSchedule")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addSchedule(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addSchedule(inObj.getString("hotelId"), inObj.getString("name"), inObj.getString("days"), inObj.getString("timeSlots"))) {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getSchedules")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSchedules(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray scheduleArr = new JSONArray();
		JSONObject scheduleObj = null;
		ArrayList<Schedule> schedules = dao.getSchedules(hotelId);
		try {
			for (int i = 0; i < schedules.size(); i++) {
				scheduleObj = new JSONObject();
				scheduleObj.put("scheduleId", schedules.get(i).getId());
				scheduleObj.put("scheduleName", schedules.get(i).getName());
				scheduleObj.put("days", schedules.get(i).getDays());
				scheduleObj.put("timeSlots", schedules.get(i).getTimeSlots());
			}
			scheduleArr.put(scheduleObj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return scheduleArr.toString();
	}

	@POST
	@Path("/v3/deleteSchedule")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteSchedule(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteSchedule(inObj.getString("hotelId"), inObj.getInt("scheduleId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	//-----------------------------SubCollection Apis
	
	@POST
	@Path("/v3/addSubCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addSubCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addSubCollection(inObj.getString("hotelId"), inObj.getString("name"), inObj.getInt("order"), inObj.getString("collection"))) {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getSubCollections")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSubCollections(@QueryParam("hotelId") String hotelId, @QueryParam("getActiveOnly") Boolean getActiveOnly) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject obj = null;
		ArrayList<SubCollection> subCollections = null;
		if(getActiveOnly)
			subCollections = dao.getActiveSubCollections(hotelId);
		else
			subCollections = dao.getSubCollections(hotelId);
		try {
			for (int i = 0; i < subCollections.size(); i++) {
				obj = new JSONObject();
				obj.put("subCollectionId", subCollections.get(i).getId());
				obj.put("subCollectionName", subCollections.get(i).getName());
				obj.put("collection", subCollections.get(i).getCollection());
				obj.put("order", subCollections.get(i).getSubCollectionOrder());
			}
			outArr.put(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outArr.toString();
	}

	@POST
	@Path("/v3/deleteSubCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteSubCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteSubCollection(inObj.getString("hotelId"), inObj.getInt("subCollectionId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	//-----------------------------Tier Apis

	@POST
	@Path("/v3/addTier")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addTier(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addTier(inObj.getString("hotelId"), new BigDecimal(Double.toString(inObj.getDouble("value"))), 
					inObj.getBoolean("chargeAlwaysApplicable"), new BigDecimal(Double.toString(inObj.getDouble("minBillAMount"))))) {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getTiers")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTiers(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject obj = null;
		ArrayList<Tier> tiers = dao.getTiers(hotelId);
		try {
			for (int i = 0; i < tiers.size(); i++) {
				obj = new JSONObject();
				obj.put("tierId", tiers.get(i).getId());
				obj.put("value", tiers.get(i).getValue());
				obj.put("isChargeAlwaysApplicable", tiers.get(i).getIsChargeAlwaysApplicable());
				obj.put("minBillAmount", tiers.get(i).getMinBillAmount());
			}
			outArr.put(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outArr.toString();
	}

	@POST
	@Path("/v3/deleteTier")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTier(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteTier(inObj.getString("hotelId"), inObj.getInt("tierId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	//-----------------------------Charges Apis

	@POST
	@Path("/v3/addCharge")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addCharge(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addCharge(inObj.getString("hotelId"), inObj.getString("name"), new BigDecimal(Double.toString(inObj.getDouble("value"))), 
					inObj.getString("type"), inObj.getBoolean("isActive"), inObj.getString("applicableOn"), inObj.getBoolean("isAlwaysApplicable"),
					 new BigDecimal(Double.toString(inObj.getDouble("minBillAmount"))), inObj.getBoolean("hasTierWiseValues"))) {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getCharges")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCharges(@QueryParam("hotelId") String hotelId, @QueryParam("getActiveOnly") Boolean getActiveOnly) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject obj = null;
		ArrayList<Charge> charges = null;
		if(getActiveOnly)
			charges = dao.getActiveCharges(hotelId);
		else
			charges = dao.getCharges(hotelId);
		try {
			for (int i = 0; i < charges.size(); i++) {
				obj = new JSONObject();
				obj.put("chargeId", charges.get(i).getId());
				obj.put("chargeName", charges.get(i).getName());
				obj.put("value", charges.get(i).getValue());
				obj.put("type", charges.get(i).getType());
				obj.put("minBillAmount", charges.get(i).getMinBillAmount());
				obj.put("applicableOn", charges.get(i).getApplicableOn());
				obj.put("taxesOnCharge", charges.get(i).getTaxesOnCharge());
			}
			outArr.put(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outArr.toString();
	}

	@POST
	@Path("/v3/deleteCharge")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteCharge(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteCharge(inObj.getString("hotelId"), inObj.getInt("chargeId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	//-----------------------------Taxes Apis
	
	@POST
	@Path("/v3/addTax")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addTax(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addTax(inObj.getString("hotelId"), inObj.getString("name"), new BigDecimal(Double.toString(inObj.getDouble("value"))),
					inObj.getString("type"), inObj.getBoolean("isActive"))) {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getTaxes")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTaxes(@QueryParam("hotelId") String hotelId, @QueryParam("getActiveOnly") Boolean getActiveOnly) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject obj = null;
		ArrayList<Tax> taxes = null;
		if(getActiveOnly)
			taxes = dao.getActiveTaxes(hotelId);
		else
			taxes = dao.getTaxes(hotelId);
		try {
			for (int i = 0; i < taxes.size(); i++) {
				obj = new JSONObject();
				obj.put("taxId", taxes.get(i).getId());
				obj.put("taxName", taxes.get(i).getName());
				obj.put("value", taxes.get(i).getValue());
				obj.put("type", taxes.get(i).getType());
			}
			outArr.put(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outArr.toString();
	}

	@POST
	@Path("/v3/deleteTax")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTax(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteTax(inObj.getString("hotelId"), inObj.getInt("taxId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
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

	private void printKOT(String hotelId, String orderId, Boolean isCheckKOT) {

		AccessManager dao = new AccessManager(false);
		Hotel hotel = dao.getHotelById(hotelId);
		Order order = dao.getOrderById(hotelId, orderId);
		OrderPrinter printer = Configurator.getPrinters();
		
		if (dao.getHotelById(hotelId).getHasKds())
			return;
		ArrayList<OrderItem> items = null;
		if(!isCheckKOT)
			items = dao.getOrderedItemsForKOT(hotelId, orderId);
		else {
			items = dao.getOrderedItemsForReprintKOT(hotelId, orderId);
			defineKOT(items, dao, "", printer.getCashier(), hotel, order,
					1, false, true);
			dao.updateKOTStatus(hotelId, orderId);
			return;
		}

		ArrayList<OrderItem> beverageItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> kitchenItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> barItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> outDoorItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> naBarItems = new ArrayList<OrderItem>();

		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getStation().equals("Beverage")) {
				beverageItems.add(items.get(i));
			} else if (items.get(i).getStation().equals("Kitchen")) {
				kitchenItems.add(items.get(i));
			} else if (items.get(i).getStation().equals("Bar")) {
				barItems.add(items.get(i));
			} else if (items.get(i).getStation().equals("Bar Non-Alcoholic")) {
				naBarItems.add(items.get(i));
			} else if (items.get(i).getStation().equals("Outdoor")) {
				outDoorItems.add(items.get(i));
				kitchenItems.add(items.get(i));
			}
		}

		int copies = hotel.getKOTCountInhouse();
		if (order.getInHouse() == AccessManager.HOME_DELIVERY)
			copies = hotel.getKOTCountHomeDelivery();
		else if(order.getInHouse() == AccessManager.TAKE_AWAY) {
			copies = hotel.getKOTCountTakeAway();
			if(order.getCustomerName() == "ZOMATO")
				copies = hotel.getKOTCountZomato();
			else if(order.getCustomerName() == "SWIGGY")
				copies = hotel.getKOTCountSwwigy();
			else if(order.getCustomerName() == "UBEREATS")
				copies = hotel.getKOTCountUberEats();
			else if(order.getCustomerName() == "FOODPANDA")
				copies = hotel.getKOTCountFoodPanda();
		}else if(order.getInHouse() == AccessManager.BAR)
			copies = hotel.getKOTCountBar();
		else if(order.getInHouse() == AccessManager.NON_CHARGEABLE)
			copies = hotel.getKOTCountNC();
		
		if (beverageItems.size() > 0) {
			defineKOT(beverageItems, dao, "BEVERAGE ORDER TAKING", printer.getBeverage(), hotel, order,
					copies, false, false);
		}
		if (kitchenItems.size() > 0) {
			defineKOT(kitchenItems, dao, "KITCHEN ORDER TAKING", printer.getKitchen(), hotel, order,
					copies, false, false);
		}
		if (barItems.size() > 0) {
			if (!hotelId.equals("sa0001"))
				defineKOT(barItems, dao, "BAR ORDER TAKING", printer.getBar(), hotel, order, copies,
						false, false);
		}
		if (naBarItems.size() > 0) {
			if (!hotelId.equals("sa0001"))
				defineKOT(naBarItems, dao, "BAR ORDER TAKING", printer.getBar(), hotel, order, copies,
						false, false);
		}
		if (outDoorItems.size() > 0) {
			defineKOT(outDoorItems, dao, "OUTDOOR", printer.getOutDoor(), hotel, order, 1, false, false);
		}

		if (hotel.getKOTCountSummary() == 1) {
			if (order.getInHouse() == 1)
				defineKOT(items, dao, "SUMMARY", printer.getSummary(), hotel, order, hotel.getKOTCountSummary(), false, false);
		}

		dao.updateKOTStatus(hotelId, orderId);
	}

	private void defineKOT(ArrayList<OrderItem> items, AccessManager dao, String title, String printerStation,
			Hotel hotel, Order order, int copies, boolean isCanceled, boolean isCheckKOT) {

		String out = "<h3 style='font-size:14px; margin-top:0px; margin-bottom:5px; text-align: center'>";

		if (title.equals("SUMMARY"))
			out += "ORDER TAKING SUMMARY";
		else if (isCanceled)
			out += "CANCELED KOT";
		else {
			if(isCheckKOT)
				out += "CHECK KOT: ";
			
			if (order.getInHouse() == AccessManager.INHOUSE)
				out += "TABLE ORDER";
			else if (order.getInHouse() == AccessManager.HOME_DELIVERY)
				out += "HOME DELIVERY";
			else if (order.getInHouse() == AccessManager.BAR)
				out += "BAR ORDER";
			else if (order.getInHouse() == AccessManager.NON_CHARGEABLE)
				out += "NC For " + order.getReference();
			else {
				if(order.getTakeAwayType() == OnlineOrderingPortals.SWIGGY.getValue())
					out += "SWIGGY ORDER: "+order.getReference();
				else if(order.getTakeAwayType() == OnlineOrderingPortals.ZOMATO.getValue())
					out += "ZOMATO ORDER: "+order.getReference();
				else if(order.getTakeAwayType() == OnlineOrderingPortals.FOODILOO.getValue())
					out += "FOODILOO ORDER: "+order.getReference();
				else if(order.getTakeAwayType() == OnlineOrderingPortals.FOODPANDA.getValue())
					out += "FOODPANDA ORDER: "+order.getReference();
				else if(order.getTakeAwayType() == OnlineOrderingPortals.UBEREATS.getValue())
					out += "UBEREATS ORDER: "+order.getReference();
				else if(order.getTakeAwayType() == OnlineOrderingPortals.ZOMATO.getValue())
					out += "ZOMATO ORDER: "+order.getReference();
				else
					out += "COUNTER PARCEL";
			}

			if (title.equals("OUTDOOR") && hotel.getHotelId().equals("sa0001"))
				out += " SANNIDHI KOT";
		}
		
		Table table = dao.getTableById(hotel.getHotelId(), order.getTableId().split(",")[0]);

		out += "</h3><table style='width: 90%; padding: 0px; margin-left:25px; margin-bottom:0px; margin-top:0px;'><thead>"
				+ "<tr style='padding: 0px; margin-top:3px; margin-bottom:0px;'>"
				+ "<th style='padding:0px;'>Date</th><th style='padding:0px;'>Time</th><th style='padding:0px;'>Table</th>"
				+ "<th style='padding:0px;'>Waiter</th><th style='padding:0px;'>Pax</th><th style='padding:0px;'>Bill No.</th></tr>"
				+ "</thead><tbody>";

		out += "<tr style='padding: 0px; margin:0px;'><td style='padding:0px;'>" + items.get(0).getSubOrderDate().substring(8, 10)
				+ items.get(0).getSubOrderDate().substring(4, 7) + "/" + items.get(0).getSubOrderDate().substring(0, 4)
				+ "</td>" + "<td style='padding:0px;'>" + items.get(0).getSubOrderDate().substring(11, 16) + "</td>";
		String name = "";
		if (table != null)
			name = table.getWaiterId();

		out += "<td style='padding:0px;'>" + order.getTableId() + "</td><td style='padding:0px;'>" + name 
				+ "</td><td style='padding:0px;'>" + order.getNumberOfGuests() 
				+ "</td><td style='padding:0px;'>" + order.getBillNo() + "</td></tr>"
				+ "</tbody></table>"
				+ "<p style='margin:0px;'>------------------------------------------------------------------------------------------</p>"
				+ "<table class='table-condensed' style='width: 90%; padding: 0px; margin-left: 20px; margin-bottom:0px;>";
		
		if (isCanceled)
			out += "<thead style='font-size:12px;'><tr style='padding:0px; margin:0px;'><th style='padding:0px;'><u>QTY</u></th><th class='pull-left' style='width:75%; padding:0px;'>"
					+ "<u>DESCRIPTION</u></th></tr></thead><tbody>";
		else
			out += "<thead style='font-size:12px;'><tr style='padding:0px; margin:0px;'><th style='padding:0px;'><u>QTY</u></th><th class='pull-left' style='width:50%; padding:0px;'>"
					+ "<u>DESCRIPTION</u></th><th class='pull-right' style='padding:0px;'><u>SPECS</u></th></tr>"
					+ "</thead><tbody>";

		ArrayList<OrderSpecification> specifications = new ArrayList<OrderSpecification>();
		ArrayList<OrderAddOn> addOns = new ArrayList<OrderAddOn>();
		int newQuantity = 1;
		int currentQuantity = 0;
		boolean isComplete = true;
		String fontWeight = "";
		String kotFontSize = hotel.getKotFontSize();
		String kotFontWeight = hotel.getKotFontWeight();
		boolean isSmall = false;
		if(hotel.getHotelId().equals("ka0001") || hotel.getHotelId().equals("jp0001"))
				isSmall = true;

		for (int i = 0; i < items.size(); i++) {
			String spec = "";
			newQuantity = items.get(i).getQty();
			isComplete = true;
			currentQuantity = 0;
			for (int j = 1; j <= newQuantity; j++) {
				/*if(isCheckKOT)
					specifications = dao.getOrderedSpecification(hotelId, items.get(i).getOrderId(), items.get(i).getMenuId());
				else*/
				specifications = dao.getOrderedSpecification(hotel.getHotelId(), items.get(i).getOrderId(),
							items.get(i).getMenuId(), items.get(i).getSubOrderId(), j);
				if (isCanceled)
					addOns = dao.getCanceledOrderedAddOns(hotel.getHotelId(), items.get(i).getOrderId(),
							items.get(i).getSubOrderId(), items.get(i).getMenuId(), j);
				else {
					/*if(isCheckKOT)
						addOns = dao.getOrderedAddOns(hotelId, items.get(i).getOrderId(), items.get(i).getMenuId());
					else*/
					addOns = dao.getOrderedAddOns(hotel.getHotelId(), items.get(i).getOrderId(), items.get(i).getSubOrderId(),
								items.get(i).getMenuId(), false);
				}

				for (int k = 0; k < specifications.size(); k++) {
					spec += specifications.get(k).getSpecification() + ", ";
				}
				//remove dangling comma
				spec = spec.length() > 0 ? spec.substring(0, spec.length() - 2) : spec;

				if (specifications.size() == 0 && addOns.size() == 0) {
					currentQuantity++;
					isComplete = false;
					if (j < newQuantity)
						continue;
				} else if (!isComplete) {
					fontWeight = currentQuantity == 5 ? "font-weight:regular;" : "font-weight:"+kotFontWeight+";";
					
					out += "<tr style='padding: 0px; margin-top:0px; margin-bottom:0px;'>"
						+ "<td class='pull-left' style='padding:0px; " + fontWeight + "'>" + currentQuantity
						+ "</td>" + "<td class='pull-left' style='padding:0px; letter-spacing:1px !important; font-size:"+kotFontSize+"; font-weight: "+kotFontWeight+";'>" + formatTitle(items.get(i).getTitle(), isSmall)
						+ "</td><td class='pull-right' style='padding:0px; font-size:"+kotFontSize+"; font-weight: "+kotFontWeight+"; border-bottom:1px dashed black;'>"+items.get(i).getSpecifications()+"</td></tr>";
					
					isComplete = true;
					currentQuantity = 1;
				} else
					currentQuantity++;

				fontWeight = currentQuantity == 5 ? "font-weight:regular;" : "font-weight:"+kotFontWeight+";";
				out += "<tr style='padding: 0px; margin-top:0px; margin-bottom:0px;'>"
					+ "<td class='pull-left' style='padding:0px; " + fontWeight + "'>" + currentQuantity
					+ "</td>" + "<td class='pull-left' style='padding:0px; letter-spacing:1px !important; font-size:"+kotFontSize+"; font-weight: "+kotFontWeight+";'>" + formatTitle(items.get(i).getTitle(), isSmall);
				
				if (!isCanceled) {
					spec = items.get(i).getSpecifications() + spec;
					out += "</td><td class='pull-right' style='padding:0px; font-size:"+kotFontSize+"; font-weight: "+kotFontWeight+"; border-bottom:1px dashed black;'>" + spec + "</td></tr>";
				}else
					out += "</td></tr>";

				for (int k = 0; k < addOns.size(); k++) {
					fontWeight = currentQuantity == 5 ? "font-weight:regular;" : "font-weight:"+kotFontWeight+";";
					out += "<tr style='padding: 0px; margin-top:0px; margin-bottom:0px;'>"
						+ "<td class='pull-left' style='border-top: none !important; padding: 0px !important; line-height: 1 !important;  font-size:"+kotFontSize+"; " + fontWeight + "'>"
						+ addOns.get(k).getQty() + "</td>"
						+ "<td class='pull-left' style='padding-left:50px !important; border-top: none !important; padding: 0px !important; font-size: "+kotFontSize+"; font-weight: "+kotFontWeight+";'>+ "
						+ formatTitle(addOns.get(k).getName(), true);
					
					if (!isCanceled)
						out += "</td><td></td></tr>";
					else
						out += "</td></tr>";
				}
				spec = "";
				currentQuantity = 0;
			}
		}

		out += "</tbody></table>"
				+ "<p style='margin-left:0px; margin-right:0px; margin-top:0px'>------------------------------------------------------------------------------------------</p>";

		String html = "<html><head><style>.table-condensed>thead>tr>th, .table-condensed>tbody>tr>th,"
				+ ".table-condensed>tfoot>tr>th, .table-condensed>thead>tr>td,"
				+ ".table-condensed>tbody>tr>td, .table-condensed>tfoot>tr>td {"
				+ "padding: 1px;} .pull-right{text-align: right} .pull-left{text-align: left} .table {width: 100%;}"
				+ ".table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td,.table>tbody>tr>td, "
				+ ".table>tfoot>tr>td {padding: 0px;} .table>thead>tr>th {vertical-align: bottom} th {text-align: left;}"
				+ " h3, .h3 {font-size: 18px;}"
				+ "</style></head><body style='width: 377px; font-family:" + hotel.getKotFontFamily() + ";'>" + out
				+ "</body></html>";

		print(html, printerStation, copies, hotel);
	}

	@GET
	@Path("/v1/getOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOrder(@QueryParam("hotelId") String hotelId, @QueryParam("orderId") String orderId,
			@QueryParam("showConsolidated") Boolean showConsolidated,
			@QueryParam("showComplimentary") Boolean showComplimentary,
			@QueryParam("showReturned") Boolean showReturned) {
		AccessManager dao = new AccessManager(false);
		showReturned = showReturned==null?false:showReturned;
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
		ArrayList<OrderAddOn> addOns = null;
		ArrayList<OrderSpecification> specifications = new ArrayList<OrderSpecification>();
		Order order = dao.getOrderById(hotelId, orderId);
		if (showConsolidated) {
			orderItems = dao.getOrderedItemForBill(hotelId, orderId);
			orderItems.addAll(dao.getOrderedItemForBillCI(hotelId, orderId));
			if (showComplimentary)
				orderItems.addAll(dao.getComplimentaryOrderedItemForBill(hotelId, orderId));
		} else
			orderItems = dao.getOrderedItems(hotelId, orderId);

		BigDecimal complimentaryTotal = new BigDecimal("0.0");
		BigDecimal rate = new BigDecimal("0.0");
		try {
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
				itemDetails = new JSONObject();
				addOnArr = new JSONArray();
				specArr = new JSONArray();
				rate = orderItems.get(i).getRate();
				if (orderItems.get(i).getState() == 50) {
					rate = new BigDecimal("0.0");
					complimentaryTotal.add(orderItems.get(i).getRate());
				}
				if(orderItems.get(i).getState() == 100) 
					itemDetails.put("reason", orderItems.get(i).getReason());
				else
					itemDetails.put("reason", "");
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
				if(showConsolidated) {
					addOns = dao.getOrderedAddOns(hotelId, orderId, orderItems.get(i).getMenuId(), showReturned);
					for (int k = 0; k < addOns.size(); k++) {
						addOnDetails = new JSONObject();
						rate = addOns.get(k).getRate();
						if (addOns.get(k).getState() == 50) {
							if(!showComplimentary)
								continue;
							rate = new BigDecimal("0");
							complimentaryTotal.add(addOns.get(k).getRate());
						}
						addOnDetails.put("addOnId", addOns.get(k).getAddOnId());
						addOnDetails.put("name", addOns.get(k).getName());
						addOnDetails.put("itemId", addOns.get(k).getItemId());
						addOnDetails.put("quantity", addOns.get(k).getQty());
						addOnDetails.put("state", addOns.get(k).getState());
						addOnDetails.put("rate", rate);
						addOnArr.put(addOnDetails);
					}
					specifications = dao.getOrderedSpecification(hotelId, orderId, orderItems.get(i).getMenuId());
					for (int k = 0; k < specifications.size(); k++) {
						specDetails = new JSONObject();
						specs += specifications.get(k).getSpecification() + ", ";
						specDetails.put("spec", specifications.get(k).getSpecification());
						specDetails.put("itemId", specifications.get(k).getItemId());
						specArr.put(specDetails);
					}
				}else {
					for (int j = 0; j < orderItems.get(i).getQty(); j++) {
						addOns = dao.getOrderedAddOns(hotelId, orderId, orderItems.get(i).getSubOrderId(), orderItems.get(i).getMenuId(), true);
						for (int k = 0; k < addOns.size(); k++) {
							addOnDetails = new JSONObject();
							rate = addOns.get(k).getRate();
							if (addOns.get(k).getState() == 50) {
								if(!showComplimentary)
									continue;
								rate = new BigDecimal("0");
								complimentaryTotal.add(addOns.get(k).getRate());
							}
							addOnDetails.put("addOnId", addOns.get(k).getAddOnId());
							addOnDetails.put("name", addOns.get(k).getName());
							addOnDetails.put("itemId", addOns.get(k).getItemId());
							addOnDetails.put("quantity", addOns.get(k).getQty());
							addOnDetails.put("state", addOns.get(k).getState());
							addOnDetails.put("rate", rate);
							addOnArr.put(addOnDetails);
						}
						specifications = dao.getOrderedSpecification(hotelId, orderId, orderItems.get(i).getMenuId(),
								orderItems.get(i).getSubOrderId(), (j+1));
						for (int k = 0; k < specifications.size(); k++) {
							specDetails = new JSONObject();
							specs += specifications.get(k).getSpecification() + ", ";
							specDetails.put("spec", specifications.get(k).getSpecification());
							specDetails.put("itemId", specifications.get(k).getItemId());
							specArr.put(specDetails);
						}
					}
				}
				specs = specs.length() == 0 ? orderItems.get(i).getSpecifications()
						: specs + ", " + orderItems.get(i).getSpecifications();
				itemDetails.put("specs", specs.replace(", , ", ", "));
				itemDetails.put("allSpecs", orderItems.get(i).getSpecifications());
				itemDetails.put("specArr", specArr);
				itemDetails.put("addOns", addOnArr);
				itemsArr.put(itemDetails);
			}
			if (!order.getTableId().equals(null)) {
				String tableId = order.getTableId();
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
				orderObj.put("points", customer.getPoints());
				orderObj.put("userType", customer.getUserType());
				orderObj.put("pointToRupee", dao.getLoyaltySettingByUserType(hotelId, customer.getUserType()).getPointToRupee());
				orderObj.put("loyaltyId", order.getLoyaltyId());
				orderObj.put("pointsUsed", order.getLoyaltyPaid());
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
			orderObj.put("takeAwayType", order.getTakeAwayType());
			orderObj.put("hasTakenReview", order.hasTakenReview());
			orderObj.put("portal", OnlineOrderingPortals.getType(order.getTakeAwayType()).toString());
			orderObj.put("orderId", order.getOrderId());
			orderObj.put("billNo", order.getBillNo());
			orderObj.put("state", order.getState());
			orderObj.put("reason", order.getReason());
			orderObj.put("reference", order.getReference());
			orderObj.put("authId", order.getAuthId());
			orderObj.put("discountCode", order.getDiscountCode());
			orderObj.put("remarks", order.getRemarks());
			orderObj.put("tableNumber",
					order.getTableId().length() == 3 || order.getTableId().length() == 2
							? order.getTableId().replace(",", "")
							: order.getTableId());
			orderObj.put("printCount", order.getPrintCount());
			orderObj.put("compTotal", complimentaryTotal);
			if(orderItems.size()>0)
				orderObj.put("logTime", orderItems.get(0).getLogTime());
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
			@QueryParam("query") String query, @QueryParam("mobileNo") String mobileNo, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;
		int orderState = 0;
		ArrayList<Order> order;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		if (dateFilter != null && dateFilter.length() > 0) {
			dateFilter = dateFilter.substring(6, 10) + "/" + dateFilter.substring(3, 6) + dateFilter.substring(0, 2);
			order = dao.getAllOrders(hotelId, dateFilter, query, visible);
		}else if(mobileNo != null && mobileNo.length() > 0){
			order = dao.getOrdersOfOneCustomer(hotelId, mobileNo);
		} else {
			order = dao.getAllOrders(hotelId, dateFilter, query, visible);
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
				itemDetails.put("amountRecieved", order.get(i).getTotalPayment());
				itemDetails.put("paymentType", order.get(i).getPaymentType());
				itemDetails.put("inhouse", order.get(i).getInHouse());
				itemDetails.put("takeAwayType", order.get(i).getTakeAwayType());
				itemDetails.put("state", orderState);
				itemDetails.put("foodBill", order.get(i).getFoodBill());
				itemDetails.put("barBill", order.get(i).getBarBill());
				itemDetails.put("waiterId", order.get(i).getWaiterId());
				itemDetails.put("section", order.get(i).getSection());
				itemDetails.put("deliveryBoy", order.get(i).getFirstName());
				itemDetails.put("deliveryTime", order.get(i).getDeliveryTime());
				itemDetails.put("discount", orderState == 99 ? 0 : (order.get(i).getFoodDiscount().add(order.get(i).getBarDiscount())));
				if(visible)
					itemDetails.put("billNo", order.get(i).getBillNo2());
				else
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
			JSONArray orderAddOns = inObj.getJSONArray("addOns");
			String orderId = inObj.getString("orderId");
			String hotelId = inObj.getString("hotelId");
			Order order = dao.getOrderById(hotelId, orderId);
			int newQuantity = 0;
			int returnQuantity = 0;
			ArrayList<OrderItem> beverageItems = new ArrayList<OrderItem>();
			ArrayList<OrderItem> naBarItems = new ArrayList<OrderItem>();
			ArrayList<OrderItem> kitchenItems = new ArrayList<OrderItem>();
			ArrayList<OrderItem> barItems = new ArrayList<OrderItem>();
			ArrayList<OrderItem> outDoorItems = new ArrayList<OrderItem>();
			OrderItem item = new OrderItem();
			OrderAddOn addOn = new OrderAddOn();
			boolean status = false;
			OrderPrinter printer = Configurator.getPrinters();
			JSONObject orderedItems = null;
			ArrayList<OrderAddOn> orderedAddons = null;

			for (int i = 0; i < orderItems.length(); i++) {
				orderedItems = orderItems.getJSONObject(i);
				
				String type = orderedItems.getString("type");
				newQuantity = orderedItems.getInt("qty") - orderedItems.getInt("returnQty");
				returnQuantity = orderedItems.getInt("returnQty");
				if(newQuantity < 0) {
					outObj.put("message", "Return quantity cannot be more than Ordered Quantity. Please contact OrderOn support.");
					dao.rollbackTransaction();
					return outObj.toString();
				}

				if (type.equals("return")) {
					status = dao.updateOrderItemLog(hotelId, orderId,
							orderedItems.getString("subOrderId"),
							orderedItems.getString("menuId"),
							orderedItems.getString("reason"), type, returnQuantity,
							new BigDecimal(Double.toString(orderedItems.getDouble("rate"))), 
							orderedItems.getInt("qty"));

				} else if (type.equals("cancel")) {
					status = dao.updateOrderItemLog(hotelId, orderId,
							orderedItems.getString("subOrderId"),
							orderedItems.getString("menuId"),
							orderedItems.getString("reason"), type,
							orderedItems.getInt("qty"), 
							new BigDecimal(Double.toString(orderedItems.getDouble("rate"))),
							orderedItems.getInt("qty"));
				} else {
					newQuantity = 0;
					status = dao.updateOrderItemLog(hotelId, orderId,
							orderedItems.getString("subOrderId"),
							orderedItems.getString("menuId"),
							orderedItems.getString("reason"), type,
							orderedItems.getInt("qty"), 
							new BigDecimal(Double.toString(orderedItems.getDouble("rate"))),
							orderedItems.getInt("qty"));
				}
				if (!status) {
					outObj.put("message", "OrderItemLog could not be updated. Please contact OrderOn support.");
					dao.rollbackTransaction();
					return outObj.toString();
				}

				item = dao.getOrderedItem(hotelId, orderId, orderedItems.getString("subOrderId"),
						orderedItems.getString("menuId"));
				item.setQuantity(returnQuantity);

				boolean isRemoved = dao.removeSubOrder(hotelId, orderId,
						orderedItems.getString("subOrderId"),
						orderedItems.getString("menuId"), newQuantity);
				if (item.getStation().equals("Beverage")) {
					beverageItems.add(item);
				} else if (item.getStation().equals("Kitchen")) {
					kitchenItems.add(item);
				} else if (item.getStation().equals("Bar")) {
					barItems.add(item);
				} else if (item.getStation().equals("Outdoor")) {
					outDoorItems.add(item);
				} else if (item.getStation().equals("Bar Non-Alcoholic")) {
					naBarItems.add(item);
				}
				if (isRemoved) {
					dao.updateFoodBill(hotelId, orderId, orderedItems.getString("menuId"), newQuantity,
							true, item.getRate());
					orderedAddons = dao.getOrderedAddOns(hotelId, orderId, orderedItems.getString("subOrderId"), orderedItems.getString("menuId"), false);
					OrderAddOn orderedAddon = null;
					for (int x=0; x<orderedAddons.size(); x++) {
						orderedAddon = orderedAddons.get(x);
						dao.updateOrderAddOnLog(hotelId, orderId, orderedItems.getString("subOrderId"),
								orderedItems.getString("subOrderDate"), orderedItems.getString("menuId"),orderedAddon.getItemId(), type,
								orderedAddon.getQty(), orderedAddon.getRate(), orderedAddon.getAddOnId());
						dao.updateFoodBillAddOnReturn(hotelId, orderId, orderedItems.getString("subOrderId"),
								orderedItems.getString("menuId"),orderedAddon.getItemId(), orderedAddon.getAddOnId());
						dao.removeAddOn(hotelId, orderId, orderedItems.getString("subOrderId"),
								orderedItems.getString("menuId"), newQuantity, orderedAddon.getItemId(), orderedAddon.getAddOnId());
					}
					outObj.put("isTaxable",
							dao.isTaxableMenuItem(hotelId, orderedItems.getString("menuId")));
				} else {
					outObj.put("message", "Item could not be deleted. Please contact OrderOn support.");
					dao.rollbackTransaction();
					return outObj.toString();
				}
				if (type.equals("return")) {
					String subject = "Item Returned from Bill No. " + orderedItems.getString("billNo");
					String text = "<div style='width:350px; ' class='alert alert-warning'><h3>An Item has been returned from Order. Bill No. "
							+ orderedItems.getString("billNo") + ".</h3><p> Details as follows:</p>"
							+ "<div>Item Name: " + orderedItems.getString("title").replace("$", " ")
							+ "</div><div>Price : " + orderedItems.getInt("rate")
							+ "</div><div>Time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)
							+ "</div><div>Order punched by: " + orderId.split(":")[0] + "</div><div>Item Returned by : "
							+ inObj.getString("userId") + "</div><div>Reason: "
							+ orderedItems.getString("reason") + "</div></div>";
					status = netIsAvailable()?SendEmailAndSMS(hotelId, subject, text, "", "", Designation.OWNER):false;
					if (!status) {
						outObj.put("message", "Order Returned. Email could not be sent. Please check your internet connection");
					}
				}
			}
			if(orderItems.length() == 0) {
				JSONObject orderedAddOns = null;
				for (int i = 0; i < orderAddOns.length(); i++) {
					orderedAddOns = orderAddOns.getJSONObject(i);
					
					String type = orderedAddOns.getString("type");
					newQuantity = orderedAddOns.getInt("qty") - orderedAddOns.getInt("returnQty");
					returnQuantity = orderedAddOns.getInt("returnQty");
					if(newQuantity < 0) {
						outObj.put("message", "Return quantity cannot be more than Ordered Quantity. Please contact OrderOn support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}
	
					if (type.equals("return")) {
						status = dao.updateOrderAddOnLog(hotelId, orderId, 
								orderedAddOns.getString("subOrderId"),
								orderedAddOns.getString("subOrderDate"), 
								orderedAddOns.getString("menuId"),
								orderedAddOns.getInt("itemId"), type,
								returnQuantity, 
								new BigDecimal(Double.toString(orderedAddOns.getDouble("rate"))), 
								orderedAddOns.getInt("addOnId"));
	
					} else if (type.equals("cancel")) {
						status = dao.updateOrderAddOnLog(hotelId, orderId, 
								orderedAddOns.getString("subOrderId"),
								orderedAddOns.getString("subOrderDate"), 
								orderedAddOns.getString("menuId"),
								orderedAddOns.getInt("itemId"), type,
								orderedAddOns.getInt("qty"), 
								new BigDecimal(Double.toString(orderedAddOns.getDouble("rate"))), 
								orderedAddOns.getInt("addOnId"));
					} else {
						newQuantity = 0;
						status = dao.updateOrderAddOnLog(hotelId, orderId, 
								orderedAddOns.getString("subOrderId"),
								orderedAddOns.getString("subOrderDate"), 
								orderedAddOns.getString("menuId"),
								orderedAddOns.getInt("itemId"), type,
								orderedAddOns.getInt("qty"), 
								new BigDecimal(Double.toString(orderedAddOns.getDouble("rate"))), 
								orderedAddOns.getInt("addOnId"));
					}
					if (!status) {
						outObj.put("message", "OrderAddOnLog could not be updated. Please contact OrderOn support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}
	
					addOn = dao.getOrderedAddOnById(hotelId, orderId, orderedAddOns.getString("subOrderId"), 
							orderedAddOns.getString("menuId"),
							orderedAddOns.getInt("itemId"),
							orderedAddOns.getInt("addOnId"));
					addOn.setQty(returnQuantity);
	
					dao.removeAddOn(hotelId, orderId, orderedAddOns.getString("subOrderId"),orderedAddOns.getString("menuId"),
							newQuantity,orderedAddOns.getInt("addOnId"), orderedAddOns.getInt("itemId"));
				}
			}
			Hotel hotel = dao.getHotelById(hotelId);
			if (beverageItems.size() > 0) {
				defineKOT(beverageItems, dao, "BEVERAGE ORDER TAKING", printer.getBeverage(), hotel,
						order, 1, true, false);
			}
			if (kitchenItems.size() > 0) {
				defineKOT(kitchenItems, dao, "KITCHEN ORDER TAKING", printer.getKitchen(), hotel, order,
						1, true, false);
			}
			if (barItems.size() > 0) {
				defineKOT(barItems, dao, "BAR ORDER TAKING", printer.getBar(), hotel, order, 1, true, false);
			}
			if (outDoorItems.size() > 0) {
				defineKOT(outDoorItems, dao, "OUTDOOR", printer.getOutDoor(), hotel, order, 1, true, false);
			}
			if (naBarItems.size() > 0) {
				defineKOT(naBarItems, dao, "BAR ORDER TAKING", printer.getBar(), hotel, order, 1, true, false);
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
			String hotelId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			String billFormat = inObj.getString("billFormat");
			Order order = dao.getOrderById(hotelId, orderId);
			if(order.getState()==AccessManager.ORDER_STATE_BILLING || order.getState()==AccessManager.ORDER_STATE_COMPLETE) {
				outObj.put("status", dao.updateOrderPrintCount(hotelId, orderId));
				outObj.put("billSize", Configurator.getBillSize());
				outObj.put("billFont", Configurator.getBillFont());
				if(!billFormat.equals("")){
					billFormat = billStyle + billFormat + "</body></html>";
					this.print(billFormat, "Cashier", 1, dao.getHotelById(hotelId));
				}
				if(order.getInHouse()==AccessManager.HOME_DELIVERY) {
					dao.updateDeliveryTime(hotelId, orderId);
				}
			}else {
				outObj.put("status", false);
				outObj.put("message", "Please Checkout order before printing bill.");
			}
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
			inObj.getString("authId"), inObj.getString("section"));
			if (outObj.getBoolean("status")) {
				if(netIsAvailable()) {
					String subject = "Bill No. " + billNo + " marked void!";
					String text = "<div style='width:300px; '><div class='alert alert-warning'><b>Bill No. " + billNo
							+ " </b>has been marked void.</h3><p> Details as follows:</p>" + "<div>Time: "
							+ LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + "</div><div>Order punched by: "
							+ orderId.split(":")[0] + "</div><div>Authorizer: " + inObj.getString("authId")
							+ "</div><div>Reason: " + inObj.getString("reason") + "</div></div>";
					
					String smsText = "Order Marked Void. BillNo: "+billNo+", UserName: "+orderId.split(":")[0]
							+" , Reason: "+inObj.getString("reason")+" , Authorizer: " +inObj.getString("authId") + ".";
				
					SendEmailAndSMS(inObj.getString("hotelId"), subject, text, smsText, "", Designation.OWNER);
				}
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
				if(netIsAvailable())
					SendEmailAndSMS(inObj.getString("hotelId"), subject, text, "", "", Designation.OWNER);
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
							new BigDecimal(Double.toString(compItems.getJSONObject(i).getDouble("rate"))), 
							compItems.getJSONObject(i).getInt("qty"), "Complimentary")) {
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
							compItems.getJSONObject(i).getString("subOrderDate"),
							compAddons.getJSONObject(j).getInt("itemId"), 
							new BigDecimal(Double.toString(compAddons.getJSONObject(j).getDouble("rate"))),
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
		ArrayList<Order> orderItems = dao.getCompletedOrders(hotelId);
		try {
			for (int i = 0; i < orderItems.size(); i++) {
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
				itemDetails.put("covers", orderItems.get(i).getNumberOfGuests());
				itemDetails.put("inhouse", orderItems.get(i).getInHouse());
				itemDetails.put("foodBill", orderItems.get(i).getFoodBill());
				itemDetails.put("barBill", orderItems.get(i).getBarBill());
				itemDetails.put("billNo", orderItems.get(i).getBillNo());
				itemDetails.put("orderNo", orderItems.get(i).getOrderNumber());
				itemDetails.put("section", orderItems.get(i).getSection());
				itemDetails.put("remarks", orderItems.get(i).getRemarks()==null?"":orderItems.get(i).getRemarks());
				itemDetails.put("reference", orderItems.get(i).getReference()==null?"":orderItems.get(i).getReference());
				itemsArr.put(itemDetails);
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
		int newQuantity = 0;
		int currentQuantity = 0;
		boolean hasAddedLast = false;
		try {
			for (int i = 0; i < orderItems.size(); i++) {
				orderId = orderItems.get(i).getOrderId();
				newQuantity = orderItems.get(i).getQty();
				currentQuantity = 0;
				hasAddedLast = false;
				for (int x = 1; x <= newQuantity; x++) {
					addOns = dao.getOrderedAddOns(hotelId, orderId, orderItems.get(i).getSubOrderId(),
							orderItems.get(i).getMenuId(), x, false);
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
				itemDetails.put("onlineRate", orderItems.get(i).getOnlineRate());
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
						addOnDetails.put("onlineRate", addOn.getOnlineRate());
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
	@Path("/v1/getAddOns")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAddons(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray addOnArr = new JSONArray();
		ArrayList<AddOn> addOns = dao.getAddOns(hotelId);
		JSONObject addOnObj = new JSONObject();
		try {
			for (int i = 0; i < addOns.size(); i++) {
				addOnObj = new JSONObject();
				addOnObj.put("id", addOns.get(i).getId());
				addOnObj.put("name", addOns.get(i).getName());
				addOnObj.put("inHouseRate", addOns.get(i).getInHouseRate());
				addOnObj.put("deliveryRate", addOns.get(i).getDeliveryRate());
				addOnObj.put("onlineRate", addOns.get(i).getOnlineRate());
				addOnArr.put(addOnObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addOnArr.toString();
	}
	
	@GET
	@Path("/v2/getMenu")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuv2(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();
		ArrayList<MenuItem> orderItems = dao.getMenu(hotelId);
		ArrayList<AddOn> addOns = dao.getAddOns(hotelId);
		ArrayList<Specifications> specs = dao.getSpecifications(hotelId);
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
				itemDetails.put("onlineRate", orderItems.get(i).getOnlineRate());
				itemDetails.put("image", orderItems.get(i).getImage());
				itemDetails.put("costPrice", orderItems.get(i).getCostPrice());
				itemDetails.put("state", orderItems.get(i).getState());
				itemDetails.put("isTaxable", orderItems.get(i).getIsTaxable());
				itemDetails.put("shortForm", orderItems.get(i).getShortForm());
				itemDetails.put("addOns", orderItems.get(i).getAddOnString());
				itemDetails.put("hasAddOns", orderItems.get(i).getAddOnString().length()==0?false:true);
				itemsArr.put(itemDetails);
			}
			outObj.put("menu", itemsArr);
			itemsArr = new JSONArray();
			for (int i = 0; i < addOns.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("sr", i);
				itemDetails.put("id", addOns.get(i).getId());
				itemDetails.put("name", addOns.get(i).getName());
				itemDetails.put("inHouseRate", addOns.get(i).getInHouseRate());
				itemDetails.put("onlineRate", addOns.get(i).getOnlineRate());
				itemDetails.put("deliveryRate", addOns.get(i).getDeliveryRate());
				itemsArr.put(itemDetails);
			}
			outObj.put("addOns", itemsArr);
			itemsArr = new JSONArray();
			for (int i = 0; i < specs.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("specification", specs.get(i).getSpecification());
				itemDetails.put("category", specs.get(i).getCategory());
				itemDetails.put("type", specs.get(i).getType());
				itemsArr.put(itemDetails);
			}
			outObj.put("specifications", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
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
				arrObj.put("category", specs.get(i).getCategory());
				arrObj.put("type", specs.get(i).getType());
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
			itemDetails.put("onlineRate", orderItems.getOnlineRate());
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
			outObj.put("status", -1);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			
			JSONArray tablesArr = inObj.getJSONArray("tableIds");
			String[] tableIds = new String[tablesArr.length()];
			for (int i = 0; i < tableIds.length; i++) {
				tableIds[i] = tablesArr.getString(i);
			}
			outObj = dao.newOrder(inObj.getString("hotelId"), inObj.getString("userId"), tableIds,
					inObj.getInt("peopleCount"), inObj.getString("customer"), inObj.getString("contactNumber"), inObj.has("customerAddress")?inObj.getString("customerAddress"):"",
					inObj.getString("allergyInfo"), inObj.has("section")?inObj.getString("section"):"");
			dao.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/newOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String newOrder(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", -1);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			
			String decoded = Base64.decodeAsString(authString.getBytes());
			String[] auth = decoded.split(":");
			 
			if(dao.validateToken(inObj.getString("hotelId"), auth[0], auth[1]) == (UserType.UNAUTHORIZED)) {
				outObj.put("status", -2);
				outObj.put("message", "You session  has timed out. Please login again to continue.");
				return outObj.toString();
			}
			JSONArray tablesArr = inObj.getJSONArray("tableIds");
			String[] tableIds = new String[tablesArr.length()];
			for (int i = 0; i < tableIds.length; i++) {
				tableIds[i] = tablesArr.getString(i);
			}
			outObj = dao.newOrder(inObj.getString("hotelId"), inObj.getString("userId"), tableIds,
					inObj.getInt("peopleCount"), inObj.getString("customer"), inObj.getString("contactNumber"), inObj.has("customerAddress")?inObj.getString("customerAddress"):"",
					inObj.getString("allergyInfo"), inObj.has("section")?inObj.getString("section"):"");
			dao.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/hideOrders")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String hideOrders(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", -1);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			String decoded = Base64.decodeAsString(authString.getBytes());
			String[] auth = decoded.split(":");
			UserType userType = dao.validateToken(hotelId, auth[0], auth[1]);
			 
			if(userType != (UserType.SECRET)) {
				outObj.put("status", -2);
				outObj.put("message", "Access Denied. Unauthorized user.");
				return outObj.toString();
			}
			
			outObj = dao.hideOrder(inObj.getString("hotelId"), inObj.getString("serviceDate"),
					inObj.getString("serviceType"), new BigDecimal(Double.toString(inObj.getDouble("amount"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	/*@POST
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
	}*/

	@POST
	@Path("/v1/newNCOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String newNCOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newNCOrder(inObj.getString("hotelId"), inObj.getString("userId"), inObj.getString("reference") , 
					inObj.has("section")?inObj.getString("section"):"", inObj.has("remarks")?inObj.getString("remarks"):"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/newOnlineOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String newZomatoOrder(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			JSONObject order = inObj.getJSONObject("order");
			String[] auth = Base64.decodeAsString(authString.getBytes()).split(":");
			System.out.println(jsonObject);
			//creds: Zomato-zomato
			if(!dao.validOnlinePlatform(order.getString("outlet_id"), auth[0], auth[1])) {
				outObj.put("message", "Invalid Credentials.");
				outObj.put("status", "failed");
				outObj.put("code", "401");
				System.out.println(authString);
				System.out.println("Invalid Credentials");
				return outObj.toString();
			}
			outObj = dao.newOnlineOrder(order, OnlineOrderingPortals.ZOMATO.getValue());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/getOnlineOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOnlineOrders(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<OnlineOrder> order;
		order = dao.getOnlineOrders(hotelId);
		try {
			for (int i = 0; i < order.size(); i++) {

				itemDetails = new JSONObject();
				itemDetails.put("portalId", order.get(i).getPortalId());
				itemDetails.put("portal", OnlineOrderingPortals.getType(order.get(i).getPortalId()));
				itemDetails.put("data", order.get(i).getData());
				
				itemsArr.put(itemDetails);
			}
			orderObj.put("orders", itemsArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderObj.toString();
	}
	
	@POST
	@Path("/v1/editOnlineOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String editOnlineOrder(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			JSONObject order = inObj.getJSONObject("order");
			String[] auth = Base64.decodeAsString(authString.getBytes()).split(":");
			System.out.println("V1");
			System.out.println("Auth String: " + authString);
			System.out.println(jsonObject);
			if(!dao.validOnlinePlatform(order.getString("outlet_id"), auth[0], auth[1])) {
				outObj.put("message", "Invalid Credentials.");
				outObj.put("status", "failed");
				outObj.put("code", "401");
				System.out.println("Invalid Credentials");
				return outObj.toString();
			}
			System.out.println("Authenticated");
			outObj.put("hotelId", Configurator.getHotelId());
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
		Hotel hotel = null;
		boolean printKOT = false;
		try {
			dao.beginTransaction();
			outObj.put("status", -1);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			hotelId = inObj.getString("hotelId");
			orderId = inObj.getString("orderId");
			hotel = dao.getHotelById(hotelId);
			JSONArray newItems = inObj.getJSONArray("newItems");
			JSONObject orderedItem = null;
			String subOrderId = dao.getNextSubOrderId(hotelId, orderId);
			ArrayList<Integer> itemIds = new ArrayList<Integer>();
			ArrayList<Integer> addedItemIds = new ArrayList<Integer>();
			int itemQantity = 0;
			Boolean addAddons = false;
			int offset = 0;
			
			for (int i = 0; i < newItems.length(); i++) {
				orderedItem = newItems.getJSONObject(i);
				JSONObject subOrder = null;
				JSONArray addOns = orderedItem.getJSONArray("addOnArr");
				for(int k=0; k<addOns.length(); k++) {
					itemIds.add(addOns.getJSONObject(k).getInt("itemId"));
				}
				
				//Loop around every item to check for addons.
				for(int j=1; j<=orderedItem.getInt("qty"); j++) {
					
					//if this item does not have addon
					if(!itemIds.contains(j)) {
						itemQantity++;
						addedItemIds.add(j);
						if(j < orderedItem.getInt("qty"))
							continue;
					}else {
						if(itemQantity>0) {
							j--;
							addAddons = false;
						}else {
							itemQantity++;
							addedItemIds.add(j);
							addAddons = true;
						}
					}
						
					menuId = orderedItem.getString("menuId");
					subOrder = dao.newSubOrder(hotelId, orderId, menuId, itemQantity, "",
							subOrderId, inObj.has("userId") ? inObj.getString("userId") : "",
							orderedItem.has("rate") ? new BigDecimal(Double.toString(orderedItem.getDouble("rate"))) : new BigDecimal("0"));
					if (subOrder == null || subOrder.getInt("status") == -1) {
						dao.rollbackTransaction();
						if (subOrder != null) {
							outObj.put("message", subOrder.get("message"));
						}
						printKOT = false;
						return outObj.toString();
					} else
						printKOT = true;
					if(addAddons) {
						for (int k = 0; k < addOns.length(); k++) {
							if(addOns.getJSONObject(k).getInt("itemId")==j) {
								Boolean addedAddOn = dao.addOrderAddon(hotelId, orderId, menuId,
										addOns.getJSONObject(k).getInt("qty"), addOns.getJSONObject(k).getInt("id"), subOrderId,
										addOns.getJSONObject(k).getInt("itemId"), 
										new BigDecimal(Double.toString(addOns.getJSONObject(k).getDouble("rate"))));
			
								if (!addedAddOn) {
									dao.rollbackTransaction();
									outObj.put("status", -1);
									outObj.put("message", "Failed to add Addon");
									return outObj.toString();
								}
							}
							addAddons = false;
						}
					}
					String commonSpecs = "";
					Boolean addedSpec = false;
					JSONArray specifications = orderedItem.getJSONArray("specArr");
					for (int k = 0; k < specifications.length(); k++) {
						if (specifications.getJSONObject(k).getInt("itemId") == 101) {
							commonSpecs += specifications.getJSONObject(k).getString("spec") + ", ";
						} else {
							if(addedItemIds.contains(specifications.getJSONObject(k).getInt("itemId"))) {
								int itemId = specifications.getJSONObject(k).getInt("itemId") - offset;
								addedSpec = dao.addOrderSpecification(hotelId, orderId, subOrderId, menuId,
										itemId, specifications.getJSONObject(k).getString("spec"));
								if (!addedSpec) {
									dao.rollbackTransaction();
									outObj.put("status", -1);
									outObj.put("message", "Failed to add Specification");
									return outObj.toString();
								}
							}
						}
					}
					if (commonSpecs.length() > 0)
						dao.updateSpecifications(hotelId, orderId, subOrderId, menuId, commonSpecs);
					if(addOns.length()>0) {
						subOrderId = dao.getNextSubOrderId(hotelId, orderId);
						offset++;
					}
					itemQantity = 0;
				}
			}
			String billNo = dao.updateBillNoInOrders(hotelId, orderId);
			int orderType = inObj.has("orderType")?inObj.getInt("orderType"):100;
			if(orderType == AccessManager.NON_CHARGEABLE) {
				Order order = dao.getOrderById(hotelId, orderId);
				if(!dao.addPayment(hotelId, orderId, order.getFoodBill(), order.getBarBill(), 
									new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"), 
									new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"), 
									new BigDecimal("0.0"), "",
									"NON CHARGEABLE", new BigDecimal("0.0"), inObj.has("section")?inObj.getString("section"):"")) {
					dao.rollbackTransaction();
					outObj.put("status", -1);
					outObj.put("message", "Failed to add NC order. Please try again.");
					return outObj.toString();
				}else {
					BigDecimal total = order.getFoodBill().add(order.getBarBill());
					String subject = "Non-chargeable Order placed for "+order.getReference()+". Bill No. " + billNo;
					String emailText = "<div style='width:350px; ' class='alert alert-warning'><h3>A Non-Chargeable Order has been placed"
							+ ".</h3><p> Details as follows:</p>"
							+ "<div>Hotel Name: " + hotel.getHotelName()
							+ "<div>Bill No: " + billNo
							+ "</div><div>Ordered For : " + order.getReference()
							+ "</div><div>Ordered By: " + order.getWaiterId()
							+ "</div><div>Time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)
							+ "</div><div>Total bill amount: " + total+ "</div></div>";
					String smsText = "Non Chargeable Order Placed. BillNo: "+billNo
							+", Placed For: "+ order.getReference()
							+ ", Cashier: "+ order.getWaiterId()
							+ ", Total Bill Amt: "+total+".";
					if(netIsAvailable())
						this.SendEmailAndSMS(hotelId, subject, emailText, smsText, "", Designation.OWNER);
				}
				
			}
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
					dao.updateFoodBill(hotelId, orderId, changeQtyItems.getJSONObject(i).getString("menuId"), 1, true,
							new BigDecimal(Double.toString(changeQtyItems.getJSONObject(i).getDouble("rate"))));
			}
			JSONObject customerDetails = inObj.getJSONObject("editCustomerDetails");
			if (customerDetails.toString().trim().length() > 2)
				if(!dao.editCustomerDetails(hotelId, orderId, customerDetails.getString("customer"),
						customerDetails.getString("contactNumber"), customerDetails.getString("address"),
						customerDetails.getInt("peopleCount"), customerDetails.has("allergyInfo")?customerDetails.getString("allergyInfo"):"")) {
					dao.rollbackTransaction();
					outObj.put("message", "Failed update customer details");
					return outObj.toString();
				}

			outObj.put("status", 0);
			outObj.put("message", "Edited order successfully!");
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		if (printKOT && !Configurator.getIsServer() && hotel.getHasKot())
			this.printKOT(hotelId, orderId, false);
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/checkKOT")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String checkKOT(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		try {
			outObj = new JSONObject();
			outObj.put("status", true);
			outObj.put("message", "Check KOT Printed");
			inObj = new JSONObject(jsonObject);
			this.printKOT(inObj.getString("hotelId"), inObj.getString("orderId"), true);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return outObj.toString();
	}

	/*@POST
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
						inObj.has("userId") ? inObj.getString("userId") : "", new BigDecimal("0.0"));
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
							addOns.getJSONObject(j).getInt("id"), subOrderId, addOns.getJSONObject(j).getInt("itemId"),
							new BigDecimal(Double.toString(addOns.getJSONObject(j).getDouble("rate"))));

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
					new BigDecimal(Double.toString(inObj.getDouble("foodBill")),
					new BigDecimal(Double.toString(inObj.getDouble("barBill")), 
					new BigDecimal(Double.toString(inObj.getDouble("discount")), 0, 
					inObj.has("loyalty")?new BigDecimal(Double.toString(inObj.getDouble("loyalty")):0, 
					new BigDecimal(Double.toString(inObj.getDouble("total")),
					new BigDecimal(Double.toString(inObj.getDouble("sc")), 
					new BigDecimal(Double.toString(inObj.getDouble("gst")), 
					inObj.has("varBar")?new BigDecimal(Double.toString(inObj.getDouble("varBar")):0, 
					new BigDecimal(Double.toString(inObj.getDouble("tip")),
					new BigDecimal(Double.toString(inObj.getDouble("cashPayment")), 
					new BigDecimal(Double.toString(inObj.getDouble("cardPayment")), 
					new BigDecimal(Double.toString(inObj.getDouble("appPayment")), inObj.getString("discountName"),
					inObj.getString("cardType"), new BigDecimal(Double.toString(inObj.getDouble("complimentary")), 
					inObj.has("section")?inObj.getString("section"):"");
			outObj.put("status", 0);
			outObj.put("message", "Edited order successfully!");
			hotel = dao.getHotelById(hotelId);
			dao.commitTransaction();
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		if (!hotel.getHasKds())
			this.printKOT(hotelId, orderId, false);
		return outObj.toString();
	}*/

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
	@Path("/v1/unCheckoutOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String unCheckoutOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			if (!dao.unCheckOutOrder(inObj.getString("hotelId"), inObj.getString("orderId"))) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to unCheckout order");
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
			String hotelId = inObj.getString("hotelId");
			if (!dao.checkOutOrder(hotelId, inObj.getString("orderId"))) {
				outObj.put("status", -1);
				outObj.put("message", "Failed to checkout order");
			} else {
				outObj.put("status", 0);
				outObj.put("message", "checkout order successful");
			}
			dao.updateDeliveryBoy(hotelId, inObj.getString("orderId"), inObj.getString("deliveryPersonId"));
			
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
		String hotelId = "";
		try {
			inObj = new JSONObject(jsonObject);
			hotelId = inObj.getString("hotelId");
			Order order = dao.getOrderById(hotelId, inObj.getString("orderId"));
			JSONObject ratings = inObj.getJSONObject("ratings");
			if(ratings.getInt("ambianceRating")<4 || ratings.getInt("qualityOfFoodRating")<4 || 
					ratings.getInt("serviceRating")<4 ||ratings.getInt("hygieneRating")<4) {
				String smsText = "Alert! Low Rating received from " + inObj.getString("customerName") 
					+ " on table " + order.getTableId() + ". Mob.: " + inObj.getString("customerNumber")
					+ ". A: " + ratings.getInt("ambianceRating") + ", H: "+ ratings.getInt("hygieneRating") 
					+ ", F: "+ratings.getInt("qualityOfFoodRating")+", S: "+ratings.getInt("serviceRating")+". "
					+ "Review: " + inObj.getString("reviewSuggestions");
				Hotel hotel = dao.getHotelById(hotelId);
				String subject = "Alert| Low Rating Received";
				String emailText = "<div style='width:350px; ' class='alert alert-warning'><h3>Low Rating received at "+hotel.getHotelName()+" for order. Bill No. "
						+ order.getBillNo() + ".</h3><p> Details as follows:</p>"
						+ "<div>Customer Name: " + inObj.getString("customerName")
						+ "</div><div>Customer Number : " + inObj.getString("customerNumber")
						+ "</div><div>Bill No.: " + order.getBillNo()
						+ "</div><div>Table No.: " + order.getTableId()
						+ "</div><div>Ambiance: " + ratings.getInt("ambianceRating")
						+ "</div><div>Hygiene: " + ratings.getInt("hygieneRating")
						+ "</div><div>Food: " + ratings.getInt("qualityOfFoodRating")
						+ "</div><div>Service: " + ratings.getInt("serviceRating")
						+ "</div><div>review: " + inObj.getString("reviewSuggestions")
						+ "</div><div>Order service by: " + inObj.getString("orderId").split(":")[0] 
						+ "</div></div>"; 
				this.SendEmailAndSMS(hotelId, subject, emailText, smsText, "", Designation.MANAGER);
			}
				
			if (!dao.submitRatings(hotelId, inObj.getString("orderId"),
					inObj.getString("customerName"), inObj.getString("customerNumber"),
					inObj.getString("customerBirthdate"), inObj.getString("customerAnniversary"),
					inObj.getString("reviewSuggestions"), ratings,
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
	@Path("/v1/getActiveOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public String getActiveOrders(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject tempObj = null;
		JSONObject outObj = new JSONObject();
		ArrayList<HomeDelivery> order = dao.getActiveHomeDeliveries(hotelId, userId);
		try {
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNo());
				tempObj.put("address", order.get(i).getAddress());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				outArr.put(tempObj);
			}
			outObj.put("hdOrders", outArr);
			outArr = new JSONArray();
			order = dao.getActiveTakeAway(hotelId, userId);
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("portal", OnlineOrderingPortals.getType(order.get(i).getTakeAwayType()).getName());
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNo());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("reference", order.get(i).getReference());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				outArr.put(tempObj);
			}
			outObj.put("taOrders", outArr);
			outArr = new JSONArray();
			order = dao.getActiveBarOrders(hotelId, userId);
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNo());
				tempObj.put("address", order.get(i).getAddress());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("reference", order.get(i).getReference());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				outArr.put(tempObj);
			}
			outObj.put("barOrders", outArr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/getActiveBarOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public String getActiveBarOrders(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject tempObj = null;
		ArrayList<HomeDelivery> order = dao.getActiveBarOrders(hotelId, userId);
		try {
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNo());
				tempObj.put("address", order.get(i).getAddress());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("reference", order.get(i).getReference());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				tempObj.put("total", dao.getOrderTotal(hotelId, order.get(i).getOrderId()));
				outArr.put(tempObj);
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
		JSONObject tempObj = null;
		ArrayList<HomeDelivery> order = dao.getActiveTakeAway(hotelId, userId);
		try {
			order = dao.getActiveTakeAway(hotelId, userId);
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", OnlineOrderingPortals.getType(order.get(i).getTakeAwayType()).getName());
				tempObj.put("phone", order.get(i).getMobileNo());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("reference", order.get(i).getReference());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				tempObj.put("total", dao.getOrderTotal(hotelId, order.get(i).getOrderId()));
				outArr.put(tempObj);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outArr.toString();
	}

	@GET
	@Path("/v1/getActiveHomeDeliveries")
	@Produces(MediaType.APPLICATION_JSON)
	public String getActiveHomeDeliveries(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray outArr = new JSONArray();
		JSONObject tempObj = null;
		ArrayList<HomeDelivery> order = dao.getActiveHomeDeliveries(hotelId, userId);
		try {
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNo());
				tempObj.put("address", order.get(i).getAddress());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				tempObj.put("total", dao.getOrderTotal(hotelId, order.get(i).getOrderId()));
				outArr.put(tempObj);
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
				out.put("userType", customer.getUserType());
				out.put("points", customer.getPoints());
				out.put("remarks", customer.getRemarks());
				out.put("allergyInfo", customer.getAllergyInfo());
				out.put("visitCount", customer.getVisitCount());
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
			out.put("status", false);
			Hotel hotel = dao.getHotelById(inObj.getString("hotelId"));
			if(!netIsAvailable() || !hotel.getHasSms()) {
				return out.toString();
			}
			ArrayList<Customer> customers = dao.getCustomersForSMS(inObj.getString("hotelId"));
			if (customers == null) {
				return out.toString();
			}
			if(hotel.getHasLoyalty()==1)
				return out.toString();
			Date curtime = sdf.parse(now.format(dateFormat));
			String message = "";
			for (Customer customer : customers) {
				if(customer.getCompleteTimestamp().equals(""))
					continue;
				Date d1 = sdf.parse(customer.getCompleteTimestamp());
				long elapsed = curtime.getTime() - d1.getTime();
				int minutes = (int) (elapsed / 1000) / 60;
				if (minutes > 5 && customer.getMobileNo().length() == 10) {
					System.out.println("Sending sms to " + customer.getCustomer());
					String name = customer.getCustomer().equals("")?",": " "+customer.getCustomer() + "!";
					message = "Hi" + name + " Greetings from " + hotel.getHotelName()
							+ ". Thank you for dining with us. It was a pleasure to have you over. Have a great day!";
					if(inObj.getString("hotelId").equals("oe0001")) {
						message = "Thank you for dining at Oriental Express. :) \r\n" + 
								"Kindly review us on Zomato: http://zoma.to/r/18750632";
					}
					else if(inObj.getString("hotelId").equals("po0001"))
						message = "Hi "+name+" Greetings from Poush. Thank you for dining with us. It was a pleasure to have you over. For reservations please contact: 9820113235 / 9821213232";
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
	public String getAllCustomerDetails(@QueryParam("hotelId") String hotelId, @QueryParam("page") int page) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONArray cusArray = new JSONArray();
		ArrayList<Customer> customers = dao.getAllCustomerDetails(hotelId, page);
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
					out.put("points", customers.get(i).getPoints());
					out.put("userType", customers.get(i).getUserType());
					out.put("remarks", customers.get(i).getRemarks());
					out.put("visitCount", customers.get(i).getVisitCount());
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

	@GET
	@Path("/v1/getCustomerDetailBySearch")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomerDetailBySearch(@QueryParam("hotelId") String hotelId, @QueryParam("query") String query) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONArray cusArray = new JSONArray();
		ArrayList<Customer> customers = dao.getAllCustomerDetailsBySearch(hotelId, query);
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
					out.put("points", customers.get(i).getPoints());
					out.put("userType", customers.get(i).getUserType());
					out.put("remarks", customers.get(i).getRemarks());
					out.put("visitCount", customers.get(i).getVisitCount());
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

	@GET
	@Path("/v1/getAllCustomerDetailsForOrdering")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCustomerDetailsForOrdering(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONArray cusArray = new JSONArray();
		ArrayList<Customer> customers = dao.getAllCustomerDetailsForOrdering(hotelId);
		String mobileNo = "";
		try {
			if (customers != null) {
				for (int i = 0; i < customers.size(); i++) {
					JSONObject out = new JSONObject();
					mobileNo = customers.get(i).getMobileNo();
					out.put("customerName", customers.get(i).getCustomer());
					out.put("address", customers.get(i).getAddress());
					out.put("mobileNo", mobileNo);
					cusArray.put(out);
				}
			}
			outObj.put("customers", cusArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCustomerDetailsBySearch")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomerDetailsBySearch(@QueryParam("hotelId") String hotelId, @QueryParam("query") String query) {
		AccessManager dao = new AccessManager(false);
		JSONObject itemDetails = new JSONObject();
		Customer customer = dao.getCustomerBySearch(hotelId, query);
		try {
			itemDetails.put("status", false);
			if(customer != null) {
				itemDetails.put("status", true);
				itemDetails.put("customerName", customer.getCustomer());
				itemDetails.put("address", customer.getAddress());
				itemDetails.put("mobileNo", customer.getMobileNo());
			}else 
				itemDetails.put("message", "Customer Does not exist");
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemDetails.toString();
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
							inObj.getString("birthdate"), inObj.getString("anniversary"), inObj.getString("remarks"),
							inObj.getString("allergyInfo"), inObj.getString("address"), inObj.getBoolean("wantsPromotion")));
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
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newHomeDeliveryOrder(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("customer"), inObj.getString("mobile"), inObj.getString("address"),
					inObj.getString("allergyInfo"), inObj.has("remarks")?inObj.getString("remarks"):"");
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
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newTakeAwayOrder(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("customer"), inObj.getString("mobile"), inObj.has("externalId")?inObj.getString("externalId"):""
					, inObj.getString("allergyInfo"), inObj.has("remarks")?inObj.getString("remarks"):"");
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
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newBarOrder(inObj.getString("hotelId"), inObj.getString("userId"), inObj.has("reference")?inObj.getString("reference"):""
				, inObj.has("remarks")?inObj.getString("remarks"):"");
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
			String hotelId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			dao.beginTransaction();
			ArrayList<OrderItem> orderedItems = dao.getCancellableOrderedItems(hotelId, orderId);
			if(orderedItems.size()>0) {
				outObj.put("message", "Failed to cancel the order.");
				return outObj.toString();
			}
			orderedItems = dao.getReturnedItems(hotelId, orderId);
			if(orderedItems.size()>0) {
				dao.changeOrderStateToCancelled(hotelId, orderId);
				if(netIsAvailable()) {
					BigDecimal total = new BigDecimal("0.0");
					for(int i=0; i<orderedItems.size(); i++) {
						total.add(new BigDecimal(orderedItems.get(i).getQty()).multiply(orderedItems.get(i).getRate()));
					}
					Order order = dao.getOrderById(hotelId, orderId);
					String subject = "Order Cancelled. Bill No. " + order.getBillNo();
					String text = "<div style='width:300px; '><div class='alert alert-warning'><b>Bill No. " + order.getBillNo()
							+ " </b>has been cancelled.</h3><p> Details as follows:</p>" + "<div>Time: "
							+ LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + "</div><div>Order punched by: "
							+ orderId.split(":")[0] + "</div><div>Number of items returned: " + orderedItems.size()
							+ "</div><div>Total amount (without tax): " + total
							+ "</div></div>";
				
					SendEmail(hotelId, subject, text, "");
				}
			}else if(!dao.deleteOrder(hotelId, orderId)) {
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
	@Path("/v1/hasCheckedOut")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String hasCheckedOut(@QueryParam("hotelId") String hotelId, @QueryParam("serviceDate") String serviceDate) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			if (dao.hasCheckedOut(hotelId, serviceDate)) {
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
								inObj.getString("description"), inObj.getInt("type"), inObj.getInt("foodValue"),
								inObj.getInt("barValue"), inObj.getString("startDate"), inObj.getString("expiryDate"),
								inObj.getString("usageLimit"), inObj.getJSONArray("validCollections"), 
								inObj.getBoolean("hasExpiry")));
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
							inObj.getString("description"), inObj.getInt("type"), inObj.getInt("foodValue"),
							inObj.getInt("barValue"), inObj.getString("startDate"), inObj.getString("expiryDate"), 
							inObj.getString("usageLimit"), inObj.getJSONArray("validCollections"), 
							inObj.getBoolean("hasExpiry")));
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
				discountDetails.put("foodValue", discountItems.get(i).getFoodValue());
				discountDetails.put("barValue", discountItems.get(i).getBarValue());
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
				discountDetails.put("foodValue", discount.getFoodValue());
				discountDetails.put("barValue", discount.getBarValue());
				discountDetails.put("hasDiffBarValue", discount.getHasDiffBarValue());
				discountDetails.put("hasExpiry", discount.getHasExpiryDate());
				discountDetails.put("startDate", dao.formatDate(discount.getStartDate(), "dd/MM/yyyy", "MM/dd/yyyy"));
				discountDetails.put("expiryDate", dao.formatDate(discount.getExpiryDate(), "dd/MM/yyyy", "MM/dd/yyyy"));
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

	@GET
	@Path("/v1/getStatistics")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStatistics(@QueryParam("hotelId") String hotelId, @QueryParam("serviceDate") String serviceDate,
			@QueryParam("section") String section, @QueryParam("serviceType") String serviceType, @QueryParam("userId") String userId) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		JSONArray arr = new JSONArray();
		Report reportItems;
		MonthReport report;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;

		ArrayList<MonthReport> weeklyRevenue = dao.getWeeklyRevenue(hotelId, visible);
		ArrayList<YearlyReport> yearReport = dao.getYearlyOrders(hotelId, visible);
		try {
			outObj.put("status", false);
			if(serviceDate.length()>0) {
				report = dao.getTotalOrdersForCurMonth(hotelId, serviceDate.substring(0, 7), visible);
				outObj.put("totalOrders", report.getTotalOrders());
				report = dao.getBestWaiter(hotelId, serviceDate.substring(0, 7), visible);
				if (report == null) {
					outObj.put("waiterName", "None Yet");
					outObj.put("waitersOrders", "0");
				} else {
					Employee emp = dao.getEmployeeById(hotelId, report.getBestWaiter());
					outObj.put("waiterName", emp.getFullName());
					outObj.put("waitersOrders", report.getTotalOrderByWaiter());
					outObj.put("waitersImage", "/hotels/" + hotelId + "/Employees/" + report.getBestWaiter() + ".jpg");
					outObj.put("waiterHireDate", emp.getHiringDate());
				}

				report = dao.getMostOrderedItem(hotelId, serviceDate.substring(0, 7), visible);
				if (report == null) {
					outObj.put("title", "None Yet");
					outObj.put("orderCount", "0");
				} else {
					outObj.put("title", report.getBestItem());
					outObj.put("menuId", report.getItemId());
					outObj.put("orderCount", report.getItemOrderCount());
				}
	
				outObj.put("cashBalance", dao.getCashBalance(hotelId, section));
				reportItems = dao.getTotalSalesForService(hotelId, serviceDate, serviceType, section, visible);
				
				outObj.put("foodBill", reportItems.getFoodBill());
				outObj.put("barBill", reportItems.getBarBill());
				BigDecimal temp = (reportItems.getTotalTax().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				outObj.put("totalTax", temp.divide(new BigDecimal("100")));
				outObj.put("totalBill", reportItems.getFoodBill().add(reportItems.getBarBill()).add(temp.divide(new BigDecimal("100"))));
				
				outObj.put("inhouseSales", reportItems.getInhouseSales());
				outObj.put("hdSales", reportItems.getHomeDeliverySales());
				outObj.put("taSales", reportItems.getTakeAwaySales());
				
				outObj.put("orderCount", reportItems.getOrderCount());
	
				outObj.put("grossSale", reportItems.getGrossSale());
				outObj.put("complimentary", reportItems.getComplimentary());
				outObj.put("loyalty", reportItems.getLoyaltyAmount());
				temp = (reportItems.getTotal().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				outObj.put("total", temp.divide(new BigDecimal("100")));
				temp = (reportItems.getCashPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				outObj.put("cash", temp.divide(new BigDecimal("100")));
				temp = (dao.getTotalCardPayment(hotelId, serviceDate, serviceType).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);;
				outObj.put("card", temp.divide(new BigDecimal("100")));
				temp = (dao.getTotalAppPayment(hotelId, serviceDate, serviceType).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);;
				outObj.put("app", temp.divide(new BigDecimal("100")));
				temp = (reportItems.getFoodDiscount().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);;
				outObj.put("foodDiscount", temp.divide(new BigDecimal("100")));
				temp = (reportItems.getBarDiscount().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);;
				outObj.put("barDiscount", temp.divide(new BigDecimal("100")));
				outObj.put("zomato", dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.ZOMATO.toString()));
				outObj.put("swiggy", dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.SWIGGY.toString()));
				outObj.put("foodPanda", dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.FOODPANDA.toString()));
				outObj.put("uberEats", dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.UBEREATS.toString()));
				outObj.put("foodiloo", dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.FOODILOO.toString()));
				outObj.put("payTm", dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.PAYTM.toString()));
				outObj.put("voidTrans", dao.getVoidTransactions(hotelId, serviceDate, serviceType));
				outObj.put("nc", reportItems.getNC());

			}
			JSONObject obj = null;
			arr = new JSONArray();
			for (int i = 0; i < weeklyRevenue.size(); i++) {
				obj = new JSONObject();
				obj.put("daySale", weeklyRevenue.get(i).getTotalSales());
				arr.put(obj);
			}
			outObj.put("revenue", arr);

			arr = new JSONArray();
			for (int i = 0; i < yearReport.size(); i++) {
				obj = new JSONObject();
				obj.put("orders", yearReport.get(i).getTotalOrders());
				obj.put("month", yearReport.get(i).getMonth());
				obj.put("monthName", yearReport.get(i).getMonthName());
				arr.put(obj);
			}
			outObj.put("yearStats", arr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getSalesForService")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSalesForService(@QueryParam("hotelId") String hotelId, @QueryParam("serviceDate") String serviceDate,
			@QueryParam("section") String section, @QueryParam("serviceType") String serviceType) {
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		Report reportItems;
		try {
			outObj.put("status", false);
			
			reportItems = dao.getTotalSalesForService(hotelId, serviceDate, serviceType, section, false);
			
			BigDecimal temp = (reportItems.getTotal().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("total", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getCashPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("cash", temp.divide(new BigDecimal("100")));
			temp = (dao.getTotalCardPayment(hotelId, serviceDate, serviceType).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("card", temp.divide(new BigDecimal("100")));
			temp = (dao.getTotalAppPayment(hotelId, serviceDate, serviceType).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("app", temp.divide(new BigDecimal("100")));
			
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
	@Path("/v1/getOnlinePartner")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAppType() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (OnlinePaymentType des : OnlinePaymentType.values()) {
				JSONObject obj = new JSONObject();
				obj.put("app", des);
				outArr.put(obj);
			}
			outObj.put("apps", outArr);
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
	@Path("/v1/getOnlineOrderingPortals")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOnlineOrderingPortals() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (OnlineOrderingPortals portal : OnlineOrderingPortals.values()) {
				JSONObject obj = new JSONObject();
				if (portal == OnlineOrderingPortals.NONE)
					continue;
				obj.put("portalName", portal.getName());
				obj.put("portal", portal.toString());
				obj.put("portalKey", portal.getValue());
				outArr.put(obj);
			}
			outObj.put("onlinePortals", outArr);
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
				if (des == UserType.UNAUTHORIZED || des == UserType.SECRET)
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
				boolean checker = false;
				for (String flag : taxes) {
					if (flag.equals(tax.toString()))
						checker = true;
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
	
	@GET
	@Path("/v1/getApplicableTaxes")
	@Produces(MediaType.APPLICATION_JSON)
	public String getApplicableTaxes(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			Hotel hotel = dao.getHotelById(hotelId);
			String[] taxes = hotel.getFlags().split(";");
			for (TaxTypes tax : TaxTypes.values()) {
				JSONObject obj = new JSONObject();
				for (String flag : taxes) {
					if (flag.equals(tax.toString())) {
						obj.put("name", tax.getName());
						obj.put("percent", tax.getTaxPercent());
						obj.put("abbr", tax.toString());
						obj.put("value", tax.getValue());
						outArr.put(obj);
					}
				}
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
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			Hotel hotel = dao.getHotelById(inObj.getString("hotelId"));
			boolean status = false;

			for(int i=0; i<4; i++) {
				status = dao.addPayment(hotelId, orderId, new BigDecimal(Double.toString(inObj.getDouble("foodBill"))),
						new BigDecimal(Double.toString(inObj.getDouble("barBill"))), 
						new BigDecimal(Double.toString(inObj.getDouble("foodDiscount"))), 
						new BigDecimal(Double.toString(inObj.getDouble("barDiscount"))), 
						new BigDecimal(Double.toString(inObj.getDouble("loyalty"))), 
						new BigDecimal(Double.toString(inObj.getDouble("total"))),
						new BigDecimal(Double.toString(inObj.getDouble("sc"))), 
						new BigDecimal(Double.toString(inObj.getDouble("gst"))), 
						new BigDecimal(Double.toString(inObj.getDouble("vatBar"))), 
						new BigDecimal(Double.toString(inObj.getDouble("tip"))),
						new BigDecimal(Double.toString(inObj.getDouble("cashPayment"))), 
						new BigDecimal(Double.toString(inObj.getDouble("cardPayment"))), 
						new BigDecimal(Double.toString(inObj.getDouble("appPayment"))), inObj.getString("discountName"),
						inObj.getString("cardType"), new BigDecimal(Double.toString(inObj.getDouble("complimentary"))), 
						inObj.getString("section"));
				if (status){
					break;
				}else {
					if(i==3) {
						outObj.put("message", "Payment could not be added. Please try again. If problem persists contact OrderOn support.");
						dao.rollbackTransaction();
						return outObj.toString();
					}else {
						System.out.println("Retrying to add payment. OrderId: "+orderId+". Count:"+i);
						dao.deletePayment(hotelId, orderId);
					}
				}
			}
			dao = new AccessManager(true);
			dao.beginTransaction();
			
			if (hotel.getHotelType().equals("PREPAID") && hotel.getHasKds()) {
				if (!dao.changeOrderStatusToService(hotelId, orderId)) {
					outObj.put("message",
							"Order Status could not be updated. Please try again. If problem persists contact OrderOn support.");
					dao.rollbackTransaction();
					return outObj.toString();
				}
			} else {
				if (!dao.markPaymentComplete(hotelId, orderId)) {
					outObj.put("message", "Failed to mark order complete. Please try again. If problem persists contact support.");
					dao.rollbackTransaction();
					return outObj.toString();
				}
			}
			if (inObj.getInt("cashPayment") > 0) {
				if (!dao.updateCashBalance(hotelId, dao.getCashBalance(hotelId, inObj.has("section")?inObj.getString("section"):"").add(new BigDecimal(inObj.getInt("cashPayment"))))) {
					outObj.put("message", "Cash balance could not be updated. Please try again. If problem persists contact OrderOn support.");
					dao.rollbackTransaction();
					return outObj.toString();
				}
			}
			if (hotel.getHasCashDrawer())
				cashdrawerOpen(hotelId, dao);
			
			String mobileNo = dao.getMobileNoFromOrderId(hotelId, orderId).getEntity();
			if (hotel.getHasLoyalty() == 1 && !mobileNo.equals("") && hotel.getHasSms()) {
				int points = inObj.getInt("total");
				if (!dao.addLoyaltyPoints(hotelId, inObj.getInt("total"), mobileNo)) {
					outObj.put("message", "Loyalty Points could not be added. Please try again. If problem persists contact OrderOn support.");
					dao.rollbackTransaction();
					return outObj.toString();
				}else {
					Customer customer = dao.getCustomerDetails(hotelId, mobileNo);
					int balance = customer.getPoints()+points;
					dao.incrementVisitCount(hotelId, customer);
					if(netIsAvailable()) {
						SendSMS sms = new SendSMS();
						String scheme = "pts";
						String scheme2 = "";
						if(hotelId.equals("ka0001")) {
							scheme = scheme2 = "Kbeans";
						}
						String[] name = customer.getCustomer().split(" ");
						String message = "Hi "+ name[0]+", Greetings from "+hotel.getHotelName()
								+", Thank you for visiting us. You have earned " + points+ " "+scheme+". "
								+ "Balance: "+balance+" "+scheme2+". Visit again to Redeem exciting offers.";
						if(hotelId.equals("wc0001"))
							message = "Hi "+customer.getCustomer()+"! Thank you for dining at WestCoast Diner. We look forward to serving you soon again";
						if(mobileNo.length() > 10) {
							mobileNo = mobileNo.substring(0, 10);
						}
						System.out.println(message);
						String output = "";
						if(mobileNo.length()==10)
							output = sms.sendSms(message, mobileNo);
						System.out.println(output);
						dao.updateOrderSMSStatusDone(hotelId, customer.getOrderId());
					}
				}
			}
			Order order = dao.getOrderById(hotelId, orderId);
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
			if(order.getInHouse() == AccessManager.HOME_DELIVERY) 
				dao.updateDeliveryTime(hotelId, orderId);
			else
				dao.updateCompleteTime(hotelId, orderId);

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
					new BigDecimal(Double.toString(inObj.getDouble("foodBill"))), 
					new BigDecimal(Double.toString(inObj.getDouble("barBill"))), 
					new BigDecimal(Double.toString(inObj.getDouble("foodDiscount"))), 
					new BigDecimal(Double.toString(inObj.getDouble("barDiscount"))),
					new BigDecimal(Double.toString(inObj.getDouble("total"))), 
					new BigDecimal(Double.toString(inObj.getDouble("serviceCharge"))), 
					new BigDecimal(Double.toString(inObj.getDouble("gst"))),
					new BigDecimal(Double.toString(inObj.getDouble("VATBAR"))), 
					new BigDecimal(Double.toString(inObj.getDouble("cashPayment"))), 
					new BigDecimal(Double.toString(inObj.getDouble("cardPayment"))), 
					new BigDecimal(Double.toString(inObj.getDouble("appPayment"))));

			if (status) {
				if (inObj.getInt("cashPayment") > 0) {
					dao.updateCashBalance(hotelId, dao.getCashBalance(hotelId, inObj.has("section")?inObj.getString("section"):"").subtract(new BigDecimal(inObj.getInt("cashPayment"))));
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

			Report payment = dao.getPayment(hotelId, inObj.getString("orderId"));
			boolean status = dao.editPayment(hotelId, inObj.getString("orderId"), 
					new BigDecimal(Double.toString(inObj.getDouble("cashPayment"))),
					new BigDecimal(Double.toString(inObj.getDouble("cardPayment"))), 
					new BigDecimal(Double.toString(inObj.getDouble("appPayment"))), inObj.getString("cardType"));

			if (status) {
				BigDecimal cash = payment.getCashPayment().subtract(new BigDecimal(inObj.getInt("cashPayment")));
				if (cash.compareTo(new BigDecimal("0")) != 0) {
					dao.updateCashBalance(hotelId, dao.getCashBalance(hotelId, inObj.has("section")?inObj.getString("section"):"").subtract(cash));
				}
			}

			outObj.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	/** Stock */

	private BigDecimal convertUnit(BigDecimal number, Unit unit, boolean toMultiply) {

		for (Unit unitType : Unit.values()) {
			if (unit != unitType) {
				continue;
			}
			if (toMultiply) {
				if (unitType.getConversion().compareTo(new BigDecimal("0.001"))==0) {
					number = number.divide(new BigDecimal("1000"));
				} else {
					number = number.divide(unitType.getConversion());
				}
			} else {
				if (unit == unitType) {
					number = number.divide(unitType.getConversion());
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
		BigDecimal ratePerUnit = new BigDecimal("0.0");
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
			BigDecimal ratePerUnit = new BigDecimal(Double.toString(inObj.getDouble("ratePerUnit")));
			BigDecimal minQuantity = new BigDecimal(Double.toString(inObj.getDouble("minQuantity")));
			BigDecimal quantity = new BigDecimal(Double.toString(inObj.getDouble("quantity")));
			String hotelId = inObj.getString("hotelId");
			String name = inObj.getString("name");
			unit.setValue(ratePerUnit);
			for (Unit unitType : Unit.values()) {
				if (unit == unitType) {
					ratePerUnit = ratePerUnit.multiply(unitType.getConversion());
					minQuantity = minQuantity.divide(unitType.getConversion());
					quantity = quantity.divide(unitType.getConversion());
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
		BigDecimal ratePerUnit = new BigDecimal("0.0");
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
		BigDecimal ratePerUnit = new BigDecimal("0.0");

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
		BigDecimal curQuantity = new BigDecimal("0.0");
		Unit unit = Unit.PIECE;
		String doe = "";
		BigDecimal quantity = new BigDecimal("0.0");

		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			unit = Unit.valueOf(filterUnitToStore(inObj.getString("unit")));
			quantity = new BigDecimal(Double.toString(inObj.getDouble("quantity")));
			curQuantity = new BigDecimal(Double.toString(inObj.getDouble("currentQuantity")));

			for (Unit unitType : Unit.values()) {
				if (unit == unitType) {
					quantity = quantity.divide(unitType.getConversion());
					curQuantity = curQuantity.divide(unitType.getConversion());
					break;
				}
			}
			if (inObj.getInt("type") == 0)
				quantity = quantity.add(curQuantity);
			else
				quantity = curQuantity.subtract(quantity);
			outObj.put("status", dao.updateStock(inObj.getString("hotelId"), inObj.getString("sku"), quantity,
					new BigDecimal(Double.toString(inObj.getDouble("quantity"))), 
					new BigDecimal(Double.toString(inObj.getDouble("ratePerUnit"))), doe));
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
							convertUnit(new BigDecimal(Double.toString(inObj.getDouble("ratePerUnit"))), unit, true),
							convertUnit(new BigDecimal(Double.toString(inObj.getDouble("minQuantity"))), unit, false),
							convertUnit(new BigDecimal(Double.toString(inObj.getDouble("quantity"))), unit, false), doe, inObj.getInt("wastage"),
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
								convertUnit(new BigDecimal(Double.toString(inObj.getDouble("quantity"))), unit, false), inObj.getString("menuId"),
								inObj.getString("sku"), inObj.getString("unit")));
			} else {
				outObj.put("status",
						dao.addRecipe(inObj.getString("hotelId"), convertUnit(new BigDecimal(Double.toString(inObj.getDouble("quantity"))), unit, false),
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
					new BigDecimal(Double.toString(inObj.getDouble("curQuantity"))).subtract(new BigDecimal(Double.toString(inObj.getDouble("quantity")))), 
							new BigDecimal(Double.toString(inObj.getDouble("quantity")))));
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
			outObj.put("name", "EXPENSE REPORT");
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
				itemDetails.put("totalCovers", reportItems.get(i).getTotalGuests());
				itemDetails.put("totalWalkins", reportItems.get(i).getTotalWalkins());
				itemDetails.put("customerName", reportItems.get(i).getCustomerName());
				itemDetails.put("mobileNumber", reportItems.get(i).getMobileNumber());
				itemDetails.put("spentPerCover", reportItems.get(i).getSpentPerPax());
				itemDetails.put("spentPerWalkin", reportItems.get(i).getSpentPerWalkin());
				itemDetails.put("totalSpent", reportItems.get(i).getTotalSpent());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "CUSTOMER REPORT");
		} catch (JSONException e) {
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/getCustomerReviewReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomerReviewReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<CustomerReport> reportItems = dao.getCustomerReviewReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("customerName", reportItems.get(i).getCustomerName());
				itemDetails.put("mobileNumber", reportItems.get(i).getMobileNumber());
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("covers", reportItems.get(i).getTotalGuests());
				itemDetails.put("totalBill", reportItems.get(i).getTotal());
				itemDetails.put("ambiance", reportItems.get(i).getRatingAmbiane());
				itemDetails.put("food", reportItems.get(i).getRatingFood());
				itemDetails.put("hygiene", reportItems.get(i).getRatingHygiene());
				itemDetails.put("service", reportItems.get(i).getRatingService());
				itemDetails.put("suggestion", reportItems.get(i).getSuggestions());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "CUSTOMER REVIEW REPORT");
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
		JSONObject outObj = new JSONObject();
		ArrayList<AccessManager.DiscountReport> reportItems = dao.getDiscountReport(hotelId, startDate, endDate);
		try {
			for (AccessManager.DiscountReport report : reportItems) {
				JSONObject record = new JSONObject();
				record.put("customerName", report.getCustomerName());
				record.put("discountName", report.getDiscountName());
				record.put("orderDate", report.getOrderDate());
				record.put("total", report.getTotal());
				record.put("foodDiscount", report.getFoodDiscount());
				record.put("barDiscount", report.getBarDiscount());
				record.put("totalDiscount", report.getTotalDiscount());
				record.put("discountedTotal", report.getDiscountedTotal());
				itemsArr.put(record);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "ORDERWISE DISCOUNT REPORT");
		} catch (JSONException e) {
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getPaymentWiseSalesReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPaymentWiseSalesReport(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		PaymentWiseSalesReport reportItems = null;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		try {
			for(int i=0; i<=4; i++) {
				reportItems = dao.getPaymentWiseSalesReport(hotelId, startDate, endDate,
						i, visible);
				itemDetails = new JSONObject();
				
				itemDetails.put("foodBill", reportItems.getFoodBill());
				itemDetails.put("barBill", reportItems.getBarBill());
				itemDetails.put("total", reportItems.getTotal());
				itemDetails.put("cover", reportItems.getCover());
				itemDetails.put("foodPCover",
						(reportItems.getCover()==0?reportItems.getFoodBill().doubleValue():reportItems.getFoodBill().doubleValue()/reportItems.getCover()));
				itemDetails.put("barPCover",
						(reportItems.getCover()==0?reportItems.getFoodBill().doubleValue():reportItems.getFoodBill().doubleValue()/reportItems.getCover()));
				itemDetails.put("cash", reportItems.getCash());
				itemDetails.put("card", reportItems.getCard());
				itemDetails.put("app", reportItems.getApp());
				itemDetails.put("VISA", reportItems.getVISA());
				itemDetails.put("MASTERCARD", reportItems.getMASTERCARD());
				itemDetails.put("MAESTRO", reportItems.getMAESTRO());
				itemDetails.put("AMEX", reportItems.getAMEX());
				itemDetails.put("RUPAY", reportItems.getRUPAY());
				itemDetails.put("MSWIPE", reportItems.getMSWIPE());
				itemDetails.put("OTHERS", reportItems.getOTHERS());
				itemDetails.put("ZOMATO", reportItems.getZOMATO());
				itemDetails.put("ZOMATOPAY", reportItems.getZOMATO_PAY());
				itemDetails.put("SWIGGY", reportItems.getSWIGGY());
				itemDetails.put("PAYTM", reportItems.getPAYTM());
				itemDetails.put("DINEOUT", reportItems.getDINE_OUT());
				itemDetails.put("FOODPANDA", reportItems.getFOOD_PANDA());
				itemDetails.put("UBEREATS", reportItems.getUBER_EATS());
				itemDetails.put("FOODILOO", reportItems.getFOODILOO());
				itemDetails.put("NEARBY", reportItems.getNEARBY());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "PAYMENTWISE SALES REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getTotalSalesForService")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTotalSalesForService(@QueryParam("hotelId") String hotelId,
			@QueryParam("serviceDate") String serviceDate, @QueryParam("serviceType") String serviceType,
			@QueryParam("section") String section) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONArray arr2 = new JSONArray();
		JSONObject arrObj;
		Report reportItems;
		ArrayList<Expense> expenses = dao.getCashExpenses(hotelId, serviceDate, serviceType, section);
		cashdrawerOpen(hotelId, dao);

		try {
			reportItems = dao.getTotalSalesForService(hotelId, serviceDate, serviceType, section, true);
			outObj.put("foodBill", reportItems.getFoodBill());
			outObj.put("barBill", reportItems.getBarBill());
			outObj.put("grossSale", reportItems.getGrossSale());
			outObj.put("inhouseSales", reportItems.getInhouseSales());
			outObj.put("hdSales", reportItems.getHomeDeliverySales());
			outObj.put("taSales", reportItems.getTakeAwaySales());
			outObj.put("orderCount", reportItems.getOrderCount());
			outObj.put("printCount", reportItems.getPrintCount());
			outObj.put("reprints", reportItems.getReprints());
			outObj.put("complimentary", reportItems.getComplimentary());
			outObj.put("loyalty", reportItems.getLoyaltyAmount());
			outObj.put("nc", reportItems.getNC());
			outObj.put("total", reportItems.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
			BigDecimal zomatoCash = dao.getCardPaymentByType(hotelId, serviceDate, serviceType, "ZOMATO_CASH");
			outObj.put("zomatoCash", zomatoCash);
			BigDecimal swiggyCash = dao.getCardPaymentByType(hotelId, serviceDate, serviceType, "SWIGGY_CASH");
			outObj.put("swiggyCash", swiggyCash);
			outObj.put("cash", reportItems.getCashPayment().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("totalCash", (reportItems.getCashPayment().add(swiggyCash).add(zomatoCash)).setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("card", dao.getTotalCardPayment(hotelId, serviceDate, serviceType).setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("app", dao.getTotalAppPayment(hotelId, serviceDate, serviceType).setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("foodDiscount", reportItems.getFoodDiscount().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("barDiscount", reportItems.getBarDiscount().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("totalTax", reportItems.getTotalTax().setScale(2, BigDecimal.ROUND_HALF_UP));
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
			arrObj.put("name", OnlinePaymentType.ZOMATO.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.ZOMATO.toString()));
			outObj.put(OnlinePaymentType.ZOMATO.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.ZOMATO.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.SWIGGY.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.SWIGGY.toString()));
			outObj.put(OnlinePaymentType.SWIGGY.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.SWIGGY.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.DINEOUT.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.DINEOUT.toString()));
			outObj.put(OnlinePaymentType.DINEOUT.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.DINEOUT.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.PAYTM.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.PAYTM.toString()));
			outObj.put(OnlinePaymentType.PAYTM.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.PAYTM.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.FOODPANDA.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.FOODPANDA.toString()));
			outObj.put(OnlinePaymentType.FOODPANDA.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.FOODPANDA.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.UBEREATS.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.UBEREATS.toString()));
			outObj.put(OnlinePaymentType.UBEREATS.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.UBEREATS.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.FOODILOO.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.FOODILOO.toString()));
			outObj.put(OnlinePaymentType.FOODILOO.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.FOODILOO.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.ZOMATOPAY.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.ZOMATOPAY.toString()));
			outObj.put(OnlinePaymentType.ZOMATOPAY.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.ZOMATOPAY.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.NEARBY.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.NEARBY.toString()));
			outObj.put(OnlinePaymentType.NEARBY.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, serviceType, OnlinePaymentType.NEARBY.toString()));
			arr.put(arrObj);
			outObj.put("apps", arr);

			outObj.put("voidTrans", dao.getVoidTransactions(hotelId, serviceDate, serviceType));
			outObj.put("cashInHand", dao.getCashInHand(hotelId));
			outObj.put("cashBalance", dao.getCashBalance(hotelId, section));
			Double totalExpense = 0.0;
			Double totalPayIn = 0.0;
			Double cashLift = 0.0;

			arr = new JSONArray();
			arr2 = new JSONArray();
			for (Expense expense : expenses) {
				arrObj = new JSONObject();
				if(expense.getType().equals("PAYIN")) {
					arrObj.put("amount", expense.getAmount());
					arrObj.put("payee", expense.getPayee());
					arrObj.put("type", expense.getType());
					arrObj.accumulate("memo", expense.getMemo());
					totalPayIn+= expense.getAmount().doubleValue();
					arr2.put(arrObj);
				}else {
					arrObj.put("amount", expense.getAmount());
					arrObj.put("payee", expense.getPayee());
					arrObj.put("type", expense.getType());
					arrObj.accumulate("memo", expense.getMemo());
					if (expense.getType().equals(ExpenseType.CASH_LIFT.toString()))
						cashLift+= expense.getAmount().doubleValue();
					else
						totalExpense+= expense.getAmount().doubleValue();
					arr.put(arrObj);
				}
			}

			outObj.put("expenses", arr);
			outObj.put("payIns", arr2);
			outObj.put("totalExpense", totalExpense);
			outObj.put("totalPayIn", totalPayIn);
			outObj.put("cashLift", cashLift);
			outObj.put("hasEod", dao.getHotelById(hotelId).getHasEod());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getSalesReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSaleSummaryReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		
		ArrayList<Report> reportItems = dao.getSaleSummaryReport(hotelId, startDate, endDate, visible);
		try {
			String section= dao.getHotelById(hotelId).getSections()[0];
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				if(reportItems.get(i).getState() == AccessManager.ORDER_STATE_VOIDED) {
					itemDetails.put("foodBill", 0);
					itemDetails.put("barBill", 0);
				}else if(reportItems.get(i).getState() == AccessManager.ORDER_STATE_COMPLIMENTARY) {
					itemDetails.put("foodBill", 0);
					itemDetails.put("barBill", 0);
				}else {
					itemDetails.put("foodBill", reportItems.get(i).getFoodBill());
					itemDetails.put("barBill", reportItems.get(i).getBarBill());
				}
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("foodDiscount", reportItems.get(i).getFoodDiscount());
				itemDetails.put("barDiscount", reportItems.get(i).getBarDiscount());
				itemDetails.put("sc", reportItems.get(i).getServiceCharge());
				itemDetails.put("tip", reportItems.get(i).getTip());
				itemDetails.put("covers", reportItems.get(i).getCover());
				itemDetails.put("inhouse", reportItems.get(i).getInhouse());
				itemDetails.put("takeAwayType", OnlineOrderingPortals.getType(reportItems.get(i).getTakeAwayType()).getName());
				itemDetails.put("card", reportItems.get(i).getCardPayment());
				itemDetails.put("cash", reportItems.get(i).getCashPayment());
				itemDetails.put("app", reportItems.get(i).getAppPayment());
				itemDetails.put("trType", reportItems.get(i).getCardType());
				itemDetails.put("total", reportItems.get(i).getTotal());
				itemDetails.put("tableId", reportItems.get(i).getTableId());
				itemDetails.put("orderDate", reportItems.get(i).getOrderDate());
				itemDetails.put("cgst", reportItems.get(i).getGST().divide(new BigDecimal("2")));
				itemDetails.put("sgst", reportItems.get(i).getGST().divide(new BigDecimal("2")));
				if(!section.equals(""))
					itemDetails.put("section", reportItems.get(i).getSection());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "SALES REPORT");
			if(!section.equals(""))
				outObj.put("hasSection", true);
			else
				outObj.put("hasSection", false);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getStockReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStockReport(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();
		ArrayList<Stock> stockItems = dao.getStock(hotelId);
		BigDecimal ratePerUnit = new BigDecimal("0.0");
		Unit unit = Unit.GRAM;
		String unitStr = "";
		BigDecimal convertedUnit = new BigDecimal("0.0");

		try {
			for (int i = 0; i < stockItems.size(); i++) {
				unit = Unit.valueOf(stockItems.get(i).getDisplayableUnit());
				ratePerUnit = stockItems.get(i).getRatePerUnit();
				unit.setValue(ratePerUnit);
				unitStr = filterUnitToDisplay(unit.toString());
				convertedUnit = convertUnit(stockItems.get(i).getQuantity(), unit, true);

				itemDetails = new JSONObject();
				itemDetails.put("title", stockItems.get(i).getName());
				itemDetails.put("unit", unitStr);
				itemDetails.put("quantity", convertedUnit);
				itemDetails.put("qty", convertedUnit.toString() + " " +unitStr);

				itemsArr.put(itemDetails);
			}

			outObj.put("report", itemsArr);
			outObj.put("name", "CURRENT STOCK REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDailyDiscountReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDailyDiscountReport(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		ArrayList<DailyDiscountReport> reportItems = dao.getDailyDiscountReport(hotelId, startDate, endDate, visible);

		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("discountName", reportItems.get(i).getName());
				itemDetails.put("foodValue", reportItems.get(i).getFoodValue());
				itemDetails.put("barValue", reportItems.get(i).getBarValue());
				itemDetails.put("description", reportItems.get(i).getDescription());
				itemDetails.put("discountPer", reportItems.get(i).getDiscountPer());
				itemDetails.put("sumTotal", reportItems.get(i).getTotal());
				itemDetails.put("avgTotal", reportItems.get(i).getAvgTotal());
				itemDetails.put("sumDiscount", reportItems.get(i).getDiscount());
				itemDetails.put("avgDiscount", reportItems.get(i).getAvgDiscount());
				itemDetails.put("discountedTotal", reportItems.get(i).getSumDiscountedTotal());
				itemDetails.put("ordersAffected", reportItems.get(i).getOrdersAffected());
				itemDetails.put("ordersDiscountedPer", reportItems.get(i).getOrdersDiscountedPer());
				itemDetails.put("avgDiscountPer", reportItems.get(i).getAvgDiscountPer());
				itemDetails.put("grossDiscountPer", reportItems.get(i).getGrossDiscountPer());
				itemDetails.put("totalOrders", reportItems.get(i).getTotalOrders());
				itemDetails.put("grossSale", reportItems.get(i).getGrossSale());
				itemDetails.put("grossDiscount", reportItems.get(i).getGrossDiscount());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "DISCOUNT ANALYSIS REPORT");
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
			@QueryParam("endDate") String endDate, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		ArrayList<GrossSaleReport> reportItems = dao.getGrossSalesReport(hotelId, startDate, endDate, visible);

		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("grossTotal", reportItems.get(i).getGrossTotal());
				itemDetails.put("grossDiscount", reportItems.get(i).getGrossDiscount());
				itemDetails.put("grossLoyalty", reportItems.get(i).getGrossLoyalty());
				itemDetails.put("grossComplimentary", reportItems.get(i).getGrossComplimentary());
				itemDetails.put("grossGst", reportItems.get(i).getGrossGst().divide(new BigDecimal("2")));
				itemDetails.put("cardPayment", reportItems.get(i).getCardPayment());
				itemDetails.put("cashPayment", reportItems.get(i).getCashPayment());
				itemDetails.put("appPayment", reportItems.get(i).getAppPayment());
				itemDetails.put("grossServiceCharge", reportItems.get(i).getGrossServiceCharge());
				itemDetails.put("netSales", reportItems.get(i).getNetSales());
				itemDetails.put("grossExpenses", reportItems.get(i).getGrossExpenses());
				itemDetails.put("grossPayins", reportItems.get(i).getGrossPayIns());
				itemDetails.put("totalSale", reportItems.get(i).getTotalSale());
				itemDetails.put("sumVoids", reportItems.get(i).getSumVoids());
				itemDetails.put("sumReturns", reportItems.get(i).getSumReturns());
				itemDetails.put("countVoids", reportItems.get(i).getCountVoids());
				itemDetails.put("countReturns", reportItems.get(i).getCountReturns());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "GROSS SALE REPORT");
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
	public String getDailyOperationReport2(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		// Operating Metrics
		// main 3
		ArrayList<DailyOperationReport> reportItems4 = dao.getDailyOperationReport4(hotelId, startDate, endDate, visible);
		// tables turned
		ArrayList<DailyOperationReport> reportItems5 = dao.getDailyOperationReport5(hotelId, startDate, endDate, visible);
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

	public static Boolean netIsAvailable() {
	    try {
	        final URL url = new URL("http://www.google.com");
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        conn.getInputStream().close();
	        return true;
	    } catch (MalformedURLException e) {
	        throw new RuntimeException(e);
	    } catch (IOException e) {
	        return false;
	    }
	}
	// testing
	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String args[]) throws ParseException {

		//System.out.println("Has Internet Conection " + netIsAvailable());
		
		/*String alltitle = "";
		
		String[] titles = alltitle.split(",");
		
		for (String t : titles) {
			generateShortForm(t);
		}*/ 
		
	}

	/*private static void generateShortForm(String title) {

		String[] sf = title.split(" ");
		StringBuilder out = new StringBuilder();

		for (int i = 0; i < sf.length; i++) {
			if (sf[i].length() >= 2)
				out.append(sf[i].substring(0, 2).toUpperCase());
		}
		System.out.println(out.toString());
	}*/

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

			if (dao.addExpense(hotelId, new BigDecimal(Double.toString(inObj.getDouble("expenseAmount"))).add(new BigDecimal(Double.toString(inObj.getDouble("bonus")))),
					inObj.getString("details"), inObj.getString("payeeName"), inObj.getInt("cheque"),
					inObj.getString("paymentType"), expenseType, inObj.getString("bankAccount"),
					inObj.getString("userId"), inObj.getString("employeeId"))) {
				outObj.put("status", true);

				if (expenseType.equals(ExpenseType.LABOUR.toString())) {
					dao.updateLabourLog(hotelId, new BigDecimal(Double.toString(inObj.getDouble("expenseAmount"))),
							inObj.getString("employeeId"), new BigDecimal(Double.toString(inObj.getDouble("bonus"))));
				}
				if (expenseType.equals(ExpenseType.FLOAT.toString())) {
					dao.updateTransactionHistory(hotelId, "CREDIT", ExpenseType.FLOAT.toString(), 
							inObj.getString("bankAccount"), inObj.getString("paymentType"), new BigDecimal(Double.toString(inObj.getDouble("expenseAmount"))),
							inObj.getString("employeeId"), inObj.getString("userId"), "");
				}
				if (inObj.getString("bankAccount").equals("CASH_DRAWER")) {
					if(expenseType.equals("PAYIN"))
						dao.updateCashBalance(hotelId, dao.getCashBalance(hotelId, inObj.has("section")?inObj.getString("section"):"").add(new BigDecimal(inObj.getInt("expenseAmount"))));
					else	
						dao.updateCashBalance(hotelId, dao.getCashBalance(hotelId, inObj.has("section")?inObj.getString("section"):"").subtract(new BigDecimal(inObj.getInt("expenseAmount"))));
					Hotel hotel = dao.getHotelById(hotelId);
					if (hotel.getHasCashDrawer())
						cashdrawerOpen(hotelId, dao);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/deleteExpense")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteExpense(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");

			Expense expense = dao.getExpense(hotelId, inObj.getInt("expenseId"));
			if(expense.getType().equals(ExpenseType.FLOAT.toString())) {
				dao.clearBigDecimal(hotelId, inObj.getInt("expenseId"),inObj.getString("userId"), 
						inObj.getString("authoriser"), inObj.getString("section"), expense.getAccountName(),
						expense.getPaymentType(), expense.getAmount(), expense.getEmployeeId());
			}else
				dao.deleteExpense(hotelId, inObj.getInt("expenseId"),inObj.getString("section"),
						expense.getPaymentType(), expense.getAmount());

			if (expense.getAccountName().equals("CASH_DRAWER")) {
				if(expense.getType().equals("PAYIN"))
					dao.updateCashBalance(hotelId, 
							dao.getCashBalance(hotelId, inObj.has("section")?inObj.getString("section"):"").subtract(expense.getAmount()));
				else	
					dao.updateCashBalance(hotelId, 
							dao.getCashBalance(hotelId, inObj.has("section")?inObj.getString("section"):"").add(expense.getAmount()));
				Hotel hotel = dao.getHotelById(hotelId);
				if (hotel.getHasCashDrawer())
					cashdrawerOpen(hotelId, dao);
			}
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/getExpenses")
	@Produces(MediaType.APPLICATION_JSON)
	public String getExpenses(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Expense> reportItems = dao.getExpenses(hotelId);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("id", reportItems.get(i).getId());
				itemDetails.put("amount", reportItems.get(i).getAmount());
				itemDetails.put("type", reportItems.get(i).getType());
				itemDetails.put("payee", reportItems.get(i).getPayee());
				itemDetails.put("details", reportItems.get(i).getMemo());
				itemDetails.put("chequeNo", reportItems.get(i).getChequeNumber());
				itemDetails.put("account", reportItems.get(i).getAccountName());
				itemDetails.put("paymentType", reportItems.get(i).getPaymentType());
				itemsArr.put(itemDetails);
			}
			outObj.put("expenses", itemsArr);
		} catch (JSONException e) {
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/getPayIns")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPayIns(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Expense> reportItems = dao.getPayIns(hotelId);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("id", reportItems.get(i).getId());
				itemDetails.put("amount", reportItems.get(i).getAmount());
				itemDetails.put("type", reportItems.get(i).getType());
				itemDetails.put("payee", reportItems.get(i).getPayee());
				itemDetails.put("details", reportItems.get(i).getMemo());
				itemDetails.put("chequeNo", reportItems.get(i).getChequeNumber());
				itemDetails.put("account", reportItems.get(i).getAccountName());
				itemDetails.put("paymentType", reportItems.get(i).getPaymentType());
				itemsArr.put(itemDetails);
			}
			outObj.put("payIns", itemsArr);
		} catch (JSONException e) {
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
				itemDetails.put("foodDiscounts", reportItems.getFoodDiscount());
				itemDetails.put("barDiscounts", reportItems.getBarBill());
				itemDetails.put("sc", reportItems.getServiceCharge());
				itemDetails.put("taxes",
						reportItems.getServiceTax().add(reportItems.getSbCess()).add(reportItems.getKkCess()));
				itemDetails.put("VAT", reportItems.getVATBar().add(reportItems.getVATFood()));
				itemDetails.put("averagePerCover", reportItems.getCover()==0?reportItems.getFoodBill().doubleValue():reportItems.getTotal().doubleValue()/reportItems.getCover());
				itemDetails.put("averagePerCheck", reportItems.getChecks()==0?reportItems.getFoodBill().doubleValue():reportItems.getTotal().doubleValue()/reportItems.getChecks());
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
				dao.updateCashBalance(inObj.getString("hotelId"), new BigDecimal(inObj.getInt("cashInHand")));
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
			if (!dao.getHotelById(hotelId).getHasEod()) {
				JSONObject report = inObj.getJSONObject("report");
				try {
					if (!dao.addRevenue(inObj.getString("hotelId"), inObj.getString("serviceType"),
							inObj.getString("serviceDate"), new BigDecimal(Double.toString(report.getDouble("cash"))), 
							new BigDecimal(Double.toString(report.getDouble("card"))),
							new BigDecimal(Double.toString(report.getDouble("app"))), 
							new BigDecimal(Double.toString(report.getDouble("total"))), 
							new BigDecimal(Double.toString(report.getDouble("VISA"))),
							new BigDecimal(Double.toString(report.getDouble("MASTERCARD"))), 
							new BigDecimal(Double.toString(report.getDouble("MAESTRO"))), 
							new BigDecimal(Double.toString(report.getDouble("AMEX"))),
							new BigDecimal(Double.toString(report.getDouble("OTHERS"))), 
							new BigDecimal(Double.toString(report.getDouble("MSWIPE"))), 
							new BigDecimal(Double.toString(report.getDouble("RUPAY"))),
							new BigDecimal(Double.toString(report.getDouble("ZOMATO"))), 
							new BigDecimal(Double.toString(report.getDouble("ZOMATOPAY"))), 
							new BigDecimal(Double.toString(report.getDouble("SWIGGY"))), 
							new BigDecimal(Double.toString(report.getDouble("DINEOUT"))),
							new BigDecimal(Double.toString(report.getDouble("PAYTM"))), 
							new BigDecimal(Double.toString(report.getDouble("NEARBY"))), 
							new BigDecimal(Double.toString(report.getDouble("FOODPANDA"))), 
							new BigDecimal(Double.toString(report.getDouble("UBEREATS"))),
							new BigDecimal(Double.toString(report.getDouble("FOODILOO"))), 
							new BigDecimal(Double.toString(report.getDouble("complimentary"))), new BigDecimal("0.0"), "", "", inObj.has("section")?inObj.getString("section"):"")) {

						outObj.put("message", "Service cannot be ended right now. Please contact support!");
						return outObj.toString();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if(dao.checkSevicesEnded(hotelId, inObj.getString("serviceDate"), inObj.getString("serviceType"))) {
				if (dao.endService(hotelId, inObj.getString("serviceDate"), inObj.getString("serviceType"))) {
					outObj.put("status", true);
					dao.updateCashBalance(inObj.getString("hotelId"), new BigDecimal("0"));
				}
			}else {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/sendEODReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String sendEODReport(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			String serviceDate = inObj.getString("serviceDate");
			String serviceType = inObj.getString("serviceType");
			outObj.put("status", false);
			if(!netIsAvailable()) {
				dao.updateMessageStatus(hotelId, serviceDate, serviceType, false);
			}else {
				sendEmailSmsForEod(dao, hotelId, serviceDate, serviceType);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	private boolean sendEmailSmsForEod(AccessManager dao, String hotelId, String serviceDate, String serviceType) {
		String location = Configurator.getDownloadLocation();
		String date = serviceDate.replaceAll("/", "_");
		String file = location + "Sales Report for " + date + ".csv";
		String[] filePaths = new String[3];
		filePaths[0] = file;
		file = location + "DepartmentWise Comsumption Report for " + date + ".csv";
		filePaths[1] = file;
		file = location + "Paymentwise Sales Report for " + date + ".csv";
		filePaths[2] = file;
		
 		ServiceLog log = dao.getServiceLog(hotelId, serviceDate);
		BigDecimal netSale = dao.getSaleForService(hotelId, serviceDate, serviceType);
		String emailSubject = "End Of Day Report for " + serviceDate;
		String smsText = "Service No. "+log.getId()
				+ ". Service Date. "+serviceDate
				+ ". Service Type. "+serviceType
				+ ". Service started at " + log.getStartTimeStamp().substring(11, 19) 
				+ ". Service stopped at " + log.getEndTimeStamp().substring(11, 19) 
				+ ". Total Sale was " + netSale +".";
		this.SendEmailWithAttachment(hotelId, emailSubject, smsText, "", filePaths);
		Hotel hotel = dao.getHotelById(hotelId);
		if(hotel.getHasSms())
			this.SendSms(hotelId, smsText, Designation.OWNER);
		dao.updateMessageStatus(hotelId, serviceDate, serviceType, true);
		return true;
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
				paymentDetails.put("app", payment.getAppPayment());
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
				paymentDetails.put("foodDiscount", payment.getFoodDiscount());
				paymentDetails.put("barDiscount", payment.getBarDiscount());
				paymentDetails.put("discountName", payment.getDiscountName());
				paymentDetails.put("loyaltyAmount", payment.getLoyaltyAmount());
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
					inObj.getString("serviceDate"), new BigDecimal(Double.toString(inObj.getDouble("cash"))), 
					new BigDecimal(Double.toString(inObj.getDouble("card"))),
					new BigDecimal(Double.toString(inObj.getDouble("app"))), 
					new BigDecimal(Double.toString(inObj.getDouble("total"))), 
					new BigDecimal(Double.toString(inObj.getDouble("visa"))),
					new BigDecimal(Double.toString(inObj.getDouble("mastercard"))), 
					new BigDecimal(Double.toString(inObj.getDouble("maestro"))), 
					new BigDecimal(Double.toString(inObj.getDouble("amex"))),
					new BigDecimal(Double.toString(inObj.getDouble("others"))), 
					new BigDecimal(Double.toString(inObj.getDouble("mswipe"))), 
					new BigDecimal(Double.toString(inObj.getDouble("rupay"))),
					new BigDecimal(Double.toString(inObj.getDouble("zomato"))), 
					new BigDecimal(Double.toString(inObj.getDouble("zomatoPay"))), 
					new BigDecimal(Double.toString(inObj.getDouble("swiggy"))), 
					new BigDecimal(Double.toString(inObj.getDouble("foodPanda"))), 
					new BigDecimal(Double.toString(inObj.getDouble("uberEats"))), 
					new BigDecimal(Double.toString(inObj.getDouble("foodiloo"))), 
					new BigDecimal(Double.toString(inObj.getDouble("dineOut"))),
					new BigDecimal(Double.toString(inObj.getDouble("payTm"))), 
					new BigDecimal(Double.toString(inObj.getDouble("nearBy"))), 
					new BigDecimal(Double.toString(inObj.getDouble("complimentary"))), 
					new BigDecimal(Double.toString(inObj.getDouble("difference"))),
					inObj.getString("reason"), inObj.getString("clearance"), inObj.getString("section"));

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
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			MPNotification notification = dao.getMPNotification(hotelId);
			outObj.put("status", true);
			outObj.put("checkOutOrders", notification.getCheckoutOrders());
			outObj.put("outOfStock", notification.getOutOfStock());
			
			ArrayList<ServiceLog> logs =  dao.getServiceLogsForMessageNotSent(hotelId);
			if(logs != null && netIsAvailable()) {
				for (int i = 0; i < logs.size(); i++) {
					this.sendEmailSmsForEod(dao, hotelId, logs.get(i).getServiceDate(), logs.get(i).getServiceType());
				}
			}
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
	@Path("/v1/getVoidOrderReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getVoidOrderReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Order> reportItems = dao.getVoidOrderReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("orderDate", reportItems.get(i).getOrderDate());
				itemDetails.put("waiterId", reportItems.get(i).getWaiterId());
				itemDetails.put("inhouse", dao.getOrderType(reportItems.get(i).getInHouse()));
				itemDetails.put("reason", reportItems.get(i).getReason());
				itemDetails.put("foodBill", reportItems.get(i).getFoodBill());
				itemDetails.put("barBill", reportItems.get(i).getBarBill());
				itemDetails.put("totalBill", reportItems.get(i).getTotal());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "VOID ORDER REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getNCOrderReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getNCOrderReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<Order> reportItems = dao.getNCOrderReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("orderDate", reportItems.get(i).getOrderDate());
				itemDetails.put("waiterId", reportItems.get(i).getWaiterId());
				itemDetails.put("inhouse", dao.getOrderType(reportItems.get(i).getInHouse()));
				itemDetails.put("reference", reportItems.get(i).getReference());
				itemDetails.put("totalBill", reportItems.get(i).getTotal());
				itemDetails.put("foodBill", reportItems.get(i).getFoodBill());
				itemDetails.put("barBill", reportItems.get(i).getBarBill());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "NON CHARGEABLE ORDER REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getReturnedItemsReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getReturnedItemsReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		ArrayList<ReturnedItemsReport> reportItems = dao.getReturnedItemsReport(hotelId, startDate, endDate, visible);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("id", reportItems.get(i).getId());
				itemDetails.put("orderId", reportItems.get(i).getOrderId());
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("orderDate", reportItems.get(i).getOrderDate());
				itemDetails.put("waiterId", reportItems.get(i).getWaiterId());
				itemDetails.put("inhouse", dao.getOrderType(reportItems.get(i).getInhouse()));
				itemDetails.put("reason", reportItems.get(i).getReason());
				itemDetails.put("qty", reportItems.get(i).getQty());
				itemDetails.put("rate", reportItems.get(i).getRate());
				itemDetails.put("total", reportItems.get(i).getTotal());
				itemDetails.put("title", reportItems.get(i).getTitle());
				itemDetails.put("authorizer", reportItems.get(i).getAuthorizer());
				itemDetails.put("returnTime", reportItems.get(i).getReturnTime());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "RETURNED ORDER REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getComplimentaryItemsReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getComplimentaryItemsReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		ArrayList<ReturnedItemsReport> reportItems = dao.getComplimentaryItemsReport(hotelId, startDate, endDate, visible);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("id", reportItems.get(i).getId());
				itemDetails.put("orderId", reportItems.get(i).getOrderId());
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("orderDate", reportItems.get(i).getOrderDate());
				itemDetails.put("waiterId", reportItems.get(i).getWaiterId());
				itemDetails.put("inhouse", dao.getOrderType(reportItems.get(i).getInhouse()));
				itemDetails.put("qty", reportItems.get(i).getQty());
				itemDetails.put("rate", reportItems.get(i).getRate());
				itemDetails.put("total", reportItems.get(i).getTotal());
				itemDetails.put("title", reportItems.get(i).getTitle());
				itemDetails.put("time", reportItems.get(i).getReturnTime());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "COMPLIMENTARY ORDER REPORT");
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
		ArrayList<ItemWiseReport> reportItems = dao.getItemwiseReport(hotelId, startDate, endDate);
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
			outObj.put("name", "FOOD ITEMWISE REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDepartmentWiseConsumptionReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getDepartmentWiseConsumptionReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject reportObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<ConsumptionReport> reportItems = dao.getConsumptionReport(hotelId, startDate, endDate, AccessManager.DEPARTMENT_FOOD);
		try {
			for(int i=1; i<4; i++) {
				for (int j = 0; j < reportItems.size(); j++) {
					itemDetails = new JSONObject();
					itemDetails.put("title", reportItems.get(j).getTitle());
					itemDetails.put("category", reportItems.get(j).getCategory());
					itemDetails.put("qty", reportItems.get(j).getQty());
					itemDetails.put("compQty", reportItems.get(j).getCompQty());
					itemDetails.put("totalSaleQty", reportItems.get(j).getTotalSaleQty());
					itemDetails.put("totalCompQty", reportItems.get(j).getTotalCompQty());
					itemDetails.put("totalQty", reportItems.get(j).getTotalQty());
					itemDetails.put("percentOfTotalQty", reportItems.get(j).getPercentOfTotalQty());
					itemDetails.put("rate", reportItems.get(j).getRate());
					itemDetails.put("total", reportItems.get(j).getTotal());
					itemDetails.put("totalSale", reportItems.get(j).getTotalSale());
					itemDetails.put("departmentSale", reportItems.get(j).getDepartmentSale());
					itemDetails.put("totalAfterDiscount", reportItems.get(j).getTotalAfterDiscount());
					itemDetails.put("percentOfDepartmentSale", reportItems.get(j).getPercentOfDepartmentSale());
					itemDetails.put("percentOfTotalSale", reportItems.get(j).getPercentOfTotalSale());
					itemsArr.put(itemDetails);
				}
				
				if(i==AccessManager.DEPARTMENT_FOOD) {
					reportObj.put("food",itemsArr);
					reportItems = dao.getConsumptionReport(hotelId, startDate, endDate, AccessManager.DEPARTMENT_NON_ALCOHOLIC_BEVERAGE);
				}
				else if(i==AccessManager.DEPARTMENT_NON_ALCOHOLIC_BEVERAGE) {
					reportObj.put("non_alc_beverage",itemsArr);
					reportItems = dao.getConsumptionReport(hotelId, startDate, endDate, AccessManager.DEPARTMENT_ALCOHOLIC_BEVRAGE);
				}
				else
					reportObj.put("alc_beverage",itemsArr);
				itemsArr = new JSONArray();
					
			}
			outObj.put("report", reportObj);
			outObj.put("name", "DEPARTMENTWISE CONSUMPTION REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getIncentivisedItemReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getIncentivisedItemReport(@QueryParam("hotelId") String hotelId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		ArrayList<IncentiveReport> reportItems = dao.getIncentivisedItemReport(hotelId);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("title", reportItems.get(i).getTitle());
				itemDetails.put("incentive", reportItems.get(i).getIncentive());
				itemDetails.put("qty", reportItems.get(i).getQuantity());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "INCENTIVISED ITEMWISE REPORT");
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
		ArrayList<ItemWiseReport> reportItems = dao.getLiquorReport(hotelId, startDate, endDate);
		int totalQuantity = 0;
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("title", reportItems.get(i).getTitle());
				itemDetails.put("category", reportItems.get(i).getCategory());
				itemDetails.put("qty", reportItems.get(i).getQty());
				itemDetails.put("total", reportItems.get(i).getQty());
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
		BigDecimal totalBar = new BigDecimal("0.0");
		BigDecimal totalFood = new BigDecimal("0.0");
		BigDecimal totalBarSale = new BigDecimal("0.0");
		BigDecimal totalFoodSale = new BigDecimal("0.0");
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
				itemDetails.put("totalIncentive", reportItemsBar.getIncentive().add(reportItemsFood.getIncentive()));
				itemDetails.put("totalSale", reportItemsBar.getSale().add(reportItemsFood.getSale()));
				itemsArr.put(itemDetails);
				totalBar.add(reportItemsBar.getIncentive());
				totalBarSale.add(reportItemsBar.getSale());
				totalFood.add(reportItemsFood.getIncentive());
				totalFoodSale.add(reportItemsFood.getSale());
			}
			outObj.put("report", itemsArr);
			outObj.put("totalBarIncentive", totalBar);
			outObj.put("totalBarSale", totalBarSale);
			outObj.put("totalFoodIncentive", totalFood);
			outObj.put("totalFoodSale", totalFoodSale);
			outObj.put("totalIncentive", totalFood.add(totalBar));
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
		BigDecimal totalIncentive = new BigDecimal("0.0");
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
						totalIncentive.add(reportItems.get(j).getIncentive());
					}
					isBar = false;
				}
				captainDetails.put("items", itemsArr);
				captainDetails.put("totalIncentive", totalIncentive);
				totalIncentive = new BigDecimal("0.0");
				captainsArr.put(captainDetails);
			}
			outObj.put("report", captainsArr);
			outObj.put("name", "ITEMWISE INCENTIVE REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDeliveryReport")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getDeliveryReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate, 
			@QueryParam("endDate") String endDate, @QueryParam("userId") String userId) {
		AccessManager dao = new AccessManager(false);
		JSONArray itemsArr = new JSONArray();
		JSONArray captainsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject deliveryBoyDetails = null;
		JSONObject itemDetails = null;
		boolean visible = false;
		if(dao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		ArrayList<Employee> deliveryBoys = dao.getAllDeliveryEmployee(hotelId);
		ArrayList<DeliveryReport> reportItems;
		BigDecimal totalAmount = new BigDecimal("0.0");
		try {
			captainsArr = new JSONArray();
			for (int i = 0; i < deliveryBoys.size(); i++) {
				deliveryBoyDetails = new JSONObject();
				itemsArr = new JSONArray();
				reportItems = dao.getDeliveryReport(hotelId, deliveryBoys.get(i).getEmployeeId(), startDate, endDate, visible);
				deliveryBoyDetails.put("userId", deliveryBoys.get(i).getEmployeeId());
				deliveryBoyDetails.put("name", deliveryBoys.get(i).getFirstName());
				for (int j = 0; j < reportItems.size(); j++) {
					itemDetails = new JSONObject();
					itemDetails.put("billNo", reportItems.get(j).getBillNo());
					itemDetails.put("dispatchtime", reportItems.get(j).getDispatchTime());
					itemDetails.put("total", reportItems.get(j).getTotal());
					itemsArr.put(itemDetails);
					totalAmount.add(reportItems.get(j).getTotal());
				}
				deliveryBoyDetails.put("items", itemsArr);
				deliveryBoyDetails.put("totalAmount", totalAmount);
				totalAmount = new BigDecimal("0.0");
				captainsArr.put(deliveryBoyDetails);
			}
			outObj.put("report", captainsArr);
			itemsArr = new JSONArray();
			outObj.put("name", "ORDERWISE DELIVERY REPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCashBalance")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getCashBalance(@QueryParam("hotelId") String hotelId, @QueryParam("section") String section) {
		AccessManager dao = new AccessManager(false);
		JSONObject outObj = new JSONObject();
		BigDecimal cashBal = dao.getCashBalance(hotelId, section);
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
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj = dao.applyDiscount(inObj.getString("hotelId"), inObj.getString("orderId"),
					inObj.getString("discountCode"));
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/removeDiscount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String removeDiscount(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.removeDiscount(inObj.getString("hotelId"), inObj.getString("orderId")));
			
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
		AccessManager dao = new AccessManager(true);
		JSONObject outObj = new JSONObject();
		JSONObject inObj = null;
		try {
			inObj = new JSONObject(jsonObject);
			String content = inObj.getString("content");
			long count = content.chars().filter(num -> num == ';').count();
			System.out.println("Starting updates to server. Count :" + count + ". Hotel: "+ inObj.getString("hotelId"));
			boolean status = dao.syncOnServer(inObj.getString("hotelId"), content);
			outObj.put("status", status);
			outObj.put("count", 0);
			if (status) {
				outObj.put("count", count);
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
			outObj.put("status", false);
			if(logArray.length>100) {
				outObj.put("status", true);
			}
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
	
	public void SendEmail(String hotelId, String subject, String emailText, String header) {

		this.SendEmailWithAttachment(hotelId, subject, emailText, header, null);
	}
	
	public void SendEmailWithAttachment(String hotelId, String subject, String emailText, String header,
			String[] filePaths) {
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
		if(recipents.size()==0)
			return;
		String headerTemplete = "<html>"
				+ "<head><style>.alert {padding: 15px;margin-bottom: 20px;border: 1px solid transparent;border-radius: 4px;}"
				+ ".alert-warning {color: #8a6d3b;background-color: #fcf8e3;border-color: #faebcc;}</style></head>"
				+ "<body style='color:#797979; background:#f2f2f2;'><p>Dear Sir/Madam, </p>";

		if(!header.equals(""))
			headerTemplete = header;
		String footerTemplete = "<p>Thank you, </p><p>Kind Regards, </p><p> OrderOn Support. </p>"
				+ "<p>If you have any other questions, please feel free to reach out to us at +91-98-67334779 or write to us at support@orderon.co.in. Happy ordering!</p></body></html>";
		StringBuilder builder = new StringBuilder();
		builder.append(headerTemplete);
		builder.append(emailText);
		builder.append(footerTemplete);
		if(filePaths==null)
			email.sendEmail(recipents, subject, builder.toString());
		else
			email.sendEmailWithAttachment(recipents, subject, builder.toString(), filePaths);
	}
	
	
	public boolean SendSms(String hotelId, String smsText, Designation designation) {

		AccessManager dao = new AccessManager(false);
		ArrayList<Employee> employees = dao.getEmployeesByDesignation(hotelId, designation);
		Hotel hotel = dao.getHotelById(hotelId);
		if(!hotel.getHasSms())
			return false;
		if(!smsText.equals("")) {
			SendSMS sms = new SendSMS();
			if(employees.size() >0) {
				sms.sendSms(smsText, employees.get(0).getContactNumber());
				return true;
			}
		}
		return false;
	}
	
	public boolean SendEmailAndSMS(String hotelId, String subject, String emailText, String smsText, String header
			, Designation designation) {
		this.SendEmail(hotelId, subject, emailText, header);
		return this.SendSms(hotelId, smsText, designation);
	}

	private static final byte[] kaffineDrawerCode = { 27, 112, 0, 100, (byte) 250 };

	public void cashdrawerOpen(String hotelId, AccessManager dao) {
		
		if(Configurator.getIsServer())
			return;
		byte[] open = { 27, 112, 0, 25, (byte) 251 };
		if (hotelId.equals("ka0001")) {
			open = kaffineDrawerCode;
		} else
			return;
		// visit http://keyhut.com/popopen4.htm
		if(SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC){
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
		MenuItem menu = null;
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String offerValue = inObj.getString("offerValue");
			if(inObj.getInt("offerType") == 2) {
				 menu = dao.getMenuItemByTitle(inObj.getString("hotelId"), offerValue);
				 offerValue = menu.getMenuId();
			}
			outObj = dao.addLoyaltyOffer(inObj.getString("name"), inObj.getInt("offerType"), inObj.getInt("points"), offerValue,
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
			 @QueryParam("customerNumber") String customerNumber) {
		AccessManager dao = new AccessManager(false);
		JSONArray loyaltyOfferArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject loyaltyDetails = null;
		ArrayList<LoyaltyOffer> loyaltyOffers = null;
		Customer customer = null;
		try {
			outObj.put("status", false);
			if(!hotelId.equals("")) {
				if(customerNumber != null && customerNumber.length()>0) {
					customer = dao.getCustomerDetails(hotelId, customerNumber);
					loyaltyOffers = dao.getAllLoyaltyOffersForCustomer(hotelId, customer);
				}else
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
				if(loyaltyOffers.get(i).getOfferType()==2)
					loyaltyDetails.put("offerValue", dao.getMenuById(hotelId, loyaltyOffers.get(i).getOfferValue()).getTitle());
				loyaltyDetails.put("offerQuantity", loyaltyOffers.get(i).getOfferQuantity());
				loyaltyDetails.put("points", loyaltyOffers.get(i).getPoints());
				if(customer != (null))
					loyaltyDetails.put("pointToRupee", dao.getLoyaltySettingByUserType(hotelId, customer.getUserType()).getPointToRupee());
				
				loyaltyDetails.put("startDate", loyaltyOffers.get(i).getStartDate());
				loyaltyDetails.put("expiryDate", loyaltyOffers.get(i).getExpiryDate());
				loyaltyDetails.put("userType", loyaltyOffers.get(i).getUserType());
				loyaltyDetails.put("usageLimit", loyaltyOffers.get(i).getUsageLimit());
				loyaltyDetails.put("hasUsageLimit", loyaltyOffers.get(i).gethasUsageLimit());
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
				outObj.put("userType", loyalty.getUserType());
				outObj.put("offerTypeView", loyalty.getOfferTypeView());
				outObj.put("offerValue", loyalty.getOfferValue());
				if(loyalty.getOfferType()==2)
					outObj.put("offerValue", dao.getMenuById(hotelId, loyalty.getOfferValue()).getTitle());
				outObj.put("offerQuantity", loyalty.getOfferQuantity());
				outObj.put("points", loyalty.getPoints());
				outObj.put("usageLimit", loyalty.getUsageLimit());
				outObj.put("hasUsageLimit", loyalty.gethasUsageLimit());
				outObj.put("minBill", loyalty.getMinBill());
				
				outObj.put("startDate", dao.formatDate(loyalty.getStartDate(), "yyyy/MM/dd", "yyyy-MM-dd"));
				outObj.put("expiryDate", dao.formatDate(loyalty.getExpiryDate(), "yyyy/MM/dd", "yyyy-MM-dd"));
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
						inObj.getInt("loyaltyId"), inObj.getInt("redeemablePoints"));
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

	@POST
	@Path("/v1/updateLoyaltySettings")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateLoyaltySettings(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			JSONArray settings = inObj.getJSONArray("settings");
			for(int i=0; i<settings.length(); i++) {
				dao.editLoyaltySettings(inObj.getString("hotelId"), 
						settings.getJSONObject(i).getString("userType"), 
						settings.getJSONObject(i).getInt("requiredPoints"), 
						new BigDecimal(settings.getJSONObject(i).getDouble("pointToRupee")));
			}
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	//--------------------------------------------Reservations

	@POST
	@Path("/v1/addNewReservation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addNewReservation(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
				dao.createNewReservation(inObj.getString("hotelId"), inObj.getInt("maleCount")
						, inObj.getInt("femaleCount"), inObj.getInt("childrenCount"), inObj.getString("bookedTime"),
						inObj.getString("customerName"), inObj.getString("mobileNo"), inObj.getBoolean("isPriorityCust")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/addNewWaitList")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addNewWaitList(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
				dao.createNewWaitList(inObj.getString("hotelId"), inObj.getInt("maleCount")
						, inObj.getInt("femaleCount"), inObj.getInt("childrenCount"),inObj.getString("customerName"), 
						inObj.getString("mobileNo"), inObj.getBoolean("isPriorityCust")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/getReservations")
	@Produces(MediaType.APPLICATION_JSON)
	public String getReservations(@QueryParam("hotelId") String hotelId, @QueryParam("bookingDate") String bookingDate) {
		AccessManager dao = new AccessManager(false);
		JSONObject reservationObj = null;
		JSONArray reservationArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		try {
			ArrayList<Reservation> reservations = dao.getReservations(hotelId, bookingDate);
			
			for(int j=0; j<2; j++) {
				for (int i = 0; i < reservations.size(); i++) {
					reservationObj = new JSONObject();
					reservationObj.put("reservationId", reservations.get(i).getReservationId());
					reservationObj.put("customerId", reservations.get(i).getCustomerId());
					reservationObj.put("customerName", reservations.get(i).getCustomerName());
					reservationObj.put("mobileNumber", reservations.get(i).getMobileNumber());
					reservationObj.put("isPriorityCust", reservations.get(i).getIsPriorityCust());
					reservationObj.put("maleCount", reservations.get(i).getMaleCount());
					reservationObj.put("femaleCount", reservations.get(i).getFemaleCount());
					reservationObj.put("childrenCount", reservations.get(i).getChildrenCount());
					reservationObj.put("bookingDate", reservations.get(i).getBookingDate());
					reservationObj.put("bookedTime", reservations.get(i).getBookingTime());
					reservationObj.put("state", reservations.get(i).getState());
					reservationObj.put("timeStamp", reservations.get(i).getTimeStamp());
					reservationArr.put(reservationObj);
				}
				if(j==0) {
					outObj.put("reservations", reservationArr);
					reservations = dao.getWaitList(hotelId, bookingDate);
				}else {
					outObj.put("waitlist", reservationArr);
				}
				reservationArr = new JSONArray();
			}
			reservationObj = new JSONObject();
			reservationObj.put("booked", AccessManager.RESERVATION_STATE_BOOKED);
			reservationObj.put("cancelled", AccessManager.RESERVATION_STATE_CANCELLED);
			reservationObj.put("delayed", AccessManager.RESERVATION_STATE_DELAYED);
			reservationObj.put("waiting", AccessManager.RESERVATION_STATE_WAITING);
			reservationObj.put("seated", AccessManager.RESERVATION_STATE_SEATED);
			reservationArr.put(reservationObj);
			outObj.put("states", reservationArr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/editReservation")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String editReservation(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
					dao.editReservation(inObj.getString("hotelId"), inObj.getInt("reservationId"), inObj.getInt("maleCount")
							, inObj.getInt("femaleCount"), inObj.getInt("childrenCount"), inObj.getString("bookedTime")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateReservationState")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateReservationState(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status",
					dao.updateReservationState(inObj.getString("hotelId"), inObj.getInt("reservationId"), inObj.getInt("state")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/transferReservationToTable")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String transferReservationToTable(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", -1);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			
			JSONArray tablesArr = inObj.getJSONArray("tableIds");
			String[] tableIds = new String[tablesArr.length()];
			for (int i = 0; i < tableIds.length; i++) {
				tableIds[i] = tablesArr.getString(i);
			}
			Reservation reservation = dao.getReservation(inObj.getString("hotelId"), inObj.getInt("reservationId"));
			outObj = dao.newOrder(inObj.getString("hotelId"), inObj.getString("userId"), tableIds,
					reservation.getCovers(), reservation.getCustomerName(), reservation.getMobileNumber(), reservation.getCustomerAddress(),
					reservation.getAllergyInfo(), inObj.has("section")?inObj.getString("section"):"");
			dao.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	//--------------------------------------

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
						+ "  ";
			}
		} else {
			for (String titleBuilder : titleSplit) {
				title +=" " + titleBuilder.toUpperCase() + " ";
			}
		}

		return title;
	}

	private void print(String html, String printerName, int copies, Hotel hotel) {
		if(SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC) {
			JEditorPane ed1 = new JEditorPane("text/html", html);
			PrinterJob pjob = PrinterJob.getPrinterJob();
			JFrame frame = new JFrame("KOT");
			frame.setSize(hotel.getKOTWidth(), hotel.getKOTHeight());
			frame.setContentPane(ed1);
			frame.setVisible(true);
			PageFormat preformat = pjob.defaultPage();
			preformat.setOrientation(PageFormat.PORTRAIT);        
			Paper paper = new Paper();
			double height = (double)hotel.getKOTHeight();
			double width = (double)(hotel.getKOTWidth() / hotel.getKOTDivisor());
			paper.setSize(width, height);
			paper.setImageableArea(0, 0, width, height);
			// Orientation
			preformat.setPaper(paper);
			PageFormat postFormat = pjob.validatePage(preformat);
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
			for (PrintService printService : printServices) {
				try {
					if (printService.getName().equals(printerName)) {
						pjob.setPrintService(printService);
						pjob.setPrintable(new Printer(ed1), postFormat);
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
}