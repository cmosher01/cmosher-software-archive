package com.mediamania;

import java.util.Collection;
import java.util.Iterator;

public class GetOptions extends MediaManiaApp {
    
public static void main(String[] args)
{
    GetOptions options = new GetOptions();
    options.print();
}

public void print()
{
    Collection options = pmf.supportedOptions();
    Iterator iter = options.iterator();
    System.out.println("Supported options:");
    while( iter.hasNext() ){
        String option = (String) iter.next();
        System.out.println(option);
    }
    System.out.println("\nDefault values for options:");
    System.out.print("IgnoreCache           ");
    System.out.println( pmf.getIgnoreCache() );
    System.out.print("NontransactionalRead  ");
    System.out.println( pmf.getNontransactionalRead() );
    System.out.print("NontransactionalWrite ");
    System.out.println( pmf.getNontransactionalWrite() );
    System.out.print("Optimistic            ");
    System.out.println( pmf.getOptimistic() );
    System.out.print("RestoreValues         ");
    System.out.println( pmf.getRestoreValues() );
    System.out.print("RetainValues          ");
    System.out.println( pmf.getRetainValues() );
}

public void execute() {
}

}