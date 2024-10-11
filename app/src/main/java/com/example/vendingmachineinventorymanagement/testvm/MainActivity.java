package com.example.vendingmachineinventorymanagement.testvm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.serialport.SerialPort;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vendingmachineinventorymanagement.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ListAdapter listAdapter;
    Button btnConnect,btnDeliverProduct;
    Thread mThread;
    SerialPort serialPort;
    String devPath;
    int baudrate;
    int no = 0;
    byte[] ackBytes = new byte[]{(byte) 0xFA,(byte)0xFB,0x42,0x00,0x43};
    private ByteArrayOutputStream mBuffer = new ByteArrayOutputStream();
    Queue<byte[]> queue =  new LinkedList<byte[]>();

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView1);
        listAdapter = new ListAdapter();
        listAdapter.setContext(this);
        listView.setAdapter(listAdapter);

        SerialUtils su = new SerialUtils();
        su.openAndConnectPort(MainActivity.this);
        // Example usage of calling openAndConnectPort method
      //  boolean result = serialUtils.openAndConnectPort(this);
        btnConnect= findViewById(R.id.connect);
        btnDeliverProduct= findViewById(R.id.driverhd);

        btnConnect.setOnClickListener(view -> {
            su.openAndConnectPort(MainActivity.this);
        });
        btnDeliverProduct.setOnClickListener(view -> {
            try {
                int hdh = Integer.parseInt(((EditText) findViewById(R.id.hdh)).getText().toString());
                short[] hdhbyte = HexDataHelper.Int2Short16_2(hdh);

                // Pad lane number to two bytes
                if(hdhbyte.length == 1)
                {
                    short temp = hdhbyte[0];
                    hdhbyte = new short[2];
                    hdhbyte[0]= 0;
                    hdhbyte[1] = temp;
                }


                byte[] data  = new byte[]{(byte) 0xFA, (byte) 0xFB, 0x06, 0x05, (byte) getNextNo(), 0x01, 0x00, (byte) hdhbyte[0], (byte) hdhbyte[1], 0x00};
                data[data.length - 1] = (byte) HexDataHelper.computerXor(data, 0, data.length - 1);
              //  writeCmd(data);
                su.vendItem(data);
            }
            catch (Exception e)
            {

            }
        });
    }
    public int getNextNo(){
        no++;
        if(no>=255){
            no=0;
        }
        return no;
    }

    public void onSerialPortConnectStateChanged(boolean connected){
        if(connected){
            ((Button)findViewById(R.id.connect)).setText("Disconnect");
        }
        else{
            ((Button)findViewById(R.id.connect)).setText("Connect");
        }
    }
    private void  bindSerialPort()
    {
        devPath = ((EditText)findViewById(R.id.dev)).getText().toString();
        baudrate = Integer.parseInt(((EditText)findViewById(R.id.baudrate)).getText().toString());
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File serialFile = new File(devPath);
                    if (!serialFile.exists() || baudrate == -1) {
                        return;
                    }

                    try {
                        serialPort = new SerialPort(serialFile, baudrate);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onSerialPortConnectStateChanged(true);
                            }
                        });
                        readSerialPortData();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                finally
                {
                    if(null!=serialPort){
                        try
                        {
                            serialPort.close();
                            serialPort = null;
                        }
                        catch (Exception e) {
                        }
                    }

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onSerialPortConnectStateChanged(false);
                    }
                });
            }
        });
        mThread.start();
    }
    private void readSerialPortData()
    {
        while (true)
        {
            try{
                if(null == serialPort){
                    Thread.sleep(1000);
                    continue;
                }
                int available = serialPort.getInputStream().available();
                if(0 == available){
                    Thread.sleep(10);
                    continue;
                }

                byte[] data = readBytes(serialPort.getInputStream(),available);
                mBuffer.write(data);
                while(true) {
                    byte[] bytes = mBuffer.toByteArray();
                    int start = 0;
                    int cmdCount = 0;
                    boolean shuldBreak = false;
                    for(; start<= bytes.length-5; start++)
                    {
                        if((short) (bytes[start] & 0xff)==0xFA&&(short) (bytes[start+1] & 0xff)==0xFB) {
                            try {
                                int len = bytes[start + 3];
                                byte[] cmd = new byte[len + 5];
                                System.arraycopy(bytes, start, cmd, 0, cmd.length);
                                cmdCount++;
                                proccessCmd(cmd);


                                // Calculate how many bytes are left to parse. If none, break; otherwise, continue processing
                                int remain = bytes.length - start - cmd.length;
                                if(0 == remain)
                                {
                                    shuldBreak = true;
                                    mBuffer.reset();
                                    break;
                                }
                                byte[] buffer2 = new byte[remain];
                                System.arraycopy(bytes, start + cmd.length, buffer2, 0, buffer2.length);
                                mBuffer.reset();
                                mBuffer.write(buffer2);
                            }
                            catch (Exception e)
                            {
                                shuldBreak = true;
                                // Skip due to an incomplete packet causing an out-of-bounds exception
                            }
                            break;
                        }
                    }
                    if(0==cmdCount||shuldBreak)
                    {
                        break;
                    }

                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public byte[] readBytes(InputStream stream, int length) throws IOException {
        byte[] buffer = new byte[length];

        int total = 0;

        while (total < length) {
            int count =stream.read(buffer, total, length - total);
            if (count == -1) {
                break;
            }
            total += count;
        }

        if (total != length) {
            throw new IOException(String.format("Read wrong number of bytes. Got: %s, Expected: %s.", total, length));
        }

        return buffer;
    }
    public void writeCmd(byte[] cmd)
    {
        try{
            serialPort.getOutputStream().write(cmd);
            serialPort.getOutputStream().flush();
            addText(">> "+ HexDataHelper.hex2String(cmd));
        }
        catch (Exception e)
        {

        }
    }
    public void proccessCmd(byte[] cmd)
    {
        addText("<<"+ HexDataHelper.hex2String(cmd));

        if(0x41==(short) (cmd[2] & 0xff)){
            // Received POLL packet

            if(queue.size()==0){
                writeCmd(ackBytes);
            }
            else{
                writeCmd(queue.poll());
            }
        }
        else  if(0x42==(short) (cmd[2] & 0xff)){
            // Received ACK
        }
        else{
            writeCmd(ackBytes);
        }
    }
    public void addText(String text){
        handler.post(new RunableEx(text) {
            public void run() {
                listAdapter.addText(data);
            }
        });

    }
}
