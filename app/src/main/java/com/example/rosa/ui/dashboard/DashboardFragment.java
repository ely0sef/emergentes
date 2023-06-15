package com.example.rosa.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rosa.R;
import com.example.rosa.databinding.FragmentDashboardBinding;
import com.example.rosa.ui.notifications.Sensor3Fragment;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;

import java.io.IOException;

public class DashboardFragment extends Fragment {
    private static final String CONNECTION_STRING = "HostName=rbpecera.azure-devices.net;DeviceId=picow;SharedAccessKey=ox92G0pJ9Nju26+psgelqWfxtEOqpF0LN/SlKH7bNoM=";
    private static final String DEVICE_ID = "picow";

    private DeviceClient deviceClient;
    private EditText txtTemp;
    private Handler handler;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        txtTemp = root.findViewById(R.id.txtPh);
        handler = new Handler(Looper.getMainLooper());
        connectToDevice();
       // final TextView textView = binding.textDashboard;
       // dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    private void connectToDevice() {
        try {
            deviceClient = new DeviceClient(CONNECTION_STRING, IotHubClientProtocol.MQTT);
            deviceClient.registerConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallback() {
                public void execute(IotHubConnectionStatus status, IotHubConnectionStatusChangeReason reason, Throwable throwable, Object callbackContext) {
                    if (status == IotHubConnectionStatus.DISCONNECTED) {
                        Log.e("DeviceClient", "Desconectado. Se intentará reconectar...");
                        try {
                            deviceClient.open();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }, null);

            deviceClient.open();

            deviceClient.setMessageCallback(new DashboardFragment.MessageCallback(), null);

            Log.i("DeviceClient", "Conexión establecida. Esperando mensajes...");
        } catch (Exception e) {
            Log.e("DeviceClient", "Error al conectarse al dispositivo: " + e.getMessage());
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private class MessageCallback implements com.microsoft.azure.sdk.iot.device.MessageCallback {
        public IotHubMessageResult execute(Message msg, Object context) {
            final String messageData = new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET);

            handler.post(new Runnable() {
                public void run() {
                    Log.i("DeviceClient", "Mensaje recibido: " + messageData);
                    txtTemp.setText(messageData + " PH");
                    try {
                        deviceClient.closeNow();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            return IotHubMessageResult.COMPLETE;
        }
    }
}