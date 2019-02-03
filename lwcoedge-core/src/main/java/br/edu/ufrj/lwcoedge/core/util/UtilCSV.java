package br.edu.ufrj.lwcoedge.core.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/*
 * Original source
 * http://www.mkyong.com/java/how-to-export-data-to-csv-file-java/
 */
public class UtilCSV {

private static final char DEFAULT_SEPARATOR = ',';

	/**
	 * New method responsible for writing a simple value.
	 * @param w Writer
	 * @param value Simple String value
	 * @throws IOException
	 */
    public static void writeLine(Writer w, String value) throws IOException {
        writeLine(w, Arrays.asList(value), DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    public static void writeLine(Writer w, List<String> values, char separators) throws IOException {
        writeLine(w, values, separators, ' ');
    }

    //https://tools.ietf.org/html/rfc4180
    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    public static void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote);
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());

    }
}
