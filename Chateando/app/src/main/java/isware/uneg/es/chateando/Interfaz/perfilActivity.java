package isware.uneg.es.chateando.Interfaz;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import isware.uneg.es.chateando.Logica.ListaString;
import isware.uneg.es.chateando.R;

import static isware.uneg.es.chateando.Logica.ListaString.agregarLista;
import static isware.uneg.es.chateando.Logica.ListaString.eliminarLista;
import static isware.uneg.es.chateando.Logica.ListaString.enLista;

public class perfilActivity extends AppCompatActivity {

    private TextView displayName;
    private TextView displayStatus;
    private TextView diplayNAmigos;

    private Button solicitudAmistad_btn;
    private Button ignorado_btn;
    private Button rechazar_btn;

    private CircleImageView displayImage;

    private DatabaseReference mUsuarioDatabaseReference;

    private DatabaseReference mSolicitudDatabaseReference;

    private DatabaseReference mAmigosDatabaseReference;

    private DatabaseReference mNotificacionesDatabaseReference;

    private DatabaseReference mIgnoradosDatabaseReference;

    private DatabaseReference mRootRef;


    private FirebaseUser mUsuarioActual;

    private ProgressDialog mProgressdialog;

    private String mCurrent_state;
    private String mIgnorado;
    private String datos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        mIgnorado = "No";

        final String uid = getIntent().getStringExtra("user_id");

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsuarioDatabaseReference = FirebaseDatabase.getInstance().
                getReference().child("Usuarios").child(uid);

        mSolicitudDatabaseReference = FirebaseDatabase.getInstance().
                getReference().child("Solicitudes_Amistad");

        mAmigosDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Amigos");

        mNotificacionesDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Notificaciones");

        mUsuarioActual = FirebaseAuth.getInstance().getCurrentUser();


        mIgnoradosDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        displayName = findViewById( R.id.perfil_name);
        displayName.setText( uid );

        displayStatus = findViewById( R.id.perfil_status);
        diplayNAmigos = findViewById(R.id.perfil_amigos);
        solicitudAmistad_btn = findViewById( R.id.perfil_solicitud_btn);
        displayImage = findViewById( R.id.perfil_imagen);

        ignorado_btn = findViewById(R.id.pefil_ignorar_btn);

        rechazar_btn = findViewById(R.id.perfil_Rsolicitud_btn);

        mCurrent_state = "no_amigo";

        rechazar_btn.setVisibility( View.INVISIBLE);
        rechazar_btn.setEnabled( false);

        mProgressdialog = new ProgressDialog( this);
        mProgressdialog.setTitle("Cargando datos del Usuario");
        mProgressdialog.setMessage("Por favor espere, mientras cargamos los datos del usuario");
        mProgressdialog.setCanceledOnTouchOutside(false);
        mProgressdialog.show();





        mIgnoradosDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String ignorados = dataSnapshot.child(mUsuarioActual.getUid()).child("ignorados").getValue().toString();

                datos = ignorados;

                String []listaIgnorados;

                listaIgnorados = ignorados.split(" ");
                ignorados = uid;


                if( enLista(listaIgnorados, ignorados)){
                    mIgnorado ="Si";
                    ignorado_btn.setText("Eliminar de ignorados");


                }
                else{

                    mIgnorado ="No";
                    ignorado_btn.setText("Ignorar");

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mUsuarioDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String ignorados = dataSnapshot.child("ignorados").getValue().toString();
                String nombre = dataSnapshot.child("nombre").getValue().toString();
                String estado = dataSnapshot.child("status").getValue().toString();
                String imagen1 = dataSnapshot.child("imagenA").getValue().toString();

                if(ListaString.enLista( ignorados.split(" "), mUsuarioActual.getUid())){
                    estado = dataSnapshot.child("status2").getValue().toString();
                    imagen1 = dataSnapshot.child("imagenS").getValue().toString();
                }



                displayName.setText(nombre);
                displayStatus.setText( estado);
                if(!imagen1.equals("default")){
                    Picasso.get().load(imagen1).placeholder(R.drawable.perfil).into(displayImage);
                }

                //Solicitudes de amistad

                mSolicitudDatabaseReference.child(mUsuarioActual.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(dataSnapshot.hasChild(uid)){

                                    String request_type = dataSnapshot.child( uid).

                                            child("tipo_solicitudes").getValue().toString();


                                    if( request_type.equals("recibida")){

                                        mCurrent_state = "solicitud_recibida";
                                        solicitudAmistad_btn.setText("Aceptar solicitud");

                                        rechazar_btn.setVisibility( View.VISIBLE);
                                        rechazar_btn.setEnabled( true);

                                    }
                                    else if(request_type.equals("enviado")){

                                                mCurrent_state ="solicitud_enviada";
                                                solicitudAmistad_btn.setText("Cancelar solicitud de amistad");
                                                rechazar_btn.setVisibility( View.INVISIBLE);
                                                rechazar_btn.setEnabled( false);
                                    }

                                    mProgressdialog.dismiss();

                                } else{

                                    mAmigosDatabaseReference.child(mUsuarioActual.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.hasChild(uid)){
                                                mCurrent_state = "amigos";
                                                solicitudAmistad_btn.setText("Dejar de ser Amigos");

                                                rechazar_btn.setVisibility( View.INVISIBLE);
                                                rechazar_btn.setEnabled( false);
                                            }
                                            mProgressdialog.dismiss();


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                            mProgressdialog.dismiss();

                                        }
                                    });
                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        solicitudAmistad_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                solicitudAmistad_btn.setEnabled(false);


