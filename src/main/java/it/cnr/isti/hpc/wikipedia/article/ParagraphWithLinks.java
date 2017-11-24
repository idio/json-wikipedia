package it.cnr.isti.hpc.wikipedia.article;

import java.util.List;

/**
 * Created by David Przybilla
 */
public class ParagraphWithLinks {

    private String paragraph;
    private List<Link> links;
    private List<Ref> refs;

    public ParagraphWithLinks(String paragraph, List<Link> links, List<Ref> refs){
        this.paragraph = paragraph;
        this.links = links;
        this.refs = refs;
    }

    public String getParagraph(){
        return paragraph;
    }

    public List<Link> getLinks(){
        return links;
    }

    public List<Ref> getRefs(){
        return refs;
    }
}
