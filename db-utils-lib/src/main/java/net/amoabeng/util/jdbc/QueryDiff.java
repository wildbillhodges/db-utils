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

import net.amoabeng.util.CompareUtils;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;
import static net.amoabeng.util.CollectionUtils.join;
import static net.amoabeng.util.jdbc.JdbcUtils.getColumnMetaData;

/**
 * @author Manuel Amoabeng
 */
public class QueryDiff {

    private static final Object NULL = new Object() {
        public String toString() {
            return "<empty>";
        }
    };

    private List<ColumnMetaData> thisColumns;
    private List<ColumnMetaData> thatColumns;
    private List<ColumnMetaData> commonColumns;

    private List<Object[]> records = new ArrayList<>();

    QueryDiff(ResultSet thisResult, ResultSet thatResult) throws ClassNotFoundException, SQLException {
        thisColumns = getColumnMetaData(thisResult.getMetaData());
        thatColumns = getColumnMetaData(thatResult.getMetaData());
        commonColumns = new ArrayList<>(thisColumns);
        commonColumns.retainAll(thatColumns);
        thisColumns.removeAll(commonColumns);
        thatColumns.removeAll(commonColumns);

        boolean thisNext, thatNext;
        int rowNum = 0;
        int l = commonColumns.size();
        Object[] emptyRecord = emptyRecord(l);
        Object[] nextRecord = emptyRecord(l * 2 + 1);
        while ((thisNext = thisResult.next()) | (thatNext = thatResult.next())) {
            rowNum++;
            Object[] thisRecord;
            Object[] thatRecord;
            if (thisNext) {
                thisRecord = readCurrent(thisResult);
            } else {
                thisRecord = emptyRecord;
            }
            if (thatNext) {
                thatRecord = readCurrent(thatResult);
            } else {
                thatRecord = emptyRecord;
            }
            boolean diffFound = false;
            for (int i = 0; i < l; i++) {
                if (!CompareUtils.isEqual(thisRecord[i], thatRecord[i])) {
                    diffFound = true;
                    nextRecord[i + 1] = thisRecord[i];
                    nextRecord[i + l + 1] = thatRecord[i];
                }
            }
            if (diffFound) {
                records.add(nextRecord);
                nextRecord[0] = rowNum;
                nextRecord = emptyRecord(l * 2 + 1);
            }
        }
    }

    public boolean isEqual() {
        return thisColumns.isEmpty() &&
                thatColumns.isEmpty() &&
                records.isEmpty();
    }

    Object[] emptyRecord(int length) {
        Object[] empty = new Object[length];
        for (int i = 0; i < length; i++) {
            empty[i] = NULL;
        }
        return empty;
    }

    Object[] readCurrent(ResultSet resultSet) throws SQLException {
        Object[] record = new Object[commonColumns.size()];
        int i = 0;
        for (ColumnMetaData column : commonColumns) {
            record[i++] = resultSet.getObject(column.getColumnIndex());
        }
        return record;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        printTo(sw);
        return sw.toString();
    }

    public void printTo(Writer out) {
        PrintWriter pw = out instanceof PrintWriter ? (PrintWriter) out : new PrintWriter(out);
        pw
                .append("columns: [\n\tall: [\n\t\t")
                .append(join(",\n\t\t", commonColumns))
                .append("\n\t],\n\tthisResult: [\n\t\t")
                .append(join(",\n\t\t", thisColumns))
                .append("\n\t],\n\tthatResult: [\n\t\t")
                .append(join(",\n\t\t", thatColumns))
                .append("\n\t]\n]\ndata: [");

        int l = commonColumns.size();
        String sep = "\n\t";
        for (Object[] record : records) {
            pw
                    .append(sep)
                    .append("[")
                    .append(valueOf(record[0]));
            sep = ",\n\t";
            for (int i = 1; i < l; i++) {
                pw
                        .append(", (")
                        .append(valueOf(record[i]))
                        .append(" | ")
                        .append(valueOf(record[i + l])).append(")");
            }
            pw.append("]");
        }
        pw.append("]\n");
    }
}
                               