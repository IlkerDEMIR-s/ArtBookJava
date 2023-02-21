package com.example.artbookjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.artbookjava.databinding.ActivityArtActivity2ndBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class ArtActivity2nd extends AppCompatActivity {

    private ActivityArtActivity2ndBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionResultLauncher; // String is the permission type

    Bitmap selectedImage;

    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtActivity2ndBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        database = this.openOrCreateDatabase("artsInBook", MODE_PRIVATE, null);

        Intent intent = getIntent(); // get the intent from the main activity
        String info = intent.getStringExtra("info");  // get the info from the intent

        if(info.equals("new")){
            //new Art
            binding.nameText.setText("");
            binding.artistText.setText("");
            binding.yearText.setText("");
            binding.saveButton.setVisibility(View.VISIBLE);
            binding.imageView2.setImageResource(R.drawable.add);


        }
        else{
            //old Art
            int artId = intent.getIntExtra("artId", 1);
            binding.saveButton.setVisibility(View.INVISIBLE);




            try {

                Cursor cursor = database.rawQuery("SELECT * FROM artsInBook WHERE id = ?", new String[] {String.valueOf(artId)}); // get the data from the database
                int nameIx = cursor.getColumnIndex("artName");
                int artistIx = cursor.getColumnIndex("artistName");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");

                while (cursor.moveToNext()){
                    binding.nameText.setText(cursor.getString(nameIx));
                    binding.artistText.setText(cursor.getString(artistIx));
                    binding.yearText.setText(cursor.getString(yearIx));

                    byte[] bytes = cursor.getBlob(imageIx);  // get the image from the database
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length); // convert the byte array to bitmap
                    binding.imageView2.setImageBitmap(bitmap);
                }
                cursor.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void save(View view) {

        String artName = binding.nameText.getText().toString();
        String artistName = binding.artistText.getText().toString();
        String year = binding.yearText.getText().toString();

        Bitmap smallImage = makeSmallerImage(selectedImage, 300);

        // convert bitmap to byte array (for saving in database)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray(); // we convert the image to byte array

        try {

            database.execSQL("CREATE TABLE IF NOT EXISTS artsInBook (id INTEGER PRIMARY KEY, artName VARCHAR, artistName VARCHAR, year VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO artsInBook (artName, artistName, year, image) VALUES (?, ?, ?, ?)"; // ? is used to bind the string to the sql statement
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString); // compileStatement() is used to compile the sql statement
            sqLiteStatement.bindString(1, artName);  // bindString() is used to bind the string to the sql statement
            sqLiteStatement.bindString(2, artistName); // index starts from 1 not 0 (1st ?, 2nd ?, 3rd ?, 4th ?)
            sqLiteStatement.bindString(3, year);
            sqLiteStatement.bindBlob(4, byteArray);
            sqLiteStatement.execute();

            //database.execSQL("DROP TABLE arts");

        }catch (Exception e) {
            e.printStackTrace();
        }

        //finish();  // this method is used to close the activity. we don't use this method because we want to go back to the main activity when we renounce our process.
        Intent intent = new Intent(ArtActivity2nd.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // this method is used to clear the top activity from the stack
        startActivity(intent);

    }

    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {

        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;  // vertical or horizontal

        if (bitmapRatio > 1) { // horizontal
            // landscape image
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        } else {
            // portrait image
            height = maximumSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void selectImage(View view) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            // Android 33+ -> READ_MEDIA_IMAGES //

            // we use ContextCompat to check if the permission is available or not for older versions
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                    != getPackageManager().PERMISSION_GRANTED) {
                // permission not granted
                // explain permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES)) {

                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // request permission
                            permissionResultLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();

                }
                else {
                    // request permission
                    permissionResultLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }


            } else {
                // select image
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }

        }


        else{

            // Android 32 -> READ_EXTERNAL_STORAGE //

            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != getPackageManager().PERMISSION_GRANTED) {
                // permission not granted

                // explain permission
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // request permission
                            permissionResultLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();

                }
                else {
                    // request permission
                    permissionResultLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }


            } else {
                // select image
                // Uri -> Uniform Resource Identifier. It is used to identify the resource.
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery); // new method
                // startActivityForResult(intentToGallery, 2); // old method

            }

        }


    }


    private void registerLauncher() {  // this method is used to register the launchers (Activity Result Launcher and Permission Result Launcher)

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null) {
                        // get image from intent
                        Uri imageData = intentFromResult.getData();
                        //binding.imageView2.setImageURI(imageData);  // not works for converting to bitmap for saving in database
                                                                     // it is used to set the image to the image view
                        //Uri -> Uniform Resource Identifier. Give the location of the resource.

                        try {

                            if(Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(ArtActivity2nd.this.getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView2.setImageBitmap(selectedImage);
                            }else{
                                  selectedImage = MediaStore.Images.Media.getBitmap(ArtActivity2nd.this.getContentResolver(), imageData);
                                  binding.imageView2.setImageBitmap(selectedImage);
                            }


                        }catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
         });


            permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result) {
                    // permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
                else {
                    // permission denied
                    Toast.makeText(ArtActivity2nd.this, "Permission needed!", Toast.LENGTH_LONG).show();

                }

            }

        });

    }
}