package com.example.vendingmachineinventorymanagement.testvm;

import android.content.Context;
import android.serialport.SerialPort;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialUtils {
    private static final String SLAVE_VENDING_MACHINE_SERIAL_PORT = "/dev/ttyS1";
    private SerialPort mSerialPort = null;
    private final int baudrate = 57600;
    private ReadThread mReadThread = null;

    // Output and Input streams
    private static OutputStream mOutputStream = null;
    private static InputStream mInputStream = null;

    public String vendItem(String slotNumber) {
        try {
            // Convert slot number to hexadecimal
            String convertSlot = Integer.toHexString(Integer.parseInt(slotNumber) % 100).toUpperCase();
            byte[] vendCommand = new byte[]{
                    0x02, 0x06, 0x02, 0x00, (byte) Integer.parseInt(convertSlot, 16),
                    0x00, 0x00, (byte) Integer.parseInt(convertSlot, 16), 0x03, 0x05
            };
            mOutputStream.write(vendCommand);
            Thread.sleep(10000); // Use Thread.sleep instead of delay from Kotlin coroutines

        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return mReadThread != null ? mReadThread.getResult() : null;
    }

    public boolean openAndConnectPort(Context context) {
        try {
            boolean result = openAndConnectPort(SLAVE_VENDING_MACHINE_SERIAL_PORT);
            Toast.makeText(context, "Connection: " + result, Toast.LENGTH_SHORT).show();
            return result;
        } catch (Exception exception) {
            Toast.makeText(context, "Exception: " + exception, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean openAndConnectPort(String serialPort) {
        try {
            mSerialPort = new SerialPort(new File(serialPort), baudrate);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            mReadThread = new ReadThread();
            mReadThread.start();
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public boolean closePort() {
        try {
            if (mSerialPort != null) {
                mSerialPort.close();
                mSerialPort = null;
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return false;
    }

    public void vendItem(byte[] data) {
        try {
            if (mOutputStream != null) {
                mOutputStream.write(data);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
