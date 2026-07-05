import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.util.Marshalling;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class MainMusic {

    public static void main(String[] args) throws Exception {
        // ScorePartwise score = loadScore("resources/messiah-hwv-56-hallelujah-chorus-georg-friedrich-handel.mxl");
        ScorePartwise score = loadScore("resources/tallis-thomas-if-ye-love-me.mxl");
        String title = null;
        if (score.getWork() != null) {
            title = score.getWork().getWorkTitle();
        }
        if (title == null) {
            title = score.getMovementTitle();
        }
        if (title == null) {
            title = "(untitled)";
        }


        System.out.println("Loaded score: " + title);
        System.out.println("Parts: " + score.getPart().size());
    }

    /**
     * Loads a ScorePartwise from either a plain .musicxml/.xml file
     * or a compressed .mxl file, based on the file's extension.
     */
    public static ScorePartwise loadScore(String path) throws Exception {
        if (path.toLowerCase().endsWith(".mxl")) {
            return loadFromMxl(path);
        } else {
            try (FileInputStream in = new FileInputStream(path)) {
                return (ScorePartwise) Marshalling.unmarshal(in);
            }
        }
    }

    private static ScorePartwise loadFromMxl(String path) throws Exception {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(path))) {

            // Pass 1: find META-INF/container.xml and read the root file path
            String rootFilePath = null;
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("META-INF/container.xml")) {
                    rootFilePath = readRootFilePath(zis);
                    break;
                }
            }

            if (rootFilePath == null) {
                throw new RuntimeException("No META-INF/container.xml found in " + path);
            }

            System.out.println("Root file inside .mxl: " + rootFilePath);

            // Pass 2: re-open the zip and extract the declared root file
            try (ZipInputStream zis2 = new ZipInputStream(new FileInputStream(path))) {
                ZipEntry entry2;
                while ((entry2 = zis2.getNextEntry()) != null) {
                    if (entry2.getName().equals(rootFilePath)) {
                        return (ScorePartwise) Marshalling.unmarshal(zis2);
                    }
                }
            }

            throw new RuntimeException("Declared root file '" + rootFilePath
                    + "' not found inside " + path);
        }
    }

    /**
     * Parses container.xml to find the <rootfile full-path="..."/> entry.
     */
    private static String readRootFilePath(InputStream containerXmlStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(containerXmlStream);

        NodeList rootFiles = doc.getElementsByTagName("rootfile");
        if (rootFiles.getLength() == 0) {
            throw new RuntimeException("No <rootfile> element found in container.xml");
        }

        Element rootFile = (Element) rootFiles.item(0);
        return rootFile.getAttribute("full-path");
    }
}