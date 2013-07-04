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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ResourceLoggingClassLoader
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
name|AutoDetectParser
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
comment|/**      * Make sure that a configuration file can't reference the      * {@link AutoDetectParser} class a&lt;parser&gt; configuration element.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-866">TIKA-866</a>      */
specifier|public
name|void
name|testInvalidParser
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
name|TikaConfigTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"TIKA-866-invalid.xml"
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tika.config"
argument_list|,
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|TikaConfig
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"AutoDetectParser allowed in a<parser> element"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|expected
parameter_list|)
block|{         }
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tika.config"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Make sure that a configuration file can reference also a composite      * parser class like {@link DefaultParser} in a&lt;parser&gt;      * configuration element.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-866">TIKA-866</a>      */
specifier|public
name|void
name|testCompositeParser
parameter_list|()
throws|throws
name|Exception
block|{
name|URL
name|url
init|=
name|TikaConfigTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"TIKA-866-composite.xml"
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tika.config"
argument_list|,
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|TikaConfig
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected TikaException: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tika.config"
argument_list|)
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
name|URL
name|url
init|=
name|TikaConfigTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"TIKA-866-valid.xml"
argument_list|)
decl_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"tika.config"
argument_list|,
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
operator|new
name|TikaConfig
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unexpected TikaException: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"tika.config"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * TIKA-1145 If the TikaConfig has a ClassLoader set on it,      * that should be used when loading the mimetypes and when      * discovering services      */
specifier|public
name|void
name|testClassLoaderUsedEverywhere
parameter_list|()
throws|throws
name|Exception
block|{
name|ResourceLoggingClassLoader
name|customLoader
init|=
operator|new
name|ResourceLoggingClassLoader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
name|TikaConfig
name|config
decl_stmt|;
comment|// Without a classloader set, normal one will be used
name|config
operator|=
operator|new
name|TikaConfig
argument_list|()
expr_stmt|;
name|config
operator|.
name|getMediaTypeRegistry
argument_list|()
expr_stmt|;
name|config
operator|.
name|getParser
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|customLoader
operator|.
name|getLoadedResources
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// With a classloader set, resources will come through it
name|config
operator|=
operator|new
name|TikaConfig
argument_list|(
name|customLoader
argument_list|)
expr_stmt|;
name|config
operator|.
name|getMediaTypeRegistry
argument_list|()
expr_stmt|;
name|config
operator|.
name|getParser
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|URL
argument_list|>
argument_list|>
name|resources
init|=
name|customLoader
operator|.
name|getLoadedResources
argument_list|()
decl_stmt|;
name|int
name|resourcesCount
init|=
name|resources
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not enough things used the classloader, found only "
operator|+
name|resourcesCount
argument_list|,
name|resourcesCount
operator|>
literal|3
argument_list|)
expr_stmt|;
comment|// Ensure everything that should do, did use it
comment|// - Parsers
name|assertNotNull
argument_list|(
name|resources
operator|.
name|get
argument_list|(
literal|"META-INF/services/org.apache.tika.parser.Parser"
argument_list|)
argument_list|)
expr_stmt|;
comment|// - Detectors
name|assertNotNull
argument_list|(
name|resources
operator|.
name|get
argument_list|(
literal|"META-INF/services/org.apache.tika.detect.Detector"
argument_list|)
argument_list|)
expr_stmt|;
comment|// - Built-In Mimetypes
name|assertNotNull
argument_list|(
name|resources
operator|.
name|get
argument_list|(
literal|"org/apache/tika/mime/tika-mimetypes.xml"
argument_list|)
argument_list|)
expr_stmt|;
comment|// - Custom Mimetypes
name|assertNotNull
argument_list|(
name|resources
operator|.
name|get
argument_list|(
literal|"org/apache/tika/mime/custom-mimetypes.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

