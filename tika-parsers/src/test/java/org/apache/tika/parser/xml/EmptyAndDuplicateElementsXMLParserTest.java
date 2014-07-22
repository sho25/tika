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
name|Property
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
name|TeeContentHandler
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
name|EmptyAndDuplicateElementsXMLParserTest
extends|extends
name|TikaTest
block|{
specifier|private
name|Property
name|FIRST_NAME
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
literal|"custom"
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"FirstName"
argument_list|)
decl_stmt|;
specifier|private
name|Property
name|LAST_NAME
init|=
name|Property
operator|.
name|internalTextBag
argument_list|(
literal|"custom"
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"LastName"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testDefaultBehavior
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|EmptyAndDuplicateElementsXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testXML3.xml"
argument_list|)
decl_stmt|;
try|try
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
name|DefaultCustomXMLTestParser
argument_list|()
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
literal|4
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|LAST_NAME
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"John"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Smith"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|LAST_NAME
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Jane"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Doe"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|LAST_NAME
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// We didn't know Bob's last name, but now we don't know an entry existed
name|assertEquals
argument_list|(
literal|"Bob"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
comment|// We don't know Kate's last name because it was a duplicate
name|assertEquals
argument_list|(
literal|"Kate"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
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
annotation|@
name|Test
specifier|public
name|void
name|testEmptiesAndRepeats
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|EmptyAndDuplicateElementsXMLParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testXML3.xml"
argument_list|)
decl_stmt|;
try|try
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
name|AllowEmptiesAndDuplicatesCustomXMLTestParser
argument_list|()
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
literal|4
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|LAST_NAME
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"John"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Smith"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|LAST_NAME
argument_list|)
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Jane"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Doe"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|LAST_NAME
argument_list|)
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bob"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|LAST_NAME
argument_list|)
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Kate"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|FIRST_NAME
argument_list|)
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Smith"
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
name|LAST_NAME
argument_list|)
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
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
specifier|private
class|class
name|DefaultCustomXMLTestParser
extends|extends
name|XMLParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|2458579047014545931L
decl_stmt|;
specifier|protected
name|ElementMetadataHandler
name|getCustomElementHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Property
name|tikaProperty
parameter_list|,
name|String
name|localPart
parameter_list|)
block|{
return|return
operator|new
name|ElementMetadataHandler
argument_list|(
literal|"http://custom"
argument_list|,
name|localPart
argument_list|,
name|metadata
argument_list|,
name|tikaProperty
argument_list|)
return|;
block|}
specifier|protected
name|ContentHandler
name|getContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|TeeContentHandler
argument_list|(
name|super
operator|.
name|getContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
argument_list|,
name|getCustomElementHandler
argument_list|(
name|metadata
argument_list|,
name|FIRST_NAME
argument_list|,
literal|"FirstName"
argument_list|)
argument_list|,
name|getCustomElementHandler
argument_list|(
name|metadata
argument_list|,
name|LAST_NAME
argument_list|,
literal|"LastName"
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
class|class
name|AllowEmptiesAndDuplicatesCustomXMLTestParser
extends|extends
name|DefaultCustomXMLTestParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3735646809954466229L
decl_stmt|;
specifier|protected
name|ElementMetadataHandler
name|getCustomElementHandler
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Property
name|tikaProperty
parameter_list|,
name|String
name|localPart
parameter_list|)
block|{
return|return
operator|new
name|ElementMetadataHandler
argument_list|(
literal|"http://custom"
argument_list|,
name|localPart
argument_list|,
name|metadata
argument_list|,
name|tikaProperty
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

