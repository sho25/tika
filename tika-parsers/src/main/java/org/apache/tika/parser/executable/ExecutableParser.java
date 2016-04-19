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
name|executable
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
name|sql
operator|.
name|Date
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
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|util
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
name|poi
operator|.
name|util
operator|.
name|LittleEndian
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
name|TikaException
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
name|EndianUtils
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
name|AbstractParser
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
name|XHTMLContentHandler
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Parser for executable files. Currently supports ELF and PE  */
end_comment

begin_class
specifier|public
class|class
name|ExecutableParser
extends|extends
name|AbstractParser
implements|implements
name|MachineMetadata
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|32128791892482l
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|PE_EXE
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-msdownload"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|ELF_GENERAL
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-elf"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|ELF_OBJECT
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-object"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|ELF_EXECUTABLE
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-executable"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|ELF_SHAREDLIB
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-sharedlib"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MediaType
name|ELF_COREDUMP
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-coredump"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|PE_EXE
argument_list|,
name|ELF_GENERAL
argument_list|,
name|ELF_OBJECT
argument_list|,
name|ELF_EXECUTABLE
argument_list|,
name|ELF_SHAREDLIB
argument_list|,
name|ELF_COREDUMP
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|SUPPORTED_TYPES
return|;
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
comment|// We only do metadata, for now
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
comment|// What kind is it?
name|byte
index|[]
name|first4
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|first4
argument_list|)
expr_stmt|;
if|if
condition|(
name|first4
index|[
literal|0
index|]
operator|==
operator|(
name|byte
operator|)
literal|'M'
operator|&&
name|first4
index|[
literal|1
index|]
operator|==
operator|(
name|byte
operator|)
literal|'Z'
condition|)
block|{
name|parsePE
argument_list|(
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|stream
argument_list|,
name|first4
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|first4
index|[
literal|0
index|]
operator|==
operator|(
name|byte
operator|)
literal|0x7f
operator|&&
name|first4
index|[
literal|1
index|]
operator|==
operator|(
name|byte
operator|)
literal|'E'
operator|&&
name|first4
index|[
literal|2
index|]
operator|==
operator|(
name|byte
operator|)
literal|'L'
operator|&&
name|first4
index|[
literal|3
index|]
operator|==
operator|(
name|byte
operator|)
literal|'F'
condition|)
block|{
name|parseELF
argument_list|(
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|stream
argument_list|,
name|first4
argument_list|)
expr_stmt|;
block|}
comment|// Finish everything
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * Parses a DOS or Windows PE file      */
specifier|public
name|void
name|parsePE
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|InputStream
name|stream
parameter_list|,
name|byte
index|[]
name|first4
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|PE_EXE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_WINDOWS
argument_list|)
expr_stmt|;
comment|// Skip over the MS-DOS bit
name|byte
index|[]
name|msdosSection
init|=
operator|new
name|byte
index|[
literal|0x3c
operator|-
literal|4
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|msdosSection
argument_list|)
expr_stmt|;
comment|// Grab the PE header offset
name|int
name|peOffset
init|=
name|LittleEndian
operator|.
name|readInt
argument_list|(
name|stream
argument_list|)
decl_stmt|;
comment|// Sanity check - while it may go anywhere, it's normally in the first few kb
if|if
condition|(
name|peOffset
operator|>
literal|4096
operator|||
name|peOffset
operator|<
literal|0x3f
condition|)
return|return;
comment|// Skip the rest of the MS-DOS stub (if PE), until we reach what should
comment|//  be the PE header (if this is a PE executable)
name|stream
operator|.
name|skip
argument_list|(
name|peOffset
operator|-
literal|0x40
argument_list|)
expr_stmt|;
comment|// Read the PE header
name|byte
index|[]
name|pe
init|=
operator|new
name|byte
index|[
literal|24
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|pe
argument_list|)
expr_stmt|;
comment|// Check it really is a PE header
if|if
condition|(
name|pe
index|[
literal|0
index|]
operator|==
operator|(
name|byte
operator|)
literal|'P'
operator|&&
name|pe
index|[
literal|1
index|]
operator|==
operator|(
name|byte
operator|)
literal|'E'
operator|&&
name|pe
index|[
literal|2
index|]
operator|==
literal|0
operator|&&
name|pe
index|[
literal|3
index|]
operator|==
literal|0
condition|)
block|{
comment|// Good, has a valid PE signature
block|}
else|else
block|{
comment|// Old style MS-DOS
return|return;
block|}
comment|// Read the header values
name|int
name|machine
init|=
name|LittleEndian
operator|.
name|getUShort
argument_list|(
name|pe
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|int
name|numSectors
init|=
name|LittleEndian
operator|.
name|getUShort
argument_list|(
name|pe
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|long
name|createdAt
init|=
name|LittleEndian
operator|.
name|getInt
argument_list|(
name|pe
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|long
name|symbolTableOffset
init|=
name|LittleEndian
operator|.
name|getInt
argument_list|(
name|pe
argument_list|,
literal|12
argument_list|)
decl_stmt|;
name|long
name|numSymbols
init|=
name|LittleEndian
operator|.
name|getInt
argument_list|(
name|pe
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|int
name|sizeOptHdrs
init|=
name|LittleEndian
operator|.
name|getUShort
argument_list|(
name|pe
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|int
name|characteristcs
init|=
name|LittleEndian
operator|.
name|getUShort
argument_list|(
name|pe
argument_list|,
literal|22
argument_list|)
decl_stmt|;
comment|// Turn this into helpful metadata
name|Date
name|createdAtD
init|=
operator|new
name|Date
argument_list|(
name|createdAt
operator|*
literal|1000l
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|,
name|createdAtD
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|machine
condition|)
block|{
case|case
literal|0x14c
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_x86_32
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|LITTLE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x8664
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_x86_32
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|LITTLE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"64"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x200
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_IA_64
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|LITTLE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"64"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x184
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_ALPHA
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|LITTLE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x284
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_ALPHA
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|LITTLE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"64"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x1c0
case|:
case|case
literal|0x1c4
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_ARM
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|LITTLE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x268
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_M68K
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|BIG
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x266
case|:
case|case
literal|0x366
case|:
case|case
literal|0x466
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_MIPS
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|BIG
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"16"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x162
case|:
case|case
literal|0x166
case|:
case|case
literal|0x168
case|:
case|case
literal|0x169
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_MIPS
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|LITTLE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"16"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x1f0
case|:
case|case
literal|0x1f1
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_PPC
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|LITTLE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x1a2
case|:
case|case
literal|0x1a3
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_SH3
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|BIG
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x1a6
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_SH4
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|BIG
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x1a8
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_SH3
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|BIG
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0x9041
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_M32R
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|BIG
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|0xebc
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_EFI
argument_list|)
expr_stmt|;
break|break;
default|default:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_UNKNOWN
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|/**      * Parses a Unix ELF file      */
specifier|public
name|void
name|parseELF
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|InputStream
name|stream
parameter_list|,
name|byte
index|[]
name|first4
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
comment|// Byte 5 is the architecture
name|int
name|architecture
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|architecture
operator|==
literal|1
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|architecture
operator|==
literal|2
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|ARCHITECTURE_BITS
argument_list|,
literal|"64"
argument_list|)
expr_stmt|;
block|}
comment|// Byte 6 is the endian-ness
name|int
name|endian
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|endian
operator|==
literal|1
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|LITTLE
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|endian
operator|==
literal|2
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|ENDIAN
argument_list|,
name|Endian
operator|.
name|BIG
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Byte 7 is the elf version
name|int
name|elfVer
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
comment|// Byte 8 is the OS, if set (lots of compilers don't)
comment|// Byte 9 is the OS (specific) ABI version
name|int
name|os
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
name|int
name|osVer
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|os
operator|>
literal|0
operator|||
name|osVer
operator|>
literal|0
condition|)
block|{
switch|switch
condition|(
name|os
condition|)
block|{
case|case
literal|0
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_SYSV
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_HPUX
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_NETBSD
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_LINUX
argument_list|)
expr_stmt|;
break|break;
case|case
literal|6
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_SOLARIS
argument_list|)
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_AIX
argument_list|)
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_IRIX
argument_list|)
expr_stmt|;
break|break;
case|case
literal|9
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_FREEBSD
argument_list|)
expr_stmt|;
break|break;
case|case
literal|10
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_TRU64
argument_list|)
expr_stmt|;
break|break;
case|case
literal|12
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_FREEBSD
argument_list|)
expr_stmt|;
break|break;
case|case
literal|64
case|:
case|case
literal|97
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_ARM
argument_list|)
expr_stmt|;
break|break;
case|case
literal|255
case|:
name|metadata
operator|.
name|set
argument_list|(
name|PLATFORM
argument_list|,
name|PLATFORM_EMBEDDED
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// Bytes 10-16 are padding and lengths
name|byte
index|[]
name|padLength
init|=
operator|new
name|byte
index|[
literal|7
index|]
decl_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|stream
argument_list|,
name|padLength
argument_list|)
expr_stmt|;
comment|// Bytes 16-17 are the object type (LE/BE)
name|int
name|type
decl_stmt|;
if|if
condition|(
name|endian
operator|==
literal|1
condition|)
block|{
name|type
operator|=
name|EndianUtils
operator|.
name|readUShortLE
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
name|EndianUtils
operator|.
name|readUShortBE
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
literal|1
case|:
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|ELF_OBJECT
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|ELF_EXECUTABLE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|ELF_SHAREDLIB
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|ELF_COREDUMP
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|ELF_GENERAL
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Bytes 18-19 are the machine (EM_*)
name|int
name|machine
decl_stmt|;
if|if
condition|(
name|endian
operator|==
literal|1
condition|)
block|{
name|machine
operator|=
name|EndianUtils
operator|.
name|readUShortLE
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|machine
operator|=
name|EndianUtils
operator|.
name|readUShortBE
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|machine
condition|)
block|{
case|case
literal|2
case|:
case|case
literal|18
case|:
case|case
literal|43
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_SPARC
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_x86_32
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_M68K
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_M88K
argument_list|)
expr_stmt|;
break|break;
case|case
literal|8
case|:
case|case
literal|10
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_MIPS
argument_list|)
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_S370
argument_list|)
expr_stmt|;
break|break;
case|case
literal|20
case|:
case|case
literal|21
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_PPC
argument_list|)
expr_stmt|;
break|break;
case|case
literal|22
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_S390
argument_list|)
expr_stmt|;
break|break;
case|case
literal|40
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_ARM
argument_list|)
expr_stmt|;
break|break;
case|case
literal|41
case|:
case|case
literal|0x9026
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_ALPHA
argument_list|)
expr_stmt|;
break|break;
case|case
literal|50
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_IA_64
argument_list|)
expr_stmt|;
break|break;
case|case
literal|62
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_x86_64
argument_list|)
expr_stmt|;
break|break;
case|case
literal|75
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_VAX
argument_list|)
expr_stmt|;
break|break;
case|case
literal|88
case|:
name|metadata
operator|.
name|set
argument_list|(
name|MACHINE_TYPE
argument_list|,
name|MACHINE_M32R
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Bytes 20-23 are the version
comment|// TODO
block|}
block|}
end_class

end_unit

