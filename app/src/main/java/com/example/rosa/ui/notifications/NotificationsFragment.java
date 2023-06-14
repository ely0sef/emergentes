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

public class NotificationsFragment extends Fragment {
    private static final String CONNECTION_STRING = "HostName=rbpecera.azure-devices.net;DeviceId=picow;SharedAccessKey=ox92G0pJ9Nju26+psgelqWfxtEOqpF0LN/SlKH7bNoM=";
    private static final String DEVICE_ID = "android-device";

    private DeviceClient deviceClient;
    private EditText txtTemp;
    private DeviceClient client;

    private Handler handler;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        txtTemp = root.findViewById(R.id.edit_max_temperature);

        handler = new Handler(Looper.getMainLooper());
        connectToIoTHub();

        return root;
    }
    private void connectToIoTHub() {
        try {
            deviceClient = new DeviceClient(CONNECTION_STRING, IotHubClientProtocol.MQTT);
            deviceClient.registerConnectionStatusChangeCallback(new IotHubConnectionStatusChangeCallback() {
                @Override
                public void execute(IotHubConnectionStatus status, IotHubConnectionStatusChangeReason reason, Throwable throwable, Object callbackContext) {
                    // Manejar cambios de estado de conexión
                }
            }, null);

            deviceClient.open();

            deviceClient.setMessageCallback(new MessageCallback() {
                @Override
                public IotHubMessageResult execute(Message message, Object callbackContext) {
                    // Se recibió un mensaje de IoT Hub
                    String payload = new String(message.getBytes(), Message.DEFAULT_IOTHUB_MESSAGE_CHARSET);
                    Log.d("AzureIoT", "Mensaje recibido: " + payload);
                    txtTemp.setText(payload);
                    // Realizar las acciones necesarias con el mensaje recibido

                    return IotHubMessageResult.COMPLETE;
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
