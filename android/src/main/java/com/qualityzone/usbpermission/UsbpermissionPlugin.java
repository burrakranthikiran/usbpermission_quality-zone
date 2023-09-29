package com.qualityzone.usbpermission;



import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "Usbpermission")
public class UsbpermissionPlugin extends Plugin {
    private static final String ACTION_USB_PERMISSION = "io.ionic.starter.USB_PERMISSION";
    private Usbpermission implementation = new Usbpermission();

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", implementation.echo(value));
        call.resolve(ret);
    }

      @PluginMethod()
      public void getUsbpermission(PluginCall call) {
            Boolean status = printUsb();
            Log.e("9999999999999999999", String.valueOf(status));
            if(status == true){
                JSObject ret = new JSObject();
                ret.put("message", "USB Permission Granted");
                call.resolve(ret);
            }else if(status == true){
                JSObject ret = new JSObject();
                ret.put("message", "USB Permission No Granted");
                call.resolve(ret);
            }
      }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            // YOUR PRINT CODE HERE
                            Log.e("Tesing_Module","Working");
                        }
                    }
                }
            }
        }
    };

//    public void printUsb() {
//        UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(getContext());
//        UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);
//        if (usbConnection != null && usbManager != null) {
//            PendingIntent permissionIntent = PendingIntent.getBroadcast(
//                    getContext(),
//                    0,
//                    new Intent(ACTION_USB_PERMISSION),
//                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
//            );
//            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
//            getActivity().registerReceiver(usbReceiver, filter);
//            usbManager.requestPermission(((UsbConnection) usbConnection).getDevice(), permissionIntent);
//        }
//    }
public boolean printUsb() {
    UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(getContext());
    UsbManager usbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);

    if (usbConnection != null && usbManager != null) {
        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                getContext(),
                0,
                new Intent(ACTION_USB_PERMISSION),
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
        );
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        getActivity().registerReceiver(usbReceiver, filter);

        // Request permission
        usbManager.requestPermission(((UsbConnection) usbConnection).getDevice(), permissionIntent);
        return true;
    }

    // Return a default value or indicate that the permission result is pending
    return false;
}



}
