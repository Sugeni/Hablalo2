package isware.uneg.es.chateando.Interfaz;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import isware.uneg.es.chateando.Logica.ListaString;
import isware.uneg.es.chateando.R;
import isware.uneg.es.chateando.Logica.Usuarios;

public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUserList;

    private DatabaseReference mUsersDatabase;
    private FirebaseUser mCurrentUser;

    private String id;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        id = mCurrentUser.getUid().toString();

        setContentView(R.layout.activity_users);

        mToolbar = findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("Todos los usuarios");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        mLayoutManager = new LinearLayoutManager(this);
        mUsersDatabase.keepSynced( true);

        mUserList = findViewById(R.id.users_lista);;
        mUserList.setLayoutManager(mLayoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Usuarios, UsersViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Usuarios, UsersViewHolder>(

                Usuarios.class,
                R.layout.users_single_layout,
                UsersViewHolder.class,
                mUsersDatabase

        )
        {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, Usuarios model, int position) {


                if(  !getRef(position).getKey().equals(id)) {


                    usersViewHolder.setName(model.getNombre());

                    if( !ListaString.enLista(model.getIgnorados().split(" ") , id )) {

                        usersViewHolder.setEstado(model.getStatus());
                        usersViewHolder.setImagenA(model.getImagenA());

                    }else{

                        usersViewHolder.setEstado(model.getStatus2());
                        usersViewHolder.setImagenA(model.getImagenS());
                    }



                    final String user_id = getRef(position).getKey();

                    usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent usuarioIntent = new Intent(UsersActivity.this, perfilActivity.class);
                            usuarioIntent.putExtra("user_id", user_id);
                            startActivity(usuarioIntent);
                        }
                    });
                }
                else{
                    usersViewHolder.setName("Tu");
                }


            }
        };

        firebaseRecyclerAdapter.startListening();
        mUserList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {


            TextView mUserNameView = mView.findViewById(R.id.user_single_name);
            mUserNameView.setText(name);

        }

        public void setEstado(String estado) {
            TextView mStatusView = mView.findViewById( R.id.user_single_status);
            mStatusView.setText( estado);
        }

        public void setImagenA(String imagen){
            if(!imagen.equals("default")) {
                CircleImageView imagenA = mView.findViewById(R.id.user_single_image);
                Picasso.get().load(imagen).into(imagenA);
            }
        }
    }
}






