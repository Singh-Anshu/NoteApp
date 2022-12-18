package com.example.noteapp.Database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.noteapp.Model.Notes;

import java.util.List;

@Dao
public interface MainDAO {

    @Query("SELECT * FROM notes ORDER BY id DESC ")
    List<Notes> getAll();

    @Insert(onConflict = REPLACE)
    void insert(Notes notes);

    @Query(" UPDATE notes SET title = :title, notes = :notes WHERE id = :id ")
    void update(int id, String title, String notes);

    @Delete
    void delete(Notes notes);

    @Query(" update notes set Pinned = :pin where id = :id")
    void pin(int id, boolean pin);
}
