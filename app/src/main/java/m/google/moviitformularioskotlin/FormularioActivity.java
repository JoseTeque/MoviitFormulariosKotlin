package m.google.moviitformularioskotlin;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import info.hoang8f.widget.FButton;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class FormularioActivity extends AppCompatActivity {
    private ImageView imageView;
    private MaterialEditText edtxName,edtxEmail,edtxPhone;
    private TextView txtName,txtEmail,txtAddress;
    private FButton btnGuardar;
    private FloatingActionButton fb;
    private FirebaseFirestore firestore;
    private static final int CAMERA_REQUEST_CODE= 1;
    private ProgressDialog mProgress;
    private StorageReference reference;

    String currentPhotoPath = "";
    Uri photoURI;
    private String TAG ="FormularioActivity";
    String dato1,dato2,dato3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        //view
        imageView= findViewById(R.id.IdImage);
        edtxName= findViewById(R.id.IdEdtxName);
        edtxEmail= findViewById(R.id.IdEdtxEmail);
        edtxPhone= findViewById(R.id.IdEdtxPhone);
        btnGuardar= findViewById(R.id.Id_Btn_GUARDAR);
         fb= findViewById(R.id.Id_fabFormu);
         txtName= findViewById(R.id.IdTxtNameQr);
         txtEmail= findViewById(R.id.IdTxtEmailQr);
         txtAddress= findViewById(R.id.IdTxtAddresQr);

         reference= FirebaseStorage.getInstance().getReference();
         firestore= FirebaseFirestore.getInstance();

     Bundle extras= this.getIntent().getExtras();
       if (extras!=null)
       {
           dato1= extras.getString("name");
           dato2= extras.getString("address");
           dato3= extras.getString("email");

           txtName.setText(dato1);
           txtAddress.setText(dato2);
           txtEmail.setText(dato3);
       }

         fb.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 Intent intent= new Intent(FormularioActivity.this,CodigoQRActivity.class);
                 startActivity(intent);

             }
         });

         mProgress= new ProgressDialog(this);

         imageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dispatchTakePictureIntent();

             }
         });

         btnGuardar.setOnClickListener(new View.OnClickListener() {
             @TargetApi(Build.VERSION_CODES.KITKAT)
             @Override
             public void onClick(View v) {

                 if ( Objects.requireNonNull(edtxName.getText()).toString().isEmpty() ||  Objects.requireNonNull(edtxPhone.getText()).toString().isEmpty() ||  Objects.requireNonNull(edtxEmail.getText()).toString().isEmpty() ){
                     Toast.makeText(FormularioActivity.this, " Ingrese Todos los campos", Toast.LENGTH_SHORT).show();
                     return;
                 }

                 AddNewContact();

                 Intent intent= new Intent(FormularioActivity.this,MainActivity.class);
                 startActivity(intent);
             }
         });

    }


    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
              Log.d("ERROR",ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                 photoURI = FileProvider.getUriForFile(this,
                        "m.google.moviitformularioskotlin",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void AddNewContact() {
        String name = Objects.requireNonNull(edtxName.getText()).toString();
        String email = Objects.requireNonNull(edtxEmail.getText()).toString();
        String phone = Objects.requireNonNull(edtxPhone.getText()).toString();


        Calendar calendar= Calendar.getInstance();
        String date= DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar.getTime());


            Map<String, Object> user = new HashMap<>();
            user.put("nombre", name);
            user.put("email", email);
            user.put("phone", phone);
            user.put("fecha",date);
            user.put("Name",dato1);
            user.put("Address",dato2);
            user.put("Email",dato3);


            firestore.collection("ListaFormulario").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(FormularioActivity.this, " Se añadio con exito", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FormularioActivity.this, " Carga fallida", Toast.LENGTH_SHORT).show();
                }
            });
        }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CAMERA_REQUEST_CODE && resultCode == RESULT_OK )
        {
            mProgress.setMessage("Cargando..");

            mProgress.show();



              Log.d("FormularioActivity", "Camara okk");

          final StorageReference filepath= reference.child("Photos").child(Objects.requireNonNull(photoURI.getLastPathSegment()));

            filepath.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgress.dismiss();
                    Toast.makeText(FormularioActivity.this, "Se cargo la imagen..!!", Toast.LENGTH_SHORT).show();

                    Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                    imageView.setImageBitmap(bitmap);
                }
            });



        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
