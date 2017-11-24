/**
 *  Copyright 2011 Diego Ceccarelli
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package it.cnr.isti.hpc.wikipedia.article.es;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.matchers.JUnitMatchers.hasItems;

import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.*;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * ArticleTest.java
 *
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 19/nov/2011
 */
public class SpanishArticleTest extends ArticleTest {
	private static Article a = new Article();
	private static ArticleParser articleParser = new ArticleParser(Language.ES);


	@BeforeClass
	public static void loadArticle() throws IOException {
		String text = readFileAsString("/es/xml-dump/article.txt");
		articleParser.parse(a, text);
	}

	@Test
	public void sections() throws IOException {
		assertTrue(a.getSections().contains("Participación política y polémicas"));
		assertTrue(a.getSections().contains("Muerte"));
		assertTrue(a.getSections().contains("Filmografía"));
	}

	@Test
	public void categories() throws IOException {
		List<String> categories = new ArrayList<String>();
		for(Link l: a.getCategories())
			categories.add(l.getAnchor());

		assertThat(categories, hasItems("Categoría:Humoristas de México", "Categoría:Actores de la Ciudad de México", "Categoría:Actores de cine de México"));
	}

	@Test
	public void links() throws IOException {
		List<String> anchors = new ArrayList<String>();
		List<String> uris = new ArrayList<String>();

		for (ParagraphWithLinks p : a.getParagraphsWithLinks()) {
			for(Link l:p.getLinks()){
				anchors.add(l.getAnchor());
			}
		}

		for (ParagraphWithLinks p : a.getParagraphsWithLinks()) {
			for(Link l:p.getLinks()){
				uris.add(l.getId());
			}
		}

		assertThat(anchors, hasItems("Viruta", "radio", "Roberto Gómez Fernández", "el Chavo del Ocho"));
        assertThat(uris, hasItems("Telesistema_Mexicano"));

		// language link
		assertFalse(uris.contains(":fr:french_topic"));
		assertFalse(uris.contains(":french_topic"));
		assertFalse(uris.contains("french_topic"));

		assertFalse(anchors.contains(":french topic"));

	}



	@Test
	public void testAnnotationsWithOtherNamespaces() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/es/xml-dump/article_with_weird_annotations");
		articleParser.parse(a, mediawiki);

		Pair<List<String>, List<String>> anchorsAndUris = getAnchorsAndUris(a);
		List<String> anchors = anchorsAndUris.getFirst();
		List<String> uris = anchorsAndUris.getSecond();

		assertThat(uris, hasItems( "Star_Wars", "Skywalker", "superficial:forma", "representaciones_de_álgebras_de_Clifford"));
		assertThat(anchors, hasItems("Star Wars", "Skywalker", "A", "representaciones de álgebras de Clifford"));
		assert(uris.size()==4);

		assertFalse(anchors.contains("Shadow Man"));
		assertFalse(anchors.contains(":User:BadUser"));
		assertFalse(anchors.contains("AN"));
		assertFalse(anchors.contains("la wiki inglesa"));
		assertFalse(anchors.contains("mensajes"));
		assertFalse(anchors.contains("Oscar"));
		assertFalse(anchors.contains("discusión"));
		assertFalse(anchors.contains("Shōko Nakagawa"));


		assertFalse(uris.contains("Usuario:AstroNomo"));
		assertFalse(uris.contains("Usuario:AstroNomo"));
		assertFalse(uris.contains("en:Shadow Man (2006 film)"));
		assertFalse(uris.contains(":en:Shadow Man (2006 film)"));
		assertFalse(uris.contains("Shadow Man (2006 film)"));
		assertFalse(uris.contains("AstroNomo"));
		assertFalse(uris.contains("Usuario Discusión:AngelRiesgo"));
		assertFalse(uris.contains("AngelRiesgo"));
		assertFalse(uris.contains(":AngelRiesgo"));
		assertFalse(uris.contains(":Volcom"));
		assertFalse(uris.contains("Volcom"));
		assertFalse(uris.contains("Discusión:Volcom"));
		assertFalse(uris.contains("Imagen:Shoko Nakagawa.jpg"));
		assertFalse(uris.contains("Archivo:Shoko Nakagawa.jpg"));
		assertFalse(uris.contains("Shoko Nakagawa.jpg"));

		testAnchorsInText(a);
	}

	@Test
	public void testAnnotationsInLists() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/es/xml-dump/Shoko_Nakagawa");
		articleParser.parse(a, mediawiki);

		Pair<List<String>, List<String>> anchorsAndUris = getAnchorsAndUris(a);
		List<String> anchors = anchorsAndUris.getFirst();
		List<String> uris = anchorsAndUris.getSecond();

		assertThat(uris, hasItems("Enredados", "Majokko_Shimai_no_Yoyo_to_Nene"));
		assertThat(anchors, hasItems("Enredados", "Nuigulumar Z", "Sailor Moon Crystal"));
		testAnchorsInText(a);
	}


	@Test
	public void testRedirect() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/es/xml-dump/redirect");
		articleParser.parse(a, mediawiki);
		assert(a.isRedirect());
		assertEquals(a.getRedirect(), "Arte_marcial");
	}


}
