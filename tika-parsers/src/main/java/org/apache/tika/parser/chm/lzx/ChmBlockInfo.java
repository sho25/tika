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
name|chm
operator|.
name|lzx
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
name|TikaException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|chm
operator|.
name|accessor
operator|.
name|ChmLzxcControlData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|chm
operator|.
name|accessor
operator|.
name|DirectoryListingEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|chm
operator|.
name|exception
operator|.
name|ChmParsingException
import|;
end_import

begin_comment
comment|/**  * A container that contains chm block information such as: i. initial block is  * using to reset main tree ii. start block is using for knowing where to start  * iii. end block is using for knowing where to stop iv. start offset is using  * for knowing where to start reading v. end offset is using for knowing where  * to stop reading  *   */
end_comment

begin_class
specifier|public
class|class
name|ChmBlockInfo
block|{
comment|/* class members */
specifier|private
name|int
name|iniBlock
decl_stmt|;
specifier|private
name|int
name|startBlock
decl_stmt|;
specifier|private
name|int
name|endBlock
decl_stmt|;
specifier|private
name|int
name|startOffset
decl_stmt|;
specifier|private
name|int
name|endOffset
decl_stmt|;
specifier|private
name|ChmBlockInfo
parameter_list|()
block|{      }
annotation|@
name|Deprecated
specifier|public
specifier|static
name|ChmBlockInfo
name|getChmBlockInfoInstance
parameter_list|(
name|DirectoryListingEntry
name|dle
parameter_list|,
name|int
name|bytesPerBlock
parameter_list|,
name|ChmLzxcControlData
name|clcd
parameter_list|)
throws|throws
name|ChmParsingException
block|{
return|return
name|getChmBlockInfoInstance
argument_list|(
name|dle
argument_list|,
name|bytesPerBlock
argument_list|,
name|clcd
argument_list|,
operator|new
name|ChmBlockInfo
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ChmBlockInfo
name|getChmBlockInfoInstance
parameter_list|(
name|DirectoryListingEntry
name|dle
parameter_list|,
name|int
name|bytesPerBlock
parameter_list|,
name|ChmLzxcControlData
name|clcd
parameter_list|,
name|ChmBlockInfo
name|chmBlockInfo
parameter_list|)
throws|throws
name|ChmParsingException
block|{
if|if
condition|(
name|chmBlockInfo
operator|==
literal|null
condition|)
block|{
name|chmBlockInfo
operator|=
operator|new
name|ChmBlockInfo
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|validateParameters
argument_list|(
name|dle
argument_list|,
name|bytesPerBlock
argument_list|,
name|clcd
argument_list|,
name|chmBlockInfo
argument_list|)
condition|)
throw|throw
operator|new
name|ChmParsingException
argument_list|(
literal|"Please check you parameters"
argument_list|)
throw|;
name|chmBlockInfo
operator|.
name|setStartBlock
argument_list|(
name|dle
operator|.
name|getOffset
argument_list|()
operator|/
name|bytesPerBlock
argument_list|)
expr_stmt|;
name|chmBlockInfo
operator|.
name|setEndBlock
argument_list|(
operator|(
name|dle
operator|.
name|getOffset
argument_list|()
operator|+
name|dle
operator|.
name|getLength
argument_list|()
operator|)
operator|/
name|bytesPerBlock
argument_list|)
expr_stmt|;
name|chmBlockInfo
operator|.
name|setStartOffset
argument_list|(
name|dle
operator|.
name|getOffset
argument_list|()
operator|%
name|bytesPerBlock
argument_list|)
expr_stmt|;
name|chmBlockInfo
operator|.
name|setEndOffset
argument_list|(
operator|(
name|dle
operator|.
name|getOffset
argument_list|()
operator|+
name|dle
operator|.
name|getLength
argument_list|()
operator|)
operator|%
name|bytesPerBlock
argument_list|)
expr_stmt|;
comment|// potential problem with casting long to int
name|chmBlockInfo
operator|.
name|setIniBlock
argument_list|(
name|chmBlockInfo
operator|.
name|startBlock
operator|-
name|chmBlockInfo
operator|.
name|startBlock
operator|%
operator|(
name|int
operator|)
name|clcd
operator|.
name|getResetInterval
argument_list|()
argument_list|)
expr_stmt|;
comment|//                (getChmBlockInfo().startBlock - getChmBlockInfo().startBlock)
comment|//                        % (int) clcd.getResetInterval());
return|return
name|chmBlockInfo
return|;
block|}
comment|/**      * Returns textual representation of ChmBlockInfo      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"iniBlock:="
operator|+
name|getIniBlock
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"startBlock:="
operator|+
name|getStartBlock
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"endBlock:="
operator|+
name|getEndBlock
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"startOffset:="
operator|+
name|getStartOffset
argument_list|()
operator|+
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"endOffset:="
operator|+
name|getEndOffset
argument_list|()
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|boolean
name|validateParameters
parameter_list|(
name|DirectoryListingEntry
name|dle
parameter_list|,
name|int
name|bytesPerBlock
parameter_list|,
name|ChmLzxcControlData
name|clcd
parameter_list|,
name|ChmBlockInfo
name|chmBlockInfo
parameter_list|)
block|{
name|int
name|goodParameter
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|dle
operator|!=
literal|null
condition|)
operator|++
name|goodParameter
expr_stmt|;
if|if
condition|(
name|bytesPerBlock
operator|>
literal|0
condition|)
operator|++
name|goodParameter
expr_stmt|;
if|if
condition|(
name|clcd
operator|!=
literal|null
condition|)
operator|++
name|goodParameter
expr_stmt|;
if|if
condition|(
name|chmBlockInfo
operator|!=
literal|null
condition|)
operator|++
name|goodParameter
expr_stmt|;
return|return
operator|(
name|goodParameter
operator|==
literal|4
operator|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{     }
comment|/**      * Returns an initial block index      *       * @return int      */
specifier|public
name|int
name|getIniBlock
parameter_list|()
block|{
return|return
name|iniBlock
return|;
block|}
comment|/**      * Sets the initial block index      *       * @param iniBlock      *            - int      */
specifier|private
name|void
name|setIniBlock
parameter_list|(
name|int
name|iniBlock
parameter_list|)
block|{
name|this
operator|.
name|iniBlock
operator|=
name|iniBlock
expr_stmt|;
block|}
comment|/**      * Returns the start block index      *       * @return int      */
specifier|public
name|int
name|getStartBlock
parameter_list|()
block|{
return|return
name|startBlock
return|;
block|}
comment|/**      * Sets the start block index      *       * @param startBlock      *            - int      */
specifier|private
name|void
name|setStartBlock
parameter_list|(
name|int
name|startBlock
parameter_list|)
block|{
name|this
operator|.
name|startBlock
operator|=
name|startBlock
expr_stmt|;
block|}
comment|/**      * Returns the end block index      *       * @return - int      */
specifier|public
name|int
name|getEndBlock
parameter_list|()
block|{
return|return
name|endBlock
return|;
block|}
comment|/**      * Sets the end block index      *       * @param endBlock      *            - int      */
specifier|private
name|void
name|setEndBlock
parameter_list|(
name|int
name|endBlock
parameter_list|)
block|{
name|this
operator|.
name|endBlock
operator|=
name|endBlock
expr_stmt|;
block|}
comment|/**      * Returns the start offset index      *       * @return - int      */
specifier|public
name|int
name|getStartOffset
parameter_list|()
block|{
return|return
name|startOffset
return|;
block|}
comment|/**      * Sets the start offset index      *       * @param startOffset      *            - int      */
specifier|private
name|void
name|setStartOffset
parameter_list|(
name|int
name|startOffset
parameter_list|)
block|{
name|this
operator|.
name|startOffset
operator|=
name|startOffset
expr_stmt|;
block|}
comment|/**      * Returns the end offset index      *       * @return - int      */
specifier|public
name|int
name|getEndOffset
parameter_list|()
block|{
return|return
name|endOffset
return|;
block|}
comment|/**      * Sets the end offset index      *       * @param endOffset      *            - int      */
specifier|private
name|void
name|setEndOffset
parameter_list|(
name|int
name|endOffset
parameter_list|)
block|{
name|this
operator|.
name|endOffset
operator|=
name|endOffset
expr_stmt|;
block|}
block|}
end_class

end_unit

