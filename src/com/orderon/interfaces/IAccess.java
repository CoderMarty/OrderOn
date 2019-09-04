package com.orderon.interfaces;

public interface IAccess {
	
	public void beginTransaction(String outletId);

	public void commitTransaction(String outletId);

	public void commitTransaction(String outletId, boolean isServerUpdate);

	public void rollbackTransaction();
}
