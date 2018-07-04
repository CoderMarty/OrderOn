package com.orderon;

public enum Department {

	KITCHEN(0), BACKOFFICE(1), FLOOR(2), RECEPTION(3), UNKNOWN(100);
	
	private int value;
	
	private Department(int value){
		this.value = value;
	}
			
}


