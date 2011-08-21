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
name|field
operator|.
name|AbstractField
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
name|field
operator|.
name|AddressListField
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
name|field
operator|.
name|DateTimeField
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
name|field
operator|.
name|MailboxListField
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
name|field
operator|.
name|ParsedField
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
name|field
operator|.
name|UnstructuredField
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
name|field
operator|.
name|address
operator|.
name|AddressList
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
name|field
operator|.
name|address
operator|.
name|MailboxList
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
name|ContentHandler
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
name|EmbeddedContentHandler
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
comment|/**  * Bridge between mime4j's content handler and the generic Sax content handler  * used by Tika. See  * http://james.apache.org/mime4j/apidocs/org/apache/james/mime4j/parser/ContentHandler.html  */
end_comment

begin_class
class|class
name|MailContentHandler
implements|implements
name|ContentHandler
block|{
specifier|private
name|boolean
name|strictParsing
init|=
literal|false
decl_stmt|;
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
parameter_list|,
name|boolean
name|strictParsing
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
name|this
operator|.
name|strictParsing
operator|=
name|strictParsing
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
try|try
block|{
name|BodyContentHandler
name|bch
init|=
operator|new
name|BodyContentHandler
argument_list|(
name|handler
argument_list|)
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
operator|new
name|EmbeddedContentHandler
argument_list|(
name|bch
argument_list|)
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
throw|throw
operator|new
name|MimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MimeException
argument_list|(
name|e
argument_list|)
throw|;
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
throw|throw
operator|new
name|MimeException
argument_list|(
name|e
argument_list|)
throw|;
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
throw|throw
operator|new
name|MimeException
argument_list|(
name|e
argument_list|)
throw|;
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
throw|throw
operator|new
name|MimeException
argument_list|(
name|e
argument_list|)
throw|;
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
block|{
return|return;
block|}
try|try
block|{
name|String
name|fieldname
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
name|ParsedField
name|parsedField
init|=
name|AbstractField
operator|.
name|parse
argument_list|(
name|field
operator|.
name|getRaw
argument_list|()
argument_list|)
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
name|MailboxListField
name|fromField
init|=
operator|(
name|MailboxListField
operator|)
name|parsedField
decl_stmt|;
name|MailboxList
name|mailboxList
init|=
name|fromField
operator|.
name|getMailboxList
argument_list|()
decl_stmt|;
if|if
condition|(
name|fromField
operator|.
name|isValidField
argument_list|()
operator|&&
name|mailboxList
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mailboxList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|from
init|=
name|mailboxList
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDisplayString
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|MESSAGE_FROM
argument_list|,
name|from
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|,
name|from
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|from
init|=
name|stripOutFieldPrefix
argument_list|(
name|field
argument_list|,
literal|"From:"
argument_list|)
decl_stmt|;
if|if
condition|(
name|from
operator|.
name|startsWith
argument_list|(
literal|"<"
argument_list|)
condition|)
block|{
name|from
operator|=
name|from
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|from
operator|.
name|endsWith
argument_list|(
literal|">"
argument_list|)
condition|)
block|{
name|from
operator|=
name|from
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|from
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|MESSAGE_FROM
argument_list|,
name|from
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|,
name|from
argument_list|)
expr_stmt|;
block|}
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
operator|(
operator|(
name|UnstructuredField
operator|)
name|parsedField
operator|)
operator|.
name|getValue
argument_list|()
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
literal|"To"
argument_list|)
condition|)
block|{
name|processAddressList
argument_list|(
name|parsedField
argument_list|,
literal|"To:"
argument_list|,
name|Metadata
operator|.
name|MESSAGE_TO
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
literal|"CC"
argument_list|)
condition|)
block|{
name|processAddressList
argument_list|(
name|parsedField
argument_list|,
literal|"Cc:"
argument_list|,
name|Metadata
operator|.
name|MESSAGE_CC
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
literal|"BCC"
argument_list|)
condition|)
block|{
name|processAddressList
argument_list|(
name|parsedField
argument_list|,
literal|"Bcc:"
argument_list|,
name|Metadata
operator|.
name|MESSAGE_BCC
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
literal|"Date"
argument_list|)
condition|)
block|{
name|DateTimeField
name|dateField
init|=
operator|(
name|DateTimeField
operator|)
name|parsedField
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|DATE
argument_list|,
name|dateField
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CREATION_DATE
argument_list|,
name|dateField
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|me
parameter_list|)
block|{
if|if
condition|(
name|strictParsing
condition|)
block|{
throw|throw
name|me
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|processAddressList
parameter_list|(
name|ParsedField
name|field
parameter_list|,
name|String
name|addressListType
parameter_list|,
name|String
name|metadataField
parameter_list|)
throws|throws
name|MimeException
block|{
name|AddressListField
name|toField
init|=
operator|(
name|AddressListField
operator|)
name|field
decl_stmt|;
if|if
condition|(
name|toField
operator|.
name|isValidField
argument_list|()
condition|)
block|{
name|AddressList
name|addressList
init|=
name|toField
operator|.
name|getAddressList
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
name|addressList
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|metadataField
argument_list|,
name|addressList
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDisplayString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|to
init|=
name|stripOutFieldPrefix
argument_list|(
name|field
argument_list|,
name|addressListType
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|eachTo
range|:
name|to
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|metadataField
argument_list|,
name|eachTo
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
throw|throw
operator|new
name|MimeException
argument_list|(
name|e
argument_list|)
throw|;
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
specifier|private
name|String
name|stripOutFieldPrefix
parameter_list|(
name|Field
name|field
parameter_list|,
name|String
name|fieldname
parameter_list|)
block|{
name|String
name|temp
init|=
name|field
operator|.
name|getRaw
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|int
name|loc
init|=
name|fieldname
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
name|temp
operator|.
name|charAt
argument_list|(
name|loc
argument_list|)
operator|==
literal|' '
condition|)
block|{
name|loc
operator|++
expr_stmt|;
block|}
return|return
name|temp
operator|.
name|substring
argument_list|(
name|loc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

