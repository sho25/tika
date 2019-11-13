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
name|microsoft
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
name|exception
operator|.
name|EncryptedDocumentException
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
name|PasswordProvider
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
name|RecursiveParserWrapperHandler
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

begin_class
specifier|public
class|class
name|JackcessParserTest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|RecursiveParserWrapper
name|w
init|=
operator|new
name|RecursiveParserWrapper
argument_list|(
name|AUTO_DETECT_PARSER
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|fName
range|:
operator|new
name|String
index|[]
block|{
literal|"testAccess2.accdb"
block|,
literal|"testAccess2_2000.mdb"
block|,
literal|"testAccess2_2002-2003.mdb"
block|}
control|)
block|{
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|RecursiveParserWrapperHandler
name|handler
init|=
operator|new
name|RecursiveParserWrapperHandler
argument_list|(
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
block|{
name|is
operator|=
name|this
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|fName
argument_list|)
expr_stmt|;
name|Metadata
name|meta
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|ParseContext
name|c
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|w
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|handler
argument_list|,
name|meta
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Metadata
argument_list|>
name|list
init|=
name|handler
operator|.
name|getMetadataList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|mainContent
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapperHandler
operator|.
name|TIKA_CONTENT
argument_list|)
decl_stmt|;
comment|//make sure there's a thead and tbody
name|assertContains
argument_list|(
literal|"</thead><tbody>"
argument_list|,
name|mainContent
argument_list|)
expr_stmt|;
comment|//assert table header
name|assertContains
argument_list|(
literal|"<th>ShortTextField</th>"
argument_list|,
name|mainContent
argument_list|)
expr_stmt|;
comment|//test date format
comment|//java 8 is 6/24/15 ...java 10 is 2015-06-24
name|assertTrue
argument_list|(
name|mainContent
operator|.
name|contains
argument_list|(
literal|"6/24/15"
argument_list|)
operator|||
name|mainContent
operator|.
name|contains
argument_list|(
literal|"2015-06-24"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test that markup is stripped
name|assertContains
argument_list|(
literal|"over the bold italic dog"
argument_list|,
name|mainContent
argument_list|)
expr_stmt|;
comment|//test unicode
name|assertContains
argument_list|(
literal|"\u666E\u6797\u65AF\u987F\u5927\u5B66"
argument_list|,
name|mainContent
argument_list|)
expr_stmt|;
comment|//test embedded document handling
name|assertContains
argument_list|(
literal|"Test Document with embedded pdf"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|(
name|RecursiveParserWrapperHandler
operator|.
name|TIKA_CONTENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPassword
parameter_list|()
throws|throws
name|Exception
block|{
name|ParseContext
name|c
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|c
operator|.
name|set
argument_list|(
name|PasswordProvider
operator|.
name|class
argument_list|,
operator|new
name|PasswordProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
literal|"tika"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|String
name|content
init|=
literal|null
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|this
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testAccess2_encrypted.accdb"
argument_list|)
init|)
block|{
name|content
operator|=
name|getText
argument_list|(
name|is
argument_list|,
name|AUTO_DETECT_PARSER
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|assertContains
argument_list|(
literal|"red and brown"
argument_list|,
name|content
argument_list|)
expr_stmt|;
comment|//now try wrong password
name|c
operator|.
name|set
argument_list|(
name|PasswordProvider
operator|.
name|class
argument_list|,
operator|new
name|PasswordProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
literal|"WRONG"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|boolean
name|ex
init|=
literal|false
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|this
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testAccess2_encrypted.accdb"
argument_list|)
init|)
block|{
name|getText
argument_list|(
name|is
argument_list|,
name|AUTO_DETECT_PARSER
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EncryptedDocumentException
name|e
parameter_list|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"failed to throw encrypted document exception for wrong password"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
comment|//now try null
name|c
operator|.
name|set
argument_list|(
name|PasswordProvider
operator|.
name|class
argument_list|,
operator|new
name|PasswordProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|ex
operator|=
literal|false
expr_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|this
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testAccess2_encrypted.accdb"
argument_list|)
init|)
block|{
name|getText
argument_list|(
name|is
argument_list|,
name|AUTO_DETECT_PARSER
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EncryptedDocumentException
name|e
parameter_list|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"failed to throw encrypted document exception for null password"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
comment|//now try missing password provider
name|c
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|ex
operator|=
literal|false
expr_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|this
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testAccess2_encrypted.accdb"
argument_list|)
init|)
block|{
name|getText
argument_list|(
name|is
argument_list|,
name|AUTO_DETECT_PARSER
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EncryptedDocumentException
name|e
parameter_list|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"failed to throw encrypted document exception for missing password provider"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
comment|//now try password on file that doesn't need a password
name|c
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|c
operator|.
name|set
argument_list|(
name|PasswordProvider
operator|.
name|class
argument_list|,
operator|new
name|PasswordProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
block|{
return|return
literal|"tika"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|ex
operator|=
literal|false
expr_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|this
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testAccess2.accdb"
argument_list|)
init|)
block|{
name|content
operator|=
name|getText
argument_list|(
name|is
argument_list|,
name|AUTO_DETECT_PARSER
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EncryptedDocumentException
name|e
parameter_list|)
block|{
name|ex
operator|=
literal|true
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"shouldn't have thrown encrypted document exception for "
operator|+
literal|"opening unencrypted file that doesn't need passowrd"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"red and brown"
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadOnly
parameter_list|()
throws|throws
name|Exception
block|{
comment|//TIKA-1681: just make sure an exception is not thrown
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testAccess_V1997.mdb"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"hijklmnop"
argument_list|,
name|r
operator|.
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMetadata
parameter_list|()
throws|throws
name|Exception
block|{
comment|//basic tests for normalized metadata
name|XMLResult
name|r
init|=
name|getXML
argument_list|(
literal|"testAccess_V1997.mdb"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"tmccune"
argument_list|,
name|r
operator|.
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
literal|"Health Market Science"
argument_list|,
name|r
operator|.
name|metadata
operator|.
name|get
argument_list|(
name|OfficeOpenXMLExtended
operator|.
name|COMPANY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|r
operator|.
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
block|}
block|}
end_class

end_unit

