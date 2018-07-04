package com.orderon;

public enum Designation {

	WAITER(0), MANAGER(1), ADMINISTRATOR(2), CHEF(3), RECEPTIONIST(4), RETAILASSCOCIATE(5), 
	BACKOFFICE(6), DELIVERYBOY(7), OWNER(8), CAPTAIN(9), UNAUTHORIZED(100);
	
	private int value;
	
	private Designation(int value){
		this.value = value;
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
		else 
			return Designation.UNAUTHORIZED;
	}		
}

