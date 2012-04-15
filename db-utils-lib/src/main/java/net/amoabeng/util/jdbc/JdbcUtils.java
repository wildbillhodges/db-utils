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

import java.sql.*;
import java.util.*;

/**
 * @author Manuel Amoabeng
 */
public class JdbcUtils {


//    static final String ROW_NUMBER = "resultSet.rowNumber";

    static final String DRIVER_NAME = "jdbc.driver", JDBC_URL = "jdbc.url";

    static Connection getConnection(Properties properties) throws ClassNotFoundException, SQLException {
        Class.forName((String) properties.get(DRIVER_NAME));
        return DriverManager.getConnection((String) properties.get(JDBC_URL), properties);
    }

    static List<ColumnMetaData> getColumnMetaData(ResultSetMetaData metaData) throws SQLException, ClassNotFoundException {
        List<ColumnMetaData> columns = new ArrayList<>();
        for (int i = 1, l = metaData.getColumnCount(); i <= l; i++) {
            columns.add(new ColumnMetaData(i, metaData));
        }
        return columns;
    }


//    static List<String> getColumnLabels(ResultSetMetaData metaData) throws SQLException {
//        List<String> labels = new ArrayList<>();
//        for (int i = 1, l = metaData.getColumnCount(); i <= l; i++) {
//            labels.add(metaData.getColumnLabel(i));
//        }
//        return labels;
//    }

//    static Map<String, Class<?>> getJavaTypes(ResultSetMetaData metaData) throws SQLException, ClassNotFoundException {
//        Map<String, Class<?>> types = new HashMap<>();
//        for (int i = 1, l = metaData.getColumnCount(); i <= l; i++) {
//            types.put(metaData.getColumnLabel(i), Class.forName(metaData.getColumnClassName(i)));
//        }
//        return types;
//    }

//    static Map<String, Object> readCurrent(ResultSet resultSet, Collection<String> columnLabels) throws SQLException {
//        Map<String, Object> result = new LinkedHashMap<>();
//        result.put(ROW_NUMBER, resultSet.getRow());
//        for (String columnLabel : columnLabels) {
//            result.put(columnLabel, resultSet.getObject(columnLabel));
//        }
//        return result;
//    }

}
