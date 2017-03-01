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
name|List
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
name|TikaTest
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
name|EmbeddedDocumentExtractor
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
name|ParsingEmbeddedDocumentExtractor
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
name|Message
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
name|Office
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
name|sax
operator|.
name|ToHTMLContentHandler
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

begin_class
specifier|public
class|class
name|OutlookPSTParserTest
extends|extends
name|TikaTest
block|{
specifier|private
name|Parser
name|parser
init|=
operator|new
name|OutlookPSTParser
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testAccept
parameter_list|()
throws|throws
name|Exception
block|{
name|assertTrue
argument_list|(
operator|(
name|parser
operator|.
name|getSupportedTypes
argument_list|(
literal|null
argument_list|)
operator|.
name|contains
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-outlook-pst"
argument_list|)
argument_list|)
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParse
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|pstParser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|ToHTMLContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|EmbeddedTrackingExtrator
name|trackingExtrator
init|=
operator|new
name|EmbeddedTrackingExtrator
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|context
operator|.
name|set
argument_list|(
name|EmbeddedDocumentExtractor
operator|.
name|class
argument_list|,
name|trackingExtrator
argument_list|)
expr_stmt|;
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|)
expr_stmt|;
name|pstParser
operator|.
name|parse
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPST.pst"
argument_list|)
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
name|output
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|output
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"<meta name=\"Content-Length\" content=\"271360\">"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"<meta name=\"Content-Type\" content=\"application/vnd.ms-outlook-pst\">"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"<body><div class=\"email-folder\"><h1>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"<div class=\"embedded\" id=\"&lt;530D9CAC.5080901@gmail.com&gt;\"><h1>Re: Feature Generators</h1>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"<div class=\"embedded\" id=\"&lt;1393363252.28814.YahooMailNeo@web140906.mail.bf1.yahoo.com&gt;\"><h1>Re: init tokenizer fails: \"Bad type in putfield/putstatic\"</h1>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"Gary Murphy commented on TIKA-1250:"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|output
operator|.
name|contains
argument_list|(
literal|"<div class=\"email-folder\"><h1>Racine (pour la recherche)</h1>"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Metadata
argument_list|>
name|metaList
init|=
name|trackingExtrator
operator|.
name|trackingMetadata
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|metaList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Metadata
name|firstMail
init|=
name|metaList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Jörn Kottmann"
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
literal|"Re: Feature Generators"
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
literal|"kottmann@gmail.com"
argument_list|,
name|firstMail
operator|.
name|get
argument_list|(
literal|"senderEmailAddress"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"users@opennlp.apache.org"
argument_list|,
name|firstMail
operator|.
name|get
argument_list|(
literal|"displayTo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|firstMail
operator|.
name|get
argument_list|(
literal|"displayCC"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|firstMail
operator|.
name|get
argument_list|(
literal|"displayBCC"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|EmbeddedTrackingExtrator
extends|extends
name|ParsingEmbeddedDocumentExtractor
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|trackingMetadata
init|=
operator|new
name|ArrayList
argument_list|<
name|Metadata
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|EmbeddedTrackingExtrator
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|shouldParseEmbedded
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|parseEmbedded
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
name|boolean
name|outputHtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|this
operator|.
name|trackingMetadata
operator|.
name|add
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|super
operator|.
name|parseEmbedded
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|outputHtml
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtendedMetadata
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Metadata
argument_list|>
name|metadataList
init|=
name|getRecursiveMetadata
argument_list|(
literal|"testPST.pst"
argument_list|)
decl_stmt|;
name|Metadata
name|m1
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Jörn Kottmann"
argument_list|,
name|m1
operator|.
name|get
argument_list|(
name|Message
operator|.
name|MESSAGE_FROM_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"kottmann@gmail.com"
argument_list|,
name|m1
operator|.
name|get
argument_list|(
name|Message
operator|.
name|MESSAGE_FROM_EMAIL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Jörn Kottmann"
argument_list|,
name|m1
operator|.
name|get
argument_list|(
name|Office
operator|.
name|MAPI_FROM_REPRESENTING_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"kottmann@gmail.com"
argument_list|,
name|m1
operator|.
name|get
argument_list|(
name|Office
operator|.
name|MAPI_FROM_REPRESENTING_EMAIL
argument_list|)
argument_list|)
expr_stmt|;
name|Metadata
name|m6
init|=
name|metadataList
operator|.
name|get
argument_list|(
literal|6
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Couchbase"
argument_list|,
name|m6
operator|.
name|get
argument_list|(
name|Message
operator|.
name|MESSAGE_FROM_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"couchbase@couchbase.com"
argument_list|,
name|m6
operator|.
name|get
argument_list|(
name|Message
operator|.
name|MESSAGE_FROM_EMAIL
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Couchbase"
argument_list|,
name|m6
operator|.
name|get
argument_list|(
name|Office
operator|.
name|MAPI_FROM_REPRESENTING_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"couchbase@couchbase.com"
argument_list|,
name|m6
operator|.
name|get
argument_list|(
name|Office
operator|.
name|MAPI_FROM_REPRESENTING_EMAIL
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

