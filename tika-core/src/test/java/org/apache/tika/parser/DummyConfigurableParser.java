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
package|;
end_package

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
name|Param
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *  * This Parser is created to test runtime configuration to parser.  * This parser simply copies parameters to metadata so that a test  * suit can be developed to test that :  * 1. Parameters were parsed from configuration file  * 2. parameters were supplied to parser via configure(ctx) method  * 3. parameters were available at parse  *  */
end_comment

begin_class
specifier|public
class|class
name|DummyConfigurableParser
block|{
comment|/*     private static Set<MediaType> MIMES = new HashSet<>();     static {         MIMES.add(MediaType.TEXT_PLAIN);         MIMES.add(MediaType.TEXT_HTML);         MIMES.add(MediaType.OCTET_STREAM);     }      @Override     public Set<MediaType> getSupportedTypes(ParseContext context) {         return MIMES;     }      @Override     public void parse(InputStream stream, ContentHandler handler,                       Metadata metadata, ParseContext context)             throws IOException, SAXException, TikaException {         for (Map.Entry<String, Param<?>> entry : getParams().entrySet()) {             Param<?> param = entry.getValue();             metadata.add(entry.getKey(), param.getValue().toString());             metadata.add(entry.getKey()+"-type", param.getValue().getClass().getName());         }     } */
block|}
end_class

end_unit

