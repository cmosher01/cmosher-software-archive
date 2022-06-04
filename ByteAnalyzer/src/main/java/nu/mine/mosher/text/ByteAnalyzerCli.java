package nu.mine.mosher.text;

import java.util.*;

public class ByteAnalyzerCli {
    public boolean help;
    public List<String> inputs = new ArrayList<>();
    public boolean verbose;

    public void help(final Optional<String> x) {
        this.help = true;
        System.out.println("Usage:");
        System.out.println("    byte-analyzer [OPTIONS] input-file");
        System.out.println("Options:");
        System.out.println("    --help     show help, and exit");
        System.out.println("    --verbose  include normal characters (20-7E)");
    }

    public void verbose(final Optional<String> x) {
        this.verbose = true;
    }

    public void __(final Optional<String> x) {
        x.ifPresent(input -> this.inputs.add(input));
    }
}
