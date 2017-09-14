begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
package|;
end_package

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
name|IOUtils
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
name|parser
operator|.
name|microsoft
operator|.
name|OfficeParserConfig
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
name|TaggedContentHandler
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
name|TextContentHandler
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
name|ToHTMLContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|FileNotFoundException
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
name|Enumeration
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
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|TestXXEInXML
extends|extends
name|TikaTest
block|{
comment|//TODO: figure out how to test XFA and xmp in PDFs
specifier|private
specifier|static
specifier|final
name|String
name|EVIL
init|=
literal|"<!DOCTYPE roottag PUBLIC \"-//OXML/XXE/EN\" \"file:///couldnt_possibly_exist\">"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testConfirmVulnerable
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|parse
argument_list|(
literal|"testXXE.xml"
argument_list|,
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testXXE.xml"
argument_list|)
argument_list|,
operator|new
name|VulnerableXMLParser
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed!!!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXML
parameter_list|()
throws|throws
name|Exception
block|{
name|parse
argument_list|(
literal|"testXXE.xml"
argument_list|,
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testXXE.xml"
argument_list|)
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInjectedXML
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?><document>blah</document>"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|byte
index|[]
name|injected
init|=
name|injectXML
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
try|try
block|{
name|parse
argument_list|(
literal|"injected"
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|injected
argument_list|)
argument_list|,
operator|new
name|VulnerableXMLParser
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"injected should have triggered xxe"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Test
specifier|public
name|void
name|test2003_2006xml
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|is
init|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD_2003ml.xml"
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|bos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|injected
init|=
name|injectXML
argument_list|(
name|bos
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|parse
argument_list|(
literal|"testWORD_2003ml.xml"
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|injected
argument_list|)
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|is
operator|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testWORD_2006ml.xml"
argument_list|)
expr_stmt|;
name|bos
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|bos
argument_list|)
expr_stmt|;
name|injected
operator|=
name|injectXML
argument_list|(
name|bos
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|parse
argument_list|(
literal|"testWORD_2006ml.xml"
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|injected
argument_list|)
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOOXML
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|fileName
range|:
operator|new
name|String
index|[]
block|{
literal|"testWORD.docx"
block|,
literal|"testWORD_1img.docx"
block|,
literal|"testWORD_2006ml.docx"
block|,
literal|"testWORD_embedded_pics.docx"
block|,
literal|"testWORD_macros.docm"
block|,
literal|"testEXCEL_textbox.xlsx"
block|,
literal|"testEXCEL_macro.xlsm"
block|,
literal|"testEXCEL_phonetic.xlsx"
block|,
literal|"testEXCEL_embeddedPDF_windows.xlsx"
block|,
literal|"testPPT_2imgs.pptx"
block|,
literal|"testPPT_comment.pptx"
block|,
literal|"testPPT_EmbeddedPDF.pptx"
block|,
literal|"testPPT_macros.pptm"
block|}
control|)
block|{
name|_testOOXML
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|_testOOXML
parameter_list|(
name|String
name|fileName
parameter_list|)
throws|throws
name|Exception
block|{
name|Path
name|originalOOXML
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/"
operator|+
name|fileName
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
name|Path
name|injected
init|=
name|injectOOXML
argument_list|(
name|originalOOXML
argument_list|)
decl_stmt|;
name|Parser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|ContentHandler
name|xhtml
init|=
operator|new
name|ToHTMLContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|OfficeParserConfig
name|officeParserConfig
init|=
operator|new
name|OfficeParserConfig
argument_list|()
decl_stmt|;
name|parseContext
operator|.
name|set
argument_list|(
name|OfficeParserConfig
operator|.
name|class
argument_list|,
name|officeParserConfig
argument_list|)
expr_stmt|;
comment|//if the SafeContentHandler is turned off, this will throw an FNFE
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
try|try
block|{
name|p
operator|.
name|parse
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|injected
argument_list|)
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
name|metadata
operator|=
operator|new
name|Metadata
argument_list|()
expr_stmt|;
name|officeParserConfig
operator|.
name|setUseSAXDocxExtractor
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|officeParserConfig
operator|.
name|setUseSAXPptxExtractor
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|p
operator|.
name|parse
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|injected
argument_list|)
argument_list|,
name|xhtml
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"problem with: "
operator|+
name|fileName
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Files
operator|.
name|delete
argument_list|(
name|injected
argument_list|)
expr_stmt|;
block|}
block|}
comment|//use this to confirm that this works
comment|//by manually turning off the SafeContentHandler in SXWPFWordExtractorDecorator's
comment|//handlePart
specifier|public
name|void
name|testDocxWithIncorrectSAXConfiguration
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|originalDocx
init|=
name|getResourceAsFile
argument_list|(
literal|"/test-documents/testWORD_macros.docm"
argument_list|)
operator|.
name|toPath
argument_list|()
decl_stmt|;
name|Path
name|injected
init|=
name|injectOOXML
argument_list|(
name|originalDocx
argument_list|)
decl_stmt|;
name|Parser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
name|ContentHandler
name|xhtml
init|=
operator|new
name|ToHTMLContentHandler
argument_list|()
decl_stmt|;
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
name|OfficeParserConfig
name|officeParserConfig
init|=
operator|new
name|OfficeParserConfig
argument_list|()
decl_stmt|;
name|officeParserConfig
operator|.
name|setUseSAXDocxExtractor
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|parseContext
operator|.
name|set
argument_list|(
name|OfficeParserConfig
operator|.
name|class
argument_list|,
name|officeParserConfig
argument_list|)
expr_stmt|;
name|parseContext
operator|.
name|set
argument_list|(
name|SAXParser
operator|.
name|class
argument_list|,
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newSAXParser
argument_list|()
argument_list|)
expr_stmt|;
comment|//if the SafeContentHandler is turned off, this will throw an FNFE
try|try
block|{
name|p
operator|.
name|parse
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|injected
argument_list|)
argument_list|,
name|xhtml
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|//Files.delete(injected);
block|}
block|}
specifier|private
name|Path
name|injectOOXML
parameter_list|(
name|Path
name|original
parameter_list|)
throws|throws
name|IOException
block|{
name|ZipFile
name|input
init|=
operator|new
name|ZipFile
argument_list|(
name|original
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|output
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|"tika-xxe-"
argument_list|,
literal|".zip"
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
name|ZipOutputStream
name|outZip
init|=
operator|new
name|ZipOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|output
argument_list|)
argument_list|)
decl_stmt|;
name|Enumeration
argument_list|<
name|?
extends|extends
name|ZipEntry
argument_list|>
name|zipEntryEnumeration
init|=
name|input
operator|.
name|entries
argument_list|()
decl_stmt|;
while|while
condition|(
name|zipEntryEnumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|ZipEntry
name|entry
init|=
name|zipEntryEnumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|input
operator|.
name|getInputStream
argument_list|(
name|entry
argument_list|)
argument_list|,
name|bos
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
operator|&&
comment|//don't inject the slides because you'll get a bean exception
comment|//Unexpected node
operator|!
name|entry
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"slides/slide"
argument_list|)
condition|)
block|{
name|bytes
operator|=
name|injectXML
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
name|ZipEntry
name|outEntry
init|=
operator|new
name|ZipEntry
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|outZip
operator|.
name|putNextEntry
argument_list|(
name|outEntry
argument_list|)
expr_stmt|;
name|outZip
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|outZip
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
block|}
name|outZip
operator|.
name|flush
argument_list|()
expr_stmt|;
name|outZip
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|output
operator|.
name|toPath
argument_list|()
return|;
block|}
specifier|private
name|byte
index|[]
name|injectXML
parameter_list|(
name|byte
index|[]
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|startXML
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|endXML
init|=
operator|-
literal|1
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
name|input
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|input
index|[
name|i
index|]
operator|==
literal|'<'
operator|&&
name|i
operator|+
literal|1
operator|<
name|input
operator|.
name|length
operator|&&
name|input
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'?'
condition|)
block|{
name|startXML
operator|=
name|i
expr_stmt|;
block|}
if|if
condition|(
name|input
index|[
name|i
index|]
operator|==
literal|'?'
operator|&&
name|i
operator|+
literal|1
operator|<
name|input
operator|.
name|length
operator|&&
name|input
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'>'
condition|)
block|{
name|endXML
operator|=
name|i
operator|+
literal|1
expr_stmt|;
break|break;
block|}
block|}
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|startXML
operator|>
operator|-
literal|1
operator|&&
name|endXML
operator|>
operator|-
literal|1
condition|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|input
argument_list|,
name|startXML
argument_list|,
name|endXML
operator|-
name|startXML
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|bos
operator|.
name|write
argument_list|(
name|EVIL
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|bos
operator|.
name|write
argument_list|(
name|input
argument_list|,
name|endXML
operator|+
literal|1
argument_list|,
operator|(
name|input
operator|.
name|length
operator|-
name|endXML
operator|-
literal|1
operator|)
argument_list|)
expr_stmt|;
return|return
name|bos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
specifier|private
name|void
name|parse
parameter_list|(
name|String
name|testFile
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|Parser
name|parser
parameter_list|)
throws|throws
name|Exception
block|{
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
argument_list|,
operator|new
name|Metadata
argument_list|()
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|VulnerableXMLParser
extends|extends
name|AbstractParser
block|{
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
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
return|;
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
name|TaggedContentHandler
name|tagged
init|=
operator|new
name|TaggedContentHandler
argument_list|(
name|handler
argument_list|)
decl_stmt|;
try|try
block|{
name|SAXParserFactory
name|saxParserFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|SAXParser
name|parser
init|=
name|saxParserFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|stream
argument_list|,
operator|new
name|TextContentHandler
argument_list|(
name|handler
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
comment|//there will be one...ignore it
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"parser config ex"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

