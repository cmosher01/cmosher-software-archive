package com.nebu.dubknowledge.server.jsf.managedbeans.test;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean(name = "nullTestBean")
public class MBNullTest {
	private final String nullValue = null;
	private final String name = "MBNullTest";

	public String getNullValue() {
		System.out.println("getNullValue, returning: "+nullValue);
		System.out.flush();
		return nullValue;
	}

	public String getName() {
		System.out.println("getName, returning: "+name);
		System.out.flush();
		return name;
	}

	public void testAction(final String s) {
		final String pr;
		if (s == null) {
			pr = "[null]";
		} else if (s.isEmpty()) {
			pr = "[empty string]";
		} else {
			pr = s;
		}
		System.out.println("testAction, argument: "+pr);
		System.out.flush();
	}
}
