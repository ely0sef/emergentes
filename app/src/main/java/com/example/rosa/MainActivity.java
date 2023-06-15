package com.example.rosa;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rosa.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "fake_notification_channel";
    private static final int NOTIFICATION_ID = 1;

    private EditText editLitros;
    private TextView textTemperatura;
    private TextView textPh;
    private TextView textPpm;
    private ActivityMainBinding binding;
    private Timer timer;

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
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_sensor3)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        createNotificationChannel();

        // Iniciar el temporizador para mostrar la notificación cada minuto
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                showFakeNotification();
            }
        }, 0, 60000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Detener el temporizador cuando se destruye la actividad
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showFakeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Cuidado")
                .setContentText("La temperatura supero los 25 grados")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // Agregar esta línea para que la notificación se cierre automáticamente al hacer clic en ella

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
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
