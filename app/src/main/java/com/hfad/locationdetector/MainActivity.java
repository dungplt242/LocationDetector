package com.hfad.locationdetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends Activity implements RecyclerMainAdapter.ItemClickListener {

    // user options resources
    private final int[] optionID =
            {R.drawable.ic_gallery, R.drawable.ic_camera, R.drawable.ic_send, R.drawable.ic_send};
    private final String[] optionName = {"Gallery", "Camera", "Send", "Test"};
    private enum Option {GALLERY, CAMERA, SEND, TEST}

    // request code
    private final int CAMERA_REQUEST = 42;
    private final int GALLERY_REQUEST = 88;

    // recycler view related fields
    private RecyclerView recyclerView;
    private ArrayList<AppOption> options;
    private RecyclerMainAdapter mainAdapter;

    // image related fields TODO: Put the fields inside a class
    private ImageView imageView;
    private SendPackage sendPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        displayOptions();
    }

    private void initComponents() {
        imageView = findViewById(R.id.myImage);
        recyclerView = findViewById(R.id.mainRecyclerView);
        sendPackage = new SendPackage();
        sendPackage.setUploadURL("http://192.168.1.10:8000/images");
    }

    /** Display options on the recycler view **/
    private void displayOptions() {
        prepareOptionList();
        setRecyclerViewLayoutStyle();
        setRecyclerViewAdapter();
    }

    private void setRecyclerViewAdapter() {
        mainAdapter = new RecyclerMainAdapter(options, this);
        recyclerView.setAdapter(mainAdapter);
    }

    private void setRecyclerViewLayoutStyle() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void prepareOptionList() {
        options = new ArrayList<>();
        for (int i = 0; i < optionID.length; ++i) {
            options.add(new AppOption(Icon.createWithResource(this, optionID[i]), optionName[i]));
        }
    }

    /** Handle screen rotation events **/
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (sendPackage.getCurrentImage() == null) return;
        imageView.setImageBitmap(sendPackage.getCurrentImage());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case GALLERY_REQUEST: handleGalleryResponse(data); break;
            case CAMERA_REQUEST: handleCameraResponse(data); break;
            default:
        }
    }

    /** Display selected images on the image view **/
    private void handleGalleryResponse(Intent data) {
        try {
            final Uri imageUri = data.getData();
            if (imageUri == null) throw new FileNotFoundException();
            getGalleryImagePath(imageUri);
            displayGalleryImage(imageUri);

        }
        catch (FileNotFoundException e) { displayToastMessage("Something went wrong"); }
    }

    private void getGalleryImagePath(Uri imageUri) {
        sendPackage.setImagePath(imageUri.getPath() + ".jpg");
    }

    private void displayGalleryImage(Uri imageUri) throws FileNotFoundException {
        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
        sendPackage.setCurrentImage(BitmapFactory.decodeStream(imageStream));
        imageView.setImageBitmap(sendPackage.getCurrentImage());
    }

    private void displayToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /** Display captured images on the image view **/
    private void handleCameraResponse(Intent data) {
        sendPackage.update(data);
        imageView.setImageBitmap(sendPackage.getCurrentImage());
    }

    /** Being executed when user select an option **/
    @Override
    public void onClick(int position) {
        Option userOption = Option.values()[position];
        switch (userOption) {
            case GALLERY: openGallery(); break;
            case CAMERA: openCamera(); break;
            case SEND: sendImageToServer(); break;
            case TEST: testFunction(); break;
            default:
        }
    }

    private void testFunction() {
        testRequest();

//        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
////        editor.clear();
////        editor.commit();
//        Map<String, ?> allPrefs = sharedPreferences.getAll();
//        Log.d("[DEBUG]", "Preferences length = " + allPrefs.size());
//        Log.d("[DEBUG]", "Version = " + sharedPreferences.getString("version", null));
    }

    private void testRequest() {
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
//        JsonObjectRequest featuresRequest = new JsonObjectRequest(Request.Method.GET, "http://192.168.1.10:8000/features", null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    displayToastMessage("Version is " + response.get("version"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                parseVolleyError(error);
//            }
//        });
//        Volley.newRequestQueue(this).add(featuresRequest);

        StringRequest testRequest = new StringRequest(Request.Method.GET, "http://192.168.1.10:8000/images/1", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("[DEBUG]", "response = " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                parseVolleyError(error);
            }
        });
        Volley.newRequestQueue(this).add(testRequest);

//        float density = getResources().getConfiguration().densityDpi;
//        int px = (int) Math.ceil(24 * density / 160);
//        Log.d("[DEBUG]", "pixel = " + px);
//        ImageRequest imageRequest = new ImageRequest("http://192.168.1.10:8000/android_download?name=location.png", new Response.Listener<Bitmap>() {
//            @Override
//            public void onResponse(Bitmap response) {
//                options.get(options.size() - 1).setIcon(Icon.createWithBitmap(response));
//                mainAdapter.notifyDataSetChanged();
//                Log.d("[DEBUG]", "Received bitmap");
//            }
//        }, px, px, null, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("[DEBUG]", "wtf");
//                parseVolleyError(error);
//            }
//        });
//        Volley.newRequestQueue(this).add(imageRequest);
    }

    private void openCamera() {
        Intent imageCaptureIntent = new Intent(this, ImageCaptureActivity.class);
        startActivityForResult(imageCaptureIntent, CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    private void sendImageToServer() {
        promptSending();
        performRequest();
    }

    private void performRequest() {
        if (sendPackage.getCurrentImage() == null) return;
        VolleyMultipartRequest uploadRequest =
                PackageRequest.Builder().createRequest(sendPackage);
        Volley.newRequestQueue(this).add(uploadRequest);
    }

    private void promptSending() {
        if (sendPackage.getCurrentImage() == null)
            displayToastMessage("Image not found");
        else displayToastMessage("Let us guess where it is...");
    }

    /** Long, ugly function to parse volley error into meaningful messages. **/
    private void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            Log.e("[VOLLEY][REQUEST]", responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (error.getCause() instanceof MalformedURLException){
            Toast.makeText(MainActivity.this, "Bad Request.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                || error.getCause() instanceof JSONException
                || error.getCause() instanceof XmlPullParserException){
            Toast.makeText(MainActivity.this, "Parse Error (because of invalid json or xml).",
                    Toast.LENGTH_SHORT).show();
        }
        else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
            Toast.makeText(MainActivity.this, "Server error.", Toast.LENGTH_SHORT).show();
        }else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                || error.getCause() instanceof ConnectTimeoutException
                || error.getCause() instanceof SocketException
                || (error.getCause().getMessage() != null
                && error.getCause().getMessage().contains("Connection timed out"))) {
            Toast.makeText(MainActivity.this, "Connection timeout error",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "An unknown error occurred.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}