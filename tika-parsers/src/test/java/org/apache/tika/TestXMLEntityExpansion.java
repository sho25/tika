begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|tika
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
name|AutoDetectParser
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
name|Parser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|XMLTestBase
operator|.
name|injectXML
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|XMLTestBase
operator|.
name|parse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Tests to confirm defenses against entity expansion attacks.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"initial draft, needs more work"
argument_list|)
specifier|public
class|class
name|TestXMLEntityExpansion
block|{
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|ENTITY_EXPANSION_BOMB
init|=
operator|new
name|String
argument_list|(
literal|"<!DOCTYPE kaboom [ "
operator|+
literal|"<!ENTITY a \"1234567890\"> "
operator|+
literal|"<!ENTITY b \"&a;&a;&a;&a;&a;&a;&a;&a;&a;&a;\">"
operator|+
literal|"<!ENTITY c \"&b;&b;&b;&b;&b;&b;&b;&b;&b;&b;\"> "
operator|+
literal|"<!ENTITY d \"&c;&c;&c;&c;&c;&c;&c;&c;&c;&c;\"> "
operator|+
literal|"<!ENTITY e \"&d;&d;&d;&d;&d;&d;&d;&d;&d;&d;\"> "
operator|+
literal|"<!ENTITY f \"&e;&e;&e;&e;&e;&e;&e;&e;&e;&e;\"> "
operator|+
literal|"<!ENTITY g \"&f;&f;&f;&f;&f;&f;&f;&f;&f;&f;\"> "
operator|+
literal|"<!ENTITY h \"&g;&g;&g;&g;&g;&g;&g;&g;&g;&g;\"> "
operator|+
literal|"<!ENTITY i \"&h;&h;&h;&h;&h;&h;&h;&h;&h;&h;\"> "
operator|+
literal|"<!ENTITY j \"&i;&i;&i;&i;&i;&i;&i;&i;&i;&i;\"> "
operator|+
literal|"<!ENTITY k \"&j;&j;&j;&j;&j;&j;&j;&j;&j;&j;\"> "
operator|+
literal|"<!ENTITY l \"&k;&k;&k;&k;&k;&k;&k;&k;&k;&k;\"> "
operator|+
literal|"<!ENTITY m \"&l;&l;&l;&l;&l;&l;&l;&l;&l;&l;\"> "
operator|+
literal|"<!ENTITY n \"&m;&m;&m;&m;&m;&m;&m;&m;&m;&m;\"> "
operator|+
literal|"<!ENTITY o \"&n;&n;&n;&n;&n;&n;&n;&n;&n;&n;\"> "
operator|+
literal|"<!ENTITY p \"&o;&o;&o;&o;&o;&o;&o;&o;&o;&o;\"> "
operator|+
literal|"<!ENTITY q \"&p;&p;&p;&p;&p;&p;&p;&p;&p;&p;\"> "
operator|+
literal|"<!ENTITY r \"&q;&q;&q;&q;&q;&q;&q;&q;&q;&q;\"> "
operator|+
literal|"<!ENTITY s \"&r;&r;&r;&r;&r;&r;&r;&r;&r;&r;\"> "
operator|+
literal|"]> "
operator|+
literal|"<kaboom>&s;</kaboom>"
argument_list|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
comment|//a truly vulnerable parser, say xerces2, doesn't oom, it thrashes with gc.
comment|//Set a reasonable amount of time as the timeout
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
specifier|public
name|void
name|testInjectedXML
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?><document>blah</document>"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|byte
index|[]
name|injected
init|=
name|injectXML
argument_list|(
name|bytes
argument_list|,
name|ENTITY_EXPANSION_BOMB
argument_list|)
decl_stmt|;
name|parse
argument_list|(
literal|"injected"
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|injected
argument_list|)
argument_list|,
operator|new
name|XMLTestBase
operator|.
name|VulnerableSAXParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|20000
argument_list|)
comment|//
specifier|public
name|void
name|testProtectedXML
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?><document>blah</document>"
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|byte
index|[]
name|injected
init|=
name|injectXML
argument_list|(
name|bytes
argument_list|,
name|ENTITY_EXPANSION_BOMB
argument_list|)
decl_stmt|;
name|test
argument_list|(
literal|"injected"
argument_list|,
name|injected
argument_list|,
operator|new
name|AutoDetectParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|test
parameter_list|(
name|String
name|testFileName
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|,
name|Parser
name|parser
parameter_list|)
throws|throws
name|Exception
block|{
name|boolean
name|ex
init|=
literal|false
decl_stmt|;
try|try
block|{
name|parse
argument_list|(
name|testFileName
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXParseException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getMessage
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"entity expansions"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Should have seen 'entity expansions' in the msg"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|ex
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|==
literal|null
operator|||
name|cause
operator|.
name|getMessage
argument_list|()
operator|==
literal|null
operator|||
operator|!
name|cause
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"entity expansions"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cause should have mentioned 'entity expansions'"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|ex
operator|=
literal|true
expr_stmt|;
block|}
name|assertTrue
argument_list|(
literal|"should have had an exception"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

