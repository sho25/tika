begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** ******************************************************************************* * Copyright (C) 2005 - 2007, International Business Machines Corporation and  * * others. All Rights Reserved.                                                * ******************************************************************************* */
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
comment|/**  * Charset recognizer for UTF-8  *  * @internal  */
end_comment

begin_class
class|class
name|CharsetRecog_UTF8
extends|extends
name|CharsetRecognizer
block|{
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"UTF-8"
return|;
block|}
comment|/* (non-Javadoc)      * @see com.ibm.icu.text.CharsetRecognizer#match(com.ibm.icu.text.CharsetDetector)      */
name|int
name|match
parameter_list|(
name|CharsetDetector
name|det
parameter_list|)
block|{
name|boolean
name|hasBOM
init|=
literal|false
decl_stmt|;
name|int
name|numValid
init|=
literal|0
decl_stmt|;
name|int
name|numInvalid
init|=
literal|0
decl_stmt|;
name|byte
name|input
index|[]
init|=
name|det
operator|.
name|fRawInput
decl_stmt|;
name|int
name|i
decl_stmt|;
name|int
name|trailBytes
init|=
literal|0
decl_stmt|;
name|int
name|confidence
decl_stmt|;
if|if
condition|(
name|det
operator|.
name|fRawLength
operator|>=
literal|3
operator|&&
operator|(
name|input
index|[
literal|0
index|]
operator|&
literal|0xFF
operator|)
operator|==
literal|0xef
operator|&&
operator|(
name|input
index|[
literal|1
index|]
operator|&
literal|0xFF
operator|)
operator|==
literal|0xbb
operator|&
operator|(
name|input
index|[
literal|2
index|]
operator|&
literal|0xFF
operator|)
operator|==
literal|0xbf
condition|)
block|{
name|hasBOM
operator|=
literal|true
expr_stmt|;
block|}
comment|// Scan for multi-byte sequences
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|det
operator|.
name|fRawLength
condition|;
name|i
operator|++
control|)
block|{
name|int
name|b
init|=
name|input
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
block|{
continue|continue;
comment|// ASCII
block|}
comment|// Hi bit on char found.  Figure out how long the sequence should be
if|if
condition|(
operator|(
name|b
operator|&
literal|0x0e0
operator|)
operator|==
literal|0x0c0
condition|)
block|{
name|trailBytes
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|b
operator|&
literal|0x0f0
operator|)
operator|==
literal|0x0e0
condition|)
block|{
name|trailBytes
operator|=
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|b
operator|&
literal|0x0f8
operator|)
operator|==
literal|0xf0
condition|)
block|{
name|trailBytes
operator|=
literal|3
expr_stmt|;
block|}
else|else
block|{
name|numInvalid
operator|++
expr_stmt|;
if|if
condition|(
name|numInvalid
operator|>
literal|5
condition|)
block|{
break|break;
block|}
name|trailBytes
operator|=
literal|0
expr_stmt|;
block|}
comment|// Verify that we've got the right number of trail bytes in the sequence
for|for
control|(
init|;
condition|;
control|)
block|{
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|det
operator|.
name|fRawLength
condition|)
block|{
break|break;
block|}
name|b
operator|=
name|input
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0xc0
operator|)
operator|!=
literal|0x080
condition|)
block|{
name|numInvalid
operator|++
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|--
name|trailBytes
operator|==
literal|0
condition|)
block|{
name|numValid
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|// Cook up some sort of confidence score, based on presense of a BOM
comment|//    and the existence of valid and/or invalid multi-byte sequences.
name|confidence
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|hasBOM
operator|&&
name|numInvalid
operator|==
literal|0
condition|)
block|{
name|confidence
operator|=
literal|100
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hasBOM
operator|&&
name|numValid
operator|>
name|numInvalid
operator|*
literal|10
condition|)
block|{
name|confidence
operator|=
literal|80
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numValid
operator|>
literal|3
operator|&&
name|numInvalid
operator|==
literal|0
condition|)
block|{
name|confidence
operator|=
literal|100
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numValid
operator|>
literal|0
operator|&&
name|numInvalid
operator|==
literal|0
condition|)
block|{
name|confidence
operator|=
literal|80
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numValid
operator|==
literal|0
operator|&&
name|numInvalid
operator|==
literal|0
condition|)
block|{
comment|// Plain ASCII.
name|confidence
operator|=
literal|10
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numValid
operator|>
name|numInvalid
operator|*
literal|10
condition|)
block|{
comment|// Probably corruput utf-8 data.  Valid sequences aren't likely by chance.
name|confidence
operator|=
literal|25
expr_stmt|;
block|}
return|return
name|confidence
return|;
block|}
block|}
end_class

end_unit

