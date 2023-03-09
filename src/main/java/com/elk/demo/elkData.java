package com.elk.demo;

import java.util.ArrayList;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class elkData {


@JsonProperty
private Integer id;

@JsonProperty
ArrayList<Integer> field1 = new ArrayList<>();

@JsonProperty
ArrayList<Integer> field2 = new ArrayList<>();

@JsonProperty
ArrayList<Integer> field3 = new ArrayList<>();

public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}

public ArrayList<Integer> getField1() {
	return field1;
}

public void setField1(ArrayList<Integer> field1) {
	this.field1 = field1;
}

public ArrayList<Integer> getField2() {
	return field2;
}

public void setField2(ArrayList<Integer> field2) {
	this.field2 = field2;
}

public ArrayList<Integer> getField3() {
	return field3;
}

public void setField3(ArrayList<Integer> field3) {
	this.field3 = field3;
}




}
