package com.github.obdjava;

//OBD Imports
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;

//Bluetooth Imports
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.TextView;

//Other Imports
import java.io.IOException;
import java.util.UUID;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    //Variables
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public TextView tvSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Variables
        tvSpeed = findViewById(R.id.tvSpeed);

        String deviceAddress = "0C:1D:A5:68:98:8C";
        final SpeedCommand speedCommand = new SpeedCommand();
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        final BluetoothSocket[] socket = {null};

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //Pair To OBD
                    socket[0] = device.createInsecureRfcommSocketToServiceRecord(uuid);
                } catch (IOException ignored) {}
                try {
                    //Initialize OBD
                    new EchoOffCommand().run(socket[0].getInputStream(), socket[0].getOutputStream());
                    new LineFeedOffCommand().run(socket[0].getInputStream(), socket[0].getOutputStream());
                    new SelectProtocolCommand(ObdProtocols.AUTO).run(socket[0].getInputStream(), socket[0].getOutputStream());
                } catch (IOException | InterruptedException ignored) {}

                //While thread is running
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        //Get SpeedCommand
                        new SpeedCommand().run(socket[0].getInputStream(), socket[0].getOutputStream());
                    } catch (IOException | InterruptedException ignored) {}

                    //Display SppedCommand Result As TextView
                    tvSpeed.setText(speedCommand.getFormattedResult());
                }
            }
        });
    }
}
