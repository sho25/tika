begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * ******************************************************************************  * Copyright (C) 2005, International Business Machines Corporation and         *  * others. All Rights Reserved.                                                *  * ******************************************************************************  */
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

begin_comment
comment|/**  * Abstract class for recognizing a single charset.  * Part of the implementation of ICU's CharsetDetector.  *  * Each specific charset that can be recognized will have an instance  * of some subclass of this class.  All interaction between the overall  * CharsetDetector and the stuff specific to an individual charset happens  * via the interface provided here.  *  * Instances of CharsetDetector DO NOT have or maintain   * state pertaining to a specific match or detect operation.  * The WILL be shared by multiple instances of CharsetDetector.  * They encapsulate const charset-specific information.  *  * @internal  */
end_comment

begin_class
specifier|abstract
class|class
name|CharsetRecognizer
block|{
comment|/**      * Get the IANA name of this charset.      * @return the charset name.      */
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Get the ISO language code for this charset.      * @return the language code, or<code>null</code> if the language cannot be determined.      */
specifier|public
name|String
name|getLanguage
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Test the match of this charset with the input text data      *      which is obtained via the CharsetDetector object.      *      * @param det  The CharsetDetector, which contains the input text      *             to be checked for being in this charset.      * @return Two values packed into one int  (Damn java, anyhow)      *<br/>      *             bits 0-7:  the match confidence, ranging from 0-100      *<br/>      *             bits 8-15: The match reason, an enum-like value.      */
specifier|abstract
name|int
name|match
parameter_list|(
name|CharsetDetector
name|det
parameter_list|)
function_decl|;
block|}
end_class

end_unit

