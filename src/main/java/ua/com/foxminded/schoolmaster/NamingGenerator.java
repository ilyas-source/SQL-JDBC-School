package ua.com.foxminded.schoolmaster;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NamingGenerator {
    static Random random = new Random();

    private static String generateRandomString(int leftLimit, int rightLimit, int targetStringLength) {
	return random.ints(leftLimit, rightLimit + 1).limit(targetStringLength)
		.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public static String generateGroupName() {
	StringBuilder name = new StringBuilder();
	name.append(generateRandomString(65, 90, 2)).append("-").append(generateRandomString(48, 57, 2));
	return name.toString();
    }

    public static String getRandomStringFromFile(String fileName) throws IOException, URISyntaxException {
	List<String> strings = getStringListFromFile(fileName);
	return strings.get(random.nextInt(strings.size()));
    }

    private static List<String> getStringListFromFile(String fileName) throws IOException, URISyntaxException {
	URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
	return Files.lines(Paths.get(url.toURI())).collect(Collectors.toList());
    }
}