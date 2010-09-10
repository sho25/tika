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
name|extractor
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
name|metadata
operator|.
name|Metadata
import|;
end_import

begin_comment
comment|/**  * Interface for different document selection strategies for purposes like  * embedded document extraction by a {@link ContainerExtractor} instance.  * An implementation of this interface defines some specific selection  * criteria to be applied against the document metadata passed to the  * {@link #select(Metadata)} method.  *  * @since Apache Tika 0.8  */
end_comment

begin_interface
specifier|public
interface|interface
name|DocumentSelector
block|{
comment|/**      * Checks if a document with the given metadata matches the specified      * selection criteria.      *      * @param metadata document metadata      * @return<code>true</code> if the document matches the selection criteria,      *<code>false</code> otherwise      */
name|boolean
name|select
parameter_list|(
name|Metadata
name|metadata
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

