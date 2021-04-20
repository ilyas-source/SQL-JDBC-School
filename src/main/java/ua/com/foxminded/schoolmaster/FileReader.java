package ua.com.foxminded.schoolmaster;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class FileReader {

    public String readFile(Path filePath) throws IOException {
	try (Stream<String> stream = Files.lines(filePath)) {
	    return stream.collect(joining(lineSeparator()));
	}
    }
}
