begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|TikaTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Ensure that our various Table-based formats produce consistent,  *  broadly similar output.  * This is mostly focused on the XHTML output  */
end_comment

begin_class
specifier|public
class|class
name|TabularFormatsTest
extends|extends
name|TikaTest
block|{
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|columnNames
init|=
operator|new
name|String
index|[]
block|{
literal|"recnum"
block|,
literal|"square"
block|,
literal|"desc"
block|,
literal|"pctdone"
block|,
literal|"pctincr"
block|,
literal|"date"
block|,
literal|"datetime"
block|,
literal|"time"
block|}
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|String
index|[]
name|columnLabels
init|=
operator|new
name|String
index|[]
block|{
literal|"Record Number"
block|,
literal|"Square of the Record Number"
block|,
literal|"Description of the Row"
block|,
literal|"Percent Done"
block|,
literal|"Percent Increment"
block|,
literal|"date"
block|,
literal|"datetime"
block|,
literal|"time"
block|}
decl_stmt|;
comment|/**      * Expected values, by<em>column</em>      */
specifier|protected
specifier|static
specifier|final
name|String
index|[]
index|[]
name|table
init|=
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"0"
block|,
literal|"1"
block|,
literal|"2"
block|,
literal|"3"
block|,
literal|"4"
block|,
literal|"5"
block|,
literal|"6"
block|,
literal|"7"
block|,
literal|"8"
block|,
literal|"9"
block|,
literal|"10"
block|}
block|,
operator|new
name|String
index|[]
block|{
literal|"0"
block|,
literal|"1"
block|,
literal|"4"
block|,
literal|"9"
block|,
literal|"16"
block|,
literal|"25"
block|,
literal|"36"
block|,
literal|"49"
block|,
literal|"64"
block|,
literal|"81"
block|,
literal|"100"
block|}
block|,
operator|new
name|String
index|[]
block|{}
block|,
comment|// Done later
operator|new
name|String
index|[]
block|{
literal|"0%"
block|,
literal|"10%"
block|,
literal|"20%"
block|,
literal|"30%"
block|,
literal|"40%"
block|,
literal|"50%"
block|,
literal|"60%"
block|,
literal|"70%"
block|,
literal|"80%"
block|,
literal|"90%"
block|,
literal|"100%"
block|}
block|,
operator|new
name|String
index|[]
block|{
literal|"M"
block|,
literal|"0.0%"
block|,
literal|"50.0%"
block|,
literal|"66.7%"
block|,
literal|"75.0%"
block|,
literal|"80.0%"
block|,
literal|"83.3%"
block|,
literal|"85.7%"
block|,
literal|"87.5%"
block|,
literal|"88.9%"
block|,
literal|"90.0%"
block|}
block|,
operator|new
name|String
index|[]
block|{
literal|"01-01-1960"
block|,
literal|"02-01-1960"
block|,
literal|"17-01-1960"
block|,
literal|"22-03-1960"
block|,
literal|"13-09-1960"
block|,
literal|"17-09-1961"
block|,
literal|"20-07-1963"
block|,
literal|"29-07-1966"
block|,
literal|"20-03-1971"
block|,
literal|"18-12-1977"
block|,
literal|"19-05-1987"
block|}
block|,
operator|new
name|String
index|[]
block|{
literal|"01JAN60:00:00:01"
block|,
literal|"01JAN60:00:00:10"
block|,
literal|"01JAN60:00:01:40"
block|,
literal|"01JAN60:00:16:40"
block|,
literal|"01JAN60:02:46:40"
block|,
literal|"02JAN60:03:46:40"
block|,
literal|"12JAN60:13:46:40"
block|,
literal|"25APR60:17:46:40"
block|,
literal|"03MAR63:09:46:40"
block|,
literal|"09SEP91:01:46:40"
block|,
literal|"19NOV76:17:46:40"
block|}
block|,
operator|new
name|String
index|[]
block|{
literal|"0:00:01"
block|,
literal|"0:00:03"
block|,
literal|"0:00:09"
block|,
literal|"0:00:27"
block|,
literal|"0:01:21"
block|,
literal|"0:04:03"
block|,
literal|"0:12:09"
block|,
literal|"0:36:27"
block|,
literal|"1:49:21"
block|,
literal|"5:28:03"
block|,
literal|"16:24:09"
block|}
block|}
decl_stmt|;
static|static
block|{
comment|// Row text in 3rd column
name|table
index|[
literal|2
index|]
operator|=
operator|new
name|String
index|[
name|table
index|[
literal|0
index|]
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|table
index|[
literal|0
index|]
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|table
index|[
literal|2
index|]
index|[
name|i
index|]
operator|=
literal|"This is row "
operator|+
name|i
operator|+
literal|" of 10"
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|String
index|[]
name|toCells
parameter_list|(
name|String
name|row
parameter_list|,
name|boolean
name|isTH
parameter_list|)
block|{
comment|// Split into cells, ignoring stuff before first cell
name|String
index|[]
name|cells
decl_stmt|;
if|if
condition|(
name|isTH
condition|)
block|{
name|cells
operator|=
name|row
operator|.
name|split
argument_list|(
literal|"<th"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cells
operator|=
name|row
operator|.
name|split
argument_list|(
literal|"<td"
argument_list|)
expr_stmt|;
block|}
name|cells
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|cells
argument_list|,
literal|1
argument_list|,
name|cells
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Ignore the closing tag onwards, and normalise whitespace
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cells
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|cells
index|[
name|i
index|]
operator|=
name|cells
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|cells
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"/>"
argument_list|)
condition|)
block|{
name|cells
index|[
name|i
index|]
operator|=
literal|""
expr_stmt|;
continue|continue;
block|}
name|int
name|splitAt
init|=
name|cells
index|[
name|i
index|]
operator|.
name|lastIndexOf
argument_list|(
literal|"</"
argument_list|)
decl_stmt|;
name|cells
index|[
name|i
index|]
operator|=
name|cells
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|splitAt
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
name|cells
index|[
name|i
index|]
operator|=
name|cells
index|[
name|i
index|]
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
block|}
return|return
name|cells
return|;
block|}
specifier|protected
name|void
name|assertHeaders
parameter_list|(
name|String
name|xml
parameter_list|,
name|boolean
name|isTH
parameter_list|,
name|boolean
name|hasLabel
parameter_list|,
name|boolean
name|hasName
parameter_list|)
block|{
comment|// Find the first row
name|int
name|splitAt
init|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"</tr>"
argument_list|)
decl_stmt|;
name|String
name|hRow
init|=
name|xml
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|splitAt
argument_list|)
decl_stmt|;
name|splitAt
operator|=
name|xml
operator|.
name|indexOf
argument_list|(
literal|"<tr>"
argument_list|)
expr_stmt|;
name|hRow
operator|=
name|hRow
operator|.
name|substring
argument_list|(
name|splitAt
operator|+
literal|4
argument_list|)
expr_stmt|;
comment|// Split into cells, ignoring stuff before first cell
name|String
index|[]
name|cells
init|=
name|toCells
argument_list|(
name|hRow
argument_list|,
name|isTH
argument_list|)
decl_stmt|;
comment|// Check we got the right number
name|assertEquals
argument_list|(
literal|"Wrong number of cells in header row "
operator|+
name|hRow
argument_list|,
name|columnLabels
operator|.
name|length
argument_list|,
name|cells
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// Check we got the right stuff
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|cells
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|hasLabel
operator|&&
name|hasName
condition|)
block|{
name|assertContains
argument_list|(
literal|"title=\""
operator|+
name|columnNames
index|[
name|i
index|]
operator|+
literal|"\""
argument_list|,
name|cells
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
literal|">"
operator|+
name|columnLabels
index|[
name|i
index|]
argument_list|,
name|cells
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hasName
condition|)
block|{
name|assertContains
argument_list|(
literal|">"
operator|+
name|columnNames
index|[
name|i
index|]
argument_list|,
name|cells
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertContains
argument_list|(
literal|">"
operator|+
name|columnLabels
index|[
name|i
index|]
argument_list|,
name|cells
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|assertContents
parameter_list|(
name|String
name|xml
parameter_list|,
name|boolean
name|hasHeader
parameter_list|)
block|{
comment|// Ignore anything before the first<tr>
comment|// Ignore the header row if there is one
name|int
name|ignores
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|hasHeader
condition|)
name|ignores
operator|++
expr_stmt|;
comment|// Split into rows, and discard the row closing (and anything after)
name|String
index|[]
name|rows
init|=
name|xml
operator|.
name|split
argument_list|(
literal|"<tr>"
argument_list|)
decl_stmt|;
name|rows
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|rows
argument_list|,
name|ignores
argument_list|,
name|rows
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rows
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|rows
index|[
name|i
index|]
operator|=
name|rows
index|[
name|i
index|]
operator|.
name|split
argument_list|(
literal|"</tr>"
argument_list|)
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
comment|// Check we got the right number of rows
for|for
control|(
name|int
name|cn
init|=
literal|0
init|;
name|cn
operator|<
name|table
operator|.
name|length
condition|;
name|cn
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"Wrong number of rows found compared to column "
operator|+
operator|(
name|cn
operator|+
literal|1
operator|)
argument_list|,
name|table
index|[
name|cn
index|]
operator|.
name|length
argument_list|,
name|rows
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|// Check each row's values
for|for
control|(
name|int
name|rn
init|=
literal|0
init|;
name|rn
operator|<
name|rows
operator|.
name|length
condition|;
name|rn
operator|++
control|)
block|{
name|String
index|[]
name|cells
init|=
name|toCells
argument_list|(
name|rows
index|[
name|rn
index|]
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of values in row "
operator|+
operator|(
name|rn
operator|+
literal|1
operator|)
argument_list|,
name|table
operator|.
name|length
argument_list|,
name|cells
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|cn
init|=
literal|0
init|;
name|cn
operator|<
name|table
operator|.
name|length
condition|;
name|cn
operator|++
control|)
block|{
comment|// Ignore cell attributes
name|String
name|val
init|=
name|cells
operator|.
name|length
operator|>
operator|(
name|cn
operator|-
literal|1
operator|)
condition|?
name|cells
index|[
name|cn
index|]
else|:
literal|""
decl_stmt|;
if|if
condition|(
operator|!
name|val
operator|.
name|isEmpty
argument_list|()
condition|)
name|val
operator|=
name|val
operator|.
name|split
argument_list|(
literal|">"
argument_list|)
index|[
literal|1
index|]
expr_stmt|;
comment|// Check
name|assertEquals
argument_list|(
literal|"Wrong text in row "
operator|+
operator|(
name|rn
operator|+
literal|1
operator|)
operator|+
literal|" and column "
operator|+
operator|(
name|cn
operator|+
literal|1
operator|)
argument_list|,
name|table
index|[
name|cn
index|]
index|[
name|rn
index|]
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSAS7BDAT
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|result
init|=
name|getXML
argument_list|(
literal|"test-columnar.sas7bdat"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
name|result
operator|.
name|xml
decl_stmt|;
name|assertHeaders
argument_list|(
name|xml
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//assertContents(xml, true);
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXLS
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|result
init|=
name|getXML
argument_list|(
literal|"test-columnar.xls"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
name|result
operator|.
name|xml
decl_stmt|;
name|assertHeaders
argument_list|(
name|xml
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//assertContents(xml, true);
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXLSX
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|result
init|=
name|getXML
argument_list|(
literal|"test-columnar.xlsx"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
name|result
operator|.
name|xml
decl_stmt|;
name|assertHeaders
argument_list|(
name|xml
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//assertContents(xml, true);
block|}
comment|// TODO Test ODS
comment|// TODO Test other formats, eg Database formats
comment|/**      * Note - we don't have a dedicated CSV parser      *       * This means we don't get proper HTML out...      */
annotation|@
name|Test
specifier|public
name|void
name|testCSV
parameter_list|()
throws|throws
name|Exception
block|{
name|XMLResult
name|result
init|=
name|getXML
argument_list|(
literal|"test-columnar.csv"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
name|result
operator|.
name|xml
decl_stmt|;
comment|// Normalise whitespace before testing
name|xml
operator|=
name|xml
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|label
range|:
name|columnLabels
control|)
block|{
name|assertContains
argument_list|(
name|label
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
index|[]
name|vals
range|:
name|table
control|)
block|{
for|for
control|(
name|String
name|val
range|:
name|vals
control|)
block|{
name|assertContains
argument_list|(
name|val
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

