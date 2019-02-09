package com.seniordesign;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO = 2;

    private Button itemListButton;
    private File mPhotoFile;
    private ImageView mPhotoView;
    private ImageButton cameraButton;
    UUID uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File filesDir = MainActivity.this.getFilesDir();
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



        itemListButton = findViewById(R.id.itemListButton);
        itemListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemListActivity.class);
                startActivity(intent);
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
