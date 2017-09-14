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
name|mime
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
name|InputStream
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
name|Collections
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
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_comment
comment|/**  * Creates instances of MimeTypes.  */
end_comment

begin_class
specifier|public
class|class
name|MimeTypesFactory
block|{
comment|/**      * System property to set a path to an additional external custom mimetypes       * XML file to be loaded.      */
specifier|public
specifier|static
specifier|final
name|String
name|CUSTOM_MIMES_SYS_PROP
init|=
literal|"tika.custom-mimetypes"
decl_stmt|;
comment|/**      * Creates an empty instance; same as calling new MimeTypes().      *      * @return an empty instance      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|()
block|{
return|return
operator|new
name|MimeTypes
argument_list|()
return|;
block|}
comment|/**      * Creates and returns a MimeTypes instance from the specified document.      * @throws MimeTypeException if the type configuration is invalid      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|Document
name|document
parameter_list|)
throws|throws
name|MimeTypeException
block|{
name|MimeTypes
name|mimeTypes
init|=
operator|new
name|MimeTypes
argument_list|()
decl_stmt|;
operator|new
name|MimeTypesReader
argument_list|(
name|mimeTypes
argument_list|)
operator|.
name|read
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|mimeTypes
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|mimeTypes
return|;
block|}
comment|/**      * Creates and returns a MimeTypes instance from the specified input stream.      * Does not close the input stream(s).      * @throws IOException if the stream can not be read      * @throws MimeTypeException if the type configuration is invalid      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|InputStream
modifier|...
name|inputStreams
parameter_list|)
throws|throws
name|IOException
throws|,
name|MimeTypeException
block|{
name|MimeTypes
name|mimeTypes
init|=
operator|new
name|MimeTypes
argument_list|()
decl_stmt|;
name|MimeTypesReader
name|reader
init|=
operator|new
name|MimeTypesReader
argument_list|(
name|mimeTypes
argument_list|)
decl_stmt|;
for|for
control|(
name|InputStream
name|inputStream
range|:
name|inputStreams
control|)
block|{
name|reader
operator|.
name|read
argument_list|(
name|inputStream
argument_list|)
expr_stmt|;
block|}
name|mimeTypes
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|mimeTypes
return|;
block|}
comment|/** @see #create(InputStream...) */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
throws|,
name|MimeTypeException
block|{
return|return
name|create
argument_list|(
operator|new
name|InputStream
index|[]
block|{
name|stream
block|}
argument_list|)
return|;
block|}
comment|/**      * Creates and returns a MimeTypes instance from the resource      * at the location specified by the URL.  Opens and closes the      * InputStream from the URL.      * If multiple URLs are supplied, then they are loaded in turn.       *      * @throws IOException if the URL can not be accessed      * @throws MimeTypeException if the type configuration is invalid      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|URL
modifier|...
name|urls
parameter_list|)
throws|throws
name|IOException
throws|,
name|MimeTypeException
block|{
name|InputStream
index|[]
name|streams
init|=
operator|new
name|InputStream
index|[
name|urls
operator|.
name|length
index|]
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
name|streams
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|streams
index|[
name|i
index|]
operator|=
name|urls
index|[
name|i
index|]
operator|.
name|openStream
argument_list|()
expr_stmt|;
block|}
try|try
block|{
return|return
name|create
argument_list|(
name|streams
argument_list|)
return|;
block|}
finally|finally
block|{
for|for
control|(
name|InputStream
name|stream
range|:
name|streams
control|)
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** @see #create(URL...) */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|URL
name|url
parameter_list|)
throws|throws
name|IOException
throws|,
name|MimeTypeException
block|{
return|return
name|create
argument_list|(
operator|new
name|URL
index|[]
block|{
name|url
block|}
argument_list|)
return|;
block|}
comment|/**      * Creates and returns a MimeTypes instance from the specified file path,      * as interpreted by the class loader in getResource().      *      * @throws IOException if the file can not be accessed      * @throws MimeTypeException if the type configuration is invalid      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|String
name|filePath
parameter_list|)
throws|throws
name|IOException
throws|,
name|MimeTypeException
block|{
return|return
name|create
argument_list|(
name|MimeTypesReader
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|filePath
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Creates and returns a MimeTypes instance. The core mimetypes      *  will be loaded from the specified file path, and any custom      *  override mimetypes found will loaded afterwards.      * The file paths will be interpreted by the default class loader in       *  getResource().      *       * @param coreFilePath The main MimeTypes file to load      * @param extensionFilePath The name of extension MimeType files to load afterwards      *      * @throws IOException if the file can not be accessed      * @throws MimeTypeException if the type configuration is invalid      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|String
name|coreFilePath
parameter_list|,
name|String
name|extensionFilePath
parameter_list|)
throws|throws
name|IOException
throws|,
name|MimeTypeException
block|{
return|return
name|create
argument_list|(
name|coreFilePath
argument_list|,
name|extensionFilePath
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Creates and returns a MimeTypes instance. The core mimetypes      *  will be loaded from the specified file path, and any custom      *  override mimetypes found will loaded afterwards.      * The file paths will be interpreted by the specified class        *  loader in getResource().      *  It will also load custom mimetypes from the system property      *  {@link #CUSTOM_MIMES_SYS_PROP}, if specified.      *       * @param coreFilePath The main MimeTypes file to load      * @param extensionFilePath The name of extension MimeType files to load afterwards      *      * @throws IOException if the file can not be accessed      * @throws MimeTypeException if the type configuration is invalid      */
specifier|public
specifier|static
name|MimeTypes
name|create
parameter_list|(
name|String
name|coreFilePath
parameter_list|,
name|String
name|extensionFilePath
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|)
throws|throws
name|IOException
throws|,
name|MimeTypeException
block|{
comment|// If no specific classloader was requested, use our own class's one
if|if
condition|(
name|classLoader
operator|==
literal|null
condition|)
block|{
name|classLoader
operator|=
name|MimeTypesReader
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
block|}
comment|// This allows us to replicate class.getResource() when using
comment|//  the classloader directly
name|String
name|classPrefix
init|=
name|MimeTypesReader
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
literal|"/"
decl_stmt|;
comment|// Get the core URL, and all the extensions URLs
name|URL
name|coreURL
init|=
name|classLoader
operator|.
name|getResource
argument_list|(
name|classPrefix
operator|+
name|coreFilePath
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|URL
argument_list|>
name|extensionURLs
init|=
name|Collections
operator|.
name|list
argument_list|(
name|classLoader
operator|.
name|getResources
argument_list|(
name|classPrefix
operator|+
name|extensionFilePath
argument_list|)
argument_list|)
decl_stmt|;
comment|// Swap that into an Array, and process
name|List
argument_list|<
name|URL
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<
name|URL
argument_list|>
argument_list|()
decl_stmt|;
name|urls
operator|.
name|add
argument_list|(
name|coreURL
argument_list|)
expr_stmt|;
name|urls
operator|.
name|addAll
argument_list|(
name|extensionURLs
argument_list|)
expr_stmt|;
name|String
name|customMimesPath
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|CUSTOM_MIMES_SYS_PROP
argument_list|)
decl_stmt|;
if|if
condition|(
name|customMimesPath
operator|!=
literal|null
condition|)
block|{
name|File
name|externalFile
init|=
operator|new
name|File
argument_list|(
name|customMimesPath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|externalFile
operator|.
name|exists
argument_list|()
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Specified custom mimetypes file not found: "
operator|+
name|customMimesPath
argument_list|)
throw|;
name|URL
name|externalURL
init|=
name|externalFile
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
decl_stmt|;
name|urls
operator|.
name|add
argument_list|(
name|externalURL
argument_list|)
expr_stmt|;
block|}
return|return
name|create
argument_list|(
name|urls
operator|.
name|toArray
argument_list|(
operator|new
name|URL
index|[
name|urls
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

