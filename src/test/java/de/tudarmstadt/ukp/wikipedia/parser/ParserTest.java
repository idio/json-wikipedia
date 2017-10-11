package de.tudarmstadt.ukp.wikipedia.parser;

import de.tudarmstadt.ukp.wikipedia.api.WikiConstants;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by dav009 on 23/06/2015.
 */
public class ParserTest {

    @Test
    public void testGalleryLinks(){
        String title = "Wikipedia API";

        String LF = "\n";
        String text = "{{Redirect2|Anarchist|Anarchists|the fictional character|Anarchist (comics)|other uses|Anarchists " +
				"(disambiguation)}} {{pp-move-indef}} {{Use British English|date=January 2014}} {{Anarchism sidebar}} {{Basic "  +
				"forms of government}} '''Anarchism''' is a [[political philosophy]] that advocates [[self-governance|self-governed]] " +
				"societies based on voluntary institutions. These are often described as [[stateless society|stateless societies]],<ref>" +
				"ANARCHISM, a social philosophy that rejects authoritarian government and maintains that voluntary institutions are best" +
				"suited to express man's natural social tendencies.\" George Woodcock. \"Anarchism\" at The Encyclopedia of Philosophy</ref><ref>\"In " +
				"a society developed [[Wikipedia:::::en:Some Anchor/Wikipedia_links#Categories|Categories]] on these lines, the voluntary associations which already now begin to cover all the fields of human activity would" +
				" take a still greater extension so as to substitute themselves for the state in all its functions.\" [http://www.theanarchistlibrary.org/HTML/" +
				"// Petr_Kropotkin___Anarchism__from_the_Encyclopaedia_Britannica.html Peter Kropotkin. \"Anarchism\" from the Encyclopædia Britannica]</ref><ref>\"Anarchism.\""+
				"The Shorter Routledge Encyclopedia of Philosophy. 2005. p. 14 \"Anarchism is the view that a society without the state, or government, is both possible and ";

        MediaWikiParserFactory pf = new MediaWikiParserFactory(WikiConstants.Language.english);
        MediaWikiParser parser = pf.createParser();

        ParsedPage pp = parser.parse(text);

        for (Section s : pp.getSections()){
            for (Link link : s.getLinks()) {
                assert(!link.getTarget().equals(""));
            }
        }


    }

    public boolean findLinkInList(Link l, Collection<Link> list){
         for(Link currentLink:list) {
             if (l.equals(currentLink)) return true;
         }
        return false;
    }


//    @Test
//    public void testExtractingLinksFromGallery(){
//
//        String text = "==History==\n" +
//                "<gallery>\n" +
//                "Image:Buckingham Branch Railroad GP16 rebuild.JPG|BB 2, a [[GP16]], getting a new coat of paint at [[Dillwyn, Virginia]].\n" +
//                "Image:BBRR4 Dillwyn WJGrimes.JPG|BBRR 4, an [[RS-4-TC]], with a new coat of paint at [[Dillwyn, Virginia]].\n" +
//                "Image:BB7 Louisa WJGrimes.JPG|BB 7, a [[GP40]], heading east at [[Louisa, Virginia]].\n" +
//                "Image:BBRR8 Doswell WJGrimes.JPG|BB 8, a GP16 in GRIV paint at [[Doswell, Virginia]].\n" +
//                "Image:Bb 13 augusta co-op.jpg|BB 13 switches Augusta CO-OP in [[Staunton, Virginia]].\n" +
//                "Image:Bb_7_svrr_staunton,_va_08292010.jpg|BB 7 with sisters, south on the [[Shenandoah Valley Railroad (short-line)|Shenandoah Valley Railroad]] in [[Staunton, Virginia]]. Having just made a pick up of Empty Cars to take West.\n" +
//                "Image:image_with_no_par.jpg\n" +
//                "Image:image_with_par.jpg|Something [[Spiderman]]\n" +
//                "||\n" +
//                "|\n" +
//                "</gallery>";
//
//        MediaWikiParserFactory pf = new MediaWikiParserFactory(WikiConstants.Language.english);
//        MediaWikiParser parser = pf.createParser();
//        ParsedPage pp = parser.parse(text);
//        Paragraph testPar;
//        testPar = pp.getSections().get(0).getParagraphs().get(0);
////        System.out.println(testPar.getLinks());
//
//        List<Link> links = testPar.getLinks();
//        System.out.println(links);
//		assert(this.findLinkInList(new Link(testPar, new Span(0, 0), "GP16",  Link.type.INTERNAL, new ArrayList<String>()), links));
//        assert(this.findLinkInList(new Link(testPar, new Span(45, 62), "Dillwyn,_Virginia",  Link.type.INTERNAL, new ArrayList<String>()), links));
//        assert(this.findLinkInList(new Link(testPar, new Span(312, 338), "Shenandoah_Valley_Railroad_(short-line)",  Link.type.INTERNAL, new ArrayList<String>()), links));
//        assert(this.findLinkInList(new Link(testPar, new Span(428, 437), "Spiderman",  Link.type.INTERNAL, new ArrayList<String>()), links));
//
//        for (Link l: testPar.getLinks()){
//            int start = l.getPos().getStart();
//            int end = l.getPos().getEnd();
//            if(start!=end){
//                assert(testPar.getText().substring(start, end).equals(l.getText()));
//            }
//        }
//        assert(testPar.getText().contains("a GP16, getting a new coat of paint at "));
//
//    }

