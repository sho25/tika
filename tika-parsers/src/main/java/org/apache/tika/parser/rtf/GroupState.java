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
name|rtf
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_comment
comment|/* Holds all state associated with current RTF group, ie {  * ... }. */
end_comment

begin_class
class|class
name|GroupState
block|{
specifier|public
name|int
name|depth
decl_stmt|;
specifier|public
name|boolean
name|bold
decl_stmt|;
specifier|public
name|boolean
name|italic
decl_stmt|;
comment|// True if we are skipping all text in current group,
comment|// eg if group leads with a \*:
specifier|public
name|boolean
name|ignore
decl_stmt|;
comment|// Default is 1 if no uc control has been seen yet:
specifier|public
name|int
name|ucSkip
init|=
literal|1
decl_stmt|;
specifier|public
name|int
name|list
decl_stmt|;
specifier|public
name|int
name|listLevel
decl_stmt|;
specifier|public
name|Charset
name|fontCharset
decl_stmt|;
comment|//in objdata
specifier|public
name|boolean
name|objdata
decl_stmt|;
comment|//depth in pict, 1 = at pict level
specifier|public
name|int
name|pictDepth
decl_stmt|;
comment|//in picprop key/value pair
specifier|public
name|boolean
name|sp
decl_stmt|;
comment|//in picprop's name
specifier|public
name|boolean
name|sn
decl_stmt|;
comment|//in picprop's value
specifier|public
name|boolean
name|sv
decl_stmt|;
comment|//in embedded object or not
specifier|public
name|boolean
name|object
decl_stmt|;
comment|// Create default (root) GroupState
specifier|public
name|GroupState
parameter_list|()
block|{     }
comment|// Create new GroupState, inheriting all properties from current one, adding 1 to the depth
specifier|public
name|GroupState
parameter_list|(
name|GroupState
name|other
parameter_list|)
block|{
name|bold
operator|=
name|other
operator|.
name|bold
expr_stmt|;
name|italic
operator|=
name|other
operator|.
name|italic
expr_stmt|;
name|ignore
operator|=
name|other
operator|.
name|ignore
expr_stmt|;
name|ucSkip
operator|=
name|other
operator|.
name|ucSkip
expr_stmt|;
name|list
operator|=
name|other
operator|.
name|list
expr_stmt|;
name|listLevel
operator|=
name|other
operator|.
name|listLevel
expr_stmt|;
name|fontCharset
operator|=
name|other
operator|.
name|fontCharset
expr_stmt|;
name|depth
operator|=
literal|1
operator|+
name|other
operator|.
name|depth
expr_stmt|;
name|pictDepth
operator|=
name|other
operator|.
name|pictDepth
operator|>
literal|0
condition|?
name|other
operator|.
name|pictDepth
operator|+
literal|1
else|:
literal|0
expr_stmt|;
comment|//do not inherit object, sn, sv or sp
block|}
block|}
end_class

end_unit

