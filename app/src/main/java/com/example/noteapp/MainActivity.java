package com.example.noteapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.RoomDatabase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.noteapp.Adapter.NotesListAdapter;
import com.example.noteapp.Database.RoomDBHelper;
import com.example.noteapp.Model.Notes;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    RecyclerView recyclerView;
    NotesListAdapter notesListAdapter;
    ArrayList<Notes> notesArrayList = new ArrayList<>();
    RoomDBHelper roomDatabase;
    FloatingActionButton fab_add;
    SearchView searchview_home;
    Notes selectedNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyler_home);
        fab_add = findViewById(R.id.fab_add);
        searchview_home = findViewById(R.id.searchview_home);
        roomDatabase = RoomDBHelper.getInstance(this);

        notesArrayList = (ArrayList<Notes>) roomDatabase.mainDAO().getAll();

        updateRecycler(notesArrayList);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
                startActivityForResult(intent,101);
            }
        });

        searchview_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

    }

    private void filter(String newText) {
        ArrayList<Notes> filterlist = new ArrayList<>();

        for(Notes singleNote: notesArrayList){
            if(singleNote.getTitle().toLowerCase().contains(newText.toLowerCase())
            || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())){
                filterlist.add(singleNote);
            }
        }
        notesListAdapter.filterList(filterlist);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            if(resultCode == Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                roomDatabase.mainDAO().insert(new_notes);
                notesArrayList.clear();
                notesArrayList.addAll(roomDatabase.mainDAO().getAll());
             //   notesListAdapter.notifyDataSetChanged();
                notesListAdapter.notifyData();
            }
        }
        else if(requestCode == 102){
            if(resultCode == Activity.RESULT_OK){
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                roomDatabase.mainDAO().update(new_notes.getId(),new_notes.getTitle(),new_notes.getNotes());
                notesArrayList.clear();
                notesArrayList.addAll(roomDatabase.mainDAO().getAll());
              //  notesListAdapter.notifyDataSetChanged();
                notesListAdapter.notifyData();
            }
        }
    }

    private void updateRecycler(ArrayList<Notes> notesArrayList) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        notesListAdapter = new NotesListAdapter(MainActivity.this, notesArrayList, notesClickListener);
        recyclerView.setAdapter(notesListAdapter);

    }


    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(MainActivity.this, NotesTakerActivity.class);
            intent.putExtra("old_note", notes);
            startActivityForResult(intent, 102);

        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNotes = new Notes();
            selectedNotes = notes;
            showPopup(cardView);

        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this,cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.pin:
            if(selectedNotes.isPin()){
                roomDatabase.mainDAO().pin(selectedNotes.getId(), false);
                Toast.makeText(this, "UnPinned!", Toast.LENGTH_SHORT).show();
            }else{
                roomDatabase.mainDAO().pin(selectedNotes.getId(), true);
                Toast.makeText(this, "Pinned!", Toast.LENGTH_SHORT).show();
            }

            notesArrayList.clear();
            notesArrayList.addAll(roomDatabase.mainDAO().getAll());
           // notesListAdapter.notifyDataSetChanged();
                notesListAdapter.notifyData();
            return  true;

            case R.id.delete:

                //roomDatabase.mainDAO().delete(selectedNotes);
                notesArrayList.remove(selectedNotes);
               // notesListAdapter.notifyDataSetChanged();
                notesListAdapter.notifyData();
               // Toast.makeText(this, "Note Deleted!", Toast.LENGTH_SHORT).show();
                View view = getWindow().getDecorView().findViewById(R.id.delete);
                showSnackBar(findViewById(R.id.delete));
                return  true;

            default:
                return false;
        }

    }

    private void showSnackBar(View view){


        try {
            Snackbar.make(recyclerView,"Note move to Bin",10000)
                    .setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e("Step1","Undo Clicked");
                  // Toast.makeText(MainActivity.this, "Undo Clicked", Toast.LENGTH_SHORT).show();
                    notesArrayList.clear();
                    notesArrayList.addAll(roomDatabase.mainDAO().getAll());
                    notesListAdapter.notifyData();
                }
            })
                    .addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);
                            if (event == DISMISS_EVENT_TIMEOUT || event == DISMISS_EVENT_SWIPE
                                    || event == DISMISS_EVENT_CONSECUTIVE || event == DISMISS_EVENT_MANUAL) {
                                Log.e("Step2","Note Deleted");
                                roomDatabase.mainDAO().delete(selectedNotes);
                            }
                        }
                    })
                    .show();


           /* Snackbar snackbar = Snackbar.make(recyclerView,"",3000);
            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            View view1 = LayoutInflater.from(this).inflate(R.layout.snackbar_layout, null);
            view1.findViewById(R.id.undo_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Undo Clicked", Toast.LENGTH_SHORT).show();
                }
            });
            snackbarLayout.setPadding(0,0,0,0);
            snackbarLayout.addView(view1);
            snackbar.show();*/

        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("showSnackBar",ex.toString());
        }

    }
}