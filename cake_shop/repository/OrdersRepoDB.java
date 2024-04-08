package repository;
import domain.Cake;
import domain.Order;
import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.*;
import java.util.Map;

public class OrdersRepoDB extends MemoryRepo<Integer, Order>{
    private final String URL;
    private Connection conn=null;
    protected CakesRepo cakes=new CakesRepo();

    public OrdersRepoDB(String u) {
        URL="jdbc:sqlite:"+u;
        connectToDatabase();
        createSchema();
        //initTables();
        try(PreparedStatement st= conn.prepareStatement("SELECT * from Cake"); ResultSet rs = st.executeQuery()) //initialize the cake repo
        {
            while (rs.next()){
                Cake c= new Cake(rs.getString("Type"),rs.getDouble("Price"),rs.getInt("Cid"));
                cakes.add(c);
            }
        }catch (Exception ignored){}

        try (PreparedStatement sto= conn.prepareStatement("SELECT * from CakeOrder"); ResultSet rs = sto.executeQuery()) //read from Order table
        {
            //System.out.println("Executing try block...");
            while (rs.next()){
                Cake c = cakes.findById(rs.getInt("Cid"));
                Order o = new Order(c,rs.getString("Client"),rs.getBoolean("Status"),rs.getInt("Oid"));
                super.add(o);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public CakesRepo getCakes(){return cakes;}

    void createSchema() {
        try {
            try (final Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Cake(Cid int PRIMARY KEY, Type varchar (400), Price real);");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS CakeOrder(Oid int PRIMARY KEY,Client varchar(400), Status boolean, Cid int, FOREIGN KEY (Cid) References Cake(Cid));");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] createSchema : " + e.getMessage());
        }
    }

    public void connectToDatabase() {
        try {
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(URL);
            if (conn == null || conn.isClosed())
                conn = ds.getConnection();
            //System.out.println("Connected to the database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void initTables() {
        try {
            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate("INSERT INTO Cake VALUES (1, 'Chocolate', 30)");
                statement.executeUpdate("INSERT INTO Cake VALUES (2, 'Red Velvet', 35)");
                statement.executeUpdate("INSERT INTO Cake VALUES (3, 'Confetti', 40)");
            }

            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate("INSERT INTO CakeOrder VALUES (1, 'Casandra Robinson', true,2)");
                statement.executeUpdate("INSERT INTO CakeOrder VALUES (2, 'Lilly Ann Jones', false,3)");
                statement.executeUpdate("INSERT INTO CakeOrder VALUES (3, 'Mario Olsen', true,2)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void addCakeToDb(Cake c){
        try {
            try (PreparedStatement statement = conn.prepareStatement("INSERT INTO Cake VALUES (?, ?, ?)")) {
                statement.setInt(1, c.getId());
                statement.setString(2, c.getFlavour());
                statement.setDouble(3, c.getPrice());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(Order o) {
        //conn.setAutoCommit(false);
        boolean found=false;
        for (Map.Entry<Integer,Cake> entry : cakes.getAll().entrySet()) {
            if (entry.getValue().getFlavour().equals(o.getCake().getFlavour()) && entry.getValue().getPrice() == o.getCake().getPrice()) {
                found = true;
                break;
            }
        }
        if(found){
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO CakeOrder (Oid, Client, Status, Cid) VALUES (?,?,?,?)")) {
                //System.out.println("Adding order...");
                ps.setInt(1, o.getId());
                ps.setString(2, o.getCustomer());
                ps.setBoolean(3, o.getStatus());
                ps.setInt(4, o.getCake().getId());
                //System.out.println(ps);
                ps.executeUpdate();
                //conn.commit();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            super.add(o);
        }
        else //cake not in cake repo, so it has to be given an unoccupied id and added to the repo
        {
            Cake c = new Cake(o.getCake().getFlavour(),o.getCake().getPrice(),cakes.getAll().size()+1);
            cakes.add(c);
            addCakeToDb(c);
            try(PreparedStatement ps = conn.prepareStatement("INSERT INTO CakeOrder (Oid, Client, Status, Cid) VALUES (?,?,?,?)")) {
                ps.setInt(1, o.getId());
                ps.setString(2, o.getCustomer());
                ps.setBoolean(3, o.getStatus());
                ps.setInt(4, c.getId());
                //System.out.println(psO);
                ps.executeUpdate();

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            super.add(o);
        }
    }

    public void remove(Order o){
        super.remove(o);
        try {
            try (PreparedStatement statement = conn.prepareStatement("DELETE FROM CakeOrder WHERE Oid=?")) {
                statement.setInt(1, o.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void update(Integer id, Order o) {
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
            addCakeToDb(c);
        }
        try{
            conn.setAutoCommit(false);
            try(PreparedStatement updateClient = conn.prepareStatement("UPDATE CakeOrder SET Client = ? WHERE Oid = ?");
                PreparedStatement updateStatus = conn.prepareStatement("UPDATE CakeOrder SET Status = ? WHERE Oid = ?");
                PreparedStatement updateCake = conn.prepareStatement("UPDATE CakeOrder SET Cid = ? WHERE Oid = ?")) {
                updateClient.setString(1, o.getCustomer());
                updateClient.setInt(2, o.getId());
                updateClient.executeUpdate();

                updateStatus.setBoolean(1, o.getStatus());
                updateStatus.setInt(2, o.getId());
                updateStatus.executeUpdate();

                updateCake.setInt(1, o.getCake().getId());
                updateCake.setInt(2, o.getId());
                updateCake.executeUpdate();
                conn.commit();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        super.update(id,o);
    }
}
