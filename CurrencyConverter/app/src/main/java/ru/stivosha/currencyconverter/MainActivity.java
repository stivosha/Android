package ru.stivosha.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String apiKey = "f8054c8e5aa929b429be";
    private Currency from, to;
    private CurrencyConverter converter;
    private boolean isActualCoefficient;
    private double actualCoefficient;
    private EditText editTextConvert1;
    private EditText editTextConvert2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        converter = new CurrencyConverter(apiKey);
        initializationSpinners();
        initializationEditText();
    }

    private void initializationSpinners(){
        Spinner spinner = findViewById(R.id.spinner_1);
        Spinner spinner2 = findViewById(R.id.spinner_2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.currency_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter2);

        int usdPosition = adapter.getPosition("USD");
        spinner.setSelection(usdPosition);

        from = Currency.valueOfCurrency("RUS");
        to = Currency.valueOfCurrency("USD");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isActualCoefficient = false;
                from = Currency.valueOfCurrency(adapterView.getItemAtPosition(i).toString());
                refreshCoefficient(converter, from, to, editTextConvert2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isActualCoefficient = false;
                to = Currency.valueOfCurrency(adapterView.getItemAtPosition(i).toString());
                refreshCoefficient(converter, from, to, editTextConvert2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initializationEditText(){
        editTextConvert1 = findViewById(R.id.edit_text_convert1);
        editTextConvert2 = findViewById(R.id.edit_text_convert2);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String dollarRate = converter.convert(Currency.USD, Currency.RUB);
                if(dollarRate.equals("IOException")){
                    toastInUi(getResources().getString(R.string.IOException));
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        editTextConvert2.setHint(dollarRate);
                        updateCoefficient(Double.valueOf(dollarRate));
                    }
                });
            }
        });
        thread.start();
        editTextConvert1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(isActualCoefficient && editTextConvert1.getText().length() != 0){
                    editTextConvert2.setText(String.valueOf(actualCoefficient
                            * Double.valueOf(editTextConvert1.getText().toString())));
                }else if(editTextConvert1.getText().length() == 0){
                    editTextConvert2.setText("");
                    editTextConvert2.setHint(String.valueOf(actualCoefficient));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void toastInUi(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void refreshCoefficient(final CurrencyConverter converter, final Currency from, final Currency to, final EditText editText){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = converter.convert(from, to);
                if(result.equals("IOException")){
                    toastInUi(getResources().getString(R.string.IOException));
                    return;
                }
                updateCoefficient(Double.valueOf(result));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(editTextConvert2.getText().length() == 0)
                            editText.setHint(String.valueOf(actualCoefficient));
                        else
                            editText.setText(String.valueOf(actualCoefficient * Double.valueOf(editTextConvert1.getText().toString())));
                    }
                });
            }
        });
        thread.start();
    }

    private void updateCoefficient(double coefficient){
        actualCoefficient = coefficient;
        isActualCoefficient = true;
    }
}
