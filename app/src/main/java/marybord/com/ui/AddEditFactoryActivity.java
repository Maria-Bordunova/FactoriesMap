package marybord.com.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import marybord.com.R;
import marybord.com.persistence.Factory;

public class AddEditFactoryActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "marybord.com.EXTRA_ID";
    public static final String EXTRA_FACTORY = "marybord.com.EXTRA_FACTORY";
    private static final int MIN_VALUE_LINES = 1;
    private static final int MAX_VALUE_LINES = 10;
    public static final int DEF_VALUE_ID = -1;
    protected static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private EditText editTextName;
    private EditText editTextAddress;
    private NumberPicker numberPickerLinesNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_add_factory);

        editTextName = findViewById(R.id.edit_text_name);
        editTextAddress = findViewById(R.id.edit_text_address);
        numberPickerLinesNumber = findViewById(R.id.number_picker_lines_number);
        initSearchButton();
        numberPickerLinesNumber.setMinValue(MIN_VALUE_LINES);
        numberPickerLinesNumber.setMaxValue(MAX_VALUE_LINES);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        /**
         * If intent has extra "ID", it means that entity has already existed and window was opened for editing
         */
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle(R.string.edit_factory);
            Factory factory = intent.getParcelableExtra(EXTRA_FACTORY);
            editTextName.setText(factory.getName());
            editTextAddress.setText(factory.getAddress());
            numberPickerLinesNumber.setValue(factory.getLinesNumber());
        } else {
            setTitle(R.string.add_factory);
        }
    }

    private void saveFactory() {
        String name = editTextName.getText().toString();
        String address = editTextAddress.getText().toString();
        int linesNumber = numberPickerLinesNumber.getValue();

        // delete gaps at the end and at the beginning of the row
        if (name.trim().isEmpty() || address.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.insert_data), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();

        Factory factory = new Factory(name, address, linesNumber);
        int id = getIntent().getIntExtra(EXTRA_ID, DEF_VALUE_ID);
        if (id != DEF_VALUE_ID) {
            data.putExtra(EXTRA_ID, id);
        }
        data.putExtra(EXTRA_FACTORY, factory);
        setResult(RESULT_OK, data);
        finish();
    }

    private void initSearchButton() {
        findViewById(R.id.button_search).setOnClickListener(view -> {
            Intent intent = new PlaceAutocomplete.IntentBuilder()
                    .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                    .placeOptions(PlaceOptions.builder()
                            .backgroundColor(Color.parseColor("#EEEEEE"))
                            .limit(10)
                            .build(PlaceOptions.MODE_CARDS))
                    .build(AddEditFactoryActivity.this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        });
    }

    // if return true ==>>> It means you want to see the option menu which you have inflated. if return false ==>>> you do not want to show it
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_factory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_factory:
                saveFactory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
            editTextAddress.setText(selectedCarmenFeature.placeName());
        }
    }
}