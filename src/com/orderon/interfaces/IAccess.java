package com.orderon.interfaces;

public interface IAccess {
	
	public void beginTransaction(String systemId);

	public void commitTransaction(String systemId);

	public void commitTransaction(String systemId, boolean isServerUpdate);

	public void rollbackTransaction();
}
