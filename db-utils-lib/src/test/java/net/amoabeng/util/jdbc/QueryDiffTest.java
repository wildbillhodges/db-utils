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

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Manuel Amoabeng
 */
public class QueryDiffTest extends Tests {

    @Test
    public void testQueryDiff1() throws SQLException, ClassNotFoundException {
        ResultSet thisResult = connection.createStatement().executeQuery("select * from test_table");
        ResultSet thatResult = connection.createStatement().executeQuery("select * from test_table");
        QueryDiff queryDiff = new QueryDiff(thisResult, thatResult);
//        System.out.println(queryDiff);
//        System.out.println("-------------------");
        assertTrue(queryDiff.isEqual());
    }

    @Test
    public void testQueryDiff2() throws SQLException, ClassNotFoundException {
        ResultSet thisResult = connection.createStatement().executeQuery("select * from test_table order by id asc");
        ResultSet thatResult = connection.createStatement().executeQuery("select * from test_table order by id desc");
        QueryDiff queryDiff = new QueryDiff(thisResult, thatResult);
//        System.out.println(queryDiff);
//        System.out.println("-------------------");
        assertFalse(queryDiff.isEqual());
    }

    @Test
    public void testQueryDiff3() throws SQLException, ClassNotFoundException {
        ResultSet thisResult = connection.createStatement().executeQuery("select id, foo from test_table");
        ResultSet thatResult = connection.createStatement().executeQuery("select id, bar from test_table");
        QueryDiff queryDiff = new QueryDiff(thisResult, thatResult);
//        System.out.println(queryDiff);
//        System.out.println("-------------------");
        assertFalse(queryDiff.isEqual());
    }
}
