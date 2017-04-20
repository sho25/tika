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
name|io
operator|.
name|InputStream
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
name|DatabaseMetaData
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
name|ResultSet
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
name|HashSet
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
name|Properties
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|JDBCUtil
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JDBCUtil
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
enum|enum
name|CREATE_TABLE
block|{
name|DROP_IF_EXISTS
block|,
name|SKIP_IF_EXISTS
block|,
name|THROW_EX_IF_EXISTS
block|,     }
specifier|private
specifier|final
name|String
name|connectionString
decl_stmt|;
specifier|private
name|String
name|driverClass
decl_stmt|;
specifier|public
name|JDBCUtil
parameter_list|(
name|String
name|connectionString
parameter_list|,
name|String
name|driverClass
parameter_list|)
block|{
name|this
operator|.
name|connectionString
operator|=
name|connectionString
expr_stmt|;
name|this
operator|.
name|driverClass
operator|=
name|driverClass
expr_stmt|;
if|if
condition|(
name|driverClass
operator|==
literal|null
operator|||
name|driverClass
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"jdbc.drivers"
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|//user has specified it on the command line
comment|//stop now
block|}
else|else
block|{
comment|//try to use the mappings in db.properties to determine the class
try|try
init|(
name|InputStream
name|is
init|=
name|JDBCUtil
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/db.properties"
argument_list|)
init|)
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|k
range|:
name|properties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?i)jdbc:"
operator|+
name|k
argument_list|)
operator|.
name|matcher
argument_list|(
name|connectionString
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|this
operator|.
name|driverClass
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                  }
block|}
block|}
block|}
comment|/**      * Override this any optimizations you want to do on the db      * before writing/reading.      *      * @return      * @throws IOException      */
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|SQLException
block|{
name|String
name|connectionString
init|=
name|getConnectionString
argument_list|()
decl_stmt|;
name|Connection
name|conn
init|=
literal|null
decl_stmt|;
name|String
name|jdbcDriver
init|=
name|getJDBCDriverClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|jdbcDriver
operator|!=
literal|null
condition|)
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
return|return
name|conn
return|;
block|}
comment|/**      * JDBC driver class.  Override as necessary.      * @return      */
specifier|public
name|String
name|getJDBCDriverClass
parameter_list|()
block|{
return|return
name|driverClass
return|;
block|}
specifier|public
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
block|{
if|if
condition|(
name|containsTable
argument_list|(
name|tableName
argument_list|)
condition|)
block|{
try|try
init|(
name|Statement
name|st
init|=
name|conn
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|String
name|sql
init|=
literal|"drop table "
operator|+
name|tableName
decl_stmt|;
return|return
name|st
operator|.
name|execute
argument_list|(
name|sql
argument_list|)
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|String
name|getConnectionString
parameter_list|()
block|{
return|return
name|connectionString
return|;
block|}
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
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tables
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|DatabaseMetaData
name|dbMeta
init|=
name|connection
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
try|try
init|(
name|ResultSet
name|rs
init|=
name|dbMeta
operator|.
name|getTables
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"%"
argument_list|,
literal|null
argument_list|)
init|)
block|{
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|tables
operator|.
name|add
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|tables
return|;
block|}
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"couldn't insert data for this row: {}"
argument_list|,
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"truncated varchar value in {} : {}"
argument_list|,
name|colInfo
operator|.
name|getName
argument_list|()
argument_list|,
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"number format exception: {} : {}"
argument_list|,
name|colInfo
operator|.
name|getName
argument_list|()
argument_list|,
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"sqlexception: {} : {}"
argument_list|,
name|colInfo
argument_list|,
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
name|createTables
parameter_list|(
name|List
argument_list|<
name|TableInfo
argument_list|>
name|tableInfos
parameter_list|,
name|CREATE_TABLE
name|createTable
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
block|{
try|try
init|(
name|Connection
name|conn
init|=
name|getConnection
argument_list|()
init|)
block|{
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
name|createTable
operator|.
name|equals
argument_list|(
name|CREATE_TABLE
operator|.
name|DROP_IF_EXISTS
argument_list|)
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
elseif|else
if|if
condition|(
name|createTable
operator|.
name|equals
argument_list|(
name|CREATE_TABLE
operator|.
name|SKIP_IF_EXISTS
argument_list|)
condition|)
block|{
if|if
condition|(
name|containsTable
argument_list|(
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
block|}
block|}
specifier|public
name|boolean
name|containsTable
parameter_list|(
name|String
name|tableName
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
init|(
name|Connection
name|connection
init|=
name|getConnection
argument_list|()
init|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tables
init|=
name|getTables
argument_list|(
name|connection
argument_list|)
decl_stmt|;
if|if
condition|(
name|tables
operator|.
name|contains
argument_list|(
name|normalizeTableName
argument_list|(
name|tableName
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Override for custom behavior      * @param tableName      * @return      */
name|String
name|normalizeTableName
parameter_list|(
name|String
name|tableName
parameter_list|)
block|{
name|tableName
operator|=
name|tableName
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
return|return
name|tableName
return|;
block|}
comment|//does not close the connection
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
try|try
init|(
name|Statement
name|st
init|=
name|conn
operator|.
name|createStatement
argument_list|()
init|)
block|{
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
block|}
name|conn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
