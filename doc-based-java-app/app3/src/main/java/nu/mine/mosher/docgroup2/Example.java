package nu.mine.mosher.docgroup2;

import nu.mine.mosher.DocModel;

public class Example {
    public static void run() {
        final SafeCloseGroup<Doc> group = new SafeCloseGroup<>();
        System.out.println(group.isEmpty());

        final Doc doc1 = group.createNew(new Doc(new DocModel("Doc 1")));
        System.out.println(group.isEmpty());

        final Doc doc2 = group.createNew(new Doc(new DocModel("Doc 2")));
        System.out.println(group.isEmpty());

        doc1.closeIfSafe();
        System.out.println(group.isEmpty());

        doc2.closeIfSafe();
        System.out.println(group.isEmpty());
    }
}
