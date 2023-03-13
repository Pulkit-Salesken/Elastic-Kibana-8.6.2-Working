package com.elk.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class vectorData {


@JsonProperty
private Integer id;

@JsonProperty
private double[] embedding;

private String sentence;

public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}

public double[] getEmbedding() {
	return embedding;
}

public void setEmbedding(double[] vecs) {
	this.embedding = vecs;
}

public String getSentence() {
	return sentence;
}

public void setSentence(String sentence) {
	this.sentence = sentence;
}




}
