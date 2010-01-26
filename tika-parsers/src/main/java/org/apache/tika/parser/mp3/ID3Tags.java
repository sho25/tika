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

begin_comment
comment|/**  * Interface that defines the common interface for ID3 tag parsers,  *  such as ID3v1 and ID3v2.3.  * Implementations should return NULL if the file lacks a given  *  tag, or if the tag isn't defined for the version.  *    * Note that so far, only the ID3v1 core tags are listed here. In  *  future, we may wish to add more to cover the extra tags that  *  our ID3v2 handlers can produce.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ID3Tags
block|{
comment|/**      * List of predefined genres.      *      * @see http://www.id3.org/id3v2-00      */
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
comment|/**      * Does the file contain this kind of tags?      */
name|boolean
name|getTagsPresent
parameter_list|()
function_decl|;
name|String
name|getTitle
parameter_list|()
function_decl|;
name|String
name|getArtist
parameter_list|()
function_decl|;
name|String
name|getAlbum
parameter_list|()
function_decl|;
name|String
name|getComment
parameter_list|()
function_decl|;
name|String
name|getGenre
parameter_list|()
function_decl|;
name|String
name|getYear
parameter_list|()
function_decl|;
name|String
name|getTrackNumber
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

