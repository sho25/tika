begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * ******************************************************************************  * Copyright (C) 2005-2007, International Business Machines Corporation and    *  * others. All Rights Reserved.                                                *  * ******************************************************************************  */
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
name|txt
package|;
end_package

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

begin_comment
comment|/**  * This class represents a charset that has been identified by a CharsetDetector  * as a possible encoding for a set of input data.  From an instance of this  * class, you can ask for a confidence level in the charset identification,  * or for Java Reader or String to access the original byte data in Unicode form.  *<p/>  * Instances of this class are created only by CharsetDetectors.  *<p/>  * Note:  this class has a natural ordering that is inconsistent with equals.  *        The natural ordering is based on the match confidence value.  *  * @stable ICU 3.4  */
end_comment

begin_class
specifier|public
class|class
name|CharsetMatch
implements|implements
name|Comparable
argument_list|<
name|CharsetMatch
argument_list|>
block|{
comment|/**      * Bit flag indicating the match is based on the the encoding scheme.      *      * @see #getMatchType      * @stable ICU 3.4      */
specifier|static
specifier|public
specifier|final
name|int
name|ENCODING_SCHEME
init|=
literal|1
decl_stmt|;
comment|/**      * Bit flag indicating the match is based on the presence of a BOM.      *      * @see #getMatchType      * @stable ICU 3.4      */
specifier|static
specifier|public
specifier|final
name|int
name|BOM
init|=
literal|2
decl_stmt|;
comment|/**      * Bit flag indicating he match is based on the declared encoding.      *      * @see #getMatchType      * @stable ICU 3.4      */
specifier|static
specifier|public
specifier|final
name|int
name|DECLARED_ENCODING
init|=
literal|4
decl_stmt|;
comment|/**      * Bit flag indicating the match is based on language statistics.      *      * @see #getMatchType      * @stable ICU 3.4      */
specifier|static
specifier|public
specifier|final
name|int
name|LANG_STATISTICS
init|=
literal|8
decl_stmt|;
comment|//
comment|//   Private Data
comment|//
specifier|private
name|int
name|fConfidence
decl_stmt|;
specifier|private
name|CharsetRecognizer
name|fRecognizer
decl_stmt|;
specifier|private
name|byte
index|[]
name|fRawInput
init|=
literal|null
decl_stmt|;
comment|// Original, untouched input bytes.
comment|//  If user gave us a byte array, this is it.
specifier|private
name|int
name|fRawLength
decl_stmt|;
comment|// Length of data in fRawInput array.
specifier|private
name|InputStream
name|fInputStream
init|=
literal|null
decl_stmt|;
comment|// User's input stream, or null if the user
comment|/*      *  Constructor.  Implementation internal      */
name|CharsetMatch
parameter_list|(
name|CharsetDetector
name|det
parameter_list|,
name|CharsetRecognizer
name|rec
parameter_list|,
name|int
name|conf
parameter_list|)
block|{
name|fRecognizer
operator|=
name|rec
expr_stmt|;
name|fConfidence
operator|=
name|conf
expr_stmt|;
comment|// The references to the original aplication input data must be copied out
comment|//   of the charset recognizer to here, in case the application resets the
comment|//   recognizer before using this CharsetMatch.
if|if
condition|(
name|det
operator|.
name|fInputStream
operator|==
literal|null
condition|)
block|{
comment|// We only want the existing input byte data if it came straight from the user,
comment|//   not if is just the head of a stream.
name|fRawInput
operator|=
name|det
operator|.
name|fRawInput
expr_stmt|;
name|fRawLength
operator|=
name|det
operator|.
name|fRawLength
expr_stmt|;
block|}
name|fInputStream
operator|=
name|det
operator|.
name|fInputStream
expr_stmt|;
block|}
comment|/**      * Create a java.io.Reader for reading the Unicode character data corresponding      * to the original byte data supplied to the Charset detect operation.      *<p/>      * CAUTION:  if the source of the byte data was an InputStream, a Reader      * can be created for only one matching char set using this method.  If more      * than one charset needs to be tried, the caller will need to reset      * the InputStream and create InputStreamReaders itself, based on the charset name.      *      * @return the Reader for the Unicode character data.      *      * @stable ICU 3.4      */
specifier|public
name|Reader
name|getReader
parameter_list|()
block|{
name|InputStream
name|inputStream
init|=
name|fInputStream
decl_stmt|;
if|if
condition|(
name|inputStream
operator|==
literal|null
condition|)
block|{
name|inputStream
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|fRawInput
argument_list|,
literal|0
argument_list|,
name|fRawLength
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|inputStream
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
operator|new
name|InputStreamReader
argument_list|(
name|inputStream
argument_list|,
name|getName
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Create a Java String from Unicode character data corresponding      * to the original byte data supplied to the Charset detect operation.      *      * @return a String created from the converted input data.      *      * @stable ICU 3.4      */
specifier|public
name|String
name|getString
parameter_list|()
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
return|return
name|getString
argument_list|(
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**      * Create a Java String from Unicode character data corresponding      * to the original byte data supplied to the Charset detect operation.      * The length of the returned string is limited to the specified size;      * the string will be trunctated to this length if necessary.  A limit value of      * zero or less is ignored, and treated as no limit.      *      * @param maxLength The maximium length of the String to be created when the      *                  source of the data is an input stream, or -1 for      *                  unlimited length.      * @return a String created from the converted input data.      *      * @stable ICU 3.4      */
specifier|public
name|String
name|getString
parameter_list|(
name|int
name|maxLength
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fInputStream
operator|!=
literal|null
condition|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
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
name|Reader
name|reader
init|=
name|getReader
argument_list|()
decl_stmt|;
name|int
name|max
init|=
name|maxLength
operator|<
literal|0
condition|?
name|Integer
operator|.
name|MAX_VALUE
else|:
name|maxLength
decl_stmt|;
name|int
name|bytesRead
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|bytesRead
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|max
argument_list|,
literal|1024
argument_list|)
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|max
operator|-=
name|bytesRead
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|String
argument_list|(
name|fRawInput
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Get an indication of the confidence in the charset detected.      * Confidence values range from 0-100, with larger numbers indicating      * a better match of the input data to the characteristics of the      * charset.      *      * @return the confidence in the charset match      *      * @stable ICU 3.4      */
specifier|public
name|int
name|getConfidence
parameter_list|()
block|{
return|return
name|fConfidence
return|;
block|}
comment|/**      * Return flags indicating what it was about the input data      * that caused this charset to be considered as a possible match.      * The result is a bitfield containing zero or more of the flags      * ENCODING_SCHEME, BOM, DECLARED_ENCODING, and LANG_STATISTICS.      * A result of zero means no information is available.      *<p>      * Note: currently, this method always returns zero.      *<p>      *      * @return the type of match found for this charset.      *      * @draft ICU 3.4      * @provisional This API might change or be removed in a future release.      */
specifier|public
name|int
name|getMatchType
parameter_list|()
block|{
comment|//      TODO: create a list of enum-like constants for common combinations of types of matches.
return|return
literal|0
return|;
block|}
comment|/**      * Get the name of the detected charset.      * The name will be one that can be used with other APIs on the      * platform that accept charset names.  It is the "Canonical name"      * as defined by the class java.nio.charset.Charset; for      * charsets that are registered with the IANA charset registry,      * this is the MIME-preferred registerd name.      *      * @see java.nio.charset.Charset      * @see java.io.InputStreamReader      *      * @return The name of the charset.      *      * @stable ICU 3.4      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|fRecognizer
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**      * Get the ISO code for the language of the detected charset.      *      * @return The ISO code for the language or<code>null</code> if the language cannot be determined.      *      * @stable ICU 3.4      */
specifier|public
name|String
name|getLanguage
parameter_list|()
block|{
return|return
name|fRecognizer
operator|.
name|getLanguage
argument_list|()
return|;
block|}
comment|/**      * Compare to other CharsetMatch objects.      * Comparison is based on the match confidence value, which      *   allows CharsetDetector.detectAll() to order its results.      *      * @param o the CharsetMatch object to compare against.      * @return a negative integer, zero, or a positive integer as the      *          confidence level of this CharsetMatch      *          is less than, equal to, or greater than that of      *          the argument.      * @throws ClassCastException if the argument is not a CharsetMatch.      * @stable ICU 3.4      */
specifier|public
name|int
name|compareTo
parameter_list|(
name|CharsetMatch
name|other
parameter_list|)
block|{
name|int
name|compareResult
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|fConfidence
operator|>
name|other
operator|.
name|fConfidence
condition|)
block|{
name|compareResult
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|fConfidence
operator|<
name|other
operator|.
name|fConfidence
condition|)
block|{
name|compareResult
operator|=
operator|-
literal|1
expr_stmt|;
block|}
return|return
name|compareResult
return|;
block|}
comment|/**      * compare this CharsetMatch to another based on confidence value      * @param o the CharsetMatch object to compare against      * @return true if equal      */
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
name|o
operator|instanceof
name|CharsetMatch
condition|)
block|{
name|CharsetMatch
name|that
init|=
operator|(
name|CharsetMatch
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|fConfidence
operator|==
name|that
operator|.
name|fConfidence
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * generates a hashCode based on the confidence value      * @return the hashCode      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|fConfidence
return|;
block|}
comment|//   gave us a byte array.
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
literal|"Match of "
operator|+
name|fRecognizer
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|fRecognizer
operator|.
name|getLanguage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|s
operator|+=
literal|" in "
operator|+
name|fRecognizer
operator|.
name|getLanguage
argument_list|()
expr_stmt|;
block|}
name|s
operator|+=
literal|" with confidence "
operator|+
name|fConfidence
expr_stmt|;
return|return
name|s
return|;
block|}
block|}
end_class

end_unit

