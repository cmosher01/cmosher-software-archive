/*
 * Created on April 18, 2005
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;



/**
 * Here we invent classes PanelistKey and OrderKey to add
 * compile-time type-safety to our use of primary keys in Java.
 * 
 * @author Chris Mosher
 */
public class DatalessKeyExample2
{
    /**
     * @param args
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        DatalessKeyAccess access = null;
        try
        {
            access = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }

        DatalessKeyExample.registerDatabaseDriver();
        final Connection db = DatalessKeyExample.connectToDatabase();



        /*
         * Get a Panelist ID first.
         */
        PreparedStatement st = db.prepareStatement(
                "SELECT panelistID FROM DA_Prod.GeoPlacement WHERE geoID = 1234 AND ROWNUM <= 1");

        ResultSet rs = st.executeQuery();
        rs.next();
        // We know this is a panelist key, so assume that here (in one place only).
        // From then on, the Java compiler will enforce this fact for us.
        PanelistKey somePanelistPK = new PanelistKey(access.createKeyFromResultSet(rs, 1));

        st.close();




        /*
         * Now get an Order ID.
         */
        st = db.prepareStatement(
            "SELECT MAX(orderID) FROM DA_Prod.Orders");
    
        rs = st.executeQuery();
        rs.next();
        // we know this is an order key, so let Java compiler enforce this fact
        OrderKey someOrderPK = new OrderKey(access.createKeyFromResultSet(rs, 1));

        st.close();




        /*
         * Now see that we cannot erroneously interchange
         * the Panelist key and the Order key.
         */
        takesPanelistKey(somePanelistPK); // OK
//        takesPanelistKey(someOrderPK); // won't compile
        takesOrderKey(someOrderPK); // OK
//        takesOrderKey(somePanelistPK); // won't compile

        System.out.println(somePanelistPK);
    }

    private static void takesPanelistKey(final PanelistKey keyPanelist)
    {
        final DatalessKeyAccess access = DatalessKeyAccessFactory.getDatalessKeyAccess(keyPanelist.getKey());
        access.keyAsString(keyPanelist.getKey());
    }

    private static void takesOrderKey(final OrderKey keyOrder)
    {
        final DatalessKeyAccess access = DatalessKeyAccessFactory.getDatalessKeyAccess(keyOrder.getKey());
        access.keyAsString(keyOrder.getKey());
    }

    /**
     * Contains a DatalessKey. Methods just
     * forward to that key.
     * 
     * @author Chris Mosher
     */
    private static class TableKey
    {
        private final DatalessKey key;
        TableKey(final DatalessKey key)
        {
            this.key = key;
        }
        /**
         * @return key
         */
        public DatalessKey getKey()
        {
            return this.key;
        }
        /**
         * key as string
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return DatalessKeyAccessFactory.getDatalessKeyAccess(this.key).keyAsString(this.key);
        }
    }
    /**
     * A key into the Panelist table.
     */
    private static final class PanelistKey extends TableKey
    {
        PanelistKey(final DatalessKey key)
        {
            super(key);
        }
    }
    /**
     * A key into the Orders table.
     */
    private static final class OrderKey extends TableKey
    {
        OrderKey(final DatalessKey key)
        {
            super(key);
        }
    }
}
