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
name|example
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
name|Set
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
name|FileUtils
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
name|exception
operator|.
name|TikaException
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
name|TikaInputStream
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
name|metadata
operator|.
name|Metadata
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
name|parser
operator|.
name|ParseContext
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
name|sax
operator|.
name|BodyContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Parses the output of /bin/ls and counts the number of files and the number of  * executables using Tika.  */
end_comment

begin_class
specifier|public
class|class
name|DirListParser
implements|implements
name|Parser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2717930544410610735L
decl_stmt|;
specifier|private
specifier|static
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|MediaType
operator|.
name|TEXT_PLAIN
argument_list|)
argument_list|)
decl_stmt|;
comment|/*      * (non-Javadoc)      *      * @see org.apache.tika.parser.Parser#getSupportedTypes(      * org.apache.tika.parser.ParseContext)      */
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|SUPPORTED_TYPES
return|;
block|}
comment|/*      * (non-Javadoc)      *      * @see org.apache.tika.parser.Parser#parse(java.io.InputStream,      * org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata)      */
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|this
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *      * @see org.apache.tika.parser.Parser#parse(java.io.InputStream,      * org.xml.sax.ContentHandler, org.apache.tika.metadata.Metadata,      * org.apache.tika.parser.ParseContext)      */
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|FileUtils
operator|.
name|readLines
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|is
argument_list|)
operator|.
name|getFile
argument_list|()
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|lines
control|)
block|{
name|String
index|[]
name|fileToks
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileToks
operator|.
name|length
operator|<
literal|8
condition|)
continue|continue;
name|String
name|filePermissions
init|=
name|fileToks
index|[
literal|0
index|]
decl_stmt|;
name|String
name|numHardLinks
init|=
name|fileToks
index|[
literal|1
index|]
decl_stmt|;
name|String
name|fileOwner
init|=
name|fileToks
index|[
literal|2
index|]
decl_stmt|;
name|String
name|fileOwnerGroup
init|=
name|fileToks
index|[
literal|3
index|]
decl_stmt|;
name|String
name|fileSize
init|=
name|fileToks
index|[
literal|4
index|]
decl_stmt|;
name|StringBuilder
name|lastModDate
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|lastModDate
operator|.
name|append
argument_list|(
name|fileToks
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
name|lastModDate
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|lastModDate
operator|.
name|append
argument_list|(
name|fileToks
index|[
literal|6
index|]
argument_list|)
expr_stmt|;
name|lastModDate
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|lastModDate
operator|.
name|append
argument_list|(
name|fileToks
index|[
literal|7
index|]
argument_list|)
expr_stmt|;
name|StringBuilder
name|fileName
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|8
init|;
name|i
operator|<
name|fileToks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|fileName
operator|.
name|append
argument_list|(
name|fileToks
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|fileName
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|fileName
operator|.
name|deleteCharAt
argument_list|(
name|fileName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|addMetadata
argument_list|(
name|metadata
argument_list|,
name|filePermissions
argument_list|,
name|numHardLinks
argument_list|,
name|fileOwner
argument_list|,
name|fileOwnerGroup
argument_list|,
name|fileSize
argument_list|,
name|lastModDate
operator|.
name|toString
argument_list|()
argument_list|,
name|fileName
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|DirListParser
name|parser
init|=
operator|new
name|DirListParser
argument_list|()
decl_stmt|;
name|Metadata
name|met
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|System
operator|.
name|in
argument_list|,
operator|new
name|BodyContentHandler
argument_list|()
argument_list|,
name|met
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Num files: "
operator|+
name|met
operator|.
name|getValues
argument_list|(
literal|"Filename"
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Num executables: "
operator|+
name|met
operator|.
name|get
argument_list|(
literal|"NumExecutables"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addMetadata
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|String
name|filePerms
parameter_list|,
name|String
name|numHardLinks
parameter_list|,
name|String
name|fileOwner
parameter_list|,
name|String
name|fileOwnerGroup
parameter_list|,
name|String
name|fileSize
parameter_list|,
name|String
name|lastModDate
parameter_list|,
name|String
name|fileName
parameter_list|)
block|{
name|metadata
operator|.
name|add
argument_list|(
literal|"FilePermissions"
argument_list|,
name|filePerms
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"NumHardLinks"
argument_list|,
name|numHardLinks
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"FileOwner"
argument_list|,
name|fileOwner
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"FileOwnerGroup"
argument_list|,
name|fileOwnerGroup
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"FileSize"
argument_list|,
name|fileSize
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"LastModifiedDate"
argument_list|,
name|lastModDate
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"Filename"
argument_list|,
name|fileName
argument_list|)
expr_stmt|;
if|if
condition|(
name|filePerms
operator|.
name|indexOf
argument_list|(
literal|"x"
argument_list|)
operator|!=
operator|-
literal|1
operator|&&
name|filePerms
operator|.
name|indexOf
argument_list|(
literal|"d"
argument_list|)
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|metadata
operator|.
name|get
argument_list|(
literal|"NumExecutables"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|int
name|numExecs
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
literal|"NumExecutables"
argument_list|)
argument_list|)
decl_stmt|;
name|numExecs
operator|++
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"NumExecutables"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|numExecs
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"NumExecutables"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

