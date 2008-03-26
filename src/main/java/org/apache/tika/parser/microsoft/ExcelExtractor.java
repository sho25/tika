begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Point
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
name|io
operator|.
name|Serializable
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
name|Comparator
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
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|hssf
operator|.
name|eventusermodel
operator|.
name|HSSFEventFactory
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
name|hssf
operator|.
name|eventusermodel
operator|.
name|HSSFListener
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
name|hssf
operator|.
name|eventusermodel
operator|.
name|HSSFRequest
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
name|hssf
operator|.
name|record
operator|.
name|BOFRecord
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
name|hssf
operator|.
name|record
operator|.
name|BoundSheetRecord
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
name|hssf
operator|.
name|record
operator|.
name|CellValueRecordInterface
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
name|hssf
operator|.
name|record
operator|.
name|CountryRecord
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
name|hssf
operator|.
name|record
operator|.
name|DateWindow1904Record
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
name|hssf
operator|.
name|record
operator|.
name|EOFRecord
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
name|hssf
operator|.
name|record
operator|.
name|ExtendedFormatRecord
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
name|hssf
operator|.
name|record
operator|.
name|FormatRecord
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
name|hssf
operator|.
name|record
operator|.
name|FormulaRecord
import|;
end_import

begin_comment
comment|//import org.apache.poi.hssf.record.HyperlinkRecord;  // FIXME - requires POI release
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|hssf
operator|.
name|record
operator|.
name|LabelRecord
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
name|hssf
operator|.
name|record
operator|.
name|LabelSSTRecord
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
name|hssf
operator|.
name|record
operator|.
name|NumberRecord
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
name|hssf
operator|.
name|record
operator|.
name|RKRecord
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
name|hssf
operator|.
name|record
operator|.
name|Record
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
name|hssf
operator|.
name|record
operator|.
name|SSTRecord
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
name|filesystem
operator|.
name|DocumentInputStream
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
name|filesystem
operator|.
name|POIFSFileSystem
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
name|sax
operator|.
name|XHTMLContentHandler
import|;
end_import

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
comment|/**  * Excel parser implementation which uses POI's Event API  * to handle the contents of a Workbook.  *<p>  * The Event API uses a much smaller memory footprint than  *<code>HSSFWorkbook</code> when processing excel files  * but at the cost of more complexity.  *<p>  * With the Event API a<i>listener</i> is registered for  * specific record types and those records are created,  * fired off to the listener and then discarded as the stream  * is being processed.  *  * @see org.apache.poi.hssf.eventusermodel.HSSFListener  * @see<a href="http://poi.apache.org/hssf/how-to.html#event_api">  * POI Event API How To</a>  */
end_comment

