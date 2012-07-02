begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  * IPTC Metadata Descriptions taken from the IPTC Photo Metadata (July 2010)   * standard. These parts Copyright 2010 International Press Telecommunications   * Council.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|xmp
operator|.
name|convert
package|;
end_package

begin_comment
comment|/**  * Utility class to hold namespace information.  */
end_comment

begin_class
specifier|public
class|class
name|Namespace
block|{
specifier|public
name|String
name|uri
decl_stmt|;
specifier|public
name|String
name|prefix
decl_stmt|;
specifier|public
name|Namespace
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
block|}
end_class

end_unit

