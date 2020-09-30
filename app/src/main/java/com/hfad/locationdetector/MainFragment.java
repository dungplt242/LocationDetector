package com.hfad.locationdetector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment implements RecyclerMainAdapter.ItemClickListener {

    // user options resources
    private final int[] optionID =
            {R.drawable.ic_gallery, R.drawable.ic_camera, R.drawable.ic_send, R.drawable.ic_send};
    private final String[] optionName = {"Gallery", "Camera", "Send", "Test"};
    private enum Option {GALLERY, CAMERA, SEND, TEST}

    // recycler view related fields
    private RecyclerView recyclerView;
    private ArrayList<AppOption> options;
    private RecyclerMainAdapter mainAdapter;

    // image related fields
    private ImageView imageView;
    MainViewModel sendPackage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.myImage);
        recyclerView = view.findViewById(R.id.mainRecyclerView);
        sendPackage.getCurrentImage().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });
        displayOptions();
    }

    private void initComponents() {
        sendPackage = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void prepareOptionList() {
        options = new ArrayList<>();
        for (int i = 0; i < optionID.length; ++i) {
            options.add(new AppOption(Icon.createWithResource(getActivity(), optionID[i]), optionName[i]));
        }
    }

    private void getGalleryImagePath(Uri imageUri) {
        sendPackage.setImagePath(imageUri.getPath() + ".jpg");
    }

    private void displayGalleryImage(Uri imageUri) throws FileNotFoundException {
        final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
        sendPackage.setCurrentImage(BitmapFactory.decodeStream(imageStream));
    }

    private void displayToastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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

    private void openCamera() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.imageCaptureFragment);
    }

    private void openGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        galleryResultLauncher.launch(photoPickerIntent);
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
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
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
        Volley.newRequestQueue(getActivity()).add(testRequest);

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

    ActivityResultLauncher<Intent> galleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            final Uri imageUri = result.getData().getData();
                            if (imageUri == null) throw new FileNotFoundException();
                            getGalleryImagePath(imageUri);
                            displayGalleryImage(imageUri);
                        }
                        catch (FileNotFoundException e) { displayToastMessage("Something went wrong"); }
                    }
                }
            });

    private void sendImageToServer() {
        promptSending();
        performRequest();
    }

    private void performRequest() {
        if (sendPackage.getCurrentImage() == null) return;
        VolleyMultipartRequest uploadRequest =
                PackageRequest.Builder().createRequest(sendPackage);
        Volley.newRequestQueue(getActivity()).add(uploadRequest);
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
            Toast.makeText(getActivity(), "Bad Request.", Toast.LENGTH_SHORT).show();
        } else if (error instanceof ParseError || error.getCause() instanceof IllegalStateException
                || error.getCause() instanceof JSONException
                || error.getCause() instanceof XmlPullParserException){
            Toast.makeText(getActivity(), "Parse Error (because of invalid json or xml).",
                    Toast.LENGTH_SHORT).show();
        }
        else if (error instanceof ServerError || error.getCause() instanceof ServerError) {
            Toast.makeText(getActivity(), "Server error.", Toast.LENGTH_SHORT).show();
        }else if (error instanceof TimeoutError || error.getCause() instanceof SocketTimeoutException
                || error.getCause() instanceof ConnectTimeoutException
                || error.getCause() instanceof SocketException
                || (error.getCause().getMessage() != null
                && error.getCause().getMessage().contains("Connection timed out"))) {
            Toast.makeText(getActivity(), "Connection timeout error",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "An unknown error occurred.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}