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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_comment
comment|/**  * Package protected class for a MS Powerpoint slide.  *   * @author Stephan Strittmatter - http://www.sybit.de  *   * @version 1.0  */
end_comment

begin_class
class|class
name|Slide
block|{
comment|/** Holds the Slide Number */
specifier|protected
specifier|transient
specifier|final
name|long
name|slideNumber
decl_stmt|;
comment|/** Holds the contents of the Slide */
specifier|protected
specifier|transient
specifier|final
name|List
comment|/*<String> */
name|contents
decl_stmt|;
comment|/**    * Initialise the Object for holding the contents of Power Point Slide    *     * @param number    */
specifier|public
name|Slide
parameter_list|(
name|long
name|number
parameter_list|)
block|{
name|this
operator|.
name|slideNumber
operator|=
name|number
expr_stmt|;
name|this
operator|.
name|contents
operator|=
operator|new
name|Vector
comment|/*<String> */
argument_list|()
expr_stmt|;
block|}
comment|/**    * Add the Content of Slide to this Object    *     * @param content    */
specifier|public
name|void
name|addContent
parameter_list|(
name|String
name|content
parameter_list|)
block|{
name|this
operator|.
name|contents
operator|.
name|add
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
comment|/**    * returns the contents of slide as a vector object    *     * @return Vector    */
specifier|public
name|List
name|getContent
parameter_list|()
block|{
return|return
name|this
operator|.
name|contents
return|;
block|}
comment|/**    * returns the slide value    *     * @return long    */
specifier|public
name|long
name|getSlideNumber
parameter_list|()
block|{
return|return
name|this
operator|.
name|slideNumber
return|;
block|}
block|}
end_class

end_unit

