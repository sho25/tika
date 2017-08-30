begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|exception
package|;
end_package

begin_comment
comment|/**  * Exception thrown by the AutoDetectParser when a file contains zero-bytes.  */
end_comment

begin_class
specifier|public
class|class
name|ZeroByteFileException
extends|extends
name|TikaException
block|{
specifier|public
name|ZeroByteFileException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

