begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|base
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
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|ParseContext
import|;
end_import

begin_comment
comment|/**  * Defines contract for configurable services  * @since Apache Tika 1.13  */
end_comment

begin_interface
specifier|public
interface|interface
name|Configurable
block|{
comment|/**      * Confure an instance with Tika Context      * @param context configuration instance in the form of context      * @throws TikaException when an instance fails to work at the given context      * @since Apache Tika 1.13      */
name|void
name|configure
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|TikaException
function_decl|;
block|}
end_interface

end_unit

