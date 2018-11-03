import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayJdbc
{
	public static void main(final String... args) throws ClassNotFoundException, SQLException, InterruptedException
	{
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		final Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://ctclsnebu:2037/;DOMAIN=SURVEYSAMPLING","christopher_mosher","Pherfs07");
		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		final PreparedStatement select = connection.prepareStatement("SELECT * FROM MOSHER_TEST");
		final ResultSet rs = select.executeQuery();
		int i = 0;
		while (rs.next())
		{
			if (++i % 100 == 0)
			{
				System.out.println("at row: "+i);
				System.out.flush();
			}
			Thread.sleep(100);
		}
		System.out.println("total row count: "+i);
		System.out.flush();
		rs.close();
		select.close();
		connection.close();
	}

	public static void doWorkInTempSchema() throws ClassNotFoundException, SQLException
	{
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		final Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://localhost/Play", "sa", ""/*put sa password here!!!!!*/);

		final String schema = createSchemaName();
		System.out.println("Schema: "+schema);

		final PreparedStatement stCreateSchema = connection.prepareStatement("CREATE SCHEMA "+schema);

		stCreateSchema.execute();

		stCreateSchema.close();

		final PreparedStatement stCreateTableAbc = connection.prepareStatement("CREATE TABLE "+schema+".ABC (X INTEGER)");
		stCreateTableAbc.execute();
		stCreateTableAbc.close();

		final PreparedStatement stInsertIntoAbc = connection.prepareStatement("INSERT INTO "+schema+".ABC (X) VALUES (?)");
		stInsertIntoAbc.setInt(1, 43);
		int cUpdated;
		cUpdated = stInsertIntoAbc.executeUpdate();
		System.out.println(""+cUpdated+" rows updated.");
		stInsertIntoAbc.setInt(1, 57);
		cUpdated = stInsertIntoAbc.executeUpdate();
		System.out.println(""+cUpdated+" rows updated.");
		stInsertIntoAbc.close();

		final PreparedStatement stSelectDoubleX = connection.prepareStatement("SELECT X*2 AS DOUBLE_X FROM "+schema+".ABC WHERE X = ?");
		stSelectDoubleX.setInt(1, 43);
		final ResultSet rs = stSelectDoubleX.executeQuery();
		while (rs.next())
		{
			System.out.println(rs.getInt("DOUBLE_X"));
		}
		stSelectDoubleX.close();

		PreparedStatement stDropTableAbc = connection.prepareStatement("DROP TABLE "+schema+".ABC");
		stDropTableAbc.execute();
		stDropTableAbc.close();

		final PreparedStatement stDropSchema = connection.prepareStatement("DROP SCHEMA "+schema);
		stDropSchema.execute();
		stDropSchema.close();

		connection.close();
	}

	/**
	 * @return
	 */
	private static String createSchemaName()
	{
		final String unique = UUID.randomUUID().toString();
		final String cleanedUnique = unique.replace('-', '_');
		final String schema = "TEST_"+cleanedUnique+"_SCHEMA";

		return schema;
	}

}
