package domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CakeTest {

    @Test
    void getId(){
        Cake c=new Cake("Chocolate",66.6,1);
        assertEquals(1,c.getId());
    }
    @Test
    void getFlavour() {
        Cake c=new Cake("Chocolate",66.6,1);
        assertEquals("Chocolate",c.getFlavour());
    }

    @Test
    void getPrice() {
        Cake c=new Cake("Chocolate",66.6,1);
        assertEquals(66.6,c.getPrice());
    }

    @Test
    void setId(){
        Cake c=new Cake("Chocolate",66.6,1);
        c.setId(44);
        assertEquals(44,c.id);
    }
    @Test
    void setFlavour() {
        Cake c=new Cake("Chocolate",66.6,1);
        c.setFlavour("Caramel");
        assertEquals("Caramel",c.flavour);
    }

    @Test
    void setPrice() {
        Cake c=new Cake("Chocolate",66.6,1);
        c.setPrice(0.0003);
        assertEquals(0.0003,c.price);
        try{
            c.setPrice(-4);
        }catch (RuntimeException e){assert(true);}
    }

    @Test
    void testEquals() {
        Cake c1=new Cake("Chocolate",66.6,1);
        Cake c2=new Cake("Chocolate",66.6,3);
        Cake c3=new Cake("Strawberry",66.6,5);
        Cake c4=new Cake("Chocolate",348,7);
        Cake c5=new Cake("Carrot",16.7,9);

        assert(c1.equals(c2));
        assert(!c1.equals(c3));
        assert(!c1.equals(c4));
        assert(!c1.equals(c5));
    }

    @Test
    void testToString() {
        Cake c1=new Cake("Chocolate",66.6,1);
        String s1 = c1.toString();
        String s2 = "Cake no.1: \n- Type: Chocolate\n- Price: $66.6\n";
        assertEquals(s1,s2);
    }

    @Test
    void testCopy() {
        Cake c1 = new Cake("Chocolate",66.6,1);
        Cake c2 = new Cake(c1);
        assertEquals(c1,c2);
        assertEquals(c1.getFlavour(),c2.getFlavour());
        assertEquals(c1.getPrice(),c2.getPrice());
        assertEquals(c1.getId(),c2.getId());
    }

    @Test
    void testDefaultConstr(){
        Cake c = new Cake();
        assertEquals(-1,c.getId());
        assertEquals("",c.getFlavour());
        assertEquals(-1,c.getPrice());
    }
}