package com.orderon;

public enum UserType {

	WAITER(0), MANAGER(1), ADMINISTRATOR(2), CHEF(3), RECEPTIONIST(4), CASHIER(5), STEWARD(6), CAPTAIN(7), DELIVARY(8), OWNER(9), DATAMANAGER(10), SECRET(99), UNAUTHORIZED(100);
	
	private int value;
	
	public int getValue(){
		return this.value;
	}
	
	private UserType(int value){
		this.value = value;
	}
	
	public static UserType getType(int value){
		
		if(value == 0)
			return UserType.WAITER;
		else if(value == 1)
			return UserType.MANAGER;
		else if(value == 2)
			return UserType.ADMINISTRATOR;
		else if(value == 3)
			return UserType.CHEF;
		else if(value == 4)
			return UserType.RECEPTIONIST;
		else if(value == 5)
			return UserType.CASHIER;
		else if(value == 6)
			return UserType.STEWARD;
		else if(value == 7)
			return UserType.CAPTAIN;
		else if(value == 8)
			return UserType.DELIVARY;
		else if(value == 9)
			return UserType.OWNER;
		else if(value == 10)
			return UserType.DATAMANAGER;
		else if(value == 99)
			return UserType.SECRET;
		else
			return UserType.UNAUTHORIZED;
	}
			
}

