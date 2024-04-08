package service;

import domain.Cake;
import domain.Order;
import org.junit.jupiter.api.Test;
import repository.OrdersRepo;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    public static OrderService createService()
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

        return new OrderService(repo,repo.getCakes());
    }
    @Test
    void getAll() {
        OrderService s = createService();
        ArrayList<Order> r=s.getAll();
        assert(r.size()==5);
        Cake c1 = new Cake("Chocolate",30,1);
        assertEquals(c1,r.get(0).getCake());
        assertEquals(1,r.get(0).getId()); //r[0] == o1
    }

    @Test
    void getCakes() {
        OrderService s = createService();
        ArrayList<Cake> c=s.getCakes();
        assert(c.size()==4);
        Cake c1 = new Cake("Chocolate",30,1);
        assertEquals(c1,c.get(0));
    }

    @Test
    void addCake() {
        OrderService s = new OrderService();
        Cake c1 = new Cake("Chocolate",30,1);
        s.addCake("Chocolate",30,1);
        assert (s.getCakes().size()==1);
        assertEquals(c1,s.getCakes().get(0));
        try {
            s.addCake("Fl",79,1);
        }catch (RuntimeException e){assert (true);} //cake with id 1 already exists
    }

    @Test
    void removeCake() {
        OrderService s = createService();
        Cake c1 = new Cake("Chocolate",30,1);
        s.removeCake(1);
        assert(s.getCakes().size()==3);
        assertNotEquals(c1,s.getCakes().get(0));
        try{
            s.removeCake(567);
        }catch (RuntimeException e){assert (true);}
    }

    @Test
    void modifyCake() {
        OrderService s = createService();
        Cake c1 = new Cake("Chocolate",30,1);
        s.modifyCake(1,"Blueberry",5);
        assertNotEquals(c1,s.getCakes().get(0));
        assertEquals("Blueberry",s.getCakes().get(0).getFlavour());
        assertFalse(s.modifyCake(66,"F",2)); //not found
    }

    @Test
    void findId() {
        OrderService s = createService();
        assert(s.findId(1));
        assertFalse(s.findId(77));
    }

    @Test
    void findCakeId(){
        OrderService s = createService();
        assert(s.findCakeId(2));
        assertFalse(s.findCakeId(98));
    }

    @Test
    void getCakeId() {
        OrderService s = createService();
        Cake c1 = new Cake("Chocolate",30,1);
        assertEquals(c1,s.getCakeId(1));
        try {
            s.getCakeId(985);
        }catch (RuntimeException e){assert (true);}
    }

    @Test
    void addOrder() {
        OrderService s = createService();
        Cake c = new Cake("Lemon",20,5);
        Cake c1 = new Cake("Chocolate",30,1);
        Order o1=new Order(c, "Elenore Johnston", false, 6);
        s.addOrder(c,"Elenore Johnston", false, 6);
        assert (s.getAll().size()==6);
        assert (s.getCakes().size()==5);
        assertEquals(o1,s.getAll().get(5));

        s.addOrder(c1,"Elenore Johnston", false, 7);
        assert (s.getAll().size()==7);
        assert (s.getCakes().size()==5);
    }

    @Test
    void cancelOrder() {
        OrderService s = createService();
        assert (s.cancelOrder(1));
        assert (s.getAll().size()==4);
        assert (s.getAll().get(0).getId()==2);
        assertFalse(s.cancelOrder(33));
    }

    @Test
    void markAsFinished() {
        OrderService s = createService();
        assertFalse(s.getAll().get(0).getStatus());
        assertTrue(s.getAll().get(1).getStatus());
        assert (s.markAsFinished(1)); //order 1 was unfinished
        assert (!s.markAsFinished(2)); //order 2 was finished
        assert (!s.markAsFinished(49)); //order 49 does not exist
        assertTrue(s.getAll().get(0).getStatus());
        assertTrue(s.getAll().get(1).getStatus());
    }

    @Test
    void markAsUnfinished() {
        OrderService s = createService();
        assertFalse(s.getAll().get(0).getStatus());
        assertTrue(s.getAll().get(1).getStatus());
        assert (!s.markAsUnfinished(1)); //order 1 was unfinished
        assert (s.markAsUnfinished(2)); //order 2 was finished
        assert (!s.markAsUnfinished(49)); //order 49 does not exist
        assertFalse(s.getAll().get(0).getStatus());
        assertFalse(s.getAll().get(1).getStatus());
    }

    @Test
    void changeOrderType() {
        OrderService s = createService();
        assertEquals ("Chocolate",s.getCakeId(1).getFlavour());
        s.changeOrderType(1,"Cake Pops",10);
        assertEquals ("Cake Pops",s.getCakeId(1).getFlavour());
        assertFalse(s.changeOrderType(66,"F",2)); //not found
    }

    @Test
    void testAllOrdersSameFlavour(){
        OrderService s = createService();
        ArrayList<Order> a = (ArrayList<Order>) s.allOrdersSameFlavour("Red Velvet");
        assert (a.size()==2);
        assertEquals(2,a.get(0).getId());
        assertEquals(5,a.get(1).getId());

        ArrayList<Order> a0 = (ArrayList<Order>) s.allOrdersSameFlavour("Flavour");
        assert (a0.isEmpty());
    }

    @Test
    void testAllOrdersSameCustomer(){
        OrderService s = createService();
        ArrayList<Order> a = (ArrayList<Order>) s.allOrdersSameCustomer("Elenore Johnston");
        assert (a.size()==2);
        assertEquals(1,a.get(0).getId());
        assertEquals(4,a.get(1).getId());

        ArrayList<Order> a0 = (ArrayList<Order>) s.allOrdersSameCustomer("Customer");
        assert (a0.isEmpty());
    }

    @Test
    void testAllCakesSamePrice(){
        OrderService s = createService();
        ArrayList<Cake> a = (ArrayList<Cake>) s.allCakesSamePrice(35);
        assert (a.size()==2);
        assertEquals (2,a.get(0).getId());
        assertEquals("Confetti",a.get(1).getFlavour());

        ArrayList<Cake> a0 = (ArrayList<Cake>) s.allCakesSamePrice(985);
        assert (a0.isEmpty());
    }

    @Test
    void testAllFinishedOrders(){
        OrderService s = createService();
        ArrayList<Order> a = (ArrayList<Order>) s.allFinishedOrders();
        assert (a.size()==3);
        assertEquals(2,a.get(0).getId());
        assertEquals(4,a.get(1).getId());
        assertEquals(5,a.get(2).getId());
    }

    @Test
    void testAllUnfinishedOrders(){
        OrderService s = createService();
        ArrayList<Order> a = (ArrayList<Order>) s.allUnfinishedOrders();
        assert (a.size()==2);
        assertEquals(1,a.get(0).getId());
        assertEquals(3,a.get(1).getId());
    }
}