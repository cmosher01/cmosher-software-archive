package com.surveysampling.testwarjee6.pojo;

import java.io.Serializable;
import java.util.logging.Logger;

import com.surveysampling.testwarjee6.FacesLoggerFactory;

public class MyRadio implements Serializable {
	private final Logger logger = FacesLoggerFactory.createFacesLogger();

	private final String id;

	private int radio1 = 0;
	private int radio2 = 0;
	private String notes = "";

	public MyRadio(final String id) {
		this.id = id;
	}

	public int getRadio1() {
		logger.info(""+this.radio1+" = "+this.id+".getRadio1()");
		return this.radio1;
	}

	public void setRadio1(final int radio1) {
		if (this.radio1 == radio1) {
			return;
		}
		logger.info(this.id+".setRadio1("+radio1+")   ----persist---->");
		this.radio1 = radio1;
	}

	public int getRadio2() {
		logger.info(""+this.radio2+" = "+this.id+".getRadio2()");
		return this.radio2;
	}

	public void setRadio2(final int radio2) {
		if (this.radio2 == radio2) {
			return;
		}
		logger.info(this.id+".setRadio2("+radio2+")   ----persist---->");
		this.radio2 = radio2;
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(final String notes) {
		if (notes == null) {
			return;
		}
		if (this.notes.equals(notes)) {
			return;
		}
		logger.info(this.id+".setNotes("+notes+")   ----persist---->");
		this.notes = notes;
	}
	public String toString() {
		return this.id;
	}
}
