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
name|config
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
name|assertEquals
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
name|assertTrue
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
name|fail
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
name|List
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
name|Tika
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
name|detect
operator|.
name|CompositeEncodingDetector
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
name|detect
operator|.
name|EncodingDetector
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
name|detect
operator|.
name|NonDetectingEncodingDetector
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
name|TikaConfigException
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
name|AbstractEncodingDetectorParser
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
name|AutoDetectParser
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
name|CompositeParser
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
name|ParserDecorator
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
name|html
operator|.
name|HtmlEncodingDetector
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
name|txt
operator|.
name|Icu4jEncodingDetector
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
name|txt
operator|.
name|TXTParser
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
name|txt
operator|.
name|UniversalEncodingDetector
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
name|TikaEncodingDetectorTest
extends|extends
name|AbstractTikaConfigTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testDefault
parameter_list|()
block|{
name|EncodingDetector
name|detector
init|=
name|TikaConfig
operator|.
name|getDefaultConfig
argument_list|()
operator|.
name|getEncodingDetector
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|detector
operator|instanceof
name|CompositeEncodingDetector
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|EncodingDetector
argument_list|>
name|detectors
init|=
operator|(
operator|(
name|CompositeEncodingDetector
operator|)
name|detector
operator|)
operator|.
name|getDetectors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|detectors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|detectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|HtmlEncodingDetector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|detectors
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|UniversalEncodingDetector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|detectors
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|instanceof
name|Icu4jEncodingDetector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBlackList
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|getConfig
argument_list|(
literal|"TIKA-2273-blacklist-encoding-detector-default.xml"
argument_list|)
decl_stmt|;
name|EncodingDetector
name|detector
init|=
name|config
operator|.
name|getEncodingDetector
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|detector
operator|instanceof
name|CompositeEncodingDetector
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|EncodingDetector
argument_list|>
name|detectors
init|=
operator|(
operator|(
name|CompositeEncodingDetector
operator|)
name|detector
operator|)
operator|.
name|getDetectors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|detectors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|EncodingDetector
name|detector1
init|=
name|detectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|detector1
operator|instanceof
name|CompositeEncodingDetector
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|EncodingDetector
argument_list|>
name|detectors1Children
init|=
operator|(
operator|(
name|CompositeEncodingDetector
operator|)
name|detector1
operator|)
operator|.
name|getDetectors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|detectors1Children
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|detectors1Children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|UniversalEncodingDetector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|detectors1Children
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|Icu4jEncodingDetector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|detectors
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|NonDetectingEncodingDetector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParameterization
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|getConfig
argument_list|(
literal|"TIKA-2273-parameterize-encoding-detector.xml"
argument_list|)
decl_stmt|;
name|EncodingDetector
name|detector
init|=
name|config
operator|.
name|getEncodingDetector
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|detector
operator|instanceof
name|CompositeEncodingDetector
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|EncodingDetector
argument_list|>
name|detectors
init|=
operator|(
operator|(
name|CompositeEncodingDetector
operator|)
name|detector
operator|)
operator|.
name|getDetectors
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|detectors
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|Icu4jEncodingDetector
operator|)
name|detectors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getStripMarkup
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|detectors
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|instanceof
name|NonDetectingEncodingDetector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEncodingDetectorsAreLoaded
parameter_list|()
block|{
name|EncodingDetector
name|encodingDetector
init|=
operator|(
operator|(
name|AbstractEncodingDetectorParser
operator|)
operator|new
name|TXTParser
argument_list|()
operator|)
operator|.
name|getEncodingDetector
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|encodingDetector
operator|instanceof
name|CompositeEncodingDetector
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEncodingDetectorConfigurability
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|tikaConfig
init|=
operator|new
name|TikaConfig
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/config/TIKA-2273-no-icu4j-encoding-detector.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|AutoDetectParser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|(
name|tikaConfig
argument_list|)
decl_stmt|;
try|try
block|{
name|Metadata
name|metadata
init|=
name|getXML
argument_list|(
literal|"english.cp500.txt"
argument_list|,
name|p
argument_list|)
operator|.
name|metadata
decl_stmt|;
name|fail
argument_list|(
literal|"can't detect w/out ICU"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|assertContains
argument_list|(
literal|"Failed to detect"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Tika
name|tika
init|=
operator|new
name|Tika
argument_list|(
name|tikaConfig
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|txt
init|=
name|tika
operator|.
name|parseToString
argument_list|(
name|getResourceAsFile
argument_list|(
literal|"/test-documents/english.cp500.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"can't detect w/out ICU"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|assertContains
argument_list|(
literal|"Failed to detect"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonDetectingDetectorParams
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|tikaConfig
init|=
operator|new
name|TikaConfig
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/config/TIKA-2273-non-detecting-params.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|AutoDetectParser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|(
name|tikaConfig
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|findEncodingDetectionParsers
argument_list|(
name|p
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|parsers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|EncodingDetector
name|encodingDetector
init|=
operator|(
operator|(
name|AbstractEncodingDetectorParser
operator|)
name|parsers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getEncodingDetector
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|encodingDetector
operator|instanceof
name|CompositeEncodingDetector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|CompositeEncodingDetector
operator|)
name|encodingDetector
operator|)
operator|.
name|getDetectors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|EncodingDetector
name|child
init|=
operator|(
operator|(
name|CompositeEncodingDetector
operator|)
name|encodingDetector
operator|)
operator|.
name|getDetectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|child
operator|instanceof
name|NonDetectingEncodingDetector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|StandardCharsets
operator|.
name|UTF_16LE
argument_list|,
operator|(
operator|(
name|NonDetectingEncodingDetector
operator|)
name|child
operator|)
operator|.
name|getCharset
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNonDetectingDetectorParamsBadCharset
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|TikaConfig
name|tikaConfig
init|=
operator|new
name|TikaConfig
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/config/TIKA-2273-non-detecting-params-bad-charset.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"should have thrown TikaConfigException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaConfigException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConfigurabilityOfUserSpecified
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|tikaConfig
init|=
operator|new
name|TikaConfig
argument_list|(
name|getResourceAsStream
argument_list|(
literal|"/org/apache/tika/config/TIKA-2273-encoding-detector-outside-static-init.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|AutoDetectParser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|(
name|tikaConfig
argument_list|)
decl_stmt|;
comment|//make sure that all static and non-static parsers are using the same encoding detector!
name|List
argument_list|<
name|Parser
argument_list|>
name|parsers
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|findEncodingDetectionParsers
argument_list|(
name|p
argument_list|,
name|parsers
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|parsers
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Parser
name|encodingDetectingParser
range|:
name|parsers
control|)
block|{
name|EncodingDetector
name|encodingDetector
init|=
operator|(
operator|(
name|AbstractEncodingDetectorParser
operator|)
name|encodingDetectingParser
operator|)
operator|.
name|getEncodingDetector
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|encodingDetector
operator|instanceof
name|CompositeEncodingDetector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|CompositeEncodingDetector
operator|)
name|encodingDetector
operator|)
operator|.
name|getDetectors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|EncodingDetector
name|child
range|:
operator|(
operator|(
name|CompositeEncodingDetector
operator|)
name|encodingDetector
operator|)
operator|.
name|getDetectors
argument_list|()
control|)
block|{
name|assertNotContained
argument_list|(
literal|"cu4j"
argument_list|,
name|child
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//also just make sure this is still true
try|try
block|{
name|Metadata
name|metadata
init|=
name|getXML
argument_list|(
literal|"english.cp500.txt"
argument_list|,
name|p
argument_list|)
operator|.
name|metadata
decl_stmt|;
name|fail
argument_list|(
literal|"can't detect w/out ICU"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|assertContains
argument_list|(
literal|"Failed to detect"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|findEncodingDetectionParsers
parameter_list|(
name|Parser
name|p
parameter_list|,
name|List
argument_list|<
name|Parser
argument_list|>
name|encodingDetectionParsers
parameter_list|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|CompositeParser
condition|)
block|{
for|for
control|(
name|Parser
name|child
range|:
operator|(
operator|(
name|CompositeParser
operator|)
name|p
operator|)
operator|.
name|getAllComponentParsers
argument_list|()
control|)
block|{
name|findEncodingDetectionParsers
argument_list|(
name|child
argument_list|,
name|encodingDetectionParsers
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|p
operator|instanceof
name|ParserDecorator
condition|)
block|{
name|findEncodingDetectionParsers
argument_list|(
operator|(
operator|(
name|ParserDecorator
operator|)
name|p
operator|)
argument_list|,
name|encodingDetectionParsers
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|p
operator|instanceof
name|AbstractEncodingDetectorParser
condition|)
block|{
name|encodingDetectionParsers
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
