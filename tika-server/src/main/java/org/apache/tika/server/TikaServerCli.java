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
name|server
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|cli
operator|.
name|CommandLine
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
name|cli
operator|.
name|CommandLineParser
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
name|cli
operator|.
name|GnuParser
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
name|cli
operator|.
name|HelpFormatter
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
name|cli
operator|.
name|Options
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
name|cxf
operator|.
name|binding
operator|.
name|BindingFactoryManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|JAXRSBindingFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|JAXRSServerFactoryBean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|lifecycle
operator|.
name|ResourceProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cxf
operator|.
name|jaxrs
operator|.
name|lifecycle
operator|.
name|SingletonResourceProvider
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
name|Tika
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

begin_class
specifier|public
class|class
name|TikaServerCli
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
name|TikaServerCli
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PORT
init|=
literal|9998
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_HOST
init|=
literal|"localhost"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|LOG_LEVELS
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"debug"
argument_list|,
literal|"info"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Options
name|getOptions
parameter_list|()
block|{
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"h"
argument_list|,
literal|"host"
argument_list|,
literal|true
argument_list|,
literal|"host name (default = "
operator|+
name|DEFAULT_HOST
operator|+
literal|')'
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"p"
argument_list|,
literal|"port"
argument_list|,
literal|true
argument_list|,
literal|"listen port (default = "
operator|+
name|DEFAULT_PORT
operator|+
literal|')'
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"l"
argument_list|,
literal|"log"
argument_list|,
literal|true
argument_list|,
literal|"request URI log level ('debug' or 'info')"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"?"
argument_list|,
literal|"help"
argument_list|,
literal|false
argument_list|,
literal|"this help message"
argument_list|)
expr_stmt|;
return|return
name|options
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Starting "
operator|+
operator|new
name|Tika
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" server"
argument_list|)
expr_stmt|;
try|try
block|{
name|Options
name|options
init|=
name|getOptions
argument_list|()
decl_stmt|;
name|CommandLineParser
name|cliParser
init|=
operator|new
name|GnuParser
argument_list|()
decl_stmt|;
name|CommandLine
name|line
init|=
name|cliParser
operator|.
name|parse
argument_list|(
name|options
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"help"
argument_list|)
condition|)
block|{
name|HelpFormatter
name|helpFormatter
init|=
operator|new
name|HelpFormatter
argument_list|()
decl_stmt|;
name|helpFormatter
operator|.
name|printHelp
argument_list|(
literal|"tikaserver"
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|host
init|=
name|DEFAULT_HOST
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"host"
argument_list|)
condition|)
block|{
name|host
operator|=
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"host"
argument_list|)
expr_stmt|;
block|}
name|int
name|port
init|=
name|DEFAULT_PORT
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"port"
argument_list|)
condition|)
block|{
name|port
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"port"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TikaLoggingFilter
name|logFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"log"
argument_list|)
condition|)
block|{
name|String
name|logLevel
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"log"
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG_LEVELS
operator|.
name|contains
argument_list|(
name|logLevel
argument_list|)
condition|)
block|{
name|boolean
name|isInfoLevel
init|=
literal|"info"
operator|.
name|equals
argument_list|(
name|logLevel
argument_list|)
decl_stmt|;
name|logFilter
operator|=
operator|new
name|TikaLoggingFilter
argument_list|(
name|isInfoLevel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Unsupported request URI log level: "
operator|+
name|logLevel
argument_list|)
expr_stmt|;
block|}
block|}
comment|// The Tika Configuration to use throughout
name|TikaConfig
name|tika
init|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
decl_stmt|;
name|JAXRSServerFactoryBean
name|sf
init|=
operator|new
name|JAXRSServerFactoryBean
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ResourceProvider
argument_list|>
name|rCoreProviders
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceProvider
argument_list|>
argument_list|()
decl_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|MetadataResource
argument_list|(
name|tika
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|DetectorResource
argument_list|(
name|tika
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaResource
argument_list|(
name|tika
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|UnpackerResource
argument_list|(
name|tika
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaMimeTypes
argument_list|(
name|tika
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaDetectors
argument_list|(
name|tika
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaParsers
argument_list|(
name|tika
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rCoreProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaVersion
argument_list|(
name|tika
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ResourceProvider
argument_list|>
name|rAllProviders
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceProvider
argument_list|>
argument_list|(
name|rCoreProviders
argument_list|)
decl_stmt|;
name|rAllProviders
operator|.
name|add
argument_list|(
operator|new
name|SingletonResourceProvider
argument_list|(
operator|new
name|TikaWelcome
argument_list|(
name|tika
argument_list|,
name|rCoreProviders
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setResourceProviders
argument_list|(
name|rAllProviders
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|providers
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|TarWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|ZipWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|CSVMessageBodyWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|JSONMessageBodyWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|TikaExceptionMapper
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|logFilter
operator|!=
literal|null
condition|)
block|{
name|providers
operator|.
name|add
argument_list|(
name|logFilter
argument_list|)
expr_stmt|;
block|}
name|sf
operator|.
name|setProviders
argument_list|(
name|providers
argument_list|)
expr_stmt|;
name|sf
operator|.
name|setAddress
argument_list|(
literal|"http://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|BindingFactoryManager
name|manager
init|=
name|sf
operator|.
name|getBus
argument_list|()
operator|.
name|getExtension
argument_list|(
name|BindingFactoryManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|JAXRSBindingFactory
name|factory
init|=
operator|new
name|JAXRSBindingFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setBus
argument_list|(
name|sf
operator|.
name|getBus
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|registerBindingFactory
argument_list|(
name|JAXRSBindingFactory
operator|.
name|JAXRS_BINDING_ID
argument_list|,
name|factory
argument_list|)
expr_stmt|;
name|sf
operator|.
name|create
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Started"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|fatal
argument_list|(
literal|"Can't start"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

