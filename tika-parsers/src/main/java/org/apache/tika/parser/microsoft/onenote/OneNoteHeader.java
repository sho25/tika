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
name|onenote
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|exception
operator|.
name|TikaException
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

begin_class
class|class
name|OneNoteHeader
implements|implements
name|Serializable
block|{
name|GUID
name|guidFileType
decl_stmt|;
name|GUID
name|guidFile
decl_stmt|;
name|GUID
name|guidLegacyFileVersion
decl_stmt|;
name|GUID
name|guidFileFormat
decl_stmt|;
name|long
name|ffvLastCodeThatWroteToThisFile
decl_stmt|;
name|long
name|ffvOldestCodeThatHasWrittenToThisFile
decl_stmt|;
name|long
name|ffvNewestCodeThatHasWrittenToThisFile
decl_stmt|;
name|long
name|ffvOldestCodeThatMayReadThisFile
decl_stmt|;
name|FileChunkReference
name|fcrLegacyFreeChunkList
decl_stmt|;
name|FileChunkReference
name|fcrLegacyTransactionLog
decl_stmt|;
name|long
name|cTransactionsInLog
decl_stmt|;
name|long
name|cbLegacyExpectedFileLength
decl_stmt|;
name|long
name|rgbPlaceholder
decl_stmt|;
name|FileChunkReference
name|fcrLegacyFileNodeListRoot
decl_stmt|;
name|long
name|cbLegacyFreeSpaceInFreeChunkList
decl_stmt|;
name|long
name|ignoredZeroA
decl_stmt|;
name|long
name|ignoredZeroB
decl_stmt|;
name|long
name|ignoredZeroC
decl_stmt|;
name|long
name|ignoredZeroD
decl_stmt|;
name|GUID
name|guidAncestor
decl_stmt|;
name|long
name|crcName
decl_stmt|;
name|FileChunkReference
name|fcrHashedChunkList
decl_stmt|;
name|FileChunkReference
name|fcrTransactionLog
decl_stmt|;
name|FileChunkReference
name|fcrFileNodeListRoot
decl_stmt|;
name|FileChunkReference
name|fcrFreeChunkList
decl_stmt|;
name|long
name|cbExpectedFileLength
decl_stmt|;
name|long
name|cbFreeSpaceInFreeChunkList
decl_stmt|;
name|GUID
name|guidFileVersion
decl_stmt|;
name|long
name|nFileVersionGeneration
decl_stmt|;
name|GUID
name|guidDenyReadFileVersion
decl_stmt|;
name|long
name|grfDebugLogFlags
decl_stmt|;
name|FileChunkReference
name|fcrDebugLogA
decl_stmt|;
name|FileChunkReference
name|fcrDebugLogB
decl_stmt|;
name|long
name|buildNumberCreated
decl_stmt|;
name|long
name|buildNumberLastWroteToFile
decl_stmt|;
name|long
name|buildNumberOldestWritten
decl_stmt|;
name|long
name|buildNumberNewestWritten
decl_stmt|;
comment|/**      * Determine if this OneNote file pre-dates the open specs published by      * microsoft.      * @return True if file is based on the MS-ONE and MS-ONESTORE specs. False otherwise.      */
specifier|public
name|boolean
name|isLegacy
parameter_list|()
block|{
return|return
operator|!
name|GUID
operator|.
name|nil
argument_list|()
operator|.
name|equals
argument_list|(
name|guidLegacyFileVersion
argument_list|)
return|;
block|}
specifier|public
name|GUID
name|getGuidFileType
parameter_list|()
block|{
return|return
name|guidFileType
return|;
block|}
specifier|public
name|OneNoteHeader
name|setGuidFileType
parameter_list|(
name|GUID
name|guidFileType
parameter_list|)
block|{
name|this
operator|.
name|guidFileType
operator|=
name|guidFileType
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|GUID
name|getGuidFile
parameter_list|()
block|{
return|return
name|guidFile
return|;
block|}
specifier|public
name|OneNoteHeader
name|setGuidFile
parameter_list|(
name|GUID
name|guidFile
parameter_list|)
block|{
name|this
operator|.
name|guidFile
operator|=
name|guidFile
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|GUID
name|getGuidLegacyFileVersion
parameter_list|()
block|{
return|return
name|guidLegacyFileVersion
return|;
block|}
specifier|public
name|OneNoteHeader
name|setGuidLegacyFileVersion
parameter_list|(
name|GUID
name|guidLegacyFileVersion
parameter_list|)
block|{
name|this
operator|.
name|guidLegacyFileVersion
operator|=
name|guidLegacyFileVersion
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|GUID
name|getGuidFileFormat
parameter_list|()
block|{
return|return
name|guidFileFormat
return|;
block|}
specifier|public
name|OneNoteHeader
name|setGuidFileFormat
parameter_list|(
name|GUID
name|guidFileFormat
parameter_list|)
block|{
name|this
operator|.
name|guidFileFormat
operator|=
name|guidFileFormat
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getFfvLastCodeThatWroteToThisFile
parameter_list|()
block|{
return|return
name|ffvLastCodeThatWroteToThisFile
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFfvLastCodeThatWroteToThisFile
parameter_list|(
name|long
name|ffvLastCodeThatWroteToThisFile
parameter_list|)
block|{
name|this
operator|.
name|ffvLastCodeThatWroteToThisFile
operator|=
name|ffvLastCodeThatWroteToThisFile
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getFfvOldestCodeThatHasWrittenToThisFile
parameter_list|()
block|{
return|return
name|ffvOldestCodeThatHasWrittenToThisFile
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFfvOldestCodeThatHasWrittenToThisFile
parameter_list|(
name|long
name|ffvOldestCodeThatHasWrittenToThisFile
parameter_list|)
block|{
name|this
operator|.
name|ffvOldestCodeThatHasWrittenToThisFile
operator|=
name|ffvOldestCodeThatHasWrittenToThisFile
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getFfvNewestCodeThatHasWrittenToThisFile
parameter_list|()
block|{
return|return
name|ffvNewestCodeThatHasWrittenToThisFile
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFfvNewestCodeThatHasWrittenToThisFile
parameter_list|(
name|long
name|ffvNewestCodeThatHasWrittenToThisFile
parameter_list|)
block|{
name|this
operator|.
name|ffvNewestCodeThatHasWrittenToThisFile
operator|=
name|ffvNewestCodeThatHasWrittenToThisFile
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getFfvOldestCodeThatMayReadThisFile
parameter_list|()
block|{
return|return
name|ffvOldestCodeThatMayReadThisFile
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFfvOldestCodeThatMayReadThisFile
parameter_list|(
name|long
name|ffvOldestCodeThatMayReadThisFile
parameter_list|)
block|{
name|this
operator|.
name|ffvOldestCodeThatMayReadThisFile
operator|=
name|ffvOldestCodeThatMayReadThisFile
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getFcrLegacyFreeChunkList
parameter_list|()
block|{
return|return
name|fcrLegacyFreeChunkList
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFcrLegacyFreeChunkList
parameter_list|(
name|FileChunkReference
name|fcrLegacyFreeChunkList
parameter_list|)
block|{
name|this
operator|.
name|fcrLegacyFreeChunkList
operator|=
name|fcrLegacyFreeChunkList
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getFcrLegacyTransactionLog
parameter_list|()
block|{
return|return
name|fcrLegacyTransactionLog
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFcrLegacyTransactionLog
parameter_list|(
name|FileChunkReference
name|fcrLegacyTransactionLog
parameter_list|)
block|{
name|this
operator|.
name|fcrLegacyTransactionLog
operator|=
name|fcrLegacyTransactionLog
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getcTransactionsInLog
parameter_list|()
block|{
return|return
name|cTransactionsInLog
return|;
block|}
specifier|public
name|OneNoteHeader
name|setcTransactionsInLog
parameter_list|(
name|long
name|cTransactionsInLog
parameter_list|)
block|{
name|this
operator|.
name|cTransactionsInLog
operator|=
name|cTransactionsInLog
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getCbLegacyExpectedFileLength
parameter_list|()
block|{
return|return
name|cbLegacyExpectedFileLength
return|;
block|}
specifier|public
name|OneNoteHeader
name|setCbLegacyExpectedFileLength
parameter_list|(
name|long
name|cbLegacyExpectedFileLength
parameter_list|)
block|{
name|this
operator|.
name|cbLegacyExpectedFileLength
operator|=
name|cbLegacyExpectedFileLength
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getRgbPlaceholder
parameter_list|()
block|{
return|return
name|rgbPlaceholder
return|;
block|}
specifier|public
name|OneNoteHeader
name|setRgbPlaceholder
parameter_list|(
name|long
name|rgbPlaceholder
parameter_list|)
block|{
name|this
operator|.
name|rgbPlaceholder
operator|=
name|rgbPlaceholder
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getFcrLegacyFileNodeListRoot
parameter_list|()
block|{
return|return
name|fcrLegacyFileNodeListRoot
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFcrLegacyFileNodeListRoot
parameter_list|(
name|FileChunkReference
name|fcrLegacyFileNodeListRoot
parameter_list|)
block|{
name|this
operator|.
name|fcrLegacyFileNodeListRoot
operator|=
name|fcrLegacyFileNodeListRoot
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getCbLegacyFreeSpaceInFreeChunkList
parameter_list|()
block|{
return|return
name|cbLegacyFreeSpaceInFreeChunkList
return|;
block|}
specifier|public
name|OneNoteHeader
name|setCbLegacyFreeSpaceInFreeChunkList
parameter_list|(
name|long
name|cbLegacyFreeSpaceInFreeChunkList
parameter_list|)
block|{
name|this
operator|.
name|cbLegacyFreeSpaceInFreeChunkList
operator|=
name|cbLegacyFreeSpaceInFreeChunkList
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getIgnoredZeroA
parameter_list|()
block|{
return|return
name|ignoredZeroA
return|;
block|}
specifier|public
name|OneNoteHeader
name|setIgnoredZeroA
parameter_list|(
name|long
name|ignoredZeroA
parameter_list|)
block|{
name|this
operator|.
name|ignoredZeroA
operator|=
name|ignoredZeroA
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getIgnoredZeroB
parameter_list|()
block|{
return|return
name|ignoredZeroB
return|;
block|}
specifier|public
name|OneNoteHeader
name|setIgnoredZeroB
parameter_list|(
name|long
name|ignoredZeroB
parameter_list|)
block|{
name|this
operator|.
name|ignoredZeroB
operator|=
name|ignoredZeroB
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getIgnoredZeroC
parameter_list|()
block|{
return|return
name|ignoredZeroC
return|;
block|}
specifier|public
name|OneNoteHeader
name|setIgnoredZeroC
parameter_list|(
name|long
name|ignoredZeroC
parameter_list|)
block|{
name|this
operator|.
name|ignoredZeroC
operator|=
name|ignoredZeroC
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getIgnoredZeroD
parameter_list|()
block|{
return|return
name|ignoredZeroD
return|;
block|}
specifier|public
name|OneNoteHeader
name|setIgnoredZeroD
parameter_list|(
name|long
name|ignoredZeroD
parameter_list|)
block|{
name|this
operator|.
name|ignoredZeroD
operator|=
name|ignoredZeroD
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|GUID
name|getGuidAncestor
parameter_list|()
block|{
return|return
name|guidAncestor
return|;
block|}
specifier|public
name|OneNoteHeader
name|setGuidAncestor
parameter_list|(
name|GUID
name|guidAncestor
parameter_list|)
block|{
name|this
operator|.
name|guidAncestor
operator|=
name|guidAncestor
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getCrcName
parameter_list|()
block|{
return|return
name|crcName
return|;
block|}
specifier|public
name|OneNoteHeader
name|setCrcName
parameter_list|(
name|long
name|crcName
parameter_list|)
block|{
name|this
operator|.
name|crcName
operator|=
name|crcName
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getFcrHashedChunkList
parameter_list|()
block|{
return|return
name|fcrHashedChunkList
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFcrHashedChunkList
parameter_list|(
name|FileChunkReference
name|fcrHashedChunkList
parameter_list|)
block|{
name|this
operator|.
name|fcrHashedChunkList
operator|=
name|fcrHashedChunkList
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getFcrTransactionLog
parameter_list|()
block|{
return|return
name|fcrTransactionLog
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFcrTransactionLog
parameter_list|(
name|FileChunkReference
name|fcrTransactionLog
parameter_list|)
block|{
name|this
operator|.
name|fcrTransactionLog
operator|=
name|fcrTransactionLog
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getFcrFileNodeListRoot
parameter_list|()
block|{
return|return
name|fcrFileNodeListRoot
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFcrFileNodeListRoot
parameter_list|(
name|FileChunkReference
name|fcrFileNodeListRoot
parameter_list|)
block|{
name|this
operator|.
name|fcrFileNodeListRoot
operator|=
name|fcrFileNodeListRoot
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getFcrFreeChunkList
parameter_list|()
block|{
return|return
name|fcrFreeChunkList
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFcrFreeChunkList
parameter_list|(
name|FileChunkReference
name|fcrFreeChunkList
parameter_list|)
block|{
name|this
operator|.
name|fcrFreeChunkList
operator|=
name|fcrFreeChunkList
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getCbExpectedFileLength
parameter_list|()
block|{
return|return
name|cbExpectedFileLength
return|;
block|}
specifier|public
name|OneNoteHeader
name|setCbExpectedFileLength
parameter_list|(
name|long
name|cbExpectedFileLength
parameter_list|)
block|{
name|this
operator|.
name|cbExpectedFileLength
operator|=
name|cbExpectedFileLength
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getCbFreeSpaceInFreeChunkList
parameter_list|()
block|{
return|return
name|cbFreeSpaceInFreeChunkList
return|;
block|}
specifier|public
name|OneNoteHeader
name|setCbFreeSpaceInFreeChunkList
parameter_list|(
name|long
name|cbFreeSpaceInFreeChunkList
parameter_list|)
block|{
name|this
operator|.
name|cbFreeSpaceInFreeChunkList
operator|=
name|cbFreeSpaceInFreeChunkList
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|GUID
name|getGuidFileVersion
parameter_list|()
block|{
return|return
name|guidFileVersion
return|;
block|}
specifier|public
name|OneNoteHeader
name|setGuidFileVersion
parameter_list|(
name|GUID
name|guidFileVersion
parameter_list|)
block|{
name|this
operator|.
name|guidFileVersion
operator|=
name|guidFileVersion
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getnFileVersionGeneration
parameter_list|()
block|{
return|return
name|nFileVersionGeneration
return|;
block|}
specifier|public
name|OneNoteHeader
name|setnFileVersionGeneration
parameter_list|(
name|long
name|nFileVersionGeneration
parameter_list|)
block|{
name|this
operator|.
name|nFileVersionGeneration
operator|=
name|nFileVersionGeneration
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|GUID
name|getGuidDenyReadFileVersion
parameter_list|()
block|{
return|return
name|guidDenyReadFileVersion
return|;
block|}
specifier|public
name|OneNoteHeader
name|setGuidDenyReadFileVersion
parameter_list|(
name|GUID
name|guidDenyReadFileVersion
parameter_list|)
block|{
name|this
operator|.
name|guidDenyReadFileVersion
operator|=
name|guidDenyReadFileVersion
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getGrfDebugLogFlags
parameter_list|()
block|{
return|return
name|grfDebugLogFlags
return|;
block|}
specifier|public
name|OneNoteHeader
name|setGrfDebugLogFlags
parameter_list|(
name|long
name|grfDebugLogFlags
parameter_list|)
block|{
name|this
operator|.
name|grfDebugLogFlags
operator|=
name|grfDebugLogFlags
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getFcrDebugLogA
parameter_list|()
block|{
return|return
name|fcrDebugLogA
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFcrDebugLogA
parameter_list|(
name|FileChunkReference
name|fcrDebugLogA
parameter_list|)
block|{
name|this
operator|.
name|fcrDebugLogA
operator|=
name|fcrDebugLogA
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|FileChunkReference
name|getFcrDebugLogB
parameter_list|()
block|{
return|return
name|fcrDebugLogB
return|;
block|}
specifier|public
name|OneNoteHeader
name|setFcrDebugLogB
parameter_list|(
name|FileChunkReference
name|fcrDebugLogB
parameter_list|)
block|{
name|this
operator|.
name|fcrDebugLogB
operator|=
name|fcrDebugLogB
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getBuildNumberCreated
parameter_list|()
block|{
return|return
name|buildNumberCreated
return|;
block|}
specifier|public
name|OneNoteHeader
name|setBuildNumberCreated
parameter_list|(
name|long
name|buildNumberCreated
parameter_list|)
block|{
name|this
operator|.
name|buildNumberCreated
operator|=
name|buildNumberCreated
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getBuildNumberLastWroteToFile
parameter_list|()
block|{
return|return
name|buildNumberLastWroteToFile
return|;
block|}
specifier|public
name|OneNoteHeader
name|setBuildNumberLastWroteToFile
parameter_list|(
name|long
name|buildNumberLastWroteToFile
parameter_list|)
block|{
name|this
operator|.
name|buildNumberLastWroteToFile
operator|=
name|buildNumberLastWroteToFile
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getBuildNumberOldestWritten
parameter_list|()
block|{
return|return
name|buildNumberOldestWritten
return|;
block|}
specifier|public
name|OneNoteHeader
name|setBuildNumberOldestWritten
parameter_list|(
name|long
name|buildNumberOldestWritten
parameter_list|)
block|{
name|this
operator|.
name|buildNumberOldestWritten
operator|=
name|buildNumberOldestWritten
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|long
name|getBuildNumberNewestWritten
parameter_list|()
block|{
return|return
name|buildNumberNewestWritten
return|;
block|}
specifier|public
name|OneNoteHeader
name|setBuildNumberNewestWritten
parameter_list|(
name|long
name|buildNumberNewestWritten
parameter_list|)
block|{
name|this
operator|.
name|buildNumberNewestWritten
operator|=
name|buildNumberNewestWritten
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

