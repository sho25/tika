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
name|eval
operator|.
name|reports
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|common
operator|.
name|usermodel
operator|.
name|HyperlinkType
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
name|ss
operator|.
name|usermodel
operator|.
name|Cell
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
name|ss
operator|.
name|usermodel
operator|.
name|CellStyle
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
name|ss
operator|.
name|usermodel
operator|.
name|Font
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
name|ss
operator|.
name|usermodel
operator|.
name|Hyperlink
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
name|ss
operator|.
name|usermodel
operator|.
name|IndexedColors
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
name|xssf
operator|.
name|usermodel
operator|.
name|XSSFWorkbook
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|HyperlinkListener
import|;
end_import

begin_class
specifier|public
class|class
name|XLSXHREFFormatter
implements|implements
name|XSLXCellFormatter
block|{
comment|//xlsx files can only have this many hyperlinks
comment|//if they have more Excel can't read the file
specifier|private
specifier|static
specifier|final
name|int
name|MAX_HYPERLINKS
init|=
literal|65000
decl_stmt|;
specifier|private
specifier|final
name|String
name|urlBase
decl_stmt|;
specifier|private
specifier|final
name|HyperlinkType
name|linkType
decl_stmt|;
specifier|private
name|XSSFWorkbook
name|workbook
decl_stmt|;
specifier|private
name|CellStyle
name|style
decl_stmt|;
specifier|private
name|int
name|links
init|=
literal|0
decl_stmt|;
specifier|public
name|XLSXHREFFormatter
parameter_list|(
name|String
name|urlBase
parameter_list|,
name|HyperlinkType
name|hyperlinkType
parameter_list|)
block|{
name|this
operator|.
name|urlBase
operator|=
name|urlBase
expr_stmt|;
name|this
operator|.
name|linkType
operator|=
name|hyperlinkType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|XSSFWorkbook
name|workbook
parameter_list|)
block|{
name|this
operator|.
name|workbook
operator|=
name|workbook
expr_stmt|;
name|style
operator|=
name|workbook
operator|.
name|createCellStyle
argument_list|()
expr_stmt|;
name|Font
name|hlinkFont
init|=
name|workbook
operator|.
name|createFont
argument_list|()
decl_stmt|;
name|hlinkFont
operator|.
name|setUnderline
argument_list|(
name|Font
operator|.
name|U_SINGLE
argument_list|)
expr_stmt|;
name|hlinkFont
operator|.
name|setColor
argument_list|(
name|IndexedColors
operator|.
name|BLUE
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|style
operator|.
name|setFont
argument_list|(
name|hlinkFont
argument_list|)
expr_stmt|;
name|links
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|applyStyleAndValue
parameter_list|(
name|int
name|dbColNum
parameter_list|,
name|ResultSet
name|resultSet
parameter_list|,
name|Cell
name|cell
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|links
operator|<
name|MAX_HYPERLINKS
condition|)
block|{
name|Hyperlink
name|hyperlink
init|=
name|workbook
operator|.
name|getCreationHelper
argument_list|()
operator|.
name|createHyperlink
argument_list|(
name|linkType
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|resultSet
operator|.
name|getString
argument_list|(
name|dbColNum
argument_list|)
decl_stmt|;
name|String
name|address
init|=
name|urlBase
operator|+
name|path
decl_stmt|;
name|hyperlink
operator|.
name|setAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|cell
operator|.
name|setHyperlink
argument_list|(
name|hyperlink
argument_list|)
expr_stmt|;
name|cell
operator|.
name|setCellStyle
argument_list|(
name|style
argument_list|)
expr_stmt|;
name|String
name|fName
init|=
name|Paths
operator|.
name|get
argument_list|(
name|path
argument_list|)
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|cell
operator|.
name|setCellValue
argument_list|(
name|fName
argument_list|)
expr_stmt|;
name|links
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|//silently stop adding hyperlinks
block|}
block|}
block|}
end_class

end_unit

