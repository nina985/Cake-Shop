package repository;

import domain.Cake;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CakesRepoTest {

    CakesRepo createRepo(){
        Cake c1=new Cake("Chocolate",66.6,1);
        Cake c2=new Cake("Vanilla",66.6,3);
        Cake c3=new Cake("Strawberry",66.6,5);
        Cake c4=new Cake("Chocolate",348,7);

        CakesRepo c=new CakesRepo();
        c.add(c1);
        c.add(c2);
        c.add(c3);
        c.add(c4);

        return c;
    }
    @Test
    void getAll() {
        CakesRepo c = createRepo();
        assert(c.getAll().size()==4);
        assert(c.getAll().containsKey(7));
        Cake c1=new Cake("Chocolate",66.6,1);
        assert(c.getAll().containsValue(c1));

        Cake c5=new Cake("Carrot",16.7,9);
        assert(!c.getAll().containsValue(c5));
    }

    @Test
    void add() {
        Cake c1=new Cake("Chocolate",66.6,1);
        Cake c2=new Cake("Vanilla",66.6,3);
        CakesRepo c=new CakesRepo();
        c.add(c1);
        c.add(c2);
        assert(c.getAll().size()==2);
        assert(c.getAll().containsKey(1));
        assert(c.getAll().containsKey(3));
    }

    @Test
    void remove() {
        CakesRepo c = createRepo();
        assert(c.getAll().size()==4);
        Cake c1=new Cake("Chocolate",66.6,1);
        c.remove(c1);
        assert(c.getAll().size()==3);
        assert(!c.getAll().containsKey(1));

        Cake c5=new Cake("Carrot",16.7,9);
        try {
            c.remove(c5);
        }catch (Exception e){assert(true);} //e5 not found in c
    }

    @Test
    void findById() {
        CakesRepo c = createRepo();
        Cake c1=new Cake("Chocolate",66.6,1);
        assertEquals(c1,c.findById(1));
        assertEquals(1,c.findById(1).getId());
        try {
            c.findById(2);
        }catch (RuntimeException e){assert(true);}//id 2 not found in c
    }

    @Test
    void update() {
        CakesRepo c = createRepo();
        Cake c1=new Cake("Chocolate",66.6,1);
        Cake nc=new Cake("Lemon",2,4);
        c.update(1,nc);
        assert (!c.getAll().containsValue(c1));
        assert (c.getAll().containsKey(1));
        assert (!c.getAll().containsKey(4)); //id doesn't update
        assertEquals("Lemon",c.findById(1).getFlavour());
        assertEquals(2,c.findById(1).getPrice());
    }

    @Test
    void findCake() {
        CakesRepo c = createRepo();
        assertEquals(1,c.findCake("Chocolate",66.6).getId()); //c1
        try {
            c.findCake("Lemon",2);
        }catch (RuntimeException e){assert (true);}
    }
}