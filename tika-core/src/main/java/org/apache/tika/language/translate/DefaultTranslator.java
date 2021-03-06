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
name|language
operator|.
name|translate
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|config
operator|.
name|ServiceLoader
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

begin_comment
comment|/**  * A translator which picks the first available {@link Translator}   * implementations available through the  * {@link javax.imageio.spi.ServiceRegistry service provider mechanism}.  *  * @since Apache Tika 1.6  */
end_comment

begin_class
specifier|public
class|class
name|DefaultTranslator
implements|implements
name|Translator
block|{
specifier|private
specifier|transient
specifier|final
name|ServiceLoader
name|loader
decl_stmt|;
specifier|public
name|DefaultTranslator
parameter_list|(
name|ServiceLoader
name|loader
parameter_list|)
block|{
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
block|}
specifier|public
name|DefaultTranslator
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ServiceLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Finds all statically loadable translators and sort the list by name,      * rather than discovery order.      *      * @param loader service loader      * @return ordered list of statically loadable translators      */
specifier|private
specifier|static
name|List
argument_list|<
name|Translator
argument_list|>
name|getDefaultTranslators
parameter_list|(
name|ServiceLoader
name|loader
parameter_list|)
block|{
name|List
argument_list|<
name|Translator
argument_list|>
name|translators
init|=
name|loader
operator|.
name|loadStaticServiceProviders
argument_list|(
name|Translator
operator|.
name|class
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|translators
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Translator
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Translator
name|t1
parameter_list|,
name|Translator
name|t2
parameter_list|)
block|{
name|String
name|n1
init|=
name|t1
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|n2
init|=
name|t2
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|boolean
name|tika1
init|=
name|n1
operator|.
name|startsWith
argument_list|(
literal|"org.apache.tika."
argument_list|)
decl_stmt|;
name|boolean
name|tika2
init|=
name|n2
operator|.
name|startsWith
argument_list|(
literal|"org.apache.tika."
argument_list|)
decl_stmt|;
if|if
condition|(
name|tika1
operator|==
name|tika2
condition|)
block|{
return|return
name|n1
operator|.
name|compareTo
argument_list|(
name|n2
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|tika1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|translators
return|;
block|}
comment|/**      * Returns the first available translator, or null if none are      */
specifier|private
specifier|static
name|Translator
name|getFirstAvailable
parameter_list|(
name|ServiceLoader
name|loader
parameter_list|)
block|{
for|for
control|(
name|Translator
name|t
range|:
name|getDefaultTranslators
argument_list|(
name|loader
argument_list|)
control|)
block|{
if|if
condition|(
name|t
operator|.
name|isAvailable
argument_list|()
condition|)
return|return
name|t
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Translate, using the first available service-loaded translator      */
specifier|public
name|String
name|translate
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|sourceLanguage
parameter_list|,
name|String
name|targetLanguage
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|Translator
name|t
init|=
name|getFirstAvailable
argument_list|(
name|loader
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
return|return
name|t
operator|.
name|translate
argument_list|(
name|text
argument_list|,
name|sourceLanguage
argument_list|,
name|targetLanguage
argument_list|)
return|;
block|}
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"No translators currently available"
argument_list|)
throw|;
block|}
comment|/**      * Translate, using the first available service-loaded translator      */
specifier|public
name|String
name|translate
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|targetLanguage
parameter_list|)
throws|throws
name|TikaException
throws|,
name|IOException
block|{
name|Translator
name|t
init|=
name|getFirstAvailable
argument_list|(
name|loader
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
return|return
name|t
operator|.
name|translate
argument_list|(
name|text
argument_list|,
name|targetLanguage
argument_list|)
return|;
block|}
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"No translators currently available"
argument_list|)
throw|;
block|}
comment|/**      * Returns all available translators      */
specifier|public
name|List
argument_list|<
name|Translator
argument_list|>
name|getTranslators
parameter_list|()
block|{
return|return
name|getDefaultTranslators
argument_list|(
name|loader
argument_list|)
return|;
block|}
comment|/**      * Returns the current translator      */
specifier|public
name|Translator
name|getTranslator
parameter_list|()
block|{
return|return
name|getFirstAvailable
argument_list|(
name|loader
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|getFirstAvailable
argument_list|(
name|loader
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
end_class

end_unit

