package repository;
import domain.Entity;

import java.util.Map;
import java.util.TreeMap;

public class MemoryRepo<ID extends java.lang.Integer, T extends Entity> implements Repo<Integer,T> {
    protected Map<Integer, T> repo;
    public MemoryRepo() {
        this.repo = new TreeMap<>();
    }
    public Map<Integer, T> getAll() {
        return this.repo;
    }

    public void add(T c) {
        repo.put(c.getId(),c);
    }
    public void remove(T c){
        repo.remove(c.getId());
    }

    public T findById(Integer i) {
        if(repo.containsKey(i))
        {
            return repo.get(i);
        }
        throw new RuntimeException("No entities of id "+i+".");
    }
    public void update(Integer i, T c) {
        repo.replace(i,c);
    }
}
