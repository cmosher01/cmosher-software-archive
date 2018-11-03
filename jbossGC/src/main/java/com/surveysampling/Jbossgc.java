package com.surveysampling;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@ManagedBean(name = "jbossgc")
public class Jbossgc {
	private final List hold = new ArrayList();
	public void allocAndFree() {
		System.out.println("begin allocAndFree...");
		System.out.flush();
        for (int i = 0; i < 10000000; ++i) {
            byte[] garbage = new byte[10];
        }
		System.out.println("done allocAndFree.");
		System.out.flush();
	}
	public void alloc() {
		System.out.println("begin alloc...");
		System.out.flush();
        Foo last = new Foo(null);
        for (int i = 0; i < 10000000; ++i) {
            last = new Foo(last);
        }
        hold.add(last);
		System.out.println("done alloc.");
		System.out.flush();
	}
	public void free() {
		System.out.println("begin free...");
		System.out.flush();
		hold.clear();
		System.out.println("done free.");
		System.out.flush();
	}
}
