package ningyuan.pan.util.tests;

import static org.junit.Assert.*;

import ningyuan.pan.util.AcceptHeaderParser;
import ningyuan.pan.util.RDFSyntaxHeaderRegister;

import org.junit.Test;

public class AcceptHeaderParserTest {
	
	RDFSyntaxHeaderRegister register = new RDFSyntaxHeaderRegister();
	AcceptHeaderParser parser = new AcceptHeaderParser(register);
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructor(){
		AcceptHeaderParser par = new AcceptHeaderParser(null);
	}
	
	@Test
	public void testParse0() {
		register.clear();
		parser.parse("text/x-audiosoft-intra, application/x-mplayer2; q=0.9, image/vnd.dwg; q=0.8");	
		assertNull("Preferred RDF syntax is not null when no accepted RDF syntax avialable.", register.getPreferredRDFSyntax()); 
	}
	
	@Test
	public void testParse1() {
		register.clear();
		parser.parse("application/*");	
		assertEquals("Preferred RDF syntax for application/* is not "+RDFSyntaxHeaderRegister.DEFAULT_SYNTAX, register.getPreferredRDFSyntax(), RDFSyntaxHeaderRegister.DEFAULT_SYNTAX); 
	}
	
	@Test
	public void testParse2(){
		register.clear();
		parser.parse("text/*");	
		assertEquals("Preferred RDF syntax for text/* is not text/x-nquads", register.getPreferredRDFSyntax(), "text/x-nquads"); 
	}
	
	@Test
	public void testParse3(){
		register.clear();
		parser.parse("text/plain");	
		assertEquals("Preferred RDF syntax for text/plain is not "+RDFSyntaxHeaderRegister.PLAIN, register.getPreferredRDFSyntax(), RDFSyntaxHeaderRegister.PLAIN); 
	}
	
	@Test
	public void testParse4(){
		register.clear();
		parser.parse("*/*");	
		assertEquals("Preferred RDF syntax for */* is not "+RDFSyntaxHeaderRegister.PLAIN, register.getPreferredRDFSyntax(), RDFSyntaxHeaderRegister.PLAIN); 
	}
	
	@Test
	public void testParse5(){
		register.clear();
		parser.parse("application/x-trig");	
		assertEquals("Preferred RDF syntax for application/x-trig is not application/x-trig", register.getPreferredRDFSyntax(), "application/x-trig"); 
	}
	
	@Test
	public void testParse6(){
		register.clear();
		parser.parse("application/x-turtle");	
		assertEquals("Preferred RDF syntax for application/x-turtle is not application/x-turtle", register.getPreferredRDFSyntax(), "application/x-turtle"); 
	}
	
	@Test
	public void testParse7(){
		register.clear();
		parser.parse("text/x-nquads");	
		assertEquals("Preferred RDF syntax for text/x-nquads is not text/x-nquads", register.getPreferredRDFSyntax(), "text/x-nquads"); 
	}
	
	@Test
	public void testParse8(){
		register.clear();
		parser.parse("text/plain;q=0.2, image/gif, application/x-gss, application/x-turtle;q=0.3, */*;q=0.4");	
		assertEquals("Preferred RDF syntax for text/plain;q=0.2, image/gif, application/x-gss, application/x-turtle;q=0.3, */*;q=0.4 is not "+RDFSyntaxHeaderRegister.PLAIN, register.getPreferredRDFSyntax(), RDFSyntaxHeaderRegister.PLAIN); 
	}
}
