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
name|pdf
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|cos
operator|.
name|COSBase
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
name|cos
operator|.
name|COSDictionary
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
name|cos
operator|.
name|COSDocument
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
name|cos
operator|.
name|COSName
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
name|cos
operator|.
name|COSObject
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
name|io
operator|.
name|MemoryUsageSetting
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
name|pdfparser
operator|.
name|XrefTrailerResolver
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
name|pdmodel
operator|.
name|PDDocument
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
name|preflight
operator|.
name|Format
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
name|preflight
operator|.
name|PreflightConfiguration
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
name|preflight
operator|.
name|PreflightContext
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
name|preflight
operator|.
name|PreflightDocument
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
name|preflight
operator|.
name|ValidationResult
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
name|preflight
operator|.
name|exception
operator|.
name|SyntaxValidationException
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
name|preflight
operator|.
name|parser
operator|.
name|PreflightParser
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
name|metadata
operator|.
name|PDF
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
name|utils
operator|.
name|ExceptionUtils
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|pdfbox
operator|.
name|preflight
operator|.
name|PreflightConstants
operator|.
name|DICTIONARY_KEY_LINEARIZED
import|;
end_import

begin_class
specifier|public
class|class
name|PDFPreflightParser
extends|extends
name|PDFParser
block|{
specifier|private
specifier|static
specifier|final
name|PDFPreflightParserConfig
name|DEFAULT
init|=
operator|new
name|PDFPreflightParserConfig
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|PDDocument
name|getPDDocument
parameter_list|(
name|InputStream
name|inputStream
parameter_list|,
name|String
name|password
parameter_list|,
name|MemoryUsageSetting
name|memoryUsageSetting
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|TikaInputStream
name|tis
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|inputStream
argument_list|)
init|)
block|{
return|return
name|getPDDocument
argument_list|(
name|tis
operator|.
name|getPath
argument_list|()
argument_list|,
name|password
argument_list|,
name|memoryUsageSetting
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|PDDocument
name|getPDDocument
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|password
parameter_list|,
name|MemoryUsageSetting
name|memoryUsageSetting
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|PDFPreflightParserConfig
name|pppConfig
init|=
name|context
operator|.
name|get
argument_list|(
name|PDFPreflightParserConfig
operator|.
name|class
argument_list|,
name|DEFAULT
argument_list|)
decl_stmt|;
name|PreflightConfiguration
name|configuration
init|=
operator|new
name|PreflightConfiguration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setMaxErrors
argument_list|(
name|pppConfig
operator|.
name|getMaxErrors
argument_list|()
argument_list|)
expr_stmt|;
name|PreflightParser
name|preflightParser
init|=
operator|new
name|PreflightParser
argument_list|(
name|path
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
name|preflightParser
operator|.
name|setLenient
argument_list|(
name|pppConfig
operator|.
name|isLenient
argument_list|)
expr_stmt|;
try|try
block|{
name|preflightParser
operator|.
name|parse
argument_list|(
name|pppConfig
operator|.
name|getFormat
argument_list|()
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SyntaxValidationException
name|e
parameter_list|)
block|{
comment|//back off to try to load the file normally
return|return
name|handleSyntaxException
argument_list|(
name|path
argument_list|,
name|password
argument_list|,
name|memoryUsageSetting
argument_list|,
name|metadata
argument_list|,
name|e
argument_list|)
return|;
block|}
name|PreflightDocument
name|preflightDocument
init|=
name|preflightParser
operator|.
name|getPreflightDocument
argument_list|()
decl_stmt|;
name|preflightDocument
operator|.
name|validate
argument_list|()
expr_stmt|;
name|extractPreflight
argument_list|(
name|preflightDocument
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
comment|//need to return this to ensure that it gets closed
comment|//the preflight document can keep some other resources open.
return|return
name|preflightParser
operator|.
name|getPreflightDocument
argument_list|()
return|;
block|}
specifier|private
name|void
name|extractPreflight
parameter_list|(
name|PreflightDocument
name|preflightDocument
parameter_list|,
name|Metadata
name|metadata
parameter_list|)
block|{
name|ValidationResult
name|result
init|=
name|preflightDocument
operator|.
name|getResult
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_SPECIFICATION
argument_list|,
name|preflightDocument
operator|.
name|getSpecification
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_IS_VALID
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|result
operator|.
name|isValid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ValidationResult
operator|.
name|ValidationError
argument_list|>
name|errors
init|=
name|result
operator|.
name|getErrorsList
argument_list|()
decl_stmt|;
for|for
control|(
name|ValidationResult
operator|.
name|ValidationError
name|err
range|:
name|errors
control|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_VALIDATION_ERRORS
argument_list|,
name|err
operator|.
name|getErrorCode
argument_list|()
operator|+
literal|" : "
operator|+
name|err
operator|.
name|getDetails
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|PreflightContext
name|preflightContext
init|=
name|preflightDocument
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|XrefTrailerResolver
name|resolver
init|=
name|preflightContext
operator|.
name|getXrefTrailerResolver
argument_list|()
decl_stmt|;
name|int
name|trailerCount
init|=
name|resolver
operator|.
name|getTrailerCount
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_TRAILER_COUNT
argument_list|,
name|trailerCount
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_XREF_TYPE
argument_list|,
name|resolver
operator|.
name|getXrefType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|preflightContext
operator|.
name|getIccProfileWrapper
argument_list|()
operator|!=
literal|null
operator|&&
name|preflightContext
operator|.
name|getIccProfileWrapper
argument_list|()
operator|.
name|getProfile
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_ICC_PROFILE
argument_list|,
name|preflightContext
operator|.
name|getIccProfileWrapper
argument_list|()
operator|.
name|getProfile
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|COSDictionary
name|linearized
init|=
name|getLinearizedDictionary
argument_list|(
name|preflightDocument
argument_list|)
decl_stmt|;
if|if
condition|(
name|linearized
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_IS_LINEARIZED
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|trailerCount
operator|>
literal|2
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_INCREMENTAL_UPDATES
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_INCREMENTAL_UPDATES
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_IS_LINEARIZED
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
if|if
condition|(
name|trailerCount
operator|>
literal|1
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_INCREMENTAL_UPDATES
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|set
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_INCREMENTAL_UPDATES
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Copied verbatim from PDFBox      *      * According to the PDF Reference, A linearized PDF contain a dictionary as first object (linearized dictionary) and      * only this one in the first section.      *      * @param document the document to validate.      * @return the linearization dictionary or null.      */
specifier|protected
specifier|static
name|COSDictionary
name|getLinearizedDictionary
parameter_list|(
name|PDDocument
name|document
parameter_list|)
block|{
comment|// ---- Get Ref to obj
name|COSDocument
name|cDoc
init|=
name|document
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|?
argument_list|>
name|lObj
init|=
name|cDoc
operator|.
name|getObjects
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|object
range|:
name|lObj
control|)
block|{
name|COSBase
name|curObj
init|=
operator|(
operator|(
name|COSObject
operator|)
name|object
operator|)
operator|.
name|getObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|curObj
operator|instanceof
name|COSDictionary
operator|&&
operator|(
operator|(
name|COSDictionary
operator|)
name|curObj
operator|)
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
name|COSName
operator|.
name|getPDFName
argument_list|(
name|DICTIONARY_KEY_LINEARIZED
argument_list|)
argument_list|)
condition|)
block|{
return|return
operator|(
name|COSDictionary
operator|)
name|curObj
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|PDDocument
name|handleSyntaxException
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|password
parameter_list|,
name|MemoryUsageSetting
name|memoryUsageSetting
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|SyntaxValidationException
name|e
parameter_list|)
throws|throws
name|IOException
block|{
name|metadata
operator|.
name|add
argument_list|(
name|PDF
operator|.
name|PREFLIGHT_PARSE_EXCEPTION
argument_list|,
name|ExceptionUtils
operator|.
name|getStackTrace
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|PDDocument
operator|.
name|load
argument_list|(
name|path
operator|.
name|toFile
argument_list|()
argument_list|,
name|password
argument_list|,
name|memoryUsageSetting
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|PDFPreflightParserConfig
block|{
specifier|private
name|int
name|maxErrors
init|=
literal|100
decl_stmt|;
specifier|private
name|boolean
name|isLenient
init|=
literal|true
decl_stmt|;
specifier|private
name|Format
name|format
init|=
name|Format
operator|.
name|PDF_A1B
decl_stmt|;
specifier|public
name|int
name|getMaxErrors
parameter_list|()
block|{
return|return
name|maxErrors
return|;
block|}
specifier|public
name|boolean
name|isLenient
parameter_list|()
block|{
return|return
name|isLenient
return|;
block|}
specifier|public
name|Format
name|getFormat
parameter_list|()
block|{
return|return
name|format
return|;
block|}
block|}
block|}
end_class

end_unit
