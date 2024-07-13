package com.example.wifilocation.locate;

import android.os.Parcel;
import android.os.Parcelable;

public class Book extends Marker implements Parcelable {
    private int id;
    private String name;

    // Constructors
    public Book() { }

    public Book(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Book(int id, String name, float x, float y, float z, String room) {
        this.id = id;
        this.name = name;
        setScaleX(x);
        setScaleY(y);
        setFloorZ(z);
        setRoom(room);
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

    // Parcelable implementation
    protected Book(Parcel in) {
        id = in.readInt();
        name = in.readString();
        super.setName("Book");
        setScaleX(in.readFloat());
        setScaleY(in.readFloat());
        setFloorZ(in.readFloat());
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
        dest.writeFloat(getScaleX());
        dest.writeFloat(getScaleY());
        dest.writeFloat(getFloorZ());
    }
}
