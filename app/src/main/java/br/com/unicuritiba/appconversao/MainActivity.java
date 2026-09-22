package br.com.unicuritiba.appconversao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputKm;
    private TextInputEditText inputMeter;
    private TextInputEditText inputCm;
    private Button buttonConvert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        inputKm = findViewById(R.id.inputKm);
        inputMeter = findViewById(R.id.inputMeter);
        inputCm = findViewById(R.id.inputCm);
        buttonConvert = findViewById(R.id.button);

        buttonConvert.setOnClickListener(v -> {

            double valueKm = Double.parseDouble(
                    inputKm.getText().toString()
            );
            double valueMeter = valueKm*1000;
            double valueCm = valueMeter*100;
            inputMeter.setText(
                    String.valueOf(
                    valueMeter
            ));

            inputCm.setText(
                    String.valueOf(
                            valueCm
                    ));
        });

    }
}
