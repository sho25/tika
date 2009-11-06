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
name|language
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
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * JUnit based test of class {@link LanguageIdentifier}.  *  * @author Sami Siren  * @author Jerome Charron - http://frutch.free.fr/  */
end_comment

begin_class
specifier|public
class|class
name|LanguageIdentifierTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|languages
init|=
operator|new
name|String
index|[]
block|{
literal|"da"
block|,
literal|"de"
block|,
comment|/* "el", */
literal|"en"
block|,
literal|"es"
block|,
literal|"fi"
block|,
literal|"fr"
block|,
literal|"it"
block|,
literal|"nl"
block|,
literal|"pt"
block|,
literal|"sv"
block|}
decl_stmt|;
specifier|public
name|void
name|testLanguageDetection
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|language
range|:
name|languages
control|)
block|{
name|ProfilingWriter
name|writer
init|=
operator|new
name|ProfilingWriter
argument_list|()
decl_stmt|;
name|writeTo
argument_list|(
name|language
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|LanguageIdentifier
name|identifier
init|=
operator|new
name|LanguageIdentifier
argument_list|(
name|writer
operator|.
name|getProfile
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|identifier
operator|.
name|toString
argument_list|()
argument_list|,
name|identifier
operator|.
name|isReasonablyCertain
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|language
argument_list|,
name|identifier
operator|.
name|getLanguage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testMixedLanguages
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|String
name|language
range|:
name|languages
control|)
block|{
for|for
control|(
name|String
name|other
range|:
name|languages
control|)
block|{
if|if
condition|(
operator|!
name|language
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
block|{
name|ProfilingWriter
name|writer
init|=
operator|new
name|ProfilingWriter
argument_list|()
decl_stmt|;
name|writeTo
argument_list|(
name|language
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|writeTo
argument_list|(
name|other
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|LanguageIdentifier
name|identifier
init|=
operator|new
name|LanguageIdentifier
argument_list|(
name|writer
operator|.
name|getProfile
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|identifier
operator|.
name|isReasonablyCertain
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|writeTo
parameter_list|(
name|String
name|language
parameter_list|,
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
name|LanguageIdentifierTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|language
operator|+
literal|".test"
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

