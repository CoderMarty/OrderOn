package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.OrderSpecification;
import com.orderon.dao.AccessManager.Specifications;

public interface ISpecification extends IAccess{
	
	public Boolean addOrderSpecification(String hotelId, String orderId, String subOrderId, String menuId, int itemId,
			String specification);

	public ArrayList<Specifications> getSpecifications(String hotelId);

	public boolean addSpecification(String name);

	public ArrayList<OrderSpecification> getOrderedSpecification(String hotelId, String orderId, String menuId,
			String subOrderId, int itemId);

	public ArrayList<OrderSpecification> getOrderedSpecification(String hotelId, String orderId, String menuId);
}
