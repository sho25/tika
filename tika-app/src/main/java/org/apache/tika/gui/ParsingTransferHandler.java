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
name|gui
package|;
end_package

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|datatransfer
operator|.
name|Clipboard
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|datatransfer
operator|.
name|DataFlavor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|datatransfer
operator|.
name|Transferable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|InputEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|Icon
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JComponent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|TransferHandler
import|;
end_import

begin_comment
comment|/**  * Utility class that turns drag-and-drop events into Tika parse requests.  */
end_comment

begin_class
class|class
name|ParsingTransferHandler
extends|extends
name|TransferHandler
block|{
comment|/**      * Serial version UID.      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|557932290014044494L
decl_stmt|;
specifier|private
specifier|final
name|TransferHandler
name|delegate
decl_stmt|;
specifier|private
specifier|final
name|TikaGUI
name|tika
decl_stmt|;
specifier|private
specifier|static
name|DataFlavor
name|uriListFlavor
decl_stmt|;
specifier|private
specifier|static
name|DataFlavor
name|urlListFlavor
decl_stmt|;
static|static
block|{
try|try
block|{
name|uriListFlavor
operator|=
operator|new
name|DataFlavor
argument_list|(
literal|"text/uri-list;class=java.lang.String"
argument_list|)
expr_stmt|;
name|urlListFlavor
operator|=
operator|new
name|DataFlavor
argument_list|(
literal|"text/plain;class=java.lang.String"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{          }
block|}
specifier|public
name|ParsingTransferHandler
parameter_list|(
name|TransferHandler
name|delegate
parameter_list|,
name|TikaGUI
name|tika
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
name|this
operator|.
name|tika
operator|=
name|tika
expr_stmt|;
block|}
specifier|public
name|boolean
name|canImport
parameter_list|(
name|JComponent
name|component
parameter_list|,
name|DataFlavor
index|[]
name|flavors
parameter_list|)
block|{
for|for
control|(
name|DataFlavor
name|flavor
range|:
name|flavors
control|)
block|{
if|if
condition|(
name|flavor
operator|.
name|equals
argument_list|(
name|DataFlavor
operator|.
name|javaFileListFlavor
argument_list|)
operator|||
name|flavor
operator|.
name|equals
argument_list|(
name|uriListFlavor
argument_list|)
operator|||
name|flavor
operator|.
name|equals
argument_list|(
name|urlListFlavor
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|boolean
name|importData
parameter_list|(
name|JComponent
name|component
parameter_list|,
name|Transferable
name|transferable
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|transferable
operator|.
name|isDataFlavorSupported
argument_list|(
name|DataFlavor
operator|.
name|javaFileListFlavor
argument_list|)
condition|)
block|{
name|importFiles
argument_list|(
operator|(
name|List
argument_list|<
name|File
argument_list|>
operator|)
name|transferable
operator|.
name|getTransferData
argument_list|(
name|DataFlavor
operator|.
name|javaFileListFlavor
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|transferable
operator|.
name|isDataFlavorSupported
argument_list|(
name|urlListFlavor
argument_list|)
condition|)
block|{
name|Object
name|data
init|=
name|transferable
operator|.
name|getTransferData
argument_list|(
name|urlListFlavor
argument_list|)
decl_stmt|;
name|tika
operator|.
name|openURL
argument_list|(
operator|new
name|URL
argument_list|(
name|data
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|transferable
operator|.
name|isDataFlavorSupported
argument_list|(
name|uriListFlavor
argument_list|)
condition|)
block|{
name|importFiles
argument_list|(
name|uriToFileList
argument_list|(
name|transferable
operator|.
name|getTransferData
argument_list|(
name|uriListFlavor
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|void
name|importFiles
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|files
parameter_list|)
block|{
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|tika
operator|.
name|openFile
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|exportAsDrag
parameter_list|(
name|JComponent
name|arg0
parameter_list|,
name|InputEvent
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|)
block|{
name|delegate
operator|.
name|exportAsDrag
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|exportToClipboard
parameter_list|(
name|JComponent
name|arg0
parameter_list|,
name|Clipboard
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|)
throws|throws
name|IllegalStateException
block|{
name|delegate
operator|.
name|exportToClipboard
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getSourceActions
parameter_list|(
name|JComponent
name|arg0
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getSourceActions
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|Icon
name|getVisualRepresentation
parameter_list|(
name|Transferable
name|arg0
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|getVisualRepresentation
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|File
argument_list|>
name|uriToFileList
parameter_list|(
name|Object
name|data
parameter_list|)
block|{
name|List
argument_list|<
name|File
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|File
argument_list|>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|data
operator|.
name|toString
argument_list|()
argument_list|,
literal|"\r\n"
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|s
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|URI
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{             }
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

