begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|recognition
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
name|config
operator|.
name|Initializable
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
name|TikaConfigException
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
name|parser
operator|.
name|recognition
operator|.
name|tf
operator|.
name|TensorflowRESTRecogniser
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
name|apache
operator|.
name|tika
operator|.
name|utils
operator|.
name|AnnotationUtils
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
name|utils
operator|.
name|ServiceLoaderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
comment|/**  * This parser recognises objects from Images.  * The Object Recognition implementation can be switched using 'class' argument.  *<p>  *<b>Example Usage :</b>  *<pre>  *&lt;properties&gt;  *&lt;parsers&gt;  *&lt;parser class=&quot;org.apache.tika.parser.recognition.ObjectRecognitionParser&quot;&gt;  *&lt;mime&gt;image/jpeg&lt;/mime&gt;  *&lt;params&gt;  *&lt;param name=&quot;topN&quot; type=&quot;int&quot;&gt;2&lt;/param&gt;  *&lt;param name=&quot;minConfidence&quot; type=&quot;double&quot;&gt;0.015&lt;/param&gt;  *&lt;param name=&quot;class&quot; type=&quot;string&quot;&gt;org.apache.tika.parser.recognition.tf.TensorflowRESTRecogniser&lt;/param&gt;  *&lt;/params&gt;  *&lt;/parser&gt;  *&lt;/parsers&gt;  *&lt;/properties&gt;  *</pre>  *  * @since Apache Tika 1.14  */
end_comment

begin_class
specifier|public
class|class
name|ObjectRecognitionParser
extends|extends
name|AbstractParser
implements|implements
name|Initializable
block|{
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ObjectRecognitionParser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MD_KEY
init|=
literal|"OBJECT"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MD_REC_IMPL_KEY
init|=
name|ObjectRecognitionParser
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|".object.rec.impl"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|RecognisedObject
argument_list|>
name|DESC_CONFIDENCE_SORTER
init|=
operator|new
name|Comparator
argument_list|<
name|RecognisedObject
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|RecognisedObject
name|o1
parameter_list|,
name|RecognisedObject
name|o2
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|o2
operator|.
name|getConfidence
argument_list|()
argument_list|,
name|o1
operator|.
name|getConfidence
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
annotation|@
name|Field
specifier|private
name|double
name|minConfidence
init|=
literal|0.05
decl_stmt|;
annotation|@
name|Field
specifier|private
name|int
name|topN
init|=
literal|2
decl_stmt|;
specifier|private
name|ObjectRecogniser
name|recogniser
init|=
operator|new
name|TensorflowRESTRecogniser
argument_list|()
decl_stmt|;
annotation|@
name|Field
argument_list|(
name|name
operator|=
literal|"class"
argument_list|)
specifier|public
name|void
name|setRecogniser
parameter_list|(
name|String
name|recogniserClass
parameter_list|)
block|{
name|this
operator|.
name|recogniser
operator|=
name|ServiceLoaderUtils
operator|.
name|newInstance
argument_list|(
name|recogniserClass
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Param
argument_list|>
name|params
parameter_list|)
throws|throws
name|TikaConfigException
block|{
name|AnnotationUtils
operator|.
name|assignFieldParams
argument_list|(
name|recogniser
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|recogniser
operator|.
name|initialize
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"minConfidence = {}, topN={}"
argument_list|,
name|minConfidence
argument_list|,
name|topN
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recogniser = {}"
argument_list|,
name|recogniser
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Recogniser Available = {}"
argument_list|,
name|recogniser
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|recogniser
operator|.
name|isAvailable
argument_list|()
condition|?
name|recogniser
operator|.
name|getSupportedMimes
argument_list|()
else|:
name|Collections
operator|.
expr|<
name|MediaType
operator|>
name|emptySet
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|synchronized
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
if|if
condition|(
operator|!
name|recogniser
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"{} is not available for service"
argument_list|,
name|recogniser
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|metadata
operator|.
name|set
argument_list|(
name|MD_REC_IMPL_KEY
argument_list|,
name|recogniser
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RecognisedObject
argument_list|>
name|objects
init|=
name|recogniser
operator|.
name|recognise
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found {} objects"
argument_list|,
name|objects
operator|!=
literal|null
condition|?
name|objects
operator|.
name|size
argument_list|()
else|:
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Time taken {}ms"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|objects
operator|!=
literal|null
operator|&&
operator|!
name|objects
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|objects
argument_list|,
name|DESC_CONFIDENCE_SORTER
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|RecognisedObject
argument_list|>
name|acceptedObjects
init|=
operator|new
name|ArrayList
argument_list|<
name|RecognisedObject
argument_list|>
argument_list|(
name|topN
argument_list|)
decl_stmt|;
comment|// first process all the MD objects
for|for
control|(
name|RecognisedObject
name|object
range|:
name|objects
control|)
block|{
if|if
condition|(
name|object
operator|.
name|getConfidence
argument_list|()
operator|>=
name|minConfidence
condition|)
block|{
if|if
condition|(
name|object
operator|.
name|getConfidence
argument_list|()
operator|>=
name|minConfidence
condition|)
block|{
name|count
operator|++
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Add {}"
argument_list|,
name|object
argument_list|)
expr_stmt|;
name|String
name|mdValue
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"%s (%.5f)"
argument_list|,
name|object
operator|.
name|getLabel
argument_list|()
argument_list|,
name|object
operator|.
name|getConfidence
argument_list|()
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|MD_KEY
argument_list|,
name|mdValue
argument_list|)
expr_stmt|;
name|acceptedObjects
operator|.
name|add
argument_list|(
name|object
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>=
name|topN
condition|)
block|{
break|break;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Object {} confidence {} less than min {}"
argument_list|,
name|object
argument_list|,
name|object
operator|.
name|getConfidence
argument_list|()
argument_list|,
name|minConfidence
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// now the handler
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
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"ol"
argument_list|,
literal|"id"
argument_list|,
literal|"objects"
argument_list|)
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|RecognisedObject
name|object
range|:
name|acceptedObjects
control|)
block|{
comment|//writing to handler
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"li"
argument_list|,
literal|"id"
argument_list|,
name|object
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|text
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|" %s [%s](confidence = %f )"
argument_list|,
name|object
operator|.
name|getLabel
argument_list|()
argument_list|,
name|object
operator|.
name|getLabelLang
argument_list|()
argument_list|,
name|object
operator|.
name|getConfidence
argument_list|()
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"li"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"ol"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"NO objects"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
literal|"no.objects"
argument_list|,
name|Boolean
operator|.
name|TRUE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

