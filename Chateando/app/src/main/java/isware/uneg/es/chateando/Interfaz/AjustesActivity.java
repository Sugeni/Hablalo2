package isware.uneg.es.chateando.Interfaz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import isware.uneg.es.chateando.R;

public class AjustesActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    File thumbFilePath;
    byte[] datas;

    //
    CircleImageView imagenA, imagenS;
    TextView estadoTV;
    TextView nombreTV;

    Button estado, imagenABtn, imagenSBtn;

    //Almacenamiento Firebase
    private StorageReference mImageStorage;


    private final int GALERY_PICK = 1;

    private int select;

    private ProgressDialog mProgresDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(current_uid);
        mUserDatabase.keepSynced( true);
        // Interfaz
        imagenA = findViewById( R.id.ajustesImagenA);
        imagenS = findViewById( R.id.ajustesImagenS);

        estadoTV = findViewById(R.id.ajustesTVDescripcion);
        nombreTV = findViewById(R.id.ajustesTvUsuario);


        estado = findViewById( R.id.ajustesCEstado);
        imagenABtn = findViewById(R.id.ajustesCImagenA);
        imagenSBtn = findViewById(R.id.ajustesCImagenS);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        //Eventos de la base de datos
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String estado = dataSnapshot.child("status").getValue().toString();
                final String imagen1 = dataSnapshot.child("imagenA").getValue().toString();
                final String imagen2 = dataSnapshot.child("imagenS").getValue().toString();
                String thumb_image1 = dataSnapshot.child("thumb_image1").getValue().toString();
                String thumb_image2 = dataSnapshot.child("thumb_image2").getValue().toString();

                nombreTV.setText(nombre);
                estadoTV.setText(estado);


                if(!imagen1.equals("default")){


                    Picasso.get().load(imagen1).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.perfil).into(imagenA, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(imagen1).placeholder(R.drawable.perfil).into(imagenA);
                        }
                    });
                }


                if(!imagen2.equals("default")){

                    Picasso.get().load(imagen2).placeholder(R.drawable.perfil).into(imagenS);

                }




            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        estado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String status_value = estadoTV.getText().toString();
                 Intent cambio = new Intent( AjustesActivity.this, EstadoActivity.class);


                 //Mutex
                 cambio.putExtra("status_value", status_value);

                 startActivity( cambio);

            }
        });

        imagenABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select= 1;
                Intent galeryIntent = new Intent();
                galeryIntent.setType("image/*");
                galeryIntent.setAction( Intent.ACTION_GET_CONTENT);

                startActivityForResult( Intent.createChooser(galeryIntent, "Seleccionar Imagen"), GALERY_PICK);

            }
        });

        imagenSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select= 2;
                Intent galeryIntent = new Intent();
                galeryIntent.setType("image/*");
                galeryIntent.setAction( Intent.ACTION_GET_CONTENT);

                startActivityForResult( Intent.createChooser(galeryIntent, "Seleccionar Imagen"), GALERY_PICK);

            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALERY_PICK && resultCode == RESULT_OK) {

            Uri  imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1 , 1)
                    .start(this);
            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
             if (resultCode == RESULT_OK) {

                mProgresDialog = new ProgressDialog( AjustesActivity.this);
                mProgresDialog.setTitle("Cargando Imagen");
                mProgresDialog.setCanceledOnTouchOutside( false );
                mProgresDialog.setMessage("");

                Uri resultUri = result.getUri();

                thumbFilePath = new File( resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();


                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap( thumbFilePath);



                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress( Bitmap.CompressFormat.JPEG, 100, baos);
                 datas = baos.toByteArray();

                StorageReference filepath;
                final StorageReference thumb_filepath;

                if( select == 1) {

                    filepath = mImageStorage.child("profile_images")
                            .child(current_user_id + ".jpg");
                    thumb_filepath = mImageStorage.child("profile_images").child("thumbs")
                            .child(current_user_id + ".jpg");
                }
                else{

                    filepath = mImageStorage.child("profile_images_s")
                            .child(current_user_id + "2" + ".jpg");
                    thumb_filepath = mImageStorage.child("profile_images_s").child("thumbs")
                            .child(current_user_id + "2" + ".jpg");
                }

                mProgresDialog.show();


                filepath.putFile( resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       if( task.isSuccessful()){

                           final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(datas);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    Map update_Hasmap = new HashMap<String, String>();

                                    if( select == 1){

                                        update_Hasmap.put( "imagenA" , downloadUrl);
                                        update_Hasmap.put("thumb_image1", thumb_downloadUrl);
                                        mUserDatabase.updateChildren(update_Hasmap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            mProgresDialog.dismiss();
                                                        }else{

                                                        }
                                                    }
                                                });

                                    } else{

                                        update_Hasmap.put( "imagenS" , downloadUrl);
                                        update_Hasmap.put("thumb_image2", thumb_downloadUrl);
                                        mUserDatabase.updateChildren(update_Hasmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){
                                                            mProgresDialog.dismiss();
                                                        }else{

                                                        }
                                                    }
                                                });
                                    }

                                }
                            });
                                                  }
                   }
               });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        }


    }

