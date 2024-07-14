package com.example.wifilocation.locate;

import android.os.Parcel;
import android.os.Parcelable;

public class Book extends Marker implements Parcelable {
    private int id;
    private String name;
    private String book_room;

    // Constructors
    public Book() {
    }

    public Book(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Book(int id, String name, String book_room) {
        this.id = id;
        this.name = name;
        this.book_room = book_room;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBook_Room() {
        return book_room;
    }

    // Parcelable implementation
    protected Book(Parcel in) {
        id = in.readInt();
        name = in.readString();
        book_room = in.readString();
        super.setName("Book");
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(book_room);
    }

    @Override
    public String toString() {
        return this.name + this.book_room;
    }
}
