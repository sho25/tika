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
name|io
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
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|config
operator|.
name|TikaConfig
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
name|eval
operator|.
name|db
operator|.
name|ColInfo
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
name|eval
operator|.
name|db
operator|.
name|Cols
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
name|eval
operator|.
name|db
operator|.
name|DBUtil
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
name|eval
operator|.
name|db
operator|.
name|MimeBuffer
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
name|eval
operator|.
name|db
operator|.
name|TableInfo
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

begin_comment
comment|/**  * This is still in its early stages.  The idea is to  * get something working with h2 and then add to that  * as necessary.  *  * Beware, this deletes the db file with each initialization.  */
end_comment

begin_class
specifier|public
class|class
name|DBWriter
implements|implements
name|IDBWriter
block|{
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DBWriter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|AtomicLong
name|insertedRows
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Long
name|commitEveryX
init|=
literal|1000L
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|TableInfo
argument_list|>
name|tableInfos
decl_stmt|;
specifier|private
specifier|final
name|Connection
name|conn
decl_stmt|;
specifier|private
specifier|final
name|DBUtil
name|dbUtil
decl_stmt|;
specifier|private
specifier|static
name|MimeBuffer
name|mimeBuffer
decl_stmt|;
comment|//<tableName, preparedStatement>
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|PreparedStatement
argument_list|>
name|inserts
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|DBWriter
parameter_list|(
name|List
argument_list|<
name|TableInfo
argument_list|>
name|tableInfos
parameter_list|,
name|TikaConfig
name|tikaConfig
parameter_list|,
name|DBUtil
name|dbUtil
parameter_list|)
throws|throws
name|IOException
throws|,
name|SQLException
block|{
name|this
operator|.
name|conn
operator|=
name|dbUtil
operator|.
name|getConnection
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|mimeBuffer
operator|==
literal|null
condition|)
block|{
name|mimeBuffer
operator|=
operator|new
name|MimeBuffer
argument_list|(
name|conn
argument_list|,
name|tikaConfig
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|tableInfos
operator|=
name|tableInfos
expr_stmt|;
name|this
operator|.
name|dbUtil
operator|=
name|dbUtil
expr_stmt|;
for|for
control|(
name|TableInfo
name|tableInfo
range|:
name|tableInfos
control|)
block|{
try|try
block|{
name|PreparedStatement
name|st
init|=
name|createPreparedInsert
argument_list|(
name|tableInfo
argument_list|)
decl_stmt|;
name|inserts
operator|.
name|put
argument_list|(
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|,
name|st
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|int
name|getMimeId
parameter_list|(
name|String
name|mimeString
parameter_list|)
block|{
return|return
name|mimeBuffer
operator|.
name|getId
argument_list|(
name|mimeString
argument_list|)
return|;
block|}
specifier|private
name|PreparedStatement
name|createPreparedInsert
parameter_list|(
name|TableInfo
name|tableInfo
parameter_list|)
throws|throws
name|SQLException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"INSERT INTO "
argument_list|)
operator|.
name|append
argument_list|(
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ColInfo
name|c
range|:
name|tableInfo
operator|.
name|getColInfos
argument_list|()
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"VALUES"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|i
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|j
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|conn
operator|.
name|prepareStatement
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|writeRow
parameter_list|(
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
name|IOException
block|{
try|try
block|{
name|PreparedStatement
name|p
init|=
name|inserts
operator|.
name|get
argument_list|(
name|table
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to create prepared statement for: "
operator|+
name|table
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|dbUtil
operator|.
name|insert
argument_list|(
name|p
argument_list|,
name|table
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|long
name|rows
init|=
name|insertedRows
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|rows
operator|%
name|commitEveryX
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"writer is committing after "
operator|+
name|rows
operator|+
literal|" rows"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|mimeBuffer
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
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOExceptionWithCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|conn
operator|.
name|close
argument_list|()
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
block|}
block|}
end_class

end_unit
