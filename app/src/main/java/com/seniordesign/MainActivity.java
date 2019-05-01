package com.seniordesign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_PHOTO = 2;
    private Button sendPictureToServerButton;
    private Button submitButton;
    private File mPhotoFile;
    private ImageView mPhotoView;
    private ImageButton cameraButton;
    private TextView textView;
    private TextView apiResultsTextView;
    private EditText choiceText;
    private String choice;
    private String clothing = " clothing";
    UUID uuid;

    int pictureChoice;

    //    final String URL = "https://acoustic-scarab-232721.appspot.com/";
    final String URL = "http://10.0.2.2:5000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File filesDir = MainActivity.this.getFilesDir();
        Log.d("tag", filesDir.getAbsolutePath());
        uuid = UUID.randomUUID();
        mPhotoFile = new File(filesDir, "IMG_" + uuid.toString() + ".jpg");
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(getPackageManager()) != null;


        cameraButton = findViewById(R.id.cameraButton);

        cameraButton.setEnabled(canTakePhoto);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: delete pickImage() later
                if (canTakePhoto){
                    startActivityForResult(captureImage, REQUEST_IMAGE_CAPTURE);
                }
                else{
                    pickImage();
                }

//                Uri uri = FileProvider.getUriForFile(MainActivity.this,
//                        "com.seniordesign.fileprovider",
//                        mPhotoFile);
//                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//
//                List<ResolveInfo> cameraActivities = MainActivity.this
//                        .getPackageManager().queryIntentActivities(captureImage,
//                                PackageManager.MATCH_DEFAULT_ONLY);
//
//                for (ResolveInfo activity : cameraActivities) {
//                    MainActivity.this.grantUriPermission(activity.activityInfo.packageName,
//                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                }
//
//                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = findViewById(R.id.item_photo);
        updatePhotoView();
//        mPhotoView.setImageResource(R.drawable.ic_android_black_24dp);

        sendPictureToServerButton = findViewById(R.id.textButton);
        sendPictureToServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RequestParams params = new RequestParams();
                    try {
                        params.put("pic", mPhotoFile);
                    } catch(FileNotFoundException e) {}

//                    send request
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(URL + "picture", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                            // handle success response
                            try {
                                String info = new String(bytes, "UTF-8");
                                String[] arr = info.split("\n");
                                System.out.println(Arrays.toString(arr));
                                apiResultsTextView.setText(info);
                                Log.d("prediction",info);
                                System.out.println(info);
                            } catch(Exception e) {

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                            // handle failure response
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        apiResultsTextView = findViewById(R.id.apiResults);
        apiResultsTextView.setText("no results yet");
        choiceText = findViewById(R.id.choiceText);
        choiceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                choice = s.toString();
                clothing = choice + " clothing";
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                try {
                    RequestParams params = new RequestParams();
                    Log.d("choice",choice);
                    try {
                        params.put("choice", clothing);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    // send request
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setTimeout(10 * 1000);
                    client.setConnectTimeout(10*1000);
                    client.setResponseTimeout(10*1000);
                    client.post(URL + "getResults", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                            // handle success response
                            try {
                                String response = new String(bytes, "UTF-8");
                                String[] arr = response.split("\n");
//                                System.out.println(response);
//                                apiResultsTextView.setText(response);
                                Log.d("prediction",new String(bytes, "UTF-8" ));

                                Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
//                                String httpStr = apiResultsTextView.getText().toString();
                                intent.putExtra("http_string", response);
                                startActivity(intent);

                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                            // handle failure response
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });

    }

    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 1);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), MainActivity.this);
            mPhotoView.setImageBitmap(bitmap);
            try{
                FileOutputStream fOut = new FileOutputStream(mPhotoFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(MainActivity.this,
                    "com.seniordesign.fileprovider",
                    mPhotoFile);

            MainActivity.this.revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();

        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mPhotoView.setImageBitmap(imageBitmap);
            int re_width = mPhotoView.getWidth();
            int re_height = mPhotoView.getHeight();
            Bitmap newBM = Bitmap.createScaledBitmap(imageBitmap, re_width - 50,  re_height - 50, false);


            mPhotoView.setImageBitmap(newBM);
        }
        else if (requestCode == 1) {
            //TODO:very hacky, delete this later
            final Bundle extras = data.getExtras();

            System.out.println("IN PICTURE MODULE");
            //Get image

            Bitmap bitmap = null;
            try {
                if(data.getData()==null){
                    bitmap = (Bitmap)data.getExtras().get("data");
                }else{
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                }
            }catch (Exception e) {
                e.printStackTrace();
            }


            mPhotoView.setImageBitmap(bitmap);
            try{
                FileOutputStream fOut = new FileOutputStream(mPhotoFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}