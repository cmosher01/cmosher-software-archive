import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;



public class main
{
	private static class SqlObj
	{
		private static final String ANY = "%";
		private static final String DEFAULT_DATABASE = "PANEL";

		public final String db;
		public final String schema;
		public final String obj;
		private final String upper;
		private final String lower;

		public SqlObj(String db, String schema, String obj)
		{
			this.db = db;
			this.schema = schema;
			this.obj = obj;
			this.upper = (this.db + "." + this.schema + "." + this.obj).toUpperCase();
			this.lower = this.upper.toLowerCase();
		}

		public static void buildListOfObjects(final String[] args, final List<SqlObj> rObj)
		{
			if (args.length <= 0)
			{
				buildOneObject("", rObj);
			}
			for (final String arg : args)
			{
				buildOneObject(arg, rObj);
			}
		}

		private static void buildOneObject(final String arg, final List<SqlObj> rObj)
		{
			final StringTokenizer strtok = new StringTokenizer(arg, ".");
			final String db = strtok.hasMoreTokens() ? strtok.nextToken() : DEFAULT_DATABASE;
			final String schema = strtok.hasMoreTokens() ? strtok.nextToken() : ANY;
			final String obj = strtok.hasMoreTokens() ? strtok.nextToken() : ANY;
			/* drop any further tokens, such as ".sql" file extension */

			rObj.add(new SqlObj(db, schema, obj));
		}

		public boolean isWild()
		{
			return schema.contains(ANY) || obj.contains(ANY);
		}
	}



	public static void main(final String... args) throws ClassNotFoundException, SQLException, IOException
	{
		new main().run(args);
	}


	private static final String CONFIG_FILE = "getproc.properties";

	private String driver;
	private String url;
	private String username;
	private String password;
	private Connection connection;



	public void run(final String... args) throws ClassNotFoundException, SQLException, IOException
	{
		final List<SqlObj> rObj = new ArrayList<SqlObj>(4);
		SqlObj.buildListOfObjects(args, rObj);

		loadConfig();

		connect();

		for (final SqlObj obj : rObj)
		{
			useDatabase(obj.db);
			if (obj.isWild())
			{
				listRoutines(obj.schema, obj.obj);
			}
			else
			{
				final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(obj.lower + ".sql")));
				try
				{
					getRoutine(obj.schema, obj.obj, out);
				}
				finally
				{
					out.flush();
					out.close();
				}
				System.err.println("got " + obj.lower + ".sql");
				System.err.flush();
			}
		}
	}

	private void connect() throws ClassNotFoundException, SQLException
	{
		Class.forName(this.driver);
		this.connection = DriverManager.getConnection(this.url, this.username, this.password);
	}

	private void loadConfig() throws UnsupportedEncodingException, IOException
	{
		final Properties config = new Properties();
		final BufferedReader inConfig = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_FILE), "UTF-8"));
		config.load(inConfig);

		this.driver = confirm("driver", config);
		this.url = confirm("connection", config);
		System.err.println("connection = " + this.url);
		System.err.flush();
		this.username = confirm("username", config);
		System.err.println("username = " + this.username);
		System.err.flush();
		this.password = confirm("password", config);
	}

	private static String confirm(final String prop, final Properties config)
	{
		final String v = config.getProperty(prop);
		if (v == null || v.isEmpty())
		{
			return ask(prop);
		}

		return v;
	}

	private static String ask(final String prop)
	{
		final Console con = System.console();
		if (con == null)
		{
			return "";
		}

		final String answer;
		if (prop.equalsIgnoreCase("password"))
		{
			final char[] rc = con.readPassword("%s", prop + ": ");
			if (rc == null || rc.length == 0)
			{
				return "";
			}
			answer = new String(rc);
		}
		else
		{
			answer = con.readLine("%s", prop + ": ");
			if (answer == null)
			{
				return "";
			}
		}
		return answer;
	}

	private void listRoutines(String schema, String obj) throws SQLException
	{
		final String sql = "SELECT SPECIFIC_CATALOG, SPECIFIC_SCHEMA, SPECIFIC_NAME FROM INFORMATION_SCHEMA.ROUTINES WHERE SPECIFIC_SCHEMA LIKE ? AND SPECIFIC_NAME LIKE ?";

		final PreparedStatement st = connection.prepareStatement(sql);
		try
		{
			st.setString(1, schema);
			st.setString(2, obj);
			final ResultSet rs = st.executeQuery();
			while (rs.next())
			{
				final SqlObj resultObj = new SqlObj(rs.getString("SPECIFIC_CATALOG"), rs.getString("SPECIFIC_SCHEMA"),
					rs.getString("SPECIFIC_NAME"));
				System.out.println(resultObj.lower);
				System.out.flush();
			}
			rs.close();
		}
		finally
		{
			st.close();
		}
	}

	private void getRoutine(final String schema, final String obj, final BufferedWriter out) throws SQLException, IOException
	{
		final String scopedName = schema + "." + obj;

		final String sql = "SELECT OBJECT_DEFINITION (OBJECT_ID(?))";

		final PreparedStatement st = connection.prepareStatement(sql);
		try
		{
			st.setString(1, scopedName);
			final ResultSet rs = st.executeQuery();
			while (rs.next())
			{
				final Reader def = rs.getCharacterStream(1);
				if (def != null)
				{
					final BufferedReader in = new BufferedReader(def);
					for (String s = in.readLine(); s != null; s = in.readLine())
					{
						out.write(s);
						out.newLine();
						out.flush();
					}
					in.close();
				}
			}
			rs.close();
		}
		finally
		{
			st.close();
		}
	}

	private void useDatabase(final String db) throws SQLException
	{
		final String sql = "USE " + db;

		final PreparedStatement st = connection.prepareStatement(sql);
		try
		{
			st.executeUpdate();
		}
		finally
		{
			st.close();
		}
	}
}
