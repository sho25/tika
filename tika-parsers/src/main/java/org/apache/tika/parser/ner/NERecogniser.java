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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|String
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
comment|/**  * Defines a contract for named entity recogniser. The NER contract includes {@link #isAvailable()},  * {@link #getEntityTypes()} and {@link #recognise( String )}  */
end_comment

begin_interface
specifier|public
interface|interface
name|NERecogniser
block|{
comment|//the common named entity classes
name|String
name|LOCATION
init|=
literal|"LOCATION"
decl_stmt|;
name|String
name|PERSON
init|=
literal|"PERSON"
decl_stmt|;
name|String
name|ORGANIZATION
init|=
literal|"ORGANIZATION"
decl_stmt|;
name|String
name|MISCELLANEOUS
init|=
literal|"MISCELLANEOUS"
decl_stmt|;
name|String
name|TIME
init|=
literal|"TIME"
decl_stmt|;
name|String
name|DATE
init|=
literal|"DATE"
decl_stmt|;
name|String
name|PERCENT
init|=
literal|"PERCENT"
decl_stmt|;
name|String
name|MONEY
init|=
literal|"MONEY"
decl_stmt|;
name|String
name|FACILITY
init|=
literal|"FACILITY"
decl_stmt|;
name|String
name|GPE
init|=
literal|"GPE"
decl_stmt|;
comment|/**      * checks if this Named Entity recogniser is available for service      * @return true if this recogniser is ready to recognise, false otherwise      */
name|boolean
name|isAvailable
parameter_list|()
function_decl|;
comment|/**      *  gets a set of entity types whose names are recognisable by this      * @return set of entity types/classes      */
name|Set
argument_list|<
name|String
argument_list|>
name|getEntityTypes
parameter_list|()
function_decl|;
comment|/**      * call for name recognition action from text      * @param text text with possibly contains names      * @return map of entityType -> set of names      */
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
function_decl|;
block|}
end_interface

end_unit

