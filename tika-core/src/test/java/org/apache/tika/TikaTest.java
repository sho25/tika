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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Collection
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
name|tika
operator|.
name|extractor
operator|.
name|EmbeddedResourceHandler
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
name|AutoDetectParser
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
name|parser
operator|.
name|RecursiveParserWrapper
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
name|BasicContentHandlerFactory
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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|ToXMLContentHandler
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * Parent class of Tika tests  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TikaTest
block|{
comment|/**     * This method will give you back the filename incl. the absolute path name     * to the resource. If the resource does not exist it will give you back the     * resource name incl. the path.     *     * @param name     *            The named resource to search for.     * @return an absolute path incl. the name which is in the same directory as     *         the the class you've called it from.     */
specifier|public
name|File
name|getResourceAsFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|URL
name|url
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|File
argument_list|(
name|url
operator|.
name|toURI
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// We have a file which does not exists
comment|// We got the path
name|url
operator|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|url
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Unable to find requested file "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|file
return|;
block|}
block|}
specifier|public
name|InputStream
name|getResourceAsStream
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|InputStream
name|stream
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Unable to find requested resource "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|stream
return|;
block|}
specifier|public
specifier|static
name|void
name|assertContains
parameter_list|(
name|String
name|needle
parameter_list|,
name|String
name|haystack
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|needle
operator|+
literal|" not found in:\n"
operator|+
name|haystack
argument_list|,
name|haystack
operator|.
name|contains
argument_list|(
name|needle
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|assertContains
parameter_list|(
name|T
name|needle
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|haystack
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|needle
operator|+
literal|" not found in:\n"
operator|+
name|haystack
argument_list|,
name|haystack
operator|.
name|contains
argument_list|(
name|needle
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|assertNotContained
parameter_list|(
name|String
name|needle
parameter_list|,
name|String
name|haystack
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|needle
operator|+
literal|" unexpectedly found in:\n"
operator|+
name|haystack
argument_list|,
name|haystack
operator|.
name|contains
argument_list|(
name|needle
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|assertNotContained
parameter_list|(
name|T
name|needle
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|haystack
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|needle
operator|+
literal|" unexpectedly found in:\n"
operator|+
name|haystack
argument_list|,
name|haystack
operator|.
name|contains
argument_list|(
name|needle
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
class|class
name|XMLResult
block|{
specifier|public
specifier|final
name|String
name|xml
decl_stmt|;
specifier|public
specifier|final
name|Metadata
name|metadata
decl_stmt|;
specifier|public
name|XMLResult
parameter_list|(
name|String
name|xml
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|xml
operator|=
name|xml
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
block|}
specifier|protected
name|XMLResult
name|getXML
parameter_list|(
name|String
name|filePath
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getXML
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filePath
argument_list|)
argument_list|,
name|parser
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
block|}
specifier|protected
name|XMLResult
name|getXML
parameter_list|(
name|String
name|filePath
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getXML
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filePath
argument_list|)
argument_list|,
name|parser
argument_list|,
name|metadata
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|protected
name|XMLResult
name|getXML
parameter_list|(
name|String
name|filePath
parameter_list|,
name|ParseContext
name|parseContext
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getXML
argument_list|(
name|filePath
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
specifier|protected
name|XMLResult
name|getXML
parameter_list|(
name|String
name|filePath
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getXML
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filePath
argument_list|)
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|,
name|metadata
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|protected
name|XMLResult
name|getXML
parameter_list|(
name|String
name|filePath
parameter_list|,
name|Parser
name|parser
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getXML
argument_list|(
name|filePath
argument_list|,
name|parser
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|XMLResult
name|getXML
parameter_list|(
name|String
name|filePath
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getXML
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filePath
argument_list|)
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|protected
name|XMLResult
name|getXML
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getXML
argument_list|(
name|input
argument_list|,
name|parser
argument_list|,
name|metadata
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|protected
name|XMLResult
name|getXML
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|context
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|ToXMLContentHandler
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return
operator|new
name|XMLResult
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
argument_list|,
name|metadata
argument_list|)
return|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|List
argument_list|<
name|Metadata
argument_list|>
name|getRecursiveJson
parameter_list|(
name|String
name|filePath
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getRecursiveJson
argument_list|(
name|filePath
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|List
argument_list|<
name|Metadata
argument_list|>
name|getRecursiveJson
parameter_list|(
name|String
name|filePath
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|RecursiveParserWrapper
name|wrapper
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|p
argument_list|,
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|XML
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filePath
argument_list|)
init|)
block|{
name|wrapper
operator|.
name|parse
argument_list|(
name|is
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|wrapper
operator|.
name|getMetadata
argument_list|()
return|;
block|}
comment|/**      * Basic text extraction.      *<p>      * Tries to close input stream after processing.      */
specifier|public
name|String
name|getText
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|ParseContext
name|context
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|(
literal|1000000
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|handler
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getText
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getText
argument_list|(
name|is
argument_list|,
name|parser
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|,
name|metadata
argument_list|)
return|;
block|}
specifier|public
name|String
name|getText
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Parser
name|parser
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getText
argument_list|(
name|is
argument_list|,
name|parser
argument_list|,
name|context
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|getText
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|Parser
name|parser
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|getText
argument_list|(
name|is
argument_list|,
name|parser
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Keeps track of media types and file names recursively.      *      */
specifier|public
specifier|static
class|class
name|TrackingHandler
implements|implements
name|EmbeddedResourceHandler
block|{
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|filenames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|List
argument_list|<
name|MediaType
argument_list|>
name|mediaTypes
init|=
operator|new
name|ArrayList
argument_list|<
name|MediaType
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|skipTypes
decl_stmt|;
specifier|public
name|TrackingHandler
parameter_list|()
block|{
name|skipTypes
operator|=
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|public
name|TrackingHandler
parameter_list|(
name|Set
argument_list|<
name|MediaType
argument_list|>
name|skipTypes
parameter_list|)
block|{
name|this
operator|.
name|skipTypes
operator|=
name|skipTypes
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|String
name|filename
parameter_list|,
name|MediaType
name|mediaType
parameter_list|,
name|InputStream
name|stream
parameter_list|)
block|{
if|if
condition|(
name|skipTypes
operator|.
name|contains
argument_list|(
name|mediaType
argument_list|)
condition|)
block|{
return|return;
block|}
name|mediaTypes
operator|.
name|add
argument_list|(
name|mediaType
argument_list|)
expr_stmt|;
name|filenames
operator|.
name|add
argument_list|(
name|filename
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Copies byte[] of embedded documents into a List.      */
specifier|public
specifier|static
class|class
name|ByteCopyingHandler
implements|implements
name|EmbeddedResourceHandler
block|{
specifier|public
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|bytes
init|=
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|handle
parameter_list|(
name|String
name|filename
parameter_list|,
name|MediaType
name|mediaType
parameter_list|,
name|InputStream
name|stream
parameter_list|)
block|{
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|stream
operator|.
name|markSupported
argument_list|()
condition|)
block|{
name|stream
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
name|stream
operator|.
name|mark
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|add
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|stream
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//swallow
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|debug
parameter_list|(
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Metadata
name|m
range|:
name|list
control|)
block|{
for|for
control|(
name|String
name|n
range|:
name|m
operator|.
name|names
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|v
range|:
name|m
operator|.
name|getValues
argument_list|(
name|n
argument_list|)
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
operator|+
literal|": "
operator|+
name|n
operator|+
literal|" : "
operator|+
name|v
argument_list|)
expr_stmt|;
block|}
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

