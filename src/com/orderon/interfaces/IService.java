package com.orderon.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.orderon.dao.AccessManager.ServiceLog;

public interface IService {

	public String getServiceDate(String hotelId);

	public String getServiceType(String hotelId);

	public boolean addService(String hotelId, String serviceType, String serviceDate, int cashInHand);

	public boolean checkSevicesEnded(String hotelId, String serviceDate, String serviceType);

	public boolean endService(String hotelId, String serviceDate, String serviceType);

	public boolean updateReportStatusToSent(String hotelId, String serviceDate, String serviceType);

	public boolean updateReportStatusToNotSent(String hotelId, String serviceDate, String serviceType, String reportForEmail);

	public ArrayList<ServiceLog> getServiceLogsForMessageNotSent(String hotelId);

	public ServiceLog getServiceLog(String hotelId, String serviceDate);

	public ServiceLog getServiceLog(String hotelId, String serviceDate, String serviceType);
	
	public BigDecimal getSaleForService(String hotelId, String serviceDate, String serviceType);

	public int getCashInHand(String hotelId, String serviceDate, String serviceType);

	public ServiceLog getCurrentService(String hotelId);
}
