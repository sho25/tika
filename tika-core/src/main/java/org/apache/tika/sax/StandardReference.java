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
name|sax
package|;
end_package

begin_comment
comment|/**  * Class that represents a standard reference.  *  */
end_comment

begin_class
specifier|public
class|class
name|StandardReference
block|{
specifier|private
name|String
name|mainOrganization
decl_stmt|;
specifier|private
name|String
name|separator
decl_stmt|;
specifier|private
name|String
name|secondOrganization
decl_stmt|;
specifier|private
name|String
name|identifier
decl_stmt|;
specifier|private
name|double
name|score
decl_stmt|;
specifier|private
name|StandardReference
parameter_list|(
name|String
name|mainOrganizationAcronym
parameter_list|,
name|String
name|separator
parameter_list|,
name|String
name|secondOrganizationAcronym
parameter_list|,
name|String
name|identifier
parameter_list|,
name|double
name|score
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|mainOrganization
operator|=
name|mainOrganizationAcronym
expr_stmt|;
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
name|this
operator|.
name|secondOrganization
operator|=
name|secondOrganizationAcronym
expr_stmt|;
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
specifier|public
name|String
name|getMainOrganizationAcronym
parameter_list|()
block|{
return|return
name|mainOrganization
return|;
block|}
specifier|public
name|void
name|setMainOrganizationAcronym
parameter_list|(
name|String
name|mainOrganizationAcronym
parameter_list|)
block|{
name|this
operator|.
name|mainOrganization
operator|=
name|mainOrganizationAcronym
expr_stmt|;
block|}
specifier|public
name|String
name|getSeparator
parameter_list|()
block|{
return|return
name|separator
return|;
block|}
specifier|public
name|void
name|setSeparator
parameter_list|(
name|String
name|separator
parameter_list|)
block|{
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
block|}
specifier|public
name|String
name|getSecondOrganizationAcronym
parameter_list|()
block|{
return|return
name|secondOrganization
return|;
block|}
specifier|public
name|void
name|setSecondOrganizationAcronym
parameter_list|(
name|String
name|secondOrganizationAcronym
parameter_list|)
block|{
name|this
operator|.
name|secondOrganization
operator|=
name|secondOrganizationAcronym
expr_stmt|;
block|}
specifier|public
name|String
name|getIdentifier
parameter_list|()
block|{
return|return
name|identifier
return|;
block|}
specifier|public
name|void
name|setIdentifier
parameter_list|(
name|String
name|identifier
parameter_list|)
block|{
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
block|}
specifier|public
name|double
name|getScore
parameter_list|()
block|{
return|return
name|score
return|;
block|}
specifier|public
name|void
name|setScore
parameter_list|(
name|double
name|score
parameter_list|)
block|{
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|standardReference
init|=
name|mainOrganization
decl_stmt|;
if|if
condition|(
name|separator
operator|!=
literal|null
operator|&&
operator|!
name|separator
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|standardReference
operator|+=
name|separator
operator|+
name|secondOrganization
expr_stmt|;
block|}
name|standardReference
operator|+=
literal|" "
operator|+
name|identifier
expr_stmt|;
return|return
name|standardReference
return|;
block|}
specifier|public
specifier|static
class|class
name|StandardReferenceBuilder
block|{
specifier|private
name|String
name|mainOrganization
decl_stmt|;
specifier|private
name|String
name|separator
decl_stmt|;
specifier|private
name|String
name|secondOrganization
decl_stmt|;
specifier|private
name|String
name|identifier
decl_stmt|;
specifier|private
name|double
name|score
decl_stmt|;
specifier|public
name|StandardReferenceBuilder
parameter_list|(
name|String
name|mainOrganization
parameter_list|,
name|String
name|identifier
parameter_list|)
block|{
name|this
operator|.
name|mainOrganization
operator|=
name|mainOrganization
expr_stmt|;
name|this
operator|.
name|separator
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|secondOrganization
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
name|this
operator|.
name|score
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|StandardReferenceBuilder
name|setSecondOrganization
parameter_list|(
name|String
name|separator
parameter_list|,
name|String
name|secondOrganization
parameter_list|)
block|{
name|this
operator|.
name|separator
operator|=
name|separator
expr_stmt|;
name|this
operator|.
name|secondOrganization
operator|=
name|secondOrganization
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|StandardReferenceBuilder
name|setScore
parameter_list|(
name|double
name|score
parameter_list|)
block|{
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|StandardReference
name|build
parameter_list|()
block|{
return|return
operator|new
name|StandardReference
argument_list|(
name|mainOrganization
argument_list|,
name|separator
argument_list|,
name|secondOrganization
argument_list|,
name|identifier
argument_list|,
name|score
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

