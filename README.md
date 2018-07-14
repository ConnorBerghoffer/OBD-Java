# OBD-Java
Bluetooth On Board Diagnostics

## Notice
**When making an app of my own, i found that there are not many resources online to help people when making an OBD app, so I created this to help newcomers with Bluetooth OBD!** <br><br>
This project includes the [obd-java-api](https://www.elmelectronics.com/help/obd/tips/#UnderstandingOBD) by Pires, I reccommend you check this out before starting.

- [Understanding OBD](https://www.elmelectronics.com/help/obd/tips/#UnderstandingOBD)
- [The ELM327](https://www.elmelectronics.com/help/obd/tips/#327_Commands)

## Setup
### 1. Create Project
- I used API 19 for this project but anything newer should work <br>
- Create project with empty class MainActivity.java <br>

### 2. Gradle
Add the following to your **build.gradle** file. 
```
dependencies {
    //OBD-Java-Api
    implementation 'com.github.pires:obd-java-api:1.0'
}
```
### 3. Permissions
Add the following to your **AndroidManifest.xml** file.
```
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```
### 4. Imports
Add this as an import in your **MainActivity.java**
```
//OBD Imports
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;

import com.github.pires.obd.commands.[Command];

//Bluetooth Imports
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

//Other Imports
import android.widget.TextView;
import java.util.UUID;
import android.os.AsyncTask;
```
Replace ```[Command]``` with the command you want to use, in this example I will use **SpeedCommand** <br><br>
**Other commands include:**
- OilTempCommand
- RPMCommand
- RuntimeCommand
- ThrottlePositionCommand
- AirFuelRatioCommand
- FuelLevelCommand
- FuelPressureCommand
- AirIntakeTemperatureCommand
- AmbientAirTemperatureCommand
- EngineCoolantTemperatureCommand
<br>
There are many more but these are the ones I have tested.

### 5. Adding TextView & Bluetooth
Add a TextView element to your **main_activity.xml** <br>
In this case i will make the id **tvSpeed**. <br> <br>
```
<TextView
android:id="@+id/tvSpeed"
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:textSize="20sp" />
```
Now, add the variable to your **MainActivity.java** and link it using **findViewById**
```
public class MainActivity {
BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

public TextView tvSpeed;
}

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    tvSpeed = findViewById(R.id.tvSpeed);
```
### 6. Initialize Your OBD And Bluetooth
In your **OnCreate** Method, add
```final SpeedCommand speedCommand = new SpeedCommand();```
to get your SpeedCommand ready for data transmit and receive. <br>
Next, type ```String deviceAddress = "[Address]";``` and add your devices address. <br>
When this is done, get your app ready for pairing using this: <br>
```
final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
final BluetoothSocket[] socket = {null};
```
### 7. Connect to Bluetooth
Now we have to connect to your OBD and read the data from it. <br> <br>

**WARNING** this process is fairly CPU intensive,so we will have to wrap it in <br>
```
AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
            //Code Here
            }
        });
```
to run it on a seperate thread. <br><br>

Connect to bluetooth OBD. (Make sure you surround with Try/Catch) <br>
```
try {
socket[0] = device.createInsecureRfcommSocketToServiceRecord(uuid);
} catch (IOException ignored) {}
```
<br><br>
### 8. Initialize your OBD:
Setup your OBD using this. (Make sure you surround with Try/Catch)<br>
```
try {
new EchoOffCommand().run(socket[0].getInputStream(), socket[0].getOutputStream());
new LineFeedOffCommand().run(socket[0].getInputStream(), socket[0].getOutputStream());
new SelectProtocolCommand(ObdProtocols.AUTO).run(socket[0].getInputStream(), socket[0].getOutputStream());
} catch (IOException | InterruptedException ignored) {}
```
Now create and display the result into the TextView we created earlier.<br>
```
//While thread is running
while (!Thread.currentThread().isInterrupted()) {
try {
new SpeedCommand().run(socket[0].getInputStream(), socket[0].getOutputStream());
} catch (IOException | InterruptedException ignored) {}

tvSpeed.setText(speedCommand.getFormattedResult());
}
```
# And We're Done!
## Final Words...
- If bluetooth is not enabled **This Will Not Work**. Let me know if you would like the app to auto enable bluetooth on launch.
- If you are not close to the bluetooth OBD, or the bluetooth OBD is not on **This Will Not Work**. Pleas make sure your OBD is connected and the car is running.
- You might have to check if your app has the correct permissions to use your phones bluetooth. If the app crashes, then check your permissions.

# Example Code:
## build.gradle
```
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:27.1.1'
    implementation 'com.android.support.constraint:constraint-layout:1.1.2'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    
    //OBD-Java-Api
    implementation 'com.github.pires:obd-java-api:1.0'
}
```
## AndroidManifest.xml
```
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
```
## activity_main.xml
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
    android:id="@+id/tvSpeed"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:textSize="20sp" />

</RelativeLayout>
```
## MainActivity.java
```
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

```
