import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Play extends Application {
    private static final Border BORDER_RED = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));

    @Override
    public void init() {
        System.err.println("start Play.init");
        System.err.println("end Play.init");
    }

    @Override
    public void start(final Stage primaryStage) {
        System.err.println("begin Play.start");
        setExitPlatformOnCloseRequestOf(primaryStage);
        primaryStage.setScene(createScene());
        primaryStage.show();
        System.err.println("end Play.start");
    }

    private static void setExitPlatformOnCloseRequestOf(final Stage stage) {
        stage.setOnCloseRequest(t -> Platform.exit());
        Platform.setImplicitExit(false);
    }

    private static Scene createScene() {
        final Pane root = new Pane();
        showAllMouseEventsDuringCaptureFor("pane", root);
        showMouseDragEventsDuringBubbleFor("pane", root, false);

        addShapesTo(root);

        final Scene scene = new Scene(root, 640, 480, Color.BEIGE);

        return scene;
    }

    private static void addShapesTo(final Pane pane) {
        final Circle c1 = new Circle(150D, 100D, 30D, Color.LIGHTBLUE);
        c1.setStroke(Color.DARKSLATEGRAY);
        showAllMouseEventsDuringCaptureFor("BLUE", c1);
        showMouseDragEventsDuringBubbleFor("BLUE", c1, false);
        pane.getChildren().add(c1);

        final Circle c2 = new Circle(250D, 200D, 30D, Color.LIGHTGREEN);
        c2.setStroke(Color.DARKSLATEGRAY);
//        showAllMouseEventsDuringCaptureFor("green", c2);
        pane.getChildren().add(c2);
    }

//    private static BooleanProperty ignoreNextClick = new SimpleBooleanProperty();

    private static void showMouseDragEventsDuringBubbleFor(final String title, final Node node, final boolean consume) {
        node.setOnMousePressed(t -> {
            log("bubbling", title, t);
            if (consume) {
                t.consume();
            }
        });
        node.setOnMouseDragged(t -> {
            log("bubbling", title, t);
//            ignoreNextClick.set(true);
            if (consume) {
                t.consume();
            }
        });
        node.setOnMouseReleased(t -> {
            log("bubbling", title, t);
            if (consume) {
                t.consume();
            }
        });
        node.setOnMouseClicked(t -> {
//            if (ignoreNextClick.get()) {
//                System.err.println("Should ignore this click:");
//                ignoreNextClick.set(false);
//            }
            log("bubbling", title, t);
            if (consume) {
                t.consume();
            }
        });
    }

    private static void showAllMouseEventsDuringCaptureFor(final String title, final Node node) {
        node.addEventFilter(MouseEvent.ANY, t -> {
//            if (!(t.getEventType().equals(MouseEvent.MOUSE_DRAGGED) || t.getEventType().equals(MouseEvent.MOUSE_MOVED))) {
            log("    ↓    capturing", title, t);
//            }
        });
    }

    @Override
    public void stop() {
        System.err.println("start Play.stop");
        System.err.println("end Play.stop");
        System.err.flush();
        System.out.flush();
    }

    private static void log(final String when, final String title, final MouseEvent t) {
        System.err.println(when+" mouse event: for="+title+",type="+t.getEventType()+",xy="+t.getX()+"/"+t.getY()+",still="+t.isStillSincePress()+",target="+t.getTarget());
    }
}
