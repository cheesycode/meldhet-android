package com.cheesycode.meldhet;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import static android.content.Context.MODE_PRIVATE;

public class IntroActivity extends AppIntro2 {

    SharedPreferences prefs = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);
        if (!prefs.getBoolean("firstrun", true)) {
            Intent mainactivity = new Intent(IntroActivity.this, MainActivity.class);
            mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(mainactivity);
            finish();
        }
        askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);


        setSwipeLock(false);
        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("Welkom");
        sliderPage.setDescription("Leuk dat je onze app gebruikt om de wereld groener te maken");
        sliderPage.setImageDrawable(R.drawable.banner);
        sliderPage.setBgColor(getResources().getColor(R.color.colorAquaDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
        sliderPage.setTitle("Vuil of Kapot");
        sliderPage.setDescription("Elke dag lopen we langs tientalle dingen die vuil of kapot zijn, waar niemand iets van weet.");
        sliderPage.setImageDrawable(R.drawable.banner);
        sliderPage.setBgColor(getResources().getColor(R.color.colorGrapeFruitDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
        sliderPage.setTitle("Ga Opzoek");
        sliderPage.setDescription("Met deze app kun jij op zoek naar de dingen die jij belangrijk vindt.");
        sliderPage.setImageDrawable(R.drawable.banner);
        sliderPage.setBgColor(getResources().getColor(R.color.colorLavanderDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);
        sliderPage.setTitle("Klik op een Categorie");
        sliderPage.setDescription("En maak een foto. Wij doen de rest, wij brengen de gemeente op de hoogte en zorgen dat alles geregeld moet worden. ");
        sliderPage.setImageDrawable(R.drawable.banner);
        sliderPage.setBgColor(getResources().getColor(R.color.colorGrassDark));
        addSlide(AppIntroFragment.newInstance(sliderPage));
        setSwipeLock(false);


    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent mainactivity = new Intent(IntroActivity.this, MainActivity.class);
        mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mainactivity);
        prefs.edit().putBoolean("firstrun", false).commit();

        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent mainactivity = new Intent(IntroActivity.this, MainActivity.class);
        mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(mainactivity);
        prefs.edit().putBoolean("firstrun", false).commit();

        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

    }
}
