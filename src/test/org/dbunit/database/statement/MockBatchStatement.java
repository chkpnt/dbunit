/*
 * MockBatchStatement.java   Mar 16, 2002
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

package org.dbunit.database.statement;

import java.sql.SQLException;

import com.mockobjects.*;

/**
 * @author Manuel Laflamme
 * @version 1.0
 */
public class MockBatchStatement implements IBatchStatement, Verifiable
{
    private ExpectationCounter _executeBatchCalls =
            new ExpectationCounter("MockBatchStatement.executeBatch");;
    private ExpectationCounter _clearBatchCalls =
            new ExpectationCounter("MockBatchStatement.clearBatch");;
    private ExpectationCounter _closeCalls =
            new ExpectationCounter("MockBatchStatement.close");;
    private ExpectationList _batchStrings =
            new ExpectationList("MockBatchStatement.batchStrings");
    private int _executeBatchResult = 1;

    public MockBatchStatement()
    {
    }

    public void addExpectedBatchString(String sql)
    {
        _batchStrings.addExpected(sql);
    }

    public void addExpectedBatchStrings(String[] sql)
    {
        _batchStrings.addExpectedMany(sql);
    }

    public void setExpectedExecuteBatchCalls(int callsCount)
    {
        _executeBatchCalls.setExpected(callsCount);
    }

    public void setupExecuteBatchResult(int result)
    {
        _executeBatchResult = result;
    }

    public void setExpectedClearBatchCalls(int callsCount)
    {
        _clearBatchCalls.setExpected(callsCount);
    }

    public void setExpectedCloseCalls(int callsCount)
    {
        _closeCalls.setExpected(callsCount);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Verifiable interface

    public void verify()
    {
        _executeBatchCalls.verify();
        _clearBatchCalls.verify();
        _closeCalls.verify();
        _batchStrings.verify();
    }

    ////////////////////////////////////////////////////////////////////////////
    // IBatchStatement interface

    public void addBatch(String sql) throws SQLException
    {
        _batchStrings.addActual(sql);
    }

    public int executeBatch() throws SQLException
    {
        _executeBatchCalls.inc();
        return _executeBatchResult;
    }

    public void clearBatch() throws SQLException
    {
        _clearBatchCalls.inc();
    }

    public void close() throws SQLException
    {
        _closeCalls.inc();
    }
}