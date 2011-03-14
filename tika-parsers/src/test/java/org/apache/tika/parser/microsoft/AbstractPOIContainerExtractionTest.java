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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|ContainerExtractor
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
name|mime
operator|.
name|MediaType
import|;
end_import

begin_comment
comment|/**  * Parent class of tests that the various POI powered parsers are  *  able to extract their embedded contents.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPOIContainerExtractionTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_DOC
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"msword"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_PPT
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_XLS
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_DOCX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_PPTX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_XLSX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_MSG
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-outlook"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_TXT
init|=
name|MediaType
operator|.
name|text
argument_list|(
literal|"plain"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_PDF
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"pdf"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_JPG
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"jpeg"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_GIF
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"gif"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_PNG
init|=
name|MediaType
operator|.
name|image
argument_list|(
literal|"png"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_EMF
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-msmetafile"
argument_list|)
decl_stmt|;
specifier|protected
name|TrackingHandler
name|process
parameter_list|(
name|String
name|filename
parameter_list|,
name|ContainerExtractor
name|extractor
parameter_list|,
name|boolean
name|recurse
parameter_list|)
throws|throws
name|Exception
block|{
name|TikaInputStream
name|stream
init|=
name|getTestFile
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|extractor
operator|.
name|isSupported
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
comment|// Process it
name|TrackingHandler
name|handler
init|=
operator|new
name|TrackingHandler
argument_list|()
decl_stmt|;
if|if
condition|(
name|recurse
condition|)
block|{
name|extractor
operator|.
name|extract
argument_list|(
name|stream
argument_list|,
name|extractor
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|extractor
operator|.
name|extract
argument_list|(
name|stream
argument_list|,
literal|null
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
comment|// So they can check what happened
return|return
name|handler
return|;
block|}
specifier|protected
name|TikaInputStream
name|getTestFile
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|Exception
block|{
name|InputStream
name|input
init|=
name|AbstractPOIContainerExtractionTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/"
operator|+
name|filename
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|filename
operator|+
literal|" not found"
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|TikaInputStream
name|stream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|stream
argument_list|)
expr_stmt|;
return|return
name|stream
return|;
block|}
specifier|protected
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
name|filenames
operator|.
name|add
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|mediaTypes
operator|.
name|add
argument_list|(
name|mediaType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

