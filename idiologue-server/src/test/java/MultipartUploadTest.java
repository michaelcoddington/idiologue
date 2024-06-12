import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MultipartUploadTest {

    public static void main(String[] args) {
        String boundary = "----------------------------618397665255609563106496";

        String sampleData = """
                ----------------------------618397665255609563106496
                Content-Disposition: form-data; name="test1"; filename="test1.txt"
                Content-Type: text/plain
                                
                hello there
                and you
                ----------------------------618397665255609563106496
                Content-Disposition: form-data; name="test2"; filename="test2.txt"
                Content-Type: text/plain
                                
                hello there again
                and you too
                ----------------------------618397665255609563106496--
                """;

        String[] strings = Iterables.toArray(Splitter.fixedLength(12).split(sampleData), String.class);

        List<String> chunks = Arrays.stream(strings).toList();
        System.out.println(chunks);


        DataBufferFactory factory = new DefaultDataBufferFactory();
        Flux<DataBuffer> stream = Flux.fromArray(strings).map(s -> factory.wrap(s.getBytes(UTF_8)));

        /*
         * As we iterate through the buffers, we want to:
         * 1. Skip over a boundary
         * 2. Find each header
         *      a. A header name will end with a colon
         *      b. A header value will end with a newline
         * 3. There will be a empty line / line break after the headers
         * 4. Until we've found the headers, we're going to start looking for the boundary.
         *
         */


        MultipartParser parser = new MultipartParser(boundary);
        System.out.println("start");
        Mono<Void> ret = parser.parse(stream);
        ret.subscribe();

        System.out.println("hm");

    }

}
