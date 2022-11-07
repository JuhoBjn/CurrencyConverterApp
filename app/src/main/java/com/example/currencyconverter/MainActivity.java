package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private double exchangeRate = 1.0;
    private String baseCurrency = "";
    private String targetCurrency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Receive values passed with the intent.
        if(getIntent().hasExtra("EXCHANGE_RATE")) {
            exchangeRate = getIntent().getDoubleExtra("EXCHANGE_RATE", 1.0);
        }
        if(getIntent().hasExtra("BASE_CURRENCY")) {
            baseCurrency = getIntent().getStringExtra("BASE_CURRENCY");
        }
        if(getIntent().hasExtra("TARGET_CURRENCY")) {
            targetCurrency = getIntent().getStringExtra("TARGET_CURRENCY");
        }

        // Write exchange rate to exchangeRatesTextView.
        // When baseCurrency and targetCurrency are empty, a placeholder text is displayed.
        if(!baseCurrency.equals("") || !targetCurrency.equals("")) {
            TextView exchangeRatesTextView = (TextView) findViewById(R.id.exchangeRatesTextView);
            String exchangeRateString = getString(R.string.exchangeRatesString, baseCurrency,
                    exchangeRate, targetCurrency);
            exchangeRatesTextView.setText(exchangeRateString);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the state of the object.
        TextView resultTextView = (TextView)findViewById(R.id.resultTextView);
        savedInstanceState.putDouble("EXCHANGE_RATE", exchangeRate);
        savedInstanceState.putString("BASE_CURRENCY", baseCurrency);
        savedInstanceState.putString("TARGET_CURRENCY", targetCurrency);
        savedInstanceState.putString("CURRENCY_CONVERSION", resultTextView.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance.
        exchangeRate = savedInstanceState.getDouble("EXCHANGE_RATE", 0.0d);
        baseCurrency = savedInstanceState.getString("BASE_CURRENCY", "");
        targetCurrency = savedInstanceState.getString("TARGET_CURRENCY", "");

        // Set exchange rate TextView.
        TextView exchangeRateTextView = (TextView)findViewById(R.id.exchangeRatesTextView);
        String exchangeRateString = getString(R.string.exchangeRatesString, baseCurrency,
                                                exchangeRate, targetCurrency);
        exchangeRateTextView.setText(exchangeRateString);

        // Set latest currency conversion result to TextView.
        String currencyConversion = savedInstanceState.getString("CURRENCY_CONVERSION");
        TextView resultTextView = (TextView)findViewById(R.id.resultTextView);
        resultTextView.setText(currencyConversion);
    }

    public void convertCurrency(View view) {
        // Convert input amount from base currency to target currency.
        // Write conversion result to TextView.
        EditText amountInput = (EditText)findViewById(R.id.amountInput);
        double inputAmount;
        if(isEmpty(amountInput)) {
            Toast.makeText(this, R.string.enterAmountToast,
                            Toast.LENGTH_SHORT).show();
            return;
        }
        else
            inputAmount = Double.parseDouble(amountInput.getText().toString());
        double result = inputAmount * exchangeRate;
        TextView resultTextView = (TextView)findViewById(R.id.resultTextView);
        String resultString = getString(R.string.resultString, inputAmount, baseCurrency, result,
                                        targetCurrency);
        resultTextView.setText(resultString);
    }

    public void showSetCurrency(View view) {
        // Move to currency selection activity.
        // Send current base and target currencies to set dropdown boxes to previously selected values.
        Intent intent = new Intent(this, SetCurrency.class);
        intent.putExtra("BASE_CURRENCY", baseCurrency);
        intent.putExtra("TARGET_CURRENCY", targetCurrency);
        intent.putExtra("EXCHANGE_RATE", exchangeRate);
        startActivity(intent);
    }

    // Check if amount input EditText is empty.
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}