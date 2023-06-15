package com.example.rosa;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rosa.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private EditText editLitros;
    private TextView textTemperatura;
    private TextView textPh;
    private TextView textPpm;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editLitros = findViewById(R.id.edit_litros);
        textTemperatura = findViewById(R.id.text_temperatura);
        textPh = findViewById(R.id.text_ph);
        textPpm = findViewById(R.id.text_ppm);

        editLitros.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateRecommendations();
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_sensor3)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void calculateRecommendations() {
        String litrosStr = editLitros.getText().toString();
        if (!litrosStr.isEmpty()) {
            double litros = Double.parseDouble(litrosStr);

            // Cálculo de las recomendaciones
            double temperaturaRecomendada = 24.0 + (litros * 0.1);
            double phRecomendado = 6.5 + (litros * 0.05);
            double ppmRecomendados = 150.0 + (litros * 2.0);

            // Mostrar las recomendaciones
            textTemperatura.setText("Temperatura recomendada: " + temperaturaRecomendada);
            textPh.setText("pH recomendado: " + phRecomendado);
            textPpm.setText("PPM recomendados: " + ppmRecomendados);
        } else {
            // Limpiar las recomendaciones si no se ingresó ningún valor
            textTemperatura.setText("Temperatura recomendada");
            textPh.setText("pH recomendado");
            textPpm.setText("PPM recomendados");
        }
    }
}
