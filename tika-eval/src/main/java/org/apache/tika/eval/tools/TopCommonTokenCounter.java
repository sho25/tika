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
name|eval
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedWriter
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Files
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
name|compress
operator|.
name|compressors
operator|.
name|gzip
operator|.
name|GzipCompressorInputStream
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
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|TextField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|DirectoryReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriterConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|LeafReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Terms
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|TermsEnum
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|FSDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|PriorityQueue
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
name|eval
operator|.
name|tokens
operator|.
name|AnalyzerManager
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
name|eval
operator|.
name|tokens
operator|.
name|URLEmailNormalizingFilterFactory
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
name|utils
operator|.
name|ProcessUtils
import|;
end_import

begin_comment
comment|/**  * Utility class that reads in a UTF-8 input file with one document per row  * and outputs the 20000 tokens with the highest document frequencies.  *  * The CommmonTokensAnalyzer intentionally drops tokens shorter than 4 characters,  * but includes bigrams for cjk.  *  * It also has a white list for __email__ and __url__ and a black list  * for common html markup terms.  */
end_comment

begin_class
specifier|public
class|class
name|TopCommonTokenCounter
block|{
specifier|private
specifier|static
name|String
name|LICENSE
init|=
literal|"# Licensed to the Apache Software Foundation (ASF) under one or more\n"
operator|+
literal|"# contributor license agreements.  See the NOTICE file distributed with\n"
operator|+
literal|"# this work for additional information regarding copyright ownership.\n"
operator|+
literal|"# The ASF licenses this file to You under the Apache License, Version 2.0\n"
operator|+
literal|"# (the \"License\"); you may not use this file except in compliance with\n"
operator|+
literal|"# the License.  You may obtain a copy of the License at\n"
operator|+
literal|"#\n"
operator|+
literal|"#     http://www.apache.org/licenses/LICENSE-2.0\n"
operator|+
literal|"#\n"
operator|+
literal|"# Unless required by applicable law or agreed to in writing, software\n"
operator|+
literal|"# distributed under the License is distributed on an \"AS IS\" BASIS,\n"
operator|+
literal|"# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
operator|+
literal|"# See the License for the specific language governing permissions and\n"
operator|+
literal|"# limitations under the License.\n"
operator|+
literal|"#\n"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FIELD
init|=
literal|"f"
decl_stmt|;
specifier|private
specifier|static
name|int
name|TOP_N
init|=
literal|30000
decl_stmt|;
specifier|private
specifier|static
name|int
name|MIN_DOC_FREQ
init|=
literal|10
decl_stmt|;
comment|//these should exist in every list
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|WHITE_LIST
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|URLEmailNormalizingFilterFactory
operator|.
name|URL
block|,
name|URLEmailNormalizingFilterFactory
operator|.
name|EMAIL
block|}
argument_list|)
argument_list|)
decl_stmt|;
comment|//words to ignore
comment|//these are common 4 letter html markup words that we do
comment|//not want to count in case of failed markup processing.
comment|//see: https://issues.apache.org/jira/browse/TIKA-2267?focusedCommentId=15872055&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-15872055
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|BLACK_LIST
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"span"
argument_list|,
literal|"table"
argument_list|,
literal|"href"
argument_list|,
literal|"head"
argument_list|,
literal|"title"
argument_list|,
literal|"body"
argument_list|,
literal|"html"
argument_list|,
literal|"tagname"
argument_list|,
literal|"lang"
argument_list|,
literal|"style"
argument_list|,
literal|"script"
argument_list|,
literal|"strong"
argument_list|,
literal|"blockquote"
argument_list|,
literal|"form"
argument_list|,
literal|"iframe"
argument_list|,
literal|"section"
argument_list|,
literal|"colspan"
argument_list|,
literal|"rowspan"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|commonTokensFile
init|=
name|Paths
operator|.
name|get
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|inputFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|inputFiles
operator|.
name|add
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|ProcessUtils
operator|.
name|unescapeCommandLine
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TopCommonTokenCounter
name|counter
init|=
operator|new
name|TopCommonTokenCounter
argument_list|()
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|commonTokensFile
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|commonTokensFile
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" exists. I'm skipping this."
argument_list|)
expr_stmt|;
return|return;
block|}
name|counter
operator|.
name|execute
argument_list|(
name|commonTokensFile
argument_list|,
name|inputFiles
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|execute
parameter_list|(
name|Path
name|commonTokensFile
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|inputFiles
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|luceneDir
init|=
name|Files
operator|.
name|createTempDirectory
argument_list|(
literal|"tika-eval-lucene-"
argument_list|)
decl_stmt|;
name|AbstractTokenTFDFPriorityQueue
name|queue
init|=
operator|new
name|TokenDFPriorityQueue
argument_list|(
name|TOP_N
argument_list|)
decl_stmt|;
name|long
name|totalDocs
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|sumDocFreqs
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|sumTotalTermFreqs
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|uniqueTerms
init|=
operator|-
literal|1
decl_stmt|;
try|try
init|(
name|Directory
name|directory
init|=
name|FSDirectory
operator|.
name|open
argument_list|(
name|luceneDir
argument_list|)
init|)
block|{
name|AnalyzerManager
name|analyzerManager
init|=
name|AnalyzerManager
operator|.
name|newInstance
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|analyzerManager
operator|.
name|getCommonTokensAnalyzer
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|indexWriterConfig
init|=
operator|new
name|IndexWriterConfig
argument_list|(
name|analyzer
argument_list|)
decl_stmt|;
name|int
name|maxLen
init|=
literal|1000000
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
try|try
init|(
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|indexWriterConfig
argument_list|)
init|)
block|{
name|List
argument_list|<
name|Document
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Path
name|inputFile
range|:
name|inputFiles
control|)
block|{
comment|//total hack
name|boolean
name|isLeipzig
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|inputFile
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"-sentences.txt"
argument_list|)
condition|)
block|{
name|isLeipzig
operator|=
literal|true
expr_stmt|;
block|}
name|int
name|lines
init|=
literal|0
decl_stmt|;
try|try
init|(
name|BufferedReader
name|reader
init|=
name|getReader
argument_list|(
name|inputFile
argument_list|)
init|)
block|{
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isLeipzig
condition|)
block|{
name|int
name|tab
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tab
operator|>
operator|-
literal|1
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
name|tab
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|len
operator|+=
name|line
operator|.
name|length
argument_list|()
expr_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
name|FIELD
argument_list|,
name|line
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|>
name|maxLen
condition|)
block|{
name|writer
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|docs
operator|.
name|clear
argument_list|()
expr_stmt|;
name|len
operator|=
literal|0
expr_stmt|;
block|}
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
operator|++
name|lines
operator|%
literal|100000
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"processed "
operator|+
name|lines
operator|+
literal|" for "
operator|+
name|inputFile
operator|.
name|getFileName
argument_list|()
operator|+
literal|" :: "
operator|+
name|commonTokensFile
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|docs
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
try|try
init|(
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
init|)
block|{
name|LeafReader
name|wrappedReader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|totalDocs
operator|=
name|wrappedReader
operator|.
name|getDocCount
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|sumDocFreqs
operator|=
name|wrappedReader
operator|.
name|getSumDocFreq
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|sumTotalTermFreqs
operator|=
name|wrappedReader
operator|.
name|getSumTotalTermFreq
argument_list|(
name|FIELD
argument_list|)
expr_stmt|;
name|Terms
name|terms
init|=
name|wrappedReader
operator|.
name|terms
argument_list|(
name|FIELD
argument_list|)
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRef
name|bytesRef
init|=
name|termsEnum
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|docsWThisField
init|=
name|wrappedReader
operator|.
name|getDocCount
argument_list|(
name|FIELD
argument_list|)
decl_stmt|;
while|while
condition|(
name|bytesRef
operator|!=
literal|null
condition|)
block|{
name|uniqueTerms
operator|++
expr_stmt|;
name|int
name|df
init|=
name|termsEnum
operator|.
name|docFreq
argument_list|()
decl_stmt|;
name|long
name|tf
init|=
name|termsEnum
operator|.
name|totalTermFreq
argument_list|()
decl_stmt|;
if|if
condition|(
name|MIN_DOC_FREQ
operator|>
operator|-
literal|1
operator|&&
name|df
operator|<
name|MIN_DOC_FREQ
condition|)
block|{
name|bytesRef
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|queue
operator|.
name|top
argument_list|()
operator|==
literal|null
operator|||
name|queue
operator|.
name|size
argument_list|()
operator|<
name|TOP_N
operator|||
name|df
operator|>=
name|queue
operator|.
name|top
argument_list|()
operator|.
name|df
condition|)
block|{
name|String
name|t
init|=
name|bytesRef
operator|.
name|utf8ToString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|BLACK_LIST
operator|.
name|contains
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|queue
operator|.
name|insertWithOverflow
argument_list|(
operator|new
name|TokenDFTF
argument_list|(
name|t
argument_list|,
name|df
argument_list|,
name|tf
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|bytesRef
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|luceneDir
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writeTopN
argument_list|(
name|commonTokensFile
argument_list|,
name|totalDocs
argument_list|,
name|sumDocFreqs
argument_list|,
name|sumTotalTermFreqs
argument_list|,
name|uniqueTerms
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BufferedReader
name|getReader
parameter_list|(
name|Path
name|inputFile
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|inputFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputFile
operator|.
name|toString
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".gz"
argument_list|)
condition|)
block|{
name|is
operator|=
operator|new
name|GzipCompressorInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|writeTopN
parameter_list|(
name|Path
name|path
parameter_list|,
name|long
name|totalDocs
parameter_list|,
name|long
name|sumDocFreqs
parameter_list|,
name|long
name|sumTotalTermFreqs
parameter_list|,
name|long
name|uniqueTerms
parameter_list|,
name|AbstractTokenTFDFPriorityQueue
name|queue
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|Files
operator|.
name|isRegularFile
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"File "
operator|+
name|path
operator|.
name|getFileName
argument_list|()
operator|+
literal|" already exists. Skipping."
argument_list|)
expr_stmt|;
return|return;
block|}
name|Files
operator|.
name|createDirectories
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedWriter
name|writer
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|path
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|LICENSE
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"#DOC_COUNT\t"
operator|+
name|totalDocs
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"#SUM_DOC_FREQS\t"
operator|+
name|sumDocFreqs
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"#SUM_TERM_FREQS\t"
operator|+
name|sumTotalTermFreqs
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"#UNIQUE_TERMS\t"
operator|+
name|uniqueTerms
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"#TOKEN\tDOCFREQ\tTERMFREQ\n"
argument_list|)
expr_stmt|;
comment|//add these tokens no matter what
for|for
control|(
name|String
name|t
range|:
name|WHITE_LIST
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newLine
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|TokenDFTF
name|tp
range|:
name|queue
operator|.
name|getArray
argument_list|()
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|getRow
argument_list|(
name|sb
argument_list|,
name|tp
argument_list|)
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|getRow
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|TokenDFTF
name|tp
parameter_list|)
block|{
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|clean
argument_list|(
name|tp
operator|.
name|token
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
operator|.
name|append
argument_list|(
name|tp
operator|.
name|df
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
operator|.
name|append
argument_list|(
name|tp
operator|.
name|tf
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|clean
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
condition|)
block|{
return|return
literal|""
return|;
block|}
return|return
name|s
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
specifier|private
specifier|abstract
class|class
name|AbstractTokenTFDFPriorityQueue
extends|extends
name|PriorityQueue
argument_list|<
name|TokenDFTF
argument_list|>
block|{
name|AbstractTokenTFDFPriorityQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|TokenDFTF
index|[]
name|getArray
parameter_list|()
block|{
name|TokenDFTF
index|[]
name|topN
init|=
operator|new
name|TokenDFTF
index|[
name|size
argument_list|()
index|]
decl_stmt|;
comment|//now we reverse the queue
name|TokenDFTF
name|term
init|=
name|pop
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|topN
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|term
operator|!=
literal|null
operator|&&
name|i
operator|>
operator|-
literal|1
condition|)
block|{
name|topN
index|[
name|i
operator|--
index|]
operator|=
name|term
expr_stmt|;
name|term
operator|=
name|pop
argument_list|()
expr_stmt|;
block|}
return|return
name|topN
return|;
block|}
block|}
specifier|private
class|class
name|TokenDFTF
block|{
specifier|final
name|String
name|token
decl_stmt|;
specifier|final
name|int
name|df
decl_stmt|;
specifier|final
name|long
name|tf
decl_stmt|;
specifier|public
name|TokenDFTF
parameter_list|(
name|String
name|token
parameter_list|,
name|int
name|df
parameter_list|,
name|long
name|tf
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|df
operator|=
name|df
expr_stmt|;
name|this
operator|.
name|tf
operator|=
name|tf
expr_stmt|;
block|}
specifier|public
name|long
name|getTF
parameter_list|()
block|{
return|return
name|tf
return|;
block|}
specifier|public
name|int
name|getDF
parameter_list|()
block|{
return|return
name|df
return|;
block|}
specifier|public
name|String
name|getToken
parameter_list|()
block|{
return|return
name|token
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|TokenDFTF
name|tokenDFTF
init|=
operator|(
name|TokenDFTF
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|df
operator|!=
name|tokenDFTF
operator|.
name|df
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|tf
operator|!=
name|tokenDFTF
operator|.
name|tf
condition|)
return|return
literal|false
return|;
return|return
name|token
operator|!=
literal|null
condition|?
name|token
operator|.
name|equals
argument_list|(
name|tokenDFTF
operator|.
name|token
argument_list|)
else|:
name|tokenDFTF
operator|.
name|token
operator|==
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|token
operator|!=
literal|null
condition|?
name|token
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|df
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|tf
operator|^
operator|(
name|tf
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TokenDFTF{"
operator|+
literal|"token='"
operator|+
name|token
operator|+
literal|'\''
operator|+
literal|", df="
operator|+
name|df
operator|+
literal|", tf="
operator|+
name|tf
operator|+
literal|'}'
return|;
block|}
block|}
specifier|private
class|class
name|TokenDFPriorityQueue
extends|extends
name|AbstractTokenTFDFPriorityQueue
block|{
name|TokenDFPriorityQueue
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|TokenDFTF
name|arg0
parameter_list|,
name|TokenDFTF
name|arg1
parameter_list|)
block|{
if|if
condition|(
name|arg0
operator|.
name|df
operator|<
name|arg1
operator|.
name|df
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|arg0
operator|.
name|df
operator|>
name|arg1
operator|.
name|df
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|arg1
operator|.
name|token
operator|.
name|compareTo
argument_list|(
name|arg0
operator|.
name|token
argument_list|)
operator|<
literal|0
return|;
block|}
specifier|public
name|TokenDFTF
index|[]
name|getArray
parameter_list|()
block|{
name|TokenDFTF
index|[]
name|topN
init|=
operator|new
name|TokenDFTF
index|[
name|size
argument_list|()
index|]
decl_stmt|;
comment|//now we reverse the queue
name|TokenDFTF
name|term
init|=
name|pop
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|topN
operator|.
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|term
operator|!=
literal|null
operator|&&
name|i
operator|>
operator|-
literal|1
condition|)
block|{
name|topN
index|[
name|i
operator|--
index|]
operator|=
name|term
expr_stmt|;
name|term
operator|=
name|pop
argument_list|()
expr_stmt|;
block|}
return|return
name|topN
return|;
block|}
block|}
block|}
end_class

end_unit

