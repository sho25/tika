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
name|apache
operator|.
name|tika
operator|.
name|mime
operator|.
name|MediaType
operator|.
name|application
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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

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
name|poi
operator|.
name|poifs
operator|.
name|filesystem
operator|.
name|DirectoryEntry
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
name|poifs
operator|.
name|filesystem
operator|.
name|DirectoryNode
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
name|poifs
operator|.
name|filesystem
operator|.
name|DocumentInputStream
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
name|poifs
operator|.
name|filesystem
operator|.
name|DocumentNode
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
name|poifs
operator|.
name|filesystem
operator|.
name|Entry
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
name|poifs
operator|.
name|filesystem
operator|.
name|NPOIFSFileSystem
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
name|detect
operator|.
name|Detector
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
comment|/**  * A detector that works on a POIFS OLE2 document  *  to figure out exactly what the file is.  * This should work for all OLE2 documents, whether  *  they are ones supported by POI or not.  */
end_comment

begin_class
specifier|public
class|class
name|POIFSContainerDetector
implements|implements
name|Detector
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3028021741663605293L
decl_stmt|;
comment|/** An ASCII String "StarImpress" */
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|STAR_IMPRESS
init|=
operator|new
name|byte
index|[]
block|{
literal|0x53
block|,
literal|0x74
block|,
literal|0x61
block|,
literal|0x72
block|,
literal|0x49
block|,
literal|0x6d
block|,
literal|0x70
block|,
literal|0x72
block|,
literal|0x65
block|,
literal|0x73
block|,
literal|0x73
block|}
decl_stmt|;
comment|/** An ASCII String "StarDraw" */
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|STAR_DRAW
init|=
operator|new
name|byte
index|[]
block|{
literal|0x53
block|,
literal|0x74
block|,
literal|0x61
block|,
literal|0x72
block|,
literal|0x44
block|,
literal|0x72
block|,
literal|0x61
block|,
literal|0x77
block|}
decl_stmt|;
comment|/** An ASCII String "Quill96" for Works Files */
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|WORKS_QUILL96
init|=
operator|new
name|byte
index|[]
block|{
literal|0x51
block|,
literal|0x75
block|,
literal|0x69
block|,
literal|0x6c
block|,
literal|0x6c
block|,
literal|0x39
block|,
literal|0x36
block|}
decl_stmt|;
comment|/** The OLE base file format */
specifier|public
specifier|static
specifier|final
name|MediaType
name|OLE
init|=
name|application
argument_list|(
literal|"x-tika-msoffice"
argument_list|)
decl_stmt|;
comment|/** The protected OOXML base file format */
specifier|public
specifier|static
specifier|final
name|MediaType
name|OOXML_PROTECTED
init|=
name|application
argument_list|(
literal|"x-tika-ooxml-protected"
argument_list|)
decl_stmt|;
comment|/** General embedded document type within an OLE2 container */
specifier|public
specifier|static
specifier|final
name|MediaType
name|GENERAL_EMBEDDED
init|=
name|application
argument_list|(
literal|"x-tika-msoffice-embedded"
argument_list|)
decl_stmt|;
comment|/** An OLE10 Native embedded document within another OLE2 document */
specifier|public
specifier|static
specifier|final
name|MediaType
name|OLE10_NATIVE
init|=
operator|new
name|MediaType
argument_list|(
name|GENERAL_EMBEDDED
argument_list|,
literal|"format"
argument_list|,
literal|"ole10_native"
argument_list|)
decl_stmt|;
comment|/** Some other kind of embedded document, in a CompObj container within another OLE2 document */
specifier|public
specifier|static
specifier|final
name|MediaType
name|COMP_OBJ
init|=
operator|new
name|MediaType
argument_list|(
name|GENERAL_EMBEDDED
argument_list|,
literal|"format"
argument_list|,
literal|"comp_obj"
argument_list|)
decl_stmt|;
comment|/** Microsoft Excel */
specifier|public
specifier|static
specifier|final
name|MediaType
name|XLS
init|=
name|application
argument_list|(
literal|"vnd.ms-excel"
argument_list|)
decl_stmt|;
comment|/** Microsoft Word */
specifier|public
specifier|static
specifier|final
name|MediaType
name|DOC
init|=
name|application
argument_list|(
literal|"msword"
argument_list|)
decl_stmt|;
comment|/** Microsoft PowerPoint */
specifier|public
specifier|static
specifier|final
name|MediaType
name|PPT
init|=
name|application
argument_list|(
literal|"vnd.ms-powerpoint"
argument_list|)
decl_stmt|;
comment|/** Microsoft Publisher */
specifier|public
specifier|static
specifier|final
name|MediaType
name|PUB
init|=
name|application
argument_list|(
literal|"x-mspublisher"
argument_list|)
decl_stmt|;
comment|/** Microsoft Visio */
specifier|public
specifier|static
specifier|final
name|MediaType
name|VSD
init|=
name|application
argument_list|(
literal|"vnd.visio"
argument_list|)
decl_stmt|;
comment|/** Microsoft Works */
specifier|public
specifier|static
specifier|final
name|MediaType
name|WPS
init|=
name|application
argument_list|(
literal|"vnd.ms-works"
argument_list|)
decl_stmt|;
comment|/** Microsoft Works Spreadsheet 7.0 */
specifier|public
specifier|static
specifier|final
name|MediaType
name|XLR
init|=
name|application
argument_list|(
literal|"x-tika-msworks-spreadsheet"
argument_list|)
decl_stmt|;
comment|/** Microsoft Outlook */
specifier|public
specifier|static
specifier|final
name|MediaType
name|MSG
init|=
name|application
argument_list|(
literal|"vnd.ms-outlook"
argument_list|)
decl_stmt|;
comment|/** Microsoft Project */
specifier|public
specifier|static
specifier|final
name|MediaType
name|MPP
init|=
name|application
argument_list|(
literal|"vnd.ms-project"
argument_list|)
decl_stmt|;
comment|/** StarOffice Calc */
specifier|public
specifier|static
specifier|final
name|MediaType
name|SDC
init|=
name|application
argument_list|(
literal|"vnd.stardivision.calc"
argument_list|)
decl_stmt|;
comment|/** StarOffice Draw */
specifier|public
specifier|static
specifier|final
name|MediaType
name|SDA
init|=
name|application
argument_list|(
literal|"vnd.stardivision.draw"
argument_list|)
decl_stmt|;
comment|/** StarOffice Impress */
specifier|public
specifier|static
specifier|final
name|MediaType
name|SDD
init|=
name|application
argument_list|(
literal|"vnd.stardivision.impress"
argument_list|)
decl_stmt|;
comment|/** StarOffice Writer */
specifier|public
specifier|static
specifier|final
name|MediaType
name|SDW
init|=
name|application
argument_list|(
literal|"vnd.stardivision.writer"
argument_list|)
decl_stmt|;
comment|/** Regexp for matching the MPP Project Data stream */
specifier|private
specifier|static
specifier|final
name|Pattern
name|mppDataMatch
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\s\\s\\s\\d+"
argument_list|)
decl_stmt|;
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
comment|// Check if we have access to the document
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
comment|// If this is a TikaInputStream wrapping an already
comment|// parsed NPOIFileSystem/DirectoryNode, just get the
comment|// names from the root:
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|cast
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|tis
operator|!=
literal|null
condition|)
block|{
name|Object
name|container
init|=
name|tis
operator|.
name|getOpenContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|container
operator|instanceof
name|NPOIFSFileSystem
condition|)
block|{
name|names
operator|=
name|getTopLevelNames
argument_list|(
operator|(
operator|(
name|NPOIFSFileSystem
operator|)
name|container
operator|)
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|container
operator|instanceof
name|DirectoryNode
condition|)
block|{
name|names
operator|=
name|getTopLevelNames
argument_list|(
operator|(
name|DirectoryNode
operator|)
name|container
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|names
operator|==
literal|null
condition|)
block|{
comment|// Check if the document starts with the OLE header
name|input
operator|.
name|mark
argument_list|(
literal|8
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|input
operator|.
name|read
argument_list|()
operator|!=
literal|0xd0
operator|||
name|input
operator|.
name|read
argument_list|()
operator|!=
literal|0xcf
operator|||
name|input
operator|.
name|read
argument_list|()
operator|!=
literal|0x11
operator|||
name|input
operator|.
name|read
argument_list|()
operator|!=
literal|0xe0
operator|||
name|input
operator|.
name|read
argument_list|()
operator|!=
literal|0xa1
operator|||
name|input
operator|.
name|read
argument_list|()
operator|!=
literal|0xb1
operator|||
name|input
operator|.
name|read
argument_list|()
operator|!=
literal|0x1a
operator|||
name|input
operator|.
name|read
argument_list|()
operator|!=
literal|0xe1
condition|)
block|{
return|return
name|MediaType
operator|.
name|OCTET_STREAM
return|;
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
comment|// We can only detect the exact type when given a TikaInputStream
if|if
condition|(
name|names
operator|==
literal|null
operator|&&
name|tis
operator|!=
literal|null
condition|)
block|{
comment|// Look for known top level entry names to detect the document type
name|names
operator|=
name|getTopLevelNames
argument_list|(
name|tis
argument_list|)
expr_stmt|;
block|}
comment|// Detect based on the names (as available)
if|if
condition|(
name|tis
operator|!=
literal|null
operator|&&
name|tis
operator|.
name|getOpenContainer
argument_list|()
operator|!=
literal|null
operator|&&
name|tis
operator|.
name|getOpenContainer
argument_list|()
operator|instanceof
name|NPOIFSFileSystem
condition|)
block|{
return|return
name|detect
argument_list|(
name|names
argument_list|,
operator|(
operator|(
name|NPOIFSFileSystem
operator|)
name|tis
operator|.
name|getOpenContainer
argument_list|()
operator|)
operator|.
name|getRoot
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|detect
argument_list|(
name|names
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
comment|/**      * Internal detection of the specific kind of OLE2 document, based on the      * names of the top level streams within the file.      *       * @deprecated Use {@link #detect(Set, DirectoryEntry)} and pass the root      *             entry of the filesystem whose type is to be detected, as a      *             second argument.      */
specifier|protected
specifier|static
name|MediaType
name|detect
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
return|return
name|detect
argument_list|(
name|names
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Internal detection of the specific kind of OLE2 document, based on the      * names of the top-level streams within the file. In some cases the      * detection may need access to the root {@link DirectoryEntry} of that file      * for best results. The entry can be given as a second, optional argument.      *       * @param names      * @param root      * @return      */
specifier|protected
specifier|static
name|MediaType
name|detect
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|DirectoryEntry
name|root
parameter_list|)
block|{
if|if
condition|(
name|names
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"StarCalcDocument"
argument_list|)
condition|)
block|{
comment|// Star Office Calc
return|return
name|SDC
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"StarWriterDocument"
argument_list|)
condition|)
block|{
return|return
name|SDW
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"StarDrawDocument3"
argument_list|)
condition|)
block|{
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
comment|/*                      * This is either StarOfficeDraw or StarOfficeImpress, we have                      * to consult the CompObj to distinguish them, if this method is                      * called in "legacy mode", without the root, just return                      * x-tika-msoffice. The one-argument method is only for backward                      * compatibility, if someone calls old API he/she can get the                      * old result.                      */
return|return
name|OLE
return|;
block|}
else|else
block|{
return|return
name|processCompObjFormatType
argument_list|(
name|root
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"WksSSWorkBook"
argument_list|)
condition|)
block|{
comment|// This check has to be before names.contains("Workbook")
comment|// Works 7.0 spreadsheet files contain both
comment|// we want to avoid classifying this as Excel
return|return
name|XLR
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"Workbook"
argument_list|)
condition|)
block|{
return|return
name|XLS
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"EncryptedPackage"
argument_list|)
operator|&&
name|names
operator|.
name|contains
argument_list|(
literal|"EncryptionInfo"
argument_list|)
operator|&&
name|names
operator|.
name|contains
argument_list|(
literal|"\u0006DataSpaces"
argument_list|)
condition|)
block|{
comment|// This is a protected OOXML document, which is an OLE2 file
comment|//  with an Encrypted Stream which holds the OOXML data
comment|// Without decrypting the stream, we can't tell what kind of
comment|//  OOXML file we have. Return a general OOXML Protected type,
comment|//  and hope the name based detection can guess the rest!
return|return
name|OOXML_PROTECTED
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"EncryptedPackage"
argument_list|)
condition|)
block|{
return|return
name|OLE
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"WordDocument"
argument_list|)
condition|)
block|{
return|return
name|DOC
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"Quill"
argument_list|)
condition|)
block|{
return|return
name|PUB
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"PowerPoint Document"
argument_list|)
condition|)
block|{
return|return
name|PPT
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"VisioDocument"
argument_list|)
condition|)
block|{
return|return
name|VSD
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"\u0001Ole10Native"
argument_list|)
condition|)
block|{
return|return
name|OLE10_NATIVE
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"MatOST"
argument_list|)
condition|)
block|{
comment|// this occurs on older Works Word Processor files (versions 3.0 and 4.0)
return|return
name|WPS
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"CONTENTS"
argument_list|)
operator|&&
name|names
operator|.
name|contains
argument_list|(
literal|"SPELLING"
argument_list|)
condition|)
block|{
comment|// Newer Works files
return|return
name|WPS
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"Contents"
argument_list|)
operator|&&
name|names
operator|.
name|contains
argument_list|(
literal|"\u0003ObjInfo"
argument_list|)
condition|)
block|{
return|return
name|COMP_OBJ
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"CONTENTS"
argument_list|)
operator|&&
name|names
operator|.
name|contains
argument_list|(
literal|"\u0001CompObj"
argument_list|)
condition|)
block|{
comment|// CompObj is a general kind of OLE2 embedding, but this may be an old Works file
comment|// If we have the Directory, check
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
name|MediaType
name|type
init|=
name|processCompObjFormatType
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|WPS
condition|)
block|{
return|return
name|WPS
return|;
block|}
else|else
block|{
comment|// Assume it's a general CompObj embedded resource
return|return
name|COMP_OBJ
return|;
block|}
block|}
else|else
block|{
comment|// Assume it's a general CompObj embedded resource
return|return
name|COMP_OBJ
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"CONTENTS"
argument_list|)
condition|)
block|{
comment|// CONTENTS without SPELLING nor CompObj normally means some sort
comment|//  of embedded non-office file inside an OLE2 document
comment|// This is most commonly triggered on nested directories
return|return
name|OLE
return|;
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"\u0001CompObj"
argument_list|)
operator|&&
operator|(
name|names
operator|.
name|contains
argument_list|(
literal|"Props"
argument_list|)
operator|||
name|names
operator|.
name|contains
argument_list|(
literal|"Props9"
argument_list|)
operator|||
name|names
operator|.
name|contains
argument_list|(
literal|"Props12"
argument_list|)
operator|)
condition|)
block|{
comment|// Could be Project, look for common name patterns
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
if|if
condition|(
name|mppDataMatch
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
return|return
name|MPP
return|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"PerfectOffice_MAIN"
argument_list|)
condition|)
block|{
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"SlideShow"
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-corelpresentations"
argument_list|)
return|;
comment|// .shw
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"PerfectOffice_OBJECTS"
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-quattro-pro"
argument_list|)
return|;
comment|// .wb?
block|}
block|}
elseif|else
if|if
condition|(
name|names
operator|.
name|contains
argument_list|(
literal|"NativeContent_MAIN"
argument_list|)
condition|)
block|{
return|return
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-quattro-pro"
argument_list|)
return|;
comment|// .qpw
block|}
else|else
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"__substg1.0_"
argument_list|)
condition|)
block|{
return|return
name|MSG
return|;
block|}
block|}
block|}
block|}
comment|// Couldn't detect a more specific type
return|return
name|OLE
return|;
block|}
comment|/**      * Is this one of the kinds of formats which uses CompObj to      *  store all of their data, eg Star Draw, Star Impress or      *  (older) Works?      * If not, it's likely an embedded resource      */
specifier|private
specifier|static
name|MediaType
name|processCompObjFormatType
parameter_list|(
name|DirectoryEntry
name|root
parameter_list|)
block|{
try|try
block|{
name|Entry
name|e
init|=
name|root
operator|.
name|getEntry
argument_list|(
literal|"\u0001CompObj"
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
operator|&&
name|e
operator|.
name|isDocumentEntry
argument_list|()
condition|)
block|{
name|DocumentNode
name|dn
init|=
operator|(
name|DocumentNode
operator|)
name|e
decl_stmt|;
name|DocumentInputStream
name|stream
init|=
operator|new
name|DocumentInputStream
argument_list|(
name|dn
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|IOUtils
operator|.
name|toByteArray
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|/*                  * This array contains a string with a normal ASCII name of the                  * application used to create this file. We want to search for that                  * name.                  */
if|if
condition|(
name|arrayContains
argument_list|(
name|bytes
argument_list|,
name|STAR_DRAW
argument_list|)
condition|)
block|{
return|return
name|SDA
return|;
block|}
elseif|else
if|if
condition|(
name|arrayContains
argument_list|(
name|bytes
argument_list|,
name|STAR_IMPRESS
argument_list|)
condition|)
block|{
return|return
name|SDD
return|;
block|}
elseif|else
if|if
condition|(
name|arrayContains
argument_list|(
name|bytes
argument_list|,
name|WORKS_QUILL96
argument_list|)
condition|)
block|{
return|return
name|WPS
return|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/*              * "root.getEntry" can throw FileNotFoundException. The code inside              * "if" can throw IOExceptions. Theoretically. Practically no              * exceptions will likely ever appear.              *               * Swallow all of them. If any occur, we just assume that we can't              * distinguish between Draw and Impress and return something safe:              * x-tika-msoffice              */
block|}
return|return
name|OLE
return|;
block|}
comment|// poor man's search for byte arrays, replace with some library call if
comment|// you know one without adding new dependencies
specifier|private
specifier|static
name|boolean
name|arrayContains
parameter_list|(
name|byte
index|[]
name|larger
parameter_list|,
name|byte
index|[]
name|smaller
parameter_list|)
block|{
name|int
name|largerCounter
init|=
literal|0
decl_stmt|;
name|int
name|smallerCounter
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|largerCounter
operator|<
name|larger
operator|.
name|length
condition|)
block|{
if|if
condition|(
name|larger
index|[
name|largerCounter
index|]
operator|==
name|smaller
index|[
name|smallerCounter
index|]
condition|)
block|{
name|largerCounter
operator|++
expr_stmt|;
name|smallerCounter
operator|++
expr_stmt|;
if|if
condition|(
name|smallerCounter
operator|==
name|smaller
operator|.
name|length
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
name|largerCounter
operator|=
name|largerCounter
operator|-
name|smallerCounter
operator|+
literal|1
expr_stmt|;
name|smallerCounter
operator|=
literal|0
expr_stmt|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getTopLevelNames
parameter_list|(
name|TikaInputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Force the document stream to a (possibly temporary) file
comment|// so we don't modify the current position of the stream
name|FileChannel
name|channel
init|=
name|stream
operator|.
name|getFileChannel
argument_list|()
decl_stmt|;
try|try
block|{
name|NPOIFSFileSystem
name|fs
init|=
operator|new
name|NPOIFSFileSystem
argument_list|(
name|channel
argument_list|)
decl_stmt|;
comment|// Optimize a possible later parsing process by keeping
comment|// a reference to the already opened POI file system
name|stream
operator|.
name|setOpenContainer
argument_list|(
name|fs
argument_list|)
expr_stmt|;
return|return
name|getTopLevelNames
argument_list|(
name|fs
operator|.
name|getRoot
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Parse error in POI, so we don't know the file type
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// Another problem in POI
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getTopLevelNames
parameter_list|(
name|DirectoryNode
name|root
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
name|entry
range|:
name|root
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
block|}
end_class

end_unit

