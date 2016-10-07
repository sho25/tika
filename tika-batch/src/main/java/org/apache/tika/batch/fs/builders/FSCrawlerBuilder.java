begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|batch
operator|.
name|fs
operator|.
name|builders
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

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
name|UnsupportedEncodingException
import|;
end_import

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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ArrayBlockingQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|batch
operator|.
name|FileResource
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
name|batch
operator|.
name|FileResourceCrawler
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
name|batch
operator|.
name|builders
operator|.
name|BatchProcessBuilder
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
name|batch
operator|.
name|builders
operator|.
name|ICrawlerBuilder
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
name|batch
operator|.
name|fs
operator|.
name|FSDirectoryCrawler
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
name|batch
operator|.
name|fs
operator|.
name|FSDocumentSelector
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
name|batch
operator|.
name|fs
operator|.
name|FSListCrawler
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
name|extractor
operator|.
name|DocumentSelector
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
name|util
operator|.
name|PropsUtil
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
name|util
operator|.
name|XMLDOMUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Builds either an FSDirectoryCrawler or an FSListCrawler.  */
end_comment

begin_class
specifier|public
class|class
name|FSCrawlerBuilder
implements|implements
name|ICrawlerBuilder
block|{
specifier|private
specifier|final
specifier|static
name|String
name|MAX_CONSEC_WAIT_MILLIS
init|=
literal|"maxConsecWaitMillis"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|MAX_FILES_TO_ADD_ATTR
init|=
literal|"maxFilesToAdd"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|MAX_FILES_TO_CONSIDER_ATTR
init|=
literal|"maxFilesToConsider"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|CRAWL_ORDER
init|=
literal|"crawlOrder"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|INPUT_DIR_ATTR
init|=
literal|"inputDir"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|INPUT_START_DIR_ATTR
init|=
literal|"startDir"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|MAX_FILE_SIZE_BYTES_ATTR
init|=
literal|"maxFileSizeBytes"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|MIN_FILE_SIZE_BYTES_ATTR
init|=
literal|"minFileSizeBytes"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|INCLUDE_FILE_PAT_ATTR
init|=
literal|"includeFilePat"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|EXCLUDE_FILE_PAT_ATTR
init|=
literal|"excludeFilePat"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|FileResourceCrawler
name|build
parameter_list|(
name|Node
name|node
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|runtimeAttributes
parameter_list|,
name|ArrayBlockingQueue
argument_list|<
name|FileResource
argument_list|>
name|queue
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
name|XMLDOMUtil
operator|.
name|mapifyAttrs
argument_list|(
name|node
argument_list|,
name|runtimeAttributes
argument_list|)
decl_stmt|;
name|int
name|numConsumers
init|=
name|BatchProcessBuilder
operator|.
name|getNumConsumers
argument_list|(
name|runtimeAttributes
argument_list|)
decl_stmt|;
name|Path
name|inputDir
init|=
name|PropsUtil
operator|.
name|getPath
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|INPUT_DIR_ATTR
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"input"
argument_list|)
argument_list|)
decl_stmt|;
name|FileResourceCrawler
name|crawler
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|attributes
operator|.
name|containsKey
argument_list|(
literal|"fileList"
argument_list|)
condition|)
block|{
name|String
name|randomCrawlString
init|=
name|attributes
operator|.
name|get
argument_list|(
name|CRAWL_ORDER
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomCrawlString
operator|!=
literal|null
condition|)
block|{
comment|//TODO: change to logger warn or throw RuntimeException?
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"randomCrawl attribute is ignored by FSListCrawler"
argument_list|)
expr_stmt|;
block|}
name|Path
name|fileList
init|=
name|PropsUtil
operator|.
name|getPath
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
literal|"fileList"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|encodingString
init|=
name|PropsUtil
operator|.
name|getString
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
literal|"fileListEncoding"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|Charset
name|encoding
init|=
name|Charset
operator|.
name|forName
argument_list|(
name|encodingString
argument_list|)
decl_stmt|;
name|crawler
operator|=
operator|new
name|FSListCrawler
argument_list|(
name|queue
argument_list|,
name|numConsumers
argument_list|,
name|inputDir
argument_list|,
name|fileList
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"fileList file not found for FSListCrawler: "
operator|+
name|fileList
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"fileList encoding not supported: "
operator|+
name|encodingString
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"IOException while trying to open fileList: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|FSDirectoryCrawler
operator|.
name|CRAWL_ORDER
name|crawlOrder
init|=
name|getCrawlOrder
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|CRAWL_ORDER
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|startDir
init|=
name|PropsUtil
operator|.
name|getPath
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|INPUT_START_DIR_ATTR
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|startDir
operator|==
literal|null
condition|)
block|{
name|crawler
operator|=
operator|new
name|FSDirectoryCrawler
argument_list|(
name|queue
argument_list|,
name|numConsumers
argument_list|,
name|inputDir
argument_list|,
name|crawlOrder
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|crawler
operator|=
operator|new
name|FSDirectoryCrawler
argument_list|(
name|queue
argument_list|,
name|numConsumers
argument_list|,
name|inputDir
argument_list|,
name|startDir
argument_list|,
name|crawlOrder
argument_list|)
expr_stmt|;
block|}
block|}
name|crawler
operator|.
name|setMaxFilesToConsider
argument_list|(
name|PropsUtil
operator|.
name|getInt
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|MAX_FILES_TO_CONSIDER_ATTR
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|crawler
operator|.
name|setMaxFilesToAdd
argument_list|(
name|PropsUtil
operator|.
name|getInt
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|MAX_FILES_TO_ADD_ATTR
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|DocumentSelector
name|selector
init|=
name|buildSelector
argument_list|(
name|attributes
argument_list|)
decl_stmt|;
if|if
condition|(
name|selector
operator|!=
literal|null
condition|)
block|{
name|crawler
operator|.
name|setDocumentSelector
argument_list|(
name|selector
argument_list|)
expr_stmt|;
block|}
name|crawler
operator|.
name|setMaxConsecWaitInMillis
argument_list|(
name|PropsUtil
operator|.
name|getLong
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|MAX_CONSEC_WAIT_MILLIS
argument_list|)
argument_list|,
literal|300000L
argument_list|)
argument_list|)
expr_stmt|;
comment|//5 minutes
return|return
name|crawler
return|;
block|}
specifier|private
name|FSDirectoryCrawler
operator|.
name|CRAWL_ORDER
name|getCrawlOrder
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|s
operator|.
name|equals
argument_list|(
literal|"os"
argument_list|)
condition|)
block|{
return|return
name|FSDirectoryCrawler
operator|.
name|CRAWL_ORDER
operator|.
name|OS_ORDER
return|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|contains
argument_list|(
literal|"rand"
argument_list|)
condition|)
block|{
return|return
name|FSDirectoryCrawler
operator|.
name|CRAWL_ORDER
operator|.
name|RANDOM
return|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|contains
argument_list|(
literal|"sort"
argument_list|)
condition|)
block|{
return|return
name|FSDirectoryCrawler
operator|.
name|CRAWL_ORDER
operator|.
name|SORTED
return|;
block|}
else|else
block|{
return|return
name|FSDirectoryCrawler
operator|.
name|CRAWL_ORDER
operator|.
name|OS_ORDER
return|;
block|}
block|}
specifier|private
name|DocumentSelector
name|buildSelector
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|)
block|{
name|String
name|includeString
init|=
name|attributes
operator|.
name|get
argument_list|(
name|INCLUDE_FILE_PAT_ATTR
argument_list|)
decl_stmt|;
name|String
name|excludeString
init|=
name|attributes
operator|.
name|get
argument_list|(
name|EXCLUDE_FILE_PAT_ATTR
argument_list|)
decl_stmt|;
name|long
name|maxFileSize
init|=
name|PropsUtil
operator|.
name|getLong
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|MAX_FILE_SIZE_BYTES_ATTR
argument_list|)
argument_list|,
operator|-
literal|1L
argument_list|)
decl_stmt|;
name|long
name|minFileSize
init|=
name|PropsUtil
operator|.
name|getLong
argument_list|(
name|attributes
operator|.
name|get
argument_list|(
name|MIN_FILE_SIZE_BYTES_ATTR
argument_list|)
argument_list|,
operator|-
literal|1L
argument_list|)
decl_stmt|;
name|Pattern
name|includePat
init|=
operator|(
name|includeString
operator|!=
literal|null
operator|&&
name|includeString
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|Pattern
operator|.
name|compile
argument_list|(
name|includeString
argument_list|)
else|:
literal|null
decl_stmt|;
name|Pattern
name|excludePat
init|=
operator|(
name|excludeString
operator|!=
literal|null
operator|&&
name|excludeString
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|?
name|Pattern
operator|.
name|compile
argument_list|(
name|excludeString
argument_list|)
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|FSDocumentSelector
argument_list|(
name|includePat
argument_list|,
name|excludePat
argument_list|,
name|minFileSize
argument_list|,
name|maxFileSize
argument_list|)
return|;
block|}
block|}
end_class

end_unit

