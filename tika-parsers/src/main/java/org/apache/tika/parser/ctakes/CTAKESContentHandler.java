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
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ctakes
operator|.
name|typesystem
operator|.
name|type
operator|.
name|textsem
operator|.
name|IdentifiedAnnotation
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
name|sax
operator|.
name|ContentHandlerDecorator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|analysis_engine
operator|.
name|AnalysisEngine
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|fit
operator|.
name|util
operator|.
name|JCasUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|jcas
operator|.
name|JCas
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
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * Class used to extract biomedical information while parsing.   *  *<p>  * This class relies on<a href="http://ctakes.apache.org/">Apache cTAKES</a>   * that is a natural language processing system for extraction of information   * from electronic medical record clinical free-text.  *</p>  *  */
end_comment

begin_class
specifier|public
class|class
name|CTAKESContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
comment|// Prefix used for metadata including cTAKES annotations
specifier|public
specifier|static
name|String
name|CTAKES_META_PREFIX
init|=
literal|"ctakes:"
decl_stmt|;
comment|// Configuration object for CTAKESContentHandler
specifier|private
name|CTAKESConfig
name|config
init|=
literal|null
decl_stmt|;
comment|// StringBuilder object used to build the clinical free-text for cTAKES
specifier|private
name|StringBuilder
name|sb
init|=
literal|null
decl_stmt|;
comment|// Metadata object used for cTAKES annotations
specifier|private
name|Metadata
name|metadata
init|=
literal|null
decl_stmt|;
comment|/** 	 * Creates a new {@see CTAKESContentHandler} for the given {@see ContentHandler} and Metadata objects.  	 * @param handler the {@see ContentHandler} object to be decorated. 	 * @param metadata the {@see Metadata} object that will be populated using biomedical information extracted by cTAKES. 	 * @param config the {@see CTAKESConfig} object used to configure the handler. 	 */
specifier|public
name|CTAKESContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|CTAKESConfig
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Creates a new {@see CTAKESContentHandler} for the given {@see ContentHandler} and Metadata objects. 	 * @param handler the {@see ContentHandler} object to be decorated. 	 * @param metadata the {@see Metadata} object that will be populated using biomedical information extracted by cTAKES. 	 */
specifier|public
name|CTAKESContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|this
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|CTAKESConfig
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Default constructor. 	 */
specifier|public
name|CTAKESContentHandler
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|)
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
if|if
condition|(
name|config
operator|.
name|isText
argument_list|()
condition|)
block|{
name|sb
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
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
try|try
block|{
comment|// create an Analysis Engine
name|AnalysisEngine
name|ae
init|=
name|CTAKESUtils
operator|.
name|getAnalysisEngine
argument_list|(
name|config
operator|.
name|getAeDescriptorPath
argument_list|()
argument_list|,
name|config
operator|.
name|getUMLSUser
argument_list|()
argument_list|,
name|config
operator|.
name|getUMLSPass
argument_list|()
argument_list|)
decl_stmt|;
comment|// create a JCas, given an AE
name|JCas
name|jcas
init|=
name|CTAKESUtils
operator|.
name|getJCas
argument_list|(
name|ae
argument_list|)
decl_stmt|;
name|StringBuilder
name|metaText
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|config
operator|.
name|getMetadata
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|value
range|:
name|metadata
operator|.
name|getValues
argument_list|(
name|name
argument_list|)
control|)
block|{
name|metaText
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|metaText
operator|.
name|append
argument_list|(
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// analyze text
name|jcas
operator|.
name|setDocumentText
argument_list|(
name|metaText
operator|.
name|toString
argument_list|()
operator|+
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ae
operator|.
name|process
argument_list|(
name|jcas
argument_list|)
expr_stmt|;
comment|// add annotations to metadata
name|metadata
operator|.
name|add
argument_list|(
name|CTAKES_META_PREFIX
operator|+
literal|"schema"
argument_list|,
name|config
operator|.
name|getAnnotationPropsAsString
argument_list|()
argument_list|)
expr_stmt|;
name|CTAKESAnnotationProperty
index|[]
name|annotationPros
init|=
name|config
operator|.
name|getAnnotationProps
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|IdentifiedAnnotation
argument_list|>
name|collection
init|=
name|JCasUtil
operator|.
name|select
argument_list|(
name|jcas
argument_list|,
name|IdentifiedAnnotation
operator|.
name|class
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|IdentifiedAnnotation
argument_list|>
name|iterator
init|=
name|collection
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|IdentifiedAnnotation
name|annotation
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|StringBuilder
name|annotationBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|annotationBuilder
operator|.
name|append
argument_list|(
name|annotation
operator|.
name|getCoveredText
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|annotationPros
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CTAKESAnnotationProperty
name|property
range|:
name|annotationPros
control|)
block|{
name|annotationBuilder
operator|.
name|append
argument_list|(
name|config
operator|.
name|getSeparatorChar
argument_list|()
argument_list|)
expr_stmt|;
name|annotationBuilder
operator|.
name|append
argument_list|(
name|CTAKESUtils
operator|.
name|getAnnotationProperty
argument_list|(
name|annotation
argument_list|,
name|property
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|metadata
operator|.
name|add
argument_list|(
name|CTAKES_META_PREFIX
operator|+
name|annotation
operator|.
name|getType
argument_list|()
operator|.
name|getShortName
argument_list|()
argument_list|,
name|annotationBuilder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|.
name|isSerialize
argument_list|()
condition|)
block|{
comment|// serialize data
name|CTAKESUtils
operator|.
name|serialize
argument_list|(
name|config
operator|.
name|getSerializerType
argument_list|()
argument_list|,
name|config
operator|.
name|isPrettyPrint
argument_list|()
argument_list|,
name|config
operator|.
name|getOutputStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Returns metadata that includes cTAKES annotations. 	 * @return {@Metadata} object that includes cTAKES annotations. 	 */
specifier|public
name|Metadata
name|getMetadata
parameter_list|()
block|{
return|return
name|metadata
return|;
block|}
block|}
end_class

end_unit

