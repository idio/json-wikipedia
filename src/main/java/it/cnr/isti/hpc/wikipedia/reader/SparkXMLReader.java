package it.cnr.isti.hpc.wikipedia.reader;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.avro.Schema;

import java.io.File;
import java.io.IOException;

public class SparkXMLReader {

    private String lang;
    private String xmlInput;


    public SparkXMLReader(String xmlInput, String lang){
        this.lang = lang;
        this.xmlInput = xmlInput;
    }

    public void parse(SparkSession spark) {
        try {
            Schema pageSchema = new Schema.Parser().parse(new File("./article.avsc"));
            Dataset<Row> pages = spark.read().format("xml").option("rowTag", "page")
                    .option("avroSchema", pageSchema.toString())
                    .load(this.xmlInput);
            pages.show();
        } catch (IOException io) {
          System.out.println("Blah");
        }


    }
}
