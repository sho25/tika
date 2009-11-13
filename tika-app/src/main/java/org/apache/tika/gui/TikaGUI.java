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
name|gui
package|;
end_package

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Dimension
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
name|PrintWriter
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JEditorPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JFrame
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JOptionPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JScrollPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTabbedPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|ProgressMonitorInputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|SwingUtilities
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|UIManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXTransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|TransformerHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
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
name|BodyContentHandler
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
name|ContentHandlerDecorator
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
name|apache
operator|.
name|tika
operator|.
name|sax
operator|.
name|XHTMLContentHandler
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
name|Attributes
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
comment|/**  * Simple Swing GUI for Apache Tika. You can drag and drop files on top  * of the window to have them parsed.  */
end_comment

begin_class
specifier|public
class|class
name|TikaGUI
extends|extends
name|JFrame
block|{
comment|/**      * Main method. Sets the Swing look and feel to the operating system      * settings, and starts the Tika GUI with an {@link AutoDetectParser}      * instance as the default parser.      *      * @param args ignored      * @throws Exception if an error occurs      */
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
name|UIManager
operator|.
name|setLookAndFeel
argument_list|(
name|UIManager
operator|.
name|getSystemLookAndFeelClassName
argument_list|()
argument_list|)
expr_stmt|;
name|SwingUtilities
operator|.
name|invokeLater
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
operator|new
name|TikaGUI
argument_list|(
operator|new
name|AutoDetectParser
argument_list|()
argument_list|)
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Parsing context.      */
specifier|private
specifier|final
name|ParseContext
name|context
decl_stmt|;
comment|/**      * Configured parser instance.      */
specifier|private
specifier|final
name|Parser
name|parser
decl_stmt|;
comment|/**      * Tabs in the Tika GUI window.      */
specifier|private
specifier|final
name|JTabbedPane
name|tabs
decl_stmt|;
comment|/**      * Formatted XHTML output.      */
specifier|private
specifier|final
name|JEditorPane
name|html
decl_stmt|;
comment|/**      * Plain text output.      */
specifier|private
specifier|final
name|JEditorPane
name|text
decl_stmt|;
comment|/**      * Raw XHTML source.      */
specifier|private
specifier|final
name|JEditorPane
name|xml
decl_stmt|;
comment|/**      * Document metadata.      */
specifier|private
specifier|final
name|JEditorPane
name|metadata
decl_stmt|;
comment|/**      * Parsing errors.      */
specifier|private
specifier|final
name|JEditorPane
name|errors
decl_stmt|;
specifier|public
name|TikaGUI
parameter_list|(
name|Parser
name|parser
parameter_list|)
block|{
name|super
argument_list|(
literal|"Apache Tika"
argument_list|)
expr_stmt|;
name|setDefaultCloseOperation
argument_list|(
name|JFrame
operator|.
name|EXIT_ON_CLOSE
argument_list|)
expr_stmt|;
name|tabs
operator|=
operator|new
name|JTabbedPane
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|tabs
argument_list|)
expr_stmt|;
name|html
operator|=
name|createEditor
argument_list|(
literal|"Formatted text"
argument_list|,
literal|"text/html"
argument_list|)
expr_stmt|;
name|text
operator|=
name|createEditor
argument_list|(
literal|"Plain text"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|xml
operator|=
name|createEditor
argument_list|(
literal|"Structured text"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|metadata
operator|=
name|createEditor
argument_list|(
literal|"Metadata"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|errors
operator|=
name|createEditor
argument_list|(
literal|"Errors"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|500
argument_list|,
literal|400
argument_list|)
argument_list|)
expr_stmt|;
name|pack
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
operator|new
name|ParseContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|this
operator|.
name|context
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|importStream
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|StringWriter
name|htmlBuffer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|textBuffer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringWriter
name|xmlBuffer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|StringBuilder
name|metadataBuffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|TeeContentHandler
argument_list|(
name|getHtmlHandler
argument_list|(
name|htmlBuffer
argument_list|)
argument_list|,
name|getTextContentHandler
argument_list|(
name|textBuffer
argument_list|)
argument_list|,
name|getXmlContentHandler
argument_list|(
name|xmlBuffer
argument_list|)
argument_list|)
decl_stmt|;
name|Metadata
name|md
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|input
operator|=
operator|new
name|ProgressMonitorInputStream
argument_list|(
name|this
argument_list|,
literal|"Parsing stream"
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|input
argument_list|,
name|handler
argument_list|,
name|md
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|String
index|[]
name|names
init|=
name|md
operator|.
name|names
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|names
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|metadataBuffer
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|metadataBuffer
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|metadataBuffer
operator|.
name|append
argument_list|(
name|md
operator|.
name|get
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|metadataBuffer
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|setText
argument_list|(
name|errors
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|metadata
argument_list|,
name|metadataBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|xml
argument_list|,
name|xmlBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|text
argument_list|,
name|textBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|html
argument_list|,
name|htmlBuffer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|setSelectedIndex
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|errors
argument_list|,
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|metadata
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|xml
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|text
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|html
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|setSelectedIndex
argument_list|(
name|tabs
operator|.
name|getTabCount
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|JOptionPane
operator|.
name|showMessageDialog
argument_list|(
name|this
argument_list|,
literal|"Apache Tika was unable to parse the file or url.\n "
operator|+
literal|" See the errors tab for"
operator|+
literal|" the detailed stack trace of this error."
argument_list|,
literal|"Parse error"
argument_list|,
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|JEditorPane
name|createEditor
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|JEditorPane
name|editor
init|=
operator|new
name|JEditorPane
argument_list|()
decl_stmt|;
name|editor
operator|.
name|setContentType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setTransferHandler
argument_list|(
operator|new
name|ParsingTransferHandler
argument_list|(
name|editor
operator|.
name|getTransferHandler
argument_list|()
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|tabs
operator|.
name|add
argument_list|(
name|title
argument_list|,
operator|new
name|JScrollPane
argument_list|(
name|editor
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|editor
return|;
block|}
specifier|private
name|void
name|setText
parameter_list|(
name|JEditorPane
name|editor
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|editor
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setCaretPosition
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates and returns a content handler that turns XHTML input to      * simplified HTML output that can be correctly parsed and displayed      * by {@link JEditorPane}.      *<p>      * The returned content handler is set to output<code>html</code>      * to the given writer. The XHTML namespace is removed from the output      * to prevent the serializer from using the&lt;tag/&gt; empty element      * syntax that causes extra "&gt;" characters to be displayed.      * The&lt;head&gt; tags are dropped to prevent the serializer from      * generating a&lt;META&gt; content type tag that makes      * {@link JEditorPane} fail thinking that the document character set      * is inconsistent.      *      * @param writer output writer      * @return HTML content handler      * @throws TransformerConfigurationException if an error occurs      */
specifier|private
name|ContentHandler
name|getHtmlHandler
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
name|SAXTransformerFactory
name|factory
init|=
operator|(
name|SAXTransformerFactory
operator|)
name|SAXTransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"html"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|setResult
argument_list|(
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|ContentHandlerDecorator
argument_list|(
name|handler
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|name
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|XHTMLContentHandler
operator|.
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|uri
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
literal|"head"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|super
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|name
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|XHTMLContentHandler
operator|.
name|XHTML
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|uri
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
operator|!
literal|"head"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|super
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{             }
block|}
return|;
block|}
specifier|private
name|ContentHandler
name|getTextContentHandler
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
return|return
operator|new
name|BodyContentHandler
argument_list|(
name|writer
argument_list|)
return|;
block|}
specifier|private
name|ContentHandler
name|getXmlContentHandler
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|TransformerConfigurationException
block|{
name|SAXTransformerFactory
name|factory
init|=
operator|(
name|SAXTransformerFactory
operator|)
name|SAXTransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|setResult
argument_list|(
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|handler
return|;
block|}
block|}
end_class

end_unit

