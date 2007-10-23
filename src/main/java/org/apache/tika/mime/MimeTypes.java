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
name|mime
package|;
end_package

begin_comment
comment|// JDK imports
end_comment

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Arrays
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
name|HashMap
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
name|Collections
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

begin_comment
comment|/**  * This class is a MimeType repository. It gathers a set of MimeTypes and  * enables to retrieves a content-type from its name, from a file name, or from  * a magic character sequence.  *<p>  * The MIME type detection methods that take an {@link InputStream} as  * an argument will never reads more than {@link #getMinLength()} bytes  * from the stream. Also the given stream is never  * {@link InputStream#close() closed}, {@link InputStream#mark(int) marked},  * or {@link InputStream#reset() reset} by the methods. Thus a client can  * use the {@link InputStream#markSupported() mark feature} of the stream  * (if available) to restore the stream back to the state it was before type  * detection if it wants to process the stream based on the detected type.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|MimeTypes
block|{
comment|/** The default<code>application/octet-stream</code> MimeType */
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT
init|=
literal|"application/octet-stream"
decl_stmt|;
comment|/** All the registered MimeTypes indexed on their name */
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|MimeInfo
argument_list|>
name|types
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|MimeInfo
argument_list|>
argument_list|()
decl_stmt|;
comment|/** The patterns matcher */
specifier|private
name|Patterns
name|patterns
init|=
operator|new
name|Patterns
argument_list|()
decl_stmt|;
comment|/** List of all registered magics */
specifier|private
name|ArrayList
argument_list|<
name|Magic
argument_list|>
name|magics
init|=
operator|new
name|ArrayList
argument_list|<
name|Magic
argument_list|>
argument_list|()
decl_stmt|;
comment|/** List of all registered rootXML */
specifier|private
name|ArrayList
argument_list|<
name|MimeInfo
argument_list|>
name|xmls
init|=
operator|new
name|ArrayList
argument_list|<
name|MimeInfo
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|MimeInfo
argument_list|>
argument_list|>
name|unsolvedDeps
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|MimeInfo
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * A comparator used to sort the mime types based on their magics (it is      * sorted first on the magic's priority, then on the magic's size).      */
specifier|final
specifier|static
name|Comparator
argument_list|<
name|Magic
argument_list|>
name|MAGICS_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|Magic
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Magic
name|m1
parameter_list|,
name|Magic
name|m2
parameter_list|)
block|{
name|int
name|p1
init|=
name|m1
operator|.
name|getPriority
argument_list|()
decl_stmt|;
name|int
name|p2
init|=
name|m2
operator|.
name|getPriority
argument_list|()
decl_stmt|;
if|if
condition|(
name|p1
operator|!=
name|p2
condition|)
block|{
return|return
name|p2
operator|-
name|p1
return|;
block|}
return|return
name|m2
operator|.
name|size
argument_list|()
operator|-
name|m1
operator|.
name|size
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**      * A comparator used to sort the mime types based on their level (the level      * is the number of super-types for a type)      */
specifier|private
specifier|final
specifier|static
name|Comparator
argument_list|<
name|MimeInfo
argument_list|>
name|LEVELS_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|MimeInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|MimeInfo
name|o1
parameter_list|,
name|MimeInfo
name|o2
parameter_list|)
block|{
return|return
name|o2
operator|.
name|getLevel
argument_list|()
operator|-
name|o1
operator|.
name|getLevel
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** The minimum length of data to provide to check all MimeTypes */
specifier|private
name|int
name|minLength
init|=
literal|0
decl_stmt|;
comment|/**      * Find the Mime Content Type of a file.      *       * @param file      *            to analyze.      * @return the Mime Content Type of the specified file, or<code>null</code>      *         if none is found.      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|getMimeType
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Find the Mime Content Type of a document from its URL.      *       * @param url      *            of the document to analyze.      * @return the Mime Content Type of the specified document URL, or      *<code>null</code> if none is found.      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
return|return
name|getMimeType
argument_list|(
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Find the Mime Content Type of a document from its name.      *       * @param name      *            of the document to analyze.      * @return the Mime Content Type of the specified document name, or      *<code>null</code> if none is found.      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|MimeType
name|type
init|=
name|patterns
operator|.
name|matches
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
return|return
name|type
return|;
comment|// if it's null here, then return the default type
return|return
name|forName
argument_list|(
name|DEFAULT
argument_list|)
return|;
block|}
comment|/**      * Returns the MIME type that best matches the given first few bytes      * of a document stream.      *<p>      * The given byte array is expected to be at least {@link #getMinLength()}      * long, or shorter only if the document stream itself is shorter.      *      * @param data first few bytes of a document stream      * @return matching MIME type, or<code>null</code> if no match is found      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
assert|assert
name|data
operator|!=
literal|null
assert|;
comment|// First, check for XML descriptions (level by level)
for|for
control|(
name|MimeInfo
name|info
range|:
name|xmls
control|)
block|{
name|MimeType
name|type
init|=
name|info
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|matchesXML
argument_list|(
name|data
argument_list|)
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
comment|// Then, check for magic bytes
for|for
control|(
name|Magic
name|magic
range|:
name|magics
control|)
block|{
if|if
condition|(
name|magic
operator|.
name|eval
argument_list|(
name|data
argument_list|)
condition|)
block|{
return|return
name|magic
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the MIME type that best matches the first few bytes of the      * given document stream.      *      * @see #getMimeType(byte[])      * @param stream document stream      * @return matching MIME type, or<code>null</code> if no match is found      * @throws IOException if the stream can be read      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMimeType
argument_list|(
name|readMagicHeader
argument_list|(
name|stream
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Reads the first {@link #getMinLength()} bytes from the given stream.      * If the stream is shorter, then the entire content of the stream is      * returned.      *<p>      * The given stream is never {@link InputStream#close() closed},      * {@link InputStream#mark(int) marked}, or      * {@link InputStream#reset() reset} by this method.      *      * @param stream stream to be read      * @return first {@link #getMinLength()} (or fewer) bytes of the stream      * @throws IOException if the stream can not be read      */
specifier|private
name|byte
index|[]
name|readMagicHeader
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|stream
operator|!=
literal|null
assert|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|getMinLength
argument_list|()
index|]
decl_stmt|;
name|int
name|totalRead
init|=
literal|0
decl_stmt|;
name|int
name|lastRead
init|=
name|stream
operator|.
name|read
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
while|while
condition|(
name|lastRead
operator|!=
operator|-
literal|1
condition|)
block|{
name|totalRead
operator|+=
name|lastRead
expr_stmt|;
if|if
condition|(
name|totalRead
operator|==
name|bytes
operator|.
name|length
condition|)
block|{
return|return
name|bytes
return|;
block|}
name|lastRead
operator|=
name|stream
operator|.
name|read
argument_list|(
name|bytes
argument_list|,
name|totalRead
argument_list|,
name|bytes
operator|.
name|length
operator|-
name|totalRead
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|shorter
init|=
operator|new
name|byte
index|[
name|totalRead
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|shorter
argument_list|,
literal|0
argument_list|,
name|totalRead
argument_list|)
expr_stmt|;
return|return
name|shorter
return|;
block|}
comment|/**      * Find the Mime Content Type of a document from its name and its content.      * The policy used to guess the Mime Content Type is:      *<ol>      *<li>Try to find the type based on the provided data.</li>      *<li>If a type is found, then return it, otherwise try to find the type      * based on the file name</li>      *</ol>      *       * @param name      *            of the document to analyze.      * @param data      *            are the first bytes of the document's content.      * @return the Mime Content Type of the specified document, or      *<code>null</code> if none is found.      * @see #getMinLength()      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
block|{
comment|// First, try to get the mime-type from the content
name|MimeType
name|mimeType
init|=
name|getMimeType
argument_list|(
name|data
argument_list|)
decl_stmt|;
comment|// If no mime-type found, then try to get the mime-type from
comment|// the document name
if|if
condition|(
name|mimeType
operator|==
literal|null
condition|)
block|{
name|mimeType
operator|=
name|getMimeType
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|mimeType
return|;
block|}
comment|/**      * Returns the MIME type that best matches the given document name and      * the first few bytes of the given document stream.      *      * @see #getMimeType(String, byte[])      * @param name document name      * @param stream document stream      * @return matching MIME type, or<code>null</code> if no match is found      * @throws IOException if the stream can not be read      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|String
name|name
parameter_list|,
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getMimeType
argument_list|(
name|name
argument_list|,
name|readMagicHeader
argument_list|(
name|stream
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Find a Mime Content Type from its name.      *       * @param name      *            is the content type name      * @return the MimeType for the specified name, or<code>null</code> if no      *         MimeType is registered for this name.      */
specifier|public
name|MimeType
name|forName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|MimeInfo
name|info
init|=
name|types
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
operator|(
name|info
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|info
operator|.
name|getType
argument_list|()
return|;
block|}
comment|/**      * Return the minimum length of data to provide to analyzing methods based      * on the document's content in order to check all the known MimeTypes.      *       * @return the minimum length of data to provide.      * @see #getMimeType(byte[])      * @see #getMimeType(String, byte[])      */
specifier|public
name|int
name|getMinLength
parameter_list|()
block|{
return|return
literal|1024
return|;
comment|// return minLength;
block|}
comment|/**      * Add the specified mime-types in the repository.      *       * @param types      *            are the mime-types to add.      */
name|void
name|add
parameter_list|(
name|MimeType
index|[]
name|types
parameter_list|)
block|{
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|types
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Add the specified mime-type in the repository.      *       * @param type      *            is the mime-type to add.      */
name|void
name|add
parameter_list|(
name|MimeType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// Add the new type in the repository
name|MimeInfo
name|info
init|=
operator|new
name|MimeInfo
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|types
operator|.
name|put
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// Checks for some unsolved dependencies on this new type
name|List
argument_list|<
name|MimeInfo
argument_list|>
name|deps
init|=
name|unsolvedDeps
operator|.
name|get
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|deps
operator|!=
literal|null
condition|)
block|{
name|int
name|level
init|=
name|info
operator|.
name|getLevel
argument_list|()
decl_stmt|;
for|for
control|(
name|MimeInfo
name|dep
range|:
name|deps
control|)
block|{
name|level
operator|=
name|Math
operator|.
name|max
argument_list|(
name|level
argument_list|,
name|dep
operator|.
name|getLevel
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|info
operator|.
name|setLevel
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|unsolvedDeps
operator|.
name|remove
argument_list|(
name|type
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|type
operator|.
name|getSuperTypes
argument_list|()
control|)
block|{
name|MimeInfo
name|superType
init|=
name|types
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|superType
operator|==
literal|null
condition|)
block|{
name|deps
operator|=
name|unsolvedDeps
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|deps
operator|==
literal|null
condition|)
block|{
name|deps
operator|=
operator|new
name|ArrayList
argument_list|<
name|MimeInfo
argument_list|>
argument_list|()
expr_stmt|;
name|unsolvedDeps
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|deps
argument_list|)
expr_stmt|;
block|}
name|deps
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Update minLentgth
name|minLength
operator|=
name|Math
operator|.
name|max
argument_list|(
name|minLength
argument_list|,
name|type
operator|.
name|getMinLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// Update the extensions index...
name|patterns
operator|.
name|add
argument_list|(
name|type
operator|.
name|getPatterns
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
comment|// Update the magics index...
if|if
condition|(
name|type
operator|.
name|hasMagic
argument_list|()
condition|)
block|{
name|magics
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|type
operator|.
name|getMagics
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|magics
argument_list|,
name|MAGICS_COMPARATOR
argument_list|)
expr_stmt|;
comment|// Update the xml (xmlRoot) index...
if|if
condition|(
name|type
operator|.
name|hasRootXML
argument_list|()
condition|)
block|{
name|this
operator|.
name|xmls
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|xmls
argument_list|,
name|LEVELS_COMPARATOR
argument_list|)
expr_stmt|;
block|}
comment|// Inherited Javadoc
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|MimeInfo
name|info
range|:
name|types
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|info
operator|.
name|getType
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|final
class|class
name|MimeInfo
block|{
specifier|private
specifier|final
name|MimeType
name|type
decl_stmt|;
specifier|private
name|int
name|level
decl_stmt|;
name|MimeInfo
parameter_list|(
name|MimeType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|level
operator|=
literal|0
expr_stmt|;
block|}
name|MimeType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
name|int
name|getLevel
parameter_list|()
block|{
return|return
name|level
return|;
block|}
name|void
name|setLevel
parameter_list|(
name|int
name|level
parameter_list|)
block|{
if|if
condition|(
name|level
operator|>
name|this
operator|.
name|level
condition|)
block|{
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
comment|// Update all my super-types
for|for
control|(
name|String
name|name
range|:
name|type
operator|.
name|getSuperTypes
argument_list|()
control|)
block|{
name|MimeInfo
name|info
init|=
name|types
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|setLevel
argument_list|(
name|level
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

