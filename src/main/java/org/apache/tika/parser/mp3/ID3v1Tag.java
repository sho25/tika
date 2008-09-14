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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * This class parses and represents a ID3v1 Tag.  *   * @see http://www.id3.org/ID3v1  */
end_comment

begin_class
specifier|public
class|class
name|ID3v1Tag
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
block|}
decl_stmt|;
specifier|private
name|String
name|title
decl_stmt|;
specifier|private
name|String
name|artist
decl_stmt|;
specifier|private
name|String
name|album
decl_stmt|;
specifier|private
name|String
name|year
decl_stmt|;
specifier|private
name|String
name|comment
decl_stmt|;
specifier|private
name|int
name|genre
decl_stmt|;
comment|/**      * Default private constructor.      *      * @param title   the title.      * @param artist  the artist.      * @param album   the album.      * @param year    the year.      * @param comment the comment.      * @param genre   the genre code.      */
specifier|private
name|ID3v1Tag
parameter_list|(
name|String
name|title
parameter_list|,
name|String
name|artist
parameter_list|,
name|String
name|album
parameter_list|,
name|String
name|year
parameter_list|,
name|String
name|comment
parameter_list|,
name|int
name|genre
parameter_list|)
block|{
name|this
operator|.
name|title
operator|=
name|title
expr_stmt|;
name|this
operator|.
name|artist
operator|=
name|artist
expr_stmt|;
name|this
operator|.
name|album
operator|=
name|album
expr_stmt|;
name|this
operator|.
name|year
operator|=
name|year
expr_stmt|;
name|this
operator|.
name|comment
operator|=
name|comment
expr_stmt|;
name|this
operator|.
name|genre
operator|=
name|genre
expr_stmt|;
block|}
specifier|public
name|String
name|getTitle
parameter_list|()
block|{
return|return
name|title
return|;
block|}
specifier|public
name|String
name|getArtist
parameter_list|()
block|{
return|return
name|artist
return|;
block|}
specifier|public
name|String
name|getAlbum
parameter_list|()
block|{
return|return
name|album
return|;
block|}
specifier|public
name|String
name|getYear
parameter_list|()
block|{
return|return
name|year
return|;
block|}
specifier|public
name|String
name|getComment
parameter_list|()
block|{
return|return
name|comment
return|;
block|}
specifier|public
name|int
name|getGenre
parameter_list|()
block|{
return|return
name|genre
return|;
block|}
specifier|public
name|String
name|getGenreAsString
parameter_list|()
block|{
if|if
condition|(
literal|0
operator|<=
name|genre
operator|&&
name|genre
operator|<
name|GENRES
operator|.
name|length
condition|)
block|{
return|return
name|GENRES
index|[
name|genre
index|]
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Create an<code>ID3v1Tag</code> from an<code>InputStream</code>.      *      * @param stream the<code>InputStream</code> to parse.      * @return a<code>ID3v1Tag</code> if ID3 v1 information is available, null otherwise.      * @throws IOException if the stream can not be read      */
specifier|public
specifier|static
name|ID3v1Tag
name|createID3v1Tag
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buffer
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
name|buffer
operator|.
name|length
operator|!=
literal|128
operator|||
name|buffer
index|[
literal|0
index|]
operator|!=
literal|'T'
operator|||
name|buffer
index|[
literal|0
index|]
operator|!=
literal|'A'
operator|||
name|buffer
index|[
literal|2
index|]
operator|!=
literal|'G'
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
name|tag
init|=
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|"ISO-8859-1"
argument_list|)
decl_stmt|;
name|String
name|title
init|=
name|StringUtils
operator|.
name|substring
argument_list|(
name|tag
argument_list|,
literal|3
argument_list|,
literal|33
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|artist
init|=
name|StringUtils
operator|.
name|substring
argument_list|(
name|tag
argument_list|,
literal|33
argument_list|,
literal|63
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|album
init|=
name|StringUtils
operator|.
name|substring
argument_list|(
name|tag
argument_list|,
literal|63
argument_list|,
literal|93
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|year
init|=
name|StringUtils
operator|.
name|substring
argument_list|(
name|tag
argument_list|,
literal|93
argument_list|,
literal|97
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|comment
init|=
name|StringUtils
operator|.
name|substring
argument_list|(
name|tag
argument_list|,
literal|97
argument_list|,
literal|127
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|int
name|genre
init|=
operator|new
name|Byte
argument_list|(
operator|(
name|byte
operator|)
name|tag
operator|.
name|charAt
argument_list|(
literal|127
argument_list|)
argument_list|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
comment|// Return new ID3v1Tag instance.
return|return
operator|new
name|ID3v1Tag
argument_list|(
name|title
argument_list|,
name|artist
argument_list|,
name|album
argument_list|,
name|year
argument_list|,
name|comment
argument_list|,
name|genre
argument_list|)
return|;
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

