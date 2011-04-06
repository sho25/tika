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
name|external
package|;
end_package

begin_comment
comment|/**  * Met Keys used by the {@link ExternalParsersConfigReader}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExternalParsersConfigReaderMetKeys
block|{
name|String
name|EXTERNAL_PARSERS_TAG
init|=
literal|"external-parsers"
decl_stmt|;
name|String
name|PARSER_TAG
init|=
literal|"parser"
decl_stmt|;
name|String
name|COMMAND_TAG
init|=
literal|"command"
decl_stmt|;
name|String
name|CHECK_TAG
init|=
literal|"check"
decl_stmt|;
name|String
name|MIMETYPES_TAG
init|=
literal|"mime-types"
decl_stmt|;
name|String
name|MIMETYPE_TAG
init|=
literal|"mime-type"
decl_stmt|;
name|String
name|METADATA_TAG
init|=
literal|"metadata"
decl_stmt|;
name|String
name|METADATA_MATCH_TAG
init|=
literal|"match"
decl_stmt|;
name|String
name|METADATA_KEY_ATTR
init|=
literal|"key"
decl_stmt|;
block|}
end_interface

end_unit

