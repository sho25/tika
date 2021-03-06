begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// © 2016 and later: Unicode, Inc. and others.
end_comment

begin_comment
comment|// License& terms of use: http://www.unicode.org/copyright.html#License
end_comment

begin_comment
comment|/* ******************************************************************************* * Copyright (C) 2005 - 2012, International Business Machines Corporation and  * * others. All Rights Reserved.                                                * ******************************************************************************* */
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
comment|/**  * class CharsetRecog_2022  part of the ICU charset detection imlementation.  * This is a superclass for the individual detectors for  * each of the detectable members of the ISO 2022 family  * of encodings.  *<p>  * The separate classes are nested within this class.  */
end_comment

begin_class
specifier|abstract
class|class
name|CharsetRecog_2022
extends|extends
name|CharsetRecognizer
block|{
comment|/**      * Matching function shared among the 2022 detectors JP, CN and KR      * Counts up the number of legal an unrecognized escape sequences in      * the sample of text, and computes a score based on the total number&      * the proportion that fit the encoding.      *      * @param text            the byte buffer containing text to analyse      * @param textLen         the size of the text in the byte.      * @param escapeSequences the byte escape sequences to test for.      * @return match quality, in the range of 0-100.      */
name|int
name|match
parameter_list|(
name|byte
index|[]
name|text
parameter_list|,
name|int
name|textLen
parameter_list|,
name|byte
index|[]
index|[]
name|escapeSequences
parameter_list|)
block|{
name|int
name|i
decl_stmt|,
name|j
decl_stmt|;
name|int
name|escN
decl_stmt|;
name|int
name|hits
init|=
literal|0
decl_stmt|;
name|int
name|misses
init|=
literal|0
decl_stmt|;
name|int
name|shifts
init|=
literal|0
decl_stmt|;
name|int
name|quality
decl_stmt|;
name|scanInput
label|:
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|textLen
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|text
index|[
name|i
index|]
operator|==
literal|0x1b
condition|)
block|{
name|checkEscapes
label|:
for|for
control|(
name|escN
operator|=
literal|0
init|;
name|escN
operator|<
name|escapeSequences
operator|.
name|length
condition|;
name|escN
operator|++
control|)
block|{
name|byte
index|[]
name|seq
init|=
name|escapeSequences
index|[
name|escN
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|textLen
operator|-
name|i
operator|)
operator|<
name|seq
operator|.
name|length
condition|)
block|{
continue|continue
name|checkEscapes
continue|;
block|}
for|for
control|(
name|j
operator|=
literal|1
init|;
name|j
operator|<
name|seq
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|seq
index|[
name|j
index|]
operator|!=
name|text
index|[
name|i
operator|+
name|j
index|]
condition|)
block|{
continue|continue
name|checkEscapes
continue|;
block|}
block|}
name|hits
operator|++
expr_stmt|;
name|i
operator|+=
name|seq
operator|.
name|length
operator|-
literal|1
expr_stmt|;
continue|continue
name|scanInput
continue|;
block|}
name|misses
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|text
index|[
name|i
index|]
operator|==
literal|0x0e
operator|||
name|text
index|[
name|i
index|]
operator|==
literal|0x0f
condition|)
block|{
comment|// Shift in/out
name|shifts
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|hits
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|//
comment|// Initial quality is based on relative proportion of recongized vs.
comment|//   unrecognized escape sequences.
comment|//   All good:  quality = 100;
comment|//   half or less good: quality = 0;
comment|//   linear inbetween.
name|quality
operator|=
operator|(
literal|100
operator|*
name|hits
operator|-
literal|100
operator|*
name|misses
operator|)
operator|/
operator|(
name|hits
operator|+
name|misses
operator|)
expr_stmt|;
comment|// Back off quality if there were too few escape sequences seen.
comment|//   Include shifts in this computation, so that KR does not get penalized
comment|//   for having only a single Escape sequence, but many shifts.
if|if
condition|(
name|hits
operator|+
name|shifts
operator|<
literal|5
condition|)
block|{
name|quality
operator|-=
operator|(
literal|5
operator|-
operator|(
name|hits
operator|+
name|shifts
operator|)
operator|)
operator|*
literal|10
expr_stmt|;
block|}
if|if
condition|(
name|quality
operator|<
literal|0
condition|)
block|{
name|quality
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|quality
return|;
block|}
specifier|static
class|class
name|CharsetRecog_2022JP
extends|extends
name|CharsetRecog_2022
block|{
specifier|private
name|byte
index|[]
index|[]
name|escapeSequences
init|=
block|{
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x28
block|,
literal|0x43
block|}
block|,
comment|// KS X 1001:1992
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x28
block|,
literal|0x44
block|}
block|,
comment|// JIS X 212-1990
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x40
block|}
block|,
comment|// JIS C 6226-1978
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x41
block|}
block|,
comment|// GB 2312-80
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x42
block|}
block|,
comment|// JIS X 208-1983
block|{
literal|0x1b
block|,
literal|0x26
block|,
literal|0x40
block|}
block|,
comment|// JIS X 208 1990, 1997
block|{
literal|0x1b
block|,
literal|0x28
block|,
literal|0x42
block|}
block|,
comment|// ASCII
block|{
literal|0x1b
block|,
literal|0x28
block|,
literal|0x48
block|}
block|,
comment|// JIS-Roman
block|{
literal|0x1b
block|,
literal|0x28
block|,
literal|0x49
block|}
block|,
comment|// Half-width katakana
block|{
literal|0x1b
block|,
literal|0x28
block|,
literal|0x4a
block|}
block|,
comment|// JIS-Roman
block|{
literal|0x1b
block|,
literal|0x2e
block|,
literal|0x41
block|}
block|,
comment|// ISO 8859-1
block|{
literal|0x1b
block|,
literal|0x2e
block|,
literal|0x46
block|}
comment|// ISO 8859-7
block|}
decl_stmt|;
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"ISO-2022-JP"
return|;
block|}
name|CharsetMatch
name|match
parameter_list|(
name|CharsetDetector
name|det
parameter_list|)
block|{
name|int
name|confidence
init|=
name|match
argument_list|(
name|det
operator|.
name|fInputBytes
argument_list|,
name|det
operator|.
name|fInputLen
argument_list|,
name|escapeSequences
argument_list|)
decl_stmt|;
return|return
name|confidence
operator|==
literal|0
condition|?
literal|null
else|:
operator|new
name|CharsetMatch
argument_list|(
name|det
argument_list|,
name|this
argument_list|,
name|confidence
argument_list|)
return|;
block|}
block|}
specifier|static
class|class
name|CharsetRecog_2022KR
extends|extends
name|CharsetRecog_2022
block|{
specifier|private
name|byte
index|[]
index|[]
name|escapeSequences
init|=
block|{
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x29
block|,
literal|0x43
block|}
block|}
decl_stmt|;
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"ISO-2022-KR"
return|;
block|}
name|CharsetMatch
name|match
parameter_list|(
name|CharsetDetector
name|det
parameter_list|)
block|{
name|int
name|confidence
init|=
name|match
argument_list|(
name|det
operator|.
name|fInputBytes
argument_list|,
name|det
operator|.
name|fInputLen
argument_list|,
name|escapeSequences
argument_list|)
decl_stmt|;
return|return
name|confidence
operator|==
literal|0
condition|?
literal|null
else|:
operator|new
name|CharsetMatch
argument_list|(
name|det
argument_list|,
name|this
argument_list|,
name|confidence
argument_list|)
return|;
block|}
block|}
specifier|static
class|class
name|CharsetRecog_2022CN
extends|extends
name|CharsetRecog_2022
block|{
specifier|private
name|byte
index|[]
index|[]
name|escapeSequences
init|=
block|{
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x29
block|,
literal|0x41
block|}
block|,
comment|// GB 2312-80
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x29
block|,
literal|0x47
block|}
block|,
comment|// CNS 11643-1992 Plane 1
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x2A
block|,
literal|0x48
block|}
block|,
comment|// CNS 11643-1992 Plane 2
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x29
block|,
literal|0x45
block|}
block|,
comment|// ISO-IR-165
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x2B
block|,
literal|0x49
block|}
block|,
comment|// CNS 11643-1992 Plane 3
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x2B
block|,
literal|0x4A
block|}
block|,
comment|// CNS 11643-1992 Plane 4
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x2B
block|,
literal|0x4B
block|}
block|,
comment|// CNS 11643-1992 Plane 5
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x2B
block|,
literal|0x4C
block|}
block|,
comment|// CNS 11643-1992 Plane 6
block|{
literal|0x1b
block|,
literal|0x24
block|,
literal|0x2B
block|,
literal|0x4D
block|}
block|,
comment|// CNS 11643-1992 Plane 7
block|{
literal|0x1b
block|,
literal|0x4e
block|}
block|,
comment|// SS2
block|{
literal|0x1b
block|,
literal|0x4f
block|}
block|,
comment|// SS3
block|}
decl_stmt|;
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"ISO-2022-CN"
return|;
block|}
name|CharsetMatch
name|match
parameter_list|(
name|CharsetDetector
name|det
parameter_list|)
block|{
name|int
name|confidence
init|=
name|match
argument_list|(
name|det
operator|.
name|fInputBytes
argument_list|,
name|det
operator|.
name|fInputLen
argument_list|,
name|escapeSequences
argument_list|)
decl_stmt|;
return|return
name|confidence
operator|==
literal|0
condition|?
literal|null
else|:
operator|new
name|CharsetMatch
argument_list|(
name|det
argument_list|,
name|this
argument_list|,
name|confidence
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

