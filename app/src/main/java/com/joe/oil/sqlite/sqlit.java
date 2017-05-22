package com.joe.oil.sqlite;

public class sqlit {
	public String date;
	String isselct;
	 String name;

	public sqlit() {
	}

	public sqlit(String date, String isselct,String name) {
		this.date = date;
		this.isselct = isselct;
		this.name=name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getIsselct() {
		return isselct;
	}

	public void setIsselct(String isselct) {
		this.isselct = isselct;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
