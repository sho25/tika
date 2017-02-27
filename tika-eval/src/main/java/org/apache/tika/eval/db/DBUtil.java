begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|eval
operator|.
name|db
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Types
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|io
operator|.
name|IOExceptionWithCause
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|DBUtil
block|{
specifier|public
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DBUtil
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|abstract
name|String
name|getJDBCDriverClass
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|dropTableIfExists
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|String
name|tableName
parameter_list|)
throws|throws
name|SQLException
function_decl|;
specifier|private
specifier|final
name|Path
name|db
decl_stmt|;
specifier|public
name|DBUtil
parameter_list|(
name|Path
name|db
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
block|}
comment|/**      * This is intended for a file/directory based db.      *<p>      * Override this any optimizations you want to do on the db      * before writing/reading.      *      * @return      * @throws IOException      */
specifier|public
name|Connection
name|getConnection
parameter_list|(
name|boolean
name|createIfDoesntExist
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|connectionString
init|=
name|getConnectionString
argument_list|(
name|db
argument_list|,
name|createIfDoesntExist
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
try|try
block|{
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
name|getJDBCDriverClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|conn
operator|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|connectionString
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|conn
return|;
block|}
specifier|abstract
specifier|public
name|String
name|getConnectionString
parameter_list|(
name|Path
name|db
parameter_list|,
name|boolean
name|createIfDoesntExist
parameter_list|)
function_decl|;
comment|/**      *      * @param connection      * @return a list of uppercased table names      * @throws SQLException      */
specifier|abstract
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTables
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|SQLException
function_decl|;
specifier|public
specifier|static
name|int
name|insert
parameter_list|(
name|PreparedStatement
name|insertStatement
parameter_list|,
name|TableInfo
name|table
parameter_list|,
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
name|data
parameter_list|)
throws|throws
name|SQLException
block|{
comment|//clear parameters before setting
name|insertStatement
operator|.
name|clearParameters
argument_list|()
expr_stmt|;
try|try
block|{
name|int
name|i
init|=
literal|1
decl_stmt|;
for|for
control|(
name|ColInfo
name|colInfo
range|:
name|table
operator|.
name|getColInfos
argument_list|()
control|)
block|{
name|updateInsertStatement
argument_list|(
name|i
argument_list|,
name|insertStatement
argument_list|,
name|colInfo
argument_list|,
name|data
operator|.
name|get
argument_list|(
name|colInfo
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
for|for
control|(
name|Cols
name|c
range|:
name|data
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|table
operator|.
name|containsColumn
argument_list|(
name|c
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't add data to "
operator|+
name|c
operator|+
literal|" because it doesn't exist in the table: "
operator|+
name|table
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|insertStatement
operator|.
name|executeUpdate
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"couldn't insert data for this row: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
specifier|public
specifier|static
name|void
name|updateInsertStatement
parameter_list|(
name|int
name|dbColOffset
parameter_list|,
name|PreparedStatement
name|st
parameter_list|,
name|ColInfo
name|colInfo
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|st
operator|.
name|setNull
argument_list|(
name|dbColOffset
argument_list|,
name|colInfo
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
switch|switch
condition|(
name|colInfo
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Types
operator|.
name|VARCHAR
case|:
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
name|colInfo
operator|.
name|getPrecision
argument_list|()
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colInfo
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"truncated varchar value in "
operator|+
name|colInfo
operator|.
name|getName
argument_list|()
operator|+
literal|" : "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|st
operator|.
name|setString
argument_list|(
name|dbColOffset
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|CHAR
case|:
name|st
operator|.
name|setString
argument_list|(
name|dbColOffset
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|DOUBLE
case|:
name|st
operator|.
name|setDouble
argument_list|(
name|dbColOffset
argument_list|,
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|FLOAT
case|:
name|st
operator|.
name|setDouble
argument_list|(
name|dbColOffset
argument_list|,
name|Float
operator|.
name|parseFloat
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|INTEGER
case|:
name|st
operator|.
name|setInt
argument_list|(
name|dbColOffset
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|BIGINT
case|:
name|st
operator|.
name|setLong
argument_list|(
name|dbColOffset
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|BOOLEAN
case|:
name|st
operator|.
name|setBoolean
argument_list|(
name|dbColOffset
argument_list|,
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Don't yet support type: "
operator|+
name|colInfo
operator|.
name|getType
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"number format exception: "
operator|+
name|colInfo
operator|.
name|getName
argument_list|()
operator|+
literal|" : "
operator|+
name|value
argument_list|)
expr_stmt|;
block|}
name|st
operator|.
name|setNull
argument_list|(
name|dbColOffset
argument_list|,
name|colInfo
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"sqlexception: "
operator|+
name|colInfo
operator|+
literal|" : "
operator|+
name|value
argument_list|)
expr_stmt|;
name|st
operator|.
name|setNull
argument_list|(
name|dbColOffset
argument_list|,
name|colInfo
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|createDB
parameter_list|(
name|List
argument_list|<
name|TableInfo
argument_list|>
name|tableInfos
parameter_list|,
name|boolean
name|append
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
block|{
name|Connection
name|conn
init|=
name|getConnection
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|tables
init|=
name|getTables
argument_list|(
name|conn
argument_list|)
decl_stmt|;
for|for
control|(
name|TableInfo
name|tableInfo
range|:
name|tableInfos
control|)
block|{
if|if
condition|(
name|append
operator|&&
name|tables
operator|.
name|contains
argument_list|(
name|tableInfo
operator|.
name|getName
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|append
condition|)
block|{
name|dropTableIfExists
argument_list|(
name|conn
argument_list|,
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|createTable
argument_list|(
name|conn
argument_list|,
name|tableInfo
argument_list|)
expr_stmt|;
block|}
name|conn
operator|.
name|commit
argument_list|()
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createTable
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|TableInfo
name|tableInfo
parameter_list|)
throws|throws
name|SQLException
block|{
name|StringBuilder
name|createSql
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|createSql
operator|.
name|append
argument_list|(
literal|"CREATE TABLE "
operator|+
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|createSql
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|int
name|last
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ColInfo
name|col
range|:
name|tableInfo
operator|.
name|getColInfos
argument_list|()
control|)
block|{
name|last
operator|++
expr_stmt|;
if|if
condition|(
name|last
operator|>
literal|1
condition|)
block|{
name|createSql
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|createSql
operator|.
name|append
argument_list|(
name|col
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|createSql
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|createSql
operator|.
name|append
argument_list|(
name|col
operator|.
name|getSqlDef
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|constraints
init|=
name|col
operator|.
name|getConstraints
argument_list|()
decl_stmt|;
if|if
condition|(
name|constraints
operator|!=
literal|null
condition|)
block|{
name|createSql
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|createSql
operator|.
name|append
argument_list|(
name|constraints
argument_list|)
expr_stmt|;
block|}
block|}
name|createSql
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|Statement
name|st
init|=
name|conn
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|st
operator|.
name|execute
argument_list|(
name|createSql
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|st
operator|.
name|close
argument_list|()
expr_stmt|;
name|conn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

