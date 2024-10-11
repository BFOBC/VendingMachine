package com.example.vendingmachineinventorymanagement.testvm;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ReadThread extends Thread {
    private static final String TAG = "VmConfigurationRepoImp";
    private String result; // Variable to hold the result
    private InputStream mInputStream; // Input stream (make sure to initialize it)

    @Override
    public void run() {
        super.run();
        while (!isInterrupted()) {
            int size;
            try {
                byte[] buffer = new byte[64];
                if (mInputStream == null) return;
                size = mInputStream.read(buffer);
                if (size > 0) {
                    String hexString = bytesToHex(Arrays.copyOfRange(buffer, 0, size));
                    Log.d(TAG, "data: " + hexString);

                    switch (hexString) {
                        case "020502000000000306":
                            result = "Shipment successful";
                            break;
                        case "020502000001010306":
                            result = "Unlock completely when locking";
                            break;
                        case "020502000002020306":
                            result = "Door not closed completely when locking";
                            break;
                        case "020502000003030306":
                            result = "Motor current is heavier";
                            break;
                        // Add more cases as needed
                        default:
                            result = null; // Return null if none of the conditions match
                    }

                    // appendDataToInternalStorage("SlaveMachine.txt", "data : " + hexString + " : result : " + result);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public String getResult() {
        return result; // Return the result
    }

    private final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
