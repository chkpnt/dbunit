/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002, Manuel Laflamme
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.dbunit.dataset;

import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.Assertion;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.sql.Date;
import java.math.BigDecimal;

/**
 *
 * @author Manuel Laflamme
 * @since Mar 17, 2003
 * @version $Revision$
 */
public class ReplacementTableTest extends AbstractTableTest
{
    public ReplacementTableTest(String s)
    {
        super(s);
    }

    protected ITable createTable() throws Exception
    {
        return createDataSet().getTable("TEST_TABLE");
    }

    private IDataSet createDataSet() throws Exception
    {
        return new BooleanDataSet(
                new FlatXmlDataSet(new File("src/xml/flatXmlTableTest.xml")));
    }

    public void testGetMissingValue() throws Exception
    {
        // TODO test something usefull
    }

    public void testObjectReplacement() throws Exception
    {
        String tableName = "TABLE_NAME";
        BigDecimal trueObject = new BigDecimal(1);
        BigDecimal falseObject = new BigDecimal(0);
        Date now = new Date(System.currentTimeMillis());

        Column[] columns = new Column[] {
            new Column("BOOLEAN_TRUE", DataType.BOOLEAN),
            new Column("BOOLEAN_FALSE", DataType.BOOLEAN),
            new Column("STRING_TRUE", DataType.CHAR),
            new Column("STRING_FALSE", DataType.CHAR),
            new Column("STRING_VALUE", DataType.CHAR),
            new Column("DATE_VALUE", DataType.DATE),
            new Column("NULL_TO_STRING_VALUE", DataType.CHAR),
            new Column("STRING_TO_NULL_VALUE", DataType.CHAR),
        };

        // Setup actual table
        Object[] actualRow = new Object[] {
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.TRUE.toString(),
            Boolean.FALSE.toString(),
            "value",
            "now",
            null,
            "null",
        };

        List actualRowList = new ArrayList();
        actualRowList.add(actualRow);
        ITable originalTable = new DefaultTable(tableName, columns, actualRowList);
        ReplacementTable actualTable = new ReplacementTable(originalTable);
        actualTable.addReplacementObject(Boolean.TRUE, trueObject);
        actualTable.addReplacementObject(Boolean.FALSE, falseObject);
        actualTable.addReplacementObject("now", now);
        actualTable.addReplacementObject("null", null);
        actualTable.addReplacementObject(null, "nullreplacement");

        // Setup expected table
        Object[] expectedRow = new Object[] {
            trueObject,
            falseObject,
            Boolean.TRUE.toString(),
            Boolean.FALSE.toString(),
            "value",
            now,
            "nullreplacement",
            null,
        };

        List expectedRowList = new ArrayList();
        expectedRowList.add(expectedRow);
        ITable expectedTable = new DefaultTable(tableName, columns, expectedRowList);

        Assertion.assertEquals(expectedTable, actualTable);
    }

    public void testSubstringReplacement() throws Exception
    {
        String tableName = "TABLE_NAME";

        Column[] columns = new Column[] {
            new Column("ONLY_SUBSTRING", DataType.CHAR),
            new Column("START_SUBSTRING", DataType.CHAR),
            new Column("MIDDLE_SUBSTRING", DataType.CHAR),
            new Column("END_SUBSTRING", DataType.CHAR),
            new Column("MULTIPLE_SUBSTRING", DataType.CHAR),
            new Column("NO_SUBSTRING", DataType.CHAR),
            new Column("NOT_A_STRING", DataType.NUMERIC),
            new Column("NULL_VALUE", DataType.CHAR),
        };

        // Setup actual table
        Object[] actualRow = new Object[] {
            "substring",
            "substring_",
            "_substring_",
            "_substring",
            "substringsubstring substring",
            "this is a string",
            new Long(0),
            null,
        };

        List actualRowList = new ArrayList();
        actualRowList.add(actualRow);
        ITable originalTable = new DefaultTable(tableName, columns, actualRowList);
        ReplacementTable actualTable = new ReplacementTable(originalTable);
        actualTable.addReplacementSubstring("substring", "replacement");

        // Setup expected table
        Object[] expectedRow = new Object[] {
            "replacement",
            "replacement_",
            "_replacement_",
            "_replacement",
            "replacementreplacement replacement",
            "this is a string",
            new Long(0),
            null,
        };

        List expectedRowList = new ArrayList();
        expectedRowList.add(expectedRow);
        ITable expectedTable = new DefaultTable(tableName, columns, expectedRowList);

        Assertion.assertEquals(expectedTable, actualTable);
    }

    public void testAddNullReplacementSubstring() throws Exception
    {
        ReplacementTable replacementTable =
                new ReplacementTable(new DefaultTable("TABLE"));
        try
        {
            replacementTable.addReplacementSubstring(null, "replacement");
            fail("Should not be here!");
        }
        catch (NullPointerException e)
        {
        }

        try
        {
            replacementTable.addReplacementSubstring("substring", null);
            fail("Should not be here!");
        }
        catch (NullPointerException e)
        {
        }
    }

}