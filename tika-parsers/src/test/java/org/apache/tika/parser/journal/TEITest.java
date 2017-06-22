begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|journal
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|TikaTest
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
name|IOUtils
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|TEITest
extends|extends
name|TikaTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|TEIDOMParser
name|teiParser
init|=
operator|new
name|TEIDOMParser
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|getResourceAsStream
argument_list|(
literal|"/test-documents/testTEI.xml"
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|bos
argument_list|)
expr_stmt|;
block|}
name|String
name|xml
init|=
operator|new
name|String
argument_list|(
name|bos
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|Metadata
name|metadata
init|=
name|teiParser
operator|.
name|parse
argument_list|(
name|xml
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Montbonnot Saint-Martin, Montbonnot Saint-Martin, Montbonnot Saint-Martin, "
operator|+
literal|"Montbonnot Saint-Martin, null 38330, 38330, 38330, 38330 "
operator|+
literal|"France, France, France, France "
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Address"
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
name|keywords
init|=
operator|new
name|String
index|[]
block|{
literal|"F22 [Analysis of Algorithms and Problem Complexity]: Nonnumerical Algorithms and Problems\u2014Sequencing"
block|,
literal|"and scheduling; D41 [Operating Systems]: Process management\u2014Scheduling, Concurrency"
block|,
literal|"Keywords"
block|,
literal|"Parallel Computing, Algorithms, Scheduling, Parallel Tasks,"
block|,
literal|"Moldable Tasks, Bi-criteria"
block|}
decl_stmt|;
name|assertArrayEquals
argument_list|(
name|keywords
argument_list|,
name|metadata
operator|.
name|getValues
argument_list|(
literal|"Keyword"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Pierre-François  Dutot 1 Lionel  Eyraud 1 Grégory  Gr´ 1 Grégory  Mouní 1 Denis  Trystram 1 "
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Authors"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bi-criteria Algorithm for Scheduling Jobs on Cluster Platforms *"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1 ID-IMAG ID-IMAG ID-IMAG ID-IMAG"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"Affiliation"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[Affiliation {orgName=ID-IMAG ID-IMAG ID-IMAG ID-IMAG , "
operator|+
literal|"address=Montbonnot Saint-Martin, Montbonnot Saint-Martin, Montbonnot Saint-Martin, Montbonnot Saint-Martin, "
operator|+
literal|"null 38330, 38330, 38330, 38330 France, France, France, France}"
operator|+
literal|"[Affiliation {orgName=ID-IMAG ID-IMAG ID-IMAG ID-IMAG , "
operator|+
literal|"address=Montbonnot Saint-Martin, Montbonnot Saint-Martin, Montbonnot Saint-Martin, Montbonnot Saint-Martin, "
operator|+
literal|"null 38330, 38330, 38330, 38330 France, France, France, France}]"
argument_list|,
name|metadata
operator|.
name|get
argument_list|(
literal|"FullAffiliations"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

