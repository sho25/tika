begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|example
package|;
end_package

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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
operator|.
name|Store
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexWriter
operator|.
name|MaxFieldLength
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|SimpleFSDirectory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
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
name|Tika
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
class|class
name|LuceneIndexerExtended
block|{
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
specifier|private
specifier|final
name|Tika
name|tika
decl_stmt|;
specifier|public
name|LuceneIndexerExtended
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|Tika
name|tika
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|tika
operator|=
name|tika
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
throws|throws
name|Exception
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
operator|new
name|SimpleFSDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|,
operator|new
name|StandardAnalyzer
argument_list|(
name|Version
operator|.
name|LUCENE_30
argument_list|)
argument_list|,
name|MaxFieldLength
operator|.
name|UNLIMITED
argument_list|)
decl_stmt|;
try|try
block|{
name|LuceneIndexer
name|indexer
init|=
operator|new
name|LuceneIndexer
argument_list|(
operator|new
name|Tika
argument_list|()
argument_list|,
name|writer
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexer
operator|.
name|indexDocument
argument_list|(
operator|new
name|File
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|indexDocument
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|Exception
block|{
name|Reader
name|fulltext
init|=
name|tika
operator|.
name|parse
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"filename"
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|,
name|Store
operator|.
name|YES
argument_list|,
name|Index
operator|.
name|ANALYZED
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"fulltext"
argument_list|,
name|fulltext
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fulltext
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
