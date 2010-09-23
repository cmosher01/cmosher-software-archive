/**
 * 
 */
package com.ipc.uda.types.util;

import java.util.HashMap;

import org.junit.Test;

/**
 * @author mordarsd
 *
 */
public class ConvertTest
{

    @Test
    public void test1() 
    {
	
	Converter c = new Converter( ClassA.class, ClassB.class );

	
    }
    
    
    
    private class ClassA 
    {
	private String name;
	public void setName(String name)
	{
	    this.name = name;
	}
	public String getName()
	{
	    return this.name;
	}
    
    }
    
    private class ClassB
    {
	private String name;
	public void setName(String name)
	{
	    this.name = name;
	}
	public String getName()
	{
	    return this.name;
	}
    }
}
