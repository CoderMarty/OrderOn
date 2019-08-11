package com.orderon.services;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
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

import com.orderon.commons.CardType;
import com.orderon.commons.Configurator;
import com.orderon.commons.EncryptDecryptString;
import com.orderon.commons.ExpenseType;
import com.orderon.commons.OnlineOrderingPortals;
import com.orderon.commons.OnlinePaymentType;
import com.orderon.commons.PaymentType;
import com.orderon.commons.ReferencesForReveiw;
import com.orderon.commons.SendEmail;
import com.orderon.commons.SendSMS;
import com.orderon.commons.UserType;
import com.orderon.dao.AccessManager;
import com.orderon.dao.AccessManager.Account;
import com.orderon.dao.AccessManager.Attendance;
import com.orderon.dao.AccessManager.Charge;
import com.orderon.dao.AccessManager.Collection;
import com.orderon.dao.AccessManager.CollectionWiseReportA;
import com.orderon.dao.AccessManager.CollectionWiseReportB;
import com.orderon.dao.AccessManager.ConsumptionReport;
import com.orderon.dao.AccessManager.Customer;
import com.orderon.dao.AccessManager.CustomerForOrdering;
import com.orderon.dao.AccessManager.CustomerReport;
import com.orderon.dao.AccessManager.DBTransaction;
import com.orderon.dao.AccessManager.DailyDiscountReport;
import com.orderon.dao.AccessManager.DailyOperationReport;
import com.orderon.dao.AccessManager.DeliveryReport;
import com.orderon.dao.AccessManager.Designation;
import com.orderon.dao.AccessManager.Discount;
import com.orderon.dao.AccessManager.Employee;
import com.orderon.dao.AccessManager.EntityString;
import com.orderon.dao.AccessManager.Expense;
import com.orderon.dao.AccessManager.Flag;
import com.orderon.dao.AccessManager.GrossSaleReport;
import com.orderon.dao.AccessManager.Group;
import com.orderon.dao.AccessManager.HomeDelivery;
import com.orderon.dao.AccessManager.IncentiveReport;
import com.orderon.dao.AccessManager.ItemWiseReport;
import com.orderon.dao.AccessManager.KitchenStation;
import com.orderon.dao.AccessManager.LoyaltyOffer;
import com.orderon.dao.AccessManager.LoyaltySetting;
import com.orderon.dao.AccessManager.MPNotification;
import com.orderon.dao.AccessManager.Material;
import com.orderon.dao.AccessManager.MenuItem;
import com.orderon.dao.AccessManager.MonthReport;
import com.orderon.dao.AccessManager.Notification;
import com.orderon.dao.AccessManager.OnlineOrder;
import com.orderon.dao.AccessManager.OnlineOrderingPortal;
import com.orderon.dao.AccessManager.OnlineOrderingSalesReport;
import com.orderon.dao.AccessManager.Order;
import com.orderon.dao.AccessManager.OrderAddOn;
import com.orderon.dao.AccessManager.OrderItem;
import com.orderon.dao.AccessManager.OrderSpecification;
import com.orderon.dao.AccessManager.Outlet;
import com.orderon.dao.AccessManager.PaymentWiseSalesReport;
import com.orderon.dao.AccessManager.PromotionalCampaign;
import com.orderon.dao.AccessManager.Report;
import com.orderon.dao.AccessManager.ReportBuffer;
import com.orderon.dao.AccessManager.ReturnedItemsReport;
import com.orderon.dao.AccessManager.Schedule;
import com.orderon.dao.AccessManager.Section;
import com.orderon.dao.AccessManager.ServerLog;
import com.orderon.dao.AccessManager.ServiceLog;
import com.orderon.dao.AccessManager.Settings;
import com.orderon.dao.AccessManager.Specifications;
import com.orderon.dao.AccessManager.SubCollection;
import com.orderon.dao.AccessManager.Table;
import com.orderon.dao.AccessManager.TableUsage;
import com.orderon.dao.AccessManager.Tax;
import com.orderon.dao.AccessManager.User;
import com.orderon.dao.AccessManager.YearlyReport;
import com.orderon.dao.AccountManager;
import com.orderon.dao.AttendanceManager;
import com.orderon.dao.ChargeManager;
import com.orderon.dao.CollectionManager;
import com.orderon.dao.ComboManager;
import com.orderon.dao.CustomerManager;
import com.orderon.dao.DesignationManager;
import com.orderon.dao.DiscountManager;
import com.orderon.dao.EmployeeManager;
import com.orderon.dao.ExpenseManager;
import com.orderon.dao.FlagManager;
import com.orderon.dao.GroupManager;
import com.orderon.dao.InventoryManager;
import com.orderon.dao.LoyaltyManager;
import com.orderon.dao.MaterialManager;
import com.orderon.dao.MenuItemManager;
import com.orderon.dao.NotificationManager;
import com.orderon.dao.OnlineOrderingPortalManager;
import com.orderon.dao.OrderAddOnManager;
import com.orderon.dao.OrderManager;
import com.orderon.dao.OutletManager;
import com.orderon.dao.PaymentManager;
import com.orderon.dao.RatingManager;
import com.orderon.dao.ReportBufferManager;
import com.orderon.dao.ReportManager;
import com.orderon.dao.ScheduleManager;
import com.orderon.dao.SectionManager;
import com.orderon.dao.ServerManager;
import com.orderon.dao.ServiceManager;
import com.orderon.dao.SpecificationManager;
import com.orderon.dao.SubCollectionManager;
import com.orderon.dao.TableManager;
import com.orderon.dao.TaxManager;
import com.orderon.dao.TransactionHistoryManager;
import com.orderon.dao.UserManager;
import com.orderon.interfaces.IAccount;
import com.orderon.interfaces.IAttendance;
import com.orderon.interfaces.ICharge;
import com.orderon.interfaces.ICollection;
import com.orderon.interfaces.ICombo;
import com.orderon.interfaces.ICustomer;
import com.orderon.interfaces.IDesignation;
import com.orderon.interfaces.IDiscount;
import com.orderon.interfaces.IEmployee;
import com.orderon.interfaces.IExpense;
import com.orderon.interfaces.IFlag;
import com.orderon.interfaces.IGroup;
import com.orderon.interfaces.IInventory;
import com.orderon.interfaces.ILoyalty;
import com.orderon.interfaces.ILoyaltySettings;
import com.orderon.interfaces.IMaterial;
import com.orderon.interfaces.IMenuItem;
import com.orderon.interfaces.INotification;
import com.orderon.interfaces.IOnlineOrderingPortal;
import com.orderon.interfaces.IOrder;
import com.orderon.interfaces.IOrderAddOn;
import com.orderon.interfaces.IOrderItem;
import com.orderon.interfaces.IOutlet;
import com.orderon.interfaces.IPayment;
import com.orderon.interfaces.IPromotionalCampaign;
import com.orderon.interfaces.IRating;
import com.orderon.interfaces.IReport;
import com.orderon.interfaces.IReportBuffer;
import com.orderon.interfaces.ISchedules;
import com.orderon.interfaces.ISection;
import com.orderon.interfaces.IServer;
import com.orderon.interfaces.IService;
import com.orderon.interfaces.ISpecification;
import com.orderon.interfaces.ISubCollection;
import com.orderon.interfaces.ITable;
import com.orderon.interfaces.ITax;
import com.orderon.interfaces.ITier;
import com.orderon.interfaces.ITransactionHistory;
import com.orderon.interfaces.IUser;
import com.orderon.interfaces.IUserAuthentication;

@Path("/Services")
public class Services {

	// static Logger logger = Logger.getLogger(Services.class);
	private static final String api_version = "3.4.3";
	private static final String stable_api_version = "3.4.3";
	private static final String billStyle = "<html style='max-width:377px;'><head><style>p{margin: 0 0 10px;} .table-condensed>thead>tr>th, .table-condensed>tbody>tr>th, .table-condensed>tfoot>tr>th, .table-condensed>thead>tr>td,"
			+ " h1, h2, h3, h4, h5, h6, .h1, .h2, .h3, .h4, .h5, .h6 {font-family: inherit;font-weight: 500;line-height: 1.1;color: inherit;}"
			+ " .table-condensed>tbody>tr>td, .table-condensed>tfoot>tr>td {padding: 1px;} .centered{text-align: center;} .text-right{text-align: right;} .mt0{margin-top: 0px;} .mt5{margin-top: 5px;} .mt-20{margin-top: 20px;}"
			+ " .table {width: 100%;max-width: 100%;margin-bottom: 20px;}"
			+ " .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td,.table>tbody>tr>td, "
			+ " .table>tfoot>tr>td {border-top: 1px solid #ddd;}"
			+ " .table>thead>tr>th {vertical-align: bottom;border-bottom: 2px solid #ddd; background-color: #ffa300; color: #fff; font-weight: bolder;} th {text-align: left;}"
			+ " h3, .h3 {font-size: 18px;} h6, .h6 {font-size: 12px;} .mb0{margin-bottom: 0px;} .mb10 {margin-bottom: 10px;} .mt-10 {margin-top: 10px;}"
			+ " .ml{margin-left: 5px;} .ml2{margin-left: 20px;} .ml25{margin-left: 5px;} .mr25{margin-right: 5px;} .mr{margin-right: 20px;} .mr1{margin-right: 10px;}"
			+ " .mr-100 {margin-right: 100px;}"
			+ " .table-xs>thead>tr>td, .table-xs>tbody>tr>td, .table-xs>tfoot>tr>td {padding-top: 1px; padding-bottom: 1px; padding-left: 5px; padding-right: 5px;}"
			+ " .table-condensed>thead>tr>th, .table-condensed>tbody>tr>th,.table-condensed>tfoot>tr>th, .table-condensed>thead>tr>td,"
			+ " .table-condensed>tbody>tr>td, .table-condensed>tfoot>tr>td,.table-xs>tfoot>tr>th, .table-xs>thead>tr>th, .table-xs>tbody>tr>th{padding: 5px;}" 
			+ " .pull-right {float: right !important;}"
			+ " .eod-hr-big{ margin-top: 2px; margin-bottom: 2px; margin-left: 25px; margin-right: 25px; line-height: 50%; border: 0.8px solid #ffa300;}"
			+ " .eod-hr-small{ margin-top: 0; margin-bottom: 0; margin-left: 25px; margin-right: 25px; line-height: 40%; border: 1px dashed black; border: 0.5px dashed #ffa300;}"
			+ " .eod-header{ background: #ffa300; color: white; font-weight: bold; margin-left: 20px; margin-right: 20px; padding-top: 5px; padding-bottom: 5px;}"
			+ " .w1{width:50%} .w2{width:10%} .w3{width:20%}"
			+ " .billHeader {text-align: center; margin-top: 0px; margin-bottom: 0px;} .billHeader2 {text-align: center; margin-top: 5px; margin-bottom: 0px;}"
			+ " .addOn-td{border-top: none !important;padding-top: 5px !important;padding-bottom: 5px !important;line-height: 1 !important;font-size: 12px !important}"
			+ "</style></head><body style='max-width:377px; margin: 0px; font-family:Arial; background:#efefef;'>";

