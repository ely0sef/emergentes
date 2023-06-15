package com.example.rosa.ui.notifications;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.rosa.R;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeCallback;
import com.microsoft.azure.sdk.iot.device.IotHubConnectionStatusChangeReason;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.Message;
import com.microsoft.azure.sdk.iot.device.MessageCallback;
import com.microsoft.azure.sdk.iot.device.transport.IotHubConnectionStatus;

import java.io.IOException;

public class NotificationsFragment extends Fragment {
    private static final String CONNECTION_STRING = "HostName=rbpecera.azure-devices.net;DeviceId=picow;SharedAccessKey=ox92G0pJ9Nju26+psgelqWfxtEOqpF0LN/SlKH7bNoM=";
    private static final String DEVICE_ID = "picow";

    private DeviceClient deviceClient;
    private EditText txtTemp;

    private Handler handler;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        txtTemp = root.findViewById(R.id.edit_max_temperature);
        handler = new Handler(Looper.getMainLooper());

        connectToDevice();

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

            deviceClient.setMessageCallback(new MessageCallback(), null);

            Log.i("DeviceClient", "Conexión establecida. Esperando mensajes...");
        } catch (Exception e) {
            Log.e("DeviceClient", "Error al conectarse al dispositivo: " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deviceClient != null) {
            try {
                deviceClient.closeNow();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class MessageCallback implements com.microsoft.azure.sdk.iot.device.MessageCallback {
        public IotHubMessageResult execute(Message msg, Object context) {
            final String messageData = new String(msg.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET);

            handler.post(new Runnable() {
                public void run() {
                    Log.i("DeviceClient", "Mensaje recibido: " + messageData);
                    txtTemp.setText(messageData);
                }
            });

            return IotHubMessageResult.COMPLETE;
        }
    }
}
