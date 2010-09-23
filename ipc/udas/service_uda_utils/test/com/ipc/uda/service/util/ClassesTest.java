/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.util;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.junit.Test;

/**
 * @author mosherc
 *
 */
public class ClassesTest
{
    // just a simple class hierarchy
    static abstract class A
    {
        public abstract String get();
    }
    static class B extends A
    {
        @Override public String get() { return "B"; }
        
    }
    static class C extends A
    {
        @Override public String get() { return "C"; }
    }





    @Test(expected=IllegalStateException.class)
    public void testCannotInstantiate()
    {
        new Classes();
    }

    @Test
    public void testCreateInstancesByClassName() throws ExceptionInInitializerError, SecurityException, ClassCastException, InstantiationException, IllegalAccessException, ClassNotFoundException, LinkageError
    {
        final String classList = "com.ipc.uda.service.util.ClassesTest$B,com.ipc.uda.service.util.ClassesTest$C";
        final ArrayList<A> rInstances = new ArrayList<A>();
        Classes.<A>createInstancesByClassName(classList,A.class,rInstances);
        assertEquals(2,rInstances.size());

        final A ab = rInstances.get(0);
        assertTrue(ab instanceof B);

        final A ac = rInstances.get(1);
        assertTrue(ac instanceof C);
    }

    @Test
    public void testCreateInstances() throws ExceptionInInitializerError, ClassCastException, ClassNotFoundException, LinkageError, SecurityException, InstantiationException, IllegalAccessException
    {
        final ArrayList<Class<? extends A>> rclassA = new ArrayList<Class<? extends A>>();
        final String classList = "com.ipc.uda.service.util.ClassesTest$B,com.ipc.uda.service.util.ClassesTest$C";
        Classes.<A>getClassesByName(classList,A.class,rclassA);

        final ArrayList<A> rInstances = new ArrayList<A>();
        Classes.<A>createInstances(rclassA,rInstances);

        assertEquals(2,rInstances.size());

        final A ab = rInstances.get(0);
        assertTrue(ab instanceof B);

        final A ac = rInstances.get(1);
        assertTrue(ac instanceof C);
    }

    @Test
    public void testGetClassesByName() throws ExceptionInInitializerError, ClassCastException, ClassNotFoundException, LinkageError, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, InvocationTargetException, NoSuchMethodException
    {
        final ArrayList<Class<? extends Number>> rNum = new ArrayList<Class<? extends Number>>();
        final String classList = "java.lang.Integer,java.lang.Long";
        Classes.<Number>getClassesByName(classList,Number.class,rNum);
        assertEquals(2,rNum.size());
        final Number aInteger = rNum.get(0).getConstructor(String.class).newInstance("77");
        assertTrue(aInteger instanceof Integer);
        final Integer aIntegerCast = (Integer)aInteger;
        assertEquals(77,aIntegerCast.intValue());

        final Number aLong = rNum.get(1).getConstructor(String.class).newInstance("77");
        assertTrue(aLong instanceof Long);
        final Long aLongCast = (Long)aLong;
        assertEquals(77L,aLongCast.longValue());
    }

    @Test
    public void nominalGetClassByName() throws InstantiationException, IllegalAccessException, ExceptionInInitializerError, ClassCastException, ClassNotFoundException, LinkageError
    {
        final Class<? extends String> cs = Classes.<String>getClassByName("java.lang.String",String.class);
        assertNotNull(cs);
        final String s = cs.newInstance();
        assertEquals("",s);
    }

    @Test(expected=ClassNotFoundException.class)
    public void errorGetClassByName() throws InstantiationException, IllegalAccessException, ExceptionInInitializerError, ClassCastException, ClassNotFoundException, LinkageError
    {
        final Class<? extends String> cs = Classes.<String>getClassByName("com.ipc.uda.service.util.ThisClassDoesNotExist-12330480-0ebf-11df-8a39-0800200c9a66",String.class);
        assertNotNull(cs);
        final String s = cs.newInstance();
        assertEquals("",s);
    }

    @Test
    public void nominalGetClassByNameStrict() throws InstantiationException, IllegalAccessException
    {
        final Class<? extends String> cs = Classes.<String>getClassByNameStrict("java.lang.String",String.class);
        assertNotNull(cs);
        final String s = cs.newInstance();
        assertEquals("",s);
    }

    @Test(expected=IllegalStateException.class)
    public void errorGetClassByNameStrict()
    {
        Classes.getClassByNameStrict("com.ipc.uda.service.util.ThisClassDoesNotExist-12330480-0ebf-11df-8a39-0800200c9a66",Class.class);
    }
}
