package nu.mine.mosher.example;

import javafx.application.Application;
import javafx.stage.Stage;

public class Example extends Application {
    @Override
    public void start(final Stage primaryStage) throws Exception {
        primaryStage.close();
        ignoreUserCancelledCloseExceptions();

//        final CommandLineOptions commandLineOptions = new CommandLineOptions(getParameters());
        // could have EnvironmentVariableOptions, SystemPropertyOptions, or other types of immutable externally injected options
        // put all into here:

//        final OptionManager optionManager = new OptionManager();
        final Session session = Session.create();

        session.run();
    }

    private static void ignoreUserCancelledCloseExceptions() {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            if (throwable instanceof UserCancelledClose) {
                return;
            }
            Thread.currentThread().getThreadGroup().uncaughtException(thread, throwable);
        });
    }
}
