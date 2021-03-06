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
name|server
operator|.
name|resource
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|GET
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
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
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|Gson
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|GsonBuilder
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
name|mime
operator|.
name|MediaType
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
name|mime
operator|.
name|MediaTypeRegistry
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
name|parser
operator|.
name|CompositeParser
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
name|parser
operator|.
name|Parser
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
name|server
operator|.
name|HTMLHelper
import|;
end_import

begin_comment
comment|/**  *<p>Provides details of all the mimetypes known to Apache Tika,  * similar to<em>--list-supported-types</em> with the Tika CLI.  */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|"/mime-types"
argument_list|)
specifier|public
class|class
name|TikaMimeTypes
block|{
specifier|private
specifier|static
specifier|final
name|Gson
name|GSON
init|=
operator|new
name|GsonBuilder
argument_list|()
operator|.
name|disableHtmlEscaping
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
name|HTMLHelper
name|html
decl_stmt|;
specifier|public
name|TikaMimeTypes
parameter_list|()
block|{
name|this
operator|.
name|html
operator|=
operator|new
name|HTMLHelper
argument_list|()
expr_stmt|;
block|}
annotation|@
name|GET
annotation|@
name|Produces
argument_list|(
literal|"text/html"
argument_list|)
specifier|public
name|String
name|getMimeTypesHTML
parameter_list|()
block|{
name|StringBuffer
name|h
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|html
operator|.
name|generateHeader
argument_list|(
name|h
argument_list|,
literal|"Apache Tika Supported Mime Types"
argument_list|)
expr_stmt|;
comment|// Get our types
name|List
argument_list|<
name|MediaTypeDetails
argument_list|>
name|types
init|=
name|getMediaTypes
argument_list|()
decl_stmt|;
comment|// Get the first type in each section
name|SortedMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|firstType
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|MediaTypeDetails
name|type
range|:
name|types
control|)
block|{
if|if
condition|(
operator|!
name|firstType
operator|.
name|containsKey
argument_list|(
name|type
operator|.
name|type
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|firstType
operator|.
name|put
argument_list|(
name|type
operator|.
name|type
operator|.
name|getType
argument_list|()
argument_list|,
name|type
operator|.
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|h
operator|.
name|append
argument_list|(
literal|"<ul>"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|section
range|:
name|firstType
operator|.
name|keySet
argument_list|()
control|)
block|{
name|h
operator|.
name|append
argument_list|(
literal|"<li><a href=\"#"
argument_list|)
operator|.
name|append
argument_list|(
name|firstType
operator|.
name|get
argument_list|(
name|section
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
operator|.
name|append
argument_list|(
name|section
argument_list|)
operator|.
name|append
argument_list|(
literal|"</a></li>\n"
argument_list|)
expr_stmt|;
block|}
name|h
operator|.
name|append
argument_list|(
literal|"</ul>"
argument_list|)
expr_stmt|;
comment|// Output all of them
for|for
control|(
name|MediaTypeDetails
name|type
range|:
name|types
control|)
block|{
name|h
operator|.
name|append
argument_list|(
literal|"<a name=\""
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|.
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|"\"></a>\n"
argument_list|)
expr_stmt|;
name|h
operator|.
name|append
argument_list|(
literal|"<h2>"
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|.
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|"</h2>\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|MediaType
name|alias
range|:
name|type
operator|.
name|aliases
control|)
block|{
name|h
operator|.
name|append
argument_list|(
literal|"<div>Alias: "
argument_list|)
operator|.
name|append
argument_list|(
name|alias
argument_list|)
operator|.
name|append
argument_list|(
literal|"</div>\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|supertype
operator|!=
literal|null
condition|)
block|{
name|h
operator|.
name|append
argument_list|(
literal|"<div>Super Type:<a href=\"#"
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|.
name|supertype
argument_list|)
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|.
name|supertype
argument_list|)
operator|.
name|append
argument_list|(
literal|"</a></div>\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|parser
operator|!=
literal|null
condition|)
block|{
name|h
operator|.
name|append
argument_list|(
literal|"<div>Parser: "
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|.
name|parser
argument_list|)
operator|.
name|append
argument_list|(
literal|"</div>\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|html
operator|.
name|generateFooter
argument_list|(
name|h
argument_list|)
expr_stmt|;
return|return
name|h
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|GET
annotation|@
name|Produces
argument_list|(
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
specifier|public
name|String
name|getMimeTypesJSON
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|details
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|MediaTypeDetails
name|type
range|:
name|getMediaTypes
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|typeDets
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|typeDets
operator|.
name|put
argument_list|(
literal|"alias"
argument_list|,
name|copyToStringArray
argument_list|(
name|type
operator|.
name|aliases
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|supertype
operator|!=
literal|null
condition|)
block|{
name|typeDets
operator|.
name|put
argument_list|(
literal|"supertype"
argument_list|,
name|type
operator|.
name|supertype
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|parser
operator|!=
literal|null
condition|)
block|{
name|typeDets
operator|.
name|put
argument_list|(
literal|"parser"
argument_list|,
name|type
operator|.
name|parser
argument_list|)
expr_stmt|;
block|}
name|details
operator|.
name|put
argument_list|(
name|type
operator|.
name|type
operator|.
name|toString
argument_list|()
argument_list|,
name|typeDets
argument_list|)
expr_stmt|;
block|}
return|return
name|GSON
operator|.
name|toJson
argument_list|(
name|details
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
index|[]
name|copyToStringArray
parameter_list|(
name|MediaType
index|[]
name|aliases
parameter_list|)
block|{
name|String
index|[]
name|strings
init|=
operator|new
name|String
index|[
name|aliases
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|aliases
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|strings
index|[
name|i
index|]
operator|=
name|aliases
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
annotation|@
name|GET
annotation|@
name|Produces
argument_list|(
literal|"text/plain"
argument_list|)
specifier|public
name|String
name|getMimeTypesPlain
parameter_list|()
block|{
name|StringBuffer
name|text
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|MediaTypeDetails
name|type
range|:
name|getMediaTypes
argument_list|()
control|)
block|{
name|text
operator|.
name|append
argument_list|(
name|type
operator|.
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|MediaType
name|alias
range|:
name|type
operator|.
name|aliases
control|)
block|{
name|text
operator|.
name|append
argument_list|(
literal|"  alias:     "
argument_list|)
operator|.
name|append
argument_list|(
name|alias
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|supertype
operator|!=
literal|null
condition|)
block|{
name|text
operator|.
name|append
argument_list|(
literal|"  supertype: "
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|.
name|supertype
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|.
name|parser
operator|!=
literal|null
condition|)
block|{
name|text
operator|.
name|append
argument_list|(
literal|"  parser:    "
argument_list|)
operator|.
name|append
argument_list|(
name|type
operator|.
name|parser
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|text
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|List
argument_list|<
name|MediaTypeDetails
argument_list|>
name|getMediaTypes
parameter_list|()
block|{
name|MediaTypeRegistry
name|registry
init|=
name|TikaResource
operator|.
name|getConfig
argument_list|()
operator|.
name|getMediaTypeRegistry
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|MediaType
argument_list|,
name|Parser
argument_list|>
name|parsers
init|=
operator|(
operator|(
name|CompositeParser
operator|)
name|TikaResource
operator|.
name|getConfig
argument_list|()
operator|.
name|getParser
argument_list|()
operator|)
operator|.
name|getParsers
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MediaTypeDetails
argument_list|>
name|types
init|=
operator|new
name|ArrayList
argument_list|<
name|TikaMimeTypes
operator|.
name|MediaTypeDetails
argument_list|>
argument_list|(
name|registry
operator|.
name|getTypes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|MediaType
name|type
range|:
name|registry
operator|.
name|getTypes
argument_list|()
control|)
block|{
name|MediaTypeDetails
name|details
init|=
operator|new
name|MediaTypeDetails
argument_list|()
decl_stmt|;
name|details
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|details
operator|.
name|aliases
operator|=
name|registry
operator|.
name|getAliases
argument_list|(
name|type
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|MediaType
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|MediaType
name|supertype
init|=
name|registry
operator|.
name|getSupertype
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|supertype
operator|!=
literal|null
operator|&&
operator|!
name|MediaType
operator|.
name|OCTET_STREAM
operator|.
name|equals
argument_list|(
name|supertype
argument_list|)
condition|)
block|{
name|details
operator|.
name|supertype
operator|=
name|supertype
expr_stmt|;
block|}
name|Parser
name|p
init|=
name|parsers
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|CompositeParser
condition|)
block|{
name|p
operator|=
operator|(
operator|(
name|CompositeParser
operator|)
name|p
operator|)
operator|.
name|getParsers
argument_list|()
operator|.
name|get
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|details
operator|.
name|parser
operator|=
name|p
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|types
operator|.
name|add
argument_list|(
name|details
argument_list|)
expr_stmt|;
block|}
return|return
name|types
return|;
block|}
specifier|private
specifier|static
class|class
name|MediaTypeDetails
block|{
specifier|private
name|MediaType
name|type
decl_stmt|;
specifier|private
name|MediaType
index|[]
name|aliases
decl_stmt|;
specifier|private
name|MediaType
name|supertype
decl_stmt|;
specifier|private
name|String
name|parser
decl_stmt|;
block|}
block|}
end_class

end_unit

