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
name|sax
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
name|assertEquals
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
name|io
operator|.
name|UnsupportedEncodingException
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
name|junit
operator|.
name|Test
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
comment|/**  * Test cases for the {@link org.apache.tika.sax.BodyContentHandler} class.  */
end_comment

begin_class
specifier|public
class|class
name|BasicContentHandlerFactoryTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|ENCODING
init|=
name|IOUtils
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
decl_stmt|;
comment|//default max char len (at least in WriteOutContentHandler is 100k)
specifier|private
specifier|static
specifier|final
name|int
name|OVER_DEFAULT
init|=
literal|120000
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testIgnore
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|MockParser
argument_list|(
name|OVER_DEFAULT
argument_list|)
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|IGNORE
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|DefaultHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//unfortunatley, the DefaultHandler does not return "",
name|assertContains
argument_list|(
literal|"org.xml.sax.helpers.DefaultHandler"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//tests that no write limit exception is thrown
name|p
operator|=
operator|new
name|MockParser
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|IGNORE
argument_list|,
literal|5
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|DefaultHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"org.xml.sax.helpers.DefaultHandler"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testText
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|MockParser
argument_list|(
name|OVER_DEFAULT
argument_list|)
decl_stmt|;
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
name|type
init|=
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|TEXT
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|ToTextContentHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|extracted
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"This is the title"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"aaaaaaaaaa"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"<body"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"<html"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|extracted
operator|.
name|length
argument_list|()
operator|>
literal|110000
argument_list|)
expr_stmt|;
comment|//now test write limit
name|p
operator|=
operator|new
name|MockParser
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
literal|5
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|WriteOutContentHandler
argument_list|)
expr_stmt|;
name|assertWriteLimitReached
argument_list|(
name|p
argument_list|,
operator|(
name|WriteOutContentHandler
operator|)
name|handler
argument_list|)
expr_stmt|;
name|extracted
operator|=
name|handler
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertContains
argument_list|(
literal|"This "
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"aaaa"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
comment|//now test outputstream call
name|p
operator|=
operator|new
name|MockParser
argument_list|(
name|OVER_DEFAULT
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|ToTextContentHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is the title"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"aaaaaaaaaa"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
operator|>
literal|110000
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"<body"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"<html"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|MockParser
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|os
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
literal|5
argument_list|)
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|WriteOutContentHandler
argument_list|)
expr_stmt|;
name|assertWriteLimitReached
argument_list|(
name|p
argument_list|,
operator|(
name|WriteOutContentHandler
operator|)
name|handler
argument_list|)
expr_stmt|;
comment|//When writing to an OutputStream and a write limit is reached,
comment|//currently, nothing is written.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHTML
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|MockParser
argument_list|(
name|OVER_DEFAULT
argument_list|)
decl_stmt|;
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
name|type
init|=
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|HTML
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|ToHTMLContentHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|extracted
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"<head><title>This is the title"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"aaaaaaaaaa"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|extracted
operator|.
name|length
argument_list|()
operator|>
literal|110000
argument_list|)
expr_stmt|;
comment|//now test write limit
name|p
operator|=
operator|new
name|MockParser
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
literal|5
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|WriteOutContentHandler
argument_list|)
expr_stmt|;
name|assertWriteLimitReached
argument_list|(
name|p
argument_list|,
operator|(
name|WriteOutContentHandler
operator|)
name|handler
argument_list|)
expr_stmt|;
name|extracted
operator|=
name|handler
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertContains
argument_list|(
literal|"This "
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"aaaa"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
comment|//now test outputstream call
name|p
operator|=
operator|new
name|MockParser
argument_list|(
name|OVER_DEFAULT
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|ToHTMLContentHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is the title"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"aaaaaaaaaa"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<body"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<html"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
operator|>
literal|110000
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|MockParser
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|os
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
literal|5
argument_list|)
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|WriteOutContentHandler
argument_list|)
expr_stmt|;
name|assertWriteLimitReached
argument_list|(
name|p
argument_list|,
operator|(
name|WriteOutContentHandler
operator|)
name|handler
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXML
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|MockParser
argument_list|(
name|OVER_DEFAULT
argument_list|)
decl_stmt|;
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
name|type
init|=
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|HTML
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|ToXMLContentHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|extracted
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"<head><title>This is the title"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"aaaaaaaaaa"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|110000
argument_list|)
expr_stmt|;
comment|//now test write limit
name|p
operator|=
operator|new
name|MockParser
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
literal|5
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|WriteOutContentHandler
argument_list|)
expr_stmt|;
name|assertWriteLimitReached
argument_list|(
name|p
argument_list|,
operator|(
name|WriteOutContentHandler
operator|)
name|handler
argument_list|)
expr_stmt|;
name|extracted
operator|=
name|handler
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertContains
argument_list|(
literal|"This "
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"aaaa"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
comment|//now test outputstream call
name|p
operator|=
operator|new
name|MockParser
argument_list|(
name|OVER_DEFAULT
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|ToXMLContentHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"This is the title"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"aaaaaaaaaa"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<body"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<html"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
operator|>
literal|110000
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|MockParser
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|os
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
literal|5
argument_list|)
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|WriteOutContentHandler
argument_list|)
expr_stmt|;
name|assertWriteLimitReached
argument_list|(
name|p
argument_list|,
operator|(
name|WriteOutContentHandler
operator|)
name|handler
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBody
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|p
init|=
operator|new
name|MockParser
argument_list|(
name|OVER_DEFAULT
argument_list|)
decl_stmt|;
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
name|type
init|=
name|BasicContentHandlerFactory
operator|.
name|HANDLER_TYPE
operator|.
name|BODY
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|BodyContentHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|String
name|extracted
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertNotContains
argument_list|(
literal|"title"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"aaaaaaaaaa"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|extracted
operator|.
name|length
argument_list|()
operator|>
literal|110000
argument_list|)
expr_stmt|;
comment|//now test write limit
name|p
operator|=
operator|new
name|MockParser
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
literal|5
argument_list|)
operator|.
name|getNewContentHandler
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|BodyContentHandler
argument_list|)
expr_stmt|;
name|assertWriteLimitReached
argument_list|(
name|p
argument_list|,
operator|(
name|BodyContentHandler
operator|)
name|handler
argument_list|)
expr_stmt|;
name|extracted
operator|=
name|handler
operator|.
name|toString
argument_list|()
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"This "
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"aaaa"
argument_list|,
name|extracted
argument_list|)
expr_stmt|;
comment|//now test outputstream call
name|p
operator|=
operator|new
name|MockParser
argument_list|(
name|OVER_DEFAULT
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|BodyContentHandler
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"title"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"aaaaaaaaaa"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"<body"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotContains
argument_list|(
literal|"<html"
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
operator|>
literal|110000
argument_list|)
expr_stmt|;
name|p
operator|=
operator|new
name|MockParser
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|os
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|handler
operator|=
operator|new
name|BasicContentHandlerFactory
argument_list|(
name|type
argument_list|,
literal|5
argument_list|)
operator|.
name|getNewContentHandler
argument_list|(
name|os
argument_list|,
name|ENCODING
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|handler
operator|instanceof
name|WriteOutContentHandler
argument_list|)
expr_stmt|;
name|assertWriteLimitReached
argument_list|(
name|p
argument_list|,
operator|(
name|WriteOutContentHandler
operator|)
name|handler
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertWriteLimitReached
parameter_list|(
name|Parser
name|p
parameter_list|,
name|WriteOutContentHandler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|wlr
init|=
literal|false
decl_stmt|;
try|try
block|{
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|handler
operator|.
name|isWriteLimitReached
argument_list|(
name|e
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|wlr
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"WriteLimitReached"
argument_list|,
name|wlr
argument_list|)
expr_stmt|;
block|}
comment|//TODO: is there a better way than to repeat this with diff signature?
specifier|private
name|void
name|assertWriteLimitReached
parameter_list|(
name|Parser
name|p
parameter_list|,
name|BodyContentHandler
name|handler
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|wlr
init|=
literal|false
decl_stmt|;
try|try
block|{
name|p
operator|.
name|parse
argument_list|(
literal|null
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"org.apache.tika.sax.WriteOutContentHandler$WriteLimitReachedException"
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|wlr
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"WriteLimitReached"
argument_list|,
name|wlr
argument_list|)
expr_stmt|;
block|}
comment|//copied from TikaTest in tika-parsers package
specifier|public
specifier|static
name|void
name|assertNotContains
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
literal|" found in:\n"
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
name|assertNotContains
parameter_list|(
name|String
name|needle
parameter_list|,
name|byte
index|[]
name|hayStack
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|assertNotContains
argument_list|(
name|needle
argument_list|,
operator|new
name|String
argument_list|(
name|hayStack
argument_list|,
name|ENCODING
argument_list|)
argument_list|)
expr_stmt|;
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
name|void
name|assertContains
parameter_list|(
name|String
name|needle
parameter_list|,
name|byte
index|[]
name|hayStack
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
name|assertContains
argument_list|(
name|needle
argument_list|,
operator|new
name|String
argument_list|(
name|hayStack
argument_list|,
name|ENCODING
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Simple mockparser that writes a title
comment|//and charsToWrite number of 'a'
specifier|private
class|class
name|MockParser
implements|implements
name|Parser
block|{
specifier|private
specifier|final
name|String
name|XHTML
init|=
literal|"http://www.w3.org/1999/xhtml"
decl_stmt|;
specifier|private
specifier|final
name|Attributes
name|EMPTY_ATTRIBUTES
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|char
index|[]
name|TITLE
init|=
literal|"This is the title"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|int
name|charsToWrite
decl_stmt|;
specifier|public
name|MockParser
parameter_list|(
name|int
name|charsToWrite
parameter_list|)
block|{
name|this
operator|.
name|charsToWrite
operator|=
name|charsToWrite
expr_stmt|;
block|}
annotation|@
name|Override
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
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
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
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|handler
operator|.
name|startPrefixMapping
argument_list|(
literal|""
argument_list|,
name|XHTML
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|XHTML
argument_list|,
literal|"html"
argument_list|,
literal|"html"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|XHTML
argument_list|,
literal|"head"
argument_list|,
literal|"head"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|XHTML
argument_list|,
literal|"title"
argument_list|,
literal|"head"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|TITLE
argument_list|,
literal|0
argument_list|,
name|TITLE
operator|.
name|length
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|XHTML
argument_list|,
literal|"title"
argument_list|,
literal|"head"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|XHTML
argument_list|,
literal|"head"
argument_list|,
literal|"head"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|XHTML
argument_list|,
literal|"body"
argument_list|,
literal|"body"
argument_list|,
name|EMPTY_ATTRIBUTES
argument_list|)
expr_stmt|;
name|char
index|[]
name|body
init|=
operator|new
name|char
index|[
name|charsToWrite
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
name|charsToWrite
condition|;
name|i
operator|++
control|)
block|{
name|body
index|[
name|i
index|]
operator|=
literal|'a'
expr_stmt|;
block|}
name|handler
operator|.
name|characters
argument_list|(
name|body
argument_list|,
literal|0
argument_list|,
name|body
operator|.
name|length
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|XHTML
argument_list|,
literal|"body"
argument_list|,
literal|"body"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|XHTML
argument_list|,
literal|"html"
argument_list|,
literal|"html"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

