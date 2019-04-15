package com.seniordesign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
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
    UUID uuid;

    final String URL = "https://acoustic-scarab-232721.appspot.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File filesDir = MainActivity.this.getFilesDir();
        Log.d("tag", filesDir.getAbsolutePath());
        uuid = UUID.randomUUID();
        mPhotoFile = new File(filesDir, "IMG_" + uuid.toString() + ".jpg");
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(getPackageManager()) != null;


        cameraButton = findViewById(R.id.cameraButton);

        cameraButton.setEnabled(canTakePhoto);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(MainActivity.this,
                        "com.seniordesign.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = MainActivity.this
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    MainActivity.this.grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = findViewById(R.id.item_photo);
        updatePhotoView();

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
                                String[] arr = new String(bytes, "UTF-8").split("\n");
                                System.out.println(Arrays.toString(arr));
                                apiResultsTextView.setText(Arrays.toString(arr));
                                Log.d("prediction",new String(bytes, "UTF-8" ));
                                System.out.println(new String(bytes, "UTF-8"));
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
                        params.put("choice", choice);
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
                                apiResultsTextView.setText(response);
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

//    private class HttpGetRequest extends AsyncTask<String, Void, String> {
//
//        public static final String REQUEST_METHOD = "GET";
//        public static final int READ_TIMEOUT = 15000;
//        public static final int CONNECTION_TIMEOUT = 15000;
//
//        @Override
//        protected String doInBackground(String... params) {
//            String stringUrl = params[0];
//            String result = "";
//            String inputLine;
//
//            try {
//                //Create a URL object holding our url
//                URL myUrl = new URL(stringUrl);
//                //Create a connection
//                HttpURLConnection connection =(HttpURLConnection)
//                        myUrl.openConnection();
//
//                //Set methods and timeouts
//                connection.setRequestMethod(REQUEST_METHOD);
//                connection.setReadTimeout(READ_TIMEOUT);
//                connection.setConnectTimeout(CONNECTION_TIMEOUT);
//                //Connect to our url
//                connection.connect();
//                //Create a new InputStreamReader
//                InputStreamReader streamReader = new
//                        InputStreamReader(connection.getInputStream());
//                //Create a new buffered reader and String Builder
//                BufferedReader reader = new BufferedReader(streamReader);
//                StringBuilder stringBuilder = new StringBuilder();
//                //Check if the line we are reading is not null
//                while((inputLine = reader.readLine()) != null){
//                    stringBuilder.append(inputLine);
//                }
//                //Close our InputStream and Buffered reader
//                reader.close();
//                streamReader.close();
//                //Set our result equal to our stringBuilder
//                result = stringBuilder.toString();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(String clothes) {
//            super.onPostExecute(clothes);
//            System.out.println(clothes);
//        }
//
//    }

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
    }
}
