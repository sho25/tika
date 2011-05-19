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
name|sax
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
name|io
operator|.
name|TikaInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Content handler decorator that attempts to prevent denial of service  * attacks against Tika parsers.  *<p>  * Currently this class simply compares the number of output characters  * to to the number of input bytes, and throws an exception if the output  * is truly excessive when compared to the input. This is a strong indication  * of a zip bomb.  *  * @since Apache Tika 0.4  * @see<a href="https://issues.apache.org/jira/browse/TIKA-216">TIKA-216</a>  */
end_comment

begin_class
specifier|public
class|class
name|SecureContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
comment|/**      * The input stream that Tika is parsing.      */
specifier|private
specifier|final
name|TikaInputStream
name|stream
decl_stmt|;
comment|/**      * Number of output characters that Tika has produced so far.      */
specifier|private
name|long
name|characterCount
init|=
literal|0
decl_stmt|;
comment|/**      * Output threshold.      */
specifier|private
name|long
name|threshold
init|=
literal|1000000
decl_stmt|;
comment|/**      * Maximum compression ratio.      */
specifier|private
name|long
name|ratio
init|=
literal|100
decl_stmt|;
comment|/**      * Decorates the given content handler with zip bomb prevention based      * on the count of bytes read from the given counting input stream.      * The resulting decorator can be passed to a Tika parser along with      * the given counting input stream.      *      * @param handler the content handler to be decorated      * @param stream the input stream to be parsed      */
specifier|public
name|SecureContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|TikaInputStream
name|stream
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
comment|/**      * Returns the configured output threshold.      *      * @return output threshold      */
specifier|public
name|long
name|getOutputThreshold
parameter_list|()
block|{
return|return
name|threshold
return|;
block|}
comment|/**      * Sets the threshold for output characters before the zip bomb prevention      * is activated. This avoids false positives in cases where an otherwise      * normal document for some reason starts with a highly compressible      * sequence of bytes.      *      * @param threshold new output threshold      */
specifier|public
name|void
name|setOutputThreshold
parameter_list|(
name|long
name|threshold
parameter_list|)
block|{
name|this
operator|.
name|threshold
operator|=
name|threshold
expr_stmt|;
block|}
comment|/**      * Returns the maximum compression ratio.      *      * @return maximum compression ratio      */
specifier|public
name|long
name|getMaximumCompressionRatio
parameter_list|()
block|{
return|return
name|ratio
return|;
block|}
comment|/**      * Sets the ratio between output characters and input bytes. If this      * ratio is exceeded (after the output threshold has been reached) then      * an exception gets thrown.      *      * @param ratio new maximum compression ratio      */
specifier|public
name|void
name|setMaximumCompressionRatio
parameter_list|(
name|long
name|ratio
parameter_list|)
block|{
name|this
operator|.
name|ratio
operator|=
name|ratio
expr_stmt|;
block|}
comment|/**      * Converts the given {@link SAXException} to a corresponding      * {@link TikaException} if it's caused by this instance detecting      * a zip bomb.      *      * @param e SAX exception      * @throws TikaException zip bomb exception      */
specifier|public
name|void
name|throwIfCauseOf
parameter_list|(
name|SAXException
name|e
parameter_list|)
throws|throws
name|TikaException
block|{
if|if
condition|(
name|e
operator|instanceof
name|SecureSAXException
operator|&&
operator|(
operator|(
name|SecureSAXException
operator|)
name|e
operator|)
operator|.
name|isCausedBy
argument_list|(
name|this
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Zip bomb detected!"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|long
name|getByteCount
parameter_list|()
throws|throws
name|SAXException
block|{
try|try
block|{
if|if
condition|(
name|stream
operator|.
name|hasLength
argument_list|()
condition|)
block|{
return|return
name|stream
operator|.
name|getLength
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|stream
operator|.
name|getPosition
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Unable to get stream length"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Records the given number of output characters (or more accurately      * UTF-16 code units). Throws an exception if the recorded number of      * characters highly exceeds the number of input bytes read.      *      * @param length number of new output characters produced      * @throws SAXException if a zip bomb is detected      */
specifier|private
name|void
name|advance
parameter_list|(
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|characterCount
operator|+=
name|length
expr_stmt|;
name|long
name|byteCount
init|=
name|getByteCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|characterCount
operator|>
name|threshold
operator|&&
name|characterCount
operator|>
name|byteCount
operator|*
name|ratio
condition|)
block|{
throw|throw
operator|new
name|SecureSAXException
argument_list|(
name|byteCount
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|advance
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|super
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|advance
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|super
operator|.
name|ignorableWhitespace
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * Private exception class used to indicate a suspected zip bomb.      *      * @see SecureContentHandler#throwIfCauseOf(SAXException)      */
specifier|private
class|class
name|SecureSAXException
extends|extends
name|SAXException
block|{
specifier|public
name|SecureSAXException
parameter_list|(
name|long
name|byteCount
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
argument_list|(
literal|"Suspected zip bomb: "
operator|+
name|byteCount
operator|+
literal|" input bytes produced "
operator|+
name|characterCount
operator|+
literal|" output characters"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCausedBy
parameter_list|(
name|SecureContentHandler
name|handler
parameter_list|)
block|{
return|return
name|SecureContentHandler
operator|.
name|this
operator|==
name|handler
return|;
block|}
block|}
block|}
end_class

end_unit

