/**
 * 
 */
package com.ipc.uda.types.util;



import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.Contact;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserCDI.EnumICMDivertCondition;
import com.ipc.uda.types.ButtonType;
import com.ipc.uda.types.ButtonTypeEnum;
import com.ipc.uda.types.DivertReasonTypeEnum;
import com.ipc.uda.types.IconTypeEnum;
import com.ipc.uda.types.ImmutableContactType;
import com.ipc.uda.types.IncomingActionCLITypeEnum;
import com.ipc.uda.types.IncomingActionFloatTypeEnum;
import com.ipc.uda.types.IncomingActionPriorityTypeEnum;
import com.ipc.uda.types.IncomingActionRingsTypeEnum;
import com.ipc.uda.types.PocMediaTypeType;
import com.ipc.uda.types.PocTypeType;
import com.ipc.uda.types.UID;
import com.ipc.uda.types.UserCdiType;



/**
 * Utility class containing various Converters for converting to/from DS and UDA types
 * 
 * @author mordarsd
 * 
 */
public final class Converters
{

    private static final Set<Class<?>> IGNORE_SET = new HashSet<Class<?>>(2,1.0f);
    private static final Map<Class<? extends Enum<?>>, Class<? extends Enum<?>>> ENUM_CONVERSION_MAP = 
        new HashMap<Class<? extends Enum<?>>, Class<? extends Enum<?>>>(64,1.0f);

    static
    {
        IGNORE_SET.add(List.class);
        IGNORE_SET.add(Set.class);

        ENUM_CONVERSION_MAP.put(Button.EnumButtonType.class,com.ipc.uda.types.EnumButtonType.class);
        ENUM_CONVERSION_MAP.put(Button.EnumDivertReason.class,DivertReasonTypeEnum.class);
        ENUM_CONVERSION_MAP.put(Button.EnumIcon.class,IconTypeEnum.class);
        ENUM_CONVERSION_MAP.put(Button.EnumIncomingActionCLI.class,IncomingActionCLITypeEnum.class);
        ENUM_CONVERSION_MAP.put(Button.EnumIncomingActionFloat.class,IncomingActionFloatTypeEnum.class);
        ENUM_CONVERSION_MAP.put(Button.EnumIncomingActionPriority.class,IncomingActionPriorityTypeEnum.class);
        ENUM_CONVERSION_MAP.put(Button.EnumIncomingActionRings.class,IncomingActionRingsTypeEnum.class);

        ENUM_CONVERSION_MAP.put(PersonalPointOfContact.EnumMediaType.class, PocMediaTypeType.class);
        ENUM_CONVERSION_MAP.put(PersonalPointOfContact.EnumPOCType.class, PocTypeType.class);
        
        ENUM_CONVERSION_MAP.put(PointOfContact.EnumMediaType.class, PocMediaTypeType.class);
        ENUM_CONVERSION_MAP.put(PointOfContact.EnumPOCType.class, PocTypeType.class);
        
        ENUM_CONVERSION_MAP.put(com.ipc.uda.types.EnumICMDivertCondition.class, EnumICMDivertCondition.class);
        
    }

    /**
     * Returns a type Converter for converting a DS button to a UDA ButtonType
     */
    public static final Converter<Button, ButtonType> DSButtonConverter = new Converter<Button, ButtonType>(Button.class,
            ButtonType.class,IGNORE_SET,new ConversionHandler<Button, ButtonType>(){
                @Override
                public void customConvert(Button src, ButtonType target, String propertyName)
                {
                    if ( "Id".equals(propertyName))
                    {
                        int id = src.getId();
                        target.setButtonId(new UID(String.valueOf(id)));
                    
                    }
                    else
                    {
                        System.err.println("DSButtonHandler Button Conversion not handled for property: " + propertyName);    
                    }
                    
                }
            },ENUM_CONVERSION_MAP);

    /**
     * Returns a type Converter for converting a DS button to a UDA ButtonType
     */
    public static final Converter<ButtonType, Button> UDAButtonConverter = new Converter<ButtonType, Button>(ButtonType.class,
            Button.class,IGNORE_SET,new ConversionHandler<ButtonType, Button>() 
            {
                @Override
                public void customConvert(ButtonType src, Button target, String propertyName)
                {
                    System.err.println("UDAButtonHandler Button Conversion not handled for property: " + propertyName);
                }
            },ENUM_CONVERSION_MAP);
            
    /**
     * Returns a type Converter for converting a DS Contact to a UDA ImmutableContactType
     */
    public static final Converter<Contact, ImmutableContactType> DSContactConverter = new Converter<Contact, ImmutableContactType>(
            Contact.class, ImmutableContactType.class, IGNORE_SET,
            new ConversionHandler<Contact, ImmutableContactType>()
            {
                @Override
                public void customConvert(Contact src, ImmutableContactType target,
                        String propertyName)
                {
                    if ( "Id".equals(propertyName))
                    {
                        target.setContactId(new UID(String.valueOf((src.getId()))));
                    } 
                    else if ("Company".equals(propertyName))
                    {
                        target.setCompanyName(src.getCompany());
                    }
                    else
                    {
                        System.err
                                .println("DSContactConverter Conversion not handled for property: "
                                        + propertyName);
                    }
                }
            }, ENUM_CONVERSION_MAP);

    /**
     * Returns a type Converter for converting a DS PersonalContact to a UDA ImmutableContactType
     */
    public static final Converter<PersonalContact, ImmutableContactType> DSPersonalContactConverter = new Converter<PersonalContact, ImmutableContactType>(
            PersonalContact.class, ImmutableContactType.class, IGNORE_SET,
            new ConversionHandler<PersonalContact, ImmutableContactType>()
            {
                @Override
                public void customConvert(PersonalContact src, ImmutableContactType target,
                        String propertyName)
                {
                    if ( "Id".equals(propertyName))
                    {
                        target.setContactId(new UID(String.valueOf((src.getId()))));
                    }
                    else if ("Company".equals(propertyName))
                    {
                        target.setCompanyName(src.getCompany());
                    }
                    else
                    {
                        System.err
                                .println("DSPersonalContactConverter Conversion not handled for property: "
                                        + propertyName);
                    }
                }
            }, ENUM_CONVERSION_MAP);

    /**
     * Returns a type Converter for converting a DS UserCDI to a UDA UserCdiType
     */
    public static final Converter<UserCDI, UserCdiType> DSUserCDIConverter = new Converter<UserCDI, UserCdiType>(
            UserCDI.class, UserCdiType.class, IGNORE_SET,
            new ConversionHandler<UserCDI, UserCdiType>()
            {
                @Override
                public void customConvert(UserCDI src, UserCdiType target,
                        String propertyName)
                {
                    System.err
                            .println("DSButtonHandler Button Conversion not handled for property: "
                                    + propertyName);
                }
            }, ENUM_CONVERSION_MAP);
    
}
