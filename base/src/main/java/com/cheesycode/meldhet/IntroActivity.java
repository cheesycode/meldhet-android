package com.cheesycode.meldhet;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IntroActivity extends AppIntro2 {
    public static boolean isInstantApp =false;
    SharedPreferences prefs = null;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            signInAnonymously();
        }

        prefs = getSharedPreferences("com.cheesycode.MeldHet", MODE_PRIVATE);
        if (!prefs.getBoolean(getString(R.string.firstrun), true)) {
            Intent mainactivity = new Intent(IntroActivity.this, MainActivity.class);
            mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(mainactivity);
            finish();
        }
        askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        setSwipeLock(false);
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle(getString(R.string.Intro1));
        sliderPage.setDescription(getString(R.string.Intro1SubText));

        sliderPage.setImageDrawable(R.drawable.banner);
        sliderPage.setBgColor(getColor(R.color.colorAquaDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
        sliderPage.setTitle(getString(R.string.Intro2));
        sliderPage.setDescription(getString(R.string.Intro2Subtext));
        sliderPage.setImageDrawable(R.drawable.trash);
        sliderPage.setBgColor(getColor(R.color.colorGrapeFruitDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
        sliderPage.setTitle(getString(R.string.Intro3));
        sliderPage.setDescription(getString(R.string.Intro3SubText));
        sliderPage.setImageDrawable(R.drawable.city_hall_1);
        sliderPage.setBgColor(getColor(R.color.colorLavanderDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
        sliderPage.setTitle(getString(R.string.Intro4));
        sliderPage.setDescription(getString(R.string.Intro4SubText));
        sliderPage.setImageDrawable(R.drawable.nature);
        sliderPage.setBgColor(getColor(R.color.colorGrassDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent mainactivity = new Intent(IntroActivity.this, MainActivity.class);
        mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mainactivity);
        prefs.edit().putBoolean(getString(R.string.firstrun), false).apply();

        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent mainactivity = new Intent(IntroActivity.this, MainActivity.class);
        mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mainactivity);
        prefs.edit().putBoolean(getString(R.string.firstrun), false).apply();

        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("FIREBASEAUTH", "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            Log.w("FIREBASEAUTH", "signInAnonymously:failure", task.getException());
                            Toast.makeText(IntroActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            AlertDialog.Builder builder = new AlertDialog.Builder(IntroActivity.this);
                            builder.setTitle("De applicatie is momenteel niet beschikbaar probeer het later opnieuw");


                            builder.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finishAndRemoveTask();
                                }
                            });
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finishAndRemoveTask();
                                }
                            });

                            builder.show();
                        }
                    }
                });
    }
}
