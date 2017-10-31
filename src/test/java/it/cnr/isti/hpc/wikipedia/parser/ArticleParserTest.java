package it.cnr.isti.hpc.wikipedia.parser;

import it.cnr.isti.hpc.wikipedia.article.Language;
import it.cnr.isti.hpc.wikipedia.article.Ref;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArticleParserTest {
	/*
	* Basic reference extraction check.
	* */
	@Test
	public void testBasicReferences() throws IOException {
		ArticleParser parser = new ArticleParser(Language.EN);
		List<Ref> refs = parser.extractInlineReferences("Before <ref>Hello World</ref> After.");
		assertEquals(1, refs.size());
	}
	/*
	* Nested reference extraction.
	* */
	@Test
	public void testNestedReferences() throws IOException {
		ArticleParser parser = new ArticleParser(Language.EN);
		assertEquals(1, parser.extractInlineReferences("Before <ref>Hello World <ref name=&quote;nested&quote;>Hello Again World</ref> Still not out of the top reference! </ref> After.").size());
	}
	/*
	* More reference parsing edge cases.
	* */
	@Test
	public void testReferencesEdgeCases() throws IOException {
		ArticleParser parser = new ArticleParser(Language.EN);
		assertTrue(parser.extractInlineReferences(" ").isEmpty());
		assertTrue(parser.extractInlineReferences(null).isEmpty());
		assertTrue(parser.extractInlineReferences("").isEmpty());
	}
}
