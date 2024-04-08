package domain;

import java.io.Serializable;

public class Entity implements Serializable {
    protected int id;

    public Entity(){id=-1;}
    public Entity(int id) {
        this.id = id;
    }

    public Entity(Entity another){
        this.id=another.id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                '}';
    }
}
