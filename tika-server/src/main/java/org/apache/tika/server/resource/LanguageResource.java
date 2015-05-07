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
name|server
operator|.
name|resource
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Consumes
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|POST
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PUT
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|TikaConfig
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
name|language
operator|.
name|LanguageIdentifier
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
name|language
operator|.
name|LanguageProfile
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_class
annotation|@
name|Path
argument_list|(
literal|"/language"
argument_list|)
specifier|public
class|class
name|LanguageResource
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|logger
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LanguageResource
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|TikaConfig
name|config
decl_stmt|;
specifier|public
name|LanguageResource
parameter_list|(
name|TikaConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
annotation|@
name|PUT
annotation|@
name|POST
annotation|@
name|Path
argument_list|(
literal|"/stream"
argument_list|)
annotation|@
name|Consumes
argument_list|(
literal|"*/*"
argument_list|)
annotation|@
name|Produces
argument_list|(
literal|"text/plain"
argument_list|)
specifier|public
name|String
name|detect
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
comment|// comme çi comme ça
comment|// this is English!
name|String
name|fileTxt
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|is
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"File: "
operator|+
name|fileTxt
argument_list|)
expr_stmt|;
name|LanguageIdentifier
name|lang
init|=
operator|new
name|LanguageIdentifier
argument_list|(
operator|new
name|LanguageProfile
argument_list|(
name|fileTxt
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|detectedLang
init|=
name|lang
operator|.
name|getLanguage
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Detecting language for incoming resource: ["
operator|+
name|detectedLang
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|detectedLang
return|;
block|}
annotation|@
name|PUT
annotation|@
name|POST
annotation|@
name|Path
argument_list|(
literal|"/string"
argument_list|)
annotation|@
name|Consumes
argument_list|(
literal|"*/*"
argument_list|)
annotation|@
name|Produces
argument_list|(
literal|"text/plain"
argument_list|)
specifier|public
name|String
name|detect
parameter_list|(
specifier|final
name|String
name|string
parameter_list|)
throws|throws
name|IOException
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"String: "
operator|+
name|string
argument_list|)
expr_stmt|;
name|LanguageIdentifier
name|lang
init|=
operator|new
name|LanguageIdentifier
argument_list|(
operator|new
name|LanguageProfile
argument_list|(
name|string
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|detectedLang
init|=
name|lang
operator|.
name|getLanguage
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Detecting language for incoming resource: ["
operator|+
name|detectedLang
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|detectedLang
return|;
block|}
block|}
end_class

end_unit
