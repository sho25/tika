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

begin_comment
comment|/* import java.util.ArrayList; import java.util.List; */
end_comment

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * Content handler decorator that makes sure that the character events  * ({@link #characters(char[], int, int)} or  * {@link #ignorableWhitespace(char[], int, int)}) passed to the decorated  * content handler contain only valid XML characters. All invalid characters  * are replaced with spaces.  *<p>  * The XML standard defines the following Unicode character ranges as  * valid XML characters:  *<pre>  * #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]  *</pre>  *<p>  * Note that currently this class only detects those invalid characters whose  * UTF-16 representation fits a single char. Also, this class does not ensure  * that the UTF-16 encoding of incoming characters is correct.  */
end_comment

begin_class
specifier|public
class|class
name|SafeContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
comment|/**      * Replacement for invalid characters.      */
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
literal|'\ufffd'
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
specifier|private
specifier|static
class|class
name|StringOutput
implements|implements
name|Output
block|{
specifier|private
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
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
block|{
name|builder
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
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
name|ignorableWhitespace
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
comment|/**      * Filters and outputs the contents of the given input buffer. Any      * invalid characters in the input buffer area handled by sending a      * replacement (a space character) to the given output. Any sequences      * of valid characters are passed as-is to the given output.       *       * @param ch input buffer      * @param start start offset within the buffer      * @param length number of characters to read from the buffer      * @param output output channel      * @throws SAXException if the filtered characters could not be written out      */
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
name|int
name|i
init|=
name|start
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|end
condition|)
block|{
name|int
name|c
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|ch
argument_list|,
name|i
argument_list|,
name|end
argument_list|)
decl_stmt|;
name|int
name|j
init|=
name|i
operator|+
name|Character
operator|.
name|charCount
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|isInvalid
argument_list|(
name|c
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
name|j
expr_stmt|;
block|}
name|i
operator|=
name|j
expr_stmt|;
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
comment|/**      * Checks if the given string contains any invalid XML characters.      *      * @param value string to be checked      * @return<code>true</code> if the string contains invalid XML characters,      *<code>false</code> otherwise      */
specifier|private
name|boolean
name|isInvalid
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|char
index|[]
name|ch
init|=
name|value
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|ch
operator|.
name|length
condition|)
block|{
name|int
name|c
init|=
name|Character
operator|.
name|codePointAt
argument_list|(
name|ch
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|isInvalid
argument_list|(
name|c
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|i
operator|=
name|i
operator|+
name|Character
operator|.
name|charCount
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Checks whether the given Unicode character is an invalid XML character      * and should be replaced for output. Subclasses can override this method      * to use an alternative definition of which characters should be replaced      * in the XML output. The default definition from the XML specification is:      *<pre>      * Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]      *</pre>      *      * @param ch character      * @return<code>true</code> if the character should be replaced,      *<code>false</code> otherwise      */
specifier|protected
name|boolean
name|isInvalid
parameter_list|(
name|int
name|ch
parameter_list|)
block|{
if|if
condition|(
name|ch
operator|<
literal|0x20
condition|)
block|{
return|return
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
elseif|else
if|if
condition|(
name|ch
operator|<
literal|0xE000
condition|)
block|{
return|return
name|ch
operator|>
literal|0xD7FF
return|;
block|}
elseif|else
if|if
condition|(
name|ch
operator|<
literal|0x10000
condition|)
block|{
return|return
name|ch
operator|>
literal|0xFFFD
return|;
block|}
else|else
block|{
return|return
name|ch
operator|>
literal|0x10FFFF
return|;
block|}
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
comment|/*     private final List<String> elements = new ArrayList<String>();      // Called only from assert     private boolean verifyStartElement(String name) {         // TODO: we could strengthen this to do full         // XTHML validation, eg you shouldn't start p inside         // another p (but ODF parser, at least, seems to         // violate this):         //if (name.equals("p")) {         //assert elements.size() == 0 || !elements.get(elements.size()-1).equals("p");         //}         elements.add(name);         return true;     }      // Called only from assert     private boolean verifyEndElement(String name) {         assert elements.size()> 0: "end tag=" + name + " with no startElement";         final String currentElement = elements.get(elements.size()-1);         assert currentElement.equals(name): "mismatched elements open=" + currentElement + " close=" + name;         elements.remove(elements.size()-1);         return true;     }      // Called only from assert     private boolean verifyEndDocument() {         assert elements.size() == 0;         return true;     }     */
comment|//------------------------------------------------------< ContentHandler>
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|name
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// TODO: enable this, but some parsers currently
comment|// trip it
comment|//assert verifyStartElement(name);
comment|// Look for any invalid characters in attribute values.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|isInvalid
argument_list|(
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
comment|// Found an invalid character, so need to filter the attributes
name|AttributesImpl
name|filtered
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|String
name|value
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|j
operator|>=
name|i
operator|&&
name|isInvalid
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// Filter the attribute value when needed
name|Output
name|buffer
init|=
operator|new
name|StringOutput
argument_list|()
decl_stmt|;
name|filter
argument_list|(
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
name|value
operator|=
name|buffer
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|filtered
operator|.
name|addAttribute
argument_list|(
name|atts
operator|.
name|getURI
argument_list|(
name|j
argument_list|)
argument_list|,
name|atts
operator|.
name|getLocalName
argument_list|(
name|j
argument_list|)
argument_list|,
name|atts
operator|.
name|getQName
argument_list|(
name|j
argument_list|)
argument_list|,
name|atts
operator|.
name|getType
argument_list|(
name|j
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|atts
operator|=
name|filtered
expr_stmt|;
break|break;
block|}
block|}
name|super
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|name
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// TODO: enable this, but some parsers currently
comment|// trip it
comment|//assert verifyEndElement(name);
name|super
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
comment|// TODO: enable this, but some parsers currently
comment|// trip it
comment|//assert verifyEndDocument();
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
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

