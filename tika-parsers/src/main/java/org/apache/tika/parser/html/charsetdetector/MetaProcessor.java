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
operator|.
name|html
operator|.
name|charsetdetector
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|html
operator|.
name|charsetdetector
operator|.
name|PreScanner
operator|.
name|getEncodingFromMeta
import|;
end_import

begin_comment
comment|/**  * A class to process the attributes of an HTML meta tag in order to extract a character set.  * The user should repeatedly call {@link #processAttribute} on each attributes of the tag,  * then update its current detection result with {@link #updateDetectedCharset(CharsetDetectionResult)}  *<p>  * The algorithm implemented is meant to match the one described by the W3C here:  * https://html.spec.whatwg.org/multipage/parsing.html#prescan-a-byte-stream-to-determine-its-encoding  */
end_comment

begin_class
class|class
name|MetaProcessor
block|{
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|attributeNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|gotPragma
init|=
literal|false
decl_stmt|;
specifier|private
name|Boolean
name|needPragma
init|=
literal|null
decl_stmt|;
comment|// needPragma can be null, true, or false
specifier|private
name|CharsetDetectionResult
name|detectionResult
init|=
name|CharsetDetectionResult
operator|.
name|notFound
argument_list|()
decl_stmt|;
name|void
name|updateDetectedCharset
parameter_list|(
name|CharsetDetectionResult
name|currentDetectionResult
parameter_list|)
block|{
if|if
condition|(
name|detectionResult
operator|.
name|isFound
argument_list|()
operator|&&
name|needPragma
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|needPragma
operator|&&
operator|!
name|gotPragma
operator|)
condition|)
block|{
name|currentDetectionResult
operator|.
name|setCharset
argument_list|(
name|detectionResult
operator|.
name|getCharset
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|void
name|processAttribute
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attribute
parameter_list|)
block|{
comment|// Ignore duplicate attributes
if|if
condition|(
name|attributeNames
operator|.
name|contains
argument_list|(
name|attribute
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
return|return;
name|attributeNames
operator|.
name|add
argument_list|(
name|attribute
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
comment|// Handle charset-related attributes
switch|switch
condition|(
name|attribute
operator|.
name|getKey
argument_list|()
condition|)
block|{
case|case
literal|"http-equiv"
case|:
if|if
condition|(
name|attribute
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"content-type"
argument_list|)
condition|)
name|gotPragma
operator|=
literal|true
expr_stmt|;
break|break;
case|case
literal|"content"
case|:
name|String
name|charsetName
init|=
name|getEncodingFromMeta
argument_list|(
name|attribute
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|detectionResult
operator|.
name|isFound
argument_list|()
operator|&&
name|charsetName
operator|!=
literal|null
condition|)
block|{
name|detectionResult
operator|.
name|find
argument_list|(
name|charsetName
argument_list|)
expr_stmt|;
name|needPragma
operator|=
literal|true
expr_stmt|;
block|}
break|break;
case|case
literal|"charset"
case|:
name|detectionResult
operator|.
name|find
argument_list|(
name|attribute
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|needPragma
operator|=
literal|false
expr_stmt|;
break|break;
default|default:
comment|// Ignore non-charset related attributes
block|}
block|}
block|}
end_class

end_unit

