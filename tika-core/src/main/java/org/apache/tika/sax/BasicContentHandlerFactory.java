begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_comment
comment|/**  * Basic factory for creating common types of ContentHandlers  */
end_comment

begin_class
specifier|public
class|class
name|BasicContentHandlerFactory
implements|implements
name|ContentHandlerFactory
block|{
comment|/**      * Common handler types for content.      */
specifier|public
enum|enum
name|HANDLER_TYPE
block|{
name|BODY
block|,
name|IGNORE
block|,
comment|//don't store content
name|TEXT
block|,
name|HTML
block|,
name|XML
block|}
empty_stmt|;
specifier|private
specifier|final
name|HANDLER_TYPE
name|type
decl_stmt|;
specifier|private
specifier|final
name|int
name|writeLimit
decl_stmt|;
comment|/**      *      * @param type basic type of handler      * @param writeLimit max number of characters to store; if< 0, the handler will store all characters      */
specifier|public
name|BasicContentHandlerFactory
parameter_list|(
name|HANDLER_TYPE
name|type
parameter_list|,
name|int
name|writeLimit
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|writeLimit
operator|=
name|writeLimit
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ContentHandler
name|getNewContentHandler
parameter_list|()
block|{
if|if
condition|(
name|writeLimit
operator|>
operator|-
literal|1
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BODY
case|:
return|return
operator|new
name|BodyContentHandler
argument_list|(
name|writeLimit
argument_list|)
return|;
case|case
name|IGNORE
case|:
return|return
operator|new
name|DefaultHandler
argument_list|()
return|;
case|case
name|TEXT
case|:
return|return
operator|new
name|WriteOutContentHandler
argument_list|(
operator|new
name|ToTextContentHandler
argument_list|()
argument_list|,
name|writeLimit
argument_list|)
return|;
case|case
name|HTML
case|:
return|return
operator|new
name|WriteOutContentHandler
argument_list|(
operator|new
name|ToHTMLContentHandler
argument_list|()
argument_list|,
name|writeLimit
argument_list|)
return|;
case|case
name|XML
case|:
return|return
operator|new
name|WriteOutContentHandler
argument_list|(
operator|new
name|ToXMLContentHandler
argument_list|()
argument_list|,
name|writeLimit
argument_list|)
return|;
default|default:
return|return
operator|new
name|WriteOutContentHandler
argument_list|(
operator|new
name|ToTextContentHandler
argument_list|()
argument_list|,
name|writeLimit
argument_list|)
return|;
block|}
block|}
else|else
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BODY
case|:
return|return
operator|new
name|BodyContentHandler
argument_list|()
return|;
case|case
name|IGNORE
case|:
return|return
operator|new
name|DefaultHandler
argument_list|()
return|;
case|case
name|TEXT
case|:
return|return
operator|new
name|ToTextContentHandler
argument_list|()
return|;
case|case
name|HTML
case|:
return|return
operator|new
name|ToHTMLContentHandler
argument_list|()
return|;
case|case
name|XML
case|:
return|return
operator|new
name|ToXMLContentHandler
argument_list|()
return|;
default|default:
return|return
operator|new
name|ToTextContentHandler
argument_list|()
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|ContentHandler
name|getNewContentHandler
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
if|if
condition|(
name|writeLimit
operator|>
operator|-
literal|1
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BODY
case|:
return|return
operator|new
name|WriteOutContentHandler
argument_list|(
operator|new
name|BodyContentHandler
argument_list|(
operator|new
name|ToTextContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
argument_list|)
argument_list|,
name|writeLimit
argument_list|)
return|;
case|case
name|IGNORE
case|:
return|return
operator|new
name|DefaultHandler
argument_list|()
return|;
case|case
name|TEXT
case|:
return|return
operator|new
name|WriteOutContentHandler
argument_list|(
operator|new
name|ToTextContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
argument_list|,
name|writeLimit
argument_list|)
return|;
case|case
name|HTML
case|:
return|return
operator|new
name|WriteOutContentHandler
argument_list|(
operator|new
name|ToHTMLContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
argument_list|,
name|writeLimit
argument_list|)
return|;
case|case
name|XML
case|:
return|return
operator|new
name|WriteOutContentHandler
argument_list|(
operator|new
name|ToXMLContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
argument_list|,
name|writeLimit
argument_list|)
return|;
default|default:
return|return
operator|new
name|WriteOutContentHandler
argument_list|(
operator|new
name|ToTextContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
argument_list|,
name|writeLimit
argument_list|)
return|;
block|}
block|}
else|else
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|BODY
case|:
return|return
operator|new
name|BodyContentHandler
argument_list|(
operator|new
name|ToTextContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
argument_list|)
return|;
case|case
name|IGNORE
case|:
return|return
operator|new
name|DefaultHandler
argument_list|()
return|;
case|case
name|TEXT
case|:
return|return
operator|new
name|ToTextContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
return|;
case|case
name|HTML
case|:
return|return
operator|new
name|ToHTMLContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
return|;
case|case
name|XML
case|:
return|return
operator|new
name|ToXMLContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
return|;
default|default:
return|return
operator|new
name|ToTextContentHandler
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

