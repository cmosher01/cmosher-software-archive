import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ResourceBundle;

public class Primary {
    @FXML
    private ResourceBundle resources;
    private ExampleAppController app;
    private ExampleStage stage;

    private final BooleanProperty nonMac = new SimpleBooleanProperty(!mac());

    private boolean mac() {
        return os().startsWith("mac") || os().startsWith("darwin");
    }

    private static String os() {
        return System.getProperty("os.name","unknown").toLowerCase();
    }

    public BooleanProperty nonMacProperty() {
        return this.nonMac;
    }

    public void setNonMac(final boolean nonMac) {
        this.nonMac.set(nonMac);
    }

    public boolean getNonMac() {
        return this.nonMac.get();
    }

    public ExampleAppController getApp() {
        return app;
    }

    public void onFileQuit(ActionEvent actionEvent) {
//        this.app.onFileQuit(actionEvent);
    }

    public void onFileClose(ActionEvent actionEvent) {
        this.stage.onFileClose(actionEvent);
    }

    public void onFileExportChanged(ActionEvent actionEvent) {
    }

    public void onFileExportAll(ActionEvent actionEvent) {
    }

    public void onFileSaveAs(ActionEvent actionEvent) {
    }

    public void onEditNormalize(ActionEvent actionEvent) {
    }

    public void onEditSnap(ActionEvent actionEvent) {
    }

//    public void setApp(final ExampleAppController app) {
//        this.app = app;
//    }
}
