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
name|wordperfect
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
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|collections4
operator|.
name|MapUtils
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Extracts WordPerfect Document Area text from a WordPerfect document  * version 5.x.  * @author Pascal Essiembre  */
end_comment

begin_class
class|class
name|WP5DocumentAreaExtractor
extends|extends
name|WPDocumentAreaExtractor
block|{
comment|/* 192-207 characters represent fixed-length multi-byte functions.        * Those that are not handled explicitely in the code below should be      * skipped according to their size (minus the first char if already read).      */
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|FIXED_LENGTH_FUNCTION_SIZES
init|=
name|MapUtils
operator|.
name|putAll
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
argument_list|,
operator|new
name|Integer
index|[]
block|{
literal|192
block|,
literal|4
block|,
comment|// Extended character
literal|193
block|,
literal|9
block|,
comment|// Center/Align/ Tab/Left Margin Release
literal|194
block|,
literal|11
block|,
comment|// Indent
literal|195
block|,
literal|3
block|,
comment|// Attribute ON
literal|196
block|,
literal|3
block|,
comment|// Attribute OFF
literal|197
block|,
literal|5
block|,
comment|// Block Protect
literal|198
block|,
literal|6
block|,
comment|// End of Indent
literal|199
block|,
literal|7
block|,
comment|// Different Display Character when Hyphenated
literal|200
block|,
literal|4
block|,
comment|// (Reserved)
literal|201
block|,
literal|5
block|,
comment|// (Reserved)
literal|202
block|,
literal|6
block|,
comment|// (Reserved)
literal|203
block|,
literal|6
block|,
comment|// (Reserved)
literal|204
block|,
literal|8
block|,
comment|// (Reserved)
literal|205
block|,
literal|10
block|,
comment|// (Reserved)
literal|206
block|,
literal|10
block|,
comment|// (Reserved)
literal|207
block|,
literal|12
block|,
comment|// (Reserved)
block|}
argument_list|)
decl_stmt|;
specifier|protected
name|void
name|extract
parameter_list|(
name|int
name|c
parameter_list|,
name|WPInputStream
name|in
parameter_list|,
name|StringBuilder
name|out
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
comment|// 0-31: control characters
if|if
condition|(
name|c
operator|==
literal|10
condition|)
block|{
name|endParagraph
argument_list|(
name|out
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|// hard return ("Enter")
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|11
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// soft page break
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|12
condition|)
block|{
name|endParagraph
argument_list|(
name|out
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|// hard page break
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|13
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// soft return (line wrap)
comment|// 32-126: ASCII characters
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|32
operator|&&
name|c
operator|<=
literal|126
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|c
argument_list|)
expr_stmt|;
comment|// ASCII character
comment|// 128-191: single-byte functions
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|140
condition|)
block|{
name|endParagraph
argument_list|(
name|out
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|// combination hard return/soft page (WP5.1)
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|144
operator|&&
name|c
operator|<=
literal|149
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
comment|// deletable/invisible soft return/page
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|153
condition|)
block|{
name|endParagraph
argument_list|(
name|out
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
comment|// Dormant Hard return (WP5.1)
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|160
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'\u00A0'
argument_list|)
expr_stmt|;
comment|// Hard space
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|169
operator|&&
name|c
operator|<=
literal|171
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
comment|// Hard hyphen
comment|// 192-207: fixed-length multi-byte function
block|}
elseif|else
if|if
condition|(
name|c
operator|==
literal|192
condition|)
block|{
comment|// extended char
name|int
name|charval
init|=
name|in
operator|.
name|readWP
argument_list|()
decl_stmt|;
name|int
name|charset
init|=
name|in
operator|.
name|readWP
argument_list|()
decl_stmt|;
name|in
operator|.
name|readWP
argument_list|()
expr_stmt|;
comment|// closing character
name|WP5Charsets
operator|.
name|append
argument_list|(
name|out
argument_list|,
name|charset
argument_list|,
name|charval
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|193
operator|&&
name|c
operator|<=
literal|207
condition|)
block|{
comment|// removing 1 from function length since first char already read
name|in
operator|.
name|skipWPByte
argument_list|(
name|FIXED_LENGTH_FUNCTION_SIZES
operator|.
name|get
argument_list|(
name|c
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// 208-255: variable-length multi-byte function
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|208
operator|&&
name|c
operator|<=
literal|255
condition|)
block|{
comment|// Variable-Length Multi-Byte Functions
name|in
operator|.
name|readWP
argument_list|()
expr_stmt|;
comment|// subgroup (the function code)
name|int
name|functionSize
init|=
name|in
operator|.
name|readWPShort
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|functionSize
condition|;
name|i
operator|++
control|)
block|{
name|in
operator|.
name|readWP
argument_list|()
expr_stmt|;
block|}
comment|//TODO Are there functions containing data? Like footnotes?
block|}
block|}
block|}
end_class

end_unit

