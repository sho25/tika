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
name|parser
operator|.
name|ocr
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|imageio
operator|.
name|ImageIO
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Image
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|image
operator|.
name|BufferedImage
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
name|HashMap
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
name|Map
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
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
name|ExecutionException
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
name|FutureTask
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
name|TimeUnit
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
name|TimeoutException
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
name|IOUtils
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
name|logging
operator|.
name|LogFactory
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
name|TemporaryResources
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
name|mime
operator|.
name|MediaTypeRegistry
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
name|CompositeParser
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
name|parser
operator|.
name|external
operator|.
name|ExternalParser
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
name|image
operator|.
name|ImageParser
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
name|image
operator|.
name|TiffParser
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
name|jpeg
operator|.
name|JpegParser
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * TesseractOCRParser powered by tesseract-ocr engine. To enable this parser,  * create a {@link TesseractOCRConfig} object and pass it through a  * ParseContext. Tesseract-ocr must be installed and on system path or the path  * to its root folder must be provided:  *<p>  * TesseractOCRConfig config = new TesseractOCRConfig();<br>  * //Needed if tesseract is not on system path<br>  * config.setTesseractPath(tesseractFolder);<br>  * parseContext.set(TesseractOCRConfig.class, config);<br>  *</p>  *  *  */
end_comment

