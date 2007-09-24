begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2007 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_comment
comment|// Commons Logging imports
end_comment

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

begin_comment
comment|/**  * This class is a MimeType repository. It gathers a set of MimeTypes and  * enables to retrieves a content-type from its name, from a file name, or from  * a magic character sequence.  *   *   */
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
comment|/** My logger */
specifier|private
name|Log
name|logger
init|=
literal|null
decl_stmt|;
comment|/** All the registered MimeTypes indexed on their name */
specifier|private
name|Map
name|types
init|=
operator|new
name|HashMap
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
name|magics
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
comment|/** List of all registered rootXML */
specifier|private
name|ArrayList
name|xmls
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|unsolvedDeps
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|/**      * A comparator used to sort the mime types based on their magics (it is      * sorted first on the magic's priority, then on the magic's size).      */
specifier|final
specifier|static
name|Comparator
name|MAGICS_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|Magic
name|m1
init|=
operator|(
name|Magic
operator|)
name|o1
decl_stmt|;
name|Magic
name|m2
init|=
operator|(
name|Magic
operator|)
name|o2
decl_stmt|;
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
name|LEVELS_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
operator|(
operator|(
name|MimeInfo
operator|)
name|o2
operator|)
operator|.
name|getLevel
argument_list|()
operator|-
operator|(
operator|(
name|MimeInfo
operator|)
name|o1
operator|)
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
comment|/**      * Creates a new MimeTypes instance.      *       * @param filepath      *            is the mime-types definitions xml file.      * @param logger      *            is it Logger to uses for ouput messages.      */
specifier|public
name|MimeTypes
parameter_list|(
name|String
name|filepath
parameter_list|,
name|Log
name|logger
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|logger
operator|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
name|MimeTypesReader
name|reader
init|=
operator|new
name|MimeTypesReader
argument_list|(
name|logger
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|reader
operator|.
name|read
argument_list|(
name|filepath
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new MimeTypes instance.      *       * @param filepath      *            is the mime-types definitions xml file.      * @return A MimeTypes instance for the specified filepath xml file.      */
specifier|public
name|MimeTypes
parameter_list|(
name|String
name|filepath
parameter_list|)
block|{
name|this
argument_list|(
name|filepath
argument_list|,
operator|(
name|Log
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new MimeTypes instance.      *       * @param is      *            the document of the mime types definition file.      * @param logger      *            is it Logger to uses for ouput messages.      */
specifier|public
name|MimeTypes
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Log
name|logger
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|logger
operator|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
name|MimeTypesReader
name|reader
init|=
operator|new
name|MimeTypesReader
argument_list|(
name|logger
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|reader
operator|.
name|read
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new MimeTypes instance.      *       * @param is      *            the document of the mime types definition file.      */
specifier|public
name|MimeTypes
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
name|this
argument_list|(
name|doc
argument_list|,
operator|(
name|Log
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * Find the Mime Content Type of a stream from its content.      *       * @param data      *            are the first bytes of data of the content to analyze.      *            Depending on the length of provided data, all known MimeTypes      *            are checked. If the length of provided data is greater or      *            egals to the value returned by {@link #getMinLength()}, then      *            all known MimeTypes are checked, otherwise only the MimeTypes      *            that could be analyzed with the length of provided data are      *            analyzed.      *       * @return The Mime Content Type found for the specified data, or      *<code>null</code> if none is found.      * @see #getMinLength()      */
specifier|public
name|MimeType
name|getMimeType
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
comment|// Preliminary checks
if|if
condition|(
operator|(
name|data
operator|==
literal|null
operator|)
operator|||
operator|(
name|data
operator|.
name|length
operator|<
literal|1
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// First, check for XML descriptions (level by level)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|xmls
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|MimeType
name|type
init|=
operator|(
operator|(
name|MimeInfo
operator|)
name|xmls
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|magics
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Magic
name|magic
init|=
operator|(
name|Magic
operator|)
name|magics
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
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
operator|(
name|MimeInfo
operator|)
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
name|info
operator|.
name|getName
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// Checks for some unsolved dependencies on this new type
name|List
name|deps
init|=
operator|(
name|List
operator|)
name|unsolvedDeps
operator|.
name|get
argument_list|(
name|info
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|deps
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
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
operator|(
operator|(
name|MimeInfo
operator|)
name|deps
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
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
name|info
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Checks if some of my super-types are not already solved
name|String
index|[]
name|superTypes
init|=
name|type
operator|.
name|getSuperTypes
argument_list|()
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
name|superTypes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|MimeInfo
name|superType
init|=
operator|(
name|MimeInfo
operator|)
name|types
operator|.
name|get
argument_list|(
name|superTypes
index|[
name|i
index|]
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
operator|(
name|List
operator|)
name|unsolvedDeps
operator|.
name|get
argument_list|(
name|superTypes
index|[
name|i
index|]
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
argument_list|()
expr_stmt|;
name|unsolvedDeps
operator|.
name|put
argument_list|(
name|superTypes
index|[
name|i
index|]
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
name|Magic
index|[]
name|magics
init|=
name|type
operator|.
name|getMagics
argument_list|()
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
name|magics
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|magics
operator|.
name|add
argument_list|(
name|magics
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
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
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Iterator
name|iter
init|=
name|types
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MimeType
name|type
init|=
operator|(
operator|(
name|MimeInfo
operator|)
name|iter
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
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
name|MimeType
name|type
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|level
init|=
literal|0
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
operator|<=
name|this
operator|.
name|level
condition|)
block|{
return|return;
block|}
name|this
operator|.
name|level
operator|=
name|level
expr_stmt|;
comment|// Update all my super-types
name|String
index|[]
name|supers
init|=
name|type
operator|.
name|getSuperTypes
argument_list|()
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
name|supers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|MimeInfo
name|sup
init|=
operator|(
name|MimeInfo
operator|)
name|types
operator|.
name|get
argument_list|(
name|supers
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|sup
operator|!=
literal|null
condition|)
block|{
name|sup
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
name|String
name|getName
parameter_list|()
block|{
return|return
name|type
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

