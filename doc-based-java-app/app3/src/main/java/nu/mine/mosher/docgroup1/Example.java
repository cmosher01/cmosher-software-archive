package nu.mine.mosher.docgroup1;

import nu.mine.mosher.DocModel;

public class Example {
    public static void run() {
        final SafeCloseGroup<SafelyCloseable> group = new SafeCloseGroup<>();
        System.out.println(group.isEmpty());

        final Doc doc1 = Doc.create(group, new DocModel("Doc 1"));
        System.out.println(group.isEmpty());

        final Doc doc2 = Doc.create(group, new DocModel("Doc 2"));
        System.out.println(group.isEmpty());

        doc1.closeIfSafe();
        System.out.println(group.isEmpty());

        doc2.closeIfSafe();
        System.out.println(group.isEmpty());
    }
}
