begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|pdf
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

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
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|rendering
operator|.
name|ImageType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|text
operator|.
name|PDFTextStripper
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
name|config
operator|.
name|Field
import|;
end_import

begin_comment
comment|/**  * Config for PDFParser.  *<p/>  * This allows parameters to be set programmatically:  *<ol>  *<li>Calls to PDFParser, i.e. parser.getPDFParserConfig().setEnableAutoSpace() (as before)</li>  *<li>Constructor of PDFParser</li>  *<li>Passing to PDFParser through a ParseContext: context.set(PDFParserConfig.class, config);</li>  *</ol>  *<p/>  * Parameters can also be set by modifying the PDFParserConfig.properties file,  * which lives in the expected places, in trunk:  * tika-parsers/src/main/resources/org/apache/tika/parser/pdf  *<p/>  * Or, in tika-app-x.x.jar or tika-parsers-x.x.jar:  * org/apache/tika/parser/pdf  */
end_comment

begin_class
specifier|public
class|class
name|PDFParserConfig
implements|implements
name|Serializable
block|{
specifier|public
enum|enum
name|OCR_STRATEGY
block|{
name|NO_OCR
block|,
name|OCR_ONLY
block|,
name|OCR_AND_TEXT_EXTRACTION
block|;
specifier|private
specifier|static
name|OCR_STRATEGY
name|parse
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
return|return
name|NO_OCR
return|;
block|}
elseif|else
if|if
condition|(
literal|"no_ocr"
operator|.
name|equals
argument_list|(
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|NO_OCR
return|;
block|}
elseif|else
if|if
condition|(
literal|"ocr_only"
operator|.
name|equals
argument_list|(
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|OCR_ONLY
return|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|contains
argument_list|(
literal|"ocr_and_text"
argument_list|)
condition|)
block|{
return|return
name|OCR_AND_TEXT_EXTRACTION
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"I regret that I don't recognize '"
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"' as an OCR_STRATEGY. I only recognize:"
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|OCR_STRATEGY
name|strategy
range|:
name|OCR_STRATEGY
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|strategy
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6492570218190936986L
decl_stmt|;
comment|// True if we let PDFBox "guess" where spaces should go:
specifier|private
name|boolean
name|enableAutoSpace
init|=
literal|true
decl_stmt|;
comment|// True if we let PDFBox remove duplicate overlapping text:
specifier|private
name|boolean
name|suppressDuplicateOverlappingText
decl_stmt|;
comment|// True if we extract annotation text ourselves
comment|// (workaround for PDFBOX-1143):
specifier|private
name|boolean
name|extractAnnotationText
init|=
literal|true
decl_stmt|;
comment|// True if we should sort text tokens by position
comment|// (necessary for some PDFs, but messes up other PDFs):
annotation|@
name|Field
specifier|private
name|boolean
name|sortByPosition
init|=
literal|false
decl_stmt|;
comment|//True if acroform content should be extracted
specifier|private
name|boolean
name|extractAcroFormContent
init|=
literal|true
decl_stmt|;
comment|//True if inline PDXImage objects should be extracted
specifier|private
name|boolean
name|extractInlineImages
init|=
literal|false
decl_stmt|;
comment|//True if inline images (as identified by their object id within
comment|//a pdf file) should only be extracted once.
specifier|private
name|boolean
name|extractUniqueInlineImagesOnly
init|=
literal|true
decl_stmt|;
comment|//The character width-based tolerance value used to estimate where spaces in text should be added
specifier|private
name|Float
name|averageCharTolerance
decl_stmt|;
comment|//The space width-based tolerance value used to estimate where spaces in text should be added
specifier|private
name|Float
name|spacingTolerance
decl_stmt|;
comment|//If the PDF has an XFA element, process only that and skip extracting
comment|//content from elsewhere in the document.
specifier|private
name|boolean
name|ifXFAExtractOnlyXFA
init|=
literal|false
decl_stmt|;
specifier|private
name|OCR_STRATEGY
name|ocrStrategy
init|=
name|OCR_STRATEGY
operator|.
name|NO_OCR
decl_stmt|;
specifier|private
name|int
name|ocrDPI
init|=
literal|200
decl_stmt|;
specifier|private
name|ImageType
name|ocrImageType
init|=
name|ImageType
operator|.
name|GRAY
decl_stmt|;
specifier|private
name|String
name|ocrImageFormatName
init|=
literal|"png"
decl_stmt|;
specifier|private
name|AccessChecker
name|accessChecker
decl_stmt|;
comment|//The PDFParser can throw IOExceptions if there is a problem
comment|//with a streams.  If this is set to true, Tika's
comment|//parser catches these exceptions, reports them in the metadata
comment|//and then throws the first stored exception after the parse has completed.
specifier|private
name|boolean
name|isCatchIntermediateIOExceptions
init|=
literal|true
decl_stmt|;
specifier|public
name|PDFParserConfig
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
literal|"PDFParser.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Loads properties from InputStream and then tries to close InputStream.      * If there is an IOException, this silently swallows the exception      * and goes back to the default.      *      * @param is      */
specifier|public
name|PDFParserConfig
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
comment|//initializes object and then tries to close inputstream
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
block|{         }
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
comment|//swallow
block|}
block|}
block|}
name|setEnableAutoSpace
argument_list|(
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"enableAutoSpace"
argument_list|)
argument_list|,
name|getEnableAutoSpace
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setSuppressDuplicateOverlappingText
argument_list|(
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"suppressDuplicateOverlappingText"
argument_list|)
argument_list|,
name|getSuppressDuplicateOverlappingText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setExtractAnnotationText
argument_list|(
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"extractAnnotationText"
argument_list|)
argument_list|,
name|getExtractAnnotationText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setSortByPosition
argument_list|(
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"sortByPosition"
argument_list|)
argument_list|,
name|getSortByPosition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setExtractAcroFormContent
argument_list|(
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"extractAcroFormContent"
argument_list|)
argument_list|,
name|getExtractAcroFormContent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setExtractInlineImages
argument_list|(
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"extractInlineImages"
argument_list|)
argument_list|,
name|getExtractInlineImages
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setExtractUniqueInlineImagesOnly
argument_list|(
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"extractUniqueInlineImagesOnly"
argument_list|)
argument_list|,
name|getExtractUniqueInlineImagesOnly
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setIfXFAExtractOnlyXFA
argument_list|(
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"ifXFAExtractOnlyXFA"
argument_list|)
argument_list|,
name|getIfXFAExtractOnlyXFA
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setCatchIntermediateIOExceptions
argument_list|(
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"catchIntermediateIOExceptions"
argument_list|)
argument_list|,
name|isCatchIntermediateIOExceptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setOcrStrategy
argument_list|(
name|OCR_STRATEGY
operator|.
name|parse
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"ocrStrategy"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|setOcrDPI
argument_list|(
name|getIntProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"ocrDPI"
argument_list|)
argument_list|,
name|getOcrDPI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setOcrImageFormatName
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"ocrImageFormatName"
argument_list|)
argument_list|)
expr_stmt|;
name|setOcrImageType
argument_list|(
name|parseImageType
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"ocrImageType"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|checkExtractAccessPermission
init|=
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"checkExtractAccessPermission"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|allowExtractionForAccessibility
init|=
name|getBooleanProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"allowExtractionForAccessibility"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkExtractAccessPermission
operator|==
literal|false
condition|)
block|{
comment|//silently ignore the crazy configuration of checkExtractAccessPermission = false,
comment|//but allowExtractionForAccessibility=false
name|accessChecker
operator|=
operator|new
name|AccessChecker
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|accessChecker
operator|=
operator|new
name|AccessChecker
argument_list|(
name|allowExtractionForAccessibility
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Configures the given pdf2XHTML.      *      * @param pdf2XHTML      */
specifier|public
name|void
name|configure
parameter_list|(
name|PDF2XHTML
name|pdf2XHTML
parameter_list|)
block|{
name|pdf2XHTML
operator|.
name|setSortByPosition
argument_list|(
name|getSortByPosition
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getEnableAutoSpace
argument_list|()
condition|)
block|{
name|pdf2XHTML
operator|.
name|setWordSeparator
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pdf2XHTML
operator|.
name|setWordSeparator
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getAverageCharTolerance
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pdf2XHTML
operator|.
name|setAverageCharTolerance
argument_list|(
name|getAverageCharTolerance
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getSpacingTolerance
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pdf2XHTML
operator|.
name|setSpacingTolerance
argument_list|(
name|getSpacingTolerance
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pdf2XHTML
operator|.
name|setSuppressDuplicateOverlappingText
argument_list|(
name|getSuppressDuplicateOverlappingText
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see #setExtractAcroFormContent(boolean)      */
specifier|public
name|boolean
name|getExtractAcroFormContent
parameter_list|()
block|{
return|return
name|extractAcroFormContent
return|;
block|}
comment|/**      * If true (the default), extract content from AcroForms      * at the end of the document.  If an XFA is found,      * try to process that, otherwise, process the AcroForm.      *      * @param extractAcroFormContent      */
specifier|public
name|void
name|setExtractAcroFormContent
parameter_list|(
name|boolean
name|extractAcroFormContent
parameter_list|)
block|{
name|this
operator|.
name|extractAcroFormContent
operator|=
name|extractAcroFormContent
expr_stmt|;
block|}
comment|/**      * @see #setIfXFAExtractOnlyXFA(boolean)      * @return how to handle XFA data if it exists      */
specifier|public
name|boolean
name|getIfXFAExtractOnlyXFA
parameter_list|()
block|{
return|return
name|ifXFAExtractOnlyXFA
return|;
block|}
comment|/**      * If false (the default), extract content from the full PDF      * as well as the XFA form.  This will likely lead to some duplicative      * content.      *      * @param ifXFAExtractOnlyXFA      */
specifier|public
name|void
name|setIfXFAExtractOnlyXFA
parameter_list|(
name|boolean
name|ifXFAExtractOnlyXFA
parameter_list|)
block|{
name|this
operator|.
name|ifXFAExtractOnlyXFA
operator|=
name|ifXFAExtractOnlyXFA
expr_stmt|;
block|}
comment|/**      * @see #setExtractInlineImages(boolean)      */
specifier|public
name|boolean
name|getExtractInlineImages
parameter_list|()
block|{
return|return
name|extractInlineImages
return|;
block|}
comment|/**      * If true, extract inline embedded OBXImages.      *<b>Beware:</b> some PDF documents of modest size (~4MB) can contain      * thousands of embedded images totaling> 2.5 GB.  Also, at least as of PDFBox 1.8.5,      * there can be surprisingly large memory consumption and/or out of memory errors.      * Set to<code>true</code> with caution.      *<p/>      * The default is<code>false</code>.      *<p/>      * See also: {@see #setExtractUniqueInlineImagesOnly(boolean)};      *      * @param extractInlineImages      */
specifier|public
name|void
name|setExtractInlineImages
parameter_list|(
name|boolean
name|extractInlineImages
parameter_list|)
block|{
name|this
operator|.
name|extractInlineImages
operator|=
name|extractInlineImages
expr_stmt|;
block|}
comment|/**      * @see #setExtractUniqueInlineImagesOnly(boolean)      */
specifier|public
name|boolean
name|getExtractUniqueInlineImagesOnly
parameter_list|()
block|{
return|return
name|extractUniqueInlineImagesOnly
return|;
block|}
comment|/**      * Multiple pages within a PDF file might refer to the same underlying image.      * If {@link #extractUniqueInlineImagesOnly} is set to<code>false</code>, the      * parser will call the EmbeddedExtractor each time the image appears on a page.      * This might be desired for some use cases.  However, to avoid duplication of      * extracted images, set this to<code>true</code>.  The default is<code>true</code>.      *<p/>      * Note that uniqueness is determined only by the underlying PDF COSObject id, not by      * file hash or similar equality metric.      * If the PDF actually contains multiple copies of the same image      * -- all with different object ids -- then all images will be extracted.      *<p/>      * For this parameter to have any effect, {@link #extractInlineImages} must be      * set to<code>true</code>.      *<p>      * Because of TIKA-1742 -- to avoid infinite recursion -- no matter the setting      * of this parameter, the extractor will only pull out one copy of each image per      * page.  This parameter tries to capture uniqueness across the entire document.      *      * @param extractUniqueInlineImagesOnly      */
specifier|public
name|void
name|setExtractUniqueInlineImagesOnly
parameter_list|(
name|boolean
name|extractUniqueInlineImagesOnly
parameter_list|)
block|{
name|this
operator|.
name|extractUniqueInlineImagesOnly
operator|=
name|extractUniqueInlineImagesOnly
expr_stmt|;
block|}
comment|/**      * @see #setEnableAutoSpace(boolean)      */
specifier|public
name|boolean
name|getEnableAutoSpace
parameter_list|()
block|{
return|return
name|enableAutoSpace
return|;
block|}
comment|/**      * If true (the default), the parser should estimate      * where spaces should be inserted between words.  For      * many PDFs this is necessary as they do not include      * explicit whitespace characters.      */
specifier|public
name|void
name|setEnableAutoSpace
parameter_list|(
name|boolean
name|enableAutoSpace
parameter_list|)
block|{
name|this
operator|.
name|enableAutoSpace
operator|=
name|enableAutoSpace
expr_stmt|;
block|}
comment|/**      * @see #setSuppressDuplicateOverlappingText(boolean)      */
specifier|public
name|boolean
name|getSuppressDuplicateOverlappingText
parameter_list|()
block|{
return|return
name|suppressDuplicateOverlappingText
return|;
block|}
comment|/**      * If true, the parser should try to remove duplicated      * text over the same region.  This is needed for some      * PDFs that achieve bolding by re-writing the same      * text in the same area.  Note that this can      * slow down extraction substantially (PDFBOX-956) and      * sometimes remove characters that were not in fact      * duplicated (PDFBOX-1155).  By default this is disabled.      */
specifier|public
name|void
name|setSuppressDuplicateOverlappingText
parameter_list|(
name|boolean
name|suppressDuplicateOverlappingText
parameter_list|)
block|{
name|this
operator|.
name|suppressDuplicateOverlappingText
operator|=
name|suppressDuplicateOverlappingText
expr_stmt|;
block|}
comment|/**      * @see #setExtractAnnotationText(boolean)      */
specifier|public
name|boolean
name|getExtractAnnotationText
parameter_list|()
block|{
return|return
name|extractAnnotationText
return|;
block|}
comment|/**      * If true (the default), text in annotations will be      * extracted.      */
specifier|public
name|void
name|setExtractAnnotationText
parameter_list|(
name|boolean
name|extractAnnotationText
parameter_list|)
block|{
name|this
operator|.
name|extractAnnotationText
operator|=
name|extractAnnotationText
expr_stmt|;
block|}
comment|/**      * @see #setSortByPosition(boolean)      */
specifier|public
name|boolean
name|getSortByPosition
parameter_list|()
block|{
return|return
name|sortByPosition
return|;
block|}
comment|/**      * If true, sort text tokens by their x/y position      * before extracting text.  This may be necessary for      * some PDFs (if the text tokens are not rendered "in      * order"), while for other PDFs it can produce the      * wrong result (for example if there are 2 columns,      * the text will be interleaved).  Default is false.      */
specifier|public
name|void
name|setSortByPosition
parameter_list|(
name|boolean
name|sortByPosition
parameter_list|)
block|{
name|this
operator|.
name|sortByPosition
operator|=
name|sortByPosition
expr_stmt|;
block|}
comment|/**      * @see #setAverageCharTolerance(Float)      */
specifier|public
name|Float
name|getAverageCharTolerance
parameter_list|()
block|{
return|return
name|averageCharTolerance
return|;
block|}
comment|/**      * See {@link PDFTextStripper#setAverageCharTolerance(float)}      */
specifier|public
name|void
name|setAverageCharTolerance
parameter_list|(
name|Float
name|averageCharTolerance
parameter_list|)
block|{
name|this
operator|.
name|averageCharTolerance
operator|=
name|averageCharTolerance
expr_stmt|;
block|}
comment|/**      * @see #setSpacingTolerance(Float)      */
specifier|public
name|Float
name|getSpacingTolerance
parameter_list|()
block|{
return|return
name|spacingTolerance
return|;
block|}
comment|/**      * See {@link PDFTextStripper#setSpacingTolerance(float)}      */
specifier|public
name|void
name|setSpacingTolerance
parameter_list|(
name|Float
name|spacingTolerance
parameter_list|)
block|{
name|this
operator|.
name|spacingTolerance
operator|=
name|spacingTolerance
expr_stmt|;
block|}
specifier|public
name|AccessChecker
name|getAccessChecker
parameter_list|()
block|{
return|return
name|accessChecker
return|;
block|}
specifier|public
name|void
name|setAccessChecker
parameter_list|(
name|AccessChecker
name|accessChecker
parameter_list|)
block|{
name|this
operator|.
name|accessChecker
operator|=
name|accessChecker
expr_stmt|;
block|}
comment|/**      * See {@link #setCatchIntermediateIOExceptions(boolean)}      * @return whether or not to catch IOExceptions      */
specifier|public
name|boolean
name|isCatchIntermediateIOExceptions
parameter_list|()
block|{
return|return
name|isCatchIntermediateIOExceptions
return|;
block|}
comment|/**      * The PDFBox parser will throw an IOException if there is      * a problem with a stream.  If this is set to<code>true</code>,      * Tika's PDFParser will catch these exceptions and try to parse      * the rest of the document.  After the parse is completed,      * Tika's PDFParser will throw the first caught exception.      * @param catchIntermediateIOExceptions      */
specifier|public
name|void
name|setCatchIntermediateIOExceptions
parameter_list|(
name|boolean
name|catchIntermediateIOExceptions
parameter_list|)
block|{
name|isCatchIntermediateIOExceptions
operator|=
name|catchIntermediateIOExceptions
expr_stmt|;
block|}
comment|/**      * Which strategy to use for OCR      * @param ocrStrategy      */
specifier|public
name|void
name|setOcrStrategy
parameter_list|(
name|OCR_STRATEGY
name|ocrStrategy
parameter_list|)
block|{
name|this
operator|.
name|ocrStrategy
operator|=
name|ocrStrategy
expr_stmt|;
block|}
comment|/**      * Which strategy to use for OCR      * @param ocrStrategyString      */
specifier|public
name|void
name|setOcrStrategy
parameter_list|(
name|String
name|ocrStrategyString
parameter_list|)
block|{
name|this
operator|.
name|ocrStrategy
operator|=
name|OCR_STRATEGY
operator|.
name|parse
argument_list|(
name|ocrStrategyString
argument_list|)
expr_stmt|;
block|}
comment|/**      *      * @return strategy to use for OCR      */
specifier|public
name|OCR_STRATEGY
name|getOcrStrategy
parameter_list|()
block|{
return|return
name|ocrStrategy
return|;
block|}
specifier|private
name|boolean
name|getBooleanProp
parameter_list|(
name|String
name|p
parameter_list|,
name|boolean
name|defaultMissing
parameter_list|)
block|{
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
name|defaultMissing
return|;
block|}
if|if
condition|(
name|p
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|defaultMissing
return|;
block|}
block|}
comment|//throws NumberFormatException if there's a non-null unparseable
comment|//string passed in
specifier|private
name|int
name|getIntProp
parameter_list|(
name|String
name|p
parameter_list|,
name|int
name|defaultMissing
parameter_list|)
block|{
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
name|defaultMissing
return|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|p
argument_list|)
return|;
block|}
comment|/**      * String representation of the image format used to render      * the page image for OCR (examples: png, tiff, jpeg)      * @return      */
specifier|public
name|String
name|getOcrImageFormatName
parameter_list|()
block|{
return|return
name|ocrImageFormatName
return|;
block|}
comment|/**      * @see #getOcrImageFormatName()      *      * @param ocrImageFormatName name of image format used to render      *                           page image      */
specifier|public
name|void
name|setOcrImageFormatName
parameter_list|(
name|String
name|ocrImageFormatName
parameter_list|)
block|{
name|this
operator|.
name|ocrImageFormatName
operator|=
name|ocrImageFormatName
expr_stmt|;
block|}
comment|/**      * Image type used to render the page image for OCR.      * @see #setOcrImageType(ImageType)      * @return image type      */
specifier|public
name|ImageType
name|getOcrImageType
parameter_list|()
block|{
return|return
name|ocrImageType
return|;
block|}
comment|/**      * Image type used to render the page image for OCR.      * @param ocrImageType      */
specifier|public
name|void
name|setOcrImageType
parameter_list|(
name|ImageType
name|ocrImageType
parameter_list|)
block|{
name|this
operator|.
name|ocrImageType
operator|=
name|ocrImageType
expr_stmt|;
block|}
comment|/**      * Image type used to render the page image for OCR.      * @see #setOcrImageType(ImageType)     */
specifier|public
name|void
name|setOcrImageType
parameter_list|(
name|String
name|ocrImageTypeString
parameter_list|)
block|{
name|this
operator|.
name|ocrImageType
operator|=
name|parseImageType
argument_list|(
name|ocrImageTypeString
argument_list|)
expr_stmt|;
block|}
comment|/**      * Dots per inch used to render the page image for OCR      * @return dots per inch      */
specifier|public
name|int
name|getOcrDPI
parameter_list|()
block|{
return|return
name|ocrDPI
return|;
block|}
comment|/**      * Dots per inche used to render the page image for OCR      * @param ocrDPI      */
specifier|public
name|void
name|setOcrDPI
parameter_list|(
name|int
name|ocrDPI
parameter_list|)
block|{
name|this
operator|.
name|ocrDPI
operator|=
name|ocrDPI
expr_stmt|;
block|}
specifier|private
name|ImageType
name|parseImageType
parameter_list|(
name|String
name|ocrImageType
parameter_list|)
block|{
for|for
control|(
name|ImageType
name|t
range|:
name|ImageType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|ocrImageType
operator|.
name|equalsIgnoreCase
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|t
return|;
block|}
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"I regret that I could not parse '"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ocrImageType
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"'. I'm only familiar with: "
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ImageType
name|t
range|:
name|ImageType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|==
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|t
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|PDFParserConfig
operator|)
condition|)
return|return
literal|false
return|;
name|PDFParserConfig
name|config
init|=
operator|(
name|PDFParserConfig
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|getEnableAutoSpace
argument_list|()
operator|!=
name|config
operator|.
name|getEnableAutoSpace
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getSuppressDuplicateOverlappingText
argument_list|()
operator|!=
name|config
operator|.
name|getSuppressDuplicateOverlappingText
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getExtractAnnotationText
argument_list|()
operator|!=
name|config
operator|.
name|getExtractAnnotationText
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getSortByPosition
argument_list|()
operator|!=
name|config
operator|.
name|getSortByPosition
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getExtractAcroFormContent
argument_list|()
operator|!=
name|config
operator|.
name|getExtractAcroFormContent
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getExtractInlineImages
argument_list|()
operator|!=
name|config
operator|.
name|getExtractInlineImages
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getExtractUniqueInlineImagesOnly
argument_list|()
operator|!=
name|config
operator|.
name|getExtractUniqueInlineImagesOnly
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getIfXFAExtractOnlyXFA
argument_list|()
operator|!=
name|config
operator|.
name|getIfXFAExtractOnlyXFA
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getOcrDPI
argument_list|()
operator|!=
name|config
operator|.
name|getOcrDPI
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|isCatchIntermediateIOExceptions
argument_list|()
operator|!=
name|config
operator|.
name|isCatchIntermediateIOExceptions
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|getAverageCharTolerance
argument_list|()
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getAverageCharTolerance
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|getSpacingTolerance
argument_list|()
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getSpacingTolerance
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|getOcrStrategy
argument_list|()
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getOcrStrategy
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getOcrImageType
argument_list|()
operator|!=
name|config
operator|.
name|getOcrImageType
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|getOcrImageFormatName
argument_list|()
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getOcrImageFormatName
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|getAccessChecker
argument_list|()
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getAccessChecker
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
operator|(
name|getEnableAutoSpace
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|getSuppressDuplicateOverlappingText
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|getExtractAnnotationText
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|getSortByPosition
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|getExtractAcroFormContent
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|getExtractInlineImages
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|getExtractUniqueInlineImagesOnly
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|getAverageCharTolerance
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|getSpacingTolerance
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|getIfXFAExtractOnlyXFA
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|ocrStrategy
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|getOcrDPI
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|getOcrImageType
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|getOcrImageFormatName
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|getAccessChecker
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|isCatchIntermediateIOExceptions
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"PDFParserConfig{"
operator|+
literal|"enableAutoSpace="
operator|+
name|enableAutoSpace
operator|+
literal|", suppressDuplicateOverlappingText="
operator|+
name|suppressDuplicateOverlappingText
operator|+
literal|", extractAnnotationText="
operator|+
name|extractAnnotationText
operator|+
literal|", sortByPosition="
operator|+
name|sortByPosition
operator|+
literal|", extractAcroFormContent="
operator|+
name|extractAcroFormContent
operator|+
literal|", extractInlineImages="
operator|+
name|extractInlineImages
operator|+
literal|", extractUniqueInlineImagesOnly="
operator|+
name|extractUniqueInlineImagesOnly
operator|+
literal|", averageCharTolerance="
operator|+
name|averageCharTolerance
operator|+
literal|", spacingTolerance="
operator|+
name|spacingTolerance
operator|+
literal|", ifXFAExtractOnlyXFA="
operator|+
name|ifXFAExtractOnlyXFA
operator|+
literal|", ocrStrategy="
operator|+
name|ocrStrategy
operator|+
literal|", ocrDPI="
operator|+
name|ocrDPI
operator|+
literal|", ocrImageType="
operator|+
name|ocrImageType
operator|+
literal|", ocrImageFormatName='"
operator|+
name|ocrImageFormatName
operator|+
literal|'\''
operator|+
literal|", accessChecker="
operator|+
name|accessChecker
operator|+
literal|", isCatchIntermediateIOExceptions="
operator|+
name|isCatchIntermediateIOExceptions
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

