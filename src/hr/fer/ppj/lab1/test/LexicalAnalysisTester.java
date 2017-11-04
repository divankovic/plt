package hr.fer.ppj.lab1.test;

import hr.fer.ppj.lab1.GLA;
import hr.fer.ppj.lab1.LA;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Class for testing completed assignment
 */
public class LexicalAnalysisTester {

    private static final String PATH_PREFIX_IN = "./src/hr.fer.ppj.lab1.res/in/";
    private static final String PATH_PREFIX_OUT = "./src/hr.fer.ppj.lab1.res/out/";

    private static final String LANG_FILE = ".lan";
    private static final String INPUT_FILE = ".in";
    private static final String OUT_FILE = ".out";
    private static final String RESULT_FILE = "LA_out.txt";

    private static List<String> testFiles = Arrays.asList(
            "minusLang",
            "c",
            "nadji_a1",
            "nadji_a2",
            "simplePpjLang",
            "svaki_drugi_a1",
            "svaki_drugi_a2"
    );

    /**
     * Entry point
     */
    public static void main(String[] args) throws IOException {

        for (String testFile : testFiles) {

            GLA.main(new String[]{PATH_PREFIX_IN + testFile + LANG_FILE});
            LA.main(new String[]{PATH_PREFIX_IN + testFile + INPUT_FILE});

            String expected = new String(Files.readAllBytes(Paths.get(PATH_PREFIX_IN + testFile + OUT_FILE)), StandardCharsets.UTF_8);
            String actual = new String(Files.readAllBytes(Paths.get(PATH_PREFIX_OUT + RESULT_FILE)), StandardCharsets.UTF_8);

            if (!expected.equals(actual)) {
                System.err.println("ERROR FOUND IN FILE: " + testFile);
            }

        }

    }

}
