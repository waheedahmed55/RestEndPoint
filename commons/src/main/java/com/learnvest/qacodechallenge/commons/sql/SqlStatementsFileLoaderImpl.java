package com.learnvest.qacodechallenge.commons.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

public class SqlStatementsFileLoaderImpl implements SqlStatementsFileLoader, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(SqlStatementsFileLoaderImpl.class);

    private static final int READ_STREAM_CHARACTERS = 65536;

    private ApplicationContext ctx = null;

    private Map<String, String> statements = new HashMap<>();

    private SqlStatementsFileParser parser = new SqlStatementsFileParser();

    private String statementResourceLocation;

    @Override
    public String sql(String name) {
        String statement = statements.get(name);
        if (statement == null) {
            throw new SqlStatementsMissingException("No SQL statement with name " + name + " found in store");
        }
        return statement;
    }

    private String readStream(InputStream stream) throws IOException {
        String contents = "";
        char[] cbuf = new char[READ_STREAM_CHARACTERS];
        InputStreamReader reader = new InputStreamReader(stream, Charset.forName("UTF-8"));
        while (reader.ready()) {
            reader.read(cbuf);
            contents += new String(cbuf);
        }
        return contents;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
        try {
            Resource[] resources = this.ctx.getResources(statementResourceLocation);
            for (Resource r : resources) {
                try {
                    LOG.info("Loading sql statements from {}", r.getFilename());
                    String fileContents = readStream(r.getInputStream());
                    Map<String, String> sqlStatements = parser.parse(fileContents);

                    if (LOG.isDebugEnabled()) {
                        for (String name : sqlStatements.keySet()) {
                            LOG.debug("Statement {} : {}", name, sqlStatements.get(name));
                        }
                    }

                    this.statements.putAll(sqlStatements);
                } catch (IOException e) {
                    throw new SqlStatementsFileResourceException("Error while reading sql resource file " + r.getFilename(), e);
                } catch (SqlStatementsFileParseException e) {
                    throw new SqlStatementsFileResourceException("Error parsing sql resource file " + r.getFilename(), e);
                }
            }
        } catch (IOException e) {
            throw new SqlStatementsFileResourceException("Error locating sql resource files in classpath", e);
        }
    }

    @Override
    public String getStatementResourceLocation() {
        return statementResourceLocation;
    }

    @Override
    public void setStatementResourceLocation(String statementResourceLocation) {
        this.statementResourceLocation = statementResourceLocation;
    }

}
