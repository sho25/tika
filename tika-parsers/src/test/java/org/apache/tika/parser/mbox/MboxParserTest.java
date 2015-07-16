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
name|mbox
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|TikaTest
operator|.
name|assertContains
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
name|assertEquals
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
name|detect
operator|.
name|TypeDetector
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
name|sax
operator|.
name|BodyContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|ContentHandler
import|;
end_import

begin_class
specifier|public
class|class
name|MboxParserTest
block|{
specifier|protected
name|ParseContext
name|recursingContext
decl_stmt|;
specifier|private
name|Parser
name|autoDetectParser
decl_stmt|;
specifier|private
name|TypeDetector
name|typeDetector
decl_stmt|;
specifier|private
name|MboxParser
name|mboxParser
decl_stmt|;
specifier|private
specifier|static
name|InputStream
name|getStream
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|MboxParserTest
operator|.
name|class
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|typeDetector
operator|=
operator|new
name|TypeDetector
argument_list|()
expr_stmt|;
name|autoDetectParser
operator|=
operator|new
name|AutoDetectParser
argument_list|(
name|typeDetector
argument_list|)
expr_stmt|;
name|recursingContext
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|recursingContext
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|autoDetectParser
argument_list|)
expr_stmt|;
name|mboxParser
operator|=
operator|new
name|MboxParser
argument_list|()
expr_stmt|;
name|mboxParser
operator|.
name|setTracking
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|getStream
argument_list|(
literal|"/test-documents/simple.mbox"
argument_list|)
decl_stmt|;
try|try
block|{
name|mboxParser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|recursingContext
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertContains
argument_list|(
literal|"Test content 1"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Test content 2"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/mbox"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Metadata
argument_list|>
name|mailsMetadata
init|=
name|mboxParser
operator|.
name|getTrackingMetadata
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Nb. Of mails"
argument_list|,
literal|2
argument_list|,
name|mailsMetadata
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|mail1
init|=
name|mailsMetadata
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"message/rfc822"
argument_list|,
name|mail1
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"envelope-sender-mailbox-name Mon Jun 01 10:00:00 2009"
argument_list|,
name|mail1
operator|.
name|get
argument_list|(
literal|"MboxParser-from"
argument_list|)
argument_list|)
expr_stmt|;
name|Metadata
name|mail2
init|=
name|mailsMetadata
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"message/rfc822"
argument_list|,
name|mail2
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"envelope-sender-mailbox-name Mon Jun 01 11:00:00 2010"
argument_list|,
name|mail2
operator|.
name|get
argument_list|(
literal|"MboxParser-from"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHeaders
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|getStream
argument_list|(
literal|"/test-documents/headers.mbox"
argument_list|)
decl_stmt|;
try|try
block|{
name|mboxParser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|recursingContext
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertContains
argument_list|(
literal|"Test content"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Nb. Of mails"
argument_list|,
literal|1
argument_list|,
name|mboxParser
operator|.
name|getTrackingMetadata
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|mailMetadata
init|=
name|mboxParser
operator|.
name|getTrackingMetadata
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"2009-06-10T03:58:45Z"
argument_list|,
name|mailMetadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<author@domain.com>"
argument_list|,
name|mailMetadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"subject"
argument_list|,
name|mailMetadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<author@domain.com>"
argument_list|,
name|mailMetadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"message/rfc822"
argument_list|,
name|mailMetadata
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"author@domain.com"
argument_list|,
name|mailMetadata
operator|.
name|get
argument_list|(
literal|"Message-From"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"<name@domain.com>"
argument_list|,
name|mailMetadata
operator|.
name|get
argument_list|(
literal|"MboxParser-return-path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultilineHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|getStream
argument_list|(
literal|"/test-documents/multiline.mbox"
argument_list|)
decl_stmt|;
try|try
block|{
name|mboxParser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|recursingContext
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Nb. Of mails"
argument_list|,
literal|1
argument_list|,
name|mboxParser
operator|.
name|getTrackingMetadata
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|mailMetadata
init|=
name|mboxParser
operator|.
name|getTrackingMetadata
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"from xxx by xxx with xxx; date"
argument_list|,
name|mailMetadata
operator|.
name|get
argument_list|(
literal|"MboxParser-received"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuoted
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|getStream
argument_list|(
literal|"/test-documents/quoted.mbox"
argument_list|)
decl_stmt|;
try|try
block|{
name|mboxParser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|recursingContext
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertContains
argument_list|(
literal|"Test content"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"> quoted stuff"
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
name|testComplex
parameter_list|()
throws|throws
name|Exception
block|{
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|getStream
argument_list|(
literal|"/test-documents/complex.mbox"
argument_list|)
decl_stmt|;
try|try
block|{
name|mboxParser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|recursingContext
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Nb. Of mails"
argument_list|,
literal|3
argument_list|,
name|mboxParser
operator|.
name|getTrackingMetadata
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|firstMail
init|=
name|mboxParser
operator|.
name|getTrackingMetadata
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Re: question about when shuffle/sort start working"
argument_list|,
name|firstMail
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Re: question about when shuffle/sort start working"
argument_list|,
name|firstMail
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Jothi Padmanabhan<jothipn@yahoo-inc.com>"
argument_list|,
name|firstMail
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Jothi Padmanabhan<jothipn@yahoo-inc.com>"
argument_list|,
name|firstMail
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"core-user@hadoop.apache.org"
argument_list|,
name|firstMail
operator|.
name|get
argument_list|(
name|Metadata
operator|.
name|MESSAGE_RECIPIENT_ADDRESS
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"When a Mapper completes"
argument_list|,
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

