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
name|metadata
package|;
end_package

begin_interface
specifier|public
interface|interface
name|XMPIdq
block|{
name|String
name|NAMESPACE_URI
init|=
literal|"http://ns.adobe.com/xmp/identifier/qual/1.0/"
decl_stmt|;
name|String
name|PREFIX
init|=
literal|"xmpidq"
decl_stmt|;
name|String
name|PREFIX_DELIMITER
init|=
literal|":"
decl_stmt|;
comment|/**      * A qualifier providing the name of the formal identification      * scheme used for an item in the xmp:Identifier array.      */
name|Property
name|SCHEME
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX
operator|+
name|PREFIX_DELIMITER
operator|+
literal|"Scheme"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

