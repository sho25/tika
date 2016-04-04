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
name|ner
operator|.
name|opennlp
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
name|parser
operator|.
name|ner
operator|.
name|NERecogniser
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
name|Collections
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
name|HashSet
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
comment|/**  *  * This implementation of {@link NERecogniser} chains an array of  * {@link OpenNLPNameFinder}s for which NER models are  * available in classpath.  *  * The following models are scanned during initialization via class loader.:  *  *<table>  *<tr>  *<th>Entity Type</th><th>Path</th>  *</tr>  *<tr>  *<td>{@value PERSON}</td><td> {@value PERSON_FILE}</td>  *</tr>  *<tr>  *<td>{@value LOCATION}</td><td>{@value LOCATION_FILE}</td>  *</tr>  *<tr>  *<td>{@value ORGANIZATION}</td><td>{@value ORGANIZATION_FILE}</td>  *</tr>  *<tr>  *<td>{@value TIME}</td><td>{@value TIME_FILE}</td>  *</tr>  *<tr>  *<td>{@value DATE}</td><td>{@value DATE_FILE}</td>  *</tr>  *<tr>  *<td>{@value PERCENT}</td><td>{@value PERCENT_FILE}</td>  *</tr>  *<tr>  *<td>{@value MONEY}</td><td>{@value MONEY_FILE}</td>  *</tr>  *</table>  *  * @see org.apache.tika.parser.ner.NamedEntityParser#DEFAULT_NER_IMPL  */
end_comment

begin_class
specifier|public
class|class
name|OpenNLPNERecogniser
implements|implements
name|NERecogniser
block|{
specifier|public
specifier|static
specifier|final
name|String
name|MODELS_DIR
init|=
name|OpenNLPNERecogniser
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|"."
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PERSON_FILE
init|=
literal|"ner-person.bin"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LOCATION_FILE
init|=
literal|"ner-location.bin"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ORGANIZATION_FILE
init|=
literal|"ner-organization.bin"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TIME_FILE
init|=
literal|"ner-time.bin"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DATE_FILE
init|=
literal|"ner-date.bin"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PERCENT_FILE
init|=
literal|"ner-percentage.bin"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MONEY_FILE
init|=
literal|"ner-money.bin"
decl_stmt|;
comment|//Default (English) Models for the common 7 classes of named types
specifier|public
specifier|static
specifier|final
name|String
name|NER_PERSON_MODEL
init|=
name|MODELS_DIR
operator|+
literal|"/"
operator|+
name|PERSON_FILE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NER_LOCATION_MODEL
init|=
name|MODELS_DIR
operator|+
literal|"/"
operator|+
name|LOCATION_FILE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NER_ORGANIZATION_MODEL
init|=
name|MODELS_DIR
operator|+
literal|"/"
operator|+
name|ORGANIZATION_FILE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NER_TIME_MODEL
init|=
name|MODELS_DIR
operator|+
literal|"/"
operator|+
name|TIME_FILE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NER_DATE_MODEL
init|=
name|MODELS_DIR
operator|+
literal|"/"
operator|+
name|DATE_FILE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NER_PERCENT_MODEL
init|=
name|MODELS_DIR
operator|+
literal|"/"
operator|+
name|PERCENT_FILE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NER_MONEY_MODEL
init|=
name|MODELS_DIR
operator|+
literal|"/"
operator|+
name|MONEY_FILE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|DEFAULT_MODELS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
parameter_list|(
name|PERSON
parameter_list|,
name|NER_PERSON_MODEL
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|LOCATION
parameter_list|,
name|NER_LOCATION_MODEL
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|ORGANIZATION
parameter_list|,
name|NER_ORGANIZATION_MODEL
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|TIME
parameter_list|,
name|NER_TIME_MODEL
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|DATE
parameter_list|,
name|NER_DATE_MODEL
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|PERCENT
parameter_list|,
name|NER_PERCENT_MODEL
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|MONEY
parameter_list|,
name|NER_MONEY_MODEL
parameter_list|)
constructor_decl|;
block|}
block|}
decl_stmt|;
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|entityTypes
decl_stmt|;
specifier|private
name|List
argument_list|<
name|OpenNLPNameFinder
argument_list|>
name|nameFinders
decl_stmt|;
specifier|private
name|boolean
name|available
decl_stmt|;
comment|/**      * Creates a default chain of Name finders using default OpenNLP recognizers      */
specifier|public
name|OpenNLPNERecogniser
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_MODELS
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a chain of Named Entity recognisers      * @param models map of entityType -> model path      * NOTE: the model path should be known to class loader.      */
specifier|public
name|OpenNLPNERecogniser
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|models
parameter_list|)
block|{
name|this
operator|.
name|nameFinders
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|entityTypes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|models
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|OpenNLPNameFinder
name|finder
init|=
operator|new
name|OpenNLPNameFinder
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|finder
operator|.
name|isAvailable
argument_list|()
condition|)
block|{
name|this
operator|.
name|nameFinders
operator|.
name|add
argument_list|(
name|finder
argument_list|)
expr_stmt|;
name|this
operator|.
name|entityTypes
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|entityTypes
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|this
operator|.
name|entityTypes
argument_list|)
expr_stmt|;
name|this
operator|.
name|available
operator|=
name|nameFinders
operator|.
name|size
argument_list|()
operator|>
literal|0
expr_stmt|;
comment|//at least one finder is present
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
name|available
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getEntityTypes
parameter_list|()
block|{
return|return
name|entityTypes
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|recognise
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|String
index|[]
name|tokens
init|=
name|OpenNLPNameFinder
operator|.
name|tokenize
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|names
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|OpenNLPNameFinder
name|finder
range|:
name|nameFinders
control|)
block|{
name|names
operator|.
name|putAll
argument_list|(
name|finder
operator|.
name|findNames
argument_list|(
name|tokens
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
block|}
end_class

end_unit
