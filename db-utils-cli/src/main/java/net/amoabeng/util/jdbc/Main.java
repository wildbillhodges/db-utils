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

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.amoabeng.util.cli.Command;
import net.amoabeng.util.cli.FileContentConverter;

import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

import static net.amoabeng.util.cli.CliUtils.run;
import static net.amoabeng.util.jdbc.JdbcUtils.getConnection;

/**
 * @author Manuel Amoabeng
 */
public class Main {

    public static void main(String... args) {
        run(args, QueryDiffCommand.class);
    }

    @Parameters(
            commandNames = "query-diff",
            commandDescription = "Compares two SQL queries")
    private static class QueryDiffCommand implements Command {

        @Parameter(
                names = {"-c", "--connection"},
                description = "A properties file containing the connection data",
                required = true)
        private Properties connectionProperties;

        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        @Parameter(
                names = {"-q", "--queries"},
                description = "Two files containing the queries to compare",
                arity = 2,
                required = true,
                converter = FileContentConverter.class)
        private List<String> queries;

        @Override
        public void execute(Object mainOptions) throws Exception {
            try (Connection connection = getConnection(connectionProperties);
                 ResultSet thisResult = connection.createStatement().executeQuery(queries.get(0));
                 ResultSet thatResult = connection.createStatement().executeQuery(queries.get(1))) {

                QueryDiff queryDiff = new QueryDiff(thisResult, thatResult);
                queryDiff.printTo(new OutputStreamWriter(System.out));
            }
        }
    }
}
