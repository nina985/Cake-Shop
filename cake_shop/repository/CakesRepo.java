package repository;

import domain.Cake;

import java.util.Map;

public class CakesRepo extends MemoryRepo<Integer, Cake>{
    public CakesRepo() {super();}

    public Cake findCake(String fl,double pr) //find a cake but attributes other than id
    {
        for (Map.Entry<Integer,Cake> entry : repo.entrySet()) {
            if (entry.getValue().getFlavour().equals(fl) && entry.getValue().getPrice()==pr) {
                return entry.getValue();
            }
        }
        throw new RuntimeException("No cakes with specified attributes.");
    }

    public void update(int i, Cake c)
    {
        repo.get(i).setFlavour(c.getFlavour());
        repo.get(i).setPrice(c.getPrice());
        //id stays the same
    }
}
