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
name|microsoft
operator|.
name|onenote
package|;
end_package

begin_comment
comment|/**  * The JCID structure specifies the type of object and the type of data the object contains. A JCID structure can be  * considered to be an unsigned integer of size four bytes as specified by property set and  * file data object.  *  *<pre>[0,15] - the index</pre>  *<pre>16 - A</pre>  *<pre>17 - B</pre>  *<pre>18 - C</pre>  *<pre>19 - D</pre>  *<pre>20 - E</pre>  *<pre>21 - 31 = reserved</pre>  *<p>  * index (2 bytes): An unsigned integer that specifies the type of object.  *<p>  * A - IsBinary (1 bit): Specifies whether the object contains encryption data transmitted over the File Synchronization via SOAP over  * HTTP Protocol, as specified in [MS-FSSHTTP].  *<p>  * B - IsPropertySet (1 bit): Specifies whether the object contains a property set.  *<p>  * C - IsGraphNode (1 bit): Undefined and MUST be ignored.  *<p>  * D - IsFileData (1 bit): Specifies whether the object is a file data object. If the value of IsFileData is "true", then the values of  * the IsBinary, IsPropertySet, IsGraphNode, and IsReadOnly fields MUST all be false.  *<p>  * E - IsReadOnly (1 bit): Specifies whether the object's data MUST NOT be changed when the object is revised.  *<p>  * reserved (11 bits): MUST be zero, and MUST be ignored.  */
end_comment

begin_class
class|class
name|JCID
block|{
name|long
name|jcid
decl_stmt|;
name|long
name|index
decl_stmt|;
name|boolean
name|isBinary
decl_stmt|;
name|boolean
name|isPropertySet
decl_stmt|;
name|boolean
name|isGraphNode
decl_stmt|;
name|boolean
name|isFileData
decl_stmt|;
name|boolean
name|isReadOnly
decl_stmt|;
comment|/**      * If the value of the JCID.IsPropertySet field is "true" or if only JCID.index is specified, then the data      * for the Object Space Object structure MUST be an ObjectSpaceObjectPropSet structure.      *      * @return true if is ObjectSpaceObjectPropSet. false otherwise.      */
specifier|public
name|boolean
name|isObjectSpaceObjectPropSet
parameter_list|()
block|{
return|return
name|isPropertySet
operator|||
operator|!
name|isBinary
operator|&&
operator|!
name|isGraphNode
operator|&&
operator|!
name|isFileData
operator|&&
operator|!
name|isReadOnly
operator|&&
name|index
operator|>
literal|0
return|;
block|}
specifier|public
name|void
name|loadFrom32BitIndex
parameter_list|(
name|long
name|fullIndex
parameter_list|)
block|{
name|jcid
operator|=
name|fullIndex
expr_stmt|;
name|index
operator|=
name|fullIndex
operator|&
literal|0xffff
expr_stmt|;
name|isBinary
operator|=
operator|(
operator|(
name|fullIndex
operator|>>
literal|16
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
expr_stmt|;
name|isPropertySet
operator|=
operator|(
operator|(
name|fullIndex
operator|>>
literal|17
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
expr_stmt|;
name|isGraphNode
operator|=
operator|(
operator|(
name|fullIndex
operator|>>
literal|18
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
expr_stmt|;
name|isFileData
operator|=
operator|(
operator|(
name|fullIndex
operator|>>
literal|19
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
expr_stmt|;
name|isReadOnly
operator|=
operator|(
operator|(
name|fullIndex
operator|>>
literal|20
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
expr_stmt|;
if|if
condition|(
operator|(
name|fullIndex
operator|>>
literal|21
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"RESERVED_NONZERO"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"JCID{"
operator|+
literal|"jcid="
operator|+
name|JCIDPropertySetTypeEnum
operator|.
name|of
argument_list|(
name|jcid
argument_list|)
operator|+
literal|" (0x"
operator|+
name|Long
operator|.
name|toHexString
argument_list|(
name|jcid
argument_list|)
operator|+
literal|")"
operator|+
literal|", index="
operator|+
name|index
operator|+
literal|", isBinary="
operator|+
name|isBinary
operator|+
literal|", isPropertySet="
operator|+
name|isPropertySet
operator|+
literal|", isGraphNode="
operator|+
name|isGraphNode
operator|+
literal|", isFileData="
operator|+
name|isFileData
operator|+
literal|", isReadOnly="
operator|+
name|isReadOnly
operator|+
literal|'}'
return|;
block|}
specifier|public
name|long
name|getJcid
parameter_list|()
block|{
return|return
name|jcid
return|;
block|}
specifier|public
name|void
name|setJcid
parameter_list|(
name|long
name|jcid
parameter_list|)
block|{
name|this
operator|.
name|jcid
operator|=
name|jcid
expr_stmt|;
block|}
specifier|public
name|long
name|getIndex
parameter_list|()
block|{
return|return
name|index
return|;
block|}
specifier|public
name|void
name|setIndex
parameter_list|(
name|long
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
specifier|public
name|boolean
name|isBinary
parameter_list|()
block|{
return|return
name|isBinary
return|;
block|}
specifier|public
name|void
name|setBinary
parameter_list|(
name|boolean
name|binary
parameter_list|)
block|{
name|isBinary
operator|=
name|binary
expr_stmt|;
block|}
specifier|public
name|boolean
name|isPropertySet
parameter_list|()
block|{
return|return
name|isPropertySet
return|;
block|}
specifier|public
name|void
name|setPropertySet
parameter_list|(
name|boolean
name|propertySet
parameter_list|)
block|{
name|isPropertySet
operator|=
name|propertySet
expr_stmt|;
block|}
specifier|public
name|boolean
name|isGraphNode
parameter_list|()
block|{
return|return
name|isGraphNode
return|;
block|}
specifier|public
name|void
name|setGraphNode
parameter_list|(
name|boolean
name|graphNode
parameter_list|)
block|{
name|isGraphNode
operator|=
name|graphNode
expr_stmt|;
block|}
specifier|public
name|boolean
name|isFileData
parameter_list|()
block|{
return|return
name|isFileData
return|;
block|}
specifier|public
name|void
name|setFileData
parameter_list|(
name|boolean
name|fileData
parameter_list|)
block|{
name|isFileData
operator|=
name|fileData
expr_stmt|;
block|}
specifier|public
name|boolean
name|isReadOnly
parameter_list|()
block|{
return|return
name|isReadOnly
return|;
block|}
specifier|public
name|void
name|setReadOnly
parameter_list|(
name|boolean
name|readOnly
parameter_list|)
block|{
name|isReadOnly
operator|=
name|readOnly
expr_stmt|;
block|}
block|}
end_class

end_unit

