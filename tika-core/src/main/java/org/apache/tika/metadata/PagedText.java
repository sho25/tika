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
name|metadata
package|;
end_package

begin_comment
comment|/**  * XMP Paged-text schema. This is a collection of  * {@link Property property definition} constants for the paged text  * properties defined in the XMP standard.  *  * @since Apache Tika 0.8  * @see<a href="http://www.adobe.com/devnet/xmp/pdfs/XMPSpecificationPart2.pdf"  *>XMP Specification, Part 2: Standard Schemas</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|PagedText
block|{
comment|/**      * "The number of pages in the document (including any in contained      * documents)."      */
name|Property
name|N_PAGES
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
literal|"xmpTPg:NPages"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

