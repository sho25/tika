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
name|config
package|;
end_package

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
name|exception
operator|.
name|TikaException
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
name|parser
operator|.
name|DefaultParser
import|;
end_import

begin_class
specifier|public
class|class
name|TikaConfigTest
extends|extends
name|TestCase
block|{
comment|/**      * Make sure that a configuration file can't reference to composite      * parser classes like {@link DefaultParser} in the&lt;parser&gt;      * configuration elements.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-866">TIKA-866</a>      */
specifier|public
name|void
name|testInvalidParser
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|xml
init|=
name|TikaConfigTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"TIKA-866-invalid.xml"
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|TikaConfig
argument_list|(
name|xml
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Composite parser class was allowed in<parser>"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|expected
parameter_list|)
block|{
comment|// OK
block|}
finally|finally
block|{
name|xml
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Make sure that a valid configuration file without mimetypes or      * detector entries can be loaded without problems.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-866">TIKA-866</a>      */
specifier|public
name|void
name|testValidParser
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|xml
init|=
name|TikaConfigTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"TIKA-866-valid.xml"
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|TikaConfig
argument_list|(
name|xml
argument_list|)
expr_stmt|;
comment|// OK
block|}
finally|finally
block|{
name|xml
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

