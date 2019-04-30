package com.marc.app.musicplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    private FirebaseStorage firebaseStorage; // Storage
    private FirebaseDatabase firebaseDatabase; // Realtime Database
    ArrayList<Song> songs = new ArrayList<>(); //lista de canciones sacadas de firebase db
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView); // Pillar el RecyclerView, es el contenedor de los ViewHolder y permite hacer scroll(recycler)
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Para que el listado sea en horizontal

        // Unimos el RecyclerView con el listado de canciones
        musicAdapter = new MusicAdapter();
        recyclerView.setAdapter(musicAdapter);

        // Obtener datos de Realtime Database
        firebaseDatabase = FirebaseDatabase.getInstance(); // Apunta el objeto a mi proyecto de Firebase

        // Para cada hijo del nodo raiz hace un listener
        firebaseDatabase.getReference().child("songs").addChildEventListener(new ChildEventListener() { // Añadir el child para cada elemento hijo del padre (musicplayer-97ddf) algo así
                                                                                                        // En este caso es /songs/ y de ahi ya se cogen todos los hijos
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { // Leer de Firebase
                Song song;

                song = dataSnapshot.getValue(Song.class); // Pillo el elemento del Firebase como tipo Song, tiene que tener un constructor vacio para que funcione

                songs.add(song); // Añadir el elemento sacado de firebase a la lista de canciones
                musicAdapter.notifyDataSetChanged(); // Avisar al adapter que se ha añadido un elemento
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        // Generar instacia que apunta mi Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance();
    }

     // El Adapter se encarga de enlazar el ViewHolder con el listado de datos, los prepara para pintarlos
    public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
        // Listado con los elementos a poner en el RecyclerView, cada uno de ellos ser

        @NonNull
        @Override
        public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { // Genera ViewHolder
            // Dibujara en pantalla los ViewHolder (elementos de la lista)
            View itemView = getLayoutInflater().inflate(R.layout.song_item, viewGroup, false);
            return new MusicViewHolder(itemView); // Retorno un nuevo ViewHolder
        }

        @Override
        public void onBindViewHolder(@NonNull final MusicViewHolder musicViewHolder, int i) { // Enlaza el listado con el ViewHolder
            // Poner el autor y el titulo de la cancion en el ViewHolder
            musicViewHolder.title.setText(songs.get(i).getTitle());
            musicViewHolder.author.setText(songs.get(i).getAuthor());

            // Tamaño maxima de la foto en este caso 1MB
            // Y la pillamos de una URL
            firebaseStorage.getReferenceFromUrl(songs.get(i).getImageURL()) // Pillamos el String con la URL de la imagen metida
            .getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length); // Transforma el Array de bytes con la foto para setearlo al ImageView
                    musicViewHolder.image.setImageBitmap(bitmap);
                }
            });

        }

        @Override
        public int getItemCount() { // Devuelve la cantidad de items de la lista
            return songs.size();
        }

        public class MusicViewHolder extends RecyclerView.ViewHolder { // Un ViewHolder es un elemento de la lista (su caja)
            ImageView image;
            TextView title, author;

            public MusicViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.titleTextView); // Buscar el ItemView de la vista del elemento de la lista
                author = itemView.findViewById(R.id.authorTextView);
                image = itemView.findViewById(R.id.imageView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(MainActivity.this, "Has pulsado en " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                        int position = getAdapterPosition();
                        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class); // Para pasar de pantalla a la Player pasandole paramentros
                        intent.putExtra("title", songs.get(position).getTitle());
                        intent.putExtra("author", songs.get(position).getAuthor());
                        intent.putExtra("imageURL", songs.get(position).getImageURL());
                        //Log.i("ImageURL", songs.get(position).getImageURL());
                        intent.putExtra("songURL", songs.get(position).getSongURL());
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
