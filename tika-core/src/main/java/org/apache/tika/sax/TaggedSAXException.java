begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *   *      http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * A {@link SAXException} wrapper that tags the wrapped exception with  * a given object reference. Both the tag and the wrapped original exception  * can be used to determine further processing when this exception is caught.  */
end_comment

begin_class
specifier|public
class|class
name|TaggedSAXException
extends|extends
name|SAXException
block|{
comment|/**      * The object reference used to tag the exception.      */
specifier|private
specifier|final
name|Object
name|tag
decl_stmt|;
comment|/**      * Creates a tagged wrapper for the given exception.      *      * @param original the exception to be tagged      * @param tag tag object      */
specifier|public
name|TaggedSAXException
parameter_list|(
name|SAXException
name|original
parameter_list|,
name|Object
name|tag
parameter_list|)
block|{
name|super
argument_list|(
name|original
operator|.
name|getMessage
argument_list|()
argument_list|,
name|original
argument_list|)
expr_stmt|;
name|initCause
argument_list|(
name|original
argument_list|)
expr_stmt|;
comment|// SAXException has it's own chaining mechanism!
name|this
operator|.
name|tag
operator|=
name|tag
expr_stmt|;
block|}
comment|/**      * Returns the object reference used as the tag this exception.      *      * @return tag object      */
specifier|public
name|Object
name|getTag
parameter_list|()
block|{
return|return
name|tag
return|;
block|}
comment|/**      * Returns the wrapped exception. The only difference to the overridden      * {@link Throwable#getCause()} method is the narrower return type.      *      * @return wrapped exception      */
annotation|@
name|Override
specifier|public
name|SAXException
name|getCause
parameter_list|()
block|{
return|return
operator|(
name|SAXException
operator|)
name|super
operator|.
name|getCause
argument_list|()
return|;
block|}
block|}
end_class

end_unit

