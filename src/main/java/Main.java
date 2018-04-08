public class Main {
    public static void main(String args[]) throws Exception {
        Extractor extractor = new Extractor("/Users/yixiangding/Sites/csci572_solr/FOX_News/UrlToHtml_foxnews.csv");
        extractor.extractAll("/Users/yixiangding/Sites/csci572_solr/FOX_News/HTML_files");
    }
}
