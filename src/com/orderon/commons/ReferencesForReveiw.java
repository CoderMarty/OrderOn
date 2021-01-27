package com.orderon.commons;

public enum ReferencesForReveiw {
	
	FACEBOOK(0), INSTAGRAM(1), WORD_OF_MOUTH(2), WALK_IN(3), FLYER(4), PROMOTIONAL_MARKETING(5);
	
	private int value;
	
	private ReferencesForReveiw(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}