begin_class
specifier|public
class|class
name|TesseractOCRParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|8167538283213097265L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|TesseractOCRConfig
name|DEFAULT_CONFIG
init|=
operator|new
name|TesseractOCRConfig
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|MediaType
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|MediaType
index|[]
block|{
name|MediaType
operator|.
name|image
argument_list|(
literal|"png"
argument_list|)
block|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"jpeg"
argument_list|)
block|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"tiff"
argument_list|)
block|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-ms-bmp"
argument_list|)
block|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"gif"
argument_list|)
block|}
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|TESSERACT_PRESENT
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
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
comment|// If Tesseract is installed, offer our supported image types
name|TesseractOCRConfig
name|config
init|=
name|context
operator|.
name|get
argument_list|(
name|TesseractOCRConfig
operator|.
name|class
argument_list|,
name|DEFAULT_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasTesseract
argument_list|(
name|config
argument_list|)
condition|)
return|return
name|SUPPORTED_TYPES
return|;
comment|// Otherwise don't advertise anything, so the other image parsers
comment|//  can be selected instead
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
specifier|private
name|void
name|setEnv
parameter_list|(
name|TesseractOCRConfig
name|config
parameter_list|,
name|ProcessBuilder
name|pb
parameter_list|)
block|{
name|String
name|tessdataPrefix
init|=
literal|"TESSDATA_PREFIX"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
init|=
name|pb
operator|.
name|environment
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|config
operator|.
name|getTessdataPath
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
name|tessdataPrefix
argument_list|,
name|config
operator|.
name|getTessdataPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|config
operator|.
name|getTesseractPath
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|env
operator|.
name|put
argument_list|(
name|tessdataPrefix
argument_list|,
name|config
operator|.
name|getTesseractPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|hasTesseract
parameter_list|(
name|TesseractOCRConfig
name|config
parameter_list|)
block|{
comment|// Fetch where the config says to find Tesseract
name|String
name|tesseract
init|=
name|config
operator|.
name|getTesseractPath
argument_list|()
operator|+
name|getTesseractProg
argument_list|()
decl_stmt|;
comment|// Have we already checked for a copy of Tesseract there?
if|if
condition|(
name|TESSERACT_PRESENT
operator|.
name|containsKey
argument_list|(
name|tesseract
argument_list|)
condition|)
block|{
return|return
name|TESSERACT_PRESENT
operator|.
name|get
argument_list|(
name|tesseract
argument_list|)
return|;
block|}
comment|// Try running Tesseract from there, and see if it exists + works
name|String
index|[]
name|checkCmd
init|=
block|{
name|tesseract
block|}
decl_stmt|;
try|try
block|{
name|boolean
name|hasTesseract
init|=
name|ExternalParser
operator|.
name|check
argument_list|(
name|checkCmd
argument_list|)
decl_stmt|;
name|TESSERACT_PRESENT
operator|.
name|put
argument_list|(
name|tesseract
argument_list|,
name|hasTesseract
argument_list|)
expr_stmt|;
return|return
name|hasTesseract
return|;
block|}
catch|catch
parameter_list|(
name|NoClassDefFoundError
name|e
parameter_list|)
block|{
comment|// This happens under OSGi + Fork Parser - see TIKA-1507
comment|// As a workaround for now, just say we can't use OCR
comment|// TODO Resolve it so we don't need this try/catch block
name|TESSERACT_PRESENT
operator|.
name|put
argument_list|(
name|tesseract
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|Image
name|image
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
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
name|TikaInputStream
name|tis
init|=
literal|null
decl_stmt|;
try|try
block|{
name|int
name|w
init|=
name|image
operator|.
name|getWidth
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|int
name|h
init|=
name|image
operator|.
name|getHeight
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|BufferedImage
name|bImage
init|=
operator|new
name|BufferedImage
argument_list|(
name|w
argument_list|,
name|h
argument_list|,
name|BufferedImage
operator|.
name|TYPE_INT_RGB
argument_list|)
decl_stmt|;
name|File
name|file
init|=
name|tmp
operator|.
name|createTemporaryFile
argument_list|()
decl_stmt|;
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|ImageIO
operator|.
name|write
argument_list|(
name|bImage
argument_list|,
literal|"png"
argument_list|,
name|fos
argument_list|)
expr_stmt|;
name|tis
operator|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|parse
argument_list|(
name|tis
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
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
if|if
condition|(
name|tis
operator|!=
literal|null
condition|)
name|tis
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|TesseractOCRConfig
name|config
init|=
name|context
operator|.
name|get
argument_list|(
name|TesseractOCRConfig
operator|.
name|class
argument_list|,
name|DEFAULT_CONFIG
argument_list|)
decl_stmt|;
comment|// If Tesseract is not on the path with the current config, do not try to run OCR
comment|// getSupportedTypes shouldn't have listed us as handling it, so this should only
comment|//  occur if someone directly calls this parser, not via DefaultParser or similar
if|if
condition|(
operator|!
name|hasTesseract
argument_list|(
name|config
argument_list|)
condition|)
return|return;
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
name|File
name|output
init|=
literal|null
decl_stmt|;
try|try
block|{
name|TikaInputStream
name|tikaStream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
name|File
name|input
init|=
name|tikaStream
operator|.
name|getFile
argument_list|()
decl_stmt|;
name|long
name|size
init|=
name|tikaStream
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|>=
name|config
operator|.
name|getMinFileSizeToOcr
argument_list|()
operator|&&
name|size
operator|<=
name|config
operator|.
name|getMaxFileSizeToOcr
argument_list|()
condition|)
block|{
name|output
operator|=
name|tmp
operator|.
name|createTemporaryFile
argument_list|()
expr_stmt|;
name|doOCR
argument_list|(
name|input
argument_list|,
name|output
argument_list|,
name|config
argument_list|)
expr_stmt|;
comment|// Tesseract appends .txt to output file name
name|output
operator|=
operator|new
name|File
argument_list|(
name|output
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|".txt"
argument_list|)
expr_stmt|;
if|if
condition|(
name|output
operator|.
name|exists
argument_list|()
condition|)
name|extractOutput
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|output
argument_list|)
argument_list|,
name|xhtml
argument_list|)
expr_stmt|;
block|}
comment|// Temporary workaround for TIKA-1445 - until we can specify
comment|//  composite parsers with strategies (eg Composite, Try In Turn),
comment|//  always send the image onwards to the regular parser to have
comment|//  the metadata for them extracted as well
name|_TMP_IMAGE_METADATA_PARSER
operator|.
name|parse
argument_list|(
name|tikaStream
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
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
if|if
condition|(
name|output
operator|!=
literal|null
condition|)
block|{
name|output
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// TIKA-1445 workaround parser
specifier|private
specifier|static
name|Parser
name|_TMP_IMAGE_METADATA_PARSER
init|=
operator|new
name|CompositeImageParser
argument_list|()
decl_stmt|;
specifier|private
specifier|static
class|class
name|CompositeImageParser
extends|extends
name|CompositeParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|2398203346206381382L
decl_stmt|;
specifier|private
specifier|static
name|List
argument_list|<
name|Parser
argument_list|>
name|imageParsers
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Parser
index|[]
block|{
operator|new
name|ImageParser
argument_list|()
block|,
operator|new
name|JpegParser
argument_list|()
block|,
operator|new
name|TiffParser
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|CompositeImageParser
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|MediaTypeRegistry
argument_list|()
argument_list|,
name|imageParsers
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Run external tesseract-ocr process.      *      * @param input      *          File to be ocred      * @param output      *          File to collect ocr result      * @param config      *          Configuration of tesseract-ocr engine      * @throws TikaException      *           if the extraction timed out      * @throws IOException      *           if an input error occurred      */
specifier|private
name|void
name|doOCR
parameter_list|(
name|File
name|input
parameter_list|,
name|File
name|output
parameter_list|,
name|TesseractOCRConfig
name|config
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaException
block|{
name|String
index|[]
name|cmd
init|=
block|{
name|config
operator|.
name|getTesseractPath
argument_list|()
operator|+
name|getTesseractProg
argument_list|()
block|,
name|input
operator|.
name|getPath
argument_list|()
block|,
name|output
operator|.
name|getPath
argument_list|()
block|,
literal|"-l"
block|,
name|config
operator|.
name|getLanguage
argument_list|()
block|,
literal|"-psm"
block|,
name|config
operator|.
name|getPageSegMode
argument_list|()
block|}
decl_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|setEnv
argument_list|(
name|config
argument_list|,
name|pb
argument_list|)
expr_stmt|;
specifier|final
name|Process
name|process
init|=
name|pb
operator|.
name|start
argument_list|()
decl_stmt|;
name|process
operator|.
name|getOutputStream
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|InputStream
name|out
init|=
name|process
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|InputStream
name|err
init|=
name|process
operator|.
name|getErrorStream
argument_list|()
decl_stmt|;
name|logStream
argument_list|(
literal|"OCR MSG"
argument_list|,
name|out
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|logStream
argument_list|(
literal|"OCR ERROR"
argument_list|,
name|err
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|FutureTask
argument_list|<
name|Integer
argument_list|>
name|waitTask
init|=
operator|new
name|FutureTask
argument_list|<
name|Integer
argument_list|>
argument_list|(
operator|new
name|Callable
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|process
operator|.
name|waitFor
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|Thread
name|waitThread
init|=
operator|new
name|Thread
argument_list|(
name|waitTask
argument_list|)
decl_stmt|;
name|waitThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|waitTask
operator|.
name|get
argument_list|(
name|config
operator|.
name|getTimeout
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|waitThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"TesseractOCRParser interrupted"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// should not be thrown
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|waitThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"TesseractOCRParser timeout"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Reads the contents of the given stream and write it to the given XHTML      * content handler. The stream is closed once fully processed.      *      * @param stream      *          Stream where is the result of ocr      * @param xhtml      *          XHTML content handler      * @throws SAXException      *           if the XHTML SAX events could not be handled      * @throws IOException      *           if an input error occurred      */
specifier|private
name|void
name|extractOutput
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
init|;
name|n
operator|!=
operator|-
literal|1
condition|;
name|n
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
control|)
block|{
if|if
condition|(
name|n
operator|>
literal|0
condition|)
name|xhtml
operator|.
name|characters
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * Starts a thread that reads the contents of the standard output or error      * stream of the given process to not block the process. The stream is closed      * once fully processed.      */
specifier|private
name|void
name|logStream
parameter_list|(
specifier|final
name|String
name|logType
parameter_list|,
specifier|final
name|InputStream
name|stream
parameter_list|,
specifier|final
name|File
name|file
parameter_list|)
block|{
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|n
init|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
init|;
name|n
operator|!=
operator|-
literal|1
condition|;
name|n
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
control|)
name|out
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{                  }
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
name|String
name|msg
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TesseractOCRParser
operator|.
name|class
argument_list|)
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|static
name|String
name|getTesseractProg
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
condition|?
literal|"tesseract.exe"
else|:
literal|"tesseract"
return|;
block|}
block|}
end_class

end_unit

