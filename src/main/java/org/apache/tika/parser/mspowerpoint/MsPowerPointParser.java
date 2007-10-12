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
name|mspowerpoint
package|;
end_package

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
name|poi
operator|.
name|poifs
operator|.
name|eventfilesystem
operator|.
name|POIFSReader
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
name|ms
operator|.
name|MSParser
import|;
end_import

begin_comment
comment|/**  * Power point parser  */
end_comment

begin_class
specifier|public
class|class
name|MsPowerPointParser
extends|extends
name|MSParser
block|{
specifier|protected
name|String
name|extractText
parameter_list|(
name|InputStream
name|input
parameter_list|)
throws|throws
name|Exception
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|POIFSReader
name|reader
init|=
operator|new
name|POIFSReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|registerListener
argument_list|(
operator|new
name|ContentReaderListener
argument_list|(
name|builder
argument_list|)
argument_list|,
name|PPTConstants
operator|.
name|POWERPOINT_DOCUMENT
argument_list|)
expr_stmt|;
name|reader
operator|.
name|read
argument_list|(
name|input
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

