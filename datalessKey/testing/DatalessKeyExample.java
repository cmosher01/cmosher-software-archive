/*
 * Created on April 13, 2005
 */

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;

/**
 * Simple example using <code>DatalessKey</code>s.
 * 
 * @author Chris Mosher
 */
public class DatalessKeyExample
{
    /**
     * Simple example using <code>DatalessKey</code>s.
     * @param args
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        /*
         * Get the accessor for the underlying type we want to use.
         * Note that we only do this here, in one place in all the code;
         * from then on, we do all work through the two interfaces
         * DatalessKeyAccess and DatalessKey, without needing to refer
         * to the underlying datatype.
         */
        DatalessKeyAccess access = null;
        try
        {
            access = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
        }
        catch (Throwable e)
        {
            throw new RuntimeException(e);
        }







        // read keys from persistent String storage (would be XML file or
        // text file, for example, in a real application)
        DatalessKey key1 = access.createKeyFromString("0xDeadBeef");
        DatalessKey key2 = access.createKeyFromString("0XDEADBEEF");
        // note that underlying type of key1 and key2 is long,
        // but we don't need to know that to make use of them

        // test keys equals to each other
        System.out.println(key1.equals(key2));
        System.out.println(key2.equals(key1));

        // example of persisting keys (as XML)
        System.out.println("<record><pk>"+access.keyAsString(key1)+"</pk></record>");
        System.out.println("<record><pk>"+access.keyAsString(key2)+"</pk></record>");

        // insert string into map under key1
        Map map = new HashMap();
        map.put(key1, "test using keys in maps");

        // retrieve string from map using (equivalent) key2
        String test = (String)map.get(key2);
        System.out.println(test);




        // now read some foreign key from a record in the database
        registerDatabaseDriver();
        final Connection db = connectToDatabase();
        PreparedStatement st = db.prepareStatement(
                "SELECT geoTypeID FROM DA_Prod.Geo WHERE name = \'BATON ROUGE, LA\'");

        ResultSet rs = st.executeQuery();
        rs.next();
        // get the key from column 1 (we still don't need to know the datatype)
        DatalessKey someGeoTypePK = access.createKeyFromResultSet(rs, 1);

        st.close();



        // now use that key in a select from another table, just to
        // prove that the key will work when set into a PreparedStatement
        // (this could just as easily have been an update statement):
        PreparedStatement prep = db.prepareStatement(
                "SELECT name FROM DA_Prod.GeoType WHERE geoTypeID = ?");

        // we still don't need to know the underlying datatype
        access.putKeyToPreparedStatement(someGeoTypePK, prep, 1);
        ResultSet rs2 = prep.executeQuery();
        rs2.next();
        System.out.println(rs2.getString(1));

        prep.close();
        db.close();
    }

    /**
     * @throws SQLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static void registerDatabaseDriver() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        DriverManager.registerDriver((Driver)Class.forName("oracle.jdbc.driver.OracleDriver").newInstance());
    }

    /**
     * @return
     * @throws SQLException
     */
    public static Connection connectToDatabase() throws SQLException
    {
        return DriverManager.getConnection("jdbc:oracle:thin:@dev1.surveysampling.com:1521:DEV","projectdirector","darkwater");
    }
}
