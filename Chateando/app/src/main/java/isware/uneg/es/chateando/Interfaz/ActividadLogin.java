package isware.uneg.es.chateando.Interfaz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import isware.uneg.es.chateando.R;

public class ActividadLogin extends AppCompatActivity {

    // boton registro login
    private Button btnRegLog;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_login);
        btnRegLog = findViewById( R.id.ini_Registro_btn);
        btnLogin = findViewById( R.id.ini_Login_btn);

        btnRegLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent reg_Intent = new Intent( ActividadLogin.this, ActividadRegistro.class);
                startActivity( reg_Intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log_Intent = new Intent( ActividadLogin.this, ActividadEntrar.class);
                startActivity( log_Intent);
            }
        });
    }
}
