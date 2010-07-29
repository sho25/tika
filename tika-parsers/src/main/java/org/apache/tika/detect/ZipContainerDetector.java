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
name|detect
package|;
end_package

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
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|extractor
operator|.
name|ExtractorFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|exceptions
operator|.
name|InvalidFormatException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|OPCPackage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|PackagePart
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|openxml4j
operator|.
name|opc
operator|.
name|PackageRelationshipCollection
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
name|mime
operator|.
name|MediaType
import|;
end_import

begin_comment
comment|/**  * A detector that works on a Zip document  *  to figure out exactly what the file is  */
end_comment

begin_class
specifier|public
class|class
name|ZipContainerDetector
implements|implements
name|Detector
block|{
specifier|public
name|MediaType
name|detect
parameter_list|(
name|InputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|instanceof
name|TikaInputStream
condition|)
block|{
return|return
name|detect
argument_list|(
operator|(
name|TikaInputStream
operator|)
name|input
argument_list|,
name|metadata
argument_list|)
return|;
block|}
return|return
name|detect
argument_list|(
name|TikaInputStream
operator|.
name|get
argument_list|(
name|input
argument_list|)
argument_list|,
name|metadata
argument_list|)
return|;
block|}
specifier|public
name|MediaType
name|detect
parameter_list|(
name|TikaInputStream
name|input
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
throws|throws
name|IOException
block|{
name|ZipInputStream
name|zip
init|=
operator|new
name|ZipInputStream
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|ZipEntry
name|entry
init|=
name|zip
operator|.
name|getNextEntry
argument_list|()
decl_stmt|;
while|while
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
comment|// Is it an Open Document file?
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"mimetype"
argument_list|)
condition|)
block|{
name|String
name|type
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|zip
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
return|return
name|fromString
argument_list|(
name|type
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"_rels/.rels"
argument_list|)
operator|||
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"[Content_Types].xml"
argument_list|)
condition|)
block|{
comment|// Office Open XML File
comment|// As POI to open and investigate it for us
try|try
block|{
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
name|OPCPackage
name|pkg
init|=
name|OPCPackage
operator|.
name|open
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|input
operator|.
name|setOpenContainer
argument_list|(
name|pkg
argument_list|)
expr_stmt|;
name|PackageRelationshipCollection
name|core
init|=
name|pkg
operator|.
name|getRelationshipsByType
argument_list|(
name|ExtractorFactory
operator|.
name|CORE_DOCUMENT_REL
argument_list|)
decl_stmt|;
if|if
condition|(
name|core
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid OOXML Package received - expected 1 core document, found "
operator|+
name|core
operator|.
name|size
argument_list|()
argument_list|)
throw|;
block|}
comment|// Get the type of the core document part
name|PackagePart
name|corePart
init|=
name|pkg
operator|.
name|getPart
argument_list|(
name|core
operator|.
name|getRelationship
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|coreType
init|=
name|corePart
operator|.
name|getContentType
argument_list|()
decl_stmt|;
comment|// Turn that into the type of the overall document
name|String
name|docType
init|=
name|coreType
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|coreType
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|fromString
argument_list|(
name|docType
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Office Open XML File detected, but corrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|entry
operator|=
name|zip
operator|.
name|getNextEntry
argument_list|()
expr_stmt|;
block|}
return|return
name|MediaType
operator|.
name|APPLICATION_ZIP
return|;
block|}
specifier|private
specifier|static
name|MediaType
name|fromString
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|int
name|splitAt
init|=
name|type
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|splitAt
operator|>
operator|-
literal|1
condition|)
block|{
return|return
operator|new
name|MediaType
argument_list|(
name|type
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|splitAt
argument_list|)
argument_list|,
name|type
operator|.
name|substring
argument_list|(
name|splitAt
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
return|return
name|MediaType
operator|.
name|APPLICATION_ZIP
return|;
block|}
block|}
end_class

end_unit

