package domain;

public class Order extends Entity
{
    private String customer;
    private boolean status;
    protected Cake cake;

    public Order(Cake c,String customer, boolean status, int id) {
        super(id);
        this.cake=c;
        this.customer = customer;
        this.status = status;
    }

    public Order(Order another)  //copy constructor
    {
        super(another.getId());
        this.cake=another.cake;
        this.customer = another.customer;
        this.status = another.status;
    }

    public Cake getCake() {
        return cake;
    }
    public String getCustomer() {
        return customer;
    }
    public boolean getStatus() {
        return status;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setCake(Cake cake) {
        this.cake = cake;
    }

    @Override
    public String toString() {
        String st;
        if(status)
            st="finished";
        else
            st="work in progress";

        return "Order no."+getId() +": "+ '\n' +
                "- Type: " + cake.getFlavour()+ '\n' +
                "- Customer: " + customer + '\n' +
                "- Price: $" + cake.getPrice() + "\n"+
                "- Status: " + st + '\n';

    }

    public boolean equals(Object o)
    {
        if (!(o instanceof Order c))
            return false;
        return c.customer.equals(this.customer) && c.status == this.status &&
                c.cake.equals(this.cake) && c.getId() == this.getId();
    }
}
