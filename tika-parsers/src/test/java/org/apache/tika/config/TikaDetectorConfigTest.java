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
name|assertNotNull
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|detect
operator|.
name|CompositeDetector
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
name|DefaultDetector
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
name|Detector
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
name|EmptyDetector
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
name|microsoft
operator|.
name|POIFSContainerDetector
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
name|pkg
operator|.
name|ZipContainerDetector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_comment
comment|/**  * Junit test class for {@link TikaConfig}, which cover things  *  that {@link AbstractTikaConfigTest} can't do due to a need for the  *  full set of detectors  */
end_comment

begin_class
specifier|public
class|class
name|TikaDetectorConfigTest
extends|extends
name|AbstractTikaConfigTest
block|{
annotation|@
name|Test
annotation|@
name|Ignore
comment|// TODO Finish support
specifier|public
name|void
name|testDetectorExcludeFromDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|TikaConfig
name|config
init|=
name|getConfig
argument_list|(
literal|"TIKA-1702-detector-blacklist.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getParser
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|config
operator|.
name|getDetector
argument_list|()
argument_list|)
expr_stmt|;
name|CompositeDetector
name|detector
init|=
operator|(
name|CompositeDetector
operator|)
name|config
operator|.
name|getDetector
argument_list|()
decl_stmt|;
comment|// Should be wrapping two detectors
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|detector
operator|.
name|getDetectors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// First should be DefaultDetector, second Empty, that order
name|assertEquals
argument_list|(
name|DefaultDetector
operator|.
name|class
argument_list|,
name|detector
operator|.
name|getDetectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EmptyDetector
operator|.
name|class
argument_list|,
name|detector
operator|.
name|getDetectors
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get the DefaultDetector from the config
name|DefaultDetector
name|confDetecotor
init|=
operator|(
name|DefaultDetector
operator|)
name|detector
operator|.
name|getDetectors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Get a fresh "default" DefaultParser
name|DefaultDetector
name|normDetector
init|=
operator|new
name|DefaultDetector
argument_list|(
name|config
operator|.
name|getMimeRepository
argument_list|()
argument_list|)
decl_stmt|;
comment|// The default one will offer the Zip and POIFS detectors
name|boolean
name|hasZip
init|=
literal|false
decl_stmt|;
name|boolean
name|hasPOIFS
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Detector
name|d
range|:
name|normDetector
operator|.
name|getDetectors
argument_list|()
control|)
block|{
if|if
condition|(
name|d
operator|instanceof
name|ZipContainerDetector
condition|)
block|{
name|hasZip
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|d
operator|instanceof
name|POIFSContainerDetector
condition|)
block|{
name|hasPOIFS
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|hasZip
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hasPOIFS
argument_list|)
expr_stmt|;
comment|// The one from the config won't, as we excluded those
for|for
control|(
name|Detector
name|d
range|:
name|confDetecotor
operator|.
name|getDetectors
argument_list|()
control|)
block|{
if|if
condition|(
name|d
operator|instanceof
name|ZipContainerDetector
condition|)
name|fail
argument_list|(
literal|"Shouldn't have the ZipContainerDetector from config"
argument_list|)
expr_stmt|;
if|if
condition|(
name|d
operator|instanceof
name|POIFSContainerDetector
condition|)
name|fail
argument_list|(
literal|"Shouldn't have the POIFSContainerDetector from config"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

