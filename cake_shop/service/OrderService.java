package service;

import domain.Cake;
import domain.Order;
import repository.CakesRepo;
import repository.OrdersRepo;
import repository.Repo;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class OrderService {
    private final Repo repo;
    private final CakesRepo cakes;

    public OrderService(Repo repo,CakesRepo cakes) {
        this.repo = repo;
        this.cakes= cakes;
    }

    public OrderService() {
        this.repo = new OrdersRepo();
        this.cakes = new CakesRepo();
    }

    public ArrayList<Order> getAll()
    {
        return new ArrayList<>(repo.getAll().values());
    }
    public ArrayList<Cake> getCakes(){return new ArrayList<>(cakes.getAll().values());}

    public void addCake(String f,double p,int id){
        try{
            cakes.findById(id);
            throw new  RuntimeException("Cake already exists");
        }
        catch (Exception e) //if cake not found already, it can be added
        {
            Cake c=new Cake(f,p,id);
            cakes.add(c);
        }
    }
    public void removeCake(int id){
        try{
            cakes.remove(cakes.findById(id));
        }
        catch (Exception ignored){
            throw new RuntimeException();
        }
    }
    public boolean modifyCake(int id,String type,double price){
        try{
            Cake c=new Cake(type,price,id);
            cakes.update(id,c);
            return true;  //cake was modified
        }
        catch (Exception e){
            return false; //cake not found
        }
    }

    public boolean findId(int i){ //order by id
        try{
            repo.findById(i);
            return true; //found
        }
        catch (Exception e){
            return false; //not found
        }
    }
    public boolean findCakeId(int i){ //is cake with id
        try{
            cakes.findById(i);
            return true; //found
        }
        catch (Exception e){
            return false; //not found
        }
    }
    public Cake getCakeId(int i){ //give cake by id
        if(findCakeId(i))
            return cakes.getAll().get(i);
        else
            throw new RuntimeException("cake with specified id doesn't exist");
    }

    //public Cake getCakeFlPr(String)

    public void addOrder(Cake cake, String customer, boolean status, int id)
    {
        Order o=new Order(cake,customer,status,id);
        repo.add(o);
    }
    public boolean cancelOrder(int id)
    {
        try{
            repo.remove(repo.findById(id));
            return true;  //cake order was removed
        }
        catch (Exception e){
            return false; //cake order not found
        }
    }

    public boolean markAsFinished(int id)
    {
        try {
            Order c = new Order((Order) repo.findById(id));
            if(c.getStatus())
                return false; //already finished
            c.setStatus(true);
            repo.update(id,c);
            return true;
        }
        catch (Exception e){
            return false; //cake order not found
        }
    }
    public boolean markAsUnfinished(int id)
    {
        try {
            Order c = new Order((Order) repo.findById(id));
            if(!c.getStatus())
                return false; //already unfinished
            c.setStatus(false);
            repo.update(id,c);
            return true;
        }
        catch (Exception e){
            return false; //cake order not found
        }
    }

    public boolean changeOrderType(int id , String f,double p)
    {
        try {
            Order o = new Order((Order) repo.findById(id));
            try{
                Cake c=cakes.findCake(f,p);
                o.setCake(c);
                repo.update(id,o); //if cake exists in cake repo
            }
            catch (RuntimeException e){
                Cake c=new Cake(f,p,getCakes().size()+1);
                //cakes.add(c);
                o.setCake(c);
                repo.update(id,o); //if cake doesn't already exist
            }
            return true;
        }
        catch (Exception e){
            return false; //cake order not found
        }

    }

    public List<Order> allOrdersSameFlavour(String f){
        return this.getAll().stream()
                .filter(o->o.getCake().getFlavour().equals(f))
                .collect(toList());
    }
    public List<Order> allOrdersSameCustomer(String c){
        return this.getAll().stream()
                .filter(o->o.getCustomer().equals(c))
                .collect(toList());
    }
    public List<Cake> allCakesSamePrice(double p){
        return this.getCakes().stream()
                .filter(o->o.getPrice()==p)
                .collect(toList());
    }

    public List<Order> allFinishedOrders(){
        return this.getAll().stream()
                .filter(Order::getStatus)
                .collect(toList());
    }
    public List<Order> allUnfinishedOrders(){
        return this.getAll().stream()
                .filter(o->!o.getStatus())
                .collect(toList());
    }
}
