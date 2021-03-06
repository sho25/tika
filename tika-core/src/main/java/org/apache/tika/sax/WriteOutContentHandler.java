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
name|OutputStream
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|UUID
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
comment|/**  * SAX event handler that writes content up to an optional write  * limit out to a character stream or other decorated handler.  */
end_comment

begin_class
specifier|public
class|class
name|WriteOutContentHandler
extends|extends
name|ContentHandlerDecorator
block|{
comment|/**      * The unique tag associated with exceptions from stream.      */
specifier|private
specifier|final
name|Serializable
name|tag
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
comment|/**      * The maximum number of characters to write to the character stream.      * Set to -1 for no limit.      */
specifier|private
specifier|final
name|int
name|writeLimit
decl_stmt|;
comment|/**      * Number of characters written so far.      */
specifier|private
name|int
name|writeCount
init|=
literal|0
decl_stmt|;
comment|/**      * Creates a content handler that writes content up to the given      * write limit to the given content handler.      *      * @since Apache Tika 0.10      * @param handler content handler to be decorated      * @param writeLimit write limit      */
specifier|public
name|WriteOutContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|int
name|writeLimit
parameter_list|)
block|{
name|super
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeLimit
operator|=
name|writeLimit
expr_stmt|;
block|}
comment|/**      * Creates a content handler that writes content up to the given      * write limit to the given character stream.      *      * @since Apache Tika 0.10      * @param writer character stream      * @param writeLimit write limit      */
specifier|public
name|WriteOutContentHandler
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|int
name|writeLimit
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|ToTextContentHandler
argument_list|(
name|writer
argument_list|)
argument_list|,
name|writeLimit
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a content handler that writes character events to      * the given writer.      *      * @param writer writer      */
specifier|public
name|WriteOutContentHandler
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|this
argument_list|(
name|writer
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a content handler that writes character events to      * the given output stream using the default encoding.      *      * @param stream output stream      */
specifier|public
name|WriteOutContentHandler
parameter_list|(
name|OutputStream
name|stream
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|stream
argument_list|,
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a content handler that writes character events      * to an internal string buffer. Use the {@link #toString()}      * method to access the collected character content.      *<p>      * The internal string buffer is bounded at the given number of characters.      * If this write limit is reached, then a {@link SAXException} is thrown.      * The {@link #isWriteLimitReached(Throwable)} method can be used to      * detect this case.      *      * @since Apache Tika 0.7      * @param writeLimit maximum number of characters to include in the string,      *                   or -1 to disable the write limit      */
specifier|public
name|WriteOutContentHandler
parameter_list|(
name|int
name|writeLimit
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|StringWriter
argument_list|()
argument_list|,
name|writeLimit
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a content handler that writes character events      * to an internal string buffer. Use the {@link #toString()}      * method to access the collected character content.      *<p>      * The internal string buffer is bounded at 100k characters. If this      * write limit is reached, then a {@link SAXException} is thrown. The      * {@link #isWriteLimitReached(Throwable)} method can be used to detect      * this case.      */
specifier|public
name|WriteOutContentHandler
parameter_list|()
block|{
name|this
argument_list|(
literal|100
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes the given characters to the given character stream.      */
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
if|if
condition|(
name|writeLimit
operator|==
operator|-
literal|1
operator|||
name|writeCount
operator|+
name|length
operator|<=
name|writeLimit
condition|)
block|{
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
name|writeCount
operator|+=
name|length
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|writeLimit
operator|-
name|writeCount
argument_list|)
expr_stmt|;
name|writeCount
operator|=
name|writeLimit
expr_stmt|;
throw|throw
operator|new
name|WriteLimitReachedException
argument_list|(
literal|"Your document contained more than "
operator|+
name|writeLimit
operator|+
literal|" characters, and so your requested limit has been"
operator|+
literal|" reached. To receive the full text of the document,"
operator|+
literal|" increase your limit. (Text up to the limit is"
operator|+
literal|" however available)."
argument_list|,
name|tag
argument_list|)
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
if|if
condition|(
name|writeLimit
operator|==
operator|-
literal|1
operator|||
name|writeCount
operator|+
name|length
operator|<=
name|writeLimit
condition|)
block|{
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
name|writeCount
operator|+=
name|length
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|ignorableWhitespace
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|writeLimit
operator|-
name|writeCount
argument_list|)
expr_stmt|;
name|writeCount
operator|=
name|writeLimit
expr_stmt|;
throw|throw
operator|new
name|WriteLimitReachedException
argument_list|(
literal|"Your document contained more than "
operator|+
name|writeLimit
operator|+
literal|" characters, and so your requested limit has been"
operator|+
literal|" reached. To receive the full text of the document,"
operator|+
literal|" increase your limit. (Text up to the limit is"
operator|+
literal|" however available)."
argument_list|,
name|tag
argument_list|)
throw|;
block|}
block|}
comment|/**      * Checks whether the given exception (or any of it's root causes) was      * thrown by this handler as a signal of reaching the write limit.      *      * @since Apache Tika 0.7      * @param t throwable      * @return<code>true</code> if the write limit was reached,      *<code>false</code> otherwise      */
specifier|public
name|boolean
name|isWriteLimitReached
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|instanceof
name|WriteLimitReachedException
condition|)
block|{
return|return
name|tag
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|WriteLimitReachedException
operator|)
name|t
operator|)
operator|.
name|tag
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|t
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
operator|&&
name|isWriteLimitReached
argument_list|(
name|t
operator|.
name|getCause
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * The exception used as a signal when the write limit has been reached.      */
specifier|private
specifier|static
class|class
name|WriteLimitReachedException
extends|extends
name|SAXException
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|1850581945459429943L
decl_stmt|;
comment|/** Serializable tag of the handler that caused this exception */
specifier|private
specifier|final
name|Serializable
name|tag
decl_stmt|;
specifier|public
name|WriteLimitReachedException
parameter_list|(
name|String
name|message
parameter_list|,
name|Serializable
name|tag
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