begin_class
specifier|public
class|class
name|ExcelExtractor
block|{
comment|/** Logging instance */
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ExcelExtractor
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      *<code>true</code> if the HSSFListener should be registered      * to listen for all records or<code>false</code> (the default)      * if the listener should be configured to only receive specified      * records.      */
specifier|private
name|boolean
name|listenForAllRecords
init|=
literal|false
decl_stmt|;
comment|/**      * Returns<code>true</code> if this parser is configured to listen      * for all records instead of just the specified few.      */
specifier|public
name|boolean
name|isListenForAllRecords
parameter_list|()
block|{
return|return
name|listenForAllRecords
return|;
block|}
comment|/**      * Specifies whether this parser should to listen for all      * records or just for the specified few.      *<p>      *<strong>Note:</strong> Under normal operation this setting should      * be<code>false</code> (the default), but you can experiment with      * this setting for testing and debugging purposes.      *      * @param listenForAllRecords<code>true</code> if the HSSFListener      * should be registered to listen for all records or<code>false</code>      * if the listener should be configured to only receive specified records.      */
specifier|public
name|void
name|setListenForAllRecords
parameter_list|(
name|boolean
name|listenForAllRecords
parameter_list|)
block|{
name|this
operator|.
name|listenForAllRecords
operator|=
name|listenForAllRecords
expr_stmt|;
block|}
comment|/**      * Extracts text from an Excel Workbook writing the extracted content      * to the specified {@link Appendable}.      *      * @param filesystem POI file system      * @throws IOException if an error occurs processing the workbook      * or writing the extracted content      */
specifier|protected
name|void
name|parse
parameter_list|(
name|POIFSFileSystem
name|filesystem
parameter_list|,
name|XHTMLContentHandler
name|xhtml
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Starting listenForAllRecords="
operator|+
name|listenForAllRecords
argument_list|)
expr_stmt|;
comment|// Set up listener and register the records we want to process
name|TikaHSSFListener
name|listener
init|=
operator|new
name|TikaHSSFListener
argument_list|(
name|xhtml
argument_list|)
decl_stmt|;
name|HSSFRequest
name|hssfRequest
init|=
operator|new
name|HSSFRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|listenForAllRecords
condition|)
block|{
name|hssfRequest
operator|.
name|addListenerForAllRecords
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|BOFRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|EOFRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|DateWindow1904Record
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|CountryRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|BoundSheetRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|FormatRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|ExtendedFormatRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|SSTRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|FormulaRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|LabelRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|LabelSSTRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|NumberRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
name|hssfRequest
operator|.
name|addListener
argument_list|(
name|listener
argument_list|,
name|RKRecord
operator|.
name|sid
argument_list|)
expr_stmt|;
comment|//hssfRequest.addListener(listener, HyperlinkRecord.sid); // FIXME - requires POI release
block|}
comment|// Create event factory and process Workbook (fire events)
name|DocumentInputStream
name|documentInputStream
init|=
name|filesystem
operator|.
name|createDocumentInputStream
argument_list|(
literal|"Workbook"
argument_list|)
decl_stmt|;
name|HSSFEventFactory
name|eventFactory
init|=
operator|new
name|HSSFEventFactory
argument_list|()
decl_stmt|;
name|eventFactory
operator|.
name|processEvents
argument_list|(
name|hssfRequest
argument_list|,
name|documentInputStream
argument_list|)
expr_stmt|;
name|listener
operator|.
name|throwStoredException
argument_list|()
expr_stmt|;
block|}
comment|// ======================================================================
comment|/**      * HSSF Listener implementation which processes the HSSF records.      */
specifier|private
specifier|static
class|class
name|TikaHSSFListener
implements|implements
name|HSSFListener
implements|,
name|Serializable
block|{
specifier|private
specifier|final
name|XHTMLContentHandler
name|handler
decl_stmt|;
specifier|private
name|SAXException
name|exception
decl_stmt|;
specifier|private
name|SSTRecord
name|sstRecord
decl_stmt|;
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|sheetNames
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|short
name|currentSheetIndex
decl_stmt|;
specifier|private
name|boolean
name|insideWorksheet
init|=
literal|false
decl_stmt|;
specifier|private
name|SortedMap
argument_list|<
name|Point
argument_list|,
name|Cell
argument_list|>
name|currentSheet
init|=
operator|new
name|TreeMap
argument_list|<
name|Point
argument_list|,
name|Cell
argument_list|>
argument_list|(
operator|new
name|Comparator
argument_list|<
name|Point
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Point
name|a
parameter_list|,
name|Point
name|b
parameter_list|)
block|{
name|int
name|diff
init|=
name|a
operator|.
name|y
operator|-
name|b
operator|.
name|y
decl_stmt|;
if|if
condition|(
name|diff
operator|==
literal|0
condition|)
block|{
name|diff
operator|=
name|a
operator|.
name|x
operator|-
name|b
operator|.
name|x
expr_stmt|;
block|}
return|return
name|diff
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**          * Contstruct a new listener instance outputting parsed data to          * the specified XHTML content handler.          *          * @param handler Destination to write the parsed output to          */
specifier|private
name|TikaHSSFListener
parameter_list|(
name|XHTMLContentHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|exception
operator|=
literal|null
expr_stmt|;
block|}
comment|/**          * Process a HSSF record.          *          * @param record HSSF Record          */
specifier|public
name|void
name|processRecord
parameter_list|(
name|Record
name|record
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
name|record
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|internalProcessRecord
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|throwStoredException
parameter_list|()
throws|throws
name|SAXException
block|{
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
block|}
specifier|private
name|void
name|internalProcessRecord
parameter_list|(
name|Record
name|record
parameter_list|)
throws|throws
name|SAXException
block|{
switch|switch
condition|(
name|record
operator|.
name|getSid
argument_list|()
condition|)
block|{
comment|/* BOFRecord: indicates start of workbook, worksheet etc. records */
case|case
name|BOFRecord
operator|.
name|sid
case|:
switch|switch
condition|(
operator|(
operator|(
name|BOFRecord
operator|)
name|record
operator|)
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|BOFRecord
operator|.
name|TYPE_WORKBOOK
case|:
name|currentSheetIndex
operator|=
operator|-
literal|1
expr_stmt|;
break|break;
case|case
name|BOFRecord
operator|.
name|TYPE_WORKSHEET
case|:
name|currentSheetIndex
operator|++
expr_stmt|;
name|currentSheet
operator|.
name|clear
argument_list|()
expr_stmt|;
name|insideWorksheet
operator|=
literal|true
expr_stmt|;
break|break;
block|}
break|break;
comment|/* EOFRecord: indicates end of workbook, worksheet etc. records */
case|case
name|EOFRecord
operator|.
name|sid
case|:
comment|// ignore empty sheets
if|if
condition|(
name|insideWorksheet
operator|&&
operator|!
name|currentSheet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|processSheet
argument_list|()
expr_stmt|;
block|}
name|insideWorksheet
operator|=
literal|false
expr_stmt|;
break|break;
comment|/* SSTRecord: holds all the strings for LabelSSTRecords */
case|case
name|SSTRecord
operator|.
name|sid
case|:
name|sstRecord
operator|=
operator|(
name|SSTRecord
operator|)
name|record
expr_stmt|;
break|break;
comment|/* BoundSheetRecord: Worksheet index record */
case|case
name|BoundSheetRecord
operator|.
name|sid
case|:
name|BoundSheetRecord
name|boundSheetRecord
init|=
operator|(
name|BoundSheetRecord
operator|)
name|record
decl_stmt|;
name|String
name|sheetName
init|=
name|boundSheetRecord
operator|.
name|getSheetname
argument_list|()
decl_stmt|;
name|sheetNames
operator|.
name|add
argument_list|(
name|sheetName
argument_list|)
expr_stmt|;
break|break;
comment|// FIXME - requires POI release
comment|///* HyperlinkRecord: holds a URL associated with a cell */
comment|//case HyperlinkRecord.sid:
comment|//    HyperlinkRecord hyperlinkRecord = (HyperlinkRecord)record;
comment|//    if (insideWorksheet) {
comment|//        int row = hyperlinkRecord.getFirstRow();
comment|//        short column =  hyperlinkRecord.getFirstColumn();
comment|//        Point point = new Point(column, row);
comment|//        Cell cell = currentSheet.get(point);
comment|//        if (cell != null) {
comment|//            cell = new LinkedCell(cell, hyperlinkRecord.getAddress());
comment|//            currentSheet.put(point, cell);
comment|//        }
comment|//    }
comment|//    break;
default|default:
if|if
condition|(
name|insideWorksheet
operator|&&
name|record
operator|instanceof
name|CellValueRecordInterface
condition|)
block|{
name|CellValueRecordInterface
name|value
init|=
operator|(
name|CellValueRecordInterface
operator|)
name|record
decl_stmt|;
name|Cell
name|cell
init|=
name|getCellValue
argument_list|(
name|record
operator|.
name|getSid
argument_list|()
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|cell
operator|!=
literal|null
condition|)
block|{
name|Point
name|point
init|=
operator|new
name|Point
argument_list|(
name|value
operator|.
name|getColumn
argument_list|()
argument_list|,
name|value
operator|.
name|getRow
argument_list|()
argument_list|)
decl_stmt|;
name|currentSheet
operator|.
name|put
argument_list|(
name|point
argument_list|,
name|cell
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
block|}
block|}
comment|/**          * Process a Cell Value record.          *          * @param sid record type identifier          * @param record The cell value record          */
specifier|private
name|Cell
name|getCellValue
parameter_list|(
name|short
name|sid
parameter_list|,
name|CellValueRecordInterface
name|record
parameter_list|)
throws|throws
name|SAXException
block|{
name|String
name|text
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|sid
condition|)
block|{
comment|/* FormulaRecord: Cell value from a formula */
case|case
name|FormulaRecord
operator|.
name|sid
case|:
return|return
operator|new
name|NumberCell
argument_list|(
operator|(
operator|(
name|FormulaRecord
operator|)
name|record
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
comment|/* LabelRecord: strings stored directly in the cell */
case|case
name|LabelRecord
operator|.
name|sid
case|:
name|text
operator|=
operator|(
operator|(
name|LabelRecord
operator|)
name|record
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
break|break;
comment|/* LabelSSTRecord: Ref. a string in the shared string table */
case|case
name|LabelSSTRecord
operator|.
name|sid
case|:
name|LabelSSTRecord
name|labelSSTRecord
init|=
operator|(
name|LabelSSTRecord
operator|)
name|record
decl_stmt|;
name|int
name|sstIndex
init|=
name|labelSSTRecord
operator|.
name|getSSTIndex
argument_list|()
decl_stmt|;
name|text
operator|=
name|sstRecord
operator|.
name|getString
argument_list|(
name|sstIndex
argument_list|)
operator|.
name|getString
argument_list|()
expr_stmt|;
break|break;
comment|/* NumberRecord: Contains a numeric cell value */
case|case
name|NumberRecord
operator|.
name|sid
case|:
return|return
operator|new
name|NumberCell
argument_list|(
operator|(
operator|(
name|NumberRecord
operator|)
name|record
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
comment|/* RKRecord: Excel internal number record */
case|case
name|RKRecord
operator|.
name|sid
case|:
return|return
operator|new
name|NumberCell
argument_list|(
operator|(
operator|(
name|RKRecord
operator|)
name|record
operator|)
operator|.
name|getRKNumber
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|text
operator|=
name|text
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|text
operator|!=
literal|null
operator|&&
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|TextCell
argument_list|(
name|text
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**          * Process an excel sheet.          *          * @throws SAXException if an error occurs          */
specifier|private
name|void
name|processSheet
parameter_list|()
throws|throws
name|SAXException
block|{
comment|// Sheet Start
name|handler
operator|.
name|startElement
argument_list|(
literal|"div"
argument_list|,
literal|"class"
argument_list|,
literal|"page"
argument_list|)
expr_stmt|;
if|if
condition|(
name|currentSheetIndex
operator|<
name|sheetNames
operator|.
name|size
argument_list|()
condition|)
block|{
name|handler
operator|.
name|element
argument_list|(
literal|"h1"
argument_list|,
name|sheetNames
operator|.
name|get
argument_list|(
name|currentSheetIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|characters
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|"tbody"
argument_list|)
expr_stmt|;
comment|// Process Rows
name|int
name|currentRow
init|=
literal|1
decl_stmt|;
name|int
name|currentColumn
init|=
literal|1
decl_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Point
argument_list|,
name|Cell
argument_list|>
name|entry
range|:
name|currentSheet
operator|.
name|entrySet
argument_list|()
control|)
block|{
while|while
condition|(
name|currentRow
operator|<
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|y
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|characters
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
name|currentRow
operator|++
expr_stmt|;
name|currentColumn
operator|=
literal|1
expr_stmt|;
block|}
while|while
condition|(
name|currentColumn
operator|<
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|x
condition|)
block|{
name|handler
operator|.
name|endElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|characters
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
name|currentColumn
operator|++
expr_stmt|;
block|}
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|render
argument_list|(
name|handler
argument_list|)
expr_stmt|;
block|}
name|handler
operator|.
name|endElement
argument_list|(
literal|"td"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
literal|"tr"
argument_list|)
expr_stmt|;
comment|// Sheet End
name|handler
operator|.
name|endElement
argument_list|(
literal|"tbody"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
literal|"table"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
literal|"div"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|characters
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

