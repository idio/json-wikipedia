package it.cnr.isti.hpc.wikipedia.article;

/**
 *  This class represents inline wikipedia references. You can find the pattern to detect those inside ArticleParser.
 *
 * Created by Stathis Charitos
 */
public class Ref {

    private String text;

    private int start;
    private int end;

    public Ref(String text, int start, int end) {
        this.start = start;
        this.end = end;
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
