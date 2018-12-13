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
name|mp4
package|;
end_package

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|IsoFile
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|Box
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|Container
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|FileTypeBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|MetaBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|MovieBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|MovieHeaderBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|SampleDescriptionBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|SampleTableBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|TrackBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|TrackHeaderBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|UserDataBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleItemListBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|coremedia
operator|.
name|iso
operator|.
name|boxes
operator|.
name|sampleentry
operator|.
name|AudioSampleEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|DataSource
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleAlbumBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleArtist2Box
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleArtistBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleCommentBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleCompilationBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleDiskNumberBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleEncoderBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleGenreBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleNameBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleRecordingYear2Box
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleTrackAuthorBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|AppleTrackNumberBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|mp4parser
operator|.
name|boxes
operator|.
name|apple
operator|.
name|Utf8AppleDataBox
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|ss
operator|.
name|formula
operator|.
name|functions
operator|.
name|T
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
name|TemporaryResources
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
name|Property
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
name|XMP
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
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|HashMap
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Parser for the MP4 media container format, as well as the older  *  QuickTime format that MP4 is based on.  *   * This uses the MP4Parser project from http://code.google.com/p/mp4parser/  *  to do the underlying parsing  */
end_comment

begin_class
specifier|public
class|class
name|MP4Parser
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
literal|84011216792285L
decl_stmt|;
comment|/** TODO Replace this with a 2dp Duration Property Converter */
specifier|private
specifier|static
specifier|final
name|DecimalFormat
name|DURATION_FORMAT
init|=
operator|(
name|DecimalFormat
operator|)
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
static|static
block|{
name|DURATION_FORMAT
operator|.
name|applyPattern
argument_list|(
literal|"0.0#"
argument_list|)
expr_stmt|;
block|}
comment|// Ensure this stays in Sync with the entries in tika-mimetypes.xml
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|MediaType
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|typesMap
init|=
operator|new
name|HashMap
argument_list|<
name|MediaType
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
comment|// All types should be 4 bytes long, space padded as needed
name|typesMap
operator|.
name|put
argument_list|(
name|MediaType
operator|.
name|audio
argument_list|(
literal|"mp4"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"M4A "
argument_list|,
literal|"M4B "
argument_list|,
literal|"F4A "
argument_list|,
literal|"F4B "
argument_list|)
argument_list|)
expr_stmt|;
name|typesMap
operator|.
name|put
argument_list|(
name|MediaType
operator|.
name|video
argument_list|(
literal|"3gpp"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"3ge6"
argument_list|,
literal|"3ge7"
argument_list|,
literal|"3gg6"
argument_list|,
literal|"3gp1"
argument_list|,
literal|"3gp2"
argument_list|,
literal|"3gp3"
argument_list|,
literal|"3gp4"
argument_list|,
literal|"3gp5"
argument_list|,
literal|"3gp6"
argument_list|,
literal|"3gs7"
argument_list|)
argument_list|)
expr_stmt|;
name|typesMap
operator|.
name|put
argument_list|(
name|MediaType
operator|.
name|video
argument_list|(
literal|"3gpp2"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"3g2a"
argument_list|,
literal|"3g2b"
argument_list|,
literal|"3g2c"
argument_list|)
argument_list|)
expr_stmt|;
name|typesMap
operator|.
name|put
argument_list|(
name|MediaType
operator|.
name|video
argument_list|(
literal|"mp4"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"mp41"
argument_list|,
literal|"mp42"
argument_list|)
argument_list|)
expr_stmt|;
name|typesMap
operator|.
name|put
argument_list|(
name|MediaType
operator|.
name|video
argument_list|(
literal|"x-m4v"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"M4V "
argument_list|,
literal|"M4VH"
argument_list|,
literal|"M4VP"
argument_list|)
argument_list|)
expr_stmt|;
name|typesMap
operator|.
name|put
argument_list|(
name|MediaType
operator|.
name|video
argument_list|(
literal|"quicktime"
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|typesMap
operator|.
name|put
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"mp4"
argument_list|)
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|unmodifiableSet
argument_list|(
name|typesMap
operator|.
name|keySet
argument_list|()
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
comment|// The MP4Parser library accepts either a File, or a byte array
comment|// As MP4 video files are typically large, always use a file to
comment|//  avoid OOMs that may occur with in-memory buffering
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
name|TikaInputStream
name|tstream
init|=
name|TikaInputStream
operator|.
name|get
argument_list|(
name|stream
argument_list|,
name|tmp
argument_list|)
decl_stmt|;
try|try
init|(
name|DataSource
name|dataSource
init|=
operator|new
name|DirectFileReadDataSource
argument_list|(
name|tstream
operator|.
name|getFile
argument_list|()
argument_list|)
init|)
block|{
try|try
init|(
name|IsoFile
name|isoFile
init|=
operator|new
name|IsoFile
argument_list|(
name|dataSource
argument_list|)
init|)
block|{
name|tmp
operator|.
name|addResource
argument_list|(
name|isoFile
argument_list|)
expr_stmt|;
comment|// Grab the file type box
name|FileTypeBox
name|fileType
init|=
name|getOrNull
argument_list|(
name|isoFile
argument_list|,
name|FileTypeBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileType
operator|!=
literal|null
condition|)
block|{
comment|// Identify the type
name|MediaType
name|type
init|=
name|MediaType
operator|.
name|application
argument_list|(
literal|"mp4"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|MediaType
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|e
range|:
name|typesMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|contains
argument_list|(
name|fileType
operator|.
name|getMajorBrand
argument_list|()
argument_list|)
condition|)
block|{
name|type
operator|=
name|e
operator|.
name|getKey
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"audio"
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|AUDIO_COMPRESSOR
argument_list|,
name|fileType
operator|.
name|getMajorBrand
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Some older QuickTime files lack the FileType
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"video/quicktime"
argument_list|)
expr_stmt|;
block|}
comment|// Get the main MOOV box
name|MovieBox
name|moov
init|=
name|getOrNull
argument_list|(
name|isoFile
argument_list|,
name|MovieBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|moov
operator|==
literal|null
condition|)
block|{
comment|// Bail out
return|return;
block|}
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
comment|// Pull out some information from the header box
name|MovieHeaderBox
name|mHeader
init|=
name|getOrNull
argument_list|(
name|moov
argument_list|,
name|MovieHeaderBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|mHeader
operator|!=
literal|null
condition|)
block|{
comment|// Get the creation and modification dates
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|mHeader
operator|.
name|getCreationTime
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
name|mHeader
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get the duration
name|double
name|durationSeconds
init|=
operator|(
operator|(
name|double
operator|)
name|mHeader
operator|.
name|getDuration
argument_list|()
operator|)
operator|/
name|mHeader
operator|.
name|getTimescale
argument_list|()
decl_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|DURATION
argument_list|,
name|DURATION_FORMAT
operator|.
name|format
argument_list|(
name|durationSeconds
argument_list|)
argument_list|)
expr_stmt|;
comment|// The timescale is normally the sampling rate
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|AUDIO_SAMPLE_RATE
argument_list|,
operator|(
name|int
operator|)
name|mHeader
operator|.
name|getTimescale
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Get some more information from the track header
comment|// TODO Decide how to handle multiple tracks
name|List
argument_list|<
name|TrackBox
argument_list|>
name|tb
init|=
name|moov
operator|.
name|getBoxes
argument_list|(
name|TrackBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|tb
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|TrackBox
name|track
init|=
name|tb
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|TrackHeaderBox
name|header
init|=
name|track
operator|.
name|getTrackHeaderBox
argument_list|()
decl_stmt|;
comment|// Get the creation and modification dates
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATED
argument_list|,
name|header
operator|.
name|getCreationTime
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|TikaCoreProperties
operator|.
name|MODIFIED
argument_list|,
name|header
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get the video with and height
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|IMAGE_WIDTH
argument_list|,
operator|(
name|int
operator|)
name|header
operator|.
name|getWidth
argument_list|()
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|IMAGE_LENGTH
argument_list|,
operator|(
name|int
operator|)
name|header
operator|.
name|getHeight
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get the sample information
name|SampleTableBox
name|samples
init|=
name|track
operator|.
name|getSampleTableBox
argument_list|()
decl_stmt|;
name|SampleDescriptionBox
name|sampleDesc
init|=
name|samples
operator|.
name|getSampleDescriptionBox
argument_list|()
decl_stmt|;
if|if
condition|(
name|sampleDesc
operator|!=
literal|null
condition|)
block|{
comment|// Look for the first Audio Sample, if present
name|AudioSampleEntry
name|sample
init|=
name|getOrNull
argument_list|(
name|sampleDesc
argument_list|,
name|AudioSampleEntry
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|sample
operator|!=
literal|null
condition|)
block|{
name|XMPDM
operator|.
name|ChannelTypePropertyConverter
operator|.
name|convertAndSet
argument_list|(
name|metadata
argument_list|,
name|sample
operator|.
name|getChannelCount
argument_list|()
argument_list|)
expr_stmt|;
comment|//metadata.set(XMPDM.AUDIO_SAMPLE_TYPE, sample.getSampleSize());    // TODO Num -> Type mapping
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|AUDIO_SAMPLE_RATE
argument_list|,
operator|(
name|int
operator|)
name|sample
operator|.
name|getSampleRate
argument_list|()
argument_list|)
expr_stmt|;
comment|//metadata.set(XMPDM.AUDIO_, sample.getSamplesPerPacket());
comment|//metadata.set(XMPDM.AUDIO_, sample.getBytesPerSample());
block|}
block|}
block|}
comment|// Get metadata from the User Data Box
name|UserDataBox
name|userData
init|=
name|getOrNull
argument_list|(
name|moov
argument_list|,
name|UserDataBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|userData
operator|!=
literal|null
condition|)
block|{
name|MetaBox
name|meta
init|=
name|getOrNull
argument_list|(
name|userData
argument_list|,
name|MetaBox
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Check for iTunes Metadata
comment|// See http://atomicparsley.sourceforge.net/mpeg-4files.html and
comment|//  http://code.google.com/p/mp4v2/wiki/iTunesMetadata for more on these
name|AppleItemListBox
name|apple
init|=
name|getOrNull
argument_list|(
name|meta
argument_list|,
name|AppleItemListBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|apple
operator|!=
literal|null
condition|)
block|{
comment|// Title
name|AppleNameBox
name|title
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleNameBox
operator|.
name|class
argument_list|)
decl_stmt|;
name|addMetadata
argument_list|(
name|TikaCoreProperties
operator|.
name|TITLE
argument_list|,
name|metadata
argument_list|,
name|title
argument_list|)
expr_stmt|;
comment|// Artist
name|AppleArtistBox
name|artist
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleArtistBox
operator|.
name|class
argument_list|)
decl_stmt|;
name|addMetadata
argument_list|(
name|TikaCoreProperties
operator|.
name|CREATOR
argument_list|,
name|metadata
argument_list|,
name|artist
argument_list|)
expr_stmt|;
name|addMetadata
argument_list|(
name|XMPDM
operator|.
name|ARTIST
argument_list|,
name|metadata
argument_list|,
name|artist
argument_list|)
expr_stmt|;
comment|// Album Artist
name|AppleArtist2Box
name|artist2
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleArtist2Box
operator|.
name|class
argument_list|)
decl_stmt|;
name|addMetadata
argument_list|(
name|XMPDM
operator|.
name|ALBUM_ARTIST
argument_list|,
name|metadata
argument_list|,
name|artist2
argument_list|)
expr_stmt|;
comment|// Album
name|AppleAlbumBox
name|album
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleAlbumBox
operator|.
name|class
argument_list|)
decl_stmt|;
name|addMetadata
argument_list|(
name|XMPDM
operator|.
name|ALBUM
argument_list|,
name|metadata
argument_list|,
name|album
argument_list|)
expr_stmt|;
comment|// Composer
name|AppleTrackAuthorBox
name|composer
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleTrackAuthorBox
operator|.
name|class
argument_list|)
decl_stmt|;
name|addMetadata
argument_list|(
name|XMPDM
operator|.
name|COMPOSER
argument_list|,
name|metadata
argument_list|,
name|composer
argument_list|)
expr_stmt|;
comment|// Genre
name|AppleGenreBox
name|genre
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleGenreBox
operator|.
name|class
argument_list|)
decl_stmt|;
name|addMetadata
argument_list|(
name|XMPDM
operator|.
name|GENRE
argument_list|,
name|metadata
argument_list|,
name|genre
argument_list|)
expr_stmt|;
comment|// Year
name|AppleRecordingYear2Box
name|year
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleRecordingYear2Box
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|year
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|RELEASE_DATE
argument_list|,
name|year
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Track number
name|AppleTrackNumberBox
name|trackNum
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleTrackNumberBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|trackNum
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|TRACK_NUMBER
argument_list|,
name|trackNum
operator|.
name|getA
argument_list|()
argument_list|)
expr_stmt|;
comment|//metadata.set(XMPDM.NUMBER_OF_TRACKS, trackNum.getB()); // TODO
block|}
comment|// Disc number
name|AppleDiskNumberBox
name|discNum
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleDiskNumberBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|discNum
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|DISC_NUMBER
argument_list|,
name|discNum
operator|.
name|getA
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Compilation
name|AppleCompilationBox
name|compilation
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleCompilationBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|compilation
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMPDM
operator|.
name|COMPILATION
argument_list|,
operator|(
name|int
operator|)
name|compilation
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Comment
name|AppleCommentBox
name|comment
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleCommentBox
operator|.
name|class
argument_list|)
decl_stmt|;
name|addMetadata
argument_list|(
name|XMPDM
operator|.
name|LOG_COMMENT
argument_list|,
name|metadata
argument_list|,
name|comment
argument_list|)
expr_stmt|;
comment|// Encoder
name|AppleEncoderBox
name|encoder
init|=
name|getOrNull
argument_list|(
name|apple
argument_list|,
name|AppleEncoderBox
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoder
operator|!=
literal|null
condition|)
block|{
name|metadata
operator|.
name|set
argument_list|(
name|XMP
operator|.
name|CREATOR_TOOL
argument_list|,
name|encoder
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// As text
for|for
control|(
name|Box
name|box
range|:
name|apple
operator|.
name|getBoxes
argument_list|()
control|)
block|{
if|if
condition|(
name|box
operator|instanceof
name|Utf8AppleDataBox
condition|)
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
operator|(
operator|(
name|Utf8AppleDataBox
operator|)
name|box
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// TODO Check for other kinds too
block|}
comment|// All done
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|tmp
operator|.
name|dispose
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|addMetadata
parameter_list|(
name|Property
name|prop
parameter_list|,
name|Metadata
name|m
parameter_list|,
name|Utf8AppleDataBox
name|metadata
parameter_list|)
block|{
if|if
condition|(
name|metadata
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|set
argument_list|(
name|prop
argument_list|,
name|metadata
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Box
parameter_list|>
name|T
name|getOrNull
parameter_list|(
name|Container
name|box
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|box
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|List
argument_list|<
name|T
argument_list|>
name|boxes
init|=
name|box
operator|.
name|getBoxes
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|boxes
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|boxes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

