package it.cnr.isti.hpc.wikipedia.article;

import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
/**
 * Created by dav009 on 05/01/2016.
 */
public class ArticleTest {

	@Test
	public void testExtractingLinksFromGallery(){

		String mediawiki =
				"==Gallery==\n" +
						"<gallery heights=\"150\" widths=\"150\">\n" +
						"Jordantreppe Petersburg Eremitage 02.JPG\n" +
						"The State (Jordan, or Ambassadors') Staircase.jpg\n" +
						"The State (Jordan, or Ambassadors') Staircase 2.jpg\n" +
						"Hermitage St. Petersburg Interior 20021009.jpg\n" +
						"Hermitage staicase.jpg\n" +
						"Jordantreppe Decke Petersburg Eremitage.jpg\n" +
						"RIAN archive 139402 Main Staircase at the Hermitage.jpg\n" +
						"Jordan Staircase 9.JPG\n" +
						"</gallery>\n";

		ArticleParser parser = new ArticleParser(Language.EN);
		Article a = new Article();
		parser.parse(a, mediawiki);


		assert(a.getLinks().isEmpty());

	}

	protected Pair<List<String>, List<String>> getAnchorsAndUris(Article a){

		List<String> uris = new ArrayList<String>();
		List<String> anchors = new ArrayList<String>();

		for (ParagraphWithLinks p : a.getParagraphsWithLinks()) {
			for(Link l:p.getLinks()){
				uris.add(l.getId());
				anchors.add(l.getAnchor());
			}
		}

		return new Pair<List<String>, List<String>>(anchors, uris);
	}

	/*
	* Matches the extracted anchors and spans against the text
	* they have been extracted from.
	* */
	protected void testAnchorsInText(Article article){
		for(ParagraphWithLinks p:  article.getParagraphsWithLinks()){
			for(Link link: p.getLinks()){
				String anchorInPar = p.getParagraph().substring(link.getStart(),link.getEnd() );
				assertEquals(anchorInPar, link.getAnchor());
			}
		}
	}


	protected static String readFileAsString(String filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				ArticleTest.class.getResourceAsStream(filePath), "UTF-8"));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	/*
	* Check a reference for expected values of start and end index, and expected text inside.
	* */
	public void checkRef(Ref ref, int start, int end, String text) {
		assertEquals(start, ref.getStart());
		assertEquals(end, ref.getEnd());
		assertTrue(ref.getText().contains(text));
	}

	@Test
	public void testExtractingLinksOtherLang(){

		String mediawiki = "* La Douceur de croire, pièce en trois actes, Théâtre Français, 8 July 1899\n" +
				"\n" +
				"[[:France]]In collaboration with [[:fr:André Delavigne]] " +
				"* Blakson père [[::::::Potato|Pommes]] et fils,[[fr:Something]] comédie en quatre actes, Théâtre de l'Odéon\n" +
				"* Les petites  marmites, comédie en trois actes, Théâtre du Gymnase\n" +
				"[[cite:Gundam]] * Voilà Monsieur !, comédie en un acte, Théâtre du Gymnase";

		ArticleParser parser = new ArticleParser(Language.EN);
		Article a = new Article();
		parser.parse(a, mediawiki);

		Pair<List<String>, List<String>> anchorsAndUris = getAnchorsAndUris(a);
		List<String> uris = anchorsAndUris.getSecond();

		assertFalse(uris.contains("André_Delavigne"));
		assertFalse(uris.contains("Something"));
		assertThat(uris, hasItems("France", "Potato"));
		assert(uris.size()==2);

		// Making sure Links with ":" are considered Internals
		Link andreAnnotation = getLink(a, "France");
		assertEquals(andreAnnotation.getType(), Link.type.INTERNAL);

		Link potato = getLink(a, "Potato");
		assertEquals(potato.getType(), Link.type.INTERNAL);
		assertEquals(potato.getAnchor(), "Pommes");

		testAnchorsInText(a);

	}

	/*
	* Test for inline ref parsing.
	* */
	@Test
	public void testReferences() throws IOException {
		ArticleParser parser = new ArticleParser(Language.EN);
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/misc/anarchism.xml");
		parser.parse(a, mediawiki);

		// check that the first paragraph of anarchism has 10 refs detected.
		List<Ref> refs = a.getParagraphsWithLinks().get(0).getRefs();
		assertEquals(10, refs.size());

		// Check first and last detected references inside values
		Ref firstRef = refs.get(0);
		checkRef(firstRef, 157, 435, "a social philosophy that rejects authoritarian government and maintains");

		Ref lastRef = refs.get(9);
		checkRef(lastRef, 2529, 2713, "The Concise Oxford Dictionary of Politics. Ed. Iain McLean and Alistair McMillan");

	}

	/*
	* Get a list with all the annotated URIs
	* */
	private Link getLink(Article page, String uri){
		for (Link link : page.getLinks()) {
			if (uri.equals(link.getId()))
				return link;
		}
		return null;
	}
}
