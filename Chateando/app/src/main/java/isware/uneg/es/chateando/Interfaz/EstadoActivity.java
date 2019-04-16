package isware.uneg.es.chateando.Interfaz;


import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import isware.uneg.es.chateando.R;

public class EstadoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    TextView estadoTI;
    TextView estadoTS;
    Button guardarBtn;

    //Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //Progres Dialog
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(current_uid);

        //Toolbar
        mToolbar = findViewById( R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Ajustes de la cuenta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true );



       String status_value = getIntent().getStringExtra("status_value");


        estadoTI = findViewById( R.id.estado_input_a);
        estadoTS = findViewById( R.id.estado_input_s);
        guardarBtn = findViewById( R.id.estado_guardarBtn);

        estadoTI.setText(status_value);
        guardarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Barra de progreso
                mProgressDialog = new ProgressDialog(EstadoActivity.this);
                mProgressDialog.setTitle("Guardando cambios");
                mProgressDialog.setMessage("Cambiando estado, por favor espere");
                mProgressDialog.show();

                final String estado = estadoTI.getText().toString();
                mStatusDatabase.child("status").setValue(estado).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                           String estado = estadoTS.getText().toString();
                           mStatusDatabase.child("status2").setValue(estado);
                           mProgressDialog.dismiss();

                        }
                        else{

                            Toast.makeText( getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
