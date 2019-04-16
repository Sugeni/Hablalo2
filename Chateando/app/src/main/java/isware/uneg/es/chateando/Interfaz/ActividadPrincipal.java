package isware.uneg.es.chateando.Interfaz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import isware.uneg.es.chateando.R;
import isware.uneg.es.chateando.Logica.SectionsPagerAdapter;

public class ActividadPrincipal extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Toolbar barra;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;

    private DatabaseReference mUserRef; 

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_principal);

        mAuth = FirebaseAuth.getInstance();


        barra = (Toolbar) findViewById( R.id.barra_Herramientas_Principal);
        setSupportActionBar( barra );
        getSupportActionBar().setTitle("Háblalo!");

        if (mAuth.getCurrentUser() != null) {

            mUserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios").child(mAuth.getCurrentUser().getUid());
        }
        //Pestanas
        mViewPager = (ViewPager) findViewById(R.id.mainTabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager() );

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById( R.id.main_tabs);
        mTabLayout.setupWithViewPager( mViewPager);
 }

    @Override
    public void onStart(){
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        //Se comprueba si el usuario esta registrado en el sistema
        if( currentUser == null){
            mandarAlInicio();
        }   else{
            mUserRef.child("online").setValue(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if( currentUser != null) {
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
        }

    private void mandarAlInicio() {

        Intent loginIntent =  new Intent( ActividadPrincipal.this,
                ActividadLogin.class);
        startActivity( loginIntent);
        finish();
    }

    private void updateUI(FirebaseUser currentUser) {



    }

    public boolean onCreateOptionsMenu( Menu menu){

        super.onCreateOptionsMenu( menu );
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if( item.getItemId() == R.id.desconectar_Btn) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();

            Intent regresar = new Intent(ActividadPrincipal.this, ActividadLogin.class);
            regresar.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity( regresar);

        }
        if(item.getItemId() ==  R.id.users_Btn){

            Intent usuariosIntent = new Intent( ActividadPrincipal.this , UsersActivity.class);
            startActivity( usuariosIntent);

            }

        else if(item.getItemId() ==  R.id.configCuenta_Btn){
            Intent config = new Intent(ActividadPrincipal.this , AjustesActivity.class);
            startActivity( config);
        }
        return true;
    }
}
