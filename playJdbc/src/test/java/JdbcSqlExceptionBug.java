import java.sql.SQLException;

import org.junit.Test;

public class JdbcSqlExceptionBug
{
	@Test(timeout=3000)
	public void exploit()
	{
		final SQLException e = new SQLException();
		e.setNextException(e);
		e.setNextException(e);
	}
}
