package com.ipc.uda.types;



import javax.xml.bind.annotation.XmlRootElement;

import com.ipc.uda.service.execution.Returnable;



/**
 * @author mordarsd
 * 
 * 
 */
@XmlRootElement
public class UdaResponse extends UdaResponseType
{

    public void setReturnable(final Returnable ret)
    {
        if (ret instanceof Event)
        {
            setEvent((EventType)ret);
        }
        else if (ret instanceof QueryResult)
        {
            setQueryResult((QueryResult)ret);
        }
        else
        {
            throw new IllegalArgumentException("Unable to set unknown type: " + ret.getClass());
        }
    }

}
