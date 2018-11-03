package com.surveysampling.apps.QueryRunner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class QueryRunner
{
	private static final List<QueryExecInfo> queries = new ArrayList<QueryExecInfo>(1024);

	private static void execute(final PreparedStatement statement) throws SQLException
	{
		/*
		 * Do event-driven processing of the result set(s) and the "warning"
		 * messages from the SQL statement execution.
		 */
		QueryExecInfo cur = new QueryExecInfo();

		final SqlMessageStream msgs = new SqlMessageStream(new WarningGenerator(statement));

		ResultSet resultSet = statement.executeQuery();
		String msg = msgs.get();
		while (resultSet != null || !msg.isEmpty())
		{
			final MessageType tMsg = MessageType.guessMessageType(msg);

			/*
			 * the order of the cases listed below is the order they come back
			 * from SQL Server
			 */
			switch (tMsg)
			{
				case PARSE_TIMES:
				{
					cur.addParseTimes(msg);
				}
				break;
				/* the query's record set is returned here */
				case OTHER:
				{
					cur.addOther(msg);
				}
				break;
				case READ_COUNTS:
				{
					cur.addReadCounts(msg);
				}
				break;
				/* the query plan's record set is returned here */
				case EXECUTION_TIMES:
				{
					cur.addExecutionTimes(msg);
					/*
					 * the Execution Times message is the last message SQL
					 * Server sends back for a query
					 */
					queries.add(cur);
					cur = new QueryExecInfo();
				}
				break;

				case END_OF_MESSAGES:
				{
					cur.addResultSet(resultSet);
					if (statement.getMoreResults())
					{
						resultSet = statement.getResultSet();
					}
					else
					{
						resultSet = null;
					}
				}
				break;
			}

			msg = msgs.get();
		}
	}


	private static void dumpAll()
	{
		for (final QueryExecInfo query : queries)
		{
			query.dump(System.out);
		}
	}

	private static void dumpWorst()
	{
		final SortedSet<QueryExecInfo> byElapsed = new TreeSet<QueryExecInfo>(new Comparator<QueryExecInfo>()
		{
			public int compare(QueryExecInfo o1, QueryExecInfo o2)
			{
				if (o1.getElapsed() < o2.getElapsed())
				{
					return +1;
				}
				if (o2.getElapsed() < o1.getElapsed())
				{
					return -1;
				}
				return 0;
			}
		});

		final SortedSet<QueryExecInfo> byCpu = new TreeSet<QueryExecInfo>(new Comparator<QueryExecInfo>()
		{
			public int compare(QueryExecInfo o1, QueryExecInfo o2)
			{
				if (o1.getCpu() < o2.getCpu())
				{
					return +1;
				}
				if (o2.getCpu() < o1.getCpu())
				{
					return -1;
				}
				return 0;
			}
		});

		final SortedSet<QueryExecInfo> byReads = new TreeSet<QueryExecInfo>(new Comparator<QueryExecInfo>()
		{
			public int compare(QueryExecInfo o1, QueryExecInfo o2)
			{
				if (o1.getReads() < o2.getReads())
				{
					return +1;
				}
				if (o2.getReads() < o1.getReads())
				{
					return -1;
				}
				return 0;
			}
		});


		for (final QueryExecInfo query : queries)
		{
			byElapsed.add(query);
			byCpu.add(query);
			byReads.add(query);
		}

		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("BY ELAPSED TIME");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		for (final QueryExecInfo query : byElapsed)
		{
//			if (query.getElapsed() >= 100)
//			{
				query.dump(System.out);
//			}
		}



		System.out.println();
		System.out.println();
		System.out.println();



		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("BY CPU TIME");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		for (final QueryExecInfo query : byCpu)
		{
//			if (query.getCpu() >= 20)
//			{
				query.dump(System.out);
//			}
		}



		System.out.println();
		System.out.println();
		System.out.println();



		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("BY LOGICAL READS");
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		for (final QueryExecInfo query : byReads)
		{
//			if (query.getReads() >= 100)
//			{
				query.dump(System.out);
//			}
		}

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("total count of executed statements: " + queries.size());
	}



	public static void main(final String... args) throws SQLException, ClassNotFoundException, IOException
	{
		final String PROD_DB_URL = "jdbc:jtds:sqlserver://sqlcls.surveysampling.com/ssi;instance=nebu";
		final String QA_DB_URL = "jdbc:jtds:sqlserver://ctsqla.surveysampling.com/ssi;instance=nebu";

		Class.forName("net.sourceforge.jtds.jdbc.Driver");
//		final Connection connection = DriverManager.getConnection(PROD_DB_URL, "db_app_DK_routing", "dkrout_39_kensucks");
		final Connection connection = DriverManager.getConnection(QA_DB_URL, "sa", "!m@d31T#$");

//		final String query = "exec panel.dbo.get_projects_to_show_in_picker 1125495859, 7, 1, -1, 0, 1"; // Ken Roe 
//        final String query =
//            "SELECT "+
//            "       M.CELL_ORDER, "+
//            "       V.VALUE AS STATUS, "+
//            "       COUNT(*) AS COUNT "+
//            "FROM "+
//            "       PANEL.DBO.PROJECT_MEMBERS AS M WITH (NOLOCK) INNER JOIN "+
//            "       PANEL.DBO.PROJECTS AS P WITH (NOLOCK) ON (P.PROJECT_ID = M.PROJECT_ID) INNER JOIN "+
//            "       PANEL.DBO.FVALUES8 AS V WITH (NOLOCK) ON (V.FEATURE_ID = P.QUEST_FEATURE_ID AND V.ENTITY_ID = M.ENTITY_ID) "+
//            "WHERE "+
//            "       M.SAMPLE_VERSION_ID = 1565737 AND "+
//            "       V.STARTED_AT BETWEEN GETDATE() AND DATEADD(DD,-5,GETDATE()) AND "+
//            "       V.VALUE > 0 "+
//            "GROUP BY "+
//            "       M.CELL_ORDER, "+
//            "       V.VALUE "+
//            "";
// 		final String query = "exec panel.dbo.get_projects_to_show_in_picker 1132601240, 30, 1, -1, 0, 1"; // Matthew Butcaris
//		final String query = "exec panel.dbo.get_projects_to_show_in_picker 1079308784, 27, 0, -1";
//		final String query = "exec panel.dbo.get_projects_to_show_in_picker 1131985987, 30, 0, -1, 0, 1"; // A SLOW ONE
//		final String query = "exec panel.dbo.get_projects_to_show_in_picker 1100167392, 7, 0, -1, 0, 1"; // ANOTHER SLOW ONE
//		final String query = "exec panel.dbo.get_projects_to_show_in_picker 1132217874, 30, 0, -1, 0, 1"; // ANOTHER SLOW ONE
//		final String query = "exec panel.dbo.get_projects_to_show_in_picker 1144759015, 27, 0, 1319725"; // with preferred sample
//		final String query = "exec panel.dbo.get_projects_to_show_in_picker 1106422241, 27, 0, -1, 0, 1";
//		final String query = "delete from panel.dbo.picker_project_member_cache where entity_id=1079442393; EXECUTE PANEL.DBO.GET_PROJECTS_TO_SHOW_IN_PICKER_TEST 1079442393,30,0, -1, 0, 1";
//		final String query =
//"DECLARE @SAMPLE_VERSION_ID INTEGER = 1512891; "+
//"DECLARE @ENTITY_ID INTEGER = 1131901697; "+
//"CREATE TABLE #ENTITY_CELL_CACHE ( "+
//"  ENTITY_ID INT NOT NULL, "+
//"  SQL_EXPRESSION NVARCHAR(4000) NOT NULL, "+
//"  FOUND_SUITABLE_CELL TINYINT NOT NULL, "+
//"  PRIMARY KEY (ENTITY_ID, SQL_EXPRESSION) "+
//"); "+
//"DECLARE @CELL_ORDER INTEGER = -99; "+
//"EXECUTE @cell_order = PANEL.dbo.get_sample_quotacell_for_entity @sample_version_id, @entity_id "+
//"DROP TABLE #ENTITY_CELL_CACHE; "+
//"";

		final String query = "EXECUTE panel.dbo.add_picker_refinement_data ?,?,? SELECT 1";
		final String sql = "set nocount on set statistics profile on set statistics io on set statistics time on " + query;
		final PreparedStatement statement = connection.prepareStatement(sql);

		statement.setInt(1, 1152902876);
		statement.setString(2, "<rfs><rf><fid>17</fid><fv>11</fv><sid>1752458</sid><svid>2076500</svid><co>1</co><sp>30</sp></rf></rfs>");
		statement.setInt(3, 0);
		execute(statement);
		dumpWorst();
//		dumpAll();

		statement.close();
		connection.close();
	}
}
