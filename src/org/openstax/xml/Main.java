package org.openstax.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Main {

    static String XHTML_FILE = "xhtml";

    public static void main(String[] args) throws Throwable {

        if (args.length == 0) {
            throw new RuntimeException(
                    "Requires 2 arguments: the path to a file to validate and which checks to run ('all-checks', 'duplicate-id', 'broken-link')");
        }
        String[] desiredChecks =
                args.length == 1 ? new String[] {"all"} : Arrays.copyOfRange(args, 1, args.length);
        Set<String> inputs;
        InputStream input;
        if ("-".equals(args[0])) {
            // Read from stdin
            input = System.in;
            runChecks(input, desiredChecks);
        } else {
            System.out.println("Validating " + args[0]);
            inputs = getResources(args[0], XHTML_FILE);
            System.out.println("Found " + inputs.size() + " files");
            for (String file : inputs){
                System.out.println("Validating " + file);
                runChecks(file, desiredChecks);
            }
        }
    }

    public static Set<String> getResources(String path, String pattern) throws Throwable {
        File file = new File(path);
        if (!file.exists())
            return Collections.emptySet();
        else if (file.isFile())
            return new HashSet<>(Arrays.asList(path));
        else {
            try (Stream<Path> stream = Files.list(Paths.get(path))) {
                return stream.filter(f -> !Files.isDirectory(f)).map(f->f.toAbsolutePath().toString()).filter(filePath -> filePath.endsWith(pattern)).collect(Collectors.toSet());
            }
        }
    }

    public static void runChecks(String path, String[] desiredChecks) throws Throwable {
        runChecks(new FileInputStream(path), desiredChecks);
    }

    public static void runChecks(InputStream inputStream, String[] desiredChecks) throws Throwable {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        XMLVisitor visitor = new XMLVisitor();

        saxParser.parse(inputStream, visitor);
        String[] availableChecks = {"all", "duplicate-id", "broken-link", "link-to-duplicate-id"};

        boolean errorsFound = false;

        for (String check : desiredChecks) {
            // System.err.println(String.format("Running %s", check));
            switch (check) {
                case "all":
                    errorsFound |= visitor.linksToDuplicateIds();
                    errorsFound |= visitor.brokenLinks();
                    errorsFound |= visitor.duplicateIds();
                    break;
                case "duplicate-id":
                    errorsFound |= visitor.duplicateIds();
                    break;
                case "broken-link":
                    errorsFound |= visitor.brokenLinks();
                    break;
                case "link-to-duplicate-id":
                    errorsFound |= visitor.linksToDuplicateIds();
                    break;
                default:
                    throw new RuntimeException(
                            String.format("Invalid check name: %s. Valid names are: %s", check,
                                    Arrays.toString(availableChecks)));
            }
        }

        if (errorsFound) {
            throw new RuntimeException("File failed validation");
        }
    }
}
