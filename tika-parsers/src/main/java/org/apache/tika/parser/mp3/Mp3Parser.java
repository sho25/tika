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
name|io
operator|.
name|UnsupportedEncodingException
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
name|Parser
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
comment|/**  * The<code>Mp3Parser</code> is used to parse ID3 Version 1 Tag information  * from an MP3 file, if available.  *  * @see<a href="http://www.id3.org/ID3v1">MP3 ID3 Version 1 specification</a>  */
end_comment

begin_class
specifier|public
class|class
name|Mp3Parser
implements|implements
name|Parser
block|{
comment|/**      * List of predefined genres.      *      * @see http://www.id3.org/id3v2-00      */
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|GENRES
init|=
operator|new
name|String
index|[]
block|{
comment|/*  0 */
literal|"Blues"
block|,
comment|/*  1 */
literal|"Classic Rock"
block|,
comment|/*  2 */
literal|"Country"
block|,
comment|/*  3 */
literal|"Dance"
block|,
comment|/*  4 */
literal|"Disco"
block|,
comment|/*  5 */
literal|"Funk"
block|,
comment|/*  6 */
literal|"Grunge"
block|,
comment|/*  7 */
literal|"Hip-Hop"
block|,
comment|/*  8 */
literal|"Jazz"
block|,
comment|/*  9 */
literal|"Metal"
block|,
comment|/* 10 */
literal|"New Age"
block|,
comment|/* 11 */
literal|"Oldies"
block|,
comment|/* 12 */
literal|"Other"
block|,
comment|/* 13 */
literal|"Pop"
block|,
comment|/* 14 */
literal|"R&B"
block|,
comment|/* 15 */
literal|"Rap"
block|,
comment|/* 16 */
literal|"Reggae"
block|,
comment|/* 17 */
literal|"Rock"
block|,
comment|/* 18 */
literal|"Techno"
block|,
comment|/* 19 */
literal|"Industrial"
block|,
comment|/* 20 */
literal|"Alternative"
block|,
comment|/* 21 */
literal|"Ska"
block|,
comment|/* 22 */
literal|"Death Metal"
block|,
comment|/* 23 */
literal|"Pranks"
block|,
comment|/* 24 */
literal|"Soundtrack"
block|,
comment|/* 25 */
literal|"Euro-Techno"
block|,
comment|/* 26 */
literal|"Ambient"
block|,
comment|/* 27 */
literal|"Trip-Hop"
block|,
comment|/* 28 */
literal|"Vocal"
block|,
comment|/* 29 */
literal|"Jazz+Funk"
block|,
comment|/* 30 */
literal|"Fusion"
block|,
comment|/* 31 */
literal|"Trance"
block|,
comment|/* 32 */
literal|"Classical"
block|,
comment|/* 33 */
literal|"Instrumental"
block|,
comment|/* 34 */
literal|"Acid"
block|,
comment|/* 35 */
literal|"House"
block|,
comment|/* 36 */
literal|"Game"
block|,
comment|/* 37 */
literal|"Sound Clip"
block|,
comment|/* 38 */
literal|"Gospel"
block|,
comment|/* 39 */
literal|"Noise"
block|,
comment|/* 40 */
literal|"AlternRock"
block|,
comment|/* 41 */
literal|"Bass"
block|,
comment|/* 42 */
literal|"Soul"
block|,
comment|/* 43 */
literal|"Punk"
block|,
comment|/* 44 */
literal|"Space"
block|,
comment|/* 45 */
literal|"Meditative"
block|,
comment|/* 46 */
literal|"Instrumental Pop"
block|,
comment|/* 47 */
literal|"Instrumental Rock"
block|,
comment|/* 48 */
literal|"Ethnic"
block|,
comment|/* 49 */
literal|"Gothic"
block|,
comment|/* 50 */
literal|"Darkwave"
block|,
comment|/* 51 */
literal|"Techno-Industrial"
block|,
comment|/* 52 */
literal|"Electronic"
block|,
comment|/* 53 */
literal|"Pop-Folk"
block|,
comment|/* 54 */
literal|"Eurodance"
block|,
comment|/* 55 */
literal|"Dream"
block|,
comment|/* 56 */
literal|"Southern Rock"
block|,
comment|/* 57 */
literal|"Comedy"
block|,
comment|/* 58 */
literal|"Cult"
block|,
comment|/* 59 */
literal|"Gangsta"
block|,
comment|/* 60 */
literal|"Top 40"
block|,
comment|/* 61 */
literal|"Christian Rap"
block|,
comment|/* 62 */
literal|"Pop/Funk"
block|,
comment|/* 63 */
literal|"Jungle"
block|,
comment|/* 64 */
literal|"Native American"
block|,
comment|/* 65 */
literal|"Cabaret"
block|,
comment|/* 66 */
literal|"New Wave"
block|,
comment|/* 67 */
literal|"Psychadelic"
block|,
comment|/* 68 */
literal|"Rave"
block|,
comment|/* 69 */
literal|"Showtunes"
block|,
comment|/* 70 */
literal|"Trailer"
block|,
comment|/* 71 */
literal|"Lo-Fi"
block|,
comment|/* 72 */
literal|"Tribal"
block|,
comment|/* 73 */
literal|"Acid Punk"
block|,
comment|/* 74 */
literal|"Acid Jazz"
block|,
comment|/* 75 */
literal|"Polka"
block|,
comment|/* 76 */
literal|"Retro"
block|,
comment|/* 77 */
literal|"Musical"
block|,
comment|/* 78 */
literal|"Rock& Roll"
block|,
comment|/* 79 */
literal|"Hard Rock"
block|,
comment|/* 80 */
literal|"Folk"
block|,
comment|/* 81 */
literal|"Folk-Rock"
block|,
comment|/* 82 */
literal|"National Folk"
block|,
comment|/* 83 */
literal|"Swing"
block|,
comment|/* 84 */
literal|"Fast Fusion"
block|,
comment|/* 85 */
literal|"Bebob"
block|,
comment|/* 86 */
literal|"Latin"
block|,
comment|/* 87 */
literal|"Revival"
block|,
comment|/* 88 */
literal|"Celtic"
block|,
comment|/* 89 */
literal|"Bluegrass"
block|,
comment|/* 90 */
literal|"Avantgarde"
block|,
comment|/* 91 */
literal|"Gothic Rock"
block|,
comment|/* 92 */
literal|"Progressive Rock"
block|,
comment|/* 93 */
literal|"Psychedelic Rock"
block|,
comment|/* 94 */
literal|"Symphonic Rock"
block|,
comment|/* 95 */
literal|"Slow Rock"
block|,
comment|/* 96 */
literal|"Big Band"
block|,
comment|/* 97 */
literal|"Chorus"
block|,
comment|/* 98 */
literal|"Easy Listening"
block|,
comment|/* 99 */
literal|"Acoustic"
block|,
comment|/* 100 */
literal|"Humour"
block|,
comment|/* 101 */
literal|"Speech"
block|,
comment|/* 102 */
literal|"Chanson"
block|,
comment|/* 103 */
literal|"Opera"
block|,
comment|/* 104 */
literal|"Chamber Music"
block|,
comment|/* 105 */
literal|"Sonata"
block|,
comment|/* 106 */
literal|"Symphony"
block|,
comment|/* 107 */
literal|"Booty Bass"
block|,
comment|/* 108 */
literal|"Primus"
block|,
comment|/* 109 */
literal|"Porn Groove"
block|,
comment|/* 110 */
literal|"Satire"
block|,
comment|/* 111 */
literal|"Slow Jam"
block|,
comment|/* 112 */
literal|"Club"
block|,
comment|/* 113 */
literal|"Tango"
block|,
comment|/* 114 */
literal|"Samba"
block|,
comment|/* 115 */
literal|"Folklore"
block|,
comment|/* 116 */
literal|"Ballad"
block|,
comment|/* 117 */
literal|"Power Ballad"
block|,
comment|/* 118 */
literal|"Rhythmic Soul"
block|,
comment|/* 119 */
literal|"Freestyle"
block|,
comment|/* 120 */
literal|"Duet"
block|,
comment|/* 121 */
literal|"Punk Rock"
block|,
comment|/* 122 */
literal|"Drum Solo"
block|,
comment|/* 123 */
literal|"A capella"
block|,
comment|/* 124 */
literal|"Euro-House"
block|,
comment|/* 125 */
literal|"Dance Hall"
block|,
comment|/* sentinel */
literal|""
block|}
decl_stmt|;
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
name|byte
index|[]
name|tag
init|=
name|getSuffix
argument_list|(
name|stream
argument_list|,
literal|128
argument_list|)
decl_stmt|;
if|if
condition|(
name|tag
operator|.
name|length
operator|==
literal|128
operator|&&
name|tag
index|[
literal|0
index|]
operator|==
literal|'T'
operator|&&
name|tag
index|[
literal|1
index|]
operator|==
literal|'A'
operator|&&
name|tag
index|[
literal|2
index|]
operator|==
literal|'G'
condition|)
block|{
name|String
name|title
init|=
name|getString
argument_list|(
name|tag
argument_list|,
literal|3
argument_list|,
literal|33
argument_list|)
decl_stmt|;
name|String
name|artist
init|=
name|getString
argument_list|(
name|tag
argument_list|,
literal|33
argument_list|,
literal|63
argument_list|)
decl_stmt|;
name|String
name|album
init|=
name|getString
argument_list|(
name|tag
argument_list|,
literal|63
argument_list|,
literal|93
argument_list|)
decl_stmt|;
name|String
name|year
init|=
name|getString
argument_list|(
name|tag
argument_list|,
literal|93
argument_list|,
literal|97
argument_list|)
decl_stmt|;
name|String
name|comment
init|=
name|getString
argument_list|(
name|tag
argument_list|,
literal|97
argument_list|,
literal|127
argument_list|)
decl_stmt|;
name|int
name|genre
init|=
operator|(
name|int
operator|)
name|tag
index|[
literal|127
index|]
operator|&
literal|0xff
decl_stmt|;
comment|// unsigned byte
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|TITLE
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|metadata
operator|.
name|set
argument_list|(
name|Metadata
operator|.
name|AUTHOR
argument_list|,
name|artist
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|artist
argument_list|)
expr_stmt|;
comment|// ID3v1.1 Track addition
comment|// If the last two bytes of the comment field are zero and
comment|// non-zero, then the last byte is the track number
if|if
condition|(
name|tag
index|[
literal|125
index|]
operator|==
literal|0
operator|&&
name|tag
index|[
literal|126
index|]
operator|!=
literal|0
condition|)
block|{
name|int
name|track
init|=
operator|(
name|int
operator|)
name|tag
index|[
literal|126
index|]
operator|&
literal|0xff
decl_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|album
operator|+
literal|", track "
operator|+
name|track
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|album
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|year
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|comment
argument_list|)
expr_stmt|;
name|xhtml
operator|.
name|element
argument_list|(
literal|"p"
argument_list|,
name|GENRES
index|[
name|Math
operator|.
name|min
argument_list|(
name|genre
argument_list|,
name|GENRES
operator|.
name|length
operator|-
literal|1
argument_list|)
index|]
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/**      * @deprecated This method will be removed in Apache Tika 1.0.      */
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
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|TikaException
block|{
name|parse
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|,
name|metadata
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the identified ISO-8859-1 substring from the given byte buffer.      * The return value is the zero-terminated substring retrieved from      * between the given start and end positions in the given byte buffer.      * Extra whitespace (and control characters) from the beginning and the      * end of the substring is removed.      *      * @param buffer byte buffer      * @param start start index of the substring      * @param end end index of the substring      * @return the identified substring      * @throws TikaException if the ISO-8859-1 encoding is not available      */
specifier|private
specifier|static
name|String
name|getString
parameter_list|(
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|TikaException
block|{
comment|// Find the zero byte that marks the end of the string
name|int
name|zero
init|=
name|start
decl_stmt|;
while|while
condition|(
name|zero
operator|<
name|end
operator|&&
name|buffer
index|[
name|zero
index|]
operator|!=
literal|0
condition|)
block|{
name|zero
operator|++
expr_stmt|;
block|}
comment|// Skip trailing whitespace
name|end
operator|=
name|zero
expr_stmt|;
while|while
condition|(
name|start
operator|<
name|end
operator|&&
name|buffer
index|[
name|end
operator|-
literal|1
index|]
operator|<=
literal|' '
condition|)
block|{
name|end
operator|--
expr_stmt|;
block|}
comment|// Skip leading whitespace
while|while
condition|(
name|start
operator|<
name|end
operator|&&
name|buffer
index|[
name|start
index|]
operator|<=
literal|' '
condition|)
block|{
name|start
operator|++
expr_stmt|;
block|}
comment|// Return the remaining substring
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|,
literal|"ISO-8859-1"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TikaException
argument_list|(
literal|"ISO-8859-1 encoding is not available"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Reads and returns the last<code>length</code> bytes from the      * given stream.      * @param stream input stream      * @param length number of bytes from the end to read and return      * @return stream the<code>InputStream</code> to read from.      * @throws IOException if the stream could not be read from.      */
specifier|private
specifier|static
name|byte
index|[]
name|getSuffix
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|2
operator|*
name|length
index|]
decl_stmt|;
name|int
name|bytesInBuffer
init|=
literal|0
decl_stmt|;
name|int
name|n
init|=
name|stream
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
while|while
condition|(
name|n
operator|!=
operator|-
literal|1
condition|)
block|{
name|bytesInBuffer
operator|+=
name|n
expr_stmt|;
if|if
condition|(
name|bytesInBuffer
operator|==
name|buffer
operator|.
name|length
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bytesInBuffer
operator|-
name|length
argument_list|,
name|buffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|bytesInBuffer
operator|=
name|length
expr_stmt|;
block|}
name|n
operator|=
name|stream
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|bytesInBuffer
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|bytesInBuffer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bytesInBuffer
operator|<
name|length
condition|)
block|{
name|length
operator|=
name|bytesInBuffer
expr_stmt|;
block|}
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|bytesInBuffer
operator|-
name|length
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

