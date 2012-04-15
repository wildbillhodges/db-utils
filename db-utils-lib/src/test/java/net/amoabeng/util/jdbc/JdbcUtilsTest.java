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

import org.testng.annotations.Test;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;


/**
 * @author Manuel Amoabeng
 */
public class JdbcUtilsTest extends Tests {


    @Test
    public void testGetColumnLabels() throws SQLException {
        List<String> columnLabels = JdbcUtils.getColumnLabels(
                connection
                        .createStatement()
                        .executeQuery("select * from test_table")
                        .getMetaData());
        assertTrue(columnLabels.contains("ID"));
        assertTrue(columnLabels.contains("FOO"));
        assertTrue(columnLabels.contains("BAR"));
        assertTrue(columnLabels.contains("BAZ"));
    }

    @Test
    public void testGetJavaTypes() throws SQLException, ClassNotFoundException {
        Map<String, Class<?>> javaTypes = JdbcUtils.getJavaTypes(
                connection
                        .createStatement()
                        .executeQuery("select * from test_table")
                        .getMetaData());
        assertEquals(Integer.class, javaTypes.get("ID"));
        assertEquals(String.class, javaTypes.get("FOO"));
        assertEquals(String.class, javaTypes.get("BAR"));
        assertEquals(String.class, javaTypes.get("BAZ"));
    }

    @Test
    public void testReadCurrent() throws SQLException {
        ResultSet resultSet = connection
                .createStatement()
                .executeQuery("select * from test_table order by id asc");

        resultSet.next();
        Map<String, Object> data = JdbcUtils.readCurrent(resultSet, asList("ID", "BAR"));
        assertTrue(data.size() == 3);
        assertEquals(1, data.get(JdbcUtils.ROW_NUMBER));
        assertEquals(1, data.get("ID"));
        assertEquals("1_BAR", data.get("BAR"));

        resultSet.next();
        data = JdbcUtils.readCurrent(resultSet, asList("FOO", "BAZ"));
        assertTrue(data.size() == 3);
        assertEquals(2, data.get(JdbcUtils.ROW_NUMBER));
        assertEquals("2_FOO", data.get("FOO"));
        assertEquals("2_BAZ", data.get("BAZ"));
    }
}
