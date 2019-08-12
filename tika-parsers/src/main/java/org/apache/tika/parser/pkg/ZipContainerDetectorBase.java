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
name|pkg
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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

begin_class
specifier|abstract
class|class
name|ZipContainerDetectorBase
block|{
specifier|static
specifier|final
name|MediaType
name|TIKA_OOXML
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-ooxml"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|DOCX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.wordprocessingml.document"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|DOCM
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-word.document.macroEnabled.12"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|DOTM
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-word.template.macroenabled.12"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|DOTX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.wordprocessingml.template"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|PPTX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.presentationml.presentation"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|PPSM
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint.slideshow.macroEnabled.12"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|PPSX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.presentationml.slideshow"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|PPTM
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint.presentation.macroEnabled.12"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|POTM
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-powerpoint.template.macroenabled.12"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|POTX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.presentationml.template"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|THMX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|XLSB
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.sheet.binary.macroenabled.12"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|XLSX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.spreadsheetml.sheet"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|XLSM
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.sheet.macroEnabled.12"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|XLTM
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.template.macroenabled.12"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|XLTX
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.openxmlformats-officedocument.spreadsheetml.template"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|XLAM
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-excel.addin.macroEnabled.12"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MediaType
name|XPS
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.ms-xpsdocument"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|OOXML_HINTS
init|=
name|fillSet
argument_list|(
literal|"word/document.xml"
argument_list|,
literal|"_rels/.rels"
argument_list|,
literal|"[Content_Types].xml"
argument_list|,
literal|"ppt/presentation.xml"
argument_list|,
literal|"ppt/slides/slide1.xml"
argument_list|,
literal|"xl/workbook.xml"
argument_list|,
literal|"xl/sharedStrings.xml"
argument_list|,
literal|"xl/worksheets/sheet1.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|fillSet
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|tmp
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|tmp
argument_list|)
return|;
block|}
specifier|static
name|MediaType
name|detectJar
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|entryNames
parameter_list|)
block|{
if|if
condition|(
name|entryNames
operator|.
name|contains
argument_list|(
literal|"META-INF/MANIFEST.MF"
argument_list|)
condition|)
block|{
comment|// It's a Jar file, or something based on Jar
comment|// Is it an Android APK?
if|if
condition|(
name|entryNames
operator|.
name|contains
argument_list|(
literal|"AndroidManifest.xml"
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.android.package-archive"
argument_list|)
return|;
block|}
comment|// Check for WAR and EAR
if|if
condition|(
name|entryNames
operator|.
name|contains
argument_list|(
literal|"WEB-INF/"
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-java-web-archive"
argument_list|)
return|;
block|}
if|if
condition|(
name|entryNames
operator|.
name|contains
argument_list|(
literal|"META-INF/application.xml"
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tika-java-enterprise-archive"
argument_list|)
return|;
block|}
comment|// Looks like a regular Jar Archive
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"java-archive"
argument_list|)
return|;
block|}
else|else
block|{
comment|// Some Android APKs miss the default Manifest
if|if
condition|(
name|entryNames
operator|.
name|contains
argument_list|(
literal|"AndroidManifest.xml"
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.android.package-archive"
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
specifier|static
name|MediaType
name|detectKmz
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|entryFileNames
parameter_list|)
block|{
comment|//look for a single kml at the main level
name|boolean
name|kmlFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|entryFileName
range|:
name|entryFileNames
control|)
block|{
if|if
condition|(
name|entryFileName
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|!=
operator|-
literal|1
operator|||
name|entryFileName
operator|.
name|indexOf
argument_list|(
literal|'\\'
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|entryFileName
operator|.
name|endsWith
argument_list|(
literal|".kml"
argument_list|)
operator|&&
operator|!
name|kmlFound
condition|)
block|{
name|kmlFound
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
name|kmlFound
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"vnd.google-earth.kmz"
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * To be considered as an IPA file, it needs to match all of these      */
specifier|private
specifier|static
name|HashSet
argument_list|<
name|Pattern
argument_list|>
name|ipaEntryPatterns
init|=
operator|new
name|HashSet
argument_list|<
name|Pattern
argument_list|>
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6545295886322115362L
decl_stmt|;
block|{
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/_CodeSignature/$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/_CodeSignature/CodeResources$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/Info\\.plist$"
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^Payload/.*\\.app/PkgInfo$"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|static
name|MediaType
name|detectIpa
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|entryNames
parameter_list|)
block|{
comment|// Note - consider generalising this logic, if another format needs many regexp matching
name|Set
argument_list|<
name|Pattern
argument_list|>
name|tmpPatterns
init|=
operator|(
name|Set
argument_list|<
name|Pattern
argument_list|>
operator|)
name|ipaEntryPatterns
operator|.
name|clone
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|entryName
range|:
name|entryNames
control|)
block|{
name|Iterator
argument_list|<
name|Pattern
argument_list|>
name|ip
init|=
name|tmpPatterns
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|ip
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
name|ip
operator|.
name|next
argument_list|()
operator|.
name|matcher
argument_list|(
name|entryName
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|ip
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tmpPatterns
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// We've found everything we need to find
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-itunes-ipa"
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

