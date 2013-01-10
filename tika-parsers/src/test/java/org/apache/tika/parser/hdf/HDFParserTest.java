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
name|hdf
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
name|InputStream
import|;
end_import

begin_comment
comment|//TIKA imports
end_comment

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
name|parser
operator|.
name|hdf
operator|.
name|HDFParser
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
name|BodyContentHandler
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

begin_comment
comment|//Junit imports
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  *   * Test suite for the {@link HDFParser}.  *   */
end_comment

begin_class
specifier|public
class|class
name|HDFParserTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testParseGlobalMetadata
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"1.5"
argument_list|)
condition|)
block|{
return|return;
block|}
name|Parser
name|parser
init|=
operator|new
name|HDFParser
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|/*          * this is a publicly available HDF5 file from the MLS mission:          *           *           * ftp://acdisc.gsfc.nasa.gov/data/s4pa///Aura_MLS_Level2/ML2O3.002//2009          * /MLS-Aura_L2GP-O3_v02-23-c01_2009d122.he5          */
name|InputStream
name|stream
init|=
name|HDFParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test.he5"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
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
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"5"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"GranuleMonth"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testHDF4
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"1.5"
argument_list|)
condition|)
block|{
return|return;
block|}
name|Parser
name|parser
init|=
operator|new
name|HDFParser
argument_list|()
decl_stmt|;
name|ContentHandler
name|handler
init|=
operator|new
name|BodyContentHandler
argument_list|()
decl_stmt|;
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
comment|/*        * this is a publicly available HDF4 file from the HD4 examples:        *         * http://www.hdfgroup.org/training/hdf4_chunking/Chunkit/bin/input54kmdata.hdf        */
name|InputStream
name|stream
init|=
name|HDFParser
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/test-documents/test.hdf"
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
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
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|metadata
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Direct read of HDF4 file through CDM library"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"_History"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Ascending"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Pass"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

