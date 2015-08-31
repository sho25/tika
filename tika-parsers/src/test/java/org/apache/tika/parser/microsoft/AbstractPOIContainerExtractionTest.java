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
name|assertNotNull
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|TikaTest
operator|.
name|TrackingHandler
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
comment|/**  * Parent class of tests that the various POI powered parsers are  * able to extract their embedded contents.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPOIContainerExtractionTest
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
literal|"x-emf"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MediaType
name|TYPE_WMF
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-msmetafile"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|TikaInputStream
name|getTestFile
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|Exception
block|{
name|URL
name|input
init|=
name|AbstractPOIContainerExtractionTest
operator|.
name|class
operator|.
name|getResource
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
return|return
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
argument_list|)
return|;
block|}
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
try|try
init|(
name|TikaInputStream
name|stream
init|=
name|getTestFile
argument_list|(
name|filename
argument_list|)
init|)
block|{
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
block|}
block|}
end_class

end_unit

