/*
 * DatabaseTableMetaData.java   Mar 8, 2002
 *
 * The dbUnit database testing framework.
 * Copyright (C) 2002   Manuel Laflamme
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

package org.dbunit.database;

import java.sql.*;
import java.util.*;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class DatabaseTableMetaData extends AbstractTableMetaData
{
    private final String _tableName;
    private final IDatabaseConnection _connection;
    private Column[] _columns;
    private Column[] _primaryKeys;

    public DatabaseTableMetaData(String tableName, IDatabaseConnection connection)
    {
        _tableName = tableName;
        _connection = connection;
    }

    private String[] getPrimaryKeyNames() throws SQLException
    {
        Connection connection = _connection.getConnection();
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        ResultSet resultSet = databaseMetaData.getPrimaryKeys(
                null, _connection.getSchema(), _tableName);

        List list = new ArrayList();
        try
        {
            while (resultSet.next())
            {
                String name = resultSet.getString(4);
                int index = resultSet.getInt(5);
                list.add(new PrimaryKeyData(name, index));
            }
        }
        finally
        {
            resultSet.close();
        }

        Collections.sort(list);
        String[] keys = new String[list.size()];
        for (int i = 0; i < keys.length; i++)
        {
            PrimaryKeyData data = (PrimaryKeyData)list.get(i);
            keys[i] = data.getName();
        }

        return keys;
    }

    private class PrimaryKeyData implements Comparable
    {
        private final String _name;
        private final int _index;

        public PrimaryKeyData(String name, int index)
        {
            _name = name;
            _index = index;
        }

        public String getName()
        {
            return _name;
        }

        public int getIndex()
        {
            return _index;
        }

        ////////////////////////////////////////////////////////////////////////
        // Comparable interface

        public int compareTo(Object o)
        {
            PrimaryKeyData data = (PrimaryKeyData)o;
            return data.getIndex() - getIndex();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITableMetaData interface

    public String getTableName()
    {
        return _tableName;
    }

    public Column[] getColumns() throws DataSetException
    {
        if (_columns == null)
        {
            try
            {
                Connection jdbcConnection = _connection.getConnection();
                DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();
                ResultSet resultSet = databaseMetaData.getColumns(
                        null, _connection.getSchema(), _tableName, null);

                try
                {
                    List columnList = new ArrayList();
                    while (resultSet.next())
                    {
                        String columnName = resultSet.getString(4);
                        int sqlType = resultSet.getInt(5);
//                        String sqlTypeName = resultSet.getString(6);
//                        int columnSize = resultSet.getInt(7);
                        int nullable = resultSet.getInt(11);

                        try
                        {
                            Column column = new Column(
                                    columnName,
                                    DataType.forSqlType(sqlType),
                                    Column.nullableValue(nullable));
                            columnList.add(column);
                        }
                        catch (DataTypeException e)
                        {
                            // ignore unknown column datatype
                        }
                    }

                    _columns = (Column[])columnList.toArray(new Column[0]);
                }
                finally
                {
                    resultSet.close();
                }
            }
            catch (SQLException e)
            {
                throw new DataSetException(e);
            }
        }
        return _columns;
    }

    public Column[] getPrimaryKeys() throws DataSetException
    {
        if (_primaryKeys == null)
        {
            try
            {
                _primaryKeys = getPrimaryKeys(getColumns(), getPrimaryKeyNames());
            }
            catch (SQLException e)
            {
                throw new DataSetException(e);
            }
        }
        return _primaryKeys;
    }

}


