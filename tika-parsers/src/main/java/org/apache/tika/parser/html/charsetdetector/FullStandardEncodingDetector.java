begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
operator|.
name|charsetdetector
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
name|detect
operator|.
name|CompositeEncodingDetector
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_comment
comment|/**  * A composite encoding detector chaining a {@link StandardHtmlEncodingDetector}  * (that may return null) and a {@link StandardIcu4JEncodingDetector} (that always returns a value)  * This full detector thus always returns an encoding, and still works very well with data coming  * from the web.  */
end_comment

begin_class
specifier|public
class|class
name|FullStandardEncodingDetector
extends|extends
name|CompositeEncodingDetector
block|{
specifier|public
name|FullStandardEncodingDetector
parameter_list|()
block|{
name|super
argument_list|(
name|asList
argument_list|(
operator|new
name|StandardHtmlEncodingDetector
argument_list|()
argument_list|,
name|StandardIcu4JEncodingDetector
operator|.
name|STANDARD_ICU4J_ENCODING_DETECTOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

