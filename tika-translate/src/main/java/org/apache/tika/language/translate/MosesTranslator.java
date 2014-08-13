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
name|language
operator|.
name|translate
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
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Translator that uses the Moses decoder for translation.  * Users must install the Moses system before using this Translator. @link http://www.statmt.org/moses/.  */
end_comment

begin_class
specifier|public
class|class
name|MosesTranslator
extends|extends
name|ExternalTranslator
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PATH
init|=
literal|"dummy-path"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TMP_FILE_NAME
init|=
literal|"tika.moses.translation.tmp"
decl_stmt|;
specifier|private
name|String
name|smtPath
init|=
name|DEFAULT_PATH
decl_stmt|;
specifier|private
name|String
name|scriptPath
init|=
name|DEFAULT_PATH
decl_stmt|;
comment|/**      * Default constructor that attempts to read the smt jar and script paths from the      * translator.moses.properties file.      *      * @throws java.lang.AssertionError When the properties file is unreadable.      */
specifier|public
name|MosesTranslator
parameter_list|()
block|{
name|Properties
name|config
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|config
operator|.
name|load
argument_list|(
name|MosesTranslator
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"org/apache/tika/language/translate/translator.moses.properties"
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|MosesTranslator
argument_list|(
name|config
operator|.
name|getProperty
argument_list|(
literal|"translator.smt_path"
argument_list|)
argument_list|,
name|config
operator|.
name|getProperty
argument_list|(
literal|"translator.script_path"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Failed to read translator.moses.properties."
argument_list|)
throw|;
block|}
block|}
comment|/**      * Create a Moses Translator with the specified smt jar and script paths.      *      * @param smtPath Full path to the jar to run.      * @param scriptPath Full path to the script to pass to the smt jar.      */
specifier|public
name|MosesTranslator
parameter_list|(
name|String
name|smtPath
parameter_list|,
name|String
name|scriptPath
parameter_list|)
block|{
name|this
operator|.
name|smtPath
operator|=
name|smtPath
expr_stmt|;
name|this
operator|.
name|scriptPath
operator|=
name|scriptPath
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|buildCommand
argument_list|(
name|smtPath
argument_list|,
name|scriptPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|translate
parameter_list|(
name|String
name|text
parameter_list|,
name|String
name|sourceLanguage
parameter_list|,
name|String
name|targetLanguage
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|isAvailable
argument_list|()
operator|||
operator|!
name|checkCommand
argument_list|(
name|buildCheckCommand
argument_list|(
name|smtPath
argument_list|)
argument_list|,
literal|1
argument_list|)
condition|)
return|return
name|text
return|;
name|File
name|tmpFile
init|=
operator|new
name|File
argument_list|(
name|TMP_FILE_NAME
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|out
init|=
operator|new
name|OutputStreamWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|tmpFile
argument_list|)
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|append
argument_list|(
name|text
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|buildCommand
argument_list|(
name|smtPath
argument_list|,
name|scriptPath
argument_list|)
argument_list|,
operator|new
name|String
index|[]
block|{}
argument_list|,
name|buildWorkingDirectory
argument_list|(
name|scriptPath
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|tmpTranslatedFile
init|=
operator|new
name|File
argument_list|(
name|TMP_FILE_NAME
operator|+
literal|".translated"
argument_list|)
decl_stmt|;
name|StringBuilder
name|stringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|BufferedReader
name|reader
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
name|tmpTranslatedFile
argument_list|)
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
name|stringBuilder
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|tmpFile
operator|.
name|delete
argument_list|()
operator|||
operator|!
name|tmpTranslatedFile
operator|.
name|delete
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete temporary files."
argument_list|)
throw|;
block|}
return|return
name|stringBuilder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAvailable
parameter_list|()
block|{
return|return
operator|!
name|smtPath
operator|.
name|equals
argument_list|(
name|DEFAULT_PATH
argument_list|)
operator|&&
operator|!
name|scriptPath
operator|.
name|equals
argument_list|(
name|DEFAULT_PATH
argument_list|)
return|;
block|}
comment|/**      * Build the command String to be executed.      * @param smtPath Full path to the jar to run.      * @param scriptPath Full path to the script to pass to the smt jar.      * @return String to run on the command line.      */
specifier|private
name|String
name|buildCommand
parameter_list|(
name|String
name|smtPath
parameter_list|,
name|String
name|scriptPath
parameter_list|)
block|{
return|return
literal|"java -jar "
operator|+
name|smtPath
operator|+
literal|" -c NONE "
operator|+
name|scriptPath
operator|+
literal|" "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
operator|+
literal|"/"
operator|+
name|TMP_FILE_NAME
return|;
block|}
comment|/**      * Build the command String to check if we can execute the smt jar.      * @param smtPath Full path to the jar to run.      * @return String to run on the command line.      */
specifier|private
name|String
name|buildCheckCommand
parameter_list|(
name|String
name|smtPath
parameter_list|)
block|{
return|return
literal|"java -jar "
operator|+
name|smtPath
return|;
block|}
comment|/**      * Build the File that represents the desired working directory. In this case,      * the directory the script is in.      * @param scriptPath Full path to the script passed to the smt jar.      * @return File of the directory with the script in it.      */
specifier|private
name|File
name|buildWorkingDirectory
parameter_list|(
name|String
name|scriptPath
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|scriptPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|scriptPath
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

