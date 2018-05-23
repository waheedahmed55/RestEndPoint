package com.learnvest.qacodechallenge.commons.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlStatementsFileParser {

    private Pattern fileFormat;

    public static final String STMT_SEPARATOR = "--STATEMENT";

    /**
     * Parse SQL statement file contents. Syntax:
     *
     * --STATEMENT statementName optional ignored text SQL Statement --STATEMENT statement2Name more optional ignored
     * text SQL Statement 2
     *
     * The parser loads SQL statements into a map, with the key given in --STATEMENT construct. There must be no
     * whitespace between the beginning of the line and --STATEMENT to be recognized. The text on the lines following
     * the --STATEMENT until the next --STATEMENT or end of file will be stored in the map value. The parser will throw
     * an exception if there are duplicate statement names in the file.
     */
    public SqlStatementsFileParser() {
        fileFormat = Pattern.compile("^--STATEMENT\\s+(\\w+).*?[\\n\\r](.+?)(?=^--STATEMENT|\\z)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    }

    public Map<String, String> parse(String fileContents) throws SqlStatementsFileParseException {
        Map<String, String> statements = new HashMap<>();
        Matcher m = fileFormat.matcher(fileContents);
        while (m.find()) {
            String key = m.group(1);
            String sql = m.group(2);
            if (statements.containsKey(key)) {
                throw new SqlStatementsFileParseException("SQL statement named " + key + " already defined earlier or in another file");
            }
            statements.put(key, formatSql(sql.trim()));
        }
        return statements;
    }

    private String formatSql(String sql) {
        // replace all the other comments starting with --. Remove carriage returns.
        String formatted = sql.replaceAll("(--.*$|\\/\\*[\\s\\S]*?\\*\\/)", "").replaceAll("\n", " ").replaceAll("\r", " ");
        return formatted;
    }

}
