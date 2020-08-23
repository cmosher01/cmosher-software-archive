import javafx.application.Application;
import javafx.stage.Stage;

public abstract class FxApplication extends Application {
    @Override
    public void start(final Stage primaryStage) throws Exception {
        final Parameters parameters = getParameters();
        parameters.getUnnamed().forEach(System.out::println);
        parameters.getNamed().forEach((k,v) -> {System.out.println(k+"="+v);});
        discard(primaryStage);
        createController().run();
    }

    protected abstract AppController createController();

    private static void discard(final Stage stage) {
        stage.setMaxWidth(0D);
        stage.setMaxHeight(0D);
    }
}
