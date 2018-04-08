import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Extractor {
    HashMap<String, String> fileUrlMap = new HashMap<>();
    HashMap<String, String> urlFileMap = new HashMap<>();

    private Extractor() {}

    public Extractor(String pathToCsv) {
        try {
            createMaps(pathToCsv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create both maps from CSV file within given path
     */
    private void createMaps(String pathToCsv) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(pathToCsv));
        String line = null;

        while ((line = br.readLine()) != null) {
            String[] mapping = line.split(",");
            fileUrlMap.put(mapping[0], mapping[1]);
            urlFileMap.put(mapping[1], mapping[0]);
        }

        br.close();
    }

    /**
     * Extract all HTML files in directory with mapping of base URIs
     * @param pathToDir path to directory which stores HTMLs
     * @throws Exception IOException when file not found
     */
    public void extractAll(String pathToDir) throws Exception {
        File dir = new File(pathToDir);
        File[] files = dir.listFiles();

        Set<String> edges = readEdges(files);
        writeEdgeFile(edges, pathToDir + "/../edge_file.txt");
    }

    private Set<String> readEdges(File[] files) throws Exception {
        int counter = 0;
        int totalFileNumber = files.length;
        Set<String> edges = new HashSet<>();
        for (File file : files) {
            String fileName = file.getName();
            if (!fileUrlMap.containsKey(fileName)) {
                continue;
            }
            Document doc = Jsoup.parse(file, "UTF-8", fileUrlMap.get(fileName));
            Elements links = doc.select("a[href]");

            for (Element link : links) {
                String url = link.attr("href").trim();
                if (urlFileMap.containsKey(url)) { // Skip outsider links
                    String edge = String.format("%s %s", fileName, urlFileMap.get(url));
                    edges.add(edge);
                }
            }

            System.out.println(String.format("DEBUG: Complete: %d, Total: %d", counter++, totalFileNumber));
        }
        return edges;
    }

    private void writeEdgeFile(Set<String> edges, String path) throws Exception {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        System.out.println("Writing edge list file......");
        for (String edge : edges) {
            writer.write(edge + '\n');
        }
        writer.flush();
        writer.close();
    }

    /**
     * Extract single HTML file with given baseUri
     * @param pathToPageFile path to downloaded HTML file
     * @param baseUri base uri
     * @throws Exception IOException when file not found
     */
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
