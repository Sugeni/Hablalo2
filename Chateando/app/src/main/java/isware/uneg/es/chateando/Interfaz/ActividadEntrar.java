package isware.uneg.es.chateando.Interfaz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import isware.uneg.es.chateando.R;

public class ActividadEntrar extends AppCompatActivity {

    private TextView tv_usuario_email;
    private TextView tv_usuario_pass;

    private Button  ingresar_btn;

    private Toolbar barra_Login;

    private FirebaseAuth mAuth;

    private DatabaseReference mLoginRefence;

    //Barra de progreso
    private ProgressDialog barraDeProgreso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_entrar);

        mAuth = FirebaseAuth.getInstance();

        barra_Login = (Toolbar) findViewById( R.id.barra_Herramientas_Registro);
        setSupportActionBar( barra_Login );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ingresar");

        mLoginRefence = FirebaseDatabase.getInstance().getReference().child("Usuarios");

        //Entrada del Usuario
        tv_usuario_email = (TextView) findViewById( R.id.tview_Login_Correo );
        tv_usuario_pass = (TextView) findViewById( R.id.tview_Login_Pass );
        ingresar_btn = (Button) findViewById( R.id.btn_Ingresar);

        //Barra de carga
        barraDeProgreso = new ProgressDialog(this);

        ingresar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String correo = tv_usuario_email.getText().toString().trim();
                String pass = tv_usuario_pass.getText().toString();
                if ( !( TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass) ) ){
                    barraDeProgreso.setCanceledOnTouchOutside(false);
                    barraDeProgreso.setTitle("Ingresando al sistema");
                    barraDeProgreso.setMessage("Por favor espere mientras nosotros"+
                                               " chequeamos sus credenciales.");
                    barraDeProgreso.show();

                    ingresar_Usuario(correo, pass);
                }
            }
        });
    }

    private void ingresar_Usuario(String correo, String pass) {

        mAuth.signInWithEmailAndPassword(correo, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    barraDeProgreso.dismiss();

                    String usuarioActual = mAuth.getUid();
                    String tokenID = FirebaseInstanceId.getInstance().getToken();

                    mLoginRefence.child( usuarioActual ).child("token_dispositivo").setValue( tokenID ).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Intent principal = new Intent( ActividadEntrar.this, ActividadPrincipal.class );
                            principal.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity( principal );
                            finish();


                        }
                    });

                                    }
                else{
                    barraDeProgreso.hide();
                    Toast.makeText(ActividadEntrar.this, "Autenticacion Fallida.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
