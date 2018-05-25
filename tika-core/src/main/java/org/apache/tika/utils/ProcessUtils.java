begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|utils
package|;
end_package

begin_class
specifier|public
class|class
name|ProcessUtils
block|{
comment|/**      * This should correctly put double-quotes around an argument if      * ProcessBuilder doesn't seem to work (as it doesn't      * on paths with spaces on Windows)      *      * @param arg      * @return      */
specifier|public
specifier|static
name|String
name|escapeCommandLine
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
if|if
condition|(
name|arg
operator|==
literal|null
condition|)
block|{
return|return
name|arg
return|;
block|}
comment|//need to test for " " on windows, can't just add double quotes
comment|//across platforms.
if|if
condition|(
name|arg
operator|.
name|contains
argument_list|(
literal|" "
argument_list|)
operator|&&
name|SystemUtils
operator|.
name|IS_OS_WINDOWS
condition|)
block|{
name|arg
operator|=
literal|"\""
operator|+
name|arg
operator|+
literal|"\""
expr_stmt|;
block|}
return|return
name|arg
return|;
block|}
block|}
end_class

end_unit

