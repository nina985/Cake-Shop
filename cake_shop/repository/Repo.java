package repository;

import java.util.Map;

public interface Repo<ID,T> {
    Map<ID,T> getAll();
    void add(T c);
    void remove(T c);
    T findById(ID i)throws Exception;
    void update(ID i,T c);
}
