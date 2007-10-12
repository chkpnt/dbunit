/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Manuel Laflamme
 * @version $Revision$
 * @since Feb 17, 2002
 */
public class CompositeTable extends AbstractTable
{

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CompositeTable.class);

    private final ITableMetaData _metaData;
    private final ITable[] _tables;

    /**
     * Creates a composite table that combines the specified metadata with the
     * specified table.
     */
    public CompositeTable(ITableMetaData metaData, ITable table)
    {
        _metaData = metaData;
        _tables = new ITable[]{table};
    }

    /**
     * Creates a composite table that combines the specified metadata with the
     * specified tables.
     */
    public CompositeTable(ITableMetaData metaData, ITable[] tables)
    {
        _metaData = metaData;
        _tables = tables;
    }

    /**
     * Creates a composite table that combines the specified specified tables.
     * The metadata from the first table is used as metadata for the new table.
     */
    public CompositeTable(ITable table1, ITable table2)
    {
        _metaData = table1.getTableMetaData();
        _tables = new ITable[]{table1, table2};
    }

    /**
     * Creates a composite dataset that encapsulate the specified table with
     * a new name.
     */
    public CompositeTable(String newName, ITable table)
            throws DataSetException
    {
        ITableMetaData metaData = table.getTableMetaData();
        _metaData = new DefaultTableMetaData(newName,
                metaData.getColumns(), metaData.getPrimaryKeys());
        _tables = new ITable[]{table};
    }

    ////////////////////////////////////////////////////////////////////////////
    // ITable interface

    public ITableMetaData getTableMetaData()
    {
        logger.debug("getTableMetaData() - start");

        return _metaData;
    }

    public int getRowCount()
    {
        logger.debug("getRowCount() - start");

        int totalCount = 0;
        for (int i = 0; i < _tables.length; i++)
        {
            ITable table = _tables[i];
            totalCount += table.getRowCount();
        }

        return totalCount;
    }

    public Object getValue(int row, String column) throws DataSetException
    {
        logger.debug("getValue(row=" + row + ", column=" + column + ") - start");

        if (row < 0)
        {
            throw new RowOutOfBoundsException(row + " < 0 ");
        }

        int totalCount = 0;
        for (int i = 0; i < _tables.length; i++)
        {
            ITable table = _tables[i];

            int count = table.getRowCount();
            if (totalCount + count > row)
            {
                return table.getValue(row - totalCount, column);
            }
            totalCount += count;
        }

        throw new RowOutOfBoundsException(row + " > " + totalCount);
    }
}





