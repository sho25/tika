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
name|parser
operator|.
name|geo
operator|.
name|topic
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|opennlp
operator|.
name|tools
operator|.
name|namefind
operator|.
name|NameFinderME
import|;
end_import

begin_import
import|import
name|opennlp
operator|.
name|tools
operator|.
name|namefind
operator|.
name|TokenNameFinderModel
import|;
end_import

begin_import
import|import
name|opennlp
operator|.
name|tools
operator|.
name|util
operator|.
name|InvalidFormatException
import|;
end_import

begin_import
import|import
name|opennlp
operator|.
name|tools
operator|.
name|util
operator|.
name|Span
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_class
specifier|public
class|class
name|NameEntityExtractor
block|{
specifier|private
name|String
name|nerModelPath
init|=
literal|null
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|locationNameEntities
decl_stmt|;
name|String
name|bestNameEntity
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|tf
decl_stmt|;
specifier|public
name|NameEntityExtractor
parameter_list|(
name|String
name|nerModelpath
parameter_list|)
block|{
name|this
operator|.
name|locationNameEntities
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|bestNameEntity
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|nerModelPath
operator|=
name|nerModelpath
expr_stmt|;
name|tf
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/* 	 * Use OpenNLP to extract location names that's appearing in the steam. 	 * OpenNLP's default Name Finder accuracy is not very good, please refer to 	 * its documentation. 	 *  	 * @param stream stream that passed from this.parse() 	 */
specifier|public
name|void
name|getAllNameEntitiesfromInput
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|InvalidFormatException
throws|,
name|IOException
block|{
name|InputStream
name|modelIn
init|=
operator|new
name|FileInputStream
argument_list|(
name|nerModelPath
argument_list|)
decl_stmt|;
name|TokenNameFinderModel
name|model
init|=
operator|new
name|TokenNameFinderModel
argument_list|(
name|modelIn
argument_list|)
decl_stmt|;
name|NameFinderME
name|nameFinder
init|=
operator|new
name|NameFinderME
argument_list|(
name|model
argument_list|)
decl_stmt|;
name|String
index|[]
name|in
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|Span
name|nameE
index|[]
init|=
name|nameFinder
operator|.
name|find
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|String
name|spanNames
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|Span
operator|.
name|spansToStrings
argument_list|(
name|nameE
argument_list|,
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|spanNames
operator|=
name|spanNames
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|spanNames
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|modelIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
index|[]
name|tmp
init|=
name|spanNames
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|tmp
control|)
block|{
name|name
operator|=
name|name
operator|.
name|trim
argument_list|()
expr_stmt|;
name|this
operator|.
name|locationNameEntities
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* 	 * Get the best location entity extracted from the input stream. Simply 	 * return the most frequent entity, If there several highest frequent 	 * entity, pick one randomly. May not be the optimal solution, but works. 	 *  	 * @param locationNameEntities OpenNLP name finder's results, stored in 	 * ArrayList 	 */
specifier|public
name|void
name|getBestNameEntity
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|locationNameEntities
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|this
operator|.
name|locationNameEntities
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|tf
operator|.
name|containsKey
argument_list|(
name|this
operator|.
name|locationNameEntities
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
name|tf
operator|.
name|put
argument_list|(
name|this
operator|.
name|locationNameEntities
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|tf
operator|.
name|get
argument_list|(
name|this
operator|.
name|locationNameEntities
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
else|else
name|tf
operator|.
name|put
argument_list|(
name|this
operator|.
name|locationNameEntities
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|max
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|(
name|tf
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|o1
parameter_list|,
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|o2
parameter_list|)
block|{
return|return
name|o2
operator|.
name|getValue
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o1
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
comment|// descending
comment|// order
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|locationNameEntities
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// update so that they are in
comment|// descending order
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|list
control|)
block|{
name|this
operator|.
name|locationNameEntities
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|bestNameEntity
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