                //   -------------------------  Si No son Amigos
                if (mCurrent_state.equals("no_amigo")) {

                    DatabaseReference newNotificationRef = mRootRef.child("Notificaciones").child(uid).push();
                    String newNotificationId = newNotificationRef.getKey();


                    HashMap<String , String> datosNotificaciones = new HashMap<String, String>();
                    datosNotificaciones.put("from", mUsuarioActual.getUid());
                    datosNotificaciones.put("type","request");

                    Map requestMap = new HashMap();
                    requestMap.put("Solicitudes_Amistad/" + mUsuarioActual.getUid() + "/" + uid + "/tipo_solicitudes", "enviado");
                    requestMap.put("Solicitudes_Amistad/" + uid  + "/" + mUsuarioActual.getUid()+ "/tipo_solicitudes", "recibida");
                    requestMap.put("Notificaciones/" + uid + "/" + newNotificationId, datosNotificaciones);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {


                            if( databaseError != null){

                                Toast.makeText(perfilActivity.this,"Ocurrio un error", Toast.LENGTH_LONG).show();

                            }


                            mCurrent_state = "solicitud_enviada";
                            solicitudAmistad_btn.setEnabled(true);
                            solicitudAmistad_btn.setText("Cancelar solicitud");

                            rechazar_btn.setVisibility( View.INVISIBLE);
                            rechazar_btn.setEnabled( false);

                        }
                    });
                }
                // ---------------------------------------------Si mando la solicitud
                if (mCurrent_state.equals("solicitud_enviada")) {


                    mSolicitudDatabaseReference.child(mUsuarioActual.getUid())
                            .child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mSolicitudDatabaseReference.child(uid).child(mUsuarioActual.getUid())
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    solicitudAmistad_btn.setEnabled(true);
                                    mCurrent_state = "no_amigo";
                                    solicitudAmistad_btn.setText("Mandar Solicitud");

                                    rechazar_btn.setVisibility( View.INVISIBLE);
                                    rechazar_btn.setEnabled( false);


                                }
                            });
                        }
                    });

                }


                //---------------------------Solicitud Recibida

                if(mCurrent_state.equals("solicitud_recibida"))

                {

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    Map friendMap = new HashMap();

                    friendMap.put("Amigos/" + mUsuarioActual.getUid() + "/" + uid + "/fecha", currentDate);
                    friendMap.put("Amigos/"+ uid + "/" + mUsuarioActual.getUid() + "/fecha", currentDate);

                    friendMap.put("Solicitudes_Amistad/" +mUsuarioActual.getUid() + "/" + uid  , null);
                    friendMap.put("Solicitudes_Amistad/" + uid + "/" + mUsuarioActual.getUid() , null);

                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                solicitudAmistad_btn.setEnabled(true);
                                mCurrent_state = "amigos";
                                solicitudAmistad_btn.setText("Dejar de ser Amigos");

                                rechazar_btn.setVisibility( View.INVISIBLE);
                                rechazar_btn.setEnabled( false);

                            }else{

                                String error = databaseError.getMessage();
                                Toast.makeText(perfilActivity.this, error, Toast.LENGTH_LONG);

                            }


                        }
                    });

                }


                //--------------------------Unfriend : Dejar de ser Amigos

                if( mCurrent_state.equals("amigos")){

                    Map unFriend = new HashMap();
                    unFriend.put("Amigos/" + mUsuarioActual.getUid() + "/" + uid , null );
                    unFriend.put("Amigos/" + uid + "/" + mUsuarioActual.getUid()  , null );

                    mRootRef.updateChildren(unFriend, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                mCurrent_state = "no_amigo";
                                solicitudAmistad_btn.setText("Dejar de ser Amigos");

                                rechazar_btn.setVisibility( View.INVISIBLE);
                                rechazar_btn.setEnabled( false);

                            }else{

                                String error = databaseError.getMessage();
                                Toast.makeText(perfilActivity.this, error, Toast.LENGTH_LONG);

                            }

                            solicitudAmistad_btn.setEnabled(true);

                        }
                    });



                }
            }

        });


        ignorado_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                accionIgnorar( uid);

            }
        });

        }



        private void accionIgnorar( final String uid){
            ignorado_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String [] lista;
                    lista = datos.split(" ");
                    if(mIgnorado.equals("No")){

                        mIgnorado ="Si";
                        ignorado_btn.setText("Eliminar de ignorados");

                        mIgnoradosDatabaseReference.child(mUsuarioActual.getUid()).child("ignorados").setValue(agregarLista(lista, uid));



                    }else if(mIgnorado.equals("Si")){

                        mIgnorado ="No";
                        ignorado_btn.setText("Ignorar");
                        mIgnoradosDatabaseReference.child(mUsuarioActual.getUid()).child("ignorados").setValue(eliminarLista( lista, uid));

                    }

                }
            });
        }

    }

