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
package org.dbunit.ext.oracle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.dbunit.dataset.datatype.TypeCastException;

/**
 * NCLOB handler
 * @author cris.daniluk
 * @since May 3, 2005
 * @version $Revision$
 */
public class OracleNClobDataType extends OracleClobDataType {

    protected static final Short FORM_NCHAR = new Short((short)2);
    
    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException
    {
        try 
        {
            Class statementClass = Class.forName("oracle.jdbc.OraclePreparedStatement");
            Method formOfUse = statementClass.getMethod("setFormOfUse", new Class[] { Integer.TYPE, Short.TYPE });
            formOfUse.invoke(statement, new Object[] { new Integer(column), FORM_NCHAR });
        }
        catch (IllegalAccessException e) 
        {
            throw new TypeCastException(value, this, e);
        } 
        catch (NoSuchMethodException e) 
        {
            throw new TypeCastException(value, this, e);
        } 
        catch (InvocationTargetException e) 
        {
            throw new TypeCastException(value, this, e.getTargetException());
        }
        catch (ClassNotFoundException e) 
        {
            throw new TypeCastException(value, this, e);
        }
        
        statement.setObject(column, getClob(value, statement.getConnection()));
    }
    
}