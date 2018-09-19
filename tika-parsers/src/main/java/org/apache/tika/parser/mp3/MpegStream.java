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
name|mp3
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|PushbackInputStream
import|;
end_import

begin_comment
comment|/**  *<p>  * A specialized stream class which can be used to extract single frames of MPEG  * audio files.  *</p>  *<p>  * Instances of this class are constructed with an underlying stream which  * should point to an audio file. Read operations are possible in the usual way.  * However, there are special methods for searching and extracting headers of  * MPEG frames. Some meta information of frames can be queried.  *</p>  */
end_comment

begin_class
class|class
name|MpegStream
extends|extends
name|PushbackInputStream
block|{
comment|/** Bit rate table for MPEG V1, layer 1. */
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|BIT_RATE_MPEG1_L1
init|=
block|{
literal|0
block|,
literal|32000
block|,
literal|64000
block|,
literal|96000
block|,
literal|128000
block|,
literal|160000
block|,
literal|192000
block|,
literal|224000
block|,
literal|256000
block|,
literal|288000
block|,
literal|320000
block|,
literal|352000
block|,
literal|384000
block|,
literal|416000
block|,
literal|448000
block|}
decl_stmt|;
comment|/** Bit rate table for MPEG V1, layer 2. */
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|BIT_RATE_MPEG1_L2
init|=
block|{
literal|0
block|,
literal|32000
block|,
literal|48000
block|,
literal|56000
block|,
literal|64000
block|,
literal|80000
block|,
literal|96000
block|,
literal|112000
block|,
literal|128000
block|,
literal|160000
block|,
literal|192000
block|,
literal|224000
block|,
literal|256000
block|,
literal|320000
block|,
literal|384000
block|}
decl_stmt|;
comment|/** Bit rate table for MPEG V1, layer 3. */
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|BIT_RATE_MPEG1_L3
init|=
block|{
literal|0
block|,
literal|32000
block|,
literal|40000
block|,
literal|48000
block|,
literal|56000
block|,
literal|64000
block|,
literal|80000
block|,
literal|96000
block|,
literal|112000
block|,
literal|128000
block|,
literal|160000
block|,
literal|192000
block|,
literal|224000
block|,
literal|256000
block|,
literal|320000
block|}
decl_stmt|;
comment|/** Bit rate table for MPEG V2/V2.5, layer 1. */
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|BIT_RATE_MPEG2_L1
init|=
block|{
literal|0
block|,
literal|32000
block|,
literal|48000
block|,
literal|56000
block|,
literal|64000
block|,
literal|80000
block|,
literal|96000
block|,
literal|112000
block|,
literal|128000
block|,
literal|144000
block|,
literal|160000
block|,
literal|176000
block|,
literal|192000
block|,
literal|224000
block|,
literal|256000
block|}
decl_stmt|;
comment|/** Bit rate table for MPEG V2/V2.5, layer 2 and 3. */
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|BIT_RATE_MPEG2_L2
init|=
block|{
literal|0
block|,
literal|8000
block|,
literal|16000
block|,
literal|24000
block|,
literal|32000
block|,
literal|40000
block|,
literal|48000
block|,
literal|56000
block|,
literal|64000
block|,
literal|80000
block|,
literal|96000
block|,
literal|112000
block|,
literal|128000
block|,
literal|144000
block|,
literal|160000
block|}
decl_stmt|;
comment|/** Sample rate table for MPEG V1. */
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|SAMPLE_RATE_MPEG1
init|=
block|{
literal|44100
block|,
literal|48000
block|,
literal|32000
block|}
decl_stmt|;
comment|/** Sample rate table for MPEG V2. */
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|SAMPLE_RATE_MPEG2
init|=
block|{
literal|22050
block|,
literal|24000
block|,
literal|16000
block|}
decl_stmt|;
comment|/** Sample rate table for MPEG V2.5. */
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|SAMPLE_RATE_MPEG2_5
init|=
block|{
literal|11025
block|,
literal|12000
block|,
literal|8000
block|}
decl_stmt|;
comment|/** Sample rate table for all MPEG versions. */
specifier|private
specifier|static
specifier|final
name|int
index|[]
index|[]
name|SAMPLE_RATE
init|=
name|createSampleRateTable
argument_list|()
decl_stmt|;
comment|/** Constant for the number of samples for a layer 1 frame. */
specifier|private
specifier|static
specifier|final
name|int
name|SAMPLE_COUNT_L1
init|=
literal|384
decl_stmt|;
comment|/** Constant for the number of samples for a layer 2 or 3 frame. */
specifier|private
specifier|static
specifier|final
name|int
name|SAMPLE_COUNT_L2
init|=
literal|1152
decl_stmt|;
comment|/** Constant for the size of an MPEG frame header in bytes. */
specifier|private
specifier|static
specifier|final
name|int
name|HEADER_SIZE
init|=
literal|4
decl_stmt|;
comment|/** The current MPEG header. */
specifier|private
name|AudioFrame
name|currentHeader
decl_stmt|;
comment|/** A flag whether the end of the stream is reached. */
specifier|private
name|boolean
name|endOfStream
decl_stmt|;
comment|/**      * Creates a new instance of {@code MpegStream} and initializes it with the      * underlying stream.      *       * @param in the underlying audio stream      */
specifier|public
name|MpegStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
literal|2
operator|*
name|HEADER_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Searches for the next MPEG frame header from the current stream position      * on. This method advances the underlying input stream until it finds a      * valid frame header or the end of the stream is reached. In the former      * case a corresponding {@code AudioFrame} object is created. In the latter      * case there are no more headers, so the end of the stream is probably      * reached.      *       * @return the next {@code AudioFrame} or<b>null</b>      * @throws IOException if an IO error occurs      */
specifier|public
name|AudioFrame
name|nextFrame
parameter_list|()
throws|throws
name|IOException
block|{
name|AudioFrame
name|frame
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|!
name|endOfStream
operator|&&
name|frame
operator|==
literal|null
condition|)
block|{
name|findFrameSyncByte
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|endOfStream
condition|)
block|{
name|HeaderBitField
name|headerField
init|=
name|createHeaderField
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|endOfStream
condition|)
block|{
name|frame
operator|=
name|createHeader
argument_list|(
name|headerField
argument_list|)
expr_stmt|;
if|if
condition|(
name|frame
operator|==
literal|null
condition|)
block|{
name|pushBack
argument_list|(
name|headerField
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|currentHeader
operator|=
name|frame
expr_stmt|;
return|return
name|frame
return|;
block|}
comment|/**      * Skips the current MPEG frame. This method can be called after a valid      * MPEG header has been retrieved using {@code nextFrame()}. In this case      * the underlying stream is advanced to the end of the associated MPEG      * frame or until the EOF is reached. The return value indicates      * whether the full frame could be skipped.      *       * @return<b>true</b> if a frame could be skipped,<b>false</b> otherwise, perhaps EOF?      * @throws IOException if an IO error occurs      */
specifier|public
name|boolean
name|skipFrame
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentHeader
operator|!=
literal|null
condition|)
block|{
name|long
name|toSkip
init|=
name|currentHeader
operator|.
name|getLength
argument_list|()
operator|-
name|HEADER_SIZE
decl_stmt|;
name|long
name|skipped
init|=
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|in
argument_list|,
name|toSkip
argument_list|)
decl_stmt|;
name|currentHeader
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|skipped
operator|<
name|toSkip
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Advances the underlying stream until the first byte of frame sync is      * found.      *       * @throws IOException if an error occurs      */
specifier|private
name|void
name|findFrameSyncByte
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|found
operator|&&
operator|!
name|endOfStream
condition|)
block|{
if|if
condition|(
name|nextByte
argument_list|()
operator|==
literal|0xFF
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Creates a bit field for the MPEG frame header.      *       * @return the bit field      * @throws IOException if an error occurs      */
specifier|private
name|HeaderBitField
name|createHeaderField
parameter_list|()
throws|throws
name|IOException
block|{
name|HeaderBitField
name|field
init|=
operator|new
name|HeaderBitField
argument_list|()
decl_stmt|;
name|field
operator|.
name|add
argument_list|(
name|nextByte
argument_list|()
argument_list|)
expr_stmt|;
name|field
operator|.
name|add
argument_list|(
name|nextByte
argument_list|()
argument_list|)
expr_stmt|;
name|field
operator|.
name|add
argument_list|(
name|nextByte
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|field
return|;
block|}
comment|/**      * Creates an {@code AudioFrame} object based on the given header field. If      * the header field contains invalid values, result is<b>null</b>.      *       * @param bits the header bit field      * @return the {@code AudioFrame}      */
specifier|private
name|AudioFrame
name|createHeader
parameter_list|(
name|HeaderBitField
name|bits
parameter_list|)
block|{
if|if
condition|(
name|bits
operator|.
name|get
argument_list|(
literal|21
argument_list|,
literal|23
argument_list|)
operator|!=
literal|7
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|mpegVer
init|=
name|bits
operator|.
name|get
argument_list|(
literal|19
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|int
name|layer
init|=
name|bits
operator|.
name|get
argument_list|(
literal|17
argument_list|,
literal|18
argument_list|)
decl_stmt|;
name|int
name|bitRateCode
init|=
name|bits
operator|.
name|get
argument_list|(
literal|12
argument_list|,
literal|15
argument_list|)
decl_stmt|;
name|int
name|sampleRateCode
init|=
name|bits
operator|.
name|get
argument_list|(
literal|10
argument_list|,
literal|11
argument_list|)
decl_stmt|;
name|int
name|padding
init|=
name|bits
operator|.
name|get
argument_list|(
literal|9
argument_list|)
decl_stmt|;
if|if
condition|(
name|mpegVer
operator|==
literal|1
operator|||
name|layer
operator|==
literal|0
operator|||
name|bitRateCode
operator|==
literal|0
operator|||
name|bitRateCode
operator|==
literal|15
operator|||
name|sampleRateCode
operator|==
literal|3
condition|)
block|{
comment|// invalid header values
return|return
literal|null
return|;
block|}
name|int
name|bitRate
init|=
name|calculateBitRate
argument_list|(
name|mpegVer
argument_list|,
name|layer
argument_list|,
name|bitRateCode
argument_list|)
decl_stmt|;
name|int
name|sampleRate
init|=
name|calculateSampleRate
argument_list|(
name|mpegVer
argument_list|,
name|sampleRateCode
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|calculateFrameLength
argument_list|(
name|layer
argument_list|,
name|bitRate
argument_list|,
name|sampleRate
argument_list|,
name|padding
argument_list|)
decl_stmt|;
name|float
name|duration
init|=
name|calculateDuration
argument_list|(
name|layer
argument_list|,
name|sampleRate
argument_list|)
decl_stmt|;
name|int
name|channels
init|=
name|calculateChannels
argument_list|(
name|bits
operator|.
name|get
argument_list|(
literal|6
argument_list|,
literal|7
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|AudioFrame
argument_list|(
name|mpegVer
argument_list|,
name|layer
argument_list|,
name|bitRate
argument_list|,
name|sampleRate
argument_list|,
name|channels
argument_list|,
name|length
argument_list|,
name|duration
argument_list|)
return|;
block|}
comment|/**      * Reads the next byte.      *       * @return the next byte      * @throws IOException if an error occurs      */
specifier|private
name|int
name|nextByte
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|endOfStream
condition|)
block|{
name|result
operator|=
name|read
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|==
operator|-
literal|1
condition|)
block|{
name|endOfStream
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|endOfStream
condition|?
literal|0
else|:
name|result
return|;
block|}
comment|/**      * Pushes the given header field back in the stream so that the bytes are      * read again. This method is called if an invalid header was detected. Then      * search has to continue at the next byte after the frame sync byte.      *       * @param field the header bit field with the invalid frame header      * @throws IOException if an error occurs      */
specifier|private
name|void
name|pushBack
parameter_list|(
name|HeaderBitField
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|unread
argument_list|(
name|field
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Calculates the bit rate based on the given parameters.      *       * @param mpegVer the MPEG version      * @param layer the layer      * @param code the code for the bit rate      * @return the bit rate in bits per second      */
specifier|private
specifier|static
name|int
name|calculateBitRate
parameter_list|(
name|int
name|mpegVer
parameter_list|,
name|int
name|layer
parameter_list|,
name|int
name|code
parameter_list|)
block|{
name|int
index|[]
name|arr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|mpegVer
operator|==
name|AudioFrame
operator|.
name|MPEG_V1
condition|)
block|{
switch|switch
condition|(
name|layer
condition|)
block|{
case|case
name|AudioFrame
operator|.
name|LAYER_1
case|:
name|arr
operator|=
name|BIT_RATE_MPEG1_L1
expr_stmt|;
break|break;
case|case
name|AudioFrame
operator|.
name|LAYER_2
case|:
name|arr
operator|=
name|BIT_RATE_MPEG1_L2
expr_stmt|;
break|break;
case|case
name|AudioFrame
operator|.
name|LAYER_3
case|:
name|arr
operator|=
name|BIT_RATE_MPEG1_L3
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
if|if
condition|(
name|layer
operator|==
name|AudioFrame
operator|.
name|LAYER_1
condition|)
block|{
name|arr
operator|=
name|BIT_RATE_MPEG2_L1
expr_stmt|;
block|}
else|else
block|{
name|arr
operator|=
name|BIT_RATE_MPEG2_L2
expr_stmt|;
block|}
block|}
return|return
name|arr
index|[
name|code
index|]
return|;
block|}
comment|/**      * Calculates the sample rate based on the given parameters.      *       * @param mpegVer the MPEG version      * @param code the code for the sample rate      * @return the sample rate in samples per second      */
specifier|private
specifier|static
name|int
name|calculateSampleRate
parameter_list|(
name|int
name|mpegVer
parameter_list|,
name|int
name|code
parameter_list|)
block|{
return|return
name|SAMPLE_RATE
index|[
name|mpegVer
index|]
index|[
name|code
index|]
return|;
block|}
comment|/**      * Calculates the length of an MPEG frame based on the given parameters.      *       * @param layer the layer      * @param bitRate the bit rate      * @param sampleRate the sample rate      * @param padding the padding flag      * @return the length of the frame in bytes      */
specifier|private
specifier|static
name|int
name|calculateFrameLength
parameter_list|(
name|int
name|layer
parameter_list|,
name|int
name|bitRate
parameter_list|,
name|int
name|sampleRate
parameter_list|,
name|int
name|padding
parameter_list|)
block|{
if|if
condition|(
name|layer
operator|==
name|AudioFrame
operator|.
name|LAYER_1
condition|)
block|{
return|return
operator|(
literal|12
operator|*
name|bitRate
operator|/
name|sampleRate
operator|+
name|padding
operator|)
operator|*
literal|4
return|;
block|}
else|else
block|{
return|return
literal|144
operator|*
name|bitRate
operator|/
name|sampleRate
operator|+
name|padding
return|;
block|}
block|}
comment|/**      * Calculates the duration of a MPEG frame based on the given parameters.      *       * @param layer the layer      * @param sampleRate the sample rate      * @return the duration of this frame in milliseconds      */
specifier|private
specifier|static
name|float
name|calculateDuration
parameter_list|(
name|int
name|layer
parameter_list|,
name|int
name|sampleRate
parameter_list|)
block|{
name|int
name|sampleCount
init|=
operator|(
name|layer
operator|==
name|AudioFrame
operator|.
name|LAYER_1
operator|)
condition|?
name|SAMPLE_COUNT_L1
else|:
name|SAMPLE_COUNT_L2
decl_stmt|;
return|return
operator|(
literal|1000.0f
operator|/
name|sampleRate
operator|)
operator|*
name|sampleCount
return|;
block|}
comment|/**      * Calculates the number of channels based on the given parameters.      *       * @param chan the code for the channels      * @return the number of channels      */
specifier|private
specifier|static
name|int
name|calculateChannels
parameter_list|(
name|int
name|chan
parameter_list|)
block|{
return|return
name|chan
operator|<
literal|3
condition|?
literal|2
else|:
literal|1
return|;
block|}
comment|/**      * Creates the complete array for the sample rate mapping.      *       * @return the table for the sample rates      */
specifier|private
specifier|static
name|int
index|[]
index|[]
name|createSampleRateTable
parameter_list|()
block|{
name|int
index|[]
index|[]
name|arr
init|=
operator|new
name|int
index|[
literal|4
index|]
index|[]
decl_stmt|;
name|arr
index|[
name|AudioFrame
operator|.
name|MPEG_V1
index|]
operator|=
name|SAMPLE_RATE_MPEG1
expr_stmt|;
name|arr
index|[
name|AudioFrame
operator|.
name|MPEG_V2
index|]
operator|=
name|SAMPLE_RATE_MPEG2
expr_stmt|;
name|arr
index|[
name|AudioFrame
operator|.
name|MPEG_V2_5
index|]
operator|=
name|SAMPLE_RATE_MPEG2_5
expr_stmt|;
return|return
name|arr
return|;
block|}
comment|/**      * A class representing the bit field of an MPEG header. It allows      * convenient access to specific bit groups.      */
specifier|private
specifier|static
class|class
name|HeaderBitField
block|{
comment|/** The internal value. */
specifier|private
name|int
name|value
decl_stmt|;
comment|/**          * Adds a byte to this field.          *           * @param b the byte to be added          */
specifier|public
name|void
name|add
parameter_list|(
name|int
name|b
parameter_list|)
block|{
name|value
operator|<<=
literal|8
expr_stmt|;
name|value
operator||=
name|b
expr_stmt|;
block|}
comment|/**          * Returns the value of the bit group from the given start and end          * index. E.g. ''from'' = 0, ''to'' = 3 will return the value of the          * first 4 bits.          *           * @param from index          * @param to the to index          * @return the value of this group of bits          */
specifier|public
name|int
name|get
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
name|int
name|shiftVal
init|=
name|value
operator|>>
name|from
decl_stmt|;
name|int
name|mask
init|=
operator|(
literal|1
operator|<<
operator|(
name|to
operator|-
name|from
operator|+
literal|1
operator|)
operator|)
operator|-
literal|1
decl_stmt|;
return|return
name|shiftVal
operator|&
name|mask
return|;
block|}
comment|/**          * Returns the value of the bit with the given index. The bit index is          * 0-based. Result is either 0 or 1, depending on the value of this bit.          *           * @param bit the bit index          * @return the value of this bit          */
specifier|public
name|int
name|get
parameter_list|(
name|int
name|bit
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|bit
argument_list|,
name|bit
argument_list|)
return|;
block|}
comment|/**          * Returns the internal value of this field as an array. The array          * contains 3 bytes.          *           * @return the internal value of this field as int array          */
specifier|public
name|byte
index|[]
name|toArray
parameter_list|()
block|{
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
literal|3
index|]
decl_stmt|;
name|result
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
name|get
argument_list|(
literal|16
argument_list|,
literal|23
argument_list|)
expr_stmt|;
name|result
index|[
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|get
argument_list|(
literal|8
argument_list|,
literal|15
argument_list|)
expr_stmt|;
name|result
index|[
literal|2
index|]
operator|=
operator|(
name|byte
operator|)
name|get
argument_list|(
literal|0
argument_list|,
literal|7
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

