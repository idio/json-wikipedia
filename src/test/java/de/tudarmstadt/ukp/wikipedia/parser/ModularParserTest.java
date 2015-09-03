package de.tudarmstadt.ukp.wikipedia.parser;

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.ModularParser;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;


public class ModularParserTest {
	@Test
	public  void testExtractNETopic(){
		Pair<String, String> noNE = ModularParser.extractNETopic("::::::::SansNE");
		assert(noNE.getFirst()==null);
		assert(noNE.getSecond().equals("SansNE"));

		Pair<String, String> noNE1 = ModularParser.extractNETopic("SansNE");
		assert(noNE1.getFirst()==null);
		assert(noNE1.getSecond().equals("SansNE"));

		Pair<String, String> withNE = ModularParser.extractNETopic("fr:SansNE");
		assert(withNE.getFirst().equals("fr"));
		assert(withNE.getSecond().equals("SansNE"));

		Pair<String, String> withNE1 = ModularParser.extractNETopic(":fr:SansNE");
		assert(withNE1.getFirst().equals("fr"));
		assert(withNE1.getSecond().equals("SansNE"));

		Pair<String, String> withNE2 = ModularParser.extractNETopic(":::fr:SansNE");
		assert(withNE2.getFirst().equals("fr"));
		assert(withNE2.getSecond().equals("SansNE"));

		Pair<String, String> withNE3 = ModularParser.extractNETopic(":::fr:h20:japanese");
		assert(withNE3.getFirst().equals("fr"));
		assert(withNE3.getSecond().equals("h20:japanese"));

		Pair<String, String> withNE4 = ModularParser.extractNETopic("h20:japanese");
		assert(withNE4.getFirst().equals("h20"));
		assert(withNE4.getSecond().equals("japanese"));
	}

	@Test
	public  void testGetLinkNameSpace(){

		Pair<String, String> noNE = ModularParser.getLinkNameSpace("::::::::SansNE");
		assert(noNE.getFirst()==null);
		assert(noNE.getSecond().equals("SansNE"));

		Pair<String, String> noNE1 = ModularParser.getLinkNameSpace("SansNE");
		assert(noNE1.getFirst()==null);
		assert(noNE1.getSecond().equals("SansNE"));

		Pair<String, String> withNE = ModularParser.getLinkNameSpace("fr:SansNE");
		assert(withNE.getFirst().equals("fr"));
		assert(withNE.getSecond().equals("fr:SansNE"));

		Pair<String, String> withNE1 = ModularParser.getLinkNameSpace(":fr:SansNE");
		assert(withNE1.getFirst().equals("fr"));
		assert(withNE1.getSecond().equals("fr:SansNE"));

		Pair<String, String> withNE2 = ModularParser.getLinkNameSpace(":::fr:SansNE");
		assert(withNE2.getFirst().equals("fr"));
		assert(withNE2.getSecond().equals("fr:SansNE"));

		Pair<String, String> withNE3 = ModularParser.getLinkNameSpace(":::fr:h20:japanese");
		assert(withNE3.getFirst().equals("fr"));
		assert(withNE3.getSecond().equals("fr:h20:japanese"));

		Pair<String, String> withNE4 = ModularParser.getLinkNameSpace("h20:japanese");
		assert(withNE4.getFirst()==null);
		assert(withNE4.getSecond().equals("h20:japanese"));


		Pair<String, String> otherNE = ModularParser.getLinkNameSpace("Cite:japanese");
		assert(otherNE.getFirst().equals("cite"));
		assert(otherNE.getSecond().equals("Cite:japanese"));
	}
}
