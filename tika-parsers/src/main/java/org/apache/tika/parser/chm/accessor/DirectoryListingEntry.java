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
name|accessor
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
name|assertion
operator|.
name|ChmAssert
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
name|core
operator|.
name|ChmCommons
import|;
end_import

begin_comment
comment|/**  * The format of a directory listing entry is as follows: BYTE: length of name  * BYTEs: name (UTF-8 encoded) ENCINT: content section ENCINT: offset ENCINT:  * length The offset is from the beginning of the content section the file is  * in, after the section has been decompressed (if appropriate). The length also  * refers to length of the file in the section after decompression. There are  * two kinds of file represented in the directory: user data and format related  * files. The files which are format-related have names which begin with '::',  * the user data files have names which begin with "/".  *   */
end_comment

begin_class
specifier|public
class|class
name|DirectoryListingEntry
block|{
comment|/* Length of the entry name */
specifier|private
name|int
name|name_length
decl_stmt|;
comment|/* Entry name or directory name */
specifier|private
name|String
name|name
decl_stmt|;
comment|/* Entry type */
specifier|private
name|ChmCommons
operator|.
name|EntryType
name|entryType
decl_stmt|;
comment|/* Entry offset */
specifier|private
name|int
name|offset
decl_stmt|;
comment|/* Entry size */
specifier|private
name|int
name|length
decl_stmt|;
specifier|public
name|DirectoryListingEntry
parameter_list|()
block|{      }
comment|/**      * Constructs directoryListingEntry      *       * @param name_length      *            int      * @param name      *            String      * @param isCompressed      *            ChmCommons.EntryType      * @param offset      *            int      * @param length      *            int      * @throws TikaException       */
specifier|public
name|DirectoryListingEntry
parameter_list|(
name|int
name|name_length
parameter_list|,
name|String
name|name
parameter_list|,
name|ChmCommons
operator|.
name|EntryType
name|isCompressed
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|TikaException
block|{
name|ChmAssert
operator|.
name|assertDirectoryListingEntry
argument_list|(
name|name_length
argument_list|,
name|name
argument_list|,
name|isCompressed
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|setNameLength
argument_list|(
name|name_length
argument_list|)
expr_stmt|;
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|setEntryType
argument_list|(
name|isCompressed
argument_list|)
expr_stmt|;
name|setOffset
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|setLength
argument_list|(
name|length
argument_list|)
expr_stmt|;
block|}
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
literal|"name_length:="
operator|+
name|getNameLength
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
name|sb
operator|.
name|append
argument_list|(
literal|"name:="
operator|+
name|getName
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
name|sb
operator|.
name|append
argument_list|(
literal|"entryType:="
operator|+
name|getEntryType
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
name|sb
operator|.
name|append
argument_list|(
literal|"offset:="
operator|+
name|getOffset
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
name|sb
operator|.
name|append
argument_list|(
literal|"length:="
operator|+
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns an entry name length      *       * @return int      */
specifier|public
name|int
name|getNameLength
parameter_list|()
block|{
return|return
name|name_length
return|;
block|}
comment|/**      * Sets an entry name length      *       * @param name_length      *            int      */
specifier|protected
name|void
name|setNameLength
parameter_list|(
name|int
name|name_length
parameter_list|)
block|{
name|this
operator|.
name|name_length
operator|=
name|name_length
expr_stmt|;
block|}
comment|/**      * Returns an entry name      *       * @return String      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Sets entry name      *       * @param name      *            String      */
specifier|protected
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**      * Returns ChmCommons.EntryType (COMPRESSED or UNCOMPRESSED)      *       * @return ChmCommons.EntryType      */
specifier|public
name|ChmCommons
operator|.
name|EntryType
name|getEntryType
parameter_list|()
block|{
return|return
name|entryType
return|;
block|}
specifier|protected
name|void
name|setEntryType
parameter_list|(
name|ChmCommons
operator|.
name|EntryType
name|entryType
parameter_list|)
block|{
name|this
operator|.
name|entryType
operator|=
name|entryType
expr_stmt|;
block|}
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
specifier|protected
name|void
name|setOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
specifier|protected
name|void
name|setLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
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
block|}
end_class

end_unit

