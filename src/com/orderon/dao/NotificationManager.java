package com.orderon.dao;

import java.util.ArrayList;

import com.orderon.interfaces.INotification;

public class NotificationManager extends AccessManager implements INotification {

	public NotificationManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}

	public int getNextNotificationId(String userId, String hotelId) {
		String sql = "SELECT * FROM Notification WHERE orderId LIKE '" + userId + ":%' AND hotelId = '" + hotelId
				+ "' ORDER BY notId";
		ArrayList<Notification> notifs = db.getRecords(sql, Notification.class, hotelId);
		if (notifs.size() == 0) {
			return 0;
		}
		return notifs.get(notifs.size() - 1).getNotificationId() + 1;
	}

	@Override
	public Notification getNextNotification(String hotelId, String userId) {
		String sql = "SELECT * FROM Notification WHERE hotelId='" + hotelId + "' AND orderId LIKE '" + userId
				+ ":%' ORDER BY notId";
		ArrayList<Notification> notifs = db.getRecords(sql, Notification.class, hotelId);
		if (notifs.size() == 0) {
			return null;
		} else {
			sql = "DELETE FROM Notification WHERE notId=" + notifs.get(0).getNotificationId() + " AND hotelId='" + hotelId
					+ "' AND orderId LIKE '" + userId + ":%';";
			db.executeUpdate(sql, true);
			return notifs.get(0);
		}
	}

	@Override
	public MPNotification getMPNotification(String hotelId) {

		String sql = "SELECT COUNT(Stock.sku) AS outOfStock FROM Material, Stock WHERE Material.outletId= '" + hotelId
				+ "' AND Material.sku == Stock.sku AND Stock.quantity <= Material.minQuantity ";

		MPNotification notification = new MPNotification();

		//notification.setOutOfStock(db.getOneRecord(sql, MPNotification.class, hotelId).getOutOfStock());
		notification.setHotelId(hotelId);

		sql = "SELECT COUNT(Id) AS checkOutOrders FROM Orders WHERE hotelId = '" + hotelId + "' AND state = 1";

		notification.setCheckoutOrders(db.getOneRecord(sql, MPNotification.class, hotelId).getCheckoutOrders());

		return notification;
	}
}
