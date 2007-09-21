begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|//Copyright (c) 2007, California Institute of Technology.
end_comment

begin_comment
comment|//ALL RIGHTS RESERVED. U.S. Government sponsorship acknowledged.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//$Id$
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
package|;
end_package

begin_comment
comment|//JDK imports
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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

begin_comment
comment|//Tika imports
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|TikaMimeKeys
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
name|utils
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|//Junit imports
end_comment

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * @author mattmann  * @version $Revision$  *   *<p>  * Test Suite for the {@link MimeTypes} repository.  *</p>.  */
end_comment

begin_class
specifier|public
class|class
name|TestMimeUtils
extends|extends
name|TestCase
implements|implements
name|TikaMimeKeys
block|{
specifier|private
specifier|static
specifier|final
name|String
name|tikaMimeFile
init|=
literal|"org/apache/tika/mime/tika-mimetypes.xml"
decl_stmt|;
specifier|private
name|Configuration
name|conf
decl_stmt|;
specifier|private
specifier|static
name|URL
name|u
decl_stmt|;
static|static
block|{
try|try
block|{
name|u
operator|=
operator|new
name|URL
argument_list|(
literal|"http://mydomain.com/x.pdf?x=y"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"/a/b/c/x.pdf"
argument_list|)
decl_stmt|;
specifier|private
name|MimeUtils
name|utils
decl_stmt|;
specifier|public
name|TestMimeUtils
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|TIKA_MIME_FILE
argument_list|,
name|tikaMimeFile
argument_list|)
expr_stmt|;
name|utils
operator|=
operator|new
name|MimeUtils
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|utils
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLoadMimeTypes
parameter_list|()
block|{
name|assertNotNull
argument_list|(
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|forName
argument_list|(
literal|"application/octet-stream"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|forName
argument_list|(
literal|"text/x-tex"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testGuessMimeTypes
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.pdf"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|u
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/pdf"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|f
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/plain"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.txt"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/html"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.htm"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"text/html"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.html"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/xhtml+xml"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.xhtml"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/xml"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.xml"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/msword"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.doc"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-powerpoint"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.ppt"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.ms-excel"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.xls"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/zip"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.zip"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/vnd.oasis.opendocument.text"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.odt"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application/octet-stream"
argument_list|,
name|utils
operator|.
name|getRepository
argument_list|()
operator|.
name|getMimeType
argument_list|(
literal|"x.xyz"
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

