package marybord.com.persistence;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface FactoryDao {

    @Query("SELECT * FROM Factories ORDER BY name")
    Flowable<List<Factory>> getAllFactories();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Factory factory);

    @Insert
    Completable insert(List<Factory> factories);

    @Delete
    Completable delete(Factory factory);

    @Query("DELETE FROM Factories")
    Completable deleteAllFactories();
}
