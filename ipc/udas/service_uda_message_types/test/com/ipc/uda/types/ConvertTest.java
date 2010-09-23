package com.ipc.uda.types;

import junit.framework.Assert;

import org.junit.Test;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.internal.dto.impl.ButtonImpl;
import com.ipc.uda.types.util.Converters;



public class ConvertTest
{



    @Test
    public void testButton_DS2UDA()
    {
	Button dsButton = new ButtonImpl();
	ButtonType udaButton = new ButtonType();
	
	dsButton.setButtonType(Button.EnumButtonType.ICM);
	
	// TODO: fill in other fields
	
	Converters.DSButtonConverter.convert(dsButton, udaButton);

	Assert.assertEquals(udaButton.getButtonType().toString(), dsButton.getButtonType().toString());
    }
    
    @Test
    public void testButton_UDA2DS()
    {
	
    }
    
    
}
