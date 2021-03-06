begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|jdbc
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|sql
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Clob
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
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSetMetaData
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
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FilenameUtils
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
name|IOExceptionWithCause
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
name|detect
operator|.
name|Detector
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
name|extractor
operator|.
name|EmbeddedDocumentUtil
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
name|Database
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
name|metadata
operator|.
name|TikaCoreProperties
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
name|MimeTypes
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
name|sax
operator|.
name|XHTMLContentHandler
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
name|Attributes
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
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * General base class to iterate through rows of a JDBC table  */
end_comment

begin_class
class|class
name|JDBCTableReader
block|{
specifier|private
specifier|final
specifier|static
name|Attributes
name|EMPTY_ATTRIBUTES
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Connection
name|connection
decl_stmt|;
specifier|private
specifier|final
name|String
name|tableName
decl_stmt|;
name|int
name|maxClobLength
init|=
literal|1000000
decl_stmt|;
name|ResultSet
name|results
init|=
literal|null
decl_stmt|;
name|int
name|rows
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|EmbeddedDocumentUtil
name|embeddedDocumentUtil
decl_stmt|;
specifier|public
name|JDBCTableReader
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|String
name|tableName
parameter_list|,
name|EmbeddedDocumentUtil
name|embeddedDocumentUtil
parameter_list|)
block|{
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
name|this
operator|.
name|tableName
operator|=
name|tableName
expr_stmt|;
name|this
operator|.
name|embeddedDocumentUtil
operator|=
name|embeddedDocumentUtil
expr_stmt|;
block|}
specifier|public
name|boolean
name|nextRow
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
comment|//lazy initialization
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
try|try
block|{
if|if
condition|(
operator|!
name|results
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
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
name|IOExceptionWithCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|ResultSetMetaData
name|meta
init|=
name|results
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"tr"
argument_list|,
literal|"tr"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|meta
operator|.
name|getColumnCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|handler
operator|.
name|startElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"td"
argument_list|,
literal|"td"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|handleCell
argument_list|(
name|meta
argument_list|,
name|i
argument_list|,
name|handler
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"td"
argument_list|,
literal|"td"
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|endElement
argument_list|(
name|XHTMLContentHandler
operator|.
name|XHTML
argument_list|,
literal|"tr"
argument_list|,
literal|"tr"
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
name|rows
operator|++
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|handleCell
parameter_list|(
name|ResultSetMetaData
name|rsmd
parameter_list|,
name|int
name|i
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
throws|,
name|SAXException
block|{
switch|switch
condition|(
name|rsmd
operator|.
name|getColumnType
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
name|Types
operator|.
name|BLOB
case|:
name|handleBlob
argument_list|(
name|tableName
argument_list|,
name|rsmd
operator|.
name|getColumnName
argument_list|(
name|i
argument_list|)
argument_list|,
name|rows
argument_list|,
name|results
argument_list|,
name|i
argument_list|,
name|handler
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|CLOB
case|:
name|handleClob
argument_list|(
name|tableName
argument_list|,
name|rsmd
operator|.
name|getColumnName
argument_list|(
name|i
argument_list|)
argument_list|,
name|rows
argument_list|,
name|results
argument_list|,
name|i
argument_list|,
name|handler
argument_list|,
name|context
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|BOOLEAN
case|:
name|handleBoolean
argument_list|(
name|results
argument_list|,
name|i
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|DATE
case|:
name|handleDate
argument_list|(
name|results
argument_list|,
name|i
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|TIMESTAMP
case|:
name|handleTimeStamp
argument_list|(
name|results
argument_list|,
name|i
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|INTEGER
case|:
name|handleInteger
argument_list|(
name|results
argument_list|,
name|i
argument_list|,
name|handler
argument_list|)
expr_stmt|;
break|break;
case|case
name|Types
operator|.
name|FLOAT
case|:
comment|//this is necessary to handle rounding issues in presentation
comment|//Should we just use getString(i)?
name|float
name|f
init|=
name|results
operator|.
name|getFloat
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|results
operator|.
name|wasNull
argument_list|()
condition|)
block|{
name|addAllCharacters
argument_list|(
name|Float
operator|.
name|toString
argument_list|(
name|f
argument_list|)
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|Types
operator|.
name|DOUBLE
case|:
name|double
name|d
init|=
name|results
operator|.
name|getDouble
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|results
operator|.
name|wasNull
argument_list|()
condition|)
block|{
name|addAllCharacters
argument_list|(
name|Double
operator|.
name|toString
argument_list|(
name|d
argument_list|)
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|String
name|s
init|=
name|results
operator|.
name|getString
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|results
operator|.
name|wasNull
argument_list|()
condition|)
block|{
name|addAllCharacters
argument_list|(
name|s
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getHeaders
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|headers
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//lazy initialization
if|if
condition|(
name|results
operator|==
literal|null
condition|)
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|ResultSetMetaData
name|meta
init|=
name|results
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|meta
operator|.
name|getColumnCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|headers
operator|.
name|add
argument_list|(
name|meta
operator|.
name|getColumnName
argument_list|(
name|i
argument_list|)
argument_list|)
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
name|IOExceptionWithCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|headers
return|;
block|}
specifier|protected
name|void
name|handleInteger
parameter_list|(
name|ResultSet
name|rs
parameter_list|,
name|int
name|columnIndex
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SQLException
throws|,
name|SAXException
block|{
name|int
name|i
init|=
name|rs
operator|.
name|getInt
argument_list|(
name|columnIndex
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rs
operator|.
name|wasNull
argument_list|()
condition|)
block|{
name|addAllCharacters
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleBoolean
parameter_list|(
name|ResultSet
name|rs
parameter_list|,
name|int
name|columnIndex
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
throws|,
name|SQLException
block|{
name|boolean
name|b
init|=
name|rs
operator|.
name|getBoolean
argument_list|(
name|columnIndex
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rs
operator|.
name|wasNull
argument_list|()
condition|)
block|{
name|addAllCharacters
argument_list|(
name|Boolean
operator|.
name|toString
argument_list|(
name|b
argument_list|)
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|handleClob
parameter_list|(
name|String
name|tableName
parameter_list|,
name|String
name|columnName
parameter_list|,
name|int
name|rowNum
parameter_list|,
name|ResultSet
name|resultSet
parameter_list|,
name|int
name|columnIndex
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|Clob
name|clob
init|=
name|resultSet
operator|.
name|getClob
argument_list|(
name|columnIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|wasNull
argument_list|()
condition|)
block|{
return|return;
block|}
name|boolean
name|truncated
init|=
name|clob
operator|.
name|length
argument_list|()
operator|>
name|Integer
operator|.
name|MAX_VALUE
operator|||
name|clob
operator|.
name|length
argument_list|()
operator|>
name|maxClobLength
decl_stmt|;
name|int
name|readSize
init|=
operator|(
name|clob
operator|.
name|length
argument_list|()
operator|<
name|maxClobLength
condition|?
operator|(
name|int
operator|)
name|clob
operator|.
name|length
argument_list|()
else|:
name|maxClobLength
operator|)
decl_stmt|;
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|TABLE_NAME
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|COLUMN_NAME
argument_list|,
name|columnName
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|PREFIX
operator|+
literal|"ROW_NUM"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|rowNum
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|PREFIX
operator|+
literal|"IS_CLOB"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|PREFIX
operator|+
literal|"CLOB_LENGTH"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|clob
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|PREFIX
operator|+
literal|"IS_CLOB_TRUNCATED"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|truncated
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"text/plain; charset=UTF-8"
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_LENGTH
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|readSize
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
comment|//just in case something screwy is going on with the column name
name|FilenameUtils
operator|.
name|normalize
argument_list|(
name|FilenameUtils
operator|.
name|getName
argument_list|(
name|columnName
operator|+
literal|"_"
operator|+
name|rowNum
operator|+
literal|".txt"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//is there a more efficient way to go from a Reader to an InputStream?
name|String
name|s
init|=
name|clob
operator|.
name|getSubString
argument_list|(
literal|0
argument_list|,
name|readSize
argument_list|)
decl_stmt|;
if|if
condition|(
name|embeddedDocumentUtil
operator|.
name|shouldParseEmbedded
argument_list|(
name|m
argument_list|)
condition|)
block|{
name|embeddedDocumentUtil
operator|.
name|parseEmbedded
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|s
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
name|handler
argument_list|,
name|m
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|handleBlob
parameter_list|(
name|String
name|tableName
parameter_list|,
name|String
name|columnName
parameter_list|,
name|int
name|rowNum
parameter_list|,
name|ResultSet
name|resultSet
parameter_list|,
name|int
name|columnIndex
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|SQLException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|TABLE_NAME
argument_list|,
name|tableName
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|COLUMN_NAME
argument_list|,
name|columnName
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|PREFIX
operator|+
literal|"ROW_NUM"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|rowNum
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|set
argument_list|(
name|Database
operator|.
name|PREFIX
operator|+
literal|"IS_BLOB"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|Blob
name|blob
init|=
literal|null
decl_stmt|;
name|TikaInputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|blob
operator|=
name|getBlob
argument_list|(
name|resultSet
argument_list|,
name|columnIndex
argument_list|,
name|m
argument_list|)
expr_stmt|;
if|if
condition|(
name|blob
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|is
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|blob
argument_list|,
name|m
argument_list|)
expr_stmt|;
name|Attributes
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
operator|(
operator|(
name|AttributesImpl
operator|)
name|attrs
operator|)
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"blob"
argument_list|)
expr_stmt|;
operator|(
operator|(
name|AttributesImpl
operator|)
name|attrs
operator|)
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"column_name"
argument_list|,
literal|"column_name"
argument_list|,
literal|"CDATA"
argument_list|,
name|columnName
argument_list|)
expr_stmt|;
operator|(
operator|(
name|AttributesImpl
operator|)
name|attrs
operator|)
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"row_number"
argument_list|,
literal|"row_number"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|rowNum
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"span"
argument_list|,
literal|"span"
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|String
name|extension
init|=
name|embeddedDocumentUtil
operator|.
name|getExtension
argument_list|(
name|is
argument_list|,
name|m
argument_list|)
decl_stmt|;
name|m
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|RESOURCE_NAME_KEY
argument_list|,
comment|//just in case something screwy is going on with the column name
name|FilenameUtils
operator|.
name|normalize
argument_list|(
name|FilenameUtils
operator|.
name|getName
argument_list|(
name|columnName
operator|+
literal|"_"
operator|+
name|rowNum
operator|+
name|extension
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|embeddedDocumentUtil
operator|.
name|shouldParseEmbedded
argument_list|(
name|m
argument_list|)
condition|)
block|{
name|embeddedDocumentUtil
operator|.
name|parseEmbedded
argument_list|(
name|is
argument_list|,
name|handler
argument_list|,
name|m
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|blob
operator|.
name|free
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
decl||
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|"span"
argument_list|,
literal|"span"
argument_list|)
expr_stmt|;
block|}
comment|/**      *      * @param resultSet result set to grab value from      * @param columnIndex index in result set      * @param metadata metadata to populate or use for each implementation      * @return the blob or<code>null</code> if the value was null      * @throws SQLException      */
specifier|protected
name|Blob
name|getBlob
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|int
name|columnIndex
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|SQLException
block|{
name|Blob
name|blob
init|=
name|resultSet
operator|.
name|getBlob
argument_list|(
name|columnIndex
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|resultSet
operator|.
name|wasNull
argument_list|()
condition|)
block|{
return|return
name|blob
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|handleDate
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|int
name|columnIndex
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
throws|,
name|SQLException
block|{
name|addAllCharacters
argument_list|(
name|resultSet
operator|.
name|getString
argument_list|(
name|columnIndex
argument_list|)
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|handleTimeStamp
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|int
name|columnIndex
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
throws|,
name|SQLException
block|{
name|addAllCharacters
argument_list|(
name|resultSet
operator|.
name|getString
argument_list|(
name|columnIndex
argument_list|)
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addAllCharacters
parameter_list|(
name|String
name|s
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|char
index|[]
name|chars
init|=
name|s
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|chars
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|results
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|results
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
comment|//swallow
block|}
block|}
name|String
name|sql
init|=
literal|"SELECT * from "
operator|+
name|tableName
decl_stmt|;
try|try
block|{
name|Statement
name|st
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|results
operator|=
name|st
operator|.
name|executeQuery
argument_list|(
name|sql
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
name|rows
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|String
name|getTableName
parameter_list|()
block|{
return|return
name|tableName
return|;
block|}
specifier|protected
name|TikaConfig
name|getTikaConfig
parameter_list|()
block|{
return|return
name|embeddedDocumentUtil
operator|.
name|getTikaConfig
argument_list|()
return|;
block|}
specifier|protected
name|Detector
name|getDetector
parameter_list|()
block|{
return|return
name|embeddedDocumentUtil
operator|.
name|getDetector
argument_list|()
return|;
block|}
specifier|protected
name|MimeTypes
name|getMimeTypes
parameter_list|()
block|{
return|return
name|embeddedDocumentUtil
operator|.
name|getMimeTypes
argument_list|()
return|;
block|}
block|}
end_class

end_unit

