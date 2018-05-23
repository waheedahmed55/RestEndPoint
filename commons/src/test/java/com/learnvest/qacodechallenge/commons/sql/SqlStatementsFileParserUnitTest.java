package com.learnvest.qacodechallenge.commons.sql;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SqlStatementsFileParserUnitTest {

    private static SqlStatementsFileParser parser;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        parser = new SqlStatementsFileParser();
    }

    @Test
    public void testParseEmpty() throws SqlStatementsFileParseException {
        Map<? extends String, ? extends String> statements = parser.parse("");
        Assert.assertTrue(statements.isEmpty());
    }

    @Test
    public void testParseOne() throws SqlStatementsFileParseException {
        Map<? extends String, ? extends String> statements = parser.parse("--STATEMENT test1\nselect * from user;");
        Assert.assertTrue(statements.size() == 1);
        Assert.assertTrue(statements.keySet().contains("test1"));
        Assert.assertEquals("select * from user;", statements.get("test1"));
    }

    @Test
    public void testParseTwo() throws SqlStatementsFileParseException {
        Map<? extends String, ? extends String> statements = parser.parse(
                "--STATEMENT test1\nselect * from user;\n"
                        + "--STATEMENT test2\nselect * from transaction;");
        Assert.assertTrue(statements.size() == 2);
        Assert.assertTrue(statements.keySet().contains("test1"));
        Assert.assertEquals("select * from user;", statements.get("test1"));
        Assert.assertTrue(statements.keySet().contains("test2"));
        Assert.assertEquals("select * from transaction;", statements.get("test2"));
    }

    @Test
    public void testParseMultiLine() throws SqlStatementsFileParseException {
        Map<? extends String, ? extends String> statements = parser.parse(
                "--STATEMENT test1\nselect *\nfrom user;\n"
                        + "--STATEMENT test2\nselect *\nfrom transaction;");
        Assert.assertTrue(statements.size() == 2);
        Assert.assertTrue(statements.keySet().contains("test1"));
        Assert.assertEquals("select * from user;", statements.get("test1"));
        Assert.assertTrue(statements.keySet().contains("test2"));
        Assert.assertEquals("select * from transaction;", statements.get("test2"));
    }

    @Test(expected = SqlStatementsFileParseException.class)
    public void testRepeatedStatementError() throws SqlStatementsFileParseException {
        @SuppressWarnings("unused")
        Map<? extends String, ? extends String> statements = parser.parse(
                "--STATEMENT test1\n select * from user;\n"
                        + "--STATEMENT test1\n select * from user;");
        Assert.fail("Failed to throw expected exception");
    }

    @Test
    public void testIgnoreCharactersAfterName() throws SqlStatementsFileParseException {
        Map<? extends String, ? extends String> statements = parser.parse(
                "--STATEMENT test1    \nselect *\nfrom user;\n"
                        + "--STATEMENT test2 wefwefwef\nselect *\nfrom transaction;");
        Assert.assertTrue(statements.size() == 2);
        Assert.assertTrue(statements.keySet().contains("test1"));
        Assert.assertEquals("select * from user;", statements.get("test1"));
        Assert.assertTrue(statements.keySet().contains("test2"));
        Assert.assertEquals("select * from transaction;", statements.get("test2"));
    }

}
