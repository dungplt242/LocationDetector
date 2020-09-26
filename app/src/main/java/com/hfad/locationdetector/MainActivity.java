package com.hfad.locationdetector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class MainActivity extends Activity implements RecyclerMainAdapter.ItemClickListener {

    private final int[] optionID =
            {R.drawable.ic_gallery, R.drawable.ic_camera, R.drawable.ic_send};
    private final String[] optionName = {"Gallery", "Camera", "Send"};

    // request code
    private final int CAMERA_REQUEST = 42;
    private final int GALLERY_REQUEST = 88;
    private final int SEND_REQUEST = 19;

    // recycler view related fields
    private RecyclerView recyclerView;
    private ArrayList<AppOption> options;
    private RecyclerMainAdapter mainAdapter;

    // image related fields
    private ImageView imageView;
    private Bitmap currentImage;
    private String imagePath;
    private String uploadURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();
        displayOptions();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (currentImage == null) return;
        imageView.setImageBitmap(currentImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO: Implement this method
        // Check if resultCode == RESULT_OK, then call the corresponding methods
    }

    private void sendImageToServer() {
        if (currentImage == null) return;
        VolleyMultipartRequest uploadRequest =
                ImageRequest.Builder().createRequest(currentImage, uploadURL, imagePath);
        Volley.newRequestQueue(this).add(uploadRequest);
    }

    private void handleImageResponse(Intent data) {
        imagePath = data.getStringExtra("imagePath");
        currentImage = getCapturedImageFromOutPath();
        imageView.setImageBitmap(currentImage);
    }

    private Bitmap getCapturedImageFromOutPath() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap image = BitmapFactory.decodeFile(imagePath, options);
        return ImageHandling.Builder().adjustImageOrientation(image, imagePath);
    }

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

    private void initComponents() {
        imageView = findViewById(R.id.myImage);
        recyclerView = findViewById(R.id.mainRecyclerView);
        uploadURL = getResources().getString(R.string.image_upload_url);
    }

    @Override
    public void onClick(int position) {
        // TODO: Implement this method
    }
}