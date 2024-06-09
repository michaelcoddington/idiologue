import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

public class TikaTest {

    public static void main(String[] args) {
        try {
            File f = new File("/Users/michaelcoddington/Downloads/m0538_boris1boriso_4cc-rgb.jpg");
            FileInputStream fis = new FileInputStream(f);
            Parser parser = new AutoDetectParser();
            ContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            parser.parse(fis, handler, metadata, context);

            String[] metadataNames = metadata.names();
            Arrays.stream(metadataNames).forEach(name -> {
                System.out.println("File has property " + name + ", values = " + metadata.get(name));
            });

        } catch (Exception er) {
            er.printStackTrace();
        }
    }

}