	@GET
	@Path("/v1/heartbeat")
	@Produces(MediaType.APPLICATION_JSON)
	public String hearbeat() {
		JSONObject outObj = new JSONObject();
		try {
			String systemId = Configurator.getSystemId();
			if(systemId.isEmpty()) {
				outObj.put("status", false);
				outObj.put("message", "Pleae add SystemId to config file");
				return outObj.toString();
			}
			outObj.put("hotelId", systemId);
			outObj.put("systemId", systemId);
			outObj.put("ip", Configurator.getIp());
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getHotelName")
	@Produces(MediaType.TEXT_PLAIN)
	public String getHotel(@QueryParam("hotelId") String outletId) {
		IOutlet dao = new OutletManager(false);
		Outlet outlet = dao.getOutlet(outletId);
		if (outlet != null) {
			return outlet.getName();
		}
		return "";
	}

	@GET
	@Path("/v1/getHotelDetailsJSON")
	@Produces(MediaType.APPLICATION_JSON)
	@Deprecated
	public String getHotelDetailv1(@QueryParam("hotelId") String hotelId) {
		
		return this.getHotelDetails(hotelId);
	}

	@GET
	@Path("/v3/getHotelDetailsJSON")
	@Produces(MediaType.APPLICATION_JSON)
	public String getHotelDetail(@QueryParam("systemId") String systemId) {
		
		return this.getHotelDetails(systemId);
	}
	
	private String getHotelDetails(String systemId) {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		JSONObject tempObj = new JSONObject();
		
		IOutlet dao = new OutletManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		try {
			Settings settings = dao.getSettings(systemId);
			Outlet outlet = dao.getOutlet(systemId);
			outObj.put("hotelName", outlet.getName());
			outObj.put("corporateId", outlet.getCorporateId());
			outObj.put("hotelId", outlet.getOutletId());
			outObj.put("outletId", outlet.getOutletId());
			outObj.put("systemId", outlet.getOutletId());
			outObj.put("hotelAddress", outlet.getAddress());
			outObj.put("hotelContact", outlet.getContact());
			outObj.put("gstNumber", outlet.getGstNumber());
			outObj.put("vatNumber", outlet.getVatNumber());
			outObj.put("hotelDescription", outlet.getCompanyName());
			
			JSONObject location = outlet.getLocation();
			if(location.has("place")) {
				outObj.put("location", location.getString("place"));
			}
			
			outObj.put("hotelType", settings.getHotelType());
			outObj.put("hasKds", settings.getHasKds());
			outObj.put("hasEod", settings.getHasEod());
			outObj.put("hasDirectCheckout", settings.getHasDirectCheckout());
			outObj.put("hasNC", settings.getHasNC());
			outObj.put("hasBar", settings.getHasBar());
			outObj.put("hasKot", settings.getHasKot());
			outObj.put("hasServer", settings.getHasServer());
			outObj.put("hasNewOrderScreen", settings.getHasNewOrderScreen());
			outObj.put("hasLoyalty", settings.getHasLoyalty());
			outObj.put("hotelWebsite", settings.getWebsite());
			outObj.put("printMethod", settings.getPrintMethod());
			outObj.put("isServer", Configurator.getIsServer());
			outObj.put("getCustomerDb", settings.getLoadCustomerDb());
			outObj.put("allowCancelationOnPhone", settings.getAllowItemCancellationOnPhone());
			outObj.put("isCaptainBasedOrdering", settings.getIsCaptainBasedOrdering());
			outObj.put("deductionType", settings.getDeductionType());
			outObj.put("isWalletOnline", settings.getIsWalletOnline());
			outObj.put("isWalletOffline", settings.getIsWalletOffline());
			outObj.put("isCreditActive", settings.getIsCreditActive());
			outObj.put("printLogo", settings.getPrintLogo());
			outObj.put("hasConciseBill", settings.getHasConciseBill());
			outObj.put("theme", settings.getTheme());
			outObj.put("hasFullRounding", settings.getHasFullRounding());
			outObj.put("capturePayments", settings.getCapturePayments());
			outObj.put("smsApiKey", settings.getSmsAPIKey());
			outObj.put("downloadReports", settings.getDownloadReports());
			
			ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortalsForIntegration(systemId);
			for(int i=0; i<portals.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("name", portals.get(i).getName());
				tempObj.put("portal", portals.get(i).getPortal());
				tempObj.put("portalId", portals.get(i).getId());
				outArr.put(tempObj);
			}
			outObj.put("integrations", outArr);
			
			outArr = new JSONArray();
			for(int i=0; i<settings.getOnlinePlatforms().length; i++) {
				tempObj = new JSONObject();
				tempObj.put("platform", settings.getOnlinePlatforms()[i]);
				outArr.put(tempObj);
			}
			outObj.put("onlineOrderPlatforms", outArr);
			
			outArr = new JSONArray();
			for(int i=0; i<settings.getSections().length; i++) {
				tempObj = new JSONObject();
				tempObj.put("section", settings.getSections()[i]);
				outArr.put(tempObj);
			}
			outObj.put("sections", outArr);
			
			outObj.put("hasSms", settings.getIsSmsEnabled());
			if (settings.getHasServer()) {
				IServer serverDao = new ServerManager(false);
				ServerLog server = serverDao.getLastServerLog(systemId);
				outObj.put("updateStatus", server.getStatus());
				outObj.put("lastUpdateTime", server.getUpdateTime());
			}
			outObj.put("hasCashDrawer", settings.getHasCashDrawer());
			outObj.put("imgLocation", "http://"+Configurator.getIp()+":8080/Images");
			
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getOutlet")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOutlet(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();
		
		IOutlet dao = new OutletManager(false);
		Outlet outlet = dao.getOutlet(outletId);
		try {
			if(outlet==null) {
				outObj.put("status", false);
				outObj.put("message", "Outlet not found");
				return outObj.toString();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSONObject(outlet).toString();
	}

	@GET
	@Path("/v3/getPromoSmsBalance")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPromoSmsBalance(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();
		
		IOutlet dao = new OutletManager(false);
		Settings settings = dao.getSettings(outletId);
		try {
			if(settings==null) {
				outObj.put("status", false);
				outObj.put("message", "Outlet not found");
				return outObj.toString();
			}
			outObj.put("status", true);
			outObj.put("promoSmsBalance", settings.getPromotionalSMSBalance());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getOutletsForCorporate")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOutletsForCorporate(@QueryParam("corporateId") String corporateId) {
		JSONObject outObj = new JSONObject();
		
		IOutlet dao = new OutletManager(false);
		ArrayList<Outlet> outlets = dao.getOutletsForCorporate(corporateId);
		try {
			if(outlets.size()==0) {
				outObj.put("status", false);
				outObj.put("message", "Outlets not found");
				return outObj.toString();
			}
			outObj.put("outlets", outlets);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getOutlets")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOutlets(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();
		
		IOutlet dao = new OutletManager(false);
		ArrayList<Outlet> outlets = dao.getOutlets(outletId);
		try {
			if(outlets.size()==0) {
				outObj.put("status", false);
				outObj.put("message", "Outlets not found");
				return outObj.toString();
			}
			outObj.put("outlets", outlets);
		} catch (JSONException e) {
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
		
		IUserAuthentication userDao = new UserManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String userId = "";
			String outletId = "";
			String password = "";

			if(!inObj.has("hotelId")) {
				outObj.put("message", "OutletId not found.");
				return outObj.toString();
			}else if(!inObj.has("userId")) {
				outObj.put("message", "UserId not found.");
				return outObj.toString();
			}else if(!inObj.has("passwd")) {
				outObj.put("message", "Password not found.");
				return outObj.toString();
			}
			
			userId = inObj.getString("userId");
			outletId = inObj.getString("hotelId");
			password = inObj.getString("passwd");
			int userLevel = inObj.has("userLevel")?inObj.getInt("userLevel"):0;
			
			User user = null;
			
			if(userLevel == AccessManager.USER_AUTHENTICATION_ANY)
				user = userDao.validateUser(outletId, userId, password);
			else if(userLevel == AccessManager.USER_AUTHENTICATION_TOP)
				user = userDao.validateAccess1(outletId, userId, password);
			else if(userLevel == AccessManager.USER_AUTHENTICATION_ADMIN)
				user = userDao.validateAdmin(outletId, userId, password);
			else if(userLevel == AccessManager.USER_AUTHENTICATION_OWNER)
				user = userDao.validateOwner(outletId, userId, password);
			else if(userLevel == AccessManager.USER_AUTHENTICATION_SECRET)
				user = userDao.validateOwner(outletId, userId, password);
			else if(userLevel == AccessManager.USER_AUTHENTICATION_KITCHEN)
				user = userDao.validKDSUser(outletId, userId, password);
			
			if (user == null) {
				outObj.put("message", "Unable to validate User.");
				return outObj.toString();
			} else {
				outObj.put("status", true);
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
	@Path("/v1/validateMPUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String validateMPUser(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		AccessManager managerDao = new AccessManager(false);
		IOutlet outletDao = new OutletManager(false);
		IUserAuthentication dao = new UserManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			byte[] auth = authString.getBytes();
			String[] decoded = Base64.decodeAsString(auth).split(":");
			String systemId = "";

			if(!inObj.has("systemId")) {
				outObj.put("message", "SystemId not found.");
				return outObj.toString();
			}else if(decoded.length<2) {
				outObj.put("message", "UserId or Password not found.");
				return outObj.toString();
			}
			
			systemId = inObj.getString("systemId");
			
			Settings settings = outletDao.getSettings(systemId);

			User user = dao.validateUser(systemId, decoded[0], decoded[1]);

			outObj.put("systemUpdated", false);
			if (user == null) {
				outObj.put("message", "Unable to validate User.");
				return outObj.toString();
			} else {
				outObj.put("status", true);
				outObj.put("fullName", user.getName());
				outObj.put("type", user.getUserType());
				outObj.put("authToken", user.getAuthToken());
				outObj.put("hotelType", settings.getHotelType());
				if(!Configurator.getIsServer()) {
					while(!settings.getVersion().equals(api_version)) {
						System.out.println("Updating database.");
						if(!managerDao.updateDatabase(systemId, settings.getVersion(), api_version)) {
							System.out.println("Error updating database.");
							break;
						}
						settings = outletDao.getSettings(systemId);
						outObj.put("systemUpdated", true);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/createNewDb")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String createNewDb(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		AccessManager dao = new AccessManager(false);
		
		IUserAuthentication userDao = new UserManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			byte[] auth = authString.getBytes();
			String[] decoded = Base64.decodeAsString(auth).split(":");
			String outletId = "";

			if(!inObj.has("hotelId")) {
				outObj.put("message", "HotelId not found.");
				return outObj.toString();
			}else if(decoded.length<2) {
				outObj.put("message", "UserId or Password not found.");
				return outObj.toString();
			}
			
			outletId = inObj.getString("hotelId");
			
			if(!outletId.equals("h0002")) {
				outObj.put("message", "ACCESS DENIED. 1");
				return outObj.toString();
			}
			if(userDao.validateAdmin(outletId, decoded[0], decoded[1])!=null) {
				outObj.put("message", "ACCESS DENIED. 2");
				return outObj.toString();
			}
			dao.initDatabase(inObj.getString("id"));
			dao.restaurantSetup(inObj.getString("hotelName"), inObj.getString("hotelCode"), inObj.getString("id"), stable_api_version);
			outObj.put("message", "Database created for "+ inObj.getString("id"));
		} catch (Exception e) {
			dao.rollbackTransaction();
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
		
		IUserAuthentication dao = new UserManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String userId = "";
			String outletId = "";
			String authToken = "";

			if(!inObj.has("hotelId")) {
				outObj.put("message", "OutletId not found.");
				return outObj.toString();
			}else if(!inObj.has("userId")) {
				outObj.put("message", "UserId not found.");
				return outObj.toString();
			}else if(!inObj.has("authToken")) {
				outObj.put("message", "Authorization Token not found.");
				return outObj.toString();
			}
			
			userId = inObj.getString("userId");
			outletId = inObj.getString("hotelId");
			authToken = inObj.getString("authToken");
			
			outObj = dao.validateToken(outletId, userId, authToken);
			
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
		
		IUserAuthentication dao = new UserManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", false);
			String userId = "";
			String outletId = "";

			if(!inObj.has("hotelId")) {
				outObj.put("message", "OutletId not found.");
				return outObj.toString();
			}else if(!inObj.has("userId")) {
				outObj.put("message", "UserId not found.");
				return outObj.toString();
			}
			
			userId = inObj.getString("userId");
			outletId = inObj.getString("hotelId");

			if (dao.removeToken(outletId, userId))
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
	@Path("/v1/addUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addUser(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IUser dao = new UserManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			String userId = "";
			String outletId = "";
			int userType = 0;
			String password = "";
			String employeeId = "";

			if(!inObj.has("hotelId")) {
				outObj.put("message", "OutletId not found.");
				return outObj.toString();
			}else if(!inObj.has("userId")) {
				outObj.put("message", "UserId not found.");
				return outObj.toString();
			}else if(!inObj.has("userType")) {
				outObj.put("message", "UserType not found.");
				return outObj.toString();
			}else if(!inObj.has("userPasswd")) {
				outObj.put("message", "Password not found.");
				return outObj.toString();
			}else if(!inObj.has("employeeId")) {
				outObj.put("message", "Employee not found.");
				return outObj.toString();
			}
			
			userId = inObj.getString("userId");
			outletId = inObj.getString("hotelId");
			userType = UserType.valueOf(inObj.getString("userType")).getValue();
			password = inObj.getString("userPasswd");
			outletId = inObj.getString("hotelId");
			employeeId = inObj.getString("employeeId");

			String auth = Base64.decodeAsString(password.getBytes());
			User systemUser = dao.getUser(inObj.getString("hotelId"), inObj.getString("systemUserId"));
			
			outObj = dao.addUser(systemUser, outletId, userId, employeeId, userType, auth);
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
		
		IUser dao = new UserManager(false);
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
	@Path("/v1/getUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getUser(@QueryParam("hotelId") String hotelId, @QueryParam("userId") String userId) {
		JSONObject userDetails = new JSONObject();
		
		IUser dao = new UserManager(false);
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
	@Path("/v1/updateUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateUser(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IUser dao = new UserManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			User systemUser = dao.getUser(inObj.getString("hotelId"), inObj.getString("systemUserId"));
			int userType = UserType.valueOf(inObj.getString("userType")).getValue();
			outObj = dao.updateUser(systemUser, inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("oldPassword"), inObj.getString("password"), userType);
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
		
		IUser dao = new UserManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			User systemUser = dao.getUser(inObj.getString("hotelId"), inObj.getString("systemUserId"));
			User selectedUser = dao.getUser(inObj.getString("hotelId"), inObj.getString("userId"));
			outObj = dao.deleteUser(systemUser, inObj.getString("hotelId"), selectedUser);
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
		
		IEmployee dao = new EmployeeManager(false);
		IOutlet outletDao = new OutletManager(false);
		IUser userDao = new UserManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");

			User systemUser = userDao.getUser(inObj.getString("hotelId"), inObj.getString("userId"));
			com.orderon.commons.Designation d = com.orderon.commons.Designation.valueOf(inObj.getString("designation"));
			UserType userType = UserType.getType(systemUser.getUserType());
			if(userType.getUserLevel() > d.getUserLevel()) {
				outObj.put("message", "UNAUTHORISED");
				return outObj.toString();
			}
			
			Outlet outlet = outletDao.getOutlet(outletId);

			String employeeId = dao.addEmployee(outlet, inObj.getString("firstName"),
					inObj.getString("middleName"), inObj.getString("surName"), inObj.getString("address"),
					inObj.getString("contactNumber"), inObj.getString("dob"), inObj.getString("sex"),
					inObj.getString("hiringDate"), inObj.getString("designation"),
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
		
		IEmployee dao = new EmployeeManager(false);
		IUser userDao = new UserManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (inObj.getString("image") != "No image") {
				deleteFile(inObj.getString("hotelId"), inObj.getString("employeeId") + ".jpg", "Employees");
			}

			User systemUser = userDao.getUser(inObj.getString("hotelId"), inObj.getString("userId"));
			com.orderon.commons.Designation d = com.orderon.commons.Designation.valueOf(inObj.getString("designation"));
			UserType userType = UserType.getType(systemUser.getUserType());
			if(userType.getUserLevel() > d.getUserLevel()) {
				outObj.put("message", "UNAUTHORISED");
				return outObj.toString();
			}
			
			outObj.put("status",
					dao.updateEmployee(inObj.getString("hotelId"), inObj.getString("employeeId"),
							inObj.getString("firstName"), inObj.getString("middleName"), inObj.getString("surName"),
							inObj.getString("address"), inObj.getString("contactNumber"), inObj.getString("dob"),
							inObj.getString("sex"), inObj.getString("hiringDate"), inObj.getString("designation"),
							inObj.getInt("salary"), inObj.getInt("bonus"),
							inObj.getString("image"), inObj.getString("email")));
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
		
		IEmployee dao = new EmployeeManager(false);
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
		
		IEmployee dao = new EmployeeManager(false);
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
			@QueryParam("designation") String designation) {
		JSONArray outArr = new JSONArray();
		JSONObject employeeObj = null;
		
		IEmployee dao = new EmployeeManager(false);
		try {
			ArrayList<Employee> employees = dao.getEmployeesByDesignation(hotelId, designation.toUpperCase());
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
		JSONArray outArr = new JSONArray();
		JSONObject employeeObj = null;
		
		IEmployee dao = new EmployeeManager(false);
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
	@Path("/v1/getDeliveryPersons")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDeliveryPersons(@QueryParam("hotelId") String hotelId) {
		JSONArray personsArr = new JSONArray();
		JSONObject personDetails = null;
		
		IEmployee dao = new EmployeeManager(false);
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

	@POST
	@Path("/v1/deleteEmployee")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deteleEmployee(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IEmployee dao = new EmployeeManager(false);
		IUser userDao = new UserManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			User user = userDao.getUserByEmpId(inObj.getString("hotelId"), inObj.getString("employeeId"));
			if(user.getUserId().equals("Zomato")) {
				inObj.put("message", "Cannot delete this user.");
				return inObj.toString();
			}
			if (user != null) {
				User systemUser = userDao.getUser(inObj.getString("hotelId"), inObj.getString("systemUserId"));
				outObj = userDao.deleteUser(systemUser, inObj.getString("hotelId"), user);
				if(!outObj.getBoolean("status")) {
					return outObj.toString();
				}
			}
			
			outObj.put("status", dao.deleteEmployee(inObj.getString("hotelId"), inObj.getString("employeeId")));
			
			deleteFile(inObj.getString("hotelId"), inObj.getString("employeeId") + ".jpg", "Employees");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v3/addMenuItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addMenuItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String groups = inObj.getJSONArray("groups").toString();
			String flags = inObj.getJSONArray("flags").toString();
			String taxes = inObj.getJSONArray("taxes").toString();
			String charges = inObj.getJSONArray("charges").toString();
			
			if (dao.itemExists(inObj.getString("hotelId"), inObj.getString("title")) != null) {
				outObj.put("error", "Item Exists");
			} else {
				String menuId = dao.addMenuItem(inObj.getString("hotelId"), inObj.getString("title"),
						inObj.getString("description"), inObj.getString("collection"), inObj.getString("subCollection"), inObj.getString("station"),
						flags, groups, inObj.getInt("preparationTime"), new BigDecimal(Double.toString(inObj.getDouble("deliveryRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("dineInRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("dineInNonAcRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("onlineRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("zomatoRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("swiggyRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("uberEatsRate"))),
						new BigDecimal(Double.toString(inObj.getDouble("comboPrice"))),
						new BigDecimal(Double.toString(inObj.getDouble("costPrice"))), 
						inObj.getString("image"), inObj.getString("coverImgUrl"), 
						inObj.getInt("incentiveType"), inObj.getInt("incentive"), inObj.getString("shortForm"),
						taxes, charges, inObj.getBoolean("isRecomended"),
						inObj.getBoolean("isTreats"), inObj.getBoolean("isDefault"), inObj.getBoolean("isBogo"), inObj.getBoolean("isCombo"), 
						new BigDecimal(Double.toString(inObj.getDouble("comboReducedPrice"))), inObj.getBoolean("isAddon")
						, inObj.getBoolean("syncOnZomato"), inObj.getBoolean("gstInclusive"),
						inObj.getString("discountType"), new BigDecimal(inObj.getDouble("discountValue")));

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
	@Path("/v3/updateMenuItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMenuItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String groups = inObj.getJSONArray("groups").toString();
			String flags = inObj.getJSONArray("flags").toString();
			String taxes = inObj.getJSONArray("taxes").toString().replaceAll("\"", "");
			String charges = inObj.getJSONArray("charges").toString().replaceAll("\"", "");

			if (inObj.getString("image") != "No image") {
				deleteFile(inObj.getString("hotelId"), inObj.getString("menuId") + ".jpg", "MenuItems");
			}

			outObj.put("status",
					dao.updateMenuItem(inObj.getString("hotelId"), inObj.getString("menuId"), inObj.getString("title"),
							inObj.getString("description"), inObj.getString("collection"), inObj.getString("subCollection"), inObj.getString("station"),
							flags, groups, inObj.getInt("preparationTime"), 
							new BigDecimal(Double.toString(inObj.getDouble("deliveryRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("dineInRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("dineInNonAcRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("onlineRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("zomatoRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("swiggyRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("uberEatsRate"))),
							new BigDecimal(Double.toString(inObj.getDouble("comboPrice"))),
							new BigDecimal(Double.toString(inObj.getDouble("costPrice"))), 
							inObj.getString("image"), inObj.getString("coverImgUrl"), 
							inObj.getInt("incentiveType"), inObj.getInt("incentive"), inObj.getString("shortForm"),
							taxes, charges, inObj.getBoolean("isRecomended"),
							inObj.getBoolean("isTreats"), inObj.getBoolean("isDefault"), inObj.getBoolean("isBogo"), inObj.getBoolean("isCombo"), 
							new BigDecimal(Double.toString(inObj.getDouble("comboReducedPrice"))), inObj.getBoolean("isAddon"),
							inObj.getBoolean("syncOnZomato"), inObj.getBoolean("gstInclusive"),
							inObj.getString("discountType"), new BigDecimal(inObj.getDouble("discountValue"))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/updateMenuItems")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateMenuItems(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", false);
			String outletId = inObj.getString("outletId");
			JSONArray menuItems = inObj.getJSONArray("menuItems");
			JSONObject menuItem = null;
			for (int i = 0; i < menuItems.length(); i++) {
				menuItem = menuItems.getJSONObject(i);
				if(menuItem.has("item_in_stock")) {
					dao.updateMenuItemStockState(outletId, Integer.toString(menuItem.getInt("item_id")), menuItem.getInt("item_in_stock"));
				}else {
					dao.updateMenuItemStateOnZomato(outletId, Integer.toString(menuItem.getInt("item_id")), menuItem.getInt("item_is_active")==1?true:false);
				}
			}
			outObj.put("status", true);
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
		
		ITable dao = new TableManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.transferTable(inObj.getString("hotelId"), inObj.getString("oldTableId"),
					inObj.getString("newTableId"), inObj.getString("orderId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAllAttendance")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllAttendance(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = null;
		JSONArray shiftArr = new JSONArray();
		
		IAttendance dao = new AttendanceManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			outObj.put("status", false);
			String serviceDate = serviceDao.getServiceDate(hotelId);
			
			for (int x = 1; x <= 2; x++) {
				ArrayList<Attendance> attendance = dao.getAllAttendance(hotelId, x, serviceDate);
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
		
		IAttendance dao = new AttendanceManager(true);
		IEmployee employeeDao = new EmployeeManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			String hotelId =  inObj.getString("hotelId");
			String employeeId =  inObj.getString("employeeId");
			String serviceDate = serviceDao.getServiceDate(hotelId);
			
			Employee employee = employeeDao.getEmployeeById(hotelId, employeeId);
			if (dao.hasCheckedIn(hotelId, employeeId, inObj.getInt("shift"), serviceDate)) {
				outObj.put("status", false);
				outObj.put("message", employee.getFirstName() + " has Already Checked In.");
				dao.rollbackTransaction();
				return outObj.toString();
			}
			String checkInTime = dao.checkInEmployee(hotelId, employeeId,
					inObj.getInt("shift"), serviceDate);
			outObj.put("checkInTime", checkInTime);
			outObj.put("attendanceId", dao.getLastAttendanceId(hotelId));
			outObj.put("status", true);
			dao.commitTransaction(hotelId);
		} catch (Exception e) {
			dao.rollbackTransaction();
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
		IAttendance dao = new AttendanceManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			outObj.put("status", false);
			outObj.put("remark", "");
			if (dao.hasCheckedInForFirstShift(hotelId, employeeId, serviceDao.getServiceDate(hotelId))) {
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
	@Path("/v1/removeAttendance")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String removeAttendance(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		AttendanceManager dao = new AttendanceManager(false);
		IEmployee employeeDao = new EmployeeManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId =  inObj.getString("hotelId");
			String employeeId =  inObj.getString("employeeId");
			String serviceDate = serviceDao.getServiceDate(hotelId);
			
			Employee employee = employeeDao.getEmployeeById(hotelId, employeeId);
			if (!dao.removeAttendance(hotelId, employeeId, inObj.getInt("shift"), serviceDate)) {
				outObj.put("status", false);
				outObj.put("message", "Cannot cancel " + employee.getFirstName() + "'s record. Please try again.");
				return outObj.toString();
			}
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
		
		AttendanceManager dao = new AttendanceManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			if(!inObj.has("attendanceId")) {
				outObj.put("message", "Could not authorise. Please try again.");
				return outObj.toString();
			}

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
		
		IAttendance dao = new AttendanceManager(true);
		IEmployee employeeDao = new EmployeeManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId =  inObj.getString("hotelId");
			String employeeId =  inObj.getString("employeeId");
			String serviceDate = serviceDao.getServiceDate(hotelId);

			Employee employee = employeeDao.getEmployeeById(hotelId, employeeId);
			if (dao.hasCheckedIn(hotelId, employeeId, inObj.getInt("shift"), serviceDate)) {
				outObj.put("status", false);
				outObj.put("message", employee.getFirstName() + " has Already been Marked Absent.");
			}else
				outObj.put("status", dao.markAbsent(hotelId, employeeId, inObj.getInt("shift"), serviceDate));
			dao.commitTransaction(hotelId);
			
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
		
		IAttendance dao = new AttendanceManager(true);
		IEmployee employeeDao = new EmployeeManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			String hotelId =  inObj.getString("hotelId");
			String employeeId =  inObj.getString("employeeId");
			String serviceDate = serviceDao.getServiceDate(hotelId);
			
			Employee employee = employeeDao.getEmployeeById(hotelId, employeeId);
			if (dao.hasCheckedIn(hotelId, employeeId, inObj.getInt("shift"), serviceDate)) {
				outObj.put("status", false);
				outObj.put("message", employee.getFirstName() + " has Already been Marked Excused.");
			}else
				outObj.put("status", dao.markExcused(hotelId, employeeId, inObj.getString("reason"), inObj.getInt("shift"), serviceDate));
			dao.commitTransaction(hotelId);
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
		
		IAttendance dao = new AttendanceManager(false);
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
	@Path("/v1/getUserTables")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUserTables(@QueryParam("hotelId") String outletId, @QueryParam("userId") String userId) {
		JSONArray tablesArr = new JSONArray();
		JSONObject tableDetails = null;
		
		ITable tableDao = new TableManager(false);
		IOutlet outletDao = new OutletManager(false);
		Settings settings = outletDao.getSettings(outletId);
		if(settings==null) {
			JSONObject outObj = new JSONObject();
			try {
				outObj.put("status", false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return outObj.toString();
		}
		ArrayList<TableUsage> tables = tableDao.getTableUsage(settings);
		try {
			for (int i = 0; i < tables.size(); i++) {
				tableDetails = new JSONObject();
				tableDetails.put("tableId", tables.get(i).getTableId());
				tableDetails.put("tableIndex", i+1);
				tableDetails.put("orderId", tables.get(i).getOrderId());
				tableDetails.put("section", tables.get(i).getSection());
				tableDetails.put("userId", tables.get(i).getUserId());
				tableDetails.put("state", tables.get(i).getState());
				tableDetails.put("type", tables.get(i).getType());
				tableDetails.put("showTableView", tables.get(i).getShowTableView());
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
		JSONArray tablesArr = new JSONArray();
		JSONObject tableDetails = null;
		
		ITable tableDao = new TableManager(false);
		ArrayList<TableUsage> tables = tableDao.getTables(hotelId);
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
	
	@GET
	@Path("/v3/getTables")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTablesv3(@QueryParam("hotelId") String hotelId) {
		JSONObject tableDetails = new JSONObject();
		
		ITable tableDao = new TableManager(false);
		ArrayList<TableUsage> tables = tableDao.getTablesNoSubTables(hotelId);
		try {
			tableDetails.put("tables", tables);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableDetails.toString();
	}

	@POST
	@Path("/v1/switchTable")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String switchTable(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		ITable dao = new TableManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			
			String orderId = inObj.has("orderId")?inObj.getString("orderId"):"";
			
			boolean status = dao.switchTable(inObj.getString("hotelId"), orderId, inObj.getString("oldTableNumber"),
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

		ITable dao = new TableManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj = dao.moveItem(inObj.getString("hotelId"), inObj.getString("oldTableNumber"),
					inObj.getString("newTableNumber"), inObj.getJSONArray("orderItemIds"), false);
			if(outObj.getBoolean("status")) {
				dao.commitTransaction(inObj.getString("hotelId"));
			}else {
				dao.rollbackTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/mergeTables")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String mergeTables(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		ITable dao = new TableManager(true);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj = dao.mergeTables(inObj.getString("hotelId"), inObj.getString("oldTableNumber"),
					inObj.getString("newTableNumber"));
			if(outObj.getBoolean("status")) {
				dao.commitTransaction(inObj.getString("hotelId"));
			}else {
				dao.rollbackTransaction();
			}
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

		ITable dao = new TableManager(true);
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
	
	//-----------------------------GroupManager API'S

	@POST
	@Path("/v3/addGroup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addGroup(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IGroup dao = new GroupManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			JSONArray itemIds = new JSONArray();
			JSONArray items = inObj.getJSONArray("groupItems");
			for(int i=0; i<items.length(); i++) {
				itemIds.put(menuDao.getMenuItemByTitle(hotelId, items.get(i).toString()).getMenuId());
			}
			
			outObj = dao.addGroup(hotelId, itemIds.toString(), inObj.getString("name"), 
					inObj.getString("description"), inObj.getInt("max"), inObj.getInt("min"), inObj.getBoolean("isActive"), 
					inObj.getString("title"), inObj.getString("subTitle"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/updateGroup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateGroup(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IGroup dao = new GroupManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			JSONArray itemIds = new JSONArray();
			JSONArray items = inObj.getJSONArray("groupItems");
			for(int i=0; i<items.length(); i++) {
				itemIds.put(menuDao.getMenuItemByTitle(hotelId, items.get(i).toString()).getMenuId());
			}

			outObj.put("status", dao.updateGroup(hotelId, inObj.getInt("groupId"), itemIds.toString(), inObj.getString("name"), 
					inObj.getString("description"), inObj.getInt("max"), inObj.getInt("min"), inObj.getBoolean("isActive"), 
					inObj.getString("title"), inObj.getString("subTitle")));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getGroups")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroups(@QueryParam("hotelId") String hotelId) {
		JSONArray outArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONArray itemArr = new JSONArray();
		JSONObject itemObj = new JSONObject();
		JSONArray itemIds = new JSONArray();
		
		IGroup dao = new GroupManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		ArrayList<Group> groups = dao.getGroups(hotelId);
		MenuItem item;
		
		try {
			for (Group group : groups) {
				itemIds = group.getItemIds();
				itemArr = new JSONArray();
				outObj = new JSONObject();
				for (int i=0; i<itemIds.length(); i++) {
					item = menuDao.getMenuById(hotelId, itemIds.get(i).toString());
					itemObj = new JSONObject();
					if(item==null) {
						continue;
					}
					itemObj.put("menuId", item.getId());
					itemObj.put("title", item.getTitle());
					itemArr.put(itemObj);
				}
				outObj.put("id", group.getId());
				outObj.put("max", group.getMax());
				outObj.put("min", group.getMin());
				outObj.put("description", group.getDescription());
				outObj.put("isActive", group.getIsActive());
				outObj.put("itemIds", group.getItemIds());
				outObj.put("items", itemArr);
				outObj.put("name", group.getName());
				outArr.put(outObj);
			}
			outObj = new JSONObject();
			outObj.put("groups", outArr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getGroup")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGroup(@QueryParam("hotelId") String hotelId, @QueryParam("groupId") int groupId) {
		IGroup dao = new GroupManager(false);
		return new JSONObject(dao.getGroupById(hotelId, groupId)).toString();
	}

	@POST
	@Path("/v1/deleteGroup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteGroup(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IGroup dao = new GroupManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteGroup(inObj.getString("hotelId"), inObj.getInt("groupId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	//-----------------------------CollectionManager API'S


	@POST
	@Path("/v1/isExistingCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String isExistingCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		ICollection dao = new CollectionManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.collectionExists(inObj.getString("hotelId"), inObj.getString("name")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/addCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		ICollection dao = new CollectionManager(false);
		try {
			inObj = new JSONObject(jsonObject);

			outObj = dao.addCollection(inObj.getString("hotelId"), inObj.getString("name"), inObj.getString("description"),
					inObj.getString("image"), inObj.getInt("collectionOrder"), inObj.getBoolean("isActiveOnZomato"), 
					inObj.getBoolean("hasSubCollection"), inObj.getBoolean("isSpecialCombo"), inObj.getJSONArray("tags"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/editCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String editCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		ICollection dao = new CollectionManager(false);
		try {
			inObj = new JSONObject(jsonObject);

			outObj = dao.editCollection(inObj.getInt("id"), inObj.getString("hotelId"), inObj.getString("name"), inObj.getString("description"),
					inObj.getBoolean("isActive"), inObj.getInt("collectionOrder"), inObj.getBoolean("isActiveOnZomato"), 
					inObj.getBoolean("hasSubCollection"), inObj.getBoolean("isSpecialCombo"), inObj.getJSONArray("tags"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCollections")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCollections(@QueryParam("hotelId") String hotelId, @QueryParam("getActive") Boolean getActive,
			@QueryParam("getSpecialCombos") Boolean getSpecialCombos) {
		JSONArray collectionArr = new JSONArray();
		
		ICollection dao = new CollectionManager(false);
		ISubCollection subCollDao = new SubCollectionManager(false);
		Collection collection = null; 
		ArrayList<Collection> collections = null;
		if(getSpecialCombos == null) {
			getSpecialCombos = false;
		}
		if(getActive)
			collections = dao.getActiveCollections(hotelId);
		else if(getSpecialCombos) {
			collections = dao.getComboCollections(hotelId);
		}else
			collections = dao.getCollections(hotelId);
		ArrayList<SubCollection> subCollections = null;
		for (int i = 0; i < collections.size(); i++) {
			collection =  collections.get(i);
			if(collection.getHasSubCollection()) {
				subCollections = subCollDao.getSubCollections(hotelId, collection.getName());
				for(int j=0; j<subCollections.size(); j++) {
					collectionArr.put(subCollections.get(j).getName());
				}
			}else {
				collectionArr.put(collection.getName());
			}
		}
		return collectionArr.toString();
	}

	@GET
	@Path("/v3/getCollections")
	@Produces(MediaType.APPLICATION_JSON)
	public String getActiveCollections(@QueryParam("hotelId") String hotelId, @QueryParam("getActive") Boolean getActive) {
		JSONArray collectionArr = new JSONArray();
		JSONObject collectionObj = null;
		
		ICollection dao = new CollectionManager(false);
		ISubCollection subCDao = new SubCollectionManager(false);
		ISchedules scheduleDao = new ScheduleManager(false);
		ArrayList<Collection> collections = null;
		ArrayList<SubCollection> subCollections = null;
		if(getActive)
			collections = dao.getActiveCollections(hotelId);
		else
			collections = dao.getCollections(hotelId);
		try {
			for (int i = 0; i < collections.size(); i++) {
				collectionObj = new JSONObject();
				collectionObj.put("id", collections.get(i).getId());
				collectionObj.put("name", collections.get(i).getName());
				collectionObj.put("order", collections.get(i).getCollectionOrder());
				collectionObj.put("hasSubCollection", collections.get(i).getHasSubCollection());
				collectionObj.put("description", collections.get(i).getDescription());
				collectionObj.put("isActive", collections.get(i).getIsActive());
				collectionObj.put("imgUrl", collections.get(i).getImgUrl());
				collectionObj.put("isSpecialCombo", collections.get(i).getIsSpecialCombo());
				collectionObj.put("isActiveOnZomato", collections.get(i).getIsActiveOnZomato());
				collectionObj.put("tags", collections.get(i).getTags());
				if(collections.get(i).getHasSubCollection()) {
					subCollections = subCDao.getSubCollections(hotelId, collections.get(i).getName());
					collectionObj.put("subCollections", subCollections);
				}
				collectionObj.put("schedule", scheduleDao.getSchedules(hotelId, collections.get(i).getScheduleIds()));
				collectionArr.put(collectionObj);
			}
		} catch (JSONException e) {
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
		
		ICollection dao = new CollectionManager(false);
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

	//-----------------------------ScheduleManager API'S
	
	@POST
	@Path("/v3/addSchedule")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addSchedule(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		ISchedules dao = new ScheduleManager(false);
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
		JSONArray scheduleArr = new JSONArray();
		JSONObject scheduleObj = null;
		
		ISchedules dao = new ScheduleManager(false);
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
		
		ISchedules dao = new ScheduleManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteSchedule(inObj.getString("hotelId"), inObj.getInt("scheduleId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	//-----------------------------SubCollectionManager API'S
	
	@POST
	@Path("/v3/addSubCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addSubCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		ISubCollection dao = new SubCollectionManager(false);
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
	public String getSubCollections(@QueryParam("hotelId") String hotelId, @QueryParam("getActive") Boolean getActive) {
		JSONObject outObj = new JSONObject();
		
		ISubCollection dao = new SubCollectionManager(false);
		ArrayList<SubCollection> subCollections = null;
		if(getActive)
			subCollections = dao.getActiveSubCollections(hotelId);
		else
			subCollections = dao.getSubCollections(hotelId);
		try {
			outObj.put("subCollection", subCollections);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/deleteSubCollection")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteSubCollection(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		ISubCollection dao = new SubCollectionManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteSubCollection(inObj.getString("hotelId"), inObj.getInt("subCollectionId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	//-----------------------------Tier API'S

	@POST
	@Path("/v3/addTier")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addTier(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		ITier dao = new ChargeManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addTier(inObj.getString("corporateId"), inObj.getString("systemId"), inObj.getString("outletId"), 
					new BigDecimal(Double.toString(inObj.getDouble("value"))), inObj.getBoolean("chargeAlwaysApplicable"), 
					new BigDecimal(Double.toString(inObj.getDouble("minBillAMount"))))) {
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
		JSONObject outObj = new JSONObject();
		
		ITier dao = new ChargeManager(false);
		try {
			outObj.put("tiers", dao.getTiers(hotelId));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/deleteTier")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTier(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		ITier dao = new ChargeManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteTier(inObj.getString("hotelId"), inObj.getInt("tierId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	//-----------------------------Combo API's

	@POST
	@Path("/v3/addEDVCombo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addEDVCombo(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		ICollection collectionDao = new CollectionManager(false);
		ISubCollection subCollDao = new SubCollectionManager(false);
		IGroup groupDao = new GroupManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("outletId");
			
			//First: check if collection exists, if not add new collection.
			Collection collection = collectionDao.getCollectionByName(outletId, inObj.getString("collection"));
			if(collection != null) {
				if(!collection.getIsSpecialCombo()) {
					outObj.put("message", "Collection has to be a Special Combo Collection.");
					return outObj.toString();
				}
			}else {
				collectionDao.addCollection(outletId, inObj.getString("collection"), "", "", 1, 
						true, true, true, new JSONArray());
			}
			
			//Second: Add subcollection if not added.
			SubCollection subCollection = subCollDao.getSubCollectionByName(outletId, inObj.getString("subCollection"));
			if(subCollection == null) {
				subCollDao.addSubCollection(outletId, inObj.getString("subCollection"), inObj.getInt("subCollectionOrder"), inObj.getString("collection"));
			}
			
			//Third: Add all the groups which are a part of the combo.
			JSONArray groups = inObj.getJSONArray("groups");
			JSONObject group = null;
			for (int i=0; i<groups.length(); i++) {
				group = groups.getJSONObject(i);
				groupDao.addGroup(outletId, group.getJSONArray("itemIds").toString(), group.getString("name"), 
						"", 1, 1, true, group.getString("title"), group.getString("subTitle"));
			}
			
			JSONObject comboItem = inObj.getJSONObject("comboItem");
			
			BigDecimal rate = new BigDecimal(comboItem.getDouble("rate"));
			BigDecimal zero = new BigDecimal(0.0);
			
			menuDao.addMenuItem(outletId, comboItem.getString("title"), comboItem.getString("subTitle"), collection.getName(), subCollection.getName(), 
					"Kitchen", comboItem.getJSONArray("flags").toString(), groupDao.getLast2GroupIds(outletId).toString(), 0, rate, rate, rate, rate, rate, rate, rate, 
					zero, zero, comboItem.getString("imgUrl"), comboItem.getString("coverImgUrl"), 0, 0, "", comboItem.getJSONArray("taxes").toString(), 
					comboItem.getJSONArray("charges").toString(), false, false, false, false, false, zero, false, true, false, 
					comboItem.getString("discountType"), new BigDecimal(comboItem.getDouble("discountValue")));
			
			outObj.put("status", true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}


	@GET
	@Path("/v3/getCombos")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCombos(@QueryParam("outletId") String outletId) {
		
		ICombo dao = new ComboManager(false);
		return dao.getCombos(outletId).toString();
	}

	@POST
	@Path("/v3/deleteCombo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteCombo(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IGroup groupDao = new GroupManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("outletId");
			
			MenuItem menuItem = menuDao.getMenuById(outletId, inObj.getString("menuId"));
			menuDao.deleteItem(outletId, menuItem.getMenuId());
			deleteFile(outletId, menuItem.getMenuId() + ".jpg", "MenuItems");
			for (int i=0;i<menuItem.getGroups().length(); i++) {
				groupDao.deleteGroup(outletId, menuItem.getGroups().getInt(i));
			}
			outObj.put("status", true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	//-----------------------------Charges API'S

	@POST
	@Path("/v3/addCharge")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addCharge(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		ICharge dao = new ChargeManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addCharge(inObj.getString("corporateId"), inObj.getString("systemId"), inObj.getString("outletId"), 
					inObj.getString("name"), new BigDecimal(Double.toString(inObj.getDouble("value"))), inObj.getString("type"), 
					inObj.getBoolean("isActive"), inObj.getString("applicableOn"), inObj.getBoolean("isAlwaysApplicable"),
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
	public String getCharges(@QueryParam("systemId") String systemId, @QueryParam("getActive") Boolean getActive) {
		JSONObject outObj = new JSONObject();

		ICharge dao = new ChargeManager(false);
		ITier tierDao = new ChargeManager(false);
		ArrayList<Charge> charges = null;
		if(getActive)
			charges = dao.getActiveCharges(systemId);
		else
			charges = dao.getCharges(systemId);
		
		try {
			outObj.put("charges", charges);
			outObj.put("tiers", tierDao.getTiers(systemId));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return outObj.toString();
	}

	@GET
	@Path("/v3/getOrderCharges")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOrderCharges(@QueryParam("systemId") String systemId, @QueryParam("outletId") String outletId, 
			@QueryParam("getActive") Boolean getActive) {
		JSONObject outObj = new JSONObject();

		ICharge dao = new ChargeManager(false);
		ITier tierDao = new ChargeManager(false);
		ArrayList<Charge> charges = null;
		if(getActive)
			charges = dao.getActiveOrderCharges(systemId);
		else
			charges = dao.getOrderCharges(systemId);
		
		try {
			outObj.put("charges", charges);
			outObj.put("tiers", tierDao.getTiers(systemId, outletId));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/Integration/getCharges")
	@Produces(MediaType.APPLICATION_JSON)
	public String getChargesForIntegration(@QueryParam("systemId") String systemId, @QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();

		ICharge dao = new ChargeManager(false);
		ITier tierDao = new ChargeManager(false);
		try {
			outObj.put("charges", dao.getChargesForIntegration(systemId, outletId));
			outObj.put("tiers", tierDao.getTiers(systemId, outletId));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return outObj.toString();
	}

	@GET
	@Path("/v3/Integration/getOrderCharges")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOrderChargesForIntegration(@QueryParam("systemId") String systemId, @QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();

		ICharge dao = new ChargeManager(false);
		ITier tierDao = new ChargeManager(false);
		try {
			outObj.put("charges", dao.getOrderChargesForIntegration(systemId, outletId));
			outObj.put("tiers", tierDao.getTiers(systemId, outletId));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return outObj.toString();
	}

	@POST
	@Path("/v3/deleteCharge")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteCharge(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		ICharge dao = new ChargeManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteCharge(inObj.getString("systemId"), inObj.getInt("chargeId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	//-----------------------------Taxes API'S
	
	@POST
	@Path("/v3/addTax")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addTax(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		ITax dao = new TaxManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addTax(inObj.getString("corporateId"), inObj.getString("systemId"), inObj.getString("outletId"), inObj.getString("name"), 
					new BigDecimal(Double.toString(inObj.getDouble("value"))), inObj.getString("type"), inObj.getBoolean("isActive"))) {
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
	public String getTaxes(@QueryParam("systemId") String systemId, @QueryParam("getActive") Boolean getActive) {
		JSONObject outObj = new JSONObject();

		ITax dao = new TaxManager(false);
		ArrayList<Tax> taxes = null;
		if(getActive)
			taxes = dao.getActiveTaxes(systemId);
		else
			taxes = dao.getTaxes(systemId);
		try {
			outObj.put("taxes", taxes);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/deleteTax")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteTax(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		ITax dao = new TaxManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteTax(inObj.getString("systemId"), inObj.getInt("taxId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	//-----------------------------Flags API'S

	@POST
	@Path("/v3/addFlag")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addFlag(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IFlag dao = new FlagManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addFlag(inObj.getString("hotelId"), inObj.getString("name"), inObj.getString("groupId"))) {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getFlags")
	@Produces(MediaType.APPLICATION_JSON)
	public String getFlags(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();

		IFlag dao = new FlagManager(false);
		ArrayList<Flag> flags = dao.getFlags(hotelId);
		try {
			outObj.put("flags", flags);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/deleteFlag")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFlag(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IFlag dao = new FlagManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteFlag(inObj.getString("hotelId"), inObj.getInt("flagId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	private void printKOT(Outlet outlet, Settings settings, Order order, Boolean isCheckKOT, String userId, String sectionName) {

		String hotelId = outlet.getOutletId();
		String orderId = order.getOrderId();
		
		ISection sectionDao = new SectionManager(false);
		IOrderItem orderItemDao = new OrderManager(false);
		
		ArrayList<Section> sections = sectionDao.getSections(hotelId);
		Section section = null;
		if(!sectionName.isEmpty())
		for (Section s : sections) {
			if(s.getName().equals(sectionName)) {
				section = s;
				break;
			}
		}
		if(section == null) {
			for (Section s : sections) {
				if(s.getName().equals(order.getSection())) {
					section = s;
					break;
				}
			}
		}
		if(section == null) {
			section = sectionDao.getSectionByName(hotelId, "DEFAULT");
		}
		ArrayList<OrderItem> items = null;
		if(!isCheckKOT) {
			items = orderItemDao.getOrderedItemsForKOT(hotelId, orderId);
			orderItemDao.updateKOTStatus(hotelId, orderId);
		}else {
			items = orderItemDao.getOrderedItemsForReprintKOT(hotelId, orderId);
			defineKOT(items, orderItemDao, "", section.getCashierPrinter(), outlet, settings, order,
					1, false, true, userId);
			orderItemDao.updateKOTStatus(hotelId, orderId);
			return;
		}

		ArrayList<OrderItem> beverageItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> kitchenItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> pantryItems = new ArrayList<OrderItem>();
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
			} else if (items.get(i).getStation().equals("Pantry")) {
				pantryItems.add(items.get(i));
			} else if (items.get(i).getStation().equals("Outdoor")) {
				outDoorItems.add(items.get(i));
				kitchenItems.add(items.get(i));
			}
		}

		int copies = settings.getKOTCountInhouse();
		if (order.getInHouse() == AccessManager.HOME_DELIVERY)
			copies = settings.getKOTCountHomeDelivery();
		else if(order.getInHouse() == AccessManager.TAKE_AWAY) {
			copies = settings.getKOTCountTakeAway();
			if(order.getCustomerName() == "ZOMATO")
				copies = settings.getKOTCountZomato();
			else if(order.getCustomerName() == "ZOMATO PICKUP")
				copies = settings.getKOTCountZomato();
			else if(order.getCustomerName() == "SWIGGY")
				copies = settings.getKOTCountSwwigy();
			else if(order.getCustomerName() == "UBEREATS")
				copies = settings.getKOTCountUberEats();
			else if(order.getCustomerName() == "FOODPANDA")
				copies = settings.getKOTCountFoodPanda();
		}else if(order.getInHouse() == AccessManager.BAR)
			copies = settings.getKOTCountBar();
		else if(order.getInHouse() == AccessManager.NON_CHARGEABLE)
			copies = settings.getKOTCountNC();
		
		if (beverageItems.size() > 0) {
			defineKOT(beverageItems, orderItemDao, "BEVERAGE ORDER TAKING", section.getBeveragePrinter(), outlet, settings, order,
					copies, false, false, userId);
		}
		if (kitchenItems.size() > 0) {
			defineKOT(kitchenItems, orderItemDao, "KITCHEN ORDER TAKING", section.getKitchenPrinter(), outlet, settings, order,
					copies, false, false, userId);
		}
		if (barItems.size() > 0) {
			if (!hotelId.equals("sa0001"))
				defineKOT(barItems, orderItemDao, "BAR ORDER TAKING", section.getBarPrinter(), outlet, settings, order, copies,
						false, false, userId);
		}
		if (naBarItems.size() > 0) {
			if (!hotelId.equals("sa0001"))
				defineKOT(naBarItems, orderItemDao, "BAR ORDER TAKING", section.getBarPrinter(), outlet, settings, order, copies,
						false, false, userId);
		}
		if (pantryItems.size() > 0) {
				defineKOT(pantryItems, orderItemDao, "PANTRY ORDER TAKING", section.getBarPrinter(), outlet, settings, order, copies,
						false, false, userId);
		}
		if (outDoorItems.size() > 0) {
			if (!hotelId.equals("sa0001"))
				defineKOT(outDoorItems, orderItemDao, "OUTDOOR", section.getOutDoorPrinter(), outlet, settings, order, 2, false, false, userId);
			else
				defineKOT(outDoorItems, orderItemDao, "OUTDOOR", section.getOutDoorPrinter(), outlet, settings, order, 1, false, false, userId);
		}

		if (settings.getKOTCountSummary() == 1) {
			if (order.getInHouse() == 1)
				defineKOT(items, orderItemDao, "SUMMARY", section.getSummaryPrinter(), outlet, settings, order, settings.getKOTCountSummary(), false, false, userId);
		}
	}

	private void defineKOT(ArrayList<OrderItem> items, IOrderItem dao, String title, String printerStation,
			Outlet outlet, Settings settings, Order order, int copies, boolean isCanceled, boolean isCheckKOT, String userId) {
		
		//System.out.println("Sending print to : " + printerStation);
		StringBuilder out = new StringBuilder();
		
		int marginTop = 0;
		
		if(outlet.getOutletId().equals("jp0001") || outlet.getOutletId().equals("kvd0001")) {
			marginTop = 35;
		}
		if (title.equals("SUMMARY")) {
			marginTop = 0;
		}
		
		out.append("<h3 style='font-size:16px; margin-top:"+marginTop+"px; margin-bottom:5px; text-align: center'>");
		
		ITable tableDao = new TableManager(false);
		Table table = tableDao.getTableById(outlet.getOutletId(), order.getTableId().split(",")[0]);
		int kotNumber = 0;
		int botNumber = 0;
		if(items.size() >0) {
			kotNumber = items.get(0).getKotNumber();
			botNumber = items.get(0).getBotNumber();
		}
		if(kotNumber>0)
			out.append("KOT NO. " + kotNumber + " | "); 
		else if(botNumber>0)
			out.append("BOT NO. " + botNumber + " | "); 
		if (title.equals("SUMMARY"))
			out.append("ORDER TAKING SUMMARY");
		else if (isCanceled)
			out.append("CANCELED KOT");
		else {
			if(isCheckKOT)
				out.append("REPRINT KOT: ");
			
			if (order.getInHouse() == AccessManager.DINE_IN)
				out.append("TABLE NUMBER : "+ table.getTableId());
			else if (order.getInHouse() == AccessManager.HOME_DELIVERY)
				out.append("HOME DELIVERY");
			else if (order.getInHouse() == AccessManager.BAR)
				out.append("BAR ORDER");
			else if (order.getInHouse() == AccessManager.NON_CHARGEABLE)
				out.append("NC For " + order.getReference());
			else {
				IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
				ArrayList<OnlineOrderingPortal> portals =  portalDao.getOnlineOrderingPortals(outlet.getOutletId());
				String orderRef = order.getReference();
				if(order.getTakeAwayType() == AccessManager.COUNTER_PARCEL_ORDER) {
					out.append("COUNTER PARCEL");
				}else {
					for (OnlineOrderingPortal portal : portals) {
						if(order.getTakeAwayType() == portal.getId()) {
							int len = order.getReference().length();
							if(len > 5) {
								orderRef = orderRef.substring(len-4, len);
							}
							out.append(portal.getPortal()+ " ORDER: "+ orderRef);
						}
					}
				}
			}

			if (title.equals("OUTDOOR") && outlet.getOutletId().equals("sa0001"))
				out.append(" SANNIDHI KOT");
		}
		
		String name = "";

		if(settings.getIsCaptainBasedOrdering()){
			out.append("</h3><table style='width: 90%; padding: 0px; margin-left:25px; margin-bottom:0px; margin-top:0px;'><thead>");
			out.append("<tr style='padding: 0px; margin-top:3px; margin-bottom:0px;'>");
			out.append("<th style='padding:0px;'>Date</th><th style='padding:0px;'>Time</th><th style='padding:0px;'>Table</th>");
			out.append("<th style='padding:0px;'>User</th><th style='padding:0px;'>Pax</th><th style='padding:0px;'>Bill No.</th></tr>");
			out.append("</thead><tbody>");
			name = userId;
		}else {
			out.append("</h3><table style='width: 90%; padding: 0px; margin-left:25px; margin-bottom:0px; margin-top:0px;'><thead>");
			out.append("<tr style='padding: 0px; margin-top:3px; margin-bottom:0px;'>");
			out.append("<th style='padding:0px;'>Date</th><th style='padding:0px;'>Time</th><th style='padding:0px;'>Table</th>");
			out.append("<th style='padding:0px;'>Waiter</th><th style='padding:0px;'>Pax</th><th style='padding:0px;'>Bill No.</th></tr>");
			out.append("</thead><tbody>");

			if (table != null)
				name = table.getWaiterId();
		}
		String orderDate = "";
		
		try {
			AccessManager managerDao = new AccessManager(false);
			orderDate = managerDao.formatDate(order.getOrderDate(), "yyyy/MM/dd", "dd/MM/yyyy");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String time = LocalTime.parse(items.get(0).getSubOrderDate().substring(11, 16), DateTimeFormatter.ofPattern("HH:mm")).format(DateTimeFormatter.ofPattern("hh:mm a"));
		out.append("<tr><td style='padding:0px;'>" + orderDate + "</td><td style='padding:0px;'>" + time
				+ "<td style='padding:0px;'>" + order.getTableId() + "</td><td style='padding:0px;'>" + name
				+ "</td><td style='padding:0px;'>" + order.getNumberOfGuests() 
				+ "</td><td style='padding:0px;'>" + order.getBillNo() + "</td></tr>"
				+ "</tbody></table>"
				+ "<p style='margin:0px;'>------------------------------------------------------------------------------------------</p>"
				+ "<table class='table-condensed' style='width: 90%; padding: 0px; margin-left: 20px; margin-bottom:0px;>");
		
/*
  		if (isCanceled)
			out += "<thead style='font-size:12px;'><tr style='padding:0px; margin:0px;'><th style='padding:0px;'><u>QTY</u></th><th class='pull-left' style='width:75%; padding:0px;'>"
					+ "<u>DESCRIPTION</u></th></tr></thead><tbody>";
		else
			out += "<thead style='font-size:12px;'><tr style='padding:0px; margin:0px;'><th style='padding:0px;'><u>QTY</u></th><th class='pull-left' style='width:50%; padding:0px;'>"
					+ "<u>DESCRIPTION</u></th><th class='pull-right' style='padding:0px;'><u>SPECS</u></th></tr>"
					+ "</thead><tbody>";
*/
		out.append("<tbody>");

		ArrayList<OrderSpecification> specifications = new ArrayList<OrderSpecification>();
		ArrayList<OrderAddOn> addOns = new ArrayList<OrderAddOn>();
		int newQuantity = 1;
		int currentQuantity = 0;
		boolean isComplete = true;
		String fontWeight = "";
		String kotFontSize = settings.getKotFontSize();
		int size2 = Integer.parseInt(kotFontSize.replaceAll("px", ""))+1;
		String fontsize2 = size2 + "px";
		String kotFontWeight = settings.getKotFontWeight();
		ISpecification specDao = new SpecificationManager(false);
		IOrderAddOn addOnDao = new OrderAddOnManager(false);

		for (int i = 0; i < items.size(); i++) {
			String spec = "";
			newQuantity = items.get(i).getQty();
			isComplete = true;
			currentQuantity = 0;
			for (int j = 1; j <= newQuantity; j++) {
				/*if(isCheckKOT)
					specifications = dao.getOrderedSpecification(hotelId, items.get(i).getOrderId(), items.get(i).getMenuId());
				else*/
				specifications = specDao.getOrderedSpecification(outlet.getOutletId(), items.get(i).getOrderId(),
							items.get(i).getMenuId(), items.get(i).getSubOrderId(), j);
				if (isCanceled)
					addOns = addOnDao.getCanceledOrderedAddOns(outlet.getOutletId(), items.get(i).getOrderId(),
							items.get(i).getSubOrderId(), items.get(i).getMenuId(), j);
				else {
					/*if(isCheckKOT)
						addOns = dao.getOrderedAddOns(hotelId, items.get(i).getOrderId(), items.get(i).getMenuId());
					else*/
					addOns = addOnDao.getOrderedAddOns(outlet.getOutletId(), items.get(i).getOrderId(), items.get(i).getSubOrderId(),
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
					out.append("<tr style='padding: 0px; margin-top:0px; margin-bottom:0px;'>");
					if(items.get(i).getSpecifications().length()>0) {
						out.append("<td class='pull-left' style='padding:0px; width:10%; margin:0px; font-size:"+kotFontSize+";" + fontWeight + "'>" + currentQuantity);
						out.append("</td><td class='pull-left' style='padding:0px; width:60%; font-size:"+kotFontSize+"; font-weight:"+kotFontWeight+"; margin:0px;'>" + items.get(i).getTitle()
							+ "</td><td class='pull-right' style='padding:0px; padding-right:2px; font-size:"+kotFontSize+"; font-weight: "
							+ kotFontWeight+"; border-bottom:1px dashed black; margin:0px;'>"+items.get(i).getSpecifications()+"</td></tr>");	
					}else {
						out.append("<td class='pull-left' style='padding:0px; width:10%; margin:0px; font-size:"+fontsize2+";" + fontWeight + "'>" + currentQuantity);
						out.append("</td><td colspan='2' class='pull-left' style='padding:0px; width:60%; font-size:"+fontsize2+"; font-weight:"+kotFontWeight+"; margin:0px;'>" + items.get(i).getTitle()
							+ "</td></tr>");
					}
						
					isComplete = true;
					currentQuantity = 1;
				} else
					currentQuantity++;

				fontWeight = currentQuantity == 5 ? "font-weight:regular;" : "font-weight:"+kotFontWeight+";";
				out.append("<tr style='padding: 0px; margin-top:0px; margin-bottom:0px;'>");
				
				if (!isCanceled) {
					spec = items.get(i).getSpecifications() + spec;
					if(spec.length()>0) {
						out.append("<td class='pull-left' style='padding:0px; width:10%; margin:0px; font-size:"+kotFontSize+";" + fontWeight + "'>" + currentQuantity);
						out.append("</td><td class='pull-left' style='padding:0px; width:60%; font-size:"+kotFontSize+"; font-weight:"+kotFontWeight+"; margin:0px;'>" + items.get(i).getTitle()
							+ "</td><td class='pull-right' style='padding:0px; margin-right:2px; font-size:"+kotFontSize+"; font-weight: "
							+ kotFontWeight+"; border-bottom:1px dashed black; width:40%;'>" + spec + "</td></tr>");
					}else {
						String reason = items.get(i).getReason();
						out.append("<td class='pull-left' style='padding:0px; width:10%; margin:0px; font-size:"+fontsize2+";" + fontWeight + "'>" + currentQuantity);
						out.append("</td><td class='pull-left' style='padding:0px; width:60%; font-size:"+kotFontSize+"; font-weight:"+kotFontWeight+"; margin:0px;'>" + items.get(i).getTitle()
							+ "</td><td class='pull-right' style='padding:0px; margin-right:2px; font-size:"+kotFontSize+"; font-weight: "
							+ kotFontWeight+"; border-bottom:1px dashed black; width:40%;'>" + reason + "</td></tr>");
					}
				}else {
					out.append("<td class='pull-left' style='padding:0px; width:10%; margin:0px; font-size:"+fontsize2+";" + fontWeight + "'>" + currentQuantity);
					out.append("</td><td colspan='2' class='pull-left' style='padding:0px; margin:0px; font-size:"+fontsize2+"; font-weight:"+kotFontWeight+";'>" + items.get(i).getTitle() +"</td></tr>");
				}

				for (int k = 0; k < addOns.size(); k++) {
					fontWeight = currentQuantity == 5 ? "font-weight:regular;" : "font-weight:"+kotFontWeight+";";
					out.append("<tr style='padding: 0px; margin-top:0px; margin-bottom:0px;'>"
						+ "<td class='pull-left' style='border-top: none !important; padding: 0px !important; line-height: 1 !important;  font-size:"+kotFontSize+"; " + fontWeight + "'>"
						+ addOns.get(k).getQty() + "</td>"
						+ "<td class='pull-left' style='padding-left:50px !important; border-top: none !important; padding: 0px !important; font-size: "+kotFontSize+"; font-weight: "+kotFontWeight+";'>+ "
						+ addOns.get(k).getName());
					
					if (!isCanceled)
						out.append("</td><td></td></tr>");
					else
						out.append("</td></tr>");
				}
				spec = "";
				currentQuantity = 0;
			}
		}

		out.append("</tbody></table>"
				+ "<p style='margin-left:0px; margin-right:0px; margin-top:0px'>------------------------------------------------------------------------------------------</p>");

		String html = "<html><head><style>.table-condensed>thead>tr>th, .table-condensed>tbody>tr>th,"
				+ ".table-condensed>tfoot>tr>th, .table-condensed>thead>tr>td,"
				+ ".table-condensed>tbody>tr>td, .table-condensed>tfoot>tr>td {"
				+ "padding: 1px;} .pull-right{text-align: right} .pull-left{text-align: left} .table {width: 100%;}"
				+ ".table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td,.table>tbody>tr>td, "
				+ ".table>tfoot>tr>td {padding: 0px;} .table>thead>tr>th {vertical-align: bottom} th {text-align: left;}"
				+ " h3, .h3 {font-size: 18px;}"
				+ "</style></head><body style='width: 377px; font-family:" + settings.getKotFontFamily() + ";'>" + out
				+ "</body></html>";

		print(html, printerStation, copies, settings);
	}

	@GET
	@Path("/v1/getOrder")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOrder(@QueryParam("hotelId") String hotelId, @QueryParam("orderId") String orderId,
			@QueryParam("showConsolidated") Boolean showConsolidated,
			@QueryParam("showComplimentary") Boolean showComplimentary,
			@QueryParam("showReturned") Boolean showReturned) {
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
		
		IOrder orderDao = new OrderManager(false);
		IOrderItem itemDao = new OrderManager(false);
		IOrderAddOn addOnDao = new OrderAddOnManager(false);
		ISpecification specsDao = new SpecificationManager(false);
		ICustomer custDao = new CustomerManager(false);
		ILoyaltySettings loyaltyDao = new LoyaltyManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		IDiscount discountDao = new DiscountManager(false);
		ArrayList<OrderItem> orderItems = null;
		ArrayList<OrderAddOn> addOns = null;
		ArrayList<OrderSpecification> specifications = new ArrayList<OrderSpecification>();
		Order order = orderDao.getOrderById(hotelId, orderId);
		
		showReturned = showReturned==null?false:showReturned;
		
		if (showConsolidated) {
			orderItems = itemDao.getOrderedItemForBill(hotelId, orderId, showReturned);
			orderItems.addAll(itemDao.getOrderedItemForBillCI(hotelId, orderId));
			if (showComplimentary)
				orderItems.addAll(itemDao.getComplimentaryOrderedItemForBill(hotelId, orderId));
		} else {
			orderItems = itemDao.getOrderedItems(hotelId, orderId, showReturned);
			if (showComplimentary)
				orderItems.addAll(itemDao.getComplimentaryOrderedItemForBill(hotelId, orderId));
		}

		Double complimentaryTotal = 0.0;
		Double rate = 0.0;
		Double subTotal = 0.0;
		try {
			if (order.getState() == AccessManager.ORDER_STATE_OFFKDS
					|| order.getState() == AccessManager.ORDER_STATE_SERVICE) {
				if (!orderDao.isBarOrder(hotelId, orderId)) {
					if (!orderDao.isTakeAwayOrder(hotelId, orderId)) {
						if (!orderDao.isHomeDeliveryOrder(hotelId, orderId)) {
							ITable tableDao = new TableManager(false);
							if (!tableDao.isTableOrder(hotelId, orderId)) {
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
				rate = orderItems.get(i).getRate().doubleValue();
				subTotal = orderItems.get(i).getSubTotal().doubleValue();
				
				if (orderItems.get(i).getState() == 50) {
					rate = 0.0;
					subTotal = 0.0;
					complimentaryTotal += orderItems.get(i).getRate().doubleValue();
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
				itemDetails.put("collection", orderItems.get(i).getCollection());
				itemDetails.put("qty", orderItems.get(i).getQty());
				itemDetails.put("rate", rate);
				itemDetails.put("subTotal", subTotal);
				itemDetails.put("state", orderItems.get(i).getState());
				itemDetails.put("station", orderItems.get(i).getStation());
				itemDetails.put("waiterId", orderItems.get(i).getWaiterId());
				itemDetails.put("isTaxable", orderItems.get(i).getIsTaxable());
				itemDetails.put("taxes", orderItems.get(i).getTaxes());
				itemDetails.put("charges", orderItems.get(i).getCharges());
				itemDetails.put("finalAmount", orderItems.get(i).getFinalAmount());
				itemDetails.put("discountType", orderItems.get(i).getDiscountType());
				itemDetails.put("discountValue", orderItems.get(i).getDiscountValue());
				String specs = "";
				if(showConsolidated) {
					addOns = addOnDao.getOrderedAddOns(hotelId, orderId, orderItems.get(i).getMenuId(), showReturned);
					for (int k = 0; k < addOns.size(); k++) {
						addOnDetails = new JSONObject();
						rate = addOns.get(k).getRate().doubleValue();
						if (addOns.get(k).getState() == 50) {
							if(!showComplimentary)
								continue;
							rate = 0.0;
							complimentaryTotal += addOns.get(k).getRate().doubleValue();
						}
						addOnDetails.put("addOnId", addOns.get(k).getAddOnId());
						addOnDetails.put("name", addOns.get(k).getName());
						addOnDetails.put("itemId", addOns.get(k).getItemId());
						addOnDetails.put("quantity", addOns.get(k).getQty());
						addOnDetails.put("state", addOns.get(k).getState());
						addOnDetails.put("taxes", addOns.get(k).getTaxes());
						addOnDetails.put("charges", addOns.get(k).getCharges());
						addOnDetails.put("station", addOns.get(k).getStation());
						addOnDetails.put("rate", rate);
						addOnArr.put(addOnDetails);
					}
					specifications = specsDao.getOrderedSpecification(hotelId, orderId, orderItems.get(i).getMenuId());
					for (int k = 0; k < specifications.size(); k++) {
						specDetails = new JSONObject();
						specs += specifications.get(k).getSpecification() + ", ";
						specDetails.put("spec", specifications.get(k).getSpecification());
						specDetails.put("itemId", specifications.get(k).getItemId());
						specArr.put(specDetails);
					}
				}else {
					for (int j = 0; j < orderItems.get(i).getQty(); j++) {
						addOns = addOnDao.getOrderedAddOns(hotelId, orderId, orderItems.get(i).getSubOrderId(), orderItems.get(i).getMenuId(), true);
						for (int k = 0; k < addOns.size(); k++) {
							addOnDetails = new JSONObject();
							rate = addOns.get(k).getRate().doubleValue();
							if (addOns.get(k).getState() == 50) {
								if(!showComplimentary)
									continue;
								rate = 0.0;
								complimentaryTotal += addOns.get(k).getRate().doubleValue();
							}
							addOnDetails.put("addOnId", addOns.get(k).getAddOnId());
							addOnDetails.put("name", addOns.get(k).getName());
							addOnDetails.put("itemId", addOns.get(k).getItemId());
							addOnDetails.put("quantity", addOns.get(k).getQty());
							addOnDetails.put("state", addOns.get(k).getState());
							addOnDetails.put("taxes", addOns.get(k).getTaxes());
							addOnDetails.put("charges", addOns.get(k).getCharges());
							addOnDetails.put("station", addOns.get(k).getStation());
							addOnDetails.put("rate", rate);
							addOnArr.put(addOnDetails);
						}
						specifications = specsDao.getOrderedSpecification(hotelId, orderId, orderItems.get(i).getMenuId(),
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
			Customer customer = custDao.getCustomerDetails(hotelId, order.getCustomerNumber());
			if (customer != null) {
				orderObj.put("hasCustomer", true);
				orderObj.put("allergyInfo", customer.getAllergyInfo());
				orderObj.put("birthDate", customer.getBirthdate());
				orderObj.put("anniversary", customer.getAnniversary());
				orderObj.put("points", customer.getPoints());
				orderObj.put("userType", customer.getUserType());
				orderObj.put("pointToRupee", loyaltyDao.getLoyaltySettingByUserType(hotelId, customer.getUserType()).getPointToRupee());
				orderObj.put("loyaltyId", order.getLoyaltyId());
				orderObj.put("pointsUsed", order.getLoyaltyPaid());
				orderObj.put("customerEmailId", customer.getEmailId());
				orderObj.put("referenceForReview", customer.getReference());
				orderObj.put("sendSMS", customer.getSendSMS());
			} else
				orderObj.put("hasCustomer", false);
			orderObj.put("noOfGuests", order.getNumberOfGuests());
			orderObj.put("orderNumber", order.getOrderNumber());
			orderObj.put("orderDateTime", order.getOrderDateTime());
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
			orderObj.put("orderType", orderDao.getOrderType(order.getInHouse()));
			orderObj.put("takeAwayType", order.getTakeAwayType());
			orderObj.put("hasTakenReview", order.hasTakenReview());
			orderObj.put("orderId", order.getOrderId());
			orderObj.put("orderDate", order.getOrderDate());
			orderObj.put("billNo", order.getBillNo());
			orderObj.put("state", order.getState());
			orderObj.put("reason", order.getReason());
			orderObj.put("reference", order.getReference());
			orderObj.put("authId", order.getAuthId());
			OnlineOrderingPortal portal = portalDao.getOnlineOrderingPortalById(hotelId, order.getTakeAwayType());
			if(portal != null) {
				JSONObject portalObj = new JSONObject();
				portalObj.put("name", portal.getName());
				portalObj.put("portal", portal.getPortal());
				portalObj.put("hasIntegration", portal.getHasIntegration());
				portalObj.put("requiresLogistics", portal.getRequiresLogistics());
				orderObj.put("portalDetails", portalObj);
			}
			orderObj.put("portal", portal.getPortal());
			JSONArray discountCode = order.getDiscountCode();
			String appliedDiscounts = "";
			ArrayList<Discount> discounts = new ArrayList<Discount>();
			Discount discount = null;
			for(int i =0; i< discountCode.length(); i++) {
				discount = discountDao.getDiscountByName(hotelId, discountCode.getString(i));
				if(discount.getName().equals(AccessManager.DISCOUNT_TYPE_ZOMATO_VOUCHER)) {
					discount.setFoodValue(order.getZomatoVoucherAmount().intValue());
				}else if(discount.getName().equals(AccessManager.DISCOUNT_TYPE_PIGGYBANK)) {
					discount.setFoodValue(order.getPiggyBankAmount().intValue());
				}else if(discount.getName().equals(AccessManager.DISCOUNT_TYPE_FIXED_RUPEE_DISCOUNT)) {
					discount.setFoodValue(order.getFixedRupeeDiscount().intValue());
				}else if(discount.getName().equals(AccessManager.DISCOUNT_TYPE_EWARDS)) {
					discount.setFoodValue(order.getFixedRupeeDiscount().intValue());
				}
				discounts.add(discount);
				appliedDiscounts += discountCode.getString(i) + ", ";
			}
			orderObj.put("discountCode", appliedDiscounts.length()>3?appliedDiscounts.substring(0,appliedDiscounts.length()-2):appliedDiscounts);
			orderObj.put("discounts", discounts);
			orderObj.put("excludedCharges", order.getExcludedCharges());
			orderObj.put("excludedTaxes", order.getExcludedTaxes());
			orderObj.put("remarks", order.getRemarks());
			orderObj.put("externalOrderId", order.getExternalOrderId());
			orderObj.put("isIntegrationOrder", order.getIsIntegrationOrder());
			orderObj.put("cashToBeCollected", order.getCashToBeCollected());
			orderObj.put("zomatoVoucherAmount", order.getZomatoVoucherAmount());
			orderObj.put("promotionalCash", order.getPromotionalCash());
			orderObj.put("tableNumber", order.getTableId().length() == 3 || order.getTableId().length() == 2
							? order.getTableId().replace(",", "")
							: order.getTableId());
			orderObj.put("printCount", order.getPrintCount());
			orderObj.put("compTotal", complimentaryTotal);
			
			//rider details
			orderObj.put("riderName", order.getRiderName());
			orderObj.put("riderNumber", order.getRiderNumber());
			orderObj.put("riderStatus", order.getRiderStatus());
			orderObj.put("ewards", order.getEWards());
			
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONArray addOnArr = new JSONArray();
		JSONObject addOnDetails = new JSONObject();
		JSONObject itemDetails = null;
		
		IOrderItem dao = new OrderManager(false);
		IOrderAddOn addOnDao = new OrderAddOnManager(false);
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
						addOns = addOnDao.getReturnedAddOns(hotelId, orderId, orderItems.get(i).getSubOrderId(),
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
					addOns = addOnDao.getReturnedAddOns(hotelId, orderId, orderItems.get(i).getSubOrderId(),
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
			@QueryParam("orderTypeFilter") int orderTypeFilter,
			@QueryParam("query") String query, @QueryParam("mobileNo") String mobileNo, @QueryParam("userId") String userId, 
			@QueryParam("serviceDate") String serviceDate) {
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;

		IOrder dao = new OrderManager(false);
		IUser userDao = new UserManager(false);
		ArrayList<Order> order;
		int orderState = 0;
		boolean visible = false;
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		if (dateFilter != null && dateFilter.length() > 0) {
			dateFilter = dateFilter.substring(6, 10) + "/" + dateFilter.substring(3, 6) + dateFilter.substring(0, 2);
			order = dao.getAllOrders(hotelId, dateFilter, orderTypeFilter, query, visible);
		}else if(mobileNo != null && mobileNo.length() > 0){
			order = dao.getOrdersOfOneCustomer(hotelId, mobileNo);
		} else {
			order = dao.getAllOrders(hotelId, serviceDate, orderTypeFilter, query, visible);
		}
		try {
			for (int i = 0; i < order.size(); i++) {

				orderState = order.get(i).getState();
				itemDetails = new JSONObject();
				itemDetails.put("orderId", order.get(i).getOrderId());
				itemDetails.put("tableId",
						order.get(i).getTableId().length() == 3 || order.get(i).getTableId().length() == 2
								? order.get(i).getTableId().replace(",", "")
								: order.get(i).getTableId());
				itemDetails.put("orderNumber", order.get(i).getOrderNumber());
				itemDetails.put("customerName", order.get(i).getCustomerName());
				itemDetails.put("customerAddress", order.get(i).getCustomerAddress());
				itemDetails.put("amountRecieved", order.get(i).getTotalPayment());
				itemDetails.put("pointsUsed", order.get(i).getLoyaltyPaid());
				itemDetails.put("paymentType", order.get(i).getPaymentType());
				itemDetails.put("inhouse", order.get(i).getInHouse());
				itemDetails.put("takeAwayType", order.get(i).getTakeAwayType());
				itemDetails.put("state", orderState);
				itemDetails.put("foodBill", order.get(i).getFoodBill());
				itemDetails.put("barBill", order.get(i).getBarBill());
				itemDetails.put("waiterId", order.get(i).getWaiterId());
				itemDetails.put("section", order.get(i).getSection());
				itemDetails.put("deliveryBoy", order.get(i).getFirstName());
				itemDetails.put("reference", order.get(i).getReference());
				itemDetails.put("deliveryTime", order.get(i).getDeliveryTime());
				itemDetails.put("discount", orderState == 99 ? 0 : order.get(i).getFoodDiscount().doubleValue() + order.get(i).getBarDiscount().doubleValue());
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

	@GET
	@Path("/v1/getAllOrdersForCustomer")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllOrdersForCustomer(@QueryParam("hotelId") String hotelId,
			@QueryParam("mobileNo") String mobileNo, @QueryParam("userId") String userId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;

		IOrder orderDao = new OrderManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		ArrayList<Order> order = orderDao.getOrdersOfOneCustomer(hotelId, mobileNo);
		String takeAwayType = "";
		try {
			for (int i = 0; i < order.size(); i++) {

				itemDetails = new JSONObject();
				itemDetails.put("orderId", order.get(i).getOrderId());
				itemDetails.put("tableId",
						order.get(i).getTableId().length() == 3 || order.get(i).getTableId().length() == 2
								? order.get(i).getTableId().replace(",", "")
								: order.get(i).getTableId());
				itemDetails.put("orderNumber", order.get(i).getOrderNumber());
				itemDetails.put("orderDate", order.get(i).getOrderDate().toString().substring(0, 10));
				itemDetails.put("totalPayment", order.get(i).getTotalPayment());
				itemDetails.put("pointsEarned", order.get(i).getLoyaltyEarned());
				itemDetails.put("pointsUsed", order.get(i).getLoyaltyPaid());
				itemDetails.put("credit", order.get(i).getCreditAmount());
				itemDetails.put("paymentType", order.get(i).getPaymentType());
				itemDetails.put("billAmount", order.get(i).getTotal());
				takeAwayType = portalDao.getOnlineOrderingPortalById(hotelId, order.get(i).getTakeAwayType()).getName();
				itemDetails.put("orderType", order.get(i).getInHouse()==AccessManager.TAKE_AWAY?takeAwayType:orderDao.getOrderType(order.get(i).getInHouse()));
				itemDetails.put("waiterId", order.get(i).getWaiterId());
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
		
		IOrder dao = new OrderManager(false);
		IOrderItem itemDao = new OrderManager(true);
		IMenuItem menuDao = new MenuItemManager(false);
		IOutlet outletDao = new OutletManager(false);
		ISection sectionDao = new SectionManager(false);
		IUser userDao = new UserManager(false);
		IInventory inventoryDao = new InventoryManager(false); 
		ArrayList<OrderItem> beverageItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> naBarItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> kitchenItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> barItems = new ArrayList<OrderItem>();
		ArrayList<OrderItem> outDoorItems = new ArrayList<OrderItem>();
		OrderItem item = null;
		OrderAddOn addOn = null;
		boolean status = false;
		JSONObject orderedItems = null;
		ArrayList<OrderAddOn> orderedAddons = null;
		try {
			itemDao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			JSONArray orderItems = inObj.getJSONArray("items");
			JSONArray orderAddOns = inObj.has("addOns")?inObj.getJSONArray("addOns"):new JSONArray();
			String orderId = inObj.getString("orderId");
			String hotelId = inObj.getString("hotelId");
			String userId = inObj.getString("userId");
			Order order = dao.getOrderById(hotelId, orderId);
			int newQuantity = 0;
			int returnQuantity = 0;
			User user = userDao.getUser(hotelId, userId);
			Double totalRate = 0.0;
			OrderItem orderedItem = null;
			
			totalRate = (double) Math.round(totalRate);
			
			String subject = "Items Cancelled from Order No. " + order.getOrderNumber();
			String text = "<div><table style='color:#797979;'><thead>"
					+ "<tr><th style='text-align:left;'>PARTICULAR</th><th>REASON</th><th style='text-center;'>QTY</th><th style='text-align:right;'>RATE</th></tr>"
					+ "</thead><tbody>";
			
			for (int i = 0; i < orderItems.length(); i++) {
				orderedItems = orderItems.getJSONObject(i);
				
				String type = orderedItems.getString("type");
				newQuantity = orderedItems.getInt("qty") - orderedItems.getInt("returnQty");
				returnQuantity = orderedItems.getInt("returnQty");
				if(newQuantity < 0) {
					outObj.put("message", "Return quantity cannot be more than Ordered Quantity. Please contact OrderOn support.");
					itemDao.rollbackTransaction();
					return outObj.toString();
				}
				orderedItem = itemDao.getOrderedItem(hotelId, orderId, orderedItems.getString("subOrderId"), orderedItems.getString("menuId"));
				if(orderedItem.getItemIsMoved()) {
					if(!(user.getUserType() == UserType.OWNER.getValue() ||
							user.getUserType() == UserType.ADMINISTRATOR.getValue())) {
						outObj.put("message", orderedItem.getTitle() + " was moved. Moved Item cannot be canceled/returned.");
						itemDao.rollbackTransaction();
						return outObj.toString();
					}
				}
				totalRate += (orderedItems.getInt("returnQty")*orderedItems.getDouble("rate"));

				if (type.equals("return") || type.equals("cancel")) {
					status = itemDao.updateOrderItemLog(hotelId, orderId,
							orderedItems.getString("subOrderId"),
							orderedItems.getString("menuId"),
							orderedItems.getString("reason"), type, returnQuantity,
							new BigDecimal(Double.toString(orderedItems.getDouble("rate"))), 
							orderedItems.getInt("qty"));

				} else {
					newQuantity = 0;
					status = itemDao.updateOrderItemLog(hotelId, orderId,
							orderedItems.getString("subOrderId"),
							orderedItems.getString("menuId"),
							orderedItems.getString("reason"), type,
							orderedItems.getInt("qty"), 
							new BigDecimal(Double.toString(orderedItems.getDouble("rate"))),
							orderedItems.getInt("qty"));
				}
				if (!status) {
					outObj.put("message", "OrderItemLog could not be updated. Please contact OrderOn support.");
					itemDao.rollbackTransaction();
					return outObj.toString();
				}

				item = itemDao.getOrderedItem(hotelId, orderId, orderedItems.getString("subOrderId"),
						orderedItems.getString("menuId"));
				item.setQuantity(returnQuantity);

				boolean isRemoved = itemDao.removeSubOrder(hotelId, orderId,
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
					orderedAddons = itemDao.getOrderedAddOns(hotelId, orderId, orderedItems.getString("subOrderId"), orderedItems.getString("menuId"), false);
					OrderAddOn orderedAddon = null;
					for (int x=0; x<orderedAddons.size(); x++) {
						orderedAddon = orderedAddons.get(x);
						itemDao.updateOrderAddOnLog(hotelId, orderId, orderedItems.getString("subOrderId"),
								orderedItems.getString("subOrderDate"), orderedItems.getString("menuId"),orderedAddon.getItemId(), type,
								orderedAddon.getQty(), orderedAddon.getRate(), orderedAddon.getAddOnId());
						itemDao.removeAddOn(hotelId, orderId, orderedItems.getString("subOrderId"),
								orderedItems.getString("menuId"), newQuantity, orderedAddon.getAddOnId(), orderedAddon.getItemId());
					}
					outObj.put("isTaxable",
							menuDao.isTaxableMenuItem(hotelId, orderedItems.getString("menuId")));
				} else {
					outObj.put("message", "Item could not be deleted. Please contact OrderOn support.");
					itemDao.rollbackTransaction();
					return outObj.toString();
				}
				
				//Adding item details to the emailer.
				text += "<tr><td>" + orderedItem.getTitle() + "</td><td>" + orderedItems.getString("reason") + "</td><td style='text-center;'>" 
						+ orderedItems.getInt("returnQty") + "</td><td style='text-align:right;'>" + orderedItems.getDouble("rate") + "</td></tr>";
			}

			text += "<tr style='border-bottom: 1px dashed black; border-top: 1px dashed black;'><th colspan='3' style='text-align:left;'><h3>Total</h3></th><th><h3>"+totalRate+"</h3></th></tr>"
					+ "</tbody></table></div></div>";
			
			if(orderItems.length() == 0) {
				JSONObject orderedAddOns = null;
				for (int i = 0; i < orderAddOns.length(); i++) {
					orderedAddOns = orderAddOns.getJSONObject(i);
					
					String type = orderedAddOns.getString("type");
					newQuantity = orderedAddOns.getInt("qty") - orderedAddOns.getInt("returnQty");
					returnQuantity = orderedAddOns.getInt("returnQty");
					if(newQuantity < 0) {
						outObj.put("message", "Return quantity cannot be more than Ordered Quantity. Please contact OrderOn support.");
						itemDao.rollbackTransaction();
						return outObj.toString();
					}
	
					if (type.equals("return")) {
						status = itemDao.updateOrderAddOnLog(hotelId, orderId, 
								orderedAddOns.getString("subOrderId"),
								orderedAddOns.getString("subOrderDate"), 
								orderedAddOns.getString("menuId"),
								orderedAddOns.getInt("itemId"), type,
								returnQuantity, 
								new BigDecimal(Double.toString(orderedAddOns.getDouble("rate"))), 
								orderedAddOns.getString("addOnId"));
	
					} else if (type.equals("cancel")) {
						status = itemDao.updateOrderAddOnLog(hotelId, orderId, 
								orderedAddOns.getString("subOrderId"),
								orderedAddOns.getString("subOrderDate"), 
								orderedAddOns.getString("menuId"),
								orderedAddOns.getInt("itemId"), type,
								orderedAddOns.getInt("qty"), 
								new BigDecimal(Double.toString(orderedAddOns.getDouble("rate"))), 
								orderedAddOns.getString("addOnId"));
					} else {
						newQuantity = 0;
						status = itemDao.updateOrderAddOnLog(hotelId, orderId, 
								orderedAddOns.getString("subOrderId"),
								orderedAddOns.getString("subOrderDate"), 
								orderedAddOns.getString("menuId"),
								orderedAddOns.getInt("itemId"), type,
								orderedAddOns.getInt("qty"), 
								new BigDecimal(Double.toString(orderedAddOns.getDouble("rate"))), 
								orderedAddOns.getString("addOnId"));
					}
					if (!status) {
						outObj.put("message", "OrderAddOnLog could not be updated. Please contact OrderOn support.");
						itemDao.rollbackTransaction();
						return outObj.toString();
					}
	
					addOn = itemDao.getOrderedAddOnById(hotelId, orderId, orderedAddOns.getString("subOrderId"), 
							orderedAddOns.getString("menuId"),
							orderedAddOns.getInt("itemId"),
							orderedAddOns.getString("addOnId"));
					addOn.setQty(returnQuantity);
	
					itemDao.removeAddOn(hotelId, orderId, orderedAddOns.getString("subOrderId"),orderedAddOns.getString("menuId"),
							newQuantity,orderedAddOns.getString("addOnId"), orderedAddOns.getInt("itemId"));
				}
			}
			ArrayList<Section> sections = sectionDao.getSections(hotelId);
			Section section = null;
			for (Section s : sections) {
				if(hotelId.equals("am0001")) {
					if(s.getName().equals("DEFAULT"))
						section = s;
				}else {
					if(s.getName().equals(order.getSection()))
						section = s;
				}
			}
			if(section == null) {
				section = sectionDao.getSectionByName(hotelId, "DEFAULT");
			}

			Outlet outlet = outletDao.getOutlet(hotelId);
			Settings settings = outletDao.getSettings(hotelId);
			if(settings.getHasKot()) {
				if (beverageItems.size() > 0) {
					defineKOT(beverageItems, itemDao, "BEVERAGE ORDER TAKING", section.getBeveragePrinter(), outlet, settings,
							order, 1, true, false, userId);
				}
				if (kitchenItems.size() > 0) {
					defineKOT(kitchenItems, itemDao, "KITCHEN ORDER TAKING", section.getKitchenPrinter(), outlet, settings, order,
							1, true, false, userId);
				}
				if (barItems.size() > 0) {
					defineKOT(barItems, itemDao, "BAR ORDER TAKING", section.getBarPrinter(), outlet, settings, order, 1, true, false, userId);
				}
				if (outDoorItems.size() > 0) {
					defineKOT(outDoorItems, itemDao, "OUTDOOR", section.getOutDoorPrinter(), outlet, settings, order, 1, true, false, userId);
				}
				if (naBarItems.size() > 0) {
					defineKOT(naBarItems, itemDao, "BAR ORDER TAKING", section.getBarPrinter(), outlet, settings, order, 1, true, false, userId);
				}
			}
			outObj.put("status", true);
			itemDao.commitTransaction(hotelId);

			for (int i = 0; i < orderItems.length(); i++) {
				orderedItems = orderItems.getJSONObject(i);
				inventoryDao.revertInventoryForReturn(hotelId, orderId, orderedItems.getString("menuId"), orderedItems.getInt("returnQty"));
				orderedAddons = itemDao.getOrderedAddOns(hotelId, orderId, orderedItems.getString("subOrderId"), orderedItems.getString("menuId"), true);
				OrderAddOn orderedAddon = null;
				for (int x=0; x<orderedAddons.size(); x++) {
					orderedAddon = orderedAddons.get(x);
					inventoryDao.revertInventoryForReturn(hotelId, orderId, orderedAddon.getMenuId(), orderedAddon.getQty());
				}
			}
			if(orderItems.length() == 0) {
				JSONObject orderedAddOns = null;
				for (int i = 0; i < orderAddOns.length(); i++) {
					orderedAddOns = orderAddOns.getJSONObject(i);
					inventoryDao.revertInventoryForReturn(hotelId, orderId, orderedAddOns.getString("menuId"), orderedAddOns.getInt("returnQty"));
				}
			}
			if(!inObj.getString("hotelId").equals("am0001")) {
				text = "<div style='width:350px; ' class='alert alert-warning'><h3>Items have been cancelled from Order No. "
						+ order.getOrderNumber() + ".</h3><p> Details as follows:</p>"
						+ "<div>Total Amount: " + totalRate + "</div>"
						+ "<div>Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "</div>"
						+ "<div>Order punched by: " + orderId.split(":")[0] + "</div>"
						+ "<div>Item Cancelled by : " + inObj.getString("userId") + "</div>"
						+ text
						+ "</div>";
				SendEmailAndSMS(hotelId, subject, text, "", "", "OWNER", true, false);
			}
		} catch (Exception e) {
			itemDao.rollbackTransaction();
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
		
		IOrder orderDao = new OrderManager(false);
		IOutlet outletDao = new OutletManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			String billFormat = inObj.getString("billFormat");
			int billAmount = inObj.getInt("billAmount");
			Order order = orderDao.getOrderById(outletId, orderId);
			Outlet outlet = outletDao.getOutlet(outletId);
			Settings settings = outletDao.getSettings(outletId);
			if(order.getState()==AccessManager.ORDER_STATE_BILLING || order.getState()==AccessManager.ORDER_STATE_COMPLETE || order.getState()==AccessManager.ORDER_STATE_VOIDED) {
				outObj.put("status", orderDao.updateOrderPrintCount(outletId, orderId));
				outObj.put("billSize", Configurator.getBillSize());
				outObj.put("billFont", Configurator.getBillFont());
				if(!billFormat.equals("")){
					billFormat = billStyle + billFormat + "</body></html>";
					this.print(billFormat, "Cashier", 1, settings);
				}
				if(order.getInHouse()==AccessManager.HOME_DELIVERY) {
					orderDao.updateDeliveryTime(outletId, orderId);
					if(order.getCustomerNumber().length()==10 || order.getCustomerNumber().length()==11) {
						if(settings.getHasDeliverySms() && netIsAvailable()) {
							if(!order.getIsSmsSent()) {
								String messageText = "Hi! Greetings from "+outlet.getName()+". Thank you for your order. Your food is ready and will be delivered soon. Your Bill amount is Rs."+billAmount+". Enjoy!";
								SendSMS sms = new SendSMS();
								String out = sms.sendSms(messageText, order.getCustomerNumber());
								orderDao.updateOrderSMSStatusDone(outletId, orderId);
								System.out.println("SMS Sent to " + order.getCustomerName());
								System.out.println(out);
							}
						}
					}
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
		
		IOrder dao = new OrderManager(true);
		IPayment paymentDao = new PaymentManager(false);
		IUser userDao = new UserManager(false);
		IDesignation degisgnationDao = new DesignationManager(false);
		try {
			dao.beginTransaction();
			inObj = new JSONObject(jsonObject);
			String orderId = inObj.getString("orderId");
			String billNo = inObj.getString("billNo");
			String outletId = inObj.getString("hotelId");
			String section = inObj.getString("section");
			String userId = inObj.getString("userId");
			Order order = dao.getOrderById(outletId, orderId);
			Report payment = paymentDao.getPayment(outletId, orderId);
			
			User user = userDao.getUser(outletId, userId);
			Designation designation = degisgnationDao.getDesignationById(outletId, user.getUserType());
			if(order.getState() == AccessManager.ORDER_STATE_COMPLETE) {
				if(!(designation.getDesignation().equals(com.orderon.commons.Designation.OWNER.toString()) || 
						designation.getDesignation().equals(com.orderon.commons.Designation.ADMINISTRATOR.toString()))) {
					dao.rollbackTransaction();
					outObj.put("status", false);
					outObj.put("message", "Unauthorised to void.");
					return outObj.toString();
				}
			}
			outObj = dao.voidOrder(outletId, orderId, inObj.getString("reason"),
			inObj.getString("authId"), section);
			if (outObj.getBoolean("status")) {
				dao.commitTransaction(outletId);
				dao = new OrderManager(false);
				order = dao.getOrderById(outletId, orderId);
				billNo = order.getBillNo();
				
				IOrderItem orderItemsDao = new OrderManager(false);
				ArrayList<OrderItem> orderedItems = orderItemsDao.getOrderedItemForBill(outletId, orderId, false);

				BigDecimal foodBill = new BigDecimal(0.0);
				BigDecimal barBill = new BigDecimal(0.0);
				if(payment == null) {
					for (OrderItem orderItem : orderedItems) {
						if(orderItem.getStation().equals("Bar")) {
							barBill.add(orderItem.getRate());
						}else {
							foodBill.add(orderItem.getRate());
						}
					}
				}else {
					foodBill = payment.getFoodBill();
					barBill = payment.getBarBill();
				}
				paymentDao.addPaymentForVoidOrder(outletId, orderId, foodBill, barBill, billNo, section, order.getOrderDate());
				
				String subject = "Order No. " + order.getOrderNumber() + " marked void!";
				String text = "<div style='width:300px; '><div class='alert alert-warning'>"
						+ "<b>Order No. " + order.getOrderNumber() + " </b>has been marked void.</h3>"
						+ "<p> Details as follows:</p>" + "<div>Time: "
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "</div>"
						+ "<div>Bill No. : " + billNo + "</div>"
						+ "<div>Order punched by: " + orderId.split(":")[0] + "</div>"
						+ "<div>Authorizer: " + inObj.getString("authId") + "</div>"
						+ "<div>Reason: " + inObj.getString("reason") + "</div>";
				
				if(foodBill.compareTo(new BigDecimal("0")) == 1) {
					text += "<div>Food Bill Amount: " + foodBill.toString() + "</div>";
				}
				if(barBill.compareTo(new BigDecimal("0")) == 1) {
					text += "<div>Bar Bill Amount: " + barBill.toString() + "</div>";
				}
				text += "</div>";
				
				String smsText = "Order Marked Void. BillNo: "+billNo+", UserName: "+orderId.split(":")[0]
						+" , Reason: "+inObj.getString("reason")+" , Authorizer: " +inObj.getString("authId") + ".";
			
				if(inObj.getString("hotelId").equals("am0001"))
					return outObj.toString();
				SendEmailAndSMS(inObj.getString("hotelId"), subject, text, smsText, "", AccessManager.DESIGNATION_OWNER, true, false);

				outObj.put("hasWalletPayment", false);
				if(payment != null) {
					if(payment.getWalletPayment().add(payment.getPromotionalCash()).compareTo(new BigDecimal("0")) == 1) {
						outObj.put("hasWalletPayment", true);
						outObj.put("transactionId", order.getWalletTransactionId());
					}
				}
			}else {
				dao.rollbackTransaction();
			}
		} catch (Exception e) {
			dao.rollbackTransaction();
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
		
		IOrder dao = new OrderManager(false);
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
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "</div><div>Order punched by: "
						+ orderId.split(":")[0] + "</div><div>Authorizer: " + inObj.getString("authId")
						+ "</div></div>";
				SendEmailAndSMS(inObj.getString("hotelId"), subject, text, "", "", AccessManager.DESIGNATION_OWNER, true, false);
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
		
		IOrderItem dao = new OrderManager(true);
		IOrderAddOn addOnDao = new OrderAddOnManager(false);
		boolean status = false;
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String orderId = inObj.getString("orderId");
			String outletId = inObj.getString("hotelId");
			JSONArray compItems = inObj.getJSONArray("compItems");

			for (int i = 0; i < compItems.length(); i++) {
				if (compItems.getJSONObject(i).getBoolean("status")) {
					if(!dao.complimentaryItem(outletId, orderId,
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
					status = addOnDao.complimentaryAddOn(outletId, orderId,
							compAddons.getJSONObject(j).getString("addOnId"), inObj.getString("authId"),
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
			dao.commitTransaction(outletId);
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/toggleCharge")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String toggleCharge(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IOrder dao = new OrderManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			if(dao.toggleChargeInOrder(inObj.getString("hotelId"), inObj.getString("orderId"), inObj.getInt("chargeId"))) {
				outObj.put("status", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/toggleTax")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String toggleTax(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IOrder dao = new OrderManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			int taxId = inObj.getInt("taxId");
			if(taxId == 1) {
				if(dao.toggleTaxInOrder(inObj.getString("hotelId"), inObj.getString("orderId"), taxId)) {
					if(dao.toggleTaxInOrder(inObj.getString("hotelId"), inObj.getString("orderId"), 2))
						outObj.put("status", true);
				}
			}else if(taxId == 2) {
				if(dao.toggleTaxInOrder(inObj.getString("hotelId"), inObj.getString("orderId"), taxId)) {
					if(dao.toggleTaxInOrder(inObj.getString("hotelId"), inObj.getString("orderId"), 1))
						outObj.put("status", true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCompletedOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCompletedOrders(@QueryParam("hotelId") String hotelId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IOrder dao = new OrderManager(false);
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
	
	@GET
	@Path("/v3/Integration/getMenu")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuv3(@QueryParam("hotelId") String hotelId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		IGroup groupDao = new GroupManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortals(hotelId);
		ArrayList<MenuItem> items = dao.getMenuForIntegration(hotelId);
		ArrayList<Group> groups = groupDao.getGroups(hotelId);
		try {
			for (int i = 0; i < items.size(); i++) {
				itemDetails = addItemsToObject(items.get(i), portals);
				itemsArr.put(itemDetails);
			}
			outObj.put("menuItems", itemsArr);
			outObj.put("groups", groups);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getNextMenuId")
	@Produces(MediaType.APPLICATION_JSON)
	public String getNextMenuId(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		try {
			outObj.put("menuId", dao.getNextMenuId(hotelId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getMenuOnly")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuOnly(@QueryParam("hotelId") String hotelId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();
		
		IMenuItem dao = new MenuItemManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		ArrayList<MenuItem> items = dao.getMenu(hotelId);
		ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortals(hotelId);
		try {
			for (int i = 0; i < items.size(); i++) {
				itemDetails = addItemsToObject(items.get(i), portals);
				itemsArr.put(itemDetails);
			}
			outObj.put("menuItems", itemsArr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getMenu")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuv3ForMP(@QueryParam("hotelId") String hotelId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();

		IMenuItem dao = new MenuItemManager(false);
		IGroup groupDao = new GroupManager(false);
		ISpecification specsDao = new SpecificationManager(false);
		ICharge chargeDao = new ChargeManager(false);
		ITax taxDao = new TaxManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);

		ArrayList<MenuItem> items = dao.getMenu(hotelId);
		ArrayList<Specifications> specs = specsDao.getSpecifications(hotelId);
		ArrayList<Group> groups = groupDao.getGroups(hotelId);
		ArrayList<Tax> taxes = taxDao.getTaxes(hotelId);
		ArrayList<Charge> charges = chargeDao.getCharges(hotelId);
		ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortals(hotelId);
		try {
			for (int i = 0; i < items.size(); i++) {
				itemDetails = addItemsToObject(items.get(i), portals);
				itemsArr.put(itemDetails);
			}
			outObj.put("menuItems", itemsArr);
			outObj.put("addOns", itemsArr);
			outObj.put("groups", groups);
			outObj.put("specifications", specs);
			outObj.put("taxes", new JSONArray(taxes));
			outObj.put("charges", new JSONArray(charges));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	private JSONObject addItemsToObject(MenuItem item, ArrayList<OnlineOrderingPortal> portals) {
		JSONObject itemDetails = new JSONObject();
		try {
			itemDetails.put("station", item.getStation());
			itemDetails.put("menuId", item.getMenuId());
			itemDetails.put("title", item.getTitle());
			itemDetails.put("description", item.getDescription());
			itemDetails.put("collection", item.getCollection());
			itemDetails.put("subCollection", item.getSubCollection());
			itemDetails.put("flags", item.getFlags());
			itemDetails.put("vegType", item.getVegType());
			itemDetails.put("preparationTime", item.getPreparationTime());
			itemDetails.put("deliveryRate", item.getDeliveryRate());
			itemDetails.put("dineInRate", item.getDineInRate());
			itemDetails.put("dineInNonAcRate", item.getDineInNonAcRate());
			itemDetails.put("onlineRate", item.getOnlineRate());
			itemDetails.put("image", item.getImg());
			itemDetails.put("coverImgUrl", item.getCoverImgUrl());
			itemDetails.put("state", item.getState());
			itemDetails.put("code", item.getCode());
			itemDetails.put("taxes", item.getTaxes());
			itemDetails.put("groups", item.getGroups());
			itemDetails.put("charges", item.getCharges());
			itemDetails.put("isRecomended", item.getIsRecomended());
			itemDetails.put("isTreats", item.getIsTreats());
			itemDetails.put("isDefault", item.getIsDefault());
			itemDetails.put("isBogo", item.getIsBogo());
			itemDetails.put("isCombo", item.getIsCombo());
			itemDetails.put("comboPrice", item.getComboPrice());
			itemDetails.put("comboReducedPrice", item.getComboReducedPrice());
			itemDetails.put("discountType", item.getDiscountType());
			itemDetails.put("discountValue", item.getDiscountValue());

			for (OnlineOrderingPortal onlineOrderingPortal : portals) {
				if(onlineOrderingPortal.getMenuAssociation() == 0) {
					continue;
				}
				if(onlineOrderingPortal.getMenuAssociation() == 1) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate1());
				}else if(onlineOrderingPortal.getMenuAssociation() == 2) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate2());
				}else if(onlineOrderingPortal.getMenuAssociation() == 3) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate3());
				}else if(onlineOrderingPortal.getMenuAssociation() == 4) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate4());
				}else if(onlineOrderingPortal.getMenuAssociation() == 5) {
					itemDetails.put(onlineOrderingPortal.getPortal().toLowerCase() + "Rate", item.getOnlineRate5());
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return itemDetails;
	}

	@GET
	@Path("/v1/getMenuItems")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuItems(@QueryParam("hotelId") String hotelId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;

		IMenuItem dao = new MenuItemManager(false);
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
	public String getMenuMP(@QueryParam("hotelId") String hotelId, @QueryParam("query") String query) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;

		IMenuItem dao = new MenuItemManager(false);
		ArrayList<MenuItem> orderItems = null;
		if(query.equals(""))
			orderItems = dao.getMenuMP(hotelId);
		else
			orderItems = dao.getMenuItemBySearch(hotelId, query);
		try {
			for (int i = 0; i < orderItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("menuId", orderItems.get(i).getMenuId());
				itemDetails.put("collection", orderItems.get(i).getCollection());
				itemDetails.put("subCollection", orderItems.get(i).getSubCollection());
				itemDetails.put("title", orderItems.get(i).getTitle());
				itemDetails.put("description", orderItems.get(i).getDescription());
				itemDetails.put("flags", orderItems.get(i).getFlags());
				itemDetails.put("deliveryRate", orderItems.get(i).getDeliveryRate());
				itemDetails.put("dineInRate", orderItems.get(i).getDineInRate());
				itemDetails.put("onlineRate", orderItems.get(i).getOnlineRate());
				itemDetails.put("state", orderItems.get(i).getState());
				itemDetails.put("syncOnZomato", orderItems.get(i).getSyncOnZomato());
				itemsArr.put(itemDetails);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return itemsArr.toString();
	}

	@GET
	@Path("/v3/getMenuItem")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuItem(@QueryParam("hotelId") String hotelId, @QueryParam("menuId") String menuId) {
		IMenuItem dao = new MenuItemManager(false);
		return new JSONObject(dao.getMenuById(hotelId, menuId)).toString();
	}

	@GET
	@Path("/Zomato/v3/getMenuItem")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuItemForZ(@QueryParam("hotelId") String hotelId, @QueryParam("menuId") String menuId) {
		IMenuItem dao = new MenuItemManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		
		ArrayList<OnlineOrderingPortal> portals = portalDao.getOnlineOrderingPortals(hotelId);
		
		return addItemsToObject(dao.getMenuById(hotelId, menuId), portals).toString();
	}

	@POST
	@Path("/v1/deleteItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deteleItem(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IMenuItem dao = new MenuItemManager(false);
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

	@GET
	@Path("/v1/getSpecifications")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getSpecifications(@QueryParam("hotelId") String hotelId) {
		JSONObject arrObj = null;
		JSONArray arr = new JSONArray();

		ISpecification dao = new SpecificationManager(false);
		ArrayList<Specifications> specs = dao.getSpecifications(hotelId);
		try {
			for (int i = 0; i < specs.size(); i++) {
				arrObj = new JSONObject();
				arrObj.put("specification", specs.get(i).getSpecification());
				arrObj.put("collection", specs.get(i).getCollection());
				arrObj.put("type", specs.get(i).getType());
				arr.put(arrObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr.toString();
	}

	@GET
	@Path("/v3/getSpecifications")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getSpecificationsv3(@QueryParam("hotelId") String hotelId) {
		JSONObject arrObj = new JSONObject();

		ISpecification dao = new SpecificationManager(false);
		ArrayList<Specifications> specs = dao.getSpecifications(hotelId);
		try {
			arrObj.put("specifications", specs);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return arrObj.toString();
	}

	@POST
	@Path("/v1/addSpecification")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addSpecification(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		ISpecification dao = new SpecificationManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			outObj.put("status", dao.addSpecification(inObj.getString("specification")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/newOrderApp")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String newOrderApp(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IOrder dao = new OrderManager(true);
		IUserAuthentication userDao = new UserManager(false);
		IOutlet outletDao = new OutletManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			Settings settings = outletDao.getSettings(outletId);
			ServiceLog currentService = serviceDao.getCurrentService(outletId);
			outObj.put("message", "Unknown Error");
			outObj = userDao.validateToken(outletId, inObj.getString("userId"), inObj.getString("authToken"));
			if(!outObj.getBoolean("status")) {
				outObj.put("message", "Could not validate User.");
				outObj.put("status", -2);
				return outObj.toString();
			}
			if(currentService== null) {
				outObj.put("message", "Please start service before placing order.");
				outObj.put("status", -2);
				return outObj.toString();
			}
			dao.beginTransaction();
			JSONArray tablesArr = inObj.getJSONArray("tableIds");
			String[] tableIds = new String[tablesArr.length()];
			for (int i = 0; i < tableIds.length; i++) {
				tableIds[i] = tablesArr.getString(i);
			}
			String mobileNumber = inObj.getString("contactNumber");
			String address = inObj.has("customerAddress")?inObj.getString("customerAddress"):"";
			String customerName = inObj.getString("customer");
			
			outObj = dao.newOrder(inObj.getString("hotelId"), settings.getHotelType(), inObj.getString("userId"), tableIds,
					inObj.getInt("peopleCount"), customerName, mobileNumber, address, "DEFAULT", "", currentService);
			dao.commitTransaction(outletId);

			String[] name = customerName.split(" ");
			String surName = "";
			if(name.length>1) {
				surName = name[1];
			}
			if (!mobileNumber.equals("")) {
				ICustomer customerDao = new CustomerManager(false);
				if (!customerDao.hasCustomer(outletId, mobileNumber)) {
					customerDao.addCustomer(outletId, name[0], surName, mobileNumber, address, "", "", inObj.getString("allergyInfo"), Boolean.FALSE, Boolean.FALSE, "", "");
				} else {
					customerDao.updateCustomer(outletId, null, name[0], surName, mobileNumber, "", "", "", inObj.getString("allergyInfo"), address, Boolean.FALSE, "", "");
				}
			}
		} catch (Exception e) {
			dao.rollbackTransaction();
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

		IOrder dao = new OrderManager(false);
		//IUserAuthentication userDao = new UserManager(false);
		IOutlet outletDao = new OutletManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			Settings settings = outletDao.getSettings(outletId);
			ServiceLog currentService = serviceDao.getCurrentService(outletId);
			outObj.put("message", "Unknown Error");
			if (currentService == null) {
				outObj.put("status", -3);
				outObj.put("message", "Service has not started");
				return outObj.toString();
			}
			/*String decoded = Base64.decodeAsString(authString.getBytes());
			String[] auth = decoded.split(":");
			outObj = userDao.validateToken(inObj.getString("hotelId"), auth[0], auth[1]);
			if(!outObj.getBoolean("status")) {
				outObj.put("status", -2);
				return outObj.toString();
			}*/
			
			JSONArray tablesArr = inObj.getJSONArray("tableIds");
			String[] tableIds = new String[tablesArr.length()];
			for (int i = 0; i < tableIds.length; i++) {
				tableIds[i] = tablesArr.getString(i);
			}
			String mobileNumber = inObj.getString("contactNumber");
			String address = inObj.has("customerAddress")?inObj.getString("customerAddress"):"";
			String customerName = inObj.getString("customer");
			
			outObj = dao.newOrder(inObj.getString("hotelId"), settings.getHotelType(), inObj.getString("userId"), tableIds,
					inObj.getInt("peopleCount"), customerName, mobileNumber, address,
					inObj.has("section")?inObj.getString("section"):"", inObj.has("orderRemarks")?inObj.getString("orderRemarks"):""
					, currentService);

			String[] name = customerName.split(" ");
			String surName = "";
			if(name.length>1) {
				surName = name[1];
			}
			if (!mobileNumber.equals("")) {
				ICustomer customerDao = new CustomerManager(false);
				if (!customerDao.hasCustomer(outletId, mobileNumber)) {
					customerDao.addCustomer(outletId, name[0], surName, mobileNumber, address, "", "", inObj.getString("allergyInfo"), Boolean.FALSE, Boolean.FALSE, "", "");
				} else {
					customerDao.updateCustomer(outletId, null, name[0], surName, mobileNumber, "", "", "", inObj.getString("allergyInfo"), address, Boolean.FALSE, "", "");
				}
			}
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/alignBillNumbers")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String alignBillNumbers(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOrder dao = new OrderManager(false);
		IUserAuthentication userDao = new UserManager(false);
		IOutlet outletDao = new OutletManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			Settings settings = outletDao.getSettings(outletId);
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			String hotelId = inObj.getString("hotelId");
			String decoded = Base64.decodeAsString(authString.getBytes());
			String[] auth = decoded.split(":");
			outObj = userDao.validateToken(hotelId, auth[0], auth[1]);
			 
			if(!outObj.getBoolean("status")) {
				outObj.put("status", false);
				outObj.put("message", "Access Denied. Unauthorized user.");
				return outObj.toString();
			}else if(!outObj.getString("userType").equals(UserType.SECRET.toString())) {
				outObj.put("status", false);
				outObj.put("message", "Access Denied. Unauthorized user.");
				return outObj.toString();
			}
			inObj = new JSONObject(jsonObject);
			if(settings.getBillType() == AccessManager.BILLTYPE_MONTHLY_REFRESH) {
				dao.AlignBillNumbersMonthWise(hotelId, inObj.getString("serviceDate").substring(0, 7));
			}else if(settings.getBillType() == AccessManager.BILLTYPE_NUMBER_REFRESH) {
				dao.AlignBillNumbersDayWise(hotelId, inObj.getString("serviceDate"));
			}else if(settings.getBillType() == AccessManager.BILLTYPE_NUMBER) {
				dao.AlignAllBillNumbers(hotelId, inObj.getString("serviceDate").substring(0, 7));
			}
			outObj.put("status", true);
			outObj.put("message", "Bill Numbers aligned.");
			
			
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

		IOrder dao = new OrderManager(false);
		IUserAuthentication userDao = new UserManager(false);
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			String decoded = Base64.decodeAsString(authString.getBytes());
			String[] auth = decoded.split(":");
			outObj = userDao.validateToken(hotelId, auth[0], auth[1]);
			 
			if(!outObj.getBoolean("status")) {
				outObj.put("status", false);
				outObj.put("message", "Access Denied. Unauthorized user.");
				return outObj.toString();
			}else if(!outObj.getString("userType").equals(UserType.SECRET.toString())) {
				outObj.put("status", false);
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

	@POST
	@Path("/v1/deleteOrders")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String deleteOrders(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOrder dao = new OrderManager(false);
		IUserAuthentication userDao = new UserManager(false);
		IOutlet outletDao = new OutletManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			Settings settings = outletDao.getSettings(outletId);
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			String hotelId = inObj.getString("hotelId");
			String decoded = Base64.decodeAsString(authString.getBytes());
			String[] auth = decoded.split(":");
			outObj = userDao.validateToken(hotelId, auth[0], auth[1]);
			 
			if(!outObj.getBoolean("status")) {
				outObj.put("status", false);
				outObj.put("message", "Access Denied. Unauthorized user.");
				return outObj.toString();
			}else if(!outObj.getString("userType").equals(UserType.SECRET.toString())) {
				outObj.put("status", false);
				outObj.put("message", "Access Denied. Unauthorized user.");
				return outObj.toString();
			}
			if(settings.getDeductionType() == AccessManager.DEDUCTION_DELETE_MONTHLY) {
				outObj = dao.deleteOrdersMonthWise(inObj.getString("hotelId"), inObj.getString("serviceDate").substring(0, 7),
					 inObj.getDouble("foodDeduction"), inObj.getDouble("barDeduction"));
			}else if(settings.getDeductionType() == AccessManager.DEDUCTION_DELETE_DAILY) {
				outObj = dao.deleteOrdersDayWise(inObj.getString("hotelId"), inObj.getDouble("deductionAmount"),
						 inObj.getDouble("foodDeduction"), inObj.getDouble("barDeduction"), inObj.getString("serviceDate"),
							inObj.getString("serviceType"));
			}
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

		IOrder dao = new OrderManager(false);
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newNCOrder(inObj.getString("hotelId"), inObj.getString("userId"), inObj.getString("reference") , 
					inObj.has("section")?inObj.getString("section"):"", inObj.has("remarks")?inObj.getString("remarks"):"");
			
			dao.checkOutOrder(inObj.getString("hotelId"), outObj.getString("orderId"));
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

		IOrder dao = new OrderManager(true);
		IUserAuthentication userDao = new UserManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		System.out.println("New Order.");
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			JSONObject orderObj = inObj.getJSONObject("order");
			String[] auth = Base64.decodeAsString(authString.getBytes()).split(":");
			//creds: Zomato-zomato
			if(!userDao.validOnlinePlatform(orderObj.getString("outlet_id"), auth[0], auth[1])) {
				outObj.put("message", "Invalid Credentials.");
				outObj.put("status", "failed");
				outObj.put("code", "401");
				System.out.println(authString);
				System.out.println("Invalid Credentials");
				return outObj.toString();
			}
			String outletId = orderObj.getString("outlet_id");
			dao.beginTransaction(outletId);
			outObj = dao.newOnlineOrder(jsonObject, orderObj, portalDao.getOnlineOrderingPortalByPortal(outletId, OnlineOrderingPortals.ZOMATO).getId());
			dao.commitTransaction(outletId);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/test/newOnlineOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String newZomatoTestOrder(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOrder dao = new OrderManager(true);
		IUserAuthentication userDao = new UserManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		System.out.println("New Order.");
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			JSONObject orderObj = inObj.getJSONObject("order");
			String[] auth = Base64.decodeAsString(authString.getBytes()).split(":");
			//creds: Zomato-zomato
			if(!userDao.validOnlinePlatform(orderObj.getString("outlet_id"), auth[0], auth[1])) {
				outObj.put("message", "Invalid Credentials.");
				outObj.put("status", "failed");
				outObj.put("code", "401");
				System.out.println(authString);
				System.out.println("Invalid Credentials");
				return outObj.toString();
			}
			String outletId = orderObj.getString("outlet_id");
			dao.beginTransaction(outletId);
			outObj = dao.newOnlineOrder(jsonObject, orderObj, portalDao.getOnlineOrderingPortalByPortal(outletId, OnlineOrderingPortals.ZOMATO).getId());
			dao.commitTransaction(outletId);
			
			dao = null;
			userDao = null;
			portalDao = null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return outObj.toString();
	}

	@POST
	@Path("/v1/Zomato/riderStatusUpdate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String riderStatusUpdate(String jsonObject, @HeaderParam("authorization") String authString) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOrder dao = new OrderManager(true);
		try {
			outObj.put("status", "failure");
			inObj = new JSONObject(jsonObject);
			int orderId = inObj.getInt("order_id");
			String outletId = inObj.getString("outlet_id");
			String status = inObj.getString("status");
			JSONObject riderData = inObj.getJSONObject("rider_data");
			dao.beginTransaction(outletId);
			dao.updateOnlineRiderData(outletId, orderId, riderData.getString("rider_name"), riderData.getString("rider_phone_number"), status);
			dao.commitTransaction(outletId);
			outObj.put("status", "success");
			
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/getOnlineOrders")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOnlineOrders(@QueryParam("hotelId") String hotelId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;

		IOrder dao = new OrderManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		ArrayList<OnlineOrder> order;
		try {
			if(hotelId == null  || hotelId.isEmpty()) {
				System.out.println("Invalid OutletId");
				orderObj.put("status", false);
				return orderObj.toString();
			}
			order = dao.getOnlineOrders(hotelId);
			if(order == null) {
				System.out.println("Outlet is not upto date for Zomato Integration. OutletId : "+ hotelId);
				orderObj.put("status", false);
				return orderObj.toString();
			}
			for (int i = 0; i < order.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("portalId", order.get(i).getPortalId());
				itemDetails.put("portal", portalDao.getOnlineOrderingPortalById(hotelId, order.get(i).getPortalId()).getPortal());
				itemDetails.put("data", order.get(i).getData());
				
				itemsArr.put(itemDetails);
			}
			
			orderObj.put("status", true);
			orderObj.put("orders", itemsArr);
			orderObj.put("deliveryUpdate", dao.getOrderDeliveryUpdate(hotelId));
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

		IUserAuthentication userDao = new UserManager(false);
		IOrder dao = new OrderManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			System.out.println(jsonObject);
			int externalOrderId = inObj.getInt("order_id");
			String outletId = inObj.getString("outlet_id");
			String action = inObj.getString("action");
			String[] auth = Base64.decodeAsString(authString.getBytes()).split(":");
			System.out.println("V1");
			System.out.println("Auth String: " + authString);
			if(!userDao.validOnlinePlatform(outletId, auth[0], auth[1])) {
				outObj.put("message", "Invalid Credentials.");
				outObj.put("status", "failed");
				outObj.put("code", "401");
				System.out.println("Invalid Credentials");
				return outObj.toString();
			}
			if(action.equals("reject")) {
				dao.editOnlineOrder(outletId, externalOrderId, AccessManager.ONLINE_ORDER_REJECTED);
				System.out.println("Order Auto Rejected by Zomato for Restaurant: " + outletId 
				+ " Order Id :" + externalOrderId);
			}else if(action.equals("timeout")) {
				dao.editOnlineOrder(outletId, externalOrderId, AccessManager.ONLINE_ORDER_TIMEDOUT);
				System.out.println("Order Timedout for Restaurant: " + outletId 
				+ " Order Id :" + externalOrderId);
			}
			System.out.println("Authenticated");
			outObj.put("hotelId", Configurator.getSystemId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/acceptOnlineOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateOnlineOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOrder dao = new OrderManager(false);
		try {
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			OnlineOrder order = dao.getOnlineOrder(inObj.getString("hotelId"), inObj.getInt("externalRestaurantId"), inObj.getInt("externalOrderId"));
			if(order.getStatus() > 0) {
				outObj.put("message", "Order Already Accepted.");
				outObj.put("status", false);
				return outObj.toString();
			}
			boolean status = dao.updateOnlineOrderStatus(inObj.getString("hotelId"), inObj.getInt("externalRestaurantId"), 
					inObj.getInt("externalOrderId"), AccessManager.ONLINE_ORDER_ACCEPTED, inObj.getString("orderId"), inObj.has("orderNumber")?inObj.getInt("orderNumber"):0);
			outObj.put("status", status);
			if(status)
				System.out.println("Order Accepted for Restaurant: " + inObj.getInt("externalRestaurantId") 
				+ " Order Id :" + inObj.getInt("externalOrderId"));
			else
				System.out.println("Could not update order.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/completeOnlineOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String completeOnlineOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOrder dao = new OrderManager(false);
		try {
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			boolean status = dao.markOnlineOrderComplete(inObj.getString("outletId"), inObj.getInt("orderNumber"));
			outObj.put("status", status);
			if(status)
				System.out.println("Order Marked Complete for Restaurant: "+ inObj.getString("outletId") 
				+ " Order Id :" + inObj.getInt("orderNumber"));
			else
				System.out.println("Could not update order.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/rejectOnlineOrder")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String rejectOnlineOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOrder dao = new OrderManager(false);
		try {
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.updateOnlineOrderStatus(inObj.getString("hotelId"), inObj.getInt("externalRestaurantId"), 
						inObj.getInt("externalOrderId"), AccessManager.ONLINE_ORDER_DECLINED, "", 0));
			System.out.println("Order Rejected for Restaurant: " + inObj.getInt("externalRestaurantId") 
			+ " Order Id :" + inObj.getInt("externalOrderId"));
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

		IOrder dao = new OrderManager(false);
		IOrderItem itemDao = new OrderManager(true);
		IMenuItem menuDao = new MenuItemManager(false);
		IOutlet outletDao = new OutletManager(false);
		
		boolean printKOT = false;
		String outletId = "";
		String orderId = "";
		String userId = "";
		String menuId = "";
		String section = "";
		Outlet outlet = null;
		Settings settings = null;
		Order order = null;
		JSONArray addOns = null;
		try {
			outObj.put("status", -1);
			outObj.put("message", "Unknown Error");

			inObj = new JSONObject(jsonObject);
			
			outletId = inObj.getString("hotelId");
			orderId = inObj.getString("orderId");
			userId = inObj.getString("userId");
			section = inObj.has("section")?inObj.getString("section"):"";
			
			outlet = outletDao.getOutlet(outletId);
			settings = outletDao.getSettings(outletId);
			
			//Get order details.
			order = dao.getOrderById(outletId, orderId);
			
			if(order==null) {
				outObj.put("message", "Order not found.");
				return outObj.toString();
			}

			//Begin OrderItem tansactions.
			itemDao.beginTransaction();
			
			//Check if quickorder. set flag.
			boolean isQuickOrder =  inObj.has("isQuickOrder")?inObj.getBoolean("isQuickOrder"):false;
			
			JSONArray newItems = inObj.getJSONArray("newItems");
			JSONObject orderedItem = null;
			String subOrderId = itemDao.getNextSubOrderId(outletId, orderId);
			ArrayList<Integer> itemIds = new ArrayList<Integer>();
			ArrayList<Integer> addedItemIds = new ArrayList<Integer>();
			int itemQantity = 0;
			Boolean addAddons = false;
			JSONObject subOrder = null;
			int offset = 0;
			
			for (int i = 0; i < newItems.length(); i++) {
				orderedItem = newItems.getJSONObject(i);
				subOrder = null;
				addOns = orderedItem.getJSONArray("addOnArr");
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
					String tableId = "";
					if(order.getInHouse() == AccessManager.DINE_IN) {
						tableId = order.getTableId().split(",")[0];
					}
					menuId = orderedItem.getString("menuId");
					MenuItem menu = menuDao.getMenuById(outletId, menuId);
					subOrder = itemDao.newSubOrder(outletId, settings, order, menu, itemQantity, "",
							subOrderId, inObj.has("userId") ? inObj.getString("userId") : "",
							orderedItem.has("rate") ? new BigDecimal(Double.toString(orderedItem.getDouble("rate"))) : new BigDecimal("0"), 
									tableId);
					if (subOrder == null || subOrder.getInt("status") == -1) {
						itemDao.rollbackTransaction();
						if (subOrder != null) {
							outObj.put("message", subOrder.get("message"));
						}
						printKOT = false;
						return outObj.toString();
					} else
						printKOT = true;
					String commonSpecs = "";
					Boolean addedSpec = false;
					JSONArray specifications = orderedItem.getJSONArray("specArr");
					for (int k = 0; k < specifications.length(); k++) {
						if (specifications.getJSONObject(k).getInt("itemId") == 101) {
							commonSpecs += specifications.getJSONObject(k).getString("spec") + ", ";
						} else {
							if(addedItemIds.contains(specifications.getJSONObject(k).getInt("itemId"))) {
								int itemId = specifications.getJSONObject(k).getInt("itemId") - offset;
								addedSpec = itemDao.addOrderSpecification(outletId, orderId, subOrderId, menuId,
										itemId, specifications.getJSONObject(k).getString("spec"));
								if (!addedSpec) {
									itemDao.rollbackTransaction();
									outObj.put("status", -1);
									outObj.put("message", "Failed to add Specification");
									return outObj.toString();
								}
							}
						}
					}
					if (commonSpecs.length() > 0)
						itemDao.updateSpecifications(outletId, orderId, subOrderId, menuId, commonSpecs);
					if(addAddons) {
						for (int k = 0; k < addOns.length(); k++) {
							if(addOns.getJSONObject(k).getInt("itemId")==j) {
								Boolean addedAddOn = itemDao.addOrderAddon(outletId, order, menu,
										addOns.getJSONObject(k).getInt("qty"), addOns.getJSONObject(k).getString("menuId"), subOrderId,
										addOns.getJSONObject(k).getInt("itemId"), 
										new BigDecimal(Double.toString(addOns.getJSONObject(k).getDouble("rate"))));
			
								if (!addedAddOn) {
									itemDao.rollbackTransaction();
									outObj.put("status", -1);
									outObj.put("message", "Failed to add Addon");
									return outObj.toString();
								}
							}
							addAddons = false;
						}
					}
					if(addOns.length()>0) {
						subOrderId = itemDao.getNextSubOrderId(outletId, orderId);
						offset++;
					}
					itemQantity = 0;
				}
			}
			/*if(settings.getHasKds()) {
				JSONArray changeStateItems = inObj.getJSONArray("changeStateItems");
				for (int i = 0; i < changeStateItems.length(); i++) {
					if (!itemDao.changeOrderStatus(outletId, orderId, changeStateItems.getJSONObject(i).getString("subOrderId"),
							changeStateItems.getJSONObject(i).getString("menuId"))) {
						outObj.put("message", "Failed to close sub order");
						return outObj.toString();
					}
				}
				JSONArray changeQtyItems = inObj.getJSONArray("changeQtyItems");
				for (int i = 0; i < changeQtyItems.length(); i++) {
					if (!itemDao.editSubOrder(outletId, orderId, changeQtyItems.getJSONObject(i).getString("subOrderId"),
							changeQtyItems.getJSONObject(i).getString("menuId"),
							changeQtyItems.getJSONObject(i).getInt("qty"))) {
						outObj.put("message", "Failed to edit sub order");
						return outObj.toString();
					} else {
						MenuItem menu = menuDao.getMenuById(outletId, changeQtyItems.getJSONObject(i).getString("menuId"));
						dao.updateFoodBill(outletId, order, menu.getVegType(), 1, true,
								new BigDecimal(Double.toString(changeQtyItems.getJSONObject(i).getDouble("rate"))));
					}
				}
			}*/
			itemDao.commitTransaction(outletId);
			outObj.put("status", 0);
			outObj.put("message", "Edited order successfully!");
			
			if(isQuickOrder) {
				dao.checkOutOrder(outletId, orderId);
			}
			JSONObject customerDetails = inObj.getJSONObject("editCustomerDetails");
			if (customerDetails.toString().trim().length() > 2) {
				ICustomer custDao = new CustomerManager(false);
				boolean status = false;
				if(customerDetails.has("surName")) {
					status = custDao.editCustomerDetails(outletId, orderId, customerDetails.getString("firstName"), customerDetails.getString("surName"),
							customerDetails.getString("contactNumber"), customerDetails.getString("address"),
							customerDetails.getInt("peopleCount"), customerDetails.has("allergyInfo")?customerDetails.getString("allergyInfo"):"");
				}else {
					status = custDao.editCustomerDetails(outletId, orderId, customerDetails.getString("customer"),
							customerDetails.getString("contactNumber"), customerDetails.getString("address"),
							customerDetails.getInt("peopleCount"), customerDetails.has("allergyInfo")?customerDetails.getString("allergyInfo"):"");
				}
				if(!status) {

					outObj.put("message", "Failed update customer details");
				}
			}
			//Manage Inventory
			IInventory inventory = new InventoryManager(false);
			JSONObject addOn = null;
			for (int i = 0; i < newItems.length(); i++) {
				orderedItem = newItems.getJSONObject(i);
				if (!settings.getHasKds()) {
					inventory.manageInventory(outletId, orderedItem.getString("menuId"), order.getOrderId(), orderedItem.getInt("qty"));
				}
				addOns = orderedItem.getJSONArray("addOnArr");
				for(int j=0; j<addOns.length(); j++) {
					addOn = addOns.getJSONObject(i);
					inventory.manageInventory(outletId, addOn.getString("menuId"), order.getOrderId(), addOn.getInt("qty"));
				}
			}

		} catch (Exception e) {
			itemDao.rollbackTransaction();
			e.printStackTrace();
		}
		
		if (printKOT && !Configurator.getIsServer() && settings.getHasKot())
			if(!settings.getHasKds())
				this.printKOT(outlet, settings, order, false, userId, section);
		return outObj.toString();
	}
	
	@POST
	@Path("/v3/removeCustomerFromOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String removeCustomerFromOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOrder dao = new OrderManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.removeCustomerFromOrder(inObj.getString("outletId"), inObj.getString("orderId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v1/checkKOT")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String checkKOT(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IOrder dao = new OrderManager(false);
		IOutlet outletDao = new OutletManager(false);
		try {
			outObj = new JSONObject();
			outObj.put("status", true);
			outObj.put("message", "Check KOT Printed");
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			Order order = dao.getOrderById(outletId, inObj.getString("orderId"));
			Outlet outlet = outletDao.getOutlet(outletId);
			Settings settings = outletDao.getSettings(outletId);
			this.printKOT(outlet, settings, order, true, order.getWaiterId(), "DEFAULT");
		}catch (Exception e) {
			e.printStackTrace();
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
		OutletManager hotel = null;
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
			dao.commitTransaction(hotelId);
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
		
		IOrderItem dao = new OrderManager(false);
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			if (!dao.changeOrderStatus(inObj.getString("hotelId"), inObj.getString("orderId"),
					inObj.optString("subOrderId"), inObj.optString("menuId"))) {
				outObj.put("message", "Failed to change status");
				return outObj.toString();
			}
			outObj.put("status", 0);
			outObj.put("message", "Edited order successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v3/markFoodReady")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String markFoodReady(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IOrder dao = new OrderManager(false);
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			if (!dao.markFoodReady(inObj.getString("outletId"), inObj.getString("orderId"))) {
				outObj.put("message", "Failed to change status");
				return outObj.toString();
			}
			outObj.put("status", 0);
			outObj.put("message", "Edited order successfully!");
		} catch (Exception e) {
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
		
		IOrder dao = new OrderManager(true);
		try {
			dao.beginTransaction();
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
			dao.commitTransaction(inObj.getString("hotelId"));
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
		
		IOrder dao = new OrderManager(false);
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
		
		IOrder dao = new OrderManager(false);
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
		
		IOrder dao = new OrderManager(false);
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
		
		IOrder dao = new OrderManager(false);
		IRating ratingDao = new RatingManager(false);
		IOutlet outletDao = new OutletManager(false);
		String outletId = "";
		try {
			inObj = new JSONObject(jsonObject);
			outletId = inObj.getString("hotelId");
			Outlet outlet = outletDao.getOutlet(outletId);
			Order order = dao.getOrderById(outletId, inObj.getString("orderId"));
			JSONObject ratings = inObj.getJSONObject("ratings");
			if(ratings.getInt("ambianceRating")<4 || ratings.getInt("qualityOfFoodRating")<4 || 
					ratings.getInt("serviceRating")<4 ||ratings.getInt("hygieneRating")<4) {
				String smsText = "Alert! Low Rating received from " + inObj.getString("customerName") 
					+ " on table " + order.getTableId() + ". Mob.: " + inObj.getString("customerNumber")
					+ ". A: " + ratings.getInt("ambianceRating") + ", H: "+ ratings.getInt("hygieneRating") 
					+ ", F: "+ratings.getInt("qualityOfFoodRating")+", S: "+ratings.getInt("serviceRating")+". "
					+ "Review: " + inObj.getString("reviewSuggestions");
				String subject = "Alert| Low Rating Received";
				String emailText = "<div style='width:350px; ' class='alert alert-warning'><h3>Low Rating received at "+outlet.getName()+" for order. Bill No. "
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
				this.SendEmailAndSMS(outletId, subject, emailText, smsText, "", AccessManager.DESIGNATION_MANAGER, true, false);
			}
				
			if (!ratingDao.submitRatings(outletId, inObj.getString("orderId"),
					inObj.getString("customerName"), inObj.getString("customerNumber"),
					inObj.getString("customerBirthdate"), inObj.getString("customerAnniversary"),
					inObj.getString("reviewSuggestions"), ratings,
					inObj.has("wantsPromotion") ? inObj.getBoolean("wantsPromotion") : Boolean.FALSE,
					inObj.has("customerEmailId")?inObj.getString("customerEmailId"):"", 
					inObj.has("referencesForReview")?inObj.getString("referencesForReview"):"")) {
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
	public String getActiveOrders(@QueryParam("hotelId") String outletId, @QueryParam("userId") String userId) {
		JSONArray outArr = new JSONArray();
		JSONObject tempObj = null;
		JSONObject outObj = new JSONObject();
		
		IOrder dao = new OrderManager(false);
		IOutlet outletDao = new OutletManager(false);
		Settings settings = outletDao.getSettings(outletId);
		if(settings==null) {
			try {
				outObj.put("status", false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return outObj.toString();
		}
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		ArrayList<HomeDelivery> order = dao.getActiveHomeDeliveries(settings, userId);
		try {
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNumber());
				tempObj.put("address", order.get(i).getAddress());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				tempObj.put("orderDateTime", order.get(i).getOrderDateTime());
				outArr.put(tempObj);
			}
			outObj.put("hdOrders", outArr);
			outArr = new JSONArray();
			order = dao.getActiveTakeAway(settings, userId);
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("portal", portalDao.getOnlineOrderingPortalById(outletId, order.get(i).getTakeAwayType()).getName());
				tempObj.put("takeAwayType", order.get(i).getTakeAwayType());
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNumber());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("reference", order.get(i).getReference());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				tempObj.put("isFoodReady", order.get(i).getIsFoodReady());
				tempObj.put("orderNumber", order.get(i).getOrderNumber());
				tempObj.put("orderDateTime", order.get(i).getOrderDateTime());
				outArr.put(tempObj);
			}
			outObj.put("taOrders", outArr);
			outArr = new JSONArray();
			order = dao.getActiveBarOrders(settings, userId);
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNumber());
				tempObj.put("address", order.get(i).getAddress());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("reference", order.get(i).getReference());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				tempObj.put("orderDateTime", order.get(i).getOrderDateTime());
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
	public String getActiveBarOrders(@QueryParam("hotelId") String outletId, @QueryParam("userId") String userId) {
		JSONArray outArr = new JSONArray();
		JSONObject tempObj = null;
		
		IOrder dao = new OrderManager(false);
		IOrderItem itemDao = new OrderManager(false);
		IOutlet outletDao = new OutletManager(false);
		Settings settings = outletDao.getSettings(outletId);
		ArrayList<HomeDelivery> order = dao.getActiveBarOrders(settings, userId);
		try {
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNumber());
				tempObj.put("address", order.get(i).getAddress());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("reference", order.get(i).getReference());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				tempObj.put("total", itemDao.getOrderTotal(outletId, order.get(i).getOrderId()));
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
	public String getActiveTakeAway(@QueryParam("hotelId") String outletId, @QueryParam("userId") String userId) {
		JSONArray outArr = new JSONArray();
		JSONObject tempObj = null;
		
		IOrder dao = new OrderManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		IOrderItem itemDao = new OrderManager(false);
		IOutlet outletDao = new OutletManager(false);
		Settings settings = outletDao.getSettings(outletId);
		ArrayList<HomeDelivery> order = dao.getActiveTakeAway(settings, userId);
		try {
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", portalDao.getOnlineOrderingPortalById(outletId, order.get(i).getTakeAwayType()).getName());
				tempObj.put("phone", order.get(i).getMobileNumber());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("reference", order.get(i).getReference());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				tempObj.put("total", itemDao.getOrderTotal(outletId, order.get(i).getOrderId()));
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
	public String getActiveHomeDeliveries(@QueryParam("hotelId") String outletId, @QueryParam("userId") String userId) {
		JSONArray outArr = new JSONArray();
		JSONObject tempObj = null;
		
		IOrder dao = new OrderManager(false);
		IOrderItem itemDao = new OrderManager(false);
		IOutlet outletDao = new OutletManager(false);
		Settings settings = outletDao.getSettings(outletId);
		ArrayList<HomeDelivery> order = dao.getActiveHomeDeliveries(settings, userId);
		try {
			for (int i = 0; i < order.size(); i++) {
				tempObj = new JSONObject();
				tempObj.put("customer", order.get(i).getCustomer());
				tempObj.put("phone", order.get(i).getMobileNumber());
				tempObj.put("address", order.get(i).getAddress());
				tempObj.put("billNo", order.get(i).getBillNo());
				tempObj.put("orderId", order.get(i).getOrderId());
				tempObj.put("remarks", order.get(i).getRemarks());
				tempObj.put("state", order.get(i).getState());
				tempObj.put("total", itemDao.getOrderTotal(outletId, order.get(i).getOrderId()));
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
		JSONObject out = new JSONObject();
		
		ICustomer dao = new CustomerManager(false);
		Customer customer = dao.getCustomerDetails(hotelId, mobileNo);
		try {
			out.put("customer", "");
			out.put("address", "");
			if (customer != null) {
				out.put("id", customer.getId());
				out.put("customer", customer.getFullName());
				out.put("firstName", customer.getFirstName());
				out.put("surName", customer.getSurName());
				out.put("address", customer.getAddress());
				out.put("address2", customer.getAddress2());
				out.put("address3", customer.getAddress3());
				out.put("mobileNo", customer.getMobileNumber());
				out.put("birthdate", customer.getBirthdate());
				out.put("anniversary", customer.getAnniversary());
				out.put("userType", customer.getUserType());
				out.put("points", customer.getPoints());
				out.put("remarks", customer.getRemarks());
				out.put("allergyInfo", customer.getAllergyInfo());
				out.put("visitCount", customer.getVisitCount());
				out.put("customerEmailId", customer.getEmailId());
				out.put("referenceForReview", customer.getReference());
				out.put("wallet", customer.getWallet());
				out.put("promotionalCash", customer.getPromotionalCash());
				out.put("sendSMS", customer.getSendSMS());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	@GET
	@Path("/v2/getCustomerDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomerDetailsV2(@QueryParam("hotelId") String hotelId, @QueryParam("mobileNumber") String mobileNumber) {
		ICustomer dao = new CustomerManager(false);
		return new JSONObject(dao.getCustomerDetails(hotelId, mobileNumber)).toString();
	}

	@POST
	@Path("/v3/updateAllCustomers")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String updateAllCustomers(String jsonObject) {
		ICustomer dao = new CustomerManager(true);
		JSONObject outObj = new JSONObject();
		JSONObject inObj = null;
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("outletId");
			JSONArray customers = inObj.getJSONArray("customers");
			outObj.put("status", false);
			dao.beginTransaction();
			if(dao.deleteAllCustomers(outletId)) {
				if(dao.addAllCustomers(outletId, customers)) {
					outObj.put("status", true);
					outObj.put("message", "Cutomer data successfully synced with server.");
					dao.commitTransaction(outletId);
				}else {
					dao.rollbackTransaction();
					outObj.put("message", "Could not update customers.");
				}
			}else {
				outObj.put("message", "Could not delete customers.");
				dao.rollbackTransaction();
			}
			
		} catch (JSONException e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/getCustomersForSMS")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public String getCustomersForSMS(String jsonObject) {
		JSONObject out = new JSONObject();
		JSONObject inObj = null;
		
		SendSMS sms = new SendSMS();
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("HH:mm");
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		
		ICustomer dao = new CustomerManager(false);
		IOutlet outletDao = new OutletManager(false);
		IOrder orderDao = new OrderManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			out.put("status", false);
			String outletId = inObj.getString("hotelId");
			Outlet outlet = outletDao.getOutlet(outletId);
			Settings settings = outletDao.getSettings(outletId);
			if(!netIsAvailable() || !settings.getHasSms()) {
				return out.toString();
			}
			ArrayList<Customer> customers = dao.getCustomersForSMS(outletId);
			if (customers == null) {
				return out.toString();
			}
			if(settings.getHasLoyalty()==1)
				return out.toString();
			Date curtime = sdf.parse(now.format(dateFormat));
			String message = "";
			for (Customer customer : customers) {
				if(customer.getCompleteTimestamp().equals(""))
					continue;
				Date d1 = sdf.parse(customer.getCompleteTimestamp());
				long elapsed = curtime.getTime() - d1.getTime();
				int minutes = (int) (elapsed / 1000) / 60;
				if (minutes > 5 && customer.getMobileNumber().length() == 10) {
					System.out.println("Sending sms to " + customer.getFullName());
					String name = customer.getFullName().equals("")?",": " "+customer.getFullName() + "!";
					message = "Hi" + name + " Greetings from " + outlet.getName()
							+ ". Thank you for dining with us. It was a pleasure to have you over. Have a great day!";
					if(inObj.getString("hotelId").equals("po0001"))
						message = "Hi "+name+" Greetings from Poush. Thank you for dining with us. It was a pleasure to have you over. For reservations please contact: 9820113235 / 9821213232";
					if (orderDao.updateOrderSMSStatusDone(outletId, customer.getOrderId()))
						sms.sendSms(message, customer.getMobileNumber());
				} else if (customer.getMobileNumber().length() != 10) {
					orderDao.updateOrderSMSStatusDone(outletId, customer.getOrderId());
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
		JSONObject outObj = new JSONObject();
		JSONArray cusArray = new JSONArray();
		
		ICustomer dao = new CustomerManager(false);
		ArrayList<Customer> customers = dao.getAllCustomerDetails(hotelId, page);
		String mobileNo = "";
		try {
			if (customers != null) {
				for (int i = 0; i < customers.size(); i++) {
					JSONObject out = new JSONObject();
					mobileNo = customers.get(i).getMobileNumber();
					out.put("id", customers.get(i).getId());
					out.put("customerName", customers.get(i).getFullName());
					out.put("firstName", customers.get(i).getFirstName());
					out.put("surName", customers.get(i).getSurName());
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAllCustomers")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCustomers(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		
		ICustomer dao = new CustomerManager(false);
		ArrayList<Customer> customers = dao.getAllCustomers(hotelId);
		try {
			outObj.put("customers", customers);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}


	@GET
	@Path("/v1/getAllCustomersForOrdering")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomersForOrdering(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		
		ICustomer dao = new CustomerManager(false);
		ArrayList<CustomerForOrdering> customers = dao.getCustomersForOrdering(hotelId);
		try {
			outObj.put("customers", customers);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getCustomerDetailBySearch")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCustomerDetailBySearch(@QueryParam("hotelId") String hotelId, @QueryParam("query") String query) {
		JSONObject outObj = new JSONObject();
		JSONArray cusArray = new JSONArray();
		
		ICustomer dao = new CustomerManager(false);
		IRating ratingDao = new RatingManager(false);
		ArrayList<Customer> customers = dao.getAllCustomerDetailsBySearch(hotelId, query);
		String mobileNo = "";
		try {
			if (customers != null) {
				for (int i = 0; i < customers.size(); i++) {
					JSONObject out = new JSONObject();
					mobileNo = customers.get(i).getMobileNumber();
					out.put("id", customers.get(i).getId());
					out.put("customerName", customers.get(i).getFullName());
					out.put("firstName", customers.get(i).getFirstName());
					out.put("surName", customers.get(i).getFirstName());
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
			outObj.put("avgRatingFood", ratingDao.getOverallAvgFood(hotelId));
			outObj.put("avgRatingAmbiance", ratingDao.getOverallAvgAmbiance(hotelId));
			outObj.put("avgRatingService", ratingDao.getOverallAvgService(hotelId));
			outObj.put("avgRatingHygiene", ratingDao.getOverallAvgHygiene(hotelId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getAllCustomerDetailsForOrdering")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllCustomerDetailsForOrdering(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		JSONArray cusArray = new JSONArray();
		
		ICustomer dao = new CustomerManager(false);
		ArrayList<Customer> customers = dao.getAllCustomerDetailsForOrdering(hotelId);
		String mobileNo = "";
		try {
			if (customers != null) {
				for (int i = 0; i < customers.size(); i++) {
					JSONObject out = new JSONObject();
					mobileNo = customers.get(i).getMobileNumber();
					out.put("id", customers.get(i).getId());
					out.put("customerName", customers.get(i).getFullName());
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
		JSONObject itemDetails = new JSONObject();
		
		ICustomer dao = new CustomerManager(false);
		Customer customer = dao.getCustomerBySearch(hotelId, query);
		try {
			itemDetails.put("status", false);
			if(customer != null) {
				itemDetails.put("status", true);
				itemDetails.put("customerName", customer.getFullName());
				itemDetails.put("address", customer.getAddress());
				itemDetails.put("mobileNo", customer.getMobileNumber());
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
		
		ICustomer dao = new CustomerManager(false);
		try {
			JSONObject inObj = new JSONObject(jsonObject);
			outObj.put("status",
					dao.updateCustomer(inObj.getString("hotelId"), inObj.getInt("id"), inObj.getString("firstName"), inObj.getString("surName"), inObj.getString("phone"),
							inObj.getString("birthdate"), inObj.getString("anniversary"), inObj.getString("remarks"),
							inObj.getString("allergyInfo"), inObj.getString("address"), inObj.getBoolean("wantsPromotion"), inObj.has("emailId")?inObj.getString("emailId"):"",
							inObj.has("sex")?inObj.getString("sex"):""));
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
		
		IOrder dao = new OrderManager(true);
		try {
			dao.beginTransaction();
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newHomeDeliveryOrder(inObj.getString("hotelId"), inObj.getString("userId"),
					inObj.getString("customer"), inObj.getString("mobile"), inObj.getString("address"),
					inObj.getString("allergyInfo"), inObj.has("remarks")?inObj.getString("remarks"):"",
							inObj.has("section")?inObj.getString("section"):"DEFAULT");
			if(outObj.getInt("status") == 0) {
				dao.commitTransaction(inObj.getString("hotelId"));
			}else {
				dao.rollbackTransaction();
			}
		} catch (Exception e) {
			dao.rollbackTransaction();
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
		
		IOrder dao = new OrderManager(true);
		try {
			dao.beginTransaction();
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newTakeAwayOrder(inObj.getString("hotelId"), inObj.getString("userId")
					, inObj.has("customer")?inObj.getString("customer"):""
					, inObj.has("customerDetails")?inObj.getJSONObject("customerDetails"):null
					, inObj.has("mobile")?inObj.getString("mobile"):""
					, inObj.has("externalId")?inObj.getString("externalId"):""
					, inObj.getString("allergyInfo"), inObj.has("remarks")?inObj.getString("remarks"):""
					, inObj.has("externalOrderId")?inObj.getString("externalOrderId"):""
					, inObj.has("discountCodes")?inObj.getJSONArray("discountCodes"):new JSONArray(),
					inObj.has("section")?inObj.getString("section"):"DEFAULT",
					inObj.has("cashToBeCollected")?inObj.getDouble("cashToBeCollected"):0.0,
					inObj.has("goldDiscount")?inObj.getDouble("goldDiscount"):0.0,
					inObj.has("zomatoVoucherAmount")?inObj.getDouble("zomatoVoucherAmount"):0.0,
					inObj.has("piggyBank")?inObj.getDouble("piggyBank"):0.0,
					inObj.has("amountReceivable")?new BigDecimal(inObj.getDouble("amountReceivable")):new BigDecimal("0.0"));
			if(outObj.getInt("status") == 0) {
				dao.commitTransaction(inObj.getString("hotelId"));
			}else {
				dao.rollbackTransaction();
			}
			dao = new OrderManager(false);
			Order order = dao.getOrderById(inObj.getString("hotelId"), outObj.getString("orderId"));
			outObj.put("orderNumber", order.getOrderNumber());
			
		} catch (Exception e) {
			dao.rollbackTransaction();
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
		
		IOrder dao = new OrderManager(true);
		try {
			dao.beginTransaction();
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			outObj = dao.newBarOrder(inObj.getString("hotelId"), inObj.getString("userId"), inObj.has("reference")?inObj.getString("reference"):""
				, inObj.has("remarks")?inObj.getString("remarks"):"",
				inObj.has("section")?inObj.getString("section"):"DEFAULT");
			if(outObj.getInt("status") == 0) {
				dao.commitTransaction(inObj.getString("hotelId"));
			}else {
				dao.rollbackTransaction();
			}
		} catch (Exception e) {
			dao.rollbackTransaction();
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
		
		IOrderItem dao = new OrderManager(false);
		IOrder orderDao = null;
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			ArrayList<OrderItem> orderedItems = dao.getCancellableOrderedItems(hotelId, orderId);
			if(orderedItems.size()>0) {
				outObj.put("message", "Failed to cancel the order.");
				return outObj.toString();
			}
			orderedItems = dao.getReturnedItems(hotelId, orderId);

			orderDao = new OrderManager(false);
			if(orderedItems.size()>0) {
				orderDao.changeOrderStateToCancelled(hotelId, orderId);
				Double total = 0.0;
				for(int i=0; i<orderedItems.size(); i++) {
					total += orderedItems.get(i).getQty()+orderedItems.get(i).getRate().doubleValue();
				}
				Order order = orderDao.getOrderById(hotelId, orderId);
				String subject = "Order Cancelled. Bill No. " + order.getBillNo();
				String text = "<div style='width:300px; '><div class='alert alert-warning'><b>Bill No. " + order.getBillNo()
						+ " </b>has been cancelled.</h3><p> Details as follows:</p>" + "<div>Time: "
						+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")) + "</div><div>Order punched by: "
						+ orderId.split(":")[0] + "</div><div>Number of items returned: " + orderedItems.size()
						+ "</div><div>Total amount (without tax): " + total
						+ "</div></div>";
			
				SendEmail(hotelId, subject, text, "", true, false);
			}else if(!orderDao.deleteOrder(hotelId, orderId)) {
				outObj.put("message", "Failed to cancel the order");
				return outObj.toString();
			}
			
			outObj.put("status", true);
			outObj.put("message", "Success");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
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
	@Produces(MediaType.APPLICATION_JSON)
	public String uploadFile(@FormDataParam("hotelId") String hotelId, @FormDataParam("directory") String directory,
			@FormDataParam("image_file") InputStream uploadedInputStream,
			@FormDataParam("image_file") FormDataContentDisposition fileDetail, @FormDataParam("itemId") String itemId) throws Exception {
		OutputStream os = null;
		JSONObject outObj = new JSONObject();
		String originalName = fileDetail.getFileName();
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			String[] ext = originalName.split("\\.");

			String fileName = itemId + "." + ext[ext.length - 1];
			System.out.println("Uploading " + fileName);
	 
	        String tomcatDir = Configurator.getTomcatLocation()+"/webapps/";
	        
	        String uploadDir = "Images/hotels/"+ hotelId +"/"+ directory + "/";
	       
	        int width = 250;
	        int height = 250;
	 
	        // Constructs a BufferedImage of one of the predefined image types.
	        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	 
	        // Create a graphics which can be used to draw into the buffered image
	        Graphics2D g2d = bufferedImage.createGraphics();
	 
	        // fill all the image with white
	        g2d.setColor(Color.white);
	        g2d.fillRect(0, 0, width, height);
	 
	        // Disposes of this graphics context and releases any system resources that it is using. 
	        g2d.dispose();
	 
	        // Save as JPEG
	        File file = new File(tomcatDir+uploadDir+fileName);
	        
	        if(ext[ext.length - 1].equals("jpg") || ext[ext.length - 1].equals("jpeg"))
	        	ImageIO.write(bufferedImage, "jpg", file);
	        else {
	        	outObj.put("message", "Format Error. Please provide image either in jpg or png format.");
	        	return outObj.toString();
	        }
			
	        os = new FileOutputStream(tomcatDir+uploadDir+fileName);
			byte[] b = new byte[2048];
			int length;
			
			while ((length = uploadedInputStream.read(b)) != -1) {
				os.write(b, 0, length);
			}
			String ip = Configurator.getIp();
			if(Configurator.getIsServer()) {
				ip = "api.orderon.co.in";
			}
			String host = "http://"+ip+":8080/";
			if(directory.equals("MenuItems") || directory.equals("CoverImages")) {
				IMenuItem dao = new MenuItemManager(false);
				dao.updateMenuImageUrl(hotelId, itemId, host+uploadDir+fileName);
			}else if(directory.equals("Collections")) {
				ICollection dao = new CollectionManager(false);
				dao.updateCollectionImageUrl(hotelId, Integer.parseInt(itemId), host+uploadDir+fileName);
			}
			outObj.put("status", true);
			outObj.put("imgUrl", host+uploadDir+fileName);
			outObj.put("message", "Successfully uploaded Image.");
			System.out.println("Successfully uploaded " + fileName);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return outObj.toString();
	}

	public boolean deleteFile(String hotelId, String originalName, String folder) {
		File fileToDelete = new File("http://"+Configurator.getIp()+":8080/Images" + "/hotels/" + hotelId + folder + originalName);
		return fileToDelete.delete();
	}

	public boolean deleteFile(String filePath) {
		File fileToDelete = new File(filePath);
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
	
	@GET
	@Path("/v1/canStopService")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String hasAuthorizedAttendance(@QueryParam("hotelId") String hotelId, @QueryParam("serviceDate") String serviceDate) {
		JSONObject outObj = new JSONObject();

		IAttendance dao = new AttendanceManager(false);
		IOrder orderDao = new OrderManager(false);
		try {
			if (dao.hasAuthorizedAttendance(hotelId, serviceDate)) {
				outObj.put("status", false);
				outObj.put("message", "Kindly authorize attendance of your employees");
				return outObj.toString();
			}
			
			if (orderDao.hasCheckedOutOrders(hotelId, serviceDate)) {
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
	
	@POST
	@Path("/v1/addDiscount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addDiscount(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IDiscount dao = new DiscountManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			if (dao.discountExists(hotelId, inObj.getString("name"))) {
				outObj.put("status", false);
				outObj.put("message", "Discount Exists");
			} else {
				JSONArray bogoIds = new JSONArray();
				JSONArray bogoItems = inObj.getJSONArray("bogoItems");
				for(int i=0; i<bogoItems.length(); i++) {
					bogoIds.put(menuDao.getMenuItemByTitle(hotelId, bogoItems.get(i).toString()).getMenuId());
				}
			
				outObj = dao.addDiscount(hotelId, inObj.getString("name"),
								inObj.getString("description"), inObj.getInt("type"), inObj.getInt("foodValue"),
								inObj.getInt("barValue"), inObj.getString("startDate"), inObj.getString("expiryDate"),
								inObj.getString("usageLimit"), inObj.getJSONArray("validCollections"), inObj.getInt("offerQuantity"),
								inObj.getBoolean("hasExpiry"), inObj.getString("offerType"), inObj.getBoolean("applicableOnZomato"),
								bogoIds.toString(), inObj.getString("startTime"), inObj.getString("endTime"),
								inObj.getInt("minOrderAmount"), inObj.getBoolean("firstOrderOnly"), 
								inObj.getInt("maxFoodDiscountAmount"), inObj.getInt("maxBarDiscountAmount"));
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
		IDiscount dao = new DiscountManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj = dao.editDiscount(inObj.getString("hotelId"), inObj.getString("name"),
							inObj.getString("description"), inObj.getInt("type"), inObj.getInt("foodValue"),
							inObj.getInt("barValue"), inObj.getString("startDate"), inObj.getString("expiryDate"), 
							inObj.getString("usageLimit"), inObj.getJSONArray("validCollections"), inObj.getInt("offerQuantity"),
							inObj.getBoolean("hasExpiry"), inObj.getString("offerType"), inObj.getBoolean("applicableOnZomato"),
							inObj.getString("bogoItems"), inObj.getString("startTime"), inObj.getString("endTime"),
							inObj.getInt("minOrderAmount"), inObj.getBoolean("firstOrderOnly"));
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
		JSONArray discountArr = new JSONArray();
		JSONArray collectionArr = null;
		JSONObject collObj = null;
		JSONObject outObj = new JSONObject();
		JSONObject discountDetails = null;
		String[] collections = {};
		JSONArray bogoItemNames = new JSONArray();
		JSONArray bogoItems = new JSONArray();
		
		IDiscount dao = new DiscountManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		ArrayList<Discount> discountItems = dao.getAllDiscounts(hotelId);
		try {
			for (int i = 0; i < discountItems.size(); i++) {
				discountDetails = new JSONObject();
				bogoItemNames = new JSONArray();
				collectionArr = new JSONArray();
				discountDetails.put("offerType", discountItems.get(i).getOfferType());
				discountDetails.put("name", discountItems.get(i).getName());
				discountDetails.put("description", discountItems.get(i).getDescription());
				discountDetails.put("type", discountItems.get(i).getType());
				discountDetails.put("offerType", discountItems.get(i).getOfferType());
				discountDetails.put("foodValue", discountItems.get(i).getFoodValue());
				discountDetails.put("barValue", discountItems.get(i).getBarValue());
				discountDetails.put("startDate", discountItems.get(i).getStartDate());
				discountDetails.put("expiryDate", discountItems.get(i).getExpiryDate());
				discountDetails.put("startTime", discountItems.get(i).getStartTime());
				discountDetails.put("endTime", discountItems.get(i).getEndTime());
				discountDetails.put("startTime", discountItems.get(i).getStartTime());
				discountDetails.put("endTime", discountItems.get(i).getEndTime());
				discountDetails.put("usageLimit", discountItems.get(i).getUsageLimit());
				discountDetails.put("applicableOnZomato", discountItems.get(i).getApplicableOnZomato());
				discountDetails.put("offerQuantity", discountItems.get(i).getOfferQuantity());
				bogoItems = discountItems.get(i).getBogoItems();
				for(int j=0; j<bogoItems.length(); j++) {
					bogoItemNames.put(menuDao.getMenuById(hotelId, bogoItems.get(j).toString()).getTitle());
				}
				discountDetails.put("bogoItems", bogoItemNames);
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
	@Path("/v3/Integration/getDiscounts")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDiscountsForIntegration(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		
		AccessManager dao = new AccessManager(false);
		IDiscount discountDao = new DiscountManager(false);
		ArrayList<Discount> discounts = discountDao.getDiscountsForZomato(hotelId);
		try {
			JSONArray discountArr = new JSONArray();
			JSONArray timings = null;
			JSONObject timingObj = null;
			JSONObject discountObj = null;
			for(int i=0; i<discounts.size(); i++) {
				discountObj = new JSONObject();
				discountObj.put("name", discounts.get(i).getName());
				discountObj.put("type", AccessManager.getDiscountType(discounts.get(i).getType()));
				discountObj.put("value", discounts.get(i).getFoodValue());
				discountObj.put("startDate", dao.formatDate(discounts.get(i).getStartDate(), "dd/MM/yyyy", "yyyy-MM-dd"));
				discountObj.put("expiryDate", dao.formatDate(discounts.get(i).getExpiryDate(), "dd/MM/yyyy", "yyyy-MM-dd"));
				discountObj.put("offerType", discounts.get(i).getOfferType());
				
				timings = new JSONArray();
				timingObj = new JSONObject();
				timingObj.put("start_time", discounts.get(i).getStartTime().substring(0, 5));
				timingObj.put("end_time", discounts.get(i).getEndTime().substring(0, 5));
				timings.put(timingObj);
				
				discountObj.put("timings", timings);
				discountObj.put("minOrderAmount", discounts.get(i).getMinOrderAmount());
				discountObj.put("firstOrderOnly", discounts.get(i).getFirstOrderOnly()?1:0);
				discountObj.put("isActive", discounts.get(i).getIsActive()?1:0);
				discountArr.put(discountObj);
			}
			outObj.put("discounts", discountArr);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return outObj.toString();
	}

	@GET
	@Path("/v1/getDiscount")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDiscount(@QueryParam("hotelId") String hotelId, @QueryParam("name") String name) {
		JSONObject discountDetails = new JSONObject();
		JSONArray collectionArr = new JSONArray();
		JSONObject collObj = null;
		String[] collections = {};
		
		AccessManager dao = new AccessManager(false);
		IDiscount discountDao = new DiscountManager(false);
		try {
			Discount discount = discountDao.getDiscountByName(hotelId, name);
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
				discountDetails.put("applicableOnZomato", discount.getApplicableOnZomato());
				discountDetails.put("firstOrderOnly", discount.getFirstOrderOnly());
				discountDetails.put("offerType", discount.getOfferType());
				discountDetails.put("bogoItems", discount.getBogoItems().toString());
				discountDetails.put("offerQuantity", discount.getOfferQuantity());
				discountDetails.put("startTime", discount.getStartTime());
				discountDetails.put("endTime", discount.getEndTime());
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
		
		IDiscount dao = new DiscountManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			if(inObj.getString("name").equals("ZOMATO_VOUCHER")) {
				outObj.put("message", "Cannot delete this discount code.");
				return outObj.toString();
			}else if(inObj.getString("name").equals("PIGGYBANK")) {
				outObj.put("message", "Cannot delete this discount code.");
				return outObj.toString();
			}else if(inObj.getString("name").equals("FIXED_RUPEE_DISCOUNT")) {
				outObj.put("message", "Cannot delete this discount code.");
				return outObj.toString();
			}else
			outObj.put("status", dao.deleteDiscount(inObj.getString("hotelId"), inObj.getString("name")));
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
		
		IDiscount dao = new DiscountManager(true);
		IOrder orderDao = new OrderManager(false);
		try {
			dao.beginTransaction();
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			
			if(inObj.getString("discountType").equals("EWARDS")) {
				outObj.put("status", orderDao.updateEWardsOfferDetails(outletId, inObj.getString("orderId"), 
						inObj.getInt("points"), inObj.getString("couponCode"), inObj.getInt("offerType")));
			}else {
				outObj = dao.applyDiscount(outletId, orderDao.getOrderById(outletId, inObj.getString("orderId")),
						inObj.getString("discountCode"), inObj.getString("discountType"));
			}
			
			if(outObj.getBoolean("status")) {
				dao.commitTransaction(inObj.getString("hotelId"));
			}else {
				dao.rollbackTransaction();
			}
		} catch (Exception e) {
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
		
		IDiscount dao = new DiscountManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.removeDiscount(inObj.getString("hotelId"), inObj.getString("orderId")));
			
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
		JSONArray arr = new JSONArray();
		
		IUser userDao = new UserManager(false);
		IReport reportDao = new ReportManager(false);
		IEmployee empDao = new EmployeeManager(false);
		IService serviceDao = new ServiceManager(false);
		IExpense expenseDao = new ExpenseManager(false);
		
		ServiceLog service = serviceDao.getCurrentService(hotelId);
		
		Report reportItems;
		MonthReport report;
		boolean visible = false;
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;

		ArrayList<MonthReport> weeklyRevenue = reportDao.getWeeklyRevenue(hotelId, visible);
		ArrayList<YearlyReport> yearReport = reportDao.getYearlyOrders(hotelId, visible);
		try {
			outObj.put("status", false);
			if(serviceDate.length()>0) {
				report = reportDao.getTotalOrdersForCurMonth(hotelId, serviceDate.substring(0, 7), visible);
				outObj.put("totalOrders", report.getTotalOrders());
				report = reportDao.getBestWaiter(hotelId, serviceDate.substring(0, 7), visible);
				if (report == null) {
					outObj.put("waiterName", "None Yet");
					outObj.put("waitersOrders", "0");
				} else {
					Employee emp = empDao.getEmployeeById(hotelId, report.getBestWaiter());
					outObj.put("waiterName", emp.getFullName());
					outObj.put("waitersOrders", report.getTotalOrderByWaiter());
					outObj.put("waitersImage", "/hotels/" + hotelId + "/Employees/" + report.getBestWaiter() + ".jpg");
					outObj.put("waiterHireDate", emp.getHiringDate());
				}

				report = reportDao.getMostOrderedItem(hotelId, serviceDate.substring(0, 7), visible);
				if (report == null) {
					outObj.put("title", "None Yet");
					outObj.put("orderCount", "0");
				} else {
					outObj.put("title", report.getBestItem());
					outObj.put("menuId", report.getItemId());
					outObj.put("orderCount", report.getItemOrderCount());
				}
	
				//outObj.put("cashBalance", bankDao.getCashBalance(hotelId, section));
				reportItems = reportDao.getTotalSalesForService(hotelId, serviceDate, serviceType, section, visible);
				
				outObj.put("foodBill", reportItems.getFoodBill());
				outObj.put("barBill", reportItems.getBarBill());
				BigDecimal temp = (reportItems.getGst().add(reportItems.getVatBar()).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
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
				outObj.put("pendingSale", reportDao.getPendingSale(hotelId));
				temp = (reportItems.getCashPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				outObj.put("cash", temp.divide(new BigDecimal("100")));
				temp = (reportItems.getCardPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				outObj.put("card", temp.divide(new BigDecimal("100")));
				temp = (reportItems.getAppPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				outObj.put("app", temp.divide(new BigDecimal("100")));
				temp = (reportItems.getWalletPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				outObj.put("wallet", temp.divide(new BigDecimal("100")));
				temp = (reportItems.getCreditAmount().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				outObj.put("credit", temp.divide(new BigDecimal("100")));
				temp = (reportItems.getFoodDiscount().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);;
				outObj.put("foodDiscount", temp.divide(new BigDecimal("100")));
				temp = (reportItems.getBarDiscount().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);;
				outObj.put("barDiscount", temp.divide(new BigDecimal("100")));
				outObj.put("zomato", reportDao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.ZOMATO.toString()));
				outObj.put("zomatoPickup", reportDao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.ZOMATO_PICKUP.toString()));
				outObj.put("swiggy", reportDao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.SWIGGY.toString()));
				outObj.put("foodPanda", reportDao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.FOODPANDA.toString()));
				outObj.put("uberEats", reportDao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.UBEREATS.toString()));
				outObj.put("foodiloo", reportDao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.FOODILOO.toString()));
				outObj.put("payTm", reportDao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.PAYTM.toString()));
				outObj.put("voidTrans", reportDao.getVoidTransactions(hotelId, serviceDate, serviceType));
				outObj.put("nc", reportItems.getNC());
				temp = (reportItems.getCashPayment()
						.add(new BigDecimal(service.getCashInHand()))
						.add(expenseDao.getTotalCashPayIns(hotelId, service))
						.subtract(expenseDao.getTotalCashExpenses(hotelId, service))
						.multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
				outObj.put("cashBalance", temp.divide(new BigDecimal("100")));

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
	@Path("/v3/getAnalytics")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAnalytics(@QueryParam("outletId") String outletId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		JSONObject outObj = new JSONObject();
		
		IReport reportDao = new ReportManager(false);		
		
		Report reportItems;
		
		try {
			outObj.put("status", false);
			
			reportItems = reportDao.getAnalytics(outletId, startDate, endDate);
			
			outObj.put("foodBill", reportItems.getFoodBill());
			outObj.put("barBill", reportItems.getBarBill());
			BigDecimal temp = (reportItems.getGst().add(reportItems.getVatBar()).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
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
			outObj.put("pendingSale", reportDao.getPendingSale(outletId));
			temp = (reportItems.getCashPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("cash", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getCardPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("card", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getAppPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("app", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getWalletPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("wallet", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getCreditAmount().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("credit", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getFoodDiscount().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);;
			outObj.put("foodDiscount", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getBarDiscount().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);;
			outObj.put("barDiscount", temp.divide(new BigDecimal("100")));
			outObj.put("zomato", reportItems.getZomatoSale());
			outObj.put("swiggy", reportItems.getSwiggySale());
			outObj.put("foodPanda", reportItems.getFoodPandaSale());
			outObj.put("uberEats", reportItems.getUberEatsSale());
			//outObj.put("voidTrans", reportDao.getVoidTransactions(hotelId, startDate, endDate, ""));
			//outObj.put("nc", reportItems.getNC());
			
			outObj.put("cashBalance", temp.divide(new BigDecimal("100")));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getSalesForService")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSalesForService(@QueryParam("hotelId") String outletId, @QueryParam("serviceDate") String serviceDate,
			@QueryParam("section") String section, @QueryParam("serviceType") String serviceType) {
		JSONObject outObj = new JSONObject();
		Report reportItems = null;
		
		IReport reportDao = new ReportManager(false);
		IOutlet outletDao = new OutletManager(false);
		
		Settings settings = outletDao.getSettings(outletId);
		try {
			outObj.put("status", false);
			if(settings.getDeductionType() == AccessManager.DEDUCTION_HIDE)
				reportItems = reportDao.getTotalSalesForService(outletId, serviceDate, serviceType, section, false);
			else if(settings.getDeductionType() == AccessManager.DEDUCTION_DELETE_MONTHLY)
				reportItems = reportDao.getTotalSalesForMonth(outletId, serviceDate.substring(0, 7), section, false);
			else if(settings.getDeductionType() == AccessManager.DEDUCTION_DELETE_DAILY)
				reportItems = reportDao.getTotalSalesForDay(outletId, serviceDate, serviceType, section, false);
			
			BigDecimal temp = (reportItems.getTotal().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("total", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getCashPayment().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("cash", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getFoodBill().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("foodSale", temp.divide(new BigDecimal("100")));
			temp = (reportItems.getBarBill().multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP);
			outObj.put("barSale", temp.divide(new BigDecimal("100")));
			outObj.put("card", reportItems.getCardPayment());
			outObj.put("app", reportItems.getAppPayment());
			outObj.put("wallet", reportItems.getWalletPayment());
			outObj.put("credit", reportItems.getCreditAmount());
			
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

		INotification dao = new NotificationManager(false);
		ITable tableDao = new TableManager(false);
				
		try {
			outObj.put("status", false);
			Notification notif = dao.getNextNotification(hotelId, userId);
			if (notif != null) {
				outObj.put("status", true);
				JSONArray tableArr = new JSONArray();
				ArrayList<Table> tables = tableDao.getJoinedTables(notif.getHotelId(), notif.getOrderId());
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
	public String getDesignation(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		
		IDesignation dao = new DesignationManager(false);
		ArrayList<Designation> designations = dao.getDesignations(hotelId);
		try {
			outObj.put("designations", designations);
			
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
	@Path("/v1/getReferencesForReview")
	@Produces(MediaType.APPLICATION_JSON)
	public String getReferencesForReview() {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		try {
			outObj.put("status", false);
			for (ReferencesForReveiw des : ReferencesForReveiw.values()) {
				JSONObject obj = new JSONObject();
				obj.put("reference", des);
				outArr.put(obj);
			}
			outObj.put("references", outArr);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getOnlineOrderingPortals")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOnlineOrderingPortals(@QueryParam("hotelId") String hotelId) {
		JSONObject outObj = new JSONObject();
		JSONArray outArr = new JSONArray();
		
		IOnlineOrderingPortal dao = new OnlineOrderingPortalManager(false);
		try {
			outObj.put("status", false);
			ArrayList<OnlineOrderingPortal> portals = dao.getOnlineOrderingPortals(hotelId);
			for (OnlineOrderingPortal portal : portals) {
				JSONObject obj = new JSONObject();
				obj.put("portalName", portal.getName());
				obj.put("portal", portal.getPortal());
				obj.put("portalKey", portal.getId());
				obj.put("requiresLogistics", portal.getRequiresLogistics());
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

	@POST
	@Path("/v1/addPayment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addPayment(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IPayment dao = new PaymentManager(false);
		IOutlet outletDao = new OutletManager(false);
		IOrder orderDao = new OrderManager(false);
		IOrderItem orderItemsDao = new OrderManager(false);
		ICustomer custDao = new CustomerManager(false);
		ILoyalty loyaltyDao = new LoyaltyManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			Settings settings = outletDao.getSettings(outletId);
			Outlet outlet = outletDao.getOutlet(outletId);
			boolean status = false;
			double loyaltyPaid = inObj.getDouble("loyalty");
			String paymentType = inObj.getString("cardType");
			Order order = orderDao.getOrderById(outletId, orderId);
			
			for(int i=0; i<5; i++) {
				status = dao.addPayment(outletId, orderId, new BigDecimal(Double.toString(inObj.getDouble("foodBill"))),
						new BigDecimal(Double.toString(inObj.getDouble("barBill"))), 
						new BigDecimal(Double.toString(inObj.getDouble("foodDiscount"))), 
						new BigDecimal(Double.toString(inObj.getDouble("barDiscount"))), 
						new BigDecimal(Double.toString(loyaltyPaid)), 
						new BigDecimal(Double.toString(inObj.getDouble("total"))),
						new BigDecimal(Double.toString(inObj.has("serviceCharge")?inObj.getDouble("serviceCharge"):0.0)), 
						new BigDecimal(Double.toString(inObj.has("packagingCharge")?inObj.getDouble("packagingCharge"):0.0)), 
						new BigDecimal(Double.toString(inObj.has("deliveryCharge")?inObj.getDouble("deliveryCharge"):0.0)), 
						new BigDecimal(Double.toString(inObj.getDouble("gst"))), 
						new BigDecimal(Double.toString(inObj.getDouble("vatBar"))), 
						new BigDecimal(Double.toString(inObj.getDouble("tip"))),
						new BigDecimal(Double.toString(inObj.getDouble("cashPayment"))), 
						new BigDecimal(Double.toString(inObj.getDouble("cardPayment"))), 
						new BigDecimal(Double.toString(inObj.getDouble("appPayment"))),
						new BigDecimal(Double.toString(inObj.getDouble("walletPayment"))),
						new BigDecimal(Double.toString(inObj.getDouble("creditAmount"))),
						new BigDecimal(Double.toString(inObj.getDouble("promotionalCash"))),
						paymentType, new BigDecimal(Double.toString(inObj.getDouble("complimentary"))), 
						inObj.getString("section"));
				if (status){
					break;
				}else {
					if(i==5) {
						outObj.put("message", "Payment could not be added. Please try again. If problem persists contact OrderOn support.");
						return outObj.toString();
					}else {
						System.out.println("Retrying to add payment. OrderId: "+orderId+". Count:"+i);
						dao.deletePayment(outletId, orderId);
					}
				}
			}
			if(inObj.getString("cardType").equals("NON CHARGEABLE")) {
				dao.makeOrderNonChargeable(outletId, orderId, inObj.getString("guest"));
			}
			
			if (settings.getHotelType().equals("PREPAID") && settings.getHasKds()) {
				if (!orderDao.changeOrderStatusToService(outletId, orderId)) {
					outObj.put("message",
							"Order Status could not be updated. Please try again. If problem persists contact OrderOn support.");
					dao.deletePayment(outletId, orderId);
					return outObj.toString();
				}
			} else {
				if (!orderDao.markPaymentComplete(outletId, orderId)) {
					outObj.put("message", "Failed to mark order complete. Please try again. If problem persists contact support.");
					dao.deletePayment(outletId, orderId);
					return outObj.toString();
				}
			}
			if(inObj.getDouble("cashPayment")>0.0 || inObj.getDouble("cardPayment")>0.0) 
				if (settings.getHasCashDrawer())
					cashdrawerOpen(outletId);
			
			String mobileNo = orderDao.getMobileNoFromOrderId(outletId, orderId).getEntity();
			//Loyalty
			Customer customer = null;
			
			if (!mobileNo.equals("")) {
				customer = custDao.getCustomerDetails(outletId, mobileNo);
				custDao.incrementVisitCount(outletId, customer);
			}
				
			if (settings.getHasLoyalty() == 1 && customer != null && inObj.getDouble("creditAmount")==0) {
				int points = inObj.getInt("total");
				Double pointEarned = inObj.getDouble("cashPayment") + inObj.getDouble("cardPayment") +
										inObj.getDouble("appPayment")+inObj.getDouble("walletPayment");
				
				if (!loyaltyDao.addLoyaltyPoints(outletId, orderId, pointEarned.intValue(), customer)) {
					outObj.put("message", "Loyalty Points could not be added. Please try again. If problem persists contact OrderOn support.");
					return outObj.toString();
				}else {
					int balance = customer.getPoints()+points;
					if(settings.getHasSms() && netIsAvailable() && !settings.getIsWalletOnline() && customer.getSendSMS()) {
						SendSMS sms = new SendSMS();
						String scheme = "pts";
						String scheme2 = "";
						if(outletId.equals("ka0001")) {
							scheme = scheme2 = "Kbeans";
						}
						String[] name = customer.getFirstName().split(" ");
						String message = "";
						if(loyaltyPaid>0) {
							message = "Hi "+ name[0]+", Greetings from "+outlet.getName()
									+". You have used " + loyaltyPaid+ " "+scheme+", earned " + points+ " "+scheme+". "
									+ "Balance: "+balance+" "+scheme2+". Visit again to Redeem exciting offers.";
						}else {
							message = "Hi "+ name[0]+", Greetings from "+outlet.getName()
									+", Thank you for visiting us. You have earned " + inObj.getInt("total")+ " "+scheme+". "
									+ "Balance: "+balance+" "+scheme2+".";
						}
						
						if(mobileNo.length() > 10) {
							mobileNo = mobileNo.substring(0, 10);
						}
						System.out.println(message);
						String output = "";
						if(mobileNo.length()==10)
							output = sms.sendSms(message, mobileNo);
						System.out.println(output);
						orderDao.updateOrderSMSStatusDone(outletId, orderId);
					}
				}
			}else if (!mobileNo.equals("") && customer != null && inObj.getDouble("creditAmount")==0) {
				if(settings.getHasSms() && netIsAvailable() && !settings.getIsWalletOnline() && customer.getSendSMS()) {
					SendSMS sms = new SendSMS();
					String message = "";
					
					if(outletId.equals("wc0001"))
						message = "Hi "+customer.getFullName()+"! Thank you for dining at WestCoast Diner. We look forward to serving you soon again";
					else 
						message = "Hi "+customer.getFullName()+"! Greetings from " + outlet.getName() 
								+ ". Thank you for dining with us. Your bill at this visit was Rs " + inObj.getInt("total") + ". Have a nice day.";
					
					if(mobileNo.length() > 10) {
						mobileNo = mobileNo.substring(0, 10);
					}
					System.out.println(message);
					String output = "";
					if(mobileNo.length()==10)
						output = sms.sendSms(message, mobileNo);
					System.out.println(output);
					orderDao.updateOrderSMSStatusDone(outletId, orderId);
				}
			}
			int totalFoodDiscountValue = 0;
			int totalBarDiscountValue = 0;
			String discountCodes = "";
			order = orderDao.getOrderById(outletId, orderId);
			if (order.getDiscountCode().length()>0) {
				for(int x=0; x<order.getDiscountCode().length(); x++) {
					IDiscount discountDao = new DiscountManager(false);
					Discount discount = discountDao.getDiscountByName(outletId, order.getDiscountCode().getString(x));
					if (!discount.getUsageLimit().equals("Unlimited")) {
						final int usageLimit = Integer.parseInt(discount.getUsageLimit()) - 1;
						if(!discountDao.updateUsageLimit(outletId, discount.getName(), usageLimit)){
							outObj.put("message", "Discount usage limit could not be updated. Please contact OrderOn support.");
							return outObj.toString();
						}
					}
					totalFoodDiscountValue += discount.getFoodValue();
					totalBarDiscountValue += discount.getBarValue();
					discountCodes += discount.getName() + ", ";
				}
			}
			if(order.getInHouse() == AccessManager.HOME_DELIVERY) 
				orderDao.updateDeliveryTime(outletId, orderId);
			else
				orderDao.updateCompleteTime(outletId, orderId);
			
			if(inObj.getDouble("walletPayment")>0 || inObj.getDouble("promotionalCash")>0) {
				orderDao.updateWalletTransactionId(outletId, orderId, inObj.getInt("walletTransactionId"));
			}
			
			outObj.put("status", true);
			Report payment = dao.getPayment(outletId, orderId);
			if(inObj.getString("cardType").equals("NON CHARGEABLE")) {
				Double total = (double) Math.round((payment.getFoodBill().doubleValue() + payment.getBarBill().doubleValue())*100)/100;
				String subject = "Non-chargeable Order placed for "+order.getReference()+". Bill No. " + order.getBillNo();
				String emailText = "<div style='width:350px; ' class='alert alert-warning'><h3>A Non-Chargeable Order has been placed"
						+ ".</h3><p> Details as follows:</p>"
						+ "<div>Outlet Name: " + outlet.getName()
						+ "</div><div>Bill No: " + order.getBillNo()
						+ "</div><div>Ordered For : " + order.getReference()
						+ "</div><div>Ordered By: " + order.getWaiterId()
						+ "</div><div>Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
						+ "</div><div>Total bill amount: <b>" + total+ "</b></div>"
						+ "<div>Items Ordered are as follows: </div>"
						+ "<div><table style='color:#797979;'><thead>"
						+ "<tr><th style='text-align:left;'>PARTICULAR</th><th>QTY</th><th style='text-align:right;'>RATE</th></tr>"
						+ "</thead><tbody>";
				
				ArrayList<OrderItem> orderedItems = orderItemsDao.getOrderedItemForBill(outletId, orderId, false);

				for (OrderItem orderItem : orderedItems) {
					emailText += "<tr><td>" + orderItem.getTitle() + "</td><td>" + orderItem.getQty() + "</td><td style='text-align:right;'>" + orderItem.getRate() + "</td></tr>";
				}

				emailText += "<tr><th colspan='3' style='border: 1px solid #ffa300;'></th></tr>"
						+ "<tr><th colspan='2' style='text-align:left;'>Total</th><th>"+total+"</th></tr>"
						+ "</tbody></table></div></div>";
				
				String smsText = "Non Chargeable Order Placed. BillNo: "+order.getBillNo()
						+", Placed For: "+ order.getReference()
						+ ", User: "+ order.getWaiterId()
						+ ", Total Bill Amt: "+total+".";
				this.SendEmailAndSMS(outletId, subject, emailText, smsText, "", AccessManager.DESIGNATION_OWNER, true, false);
			}
			
			if(totalFoodDiscountValue >=30 || totalBarDiscountValue >=30) {
				discountCodes.substring(0, discountCodes.length()-3);
				String subject = "Discount Code/s "+discountCodes+" used. Bill No. " + order.getBillNo();
				String emailText = "<div style='width:350px; ' class='alert alert-warning'><h3>Discount Codes "+discountCodes+" used."
						+ ".</h3><p> Details as follows:</p>"
						+ "<div>OutletManager Name: " + outlet.getName()
						+ "<div>Bill No: " + order.getBillNo()
						+ "</div><div>Ordered For : " + order.getCustomerName()
						+ "</div><div>Ordered By: " + order.getWaiterId()
						+ "</div><div>Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
				if(totalFoodDiscountValue>0) {
					emailText = "</div><div>Total Food Discount Value: " + totalFoodDiscountValue
							+	"</div><div>Total Food Discount Amount: " + inObj.getDouble("foodDiscount");
				}
				if(totalBarDiscountValue>0) {
					emailText += "</div><div>Total Bar Discount Value: " + totalBarDiscountValue
							+	"</div><div>Total Bar Discount Amount: " + inObj.getDouble("barDiscount");
				}
				emailText += "</div><div>Total bill amount: " + inObj.getDouble("total") + "</div></div>";
				this.SendEmail(outletId, subject, emailText, "", false, true);
			}
				
		} catch (Exception e) {
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
		IPayment dao = new PaymentManager(false);
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
		IPayment dao = new PaymentManager(false);
		IOrder orderDao = new OrderManager(false);
		IOrderItem orderItemsDao = new OrderManager(false);
		IOutlet outletDao = new OutletManager(false);
		ICustomer custDao = new CustomerManager(false);
		ILoyalty loyaltyDao = new LoyaltyManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			Outlet outlet = outletDao.getOutlet(outletId);

			Report payment = dao.getPayment(outletId, orderId);
			boolean status = dao.editPayment(outletId, orderId, 
					new BigDecimal(Double.toString(inObj.getDouble("cashPayment"))),
					new BigDecimal(Double.toString(inObj.getDouble("cardPayment"))), 
					new BigDecimal(Double.toString(inObj.getDouble("appPayment"))), 
					new BigDecimal(Double.toString(inObj.getDouble("walletPayment"))), 
					inObj.getString("cardType"),
					new BigDecimal(Double.toString(inObj.getDouble("grandTotal"))));

			if(inObj.getDouble("walletPayment")>0) {
				orderDao.updateWalletTransactionId(outletId, orderId, inObj.getInt("walletTransactionId"));
			}
			if(inObj.getString("cardType").equals("NON CHARGEABLE")) {
				dao.makeOrderNonChargeable(outletId, orderId, inObj.getString("guest"));
			}
			if (status) {
				if(payment.getCreditAmount().compareTo(new BigDecimal("0")) ==1) {
					String mobileNo = orderDao.getMobileNoFromOrderId(outletId, orderId).getEntity();
					Settings settings = outletDao.getSettings(outletId);
					if (settings.getHasLoyalty() == 1 && !mobileNo.equals("")) {
						int points = payment.getTotal().intValue();
						Customer customer = custDao.getCustomerDetails(outletId, mobileNo);
						Double pointEarned = inObj.getDouble("cashPayment") + inObj.getDouble("cardPayment") +
						inObj.getDouble("appPayment")+inObj.getDouble("walletPayment");
						if (!loyaltyDao.addLoyaltyPoints(outletId, orderId, pointEarned.intValue(), customer)) {
							outObj.put("message", "Loyalty Points could not be added. Please try again. If problem persists contact OrderOn support.");
							dao.rollbackTransaction();
							return outObj.toString();
						}else {
							int balance = customer.getPoints()+points;
							custDao.incrementVisitCount(outletId, customer);
							if(netIsAvailable() && settings.getHasSms()) {
								SendSMS sms = new SendSMS();
								String scheme = "pts";
								String scheme2 = "";
								if(outletId.equals("ka0001")) {
									scheme = scheme2 = "Kbeans";
								}
								String[] name = customer.getFirstName().split(" ");
								String message = "";
								if(payment.getLoyaltyAmount().compareTo(new BigDecimal("0")) == 1) {
									message = "Hi "+ name[0]+", Greetings from "+outlet.getName()
											+". You have used " + payment.getLoyaltyAmount()+ " "+scheme+", earned " + points+ " "+scheme+". "
											+ "Balance: "+balance+" "+scheme2+".";
								}else {
									message = "Hi "+ name[0]+", Greetings from "+outlet.getName()
											+", Thank you for visiting us. You have earned " + points+ " "+scheme+". "
											+ "Balance: "+balance+" "+scheme2+".";
								}
								if(outletId.equals("wc0001"))
									message = "Hi "+customer.getFullName()+"! Thank you for dining at WestCoast Diner. We look forward to serving you soon again";
								if(mobileNo.length() > 10) {
									mobileNo = mobileNo.substring(0, 10);
								}
								System.out.println(message);
								String output = "";
								if(mobileNo.length()==10)
									output = sms.sendSms(message, mobileNo);
								System.out.println(output);
								orderDao.updateOrderSMSStatusDone(outletId, orderId);
							}
						}
					}
				}
			}
			Order order = orderDao.getOrderById(outletId, orderId);
			if(inObj.getString("cardType").equals("NON_CHARGEABLE")) {
				Double total = (double) Math.round((payment.getFoodBill().doubleValue() + payment.getBarBill().doubleValue())*100)/100;
				String subject = "Non-chargeable Order placed for "+order.getReference()+". Bill No. " + order.getBillNo();
				String emailText = "<div style='width:350px; ' class='alert alert-warning'><h3>A Non-Chargeable Order has been placed"
						+ ".</h3><p> Details as follows:</p>"
						+ "<div>Outlet Name: " + outlet.getName()
						+ "<div>Bill No: " + order.getBillNo()
						+ "</div><div>Ordered For : " + order.getReference()
						+ "</div><div>Ordered By: " + order.getWaiterId()
						+ "</div><div>Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
						+ "</div><div>Total bill amount: " + total+ "</div>"
						+ "<div>Items Ordered are as follows: </div>"
						+ "<div><ol>";
				
				ArrayList<OrderItem> orderedItems = orderItemsDao.getOrderedItemForBill(outletId, orderId, false);
				
				for (OrderItem orderItem : orderedItems) {
					emailText += "<li>" + orderItem.getTitle() + " : Qty = " + orderItem.getQty() + " : Rate = " + orderItem.getRate() + "</li>";
				}
				
				emailText += "</ol></div>"
						+ "</div>";
				String smsText = "Non Chargeable Order Placed. BillNo: "+order.getBillNo()
						+", Placed For: "+ order.getReference()
						+ ", User: "+ order.getWaiterId()
						+ ", Total Bill Amt: "+total+".";
				this.SendEmailAndSMS(outletId, subject, emailText, smsText, "", AccessManager.DESIGNATION_OWNER, true, false);
			}

			outObj.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	//This service is to rectify order and payment in case something goes wrong. Only for debug
	@POST
	@Path("/v1/rectifyOrder")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String rectifyOrder(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		PaymentManager dao = new PaymentManager(false);
		OrderManager orderDao = new OrderManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			Report payment = dao.getPayment(outletId, orderId);
			BigDecimal rectifiedTotal = new BigDecimal(inObj.getDouble("rectifiedTotal")).setScale(2, BigDecimal.ROUND_HALF_UP);

			if(rectifiedTotal.compareTo(payment.getTotal())==0) {
				outObj.put("message", "Both totals are the same");
				return outObj.toString();
			}
			
			double divisor = 0.95238095;
			BigDecimal totalAfterDiscount = rectifiedTotal.multiply(new BigDecimal(divisor)).round(new MathContext(6)).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal rectifiedGST = rectifiedTotal.subtract(totalAfterDiscount);
			BigDecimal discountAmount = payment.getFoodBill().add(payment.getBarBill()).subtract(totalAfterDiscount);
			String discountName = "["+ inObj.getString("discountName") + "]";
			
			orderDao.updateZoamtoVoucherInOrder(outletId, orderId, discountName, discountAmount);
			dao.rectifyZamatoPayment(outletId, orderId, rectifiedTotal, discountAmount, rectifiedGST, rectifiedTotal, discountName);

			outObj.put("status", true);
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
			@QueryParam("endDate") String endDate, @QueryParam("filter") String filter) {
		JSONObject outObj = new JSONObject();
		IReport dao = new ReportManager(false);
		ArrayList<Expense> reportItems = dao.getExpenseReport(hotelId, startDate, endDate, filter);
		try {
			outObj.put("report", reportItems);
			outObj.put("name", "EXPENSE REPORT");
		} catch (JSONException e) {
		}
		return outObj.toString();
	}

	// Reports
	@GET
	@Path("/v1/getPayInReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPayInReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		JSONObject outObj = new JSONObject();
		IReport dao = new ReportManager(false);
		ArrayList<Expense> reportItems = dao.getPayInReport(hotelId, startDate, endDate);
		try {
			outObj.put("report", reportItems);
			outObj.put("name", "PAYIN REPORT");
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		IReport dao = new ReportManager(false);
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		IReport dao = new ReportManager(false);
		ArrayList<CustomerReport> reportItems = dao.getCustomerReviewReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("customerName", reportItems.get(i).getFullName());
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		IReport dao = new ReportManager(false);
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IUser userDao = new UserManager(false);
		PaymentWiseSalesReport reportItems = null;
		boolean visible = false;
		
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
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
						(reportItems.getCover()==0?reportItems.getFoodBill().doubleValue():Math.round(reportItems.getFoodBill().doubleValue()/reportItems.getCover()*100)/100));
				itemDetails.put("barPCover",
						(reportItems.getCover()==0?reportItems.getFoodBill().doubleValue():Math.round(reportItems.getFoodBill().doubleValue()/reportItems.getCover()*100)/100));
				itemDetails.put("cash", reportItems.getCash());
				itemDetails.put("card", reportItems.getCard());
				itemDetails.put("app", reportItems.getApp());
				itemDetails.put("wallet", reportItems.getWallet());
				itemDetails.put("promotionalCash", reportItems.getPromotionalCash());
				itemDetails.put("credit", reportItems.getCredit());
				itemDetails.put("VISA", reportItems.getVISA());
				itemDetails.put("MASTERCARD", reportItems.getMASTERCARD());
				itemDetails.put("MAESTRO", reportItems.getMAESTRO());
				itemDetails.put("AMEX", reportItems.getAMEX());
				itemDetails.put("RUPAY", reportItems.getRUPAY());
				itemDetails.put("MSWIPE", reportItems.getMSWIPE());
				itemDetails.put("OTHERS", reportItems.getOTHERS());
				itemDetails.put("ZOMATO", reportItems.getZOMATO());
				itemDetails.put("ZOMATOPAY", reportItems.getZOMATO_PAY());
				itemDetails.put("ZOMATOPICKUP", reportItems.getZOMATO_PICKUP());
				itemDetails.put("SWIGGY", reportItems.getSWIGGY());
				itemDetails.put("PAYTM", reportItems.getPAYTM());
				itemDetails.put("DINEOUT", reportItems.getDINE_OUT());
				itemDetails.put("FOODPANDA", reportItems.getFOOD_PANDA());
				itemDetails.put("UBEREATS", reportItems.getUBER_EATS());
				itemDetails.put("FOODILOO", reportItems.getFOODILOO());
				itemDetails.put("NEARBY", reportItems.getNEARBY());
				itemDetails.put("SWIGGYPOP", reportItems.getSWIGGYPOP());
				itemDetails.put("GOOGLEPAY", reportItems.getGOOGLEPAY());
				itemDetails.put("MAGICPIN", reportItems.getMAGICPIN());
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
	@Path("/v1/getOnlineOrderingSalesReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOnlineOrderingSalesReport(@QueryParam("hotelId") String hotelId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		ArrayList<OnlineOrderingSalesReport> reportItems = dao.getOnlineOrderingSalesReport(hotelId, startDate, endDate);
		
		try {
			for(int i=0; i<reportItems.size(); i++) {
				itemDetails = new JSONObject();
				
				itemDetails.put("name", reportItems.get(i).getPortalName());
				itemDetails.put("portalId", reportItems.get(i).getPortalId());
				itemDetails.put("total", reportItems.get(i).getTotalBillAmount());
				itemDetails.put("netAmount", reportItems.get(i).getNetAmount());
				itemDetails.put("gst", reportItems.get(i).getGst());
				itemDetails.put("discount", reportItems.get(i).getDiscount());
				itemDetails.put("packagingCharge", reportItems.get(i).getPackagingCharge());
				itemDetails.put("serviceCharge", reportItems.get(i).getServiceCharge());
				itemDetails.put("deliveryCharge", reportItems.get(i).getDeliveryCharge());
				itemDetails.put("commissionType", reportItems.get(i).getCommisionType());
				itemDetails.put("commissionValue", reportItems.get(i).getCommisionValue());
				itemDetails.put("commissionAmount", reportItems.get(i).getCommisionAmount());
				itemDetails.put("tds", reportItems.get(i).getTds());
				itemsArr.put(itemDetails);
			}
			outObj.put("report", itemsArr);
			outObj.put("name", "ONLINE ORDERING SALES REPORT");
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
		JSONObject outObj = new JSONObject();
		JSONArray arr = new JSONArray();
		JSONArray arr2 = new JSONArray();
		JSONObject arrObj;
		
		IReport dao = new ReportManager(false);
		IService serviceDao = new ServiceManager(false);
		IOutlet outletDao = new OutletManager(false);
		Report reportItems;
		ArrayList<Expense> expenses = dao.getCashExpenses(hotelId, serviceDate, serviceType, section);
		cashdrawerOpen(hotelId);

		try {
			BigDecimal barComplimentary = dao.getBarComplimentary(hotelId, serviceDate, serviceType);
			reportItems = dao.getTotalSalesForService(hotelId, serviceDate, serviceType, section, true);
			outObj.put("foodBill", reportItems.getFoodBill());
			outObj.put("barBill", reportItems.getBarBill());
			outObj.put("foodGross", reportItems.getFoodGross().subtract(barComplimentary));
			outObj.put("barGross", reportItems.getBarGross().add(barComplimentary));
			outObj.put("foodSale", reportItems.getFoodSale());
			outObj.put("barSale", reportItems.getBarSale());
			outObj.put("grossSale", reportItems.getGrossSale());
			outObj.put("inhouseSales", reportItems.getInhouseSales());
			outObj.put("hdSales", reportItems.getHomeDeliverySales());
			outObj.put("taSales", reportItems.getTakeAwaySales());
			outObj.put("zomatoSale", reportItems.getZomatoSale());
			outObj.put("swiggySale", reportItems.getSwiggySale());
			outObj.put("uberEatsSale", reportItems.getUberEatsSale());
			outObj.put("foodPandaSale", reportItems.getFoodPandaSale());
			outObj.put("roundOff", reportItems.getRoundOff());
			outObj.put("orderCount", reportItems.getOrderCount());
			outObj.put("printCount", reportItems.getPrintCount());
			outObj.put("reprints", reportItems.getReprints());
			outObj.put("complimentary", reportItems.getComplimentary());
			outObj.put("foodComplimentary", reportItems.getComplimentary().subtract(barComplimentary));
			outObj.put("barComplimentary", barComplimentary);
			outObj.put("loyalty", reportItems.getLoyaltyAmount());
			outObj.put("nc", reportItems.getNC());
			outObj.put("total", reportItems.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("netSale", reportItems.getNetSale().setScale(2, BigDecimal.ROUND_HALF_UP));
			BigDecimal zomatoCash = dao.getCardPaymentByType(hotelId, serviceDate, serviceType, "ZOMATO_CASH");
			outObj.put("zomatoCash", zomatoCash);
			BigDecimal swiggyCash = dao.getCardPaymentByType(hotelId, serviceDate, serviceType, "SWIGGY_CASH");
			outObj.put("swiggyCash", swiggyCash);
			outObj.put("cash", reportItems.getCashPayment().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("totalCash", (reportItems.getCashPayment().add(swiggyCash).add(zomatoCash)).setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("card", reportItems.getCardPayment());
			outObj.put("app", reportItems.getAppPayment());
			outObj.put("wallet", reportItems.getWalletPayment());
			outObj.put("promotionalCash", reportItems.getPromotionalCash());
			outObj.put("credit", reportItems.getCreditAmount());
			outObj.put("foodDiscount", reportItems.getFoodDiscount().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("barDiscount", reportItems.getBarDiscount().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("totalTax", reportItems.getGst().add(reportItems.getVatBar()).setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("gst", reportItems.getGst().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("vatBar", reportItems.getVatBar().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("serviceCharge", reportItems.getServiceCharge().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("packagingCharge", reportItems.getPackagingCharge().setScale(2, BigDecimal.ROUND_HALF_UP));
			outObj.put("deliveryCharge", reportItems.getDeliveryCharge().setScale(2, BigDecimal.ROUND_HALF_UP));
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
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.ZOMATO.toString()));
			outObj.put(OnlinePaymentType.ZOMATO.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.ZOMATO.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.SWIGGY.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.SWIGGY.toString()));
			outObj.put(OnlinePaymentType.SWIGGY.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.SWIGGY.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.DINEOUT.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.DINEOUT.toString()));
			outObj.put(OnlinePaymentType.DINEOUT.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.DINEOUT.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.PAYTM.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.PAYTM.toString()));
			outObj.put(OnlinePaymentType.PAYTM.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.PAYTM.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.FOODPANDA.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.FOODPANDA.toString()));
			outObj.put(OnlinePaymentType.FOODPANDA.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.FOODPANDA.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.UBEREATS.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.UBEREATS.toString()));
			outObj.put(OnlinePaymentType.UBEREATS.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.UBEREATS.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.FOODILOO.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.FOODILOO.toString()));
			outObj.put(OnlinePaymentType.FOODILOO.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.FOODILOO.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.ZOMATOPAY.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.ZOMATOPAY.toString()));
			outObj.put(OnlinePaymentType.ZOMATOPAY.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.ZOMATOPAY.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.NEARBY.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.NEARBY.toString()));
			outObj.put(OnlinePaymentType.NEARBY.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.NEARBY.toString()));
			arr.put(arrObj);
			outObj.put("apps", arr);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.SWIGGYPOP.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.SWIGGYPOP.toString()));
			outObj.put(OnlinePaymentType.SWIGGYPOP.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.SWIGGYPOP.toString()));
			arr.put(arrObj);
			outObj.put("apps", arr);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.GOOGLEPAY.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.GOOGLEPAY.toString()));
			outObj.put(OnlinePaymentType.GOOGLEPAY.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.GOOGLEPAY.toString()));
			arr.put(arrObj);
			outObj.put("apps", arr);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.MAGICPIN.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.MAGICPIN.toString()));
			outObj.put(OnlinePaymentType.MAGICPIN.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.MAGICPIN.toString()));
			arr.put(arrObj);
			arrObj = new JSONObject();
			arrObj.put("name", OnlinePaymentType.ZOMATO_PICKUP.toString());
			arrObj.put("amount",
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.ZOMATO_PICKUP.toString()));
			outObj.put(OnlinePaymentType.ZOMATO_PICKUP.toString(),
					dao.getAppPaymentByType(hotelId, serviceDate, null, serviceType, OnlinePaymentType.ZOMATO_PICKUP.toString()));
			arr.put(arrObj);
			outObj.put("apps", arr);

			outObj.put("voidTrans", dao.getVoidTransactions(hotelId, serviceDate, serviceType));
			//outObj.put("cashBalance", bankDao.getCashBalance(hotelId, section));
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
			outObj.put("hasEod", outletDao.getSettings(hotelId).getHasEod());
			ServiceLog service = serviceDao.getServiceLog(hotelId, serviceDate, serviceType);
			outObj.put("serviceLog", new JSONObject(service));
			outObj.put("cashInHand", service.getCashInHand());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getSalesReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSaleSummaryReport(@QueryParam("hotelId") String hotelId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate, @QueryParam("userId") String userId, 
			@QueryParam("orderType") int orderType,  @QueryParam("portalId") int portalId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IUser userDao = new UserManager(false);
		IOutlet outletDao = new OutletManager(false);
		IOnlineOrderingPortal portalDao = new OnlineOrderingPortalManager(false);
		IOrder orderDao = new OrderManager(false);
		Settings settings  = outletDao.getSettings(hotelId);
		boolean visible = false;
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		String takeAwayType = "";
		String transactionType = "";
		ArrayList<Report> reportItems = dao.getSaleSummaryReport(hotelId, startDate, endDate, orderType, portalId, visible);
		OnlineOrderingPortal zomato = portalDao.getOnlineOrderingPortalByPortal(hotelId, OnlineOrderingPortals.ZOMATO);
		try {
			String section= settings.getSections()[0];
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				if(reportItems.get(i).getState() == AccessManager.ORDER_STATE_VOIDED) {
					itemDetails.put("foodBill", 0);
					itemDetails.put("barBill", 0);
					itemDetails.put("totalBill", 0);
				}else if(reportItems.get(i).getState() == AccessManager.ORDER_STATE_COMPLIMENTARY) {
					itemDetails.put("foodBill", 0);
					itemDetails.put("barBill", 0);
					itemDetails.put("totalBill", 0);
				}else {
					itemDetails.put("foodBill", reportItems.get(i).getFoodBill());
					itemDetails.put("barBill", reportItems.get(i).getBarBill());
					itemDetails.put("totalBill", reportItems.get(i).getTotalBill());
				}
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("foodDiscount", reportItems.get(i).getFoodDiscount());
				itemDetails.put("barDiscount", reportItems.get(i).getBarDiscount());
				itemDetails.put("totalDiscount", reportItems.get(i).getTotalDiscount());
				itemDetails.put("sc", reportItems.get(i).getServiceCharge());
				itemDetails.put("tip", reportItems.get(i).getTip());
				itemDetails.put("covers", reportItems.get(i).getCovers());
				itemDetails.put("customerName", reportItems.get(i).getCustomerName());
				itemDetails.put("remarks", reportItems.get(i).getRemarks().replaceAll(",", "|"));
				takeAwayType = portalDao.getOnlineOrderingPortalById(hotelId, reportItems.get(i).getTakeAwayType()).getName();
				itemDetails.put("inhouse", reportItems.get(i).getInhouse());
				itemDetails.put("takeAwayType", takeAwayType);
				itemDetails.put("card", reportItems.get(i).getCardPayment());
				itemDetails.put("cash", reportItems.get(i).getCashPayment());
				itemDetails.put("app", reportItems.get(i).getAppPayment());
				itemDetails.put("wallet", reportItems.get(i).getWalletPayment());
				itemDetails.put("promotionalCash", reportItems.get(i).getPromotionalCash());
				itemDetails.put("loyalty", reportItems.get(i).getLoyaltyAmount());
				itemDetails.put("credit", reportItems.get(i).getCreditAmount());
				if(takeAwayType.equals(zomato.getName()) && reportItems.get(i).getCardType().equals(PaymentType.CASH.toString())) {
					transactionType = "ZOMATO CASH";
				}else {
					transactionType = reportItems.get(i).getCardType();
				}
				itemDetails.put("trType", transactionType);
				itemDetails.put("total", reportItems.get(i).getTotal());
				itemDetails.put("orderType", reportItems.get(i).getInhouse()==AccessManager.TAKE_AWAY?takeAwayType:orderDao.getOrderType(reportItems.get(i).getInhouse()));
				itemDetails.put("tableId", reportItems.get(i).getTableId());
				itemDetails.put("orderDate", reportItems.get(i).getOrderDate());
				BigDecimal gst = reportItems.get(i).getGst();
				itemDetails.put("cgst", gst.divide(new BigDecimal("2")));
				itemDetails.put("sgst", gst.divide(new BigDecimal("2")));
				itemDetails.put("gst", gst);
				itemDetails.put("vatBar", reportItems.get(i).getVatBar());
				itemDetails.put("roundOff", reportItems.get(i).getRoundOff());
				itemDetails.put("reference", reportItems.get(i).getReference());
				itemDetails.put("externalOrderId", reportItems.get(i).getExternalOrderId());
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

	//o59zyLh8nMV34TlYTxMdjg==
	@GET
	@Path("/v1/ADSR/getSalesReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSaleSummaryReportForADSR(@HeaderParam("x-OrderOn-Api-Key") String authString, 
			@QueryParam("outletId") String outletId, @QueryParam("startDate") String startDate,
			@QueryParam("endDate") String endDate) {
		
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IUserAuthentication userDao = new UserManager(false);
		
		try {
			if(outletId == null || outletId.isEmpty()) {
				outObj.put("message", "Outlet Id not found.");
				outObj.put("code", 400);
				return outObj.toString();
			}else if(startDate == null || startDate.isEmpty()) {
				outObj.put("message", "Start Date not found.");
				outObj.put("code", 400);
				return outObj.toString();
			}else if(authString == null || authString.isEmpty()) {
				outObj.put("message", "API Key not found.");
				outObj.put("code", 400);
				return outObj.toString();
			}else if(authString.length() != 24) {
				outObj.put("message", "Invalid API Key.");
				outObj.put("code", 400);
				return outObj.toString();
			}
			endDate = endDate==null?"":endDate;
			
			String decryptedString = EncryptDecryptString.decrypt(authString);
			String[] auth = decryptedString.split(":");
			User user = userDao.validateUser(outletId, auth[0], auth[1]);
			if (user == null) {
				outObj.put("message", "UNAUTHORIZED.");
				outObj.put("code", 403);
				return outObj.toString();
			}
			if(auth[0].equals("adsr") && !outletId.equals("sg0003")) {
				outObj.put("message", "UNAUTHORIZED. Cannot access the specified outlet details.");
				outObj.put("code", 403);
				return outObj.toString();
			}
			
			ArrayList<Report> reportItems = dao.getSaleSummaryReport(outletId, startDate, endDate, 1000, 0, false);
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("receiptNumber", reportItems.get(i).getBillNo());
				itemDetails.put("receiptDate", reportItems.get(i).getOrderDate());
				itemDetails.put("transactionTime", reportItems.get(i).getBarDiscount());
				itemDetails.put("invoiceAmount", reportItems.get(i).getFoodBill().add(reportItems.get(i).getBarBill()));
				itemDetails.put("discountAmount", reportItems.get(i).getFoodDiscount().add(reportItems.get(i).getBarDiscount()));
				itemDetails.put("vatAmount", reportItems.get(i).getVatBar());
				itemDetails.put("serviceTaxAmount", 0.0);
				itemDetails.put("serviceChargeAmount", reportItems.get(i).getServiceCharge());
				itemDetails.put("netSale", reportItems.get(i).getTotal());
				itemDetails.put("paymentMode", reportItems.get(i).getCardType());
				itemDetails.put("card", reportItems.get(i).getCardPayment());
				itemDetails.put("cash", reportItems.get(i).getCashPayment());
				itemDetails.put("app", reportItems.get(i).getAppPayment());
				itemDetails.put("wallet", reportItems.get(i).getWalletPayment());
				itemDetails.put("promotionalCash", reportItems.get(i).getPromotionalCash());
				itemDetails.put("credit", reportItems.get(i).getCreditAmount());
				itemDetails.put("totalBill", reportItems.get(i).getFoodBill().add(reportItems.get(i).getBarBill()));
				itemDetails.put("gst", reportItems.get(i).getGst());
				itemDetails.put("transactionStatus", "SALE");
				itemsArr.put(itemDetails);
			}
			outObj.put("records", itemsArr);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@GET
	@Path("/v1/YAYMEK/getOutletDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMenuForYaymek(@HeaderParam("x-OrderOn-Api-Key") String authString, 
			@QueryParam("outletId") String outletId) {
		
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();

		try {
			outObj.put("status", false);
			
			if(outletId == null || outletId.isEmpty()) {
				outObj.put("message", "Outlet Id not found.");
				outObj.put("code", 400);
				return outObj.toString();
			}
			
			IUserAuthentication userDao = new UserManager(false);
			
			String decryptedString = EncryptDecryptString.decrypt(authString);
			String[] auth = decryptedString.split(":");
			User user = userDao.validateUser(outletId, auth[0], auth[1]);
			if (user == null) {
				outObj.put("message", "UNAUTHORIZED.");
				outObj.put("code", 403);
				return outObj.toString();
			}
			if(auth[0].equals("yaymek") && !outletId.equals("h0002")) {
				outObj.put("message", "UNAUTHORIZED. Cannot access the specified outlet details.");
				outObj.put("code", 403);
				return outObj.toString();
			}
	
			IMenuItem dao = new MenuItemManager(false);
			IGroup groupDao = new GroupManager(false);
			ISpecification specsDao = new SpecificationManager(false);
			ICharge chargeDao = new ChargeManager(false);
			ITax taxDao = new TaxManager(false);
			IOutlet outletDao = new OutletManager(false);
	
			ArrayList<MenuItem> items = dao.getMenu(outletId);
			ArrayList<Specifications> specs = specsDao.getSpecifications(outletId);
			ArrayList<Group> groups = groupDao.getGroups(outletId);
			ArrayList<Tax> taxes = taxDao.getTaxes(outletId);
			ArrayList<Charge> charges = chargeDao.getCharges(outletId);
			Outlet outlet = outletDao.getOutlet(outletId);
			
			MenuItem item = null;
			for (int i = 0; i < items.size(); i++) {
				itemDetails = new JSONObject();
				item = items.get(i);
				itemDetails.put("menuId", item.getMenuId());
				itemDetails.put("title", item.getTitle());
				itemDetails.put("description", item.getDescription());
				itemDetails.put("collection", item.getCollection());
				itemDetails.put("subCollection", item.getSubCollection());
				itemDetails.put("flags", item.getFlags());
				itemDetails.put("preparationTime", item.getPreparationTime());
				itemDetails.put("dineInRate", item.getDineInRate());
				itemDetails.put("dineInNonAcRate", item.getDineInNonAcRate());
				itemDetails.put("imageUrl", item.getImg());
				itemDetails.put("state", item.getState());
				itemDetails.put("code", item.getCode());
				itemDetails.put("taxes", item.getTaxes());
				itemDetails.put("groups", item.getGroups());
				itemDetails.put("charges", item.getCharges());
				itemDetails.put("discountType", item.getDiscountType());
				itemDetails.put("discountValue", item.getDiscountValue());
				itemsArr.put(itemDetails);
			}
			
			ITable tableDao = new TableManager(false);
			Settings settings = outletDao.getSettings(outletId);
			JSONObject tableDetails = new JSONObject();
			JSONArray tableArr = new JSONArray();
			
			ArrayList<TableUsage> tables = tableDao.getTableUsage(settings);
			try {
				for (int i = 0; i < tables.size(); i++) {
					tableDetails = new JSONObject();
					tableDetails.put("tableId", tables.get(i).getTableId());
					tableDetails.put("tableIndex", i+1);
					tableDetails.put("orderId", tables.get(i).getOrderId());
					tableDetails.put("isVacant", tables.get(i).getOrderId().isEmpty());
					tableDetails.put("section", tables.get(i).getSection());
					tableDetails.put("orderState", tables.get(i).getState());
					tableDetails.put("type", tables.get(i).getType());
					tableDetails.put("showTableView", tables.get(i).getShowTableView());
					tableArr.put(tableDetails);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			outObj.put("menuItems", itemsArr);
			outObj.put("groups", groups);
			outObj.put("specifications", specs);
			outObj.put("taxes", new JSONArray(taxes));
			outObj.put("charges", new JSONArray(charges));
			outObj.put("outlet", new JSONObject(outlet));
			outObj.put("tables", tableArr);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getStockReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getStockReport(@QueryParam("hotelId") String outletId) {
		JSONArray itemsArr = new JSONArray();
		JSONObject itemDetails = null;
		JSONObject outObj = new JSONObject();
		
		IMaterial dao = new MaterialManager(false);
		ArrayList<Material> materials = dao.getMaterials(outletId);

		try {
			for (int i = 0; i < materials.size(); i++) {

				itemDetails = new JSONObject();
				itemDetails.put("title", materials.get(i).getName());
				itemDetails.put("unit", materials.get(i).getDisplayableUnit());
				itemDetails.put("quantity", materials.get(i).getDisplayableQuantity());

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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IUser userDao = new UserManager(false);
		boolean visible = false;
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IAttendance attendanceDao = new AttendanceManager(false);
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
			outObj.put("hasSecondShift", attendanceDao.hasSecondShift(hotelId, startDate, endDate));
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IUser userDao = new UserManager(false);
		boolean visible = false;
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		ArrayList<GrossSaleReport> reportItems = dao.getGrossSalesReport(hotelId, startDate, endDate, visible);

		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("grossTotal", reportItems.get(i).getGrossTotal());
				itemDetails.put("foodBill", reportItems.get(i).getFoodBill());
				itemDetails.put("barBill", reportItems.get(i).getBarBill());
				itemDetails.put("foodDiscount", reportItems.get(i).getFoodDiscount());
				itemDetails.put("barDiscount", reportItems.get(i).getBarDiscount());
				itemDetails.put("totalDiscount", reportItems.get(i).getTotalDiscount());
				itemDetails.put("grossLoyalty", reportItems.get(i).getGrossLoyalty());
				itemDetails.put("grossComplimentary", reportItems.get(i).getGrossComplimentary());
				itemDetails.put("grossGst", reportItems.get(i).getGrossGst().divide(new BigDecimal("2")));
				itemDetails.put("grossVatBar", reportItems.get(i).getGrossVatBar());
				itemDetails.put("grossDeliveryCharge", reportItems.get(i).getGrossDeliveryCharge());
				itemDetails.put("grossPackagingCharge", reportItems.get(i).getGrossPackagingCharge());
				itemDetails.put("cardPayment", reportItems.get(i).getCardPayment());
				itemDetails.put("cashPayment", reportItems.get(i).getCashPayment());
				itemDetails.put("appPayment", reportItems.get(i).getAppPayment());
				itemDetails.put("walletPayment", reportItems.get(i).getWalletPayment());
				itemDetails.put("promotionalCash", reportItems.get(i).getPromotionalCash());
				itemDetails.put("totalPayment", reportItems.get(i).getTotalPayment());
				itemDetails.put("roundOffDifference", reportItems.get(i).getRoundOffDifference());
				itemDetails.put("creditAmount", reportItems.get(i).getCreditAmount());
				itemDetails.put("grossServiceCharge", reportItems.get(i).getGrossServiceCharge());
				itemDetails.put("netSales", reportItems.get(i).getNetSales());
				itemDetails.put("grossExpenses", reportItems.get(i).getGrossExpenses());
				itemDetails.put("grossPayins", reportItems.get(i).getGrossPayIns());
				itemDetails.put("totalSale", reportItems.get(i).getTotalSale());
				itemDetails.put("sumVoids", reportItems.get(i).getSumVoids());
				itemDetails.put("sumReturns", reportItems.get(i).getSumReturns());
				itemDetails.put("countVoids", reportItems.get(i).getCountVoids());
				itemDetails.put("countReturns", reportItems.get(i).getCountReturns());
				itemDetails.put("roundOff", reportItems.get(i).getRoundOff());
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IUser userDao = new UserManager(false);
		boolean visible = false;
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
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
				itemDetails.put("AvgAmountPerTableTurned", reportItems5.get(i).getAvgAmountPerTableTurned());
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
	    	System.out.println("Internet Unavailable.");
	        return false;
	    }
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
		JSONArray itemsArr = new JSONArray();
		JSONObject orderObj = new JSONObject();
		JSONObject itemDetails = null;

		IAccount dao = new AccountManager(false);
		ArrayList<Account> bank = dao.getBankAccounts(hotelId);
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
		IExpense expenseDao = new ExpenseManager(false);
		ITransactionHistory transDao = new TransactionHistoryManager(false);
		IOutlet outletDao = new OutletManager(false);
		IService serviceDao = new ServiceManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			String hotelId = inObj.getString("hotelId");
			String expenseType = inObj.getString("expenseType");
			Settings settings = outletDao.getSettings(hotelId);
			ServiceLog service = serviceDao.getCurrentService(hotelId);

			if (expenseDao.addExpense(hotelId, new BigDecimal(Double.toString(inObj.getDouble("expenseAmount"))).add(new BigDecimal(Double.toString(inObj.getDouble("bonus")))),
					inObj.getString("details"), inObj.getString("payeeName"), inObj.getString("invoiceNumber"), inObj.getInt("cheque"),
					inObj.getString("paymentType"), expenseType, inObj.getString("bankAccount"),
					inObj.getString("userId"), inObj.getString("employeeId"), service.getServiceDate(), service.getServiceType())) {
				outObj.put("status", true);

				if (expenseType.equals(ExpenseType.LABOUR.toString())) {
					dao.updateLabourLog(hotelId, new BigDecimal(Double.toString(inObj.getDouble("expenseAmount"))),
							inObj.getString("employeeId"), new BigDecimal(Double.toString(inObj.getDouble("bonus"))));
				}
				if (expenseType.equals(ExpenseType.FLOAT.toString())) {
					transDao.updateTransactionHistory(hotelId, "CREDIT", ExpenseType.FLOAT.toString(), 
							inObj.getString("bankAccount"), inObj.getString("paymentType"), new BigDecimal(Double.toString(inObj.getDouble("expenseAmount"))),
							inObj.getString("employeeId"), inObj.getString("userId"), "");
				}
				if (inObj.getString("bankAccount").equals("CASH_DRAWER")) {
					if (settings.getHasCashDrawer())
						cashdrawerOpen(hotelId);
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
		IExpense expenseDao = new ExpenseManager(false);
		IOutlet outletDao = new OutletManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			Settings settings = outletDao.getSettings(hotelId);

			Expense expense = expenseDao.getExpense(hotelId, inObj.getInt("expenseId"));
			if(expense.getType().equals(ExpenseType.FLOAT.toString())) {
				expenseDao.clearFloat(hotelId, inObj.getInt("expenseId"),inObj.getString("userId"), 
						inObj.getString("authoriser"), inObj.getString("section"), expense.getAccountName(),
						expense.getPaymentType(), expense.getAmount(), expense.getEmployeeId());
			}else
				expenseDao.deleteExpense(hotelId, inObj.getInt("expenseId"),inObj.getString("section"),
						expense.getPaymentType(), expense.getAmount());

			if (expense.getAccountName().equals("CASH_DRAWER")) {
				if (settings.getHasCashDrawer())
					cashdrawerOpen(hotelId);
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		IExpense dao = new ExpenseManager(false);
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		IExpense dao = new ExpenseManager(false);
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

	//-----------------------------Vendor API'S

	

	//-----------------------------End Vendor API'S
	
	@GET
	@Path("/v1/getDailyAnalysisReport")
	@Produces(MediaType.APPLICATION_JSON)
	public String getDailyAnalysisReport(@QueryParam("hotelId") String hotelId,
			@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
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
						reportItems.getGst().add(reportItems.getVatBar()));
				itemDetails.put("averagePerCover", reportItems.getCovers()==0?reportItems.getFoodBill().doubleValue():reportItems.getTotal().doubleValue()/reportItems.getCovers());
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

		IService dao = new ServiceManager(false);
		String serviceDate = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			if (dao.addService(inObj.getString("hotelId"), inObj.getString("serviceType"), serviceDate,
					inObj.getInt("cashInHand"))) {
				outObj.put("status", true);
				outObj.put("serviceDate", serviceDate);
			}else {
				outObj.put("message", "Could not start service. Please contact support.");
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
		IService serviceDao = new ServiceManager(false);
		IOutlet outletDao = new OutletManager(false);
		IAttendance attendanceDao = new AttendanceManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String hotelId = inObj.getString("hotelId");
			attendanceDao.checkOutAll(hotelId);
			if (!outletDao.getSettings(hotelId).getHasEod()) {
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
							new BigDecimal(Double.toString(report.getDouble("ZOMATO_PICKUP"))), 
							new BigDecimal(Double.toString(report.getDouble("SWIGGY"))), 
							new BigDecimal(Double.toString(report.getDouble("DINEOUT"))),
							new BigDecimal(Double.toString(report.getDouble("PAYTM"))), 
							new BigDecimal(Double.toString(report.getDouble("FOODPANDA"))), 
							new BigDecimal(Double.toString(report.getDouble("UBEREATS"))),
							new BigDecimal(Double.toString(report.getDouble("FOODILOO"))), 
							new BigDecimal(Double.toString(report.getDouble("NEARBY"))), 
							new BigDecimal(Double.toString(report.getDouble("SWIGGYPOP"))), 
							new BigDecimal(Double.toString(report.getDouble("GOOGLEPAY"))), 
							new BigDecimal(Double.toString(report.getDouble("MAGICPIN"))), 
							new BigDecimal(Double.toString(report.getDouble("complimentary"))), new BigDecimal("0.0"), "", "", inObj.has("section")?inObj.getString("section"):"")) {

						outObj.put("message", "Service cannot be ended right now. Please contact support!");
						return outObj.toString();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			//if(serviceDao.checkSevicesEnded(hotelId, inObj.getString("serviceDate"), inObj.getString("serviceType"))) {
			if (serviceDao.endService(hotelId, inObj.getString("serviceDate"), inObj.getString("serviceType"))) {
				outObj.put("status", true);
			}
			//}else {
				//outObj.put("status", true);
			//}
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
		IService serviceDao = new ServiceManager(false);
		IOutlet outletDao = new OutletManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			String serviceDate = inObj.getString("serviceDate");
			String serviceType = inObj.getString("serviceType");
			String compiledReport = inObj.getString("report");
			outObj.put("status", false);
			if(!netIsAvailable()) {
				serviceDao.updateReportStatusToNotSent(outletId, serviceDate, serviceType, compiledReport);
			}else {
				sendEmailSmsForEod(outletId, serviceDate, serviceType, compiledReport);
				Settings setting = outletDao.getSettings(outletId);
				if(setting.getIsServerEnabled()==1)
					this.updateServer(outletId);
				//TODO- Void, Cancel, Return, NC Report.
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	private boolean sendEmailSmsForEod(String hotelId, String serviceDate, String serviceType, String report) {
		String location = Configurator.getDownloadLocation();
		String date = serviceDate.replaceAll("/", "_");
		String file = location + "Sales Report for " + date + ".csv";

		IService dao = new ServiceManager(false);
		IOutlet outletDao = new OutletManager(false);
		Settings settings = outletDao.getSettings(hotelId);
		Outlet outlet = outletDao.getOutlet(hotelId);
 		ServiceLog log = dao.getServiceLog(hotelId, serviceDate);
		BigDecimal netSale = dao.getSaleForService(hotelId, serviceDate, serviceType);
		String outletName = outlet.getName();
		String outletName2 = outlet.getName().length()>25?outlet.getName().substring(0, 24):outlet.getName();
		if(!(outlet.getLocation().toString().equals("{}") || outlet.getLocation() == null)) {
			try {
				outletName += ", " + outlet.getLocation().getString("place");
				outletName2 += ", " + outlet.getLocation().getString("place");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		String[] filePaths = null;
		boolean hasSentEmail = false;
		if(settings.getDownloadReports()) {
			if(settings.getIsWalletOnline()) {
				filePaths = new String[6];
			}else {
				filePaths = new String[5];
			}
			filePaths[0] = file;
			file = location + "DepartmentWise Consumption Report for " + date + ".csv";
			filePaths[1] = file;
			file = location + "Paymentwise Sales Report for " + date + ".csv";
			filePaths[2] = file;
			file = location + "Returned Items Report for " + date + ".csv";
			filePaths[3] = file;
			file = location + "Non Chargeable Orders Report for " + date + ".csv";
			filePaths[4] = file;
			
			if(settings.getIsWalletOnline()) {
				file = location + "Wallet Transaction Report for " + date + ".csv";
				filePaths[5] = file;
			}
		}
		
		String emailSubject = "End Of Day Report for " + serviceDate + ". Outlet: " + outletName;
		
		String smsText = "Outlet: "+outletName2+
		". Service Details: Date-"+serviceDate+
		". Type-"+serviceType+
		". Started at "+ log.getStartTimeStamp().substring(11, 19) +
		". Stopped at "+ log.getEndTimeStamp().substring(11, 19)+
		". Total Sale: "+netSale+".";
		
		hasSentEmail = SendEmailWithAttachment(hotelId, emailSubject, report, "", filePaths, false, false);
		if(settings.getHasSms())
			this.SendSms(hotelId, smsText, AccessManager.DESIGNATION_OWNER, false);
		dao.updateReportStatusToSent(hotelId, serviceDate, serviceType);
		if(hasSentEmail) {
			for (String path : filePaths) {
				this.deleteFile(path);
			}
		}
		return true;
	}

	@GET
	@Path("/v1/getServiceLog")
	@Produces(MediaType.APPLICATION_JSON)
	public String getServiceLog(@QueryParam("hotelId") String hotelId, @QueryParam("serviceDate") String serviceDate) {
		JSONObject outObj = new JSONObject();
		JSONObject serviceDetails = null;
		
		IService dao = new ServiceManager(false);
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
		JSONObject outObj = new JSONObject();
		JSONObject serviceDetails = null;
		
		IService dao = new ServiceManager(false);
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
		JSONObject outObj = new JSONObject();
		JSONObject paymentDetails = null;
		
		IPayment dao = new PaymentManager(false);
		IOrderItem itemDao = new OrderManager(false);
		Report payment = dao.getPayment(hotelId, orderId);

		try {
			outObj.put("status", false);
			paymentDetails = new JSONObject();
			if (payment != null) {
				paymentDetails.put("cash", payment.getCashPayment());
				paymentDetails.put("card", payment.getCardPayment());
				paymentDetails.put("app", payment.getAppPayment());
				paymentDetails.put("wallet", payment.getWalletPayment());
				paymentDetails.put("credit", payment.getCreditAmount());
				paymentDetails.put("cardType", payment.getCardType());
				paymentDetails.put("sc", payment.getServiceCharge());
				paymentDetails.put("vatBar", payment.getVatBar());
				paymentDetails.put("gst", payment.getGst());
				paymentDetails.put("total", payment.getTotal());
				paymentDetails.put("foodBill", payment.getFoodBill());
				paymentDetails.put("barBill", payment.getBarBill());
				paymentDetails.put("taxableFoodBill", itemDao.getTaxableFoodBill(hotelId, orderId));
				paymentDetails.put("taxableBarBill", itemDao.getTaxableBarBill(hotelId, orderId));
				paymentDetails.put("foodDiscount", payment.getFoodDiscount());
				paymentDetails.put("barDiscount", payment.getBarDiscount());
				paymentDetails.put("discountName", payment.getDiscountName());
				paymentDetails.put("loyaltyAmount", payment.getLoyaltyAmount());
				paymentDetails.put("promotionalCash", payment.getPromotionalCash());
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
					new BigDecimal(Double.toString(inObj.getDouble("zomatoPickup"))), 
					new BigDecimal(Double.toString(inObj.getDouble("swiggy"))), 
					new BigDecimal(Double.toString(inObj.getDouble("dineOut"))),
					new BigDecimal(Double.toString(inObj.getDouble("payTm"))), 
					new BigDecimal(Double.toString(inObj.getDouble("foodPanda"))), 
					new BigDecimal(Double.toString(inObj.getDouble("uberEats"))), 
					new BigDecimal(Double.toString(inObj.getDouble("foodiloo"))), 
					new BigDecimal(Double.toString(inObj.getDouble("nearBy"))), 
					new BigDecimal(Double.toString(inObj.getDouble("swiggyPop"))), 
					new BigDecimal(Double.toString(inObj.getDouble("googlePay"))), 
					new BigDecimal(Double.toString(inObj.getDouble("magicPin"))), 
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
		JSONObject outObj = new JSONObject();
		INotification dao = new NotificationManager(false);
		IReportBuffer bufferDao = new ReportBufferManager(true);
		try {
			outObj.put("status", false);
			outObj.put("message", "Unknown Error");
			MPNotification notification = dao.getMPNotification(hotelId);
			outObj.put("status", true);
			outObj.put("checkOutOrders", notification.getCheckoutOrders());
			outObj.put("outOfStock", notification.getOutOfStock());
			
			if(netIsAvailable()) {
				IService serviceDao = new ServiceManager(false);
				ArrayList<ServiceLog> logs =  serviceDao.getServiceLogsForMessageNotSent(hotelId);
				if(logs != null) {
					for (int i = 0; i < logs.size(); i++) {
						this.sendEmailSmsForEod(hotelId, logs.get(i).getServiceDate(), logs.get(i).getServiceType(), logs.get(i).getReportForEmail());
					}
				}
				
				bufferDao.beginTransaction();
				ArrayList<ReportBuffer> bufferLog = bufferDao.getBuffer(hotelId);
				bufferDao.deleteBuffer(hotelId);
				bufferDao.commitTransaction(hotelId);
				ReportBuffer buffer = null;
				SendEmail email = new SendEmail();
				SendSMS sms = new SendSMS();
				ArrayList<String> emailIds = new ArrayList<String>();
				
				for (int i = 0; i < bufferLog.size(); i++) {
					buffer = bufferLog.get(i);
					if(buffer.getEmailIds().length() > 0) {
						for (int j =0; j< buffer.getEmailIds().length(); j++) {
							emailIds.add(buffer.getEmailIds().getString(j));
						}
						email.sendEmail(emailIds, buffer.getSubject(), buffer.getEmailText());
						emailIds.clear();
					}else if(buffer.getMobileNumbers().length() > 0) {
						for (int j =0; j< buffer.getMobileNumbers().length(); j++) {
							sms.sendSms(buffer.getSmsText(), buffer.getMobileNumbers().getString(j));
						}
					}
				}
			}
		} catch (Exception e) {
			bufferDao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v1/getOrderIdFromTableId")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOrderIdFromTableId(@QueryParam("hotelId") String hotelId, @QueryParam("tableId") String tableId) {
		JSONObject outObj = new JSONObject();
		
		ITable dao = new TableManager(false);
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IOrder orderDao = new OrderManager(false);
		ArrayList<Order> reportItems = dao.getVoidOrderReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("orderDate", reportItems.get(i).getOrderDate());
				itemDetails.put("waiterId", reportItems.get(i).getWaiterId());
				itemDetails.put("inhouse", orderDao.getOrderType(reportItems.get(i).getInHouse()));
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IOrder orderDao = new OrderManager(false);
		ArrayList<Order> reportItems = dao.getNCOrderReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("billNo", reportItems.get(i).getBillNo());
				itemDetails.put("orderDate", reportItems.get(i).getOrderDate());
				itemDetails.put("waiterId", reportItems.get(i).getWaiterId());
				itemDetails.put("inhouse", orderDao.getOrderType(reportItems.get(i).getInHouse()));
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IOrder orderDao = new OrderManager(false);
		IUser userDao = new UserManager(false);
		boolean visible = false;
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
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
				itemDetails.put("inhouse", orderDao.getOrderType(reportItems.get(i).getInhouse()));
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IOrder orderDao = new OrderManager(false);
		IUser userDao = new UserManager(false);
		boolean visible = false;
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
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
				itemDetails.put("inhouse", orderDao.getOrderType(reportItems.get(i).getInhouse()));
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		int totalQuantity = 0;
		ArrayList<ItemWiseReport> reportItems = dao.getItemwiseReport(hotelId, startDate, endDate);
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("title", reportItems.get(i).getTitle());
				itemDetails.put("collection", reportItems.get(i).getCollection());
				itemDetails.put("qty", reportItems.get(i).getQty());
				itemDetails.put("station", reportItems.get(i).getStation());
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject reportObj = new JSONObject();
		
		IReport dao = new ReportManager(false);
		JSONObject itemDetails = null;
		ArrayList<ConsumptionReport> reportItems = dao.getConsumptionReport(hotelId, startDate, endDate, AccessManager.DEPARTMENT_FOOD);
		try {
			for(int i=1; i<4; i++) {
				for (int j = 0; j < reportItems.size(); j++) {
					itemDetails = new JSONObject();
					itemDetails.put("title", reportItems.get(j).getTitle());
					itemDetails.put("collection", reportItems.get(j).getCollection());
					itemDetails.put("station", reportItems.get(j).getStation());
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		
		IReport dao = new ReportManager(false);
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		ArrayList<ItemWiseReport> reportItems = dao.getLiquorReport(hotelId, startDate, endDate);
		int totalQuantity = 0;
		try {
			for (int i = 0; i < reportItems.size(); i++) {
				itemDetails = new JSONObject();
				itemDetails.put("title", reportItems.get(i).getTitle());
				itemDetails.put("collection", reportItems.get(i).getCollection());
				itemDetails.put("qty", reportItems.get(i).getQty());
				itemDetails.put("station", reportItems.get(i).getStation());
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
		JSONArray itemsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IOrderItem orderDao = new OrderManager(false);
		ArrayList<EntityString> captains = orderDao.getCaptainOrderService(hotelId, startDate, endDate);
		IncentiveReport reportItemsFood;
		IncentiveReport reportItemsBar;
		Double totalBar =0.0;
		Double totalFood = 0.0;
		Double totalBarSale = 0.0;
		Double totalFoodSale = 0.0;
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
				totalBar += reportItemsBar.getIncentive().doubleValue();
				totalBarSale += reportItemsBar.getSale().doubleValue();
				totalFood += reportItemsFood.getIncentive().doubleValue();
				totalFoodSale += reportItemsFood.getSale().doubleValue();
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
		JSONArray itemsArr = new JSONArray();
		JSONArray captainsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject captainDetails = null;
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IOrderItem orderDao = new OrderManager(false);
		ArrayList<EntityString> captains = orderDao.getCaptainOrderService(hotelId, startDate, endDate);
		ArrayList<IncentiveReport> reportItems;
		Double totalIncentive = 0.0;
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
						totalIncentive += reportItems.get(j).getIncentive().doubleValue();
					}
					isBar = false;
				}
				captainDetails.put("items", itemsArr);
				captainDetails.put("totalIncentive", totalIncentive);
				totalIncentive = 0.0;
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
		JSONArray itemsArr = new JSONArray();
		JSONArray captainsArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject deliveryBoyDetails = null;
		JSONObject itemDetails = null;
		
		IReport dao = new ReportManager(false);
		IEmployee employeeDao = new EmployeeManager(false);
		IUser userDao = new UserManager(false);
		boolean visible = false;
		if(userDao.getUserById(hotelId, userId).getUserType()==UserType.SECRET.getValue())
			visible = true;
		ArrayList<Employee> deliveryBoys = employeeDao.getAllDeliveryEmployee(hotelId);
		ArrayList<DeliveryReport> reportItems;
		Double totalAmount = 0.0;
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
					totalAmount += reportItems.get(j).getTotal().doubleValue();
				}
				deliveryBoyDetails.put("items", itemsArr);
				deliveryBoyDetails.put("totalAmount", totalAmount);
				totalAmount = 0.0;
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
		JSONObject outObj = new JSONObject();
		//TODO
		//IAccount dao = new AccountManager(false);
		//BigDecimal cashBal = dao.getCashBalance(hotelId, section);
		try {
			//outObj.put("cashBalance", cashBal);
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
		IOrder dao = new OrderManager(false);
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
	@Path("/v1/applyOrderRemark")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String applyOrderRemark(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IOrder dao = new OrderManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);

			boolean status = dao.applyOrderRemark(inObj.getString("hotelId"), inObj.getString("orderId"),
					inObj.getString("remark"));

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
		JSONObject outObj = new JSONObject();
		JSONObject inObj = null;
		
		IServer dao = new ServerManager(true);
		try {
			outObj.put("status", false);
			outObj.put("count", 0);
			inObj = new JSONObject(jsonObject);
			String content = inObj.getString("content");
			long count = content.chars().filter(num -> num == ';').count();
			System.out.println("Starting updates to server. Count :" + count + ". OutletManager: "+ inObj.getString("hotelId") + ". Time: " + LocalDateTime.now());
			
			String hotelId = inObj.getString("hotelId");
			dao.beginTransaction(hotelId);
			if(!dao.syncOnServer(hotelId, content)) {
				System.out.println("Rolling back");
				dao.rollbackTransaction();
				return outObj.toString();
			}
			dao.commitTransaction(hotelId);
			outObj.put("status", true);
			outObj.put("count", count);

		} catch (JSONException e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		
		return outObj.toString();
	}

	@GET
	@Path("/v3/updateServer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateServer(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();
		StringBuilder transactionLog = new StringBuilder();
		IServer dao = new ServerManager(true);
		try {
			outObj.put("status", false);
			dao.beginTransaction();
			ArrayList<DBTransaction> transactions = dao.getAllTransactions(outletId);
			for (DBTransaction dbTransaction : transactions) {
				transactionLog.append(dbTransaction.getTransaction());
			}
			if(transactionLog.length() == 0) {
				outObj.put("status", true);
				dao.rollbackTransaction();
				return outObj.toString();
			}
			String targetUrl = "http://api.orderon.co.in:8080/OrderOn/Services/v1/syncOnSever";
			JSONObject inObj = new JSONObject();
			inObj.put("content", transactionLog.toString());
			inObj.put("hotelId", outletId);
			JSONObject response = new JSONObject(Services.executePost(targetUrl, inObj));
			if(response.getBoolean("status")) {
				outObj.put("updateTime", dao.updateServerLog(outletId).getString("time"));
				outObj.put("count", response.getInt("count"));
				System.out.println("Transaction Count is: "+ response.getInt("count"));
				outObj.put("status", true);
				dao.deleteAllTransactions(outletId);
			}
			dao.commitTransaction(outletId, true);
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
		JSONObject outObj = new JSONObject();
		IServer dao = new ServerManager(false);
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
		JSONObject outObj = new JSONObject();
		IServer dao = new ServerManager(false);
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
	@Path("/v1/updateKOTStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateKOTStatus(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IOrderItem dao = new OrderManager(false);
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
	
	public void SendEmail(String hotelId, String subject, String emailText, String header, boolean isOperational, boolean isSecurity) {

		SendEmailWithAttachment(hotelId, subject, emailText, header, null, isOperational, isSecurity);
	}
	
	public static boolean SendEmailWithAttachment(String hotelId, String subject, String emailText, String header,
			String[] filePaths, boolean isOperational, boolean isSecurity) {
		ArrayList<String> recipents = new ArrayList<String>();
		SendEmail email = new SendEmail();
		IEmployee dao = new EmployeeManager(false);
		ArrayList<Employee> employees = dao.getEmployeesByDesignation(hotelId, AccessManager.DESIGNATION_MANAGER);
		employees.addAll(dao.getEmployeesByDesignation(hotelId, AccessManager.DESIGNATION_OWNER));
		for (Employee employee : employees) {
			if(employee == null)
				continue;
			if(isSecurity) {
				if(!employee.getDesignation().equals(AccessManager.DESIGNATION_OWNER)) {
					continue;
				}
			}else if(isOperational) {
				if(!employee.getSendOperationalEmail())
					continue;
			}else{
				if(!employee.getSendEODEmail())
					continue;
			}
			if (!employee.getEmail().isEmpty()) {
				recipents.add(employee.getEmail());
			}
		}
		if(recipents.size()==0)
			return true;
		
		String headerTemplete = "";
		
		if(isOperational || isSecurity) {
			headerTemplete = "<html>"
				+ "<head><style>.alert {padding: 15px;margin-bottom: 20px;border: 1px solid transparent;border-radius: 4px;}"
				+ ".alert-warning {color: #8a6d3b;background-color: #fcf8e3;border-color: #faebcc;}</style></head>"
				+ "<body style='color:#797979; background:#f2f2f2;'><p>Dear Sir/Madam, </p>";
		}else {
			headerTemplete = billStyle;
		}

		if(!header.isEmpty())
			headerTemplete = header;
		String footerTemplete = "<p>Thank you, </p><p>Kind Regards, </p><p> OrderOn Support. </p>"
				+ "<p>If you have any other queries, please feel free to reach out to us at +91-98-67334779 or write to us at support@orderon.co.in. Happy ordering!</p></body></html>";
		StringBuilder builder = new StringBuilder();
		builder.append(headerTemplete);
		builder.append(emailText);
		builder.append(footerTemplete);
		boolean emailSent = false;
		
		if(netIsAvailable()) {
			if(filePaths==null) {
				if(email.sendEmail(recipents, subject, builder.toString()))
					emailSent = true;
			}else {
				if(email.sendEmailWithAttachment(recipents, subject, builder.toString(), filePaths))
					emailSent = true;
			}
		}
		
		if(!emailSent){
			JSONArray recipentArr = new JSONArray();
			for (String recipent : recipents) {
				recipentArr.put(recipent);
			}
			IReportBuffer bufferDao = new ReportBufferManager(false);
			bufferDao.addToBuffer(hotelId, subject, builder.toString(), "", recipentArr, new JSONArray());
		}
		
		return true;
	}
	
	public void SendSms(String hotelId, String smsText, String designation, boolean isOperational) {

		IEmployee dao = new EmployeeManager(false);
		IOutlet outletDao = new OutletManager(false);
		IReportBuffer bufferDao = new ReportBufferManager(false);
		Settings settings = outletDao.getSettings(hotelId);
		
		ArrayList<Employee> employees = dao.getEmployeesByDesignation(hotelId, designation.toUpperCase());
		if(!settings.getHasSms())
			return;
		
		boolean netAvailable = netIsAvailable();
		JSONArray recepients = new JSONArray();
		
		if(!smsText.equals("")) {
			SendSMS sms = new SendSMS();
			if(employees.size() >0) {
				for (Employee employee : employees) {
					if(isOperational) {
						if(employee.getSendSMS()) {
							if(netAvailable) {
								System.out.println("SMS to "+ employee.getFirstName() + " " + employee.getContactNumber());
								sms.sendSms(smsText, employee.getContactNumber());
							}else {
								recepients.put(employee.getContactNumber());
							}
						}
					}else {
						if(employee.getSendEODSMS()) {
							if(netAvailable) {
								System.out.println("EOD SMS sent to "+ employee.getFirstName() + " " + employee.getContactNumber());
								sms.sendSms(smsText, employee.getContactNumber());
							}else {
								recepients.put(employee.getContactNumber());
							}
						}
					}
				}
			}
		}
		if(!netAvailable && recepients.length()>0) {
			bufferDao.addToBuffer(hotelId, "", "", smsText, new JSONArray(), recepients);
		}
	}
	
	public void SendEmailAndSMS(String hotelId, String subject, String emailText, String smsText, String header
			, String designation, boolean isOperational, boolean isSecurity) {
		this.SendEmail(hotelId, subject, emailText, header, isOperational, isSecurity);
		this.SendSms(hotelId, smsText, designation, isOperational);
	}

	private static final byte[] kaffineDrawerCode = { 27, 112, 0, 100, (byte) 250 };
	private static final byte[] nomadaDrawerCode = { 27, 112, 48, 55, (byte) 121 };
	
	public static void cashdrawerOpen(String hotelId) {
		
		if(Configurator.getIsServer())
			return;
		byte[] open = { 27, 112, 0, 25, (byte) 251 };
		if (hotelId.equals("ka0001")) {
			open = kaffineDrawerCode;
		} else if(hotelId.equals("nd0001")) {
			open = nomadaDrawerCode;
		}else
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
		
		ILoyalty dao = new LoyaltyManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		MenuItem menu = null;
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String offerValue = inObj.getString("offerValue");
			if(inObj.getInt("offerType") == 2) {
				 menu = menuDao.getMenuItemByTitle(inObj.getString("hotelId"), offerValue);
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
		
		ILoyalty dao = new LoyaltyManager(false);
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
		JSONArray loyaltyOfferArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject loyaltyDetails = null;
		
		ILoyalty loyaltyDao = new LoyaltyManager(false);
		ILoyaltySettings loyaltySettingsDao = new LoyaltyManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		ICustomer custDao = new CustomerManager(false);
		ArrayList<LoyaltyOffer> loyaltyOffers = null;
		Customer customer = null;
		try {
			outObj.put("status", false);
			if(!hotelId.equals("")) {
				if(customerNumber != null && customerNumber.length()>0) {
					customer = custDao.getCustomerDetails(hotelId, customerNumber);
					loyaltyOffers = loyaltyDao.getAllLoyaltyOffersForCustomer(hotelId, customer);
				}else
					loyaltyOffers = loyaltyDao.getAllLoyaltyOffers(hotelId);
			}
			else if(!chainId.equals(""))
				loyaltyOffers = loyaltyDao.getAllLoyaltyOffersByChain(chainId);
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
					loyaltyDetails.put("offerValue", menuDao.getMenuById(hotelId, loyaltyOffers.get(i).getOfferValue()).getTitle());
				loyaltyDetails.put("offerQuantity", loyaltyOffers.get(i).getOfferQuantity());
				loyaltyDetails.put("points", loyaltyOffers.get(i).getPoints());
				if(customer != (null))
					loyaltyDetails.put("pointToRupee", loyaltySettingsDao.getLoyaltySettingByUserType(hotelId, customer.getUserType()).getPointToRupee());
				
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
		JSONObject outObj = new JSONObject();
		JSONObject collObj = new JSONObject();
		JSONArray collArr = new JSONArray();
		
		AccessManager dao = new AccessManager(false);
		ILoyalty loyaltyDao = new LoyaltyManager(false);
		IMenuItem menuDao = new MenuItemManager(false);
		String[] collections = {};
		try {
			LoyaltyOffer loyalty = loyaltyDao.getLoyaltyOfferById(hotelId, id);
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
					outObj.put("offerValue", menuDao.getMenuById(hotelId, loyalty.getOfferValue()).getTitle());
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
		
		ILoyalty dao = new LoyaltyManager(false);
		IOrder orderDao = new OrderManager(false);
		ICustomer custDao = new CustomerManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("hotelId");
			String orderId = inObj.getString("orderId");
			Order order = orderDao.getOrderById(outletId, orderId);
			Customer customer = custDao.getCustomerDetails(outletId, order.getCustomerNumber());
			
			outObj = dao.redeemLoyaltyOffer(outletId, order, 
						inObj.getInt("loyaltyId"), inObj.getInt("redeemablePoints"), customer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/redeemPromotionalCash")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String redeemPromotionalCash(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IOrder dao = new OrderManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			
			outObj.put("status", dao.redeemPromotionalCash(inObj.getString("outletId"), inObj.getString("orderId"), 
						new BigDecimal(inObj.getDouble("promotionalCash"))));
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
		JSONArray loyaltyArr = new JSONArray();
		JSONObject outObj = new JSONObject();
		JSONObject loyaltyDetails = null;
		
		ILoyaltySettings dao = new LoyaltyManager(false);
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
		
		ILoyaltySettings dao = new LoyaltyManager(false);
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


	@POST
	@Path("/v1/toggleSMS")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String toggleSMS(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		ICustomer dao = new CustomerManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			outObj.put("status", false);
			
			String outletId = "";
			String mobileNumber = "";

			if(!inObj.has("outletId")) {
				outObj.put("message", "outletId not found");
				return outObj.toString();
			}else if(!inObj.has("mobileNumber")) {
				outObj.put("message", "Mobile Number not found");
				return outObj.toString();
			}
			
			outletId = inObj.getString("outletId");
			mobileNumber = inObj.getString("mobileNumber");
			
			outObj = dao.toggleSMS(outletId, mobileNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v1/assignWaitersToTable")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String assignWaitersToTable(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		JSONObject waiterObj = new JSONObject();
		JSONArray inArr = new JSONArray();
		ITable dao = new TableManager(false);
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

	private void print(String html, String printerName, int copies, Settings settings) {
		if(SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC) {
			JEditorPane ed1 = new JEditorPane("text/html", html);
			PrinterJob pjob = PrinterJob.getPrinterJob();
			JFrame frame = new JFrame("KOT");
			frame.setSize(settings.getKOTWidth(), settings.getKOTHeight());
			frame.setContentPane(ed1);
			frame.setVisible(true);
			PageFormat preformat = pjob.defaultPage();
			preformat.setOrientation(PageFormat.PORTRAIT);        
			Paper paper = new Paper();
			double height = (double)settings.getKOTHeight();
			double width = (double)(settings.getKOTWidth() / settings.getKOTDivisor());
			paper.setSize(width, height);
			paper.setImageableArea(0, 0, width, height);
			// Orientation
			preformat.setPaper(paper);
			PageFormat postFormat = pjob.validatePage(preformat);
			//System.out.println("Breakpoint 2 : " + printerName);
			PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
			for (PrintService printService : printServices) {
				//System.out.println("Connecting to : " + printService.getName());
				try {
					if (printService.getName().equals(printerName)) {
						//System.out.println("Connected to : " + printService.getName());
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

	@POST
	@Path("/v3/connectWithZomato")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String connectWithZomato(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IOutlet dao = new OutletManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("outletId");
			Settings settings = dao.getSettings(outletId);
			JSONObject menuResponse = null;
			JSONObject response = new JSONObject(Services.executeZomatoPost(inObj.getString("targetUrl"), new JSONObject(inObj.getString("urlParameters")), settings.getApiKey()));
			System.out.println(response);
			if(response.has("menu_response")) {
				menuResponse = response.getJSONObject("menu_response");
				outObj.put("message", menuResponse.getString("message"));
				if(menuResponse.getBoolean("valid"))
					outObj.put("status", true);
			}else {
				outObj.put("message", response.getString("message"));
				if(response.getString("status").equals("success")) {
					outObj.put("status", true);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/connectWithOrderOn")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String connectWithOrderOn(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			JSONObject response = new JSONObject(Services.executePost(inObj.getString("targetUrl"), new JSONObject(inObj.getString("urlParameters"))));
			outObj.put("message", response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	public static String executeZomatoPost(String targetURL, JSONObject urlParameters, String apiKey) {
		  HttpURLConnection connection = null;

		  try {
		    //Create connection
		    URL url = new URL(targetURL);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Type", "application/json");
		    connection.setRequestProperty("accept", "application/json");
		    connection.setRequestProperty("x-zomato-api-key", apiKey);
		    connection.setUseCaches(false);
		    connection.setDoOutput(true);

		    //Send request
		    DataOutputStream wr = new DataOutputStream (
		        connection.getOutputStream());
		    wr.writeBytes(urlParameters.toString());
		    wr.close();

		    //Get Response  
		    int responseCode = connection.getResponseCode();
		    InputStream is = null;
		    if(responseCode == 422) 
		    	is = connection.getErrorStream();
		    else
		    	is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    System.out.println(response);
		    return response.toString();
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		 }
	}
	
	public static String executePost(String targetURL, JSONObject urlParameters) {
		  HttpURLConnection connection = null;

		  try {
		    //Create connection
		    URL url = new URL(targetURL);
		    connection = (HttpURLConnection) url.openConnection();
		    connection.setRequestMethod("POST");
		    connection.setRequestProperty("Content-Type", "application/json");
		    connection.setUseCaches(false);
		    connection.setDoOutput(true);

		    //Send request
		    DataOutputStream wr = new DataOutputStream (
		        connection.getOutputStream());
		    wr.writeBytes(urlParameters.toString());
		    wr.close();

		    //Get Response  
		    InputStream is = connection.getInputStream();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		    StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
		    String line;
		    while ((line = rd.readLine()) != null) {
		      response.append(line);
		      response.append('\r');
		    }
		    rd.close();
		    System.out.println(response);
		    return response.toString();
		  } catch (Exception e) {
		    e.printStackTrace();
		    return null;
		  } finally {
		    if (connection != null) {
		      connection.disconnect();
		    }
		 }
	}

	@POST
	@Path("/v3/riderUpdate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String riderUpdate(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();

		IOrder dao = new OrderManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			String ouletId = inObj.getString("outletId");
			JSONArray orders = inObj.getJSONArray("orders");
			JSONObject order = null;
			for(int i=0; i<orders.length(); i++) {
				order = orders.getJSONObject(i);
				dao.updateRiderDetails(ouletId, order.getString("orderId"), order.getString("riderName"), order.getString("riderNumber"), order.getString("riderStatus"));	
			}
			outObj.put("status", "success");
			
		} catch (Exception e) {
			dao.rollbackTransaction();
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v3/addPromotionalCampaign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String addPromotionalCampaign(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IPromotionalCampaign dao = new CustomerManager(false);
		try {
			inObj = new JSONObject(jsonObject);

			outObj.put("status", dao.addCampaign(inObj.getString("outletId"), inObj.getString("name"), inObj.getString("messageContent"),
					inObj.getJSONArray("outletIds"), inObj.getJSONArray("userTypes"), inObj.getString("sex"), inObj.getJSONArray("ageGroup")));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v3/editPromotionalCampaign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String editPromotionalCampaign(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IPromotionalCampaign dao = new CustomerManager(false);
		try {
			inObj = new JSONObject(jsonObject);

			outObj.put("status", dao.editCampaign(inObj.getString("outletId"), inObj.getInt("campaignId"), inObj.getString("name"), 
				inObj.getString("messageContent"), inObj.getJSONArray("outletIds"), inObj.getJSONArray("userTypes"), 
				inObj.getString("sex"), inObj.getJSONArray("ageGroup")));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	public static String getHTML(String urlToRead) throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
		   result.append(line);
		}
		rd.close();
		return result.toString();
	}

	@GET
	@Path("/v3/getPromotionalSMSBalance")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPromotionalSMSBalance(@QueryParam("corporateId") String corporateId, @QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();

		IOutlet outletDao = new OutletManager(false);

		try {

			outObj.put("status", false);
			Settings outlet = outletDao.getSettings(outletId);
			if(corporateId == null && outletId == null) {
				outObj.put("message", "Please provide a valid outletId/corporateId.");
				return outObj.toString();
			}
			if(corporateId != null && !corporateId.isEmpty()) {
				outletId = corporateId;
			}
			
			if(outlet == null) {
				outObj.put("message", "Please provide a valid outletId/corporateId.");
				return outObj.toString();
			}
			if(outletId.equals("h0003") || outletId.equals("h0002")) {
				outletId = "marty8";
			}
			
			String response = getHTML("http://www.smsjust.com/sms/user/balance_check.php?username="+outlet.getSmsId()+"&pass="+outlet.getSmsAPIKey());
			
			outObj.put("promotionalSMSBalance", response.split(":")[1]);
			outObj.put("status", true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getSMSBalance")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSMSBalance(@QueryParam("corporateId") String corporateId, @QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();

		IOutlet outletDao = new OutletManager(false);

		try {

			outObj.put("status", false);
			if(corporateId == null && outletId == null) {
				outObj.put("message", "Please provide a valid outletId/corporateId.");
				return outObj.toString();
			}
			if(corporateId != null && !corporateId.isEmpty()) {
				outletId = corporateId;
			}
			
			Settings outlet = outletDao.getSettings(outletId);
			if(outlet == null) {
				outObj.put("message", "Please provide a valid outletId/corporateId.");
				return outObj.toString();
			}
			
			outObj.put("transactionalSMSCount", outlet.getTransactionSMSCount());
			outObj.put("promotionalSMSBalance", outlet.getPromotionalSMSBalance());
			outObj.put("status", true);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
	
	@POST
	@Path("/v3/updateSmsBalance")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateSMSBalance(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IOutlet outletDao = new OutletManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("outletId");
			String corporateId = inObj.getString("corporateId");

			outObj.put("status", false);
			if(corporateId == null && outletId == null) {
				outObj.put("message", "Please provide a valid outletId/corporateId.");
				return outObj.toString();
			}
			if(corporateId != null && !corporateId.isEmpty()) {
				outletId = corporateId;
			}

			int promotionalSMSUsed = inObj.has("promotionalSMSCount")?inObj.getInt("promotionalSMSCount"):0;
			
			if(promotionalSMSUsed>0) {
				outletDao.updatePromotionalBalance(outletId, promotionalSMSUsed);
			}
			Settings outlet = outletDao.getSettings(outletId);

			outObj.put("status", true);
			outObj.put("promotionalSMSBalance", outlet.getPromotionalSMSBalance());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@POST
	@Path("/v3/applyPromtionalCampaign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String applyPromtionalCampaign(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		IPromotionalCampaign dao = new CustomerManager(false);
		ICustomer custDao = new CustomerManager(false);
		IOutlet outletDao = new OutletManager(false);
		try {
			inObj = new JSONObject(jsonObject);
			String outletId = inObj.getString("outletId");
			String corporateId = inObj.getString("corporateId");
			PromotionalCampaign campaign = dao.getPromotionalCampaignById(outletId, inObj.getInt("campaignId"));
			
			ArrayList<Customer> customers = custDao.getAllCustomersByFilters(outletId, campaign.getOutletIds()
					, campaign.getUserTypes(), campaign.getSex(), campaign.getAgeGroup());
			
			//For testing only
			if(outletId.equals("h0002") || outletId.equals("h0003")) {
				ArrayList<Customer> customers2 = new ArrayList<Customer>();
				
				for (Customer customer : customers) {
					if(customer.getMobileNumber().equals("9867334779")) {
						customers2.add(customer);
					}
				}
				customers = customers2;
			}
			//Test code ends
			
			Settings outlet = outletDao.getSettings(outletId);
			
			String balanceDetails = getHTML("http://www.smsjust.com/sms/user/balance_check.php?username="+outlet.getSmsId()+"&pass="+outlet.getSmsAPIKey());
			
			if(balanceDetails.equals("Invalid Username and Password. Please try again")) {
				outObj.put("message", "Invalid SMS Api Key. Please contact support.");
				return outObj.toString();
			}
			
			int currentBalance = Integer.parseInt(balanceDetails.split(":")[1]);
			if(currentBalance < customers.size()) {
				outObj.put("message", "Your balance is low. Please recharge your account to continue.");
				return outObj.toString();
			}
			
			outObj.put("status", false);
			
			if(!netIsAvailable()) {
				outObj.put("message", "Internet Unavailable! Please try again later.");
				return outObj.toString();
			}else {
				SendSMS sms = new SendSMS();
				if(outlet.getSmsAPIKey().isEmpty()) {
					outObj.put("message", "SMS API Key not found. Please contact OnderOn Support.");
					return outObj.toString();
				}
				int failedCount = 0;
				
				String smsContent = campaign.getMessageContent();
				smsContent = smsContent.replaceAll("&+", "amp;");
				smsContent = smsContent.replaceAll("#+", ";hash");
				smsContent = smsContent.replaceAll("\\++", "plus;");
				smsContent = smsContent.replaceAll("\\,+", "comma;");
				
				for (Customer customer : customers) {
					if(customer.getMobileNumber().startsWith("0", 1) || customer.getMobileNumber().startsWith("1", 1)
							|| customer.getMobileNumber().startsWith("3", 1) || customer.getMobileNumber().startsWith("2", 1)) {
						continue;
					}
					sms.sendPromotionalSms(outlet.getSmsId(), smsContent, customer.getMobileNumber(), outlet.getSmsAPIKey(), outlet.getSenderId());
				}
				
				balanceDetails = getHTML("http://www.smsjust.com/sms/user/balance_check.php?username="+outlet.getSmsId()+"&pass="+outlet.getSmsAPIKey());
				
				int newBalance = Integer.parseInt(balanceDetails.split(":")[1]);
				
				int sentSMSCount = currentBalance - newBalance;
				
				failedCount = customers.size() - sentSMSCount;
				
				JSONObject urlParameters = new JSONObject();
				urlParameters.put("outletId", outletId);
				urlParameters.put("corporateId", corporateId);
				urlParameters.put("promotionalSMSCount", sentSMSCount);
				
				dao.updateCampaign(outletId, campaign.getId(), sentSMSCount, customers.size(), failedCount, newBalance);
				outObj.put("sentCount", sentSMSCount);
				outObj.put("failedCount", failedCount);
				outObj.put("totalCustomers", customers.size());
				outObj.put("promotionalSmsBalance", newBalance);
				outObj.put("status", true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}

	@GET
	@Path("/v3/getPromotionalCampaigns")
	@Produces(MediaType.APPLICATION_JSON)
	public String getPromotionalCampaigns(@QueryParam("outletId") String outletId) {
		JSONObject outObj = new JSONObject();

		IPromotionalCampaign dao = new CustomerManager(false);
		ArrayList<PromotionalCampaign> campaigns = dao.getPromotionalCampaigns(outletId);
		
		try {
			outObj.put("campaigns", campaigns);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}	
	
	
	@POST
	@Path("/v1/deleteCampaign")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteCampaign(String jsonObject) {
		JSONObject inObj = null;
		JSONObject outObj = new JSONObject();
		
		IPromotionalCampaign dao = new CustomerManager(false);
		try {
			outObj.put("status", false);
			inObj = new JSONObject(jsonObject);
			outObj.put("status", dao.deleteCampaign(inObj.getString("outletId"), inObj.getInt("campaignId")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outObj.toString();
	}
}