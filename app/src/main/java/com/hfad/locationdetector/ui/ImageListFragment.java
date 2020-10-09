package com.hfad.locationdetector.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.hfad.locationdetector.R;
import com.hfad.locationdetector.models.ImageItem;
import com.hfad.locationdetector.models.ImagesViewModel;
import com.hfad.locationdetector.models.MainViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.hfad.locationdetector.utils.UtilFunctions.parseVolleyError;
import static com.hfad.locationdetector.utils.UtilFunctions.parseVolleyErrorResponse;

public class ImageListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ImageRecyclerAdapter imageRecyclerAdapter;
//    private ImagesViewModel imagesViewModel;
    List<ImageItem> imageItemList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
    }

    private void initComponents() {

    }

    private void getImageList() {
        JsonArrayRequest imagesRequest = new JsonArrayRequest(Request.Method.GET, getString(R.string.server_url) + "/images", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    imageItemList = new ArrayList<>(response.length());
                    for (int i = 0; i < response.length(); ++i) {
                        JSONObject imageJsonItem = response.getJSONObject(i);
                        byte[] data = Base64.decode(imageJsonItem.getString("thumbnail"), Base64.DEFAULT);
                        ImageItem imageItem = new ImageItem();
                        imageItem.setThumbnail(Icon.createWithData(data, 0, data.length));
                        imageItem.setImageID(imageJsonItem.getInt("id"));
                        imageItem.setName(imageJsonItem.getString("name"));
                        imageItemList.add(imageItem);
                    }
                    imageRecyclerAdapter.submitList(imageItemList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, parseVolleyErrorResponse(getActivity()));
        Volley.newRequestQueue(requireActivity()).add(imagesRequest);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.imageListRecyclerView);
        imageRecyclerAdapter = new ImageRecyclerAdapter(new ImageRecyclerAdapter.ItemClickListener() {
            @Override
            public void onClick(final int position) {
                ImageRequest imageRequest = new ImageRequest(getString(R.string.server_url) + "/images/" + imageItemList.get(position).getImageID(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
                        mainViewModel.setCurrentImage(response);
                        mainViewModel.setCurrentImageID(imageItemList.get(position).getImageID());
                        NavHostFragment.findNavController(ImageListFragment.this).popBackStack();
                    }
                }, 0, 0, null, Bitmap.Config.ARGB_8888, parseVolleyErrorResponse(getActivity()));
                Volley.newRequestQueue(requireActivity()).add(imageRequest);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()));
        recyclerView.setAdapter(imageRecyclerAdapter);
        getImageList();
    }
}
