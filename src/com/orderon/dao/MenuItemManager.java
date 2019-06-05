package com.orderon.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import com.orderon.interfaces.IMenuItem;

public class MenuItemManager extends AccessManager implements IMenuItem {

	public MenuItemManager(Boolean transactionBased) {
		super(transactionBased);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public MenuItem itemExists(String hotelId, String title) {
		String sql = "SELECT * FROM MenuItems WHERE title='" + escapeString(title) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";

		return db.getOneRecord(sql, MenuItem.class, hotelId);
	}

	@Override
	public boolean isTaxableMenuItem(String hotelId, String menuId) {

		String sql = "SELECT * FROM MenuItems WHERE menuId = '" + escapeString(menuId) + "' AND hotelId='"
				+ escapeString(hotelId) + "' AND isTaxable = 0;";

		return db.hasRecords(sql, hotelId);
	}

	@Override
	public String addMenuItem(String hotelId, String title, String description, String collection, String subCollection, String station,
			String flags, String groups, int preparationTime, BigDecimal deliveryRate, BigDecimal dineInRate, BigDecimal dineInNonAcRate, 
			BigDecimal onlineRate, BigDecimal costPrice, String image, int incentiveType, int incentive, String code, String taxes, String charges,
			boolean isRecomended, boolean isTreats, boolean isDefault, boolean isBogo, BigDecimal comboReducedPrice, boolean isAddon
			, boolean syncOnZomato, boolean gstInclusive, String discountType, BigDecimal discountValue) {

		String menuId = getNextMenuId(hotelId, collection);
		taxes = taxes.replaceAll("\"", "");
		charges = charges.replaceAll("\"", "");
		title = AccessManager.toTitleCase(title);
		int vegType = 1;
		if(flags.contains("2")) {
			vegType = 2;
		}else if(flags.contains("5")) {
			vegType = 3;
		}else if(flags.contains("3")) {
			vegType = 4;
		}
		if(gstInclusive) {
			BigDecimal gstCal = new BigDecimal("0.95238095");
			dineInRate = dineInRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
			dineInNonAcRate = dineInNonAcRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
			deliveryRate = deliveryRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
			onlineRate = gstCal.multiply(onlineRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		String sql = "INSERT INTO MenuItems "
				+ "(corporateId, hotelId, menuId, title, description, collection, subCollection, station, flags, groups, preparationTime, deliveryRate, dineInRate, dineInNonAcRate, onlineRate, costPrice, "
				+ "img, method, code, state, hasIncentive, incentive, taxes, charges, isRecomended, isTreats, isDefault, isBogo, "
				+ "comboReducedPrice, isAddon, vegType, isTaxable, syncOnZomato, discountType, discountValue) VALUES('', '"
				+ escapeString(hotelId) + "', '" + escapeString(menuId) + "', '" + escapeString(title) + "', '"
				+ escapeString(description) + "', '" + escapeString(collection) + "', '" + escapeString(subCollection) + "', '" 
				+ escapeString(station) + "', '" + escapeString(flags) +"', '" + escapeString(groups) +  "', " + Integer.toString(preparationTime) + ", " + deliveryRate
				+ ", " + dineInRate + ", " + dineInNonAcRate + ", " + onlineRate + ", " + costPrice + ", '" + (image.equals("No image") ? "" : "1") + "', '', '"
				+ code + "', " + MENUITEM_STATE_AVAILABLE + ", " + incentiveType + ", " + incentive + ", ?" 
				+ ", '" + escapeString(charges) + "', '" + Boolean.toString(isRecomended) + "', '" + Boolean.toString(isTreats) 
				+ "', '" + Boolean.toString(isDefault) + "', '" + Boolean.toString(isBogo) + "', " + comboReducedPrice + ", '" + Boolean.toString(isAddon)
				+ "', "+vegType+", 0, '"+syncOnZomato+"', '"+discountType+"', "+discountValue+");";

		System.out.println(sql);
		Connection conn;
		try {
			conn = db.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, taxes);;
			if(pstmt.executeUpdate() > 0) {
				return menuId;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}

	@Override
	public MenuItem getMenuById(String hotelId, String menuId) {
		String sql = "SELECT * FROM MenuItems WHERE menuId='" + escapeString(menuId) + "' AND hotelId='" + hotelId + "';";
		return db.getOneRecord(sql, MenuItem.class, hotelId);
	}

	@Override
	public MenuItem getMenuItemByTitle(String hotelId, String title) {

		String sql = "SELECT * FROM MenuItems WHERE title = '" + escapeString(title) + "' AND hotelId='"
				+ escapeString(hotelId) + "';";

		return db.getOneRecord(sql, MenuItem.class, hotelId);
	}

	@Override
	public ArrayList<MenuItem> getMenuItemBySearch(String hotelId, String query) {

		query = escapeString(query);
		String sql = "SELECT * FROM MenuItems WHERE title LIKE '%" + query + "%' OR menuId LIKE '%" + query
				+ "%' OR code LIKE '%" + query + "%' OR collection LIKE '%" + query + "%' AND hotelId='" + escapeString(hotelId) + "';";

		return db.getRecords(sql, MenuItem.class, hotelId);
	}

	@Override
	public ArrayList<MenuItem> getMenu(String hotelId) {
		String sql = "SELECT * FROM MenuItems  WHERE hotelId='" + hotelId + "' AND state = " + MENUITEM_STATE_AVAILABLE
				+ " ORDER BY title;";
		return db.getRecords(sql, MenuItem.class, hotelId);
	}

	@Override
	public ArrayList<MenuItem> getMenuForIntegration(String hotelId) {
		String sql = "SELECT * FROM MenuItems  WHERE hotelId='" + hotelId + "' AND state = " + MENUITEM_STATE_AVAILABLE
				+ " AND syncOnZomato = 'true';";
		return db.getRecords(sql, MenuItem.class, hotelId);
	}

	@Override
	public ArrayList<EntityString> getMenuItems(String hotelId) {
		String sql = "SELECT title AS entityId FROM MenuItems  WHERE hotelId='" + hotelId + "';";
		return db.getRecords(sql, EntityString.class, hotelId);
	}

	@Override
	public ArrayList<MenuItem> getMenuMP(String hotelId) {
		String sql = "SELECT * FROM MenuItems  WHERE hotelId='" + hotelId + "'";
		return db.getRecords(sql, MenuItem.class, hotelId);
	}

	@Override
	public String getNextMenuId(String hotelId, String collection) {

		String sql = "SELECT MAX(CAST(menuId AS integer)) AS entityId FROM MenuItems WHERE hotelId='" + hotelId + "'";

		EntityId entity = db.getOneRecord(sql, EntityId.class, hotelId);

		if (entity != null) {
			return Integer.toString((entity.getId() + 1));
		}
		return null;
	}

	@Override
	public Boolean updateMenuItem(String hotelId, String menuId, String title, String description, String collection, String subCollection, String station,
				String flags, String groups, int preparationTime, BigDecimal deliveryRate, BigDecimal dineInRate, BigDecimal dineInNonAcRate, 
				BigDecimal onlineRate, BigDecimal costPrice, String image, int incentiveType, int incentive, String code, String taxes, String charges,
				boolean isRecomended, boolean isTreats, boolean isDefault, boolean isBogo, BigDecimal comboReducedPrice, boolean isAddon,
				boolean syncOnZomato, boolean gstInclusive, String discountType, BigDecimal discountValue) {
		
		title = AccessManager.toTitleCase(title);

		int vegType = 1;
		if(flags.contains("2")) {
			vegType = 2;
		}else if(flags.contains("5")) {
			vegType = 3;
		}else if(flags.contains("3")) {
			vegType = 4;
		}
		MenuItem menuItem = this.getMenuById(hotelId, menuId);

		if(gstInclusive != menuItem.getGstInclusive()) {
			if(gstInclusive) {
				BigDecimal gstCal = new BigDecimal("100");
				gstCal = gstCal.divide(new BigDecimal("105"));
				dineInRate = dineInRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
				dineInNonAcRate = dineInNonAcRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
				deliveryRate = deliveryRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
				onlineRate = onlineRate.multiply(gstCal).setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}
		
		String sql = "UPDATE MenuItems SET title = '" + escapeString(title) 
				+ "', description = '" + escapeString(description) 
				+ "', collection = '" + collection 
				+ "', subCollection = '" + subCollection 
				+ "', station = '" + station 
				+ "', flags = '" + flags 
				+ "', groups = '" + groups 
				+ "', preparationTime = " + preparationTime
				+ ", deliveryRate = " + deliveryRate 
				+ ", dineInRate = " + dineInRate  
				+ ", dineInNonAcRate = " + dineInNonAcRate  
				+ ", onlineRate = " + onlineRate 
				+ ", costPrice = " + costPrice 
				+ ", hasIncentive = " + incentiveType
				+ ", incentive = " + incentive 
				+ ", vegType = " + vegType 
				+ ", code = '" + code
				+ "', taxes = '" + taxes 
				+ "', charges = '" + charges 
				+ "', img  ='" + (image.equals("No image") ? "" : "1") 
				+ "', isRecomended = '" + isRecomended
				+ "', isTreats = '" + isTreats
				+ "', syncOnZomato = '" + syncOnZomato
				+ "', isBogo = '" + isBogo
				+ "', isDefault = '" + isDefault
				+ "', isAddon = '" + isAddon
				+ "', comboReducedPrice = " + comboReducedPrice
				+ ", discountType = '" + discountType
				+ "', discountValue = " + discountValue
				+ " WHERE hotelId = '" + hotelId 
				+ "' AND menuId = '" + menuId + "';";
		System.out.println(sql);
		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean updateMenuItemStockState(String hotelId, String menuId, int state) {

		String sql = "UPDATE MenuItems SET state = " + Integer.toString(state) + " WHERE hotelId = '"
				+ escapeString(hotelId) + "' AND menuId = '" + escapeString(menuId) + "';";

		return db.executeUpdate(sql, true);
	}

	@Override
	public Boolean updateMenuItemStateOnZomato(String hotelId, String menuId, boolean state) {

		String sql = "UPDATE MenuItems SET syncOnZomato = '" + state + "' WHERE hotelId = '"
				+ escapeString(hotelId) + "' AND menuId = '" + escapeString(menuId) + "';";

		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean deleteItem(String hotelId, String menuId) {
		String sql = "DELETE FROM MenuItems WHERE menuId = '" + menuId + "' AND hotelId='" + hotelId + "';";
		return db.executeUpdate(sql, true);
	}

	@Override
	public boolean changeMenuItemState(String hotelId, String menuId, int state) {

		String sql = "UPDATE MenuItems SET state = '" + state + "' WHERE hotelId = '" + hotelId + "' AND menuId = '"
				+ menuId + "';";
		return db.executeUpdate(sql, true);
	}
	
	public void loadShortForms(String hotelId) {

		ArrayList<MenuItem> menuItems = this.getMenu(hotelId);
		for (MenuItem menuItem : menuItems) {
			String sql = "UPDATE MenuItems SET shortform = '" + this.generateShortForm(menuItem.getTitle())
					+ "' WHERE menuId = '" + menuItem.getMenuId() + "';";
			db.executeUpdate(sql, false);
		}
	}
}
