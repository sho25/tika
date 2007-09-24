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
operator|.
name|chp
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|poifs
operator|.
name|common
operator|.
name|POIFSConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|util
operator|.
name|LittleEndian
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|model
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hwpf
operator|.
name|model
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * This class holds all of the character formatting properties from a Word  * 6.0/95 document.  *  *  */
end_comment

begin_class
specifier|public
class|class
name|Word6CHPBinTable
block|{
comment|/** List of character properties.*/
name|ArrayList
name|_textRuns
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/**    * Constructor used to read a binTable in from a Word document.    *    * @param documentStream The POIFS "WordDocument" stream from a Word document    * @param offset The offset of the Chp bin table in the main stream.    * @param size The size of the Chp bin table in the main stream.    * @param fcMin The start of text in the main stream.    */
specifier|public
name|Word6CHPBinTable
parameter_list|(
name|byte
index|[]
name|documentStream
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|fcMin
parameter_list|)
block|{
name|PlexOfCps
name|binTable
init|=
operator|new
name|PlexOfCps
argument_list|(
name|documentStream
argument_list|,
name|offset
argument_list|,
name|size
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|int
name|length
init|=
name|binTable
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|length
condition|;
name|x
operator|++
control|)
block|{
name|GenericPropertyNode
name|node
init|=
name|binTable
operator|.
name|getProperty
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|int
name|pageNum
init|=
name|LittleEndian
operator|.
name|getShort
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|node
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|pageOffset
init|=
name|POIFSConstants
operator|.
name|BIG_BLOCK_SIZE
operator|*
name|pageNum
decl_stmt|;
name|CHPFormattedDiskPage
name|cfkp
init|=
operator|new
name|CHPFormattedDiskPage
argument_list|(
name|documentStream
argument_list|,
name|pageOffset
argument_list|,
name|fcMin
argument_list|)
decl_stmt|;
name|int
name|fkpSize
init|=
name|cfkp
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|fkpSize
condition|;
name|y
operator|++
control|)
block|{
name|_textRuns
operator|.
name|add
argument_list|(
name|cfkp
operator|.
name|getCHPX
argument_list|(
name|y
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|List
name|getTextRuns
parameter_list|()
block|{
return|return
name|_textRuns
return|;
block|}
block|}
end_class

end_unit

