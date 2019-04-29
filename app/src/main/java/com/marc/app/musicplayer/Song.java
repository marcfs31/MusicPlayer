package com.marc.app.musicplayer;

public class Song {
    private String imageURL;
    private String songURL;
    private String title;
    private String author;

    public Song() {
    }

    public Song(String imageURI, String songURI, String title, String author) {
        this.imageURL = imageURI;
        this.songURL = songURI;
        this.title = title;
        this.author = author;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getSongURL() {
        return songURL;
    }

    public void setSongURL(String songURL) {
        this.songURL = songURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
