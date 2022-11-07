package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class SetCurrency extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private String targetCurrency = "";
    private double exchangeRate = 1.0d;
    private RequestQueue queue;
    private Spinner baseCurrencySpinner;
    private Spinner targetCurrencySpinner;
    private String baseCurrency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_currency);
        queue = Volley.newRequestQueue(this);

        // Receive values passed in intent.
        if(getIntent().hasExtra("BASE_CURRENCY")) {
            baseCurrency = getIntent().getStringExtra("BASE_CURRENCY");
        }
        if(getIntent().hasExtra("TARGET_CURRENCY")) {
            targetCurrency = getIntent().getStringExtra("TARGET_CURRENCY");
        }
        if(getIntent().hasExtra("EXCHANGE_RATE")) {
            exchangeRate = getIntent().getDoubleExtra("EXCHANGE_RATE", 1.0);
            if(!baseCurrency.equals("") && !targetCurrency.equals("")) {
                TextView exchangeRateTextView = (TextView)findViewById(R.id.currentRateTextView);
                String exchangeRateString = getString(R.string.exchangeRatesString, baseCurrency,
                        exchangeRate, targetCurrency);
                exchangeRateTextView.setText(exchangeRateString);
            }
        }

        // Add currency symbols to spinners.
        baseCurrencySpinner = (Spinner)findViewById(R.id.baseCurrencySpinner);
        targetCurrencySpinner = (Spinner)findViewById(R.id.targetCurrencySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currencySymbols, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        baseCurrencySpinner.setAdapter(adapter);
        targetCurrencySpinner.setAdapter(adapter);
        baseCurrencySpinner.setOnItemSelectedListener(this);
        targetCurrencySpinner.setOnItemSelectedListener(this);

        // Set previously selected currencies to the spinners.
        baseCurrencySpinner.setSelection(adapter.getPosition(baseCurrency));
        targetCurrencySpinner.setSelection(adapter.getPosition(targetCurrency));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save state variables.
        savedInstanceState.putString("BASE_CURRENCY", baseCurrency);
        savedInstanceState.putString("TARGET_CURRENCY", targetCurrency);
        savedInstanceState.putDouble("EXCHANGE_RATE", exchangeRate);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance.
        baseCurrency = savedInstanceState.getString("BASE_CURRENCY");
        targetCurrency = savedInstanceState.getString("TARGET_CURRENCY");
        exchangeRate = savedInstanceState.getDouble("EXCHANGE_RATE");
        TextView currentRateTextView = (TextView)findViewById(R.id.currentRateTextView);
        String exchangeRateString = getString(R.string.exchangeRatesString, baseCurrency,
                exchangeRate, targetCurrency);
        currentRateTextView.setText(exchangeRateString);
    }

    public void fetchExchangeRate(View view) {
        // Fetch currency exchange rate from the API.
        String exchangeRateUrl = "https://api.exchangerate.host/latest?base=%s&symbols=%s";
        String url = String.format(exchangeRateUrl, baseCurrency, targetCurrency);
        System.out.println("URL: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, this::parseExchangeRate,
                        error -> Toast.makeText(this, R.string.fetchFailedToast,
                                Toast.LENGTH_SHORT).show());
        queue.add(jsonObjectRequest);
    }

    public void parseExchangeRate(JSONObject response) {
        // Parse the fetched exchange rate.
        try {
            exchangeRate = Double.parseDouble(response.getJSONObject("rates").getString(targetCurrency));
            System.out.println("Exchange rate: " + exchangeRate);
        } catch (JSONException exception) {
            System.out.println(exception.getMessage());
            Toast.makeText(this, R.string.parseFailedToast,
                    Toast.LENGTH_SHORT).show();
        }
        // Write exchange rate to activity.
        TextView currentRateTextView = (TextView)findViewById(R.id.currentRateTextView);
        String currentRate = getString(R.string.exchangeRatesString, baseCurrency, exchangeRate,
                targetCurrency);
        currentRateTextView.setText(currentRate);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        // Check which dropdown was used and set selected value to state variable.
        if(adapterView == baseCurrencySpinner) {
            baseCurrency = adapterView.getItemAtPosition(position).toString();
            System.out.println("baseCurrency = " + baseCurrency);
        }
        else if(adapterView == targetCurrencySpinner){
            targetCurrency = adapterView.getItemAtPosition(position).toString();
            System.out.println("targetCurrency = " + targetCurrency);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Set value if nothing was selected.
        if(adapterView == baseCurrencySpinner)
            baseCurrency = "";
        else
            targetCurrency = "";
    }

    public void showMain(View view) {
        // Go back to main view. Send base and target currencies along with exchange rate with intent.
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("BASE_CURRENCY", baseCurrency);
        intent.putExtra("TARGET_CURRENCY", targetCurrency);
        intent.putExtra("EXCHANGE_RATE", exchangeRate);
        startActivity(intent);
    }
}