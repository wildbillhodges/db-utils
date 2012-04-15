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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static net.amoabeng.util.CollectionUtils.*;
import static net.amoabeng.util.jdbc.JdbcUtils.*;

/**
 * @author Manuel Amoabeng
 */
public class QueryDiff {

    private static final int THIS_RECORD_DATA = 0, THAT_RECORD_DATA = 1, COMMON_DATA = 2;

    private final Collection<String> thisColumnLabels;
    private final Collection<String> thatColumnLabels;
    private final Collection<String> commonColumnLabels;

    private final Map<String, Class<?>> thisJavaTypes;
    private final Map<String, Class<?>> thatJavaTypes;
    private final Map<String, Class<?>> commonJavaTypes;


    private final Map<Integer, List<Map<String, Object>>> diffRecords = new HashMap<>();

    private int thisCount;
    private int thatCount;


    QueryDiff(ResultSet thisResult, ResultSet thatResult) throws SQLException, ClassNotFoundException {

        requireNonNull(thisResult, "thisResult is null");
        requireNonNull(thatResult, "thatResult is null");

        ResultSetMetaData thisMetaData = thisResult.getMetaData();
        ResultSetMetaData thatMetaData = thatResult.getMetaData();

        List<String> allThisColumnLabels = getColumnLabels(thisMetaData);
        List<String> allThatColumnLabels = getColumnLabels(thatMetaData);
        commonColumnLabels = intersect(allThisColumnLabels, allThatColumnLabels);
        thisColumnLabels = difference(allThisColumnLabels, commonColumnLabels);
        thatColumnLabels = difference(allThatColumnLabels, commonColumnLabels);

        Map<String, Class<?>> allThisJavaTypes = getJavaTypes(thisMetaData);
        Map<String, Class<?>> allThatJavaTypes = getJavaTypes(thatMetaData);
        commonJavaTypes = intersect(allThisJavaTypes, allThatJavaTypes);
        thisJavaTypes = difference(allThisJavaTypes, commonJavaTypes);
        thatJavaTypes = difference(allThatJavaTypes, commonJavaTypes);

        Collection<String> commonColumns = new ArrayList<>();
        for (String commonColumnLabel : commonColumnLabels) {
            if (commonJavaTypes.containsKey(commonColumnLabel)) {
                commonColumns.add(commonColumnLabel);
            }
        }

        boolean thisNext, thatNext;
        int rowNum = 0;
        while ((thisNext = thisResult.next()) | (thatNext = thatResult.next())) {
            rowNum++;
            Map<String, Object> thisRecord = emptyMap();
            Map<String, Object> thatRecord = emptyMap();
            if (thisNext) {
                thisCount++;
                thisRecord = readCurrent(thisResult, commonColumns);
            }
            if (thatNext) {
                thatCount++;
                thatRecord = readCurrent(thatResult, commonColumns);
            }
            Map<String, Object> commonData = intersect(thisRecord, thatRecord);
            thisRecord = difference(thisRecord, commonData);
            thatRecord = difference(thatRecord, commonData);
            if (!thisRecord.isEmpty() || !thatRecord.isEmpty()) {
                List<Map<String, Object>> diffRecord = new ArrayList<>(3);
                diffRecord.add(thisRecord);
                diffRecord.add(thatRecord);
                diffRecord.add(commonData);
                diffRecords.put(rowNum, diffRecord);
            }
        }
    }

    public boolean isEqual() {
        return thisColumnLabels.isEmpty() &&
                thatColumnLabels.isEmpty() &&
                thisJavaTypes.isEmpty() &&
                thatJavaTypes.isEmpty() &&
                diffRecords.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendColumnLabels(sb);
        sb.append(",\n");
        appendJavaTypes(sb);
        sb.append(",\n");
        appendCount(sb);
        sb.append(",\n");
        appendDataDiff(sb);
        return sb.toString();
    }

    private void appendColumnLabels(StringBuilder sb) {
        if (thisColumnLabels.isEmpty() && thatColumnLabels.isEmpty()) {
            sb.append("column labels: match");
        } else {
            sb
                    .append("column labels: common: [ ")
                    .append(join(", ", commonColumnLabels))
                    .append(" ], thisResult: [ ")
                    .append(join(", ", thisColumnLabels))
                    .append(" ], thatResult: [ ")
                    .append(join(", ", thatColumnLabels))
                    .append(" ]");
        }
    }

    private void appendJavaTypes(StringBuilder sb) {
        if (thisJavaTypes.isEmpty() && thatJavaTypes.isEmpty()) {
            sb.append("java types: match");
        } else {
            sb
                    .append("java types: common: [ ")
                    .append(join(", ", ": ", commonJavaTypes))
                    .append(" ], thisResult: [ ")
                    .append(join(", ", ": ", thisJavaTypes))
                    .append(" ], thatResult: [ ")
                    .append(join(", ", ": ", thatJavaTypes))
                    .append(" ]");
        }
    }

    private void appendCount(StringBuilder sb) {
        if (thisCount == thatCount) {
            sb
                    .append("count: ")
                    .append(thisCount);
        } else {
            sb
                    .append("count: [ thisResult: ")
                    .append(thisCount)
                    .append(", thatResult: ")
                    .append(thatCount)
                    .append(" ]");
        }
    }

    private void appendDataDiff(StringBuilder sb) {
        if (diffRecords.isEmpty()) {
            sb.append("data: match");
        } else {
            sb.append("data: [\n");
            String sep = "\t";
            for (Map.Entry<Integer, List<Map<String, Object>>> diffRecord : diffRecords.entrySet()) {
                List<Map<String, Object>> value = diffRecord.getValue();
                sb
                        .append(sep)
                        .append("row ")
                        .append(diffRecord.getKey())
                        .append(": common: [ ")
                        .append(join(", ", ": ", value.get(COMMON_DATA)))
                        .append(" ], thisResult: [ ")
                        .append(join(", ", ": ", value.get(THIS_RECORD_DATA)))
                        .append(" ], thatResult: [ ")
                        .append(join(", ", ": ", value.get(THAT_RECORD_DATA)))
                        .append(" ]");

                sep = ",\n";
            }
            sb.append("\n]");
        }
    }
}



