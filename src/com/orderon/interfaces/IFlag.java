package com.orderon.interfaces;

import java.util.ArrayList;

import com.orderon.dao.AccessManager.Flag;

public interface IFlag {

	public ArrayList<Flag> getFlags(String hotelId);

	public boolean addFlag(String hotelId, String name, String groupId);

	public Flag getFlagById(String hotelId, int id);

	public boolean deleteFlag(String hotelId, int id);
}