	/*
	* Matches the extracted anchors and spans against the text
	* they have been extracted from.
	* */
	private void testAnchorsInText(ParsedPage page){

		for (Section s : page.getSections()){
			for (Paragraph p: s.getParagraphs()){
				for (Link link : p.getLinks()) {
					String anchorInPar = p.getText().substring(link.getPos().getStart(), link.getPos().getEnd());
					assertEquals(anchorInPar, link.getText());
				}
			}
		}
	}

	/*
	* Get a list with all the annotated URIs
	* */
	public List<String> getUrisInParagraphs(ParsedPage page){
		List<String> uris = new ArrayList<String>();
		for (Section s : page.getSections()){
			for (Paragraph p: s.getParagraphs()){
				for (Link link : p.getLinks()) {
					uris.add(link.getTarget());
				}
			}
		}
		return uris;
	}

	/*
	* Get a list with all the annotated URIs
	* */
	public Link getLink(ParsedPage page, String uri){
		for (Section s : page.getSections()){
			for (Paragraph p: s.getParagraphs()){
				for (Link link : p.getLinks()) {
					if (uri.equals(link.getTarget()))
						return link;
				}
			}
		}
		return null;
	}

    @Test
    public void testExtractingLinksWithColons(){

		// Regular paragraphs
        String text ="'''Hayami''' Rena Hayami, [[Image:a.jgp]] images and file namespaces [[File:a.jpg]] " +
				"''[[Category: Evolution]]'' [[Category:Jackson musical family]].\n \n Kohinata Hayami, " +
				"[[H2O: Footprints in the Sand]] character. [[Cite:AAA]] [[Noriko Hayami]] (born 1959), Japanese actress.";
        MediaWikiParserFactory pf = new MediaWikiParserFactory(WikiConstants.Language.english);
        MediaWikiParser parser = pf.createParser();

        ParsedPage pp = parser.parse(text);

        List<String> uris = getUrisInParagraphs(pp);
        assertThat(uris, hasItems("H2O:_Footprints_in_the_Sand", "Noriko_Hayami"));

        // Making sure Links with ":" are considered Internals
        Link h2OAnnotation = getLink(pp, "H2O:_Footprints_in_the_Sand");
        assertEquals(h2OAnnotation.getType(), Link.type.UNKNOWN);
        assertEquals(getLink(pp, "Cite:AAA").getType(), Link.type.UNKNOWN);

        testAnchorsInText(pp);
    }
}
