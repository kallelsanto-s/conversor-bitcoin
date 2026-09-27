package br.com.unicuritiba.appconversao;

import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputBtc;
    private TextInputEditText inputReais;
    private Button buttonConvert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        inputBtc = findViewById(R.id.inputBtc);
        inputReais = findViewById(R.id.inputReais);
        buttonConvert = findViewById(R.id.button);

        buttonConvert.setOnClickListener(v -> {
            String btcText = inputBtc.getText().toString();
            String reaisText = inputReais.getText().toString();

            // Cotação fixa do Bitcoin (ex: 1 BTC = R$ 350.000,00)
            double cotacaoBtc = 350000.0;

            if (!btcText.isEmpty()) {
                double valueBtc = Double.parseDouble(btcText);
                double valueReais = valueBtc * cotacaoBtc;
                inputReais.setText(String.valueOf(valueReais));
            } else if (!reaisText.isEmpty()) {
                double valueReais = Double.parseDouble(reaisText);
                double valueBtc = valueReais / cotacaoBtc;
                inputBtc.setText(String.valueOf(valueBtc));
            }
        });
    }
}