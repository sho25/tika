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
name|microsoft
operator|.
name|ooxml
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFAbstractNum
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFNumbering
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|poi
operator|.
name|xwpf
operator|.
name|usermodel
operator|.
name|XWPFParagraph
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
name|parser
operator|.
name|microsoft
operator|.
name|AbstractListManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|wordprocessingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTAbstractNum
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|wordprocessingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTDecimalNumber
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|wordprocessingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTLvl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openxmlformats
operator|.
name|schemas
operator|.
name|wordprocessingml
operator|.
name|x2006
operator|.
name|main
operator|.
name|CTNum
import|;
end_import

begin_class
specifier|public
class|class
name|XWPFListManager
extends|extends
name|AbstractListManager
block|{
specifier|private
specifier|final
specifier|static
name|boolean
name|OVERRIDE_AVAILABLE
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|SKIP_FORMAT
init|=
name|Character
operator|.
name|toString
argument_list|(
operator|(
name|char
operator|)
literal|61623
argument_list|)
decl_stmt|;
comment|//if this shows up as the lvlText, don't show a number
static|static
block|{
name|boolean
name|b
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumLvl"
argument_list|)
expr_stmt|;
name|b
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{         }
name|b
operator|=
name|OVERRIDE_AVAILABLE
operator|=
literal|false
expr_stmt|;
block|}
specifier|private
specifier|final
name|XWPFNumbering
name|numbering
decl_stmt|;
comment|//map of numId (which paragraph series is this a member of?), levelcounts
specifier|public
name|XWPFListManager
parameter_list|(
name|XWPFDocument
name|document
parameter_list|)
block|{
name|numbering
operator|=
name|document
operator|.
name|getNumbering
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getFormattedNumber
parameter_list|(
specifier|final
name|XWPFParagraph
name|paragraph
parameter_list|)
block|{
name|int
name|currNumId
init|=
name|paragraph
operator|.
name|getNumID
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|CTNum
name|ctNum
init|=
name|numbering
operator|.
name|getNum
argument_list|(
name|paragraph
operator|.
name|getNumID
argument_list|()
argument_list|)
operator|.
name|getCTNum
argument_list|()
decl_stmt|;
name|CTDecimalNumber
name|abNum
init|=
name|ctNum
operator|.
name|getAbstractNumId
argument_list|()
decl_stmt|;
name|int
name|currAbNumId
init|=
name|abNum
operator|.
name|getVal
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|ParagraphLevelCounter
name|lc
init|=
name|listLevelMap
operator|.
name|get
argument_list|(
name|currAbNumId
argument_list|)
decl_stmt|;
name|LevelTuple
index|[]
name|overrideTuples
init|=
name|overrideTupleMap
operator|.
name|get
argument_list|(
name|currNumId
argument_list|)
decl_stmt|;
if|if
condition|(
name|lc
operator|==
literal|null
condition|)
block|{
name|lc
operator|=
name|loadLevelTuples
argument_list|(
name|abNum
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|overrideTuples
operator|==
literal|null
condition|)
block|{
name|overrideTuples
operator|=
name|loadOverrideTuples
argument_list|(
name|ctNum
argument_list|,
name|lc
operator|.
name|getNumberOfLevels
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|formattedString
init|=
name|lc
operator|.
name|incrementLevel
argument_list|(
name|paragraph
operator|.
name|getNumIlvl
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|,
name|overrideTuples
argument_list|)
decl_stmt|;
name|listLevelMap
operator|.
name|put
argument_list|(
name|currAbNumId
argument_list|,
name|lc
argument_list|)
expr_stmt|;
name|overrideTupleMap
operator|.
name|put
argument_list|(
name|currNumId
argument_list|,
name|overrideTuples
argument_list|)
expr_stmt|;
return|return
name|formattedString
return|;
block|}
comment|/**      * WARNING: currently always returns null.      * TODO: Once CTNumLvl is available to Tika,      * we can turn this back on.      *      * @param ctNum  number on which to build the overrides      * @param length length of intended array      * @return null or an array of override tuples of length {@param length}      */
specifier|private
name|LevelTuple
index|[]
name|loadOverrideTuples
parameter_list|(
name|CTNum
name|ctNum
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
literal|null
return|;
comment|/*        LevelTuple[] levelTuples = new LevelTuple[length];         int overrideLength = ctNum.sizeOfLvlOverrideArray();         if (overrideLength == 0) {             return null;         }         for (int i = 0; i< length; i++) {             LevelTuple tuple;             if (i>= overrideLength) {                 tuple = new LevelTuple("%"+i+".");             } else {                 CTNumLvl ctNumLvl = ctNum.getLvlOverrideArray(i);                 if (ctNumLvl != null) {                     tuple = buildTuple(i, ctNumLvl.getLvl());                 } else {                     tuple = new LevelTuple("%"+i+".");                 }             }             levelTuples[i] = tuple;         }         return levelTuples;*/
block|}
specifier|private
name|ParagraphLevelCounter
name|loadLevelTuples
parameter_list|(
name|CTDecimalNumber
name|abNum
parameter_list|)
block|{
comment|//Unfortunately, we need to go this far into the underlying structure
comment|//to get the abstract num information for the edge case where
comment|//someone skips a level and the format is not context-free, e.g. "1.B.i".
name|XWPFAbstractNum
name|abstractNum
init|=
name|numbering
operator|.
name|getAbstractNum
argument_list|(
name|abNum
operator|.
name|getVal
argument_list|()
argument_list|)
decl_stmt|;
name|CTAbstractNum
name|ctAbstractNum
init|=
name|abstractNum
operator|.
name|getCTAbstractNum
argument_list|()
decl_stmt|;
name|LevelTuple
index|[]
name|levels
init|=
operator|new
name|LevelTuple
index|[
name|ctAbstractNum
operator|.
name|sizeOfLvlArray
argument_list|()
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
name|levels
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|levels
index|[
name|i
index|]
operator|=
name|buildTuple
argument_list|(
name|i
argument_list|,
name|ctAbstractNum
operator|.
name|getLvlArray
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ParagraphLevelCounter
argument_list|(
name|levels
argument_list|)
return|;
block|}
specifier|private
name|LevelTuple
name|buildTuple
parameter_list|(
name|int
name|level
parameter_list|,
name|CTLvl
name|ctLvl
parameter_list|)
block|{
name|boolean
name|isLegal
init|=
literal|false
decl_stmt|;
name|int
name|start
init|=
literal|1
decl_stmt|;
name|int
name|restart
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|lvlText
init|=
literal|"%"
operator|+
name|level
operator|+
literal|"."
decl_stmt|;
name|String
name|numFmt
init|=
literal|"decimal"
decl_stmt|;
if|if
condition|(
name|ctLvl
operator|!=
literal|null
operator|&&
name|ctLvl
operator|.
name|getIsLgl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|isLegal
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|ctLvl
operator|!=
literal|null
operator|&&
name|ctLvl
operator|.
name|getNumFmt
argument_list|()
operator|!=
literal|null
operator|&&
name|ctLvl
operator|.
name|getNumFmt
argument_list|()
operator|.
name|getVal
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|numFmt
operator|=
name|ctLvl
operator|.
name|getNumFmt
argument_list|()
operator|.
name|getVal
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ctLvl
operator|!=
literal|null
operator|&&
name|ctLvl
operator|.
name|getLvlRestart
argument_list|()
operator|!=
literal|null
operator|&&
name|ctLvl
operator|.
name|getLvlRestart
argument_list|()
operator|.
name|getVal
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|restart
operator|=
name|ctLvl
operator|.
name|getLvlRestart
argument_list|()
operator|.
name|getVal
argument_list|()
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ctLvl
operator|!=
literal|null
operator|&&
name|ctLvl
operator|.
name|getStart
argument_list|()
operator|!=
literal|null
operator|&&
name|ctLvl
operator|.
name|getStart
argument_list|()
operator|.
name|getVal
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|start
operator|=
name|ctLvl
operator|.
name|getStart
argument_list|()
operator|.
name|getVal
argument_list|()
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//this is a hack. Currently, this gets the lowest possible
comment|//start for a given numFmt.  We should probably try to grab the
comment|//restartNumberingAfterBreak value in
comment|//e.g.<w:abstractNum w:abstractNumId="12" w15:restartNumberingAfterBreak="0">???
if|if
condition|(
literal|"decimal"
operator|.
name|equals
argument_list|(
name|numFmt
argument_list|)
operator|||
literal|"ordinal"
operator|.
name|equals
argument_list|(
name|numFmt
argument_list|)
operator|||
literal|"decimalZero"
operator|.
name|equals
argument_list|(
name|numFmt
argument_list|)
condition|)
block|{
name|start
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|start
operator|=
literal|1
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ctLvl
operator|!=
literal|null
operator|&&
name|ctLvl
operator|.
name|getLvlText
argument_list|()
operator|!=
literal|null
operator|&&
name|ctLvl
operator|.
name|getLvlText
argument_list|()
operator|.
name|getVal
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|lvlText
operator|=
name|ctLvl
operator|.
name|getLvlText
argument_list|()
operator|.
name|getVal
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|LevelTuple
argument_list|(
name|start
argument_list|,
name|restart
argument_list|,
name|lvlText
argument_list|,
name|numFmt
argument_list|,
name|isLegal
argument_list|)
return|;
block|}
block|}
end_class

end_unit

