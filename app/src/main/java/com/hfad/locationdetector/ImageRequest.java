package com.hfad.locationdetector;

import android.graphics.Bitmap;
import android.util.Log;

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

    public VolleyMultipartRequest createRequest(final SendPackage sendPackage) {
        return new VolleyMultipartRequest(Request.Method.POST, sendPackage.getUploadURL(),
                imgUploadResponse, imgUploadError)
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("angle", String.valueOf(sendPackage.getImageDirection()));
                params.put("longitude", String.valueOf(sendPackage.getImageLongitude()));
                params.put("latitude", String.valueOf(sendPackage.getImageLatitude()));
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(sendPackage.getImagePath(),
                        bitmapToByteArray(sendPackage.getCurrentImage())));
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
