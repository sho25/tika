begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Until we can find a common standard, we'll use these options.  They  * were mostly derived from PDFBox's AccessPermission, but some can  * apply to other document formats, especially CAN_MODIFY and FILL_IN_FORM.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AccessPermissions
block|{
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"access_permission"
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
decl_stmt|;
comment|/**      * Can any modifications be made to the document      */
name|Property
name|CAN_MODIFY
init|=
name|Property
operator|.
name|externalTextBag
argument_list|(
name|PREFIX
operator|+
literal|"can_modify"
argument_list|)
decl_stmt|;
comment|/**      * Should content be extracted, generally.      */
name|Property
name|EXTRACT_CONTENT
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX
operator|+
literal|"extract_content"
argument_list|)
decl_stmt|;
comment|/**      * Should content be extracted for the purposes      * of accessibility.      */
name|Property
name|EXTRACT_FOR_ACCESSIBILITY
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX
operator|+
literal|"extract_for_accessibility"
argument_list|)
decl_stmt|;
comment|/**      * Can the user insert/rotate/delete pages.      */
name|Property
name|ASSEMBLE_DOCUMENT
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX
operator|+
literal|"assemble_document"
argument_list|)
decl_stmt|;
comment|/**      * Can the user fill in a form      */
name|Property
name|FILL_IN_FORM
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX
operator|+
literal|"fill_in_form"
argument_list|)
decl_stmt|;
comment|/**      * Can the user modify annotations      */
name|Property
name|CAN_MODIFY_ANNOTATIONS
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX
operator|+
literal|"modify_annotations"
argument_list|)
decl_stmt|;
comment|/**      * Can the user print the document      */
name|Property
name|CAN_PRINT
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX
operator|+
literal|"can_print"
argument_list|)
decl_stmt|;
comment|/**      * Can the user print an image-degraded version of the document.      */
name|Property
name|CAN_PRINT_DEGRADED
init|=
name|Property
operator|.
name|externalText
argument_list|(
name|PREFIX
operator|+
literal|"can_print_degraded"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

