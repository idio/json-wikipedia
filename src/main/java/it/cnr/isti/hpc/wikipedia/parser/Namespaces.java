package it.cnr.isti.hpc.wikipedia.parser;

import org.apache.commons.lang3.ArrayUtils;

import java.util.HashSet;
import java.util.Set;

import java.util.Arrays;

/**
 * Created by David Przybilla on 04/09/2015.
 */
public class Namespaces {
    private static String[] namespacesEnglish = {"image", "talk", "user", "project", "file", "project_talk", "mediaWiki",
            "mediawiki_talk", "template", "template_talk", "help", "help_talk",
            "category", "category_talk", "thread", "thread_talk", "summary",
            "summary_talk", "relation", "relation_talk", "property",
            "property_talk", "type", "type_talk", "form", "form_talk", "concept",
            "concept_talk", "forum", "forum_talk", "cite", "cite_talk", "relation",
            "media", "special", "userwiki", "userwiki_talk", "user_profile",
            "user_profile_talk", "page", "page_talk", "index", "index_talk",
            "widget", "widget_talk", "jsapplet", "jsapplet_talk", "poll", "poll_talk",
            "imageannotation", "layer", "layer_talk", "quiz", "quiz_talk", "translations",
            "translations_talk", "module", "module_talk", "imageidentifers", "wikipedia",
            "meta", "additional", "portal", "project", "userbox", "userbox_talk", "interpretation",
            "interpretation_talk", "wikt", "s", "wp", "user talk", "w", "wiktionary", "file talk",
            "nds", "file", "draft", "user_talk", "m", "wikipedia talk", "wikipedia_talk", "image talk",
            "wt", "portal talk", "wikisource", "wikinews", "imdbname", "mw", "wikibooks",
            "wikiquote", "book_talk", "wikispecies", "portal_talk", "q", "c", "commons", "Nu", "x", "bugzilla", "p", "wm"
    };


    private static String[] namespaces = ArrayUtils.addAll(namespacesEnglish);

    // https://en.wikipedia.org/wiki/Help:Interlanguage_links
    // list of iso 639-1 langs
    private static String[] languages = {"aa","ab","af","ak","sq","am","ar","an","hy","as","av","ae","ay","az",
            "ba","bm","eu","be","bn","bh","bi","bo","bs","br","bg","my","ca","cs","ch","ce","zh",
            "cu","cv","kw","co","cr","cy","cs","da","de","dv","nl","dz","el","en","eo","et","eu",
            "ee","fo","fa","fj","fi","fr","fr","fy","ff","ga","de","gd","ga","gl","gv","el","gn",
            "gu","ht","ha","he","hz","hi","ho","hr","hu","hy","ig","is","io","ii","iu","ie","ia",
            "id","ik","is","it","jv","ja","kl","kn","ks","ka","kr","kk","km","ki","rw","ky","kv",
            "kg","ko","kj","ku","lo","la","lv","li","ln","lt","lb","lu","lg","mk","mh","ml","mi",
            "mr","ms","mi","mk","mg","mt","mn","mi","ms","my","na","nv","nr","nd","ng","ne","nl",
            "nn","nb","no","oc","oj","or","om","os","pa","fa","pi","pl","pt","ps","qu","rm","ro",
            "ro","rn","ru","sg","sa","si","sk","sk","sl","se","sm","sn","sd","so","st","es","sq",
            "sc","sr","ss","su","sw","sv","ty","ta","tt","te","tg","tl","th","bo","ti","to","tn",
            "ts","tk","tr","tw","ug","uk","ur","uz","ve","vi","vo","cy","wa","wo","xh","yi","yo",
            "za","zh","zu"
    };

    private static Set<String> allNamespaces = new HashSet<>(Arrays.asList(namespaces));
    private static Set<String> allLanguages = new HashSet<>(Arrays.asList(languages));


    public static boolean isLanguage(String ne){
        if (ne == null)
            return false;
        return allLanguages.contains(ne.toLowerCase());
    }

    public static boolean isNamespace(String ne){
        return isNamespace(ne, new HashSet<>());
    }

    public static boolean isNamespace(String ne, Set<String> otherNE){
        if (ne==null)
            return false;
        String neLowerCase = ne.toLowerCase();
        return allNamespaces.contains(neLowerCase) | otherNE.contains(neLowerCase) ;
    }
}
