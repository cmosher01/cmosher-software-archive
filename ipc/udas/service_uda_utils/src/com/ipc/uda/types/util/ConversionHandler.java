/**
 * 
 */
package com.ipc.uda.types.util;



/**
 * A Handler that is passed into the Converter for handling the conversion of one type to another type. This handler will be
 * invoked when a standard conversion does not suffice.
 * 
 * @author mordarsd
 * 
 */
public interface ConversionHandler<A, B>
{

    /**
     * Handle a custom type conversion
     * 
     * @param src
     * @param target
     * @param propertyName
     */
    void customConvert(A src, B target, String propertyName);

}
