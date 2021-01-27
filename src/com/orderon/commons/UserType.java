package com.orderon.commons;

public enum UserType {

	WAITER(0, 7), MANAGER(1, 4), ADMINISTRATOR(2, 1), CHEF(3, 6), RECEPTIONIST(4, 5), CASHIER(10, 5), STEWARD(11, 7), CAPTAIN(9, 6), DELIVERY(7, 7), OWNER(8, 2), DATAMANAGER(12, 5), SECRET(99, 3), UNAUTHORIZED(100, 8);
	
	private int value;
	private int userLevel;
	
	public int getValue(){
		return this.value;
	}
	
	public int getUserLevel() {
		return userLevel;
	}

	private UserType(int value, int userLevel){
		this.value = value;
		this.userLevel = userLevel;
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
		else if(value == 10)
			return UserType.CASHIER;
		else if(value == 11)
			return UserType.STEWARD;
		else if(value == 9)
			return UserType.CAPTAIN;
		else if(value == 7)
			return UserType.DELIVERY;
		else if(value == 8)
			return UserType.OWNER;
		else if(value == 12)
			return UserType.DATAMANAGER;
		else if(value == 99)
			return UserType.SECRET;
		else
			return UserType.UNAUTHORIZED;
	}
			
}

