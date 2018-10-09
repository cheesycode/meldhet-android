package com.cheesycode.meldhet;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro2 {

    SharedPreferences prefs = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        prefs = getSharedPreferences("com.cheesycode.MeldHet", MODE_PRIVATE);
        if (!prefs.getBoolean("firstrun", true)) {
            Intent mainactivity = new Intent(IntroActivity.this, MainActivity.class);
            mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(mainactivity);
            finish();
        }
        askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        setSwipeLock(false);
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Welkom");
        sliderPage.setDescription("Leuk dat je onze app gebruikt om de wereld groener te maken");
        sliderPage.setImageDrawable(R.drawable.ic_baseline_nature_people_24px);
        sliderPage.setBgColor(getColor(R.color.colorAquaDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
        sliderPage.setTitle("Vuil of Kapot");
        sliderPage.setDescription("Elke dag loop jij langs honderden, misschien wel duizenden dingen die kapot, vies, onduidelijk of gewoon verkeerd zijn.");
        sliderPage.setImageDrawable(R.drawable.ic_dog_poo);
        sliderPage.setBgColor(getColor(R.color.colorGrapeFruitDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
        sliderPage.setTitle("Doe er wat aan");
        sliderPage.setDescription("Met deze app. Hiermee maak je namelijk eenvoudig melding van dat wat jouw stoort,");
        sliderPage.setImageDrawable(R.drawable.ic_baseline_record_voice_over_24px);
        sliderPage.setBgColor(getColor(R.color.colorLavanderDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
        sliderPage.setTitle("Klik op een Categorie");
        sliderPage.setDescription("En maak een foto. Wij doen de rest, wij brengen de gemeente op de hoogte en zorgen dat alles geregeld moet worden. ");
        sliderPage.setImageDrawable(R.drawable.ic_checked_mark);
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


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent mainactivity = new Intent(IntroActivity.this, MainActivity.class);
        mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mainactivity);
        prefs.edit().putBoolean("firstrun", false).apply();

        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent mainactivity = new Intent(IntroActivity.this, MainActivity.class);
        mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mainactivity);
        prefs.edit().putBoolean("firstrun", false).apply();

        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

    }
}
