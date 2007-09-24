begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  Copyright 2004 Ryan Ackley  *  *  Licensed under the Apache License, Version 2.0 (the "License");  *  you may not use this file except in compliance with the License.  *  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|msword
package|;
end_package

begin_comment
comment|/**  * This class stores info about the data structure describing a chunk of text  * in a Word document. Specifically, whether or not a Range of text uses  * unicode or Cp1252 encoding.  *  *  */
end_comment

begin_class
class|class
name|WordTextPiece
block|{
specifier|private
name|int
name|_fcStart
decl_stmt|;
specifier|private
name|boolean
name|_usesUnicode
decl_stmt|;
specifier|private
name|int
name|_length
decl_stmt|;
specifier|public
name|WordTextPiece
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|unicode
parameter_list|)
block|{
name|_usesUnicode
operator|=
name|unicode
expr_stmt|;
name|_length
operator|=
name|length
expr_stmt|;
name|_fcStart
operator|=
name|start
expr_stmt|;
block|}
specifier|public
name|boolean
name|usesUnicode
parameter_list|()
block|{
return|return
name|_usesUnicode
return|;
block|}
specifier|public
name|int
name|getStart
parameter_list|()
block|{
return|return
name|_fcStart
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|_length
return|;
block|}
block|}
end_class

end_unit

