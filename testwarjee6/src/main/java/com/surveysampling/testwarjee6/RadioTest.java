package com.surveysampling.testwarjee6;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import com.surveysampling.testwarjee6.pojo.MyRadio;

@Named
@SessionScoped
public class RadioTest implements Serializable {
	private final Logger logger = FacesLoggerFactory.createFacesLogger();
	private ArrayList<RList> radiolists = new ArrayList<RList>();

	public static class RList {
		private ArrayList<MyRadio> radios;
		public RList(ArrayList<MyRadio> radios) {
			this.radios = radios;
		}
		public ArrayList<MyRadio> getRadios() {
			return this.radios;
		}
		public String toString() {
			String rr = "";
			for (MyRadio r : this.radios) {
				rr += " "+r;
			}
			return rr;
		}
	}

	@PostConstruct
	public void init() {
		this.logger.info("RadioTest.init()");
		ArrayList<MyRadio> rr = new ArrayList<MyRadio>();
		rr.add(new MyRadio("a"));
		rr.add(new MyRadio("b"));
		RList rl = new RList(rr);
		this.radiolists.add(rl);

		rr = new ArrayList<MyRadio>();
		rr.add(new MyRadio("c"));
		rr.add(new MyRadio("d"));
		rl = new RList(rr);
		this.radiolists.add(rl);
	}

	public ArrayList<RList> getRadios() {
		this.logger.info("RadioTest.getRadios()");
		return this.radiolists;
	}
}
