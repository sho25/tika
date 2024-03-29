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
name|xml
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
name|InputStream
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

begin_class
specifier|public
class|class
name|DcXMLParserTest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testXMLParserAsciiChars
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|DcXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testXML.xml"
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
name|DcXMLParser
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
literal|"application/xml"
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
literal|"Tika test document"
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
literal|"Rida Benjelloun"
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
comment|// The file contains 5 dc:subject tags, which come through as
comment|//  a multi-valued Tika Metadata entry in file order
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|metadata
operator|.
name|isMultiValued
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Java"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"XML"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"XSLT"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"JDOM"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Indexation"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|TikaCoreProperties
operator|.
name|SUBJECT
argument_list|)
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Framework d\'indexation des documents XML, HTML, PDF etc.."
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
literal|"http://www.apache.org"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|IDENTIFIER
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/msword"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|FORMAT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Fr"
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
name|assertTrue
argument_list|(
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|RIGHTS
argument_list|)
operator|.
name|contains
argument_list|(
literal|"testing chars"
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
literal|"Tika test document"
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2000-12-01T00:00:00.000Z"
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
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXMLParserNonAsciiChars
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|InputStream
name|input
init|=
name|DcXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testXML.xml"
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
operator|new
name|DcXMLParser
argument_list|()
operator|.
name|parse
argument_list|(
name|input
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"Archim\u00E8de et Lius \u00E0 Ch\u00E2teauneuf testing chars en \u00E9t\u00E9"
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
name|TikaCoreProperties
operator|.
name|RIGHTS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TIKA-1048
annotation|@
name|Test
specifier|public
name|void
name|testNoSpaces
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|text
init|=
name|getXML
argument_list|(
literal|"testXML2.xml"
argument_list|)
operator|.
name|xml
decl_stmt|;
name|assertFalse
argument_list|(
name|text
operator|.
name|contains
argument_list|(
literal|"testSubject"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

