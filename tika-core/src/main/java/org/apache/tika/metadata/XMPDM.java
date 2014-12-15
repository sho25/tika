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
name|metadata
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * XMP Dynamic Media schema. This is a collection of  * {@link Property property definition} constants for the dynamic media  * properties defined in the XMP standard.  *  * @since Apache Tika 0.7  * @see<a href="http://wwwimages.adobe.com/content/dam/Adobe/en/devnet/xmp/pdfs/cc-201306/XMPSpecificationPart2.pdf"  *>XMP Specification, Part 2: Standard Schemas</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|XMPDM
block|{
comment|/**      * "The absolute path to the file's peak audio file. If empty, no peak      * file exists."      */
name|Property
name|ABS_PEAK_AUDIO_FILE_PATH
init|=
name|Property
operator|.
name|internalURI
argument_list|(
literal|"xmpDM:absPeakAudioFilePath"
argument_list|)
decl_stmt|;
comment|/**      * "The name of the album."      */
name|Property
name|ALBUM
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:album"
argument_list|)
decl_stmt|;
comment|/**      * "An alternative tape name, set via the project window or timecode      * dialog in Premiere. If an alternative name has been set and has not      * been reverted, that name is displayed."      */
name|Property
name|ALT_TAPE_NAME
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:altTapeName"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "A timecode set by the user. When specified, it is used instead
comment|//     * of the startTimecode."
comment|//     */
comment|//    Property ALT_TIMECODE = "xmpDM:altTimecode";
comment|/**      * "The name of the artist or artists."      */
name|Property
name|ARTIST
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:artist"
argument_list|)
decl_stmt|;
comment|/**      * "The name of the album artist or group for compilation albums."      */
name|Property
name|ALBUM_ARTIST
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:albumArtist"
argument_list|)
decl_stmt|;
comment|/**      * "The date and time when the audio was last modified."      */
name|Property
name|AUDIO_MOD_DATE
init|=
name|Property
operator|.
name|internalDate
argument_list|(
literal|"xmpDM:audioModDate"
argument_list|)
decl_stmt|;
comment|/**      * "The audio sample rate. Can be any value, but commonly 32000, 41100,      * or 48000."      */
name|Property
name|AUDIO_SAMPLE_RATE
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
literal|"xmpDM:audioSampleRate"
argument_list|)
decl_stmt|;
comment|/**      * "The audio sample type."      */
name|Property
name|AUDIO_SAMPLE_TYPE
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:audioSampleType"
argument_list|,
literal|"8Int"
argument_list|,
literal|"16Int"
argument_list|,
literal|"32Int"
argument_list|,
literal|"32Float"
argument_list|)
decl_stmt|;
comment|/**      * "The audio channel type."      */
name|Property
name|AUDIO_CHANNEL_TYPE
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:audioChannelType"
argument_list|,
literal|"Mono"
argument_list|,
literal|"Stereo"
argument_list|,
literal|"5.1"
argument_list|,
literal|"7.1"
argument_list|)
decl_stmt|;
comment|/**      * Converter for {@link XMPDM#AUDIO_CHANNEL_TYPE}      * @deprecated Experimental method, will change shortly      */
annotation|@
name|Deprecated
specifier|static
class|class
name|ChannelTypePropertyConverter
block|{
specifier|private
specifier|static
name|Property
name|property
init|=
name|AUDIO_CHANNEL_TYPE
decl_stmt|;
comment|/**         * How a standalone converter might work         */
specifier|public
specifier|static
name|String
name|convert
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
comment|// Assume already done
return|return
operator|(
name|String
operator|)
name|value
return|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Integer
condition|)
block|{
name|int
name|channelCount
init|=
operator|(
name|Integer
operator|)
name|value
decl_stmt|;
if|if
condition|(
name|channelCount
operator|==
literal|1
condition|)
block|{
return|return
literal|"Mono"
return|;
block|}
elseif|else
if|if
condition|(
name|channelCount
operator|==
literal|2
condition|)
block|{
return|return
literal|"Stereo"
return|;
block|}
elseif|else
if|if
condition|(
name|channelCount
operator|==
literal|5
condition|)
block|{
return|return
literal|"5.1"
return|;
block|}
elseif|else
if|if
condition|(
name|channelCount
operator|==
literal|7
condition|)
block|{
return|return
literal|"7.1"
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**         * How convert+set might work         */
specifier|public
specifier|static
name|void
name|convertAndSet
parameter_list|(
name|Metadata
name|metadata
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|Integer
operator|||
name|value
operator|instanceof
name|Long
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|property
argument_list|,
name|convert
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|Date
condition|)
block|{
comment|// Won't happen in this case, just an example of already
comment|//  converted to a type metadata.set(property) handles
name|metadata
operator|.
name|set
argument_list|(
name|property
argument_list|,
operator|(
name|Date
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
comment|// Already converted, or so we hope!
name|metadata
operator|.
name|set
argument_list|(
name|property
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * "The audio compression used. For example, MP3."      */
name|Property
name|AUDIO_COMPRESSOR
init|=
name|Property
operator|.
name|internalText
argument_list|(
literal|"xmpDM:audioCompressor"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "Additional parameters for Beat Splice stretch mode."
comment|//     */
comment|//    Property BEAT_SPLICE_PARAMS = "xmpDM:beatSpliceParams";
comment|/**      * "An album created by various artists."      */
name|Property
name|COMPILATION
init|=
name|Property
operator|.
name|externalInteger
argument_list|(
literal|"xmpDM:compilation"
argument_list|)
decl_stmt|;
comment|/**      * "The composer's name."      */
name|Property
name|COMPOSER
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:composer"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "An unordered list of all media used to create this media."
comment|//     */
comment|//    Property CONTRIBUTED_MEDIA = "xmpDM:contributedMedia";
comment|/**      * "The copyright information."      */
name|Property
name|COPYRIGHT
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:copyright"
argument_list|)
decl_stmt|;
comment|/**      * "The disc number for part of an album set."      */
name|Property
name|DISC_NUMBER
init|=
name|Property
operator|.
name|externalInteger
argument_list|(
literal|"xmpDM:discNumber"
argument_list|)
decl_stmt|;
comment|/**      * "The duration of the media file."      */
name|Property
name|DURATION
init|=
name|Property
operator|.
name|externalReal
argument_list|(
literal|"xmpDM:duration"
argument_list|)
decl_stmt|;
comment|/**      * "The engineer's name."      */
name|Property
name|ENGINEER
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:engineer"
argument_list|)
decl_stmt|;
comment|/**      * "The file data rate in megabytes per second. For example:      * '36/10' = 3.6 MB/sec"      */
name|Property
name|FILE_DATA_RATE
init|=
name|Property
operator|.
name|internalRational
argument_list|(
literal|"xmpDM:fileDataRate"
argument_list|)
decl_stmt|;
comment|/**      * "The name of the genre."      */
name|Property
name|GENRE
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:genre"
argument_list|)
decl_stmt|;
comment|/**      * "The musical instrument."      */
name|Property
name|INSTRUMENT
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:instrument"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "The duration of lead time for queuing music."
comment|//     */
comment|//    Property INTRO_TIME = "xmpDM:introTime";
comment|/**      * "The audio's musical key."      */
name|Property
name|KEY
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:key"
argument_list|,
literal|"C"
argument_list|,
literal|"C#"
argument_list|,
literal|"D"
argument_list|,
literal|"D#"
argument_list|,
literal|"E"
argument_list|,
literal|"F"
argument_list|,
literal|"F#"
argument_list|,
literal|"G"
argument_list|,
literal|"G#"
argument_list|,
literal|"A"
argument_list|,
literal|"A#"
argument_list|,
literal|"B"
argument_list|)
decl_stmt|;
comment|/**      * "User's log comments."      */
name|Property
name|LOG_COMMENT
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:logComment"
argument_list|)
decl_stmt|;
comment|/**      * "When true, the clip can be looped seamlessly."      */
name|Property
name|LOOP
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
literal|"xmpDM:loop"
argument_list|)
decl_stmt|;
comment|/**      * "The number of beats."      */
name|Property
name|NUMBER_OF_BEATS
init|=
name|Property
operator|.
name|internalReal
argument_list|(
literal|"xmpDM:numberOfBeats"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * An ordered list of markers. See also {@link #TRACKS xmpDM:Tracks}.
comment|//     */
comment|//    Property MARKERS = "xmpDM:markers";
comment|/**      * "The date and time when the metadata was last modified."      */
name|Property
name|METADATA_MOD_DATE
init|=
name|Property
operator|.
name|internalDate
argument_list|(
literal|"xmpDM:metadataModDate"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "The time at which to fade out."
comment|//     */
comment|//    Property OUT_CUE = "xmpDM:outCue";
comment|//    /**
comment|//     * "A reference to the project that created this file."
comment|//     */
comment|//    Property PROJECT_REF = "xmpDM:projectRef";
comment|/**      * "The sampling phase of film to be converted to video (pull-down)."      */
name|Property
name|PULL_DOWN
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:pullDown"
argument_list|,
literal|"WSSWW"
argument_list|,
literal|"SSWWW"
argument_list|,
literal|"SWWWS"
argument_list|,
literal|"WWWSS"
argument_list|,
literal|"WWSSW"
argument_list|,
literal|"WSSWW_24p"
argument_list|,
literal|"SSWWW_24p"
argument_list|,
literal|"SWWWS_24p"
argument_list|,
literal|"WWWSS_24p"
argument_list|,
literal|"WWSSW_24p"
argument_list|)
decl_stmt|;
comment|/**      * "The relative path to the file's peak audio file. If empty, no peak      * file exists."      */
name|Property
name|RELATIVE_PEAK_AUDIO_FILE_PATH
init|=
name|Property
operator|.
name|internalURI
argument_list|(
literal|"xmpDM:relativePeakAudioFilePath"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "The start time of the media inside the audio project."
comment|//     */
comment|//    Property RELATIVE_TIMESTAMP = "xmpDM:relativeTimestamp";
comment|/**      * "The date the title was released."      */
name|Property
name|RELEASE_DATE
init|=
name|Property
operator|.
name|externalDate
argument_list|(
literal|"xmpDM:releaseDate"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "Additional parameters for Resample stretch mode."
comment|//     */
comment|//    Property RESAMPLE_PARAMS = "xmpDM:resampleParams";
comment|/**      * "The musical scale used in the music. 'Neither' is most often used      * for instruments with no associated scale, such as drums."      */
name|Property
name|SCALE_TYPE
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:scaleType"
argument_list|,
literal|"Major"
argument_list|,
literal|"Minor"
argument_list|,
literal|"Both"
argument_list|,
literal|"Neither"
argument_list|)
decl_stmt|;
comment|/**      * "The name of the scene."      */
name|Property
name|SCENE
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:scene"
argument_list|)
decl_stmt|;
comment|/**      * "The date and time when the video was shot."      */
name|Property
name|SHOT_DATE
init|=
name|Property
operator|.
name|externalDate
argument_list|(
literal|"xmpDM:shotDate"
argument_list|)
decl_stmt|;
comment|/**      * "The name of the location where the video was shot. For example:      * 'Oktoberfest, Munich, Germany'. For more accurate  positioning,      * use the EXIF GPS values."      */
name|Property
name|SHOT_LOCATION
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:shotLocation"
argument_list|)
decl_stmt|;
comment|/**      * "The name of the shot or take."      */
name|Property
name|SHOT_NAME
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:shotName"
argument_list|)
decl_stmt|;
comment|/**      * "A description of the speaker angles from center front in degrees.      * For example: 'Left = -30, Right = 30, Center = 0, LFE = 45,      * Left Surround = -110, Right Surround = 110'"      */
name|Property
name|SPEAKER_PLACEMENT
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:speakerPlacement"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "The timecode of the first frame of video in the file, as obtained
comment|//     * from the device control."
comment|//     */
comment|//    Property START_TIMECODE = "xmpDM:startTimecode";
comment|/**      * "The audio stretch mode."      */
name|Property
name|STRETCH_MODE
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:stretchMode"
argument_list|,
literal|"Fixed length"
argument_list|,
literal|"Time-Scale"
argument_list|,
literal|"Resample"
argument_list|,
literal|"Beat Splice"
argument_list|,
literal|"Hybrid"
argument_list|)
decl_stmt|;
comment|/**      * "The name of the tape from which the clip was captured, as set during      * the capture process."      */
name|Property
name|TAPE_NAME
init|=
name|Property
operator|.
name|externalText
argument_list|(
literal|"xmpDM:tapeName"
argument_list|)
decl_stmt|;
comment|/**      * "The audio's tempo."      */
name|Property
name|TEMPO
init|=
name|Property
operator|.
name|internalReal
argument_list|(
literal|"xmpDM:tempo"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "Additional parameters for Time-Scale stretch mode."
comment|//     */
comment|//    Property TIME_SCALE_PARAMS = "xmpDM:timeScaleParams";
comment|/**      * "The time signature of the music."      */
name|Property
name|TIME_SIGNATURE
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:timeSignature"
argument_list|,
literal|"2/4"
argument_list|,
literal|"3/4"
argument_list|,
literal|"4/4"
argument_list|,
literal|"5/4"
argument_list|,
literal|"7/4"
argument_list|,
literal|"6/8"
argument_list|,
literal|"9/8"
argument_list|,
literal|"12/8"
argument_list|,
literal|"other"
argument_list|)
decl_stmt|;
comment|/**      * "A numeric value indicating the order of the audio file within its      * original recording."      */
name|Property
name|TRACK_NUMBER
init|=
name|Property
operator|.
name|externalInteger
argument_list|(
literal|"xmpDM:trackNumber"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "An unordered list of tracks. A track is a named set of markers,
comment|//     * which can specify a frame rate for all markers in the set.
comment|//     * See also {@link #MARKERS xmpDM:markers}."
comment|//     */
comment|//    Property TRACKS = "xmpDM:Tracks";
comment|/**      * "The alpha mode."      */
name|Property
name|VIDEO_ALPHA_MODE
init|=
name|Property
operator|.
name|externalClosedChoise
argument_list|(
literal|"xmpDM:videoAlphaMode"
argument_list|,
literal|"straight"
argument_list|,
literal|"pre-multiplied"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "A color in CMYK or RGB to be used as the pre-multiple color when
comment|//     * alpha mode is pre-multiplied."
comment|//     */
comment|//    Property VIDEO_ALPHA_PREMULTIPLE_COLOR = "xmpDM:videoAlphaPremultipleColor";
comment|/**      * "When true, unity is clear, when false, it is opaque."      */
name|Property
name|VIDEO_ALPHA_UNITY_IS_TRANSPARENT
init|=
name|Property
operator|.
name|internalBoolean
argument_list|(
literal|"xmpDM:videoAlphaUnityIsTransparent"
argument_list|)
decl_stmt|;
comment|/**      * "The color space."      */
name|Property
name|VIDEO_COLOR_SPACE
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:videoColorSpace"
argument_list|,
literal|"sRGB"
argument_list|,
literal|"CCIR-601"
argument_list|,
literal|"CCIR-709"
argument_list|)
decl_stmt|;
comment|/**      * "Video compression used. For example, jpeg."      */
name|Property
name|VIDEO_COMPRESSOR
init|=
name|Property
operator|.
name|internalText
argument_list|(
literal|"xmpDM:videoCompressor"
argument_list|)
decl_stmt|;
comment|/**      * "The field order for video."      */
name|Property
name|VIDEO_FIELD_ORDER
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:videoFieldOrder"
argument_list|,
literal|"Upper"
argument_list|,
literal|"Lower"
argument_list|,
literal|"Progressive"
argument_list|)
decl_stmt|;
comment|/**      * "The video frame rate."      */
name|Property
name|VIDEO_FRAME_RATE
init|=
name|Property
operator|.
name|internalOpenChoise
argument_list|(
literal|"xmpDM:videoFrameRate"
argument_list|,
literal|"24"
argument_list|,
literal|"NTSC"
argument_list|,
literal|"PAL"
argument_list|)
decl_stmt|;
comment|//    /**
comment|//     * "The frame size. For example: w:720, h: 480, unit:pixels"
comment|//     */
comment|//    Property VIDEO_FRAME_SIZE = "xmpDM:videoFrameSize";
comment|/**      * "The date and time when the video was last modified."      */
name|Property
name|VIDEO_MOD_DATE
init|=
name|Property
operator|.
name|internalDate
argument_list|(
literal|"xmpDM:videoModDate"
argument_list|)
decl_stmt|;
comment|/**      * "The size in bits of each color component of a pixel. Standard      *  Windows 32-bit pixels have 8 bits per component."      */
name|Property
name|VIDEO_PIXEL_DEPTH
init|=
name|Property
operator|.
name|internalClosedChoise
argument_list|(
literal|"xmpDM:videoPixelDepth"
argument_list|,
literal|"8Int"
argument_list|,
literal|"16Int"
argument_list|,
literal|"32Int"
argument_list|,
literal|"32Float"
argument_list|)
decl_stmt|;
comment|/**      * "The aspect ratio, expressed as wd/ht. For example: '648/720' = 0.9"      */
name|Property
name|VIDEO_PIXEL_ASPECT_RATIO
init|=
name|Property
operator|.
name|internalRational
argument_list|(
literal|"xmpDM:videoPixelAspectRatio"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

