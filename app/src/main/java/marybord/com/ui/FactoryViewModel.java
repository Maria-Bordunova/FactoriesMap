package marybord.com.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import marybord.com.persistence.Factory;
import marybord.com.persistence.FactoryRepository;


public class FactoryViewModel extends AndroidViewModel {

    private FactoryRepository repository;

    public FactoryViewModel(@NonNull Application application) {
        super(application);

        repository = new FactoryRepository(application);
    }

    public Flowable<List<Factory>> getAllFactories() {
        return repository.getAllFactories();
    }

    public Completable insertOrUpdate(Factory factory) {
        return repository.insertOrUpdate(factory);
    }

    public Completable delete(Factory factory) {
        return repository.delete(factory);
    }

    public Completable deleteAllFactories() {
        return repository.deleteAllFactories();
    }


}
