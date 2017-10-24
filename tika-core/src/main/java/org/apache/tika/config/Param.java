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
name|config
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
name|utils
operator|.
name|XMLReaderUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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

begin_comment
comment|/**  * This is a serializable model class for parameters from configuration file.  *  * @param<T> value type. Should be serializable to string and have a constructor with string param  * @since Apache Tika 1.14  */
end_comment

begin_class
specifier|public
class|class
name|Param
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|reverseMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
name|map
operator|.
name|put
argument_list|(
name|Boolean
operator|.
name|class
argument_list|,
literal|"bool"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"string"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|Byte
operator|.
name|class
argument_list|,
literal|"byte"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|Short
operator|.
name|class
argument_list|,
literal|"short"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
literal|"int"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|Long
operator|.
name|class
argument_list|,
literal|"long"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|BigInteger
operator|.
name|class
argument_list|,
literal|"bigint"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|Float
operator|.
name|class
argument_list|,
literal|"float"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|Double
operator|.
name|class
argument_list|,
literal|"double"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|File
operator|.
name|class
argument_list|,
literal|"file"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|URI
operator|.
name|class
argument_list|,
literal|"uri"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|URL
operator|.
name|class
argument_list|,
literal|"url"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|reverseMap
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Class
argument_list|<
name|T
argument_list|>
name|type
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|value
decl_stmt|;
specifier|private
name|T
name|actualValue
decl_stmt|;
specifier|public
name|Param
parameter_list|()
block|{     }
specifier|public
name|Param
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|T
name|value
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Param
parameter_list|(
name|String
name|name
parameter_list|,
name|T
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|value
operator|.
name|getClass
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|Class
argument_list|<
name|T
argument_list|>
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|String
name|getTypeString
parameter_list|()
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
return|return
name|type
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|public
name|void
name|setTypeString
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
operator|||
name|type
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|type
operator|=
name|classFromType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|actualValue
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|T
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|actualValue
operator|==
literal|null
condition|)
block|{
name|actualValue
operator|=
name|getTypedValue
argument_list|(
name|type
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|actualValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Param{"
operator|+
literal|"name='"
operator|+
name|name
operator|+
literal|'\''
operator|+
literal|", value='"
operator|+
name|value
operator|+
literal|'\''
operator|+
literal|", actualValue="
operator|+
name|actualValue
operator|+
literal|'}'
return|;
block|}
specifier|public
name|void
name|save
parameter_list|(
name|OutputStream
name|stream
parameter_list|)
throws|throws
name|TransformerException
throws|,
name|TikaException
block|{
name|DocumentBuilder
name|builder
init|=
name|XMLReaderUtils
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|newDocument
argument_list|()
decl_stmt|;
name|Element
name|paramEl
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"param"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|appendChild
argument_list|(
name|paramEl
argument_list|)
expr_stmt|;
name|save
argument_list|(
name|paramEl
argument_list|)
expr_stmt|;
name|Transformer
name|transformer
init|=
name|XMLReaderUtils
operator|.
name|getTransformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
operator|new
name|DOMSource
argument_list|(
name|paramEl
argument_list|)
argument_list|,
operator|new
name|StreamResult
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|save
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|Element
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not an Element : "
operator|+
name|node
argument_list|)
throw|;
block|}
name|Element
name|el
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
name|el
operator|.
name|setAttribute
argument_list|(
literal|"name"
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|el
operator|.
name|setAttribute
argument_list|(
literal|"type"
argument_list|,
name|getTypeString
argument_list|()
argument_list|)
expr_stmt|;
name|el
operator|.
name|setTextContent
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Param
argument_list|<
name|T
argument_list|>
name|load
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
throws|,
name|TikaException
block|{
name|DocumentBuilder
name|db
init|=
name|XMLReaderUtils
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|document
init|=
name|db
operator|.
name|parse
argument_list|(
name|stream
argument_list|)
decl_stmt|;
return|return
name|load
argument_list|(
name|document
operator|.
name|getFirstChild
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Param
argument_list|<
name|T
argument_list|>
name|load
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|Node
name|nameAttr
init|=
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|Node
name|typeAttr
init|=
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|getNamedItem
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
name|Node
name|value
init|=
name|node
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|Param
argument_list|<
name|T
argument_list|>
name|ret
init|=
operator|new
name|Param
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|ret
operator|.
name|name
operator|=
name|nameAttr
operator|.
name|getTextContent
argument_list|()
expr_stmt|;
name|ret
operator|.
name|setTypeString
argument_list|(
name|typeAttr
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|.
name|value
operator|=
name|value
operator|.
name|getTextContent
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|T
argument_list|>
name|classFromType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|reverseMap
operator|.
name|containsKey
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|reverseMap
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
else|else
try|try
block|{
return|return
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|type
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|getTypedValue
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|String
name|value
parameter_list|)
block|{
try|try
block|{
name|Constructor
argument_list|<
name|T
argument_list|>
name|constructor
init|=
name|type
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|constructor
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|type
operator|+
literal|" doesnt have a constructor that takes String arg"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InstantiationException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

