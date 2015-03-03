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
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|util
operator|.
name|PDFTextStripper
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

begin_comment
comment|/**  * Config for PDFParser.  *   * This allows parameters to be set programmatically:  *<ol>  *<li>Calls to PDFParser, i.e. parser.getPDFParserConfig().setEnableAutoSpace() (as before)</li>  *<li>Constructor of PDFParser</li>  *<li>Passing to PDFParser through a ParseContext: context.set(PDFParserConfig.class, config);</li>  *</ol>  *   * Parameters can also be set by modifying the PDFParserConfig.properties file,  * which lives in the expected places, in trunk:  * tika-parsers/src/main/resources/org/apache/tika/parser/pdf  *   * Or, in tika-app-x.x.jar or tika-parsers-x.x.jar:  * org/apache/tika/parser/pdf  *  */
end_comment

begin_class
specifier|public
class|class
name|PDFParserConfig
implements|implements
name|Serializable
block|{
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
specifier|private
name|boolean
name|sortByPosition
init|=
literal|false
decl_stmt|;
comment|//True if we should use PDFBox's NonSequentialParser
specifier|private
name|boolean
name|useNonSequentialParser
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
specifier|private
name|AccessChecker
name|accessChecker
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
comment|/**      * Loads properties from InputStream and then tries to close InputStream.      * If there is an IOException, this silently swallows the exception      * and goes back to the default.      *       * @param is      */
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
name|getProp
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
name|getProp
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
name|getProp
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
name|getProp
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
name|setUseNonSequentialParser
argument_list|(
name|getProp
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"useNonSequentialParser"
argument_list|)
argument_list|,
name|getUseNonSequentialParser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setExtractAcroFormContent
argument_list|(
name|getProp
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
name|getProp
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
name|getProp
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
name|boolean
name|checkExtractAccessPermission
init|=
name|getProp
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
name|getProp
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
comment|/**      * Configures the given pdf2XHTML.      *       * @param pdf2XHTML      */
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
name|setForceParsing
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
comment|/**      * If true (the default), extract content from AcroForms      * at the end of the document.      *       * @param extractAcroFormContent      */
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
comment|/** @see #setExtractAcroFormContent(boolean) */
specifier|public
name|boolean
name|getExtractAcroFormContent
parameter_list|()
block|{
return|return
name|extractAcroFormContent
return|;
block|}
comment|/**      * If true, extract inline embedded OBXImages.      *<b>Beware:</b> some PDF documents of modest size (~4MB) can contain      * thousands of embedded images totaling> 2.5 GB.  Also, at least as of PDFBox 1.8.5,       * there can be surprisingly large memory consumption and/or out of memory errors.      * Set to<code>true</code> with caution.      *<p>      * The default is<code>false</code>.      *<p>      * See also: {@see #setExtractUniqueInlineImagesOnly(boolean)};      *       * @param extractInlineImages      */
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
comment|/** @see #setExtractInlineImages(boolean) */
specifier|public
name|boolean
name|getExtractInlineImages
parameter_list|()
block|{
return|return
name|extractInlineImages
return|;
block|}
comment|/**      * Multiple pages within a PDF file might refer to the same underlying image.      * If {@link #extractUniqueInlineImagesOnly} is set to<code>false</code>, the      * parser will call the EmbeddedExtractor each time the image appears on a page.      * This might be desired for some use cases.  However, to avoid duplication of       * extracted images, set this to<code>true</code>.  The default is<code>true</code>.      *<p>      * Note that uniqueness is determined only by the underlying PDF COSObject id, not by       * file hash or similar equality metric.      * If the PDF actually contains multiple copies of the same image       * -- all with different object ids -- then all images will be extracted.      *<p>      * For this parameter to have any effect, {@link #extractInlineImages} must be       * set to<code>true</code>.      *       * @param extractUniqueInlineImagesOnly      */
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
comment|/** @see #setExtractUniqueInlineImagesOnly(boolean) */
specifier|public
name|boolean
name|getExtractUniqueInlineImagesOnly
parameter_list|()
block|{
return|return
name|extractUniqueInlineImagesOnly
return|;
block|}
comment|/** @see #setEnableAutoSpace(boolean) */
specifier|public
name|boolean
name|getEnableAutoSpace
parameter_list|()
block|{
return|return
name|enableAutoSpace
return|;
block|}
comment|/**      *  If true (the default), the parser should estimate      *  where spaces should be inserted between words.  For      *  many PDFs this is necessary as they do not include      *  explicit whitespace characters.      */
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
comment|/** @see #setSuppressDuplicateOverlappingText(boolean)*/
specifier|public
name|boolean
name|getSuppressDuplicateOverlappingText
parameter_list|()
block|{
return|return
name|suppressDuplicateOverlappingText
return|;
block|}
comment|/**      *  If true, the parser should try to remove duplicated      *  text over the same region.  This is needed for some      *  PDFs that achieve bolding by re-writing the same      *  text in the same area.  Note that this can      *  slow down extraction substantially (PDFBOX-956) and      *  sometimes remove characters that were not in fact      *  duplicated (PDFBOX-1155).  By default this is disabled.      */
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
comment|/** @see #setExtractAnnotationText(boolean)*/
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
comment|/** @see #setSortByPosition(boolean)*/
specifier|public
name|boolean
name|getSortByPosition
parameter_list|()
block|{
return|return
name|sortByPosition
return|;
block|}
comment|/**      *  If true, sort text tokens by their x/y position      *  before extracting text.  This may be necessary for      *  some PDFs (if the text tokens are not rendered "in      *  order"), while for other PDFs it can produce the      *  wrong result (for example if there are 2 columns,      *  the text will be interleaved).  Default is false.      */
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
comment|/** @see #setUseNonSequentialParser(boolean)*/
specifier|public
name|boolean
name|getUseNonSequentialParser
parameter_list|()
block|{
return|return
name|useNonSequentialParser
return|;
block|}
comment|/**      * If true, uses PDFBox's non-sequential parser.      * The non-sequential parser should be much faster than the traditional      * full doc parser.  However, until PDFBOX-XXX is fixed,       * the non-sequential parser fails      * to extract some document metadata.      *<p>      * Default is false (use the traditional parser)      * @param useNonSequentialParser      */
specifier|public
name|void
name|setUseNonSequentialParser
parameter_list|(
name|boolean
name|useNonSequentialParser
parameter_list|)
block|{
name|this
operator|.
name|useNonSequentialParser
operator|=
name|useNonSequentialParser
expr_stmt|;
block|}
comment|/** @see #setAverageCharTolerance(Float)*/
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
comment|/** @see #setSpacingTolerance(Float)*/
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
specifier|public
name|AccessChecker
name|getAccessChecker
parameter_list|()
block|{
return|return
name|accessChecker
return|;
block|}
specifier|private
name|boolean
name|getProp
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
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|averageCharTolerance
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|averageCharTolerance
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|enableAutoSpace
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|extractAcroFormContent
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|extractAnnotationText
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|extractInlineImages
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|extractUniqueInlineImagesOnly
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|sortByPosition
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|spacingTolerance
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|spacingTolerance
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|suppressDuplicateOverlappingText
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
name|useNonSequentialParser
condition|?
literal|1231
else|:
literal|1237
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|PDFParserConfig
name|other
init|=
operator|(
name|PDFParserConfig
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|averageCharTolerance
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|averageCharTolerance
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|averageCharTolerance
operator|.
name|equals
argument_list|(
name|other
operator|.
name|averageCharTolerance
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|enableAutoSpace
operator|!=
name|other
operator|.
name|enableAutoSpace
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|extractAcroFormContent
operator|!=
name|other
operator|.
name|extractAcroFormContent
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|extractAnnotationText
operator|!=
name|other
operator|.
name|extractAnnotationText
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|extractInlineImages
operator|!=
name|other
operator|.
name|extractInlineImages
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|extractUniqueInlineImagesOnly
operator|!=
name|other
operator|.
name|extractUniqueInlineImagesOnly
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|sortByPosition
operator|!=
name|other
operator|.
name|sortByPosition
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|spacingTolerance
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|spacingTolerance
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|spacingTolerance
operator|.
name|equals
argument_list|(
name|other
operator|.
name|spacingTolerance
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|suppressDuplicateOverlappingText
operator|!=
name|other
operator|.
name|suppressDuplicateOverlappingText
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|useNonSequentialParser
operator|!=
name|other
operator|.
name|useNonSequentialParser
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
literal|"PDFParserConfig [enableAutoSpace="
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
literal|", useNonSequentialParser="
operator|+
name|useNonSequentialParser
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
literal|"]"
return|;
block|}
block|}
end_class

end_unit

