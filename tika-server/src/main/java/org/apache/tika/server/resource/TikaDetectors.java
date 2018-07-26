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
name|server
operator|.
name|resource
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|GET
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
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
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|Gson
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|GsonBuilder
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
name|detect
operator|.
name|CompositeDetector
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
name|detect
operator|.
name|Detector
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
name|server
operator|.
name|HTMLHelper
import|;
end_import

begin_comment
comment|/**  *<p>Provides details of all the {@link Detector}s registered with  * Apache Tika, similar to<em>--list-detectors</em> with the Tika CLI.  */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|"/detectors"
argument_list|)
specifier|public
class|class
name|TikaDetectors
block|{
specifier|private
specifier|static
specifier|final
name|Gson
name|GSON
init|=
operator|new
name|GsonBuilder
argument_list|()
operator|.
name|disableHtmlEscaping
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
name|HTMLHelper
name|html
decl_stmt|;
specifier|public
name|TikaDetectors
parameter_list|()
block|{
name|this
operator|.
name|html
operator|=
operator|new
name|HTMLHelper
argument_list|()
expr_stmt|;
block|}
annotation|@
name|GET
annotation|@
name|Produces
argument_list|(
literal|"text/html"
argument_list|)
specifier|public
name|String
name|getDectorsHTML
parameter_list|()
block|{
name|StringBuffer
name|h
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|html
operator|.
name|generateHeader
argument_list|(
name|h
argument_list|,
literal|"Detectors available to Apache Tika"
argument_list|)
expr_stmt|;
name|detectorAsHTML
argument_list|(
name|TikaResource
operator|.
name|getConfig
argument_list|()
operator|.
name|getDetector
argument_list|()
argument_list|,
name|h
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|html
operator|.
name|generateFooter
argument_list|(
name|h
argument_list|)
expr_stmt|;
return|return
name|h
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|detectorAsHTML
parameter_list|(
name|Detector
name|d
parameter_list|,
name|StringBuffer
name|html
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|html
operator|.
name|append
argument_list|(
literal|"<h"
argument_list|)
expr_stmt|;
name|html
operator|.
name|append
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|html
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|d
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|html
operator|.
name|append
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|html
operator|.
name|append
argument_list|(
literal|"</h"
argument_list|)
expr_stmt|;
name|html
operator|.
name|append
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|html
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
name|html
operator|.
name|append
argument_list|(
literal|"<p>Class: "
argument_list|)
expr_stmt|;
name|html
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|html
operator|.
name|append
argument_list|(
literal|"</p>"
argument_list|)
expr_stmt|;
if|if
condition|(
name|d
operator|instanceof
name|CompositeDetector
condition|)
block|{
name|html
operator|.
name|append
argument_list|(
literal|"<p>Composite Detector</p>"
argument_list|)
expr_stmt|;
for|for
control|(
name|Detector
name|cd
range|:
operator|(
operator|(
name|CompositeDetector
operator|)
name|d
operator|)
operator|.
name|getDetectors
argument_list|()
control|)
block|{
name|detectorAsHTML
argument_list|(
name|cd
argument_list|,
name|html
argument_list|,
name|level
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|GET
annotation|@
name|Produces
argument_list|(
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
specifier|public
name|String
name|getDetectorsJSON
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|details
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|detectorAsMap
argument_list|(
name|TikaResource
operator|.
name|getConfig
argument_list|()
operator|.
name|getDetector
argument_list|()
argument_list|,
name|details
argument_list|)
expr_stmt|;
return|return
name|GSON
operator|.
name|toJson
argument_list|(
name|details
argument_list|)
return|;
block|}
specifier|private
name|void
name|detectorAsMap
parameter_list|(
name|Detector
name|d
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|details
parameter_list|)
block|{
name|details
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|d
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|isComposite
init|=
operator|(
name|d
operator|instanceof
name|CompositeDetector
operator|)
decl_stmt|;
name|details
operator|.
name|put
argument_list|(
literal|"composite"
argument_list|,
name|isComposite
argument_list|)
expr_stmt|;
if|if
condition|(
name|isComposite
condition|)
block|{
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|c
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Detector
name|cd
range|:
operator|(
operator|(
name|CompositeDetector
operator|)
name|d
operator|)
operator|.
name|getDetectors
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|cdet
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|detectorAsMap
argument_list|(
name|cd
argument_list|,
name|cdet
argument_list|)
expr_stmt|;
name|c
operator|.
name|add
argument_list|(
name|cdet
argument_list|)
expr_stmt|;
block|}
name|details
operator|.
name|put
argument_list|(
literal|"children"
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|GET
annotation|@
name|Produces
argument_list|(
literal|"text/plain"
argument_list|)
specifier|public
name|String
name|getDetectorsPlain
parameter_list|()
block|{
name|StringBuffer
name|text
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|renderDetector
argument_list|(
name|TikaResource
operator|.
name|getConfig
argument_list|()
operator|.
name|getDetector
argument_list|()
argument_list|,
name|text
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|text
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|renderDetector
parameter_list|(
name|Detector
name|d
parameter_list|,
name|StringBuffer
name|text
parameter_list|,
name|int
name|indent
parameter_list|)
block|{
name|boolean
name|isComposite
init|=
operator|(
name|d
operator|instanceof
name|CompositeDetector
operator|)
decl_stmt|;
name|String
name|name
init|=
name|d
operator|.
name|getClass
argument_list|()
operator|.
name|getName
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
name|indent
condition|;
name|i
operator|++
control|)
block|{
name|text
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
name|text
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|isComposite
condition|)
block|{
name|text
operator|.
name|append
argument_list|(
literal|" (Composite Detector):\n"
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Detector
argument_list|>
name|subDetectors
init|=
operator|(
operator|(
name|CompositeDetector
operator|)
name|d
operator|)
operator|.
name|getDetectors
argument_list|()
decl_stmt|;
for|for
control|(
name|Detector
name|sd
range|:
name|subDetectors
control|)
block|{
name|renderDetector
argument_list|(
name|sd
argument_list|,
name|text
argument_list|,
name|indent
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|text
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

