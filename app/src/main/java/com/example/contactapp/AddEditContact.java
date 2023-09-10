package com.example.contactapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddEditContact extends AppCompatActivity {

    //Ovlasti koje dajemo aplikaciji
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final int IMAGE_FROM_GALLERY_CODE = 300;
    private static final int IMAGE_FROM_CAMERA_CODE = 400;
    private ImageView profileIv;
    private EditText nameEt, phoneEt, emailEt;
    private FloatingActionButton fab;
    //String variable;
    private String id, image, name, phone, email, addedTime, updatedTime;
    private Boolean isEditMode;
    //action bar
    private ActionBar actionBar;
    // polja za perissione
    private String[] cameraPermission;
    private String[] storagePermission;

    //varijabla za slike
    private Uri imageUri;

    //Helper za BP
    private DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        //inicijalizacija baze podataka
        dbHelper = new DbHelper(this);

        //inicijalizacija dopuštenja
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //inicijalizacija pogleda
        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        emailEt = findViewById(R.id.emailEt);
        fab = findViewById(R.id.fab);

        //inicijalizacija action bara
        actionBar = getSupportActionBar();


        //tipka za povratak
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        // intent podaci
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode", false);

        if (isEditMode) {
            //Naslov toolbara;
            actionBar.setTitle("Promjeni Kontact");

            //Dobivamo ostale vrijednosti intenta
            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("NAME");
            phone = intent.getStringExtra("PHONE");
            email = intent.getStringExtra("EMAIL");
            addedTime = intent.getStringExtra("ADDEDTIME");
            updatedTime = intent.getStringExtra("UPDATEDTIME");
            image = intent.getStringExtra("IMAGE");

            //postavljanje vrijednosti kao text
            nameEt.setText(name);
            phoneEt.setText(phone);
            emailEt.setText(email);

            //postavljanje slike
            imageUri = Uri.parse(image);

            if (image.equals("")) {
                profileIv.setImageResource(R.drawable.ic_baseline_person_24);
            } else {
                profileIv.setImageURI(imageUri);
            }

        } else {
            // dodavanje kontakata
            actionBar.setTitle("Dodaj Kontakt");
        }

        // add even handler
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
    }

    private void showImagePickerDialog() {


        String[] options = {"Kamera", "Gallery"};


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setTitle
        builder.setTitle("Odaberite opciju");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handler za klik na item
                if (which == 0) {
                    //Ukoliko korisnik odabere kameru
                    if (!checkCameraPermission()) {
                        //trazimo dozvolu za kameru
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }

                } else if (which == 1) {
                    //Ukoliko korisnik odabere galeriju
                    if (!checkStoragePermission()) {
                        //trazimo dozvolu za storage
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }

                }
            }
        }).create().show();
    }

    private void pickFromGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");

        startActivityForResult(galleryIntent, IMAGE_FROM_GALLERY_CODE);
    }

    private void pickFromCamera() {

//       ContentValues for image info
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "IMAGE_TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION, "IMAGE_DETAIL");

        //pohranjujemo sliku
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //otvaramo kameru
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        startActivityForResult(cameraIntent, IMAGE_FROM_CAMERA_CODE);
    }

    private void saveData() {

        //uzimamo korisninkove unesene podatke
        name = nameEt.getText().toString();
        phone = phoneEt.getText().toString();
        email = emailEt.getText().toString();


        // trenutno vrijeme
        String timeStamp = "" + System.currentTimeMillis();


        if (!name.isEmpty() || !phone.isEmpty() || !email.isEmpty()) {

            if (isEditMode) {

                dbHelper.updateContact(
                        "" + id,
                        "" + image,
                        "" + name,
                        "" + phone,
                        "" + email,
                        "" + addedTime,
                        "" + timeStamp // updated time will new time
                );

                Toast.makeText(getApplicationContext(), "Updated Successfully....", Toast.LENGTH_SHORT).show();
                // Nakon uspješnog dodavanja, povratak na glavnu stranicu (MainActivity)
                Intent intent = new Intent(AddEditContact.this, MainActivity.class);
                startActivity(intent);

                // Zatvaranje trenutne aktivnosti (AddEditContact)
                finish();

            } else {

                long id = dbHelper.insertContact(
                        "" + imageUri,
                        "" + name,
                        "" + phone,
                        "" + email,
                        "" + timeStamp,
                        "" + timeStamp
                );

                Toast.makeText(getApplicationContext(), "Inserted Successfully.... " + id, Toast.LENGTH_SHORT).show();
                // Nakon uspješnog dodavanja, povratak na glavnu stranicu (MainActivity)
                Intent intent = new Intent(AddEditContact.this, MainActivity.class);
                startActivity(intent);

                // Zatvaranje trenutne aktivnosti (AddEditContact)
                finish();
            }

        } else {

            Toast.makeText(getApplicationContext(), "Nothing to save....", Toast.LENGTH_SHORT).show();
            // Nakon uspješnog dodavanja, povratak na glavnu stranicu (MainActivity)
            Intent intent = new Intent(AddEditContact.this, MainActivity.class);
            startActivity(intent);

            // Zatvaranje trenutne aktivnosti (AddEditContact)
            finish();
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result & result1;
    }

    //request za dozvolu za kameru
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_PERMISSION_CODE); // handle request permission on override method
    }

    //provjeravamo imamo li pristup storage-u
    private boolean checkStoragePermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result1;
    }


    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0) {

                    //if all permission allowed return true , otherwise false
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {

                        pickFromCamera();
                    } else {

                        Toast.makeText(getApplicationContext(), "Camera & Storage Permission needed..", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0) {

                    //if all permission allowed return true , otherwise false
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (storageAccepted) {

                        pickFromGallery();
                    } else {

                        Toast.makeText(getApplicationContext(), "Storage Permission needed..", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_FROM_GALLERY_CODE) {
                //izrezujemo sliku
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(AddEditContact.this);

            } else if (requestCode == IMAGE_FROM_CAMERA_CODE) {
                //izrezujemo sliku
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(AddEditContact.this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

                //dobivamo izrezanu sliku
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();
                profileIv.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Toast.makeText(getApplicationContext(), "Something wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

