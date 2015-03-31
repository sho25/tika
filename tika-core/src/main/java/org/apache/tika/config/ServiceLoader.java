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
name|config
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|io
operator|.
name|InputStreamReader
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
name|Collection
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
name|Enumeration
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
name|regex
operator|.
name|Pattern
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
name|io
operator|.
name|IOUtils
import|;
end_import

begin_comment
comment|/**  * Internal utility class that Tika uses to look up service providers.  *  * @since Apache Tika 0.9  */
end_comment

begin_class
specifier|public
class|class
name|ServiceLoader
block|{
comment|/**      * The default context class loader to use for all threads, or      *<code>null</code> to automatically select the context class loader.      */
specifier|private
specifier|static
specifier|volatile
name|ClassLoader
name|contextClassLoader
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
class|class
name|RankedService
implements|implements
name|Comparable
argument_list|<
name|RankedService
argument_list|>
block|{
specifier|private
name|Object
name|service
decl_stmt|;
specifier|private
name|int
name|rank
decl_stmt|;
specifier|public
name|RankedService
parameter_list|(
name|Object
name|service
parameter_list|,
name|int
name|rank
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
expr_stmt|;
name|this
operator|.
name|rank
operator|=
name|rank
expr_stmt|;
block|}
specifier|public
name|boolean
name|isInstanceOf
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|iface
parameter_list|)
block|{
return|return
name|iface
operator|.
name|isAssignableFrom
argument_list|(
name|service
operator|.
name|getClass
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|RankedService
name|that
parameter_list|)
block|{
return|return
name|that
operator|.
name|rank
operator|-
name|rank
return|;
comment|// highest number first
block|}
block|}
comment|/**      * The dynamic set of services available in an OSGi environment.      * Managed by the {@link TikaActivator} class and used as an additional      * source of service instances in the {@link #loadServiceProviders(Class)}      * method.      */
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|RankedService
argument_list|>
name|services
init|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|RankedService
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Returns the context class loader of the current thread. If such      * a class loader is not available, then the loader of this class or      * finally the system class loader is returned.      *      * @see<a href="https://issues.apache.org/jira/browse/TIKA-441">TIKA-441</a>      * @return context class loader, or<code>null</code> if no loader      *         is available      */
specifier|static
name|ClassLoader
name|getContextClassLoader
parameter_list|()
block|{
name|ClassLoader
name|loader
init|=
name|contextClassLoader
decl_stmt|;
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
name|loader
operator|=
name|ServiceLoader
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
name|loader
operator|=
name|ClassLoader
operator|.
name|getSystemClassLoader
argument_list|()
expr_stmt|;
block|}
return|return
name|loader
return|;
block|}
comment|/**      * Sets the context class loader to use for all threads that access      * this class. Used for example in an OSGi environment to avoid problems      * with the default context class loader.      *      * @param loader default context class loader,      *               or<code>null</code> to automatically pick the loader      */
specifier|public
specifier|static
name|void
name|setContextClassLoader
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
block|{
name|contextClassLoader
operator|=
name|loader
expr_stmt|;
block|}
specifier|static
name|void
name|addService
parameter_list|(
name|Object
name|reference
parameter_list|,
name|Object
name|service
parameter_list|,
name|int
name|rank
parameter_list|)
block|{
synchronized|synchronized
init|(
name|services
init|)
block|{
name|services
operator|.
name|put
argument_list|(
name|reference
argument_list|,
operator|new
name|RankedService
argument_list|(
name|service
argument_list|,
name|rank
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|Object
name|removeService
parameter_list|(
name|Object
name|reference
parameter_list|)
block|{
synchronized|synchronized
init|(
name|services
init|)
block|{
return|return
name|services
operator|.
name|remove
argument_list|(
name|reference
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|final
name|ClassLoader
name|loader
decl_stmt|;
specifier|private
specifier|final
name|LoadErrorHandler
name|handler
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|dynamic
decl_stmt|;
specifier|public
name|ServiceLoader
parameter_list|(
name|ClassLoader
name|loader
parameter_list|,
name|LoadErrorHandler
name|handler
parameter_list|,
name|boolean
name|dynamic
parameter_list|)
block|{
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|dynamic
operator|=
name|dynamic
expr_stmt|;
block|}
specifier|public
name|ServiceLoader
parameter_list|(
name|ClassLoader
name|loader
parameter_list|,
name|LoadErrorHandler
name|handler
parameter_list|)
block|{
name|this
argument_list|(
name|loader
argument_list|,
name|handler
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ServiceLoader
parameter_list|(
name|ClassLoader
name|loader
parameter_list|)
block|{
name|this
argument_list|(
name|loader
argument_list|,
name|LoadErrorHandler
operator|.
name|IGNORE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ServiceLoader
parameter_list|()
block|{
name|this
argument_list|(
name|getContextClassLoader
argument_list|()
argument_list|,
name|LoadErrorHandler
operator|.
name|IGNORE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the load error handler used by this loader.      *      * @return load error handler      * @since Apache Tika 1.3      */
specifier|public
name|LoadErrorHandler
name|getLoadErrorHandler
parameter_list|()
block|{
return|return
name|handler
return|;
block|}
comment|/**      * Returns an input stream for reading the specified resource from the      * configured class loader.      *      * @param name resource name      * @return input stream, or<code>null</code> if the resource was not found      * @see ClassLoader#getResourceAsStream(String)      * @since Apache Tika 1.1      */
specifier|public
name|InputStream
name|getResourceAsStream
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|loader
operator|!=
literal|null
condition|)
block|{
return|return
name|loader
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**      * Loads and returns the named service class that's expected to implement      * the given interface.      *      * @param iface service interface      * @param name service class name      * @return service class      * @throws ClassNotFoundException if the service class can not be found      *                                or does not implement the given interface      * @see Class#forName(String, boolean, ClassLoader)      * @since Apache Tika 1.1      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|getServiceClass
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
if|if
condition|(
name|loader
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ClassNotFoundException
argument_list|(
literal|"Service class "
operator|+
name|name
operator|+
literal|" is not available"
argument_list|)
throw|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|klass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|loader
argument_list|)
decl_stmt|;
if|if
condition|(
name|klass
operator|.
name|isInterface
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ClassNotFoundException
argument_list|(
literal|"Service class "
operator|+
name|name
operator|+
literal|" is an interface"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|iface
operator|.
name|isAssignableFrom
argument_list|(
name|klass
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ClassNotFoundException
argument_list|(
literal|"Service class "
operator|+
name|name
operator|+
literal|" does not implement "
operator|+
name|iface
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
else|else
block|{
return|return
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
operator|)
name|klass
return|;
block|}
block|}
comment|/**      * Returns all the available service resources matching the      *  given pattern, such as all instances of tika-mimetypes.xml       *  on the classpath, or all org.apache.tika.parser.Parser       *  service files.      */
specifier|public
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|findServiceResources
parameter_list|(
name|String
name|filePattern
parameter_list|)
block|{
try|try
block|{
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|resources
init|=
name|loader
operator|.
name|getResources
argument_list|(
name|filePattern
argument_list|)
decl_stmt|;
return|return
name|resources
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ignore
parameter_list|)
block|{
comment|// We couldn't get the list of service resource files
name|List
argument_list|<
name|URL
argument_list|>
name|empty
init|=
name|Collections
operator|.
name|emptyList
argument_list|()
decl_stmt|;
return|return
name|Collections
operator|.
name|enumeration
argument_list|(
name|empty
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns all the available service providers of the given type.      *      * @param iface service provider interface      * @return available service providers      */
specifier|public
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|loadServiceProviders
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|providers
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|providers
operator|.
name|addAll
argument_list|(
name|loadDynamicServiceProviders
argument_list|(
name|iface
argument_list|)
argument_list|)
expr_stmt|;
name|providers
operator|.
name|addAll
argument_list|(
name|loadStaticServiceProviders
argument_list|(
name|iface
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|providers
return|;
block|}
comment|/**      * Returns the available dynamic service providers of the given type.      * The returned list is newly allocated and may be freely modified      * by the caller.      *      * @since Apache Tika 1.2      * @param iface service provider interface      * @return dynamic service providers      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|loadDynamicServiceProviders
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|)
block|{
if|if
condition|(
name|dynamic
condition|)
block|{
synchronized|synchronized
init|(
name|services
init|)
block|{
name|List
argument_list|<
name|RankedService
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|RankedService
argument_list|>
argument_list|(
name|services
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|providers
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RankedService
name|service
range|:
name|list
control|)
block|{
if|if
condition|(
name|service
operator|.
name|isInstanceOf
argument_list|(
name|iface
argument_list|)
condition|)
block|{
name|providers
operator|.
name|add
argument_list|(
operator|(
name|T
operator|)
name|service
operator|.
name|service
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|providers
return|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns the defined static service providers of the given type, without      * attempting to load them.      * The providers are loaded using the service provider mechanism using      * the configured class loader (if any).      *      * @since Apache Tika 1.6      * @param iface service provider interface      * @return static list of uninitialised service providers      */
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|String
argument_list|>
name|identifyStaticServiceProviders
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|loader
operator|!=
literal|null
condition|)
block|{
name|String
name|serviceName
init|=
name|iface
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|resources
init|=
name|findServiceResources
argument_list|(
literal|"META-INF/services/"
operator|+
name|serviceName
argument_list|)
decl_stmt|;
for|for
control|(
name|URL
name|resource
range|:
name|Collections
operator|.
name|list
argument_list|(
name|resources
argument_list|)
control|)
block|{
try|try
block|{
name|collectServiceClassNames
argument_list|(
name|resource
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|handler
operator|.
name|handleLoadError
argument_list|(
name|serviceName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|names
return|;
block|}
comment|/**      * Returns the available static service providers of the given type.      * The providers are loaded using the service provider mechanism using      * the configured class loader (if any). The returned list is newly      * allocated and may be freely modified by the caller.      *      * @since Apache Tika 1.2      * @param iface service provider interface      * @return static service providers      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|loadStaticServiceProviders
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|providers
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|loader
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|identifyStaticServiceProviders
argument_list|(
name|iface
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|klass
init|=
name|loader
operator|.
name|loadClass
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|iface
operator|.
name|isAssignableFrom
argument_list|(
name|klass
argument_list|)
condition|)
block|{
name|providers
operator|.
name|add
argument_list|(
operator|(
name|T
operator|)
name|klass
operator|.
name|newInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|handler
operator|.
name|handleLoadError
argument_list|(
name|name
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|providers
return|;
block|}
specifier|private
specifier|static
specifier|final
name|Pattern
name|COMMENT
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"#.*"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|WHITESPACE
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
specifier|private
name|void
name|collectServiceClassNames
parameter_list|(
name|URL
name|resource
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|stream
init|=
name|resource
operator|.
name|openStream
argument_list|()
decl_stmt|;
try|try
block|{
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|COMMENT
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|line
operator|=
name|WHITESPACE
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|names
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

