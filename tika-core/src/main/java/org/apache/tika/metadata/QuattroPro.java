begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright 2016 Norconex Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * QuattroPro properties collection.  * @author Pascal Essiembre  */
end_comment

begin_interface
specifier|public
interface|interface
name|QuattroPro
block|{
specifier|public
specifier|static
specifier|final
name|String
name|QUATTROPRO_METADATA_NAME_PREFIX
init|=
literal|"wordperfect"
decl_stmt|;
comment|/**     * ID.     */
name|Property
name|ID
init|=
name|Property
operator|.
name|internalText
argument_list|(
name|QUATTROPRO_METADATA_NAME_PREFIX
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Id"
argument_list|)
decl_stmt|;
comment|/**     * Version.     */
name|Property
name|VERSION
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|QUATTROPRO_METADATA_NAME_PREFIX
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Version"
argument_list|)
decl_stmt|;
comment|/**     * Build.     */
name|Property
name|BUILD
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|QUATTROPRO_METADATA_NAME_PREFIX
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"Build"
argument_list|)
decl_stmt|;
comment|/**     * Lowest version.     */
name|Property
name|LOWEST_VERSION
init|=
name|Property
operator|.
name|internalInteger
argument_list|(
name|QUATTROPRO_METADATA_NAME_PREFIX
operator|+
name|Metadata
operator|.
name|NAMESPACE_PREFIX_DELIMITER
operator|+
literal|"LowestVersion"
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

