package nu.mine.mosher;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTester {
    public static void main(final String... args) throws IOException {
        if (args.length != 1) {
            System.out.println("usage: RegExTester 'regex' <test-strings.txt");
            System.out.flush();
            return;
        }

        System.out.println("Using regex: "+args[0]);

        try (final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)))) {
            for (String test = in.readLine(); Objects.nonNull(test); test = in.readLine()) {
                System.out.println(test+":");
                final Pattern pattern = Pattern.compile(args[0]);
                final Matcher matcher = pattern.matcher(test);
                System.out.println("    "+(matcher.matches() ? "matches" : "does not match"));
                if (matcher.matches()) {
                    final int c = matcher.groupCount();
                    if (c > 0) {
                        System.out.println("    groups:");
                        for (int i = 1; i <= c; ++i) {
                            System.out.println("    " + i + ". \u220E" + nullToEmpty(matcher.group(i))+"\u220E");
                        }
                    }
                }
            }
        }

        System.out.flush();
    }

    private static String nullToEmpty(String s) {
        if (Objects.isNull(s)) {
            return "";
        }
        return s;
    }
}
