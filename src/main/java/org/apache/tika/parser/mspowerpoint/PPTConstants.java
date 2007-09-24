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

begin_comment
comment|/**  * Package protected class for the required internal MS PowerPoint constants.  *   *   */
end_comment

begin_class
class|class
name|PPTConstants
block|{
comment|/** ID of master slide */
specifier|public
specifier|static
specifier|final
name|long
name|PPT_MASTERSLIDE
init|=
literal|1024L
decl_stmt|;
comment|/** ATOM ID of slide */
specifier|public
specifier|static
name|long
name|PPT_ATOM_SLIDE
init|=
literal|1007l
decl_stmt|;
comment|/** ATOM ID of notes */
specifier|public
specifier|static
specifier|final
name|long
name|PPT_ATOM_NOTES
init|=
literal|1009L
decl_stmt|;
comment|/** ATOM ID of persistend slide */
specifier|public
specifier|static
specifier|final
name|long
name|PPT_ATOM_SLIDEPERSISTANT
init|=
literal|1011L
decl_stmt|;
comment|/** ATOM ID of text char area. Holds text in byte swapped unicode form. */
specifier|public
specifier|static
specifier|final
name|long
name|PPT_ATOM_TEXTCHAR
init|=
literal|4000L
decl_stmt|;
comment|/** ATOM ID of text byte area. Holds text in ascii form */
specifier|public
specifier|static
specifier|final
name|long
name|PPT_ATOM_TEXTBYTE
init|=
literal|4008L
decl_stmt|;
comment|/** ATOM ID of user edit area */
specifier|public
specifier|static
specifier|final
name|long
name|PPT_ATOM_USEREDIT
init|=
literal|4085L
decl_stmt|;
comment|/** ATOM ID of drawing group area */
specifier|public
specifier|static
specifier|final
name|long
name|PPT_ATOM_DRAWINGGROUP
init|=
literal|61448L
decl_stmt|;
comment|/** Name for PowerPoint Documents within the file */
specifier|public
specifier|static
specifier|final
name|String
name|POWERPOINT_DOCUMENT
init|=
literal|"PowerPoint Document"
decl_stmt|;
comment|/**    * Protected constructor to prevent instantiation.    */
specifier|protected
name|PPTConstants
parameter_list|()
block|{
comment|// nothing
block|}
block|}
end_class

end_unit

