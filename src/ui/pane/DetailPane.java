package ui.pane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import kernel.Display;
import todoitem.Item;
import todoitem.ItemManager;
import todoitem.util.TimeStamp;

import java.util.ArrayList;

public class DetailPane extends BorderPane {
    public DetailPane(TimeStamp from , TimeStamp to) {
        this.getStylesheets().add("/stylesheet/buttonAndLabel.css");
        this.setStyle("-fx-background-color: white;");
        this.setMaxHeight(325);
        this.setMinHeight(325);
        this.setMaxWidth(525);
        this.setMinWidth(525);
        Label title = new Label("Detail");
        title.setStyle("-fx-font-size: 35px ;-fx-font-family: \"Segoe UI Semibold\";-fx-font-color: black;");
        this.setTop(title);
        this.setAlignment(title, Pos.CENTER);
        this.setMargin(title, new Insets(20, 0 , 0 , 0 ));

        ArrayList<Item> itemList = ItemManager.getInstance().getItemsByStamp(from, to);
        GridPane detailContent = new GridPane();
        for ( int i = 0 ; i < itemList.size(); i++) {
            ItemPane itemPane = new ItemPane(itemList.get(i));
            detailContent.add(itemPane, 0, i);
        }
        ScrollPane scrollContent = new ScrollPane();
        scrollContent.setContent(detailContent);
        this.setCenter(scrollContent);
        this.setAlignment(scrollContent, Pos.CENTER);


        HBox btRow = new HBox();
        Button addBt = new Button("add");
        addBt.setOnMouseClicked(event -> {
            Item item = new Item(from, to , "" , Item.ItemType.LEISURE);
            ItemManager.getInstance().addItem(item);
            Display.removeDetailPane();
            Display.addEditPane(item);
        });
        addBt.getStyleClass().add("btn");
        addBt.setMaxSize(45 , 30);
        addBt.setMinSize(45 , 30);
        Button quitBt = new Button("quit");
        quitBt.setOnMouseClicked(event -> {
            Display.removeDetailPane();
        });
        quitBt.getStyleClass().add("btn");
        quitBt.setMaxSize(45, 30);
        addBt.setMinSize(45 , 30);
        btRow.getChildren().add(0, addBt);
        btRow.getChildren().add(1,quitBt);
        btRow.setMargin(addBt , new Insets(10, 10 , 10 , 0));
        btRow.setMargin(quitBt , new Insets(10,0,10,10));

        this.setBottom(btRow);
        btRow.setAlignment(Pos.CENTER);

        this.setStyle("-fx-background-color: rgba(255 , 255 , 255 , 0.9);-fx-background-radius: 5.0;");
    }

    private class ItemPane extends GridPane {
        private Item item;
        public ItemPane(Item item) {
            this.item = item;
            int rowIndex = 0;
            Line line = new Line(0 , 0 , 500 , 0);
            line.setStroke(Color.YELLOW);
            this.add(line , 0, rowIndex++);

            Text fromToText = new Text("From: " + item.getFrom().toString() + " To: " + item.getTo().toString());
            this.add(fromToText,0 , rowIndex++);
            this.setMargin(fromToText, new Insets(5, 0 , 0 , 0 ));

            String typeStr = "Type: " + item.getItemType().getTypeStr();
            Text typeText = new Text(typeStr);
            this.add(typeText,0 , rowIndex++);
            this.setMargin(typeText, new Insets(5, 0 , 0 , 0 ));


            Text detailText = new Text("Detail: " + item.getDetailText());
            this.add(detailText, 0 , rowIndex++);
            this.setMargin(detailText, new Insets(5, 0 , 0 , 0 ));

            GridPane buttonPane = new GridPane();
            Button removeBt = new Button("remove");
            removeBt.setOnMouseClicked(event -> {
                ItemManager.getInstance().deleteItem(item);
                Display.refreshDetailPane();
                BodyPane.getInstance().refresh();
            });
            removeBt.setStyle("-fx-background-color: linear-gradient(#ff3333 0%,  #ff3333 100%);");
            removeBt.setCursor(Cursor.HAND);

            Button editBt = new Button("edit");
            editBt.setOnMouseClicked(event -> {
                Display.removeDetailPane();
                Display.addEditPane(item);
            });
            editBt.setMaxSize(60, 23);
            editBt.setMinSize(60, 23);
            editBt.setStyle("-fx-background-color: linear-gradient(#b3ffb3 0%, #4dff4d 100%);");
            editBt.setCursor(Cursor.HAND);

            buttonPane.add(removeBt,0,0);
            buttonPane.add(editBt, 1,0);
            buttonPane.setHgap(10);
            this.add(buttonPane,0,rowIndex++);
            this.setMargin(buttonPane, new Insets(5, 0 , 0 , 0 ));
            this.setPadding(new Insets(10, 10,0 , 10));
        }
    }
}
