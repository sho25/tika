begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|odf
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
name|Arrays
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
name|OfficeOpenXMLCore
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
name|OfficeOpenXMLExtended
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
name|EmptyParser
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
name|opendocument
operator|.
name|OpenOfficeParser
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
name|ODFParserTest
extends|extends
name|TikaTest
block|{
comment|/**      * For now, allow us to run some tests against both      *  the old and the new parser      */
specifier|private
name|Parser
index|[]
name|getParsers
parameter_list|()
block|{
return|return
operator|new
name|Parser
index|[]
block|{
operator|new
name|OpenDocumentParser
argument_list|()
block|,
operator|new
name|OpenOfficeParser
argument_list|()
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOO3
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Parser
name|parser
range|:
name|getParsers
argument_list|()
control|)
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|ODFParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testODFwithOOo3.odt"
argument_list|)
init|)
block|{
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
name|BodyContentHandler
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
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
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
literal|"Tika is part of the Lucene project."
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Solr"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"one embedded"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Rectangle Title"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"a blue background and dark border"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOO2
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Parser
name|parser
range|:
name|getParsers
argument_list|()
control|)
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|ODFParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testOpenOffice2.odt"
argument_list|)
init|)
block|{
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
name|BodyContentHandler
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
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
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
name|assertEquals
argument_list|(
literal|"en-US"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|LANGUAGE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PT1M7S"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|TOTAL_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"NeoOffice/2.2$Unix OpenOffice.org_project/680m18$Build-9161"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"generator"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check date metadata, both old-style and new-style
name|assertEquals
argument_list|(
literal|"2007-09-14T11:07:10"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2007-09-14T11:06:08"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check the document statistics
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|PAGE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|PARAGRAPH_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"14"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|WORD_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"78"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|CHARACTER_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|TABLE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|OBJECT_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|IMAGE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
comment|// Custom metadata tags present but without values
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 4"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"This is a sample Open Office document,"
operator|+
literal|" written in NeoOffice 2.2.1 for the Mac."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**     * Similar to {@link #testOO2()}, but using a different     *  OO2 file with different metadata in it     */
annotation|@
name|Test
specifier|public
name|void
name|testOO2Metadata
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|ODFParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testOpenOffice2.odf"
argument_list|)
init|)
block|{
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
name|BodyContentHandler
argument_list|()
decl_stmt|;
operator|new
name|OpenDocumentParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.formula"
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
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2006-01-27T11:55:22"
argument_list|,
name|metadata
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
literal|"The quick brown fox jumps over the lazy dog"
argument_list|,
name|metadata
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
literal|"Gym class featuring a brown fox and lazy dog"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Gym class featuring a brown fox and lazy dog"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PT0S"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|TOTAL_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"editing-cycles"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OpenOffice.org/2.2$Win32 OpenOffice.org_project/680m14$Build-9134"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"generator"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Pangram, fox, dog"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
comment|// User defined metadata
name|assertEquals
argument_list|(
literal|"Text 1"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 4"
argument_list|)
argument_list|)
expr_stmt|;
comment|// No statistics present
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|PAGE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|PARAGRAPH_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|WORD_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|CHARACTER_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|TABLE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|OBJECT_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|IMAGE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"nbTab"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"nbObject"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"nbImg"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"nbPage"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"nbPara"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"nbWord"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"nbCharacter"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Note - contents of maths files not currently supported
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Similar to {@link #testOO2()} )}, but using an OO3 file     */
annotation|@
name|Test
specifier|public
name|void
name|testOO3Metadata
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|ODFParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testODFwithOOo3.odt"
argument_list|)
init|)
block|{
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
name|BodyContentHandler
argument_list|()
decl_stmt|;
operator|new
name|OpenDocumentParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
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
name|assertEquals
argument_list|(
literal|"2009-10-05T21:22:38"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2009-10-05T19:04:01"
argument_list|,
name|metadata
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
literal|"2009-10-05T19:04:01"
argument_list|,
name|metadata
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
literal|"Apache Tika"
argument_list|,
name|metadata
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
literal|"Test document"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLCore
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Test document"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A rather complex document"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bart Hanssens"
argument_list|,
name|metadata
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
literal|"2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"editing-cycles"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"PT02H03M24S"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|TOTAL_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"OpenOffice.org/3.1$Unix OpenOffice.org_project/310m19$Build-9420"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"generator"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Apache, Lucene, Tika"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
comment|// User defined metadata
name|assertEquals
argument_list|(
literal|"Bart Hanssens"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Editor"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"custom:Info 4"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check the document statistics
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|PAGE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"13"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|PARAGRAPH_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"54"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|WORD_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"351"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|CHARACTER_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|TABLE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|OBJECT_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|Office
operator|.
name|IMAGE_COUNT
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|content
init|=
name|handler
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|content
operator|.
name|contains
argument_list|(
literal|"Apache Tika Tika is part of the Lucene project."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testODPMasterFooter
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|ODFParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testMasterFooter.odp"
argument_list|)
init|)
block|{
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
name|BodyContentHandler
argument_list|()
decl_stmt|;
operator|new
name|AutoDetectParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
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
literal|"Master footer is here"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testODTFooter
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|ODFParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testFooter.odt"
argument_list|)
init|)
block|{
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
name|BodyContentHandler
argument_list|()
decl_stmt|;
operator|new
name|AutoDetectParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
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
literal|"Here is some text..."
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Here is some text on page 2"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Here is footer text"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testODSFooter
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|ODFParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testFooter.ods"
argument_list|)
init|)
block|{
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
name|BodyContentHandler
argument_list|()
decl_stmt|;
operator|new
name|AutoDetectParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
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
literal|"Here is a footer in the center area"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFromFile
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/test-documents/testODFwithOOo3.odt"
argument_list|)
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|tis
operator|.
name|hasFile
argument_list|()
argument_list|)
expr_stmt|;
name|OpenDocumentParser
name|parser
init|=
operator|new
name|OpenDocumentParser
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
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|tis
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
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
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
literal|"Tika is part of the Lucene project."
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNPEFromFile
parameter_list|()
throws|throws
name|Exception
block|{
name|OpenDocumentParser
name|parser
init|=
operator|new
name|OpenDocumentParser
argument_list|()
decl_stmt|;
try|try
init|(
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/test-documents/testNPEOpenDocument.odt"
argument_list|)
argument_list|)
init|)
block|{
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
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|tis
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
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
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
literal|"primero hay que generar un par de claves"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TIKA-1063: Test basic style support.
annotation|@
name|Test
specifier|public
name|void
name|testODTStyles
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
name|getXML
argument_list|(
literal|"testStyles.odt"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"This<i>is</i><b>just</b> a<u>test</u>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<p>And<b>another<i>test</i> is</b> here.</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<ol>\t<li><p>One</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"</ol>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"<ul>\t<li><p>First</p>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"</ul>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
comment|//TIKA-1600: Test that null pointer doesn't break parsing.
annotation|@
name|Test
specifier|public
name|void
name|testNullStylesInODTFooter
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
init|=
operator|new
name|OpenDocumentParser
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|input
init|=
name|ODFParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testODT-TIKA-6000.odt"
argument_list|)
init|)
block|{
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
name|BodyContentHandler
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
name|getNonRecursingParseContext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
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
literal|"Utilisation de ce document"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Copyright and License"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"Changer la langue"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"La page d’accueil permet de faire une recherche simple"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
comment|//TIKA-1916
specifier|public
name|void
name|testMissingMeta
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
name|getXML
argument_list|(
literal|"testODTNoMeta.odt"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"Test text"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|//TIKA-2242
specifier|public
name|void
name|testParagraphLevelFontStyles
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|xml
init|=
name|getXML
argument_list|(
literal|"testODTStyles2.odt"
argument_list|,
name|getNonRecursingParseContext
argument_list|()
argument_list|)
operator|.
name|xml
decl_stmt|;
comment|//test text span font-style properties
name|assertContains
argument_list|(
literal|"<p><b>name</b>, advocaat"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
comment|//test paragraph's font-style properties
name|assertContains
argument_list|(
literal|"<p><b>Publicatie Onbekwaamverklaring"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|//TIKA-2242
specifier|public
name|void
name|testAnnotationsAndPDepthGt1
parameter_list|()
throws|throws
name|Exception
block|{
comment|//not allowed in html:<p><annotation><p> this is an annotation</p></annotation></p>
name|String
name|xml
init|=
name|getXML
argument_list|(
literal|"testODTStyles3.odt"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertContains
argument_list|(
literal|"<p><b>WOUTERS Rolf</b><span class=\"annotation\"> Beschermde persoon is overleden</annotation>"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmbedded
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
literal|"testODTEmbedded.odt"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|metadataList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidFromStream
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|is
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/test-documents/testODTnotaZipFile.odt"
argument_list|)
operator|.
name|openStream
argument_list|()
init|)
block|{
name|OpenDocumentParser
name|parser
init|=
operator|new
name|OpenDocumentParser
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
name|BodyContentHandler
argument_list|()
decl_stmt|;
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
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testInvalidFromFile
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/test-documents/testODTnotaZipFile.odt"
argument_list|)
argument_list|)
init|)
block|{
name|OpenDocumentParser
name|parser
init|=
operator|new
name|OpenDocumentParser
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
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|tis
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
block|}
specifier|private
name|ParseContext
name|getNonRecursingParseContext
parameter_list|()
block|{
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|parseContext
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
operator|new
name|EmptyParser
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|parseContext
return|;
block|}
block|}
end_class

end_unit

