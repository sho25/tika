begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
package|;
end_package

begin_comment
comment|/**  *   * Met Keys used by the {@link MimeTypesReader}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MimeTypesReaderMetKeys
block|{
specifier|public
specifier|static
specifier|final
name|String
name|MIME_INFO_TAG
init|=
literal|"mime-info"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MIME_TYPE_TAG
init|=
literal|"mime-type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MIME_TYPE_TYPE_ATTR
init|=
literal|"type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COMMENT_TAG
init|=
literal|"_comment"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GLOB_TAG
init|=
literal|"glob"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ISREGEX_ATTR
init|=
literal|"isregex"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN_ATTR
init|=
literal|"pattern"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MAGIC_TAG
init|=
literal|"magic"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ALIAS_TAG
init|=
literal|"alias"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ALIAS_TYPE_ATTR
init|=
literal|"type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ROOT_XML_TAG
init|=
literal|"root-XML"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SUB_CLASS_OF_TAG
init|=
literal|"sub-class-of"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SUB_CLASS_TYPE_ATTR
init|=
literal|"type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MAGIC_PRIORITY_ATTR
init|=
literal|"priority"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MATCH_TAG
init|=
literal|"match"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MATCH_OFFSET_ATTR
init|=
literal|"offset"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MATCH_TYPE_ATTR
init|=
literal|"type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MATCH_VALUE_ATTR
init|=
literal|"value"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MATCH_MASK_ATTR
init|=
literal|"mask"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NS_URI_ATTR
init|=
literal|"namespaceURI"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LOCAL_NAME_ATTR
init|=
literal|"localName"
decl_stmt|;
block|}
end_interface

end_unit

