package com.marc.app.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.IOException;

public class PlayerActivity extends AppCompatActivity {
    String imageURL,songURL,title,author;
    MediaPlayer mediaPlayer;
    ImageButton imageButton;
    ImageView imageView;
    private FirebaseStorage firebaseStorage; // Storage
    Bitmap bitmap;
    TextView titleTextView, authorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        imageURL = getIntent().getExtras().getString("imageURL");
        //Log.i("Image2", getIntent().getExtras().getString("imageURL"));
        title = getIntent().getExtras().getString("title");
        author = getIntent().getExtras().getString("author");
        songURL = getIntent().getExtras().getString("songURL");

        imageButton = findViewById(R.id.imgBtn);
        imageView = findViewById(R.id.image);
        titleTextView = findViewById(R.id.titleTextView);
        authorTextView = findViewById(R.id.authorTextView);

        // Pongo el texto recibido por PutExtra en los TextView
        titleTextView.setText(title);
        authorTextView.setText(author);

        // Generar instacia que apunta mi Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance();

        // Cogemos la imagen de Firebase con la URL pasado por PutExtra
        firebaseStorage.getReferenceFromUrl(imageURL) // Pillamos el String con la URL de la imagen metida en el PutExtra
        .getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length); // Transforma el Array de bytes con la foto para setearlo al ImageView
                imageView.setImageBitmap(bitmap);
            }
        });

        // Listener para ejecutar la musica de la URL y redimensionar la imagen
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reproducir la musica desde la URL
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(songURL);  // Le pasamos la URL de la cancion
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //imageView.animate().scaleXBy((float) 1.0).scaleYBy((float) 1.0); // Hacer la foto grande
                imageView.animate().scaleX((float) 2.0).scaleY((float) 2.0); // Hacer la foto grande
                imageButton.animate().alpha(0); // Hacer desaparecer el boton
            }
        });

        // Listener para la imagen
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) { // Si la musica esta sonando se pausa
                    mediaPlayer.pause();
                    imageView.animate().scaleY((float) 1.0).scaleX((float) 1.0); // Volver a poner la foto como estaba
                    imageButton.animate().alpha(1); // Hacer aparecer el botón
                    //imageView.animate().scaleXBy((float) -1.0).scaleYBy((float) -1.0); // Volver a poner la imagen como estaba
                } else { // Sino se inicia
                    mediaPlayer.start();
                }
            }
        });
    }
}
