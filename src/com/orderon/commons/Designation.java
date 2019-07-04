package com.orderon.commons;

public enum Designation {

	WAITER(0, 7), MANAGER(1, 4), ADMINISTRATOR(2, 1), CHEF(3, 6), RECEPTIONIST(4, 5), RETAILASSCOCIATE(5, 5), 
	BACKOFFICE(6, 5), DELIVERYBOY(7, 7), OWNER(8, 2), CAPTAIN(9, 6), UNAUTHORIZED(100, 8), CASHIER(10, 5),
	HELPER(11, 8), CLEANER(12, 8), EXEC_CHEF(13, 7), COMMI_1(14, 8), COMMI_2(15, 8), COMMI_3(16, 8);
	
	private int value;
	private int userLevel;
	
	private Designation(int value, int userLevel){
		this.value = value;
		this.userLevel = userLevel;
	}
	
	public int getValue() {
		return value;
	}

	public int getUserLevel() {
		return userLevel;
	}
	
	
	public static Designation getType(int value){
		
		if(value == 0)
			return Designation.WAITER;
		else if(value == 1)
			return Designation.MANAGER;
		else if(value == 2)
			return Designation.ADMINISTRATOR;
		else if(value == 3)
			return Designation.CHEF;
		else if(value == 4)
			return Designation.RECEPTIONIST;
		else if(value == 5)
			return Designation.RETAILASSCOCIATE;
		else if(value == 6)
			return Designation.BACKOFFICE;
		else if(value == 7)
			return Designation.DELIVERYBOY;
		else if(value == 8)
			return Designation.OWNER;
		else if(value == 9)
			return Designation.CAPTAIN;
		else if(value == 10)
			return Designation.CASHIER;
		else 
			return Designation.UNAUTHORIZED;
	}		
}

