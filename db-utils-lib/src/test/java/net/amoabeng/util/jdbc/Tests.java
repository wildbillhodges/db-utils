/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package net.amoabeng.util.jdbc;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Manuel Amoabeng
 */
public class Tests {


    protected Properties hsqlProperties;
    protected Connection connection;


    @BeforeClass
    public void beforeClass() throws ClassNotFoundException, SQLException {
        hsqlProperties = new Properties();
        hsqlProperties.setProperty(JdbcUtils.DRIVER_NAME, "org.hsqldb.jdbc.JDBCDriver");
        hsqlProperties.setProperty(JdbcUtils.JDBC_URL, "jdbc:hsqldb:mem:tests");
        hsqlProperties.setProperty("user", "SA");
        hsqlProperties.setProperty("password", "");

        try (Connection connection = JdbcUtils.getConnection(hsqlProperties)) {
            connection.createStatement().execute(
                    "create table test_table (" +
                            "ID integer primary key, " +
                            "FOO varchar(100), " +
                            "BAR varchar(100), " +
                            "BAZ varchar(100))");

            createRecords(connection, 1, 500000);
        }
    }

    @AfterClass
    public void afterClass() throws ClassNotFoundException, SQLException {
        try (Connection connection = JdbcUtils.getConnection(hsqlProperties)) {
            connection.createStatement().execute("drop table test_table");
        }
    }


    protected void createRecords(Connection connection, int startId, int count) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "insert into test_table (id, foo, bar, baz) values (?,?,?,?)");
        for (int i = startId, l = startId + count; i < l; i++) {
            preparedStatement.setInt(1, i);
            preparedStatement.setString(2, i + "_FOO");
            preparedStatement.setString(3, i + "_BAR");
            preparedStatement.setString(4, i + "_BAZ");
            preparedStatement.execute();
        }
        connection.commit();
    }

    @BeforeMethod
    public void beforeMethod() throws ClassNotFoundException, SQLException {
        connection = JdbcUtils.getConnection(hsqlProperties);
        assertTrue(connection.isValid(5));
        connection.setAutoCommit(false);
    }

    @AfterMethod
    public void afterMethod() throws SQLException {
        connection.close();
    }
}
