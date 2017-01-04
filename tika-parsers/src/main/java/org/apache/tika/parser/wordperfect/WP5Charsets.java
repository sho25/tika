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
name|wordperfect
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * WordPerfect 5.x constant values used for mapping WordPerfect charsets to  * unicode equivalents when possible.  * @author Pascal Essiembre  */
end_comment

begin_class
specifier|final
class|class
name|WP5Charsets
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|WP5Charsets
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Extended character sets used when fixed-length multi-byte functions      * with a byte value of 192 (0xC0) are found in a WordPerfect document.      * Those character set codes may be specific to WordPerfect       * file specifications and may or may not be considered standard       * outside WordPerfect. Applies to version 5.x.      */
specifier|public
specifier|static
specifier|final
name|char
index|[]
index|[]
name|EXTENDED_CHARSETS
init|=
operator|new
name|char
index|[]
index|[]
block|{
comment|// WP Charset 0: ASCII (same as WP6)
name|WP6Charsets
operator|.
name|EXTENDED_CHARSETS
index|[
literal|0
index|]
block|,
comment|// WP Charset 1: Multinational 1 (same as WP6)
name|WP6Charsets
operator|.
name|EXTENDED_CHARSETS
index|[
literal|1
index|]
block|,
comment|// WP Charset 2: Multinational 2 (28 chars)
block|{
literal|'\u0323'
block|,
literal|'\u0324'
block|,
literal|'\u02da'
block|,
literal|'\u0325'
block|,
literal|'\u02bc'
block|,
literal|'\u032d'
block|,
literal|'\u2017'
block|,
literal|'\u005f'
block|,
literal|'\u0138'
block|,
literal|'\u032e'
block|,
literal|'\u033e'
block|,
literal|'\u2018'
block|,
literal|'\u0020'
block|,
literal|'\u02bd'
block|,
literal|'\u02db'
block|,
literal|'\u0327'
block|,
literal|'\u0321'
block|,
literal|'\u0322'
block|,
literal|'\u030d'
block|,
literal|'\u2019'
block|,
literal|'\u0329'
block|,
literal|'\u0020'
block|,
literal|'\u0621'
block|,
literal|'\u02be'
block|,
literal|'\u0306'
block|,
literal|'\u0310'
block|,
literal|'\u2032'
block|,
literal|'\u2034'
block|}
block|,
comment|// WP Charset 3: Box Drawing (same as WP6)
name|WP6Charsets
operator|.
name|EXTENDED_CHARSETS
index|[
literal|3
index|]
block|,
comment|// WP Charset 4: Typographic Symbols (same as WP6)
name|WP6Charsets
operator|.
name|EXTENDED_CHARSETS
index|[
literal|4
index|]
block|,
comment|// WP Charset 5: Iconic Symbol (35 chars)
block|{
literal|'\u2665'
block|,
literal|'\u2666'
block|,
literal|'\u2663'
block|,
literal|'\u2660'
block|,
literal|'\u2642'
block|,
literal|'\u2640'
block|,
literal|'\u263c'
block|,
literal|'\u263a'
block|,
literal|'\u263b'
block|,
literal|'\u266a'
block|,
literal|'\u266c'
block|,
literal|'\u25ac'
block|,
literal|'\u2302'
block|,
literal|'\u203c'
block|,
literal|'\u221a'
block|,
literal|'\u21a8'
block|,
literal|'\u2310'
block|,
literal|'\u2319'
block|,
literal|'\u25d8'
block|,
literal|'\u25d9'
block|,
literal|'\u21b5'
block|,
literal|'\u261e'
block|,
literal|'\u261c'
block|,
literal|'\u2713'
block|,
literal|'\u2610'
block|,
literal|'\u2612'
block|,
literal|'\u2639'
block|,
literal|'\u266f'
block|,
literal|'\u266d'
block|,
literal|'\u266e'
block|,
literal|'\u260e'
block|,
literal|'\u231a'
block|,
literal|'\u231b'
block|,
literal|'\u2104'
block|,
literal|'\u23b5'
block|}
block|,
comment|// WP Charset 6: Math/Scientific (same as WP6)
name|WP6Charsets
operator|.
name|EXTENDED_CHARSETS
index|[
literal|6
index|]
block|,
comment|// WP Charset 7 Math/Scientific Extended (same as WP6)
name|WP6Charsets
operator|.
name|EXTENDED_CHARSETS
index|[
literal|7
index|]
block|,
comment|// WP Charset 8: Greek (210 chars)
block|{
literal|'\u0391'
block|,
literal|'\u03b1'
block|,
literal|'\u0392'
block|,
literal|'\u03b2'
block|,
literal|'\u0392'
block|,
literal|'\u03d0'
block|,
literal|'\u0393'
block|,
literal|'\u03b3'
block|,
literal|'\u0394'
block|,
literal|'\u03b4'
block|,
literal|'\u0395'
block|,
literal|'\u03b5'
block|,
literal|'\u0396'
block|,
literal|'\u03b6'
block|,
literal|'\u0397'
block|,
literal|'\u03b7'
block|,
literal|'\u0398'
block|,
literal|'\u03b8'
block|,
literal|'\u0399'
block|,
literal|'\u03b9'
block|,
literal|'\u039a'
block|,
literal|'\u03ba'
block|,
literal|'\u039b'
block|,
literal|'\u03bb'
block|,
literal|'\u039c'
block|,
literal|'\u03bc'
block|,
literal|'\u039d'
block|,
literal|'\u03bd'
block|,
literal|'\u039e'
block|,
literal|'\u03be'
block|,
literal|'\u039f'
block|,
literal|'\u03bf'
block|,
literal|'\u03a0'
block|,
literal|'\u03c0'
block|,
literal|'\u03a1'
block|,
literal|'\u03c1'
block|,
literal|'\u03a3'
block|,
literal|'\u03c3'
block|,
literal|'\u03f9'
block|,
literal|'\u03db'
block|,
literal|'\u03a4'
block|,
literal|'\u03c4'
block|,
literal|'\u03a5'
block|,
literal|'\u03c5'
block|,
literal|'\u03a6'
block|,
literal|'\u03d5'
block|,
literal|'\u03a7'
block|,
literal|'\u03c7'
block|,
literal|'\u03a8'
block|,
literal|'\u03c8'
block|,
literal|'\u03a9'
block|,
literal|'\u03c9'
block|,
literal|'\u03ac'
block|,
literal|'\u03ad'
block|,
literal|'\u03ae'
block|,
literal|'\u03af'
block|,
literal|'\u03ca'
block|,
literal|'\u03cc'
block|,
literal|'\u03cd'
block|,
literal|'\u03cb'
block|,
literal|'\u03ce'
block|,
literal|'\u03b5'
block|,
literal|'\u03d1'
block|,
literal|'\u03f0'
block|,
literal|'\u03d6'
block|,
literal|'\u1fe5'
block|,
literal|'\u03d2'
block|,
literal|'\u03c6'
block|,
literal|'\u03c9'
block|,
literal|'\u037e'
block|,
literal|'\u0387'
block|,
literal|'\u0384'
block|,
literal|'\u00a8'
block|,
literal|'\u0385'
block|,
literal|'\u1fed'
block|,
literal|'\u1fef'
block|,
literal|'\u1fc0'
block|,
literal|'\u1fbd'
block|,
literal|'\u1fbf'
block|,
literal|'\u1fbe'
block|,
literal|'\u1fce'
block|,
literal|'\u1fde'
block|,
literal|'\u1fcd'
block|,
literal|'\u1fdd'
block|,
literal|'\u1fcf'
block|,
literal|'\u1fdf'
block|,
literal|'\u0384'
block|,
literal|'\u1fef'
block|,
literal|'\u1fc0'
block|,
literal|'\u1fbd'
block|,
literal|'\u1fbf'
block|,
literal|'\u1fce'
block|,
literal|'\u1fde'
block|,
literal|'\u1fcd'
block|,
literal|'\u1fdd'
block|,
literal|'\u1fcf'
block|,
literal|'\u1fdf'
block|,
literal|'\u1f70'
block|,
literal|'\u1fb6'
block|,
literal|'\u1fb3'
block|,
literal|'\u1fb4'
block|,
literal|'\u1fb7'
block|,
literal|'\u1f00'
block|,
literal|'\u1f04'
block|,
literal|'\u1f02'
block|,
literal|'\u1f06'
block|,
literal|'\u1f80'
block|,
literal|'\u1f84'
block|,
literal|'\u1f86'
block|,
literal|'\u1f01'
block|,
literal|'\u1f05'
block|,
literal|'\u1f03'
block|,
literal|'\u1f07'
block|,
literal|'\u1f81'
block|,
literal|'\u1f85'
block|,
literal|'\u1f87'
block|,
literal|'\u1f72'
block|,
literal|'\u1f10'
block|,
literal|'\u1f14'
block|,
literal|'\u1f13'
block|,
literal|'\u1f11'
block|,
literal|'\u1f15'
block|,
literal|'\u1f13'
block|,
literal|'\u1f74'
block|,
literal|'\u1fc6'
block|,
literal|'\u1fc3'
block|,
literal|'\u1fc4'
block|,
literal|'\u1fc2'
block|,
literal|'\u1fc7'
block|,
literal|'\u1f20'
block|,
literal|'\u1f24'
block|,
literal|'\u1f22'
block|,
literal|'\u1f26'
block|,
literal|'\u1f90'
block|,
literal|'\u1f94'
block|,
literal|'\u1f96'
block|,
literal|'\u1f21'
block|,
literal|'\u1f25'
block|,
literal|'\u1f23'
block|,
literal|'\u1f27'
block|,
literal|'\u1f91'
block|,
literal|'\u1f95'
block|,
literal|'\u1f97'
block|,
literal|'\u1f76'
block|,
literal|'\u1fd6'
block|,
literal|'\u0390'
block|,
literal|'\u1fd2'
block|,
literal|'\u1f30'
block|,
literal|'\u1f34'
block|,
literal|'\u1f32'
block|,
literal|'\u1f36'
block|,
literal|'\u1f31'
block|,
literal|'\u1f35'
block|,
literal|'\u1f33'
block|,
literal|'\u1f37'
block|,
literal|'\u1f78'
block|,
literal|'\u1f40'
block|,
literal|'\u1f44'
block|,
literal|'\u1f42'
block|,
literal|'\u1f41'
block|,
literal|'\u1f45'
block|,
literal|'\u1f43'
block|,
literal|'\u1f7a'
block|,
literal|'\u1fe6'
block|,
literal|'\u03b0'
block|,
literal|'\u1fe3'
block|,
literal|'\u1f50'
block|,
literal|'\u1f54'
block|,
literal|'\u1f52'
block|,
literal|'\u1f56'
block|,
literal|'\u1f51'
block|,
literal|'\u1f55'
block|,
literal|'\u1f53'
block|,
literal|'\u1f57'
block|,
literal|'\u1f7c'
block|,
literal|'\u1ff6'
block|,
literal|'\u1ff3'
block|,
literal|'\u1ff4'
block|,
literal|'\u1ff2'
block|,
literal|'\u1ff7'
block|,
literal|'\u1f60'
block|,
literal|'\u1f64'
block|,
literal|'\u1f62'
block|,
literal|'\u1f66'
block|,
literal|'\u1fa0'
block|,
literal|'\u1fa4'
block|,
literal|'\u1fa6'
block|,
literal|'\u1f61'
block|,
literal|'\u1f65'
block|,
literal|'\u1f63'
block|,
literal|'\u1f67'
block|,
literal|'\u1fa1'
block|,
literal|'\u1fa5'
block|,
literal|'\u1fa7'
block|,
literal|'\u0374'
block|,
literal|'\u0375'
block|,
literal|'\u03db'
block|,
literal|'\u03dd'
block|,
literal|'\u03d9'
block|,
literal|'\u03e1'
block|,
literal|'\u0386'
block|,
literal|'\u0388'
block|,
literal|'\u0389'
block|,
literal|'\u038a'
block|,
literal|'\u038c'
block|,
literal|'\u038e'
block|,
literal|'\u038f'
block|,
literal|'\u03aa'
block|,
literal|'\u03ab'
block|,
literal|'\u1fe5'
block|}
block|,
comment|// WP Charset 9: Hebrew (119 chars)
block|{
literal|'\u05d0'
block|,
literal|'\u05d1'
block|,
literal|'\u05d2'
block|,
literal|'\u05d3'
block|,
literal|'\u05d4'
block|,
literal|'\u05d5'
block|,
literal|'\u05d6'
block|,
literal|'\u05d7'
block|,
literal|'\u05d8'
block|,
literal|'\u05d9'
block|,
literal|'\u05da'
block|,
literal|'\u05db'
block|,
literal|'\u05dc'
block|,
literal|'\u05dd'
block|,
literal|'\u05de'
block|,
literal|'\u05df'
block|,
literal|'\u05e0'
block|,
literal|'\u05e1'
block|,
literal|'\u05e2'
block|,
literal|'\u05e3'
block|,
literal|'\u05e4'
block|,
literal|'\u05e5'
block|,
literal|'\u05e6'
block|,
literal|'\u05e7'
block|,
literal|'\u05e8'
block|,
literal|'\u05e9'
block|,
literal|'\u05ea'
block|,
literal|'\u05be'
block|,
literal|'\u05c0'
block|,
literal|'\u05c3'
block|,
literal|'\u05f3'
block|,
literal|'\u05f4'
block|,
literal|'\u05b0'
block|,
literal|'\u05b1'
block|,
literal|'\u05b2'
block|,
literal|'\u05b3'
block|,
literal|'\u05b4'
block|,
literal|'\u05b5'
block|,
literal|'\u05b6'
block|,
literal|'\u05b7'
block|,
literal|'\u05b8'
block|,
literal|'\u05b9'
block|,
literal|'\u05ba'
block|,
literal|'\u05bb'
block|,
literal|'\u05bc'
block|,
literal|'\u05bd'
block|,
literal|'\u05bf'
block|,
literal|'\u05b7'
block|,
literal|'\ufbe1'
block|,
literal|'\u05f0'
block|,
literal|'\u05f1'
block|,
literal|'\u05f2'
block|,
literal|'\u0591'
block|,
literal|'\u0596'
block|,
literal|'\u05ad'
block|,
literal|'\u05a4'
block|,
literal|'\u059a'
block|,
literal|'\u059b'
block|,
literal|'\u05a3'
block|,
literal|'\u05a5'
block|,
literal|'\u05a6'
block|,
literal|'\u05a7'
block|,
literal|'\u09aa'
block|,
literal|'\u0592'
block|,
literal|'\u0593'
block|,
literal|'\u0594'
block|,
literal|'\u0595'
block|,
literal|'\u0597'
block|,
literal|'\u0598'
block|,
literal|'\u0599'
block|,
literal|'\u05a8'
block|,
literal|'\u059c'
block|,
literal|'\u059d'
block|,
literal|'\u059e'
block|,
literal|'\u05a1'
block|,
literal|'\u05a9'
block|,
literal|'\u05a0'
block|,
literal|'\u059f'
block|,
literal|'\u05ab'
block|,
literal|'\u05ac'
block|,
literal|'\u05af'
block|,
literal|'\u05c4'
block|,
literal|'\u0544'
block|,
literal|'\u05d0'
block|,
literal|'\ufb31'
block|,
literal|'\ufb32'
block|,
literal|'\ufb33'
block|,
literal|'\ufb34'
block|,
literal|'\ufb35'
block|,
literal|'\ufb4b'
block|,
literal|'\ufb36'
block|,
literal|'\u05d7'
block|,
literal|'\ufb38'
block|,
literal|'\ufb39'
block|,
literal|'\ufb3b'
block|,
literal|'\ufb3a'
block|,
literal|'\u05da'
block|,
literal|'\u05da'
block|,
literal|'\u05da'
block|,
literal|'\u05da'
block|,
literal|'\u05da'
block|,
literal|'\u05da'
block|,
literal|'\ufb3c'
block|,
literal|'\ufb3e'
block|,
literal|'\ufb40'
block|,
literal|'\u05df'
block|,
literal|'\ufb41'
block|,
literal|'\ufb44'
block|,
literal|'\ufb46'
block|,
literal|'\ufb47'
block|,
literal|'\ufb2b'
block|,
literal|'\ufb2d'
block|,
literal|'\ufb2a'
block|,
literal|'\ufb2c'
block|,
literal|'\ufb4a'
block|,
literal|'\ufb4c'
block|,
literal|'\ufb4e'
block|,
literal|'\ufb1f'
block|,
literal|'\ufb1d'
block|}
block|,
comment|// WP Charset 10: Cyrillic (150 chars)
block|{
literal|'\u0410'
block|,
literal|'\u0430'
block|,
literal|'\u0411'
block|,
literal|'\u0431'
block|,
literal|'\u0412'
block|,
literal|'\u0432'
block|,
literal|'\u0413'
block|,
literal|'\u0433'
block|,
literal|'\u0414'
block|,
literal|'\u0434'
block|,
literal|'\u0415'
block|,
literal|'\u0435'
block|,
literal|'\u0401'
block|,
literal|'\u0451'
block|,
literal|'\u0416'
block|,
literal|'\u0436'
block|,
literal|'\u0417'
block|,
literal|'\u0437'
block|,
literal|'\u0418'
block|,
literal|'\u0438'
block|,
literal|'\u0419'
block|,
literal|'\u0439'
block|,
literal|'\u041a'
block|,
literal|'\u043a'
block|,
literal|'\u041b'
block|,
literal|'\u043b'
block|,
literal|'\u041c'
block|,
literal|'\u043c'
block|,
literal|'\u041d'
block|,
literal|'\u043d'
block|,
literal|'\u041e'
block|,
literal|'\u043e'
block|,
literal|'\u041f'
block|,
literal|'\u043f'
block|,
literal|'\u0420'
block|,
literal|'\u0440'
block|,
literal|'\u0421'
block|,
literal|'\u0441'
block|,
literal|'\u0422'
block|,
literal|'\u0442'
block|,
literal|'\u0423'
block|,
literal|'\u0443'
block|,
literal|'\u0424'
block|,
literal|'\u0444'
block|,
literal|'\u0425'
block|,
literal|'\u0445'
block|,
literal|'\u0426'
block|,
literal|'\u0446'
block|,
literal|'\u0427'
block|,
literal|'\u0447'
block|,
literal|'\u0428'
block|,
literal|'\u0448'
block|,
literal|'\u0429'
block|,
literal|'\u0449'
block|,
literal|'\u042a'
block|,
literal|'\u044a'
block|,
literal|'\u042b'
block|,
literal|'\u044b'
block|,
literal|'\u042c'
block|,
literal|'\u044c'
block|,
literal|'\u042d'
block|,
literal|'\u044d'
block|,
literal|'\u042e'
block|,
literal|'\u044e'
block|,
literal|'\u042f'
block|,
literal|'\u044f'
block|,
literal|'\u0490'
block|,
literal|'\u0491'
block|,
literal|'\u0402'
block|,
literal|'\u0452'
block|,
literal|'\u0403'
block|,
literal|'\u0453'
block|,
literal|'\u0404'
block|,
literal|'\u0454'
block|,
literal|'\u0405'
block|,
literal|'\u0455'
block|,
literal|'\u0406'
block|,
literal|'\u0456'
block|,
literal|'\u0407'
block|,
literal|'\u0457'
block|,
literal|'\u0408'
block|,
literal|'\u0458'
block|,
literal|'\u0409'
block|,
literal|'\u0459'
block|,
literal|'\u040a'
block|,
literal|'\u045a'
block|,
literal|'\u040b'
block|,
literal|'\u045b'
block|,
literal|'\u040c'
block|,
literal|'\u045c'
block|,
literal|'\u040e'
block|,
literal|'\u045e'
block|,
literal|'\u040f'
block|,
literal|'\u045f'
block|,
literal|'\u0462'
block|,
literal|'\u0463'
block|,
literal|'\u0472'
block|,
literal|'\u0473'
block|,
literal|'\u0474'
block|,
literal|'\u0475'
block|,
literal|'\u046a'
block|,
literal|'\u046b'
block|,
literal|'\ua640'
block|,
literal|'\ua641'
block|,
literal|'\u0429'
block|,
literal|'\u0449'
block|,
literal|'\u04c0'
block|,
literal|'\u04cf'
block|,
literal|'\u0466'
block|,
literal|'\u0467'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0400'
block|,
literal|'\u0450'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u040d'
block|,
literal|'\u045d'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0000'
block|,
literal|'\u0301'
block|,
literal|'\u0300'
block|}
block|,
comment|// WP Charset 11: Japanese (185 chars)
block|{
literal|'\u3041'
block|,
literal|'\u3043'
block|,
literal|'\u3045'
block|,
literal|'\u3047'
block|,
literal|'\u3049'
block|,
literal|'\u3053'
block|,
literal|'\u3083'
block|,
literal|'\u3085'
block|,
literal|'\u3087'
block|,
literal|'\u3094'
block|,
literal|'\u3095'
block|,
literal|'\u3096'
block|,
literal|'\u3042'
block|,
literal|'\u3044'
block|,
literal|'\u3046'
block|,
literal|'\u3048'
block|,
literal|'\u304a'
block|,
literal|'\u304b'
block|,
literal|'\u304d'
block|,
literal|'\u3047'
block|,
literal|'\u3051'
block|,
literal|'\u3053'
block|,
literal|'\u304c'
block|,
literal|'\u304e'
block|,
literal|'\u3050'
block|,
literal|'\u3052'
block|,
literal|'\u3054'
block|,
literal|'\u3055'
block|,
literal|'\u3057'
block|,
literal|'\u3059'
block|,
literal|'\u305b'
block|,
literal|'\u305d'
block|,
literal|'\u3056'
block|,
literal|'\u3058'
block|,
literal|'\u305a'
block|,
literal|'\u305c'
block|,
literal|'\u305e'
block|,
literal|'\u305f'
block|,
literal|'\u3051'
block|,
literal|'\u3064'
block|,
literal|'\u3066'
block|,
literal|'\u3068'
block|,
literal|'\u3060'
block|,
literal|'\u3062'
block|,
literal|'\u3065'
block|,
literal|'\u3067'
block|,
literal|'\u3069'
block|,
literal|'\u306a'
block|,
literal|'\u306b'
block|,
literal|'\u306c'
block|,
literal|'\u306d'
block|,
literal|'\u306e'
block|,
literal|'\u306f'
block|,
literal|'\u3072'
block|,
literal|'\u3075'
block|,
literal|'\u3078'
block|,
literal|'\u307b'
block|,
literal|'\u3070'
block|,
literal|'\u3073'
block|,
literal|'\u3076'
block|,
literal|'\u3079'
block|,
literal|'\u307c'
block|,
literal|'\u3071'
block|,
literal|'\u3074'
block|,
literal|'\u3077'
block|,
literal|'\u307a'
block|,
literal|'\u307d'
block|,
literal|'\u307e'
block|,
literal|'\u307f'
block|,
literal|'\u3080'
block|,
literal|'\u3081'
block|,
literal|'\u3082'
block|,
literal|'\u3084'
block|,
literal|'\u3086'
block|,
literal|'\u3088'
block|,
literal|'\u3089'
block|,
literal|'\u308a'
block|,
literal|'\u308b'
block|,
literal|'\u308c'
block|,
literal|'\u308d'
block|,
literal|'\u308e'
block|,
literal|'\u3092'
block|,
literal|'\u3093'
block|,
literal|'\u3014'
block|,
literal|'\u3015'
block|,
literal|'\uff3b'
block|,
literal|'\uff3d'
block|,
literal|'\u300c'
block|,
literal|'\u300d'
block|,
literal|'\u300c'
block|,
literal|'\u300d'
block|,
literal|'\u302a'
block|,
literal|'\u3002'
block|,
literal|'\u3001'
block|,
literal|'\u309d'
block|,
literal|'\u309e'
block|,
literal|'\u3003'
block|,
literal|'\u30fc'
block|,
literal|'\u309b'
block|,
literal|'\u309c'
block|,
literal|'\u30a1'
block|,
literal|'\u30a3'
block|,
literal|'\u30a5'
block|,
literal|'\u30a7'
block|,
literal|'\u30a9'
block|,
literal|'\u30c3'
block|,
literal|'\u30e3'
block|,
literal|'\u30e5'
block|,
literal|'\u3057'
block|,
literal|'\u30f4'
block|,
literal|'\u30f5'
block|,
literal|'\u30f6'
block|,
literal|'\u30a2'
block|,
literal|'\u30a4'
block|,
literal|'\u30a6'
block|,
literal|'\u30a8'
block|,
literal|'\u30aa'
block|,
literal|'\u30ab'
block|,
literal|'\u30ad'
block|,
literal|'\u30af'
block|,
literal|'\u30b1'
block|,
literal|'\u30b3'
block|,
literal|'\u30ac'
block|,
literal|'\u30ae'
block|,
literal|'\u30b0'
block|,
literal|'\u30b2'
block|,
literal|'\u30b4'
block|,
literal|'\u30b5'
block|,
literal|'\u30c4'
block|,
literal|'\u30b9'
block|,
literal|'\u30bb'
block|,
literal|'\u30bd'
block|,
literal|'\u30b6'
block|,
literal|'\u30b8'
block|,
literal|'\u30ba'
block|,
literal|'\u30bc'
block|,
literal|'\u30be'
block|,
literal|'\u30bf'
block|,
literal|'\u30c1'
block|,
literal|'\u30c4'
block|,
literal|'\u30c6'
block|,
literal|'\u30c8'
block|,
literal|'\u30c0'
block|,
literal|'\u30c2'
block|,
literal|'\u30c5'
block|,
literal|'\u30c7'
block|,
literal|'\u30c9'
block|,
literal|'\u30ca'
block|,
literal|'\u30cb'
block|,
literal|'\u30cc'
block|,
literal|'\u30cd'
block|,
literal|'\u30ce'
block|,
literal|'\u30cf'
block|,
literal|'\u30d2'
block|,
literal|'\u30d5'
block|,
literal|'\u30d8'
block|,
literal|'\u03d0'
block|,
literal|'\u30db'
block|,
literal|'\u30d3'
block|,
literal|'\u30d6'
block|,
literal|'\u30d9'
block|,
literal|'\u30dc'
block|,
literal|'\u30d1'
block|,
literal|'\u30d4'
block|,
literal|'\u30d7'
block|,
literal|'\u30da'
block|,
literal|'\u30dd'
block|,
literal|'\u30de'
block|,
literal|'\u30df'
block|,
literal|'\u30e0'
block|,
literal|'\u30e1'
block|,
literal|'\u30e2'
block|,
literal|'\u30e4'
block|,
literal|'\u30e6'
block|,
literal|'\u30e8'
block|,
literal|'\u30e9'
block|,
literal|'\u30ea'
block|,
literal|'\u30ab'
block|,
literal|'\u30ec'
block|,
literal|'\u30ed'
block|,
literal|'\u30ef'
block|,
literal|'\u30f2'
block|,
literal|'\u30f3'
block|,
literal|'\u30fd'
block|,
literal|'\u30fe'
block|}
block|,
comment|// WP Charset 12: User-defined (255 chars)
block|{
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|,
literal|' '
block|}
block|}
decl_stmt|;
comment|//TODO map multi-characters
comment|/**      * Constructor.      */
specifier|private
name|WP5Charsets
parameter_list|()
block|{     }
specifier|public
specifier|static
name|void
name|append
parameter_list|(
name|StringBuilder
name|out
parameter_list|,
name|int
name|charset
parameter_list|,
name|int
name|charval
parameter_list|)
block|{
if|if
condition|(
name|charset
operator|>=
name|WP5Charsets
operator|.
name|EXTENDED_CHARSETS
operator|.
name|length
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unsupported WordPerfect 5.x charset: "
operator|+
name|charset
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|charval
operator|>=
name|WP5Charsets
operator|.
name|EXTENDED_CHARSETS
index|[
name|charset
index|]
operator|.
name|length
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unsupported WordPerfect 5.x charset ("
operator|+
name|charset
operator|+
literal|") character value: "
operator|+
name|charval
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|append
argument_list|(
name|WP5Charsets
operator|.
name|EXTENDED_CHARSETS
index|[
name|charset
index|]
index|[
name|charval
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

