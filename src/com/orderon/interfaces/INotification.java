package com.orderon.interfaces;

import com.orderon.dao.AccessManager.MPNotification;
import com.orderon.dao.AccessManager.Notification;

public interface INotification {

	public int getNextNotificationId(String userId, String hotelId);
	
	public Notification getNextNotification(String hotelId, String userId);

	public MPNotification getMPNotification(String hotelId);
}
