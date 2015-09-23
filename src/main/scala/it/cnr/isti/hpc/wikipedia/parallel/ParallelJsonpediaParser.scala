package it.cnr.isti.hpc.wikipedia.parallel

import java.io.{File, FileWriter}
import java.util.concurrent.atomic.AtomicInteger

import it.cnr.isti.hpc.wikipedia.reader.WikipediaArticleReader
import org.apache.commons.io.FileUtils

object ParallelJsonpediaParser{

  /*
  * Given a list of files it appends the <mediawiki> tag at the beginning and </mediawiki> at the end
  * of each file.
  * Note: Each of the given files willl be overwritten.
  * @param files: List of Files
  * */
  def appendHeaderFooter(files:Seq[File]): Unit ={
    val header = "<mediawiki xmlns=\"http://www.mediawiki.org/xml/export-0.10/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/export-0.10.xsd\" version=\"0.10\" xml:lang=\"en\">"
    val footer = "</mediawiki>"
    val namespaces = "  <siteinfo>\n    <sitename>Wikipedia</sitename>\n    <dbname>enwiki</dbname>\n    <base>http://en.wikipedia.org/wiki/Main_Page</base>\n    <generator>MediaWiki 1.26wmf7</generator>\n    <case>first-letter</case>\n    <namespaces>\n      <namespace key=\"-2\" case=\"first-letter\">Media</namespace>\n      <namespace key=\"-1\" case=\"first-letter\">Special</namespace>\n      <namespace key=\"0\" case=\"first-letter\" />\n      <namespace key=\"1\" case=\"first-letter\">Talk</namespace>\n      <namespace key=\"2\" case=\"first-letter\">User</namespace>\n      <namespace key=\"3\" case=\"first-letter\">User talk</namespace>\n      <namespace key=\"4\" case=\"first-letter\">Wikipedia</namespace>\n      <namespace key=\"5\" case=\"first-letter\">Wikipedia talk</namespace>\n      <namespace key=\"6\" case=\"first-letter\">File</namespace>\n      <namespace key=\"7\" case=\"first-letter\">File talk</namespace>\n      <namespace key=\"8\" case=\"first-letter\">MediaWiki</namespace>\n      <namespace key=\"9\" case=\"first-letter\">MediaWiki talk</namespace>\n      <namespace key=\"10\" case=\"first-letter\">Template</namespace>\n      <namespace key=\"11\" case=\"first-letter\">Template talk</namespace>\n      <namespace key=\"12\" case=\"first-letter\">Help</namespace>\n      <namespace key=\"13\" case=\"first-letter\">Help talk</namespace>\n      <namespace key=\"14\" case=\"first-letter\">Category</namespace>\n      <namespace key=\"15\" case=\"first-letter\">Category talk</namespace>\n      <namespace key=\"100\" case=\"first-letter\">Portal</namespace>\n      <namespace key=\"101\" case=\"first-letter\">Portal talk</namespace>\n      <namespace key=\"108\" case=\"first-letter\">Book</namespace>\n      <namespace key=\"109\" case=\"first-letter\">Book talk</namespace>\n      <namespace key=\"118\" case=\"first-letter\">Draft</namespace>\n      <namespace key=\"119\" case=\"first-letter\">Draft talk</namespace>\n      <namespace key=\"446\" case=\"first-letter\">Education Program</namespace>\n      <namespace key=\"447\" case=\"first-letter\">Education Program talk</namespace>\n      <namespace key=\"710\" case=\"first-letter\">TimedText</namespace>\n      <namespace key=\"711\" case=\"first-letter\">TimedText talk</namespace>\n      <namespace key=\"828\" case=\"first-letter\">Module</namespace>\n      <namespace key=\"829\" case=\"first-letter\">Module talk</namespace>\n      <namespace key=\"2600\" case=\"first-letter\">Topic</namespace>\n    </namespaces>\n  </siteinfo>"

    println("Adding headers, footers to files...")
    files.par.map{ f =>
      println("processing.." + f.getName())
      val content = scala.io.Source.fromFile(f,  "utf-8").getLines().toList
      val fw = new FileWriter(f, false)
      fw.write(header +"\n")
      fw.write(namespaces+"\n")
      content.foreach(line => fw.write(line + "\n"))
      fw.write(footer)
      fw.close()
    }
  }

  /*
  * Takes a list of files containing a xml wikipedia dump
  * and in parallel calls WikipediaArticleReader
  * and generates a json wikipedia dump per input file
  *
  * @pathToFiles: Path to folder containing wikipedia xml files
  * @ouput: Path to folder where the resulting jsonWiki
  * */
  def exportToJsonpedia(pathToFiles:String, output:String, lang:String): Unit ={
    val wikiXmlFiles = new File(pathToFiles).listFiles().filter(_.getName().startsWith("part"))
    appendHeaderFooter(wikiXmlFiles)

    val total = wikiXmlFiles.length
    val done = new AtomicInteger()

    wikiXmlFiles.par.map{ file =>
      val wap: WikipediaArticleReader = new WikipediaArticleReader(file.getAbsolutePath, output + "/" + file.getName + ".json", lang)
      try {
        println("%s%% done".format(done.incrementAndGet().floatValue()/total * 100))
        wap.start
        //Deleting the input xml file to assure enough space
        FileUtils.deleteQuietly(file)
      }
      catch {
        case e: Exception => {
          println("Error parsing the mediawiki {}", e.toString)
          System.exit(-1)
        }
      }
    }
  }

}
