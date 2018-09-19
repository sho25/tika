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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|exception
operator|.
name|TikaException
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
name|TailStream
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
name|TikaCoreProperties
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
name|XMPDM
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
name|mime
operator|.
name|MediaType
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
name|AbstractParser
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
name|parser
operator|.
name|mp3
operator|.
name|ID3Tags
operator|.
name|ID3Comment
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
name|sax
operator|.
name|XHTMLContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * The<code>Mp3Parser</code> is used to parse ID3 Version 1 Tag information  * from an MP3 file, if available.  *  * @see<a href="http://www.id3.org/ID3v1">MP3 ID3 Version 1 specification</a>  * @see<a href="http://www.id3.org/id3v2.4.0-structure">MP3 ID3 Version 2.4 Structure Specification</a>  * @see<a href="http://www.id3.org/id3v2.4.0-frames">MP3 ID3 Version 2.4 Frames Specification</a>  */
end_comment

begin_class
specifier|public
class|class
name|Mp3Parser
extends|extends
name|AbstractParser
block|{
comment|/** Serial version UID */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|8537074922934844370L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|MediaType
argument_list|>
name|SUPPORTED_TYPES
init|=
name|Collections
operator|.
name|singleton
argument_list|(
name|MediaType
operator|.
name|audio
argument_list|(
literal|"mpeg"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|Set
argument_list|<
name|MediaType
argument_list|>
name|getSupportedTypes
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
return|return
name|SUPPORTED_TYPES
return|;
block|}
specifier|public
name|void
name|parse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"audio/mpeg"
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|AUDIO_COMPRESSOR
argument_list|,
literal|"MP3"
argument_list|)
expr_stmt|;
name|XHTMLContentHandler
name|xhtml
init|=
operator|new
name|XHTMLContentHandler
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
comment|// Create handlers for the various kinds of ID3 tags
name|ID3TagsAndAudio
name|audioAndTags
init|=
name|getAllTagHandlers
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|)
decl_stmt|;
comment|// Process tags metadata if the file has supported tags
if|if
condition|(
name|audioAndTags
operator|.
name|tags
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|CompositeTagHandler
name|tag
init|=
operator|new
name|CompositeTagHandler
argument_list|(
name|audioAndTags
operator|.
name|tags
argument_list|)
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|tag
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|,
name|tag
operator|.
name|getArtist
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|ARTIST
argument_list|,
name|tag
operator|.
name|getArtist
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|ALBUM_ARTIST
argument_list|,
name|tag
operator|.
name|getAlbumArtist
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|COMPOSER
argument_list|,
name|tag
operator|.
name|getComposer
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|ALBUM
argument_list|,
name|tag
operator|.
name|getAlbum
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|COMPILATION
argument_list|,
name|tag
operator|.
name|getCompilation
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|RELEASE_DATE
argument_list|,
name|tag
operator|.
name|getYear
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|GENRE
argument_list|,
name|tag
operator|.
name|getGenre
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|comments
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ID3Comment
name|comment
range|:
name|tag
operator|.
name|getComments
argument_list|()
control|)
block|{
name|StringBuffer
name|cmt
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|comment
operator|.
name|getLanguage
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cmt
operator|.
name|append
argument_list|(
name|comment
operator|.
name|getLanguage
argument_list|()
argument_list|)
expr_stmt|;
name|cmt
operator|.
name|append
argument_list|(
literal|" - "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|comment
operator|.
name|getDescription
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cmt
operator|.
name|append
argument_list|(
name|comment
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|comment
operator|.
name|getText
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cmt
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|comment
operator|.
name|getText
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cmt
operator|.
name|append
argument_list|(
name|comment
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|comments
operator|.
name|add
argument_list|(
name|cmt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|add
argument_list|(
name|XMPDM
operator|.
name|LOG_COMMENT
operator|.
name|getName
argument_list|()
argument_list|,
name|cmt
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|tag
operator|.
name|getTitle
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|tag
operator|.
name|getArtist
argument_list|()
argument_list|)
expr_stmt|;
comment|// ID3v1.1 Track addition
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
name|tag
operator|.
name|getAlbum
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|tag
operator|.
name|getTrackNumber
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", track "
argument_list|)
operator|.
name|append
argument_list|(
name|tag
operator|.
name|getTrackNumber
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|TRACK_NUMBER
argument_list|,
name|tag
operator|.
name|getTrackNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tag
operator|.
name|getDisc
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", disc "
argument_list|)
operator|.
name|append
argument_list|(
name|tag
operator|.
name|getDisc
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|DISC_NUMBER
argument_list|,
name|tag
operator|.
name|getDisc
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|tag
operator|.
name|getYear
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|tag
operator|.
name|getGenre
argument_list|()
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|audioAndTags
operator|.
name|duration
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|comment
range|:
name|comments
control|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|comment
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|audioAndTags
operator|.
name|duration
operator|>
literal|0
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|DURATION
argument_list|,
name|audioAndTags
operator|.
name|duration
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|audioAndTags
operator|.
name|audio
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
literal|"samplerate"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|audioAndTags
operator|.
name|audio
operator|.
name|getSampleRate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"channels"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|audioAndTags
operator|.
name|audio
operator|.
name|getChannels
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
literal|"version"
argument_list|,
name|audioAndTags
operator|.
name|audio
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|AUDIO_SAMPLE_RATE
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|audioAndTags
operator|.
name|audio
operator|.
name|getSampleRate
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|audioAndTags
operator|.
name|audio
operator|.
name|getChannels
argument_list|()
operator|==
literal|1
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|AUDIO_CHANNEL_TYPE
argument_list|,
literal|"Mono"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|audioAndTags
operator|.
name|audio
operator|.
name|getChannels
argument_list|()
operator|==
literal|2
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|AUDIO_CHANNEL_TYPE
argument_list|,
literal|"Stereo"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|audioAndTags
operator|.
name|audio
operator|.
name|getChannels
argument_list|()
operator|==
literal|5
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|AUDIO_CHANNEL_TYPE
argument_list|,
literal|"5.1"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|audioAndTags
operator|.
name|audio
operator|.
name|getChannels
argument_list|()
operator|==
literal|7
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|AUDIO_CHANNEL_TYPE
argument_list|,
literal|"7.1"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|audioAndTags
operator|.
name|lyrics
operator|!=
literal|null
operator|&&
name|audioAndTags
operator|.
name|lyrics
operator|.
name|hasLyrics
argument_list|()
condition|)
block|{
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|,
literal|"class"
argument_list|,
literal|"lyrics"
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|characters
argument_list|(
name|audioAndTags
operator|.
name|lyrics
operator|.
name|lyricsText
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * Scans the MP3 frames for ID3 tags, and creates ID3Tag Handlers      *  for each supported set of tags.       */
specifier|protected
specifier|static
name|ID3TagsAndAudio
name|getAllTagHandlers
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|ID3v24Handler
name|v24
init|=
literal|null
decl_stmt|;
name|ID3v23Handler
name|v23
init|=
literal|null
decl_stmt|;
name|ID3v22Handler
name|v22
init|=
literal|null
decl_stmt|;
name|ID3v1Handler
name|v1
init|=
literal|null
decl_stmt|;
name|LyricsHandler
name|lyrics
init|=
literal|null
decl_stmt|;
name|AudioFrame
name|firstAudio
init|=
literal|null
decl_stmt|;
name|TailStream
name|tailStream
init|=
operator|new
name|TailStream
argument_list|(
name|stream
argument_list|,
literal|10240
operator|+
literal|128
argument_list|)
decl_stmt|;
name|MpegStream
name|mpegStream
init|=
operator|new
name|MpegStream
argument_list|(
name|tailStream
argument_list|)
decl_stmt|;
comment|// ID3v2 tags live at the start of the file
comment|// You can apparently have several different ID3 tag blocks
comment|// So, keep going until we don't find any more
name|MP3Frame
name|f
decl_stmt|;
while|while
condition|(
operator|(
name|f
operator|=
name|ID3v2Frame
operator|.
name|createFrameIfPresent
argument_list|(
name|mpegStream
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|f
operator|instanceof
name|ID3v2Frame
condition|)
block|{
name|ID3v2Frame
name|id3F
init|=
operator|(
name|ID3v2Frame
operator|)
name|f
decl_stmt|;
if|if
condition|(
name|id3F
operator|.
name|getMajorVersion
argument_list|()
operator|==
literal|4
condition|)
block|{
name|v24
operator|=
operator|new
name|ID3v24Handler
argument_list|(
name|id3F
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|id3F
operator|.
name|getMajorVersion
argument_list|()
operator|==
literal|3
condition|)
block|{
name|v23
operator|=
operator|new
name|ID3v23Handler
argument_list|(
name|id3F
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|id3F
operator|.
name|getMajorVersion
argument_list|()
operator|==
literal|2
condition|)
block|{
name|v22
operator|=
operator|new
name|ID3v22Handler
argument_list|(
name|id3F
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Now iterate over all audio frames in the file
name|AudioFrame
name|frame
init|=
name|mpegStream
operator|.
name|nextFrame
argument_list|()
decl_stmt|;
name|float
name|duration
init|=
literal|0
decl_stmt|;
name|boolean
name|skipped
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|frame
operator|!=
literal|null
operator|&&
name|skipped
condition|)
block|{
name|duration
operator|+=
name|frame
operator|.
name|getDuration
argument_list|()
expr_stmt|;
if|if
condition|(
name|firstAudio
operator|==
literal|null
condition|)
block|{
name|firstAudio
operator|=
name|frame
expr_stmt|;
block|}
name|skipped
operator|=
name|mpegStream
operator|.
name|skipFrame
argument_list|()
expr_stmt|;
if|if
condition|(
name|skipped
condition|)
block|{
name|frame
operator|=
name|mpegStream
operator|.
name|nextFrame
argument_list|()
expr_stmt|;
block|}
block|}
comment|// ID3v1 tags live at the end of the file
comment|// Lyrics live just before ID3v1, at the end of the file
comment|// Search for both (handlers seek to the end for us)
name|lyrics
operator|=
operator|new
name|LyricsHandler
argument_list|(
name|tailStream
operator|.
name|getTail
argument_list|()
argument_list|)
expr_stmt|;
name|v1
operator|=
name|lyrics
operator|.
name|id3v1
expr_stmt|;
comment|// Go in order of preference
comment|// Currently, that's newest to oldest
name|List
argument_list|<
name|ID3Tags
argument_list|>
name|tags
init|=
operator|new
name|ArrayList
argument_list|<
name|ID3Tags
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|v24
operator|!=
literal|null
operator|&&
name|v24
operator|.
name|getTagsPresent
argument_list|()
condition|)
block|{
name|tags
operator|.
name|add
argument_list|(
name|v24
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|v23
operator|!=
literal|null
operator|&&
name|v23
operator|.
name|getTagsPresent
argument_list|()
condition|)
block|{
name|tags
operator|.
name|add
argument_list|(
name|v23
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|v22
operator|!=
literal|null
operator|&&
name|v22
operator|.
name|getTagsPresent
argument_list|()
condition|)
block|{
name|tags
operator|.
name|add
argument_list|(
name|v22
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|v1
operator|!=
literal|null
operator|&&
name|v1
operator|.
name|getTagsPresent
argument_list|()
condition|)
block|{
name|tags
operator|.
name|add
argument_list|(
name|v1
argument_list|)
expr_stmt|;
block|}
name|ID3TagsAndAudio
name|ret
init|=
operator|new
name|ID3TagsAndAudio
argument_list|()
decl_stmt|;
name|ret
operator|.
name|audio
operator|=
name|firstAudio
expr_stmt|;
name|ret
operator|.
name|lyrics
operator|=
name|lyrics
expr_stmt|;
name|ret
operator|.
name|tags
operator|=
name|tags
operator|.
name|toArray
argument_list|(
operator|new
name|ID3Tags
index|[
name|tags
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|ret
operator|.
name|duration
operator|=
name|duration
expr_stmt|;
return|return
name|ret
return|;
block|}
specifier|protected
specifier|static
class|class
name|ID3TagsAndAudio
block|{
specifier|private
name|ID3Tags
index|[]
name|tags
decl_stmt|;
specifier|private
name|AudioFrame
name|audio
decl_stmt|;
specifier|private
name|LyricsHandler
name|lyrics
decl_stmt|;
specifier|private
name|float
name|duration
decl_stmt|;
block|}
block|}
end_class

end_unit

