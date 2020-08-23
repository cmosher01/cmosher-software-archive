package nu.mine.mosher.example;

import java.io.IOException;

public class DocumentManagerController {
    private final DocumentManager documentManager;

    public DocumentManagerController(final DocumentManager documentManager) {
        this.documentManager = documentManager;
    }

    public void onFileNew() throws IOException {
        this.documentManager.createNewDocument();
    }
}
