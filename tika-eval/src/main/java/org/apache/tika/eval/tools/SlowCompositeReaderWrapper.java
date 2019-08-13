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
name|tools
package|;
end_package

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
name|HashMap
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|BinaryDocValues
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
name|CompositeReader
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
name|DirectoryReader
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
name|DocValues
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
name|DocValuesType
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
name|FieldInfo
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
name|FieldInfos
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
name|Fields
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
name|IndexReader
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
name|LeafMetaData
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
name|LeafReader
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
name|LeafReaderContext
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
name|MultiBits
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
name|MultiDocValues
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
name|MultiDocValues
operator|.
name|MultiSortedDocValues
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
name|MultiReader
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
name|MultiTerms
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
name|NumericDocValues
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
name|OrdinalMap
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
name|PointValues
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
name|SortedDocValues
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
name|SortedNumericDocValues
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
name|SortedSetDocValues
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
name|StoredFieldVisitor
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
name|Terms
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
name|Bits
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

begin_comment
comment|/**  *<b>COPIED VERBATIM FROM LUCENE</b>  * This class forces a composite reader (eg a {@link  * MultiReader} or {@link DirectoryReader}) to emulate a  * {@link LeafReader}.  This requires implementing the postings  * APIs on-the-fly, using the static methods in {@link  * MultiTerms}, {@link MultiDocValues}, by stepping through  * the sub-readers to merge fields/terms, appending docs, etc.  *  *<p><b>NOTE</b>: this class almost always results in a  * performance hit.  If this is important to your use case,  * you'll get better performance by gathering the sub readers using  * {@link IndexReader#getContext()} to get the  * leaves and then operate per-LeafReader,  * instead of using this class.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SlowCompositeReaderWrapper
extends|extends
name|LeafReader
block|{
specifier|private
specifier|final
name|CompositeReader
name|in
decl_stmt|;
specifier|private
specifier|final
name|LeafMetaData
name|metaData
decl_stmt|;
comment|// Cached copy of FieldInfos to prevent it from being re-created on each
comment|// getFieldInfos call.  Most (if not all) other LeafReader implementations
comment|// also have a cached FieldInfos instance so this is consistent. SOLR-12878
specifier|private
specifier|final
name|FieldInfos
name|fieldInfos
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Terms
argument_list|>
name|cachedTerms
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO: consider ConcurrentHashMap ?
comment|// TODO: this could really be a weak map somewhere else on the coreCacheKey,
comment|// but do we really need to optimize slow-wrapper any more?
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|OrdinalMap
argument_list|>
name|cachedOrdMaps
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** This method is sugar for getting an {@link LeafReader} from      * an {@link IndexReader} of any kind. If the reader is already atomic,      * it is returned unchanged, otherwise wrapped by this class.      */
specifier|public
specifier|static
name|LeafReader
name|wrap
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|reader
operator|instanceof
name|CompositeReader
condition|)
block|{
return|return
operator|new
name|SlowCompositeReaderWrapper
argument_list|(
operator|(
name|CompositeReader
operator|)
name|reader
argument_list|)
return|;
block|}
else|else
block|{
assert|assert
name|reader
operator|instanceof
name|LeafReader
assert|;
return|return
operator|(
name|LeafReader
operator|)
name|reader
return|;
block|}
block|}
name|SlowCompositeReaderWrapper
parameter_list|(
name|CompositeReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|in
operator|=
name|reader
expr_stmt|;
name|in
operator|.
name|registerParentReader
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|metaData
operator|=
operator|new
name|LeafMetaData
argument_list|(
name|Version
operator|.
name|LATEST
operator|.
name|major
argument_list|,
name|Version
operator|.
name|LATEST
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Version
name|minVersion
init|=
name|Version
operator|.
name|LATEST
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|leafReaderContext
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|Version
name|leafVersion
init|=
name|leafReaderContext
operator|.
name|reader
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|getMinVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|leafVersion
operator|==
literal|null
condition|)
block|{
name|minVersion
operator|=
literal|null
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|minVersion
operator|.
name|onOrAfter
argument_list|(
name|leafVersion
argument_list|)
condition|)
block|{
name|minVersion
operator|=
name|leafVersion
expr_stmt|;
block|}
block|}
name|metaData
operator|=
operator|new
name|LeafMetaData
argument_list|(
name|reader
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|getCreatedVersionMajor
argument_list|()
argument_list|,
name|minVersion
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|fieldInfos
operator|=
name|FieldInfos
operator|.
name|getMergedFieldInfos
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SlowCompositeReaderWrapper("
operator|+
name|in
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
name|in
operator|.
name|getReaderCacheHelper
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CacheHelper
name|getCoreCacheHelper
parameter_list|()
block|{
comment|// TODO: this is trappy as the expectation is that core keys live for a long
comment|// time, but here we need to bound it to the lifetime of the wrapped
comment|// composite reader? Unfortunately some features seem to rely on this...
return|return
name|in
operator|.
name|getReaderCacheHelper
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Terms
name|terms
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|cachedTerms
operator|.
name|computeIfAbsent
argument_list|(
name|field
argument_list|,
name|f
lambda|->
block|{
try|try
block|{
return|return
name|MultiTerms
operator|.
name|getTerms
argument_list|(
name|in
argument_list|,
name|f
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// yuck!  ...sigh... checked exceptions with built-in lambdas are a pain
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unwrapMe"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"unwrapMe"
argument_list|)
operator|&&
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
throw|throw
operator|(
name|IOException
operator|)
name|e
operator|.
name|getCause
argument_list|()
throw|;
block|}
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NumericDocValues
name|getNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiDocValues
operator|.
name|getNumericValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
comment|// TODO cache?
block|}
annotation|@
name|Override
specifier|public
name|BinaryDocValues
name|getBinaryDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiDocValues
operator|.
name|getBinaryValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
comment|// TODO cache?
block|}
annotation|@
name|Override
specifier|public
name|SortedNumericDocValues
name|getSortedNumericDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiDocValues
operator|.
name|getSortedNumericValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
comment|// TODO cache?
block|}
annotation|@
name|Override
specifier|public
name|SortedDocValues
name|getSortedDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|OrdinalMap
name|map
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|cachedOrdMaps
init|)
block|{
name|map
operator|=
name|cachedOrdMaps
operator|.
name|get
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
comment|// uncached, or not a multi dv
name|SortedDocValues
name|dv
init|=
name|MultiDocValues
operator|.
name|getSortedValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|instanceof
name|MultiSortedDocValues
condition|)
block|{
name|map
operator|=
operator|(
operator|(
name|MultiSortedDocValues
operator|)
name|dv
operator|)
operator|.
name|mapping
expr_stmt|;
name|CacheHelper
name|cacheHelper
init|=
name|getReaderCacheHelper
argument_list|()
decl_stmt|;
if|if
condition|(
name|cacheHelper
operator|!=
literal|null
operator|&&
name|map
operator|.
name|owner
operator|==
name|cacheHelper
operator|.
name|getKey
argument_list|()
condition|)
block|{
name|cachedOrdMaps
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dv
return|;
block|}
block|}
name|int
name|size
init|=
name|in
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|SortedDocValues
index|[]
name|values
init|=
operator|new
name|SortedDocValues
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|size
operator|+
literal|1
index|]
decl_stmt|;
name|long
name|totalCost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|LeafReaderContext
name|context
init|=
name|in
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|!=
literal|null
operator|&&
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|SORTED
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SortedDocValues
name|v
init|=
name|reader
operator|.
name|getSortedDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
name|DocValues
operator|.
name|emptySorted
argument_list|()
expr_stmt|;
block|}
name|totalCost
operator|+=
name|v
operator|.
name|cost
argument_list|()
expr_stmt|;
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
block|}
name|starts
index|[
name|size
index|]
operator|=
name|maxDoc
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiSortedDocValues
argument_list|(
name|values
argument_list|,
name|starts
argument_list|,
name|map
argument_list|,
name|totalCost
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SortedSetDocValues
name|getSortedSetDocValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|OrdinalMap
name|map
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|cachedOrdMaps
init|)
block|{
name|map
operator|=
name|cachedOrdMaps
operator|.
name|get
argument_list|(
name|field
argument_list|)
expr_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
comment|// uncached, or not a multi dv
name|SortedSetDocValues
name|dv
init|=
name|MultiDocValues
operator|.
name|getSortedSetValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|dv
operator|instanceof
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
condition|)
block|{
name|map
operator|=
operator|(
operator|(
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
operator|)
name|dv
operator|)
operator|.
name|mapping
expr_stmt|;
name|CacheHelper
name|cacheHelper
init|=
name|getReaderCacheHelper
argument_list|()
decl_stmt|;
if|if
condition|(
name|cacheHelper
operator|!=
literal|null
operator|&&
name|map
operator|.
name|owner
operator|==
name|cacheHelper
operator|.
name|getKey
argument_list|()
condition|)
block|{
name|cachedOrdMaps
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dv
return|;
block|}
block|}
assert|assert
name|map
operator|!=
literal|null
assert|;
name|int
name|size
init|=
name|in
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|SortedSetDocValues
index|[]
name|values
init|=
operator|new
name|SortedSetDocValues
index|[
name|size
index|]
decl_stmt|;
specifier|final
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|size
operator|+
literal|1
index|]
decl_stmt|;
name|long
name|cost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|LeafReaderContext
name|context
init|=
name|in
operator|.
name|leaves
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|LeafReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
specifier|final
name|FieldInfo
name|fieldInfo
init|=
name|reader
operator|.
name|getFieldInfos
argument_list|()
operator|.
name|fieldInfo
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldInfo
operator|!=
literal|null
operator|&&
name|fieldInfo
operator|.
name|getDocValuesType
argument_list|()
operator|!=
name|DocValuesType
operator|.
name|SORTED_SET
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SortedSetDocValues
name|v
init|=
name|reader
operator|.
name|getSortedSetDocValues
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|v
operator|=
name|DocValues
operator|.
name|emptySortedSet
argument_list|()
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
name|v
expr_stmt|;
name|starts
index|[
name|i
index|]
operator|=
name|context
operator|.
name|docBase
expr_stmt|;
name|cost
operator|+=
name|v
operator|.
name|cost
argument_list|()
expr_stmt|;
block|}
name|starts
index|[
name|size
index|]
operator|=
name|maxDoc
argument_list|()
expr_stmt|;
return|return
operator|new
name|MultiDocValues
operator|.
name|MultiSortedSetDocValues
argument_list|(
name|values
argument_list|,
name|starts
argument_list|,
name|map
argument_list|,
name|cost
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NumericDocValues
name|getNormValues
parameter_list|(
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiDocValues
operator|.
name|getNormValues
argument_list|(
name|in
argument_list|,
name|field
argument_list|)
return|;
comment|// TODO cache?
block|}
annotation|@
name|Override
specifier|public
name|Fields
name|getTermVectors
parameter_list|(
name|int
name|docID
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|in
operator|.
name|getTermVectors
argument_list|(
name|docID
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|in
operator|.
name|numDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
comment|// Don't call ensureOpen() here (it could affect performance)
return|return
name|in
operator|.
name|maxDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|document
parameter_list|(
name|int
name|docID
parameter_list|,
name|StoredFieldVisitor
name|visitor
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|in
operator|.
name|document
argument_list|(
name|docID
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Bits
name|getLiveDocs
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|MultiBits
operator|.
name|getLiveDocs
argument_list|(
name|in
argument_list|)
return|;
comment|// TODO cache?
block|}
annotation|@
name|Override
specifier|public
name|PointValues
name|getPointValues
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
comment|// because not supported.  Throw UOE?
block|}
annotation|@
name|Override
specifier|public
name|FieldInfos
name|getFieldInfos
parameter_list|()
block|{
return|return
name|fieldInfos
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO: as this is a wrapper, should we really close the delegate?
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkIntegrity
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|in
operator|.
name|leaves
argument_list|()
control|)
block|{
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|checkIntegrity
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|LeafMetaData
name|getMetaData
parameter_list|()
block|{
return|return
name|metaData
return|;
block|}
block|}
end_class

end_unit

