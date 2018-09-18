begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   *     http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|strings
package|;
end_package

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
name|Serializable
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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Configuration for the "strings" (or strings-alternative) command.  *  */
end_comment

begin_class
specifier|public
class|class
name|StringsConfig
implements|implements
name|Serializable
block|{
comment|/** 	 * Serial version UID 	 */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|1465227101645003594L
decl_stmt|;
specifier|private
name|String
name|stringsPath
init|=
literal|""
decl_stmt|;
comment|// Minimum sequence length (characters) to print
specifier|private
name|int
name|minLength
init|=
literal|4
decl_stmt|;
comment|// Character encoding of the strings that are to be found
specifier|private
name|StringsEncoding
name|encoding
init|=
name|StringsEncoding
operator|.
name|SINGLE_7_BIT
decl_stmt|;
comment|// Maximum time (seconds) to wait for the strings process termination
specifier|private
name|int
name|timeout
init|=
literal|120
decl_stmt|;
comment|/** 	 * Default contructor. 	 */
specifier|public
name|StringsConfig
parameter_list|()
block|{
name|init
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"Strings.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Loads properties from InputStream and then tries to close InputStream. If 	 * there is an IOException, this silently swallows the exception and goes 	 * back to the default. 	 * 	 * @param is 	 */
specifier|public
name|StringsConfig
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|init
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Initializes attributes. 	 * 	 * @param is 	 */
specifier|private
name|void
name|init
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// swallow
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// swallow
block|}
block|}
block|}
name|setStringsPath
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"stringsPath"
argument_list|,
literal|""
operator|+
name|getStringsPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setMinLength
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"minLength"
argument_list|,
literal|""
operator|+
name|getMinLength
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|setEncoding
argument_list|(
name|StringsEncoding
operator|.
name|valueOf
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"encoding"
argument_list|,
literal|""
operator|+
name|getEncoding
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|setTimeout
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"timeout"
argument_list|,
literal|""
operator|+
name|getTimeout
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Returns the "strings" installation folder. 	 *  	 * @return the "strings" installation folder. 	 */
specifier|public
name|String
name|getStringsPath
parameter_list|()
block|{
return|return
name|this
operator|.
name|stringsPath
return|;
block|}
comment|/** 	 * Returns the minimum sequence length (characters) to print. 	 *  	 * @return the minimum sequence length (characters) to print. 	 */
specifier|public
name|int
name|getMinLength
parameter_list|()
block|{
return|return
name|this
operator|.
name|minLength
return|;
block|}
comment|/** 	 * Returns the character encoding of the strings that are to be found. 	 *  	 * @return {@link StringsEncoding} enum that represents the character 	 *         encoding of the strings that are to be found. 	 */
specifier|public
name|StringsEncoding
name|getEncoding
parameter_list|()
block|{
return|return
name|this
operator|.
name|encoding
return|;
block|}
comment|/** 	 * Returns the maximum time (in seconds) to wait for the "strings" command 	 * to terminate. 	 *  	 * @return the maximum time (in seconds) to wait for the "strings" command 	 *         to terminate. 	 */
specifier|public
name|int
name|getTimeout
parameter_list|()
block|{
return|return
name|this
operator|.
name|timeout
return|;
block|}
comment|/** 	 * Sets the "strings" installation folder. 	 *  	 * @param path 	 *            the "strings" installation folder. 	 */
specifier|public
name|void
name|setStringsPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
operator|!
name|path
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|path
operator|.
name|endsWith
argument_list|(
name|File
operator|.
name|separator
argument_list|)
condition|)
block|{
name|path
operator|+=
name|File
operator|.
name|separatorChar
expr_stmt|;
block|}
name|this
operator|.
name|stringsPath
operator|=
name|path
expr_stmt|;
block|}
comment|/** 	 * Sets the minimum sequence length (characters) to print. 	 *  	 * @param minLength 	 *            the minimum sequence length (characters) to print. 	 */
specifier|public
name|void
name|setMinLength
parameter_list|(
name|int
name|minLength
parameter_list|)
block|{
if|if
condition|(
name|minLength
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid minimum length"
argument_list|)
throw|;
block|}
name|this
operator|.
name|minLength
operator|=
name|minLength
expr_stmt|;
block|}
comment|/** 	 * Sets the character encoding of the strings that are to be found. 	 *  	 * @param encoding 	 *            {@link StringsEncoding} enum that represents the character 	 *            encoding of the strings that are to be found. 	 */
specifier|public
name|void
name|setEncoding
parameter_list|(
name|StringsEncoding
name|encoding
parameter_list|)
block|{
name|this
operator|.
name|encoding
operator|=
name|encoding
expr_stmt|;
block|}
comment|/** 	 * Sets the maximum time (in seconds) to wait for the "strings" command to 	 * terminate. 	 *  	 * @param timeout 	 *            the maximum time (in seconds) to wait for the "strings" 	 *            command to terminate. 	 */
specifier|public
name|void
name|setTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
if|if
condition|(
name|timeout
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid timeout"
argument_list|)
throw|;
block|}
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
block|}
end_class

end_unit

