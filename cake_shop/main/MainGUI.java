package main;

import domain.Cake;
import domain.Order;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import repository.*;
import service.OrderService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainGUI extends Application {
    private static OrdersRepo createDefaultRepo()
    {
        OrdersRepo repo = new OrdersRepo();
        Cake c1 = new Cake("Chocolate",30,1);
        Cake c2=new Cake("Red Velvet",35,2);
        Cake c3=new Cake("Confetti",35,3);
        Cake c4=new Cake("Vanilla Ice Cream",40,4);

        Order o1=new Order(c1, "Elenore Johnston", false, 1);
        Order o2=new Order(c2, "Andy Moon", true, 2);
        Order o3=new Order(c3, "Barbara Lou", false, 3);
        Order o4=new Order(c4, "Elenore Johnston", true,4);
        Order o5=new Order(c2, "Dorothy Duke", true, 5);

        repo.add(o1);
        repo.add(o2);
        repo.add(o3);
        repo.add(o4);
        repo.add(o5);

        return repo;
    }
    protected static OrderService getService(){
        String type, file;
        try (InputStream input = new FileInputStream("cake_shop/settings.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            type = prop.getProperty("Repository");
            file = prop.getProperty("Orders");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        Repo repo;
        OrderService serv;
        switch (type) {
            case "binary" ->
            {   repo = new OrdersRepoBinaryFile(file); //Orders.bin
                serv = new OrderService(repo, ((OrdersRepoBinaryFile) repo).getCakes());
            }
            case "text" -> {
                repo = new OrdersRepoTextFile(file); //Orders.txt
                serv = new OrderService(repo, ((OrdersRepoTextFile) repo).getCakes());
            }
            case "data base" ->{
                repo= new OrdersRepoDB(file); //will be C:/Users/nlupo/IdeaProjects/a5-nina985/db/Orders.db
                serv = new OrderService(repo, ((OrdersRepoDB) repo).getCakes());
            }
            default -> {
                repo = createDefaultRepo();
                serv = new OrderService(repo, ((OrdersRepo) repo).getCakes());
            }
        }
        return serv;
    }

    @Override
    public void start(Stage stage){
        try{
            OrderService serv = getService();

            CakeShopGUIController controller = new CakeShopGUIController(serv);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CakeShopGUI.fxml"));
            loader.setController(controller);
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("Cake Shop");
            stage.show();
        }
        catch (Exception e){
            e.printStackTrace();
            Throwable cause = e.getCause();
            if (cause != null) {
                System.err.println("Root cause: " + cause.getMessage());
            }
        }
    }
    public static void main(String[] args){
        launch(args);
    }
}
