package com.surveysampling.testwarjee6;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class TestServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		System.out.println("=======================================================> starting hit");
		System.out.flush();
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		try
		{
			final DataSource db = getDataSourceFromContext(new InitialContext(), "java:MSSQLDS-DKR");
			final Connection con = db.getConnection();
			final CallableStatement st = con.prepareCall("{ CALL GET_IS_CELL(?,?) }");
			st.registerOutParameter(2, java.sql.Types.INTEGER);
			for (int x = 0; x < 50000; ++x)
			{
				st.setInt(1, x);
				st.execute();
				final Integer v = st.getInt(2);
				if (!map.containsKey(v))
				{
					map.put(v, 0);
				}
				Integer exist = map.get(v);
				++exist;
				map.put(v, exist);
			}
			st.close();
			con.close();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			System.err.flush();
		}
		System.out.println("=======================================================> hit complete");
		System.out.flush();

		final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
		out.write(map.toString());
		out.flush();
		out.close();
	}

	/**
	 * Gets the DataSource for accessing the database; used for both retrieving
	 * sample information and updating indexes.
	 * 
	 * @return the data source to use
	 * @throws NamingException
	 *             if the data source cannot be found in the JNDI tree
	 */
	static DataSource getDataSourceFromContext(final Context jndiCtx, final String name) throws NamingException
	{
		try
		{
			final DataSource dataSource = (DataSource) jndiCtx.lookup(name);

			/*
			 * Not sure if this could ever be null, but we check for it just in
			 * case.
			 */
			if (dataSource == null)
			{
				throw new IllegalStateException("Context.Lookup returned null");
			}
			return dataSource;
		}
		catch (final Throwable e)
		{
			/*
			 * Catch any exception just so we can add some more information to
			 * it, then re-throw.
			 */
			final NamingException wrap = new NamingException("Cannot find data source \"" + name
				+ "\", so pickerIndexUpdate process WILL NOT RUN. Make sure pickerIndexUpdate-PROD-ds.xml "
				+ "in JBoss server \"deploy\" or \"farm\" directory is correct.");
			wrap.initCause(e);
			throw wrap;
		}
	}
}
