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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author Manuel Amoabeng
 */
public class ColumnMetaData {

    private final int columnIndex;
    private final String columnLabel;
    private final String sqlType;

    ColumnMetaData(int column, ResultSetMetaData metaData) throws SQLException {
        columnLabel = metaData.getColumnLabel(column);
        sqlType = metaData.getColumnTypeName(column);
        columnIndex = column;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getColumnLabel() {
        return columnLabel;
    }

    public String getSqlType() {
        return sqlType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnMetaData that = (ColumnMetaData) o;

        if (columnIndex != that.columnIndex) return false;
        if (columnLabel != null ? !columnLabel.equals(that.columnLabel) : that.columnLabel != null) return false;
        return !(sqlType != null ? !sqlType.equals(that.sqlType) : that.sqlType != null);

    }

    @Override
    public int hashCode() {
        int result = columnIndex;
        result = 31 * result + (columnLabel != null ? columnLabel.hashCode() : 0);
        result = 31 * result + (sqlType != null ? sqlType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("[index: ")
                .append(columnIndex)
                .append(", label: ")
                .append(columnLabel)
                .append(", sqlType: ")
                .append(sqlType)
                .append("]");

        return sb.toString();
    }
}
