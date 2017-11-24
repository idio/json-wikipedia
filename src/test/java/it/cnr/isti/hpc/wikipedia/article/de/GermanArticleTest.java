package it.cnr.isti.hpc.wikipedia.article.de;

import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.*;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;
import org.apache.commons.math3.util.Pair;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

/**
 * Created by dav009 on 05/01/2016.
 */
public class GermanArticleTest extends ArticleTest {
	private static Article a = new Article();
	private static ArticleParser articleParser = new ArticleParser(Language.DE);

	@BeforeClass
	public static void loadArticle() throws IOException {
		String text = readFileAsString("/de/xml-dump/article.txt");
		articleParser.parse(a, text);
	}

	@Test
	public void sections() throws IOException {
		assertThat(a.getSections(), hasItems("Geographie", "Geschichte", "Wichtige Einrichtungen",
				                             "Verkehr", "Politik", "Söhne und Töchter der Stadt",
				                             "Angrenzende Städte und Gemeinden"));
	}

	@Test
	public void categories() throws IOException {
		List<String> categories = new ArrayList<String>();
		for(Link l: a.getCategories())
			categories.add(l.getAnchor());

		assertThat(categories, hasItems("Kategorie:Stadtbezirk von Tokio"));
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

		assertThat(anchors, hasItems("Tōru Hashimoto", "Momoe Yamaguchi", "Hisaoki Kamei", "Unterhaus", "Odawara-Linie", "Olympiastadion"));
		assertThat(uris, hasItems("Tōru_Hashimoto", "Momoe_Yamaguchi", "Hisaoki_Kamei", "Japanisches_Unterhaus",
				                  "Einheitliche_Regionalwahlen_in_Japan_2015", "Odawara-Linie", "Olympiastadion_Tokio"));

		assertFalse(uris.contains("ja:somethingjapanese"));
		assertFalse(uris.contains(":somethingjapanese"));
		assertFalse(uris.contains("somethingjapanese"));
		assertFalse(anchors.contains("Harajuku_Station_Tokyo.jpg"));

		assertFalse(anchors.contains("image:Aoyama_Gakuin_Majima_Memorial_Hall_02.JPG"));
		assertFalse(anchors.contains("Aoyama_Gakuin_Majima_Memorial_Hall_02.JPG"));
		assertFalse(anchors.contains("Omotesando Hills"));
		assertFalse(anchors.contains("Harajuku_Station_Tokyo.jpg"));
		assertFalse(anchors.contains("Image:Harajuku_Station_Tokyo.jpg"));

	}

	@Test
	public void testAnnotationsWithOtherNamespaces() throws IOException {
		Article a = new Article();
		String mediawiki = IOUtils.getFileAsUTF8String("./src/test/resources/de/xml-dump/article_with_weird_annotations");
		articleParser.parse(a, mediawiki);
		Pair<List<String>, List<String>> anchorsAndUris = getAnchorsAndUris(a);
		List<String> uris = anchorsAndUris.getSecond();
		assert(uris.size()==0);
	}
}
