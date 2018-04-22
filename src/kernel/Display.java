package kernel;

import javafx.application.Application;
import ui.pane.AsidePane;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import todoitem.Item;
import todoitem.util.TimeStamp;
import ui.Config;
import ui.pane.*;


/**
* You need to implement Calendar GUI here!
* show the calendar of month of today.
* jump to last/next month's calendar
* jump to last/next year's calendar
*
* jump to one specific day's calendar
* */
public class Display extends Application {
    private static Stage stage = new Stage();
    private static StackPane imageCalendarPane = new StackPane();
    private static GridPane calendarPane = new GridPane();
    private static GridPane calendarWithAside = new GridPane();
    private static DetailPane detailPane;
    private static TimeStamp fromStatic;
    private static TimeStamp toStatic;
    private static ImageView backgroundImage;
    private static EditPane editPane;
    public Display(){

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        calendarPane.add(MenuPane.getInstance(), 0 , 0);
        calendarPane.add(BodyPane.getInstance(), 0 , 1);
        calendarPane.add(QueryPane.getInstance(), 0 , 2);

        calendarWithAside.add(new AsidePane(),0,0);
        calendarWithAside.add(calendarPane,1,0);
        calendarWithAside.setHgap(10);

        calendarWithAside.setTranslateX(300);
        calendarWithAside.setTranslateY(200);

        backgroundImage = new ImageView(Config.class.getResource("/res/"+DateUtil.getToday().getMonth()+".jpg").toString());
        backgroundImage.setFitHeight(650);
        backgroundImage.setFitWidth(1050);
        imageCalendarPane.getChildren().add(backgroundImage);
        imageCalendarPane.getChildren().add(calendarWithAside);
        Scene scene = new Scene(imageCalendarPane);
        stage.setMaxHeight(Config.getWindowHeight());
        stage.setMinHeight(Config.getWindowHeight());
        stage.setMaxWidth(Config.getWindowWidth());
        stage.setMinWidth(Config.getWindowWidth());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void addDetailPane(TimeStamp from , TimeStamp to) {
        if (detailPane != null) {
            removeDetailPane();
        }
        detailPane = new DetailPane(from , to);
        fromStatic = from;
        toStatic = to;
        imageCalendarPane.getChildren().add(detailPane);
    }

    public static void removeDetailPane() {
        imageCalendarPane.getChildren().remove(detailPane);
        detailPane = null;
    }

    public static void refreshDetailPane() {
        imageCalendarPane.getChildren().remove(detailPane);
        detailPane  = new DetailPane(fromStatic , toStatic);
        imageCalendarPane.getChildren().add(detailPane);
    }

    public static void addEditPane(Item item) {
        editPane = new EditPane(item);
        imageCalendarPane.getChildren().add(editPane);
        editPane.setAlignment(Pos.CENTER);
    }

    public static void removeEditPane() {
        BodyPane.getInstance().refresh();
        imageCalendarPane.getChildren().remove(editPane);
    }


    /**
     * paint the days of whole current month on the frame with the given kernel.CalendarDate
     * @param date a valid kernel.CalendarDate param.
     */
    public static boolean paintDays(CalendarDate date){
        if (date == null){
            System.out.println("date is null!");
            return false;
        }
        if (DateUtil.isValid(date)){
            MenuPane.getInstance().changeChoice(date);
            BodyPane.getInstance().changeContent(date);
            backgroundImage.setImage(new Image(Config.class.getResource("/res/"+date.getMonth()+".jpg").toString()));
            return true;
        }else {
            System.out.println("date "+ date.getYear()+date.getMonth()+ date.getDay() + " is not valid");
            return false;
        }
    }
}
