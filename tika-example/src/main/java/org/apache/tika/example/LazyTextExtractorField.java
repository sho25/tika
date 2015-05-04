begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|example
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
name|java
operator|.
name|io
operator|.
name|Reader
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
name|Executor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|query
operator|.
name|lucene
operator|.
name|FieldNames
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|core
operator|.
name|value
operator|.
name|InternalValue
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
name|TokenStream
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
name|AbstractField
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
name|Field
operator|.
name|Store
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
operator|.
name|TermVector
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
name|metadata
operator|.
name|Metadata
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
name|ParseContext
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
name|Parser
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  *<code>LazyTextExtractorField</code> implements a Lucene field with a String  * value that is lazily initialized from a given {@link Reader}. In addition  * this class provides a method to find out whether the purpose of the reader is  * to extract text and whether the extraction process is already finished.  *  * @see #isExtractorFinished()  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
specifier|public
class|class
name|LazyTextExtractorField
extends|extends
name|AbstractField
block|{
comment|/** 	 * The logger instance for this class. 	 */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LazyTextExtractorField
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** 	 * The exception used to forcibly terminate the extraction process when the 	 * maximum field length is reached. 	 */
specifier|private
specifier|static
specifier|final
name|SAXException
name|STOP
init|=
operator|new
name|SAXException
argument_list|(
literal|"max field length reached"
argument_list|)
decl_stmt|;
comment|/** 	 * The extracted text content of the given binary value. Set to non-null 	 * when the text extraction task finishes. 	 */
specifier|private
specifier|volatile
name|String
name|extract
init|=
literal|null
decl_stmt|;
comment|/** 	 * Creates a new<code>LazyTextExtractorField</code> with the given 	 *<code>name</code>. 	 * 	 * @param name 	 *            the name of the field. 	 * @param reader 	 *            the reader where to obtain the string from. 	 * @param highlighting 	 *            set to<code>true</code> to enable result highlighting support 	 */
specifier|public
name|LazyTextExtractorField
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|InternalValue
name|value
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|Executor
name|executor
parameter_list|,
name|boolean
name|highlighting
parameter_list|,
name|int
name|maxFieldLength
parameter_list|)
block|{
name|super
argument_list|(
name|FieldNames
operator|.
name|FULLTEXT
argument_list|,
name|highlighting
condition|?
name|Store
operator|.
name|YES
else|:
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|highlighting
condition|?
name|TermVector
operator|.
name|WITH_OFFSETS
else|:
name|TermVector
operator|.
name|NO
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|ParsingTask
argument_list|(
name|parser
argument_list|,
name|value
argument_list|,
name|metadata
argument_list|,
name|maxFieldLength
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Returns the extracted text. This method blocks until the text extraction 	 * task has been completed. 	 * 	 * @return the string value of this field 	 */
specifier|public
specifier|synchronized
name|String
name|stringValue
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
operator|!
name|isExtractorFinished
argument_list|()
condition|)
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
return|return
name|extract
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Text extraction thread was interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|""
return|;
block|}
block|}
comment|/** 	 * @return always<code>null</code> 	 */
specifier|public
name|Reader
name|readerValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 * @return always<code>null</code> 	 */
specifier|public
name|byte
index|[]
name|binaryValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 * @return always<code>null</code> 	 */
specifier|public
name|TokenStream
name|tokenStreamValue
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 * Checks whether the text extraction task has finished. 	 * 	 * @return<code>true</code> if the extracted text is available 	 */
specifier|public
name|boolean
name|isExtractorFinished
parameter_list|()
block|{
return|return
name|extract
operator|!=
literal|null
return|;
block|}
specifier|private
specifier|synchronized
name|void
name|setExtractedText
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|extract
operator|=
name|value
expr_stmt|;
name|notify
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Releases all resources associated with this field. 	 */
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// TODO: Cause the ContentHandler below to throw an exception
block|}
comment|/** 	 * The background task for extracting text from a binary value. 	 */
specifier|private
class|class
name|ParsingTask
extends|extends
name|DefaultHandler
implements|implements
name|Runnable
block|{
specifier|private
specifier|final
name|Parser
name|parser
decl_stmt|;
specifier|private
specifier|final
name|InternalValue
name|value
decl_stmt|;
specifier|private
specifier|final
name|Metadata
name|metadata
decl_stmt|;
specifier|private
specifier|final
name|int
name|maxFieldLength
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ParseContext
name|context
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
comment|// NOTE: not a part of Jackrabbit code, made
specifier|private
specifier|final
name|ContentHandler
name|handler
init|=
operator|new
name|DefaultHandler
argument_list|()
decl_stmt|;
specifier|public
name|ParsingTask
parameter_list|(
name|Parser
name|parser
parameter_list|,
name|InternalValue
name|value
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|int
name|maxFieldLength
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
name|this
operator|.
name|maxFieldLength
operator|=
name|maxFieldLength
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|InputStream
name|stream
init|=
name|value
operator|.
name|getStream
argument_list|()
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|LinkageError
name|e
parameter_list|)
block|{
comment|// Capture and ignore
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|!=
name|STOP
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Failed to extract text."
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|setExtractedText
argument_list|(
literal|"TextExtractionError"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
finally|finally
block|{
name|value
operator|.
name|discard
argument_list|()
expr_stmt|;
block|}
name|setExtractedText
argument_list|(
name|handler
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|length
argument_list|,
name|maxFieldLength
operator|-
name|builder
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|builder
operator|.
name|length
argument_list|()
operator|>=
name|maxFieldLength
condition|)
block|{
throw|throw
name|STOP
throw|;
block|}
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
block|}
block|}
end_class

end_unit

