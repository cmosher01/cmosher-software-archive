import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SampleOutputParam
{
	private static final String QA_DB_URL = "jdbc:jtds:sqlserver://ctclsnebu.surveysampling.com/panel;instance=nebu";

	public static void main(String[] args) throws ClassNotFoundException, SQLException
	{
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		xxx(72229);
	}

	public static void xxx(final int projectId) throws SQLException
	{
		/*
			CREATE PROCEDURE dbo.get_project_level_quota_counters (
			  @project_id INT,
			  @limit INT OUTPUT,
			  @completed INT OUTPUT,
			  @is_auto_close BOOLEAN OUTPUT,
			  @log TINYINT = 0
			)
		*/
		final String SQL = "{CALL dbo.get_project_level_quota_counters_TEST(?,?,?,?)}";

		final Connection connection = DriverManager.getConnection(QA_DB_URL, "sa", "!m@d31T#$");
		CallableStatement ps = null;
		try
		{
			ps = connection.prepareCall(SQL);
			int i = 1;
			ps.setInt(i++, projectId);
			final int iLimit = i++;
			ps.registerOutParameter(iLimit, java.sql.Types.INTEGER);
			final int iCompleted = i++;
			ps.registerOutParameter(iCompleted, java.sql.Types.INTEGER);
			final int iIsAutoClose = i++;
			ps.registerOutParameter(iIsAutoClose, java.sql.Types.INTEGER);

			ps.execute();

			System.out.println(ps.getInt(iLimit));
			System.out.println(ps.getInt(iCompleted));
			System.out.println(ps.getInt(iIsAutoClose) == 1);
//			ptqr.setTotalQuota(rs.getInt(iLimit));
//			ptqr.setTotalQuotaSumm(rs.getInt(iCompleted));
//			ptqr.setTotalIsAutoClose(rs.getInt(iIsAutoClose) == 1);
		}
		finally
		{
			try
			{
				ps.close();
			}
			catch (Exception e)
			{
			}
		}
	}
}
