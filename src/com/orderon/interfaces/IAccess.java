package com.orderon.interfaces;

public interface IAccess {

	public void beginTransaction();
	
	public void beginTransaction(String outletId);

	public void commitTransaction();

	public void rollbackTransaction();
}
