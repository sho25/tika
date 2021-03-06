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
name|ctakes
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
name|tika
operator|.
name|config
operator|.
name|TikaConfig
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
name|parser
operator|.
name|ParserDecorator
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
comment|/**  * CTAKESParser decorates a {@link Parser} and leverages on  * {@link CTAKESContentHandler} to extract biomedical information from  * clinical text using Apache cTAKES.  *<p>It is normally called by supplying an instance to   *  {@link AutoDetectParser}, such as:  *<code>AutoDetectParser parser = new AutoDetectParser(new CTAKESParser());</code>  *<p>It can also be used by giving a Tika Config file similar to:  *<code>  *<properties>  *<parsers>  *<parser class="org.apache.tika.parser.ctakes.CTAKESParser">  *<parser class="org.apache.tika.parser.DefaultParser"/>  *</parser>  *</parsers>  *</properties>  *</code>  *<p>Because this is a Parser Decorator, and not a normal Parser in  *  it's own right, it isn't normally selected via the Parser Service Loader.  */
end_comment

begin_class
specifier|public
class|class
name|CTAKESParser
extends|extends
name|ParserDecorator
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2313482748027097961L
decl_stmt|;
comment|/**      * Wraps the default Parser      */
specifier|public
name|CTAKESParser
parameter_list|()
block|{
name|this
argument_list|(
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Wraps the default Parser for this Config      */
specifier|public
name|CTAKESParser
parameter_list|(
name|TikaConfig
name|config
parameter_list|)
block|{
name|this
argument_list|(
name|config
operator|.
name|getParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Wraps the specified Parser      */
specifier|public
name|CTAKESParser
parameter_list|(
name|Parser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|CTAKESConfig
name|config
init|=
name|context
operator|.
name|get
argument_list|(
name|CTAKESConfig
operator|.
name|class
argument_list|,
operator|new
name|CTAKESConfig
argument_list|()
argument_list|)
decl_stmt|;
name|CTAKESContentHandler
name|ctakesHandler
init|=
operator|new
name|CTAKESContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|super
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|ctakesHandler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
comment|//@Override
specifier|public
name|String
name|getDecorationName
parameter_list|()
block|{
return|return
literal|"CTakes"
return|;
block|}
block|}
end_class

end_unit

