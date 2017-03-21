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
name|cxf
operator|.
name|rs
operator|.
name|security
operator|.
name|cors
operator|.
name|CrossOriginResourceSharingFilter
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
name|DigestingParser
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
name|utils
operator|.
name|CommonsDigester
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
name|server
operator|.
name|resource
operator|.
name|DetectorResource
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
name|server
operator|.
name|resource
operator|.
name|LanguageResource
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
name|server
operator|.
name|resource
operator|.
name|MetadataResource
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
name|server
operator|.
name|resource
operator|.
name|RecursiveMetadataResource
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
name|server
operator|.
name|resource
operator|.
name|TikaDetectors
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
name|server
operator|.
name|resource
operator|.
name|TikaMimeTypes
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
name|server
operator|.
name|resource
operator|.
name|TikaParsers
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
name|server
operator|.
name|resource
operator|.
name|TikaResource
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
name|server
operator|.
name|resource
operator|.
name|TikaVersion
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
name|server
operator|.
name|resource
operator|.
name|TikaWelcome
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
name|server
operator|.
name|resource
operator|.
name|TranslateResource
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
name|server
operator|.
name|resource
operator|.
name|UnpackerResource
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
name|server
operator|.
name|writer
operator|.
name|CSVMessageBodyWriter
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
name|server
operator|.
name|writer
operator|.
name|JSONMessageBodyWriter
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
name|server
operator|.
name|writer
operator|.
name|MetadataListMessageBodyWriter
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
name|server
operator|.
name|writer
operator|.
name|TarWriter
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
name|server
operator|.
name|writer
operator|.
name|TextMessageBodyWriter
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
name|server
operator|.
name|writer
operator|.
name|XMPMessageBodyWriter
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
name|server
operator|.
name|writer
operator|.
name|ZipWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
specifier|public
class|class
name|TikaServerCli
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_PORT
init|=
literal|9998
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_DIGEST_MARK_LIMIT
init|=
literal|20
operator|*
literal|1024
operator|*
literal|1024
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
argument_list|<>
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
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TikaServerCli
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FILE_URL_WARNING
init|=
literal|"WARNING: You have chosen to run tika-server with fileUrl enabled.\n"
operator|+
literal|"Whoever has access to your service now has the same read permissions\n"
operator|+
literal|"as tika-server. Users could request and receive a sensitive file from your\n"
operator|+
literal|"drive or a webpage from your intranet.  See CVE-2015-3271.\n"
operator|+
literal|"Please make sure you know what you are doing."
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
literal|"C"
argument_list|,
literal|"cors"
argument_list|,
literal|true
argument_list|,
literal|"origin allowed to make CORS requests (default=NONE)\nall allowed if \"all\""
argument_list|)
expr_stmt|;
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
literal|", use * for all)"
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
literal|"c"
argument_list|,
literal|"config"
argument_list|,
literal|true
argument_list|,
literal|"Tika Configuration file to override default config with."
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"d"
argument_list|,
literal|"digest"
argument_list|,
literal|true
argument_list|,
literal|"include digest in metadata, e.g. md5,sha256"
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"dml"
argument_list|,
literal|"digestMarkLimit"
argument_list|,
literal|true
argument_list|,
literal|"max number of bytes to mark on stream for digest"
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
literal|"s"
argument_list|,
literal|"includeStack"
argument_list|,
literal|false
argument_list|,
literal|"whether or not to return a stack trace\nif there is an exception during 'parse'"
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
name|options
operator|.
name|addOption
argument_list|(
literal|"enableUnsecureFeatures"
argument_list|,
literal|false
argument_list|,
literal|"this is required to enable fileUrl."
argument_list|)
expr_stmt|;
name|options
operator|.
name|addOption
argument_list|(
literal|"enableFileUrl"
argument_list|,
literal|false
argument_list|,
literal|"allows user to pass in fileUrl instead of InputStream."
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting {} server"
argument_list|,
operator|new
name|Tika
argument_list|()
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
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|host
argument_list|)
condition|)
block|{
name|host
operator|=
literal|"0.0.0.0"
expr_stmt|;
block|}
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
name|boolean
name|returnStackTrace
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"includeStack"
argument_list|)
condition|)
block|{
name|returnStackTrace
operator|=
literal|true
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Unsupported request URI log level: {}"
argument_list|,
name|logLevel
argument_list|)
expr_stmt|;
block|}
block|}
name|CrossOriginResourceSharingFilter
name|corsFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"cors"
argument_list|)
condition|)
block|{
name|corsFilter
operator|=
operator|new
name|CrossOriginResourceSharingFilter
argument_list|()
expr_stmt|;
name|String
name|url
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"cors"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|origins
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|url
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
name|origins
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
comment|// Empty list allows all origins.
name|corsFilter
operator|.
name|setAllowOrigins
argument_list|(
name|origins
argument_list|)
expr_stmt|;
block|}
comment|// The Tika Configuration to use throughout
name|TikaConfig
name|tika
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"config"
argument_list|)
condition|)
block|{
name|String
name|configFilePath
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using custom config: {}"
argument_list|,
name|configFilePath
argument_list|)
expr_stmt|;
name|tika
operator|=
operator|new
name|TikaConfig
argument_list|(
name|configFilePath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tika
operator|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
expr_stmt|;
block|}
name|DigestingParser
operator|.
name|Digester
name|digester
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"digest"
argument_list|)
condition|)
block|{
name|int
name|digestMarkLimit
init|=
name|DEFAULT_DIGEST_MARK_LIMIT
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"dml"
argument_list|)
condition|)
block|{
name|String
name|dmlS
init|=
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"dml"
argument_list|)
decl_stmt|;
try|try
block|{
name|digestMarkLimit
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|dmlS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Must have parseable int after digestMarkLimit(dml): "
operator|+
name|dmlS
argument_list|)
throw|;
block|}
block|}
name|digester
operator|=
operator|new
name|CommonsDigester
argument_list|(
name|digestMarkLimit
argument_list|,
name|CommonsDigester
operator|.
name|parse
argument_list|(
name|line
operator|.
name|getOptionValue
argument_list|(
literal|"digest"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"enableFileUrl"
argument_list|)
operator|&&
operator|!
name|line
operator|.
name|hasOption
argument_list|(
literal|"enableUnsecureFeatures"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"If you want to enable fileUrl, you must also acknowledge the security risks\n"
operator|+
literal|"by including --enableUnsecureFeatures.  See CVE-2015-3271."
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
name|InputStreamFactory
name|inputStreamFactory
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|hasOption
argument_list|(
literal|"enableFileUrl"
argument_list|)
operator|&&
name|line
operator|.
name|hasOption
argument_list|(
literal|"enableUnsecureFeatures"
argument_list|)
condition|)
block|{
name|inputStreamFactory
operator|=
operator|new
name|URLEnabledInputStreamFactory
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|FILE_URL_WARNING
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|inputStreamFactory
operator|=
operator|new
name|DefaultInputStreamFactory
argument_list|()
expr_stmt|;
block|}
name|TikaResource
operator|.
name|init
argument_list|(
name|tika
argument_list|,
name|digester
argument_list|,
name|inputStreamFactory
argument_list|)
expr_stmt|;
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
argument_list|()
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
name|RecursiveMetadataResource
argument_list|()
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
argument_list|()
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
name|LanguageResource
argument_list|()
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
name|TranslateResource
argument_list|()
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
argument_list|()
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
argument_list|()
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
argument_list|()
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
argument_list|()
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
argument_list|()
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
argument_list|()
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
name|MetadataListMessageBodyWriter
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
name|XMPMessageBodyWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|TextMessageBodyWriter
argument_list|()
argument_list|)
expr_stmt|;
name|providers
operator|.
name|add
argument_list|(
operator|new
name|TikaServerParseExceptionMapper
argument_list|(
name|returnStackTrace
argument_list|)
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
if|if
condition|(
name|corsFilter
operator|!=
literal|null
condition|)
block|{
name|providers
operator|.
name|add
argument_list|(
name|corsFilter
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
name|String
name|url
init|=
literal|"http://"
operator|+
name|host
operator|+
literal|":"
operator|+
name|port
operator|+
literal|"/"
decl_stmt|;
name|sf
operator|.
name|setAddress
argument_list|(
name|url
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Started Apache Tika server at {}"
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
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

