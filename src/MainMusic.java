import org.audiveris.proxymusic.ScorePartwise;
import org.audiveris.proxymusic.util.Marshalling;

import java.io.FileInputStream;

public class MainMusic {
    public static void main(String[] args) throws Exception {
        // point this at any .musicxml/.xml file you have, or a MuseScore export
        try (FileInputStream in = new FileInputStream("src/hello-world.musicxml")) {
            ScorePartwise score = (ScorePartwise) Marshalling.unmarshal(in);
            System.out.println("Loaded score: " + score.getMovementTitle());
            System.out.println("Parts: " + score.getPart().size());
        }
    }
}