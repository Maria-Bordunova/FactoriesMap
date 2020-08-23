package marybord.com.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import marybord.com.R;
import marybord.com.persistence.Factory;

import static marybord.com.ui.AddEditFactoryActivity.DEF_VALUE_ID;
import static marybord.com.ui.AddEditFactoryActivity.EXTRA_FACTORY;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_FACTORY_REQUEST = 1;
    public static final int EDIT_FACTORY_REQUEST = 2;
    private static final String TAG = "MainActivity";

    private FactoryViewModel factoryViewModel;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FactoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_add_factory).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditFactoryActivity.class);
            startActivityForResult(intent, ADD_FACTORY_REQUEST);
        });
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        adapter = new FactoryAdapter();
        recyclerView.setAdapter(adapter);

        factoryViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(FactoryViewModel.class);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                compositeDisposable.add(factoryViewModel.delete(adapter.getFactoryAt(position))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> Toast.makeText(MainActivity.this, getString(R.string.factory_deleted), Toast.LENGTH_SHORT).show(),
                                throwable -> Log.e(TAG, "Unable to delete factory", throwable)));
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(factory -> {
            Intent intent = new Intent(MainActivity.this, AddEditFactoryActivity.class);
            intent.putExtra(AddEditFactoryActivity.EXTRA_ID, factory.getId());
            intent.putExtra(EXTRA_FACTORY, factory);
            startActivityForResult(intent, EDIT_FACTORY_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_FACTORY_REQUEST && resultCode == RESULT_OK) {

            Factory factory = data.getParcelableExtra(EXTRA_FACTORY);

            compositeDisposable.add(factoryViewModel.insertOrUpdate(factory)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> Toast.makeText(MainActivity.this, getString(R.string.factory_saved), Toast.LENGTH_SHORT).show(),
                            throwable -> Log.e(TAG, "Unable to add factory", throwable)));

        } else if (requestCode == EDIT_FACTORY_REQUEST && resultCode == RESULT_OK) {

            int id = data.getIntExtra(AddEditFactoryActivity.EXTRA_ID, DEF_VALUE_ID);
            if (id == DEF_VALUE_ID) {
                Toast.makeText(this, getString(R.string.factory_cant_updated), Toast.LENGTH_SHORT).show();
                return;
            }
            Factory factory = data.getParcelableExtra(EXTRA_FACTORY);
            factory.setId(id);

            compositeDisposable.add(factoryViewModel.insertOrUpdate(factory)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> Toast.makeText(MainActivity.this, getString(R.string.factory_updated), Toast.LENGTH_SHORT).show(),
                            throwable -> Log.e(TAG, "Unable to update factory", throwable)));
        } else {
            Toast.makeText(this, getString(R.string.factory_not_saved), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        compositeDisposable.add(factoryViewModel.getAllFactories()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(factories -> adapter.submitList(factories),
                        throwable -> Log.e(TAG, "Unable to get factories")));
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_factories:
                compositeDisposable.add(factoryViewModel.deleteAllFactories()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> Toast.makeText(MainActivity.this, getString(R.string.factories_deleted), Toast.LENGTH_SHORT).show(),
                                throwable -> Log.e(TAG, "Unable to delete all factories", throwable)));
                break;
            case R.id.show_all_factories:
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}