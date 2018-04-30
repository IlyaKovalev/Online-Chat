import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javafx.scene.control.TextField;
import javafx.util.Duration;

public class MainFrame extends Application {

    String data="";
    String Name="";
    public static void main(String args[]){launch(args);}

    public void start(Stage primaryStage){

        //Socket s;

        Thread read = null;
        boolean node=false;


        Pane pane=new Pane();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int height = dim.height;
        int width = dim.width;
        pane.setPrefSize(width/2,height/2);

        TextField userNameField = new TextField();
        userNameField.setPromptText("Username");
        TextField passwordField = new TextField();
        passwordField.setPromptText("Password");
        Button LoginBtn=new Button("Login");


        userNameField.setLayoutX(width/4-80);
        userNameField.setLayoutY(height/4-40);
        passwordField.setLayoutX(width/4-80);
        passwordField.setLayoutY(height/4);
        LoginBtn.setLayoutX(width/4-80);
        LoginBtn.setLayoutY(height/4+40);

        System.out.println(userNameField.getLength());
        pane.getChildren().addAll(userNameField,passwordField,LoginBtn);

        Scene scene = new Scene(pane);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        LoginBtn.setOnAction((ActionEvent event) ->{
            Name = userNameField.getText();
            VBox vBox =new VBox();
            HBox ForSend = new HBox();
            vBox.setStyle("-fx-background-color:#4286f4");
            ScrollPane chat = new ScrollPane();
            VBox chatField = new VBox();
            TextArea SendLine = new TextArea();
            Button SendMessageBtn = new Button("send");
            SendMessageBtn.setFont(new Font("Arial",20));
            SendMessageBtn.setPrefSize(width*0.15,height*0.05);

            chat.setContent(chatField);
            vBox.setPrefSize(width*0.6,height*0.46);
            chat.setPrefSize(width*0.55,height*0.4);
            chat.setStyle("-fx-background-color:#e1e8f4");
            chatField.setPrefSize(chat.getPrefWidth(),chat.getPrefHeight());
            SendLine.setPrefSize(width*0.45,height*0.05);
            SendLine.setFont(new Font("Arial",14));

            VBox.setMargin(chat,new Insets(5));
            HBox.setMargin(SendLine,new Insets(5));
            HBox.setMargin(SendMessageBtn,new Insets(5,5,5,0));

            ForSend.getChildren().addAll(SendLine,SendMessageBtn);
            vBox.getChildren().addAll(chat,ForSend);

            Scene scene1 = new Scene(vBox);

            primaryStage.setScene(scene1);
            primaryStage.show();

            try {
                InetAddress address = InetAddress.getByName("192.168.1.47");
                Socket s = new Socket(address, 3128);
                System.out.println(s.getInetAddress().toString());

                InputStream in = s.getInputStream();
                OutputStream out = s.getOutputStream();
                s.setSoTimeout(0);

                SendMessageBtn.setOnAction(event1-> {
                    String message =Name+" : "+SendLine.getText();
                    SendLine.clear();
                    byte[] mess = message.getBytes();

                    try{
                        out.write(mess);
                        out.flush();
                    }catch(IOException e){
                        e.printStackTrace(); }
                        });

                Thread thread = new Thread(()->{
                    while (true){
                        byte buf[] = new byte[64 * 1024];
                        int r = 0;

                        try { r = in.read(buf); }
                        catch (IOException e) { e.printStackTrace(); }

                        if (r!=-1){ data = new String(buf, 0, r); }
                    }
                });
                thread.start();
                final Timeline t = new Timeline(new KeyFrame(Duration.millis(30),myevent->{
                    if (data!=""){
                        chatField.getChildren().add(new Label(data));
                        data="";
                    }
                }));
                t.setCycleCount(Timeline.INDEFINITE);
                t.play();
            } catch(Exception e) {System.out.println("init error: "+e);} // вывод исключений
            SendLine.setOnKeyReleased(event1 -> {
                if (event1.getCode()== KeyCode.ENTER){
                    SendMessageBtn.fire();
                    }
                });
        });

    }
}
