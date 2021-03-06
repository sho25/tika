begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|gdal
package|;
end_package

begin_comment
comment|//JDK imports
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|HashSet
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
name|Scanner
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|external
operator|.
name|ExternalParser
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|external
operator|.
name|ExternalParser
operator|.
name|INPUT_FILE_TOKEN
import|;
end_import

begin_comment
comment|//Tika imports
end_comment

begin_comment
comment|//SAX imports
end_comment

begin_comment
comment|/**  * Wraps execution of the<a href="http//gdal.org/">Geospatial Data Abstraction  * Library (GDAL)</a><code>gdalinfo</code> tool used to extract geospatial  * information out of hundreds of geo file formats.  *<p/>  * The parser requires the installation of GDAL and for<code>gdalinfo</code> to  * be located on the path.  *<p/>  * Basic information (Size, Coordinate System, Bounding Box, Driver, and  * resource info) are extracted as metadata, and the remaining metadata patterns  * are extracted and added.  *<p/>  * The output of the command is available from the provided  * {@link ContentHandler} in the  * {@link #parse(InputStream, ContentHandler, Metadata, ParseContext)} method.  */
end_comment

begin_class
specifier|public
class|class
name|GDALParser
extends|extends
name|AbstractParser
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3869130527323941401L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GDALParser
operator|.
name|class
argument_list|)
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
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-netcdf"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"vrt"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"geotiff"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"nitf"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-rpf-toc"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ecrg-toc"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"hfa"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"sar-ceos"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"ceos"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"jaxa-pal-sar"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"gff"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"elas"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"aig"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"aaigrid"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"grass-ascii-grid"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"sdts-raster"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"dted"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"png"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"jpeg"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"raster"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"jdem"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"gif"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"big-gif"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"envisat"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"fits"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"fits"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"bsb"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"xpm"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"bmp"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-dimap"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-airsar"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-rs2"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-pcidsk"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"pcisdk"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-pcraster"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"ilwis"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"sgi"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-srtmhgt"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"leveller"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"terragen"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-gmt"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-isis3"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-isis2"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-pds"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-til"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ers"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-l1b"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"fit"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-grib"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"jp2"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-rmf"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-wcs"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-wms"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-msgn"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-wms"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-wms"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-rst"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ingr"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-gsag"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-gsbg"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-gs7bg"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-cosar"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-tsx"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-coasp"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-r"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-map"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-pnm"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-doq1"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-doq2"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-envi"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-envi-hdr"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-generic-bin"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-p-aux"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-mff"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-mff2"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-fujibas"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-gsc"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-fast"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-bt"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-lan"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-cpg"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"ida"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ndf"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"eir"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-dipex"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-lcp"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-gtx"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-los-las"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ntv2"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ctable2"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ace2"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-snodas"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-kro"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"arg"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-rik"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-usgs-dem"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-gxf"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-dods"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-http"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-bag"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-hdf"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-hdf5-image"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-nwt-grd"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-nwt-grc"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"adrg"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-srp"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-blx"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-rasterlite"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-epsilon"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-sdat"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-kml"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-xyz"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-geo-pdf"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|image
argument_list|(
literal|"x-ozi"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ctg"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-e00-grid"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-zmap"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-webp"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ngs-geoid"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-mbtiles"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-ppi"
argument_list|)
argument_list|,
name|MediaType
operator|.
name|application
argument_list|(
literal|"x-cappi"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|String
name|command
decl_stmt|;
specifier|public
name|GDALParser
parameter_list|()
block|{
name|setCommand
argument_list|(
literal|"gdalinfo ${INPUT}"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCommand
parameter_list|(
name|String
name|command
parameter_list|)
block|{
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
specifier|public
name|String
name|getCommand
parameter_list|()
block|{
return|return
name|this
operator|.
name|command
return|;
block|}
specifier|public
name|String
name|processCommand
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
name|TikaInputStream
name|tis
init|=
operator|(
name|TikaInputStream
operator|)
name|stream
decl_stmt|;
name|String
name|pCommand
init|=
name|this
operator|.
name|command
decl_stmt|;
try|try
block|{
if|if
condition|(
name|this
operator|.
name|command
operator|.
name|contains
argument_list|(
name|INPUT_FILE_TOKEN
argument_list|)
condition|)
block|{
name|pCommand
operator|=
name|this
operator|.
name|command
operator|.
name|replace
argument_list|(
name|INPUT_FILE_TOKEN
argument_list|,
name|tis
operator|.
name|getFile
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"exception processing command"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|pCommand
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
if|if
condition|(
operator|!
name|ExternalParser
operator|.
name|check
argument_list|(
literal|"gdalinfo"
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// first set up and run GDAL
comment|// process the command
name|TemporaryResources
name|tmp
init|=
operator|new
name|TemporaryResources
argument_list|()
decl_stmt|;
name|TikaInputStream
name|tis
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
name|String
name|runCommand
init|=
name|processCommand
argument_list|(
name|tis
argument_list|)
decl_stmt|;
name|String
name|output
init|=
name|execCommand
argument_list|(
operator|new
name|String
index|[]
block|{
name|runCommand
block|}
argument_list|)
decl_stmt|;
comment|// now extract the actual metadata params
comment|// from the GDAL output in the content stream
comment|// to do this, we need to literally process the output
comment|// from the invoked command b/c we can't read metadata and
comment|// output text from the handler in ExternalParser
comment|// at the same time, so for now, we can't use the
comment|// ExternalParser to do this and I've had to bring some of
comment|// that functionality directly into this class
comment|// TODO: investigate a way to do both using ExternalParser
name|extractMetFromOutput
argument_list|(
name|output
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|applyPatternsToOutput
argument_list|(
name|output
argument_list|,
name|metadata
argument_list|,
name|getPatterns
argument_list|()
argument_list|)
expr_stmt|;
comment|// make the content handler and provide output there
comment|// now that we have metadata
name|processOutput
argument_list|(
name|handler
argument_list|,
name|metadata
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|getPatterns
parameter_list|()
block|{
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|patterns
init|=
operator|new
name|HashMap
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|this
operator|.
name|addPatternWithColon
argument_list|(
literal|"Driver"
argument_list|,
name|patterns
argument_list|)
expr_stmt|;
name|this
operator|.
name|addPatternWithColon
argument_list|(
literal|"Files"
argument_list|,
name|patterns
argument_list|)
expr_stmt|;
name|this
operator|.
name|addPatternWithIs
argument_list|(
literal|"Size"
argument_list|,
name|patterns
argument_list|)
expr_stmt|;
name|this
operator|.
name|addPatternWithIs
argument_list|(
literal|"Coordinate System"
argument_list|,
name|patterns
argument_list|)
expr_stmt|;
name|this
operator|.
name|addBoundingBoxPattern
argument_list|(
literal|"Upper Left"
argument_list|,
name|patterns
argument_list|)
expr_stmt|;
name|this
operator|.
name|addBoundingBoxPattern
argument_list|(
literal|"Lower Left"
argument_list|,
name|patterns
argument_list|)
expr_stmt|;
name|this
operator|.
name|addBoundingBoxPattern
argument_list|(
literal|"Upper Right"
argument_list|,
name|patterns
argument_list|)
expr_stmt|;
name|this
operator|.
name|addBoundingBoxPattern
argument_list|(
literal|"Lower Right"
argument_list|,
name|patterns
argument_list|)
expr_stmt|;
return|return
name|patterns
return|;
block|}
specifier|private
name|void
name|addPatternWithColon
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|patterns
parameter_list|)
block|{
name|patterns
operator|.
name|put
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|name
operator|+
literal|"\\:\\s*([A-Za-z0-9/ _\\-\\.]+)\\s*"
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addPatternWithIs
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|patterns
parameter_list|)
block|{
name|patterns
operator|.
name|put
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|name
operator|+
literal|" is ([A-Za-z0-9\\.,\\s`']+)"
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addBoundingBoxPattern
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|patterns
parameter_list|)
block|{
name|patterns
operator|.
name|put
argument_list|(
name|Pattern
operator|.
name|compile
argument_list|(
name|name
operator|+
literal|"\\s*\\(\\s*([0-9]+\\.[0-9]+\\s*,\\s*[0-9]+\\.[0-9]+\\s*)\\)\\s*"
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|extractMetFromOutput
parameter_list|(
name|String
name|output
parameter_list|,
name|Metadata
name|met
parameter_list|)
block|{
name|Scanner
name|scanner
init|=
operator|new
name|Scanner
argument_list|(
name|output
argument_list|)
decl_stmt|;
name|String
name|currentKey
init|=
literal|null
decl_stmt|;
name|String
index|[]
name|headings
init|=
block|{
literal|"Subdatasets"
block|,
literal|"Corner Coordinates"
block|}
decl_stmt|;
name|StringBuilder
name|metVal
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|scanner
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|String
name|line
init|=
name|scanner
operator|.
name|nextLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|contains
argument_list|(
literal|"="
argument_list|)
operator|||
name|hasHeadings
argument_list|(
name|line
argument_list|,
name|headings
argument_list|)
condition|)
block|{
if|if
condition|(
name|currentKey
operator|!=
literal|null
condition|)
block|{
comment|// time to flush this key and met val
name|met
operator|.
name|add
argument_list|(
name|currentKey
argument_list|,
name|metVal
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|metVal
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
index|[]
name|lineToks
init|=
name|line
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
name|currentKey
operator|=
name|lineToks
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|lineToks
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|metVal
operator|.
name|append
argument_list|(
name|lineToks
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metVal
operator|.
name|append
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|metVal
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|boolean
name|hasHeadings
parameter_list|(
name|String
name|line
parameter_list|,
name|String
index|[]
name|headings
parameter_list|)
block|{
if|if
condition|(
name|headings
operator|!=
literal|null
operator|&&
name|headings
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|String
name|heading
range|:
name|headings
control|)
block|{
if|if
condition|(
name|line
operator|.
name|contains
argument_list|(
name|heading
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|applyPatternsToOutput
parameter_list|(
name|String
name|output
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|Map
argument_list|<
name|Pattern
argument_list|,
name|String
argument_list|>
name|metadataPatterns
parameter_list|)
block|{
name|Scanner
name|scanner
init|=
operator|new
name|Scanner
argument_list|(
name|output
argument_list|)
decl_stmt|;
while|while
condition|(
name|scanner
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|String
name|line
init|=
name|scanner
operator|.
name|nextLine
argument_list|()
decl_stmt|;
for|for
control|(
name|Pattern
name|p
range|:
name|metadataPatterns
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
if|if
condition|(
name|metadataPatterns
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|!=
literal|null
operator|&&
operator|!
name|metadataPatterns
operator|.
name|get
argument_list|(
name|p
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|metadata
operator|.
name|add
argument_list|(
name|metadataPatterns
operator|.
name|get
argument_list|(
name|p
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|add
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|,
name|m
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
name|String
name|execCommand
parameter_list|(
name|String
index|[]
name|cmd
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Execute
name|Process
name|process
decl_stmt|;
name|String
name|output
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cmd
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|process
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|cmd
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|process
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|exec
argument_list|(
name|cmd
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|InputStream
name|out
init|=
name|process
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|output
operator|=
name|extractOutput
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception extracting output"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|output
operator|=
literal|""
expr_stmt|;
block|}
block|}
finally|finally
block|{
try|try
block|{
name|process
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ignore
parameter_list|)
block|{             }
block|}
return|return
name|output
return|;
block|}
specifier|private
name|String
name|extractOutput
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
init|;
name|n
operator|!=
operator|-
literal|1
condition|;
name|n
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|processOutput
parameter_list|(
name|ContentHandler
name|handler
parameter_list|,
name|Metadata
name|metadata
parameter_list|,
name|String
name|output
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
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
name|InputStream
name|stream
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|output
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|UTF_8
argument_list|)
init|)
block|{
name|xhtml
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|xhtml
operator|.
name|startElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
for|for
control|(
name|int
name|n
init|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
init|;
name|n
operator|!=
operator|-
literal|1
condition|;
name|n
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
control|)
block|{
name|xhtml
operator|.
name|characters
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|xhtml
operator|.
name|endElement
argument_list|(
literal|"p"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|xhtml
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

