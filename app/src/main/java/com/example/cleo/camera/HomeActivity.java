package com.example.cleo.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    private ImageButton Upload, Choose, Capture, Map,Drive;
    private ImageView imgView;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    private static final int CAMERA_REQUEST_CODE = 1;
    String mCurrentPhotoPath;
    Uri pictureUri;
    private int i;
    ThumbnailUtils thumbnail;
    private String UploadUrl = "http://192.168.43.182/Projects/potholefinal/phone/upload_img.php";
    //private String UploadUrl = "http://192.168.43.60/app_db/upload_img.php";
    private String encoded_string;
    private TextView textView;
    private LocationManager lm;
    String lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //textView = (TextView) findViewById(R.id.textView);

        Capture = (ImageButton) findViewById(R.id.button_capture);
        Choose = (ImageButton) findViewById(R.id.button_choose);
        Upload = (ImageButton) findViewById(R.id.button_upload);
        Map = (ImageButton) findViewById(R.id.button_map);
        Drive = (ImageButton) findViewById(R.id.button_drive);
        imgView = (ImageView) findViewById(R.id.imageView);

        Upload.setOnClickListener(this);
        Choose.setOnClickListener(this);
        Map.setOnClickListener(this);
        Drive.setOnClickListener(this);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String [] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 1);
            return;
        }
        Location l = lm.getLastKnownLocation(lm.NETWORK_PROVIDER);
        onLocationChanged(l);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Capture.setEnabled(true);
                invokeCamera();
            }
        }
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button_choose:
                selectImage();
                break;

            case R.id.button_upload:
                uploadImage();
                break;

            case R.id.button_map:
                viewMap();
                break;

            case R.id.button_drive:
                driveMode();
                break;
        }

    }
    private void selectImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        i=0;
        startActivityForResult(intent,IMG_REQUEST);
    }

    public void dispatchTakePictureIntent(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Capture.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }else
        {
            invokeCamera();
        }
    }

    private void invokeCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //get a file reference
        try{
            pictureUri = FileProvider.getUriForFile(this,getApplicationContext().getPackageName() +".provider",createImageFile());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT,pictureUri);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        i=1;
        startActivityForResult(intent,CAMERA_REQUEST_CODE);
    }

    private File createImageFile()throws IOException {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Camera_demo");
        if(!storageDir.exists())
        {storageDir.mkdir();}
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        //Toast.makeText(this,lon+""+lat,Toast.LENGTH_LONG).show();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(i==0) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
                Uri path = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    imgView.setImageBitmap(bitmap);
                    imgView.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        else
        if (i==1) {
            if(requestCode == CAMERA_REQUEST_CODE) {
                if(resultCode == RESULT_OK) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),pictureUri);
                        imgView.setImageBitmap( thumbnail.extractThumbnail(bitmap,bitmap.getWidth(),bitmap.getHeight()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void uploadImage(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(HomeActivity.this,"Upload Successful",Toast.LENGTH_SHORT).show();
                        imgView.setImageResource(0);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this,error.toString(),Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("encoded_string",imageToString(bitmap));
                params.put("Latitude",lat);
                params.put("Longitude",lon);
                return params;
            }
        };
        MySingleton.getInstance(HomeActivity.this).addTorequestque(stringRequest);
    }

    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    private void viewMap(){
        Intent intent = new Intent(HomeActivity.this,MapsActivity.class);
        startActivity(intent);
    }

    private void driveMode(){
        Intent intent = new Intent(HomeActivity.this,DetectActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        lon = Double.toString(location.getLongitude());
        lat = Double.toString(location.getLatitude());
        //textView.setText("Longitude"+lon+" Latitude"+lat);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
