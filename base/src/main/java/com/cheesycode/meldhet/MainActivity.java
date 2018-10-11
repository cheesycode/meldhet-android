package com.cheesycode.meldhet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.cheesycode.meldhet.IntroActivity.setWindowFlag;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    SharedPreferences prefs = null;

    private String mCurrentPhotoPath;
    private String currentIssueType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            try {
                 createImageFile();
            } catch (IOException ex) {
                TextView t9 = findViewById(R.id.text9);
                ImageView i9 = findViewById(R.id.image9);
                i9.setVisibility(View.GONE);
                t9.setVisibility(View.GONE);
            }
        Intent intent = getIntent();
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);

        if(intent.getData()!=null)
                onImageOrTextClick(intent.getData().toString());
    }
    public void onImageOrTextClick(View v){
        onImageOrTextClick((Object) v);
    }
    private void onImageOrTextClick(Object view) {
        if(view instanceof TextView){
            currentIssueType = ((TextView) view).getText().toString();
        }
        else if(view instanceof ImageView)
        {
            currentIssueType = ((ImageView) view).getContentDescription().toString();
        }
        if(view instanceof String){
            currentIssueType = view.toString();
        }

        if(currentIssueType == getResources().getString(R.string.item8)){
            prefs = getSharedPreferences("com.cheesycode.MeldHet", MODE_PRIVATE);
            prefs.edit().putBoolean(getString(R.string.firstrun), true).apply();

            Intent i = new Intent(this,IntroActivity.class);
            this.startActivity(i);
            return;
        }

        if(currentIssueType == getResources().getString(R.string.item9)){
            Intent i = new Intent(this,ChatActivity.class);
            this.startActivity(i);
            return;
        }

        if(currentIssueType == getResources().getString(R.string.item7)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.popup_describe));

            final EditText input = new EditText(this);
            input.setSingleLine();
            FrameLayout container = new FrameLayout(this);
            FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialoguemargin);
            params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialoguemargin);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.dialoguemargin);
            params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialoguemargin);
            input.setLayoutParams(params);
            container.addView(input);

            builder.setView(container);

            builder.setPositiveButton(getString(R.string.notify), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   onImageOrTextClick(input.getText().toString());
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return;
        }


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("FILEIO", ex.toString());
            }
            if (photoFile != null) {

                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.cheesycode.meldhet.fileprovider",
                        photoFile);
                Toast.makeText(this, R.string.fotoinstructie , Toast.LENGTH_LONG).show();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
            else{
                Intent takePictureIntentNoStorage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntentNoStorage, 34);
            }
        }
    }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            Intent intent = new Intent(this, UploadActivity.class);
            intent.putExtra(getString(R.string.issuetyoe), currentIssueType);
            if (requestCode == REQUEST_IMAGE_CAPTURE ) {
                intent.putExtra(getString(R.string.filepath), mCurrentPhotoPath);
            }
            else if(requestCode==34 ){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get(getString(R.string.data));
                intent.putExtra(getString(R.string.file), imageBitmap);
            }
            if( resultCode == RESULT_OK){
                this.startActivity(intent);
            }
        }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(new Date());
        String imageFileName = getString(R.string.filePreFix) + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}

