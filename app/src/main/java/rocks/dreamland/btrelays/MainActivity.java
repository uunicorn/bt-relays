package rocks.dreamland.btrelays;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    BluetoothSocket sock;
    OutputStreamWriter writer;
    BufferedReader reader;

    int lastOut = 0;

    private String in() throws IOException {
        String s = reader.readLine();

        Log.e("relays", "<< " + s);

        if(s.equals(""))
            return in();
        else
            return s;
    }

    private void out(String s) throws IOException {
        Log.e("relays", ">> " + s);
        writer.write(s);
        writer.flush();
        in();
    }

    private void out(int o) throws IOException {
        if(lastOut == o)
            return;

        lastOut = o;
        out("AT+PIN=" + o + "\r");
    }

    private void connect() {
        String remoteAddress = "20:13:11:15:46:63";
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(remoteAddress);
        UUID uuid = UUID.fromString("5560049f-3b64-45ce-9707-be9680c7b8db");
        try {
            sock = device.createRfcommSocketToServiceRecord(uuid);
            Log.e("relays", "connecting...");
            sock.connect();
            Log.e("relays", "connected!");
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            writer = new OutputStreamWriter(sock.getOutputStream());
            String line = in();

            if(line.equals("+HELLO")) {
                writer.write("AT+DIR=12\r");
                writer.flush();
            }
        } catch (IOException e) {
            Log.e("relays", "can't get a sock" , e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connect();
    }

    @Override
    protected void onDestroy() {
        Log.e("relays", "destroy..." );
        try {
            sock.close();
        } catch (IOException e) {
            Log.e("relays", "can't close a sock" , e);
        }
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            try {
                out(lastOut | 4);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            try {
                out(lastOut | 8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            try {
                out(lastOut & ~4);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            try {
                out(lastOut & ~8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }
}
