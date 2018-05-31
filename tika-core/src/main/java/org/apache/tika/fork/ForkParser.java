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
name|fork
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Queue
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
name|mime
operator|.
name|MediaType
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
name|AbstractParser
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
name|AutoDetectParser
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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|AbstractRecursiveParserWrapperHandler
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
name|sax
operator|.
name|TeeContentHandler
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

begin_class
specifier|public
class|class
name|ForkParser
extends|extends
name|AbstractParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4962742892274663950L
decl_stmt|;
comment|//these are used by the legacy usage
specifier|private
specifier|final
name|ClassLoader
name|loader
decl_stmt|;
specifier|private
specifier|final
name|Parser
name|parser
decl_stmt|;
comment|//these are used when the server builds a parser via a directory
comment|//of jars, not via legacy bootstrap etc.
specifier|private
specifier|final
name|Path
name|tikaBin
decl_stmt|;
specifier|private
specifier|final
name|ParserFactoryFactory
name|parserFactoryFactory
decl_stmt|;
comment|/** Java command line */
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|java
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"java"
argument_list|,
literal|"-Xmx32m"
argument_list|)
decl_stmt|;
comment|/** Process pool size */
specifier|private
name|int
name|poolSize
init|=
literal|5
decl_stmt|;
specifier|private
name|int
name|currentlyInUse
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|Queue
argument_list|<
name|ForkClient
argument_list|>
name|pool
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|long
name|serverPulseMillis
init|=
literal|5000
decl_stmt|;
comment|/**      * If you have a directory with, say, tike-app.jar and you want the child process/server to build a parser      * and run it from that -- so that you can keep all of those dependencies out of your client code, use      * this initializer.      *      * @param tikaBin directory containing the tika-app.jar or similar -- full jar including tika-core and all      *                desired parsers and dependencies      * @param factoryFactory      */
specifier|public
name|ForkParser
parameter_list|(
name|Path
name|tikaBin
parameter_list|,
name|ParserFactoryFactory
name|factoryFactory
parameter_list|)
block|{
name|loader
operator|=
literal|null
expr_stmt|;
name|parser
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|tikaBin
operator|=
name|tikaBin
expr_stmt|;
name|this
operator|.
name|parserFactoryFactory
operator|=
name|factoryFactory
expr_stmt|;
block|}
comment|/**      *<b>EXPERT</b>      * @param tikaBin directory containing the tika-app.jar or similar -- full jar including tika-core and all      *                desired parsers and dependencies      * @param parserFactoryFactory -- the factory to use to generate the parser factory in the child process/server      * @param classLoader to use for all classes besides the parser in the child process/server      */
specifier|public
name|ForkParser
parameter_list|(
name|Path
name|tikaBin
parameter_list|,
name|ParserFactoryFactory
name|parserFactoryFactory
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|)
block|{
name|parser
operator|=
literal|null
expr_stmt|;
name|loader
operator|=
name|classLoader
expr_stmt|;
name|this
operator|.
name|tikaBin
operator|=
name|tikaBin
expr_stmt|;
name|this
operator|.
name|parserFactoryFactory
operator|=
name|parserFactoryFactory
expr_stmt|;
block|}
comment|/**      * @param loader The ClassLoader to use       * @param parser the parser to delegate to. This one cannot be another ForkParser      */
specifier|public
name|ForkParser
parameter_list|(
name|ClassLoader
name|loader
parameter_list|,
name|Parser
name|parser
parameter_list|)
block|{
if|if
condition|(
name|parser
operator|instanceof
name|ForkParser
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The underlying parser of a ForkParser should not be a ForkParser, but a specific implementation."
argument_list|)
throw|;
block|}
name|this
operator|.
name|tikaBin
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|parserFactoryFactory
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
block|}
specifier|public
name|ForkParser
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|loader
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ForkParser
parameter_list|()
block|{
name|this
argument_list|(
name|ForkParser
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the size of the process pool.      *      * @return process pool size      */
specifier|public
specifier|synchronized
name|int
name|getPoolSize
parameter_list|()
block|{
return|return
name|poolSize
return|;
block|}
comment|/**      * Sets the size of the process pool.      *      * @param poolSize process pool size      */
specifier|public
specifier|synchronized
name|void
name|setPoolSize
parameter_list|(
name|int
name|poolSize
parameter_list|)
block|{
name|this
operator|.
name|poolSize
operator|=
name|poolSize
expr_stmt|;
block|}
comment|/**      * Returns the command used to start the forked server process.      *      * @return java command line      * @deprecated since 1.8      * @see ForkParser#getJavaCommandAsList()      */
annotation|@
name|Deprecated
specifier|public
name|String
name|getJavaCommand
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|part
range|:
name|getJavaCommandAsList
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|part
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns the command used to start the forked server process.      *<p/>      * Returned list is unmodifiable.      * @return java command line args      */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getJavaCommandAsList
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|java
argument_list|)
return|;
block|}
comment|/**      * Sets the command used to start the forked server process.      * The arguments "-jar" and "/path/to/bootstrap.jar"      * or "-cp" and "/path/to/tika_bin" are      * appended to the given command when starting the process.      * The default setting is {"java", "-Xmx32m"}.      *<p/>      * Creates a defensive copy.      * @param java java command line      */
specifier|public
name|void
name|setJavaCommand
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|java
parameter_list|)
block|{
name|this
operator|.
name|java
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|java
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the command used to start the forked server process.      * The given command line is split on whitespace and the arguments 2    * "-jar" and "/path/to/bootstrap.jar" are appended to it when starting 2    * the process. The default setting is "java -Xmx32m".      *      * @param java java command line      * @deprecated since 1.8      * @see ForkParser#setJavaCommand(List)      */
annotation|@
name|Deprecated
specifier|public
name|void
name|setJavaCommand
parameter_list|(
name|String
name|java
parameter_list|)
block|{
name|setJavaCommand
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|java
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|parser
operator|.
name|getSupportedTypes
argument_list|(
name|context
argument_list|)
return|;
block|}
comment|/**      *      * This sends the objects to the server for parsing, and the server via      * the proxies acts on the handler as if it were updating it directly.      *<p>      * If using a RecursiveParserWrapper, there are two options:      *</p>      *<p>      *<ol>      *<li>Send in a class that extends {@link org.apache.tika.sax.RecursiveParserWrapperHandler},      *              and the server will proxy back the data as best it can[0].</li>      *<li>Send in a class that extends {@link AbstractRecursiveParserWrapperHandler}      *              and the server will act on the class but not proxy back the data.  This      *              can be used, for example, if all you want to do is write to disc, extend      *              {@link AbstractRecursiveParserWrapperHandler} to write to disc when      *              {@link AbstractRecursiveParserWrapperHandler#endDocument(ContentHandler, Metadata)}      *              is called, and the server will take care of the writing via the handler.</li>      *</ol>      *</p>      *<p>      *<b>NOTE:</b>[0]&quot;the server will proxy back the data as best it can&quot;.  If the handler      *     implements Serializable and is actually serializable, the server will send it and the      *     {@link Metadata} back upon {@link org.apache.tika.sax.RecursiveParserWrapperHandler#endEmbeddedDocument(ContentHandler, Metadata)}      *     or {@link org.apache.tika.sax.RecursiveParserWrapperHandler#endEmbeddedDocument(ContentHandler, Metadata)}.      *     If the handler does not implement {@link java.io.Serializable} or if there is a      *     {@link java.io.NotSerializableException} thrown during serialization, the server will      *     call {@link ContentHandler#toString()} on the ContentHandler and set that value with the      *     {@link org.apache.tika.sax.RecursiveParserWrapperHandler#TIKA_CONTENT} key and then      *     serialize and proxy that data back.      *</p>      *      * @param stream the document stream (input)      * @param handler handler for the XHTML SAX events (output)      * @param metadata document metadata (input and output)      * @param context parse context      * @throws IOException      * @throws SAXException      * @throws TikaException      */
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"null stream"
argument_list|)
throw|;
block|}
name|Throwable
name|t
decl_stmt|;
name|boolean
name|alive
init|=
literal|false
decl_stmt|;
name|ForkClient
name|client
init|=
name|acquireClient
argument_list|()
decl_stmt|;
try|try
block|{
name|ContentHandler
name|tee
init|=
operator|(
name|handler
operator|instanceof
name|AbstractRecursiveParserWrapperHandler
operator|)
condition|?
name|handler
else|:
operator|new
name|TeeContentHandler
argument_list|(
name|handler
argument_list|,
operator|new
name|MetadataContentHandler
argument_list|(
name|metadata
argument_list|)
argument_list|)
decl_stmt|;
name|t
operator|=
name|client
operator|.
name|call
argument_list|(
literal|"parse"
argument_list|,
name|stream
argument_list|,
name|tee
argument_list|,
name|metadata
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|alive
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|te
parameter_list|)
block|{
comment|// Problem occurred on our side
name|alive
operator|=
literal|true
expr_stmt|;
throw|throw
name|te
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Problem occurred on the other side
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Failed to communicate with a forked parser process."
operator|+
literal|" The process has most likely crashed due to some error"
operator|+
literal|" like running out of memory. A new process will be"
operator|+
literal|" started for the next parsing request."
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|releaseClient
argument_list|(
name|client
argument_list|,
name|alive
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|t
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|SAXException
condition|)
block|{
throw|throw
operator|(
name|SAXException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|TikaException
condition|)
block|{
throw|throw
operator|(
name|TikaException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Unexpected error in forked server process"
argument_list|,
name|t
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
for|for
control|(
name|ForkClient
name|client
range|:
name|pool
control|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|pool
operator|.
name|clear
argument_list|()
expr_stmt|;
name|poolSize
operator|=
literal|0
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|ForkClient
name|acquireClient
parameter_list|()
throws|throws
name|IOException
throws|,
name|TikaException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|ForkClient
name|client
init|=
name|pool
operator|.
name|poll
argument_list|()
decl_stmt|;
comment|// Create a new process if there's room in the pool
if|if
condition|(
name|client
operator|==
literal|null
operator|&&
name|currentlyInUse
operator|<
name|poolSize
condition|)
block|{
name|client
operator|=
name|newClient
argument_list|()
expr_stmt|;
block|}
comment|// Ping the process, and get rid of it if it's inactive
if|if
condition|(
name|client
operator|!=
literal|null
operator|&&
operator|!
name|client
operator|.
name|ping
argument_list|()
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|client
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|currentlyInUse
operator|++
expr_stmt|;
return|return
name|client
return|;
block|}
elseif|else
if|if
condition|(
name|currentlyInUse
operator|>=
name|poolSize
condition|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"Interrupted while waiting for a fork parser"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|private
name|ForkClient
name|newClient
parameter_list|()
throws|throws
name|IOException
throws|,
name|TikaException
block|{
if|if
condition|(
name|loader
operator|==
literal|null
operator|&&
name|parser
operator|==
literal|null
operator|&&
name|tikaBin
operator|!=
literal|null
operator|&&
name|parserFactoryFactory
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ForkClient
argument_list|(
name|tikaBin
argument_list|,
name|parserFactoryFactory
argument_list|,
name|java
argument_list|,
name|serverPulseMillis
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|loader
operator|!=
literal|null
operator|&&
name|parser
operator|!=
literal|null
operator|&&
name|tikaBin
operator|==
literal|null
operator|&&
name|parserFactoryFactory
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ForkClient
argument_list|(
name|loader
argument_list|,
name|parser
argument_list|,
name|java
argument_list|,
name|serverPulseMillis
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|loader
operator|!=
literal|null
operator|&&
name|parser
operator|==
literal|null
operator|&&
name|tikaBin
operator|!=
literal|null
operator|&&
name|parserFactoryFactory
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|ForkClient
argument_list|(
name|tikaBin
argument_list|,
name|parserFactoryFactory
argument_list|,
name|loader
argument_list|,
name|java
argument_list|,
name|serverPulseMillis
argument_list|)
return|;
block|}
else|else
block|{
comment|//TODO: make this more useful
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected combination of state items"
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|releaseClient
parameter_list|(
name|ForkClient
name|client
parameter_list|,
name|boolean
name|alive
parameter_list|)
block|{
name|currentlyInUse
operator|--
expr_stmt|;
if|if
condition|(
name|currentlyInUse
operator|+
name|pool
operator|.
name|size
argument_list|()
operator|<
name|poolSize
operator|&&
name|alive
condition|)
block|{
name|pool
operator|.
name|offer
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * The amount of time in milliseconds that the server      * should wait for any input or output.  If it receives no      * input or output in this amount of time, it will shutdown.      * The default is 5 seconds.      *      * @param serverPulseMillis milliseconds to sleep before checking if there has been any activity      */
specifier|public
name|void
name|setServerPulseMillis
parameter_list|(
name|long
name|serverPulseMillis
parameter_list|)
block|{
name|this
operator|.
name|serverPulseMillis
operator|=
name|serverPulseMillis
expr_stmt|;
block|}
block|}
end_class

end_unit

