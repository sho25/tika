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
name|util
operator|.
name|ArrayList
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
name|eval
operator|.
name|io
operator|.
name|IDBWriter
import|;
end_import

begin_class
specifier|public
class|class
name|MockDBWriter
implements|implements
name|IDBWriter
block|{
comment|//Map of tableName and tables
comment|//each table consists of a list of rows.
comment|//Each row consists of a map of columns/values
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
name|db
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|MockDBWriter
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Override
specifier|public
name|void
name|writeRow
parameter_list|(
name|TableInfo
name|tableInfo
parameter_list|,
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
name|row
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
name|table
init|=
name|db
operator|.
name|get
argument_list|(
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|==
literal|null
condition|)
block|{
name|table
operator|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|table
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|db
operator|.
name|put
argument_list|(
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|//no-op
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMimeId
parameter_list|(
name|String
name|mimeString
parameter_list|)
block|{
comment|//TODO: fix this
return|return
literal|0
return|;
block|}
specifier|public
name|List
argument_list|<
name|Map
argument_list|<
name|Cols
argument_list|,
name|String
argument_list|>
argument_list|>
name|getTable
parameter_list|(
name|TableInfo
name|tableInfo
parameter_list|)
block|{
if|if
condition|(
name|db
operator|.
name|get
argument_list|(
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"I can't seem to find: "
operator|+
name|tableInfo
operator|.
name|getName
argument_list|()
operator|+
literal|", but I do see:"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|table
range|:
name|db
operator|.
name|keySet
argument_list|()
control|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|table
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|db
operator|.
name|get
argument_list|(
name|tableInfo
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|db
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit
