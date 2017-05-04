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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|JAXBException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|Marshaller
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|Unmarshaller
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|helpers
operator|.
name|DefaultValidationEventHandler
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
comment|/**  * This is a JAXB serializable model class for parameters from configuration file.  *  * @param<T> value type. Should be serializable to string and have a constructor with string param  * @since Apache Tika 1.14  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|()
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|NONE
argument_list|)
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
name|JAXBContext
name|JAXB_CTX
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Marshaller
name|MARSHALLER
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Unmarshaller
name|UNMARSHALLER
decl_stmt|;
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
try|try
block|{
name|JAXB_CTX
operator|=
name|JAXBContext
operator|.
name|newInstance
argument_list|(
name|Param
operator|.
name|class
argument_list|)
expr_stmt|;
name|MARSHALLER
operator|=
name|JAXB_CTX
operator|.
name|createMarshaller
argument_list|()
expr_stmt|;
name|MARSHALLER
operator|.
name|setEventHandler
argument_list|(
operator|new
name|DefaultValidationEventHandler
argument_list|()
argument_list|)
expr_stmt|;
name|UNMARSHALLER
operator|=
name|JAXB_CTX
operator|.
name|createUnmarshaller
argument_list|()
expr_stmt|;
name|UNMARSHALLER
operator|.
name|setEventHandler
argument_list|(
operator|new
name|DefaultValidationEventHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JAXBException
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
annotation|@
name|XmlTransient
specifier|private
name|Class
argument_list|<
name|T
argument_list|>
name|type
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"name"
argument_list|)
specifier|private
name|String
name|name
decl_stmt|;
annotation|@
name|XmlValue
argument_list|()
specifier|private
name|String
name|value
decl_stmt|;
annotation|@
name|XmlTransient
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
annotation|@
name|XmlTransient
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
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"type"
argument_list|)
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
name|this
operator|.
name|type
operator|=
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
expr_stmt|;
block|}
else|else
try|try
block|{
name|this
operator|.
name|type
operator|=
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
expr_stmt|;
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
name|this
operator|.
name|actualValue
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|XmlTransient
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
name|this
operator|.
name|actualValue
operator|=
name|constructor
operator|.
name|newInstance
argument_list|(
name|value
argument_list|)
expr_stmt|;
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
name|JAXBException
block|{
name|MARSHALLER
operator|.
name|marshal
argument_list|(
name|this
argument_list|,
name|stream
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
throws|throws
name|JAXBException
block|{
name|MARSHALLER
operator|.
name|marshal
argument_list|(
name|this
argument_list|,
name|node
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
name|JAXBException
block|{
return|return
operator|(
name|Param
argument_list|<
name|T
argument_list|>
operator|)
name|UNMARSHALLER
operator|.
name|unmarshal
argument_list|(
name|stream
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
throws|throws
name|JAXBException
block|{
return|return
operator|(
name|Param
argument_list|<
name|T
argument_list|>
operator|)
name|UNMARSHALLER
operator|.
name|unmarshal
argument_list|(
name|node
argument_list|)
return|;
block|}
block|}
end_class

end_unit

