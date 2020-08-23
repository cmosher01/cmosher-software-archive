import java.io.IOException;

public class ExampleAppController extends AppController {
    @Override
    public void run() throws Exception {
        final AppStage stage = new ExampleStage(this);
        stage.run();
    }
}
