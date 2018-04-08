public class Main {
    public static void main(String args[]) throws Exception {
        Extractor extractor = new Extractor("/Users/yixiangding/Sites/csci572_solr/FOX_News/UrlToHtml_foxnews.csv");
//        String testFilePath = "/Users/yixiangding/Sites/csci572_solr/FOX_News/HTML_files/8535257914633188315337ec136e315f.html";
//        String baseUri = "https://www.foxnews.com/";
//        extractor.extract(testFilePath, baseUri);
        extractor.extractAll("/Users/yixiangding/Sites/csci572_solr/FOX_News/HTML_files");
    }
}
