package repository;

import domain.Cake;
import domain.Order;

import java.util.Map;

public class OrdersRepo extends MemoryRepo<Integer,Order>{

    protected CakesRepo cakes=new CakesRepo();
    public OrdersRepo() {super();}

    public CakesRepo getCakes(){return cakes;}
    public void add(Order o){
        try{
            findById(o.getId());
        }
        catch (Exception e){  //if id not found, then order can be added
            try{
                super.add(o);
                cakes.findById(o.getCake().getId());
            }
            catch (Exception f){   //if cake is not already in cake repo, add it as well
                this.cakes.add(o.getCake());
            }
        }
    }

    public void update(Integer i, Order o)
    {
        boolean found=false;
        for (Map.Entry<Integer,Cake> entry : cakes.getAll().entrySet()) {
            if (entry.getValue().getFlavour().equals(o.getCake().getFlavour()) && entry.getValue().getPrice() == o.getCake().getPrice()) {
                found = true;
                break;
            }
        }
        if(!found) //cake from new order doesn't already exist
        {
            Cake c = new Cake(o.getCake().getFlavour(),o.getCake().getPrice(),cakes.getAll().size()+1);
            cakes.add(c);
        }
        super.update(i,o);
    }
}
