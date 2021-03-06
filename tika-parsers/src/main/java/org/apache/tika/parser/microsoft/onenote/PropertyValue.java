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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|exception
operator|.
name|TikaMemoryLimitException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
class|class
name|PropertyValue
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PropertyValue
operator|.
name|class
argument_list|)
decl_stmt|;
name|OneNotePropertyId
name|propertyId
init|=
operator|new
name|OneNotePropertyId
argument_list|()
decl_stmt|;
comment|// union of one of these things based on the type of the corresponding PropertyID
name|long
name|scalar
decl_stmt|;
comment|// holds a boolean value if type = 0x2, retrieved from header
comment|// either ObjectID or ObjectSpaceID or ContextID (single value in array)
comment|// either ArrayOfObjectIDs or ArrayOfObjectSpaceIDs or ArrayOfContextID
name|List
argument_list|<
name|CompactID
argument_list|>
name|compactIDs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|PropertySet
name|propertySet
init|=
operator|new
name|PropertySet
argument_list|()
decl_stmt|;
comment|// or used to house a single value
name|FileChunkReference
name|rawData
init|=
operator|new
name|FileChunkReference
argument_list|()
decl_stmt|;
comment|// FourBytesOfLengthFollowedByData
specifier|public
name|void
name|print
parameter_list|(
name|OneNoteDocument
name|document
parameter_list|,
name|OneNotePtr
name|pointer
parameter_list|,
name|int
name|indentLevel
parameter_list|)
throws|throws
name|IOException
throws|,
name|TikaMemoryLimitException
block|{
name|boolean
name|isRawText
init|=
literal|true
decl_stmt|;
comment|//std::string(get_property_id_name(propertyId.id)).find("TextE")!=-1;
name|long
name|type
init|=
name|propertyId
operator|.
name|type
decl_stmt|;
if|if
condition|(
name|isRawText
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}<{}"
argument_list|,
name|IndentUtil
operator|.
name|getIndent
argument_list|(
name|indentLevel
operator|+
literal|1
argument_list|)
argument_list|,
name|propertyId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|>
literal|0
operator|&&
name|type
operator|<=
literal|6
condition|)
block|{
if|if
condition|(
name|isRawText
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"(%d)"
argument_list|,
name|scalar
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|7
condition|)
block|{
name|OneNotePtr
name|content
init|=
operator|new
name|OneNotePtr
argument_list|(
name|pointer
argument_list|)
decl_stmt|;
name|content
operator|.
name|reposition
argument_list|(
name|rawData
argument_list|)
expr_stmt|;
if|if
condition|(
name|isRawText
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|" ["
argument_list|)
expr_stmt|;
name|content
operator|.
name|dumpHex
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|0x9
operator|||
name|type
operator|==
literal|0x8
operator|||
name|type
operator|==
literal|0xb
operator|||
name|type
operator|==
literal|0xc
operator|||
name|type
operator|==
literal|0xa
operator|||
name|type
operator|==
literal|0xd
condition|)
block|{
name|String
name|xtype
init|=
literal|"contextID"
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|0x8
operator|||
name|type
operator|==
literal|0x9
condition|)
block|{
name|xtype
operator|=
literal|"OIDs"
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
literal|0xa
operator|||
name|type
operator|==
literal|0xb
condition|)
block|{
name|xtype
operator|=
literal|"OSIDS"
expr_stmt|;
block|}
if|if
condition|(
name|isRawText
condition|)
block|{
if|if
condition|(
operator|!
name|compactIDs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|CompactID
name|compactID
range|:
name|compactIDs
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}{}[{}]"
argument_list|,
name|IndentUtil
operator|.
name|getIndent
argument_list|(
name|indentLevel
operator|+
literal|1
argument_list|)
argument_list|,
name|xtype
argument_list|,
name|compactID
argument_list|)
expr_stmt|;
name|FileNodePtr
name|where
init|=
name|document
operator|.
name|guidToObject
operator|.
name|get
argument_list|(
name|compactID
operator|.
name|guid
argument_list|)
decl_stmt|;
if|if
condition|(
name|where
operator|!=
literal|null
condition|)
block|{
name|where
operator|.
name|dereference
argument_list|(
name|document
argument_list|)
operator|.
name|print
argument_list|(
name|document
argument_list|,
name|pointer
argument_list|,
name|indentLevel
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|0x10
operator|||
name|type
operator|==
literal|0x11
condition|)
block|{
if|if
condition|(
name|isRawText
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"SubProperty"
argument_list|)
expr_stmt|;
block|}
name|propertySet
operator|.
name|print
argument_list|(
name|document
argument_list|,
name|pointer
argument_list|,
name|indentLevel
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isRawText
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|OneNotePropertyId
name|getPropertyId
parameter_list|()
block|{
return|return
name|propertyId
return|;
block|}
specifier|public
name|PropertyValue
name|setPropertyId
parameter_list|(
name|OneNotePropertyId
name|propertyId
parameter_list|)
block|{
name|this
operator|.
name|propertyId
operator|=
name|propertyId
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getScalar
parameter_list|()
block|{
return|return
name|scalar
return|;
block|}
specifier|public
name|PropertyValue
name|setScalar
parameter_list|(
name|long
name|scalar
parameter_list|)
block|{
name|this
operator|.
name|scalar
operator|=
name|scalar
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|List
argument_list|<
name|CompactID
argument_list|>
name|getCompactIDs
parameter_list|()
block|{
return|return
name|compactIDs
return|;
block|}
specifier|public
name|PropertyValue
name|setCompactIDs
parameter_list|(
name|List
argument_list|<
name|CompactID
argument_list|>
name|compactIDs
parameter_list|)
block|{
name|this
operator|.
name|compactIDs
operator|=
name|compactIDs
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|PropertySet
name|getPropertySet
parameter_list|()
block|{
return|return
name|propertySet
return|;
block|}
specifier|public
name|PropertyValue
name|setPropertySet
parameter_list|(
name|PropertySet
name|propertySet
parameter_list|)
block|{
name|this
operator|.
name|propertySet
operator|=
name|propertySet
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getRawData
parameter_list|()
block|{
return|return
name|rawData
return|;
block|}
specifier|public
name|PropertyValue
name|setRawData
parameter_list|(
name|FileChunkReference
name|rawData
parameter_list|)
block|{
name|this
operator|.
name|rawData
operator|=
name|rawData
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

