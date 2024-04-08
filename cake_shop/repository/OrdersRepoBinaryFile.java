package repository;

import domain.Cake;
import domain.Order;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

public class OrdersRepoBinaryFile extends FileRepo<Integer, Order>
{
    protected CakesRepo cakes;
    public OrdersRepoBinaryFile(String filename) {
        super(filename);
        cakes = new CakesRepo();
        try{
            readFromFile();
        }catch (Exception e){
            writeToFile();
        }
    }

    public CakesRepo getCakes(){return cakes;}

    public void add(Order o) throws RuntimeException {
        super.add(o);
        try{
            Cake c = cakes.findCake(o.getCake().getFlavour(),o.getCake().getPrice()); //if a cake with the attributes from the file already exists in the cake repo
        }
        catch (RuntimeException e) //cake not in cake repo, so it has to be given an unoccupied id and added to the repo
        {
            Cake c = new Cake(o.getCake().getFlavour(),o.getCake().getPrice(),cakes.getAll().size()+1);
            cakes.add(c);
        }
        writeToFile();
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
        writeToFile();
    }

    @Override
    public void readFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            TreeMap<Integer, Order> temp = (TreeMap<Integer, Order>) ois.readObject();
            for (Map.Entry<Integer, Order> entry : temp.entrySet()) {
                add(entry.getValue());
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(repo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
