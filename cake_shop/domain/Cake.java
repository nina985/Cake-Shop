package domain;

import java.util.Objects;

public class Cake extends Entity{
    protected String  flavour;
    protected double price;

    public Cake(){super();flavour="";price=-1;}
    public Cake(String flavour, double price, int id) {
        super(id);
        this.flavour = flavour;
        this.price = price;
    }

    public Cake(Cake another)  //copy constructor
    {
        super(another.getId());
        this.flavour = another.flavour;
        this.price = another.price;
    }

    public String getFlavour() {
        return flavour;
    }

    public double getPrice() {
        return price;
    }

    public void setFlavour(String flavour) {
        this.flavour = flavour;
    }

    public void setPrice(double price) {
        if(price<0)
            throw new RuntimeException("Price can't be negative");
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cake cake = (Cake) o;
        return Double.compare(price, cake.price) == 0 && Objects.equals(flavour, cake.flavour); //does not compare id
    }

    @Override
    public String toString() {
        return "Cake no."+getId() +": "+ '\n' +
                "- Type: " + flavour+ '\n' +
                "- Price: $" + price + "\n";
    }
}
