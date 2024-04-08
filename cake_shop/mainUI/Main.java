package mainUI;//Design and implement a Java solution for managing the orders for birthday-cakes from a cake-shop.
//The program should allow CRUD operations for birthday-cakes, adding a new order, cancelling an order,
// finishing an order, creating different reports, etc.
import domain.Cake;
import domain.Order;
import repository.*;
import service.OrderService;
import ui.UI;

import java.io.*;
import java.util.Properties;

public class Main {

    public static OrdersRepo createDefaultRepo()
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
    public static void main(String[] args) {
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
        UI ui;
        switch (type) {
            case "binary" ->
            {   repo = new OrdersRepoBinaryFile(file); //Orders.bin
                serv = new OrderService(repo, ((OrdersRepoBinaryFile) repo).getCakes());
                ui = new UI(serv);
                ui.run();
            }
            case "text" -> {
                repo = new OrdersRepoTextFile(file); //Orders.txt
                serv = new OrderService(repo, ((OrdersRepoTextFile) repo).getCakes());
                ui = new UI(serv);
                ui.run();
            }
            case "data base" ->{
                repo= new OrdersRepoDB(file); //will be C:/Users/nlupo/IdeaProjects/a5-nina985/db/Orders.db
                serv = new OrderService(repo, ((OrdersRepoDB) repo).getCakes());
                ui = new UI(serv);
                ui.run();
            }
            default -> {
                repo = createDefaultRepo();
                serv = new OrderService(repo, ((OrdersRepo) repo).getCakes());
                ui = new UI(serv);
                ui.run();
            }
        }
    }
}
