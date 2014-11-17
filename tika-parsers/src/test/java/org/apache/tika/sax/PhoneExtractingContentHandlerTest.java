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
name|junit
operator|.
name|Test
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

begin_comment
comment|/**  * Test class for the {@link org.apache.tika.sax.PhoneExtractingContentHandler}  * class. This demonstrates how to parse a document and retrieve any phone numbers  * found within.  *  * The phone numbers are added to a multivalued Metadata object under the key, "phonenumbers".  * You can get an array of phone numbers by calling metadata.getValues("phonenumber").  */
end_comment

begin_class
specifier|public
class|class
name|PhoneExtractingContentHandlerTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testExtractPhoneNumbers
parameter_list|()
throws|throws
name|Exception
block|{
name|Parser
name|parser
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
comment|// The PhoneExtractingContentHandler will examine any characters for phone numbers before passing them
comment|// to the underlying Handler.
name|PhoneExtractingContentHandler
name|handler
init|=
operator|new
name|PhoneExtractingContentHandler
argument_list|(
operator|new
name|BodyContentHandler
argument_list|()
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|InputStream
name|stream
init|=
name|PhoneExtractingContentHandlerTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testPhoneNumberExtractor.odt"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
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
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|phoneNumbers
init|=
name|metadata
operator|.
name|getValues
argument_list|(
literal|"phonenumbers"
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
literal|"9498888888"
argument_list|,
name|phoneNumbers
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"9497777777"
argument_list|,
name|phoneNumbers
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"9496666666"
argument_list|,
name|phoneNumbers
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"9495555555"
argument_list|,
name|phoneNumbers
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"4193404645"
argument_list|,
name|phoneNumbers
index|[
literal|4
index|]
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"9044687081"
argument_list|,
name|phoneNumbers
index|[
literal|5
index|]
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|"2604094811"
argument_list|,
name|phoneNumbers
index|[
literal|6
index|]
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
