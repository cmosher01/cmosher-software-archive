package nu.mine.mosher.gedcom.ftm.filter;

import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.Gedcom;
import nu.mine.mosher.gedcom.GedcomLine;
import nu.mine.mosher.gedcom.GedcomTag;
import nu.mine.mosher.gedcom.GedcomTree;
import nu.mine.mosher.gedcom.exception.InvalidLevel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilterForFtm {
    private static BufferedWriter out;

    public static void main(final String... args) throws IOException, InvalidLevel {
        if (args.length != 1) {
            System.err.println("usage: java -jar gedcomFtm.jar <input.ged>");
            return;
        }
        final GedcomTree tree = Gedcom.parseFile(new File(args[0]), false);

        filterTree(tree.getRoot());

        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out), "windows-1252"));

        printGedcom(tree.getRoot());

        out.flush();
    }

    private static void filterTree(TreeNode<GedcomLine> root) {
        for (TreeNode<GedcomLine> top : root) {
            if (top.getObject().getTag().equals(GedcomTag.INDI) || top.getObject().getTag().equals(GedcomTag.FAM)) {
                filterIndividual(top);
            }
        }
    }

    private static void filterIndividual(TreeNode<GedcomLine> individual) {
        for (TreeNode<GedcomLine> node : individual) {
            GedcomLine line = node.getObject();
            if (line.getTag().equals(GedcomTag.EVEN)) {
                fixCustomEvent(node);
            }
        }
    }

    private static void fixCustomEvent(TreeNode<GedcomLine> event) {
        final List<TreeNode<GedcomLine>> nonTypeNodes = new ArrayList<>();
        TreeNode<GedcomLine> typeNode = null;
        for (TreeNode<GedcomLine> node : event) {
            if (node.getObject().getTag().equals(GedcomTag.TYPE)) {
                typeNode = node;
            } else {
                nonTypeNodes.add(node);
            }
        }
        if (typeNode != null) {
            // make sure TYPE is the *first* child of EVEN
            event.addChild(typeNode);
            for (TreeNode<GedcomLine> node : nonTypeNodes) {
                event.addChild(node);
            }
        }
    }

    private static void printGedcom(TreeNode<GedcomLine> node) throws IOException {
        printGedcomLine(node.getObject());
        for (TreeNode<GedcomLine> childNode : node) {
            printGedcom(childNode);
        }
    }

    private static boolean inSource;

    private static void printGedcomLine(GedcomLine line) throws IOException {
        if (line == null) {
            return;
        }
        if (line.getLevel() == 0) {
            if (line.getTag().equals(GedcomTag.SOUR)) {
                inSource = true;
            } else {
                inSource = false;
            }
        }
        out.write(Integer.toString(line.getLevel()));
        out.write(' ');

        if (line.hasID()) {
            out.write('@');
            out.write(line.getID());
            out.write('@');
            out.write(' ');
        }

        if (inSource && line.getLevel() == 1 && line.getTag().equals(GedcomTag.TEXT)) {
            // Convert SOUR.TEXT to SOUR.NOTE so FTM doesn't drop it
            out.write(GedcomTag.NOTE.toString());
        } else {
            if (line.getTag().equals(GedcomTag.UNKNOWN)) {
                // turn custom tags into notes so FTM doesn't turn them into events
                out.write("NOTE ");
                out.write(line.getTagString());
            } else if (line.getTag().equals(GedcomTag._UID)) {
                // _UID is a custom tag, actually
                out.write("NOTE ");
                out.write(line.getTagString());
            } else {
                out.write(line.getTag().toString());
            }
        }

        if (line.isPointer() || !line.getValue().isEmpty()) {
            out.write(' ');
        }

        if (line.isPointer()) {
            out.write('@');
            out.write(line.getPointer());
            out.write('@');
        } else {
            // do not turn @ into @@ because FTM doesn't recognize @@
            out.write(line.getValue());
        }
        out.newLine();
    }
}
