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
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_comment
comment|/**  * Identify the language of a content, based on statistical analysis.  *  * @see<a href="http://www.w3.org/WAI/ER/IG/ert/iso639.htm">ISO 639  *      Language Codes</a>  *   * @author Sami Siren  * @author J&eacute;r&ocirc;me Charron  */
end_comment

begin_class
specifier|public
class|class
name|LanguageIdentifier
block|{
specifier|private
specifier|final
specifier|static
name|int
name|DEFAULT_ANALYSIS_LENGTH
init|=
literal|0
decl_stmt|;
comment|// 0 means full content
specifier|private
name|ArrayList
argument_list|<
name|NGramProfile
argument_list|>
name|languages
init|=
operator|new
name|ArrayList
argument_list|<
name|NGramProfile
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|supportedLanguages
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/** Minimum size of NGrams */
specifier|private
name|int
name|minLength
init|=
name|NGramProfile
operator|.
name|DEFAULT_MIN_NGRAM_LENGTH
decl_stmt|;
comment|/** Maximum size of NGrams */
specifier|private
name|int
name|maxLength
init|=
name|NGramProfile
operator|.
name|DEFAULT_MAX_NGRAM_LENGTH
decl_stmt|;
comment|/** The maximum amount of data to analyze */
specifier|private
name|int
name|analyzeLength
init|=
name|DEFAULT_ANALYSIS_LENGTH
decl_stmt|;
comment|/** A global index of ngrams of all supported languages */
specifier|private
name|HashMap
argument_list|<
name|CharSequence
argument_list|,
name|NGramEntry
index|[]
argument_list|>
name|ngramsIdx
init|=
operator|new
name|HashMap
argument_list|<
name|CharSequence
argument_list|,
name|NGramEntry
index|[]
argument_list|>
argument_list|()
decl_stmt|;
comment|/** The NGramProfile used for identification */
specifier|private
name|NGramProfile
name|suspect
init|=
literal|null
decl_stmt|;
comment|/**      * Constructs a new Language Identifier.      */
specifier|public
name|LanguageIdentifier
parameter_list|()
block|{
comment|// Gets ngram sizes to take into account from the Nutch Config
name|minLength
operator|=
name|NGramProfile
operator|.
name|DEFAULT_MIN_NGRAM_LENGTH
expr_stmt|;
name|maxLength
operator|=
name|NGramProfile
operator|.
name|DEFAULT_MAX_NGRAM_LENGTH
expr_stmt|;
comment|// Ensure the min and max values are in an acceptale range
comment|// (ie min>= DEFAULT_MIN_NGRAM_LENGTH and max<= DEFAULT_MAX_NGRAM_LENGTH)
name|maxLength
operator|=
name|Math
operator|.
name|min
argument_list|(
name|maxLength
argument_list|,
name|NGramProfile
operator|.
name|ABSOLUTE_MAX_NGRAM_LENGTH
argument_list|)
expr_stmt|;
name|maxLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxLength
argument_list|,
name|NGramProfile
operator|.
name|ABSOLUTE_MIN_NGRAM_LENGTH
argument_list|)
expr_stmt|;
name|minLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|minLength
argument_list|,
name|NGramProfile
operator|.
name|ABSOLUTE_MIN_NGRAM_LENGTH
argument_list|)
expr_stmt|;
name|minLength
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minLength
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
comment|// Gets the value of the maximum size of data to analyze
name|analyzeLength
operator|=
name|DEFAULT_ANALYSIS_LENGTH
expr_stmt|;
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|p
operator|.
name|load
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"langmappings.properties"
argument_list|)
argument_list|)
expr_stmt|;
name|Enumeration
name|alllanguages
init|=
name|p
operator|.
name|keys
argument_list|()
decl_stmt|;
name|StringBuffer
name|list
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"Language identifier plugin supports:"
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|NGramEntry
argument_list|,
name|List
argument_list|<
name|NGramEntry
argument_list|>
argument_list|>
name|tmpIdx
init|=
operator|new
name|HashMap
argument_list|<
name|NGramEntry
argument_list|,
name|List
argument_list|<
name|NGramEntry
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|alllanguages
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|lang
init|=
call|(
name|String
call|)
argument_list|(
name|alllanguages
operator|.
name|nextElement
argument_list|()
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"org/apache/tika/language/"
operator|+
name|lang
operator|+
literal|"."
operator|+
name|NGramProfile
operator|.
name|FILE_EXTENSION
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|NGramProfile
name|profile
init|=
operator|new
name|NGramProfile
argument_list|(
name|lang
argument_list|,
name|minLength
argument_list|,
name|maxLength
argument_list|)
decl_stmt|;
try|try
block|{
name|profile
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|languages
operator|.
name|add
argument_list|(
name|profile
argument_list|)
expr_stmt|;
name|supportedLanguages
operator|.
name|add
argument_list|(
name|lang
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|NGramEntry
argument_list|>
name|ngrams
init|=
name|profile
operator|.
name|getSorted
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ngrams
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|NGramEntry
name|entry
init|=
name|ngrams
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|NGramEntry
argument_list|>
name|registered
init|=
name|tmpIdx
operator|.
name|get
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|registered
operator|==
literal|null
condition|)
block|{
name|registered
operator|=
operator|new
name|ArrayList
argument_list|<
name|NGramEntry
argument_list|>
argument_list|()
expr_stmt|;
name|tmpIdx
operator|.
name|put
argument_list|(
name|entry
argument_list|,
name|registered
argument_list|)
expr_stmt|;
block|}
name|registered
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|lang
operator|+
literal|"("
operator|+
name|ngrams
operator|.
name|size
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
comment|// if (LOG.isFatalEnabled()) { LOG.fatal(e1.toString()); }
block|}
block|}
block|}
comment|// transform all ngrams lists to arrays for performances
name|Iterator
argument_list|<
name|NGramEntry
argument_list|>
name|keys
init|=
name|tmpIdx
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|NGramEntry
name|entry
init|=
name|keys
operator|.
name|next
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|NGramEntry
argument_list|>
name|l
init|=
name|tmpIdx
operator|.
name|get
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|!=
literal|null
condition|)
block|{
name|NGramEntry
index|[]
name|array
init|=
name|l
operator|.
name|toArray
argument_list|(
operator|new
name|NGramEntry
index|[
name|l
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|ngramsIdx
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getSeq
argument_list|()
argument_list|,
name|array
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Create the suspect profile
name|suspect
operator|=
operator|new
name|NGramProfile
argument_list|(
literal|"suspect"
argument_list|,
name|minLength
argument_list|,
name|maxLength
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// if (LOG.isFatalEnabled()) { LOG.fatal(e.toString()); }
block|}
block|}
comment|/**      * Main method used for command line process.      *<br/>Usage is:      *<pre>      * LanguageIdentifier [-identifyrows filename maxlines]      *                    [-identifyfile charset filename]      *                    [-identifyfileset charset files]      *                    [-identifytext text]      *                    [-identifyurl url]      *</pre>      * @param args arguments.      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|String
name|usage
init|=
literal|"Usage: LanguageIdentifier"
operator|+
literal|" [-identifyrows filename maxlines]"
operator|+
literal|" [-identifyfile charset filename]"
operator|+
literal|" [-identifyfileset charset files]"
operator|+
literal|" [-identifytext text] "
decl_stmt|;
name|int
name|command
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|IDFILE
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|IDTEXT
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|IDFILESET
init|=
literal|4
decl_stmt|;
specifier|final
name|int
name|IDROWS
init|=
literal|5
decl_stmt|;
name|Vector
argument_list|<
name|String
argument_list|>
name|fileset
init|=
operator|new
name|Vector
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
name|filename
init|=
literal|""
decl_stmt|;
name|String
name|charset
init|=
literal|""
decl_stmt|;
name|String
name|text
init|=
literal|""
decl_stmt|;
name|int
name|max
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|usage
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
for|for
control|(
name|int
name|i
init|=
literal|0
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
comment|// parse command line
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-identifyfile"
argument_list|)
condition|)
block|{
name|command
operator|=
name|IDFILE
expr_stmt|;
name|charset
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
name|filename
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
block|}
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-identifyrows"
argument_list|)
condition|)
block|{
name|command
operator|=
name|IDROWS
expr_stmt|;
name|filename
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
name|max
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
operator|++
name|i
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-identifytext"
argument_list|)
condition|)
block|{
name|command
operator|=
name|IDTEXT
expr_stmt|;
for|for
control|(
name|i
operator|++
init|;
name|i
operator|<
name|args
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
name|text
operator|+=
name|args
index|[
name|i
index|]
operator|+
literal|" "
expr_stmt|;
block|}
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-identifyfileset"
argument_list|)
condition|)
block|{
name|command
operator|=
name|IDFILESET
expr_stmt|;
name|charset
operator|=
name|args
index|[
operator|++
name|i
index|]
expr_stmt|;
for|for
control|(
name|i
operator|++
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
name|File
index|[]
name|files
init|=
literal|null
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|files
operator|=
name|f
operator|.
name|listFiles
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|files
operator|=
operator|new
name|File
index|[]
block|{
name|f
block|}
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|fileset
operator|.
name|add
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|String
name|lang
init|=
literal|null
decl_stmt|;
comment|//LanguageIdentifier idfr = LanguageIdentifier.getInstance();
name|LanguageIdentifier
name|idfr
init|=
operator|new
name|LanguageIdentifier
argument_list|()
decl_stmt|;
name|File
name|f
decl_stmt|;
name|FileInputStream
name|fis
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|command
condition|)
block|{
case|case
name|IDTEXT
case|:
name|lang
operator|=
name|idfr
operator|.
name|identify
argument_list|(
name|text
argument_list|)
expr_stmt|;
break|break;
case|case
name|IDFILE
case|:
name|f
operator|=
operator|new
name|File
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|lang
operator|=
name|idfr
operator|.
name|identify
argument_list|(
name|fis
argument_list|,
name|charset
argument_list|)
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
break|break;
case|case
name|IDROWS
case|:
name|f
operator|=
operator|new
name|File
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
name|max
operator|>
literal|0
operator|&&
operator|(
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|line
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|>
literal|2
condition|)
block|{
name|max
operator|--
expr_stmt|;
name|lang
operator|=
name|idfr
operator|.
name|identify
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"R="
operator|+
name|lang
operator|+
literal|":"
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
block|}
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
case|case
name|IDFILESET
case|:
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"FILESET"
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|fileset
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|filename
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|f
operator|=
operator|new
name|File
argument_list|(
name|filename
argument_list|)
expr_stmt|;
name|fis
operator|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|lang
operator|=
name|idfr
operator|.
name|identify
argument_list|(
name|fis
argument_list|,
name|charset
argument_list|)
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|filename
operator|+
literal|" was identified as "
operator|+
name|lang
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"text was identified as "
operator|+
name|lang
argument_list|)
expr_stmt|;
block|}
comment|/**      * Identify language of a content.      *       * @param content is the content to analyze.      * @return The 2 letter      *<a href="http://www.w3.org/WAI/ER/IG/ert/iso639.htm">ISO 639      *         language code</a> (en, fi, sv, ...) of the language that best      *         matches the specified content.      */
specifier|public
name|String
name|identify
parameter_list|(
name|String
name|content
parameter_list|)
block|{
return|return
name|identify
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|content
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Identify language of a content.      *       * @param content is the content to analyze.      * @return The 2 letter      *<a href="http://www.w3.org/WAI/ER/IG/ert/iso639.htm">ISO 639      *         language code</a> (en, fi, sv, ...) of the language that best      *         matches the specified content.      */
specifier|public
name|String
name|identify
parameter_list|(
name|StringBuilder
name|content
parameter_list|)
block|{
name|StringBuilder
name|text
init|=
name|content
decl_stmt|;
if|if
condition|(
operator|(
name|analyzeLength
operator|>
literal|0
operator|)
operator|&&
operator|(
name|content
operator|.
name|length
argument_list|()
operator|>
name|analyzeLength
operator|)
condition|)
block|{
name|text
operator|=
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|text
operator|.
name|setLength
argument_list|(
name|analyzeLength
argument_list|)
expr_stmt|;
block|}
name|suspect
operator|.
name|analyze
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|NGramEntry
argument_list|>
name|iter
init|=
name|suspect
operator|.
name|getSorted
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|float
name|topscore
init|=
name|Float
operator|.
name|MIN_VALUE
decl_stmt|;
name|String
name|lang
init|=
literal|""
decl_stmt|;
name|HashMap
argument_list|<
name|NGramProfile
argument_list|,
name|Float
argument_list|>
name|scores
init|=
operator|new
name|HashMap
argument_list|<
name|NGramProfile
argument_list|,
name|Float
argument_list|>
argument_list|()
decl_stmt|;
name|NGramEntry
name|searched
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|searched
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|NGramEntry
index|[]
name|ngrams
init|=
name|ngramsIdx
operator|.
name|get
argument_list|(
name|searched
operator|.
name|getSeq
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ngrams
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|ngrams
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|NGramProfile
name|profile
init|=
name|ngrams
index|[
name|j
index|]
operator|.
name|getProfile
argument_list|()
decl_stmt|;
name|Float
name|pScore
init|=
name|scores
operator|.
name|get
argument_list|(
name|profile
argument_list|)
decl_stmt|;
if|if
condition|(
name|pScore
operator|==
literal|null
condition|)
block|{
name|pScore
operator|=
operator|new
name|Float
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|float
name|plScore
init|=
name|pScore
operator|.
name|floatValue
argument_list|()
decl_stmt|;
name|plScore
operator|+=
name|ngrams
index|[
name|j
index|]
operator|.
name|getFrequency
argument_list|()
operator|+
name|searched
operator|.
name|getFrequency
argument_list|()
expr_stmt|;
name|scores
operator|.
name|put
argument_list|(
name|profile
argument_list|,
operator|new
name|Float
argument_list|(
name|plScore
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|plScore
operator|>
name|topscore
condition|)
block|{
name|topscore
operator|=
name|plScore
expr_stmt|;
name|lang
operator|=
name|profile
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|lang
return|;
block|}
comment|/**      * Identify language from input stream.      * This method uses the platform default encoding to read the input stream.      * For using a specific encoding, use the      * {@link #identify(InputStream, String)} method.      *      * @param is is the input stream to analyze.      * @return The 2 letter      *<a href="http://www.w3.org/WAI/ER/IG/ert/iso639.htm">ISO 639      *         language code</a> (en, fi, sv, ...) of the language that best      *         matches the content of the specified input stream.      * @throws IOException if something wrong occurs on the input stream.      */
specifier|public
name|String
name|identify
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|identify
argument_list|(
name|is
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Identify language from input stream.      *       * @param is is the input stream to analyze.      * @param charset is the charset to use to read the input stream.      * @return The 2 letter      *<a href="http://www.w3.org/WAI/ER/IG/ert/iso639.htm">ISO 639      *         language code</a> (en, fi, sv, ...) of the language that best      *         matches the content of the specified input stream.      * @throws IOException if something wrong occurs on the input stream.      */
specifier|public
name|String
name|identify
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|String
name|charset
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|2048
index|]
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
operator|(
name|len
operator|=
name|is
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
operator|)
operator|&&
operator|(
operator|(
name|analyzeLength
operator|==
literal|0
operator|)
operator|||
operator|(
name|out
operator|.
name|size
argument_list|()
operator|<
name|analyzeLength
operator|)
operator|)
condition|)
block|{
if|if
condition|(
name|analyzeLength
operator|!=
literal|0
condition|)
block|{
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|analyzeLength
operator|-
name|out
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
return|return
name|identify
argument_list|(
operator|(
name|charset
operator|==
literal|null
operator|)
condition|?
name|out
operator|.
name|toString
argument_list|()
else|:
name|out
operator|.
name|toString
argument_list|(
name|charset
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

