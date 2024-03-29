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
name|parser
operator|.
name|ctakes
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
name|OutputStream
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
name|Properties
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|NullOutputStream
operator|.
name|NULL_OUTPUT_STREAM
import|;
end_import

begin_comment
comment|/**  * Configuration for {@link CTAKESContentHandler}.  *   * This class allows to enable cTAKES and set its parameters.  */
end_comment

begin_class
specifier|public
class|class
name|CTAKESConfig
implements|implements
name|Serializable
block|{
comment|/**      * Serial version UID      */
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|1599741171775528923L
decl_stmt|;
comment|// Path to XML descriptor for AnalysisEngine
specifier|private
name|String
name|aeDescriptorPath
init|=
literal|"/ctakes-core/desc/analysis_engine/SentencesAndTokensAggregate.xml"
decl_stmt|;
comment|// UMLS username
specifier|private
name|String
name|UMLSUser
init|=
literal|""
decl_stmt|;
comment|// UMLS password
specifier|private
name|String
name|UMLSPass
init|=
literal|""
decl_stmt|;
comment|// Enables formatted output
specifier|private
name|boolean
name|prettyPrint
init|=
literal|true
decl_stmt|;
comment|// Type of cTAKES (UIMA) serializer
specifier|private
name|CTAKESSerializer
name|serializerType
init|=
name|CTAKESSerializer
operator|.
name|XMI
decl_stmt|;
comment|// OutputStream object used for CAS serialization
specifier|private
name|OutputStream
name|stream
init|=
name|NULL_OUTPUT_STREAM
decl_stmt|;
comment|// Enables CAS serialization
specifier|private
name|boolean
name|serialize
init|=
literal|false
decl_stmt|;
comment|// Enables text analysis using cTAKES
specifier|private
name|boolean
name|text
init|=
literal|true
decl_stmt|;
comment|// List of metadata to analyze using cTAKES
specifier|private
name|String
index|[]
name|metadata
init|=
literal|null
decl_stmt|;
comment|// List of annotation properties to add to metadata in addition to text covered by an annotation
specifier|private
name|CTAKESAnnotationProperty
index|[]
name|annotationProps
init|=
literal|null
decl_stmt|;
comment|// Character used to separate the annotation properties into metadata
specifier|private
name|char
name|separatorChar
init|=
literal|':'
decl_stmt|;
comment|/**      * Default constructor.      */
specifier|public
name|CTAKESConfig
parameter_list|()
block|{
name|init
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"CTAKESConfig.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Loads properties from InputStream and then tries to close InputStream.      * @param stream {@link InputStream} object used to read properties.      */
specifier|public
name|CTAKESConfig
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
name|init
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|init
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// TODO warning
block|}
finally|finally
block|{
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// TODO warning
block|}
block|}
block|}
name|setAeDescriptorPath
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"aeDescriptorPath"
argument_list|,
name|getAeDescriptorPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setUMLSUser
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"UMLSUser"
argument_list|,
name|getUMLSUser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setUMLSPass
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"UMLSPass"
argument_list|,
name|getUMLSPass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|Boolean
operator|.
name|valueOf
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"text"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|isText
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|setMetadata
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"metadata"
argument_list|,
name|getMetadataAsString
argument_list|()
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
expr_stmt|;
name|setAnnotationProps
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"annotationProps"
argument_list|,
name|getAnnotationPropsAsString
argument_list|()
argument_list|)
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
expr_stmt|;
name|setSeparatorChar
argument_list|(
name|props
operator|.
name|getProperty
argument_list|(
literal|"separatorChar"
argument_list|,
name|Character
operator|.
name|toString
argument_list|(
name|getSeparatorChar
argument_list|()
argument_list|)
argument_list|)
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the path to XML descriptor for AnalysisEngine.      * @return the path to XML descriptor for AnalysisEngine.      */
specifier|public
name|String
name|getAeDescriptorPath
parameter_list|()
block|{
return|return
name|aeDescriptorPath
return|;
block|}
comment|/**      * Returns the UMLS username.      * @return the UMLS username.      */
specifier|public
name|String
name|getUMLSUser
parameter_list|()
block|{
return|return
name|UMLSUser
return|;
block|}
comment|/**      * Returns the UMLS password.      * @return the UMLS password.      */
specifier|public
name|String
name|getUMLSPass
parameter_list|()
block|{
return|return
name|UMLSPass
return|;
block|}
comment|/**      * Returns {@code true} if formatted output is enabled, {@code false} otherwise.      * @return {@code true} if formatted output is enabled, {@code false} otherwise.      */
specifier|public
name|boolean
name|isPrettyPrint
parameter_list|()
block|{
return|return
name|prettyPrint
return|;
block|}
comment|/**      * Returns the type of cTAKES (UIMA) serializer used to write the CAS.      * @return the type of cTAKES serializer.      */
specifier|public
name|CTAKESSerializer
name|getSerializerType
parameter_list|()
block|{
return|return
name|serializerType
return|;
block|}
comment|/**      * Returns an {@link OutputStream} object used write the CAS.      * @return {@link OutputStream} object used write the CAS.      */
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
block|{
return|return
name|stream
return|;
block|}
comment|/**      * Returns {@code true} if CAS serialization is enabled, {@code false} otherwise.      * @return {@code true} if CAS serialization output is enabled, {@code false} otherwise.      */
specifier|public
name|boolean
name|isSerialize
parameter_list|()
block|{
return|return
name|serialize
return|;
block|}
comment|/**      * Returns {@code true} if content text analysis is enabled {@code false} otherwise.      * @return {@code true} if content text analysis is enabled {@code false} otherwise.      */
specifier|public
name|boolean
name|isText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
comment|/**      * Returns an array of metadata whose values will be analyzed using cTAKES.      * @return an array of metadata whose values will be analyzed using cTAKES.      */
specifier|public
name|String
index|[]
name|getMetadata
parameter_list|()
block|{
return|return
name|metadata
return|;
block|}
comment|/**      * Returns a string containing a comma-separated list of metadata whose values will be analyzed using cTAKES.      * @return a string containing a comma-separated list of metadata whose values will be analyzed using cTAKES.      */
specifier|public
name|String
name|getMetadataAsString
parameter_list|()
block|{
if|if
condition|(
name|metadata
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
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
name|metadata
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|metadata
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|metadata
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns an array of {@link CTAKESAnnotationProperty}'s that will be included into cTAKES metadata.      * @return an array of {@link CTAKESAnnotationProperty}'s that will be included into cTAKES metadata.      */
specifier|public
name|CTAKESAnnotationProperty
index|[]
name|getAnnotationProps
parameter_list|()
block|{
return|return
name|annotationProps
return|;
block|}
comment|/**      * Returns a string containing a comma-separated list of {@link CTAKESAnnotationProperty} names that will be included into cTAKES metadata.      * @return      */
specifier|public
name|String
name|getAnnotationPropsAsString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"coveredText"
argument_list|)
expr_stmt|;
if|if
condition|(
name|annotationProps
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|CTAKESAnnotationProperty
name|property
range|:
name|annotationProps
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|separatorChar
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Returns the separator character used for annotation properties.      * @return the separator character used for annotation properties.      */
specifier|public
name|char
name|getSeparatorChar
parameter_list|()
block|{
return|return
name|separatorChar
return|;
block|}
comment|/**      * Sets the path to XML descriptor for AnalysisEngine.      * @param aeDescriptorPath the path to XML descriptor for AnalysisEngine.      */
specifier|public
name|void
name|setAeDescriptorPath
parameter_list|(
name|String
name|aeDescriptorPath
parameter_list|)
block|{
name|this
operator|.
name|aeDescriptorPath
operator|=
name|aeDescriptorPath
expr_stmt|;
block|}
comment|/**      * Sets the UMLS username.      * @param uMLSUser the UMLS username.      */
specifier|public
name|void
name|setUMLSUser
parameter_list|(
name|String
name|uMLSUser
parameter_list|)
block|{
name|this
operator|.
name|UMLSUser
operator|=
name|uMLSUser
expr_stmt|;
block|}
comment|/**      * Sets the UMLS password.      * @param uMLSPass the UMLS password.      */
specifier|public
name|void
name|setUMLSPass
parameter_list|(
name|String
name|uMLSPass
parameter_list|)
block|{
name|this
operator|.
name|UMLSPass
operator|=
name|uMLSPass
expr_stmt|;
block|}
comment|/**      * Enables the formatted output for serializer.      * @param prettyPrint {@code true} to enable formatted output, {@code false} otherwise.      */
specifier|public
name|void
name|setPrettyPrint
parameter_list|(
name|boolean
name|prettyPrint
parameter_list|)
block|{
name|this
operator|.
name|prettyPrint
operator|=
name|prettyPrint
expr_stmt|;
block|}
comment|/**      * Sets the type of cTAKES (UIMA) serializer used to write CAS.       * @param serializerType the type of cTAKES serializer.      */
specifier|public
name|void
name|setSerializerType
parameter_list|(
name|CTAKESSerializer
name|serializerType
parameter_list|)
block|{
name|this
operator|.
name|serializerType
operator|=
name|serializerType
expr_stmt|;
block|}
comment|/**      * Sets the {@link OutputStream} object used to write the CAS.      * @param stream the {@link OutputStream} object used to write the CAS.      */
specifier|public
name|void
name|setOutputStream
parameter_list|(
name|OutputStream
name|stream
parameter_list|)
block|{
name|this
operator|.
name|stream
operator|=
name|stream
expr_stmt|;
block|}
comment|/**      * Enables CAS serialization.      * @param serialize {@code true} to enable CAS serialization, {@code false} otherwise.      */
specifier|public
name|void
name|setSerialize
parameter_list|(
name|boolean
name|serialize
parameter_list|)
block|{
name|this
operator|.
name|serialize
operator|=
name|serialize
expr_stmt|;
block|}
comment|/**      * Enables content text analysis using cTAKES.      * @param text {@code true} to enable content text analysis, {@code false} otherwise.      */
specifier|public
name|void
name|setText
parameter_list|(
name|boolean
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
comment|/**      * Sets the metadata whose values will be analyzed using cTAKES.      * @param metadata the metadata whose values will be analyzed using cTAKES.      */
specifier|public
name|void
name|setMetadata
parameter_list|(
name|String
index|[]
name|metadata
parameter_list|)
block|{
name|this
operator|.
name|metadata
operator|=
name|metadata
expr_stmt|;
block|}
comment|/**      * Sets the {@link CTAKESAnnotationProperty}'s that will be included into cTAKES metadata.      * @param annotationProps the {@link CTAKESAnnotationProperty}'s that will be included into cTAKES metadata.      */
specifier|public
name|void
name|setAnnotationProps
parameter_list|(
name|CTAKESAnnotationProperty
index|[]
name|annotationProps
parameter_list|)
block|{
name|this
operator|.
name|annotationProps
operator|=
name|annotationProps
expr_stmt|;
block|}
comment|/**      * ets the {@link CTAKESAnnotationProperty}'s that will be included into cTAKES metadata.      * @param annotationProps the {@link CTAKESAnnotationProperty}'s that will be included into cTAKES metadata.      */
specifier|public
name|void
name|setAnnotationProps
parameter_list|(
name|String
index|[]
name|annotationProps
parameter_list|)
block|{
name|CTAKESAnnotationProperty
index|[]
name|properties
init|=
operator|new
name|CTAKESAnnotationProperty
index|[
name|annotationProps
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
name|annotationProps
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|properties
index|[
name|i
index|]
operator|=
name|CTAKESAnnotationProperty
operator|.
name|valueOf
argument_list|(
name|annotationProps
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|setAnnotationProps
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the separator character used for annotation properties.      * @param separatorChar the separator character used for annotation properties.      */
specifier|public
name|void
name|setSeparatorChar
parameter_list|(
name|char
name|separatorChar
parameter_list|)
block|{
name|this
operator|.
name|separatorChar
operator|=
name|separatorChar
expr_stmt|;
block|}
block|}
end_class

end_unit

