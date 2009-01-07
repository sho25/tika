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

begin_class
specifier|public
class|class
name|SafeContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|REPLACEMENT
init|=
operator|new
name|char
index|[]
block|{
literal|' '
block|}
decl_stmt|;
comment|/**      * Internal interface that allows both character and      * ignorable whitespace content to be filtered the same way.      */
specifier|protected
interface|interface
name|Output
block|{
name|void
name|write
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
function_decl|;
block|}
comment|/**      * Output through the {@link ContentHandler#characters(char[], int, int)}      * method of the decorated content handler.      */
specifier|private
specifier|final
name|Output
name|charactersOutput
init|=
operator|new
name|Output
argument_list|()
block|{
specifier|public
name|void
name|write
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|SafeContentHandler
operator|.
name|super
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**      * Output through the      * {@link ContentHandler#ignorableWhitespace(char[], int, int)}      * method of the decorated content handler.      */
specifier|private
specifier|final
name|Output
name|ignorableWhitespaceOutput
init|=
operator|new
name|Output
argument_list|()
block|{
specifier|public
name|void
name|write
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|SafeContentHandler
operator|.
name|super
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|public
name|SafeContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|filter
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|,
name|Output
name|output
parameter_list|)
throws|throws
name|SAXException
block|{
name|int
name|end
init|=
name|start
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isInvalid
argument_list|(
name|ch
index|[
name|i
index|]
argument_list|)
condition|)
block|{
comment|// Output any preceding valid characters
if|if
condition|(
name|i
operator|>
name|start
condition|)
block|{
name|output
operator|.
name|write
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|i
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
comment|// Output the replacement for this invalid character
name|writeReplacement
argument_list|(
name|output
argument_list|)
expr_stmt|;
comment|// Continue with the rest of the array
name|start
operator|=
name|i
operator|+
literal|1
expr_stmt|;
block|}
block|}
comment|// Output any remaining valid characters
name|output
operator|.
name|write
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks whether the given character (more accurately a UTF-16 code unit)      * is an invalid XML character and should be replaced for output.      * Subclasses can override this method to use an alternative definition      * of which characters should be replaced in the XML output.      *      * @param ch character      * @return<code>true</code> if the character should be replaced,      *<code>false</code> otherwise      */
specifier|protected
name|boolean
name|isInvalid
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
comment|// TODO: Detect also FFFE, FFFF, and the surrogate blocks
return|return
name|ch
operator|<
literal|0x20
operator|&&
name|ch
operator|!=
literal|0x09
operator|&&
name|ch
operator|!=
literal|0x0A
operator|&&
name|ch
operator|!=
literal|0x0D
return|;
block|}
comment|/**      * Outputs the replacement for an invalid character. Subclasses can      * override this method to use a custom replacement.      *      * @param output where the replacement is written to      * @throws SAXException if the replacement could not be written      */
specifier|protected
name|void
name|writeReplacement
parameter_list|(
name|Output
name|output
parameter_list|)
throws|throws
name|SAXException
block|{
name|output
operator|.
name|write
argument_list|(
name|REPLACEMENT
argument_list|,
literal|0
argument_list|,
name|REPLACEMENT
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------< ContentHandler>
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|filter
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|,
name|charactersOutput
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|filter
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|,
name|ignorableWhitespaceOutput
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

