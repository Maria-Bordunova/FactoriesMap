package marybord.com.persistence;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


@Database(entities = {Factory.class}, version = 1)
public abstract class FactoryDatabase extends RoomDatabase {
    private static volatile FactoryDatabase INSTANCE;

    public abstract FactoryDao factoryDao();

    public static FactoryDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (FactoryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FactoryDatabase.class, "Factories.db")
                            .fallbackToDestructiveMigration()
                            .addCallback(populateDatabase)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback populateDatabase = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            AsyncTask.execute(() -> {
                final CompositeDisposable compositeDisposable = new CompositeDisposable();
                List<Factory> factories = new ArrayList<>();
                factories.add(new Factory("Kloeckner Pentaplast Rus", "Russia, St. Petersburg, Irinovsky pr. 1", 1));
                factories.add(new Factory("Maria Soell GmbH", "Deutschland, Frankenstraße 45, 63667 Nidda", 5));
                factories.add(new Factory("Kloeckner Pentaplast Pol", "Bukowice 39, 56-120 Brzeg Dolny, Poland", 7));

                compositeDisposable.add(INSTANCE.factoryDao().insert(factories)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe());
            });
        }
    };
}
