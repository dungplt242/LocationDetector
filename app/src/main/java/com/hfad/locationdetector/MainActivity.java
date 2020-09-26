package com.hfad.locationdetector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends Activity implements RecyclerMainAdapter.ItemClickListener {

    // user options resources
    private final int[] optionID =
            {R.drawable.ic_gallery, R.drawable.ic_camera, R.drawable.ic_send};
    private final String[] optionName = {"Gallery", "Camera", "Send"};

    private enum Option {
        GALLERY(0), CAMERA(1), SEND(2);
        public final int value;
        Option(int value) {this.value = value;}
    }

    // request code
    private final int CAMERA_REQUEST = 42;
    private final int GALLERY_REQUEST = 88;

    // invalid state
    private final double INVALID = 812;

    // recycler view related fields
    private RecyclerView recyclerView;
    private ArrayList<AppOption> options;
    private RecyclerMainAdapter mainAdapter;

    // image related fields TODO: Put the fields inside a class
    private ImageView imageView;
    private String imagePath;
    private String uploadURL;
    private Bitmap currentImage;
    private double imageDirection;
    private double imageLongitude;
    private double imageLatitude;

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
        uploadURL = getResources().getString(R.string.image_upload_url);
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
            options.add(new AppOption(optionID[i], optionName[i]));
        }
    }

    /** Handle screen rotation events **/
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (currentImage == null) return;
        imageView.setImageBitmap(currentImage);
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
        imagePath = imageUri.getPath();
    }

    private void displayGalleryImage(Uri imageUri) throws FileNotFoundException {
        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
        currentImage = BitmapFactory.decodeStream(imageStream);
        imageView.setImageBitmap(currentImage);
    }

    private void displayToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /** Display captured images on the image view **/
    private void handleCameraResponse(Intent data) {
        imagePath = data.getStringExtra("imagePath");
        imageDirection = data.getDoubleExtra("imageDirection", INVALID);
        imageLongitude = data.getDoubleExtra("imageLongitude", INVALID);
        imageLatitude = data.getDoubleExtra("imageLatitude", INVALID);
        currentImage = getCapturedImageFromOutPath();
        imageView.setImageBitmap(currentImage);
    }

    private Bitmap getCapturedImageFromOutPath() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(imagePath, options);
        return ImageHandling.Builder().adjustImageOrientation(image, imagePath);
    }

    /** Being executed when user select an option **/
    @Override
    public void onClick(int position) {
        switch (position) {
            case 0: openGallery(); break;
            case 1: openCamera(); break;
            case 2: sendImageToServer(); break;
            default:
        }
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
        if (currentImage == null) return;
        VolleyMultipartRequest uploadRequest =
                ImageRequest.Builder().createRequest(currentImage, uploadURL, imagePath);
        Volley.newRequestQueue(this).add(uploadRequest);
    }

    private void promptSending() {
        if (currentImage == null) displayToastMessage("Image not found");
        else displayToastMessage("Let us guess where it is...");
    }
}