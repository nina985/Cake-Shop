package repository;
import domain.Cake;
import domain.Order;

import java.io.*;
import java.util.Map;

public class OrdersRepoTextFile extends FileRepo<Integer,Order>
{
    protected CakesRepo cakes;
    public OrdersRepoTextFile(String filename) {
        super(filename);
        cakes = new CakesRepo();
        readFromFile();
    }
    public CakesRepo getCakes(){return cakes;}

    public void add(int ID, String customer, boolean status, String type, double price) throws RuntimeException {
        try{
            Cake c = cakes.findCake(type,price); //if a cake with the attributes from the file already exists in the cake repo
            Order o = new Order(c, customer, status,ID);
            super.add(o);
        }
        catch (RuntimeException e) //cake not in cake repo, so it has to be given an unoccupied id and added to the repo
        {
            Cake c = new Cake(type,price,cakes.getAll().size()+1);
            Order d = new Order(c, customer, status,ID);
            super.add(d);
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
    protected void readFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.filename));
            String line;
            while ( (line = reader.readLine()) != null){
                String[] tokens = line.split("[,]",0);
                if ( tokens.length != 6 )
                    continue;
                else{
                    int ID = Integer.parseInt(tokens[0].trim());
                    String customer = tokens[1].trim();
                    boolean status =Boolean.parseBoolean(tokens[2].trim());
                    String type = tokens[4].trim();
                    double price=Double.parseDouble(tokens[5].trim());
                    add(ID,customer,status,type,price);
                }
            }
            reader.close();
        }
        catch (FileNotFoundException f){
            throw new RuntimeException("File not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void writeToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename));
            Iterable<Order> orders = this.getAll().values();
            for (Order o : orders)
                writer.write(o.getId() + ", " + o.getCustomer() + ", " + o.getStatus() + " ," + o.getCake().getId() + ", " + o.getCake().getFlavour() + ", " + o.getCake().getPrice() + "\n");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
