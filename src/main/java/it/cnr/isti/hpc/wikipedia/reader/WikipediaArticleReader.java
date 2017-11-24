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

package it.cnr.isti.hpc.wikipedia.reader;

import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;

import info.bliki.wiki.dump.WikiXMLParser;
import it.cnr.isti.hpc.io.IOUtils;
import it.cnr.isti.hpc.wikipedia.article.Article;
import it.cnr.isti.hpc.wikipedia.article.Article.Type;
import it.cnr.isti.hpc.wikipedia.parser.ArticleParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

import it.cnr.isti.hpc.wikipedia.spark.StreamWikiXmlParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import info.bliki.wiki.namespaces.INamespace.NamespaceCode;

/**
 * A reader that converts a Wikipedia dump in its json dump. The json dump will
 * contain all the article in the XML dump, one article per line. Each line will
 * be compose by the json serialization of the object Article.
 * 
 * @see Article
 * 
 * @author Diego Ceccarelli, diego.ceccarelli@isti.cnr.it created on 18/nov/2011
 */
public class WikipediaArticleReader {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory
			.getLogger(WikipediaArticleReader.class);

	private WikiXMLParser wxp;
	private BufferedWriter out;
	private String json;

	private ArticleParser parser;
	// private JsonRecordParser<Article> encoder;

	private boolean toJsonFile = true;


	/**
	 * Generates a converter from the xml to json dump.
	 * 
	 * @param inputFile
	 *            - the xml file (compressed)
	 * @param outputFile
	 *            - the json output file, containing one article per line (if
	 *            the filename ends with <tt>.gz </tt> the output will be
	 *            compressed).
	 * 
	 * @param lang
	 *            - the language of the dump
	 * 
	 * 
	 */
	public WikipediaArticleReader(String inputFile, String outputFile,
			String lang) {
		this(new File(inputFile), new File(outputFile), lang);
	}

	/**
	 * Generates a converter from the xml to json dump.
	 * 
	 * @param inputFile
	 *            - the xml file (compressed)
	 * @param outputFile
	 *            - the json output file, containing one article per line (if
	 *            the filename ends with <tt>.gz </tt> the output will be
	 *            compressed).
	 * 
	 * @param lang
	 *            - the language of the dump
	 * 
	 * 
	 */
	public WikipediaArticleReader(File inputFile, File outputFile, String lang) {
		toJsonFile = true;
		JsonConverter handler = new JsonConverter();
		// encoder = new JsonRecordParser<Article>(Article.class);
		parser = new ArticleParser(lang);
		try {
			wxp = new WikiXMLParser(new File(inputFile.getAbsolutePath()), handler);
		} catch (Exception e) {
			logger.error("creating the parser {}", e.toString());
			System.exit(-1);
		}

		out = IOUtils.getPlainOrCompressedUTF8Writer(outputFile
				.getAbsolutePath());

	}

	public WikipediaArticleReader(String XMLInput, String lang) {
		toJsonFile = false;
		JsonConverter handler = new JsonConverter();

		parser = new ArticleParser(lang);
		try {
			wxp = new StreamWikiXmlParser(XMLInput, handler);
		} catch (Exception e) {
			logger.error("creating the parser {}", e.toString());
			System.exit(-1);
		}

	}

	/**
	 * Starts the parsing
	 */
	public void start() throws IOException, SAXException {

		wxp.parse();
		if (toJsonFile)
			out.close();
	}

	private class JsonConverter implements IArticleFilter {
		public void process(WikiArticle page, Siteinfo si) {
			String title = page.getTitle();
			String id = page.getId();
			String namespace = page.getNamespace();
			Integer integerNamespace = page.getIntegerNamespace();
			String timestamp = page.getTimeStamp();

			Type type = Type.UNKNOWN;
			if (page.isCategory())
				type = Type.CATEGORY;
			if (page.isTemplate()) {
				type = Type.TEMPLATE;
				return;
			}

			if(integerNamespace.equals(NamespaceCode.HELP_NAMESPACE_KEY.code)){
				type = Type.HELP;
				return;
			}
			if(integerNamespace.equals(NamespaceCode.HELP_TALK_NAMESPACE_KEY.code)){
				type = Type.HELPTALK;
				return;
			}
			if(integerNamespace.equals(NamespaceCode.USER_NAMESPACE_KEY.code)){
				type = Type.USER;
				return;
			}
			if(integerNamespace.equals(NamespaceCode.USER_TALK_NAMESPACE_KEY.code)){
				type = Type.USERTALK;
				return;
			}

			if (page.isProject()) {
				type = Type.PROJECT;
				return;
			}
			if (page.isFile()) {
				type = Type.FILE;
				return;
			}
			if (page.isMain())
				type = Type.ARTICLE;

			Article article = new Article();
			article.setTitle(title);
			article.setWikiId(Integer.parseInt(id));
			article.setNamespace(namespace);
			article.setIntegerNamespace(integerNamespace);
			article.setTimestamp(timestamp);
			article.setType(type);
			parser.parse(article, page.getText());

			try {
				if(toJsonFile){
					out.write(article.toJson());
					out.write("\n");
				}else{
					setJson(article.toJson());
				}
			} catch (IOException e) {
				logger.error("writing the output file {}", e.toString());
				System.exit(-1);
			}

			return;
		}
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
}
