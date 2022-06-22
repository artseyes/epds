package com.EPDS.US;

import java.util.ArrayList;
import java.util.List;

public class Dog {
	private String id;
	private String color;
	private String size;
	private String type;
	private String parentDogId;

	private List<Dog> childrenDogList = new ArrayList<Dog>();

	public Dog(String color, String size, String type, String id,
			String parentDogId) {
		super();
		this.color = color;
		this.size = size;
		this.type = type;
		this.id = id;
		this.parentDogId = parentDogId;
	}

	public String getParentDogId() {
		return parentDogId;
	}

	public void setParentDogId(String parentDogId) {
		this.parentDogId = parentDogId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Dog> getChildrenDogList() {
		return childrenDogList;
	}

	public void setChildrenDogList(List<Dog> childrenDogList) {
		this.childrenDogList = childrenDogList;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
