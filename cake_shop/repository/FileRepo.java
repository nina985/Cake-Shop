package repository;
import domain.Entity;

public abstract class FileRepo <ID extends java.lang.Integer, T extends Entity> extends MemoryRepo<ID,T>
{
    protected String filename;
    public FileRepo(String filename) {
        this.filename = filename;
        //readFromFile();  IN SUBCLASS
    }

    protected abstract void readFromFile();
    protected abstract void writeToFile();
    @Override
    public void add(T elem) throws RuntimeException {
        super.add(elem);
        writeToFile();
    }
    @Override
    public void remove(T elem){
        super.remove(elem);
        writeToFile();
    }
    @Override
    public void update(Integer id, T elem){
        super.update(id, elem);
        writeToFile();
    }
}
