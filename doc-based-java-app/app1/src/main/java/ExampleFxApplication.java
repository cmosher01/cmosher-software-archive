public class ExampleFxApplication extends FxApplication {
    @Override
    protected AppController createController() {
        return new ExampleAppController();
    }
}
