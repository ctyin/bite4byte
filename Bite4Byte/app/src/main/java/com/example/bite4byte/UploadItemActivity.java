package com.example.bite4byte;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Retrofit;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class UploadItemActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;
    Data manageData;

    int foodQuantity = 0, foodID;
    Integer[] quantities = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            11, 12, 13, 14, 15, 16, 17, 18, 19, 20};
    String photoPath;
    ImageView foodPhoto;

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_item);
        foodID = (int) (Math.random() * 9999);
        manageData = (Data) getIntent().getSerializableExtra("manageData");

        Spinner quantitySpinner = findViewById(R.id.quantity_spinner);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
                 R.layout.support_simple_spinner_dropdown_item, quantities);
        quantitySpinner.setAdapter(adapter);

        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                foodQuantity = (int) adapterView.getSelectedItem();
                System.out.println(foodQuantity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                foodQuantity = adapterView.getSelectedItemPosition() + 1;
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        // init singleton service, don't need to implement yet
        Retrofit retrofitClient = new RetrofitClient().getInstance();
        iMyService = retrofitClient.create(IMyService.class);
    }

    public void onUploadFoodClick(View view) {
        String foodName = ((EditText) findViewById(R.id.food_name)).getText().toString();
        String foodDesc = ((EditText) findViewById(R.id.food_desc)).getText().toString();
        String[] ingredients = ((EditText) findViewById(R.id.ingredients)).getText().toString().toLowerCase().split(",");
        Set<String> restrictionsSet = new HashSet<String>();
        Set<String> cuisinesSet = new HashSet<String>();

        CheckBox pescatarianCheck = (CheckBox) findViewById(R.id.pescatarian_check);
        if (pescatarianCheck.isChecked()) {
            restrictionsSet.add("pescatarian");
        }

        CheckBox vegetarianCheck = (CheckBox) findViewById(R.id.vegetarian_check);
        if (vegetarianCheck.isChecked()) {
            restrictionsSet.add("vegetarian");
        }

        CheckBox veganCheck = (CheckBox) findViewById(R.id.vegan_check);
        if (veganCheck.isChecked()) {
            restrictionsSet.add("vegan");
        }

        CheckBox americanCheck = (CheckBox) findViewById(R.id.american_check);
        if (americanCheck.isChecked()) {
            cuisinesSet.add("american");
        }

        CheckBox asianCheck = (CheckBox) findViewById(R.id.asian_check);
        if (asianCheck.isChecked()) {
            cuisinesSet.add("asian");
        }

        CheckBox frenchCheck = (CheckBox) findViewById(R.id.french_check);
        if (frenchCheck.isChecked()) {
            cuisinesSet.add("french");
        }

        CheckBox indianCheck = (CheckBox) findViewById(R.id.indian_check);
        if (indianCheck.isChecked()) {
            cuisinesSet.add("indian");
        }

        CheckBox italianCheck = (CheckBox) findViewById(R.id.italian_check);
        if (italianCheck.isChecked()) {
            cuisinesSet.add("italian");
        }

        CheckBox mexicanCheck = (CheckBox) findViewById(R.id.mexican_check);
        if (mexicanCheck.isChecked()) {
            cuisinesSet.add("mexican");
        }

        CheckBox middleEasternCheck = (CheckBox) findViewById(R.id.middle_eastern_check);
        if (middleEasternCheck.isChecked()) {
            cuisinesSet.add("middle eastern");
        }

        String[] restrictions = new String[restrictionsSet.size()];
        int i = 0;
        for (String s : restrictionsSet) {
            restrictions[i] = s;
            i++;
        }

        String[] cuisines = new String[cuisinesSet.size()];
        i = 0;
        for (String s : cuisinesSet) {
            cuisines[i] = s;
            i++;
        }

        manageData.uploadFoodItem(this, foodID, foodQuantity, foodName, foodDesc, ingredients, restrictions, cuisines, photoPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap bm = BitmapFactory.decodeFile(photoPath);
                foodPhoto.setImageBitmap(bm);
            }
        }
    }

    public void onFoodPhotoButtonClick(View view) {
        foodPhoto = (ImageView) findViewById(R.id.food_photo);
        
        dispatchPictureTakerAction();
    }

    private void dispatchPictureTakerAction() {
        Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePhoto.resolveActivity(getPackageManager()) != null) {
            File photo = createPhotoFile();

            if (photo != null) {
                photoPath = photo.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(UploadItemActivity.this, "com.example.bite4byte.fileprovider", photo);
                takePhoto.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePhoto, 1);
            }
        }
    }

    private File createPhotoFile() {
        String filename = Integer.toString(foodID);
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(filename, ".jpg", storageDir);
        } catch (IOException e) {
            Log.d("myLog", e.toString());
        }

        return image;
    }

}
