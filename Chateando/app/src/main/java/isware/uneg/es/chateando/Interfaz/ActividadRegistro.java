package isware.uneg.es.chateando.Interfaz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import isware.uneg.es.chateando.R;

public class ActividadRegistro extends AppCompatActivity {

    private TextView tv_usuario_nombre;
    private TextView tv_usuario_email;
    private TextView tv_usuario_pass;

    private Button registrar_btn;

    private Toolbar barra_Registro;

    // Auth Firebase
    private FirebaseAuth mAuth;

    // Base de datos
    private DatabaseReference mDatabase;

    //Barra de progreso
    private ProgressDialog barraDeProgreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_registro);


        //Auth Firebase
            mAuth = FirebaseAuth.getInstance();

        //Ajustar barra de Herramientas
            barra_Registro = (Toolbar) findViewById( R.id.barra_Herramientas_Registro);
            setSupportActionBar( barra_Registro);
            getSupportActionBar().setTitle("Crear Cuenta");
            getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        //Barra de progreso
            barraDeProgreso = new ProgressDialog(this);
        //Campos de texto en Android y el Botton
            tv_usuario_nombre = (TextView) findViewById( R.id.tview_Registro_Usuario);
            tv_usuario_email = (TextView) findViewById( R.id.tview_Registro_Correo );
            tv_usuario_pass = (TextView) findViewById( R.id.tview_Registro_Pass );
            registrar_btn = (Button) findViewById( R.id.btn_Registro);

            registrar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = tv_usuario_nombre.getText().toString().trim();
                String correo = tv_usuario_email.getText().toString().trim();
                String pass = tv_usuario_pass.getText().toString();
            if( (!nombre.equals("") ) & !(correo.equals("")) & !(pass.equals("")) ){

                barraDeProgreso.setTitle("Registrando Usuario");
                barraDeProgreso.setMessage("Por favor, espere mientras la cuenta es creada");
                barraDeProgreso.setCanceledOnTouchOutside(false);
                barraDeProgreso.show();
                registrar_Usuario( nombre, correo, pass);
            }
            else
                Toast.makeText(ActividadRegistro.this, "Autenticacion Fallida: por favor complete los datos pedidos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrar_Usuario(final String nombre, String correo, String pass) {

        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser usuarioActual = FirebaseAuth.getInstance().getCurrentUser();
                            String idUsuario = usuarioActual.getUid();
                            String tokenID = FirebaseInstanceId.getInstance().getToken();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(idUsuario);

                            HashMap<String , String> userMap = new HashMap<String, String>();
                            userMap.put("nombre", nombre);
                            userMap.put( "status","Hola a todos, Estoy usando Háblalo");
                            userMap.put("status2", "Hola a todos, Estoy usando Háblalo");
                            userMap.put("imagenA", "default");
                            userMap.put("imagenS", "default");
                            userMap.put("thumb_image1", "default");
                            userMap.put("thumb_image2", "default");
                            userMap.put("ignorados", "");
                            userMap.put("token_dispositivo", tokenID);
                            mDatabase.setValue( userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if( task.isSuccessful()) {
                                        //Si la actividad fue completada desaparece la barra de Progreso
                                        barraDeProgreso.dismiss();

                                        Intent principal = new Intent(ActividadRegistro.this
                                                , ActividadPrincipal.class);
                                        principal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(principal);

                                        finish();
                                    }
                                }
                            });




                        } else {

                            barraDeProgreso.hide();
                            Toast.makeText(ActividadRegistro.this, "Autenticacion Fallida.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }


}
