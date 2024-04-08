package ui;

import domain.Cake;
import domain.Entity;
import domain.Order;
import service.OrderService;

import java.util.ArrayList;
import java.util.Scanner;
public class UI {
    private final OrderService serv;
    public UI(OrderService serv)
    {
        this.serv = serv;
    }
    public void listOrders(){
        ArrayList<Order> orders = this.serv.getAll();
        for (Entity c: orders)
            System.out.println(c+"\n");
    }
    public void listCakes(){
        ArrayList<Cake> cakes = this.serv.getCakes();
        for (Entity c: cakes)
            System.out.println(c+"\n");
    }

    public void addOrder(){
        Cake c=new Cake();
        System.out.print("Input order id: ");
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter("\\n");
        try{
            int id = scan.nextInt();
            if(serv.findId(id)) {
                System.out.println("Order with specified id already exits");
                return;
            }
            System.out.print("Input cake type: ");
            String f = scan.next();
            System.out.print("Input order price: ");
            try{
                double p=scan.nextDouble();
                int found=0;
                for(Cake e : serv.getCakes())
                    if(e.getFlavour().equals(f) && e.getPrice()==p)
                    {
                        c=new Cake(e); //if the cake is already in the cake repo, it will be used for the order
                        found=1;
                    }
                if(found==0)//if it doesn't exist, it's automatically given an id and added to the repo
                {
                    int cid=serv.getCakes().size()+1;
                    c=new Cake(f,p,cid);
                }
            }
            catch (java.util.InputMismatchException e){
                System.out.println("Price has to be a number");
                return;}

            System.out.print("Input client name: ");
            String cl = scan.next();

            System.out.print("Input F/U for status( finished/unfinished ): ");
            String s=scan.next();
            boolean stat;
            if(s.equals("f") || s.equals("F"))
            {
                stat=true;
            }
            else if (s.equals("u") || s.equals("U"))
            {
                stat=false;
            }
            else {System.out.println("Input F/U for status"); return;}
            serv.addOrder(c,cl,stat,id);
            System.out.println("Order added");
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("Invalid id");
        }
    }

    public void removeCake(){
        System.out.print("Input id of order to remove: ");
        Scanner scan = new Scanner(System.in);
        try{
            int id = scan.nextInt();
            if(serv.cancelOrder(id))
                System.out.println("Order cancelled");
            else System.out.println("Order no."+id+" not found");
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("Invalid id");}
    }

    public void markFinished(){
        System.out.print("Input id of order to change status: ");
        Scanner scan = new Scanner(System.in);
        try{
            int id = scan.nextInt();
            if(!serv.findId(id)) {
                System.out.println("Order no."+id+" not found");
                return;
            }
            System.out.print("Input F/U for new status ( finished/unfinished ): ");
            String s=scan.next();
            if(s.equals("f") || s.equals("F")){
                if(serv.markAsFinished(id))
                    System.out.println("Order was updated");
                else
                    System.out.println("Order is already finished");
            }
            else if (s.equals("u") || s.equals("U")){
                if(serv.markAsUnfinished(id))
                    System.out.println("Order was updated");
                else
                    System.out.println("Order is already in progress");
            }
            else System.out.println("Input F/U for status");
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("Invalid id");}
    }

    public void updateFlavour(){
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter("\\n");
        System.out.print("Input id of order to update: ");
        try{
            int id = scan.nextInt();
            if(!serv.findId(id)) {
                System.out.println("Order no."+id+" not found");
                return;
            }
            System.out.print("Input new cake type: ");
            String f = scan.next();
            System.out.print("Input order price: ");
            try{
                double p=scan.nextDouble();
                int found=0;
                for(Cake e : serv.getCakes())
                    if (e.getFlavour().equals(f) && e.getPrice() == p) {
                        //if the cake is already in the cake repo, it will be used for the order
                        found = 1;
                        break;
                    }
                if(found==0)//if it doesn't exist, it's automatically given an id anf added to the repo
                {
                    int cid=serv.getCakes().size()+1;
                    //serv.addCake(f,p,cid);
                }
                if(serv.changeOrderType(id,f,p))
                    System.out.println("Order was updated");
            }
            catch (java.util.InputMismatchException e){
                System.out.println("Price has to be a number");}
        }
        catch (java.util.InputMismatchException e)
        {
            System.out.println("Invalid id");}
    }

    public void viewSameType(){
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter("\\n");
        System.out.print("Input cake type ( key sensitive ): ");
        String f = scan.next();
        if(serv.allOrdersSameFlavour(f).isEmpty()){
            System.out.println("There are no orders of "+f+" cakes");
            return;
        }
        serv.allOrdersSameFlavour(f).forEach(order -> System.out.println(order+"\n"));
    }

    public void viewCustomerOrd(){
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter("\\n");
        System.out.print("Input customer name ( key sensitive ): ");
        String c = scan.next();
        if(serv.allOrdersSameCustomer(c).isEmpty()){
            System.out.println("There is no client by the name of "+c);
            return;
        }
        serv.allOrdersSameCustomer(c).forEach(order -> System.out.println(order+"\n"));
    }

    public void cakesSamePrice(){
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter("\\n");
        System.out.print("Input price: ");
        double p = scan.nextDouble();
        if(serv.allCakesSamePrice(p).isEmpty()){
            System.out.println("There are no cakes that cost $"+p);
            return;
        }
        serv.allCakesSamePrice(p).forEach(cake -> System.out.println(cake+"\n"));
    }


    public void finishedOrders(){
        if(serv.allFinishedOrders().isEmpty()){
            System.out.println("There are no finished orders");
            return;
        }
        serv.allFinishedOrders().forEach(order -> System.out.println(order+"\n"));
    }
    public void unfinishedOrders(){
        if(serv.allUnfinishedOrders().isEmpty()){
            System.out.println("There are no unfinished orders");
            return;
        }
        serv.allUnfinishedOrders().forEach(order -> System.out.println(order+"\n"));
    }

    public void printMenu(){
        System.out.println("m/M - Show menu");
        System.out.println("1 - List all orders");
        System.out.println("2 - Add new order");
        System.out.println("3 - Cancel order");
        System.out.println("4 - Mark order as (un)finished");
        System.out.println("5 - Update cake in order");
        System.out.println("6 - View orders with the same cake type");
        System.out.println("7 - View customer's order list");
        System.out.println("8 - View cakes of same price");
        System.out.println("9 - View finished orders");
        System.out.println("10 - View unfinished orders");
        System.out.println("11 - List all cakes");
        System.out.println("0 - Exit");
    }

    public void run()
    {
        printMenu();
        while (true)
        {
            System.out.print("Please input your option: ");
            Scanner scan = new Scanner(System.in);
            String command = scan.next();
            System.out.print("\n");
            switch (command)
            {
                case "m", "M":
                    printMenu();
                    break;
                case "0":
                    return;
                case "1":
                    listOrders();
                    break;
                case "2":
                    addOrder();
                    break;
                case "3":
                    removeCake();
                    break;
                case "4":
                    markFinished();
                    break;
                case "5":
                    updateFlavour();
                    break;
                case "6":
                    viewSameType();
                    break;
                case "7":
                    viewCustomerOrd();
                    break;
                case "8":
                    cakesSamePrice();
                    break;
                case "9":
                    finishedOrders();
                    break;
                case "10":
                    unfinishedOrders();
                    break;
                case"11":
                    listCakes();
                    break;
                default:
                    System.out.print("Invalid option.\n");
                    break;
            }
        }
    }
}
