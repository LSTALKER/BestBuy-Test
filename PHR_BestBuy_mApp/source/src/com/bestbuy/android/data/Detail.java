package com.bestbuy.android.data;

import java.io.Serializable;

public class Detail implements Serializable {
	
	private static final long serialVersionUID = 4416911928926199484L;
	private String name;
	private String value;
	private boolean isSelected;
	
	public Detail() {
		this.name = "";
		this.value = "";
	}
	
	public Detail(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSearchName() {
		String name = this.name;
		StringBuffer searchName = new StringBuffer();
		String[] splitString = name.split("[^a-zA-Z0-9]+");
		for (int i = 0; i < splitString.length; i++) {
			splitString[i] = splitString[i].replaceAll("[^a-zA-Z0-9]", "");
			String firstLetter = splitString[i].substring(0, 1);
			String rest = splitString[i].substring(1);
			if (i == 0) {
				splitString[i] = firstLetter.toLowerCase() + rest.toLowerCase();
			} else {
				splitString[i] = firstLetter.toUpperCase() + rest.toLowerCase();
			}
			searchName.append(splitString[i]);
		}
		String result = searchName.toString();
		result = result.replace("3d", "3D");
		if (result.equals("playerType")) {
			result = "dvdPlayerType";
		}
		if (result.equals("aspectRatio")) {
			result = "productAspectRatio";
		}
		if (result.equals("mobileOperatingSystem")) {
			result = "operatingSystem";
		}
		return result;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public int compareTo(Object another) {
		Detail other = (Detail)another;
		if (this.name.equals(other.name)) {
			return 0;
		} else {
			return this.name.compareTo(other.name);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Detail)) {
			return false;
		}
		Detail detail = (Detail) obj;
		return (this.name.equals(detail.name));
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
}
