package com.orderon.dao;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.orderon.interfaces.IService;

public class ServiceManager extends AccessManager implements IService{

	public ServiceManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getServiceDate(String hotelId) {
		String sql = "SELECT * FROM ServiceLog WHERE isCurrent=0 AND hotelId='" + escapeString(hotelId) + "';";
		ServiceLog service = db.getOneRecord(sql, ServiceLog.class, hotelId);
		if (service == null) {
			return null;
		}
		return service.getServiceDate();
	}

	@Override
	public String getServiceType(String hotelId) {
		String sql = "SELECT * FROM ServiceLog WHERE isCurrent=0 AND hotelId='" + escapeString(hotelId) + "';";
		ServiceLog service = db.getOneRecord(sql, ServiceLog.class, hotelId);
		if (service == null) {
			return null;
		}
		return service.getServiceType();
	}

	@Override
	public boolean addService(String hotelId, String serviceType, String serviceDate, int cashInHand) {

		String sql = "INSERT INTO ServiceLog "
				+ "(hotelId, serviceDate, startTimeStamp, endTimeStamp, serviceType, isCurrent, cashInHand, smsEmailSent) "
				+ "VALUES('" + escapeString(hotelId) + "', '" + serviceDate + "', '"
				+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', '', '"
				+ escapeString(serviceType) + "', " + 0 + ", " + cashInHand + ", 'false');";
		return db.executeUpdate(sql, hotelId, true);
	}
	
	@Override
	public boolean endService(String hotelId, String serviceDate, String serviceType) {

		String sql = "UPDATE ServiceLog SET endTimeStamp ='"
				+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "', isCurrent = 1  WHERE hotelId = '"
				+ hotelId + "' AND serviceDate = '" + serviceDate + "';";
		return db.executeUpdate(sql, hotelId, true);
	}
	
	@Override
	public boolean updateEndTime(String hotelId, String serviceDate, String serviceType) {

		String sql = "UPDATE ServiceLog SET endTimeStamp ='"
				+ new SimpleDateFormat("yyyy/MM/dd HH.mm.ss").format(new Date()) + "'  WHERE hotelId = '"
				+ hotelId + "' AND serviceDate = '" + serviceDate + "';";
		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public boolean updateReportStatusToSent(String hotelId, String serviceDate, String serviceType) {

		String sql = "UPDATE ServiceLog SET smsEmailSent ='true' WHERE hotelId = '"
				+ hotelId + "' AND serviceDate = '" + serviceDate + "' AND serviceType = '" + serviceType + "';";
		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public boolean updateReportStatusToNotSent(String hotelId, String serviceDate, String serviceType, String reportForEmail) {

		String sql = "UPDATE ServiceLog SET smsEmailSent ='false', reportForEmail = '"
				+ reportForEmail + "' WHERE hotelId = '"
				+ hotelId + "' AND serviceDate = '" + serviceDate + "' AND serviceType = '" + serviceType + "';";
		return db.executeUpdate(sql, hotelId, true);
	}

	@Override
	public ArrayList<ServiceLog> getServiceLogsForMessageNotSent(String hotelId) {

		String sql = "SELECT * FROM ServiceLog WHERE smsEmailSent = 'false' AND hotelId = '" + hotelId + "' AND isCurrent = 1;";
		return db.getRecords(sql, ServiceLog.class, hotelId);
	}

	@Override
	public ServiceLog getServiceLog(String hotelId, String serviceDate) {

		String sql = "SELECT * FROM ServiceLog WHERE hotelId = '" + hotelId + "' AND serviceDate = '" + serviceDate
				+ "';";

		return db.getOneRecord(sql, ServiceLog.class, hotelId);
	}

	@Override
	public ServiceLog getServiceLog(String hotelId, String serviceDate, String serviceType) {

		String sql = "SELECT * FROM ServiceLog WHERE hotelId = '" + hotelId + "' AND serviceDate = '" + serviceDate
				+ "' AND serviceType = '"+serviceType+"';";

		return db.getOneRecord(sql, ServiceLog.class, hotelId);
	}

	@Override
	public BigDecimal getSaleForService(String hotelId, String serviceDate, String serviceType) {
		
		String sql = "SELECT ROUND(SUM(Payments.total)*100)/100 AS entityId FROM Payments, Orders WHERE Payments.systemId = '" 
				+ hotelId + "' AND Orders.orderDate = '" + serviceDate + "' AND Orders.serviceType = '" + serviceType
				+ "' AND Orders.orderID == Payments.orderId;";

		return db.getOneRecord(sql, EntityBigDecimal.class, hotelId).getId();
	}

	@Override
	public int getCashInHand(String hotelId, String serviceDate, String serviceType) {

		String sql = "SELECT cashInHand as entityId FROM ServiceLog WHERE hotelId = '" + hotelId
				+ "' AND serviceDate = '" + serviceDate
				+ "' AND serviceType = '"+serviceType+"';";

		return db.getOneRecord(sql, EntityId.class, hotelId).getId();
	}

	@Override
	public ServiceLog getCurrentService(String hotelId) {

		String sql = "SELECT * FROM ServiceLog WHERE hotelId = '" + hotelId + "' AND isCurrent = 0;";

		return db.getOneRecord(sql, ServiceLog.class, hotelId);
	}
}
