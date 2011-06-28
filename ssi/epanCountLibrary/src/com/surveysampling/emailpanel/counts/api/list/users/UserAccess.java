/*
 * Created on Jun 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.list.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Collection;
import java.util.Iterator;

import com.surveysampling.sql.DatabaseUtil;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Allows reading and updating of the UserSees table.
 * 
 * @author Chris Mosher
 */
public class UserAccess
{
    private final DatalessKeyAccess access = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
    private final Connection db;

    /**
     * @param db
     * @throws DatalessKeyAccessCreationException
     */
    public UserAccess(final Connection db) throws DatalessKeyAccessCreationException
    {
        this.db = db;
    }

    /**
     * @param rUser <code>Collection</code> of <code>User</code> objects, one
     * for each user that could possibly be chosen
     * @throws SQLException
     */
    public void getUsers(final Collection rUser) throws SQLException
    {
        final String sql =
            "SELECT * FROM UserList ORDER BY nameLast, nameFirst";

        synchronized (this.db)
        {
            PreparedStatement st = null;
            try
            {
                st = this.db.prepareStatement(sql);
                final ResultSet rs = st.executeQuery();
                while (rs.next())
                {
                    final DatalessKey keyUserID = access.createKeyFromResultSet(rs, rs.findColumn("keyUserID"));
                    final String nameLast = rs.getString("nameLast");
                    final String nameFirst = rs.getString("nameFirst");
                    final String dept = rs.getString("dept");
                    final boolean seen = rs.getBoolean("seen");

                    final User user = new User(keyUserID,nameLast,nameFirst,dept,seen);

                    rUser.add(user);
                }
            }
            finally
            {
                DatabaseUtil.closeStatementNoThrow(st);
            }
        }
    }

    /**
     * @param rUser
     * @throws SQLException
     */
    public void setSeenUsers(final Collection rUser) throws SQLException
    {
        synchronized (this.db)
        {
            Savepoint savepoint = null;
            try
            {
                savepoint = this.db.setSavepoint();

                updateSeenUsers(rUser);

                savepoint = null;
            }
            finally
            {
                if (savepoint == null)
                {
                    this.db.commit();
                }
                else
                {
                    try
                    {
                        this.db.rollback(savepoint);
                    }
                    catch (final Throwable e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void updateSeenUsers(final Collection rUser) throws SQLException
    {
        deleteSeenUsers();
   
        for (final Iterator iUser = rUser.iterator(); iUser.hasNext();)
        {
            final User user = (User)iUser.next();
   
            if (user.isSeen())
            {
                insertSeenUser(user.getKeyUser());
            }
        }
    }

    private void deleteSeenUsers() throws SQLException
    {
        final String sql =
            "DELETE FROM UserSees WHERE userID = (SELECT userID FROM CurrentUserID)";

        PreparedStatement st = null;
        try
        {
            st = this.db.prepareStatement(sql);
            st.executeUpdate();
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(st);
        }
    }

    private void insertSeenUser(final DatalessKey keyUser) throws SQLException
    {
        final String sql =
            "INSERT INTO UserSees(userID,seenUserID) " +
            "SELECT userID, ? FROM CurrentUserID";

        PreparedStatement st = null;
        try
        {
            st = this.db.prepareStatement(sql);
            DatalessKeyAccessFactory.getDatalessKeyAccess(keyUser).putKeyToPreparedStatement(keyUser,st,1);
            st.executeUpdate();
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(st);
        }
    }
}
