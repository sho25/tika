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
name|mail
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
name|Collections
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
name|james
operator|.
name|mime4j
operator|.
name|MimeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|james
operator|.
name|mime4j
operator|.
name|descriptor
operator|.
name|BodyDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|james
operator|.
name|mime4j
operator|.
name|parser
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|james
operator|.
name|mime4j
operator|.
name|parser
operator|.
name|MimeStreamParser
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
name|AutoDetectParser
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
name|parser
operator|.
name|Parser
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
comment|/**  * Uses apache-mime4j to parse emails. Each part is treated with the  * corresponding parser and displayed within  *<p>  * elements.  *   * @author jnioche@digitalpebble.com  **/
end_comment

begin_class
specifier|public
class|class
name|RFC822Parser
implements|implements
name|Parser
block|{
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
name|singleton
argument_list|(
name|MediaType
operator|.
name|parse
argument_list|(
literal|"message/rfc822"
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
name|MimeStreamParser
name|parser
init|=
operator|new
name|MimeStreamParser
argument_list|()
decl_stmt|;
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
name|MailContentHandler
name|mch
init|=
operator|new
name|MailContentHandler
argument_list|(
name|xhtml
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|parser
operator|.
name|setContentHandler
argument_list|(
name|mch
argument_list|)
expr_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|parse
argument_list|(
name|stream
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
block|}
block|}
end_class

begin_comment
comment|/**  * Same as BodyContentHandler but does not even propagate the start |  * endDocument events  **/
end_comment

begin_class
class|class
name|StrictBodyContentHandler
extends|extends
name|BodyContentHandler
block|{
specifier|public
name|StrictBodyContentHandler
parameter_list|(
name|XHTMLContentHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{     }
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{     }
block|}
end_class

begin_comment
comment|/**  * Bridge between mime4j's content handler and the generic Sax content handler  * used by Tika. See  * http://james.apache.org/mime4j/apidocs/org/apache/james/mime4j  * /parser/ContentHandler.html  */
end_comment

begin_class
class|class
name|MailContentHandler
implements|implements
name|org
operator|.
name|apache
operator|.
name|james
operator|.
name|mime4j
operator|.
name|parser
operator|.
name|ContentHandler
block|{
specifier|private
name|XHTMLContentHandler
name|handler
decl_stmt|;
specifier|private
name|Metadata
name|metadata
decl_stmt|;
specifier|private
name|boolean
name|inPart
init|=
literal|false
decl_stmt|;
name|MailContentHandler
parameter_list|(
name|XHTMLContentHandler
name|xhtml
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|xhtml
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
specifier|public
name|void
name|body
parameter_list|(
name|BodyDescriptor
name|body
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|MimeException
throws|,
name|IOException
block|{
comment|// call the underlying parser for the part
comment|// TODO how to retrieve a non-default config?
name|AutoDetectParser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
comment|// use a different metadata object
comment|// in order to specify the mime type of the
comment|// sub part without damaging the main metadata
name|Metadata
name|submd
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|submd
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|body
operator|.
name|getMimeType
argument_list|()
argument_list|)
expr_stmt|;
name|submd
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_ENCODING
argument_list|,
name|body
operator|.
name|getCharset
argument_list|()
argument_list|)
expr_stmt|;
comment|// filter the events coming from the underlying parser
comment|// to prevent getting multiple</body></html> or</title>
comment|// in the output
name|StrictBodyContentHandler
name|bch
init|=
operator|new
name|StrictBodyContentHandler
argument_list|(
name|handler
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|bch
argument_list|,
name|submd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|endBodyPart
parameter_list|()
throws|throws
name|MimeException
block|{
try|try
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|endHeader
parameter_list|()
throws|throws
name|MimeException
block|{     }
specifier|public
name|void
name|startMessage
parameter_list|()
throws|throws
name|MimeException
block|{
try|try
block|{
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|endMessage
parameter_list|()
throws|throws
name|MimeException
block|{
try|try
block|{
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|endMultipart
parameter_list|()
throws|throws
name|MimeException
block|{
name|inPart
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|void
name|epilogue
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|MimeException
throws|,
name|IOException
block|{     }
comment|/**      * Header for the whole message or its parts      *       * @see http       *      ://james.apache.org/mime4j/apidocs/org/apache/james/mime4j/parser/      *      Field.html      **/
specifier|public
name|void
name|field
parameter_list|(
name|Field
name|field
parameter_list|)
throws|throws
name|MimeException
block|{
comment|// inPart indicates whether these metadata correspond to the
comment|// whole message or its parts
if|if
condition|(
name|inPart
condition|)
return|return;
comment|// TODO add metadata to the parts later
name|String
name|fieldname
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// TODO value could be parsed and/or encoded
name|String
name|value
init|=
name|field
operator|.
name|getBody
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldname
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"From"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldname
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"Subject"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|SUBJECT
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|preamble
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|MimeException
throws|,
name|IOException
block|{     }
specifier|public
name|void
name|raw
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|MimeException
throws|,
name|IOException
block|{     }
specifier|public
name|void
name|startBodyPart
parameter_list|()
throws|throws
name|MimeException
block|{
try|try
block|{
name|handler
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"email-entry"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|startHeader
parameter_list|()
throws|throws
name|MimeException
block|{
comment|// TODO Auto-generated method stub
block|}
specifier|public
name|void
name|startMultipart
parameter_list|(
name|BodyDescriptor
name|descr
parameter_list|)
throws|throws
name|MimeException
block|{
name|inPart
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

