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
comment|/**  * Configuration for TesseractOCRParser.  *<p>  * This allows to enable TesseractOCRParser and set its parameters:  *<p>  * TesseractOCRConfig config = new TesseractOCRConfig();<br>  * config.setTesseractPath(tesseractFolder);<br>  * parseContext.set(TesseractOCRConfig.class, config);<br>  *</p>  *<p>  * Parameters can also be set by either editing the existing TesseractOCRConfig.properties file in,  * tika-parser/src/main/resources/org/apache/tika/parser/ocr, or overriding it by creating your own  * and placing it in the package org/apache/tika/parser/ocr on the classpath.  */
end_comment

begin_class
specifier|public
class|class
name|TesseractOCRConfig
implements|implements
name|Serializable
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4861942486845757891L
decl_stmt|;
specifier|public
enum|enum
name|OUTPUT_TYPE
block|{
name|TXT
block|,
name|HOCR
block|}
comment|// Path to tesseract installation folder, if not on system path.
specifier|private
name|String
name|tesseractPath
init|=
literal|""
decl_stmt|;
comment|// Path to the 'tessdata' folder, which contains language files and config files.
specifier|private
name|String
name|tessdataPath
init|=
literal|""
decl_stmt|;
comment|// Language dictionary to be used.
specifier|private
name|String
name|language
init|=
literal|"eng"
decl_stmt|;
comment|// Tesseract page segmentation mode.
specifier|private
name|String
name|pageSegMode
init|=
literal|"1"
decl_stmt|;
comment|// Minimum file size to submit file to ocr.
specifier|private
name|int
name|minFileSizeToOcr
init|=
literal|0
decl_stmt|;
comment|// Maximum file size to submit file to ocr.
specifier|private
name|int
name|maxFileSizeToOcr
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|// Maximum time (seconds) to wait for the ocring process termination
specifier|private
name|int
name|timeout
init|=
literal|120
decl_stmt|;
comment|// The format of the ocr'ed output to be returned, txt or hocr.
specifier|private
name|OUTPUT_TYPE
name|outputType
init|=
name|OUTPUT_TYPE
operator|.
name|TXT
decl_stmt|;
comment|// enable image processing (optional)
specifier|private
name|int
name|enableImageProcessing
init|=
literal|0
decl_stmt|;
comment|// Path to ImageMagick program, if not on system path.
specifier|private
name|String
name|ImageMagickPath
init|=
literal|""
decl_stmt|;
comment|// resolution of processed image (in dpi).
specifier|private
name|int
name|density
init|=
literal|300
decl_stmt|;
comment|// number of bits in a color sample within a pixel.
specifier|private
name|int
name|depth
init|=
literal|4
decl_stmt|;
comment|// colorspace of processed image.
specifier|private
name|String
name|colorspace
init|=
literal|"gray"
decl_stmt|;
comment|// filter to be applied to the processed image.
specifier|private
name|String
name|filter
init|=
literal|"triangle"
decl_stmt|;
comment|// factor by which image is to be scaled.
specifier|private
name|int
name|resize
init|=
literal|900
decl_stmt|;
comment|// whether or not to preserve interword spacing
specifier|private
name|boolean
name|preserveInterwordSpacing
init|=
literal|false
decl_stmt|;
comment|/**      * Default contructor.      */
specifier|public
name|TesseractOCRConfig
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
literal|"TesseractOCRConfig.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Loads properties from InputStream and then tries to close InputStream.      * If there is an IOException, this silently swallows the exception      * and goes back to the default.      *      * @param is      */
specifier|public
name|TesseractOCRConfig
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
comment|// set parameters for Tesseract
name|setTesseractPath
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"tesseractPath"
argument_list|,
name|getTesseractPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setTessdataPath
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"tessdataPath"
argument_list|,
name|getTessdataPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setLanguage
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"language"
argument_list|,
name|getLanguage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setPageSegMode
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"pageSegMode"
argument_list|,
name|getPageSegMode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setMinFileSizeToOcr
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"minFileSizeToOcr"
argument_list|,
name|getMinFileSizeToOcr
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setMaxFileSizeToOcr
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"maxFileSizeToOcr"
argument_list|,
name|getMaxFileSizeToOcr
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setTimeout
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"timeout"
argument_list|,
name|getTimeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|outputTypeString
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"outputType"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"txt"
operator|.
name|equals
argument_list|(
name|outputTypeString
argument_list|)
condition|)
block|{
name|setOutputType
argument_list|(
name|OUTPUT_TYPE
operator|.
name|TXT
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"hocr"
operator|.
name|equals
argument_list|(
name|outputTypeString
argument_list|)
condition|)
block|{
name|setOutputType
argument_list|(
name|OUTPUT_TYPE
operator|.
name|HOCR
argument_list|)
expr_stmt|;
block|}
name|setPreserveInterwordSpacing
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"preserveInterwordSpacing"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// set parameters for ImageMagick
name|setEnableImageProcessing
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"enableImageProcessing"
argument_list|,
name|isEnableImageProcessing
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setImageMagickPath
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"ImageMagickPath"
argument_list|,
name|getImageMagickPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setDensity
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"density"
argument_list|,
name|getDensity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setDepth
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"depth"
argument_list|,
name|getDepth
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setColorspace
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"colorspace"
argument_list|,
name|getColorspace
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setFilter
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"filter"
argument_list|,
name|getFilter
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setResize
argument_list|(
name|getProp
argument_list|(
name|props
argument_list|,
literal|"resize"
argument_list|,
name|getResize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see #setTesseractPath(String tesseractPath)      */
specifier|public
name|String
name|getTesseractPath
parameter_list|()
block|{
return|return
name|tesseractPath
return|;
block|}
comment|/**      * Set the path to the Tesseract executable, needed if it is not on system path.      *<p>      * Note that if you set this value, it is highly recommended that you also      * set the path to the 'tessdata' folder using {@link #setTessdataPath}.      *</p>      */
specifier|public
name|void
name|setTesseractPath
parameter_list|(
name|String
name|tesseractPath
parameter_list|)
block|{
if|if
condition|(
operator|!
name|tesseractPath
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|tesseractPath
operator|.
name|endsWith
argument_list|(
name|File
operator|.
name|separator
argument_list|)
condition|)
name|tesseractPath
operator|+=
name|File
operator|.
name|separator
expr_stmt|;
name|this
operator|.
name|tesseractPath
operator|=
name|tesseractPath
expr_stmt|;
block|}
comment|/**      * @see #setTessdataPath(String tessdataPath)      */
specifier|public
name|String
name|getTessdataPath
parameter_list|()
block|{
return|return
name|tessdataPath
return|;
block|}
comment|/**      * Set the path to the 'tessdata' folder, which contains language files and config files. In some cases (such      * as on Windows), this folder is found in the Tesseract installation, but in other cases      * (such as when Tesseract is built from source), it may be located elsewhere.      */
specifier|public
name|void
name|setTessdataPath
parameter_list|(
name|String
name|tessdataPath
parameter_list|)
block|{
if|if
condition|(
operator|!
name|tessdataPath
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|tessdataPath
operator|.
name|endsWith
argument_list|(
name|File
operator|.
name|separator
argument_list|)
condition|)
name|tessdataPath
operator|+=
name|File
operator|.
name|separator
expr_stmt|;
name|this
operator|.
name|tessdataPath
operator|=
name|tessdataPath
expr_stmt|;
block|}
comment|/**      * @see #setLanguage(String language)      */
specifier|public
name|String
name|getLanguage
parameter_list|()
block|{
return|return
name|language
return|;
block|}
comment|/**      * Set tesseract language dictionary to be used. Default is "eng".      * Multiple languages may be specified, separated by plus characters.      * e.g. "chi_tra+chi_sim"      */
specifier|public
name|void
name|setLanguage
parameter_list|(
name|String
name|language
parameter_list|)
block|{
if|if
condition|(
operator|!
name|language
operator|.
name|matches
argument_list|(
literal|"([a-zA-Z]{3}(_[a-zA-Z]{3,4})?(\\+?))+"
argument_list|)
operator|||
name|language
operator|.
name|endsWith
argument_list|(
literal|"+"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid language code"
argument_list|)
throw|;
block|}
name|this
operator|.
name|language
operator|=
name|language
expr_stmt|;
block|}
comment|/**      * @see #setPageSegMode(String pageSegMode)      */
specifier|public
name|String
name|getPageSegMode
parameter_list|()
block|{
return|return
name|pageSegMode
return|;
block|}
comment|/**      * Set tesseract page segmentation mode.      * Default is 1 = Automatic page segmentation with OSD (Orientation and Script Detection)      */
specifier|public
name|void
name|setPageSegMode
parameter_list|(
name|String
name|pageSegMode
parameter_list|)
block|{
if|if
condition|(
operator|!
name|pageSegMode
operator|.
name|matches
argument_list|(
literal|"[1-9]|10"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid language code"
argument_list|)
throw|;
block|}
name|this
operator|.
name|pageSegMode
operator|=
name|pageSegMode
expr_stmt|;
block|}
comment|/**      * Whether or not to maintain interword spacing.  Default is<code>false</code>.      *      * @param preserveInterwordSpacing      */
specifier|public
name|void
name|setPreserveInterwordSpacing
parameter_list|(
name|boolean
name|preserveInterwordSpacing
parameter_list|)
block|{
name|this
operator|.
name|preserveInterwordSpacing
operator|=
name|preserveInterwordSpacing
expr_stmt|;
block|}
comment|/**      *      * @return whether or not to maintain interword spacing.      */
specifier|public
name|boolean
name|getPreserveInterwordSpacing
parameter_list|()
block|{
return|return
name|preserveInterwordSpacing
return|;
block|}
comment|/**      * @see #setMinFileSizeToOcr(int minFileSizeToOcr)      */
specifier|public
name|int
name|getMinFileSizeToOcr
parameter_list|()
block|{
return|return
name|minFileSizeToOcr
return|;
block|}
comment|/**      * Set minimum file size to submit file to ocr.      * Default is 0.      */
specifier|public
name|void
name|setMinFileSizeToOcr
parameter_list|(
name|int
name|minFileSizeToOcr
parameter_list|)
block|{
name|this
operator|.
name|minFileSizeToOcr
operator|=
name|minFileSizeToOcr
expr_stmt|;
block|}
comment|/**      * @see #setMaxFileSizeToOcr(int maxFileSizeToOcr)      */
specifier|public
name|int
name|getMaxFileSizeToOcr
parameter_list|()
block|{
return|return
name|maxFileSizeToOcr
return|;
block|}
comment|/**      * Set maximum file size to submit file to ocr.      * Default is Integer.MAX_VALUE.      */
specifier|public
name|void
name|setMaxFileSizeToOcr
parameter_list|(
name|int
name|maxFileSizeToOcr
parameter_list|)
block|{
name|this
operator|.
name|maxFileSizeToOcr
operator|=
name|maxFileSizeToOcr
expr_stmt|;
block|}
comment|/**      * Set maximum time (seconds) to wait for the ocring process to terminate.      * Default value is 120s.      */
specifier|public
name|void
name|setTimeout
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
comment|/**      * @return timeout value for Tesseract      * @see #setTimeout(int timeout)      */
specifier|public
name|int
name|getTimeout
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
comment|/**      * Set output type from ocr process.  Default is "txt", but can be "hocr".      * Default value is 120s.      */
specifier|public
name|void
name|setOutputType
parameter_list|(
name|OUTPUT_TYPE
name|outputType
parameter_list|)
block|{
name|this
operator|.
name|outputType
operator|=
name|outputType
expr_stmt|;
block|}
comment|/**      * @see #setOutputType(OUTPUT_TYPE outputType)      */
specifier|public
name|OUTPUT_TYPE
name|getOutputType
parameter_list|()
block|{
return|return
name|outputType
return|;
block|}
comment|/**      * @return image processing is enabled or not      * @see #setEnableImageProcessing(int)      */
specifier|public
name|int
name|isEnableImageProcessing
parameter_list|()
block|{
return|return
name|enableImageProcessing
return|;
block|}
comment|/**      * Set the value to true if processing is to be enabled.      * Default value is false.      */
specifier|public
name|void
name|setEnableImageProcessing
parameter_list|(
name|int
name|enableImageProcessing
parameter_list|)
block|{
name|this
operator|.
name|enableImageProcessing
operator|=
name|enableImageProcessing
expr_stmt|;
block|}
comment|/**      * @return the density      */
specifier|public
name|int
name|getDensity
parameter_list|()
block|{
return|return
name|density
return|;
block|}
comment|/**      * @param density the density to set. Valid range of values is 150-1200.      *                Default value is 300.      */
specifier|public
name|void
name|setDensity
parameter_list|(
name|int
name|density
parameter_list|)
block|{
if|if
condition|(
name|density
argument_list|<
literal|150
operator|||
name|density
argument_list|>
literal|1200
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid density value. Valid range of values is 150-1200."
argument_list|)
throw|;
block|}
name|this
operator|.
name|density
operator|=
name|density
expr_stmt|;
block|}
comment|/**      * @return the depth      */
specifier|public
name|int
name|getDepth
parameter_list|()
block|{
return|return
name|depth
return|;
block|}
comment|/**      * @param depth the depth to set. Valid values are 2, 4, 8, 16, 32, 64, 256, 4096.      *              Default value is 4.      */
specifier|public
name|void
name|setDepth
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|int
index|[]
name|allowedValues
init|=
block|{
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|16
block|,
literal|32
block|,
literal|64
block|,
literal|256
block|,
literal|4096
block|}
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
name|allowedValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|depth
operator|==
name|allowedValues
index|[
name|i
index|]
condition|)
block|{
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid depth value. Valid values are 2, 4, 8, 16, 32, 64, 256, 4096."
argument_list|)
throw|;
block|}
comment|/**      * @return the colorspace      */
specifier|public
name|String
name|getColorspace
parameter_list|()
block|{
return|return
name|colorspace
return|;
block|}
comment|/**      * @param colorspace the colorspace to set      *                   Deafult value is gray.      */
specifier|public
name|void
name|setColorspace
parameter_list|(
name|String
name|colorspace
parameter_list|)
block|{
if|if
condition|(
operator|!
name|colorspace
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
condition|)
block|{
name|this
operator|.
name|colorspace
operator|=
name|colorspace
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Colorspace value cannot be null."
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return the filter      */
specifier|public
name|String
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
comment|/**      * @param filter the filter to set. Valid values are point, hermite, cubic, box, gaussian, catrom, triangle, quadratic and mitchell.      *               Default value is triangle.      */
specifier|public
name|void
name|setFilter
parameter_list|(
name|String
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Filter value cannot be null. Valid values are point, hermite, "
operator|+
literal|"cubic, box, gaussian, catrom, triangle, quadratic and mitchell."
argument_list|)
throw|;
block|}
name|String
index|[]
name|allowedFilters
init|=
block|{
literal|"Point"
block|,
literal|"Hermite"
block|,
literal|"Cubic"
block|,
literal|"Box"
block|,
literal|"Gaussian"
block|,
literal|"Catrom"
block|,
literal|"Triangle"
block|,
literal|"Quadratic"
block|,
literal|"Mitchell"
block|}
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
name|allowedFilters
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|filter
operator|.
name|equalsIgnoreCase
argument_list|(
name|allowedFilters
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid filter value. Valid values are point, hermite, "
operator|+
literal|"cubic, box, gaussian, catrom, triangle, quadratic and mitchell."
argument_list|)
throw|;
block|}
comment|/**      * @return the resize      */
specifier|public
name|int
name|getResize
parameter_list|()
block|{
return|return
name|resize
return|;
block|}
comment|/**      * @param resize the resize to set. Valid range of values is 100-900.      *               Default value is 900.      */
specifier|public
name|void
name|setResize
parameter_list|(
name|int
name|resize
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|resize
operator|==
name|i
operator|*
literal|100
condition|)
block|{
name|this
operator|.
name|resize
operator|=
name|resize
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid resize value. Valid range of values is 100-900."
argument_list|)
throw|;
block|}
comment|/**      * @return path to ImageMagick file.      * @see #setImageMagickPath(String ImageMagickPath)      */
specifier|public
name|String
name|getImageMagickPath
parameter_list|()
block|{
return|return
name|ImageMagickPath
return|;
block|}
comment|/**      * Set the path to the ImageMagick executable, needed if it is not on system path.      *      * @param ImageMagickPath to ImageMagick file.      */
specifier|public
name|void
name|setImageMagickPath
parameter_list|(
name|String
name|ImageMagickPath
parameter_list|)
block|{
if|if
condition|(
operator|!
name|ImageMagickPath
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|ImageMagickPath
operator|.
name|endsWith
argument_list|(
name|File
operator|.
name|separator
argument_list|)
condition|)
name|ImageMagickPath
operator|+=
name|File
operator|.
name|separator
expr_stmt|;
name|this
operator|.
name|ImageMagickPath
operator|=
name|ImageMagickPath
expr_stmt|;
block|}
comment|/**      * Get property from the properties file passed in.      *      * @param properties     properties file to read from.      * @param property       the property to fetch.      * @param defaultMissing default parameter to use.      * @return the value.      */
specifier|private
name|int
name|getProp
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|String
name|property
parameter_list|,
name|int
name|defaultMissing
parameter_list|)
block|{
name|String
name|p
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
operator|||
name|p
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|defaultMissing
return|;
block|}
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|p
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Cannot parse TesseractOCRConfig variable %s, invalid integer value"
argument_list|,
name|property
argument_list|)
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * Get property from the properties file passed in.      *      * @param properties     properties file to read from.      * @param property       the property to fetch.      * @param defaultMissing default parameter to use.      * @return the value.      */
specifier|private
name|String
name|getProp
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|String
name|property
parameter_list|,
name|String
name|defaultMissing
parameter_list|)
block|{
return|return
name|properties
operator|.
name|getProperty
argument_list|(
name|property
argument_list|,
name|defaultMissing
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|getProp
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|String
name|property
parameter_list|,
name|boolean
name|defaultMissing
parameter_list|)
block|{
name|String
name|propVal
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|propVal
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
name|propVal
operator|.
name|equalsIgnoreCase
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
name|propVal
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"false"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Cannot parse TesseractOCRConfig variable %s, invalid boolean value: %s"
argument_list|,
name|property
argument_list|,
name|propVal
argument_list|)
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

