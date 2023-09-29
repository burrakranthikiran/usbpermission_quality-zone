package com.qualityzone.usbpermission;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class USBPrinter extends AppCompatActivity {

  private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

  PdfRenderer renderer = null;

  Bitmap decodedByte;

  EscPosPrinter printer_th;

  Bitmap resizedBitmap;
  private WebView webView;
  private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (ACTION_USB_PERMISSION.equals(action)) {
        synchronized (this) {
          UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
          UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
          if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
            if (usbManager != null && usbDevice != null) {
              // YOUR PRINT CODE HERE

              try {

                AssetManager assetManager = getAssets();
                String pdfFileName = "book1.pdf"; // Replace with the actual PDF file name in your assets folder
                InputStream inputStream = assetManager.open(pdfFileName);

                // Create a temporary file from the asset InputStream
                File tempFile = new File(getCacheDir(), pdfFileName);
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                  outputStream.write(buffer, 0, length);
                }
                outputStream.close();

                // Create a ParcelFileDescriptor from the temporary file
                ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);

                renderer = new PdfRenderer(parcelFileDescriptor);





//                renderer = new PdfRenderer(ParcelFileDescriptor.open(new File(getApplicationContext().getExternalFilesDir(null) + "/Pheezee", patientid + date_value + "thermal_printer" + ".pdf"), ParcelFileDescriptor.MODE_READ_ONLY));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              // Create an array to hold the bitmaps
              Bitmap[] bitmaps = new Bitmap[renderer.getPageCount()];
              int i ;
              // Render each page into a bitmap
              for (i = 0; i < renderer.getPageCount(); i++) {
                PdfRenderer.Page page = renderer.openPage(i);
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                Paint paint = new Paint();
                paint.setColorFilter(filter);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(bitmap, 0, 0, paint);
                bitmaps[i] = bitmap;
                decodedByte = bitmap;

                try {
                  printer_th = new EscPosPrinter(new UsbConnection(usbManager, usbDevice), 203, 48f, 32);
                  int width = decodedByte.getWidth(), height = decodedByte.getHeight();
                  StringBuilder textToPrint = new StringBuilder();
                  for (int y = 0; y < height; y += 256) {
                    resizedBitmap = Bitmap.createBitmap(decodedByte, 0, y, width, (y + 256 >= height) ? height - y : 256);
                    textToPrint.append("[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer_th, resizedBitmap) + "</img>\n");
                  }
                  printer_th.printFormattedTextAndCut(textToPrint.toString());
                  decodedByte.recycle();
                  page.close();
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
              // Clean up resources
              renderer.close();



            }
          }
        }
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    setContentView(R.layout.activity_usbprinter);
//     webView = findViewById(R.id.webView);
    // Enable JavaScript (optional)
    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);

    // Specify the PDF file name to check
    String pdfFileName = "book1.pdf";

    // Check if the PDF file exists in the assets folder
    if (isPdfFileExists(pdfFileName)) {
      String pdfPath = "file:///android_asset/book1.pdf";
      String googleDocsUrl = "https://docs.google.com/viewer?url=" + pdfPath;
      webView.loadUrl(googleDocsUrl);
      Log.e("22222","yes");
    } else {
      Log.e("22222","no");
      // Handle the case where the PDF file doesn't exist
      // You can display an error message or take appropriate action here
    }



//    Button usb_print_bt = findViewById(R.id.usb_print);
//    usb_print_bt.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        printUsb();
//      }
//    });

    // Call the printUsb() method to initiate USB printing

  }

  private boolean isPdfFileExists(String fileName) {
    try {
      getAssets().open(fileName);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void printUsb() {
    UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(this);
    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    if (usbConnection != null && usbManager != null) {
      PendingIntent permissionIntent = PendingIntent.getBroadcast(
        this,
        0,
        new Intent(ACTION_USB_PERMISSION),
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
      );
      IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
      registerReceiver(usbReceiver, filter);
      usbManager.requestPermission(((UsbConnection) usbConnection).getDevice(), permissionIntent);
    }
  }
}

