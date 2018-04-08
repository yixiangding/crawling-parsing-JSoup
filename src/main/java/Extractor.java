import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;

public class Extractor {
    public void extract(String pathToPageFile, String baseUri) throws Exception {

        File file = new File(pathToPageFile);
        Document doc = Jsoup.parse(file, "UTF-8", baseUri);
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        printf("\nMedia: %d", media.size());
        for (Element src: media) {
            printf(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
        }

        printf("\nImports: %d", imports.size());
        for (Element link: imports) {
            printf(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), link.attr("rel"));
        }

        printf("\nLinks: %d", links.size());
        for (Element link: links) {
            printf(" * %s <%s> (%s)", link.tagName(), link.attr("abs:href"), trim(link.text(), 35));
        }
    }

    private void printf(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private String trim(String str, int width) {
        if (str.length() <= width) {
            return str;
        }
        return str.substring(0, width);
    }
}
