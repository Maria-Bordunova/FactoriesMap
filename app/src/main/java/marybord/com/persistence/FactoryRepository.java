package marybord.com.persistence;

import android.app.Application;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class FactoryRepository {
    private FactoryDao factoryDao;

    public FactoryRepository(Application application) {
        FactoryDatabase factoryDatabase = FactoryDatabase.getInstance(application);
        factoryDao = factoryDatabase.factoryDao();
    }

    public Flowable<List<Factory>> getAllFactories() {
        return factoryDao.getAllFactories();
    }

    public Completable insertOrUpdate(Factory factory) {
        return factoryDao.insert(factory);
    }

    public Completable delete(Factory factory) {
        return factoryDao.delete(factory);
    }

    public Completable deleteAllFactories() {
        return factoryDao.deleteAllFactories();
    }
}
