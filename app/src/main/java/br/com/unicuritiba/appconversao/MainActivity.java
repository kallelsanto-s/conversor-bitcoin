package br.com.unicuritiba.appconversao;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText inputBtc;
    private TextInputEditText inputReais;
    private Button buttonConvert;

    // Cotação de fallback caso a requisição falhe ou esteja offline
    private double cotacaoBtcEmReais = 350000.0;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste de WindowInsets para respeitar o recorte da câmara do S26 Ultra
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialização de componentes
        inputBtc = findViewById(R.id.inputBtc);
        inputReais = findViewById(R.id.inputReais);
        buttonConvert = findViewById(R.id.button);

        // Busca a cotação real do mercado assim que o aplicativo abre
        buscarCotacaoEmTempoReal();

        // Clique no botão de conversão com animação
        buttonConvert.setOnClickListener(v -> {
            // Efeito visual de clique no botão
            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
            }).start();

            converterValores();
        });
    }

    private void buscarCotacaoEmTempoReal() {
        executor.execute(() -> {
            try {
                URL url = new URL("https://economia.awesomeapi.com.br/json/last/BTC-BRL");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                if (conn.getResponseCode() == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONObject btcObj = jsonObject.getJSONObject("BTCBRL");
                    double valorAtual = btcObj.getDouble("bid");

                    if (valorAtual > 0) {
                        cotacaoBtcEmReais = valorAtual;
                        mainHandler.post(() ->
                                Snackbar.make(findViewById(R.id.main),
                                        "Cotação em tempo real carregada!", Snackbar.LENGTH_SHORT).show()
                        );
                    }
                }
            } catch (Exception e) {
                // Em caso de offline, mantém o valor padrão de fallback
            }
        });
    }

    private void converterValores() {
        String btcText = inputBtc.getText() != null ? inputBtc.getText().toString().trim() : "";
        String reaisText = inputReais.getText() != null ? inputReais.getText().toString().trim() : "";

        try {
            if (!btcText.isEmpty()) {
                double valueBtc = Double.parseDouble(btcText.replace(",", "."));
                double valueReais = valueBtc * cotacaoBtcEmReais;

                // FORMATAÇÃO EM MOEDA BRASILEIRA (ex: R$ 3.000.000,00)
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                inputReais.setText(currencyFormat.format(valueReais));

                animarAparecimento(inputReais);

            } else if (!reaisText.isEmpty()) {
                // Limpa o texto caso o utilizador cole algo com "R$" ou símbolos antes de converter
                String limpo = reaisText.replaceAll("[^\\d,.]", "").replace(",", ".");
                double valueReais = Double.parseDouble(limpo);
                double valueBtc = valueReais / cotacaoBtcEmReais;

                // Exibe o Bitcoin com até 8 casas decimais
                inputBtc.setText(String.format(Locale.US, "%.8f", valueBtc));

                animarAparecimento(inputBtc);

            } else {
                Snackbar.make(findViewById(R.id.main), "Digite um valor em BTC ou R$!", Snackbar.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Snackbar.make(findViewById(R.id.main), "Insira um número válido.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void animarAparecimento(View view) {
        view.setAlpha(0.2f);
        view.setScaleX(0.9f);
        view.setScaleY(0.9f);

        view.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(250)
                .start();
    }
}