package com.example.noteapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.noteapp.Model.Notes;

import kotlin.jvm.Synchronized;

@Database(entities = Notes.class, exportSchema = false, version = 1)
public abstract class RoomDBHelper extends RoomDatabase {

    private static final String DB_NAME = "NoteApp";
    private  static RoomDBHelper instance;

    public synchronized static RoomDBHelper getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, RoomDBHelper.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }

        return  instance;
    }
    public abstract MainDAO mainDAO();
}
