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
import com.github.pires.obd.commands.[Command];

//Bluetooth Imports
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
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
```AsyncTask.execute(new Runnable() {```**[Code Here]**```}``` <br>
to run it on a seperate thread. <br><br>

Connect to bluetooth OBD <br>
```socket[0] = device.createInsecureRfcommSocketToServiceRecord(uuid);```
<br><br>
### 8. Initialize your OBD:
Setup your OBD using this. <br>
```
new EchoOffCommand().run(socket[0].getInputStream(), socket[0].getOutputStream());
new LineFeedOffCommand().run(socket[0].getInputStream(), socket[0].getOutputStream());
new SelectProtocolCommand(ObdProtocols.AUTO).run(socket[0].getInputStream(), socket[0].getOutputStream());
```
And display the result into the TextView we created earlier.<br>
```tvSpeed.setText(speedCommand.getFormattedResult());```
## And We're Done!
