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
name|assertNull
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|codec
operator|.
name|digest
operator|.
name|DigestUtils
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
name|parser
operator|.
name|utils
operator|.
name|BouncyCastleDigester
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
name|BouncyCastleDigestingParserTest
extends|extends
name|TikaTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|P
init|=
name|TikaCoreProperties
operator|.
name|TIKA_META_PREFIX
operator|+
literal|"digest"
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|UNLIMITED
init|=
literal|1000000
decl_stmt|;
comment|//well, not really, but longer than input file
specifier|private
specifier|final
specifier|static
name|long
name|SEED
init|=
operator|new
name|Random
argument_list|()
operator|.
name|nextLong
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|(
name|SEED
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Parser
name|p
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expected
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"MD2"
argument_list|,
literal|"d768c8e27b0b52c6eaabfaa7122d1d4f"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"MD5"
argument_list|,
literal|"59f626e09a8c16ab6dbc2800c685f772"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA1"
argument_list|,
literal|"7a1f001d163ac90d8ea54c050faf5a38079788a6"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA256"
argument_list|,
literal|"c4b7fab030a8b6a9d6691f6699ac8e6f"
operator|+
literal|"82bc53764a0f1430d134ae3b70c32654"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA384"
argument_list|,
literal|"ebe368b9326fef44408290724d187553"
operator|+
literal|"8b8a6923fdf251ddab72c6e4b5d54160"
operator|+
literal|"9db917ba4260d1767995a844d8d654df"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA512"
argument_list|,
literal|"ee46d973ee1852c018580c242955974d"
operator|+
literal|"da4c21f36b54d7acd06fcf68e974663b"
operator|+
literal|"fed1d256875be58d22beacf178154cc3"
operator|+
literal|"a1178cb73443deaa53aa0840324708bb"
argument_list|)
expr_stmt|;
comment|//test each one
for|for
control|(
name|String
name|algo
range|:
name|expected
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|XMLResult
name|xml
init|=
name|getXML
argument_list|(
literal|"test_recursive_embedded.docx"
argument_list|,
operator|new
name|DigestingParser
argument_list|(
name|p
argument_list|,
operator|new
name|BouncyCastleDigester
argument_list|(
name|UNLIMITED
argument_list|,
name|algo
argument_list|)
argument_list|)
argument_list|,
name|m
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|algo
argument_list|,
name|expected
operator|.
name|get
argument_list|(
name|algo
argument_list|)
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|P
operator|+
name|algo
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCommaSeparated
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expected
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"MD2"
argument_list|,
literal|"d768c8e27b0b52c6eaabfaa7122d1d4f"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"MD5"
argument_list|,
literal|"59f626e09a8c16ab6dbc2800c685f772"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA1"
argument_list|,
literal|"7a1f001d163ac90d8ea54c050faf5a38079788a6"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA256"
argument_list|,
literal|"c4b7fab030a8b6a9d6691f6699ac8e6f"
operator|+
literal|"82bc53764a0f1430d134ae3b70c32654"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA384"
argument_list|,
literal|"ebe368b9326fef44408290724d187553"
operator|+
literal|"8b8a6923fdf251ddab72c6e4b5d54160"
operator|+
literal|"9db917ba4260d1767995a844d8d654df"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA512"
argument_list|,
literal|"ee46d973ee1852c018580c242955974d"
operator|+
literal|"da4c21f36b54d7acd06fcf68e974663b"
operator|+
literal|"fed1d256875be58d22beacf178154cc3"
operator|+
literal|"a1178cb73443deaa53aa0840324708bb"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA3-512"
argument_list|,
literal|"04337f667a250348a1acb992863b3ddc"
operator|+
literal|"eab38365c206c18d356d2b31675ad669"
operator|+
literal|"5fb5497f4e79b11640aefbb8042a5dbb"
operator|+
literal|"7ec6c2c6c1b6e19210453591c52cb6eb"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|put
argument_list|(
literal|"SHA1"
argument_list|,
literal|"PIPQAHIWHLEQ3DVFJQCQ7L22HADZPCFG"
argument_list|)
expr_stmt|;
comment|//test comma separated
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|XMLResult
name|xml
init|=
name|getXML
argument_list|(
literal|"test_recursive_embedded.docx"
argument_list|,
operator|new
name|DigestingParser
argument_list|(
name|p
argument_list|,
operator|new
name|BouncyCastleDigester
argument_list|(
name|UNLIMITED
argument_list|,
literal|"MD5,SHA256,SHA384,SHA512,SHA3-512,SHA1:32"
argument_list|)
argument_list|)
argument_list|,
name|m
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|algo
range|:
operator|new
name|String
index|[]
block|{
literal|"MD5"
block|,
literal|"SHA256"
block|,
literal|"SHA384"
block|,
literal|"SHA512"
block|,
literal|"SHA3-512"
block|,
literal|"SHA1"
block|}
control|)
block|{
name|assertEquals
argument_list|(
name|algo
argument_list|,
name|expected
operator|.
name|get
argument_list|(
name|algo
argument_list|)
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|P
operator|+
name|algo
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertNull
argument_list|(
name|m
operator|.
name|get
argument_list|(
name|P
operator|+
literal|"MD2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|expectedMD5
init|=
literal|"59f626e09a8c16ab6dbc2800c685f772"
decl_stmt|;
name|Metadata
name|m
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|XMLResult
name|xml
init|=
name|getXML
argument_list|(
literal|"test_recursive_embedded.docx"
argument_list|,
operator|new
name|DigestingParser
argument_list|(
name|p
argument_list|,
operator|new
name|BouncyCastleDigester
argument_list|(
literal|100
argument_list|,
literal|"MD5"
argument_list|)
argument_list|)
argument_list|,
name|m
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedMD5
argument_list|,
name|m
operator|.
name|get
argument_list|(
name|P
operator|+
literal|"MD5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testNegativeMaxMarkLength
parameter_list|()
throws|throws
name|Exception
block|{
name|getXML
argument_list|(
literal|"test_recursive_embedded.docx"
argument_list|,
operator|new
name|DigestingParser
argument_list|(
name|p
argument_list|,
operator|new
name|BouncyCastleDigester
argument_list|(
operator|-
literal|1
argument_list|,
literal|"MD5"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testUnrecognizedEncodingOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|getXML
argument_list|(
literal|"test_recursive_embedded.docx"
argument_list|,
operator|new
name|DigestingParser
argument_list|(
name|p
argument_list|,
operator|new
name|BouncyCastleDigester
argument_list|(
literal|100000
argument_list|,
literal|"MD5:33"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleCombinations
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tmp
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|"tika-digesting-parser-test"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
try|try
block|{
comment|//try some random lengths
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|testMulti
argument_list|(
name|tmp
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
argument_list|,
name|random
operator|.
name|nextInt
argument_list|(
literal|100000
argument_list|)
argument_list|,
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//try specific lengths
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|1000
argument_list|,
literal|100000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|1000
argument_list|,
literal|100000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|10000
argument_list|,
literal|10001
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|10000
argument_list|,
literal|10001
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|10000
argument_list|,
literal|10000
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|10000
argument_list|,
literal|10000
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|10000
argument_list|,
literal|9999
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|10000
argument_list|,
literal|9999
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|1000
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|1000
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|1000
argument_list|,
literal|10
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|1000
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|1000
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|1000
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|testMulti
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Files
operator|.
name|delete
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testMulti
parameter_list|(
name|Path
name|tmp
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|int
name|markLimit
parameter_list|,
name|boolean
name|useTikaInputStream
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|os
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|Files
operator|.
name|newOutputStream
argument_list|(
name|tmp
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileLength
condition|;
name|i
operator|++
control|)
block|{
name|os
operator|.
name|write
argument_list|(
name|random
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|Metadata
name|truth
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|addTruth
argument_list|(
name|tmp
argument_list|,
literal|"MD5"
argument_list|,
name|truth
argument_list|)
expr_stmt|;
name|addTruth
argument_list|(
name|tmp
argument_list|,
literal|"SHA1"
argument_list|,
name|truth
argument_list|)
expr_stmt|;
name|addTruth
argument_list|(
name|tmp
argument_list|,
literal|"SHA512"
argument_list|,
name|truth
argument_list|)
expr_stmt|;
name|checkMulti
argument_list|(
name|truth
argument_list|,
name|tmp
argument_list|,
name|fileLength
argument_list|,
name|markLimit
argument_list|,
name|useTikaInputStream
argument_list|,
literal|"SHA512"
argument_list|,
literal|"SHA1"
argument_list|,
literal|"MD5"
argument_list|)
expr_stmt|;
name|checkMulti
argument_list|(
name|truth
argument_list|,
name|tmp
argument_list|,
name|fileLength
argument_list|,
name|markLimit
argument_list|,
name|useTikaInputStream
argument_list|,
literal|"MD5"
argument_list|,
literal|"SHA1"
argument_list|)
expr_stmt|;
name|checkMulti
argument_list|(
name|truth
argument_list|,
name|tmp
argument_list|,
name|fileLength
argument_list|,
name|markLimit
argument_list|,
name|useTikaInputStream
argument_list|,
literal|"SHA1"
argument_list|,
literal|"SHA512"
argument_list|,
literal|"MD5"
argument_list|)
expr_stmt|;
name|checkMulti
argument_list|(
name|truth
argument_list|,
name|tmp
argument_list|,
name|fileLength
argument_list|,
name|markLimit
argument_list|,
name|useTikaInputStream
argument_list|,
literal|"SHA1"
argument_list|)
expr_stmt|;
name|checkMulti
argument_list|(
name|truth
argument_list|,
name|tmp
argument_list|,
name|fileLength
argument_list|,
name|markLimit
argument_list|,
name|useTikaInputStream
argument_list|,
literal|"MD5"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkMulti
parameter_list|(
name|Metadata
name|truth
parameter_list|,
name|Path
name|tmp
parameter_list|,
name|int
name|fileLength
parameter_list|,
name|int
name|markLimit
parameter_list|,
name|boolean
name|useTikaInputStream
parameter_list|,
name|String
modifier|...
name|algos
parameter_list|)
throws|throws
name|IOException
block|{
name|Metadata
name|result
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|BouncyCastleDigester
name|digester
init|=
operator|new
name|BouncyCastleDigester
argument_list|(
name|markLimit
argument_list|,
name|StringUtils
operator|.
name|join
argument_list|(
name|algos
argument_list|,
literal|","
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|is
init|=
name|useTikaInputStream
condition|?
name|TikaInputStream
operator|.
name|get
argument_list|(
name|tmp
argument_list|)
else|:
operator|new
name|BufferedInputStream
argument_list|(
name|Files
operator|.
name|newInputStream
argument_list|(
name|tmp
argument_list|)
argument_list|)
init|)
block|{
name|digester
operator|.
name|digest
argument_list|(
name|is
argument_list|,
name|result
argument_list|,
operator|new
name|ParseContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|algo
range|:
name|algos
control|)
block|{
name|String
name|truthValue
init|=
name|truth
operator|.
name|get
argument_list|(
name|P
operator|+
name|algo
argument_list|)
decl_stmt|;
name|String
name|resultValue
init|=
name|result
operator|.
name|get
argument_list|(
name|P
operator|+
name|algo
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"truth"
argument_list|,
name|truthValue
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"result (fileLength="
operator|+
name|fileLength
operator|+
literal|", markLimit="
operator|+
name|markLimit
operator|+
literal|")"
argument_list|,
name|resultValue
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"fileLength("
operator|+
name|fileLength
operator|+
literal|") markLimit("
operator|+
name|markLimit
operator|+
literal|") useTikaInputStream("
operator|+
name|useTikaInputStream
operator|+
literal|") "
operator|+
literal|"algorithm("
operator|+
name|algo
operator|+
literal|") seed("
operator|+
name|SEED
operator|+
literal|")"
argument_list|,
name|truthValue
argument_list|,
name|resultValue
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addTruth
parameter_list|(
name|Path
name|tmp
parameter_list|,
name|String
name|algo
parameter_list|,
name|Metadata
name|truth
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|digest
init|=
literal|null
decl_stmt|;
comment|//for now, rely on CommonsDigest for truth
try|try
init|(
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|tmp
argument_list|)
init|)
block|{
if|if
condition|(
literal|"MD2"
operator|.
name|equals
argument_list|(
name|algo
argument_list|)
condition|)
block|{
name|digest
operator|=
name|DigestUtils
operator|.
name|md2Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"MD5"
operator|.
name|equals
argument_list|(
name|algo
argument_list|)
condition|)
block|{
name|digest
operator|=
name|DigestUtils
operator|.
name|md5Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"SHA1"
operator|.
name|equals
argument_list|(
name|algo
argument_list|)
condition|)
block|{
name|digest
operator|=
name|DigestUtils
operator|.
name|sha1Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"SHA256"
operator|.
name|equals
argument_list|(
name|algo
argument_list|)
condition|)
block|{
name|digest
operator|=
name|DigestUtils
operator|.
name|sha256Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"SHA384"
operator|.
name|equals
argument_list|(
name|algo
argument_list|)
condition|)
block|{
name|digest
operator|=
name|DigestUtils
operator|.
name|sha384Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"SHA512"
operator|.
name|equals
argument_list|(
name|algo
argument_list|)
condition|)
block|{
name|digest
operator|=
name|DigestUtils
operator|.
name|sha512Hex
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Sorry, not aware of algorithm: "
operator|+
name|algo
argument_list|)
throw|;
block|}
block|}
name|truth
operator|.
name|set
argument_list|(
name|P
operator|+
name|algo
argument_list|,
name|digest
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
