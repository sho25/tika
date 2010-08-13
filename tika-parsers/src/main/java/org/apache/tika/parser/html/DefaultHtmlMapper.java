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
name|html
package|;
end_package

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
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * The default HTML mapping rules in Tika.  *  * @since Apache Tika 0.6  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
specifier|public
class|class
name|DefaultHtmlMapper
implements|implements
name|HtmlMapper
block|{
comment|// Based on http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|SAFE_ELEMENTS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"H1"
argument_list|,
literal|"h1"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"H2"
argument_list|,
literal|"h2"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"H3"
argument_list|,
literal|"h3"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"H4"
argument_list|,
literal|"h4"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"H5"
argument_list|,
literal|"h5"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"H6"
argument_list|,
literal|"h6"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"P"
argument_list|,
literal|"p"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"PRE"
argument_list|,
literal|"pre"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"BLOCKQUOTE"
argument_list|,
literal|"blockquote"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"UL"
argument_list|,
literal|"ul"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"OL"
argument_list|,
literal|"ol"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"MENU"
argument_list|,
literal|"ul"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"LI"
argument_list|,
literal|"li"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"DL"
argument_list|,
literal|"dl"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"DT"
argument_list|,
literal|"dt"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"DD"
argument_list|,
literal|"dd"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TABLE"
argument_list|,
literal|"table"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"THEAD"
argument_list|,
literal|"thead"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TBODY"
argument_list|,
literal|"tbody"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TR"
argument_list|,
literal|"tr"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TH"
argument_list|,
literal|"th"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TD"
argument_list|,
literal|"td"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"ADDRESS"
argument_list|,
literal|"address"
argument_list|)
expr_stmt|;
comment|// TIKA-463 - add additional elements that contain URLs
name|put
argument_list|(
literal|"AREA"
argument_list|,
literal|"area"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"IMG"
argument_list|,
literal|"img"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"FRAMESET"
argument_list|,
literal|"frameset"
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"FRAME"
argument_list|,
literal|"frame"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|DISCARDABLE_ELEMENTS
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
block|{
name|add
argument_list|(
literal|"STYLE"
argument_list|)
expr_stmt|;
name|add
argument_list|(
literal|"SCRIPT"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|SAFE_ATTRIBUTES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
block|{
block|{
name|put
argument_list|(
literal|"a"
argument_list|,
name|attrSet
argument_list|(
literal|"rel"
argument_list|,
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"img"
argument_list|,
name|attrSet
argument_list|(
literal|"src"
argument_list|)
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"frame"
argument_list|,
name|attrSet
argument_list|(
literal|"src"
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO KKr - fill out this set.
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|attrSet
parameter_list|(
name|String
modifier|...
name|attrs
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|attr
range|:
name|attrs
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * @since Apache Tika 0.8      */
specifier|public
specifier|static
specifier|final
name|HtmlMapper
name|INSTANCE
init|=
operator|new
name|DefaultHtmlMapper
argument_list|()
decl_stmt|;
specifier|public
name|String
name|mapSafeElement
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|SAFE_ELEMENTS
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Normalizes an attribute name. Assumes that the element name       * is valid and normalized       */
specifier|public
name|String
name|mapSafeAttribute
parameter_list|(
name|String
name|elementName
parameter_list|,
name|String
name|attributeName
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|safeAttrs
init|=
name|SAFE_ATTRIBUTES
operator|.
name|get
argument_list|(
name|elementName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|safeAttrs
operator|!=
literal|null
operator|)
operator|&&
name|safeAttrs
operator|.
name|contains
argument_list|(
name|attributeName
argument_list|)
condition|)
block|{
return|return
name|attributeName
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|boolean
name|isDiscardElement
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|DISCARDABLE_ELEMENTS
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

