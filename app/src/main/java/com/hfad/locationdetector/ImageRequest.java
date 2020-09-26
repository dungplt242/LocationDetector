package com.hfad.locationdetector;

import android.graphics.Bitmap;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageRequest {

    private static final ImageRequest imageRequest = new ImageRequest();

    public static ImageRequest Builder() {
        return imageRequest;
    }

    private ImageRequest() {}

    private Response.Listener<NetworkResponse> imgUploadResponse =
            new Response.Listener<NetworkResponse>() {
        @Override
        public void onResponse(NetworkResponse response) {
            // TODO: Implement response handling
        }
    };

    private Response.ErrorListener imgUploadError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
        }
    };

    public VolleyMultipartRequest createRequest(final Bitmap bmp, String url, final String name) {
        return new VolleyMultipartRequest(Request.Method.POST, url,
                imgUploadResponse, imgUploadError)
        {
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(name, bitmapToByteArray(bmp)));
                // TODO: put location & direction info into the parameters
                return params;
            }
        };
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
