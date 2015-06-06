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
name|org
operator|.
name|apache
operator|.
name|uima
operator|.
name|cas
operator|.
name|impl
operator|.
name|XCASSerializer
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
name|cas
operator|.
name|impl
operator|.
name|XmiCasSerializer
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
name|util
operator|.
name|XmlCasSerializer
import|;
end_import

begin_comment
comment|/**  * Enumeration for types of cTAKES (UIMA) CAS serializer supported by cTAKES.  *   * A CAS serializer writes a CAS in the given format.  *  */
end_comment

begin_enum
specifier|public
enum|enum
name|CTAKESSerializer
block|{
name|XCAS
argument_list|(
name|XCASSerializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
block|,
name|XMI
argument_list|(
name|XmiCasSerializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
block|,
name|XML
argument_list|(
name|XmlCasSerializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|className
decl_stmt|;
specifier|private
name|CTAKESSerializer
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
block|}
specifier|public
name|String
name|getClassName
parameter_list|()
block|{
return|return
name|className
return|;
block|}
block|}
end_enum

end_unit

