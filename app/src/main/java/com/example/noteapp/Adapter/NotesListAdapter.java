package com.example.noteapp.Adapter;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.service.autofill.TextValueSanitizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.Model.Notes;
import com.example.noteapp.NotesClickListener;
import com.example.noteapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder> {

    Context context;
    ArrayList<Notes> notesArrayList = new ArrayList<>();
    NotesClickListener listener;

    public NotesListAdapter(Context context, ArrayList<Notes> notesArrayList, NotesClickListener listener) {
        this.context = context;
        this.notesArrayList = notesArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {

        holder.title_tv.setText(notesArrayList.get(position).getTitle());
        holder.title_tv.setSelected(true);

        holder.notes_tv.setText(notesArrayList.get(position).getNotes());

        holder.date_tv.setText(notesArrayList.get(position).getDate());
        holder.date_tv.setSelected(true);

        if(notesArrayList.get(position).isPin()){
            holder.pin_imgview.setImageResource(R.drawable.pin);
        }else{
            holder.pin_imgview.setImageResource(0);
        }

        try{

            int color_code = getRandomColor();

            holder.notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code,null));

            holder.notes_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(notesArrayList.get(holder.getAdapterPosition()));

                }
            });

            holder.notes_container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onLongClick(notesArrayList.get(holder.getAdapterPosition()),holder.notes_container);
                    return true;
                }
            });

        }catch (Exception ex){
            ex.printStackTrace();
            Log.e("onBindViewHolder", ex.toString());
         }

    }

    private  int getRandomColor(){
        List<Integer> colorCode = new ArrayList<>();

        colorCode.add(R.color.color1);
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);

        Random random = new Random();
        int random_color = random.nextInt(colorCode.size());

        return colorCode.get(random_color);
    }

    @Override
    public int getItemCount() {
        return notesArrayList.size();
    }

    public void filterList(ArrayList<Notes> filterList){
        notesArrayList = filterList;
        notifyDataSetChanged();
    }

    public void notifyData(){
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder{
    CardView notes_container;
    TextView title_tv,notes_tv,date_tv;
    ImageView pin_imgview;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);
        notes_container =itemView.findViewById(R.id.notes_container);
        title_tv =itemView.findViewById(R.id.title_tv);
        notes_tv =itemView.findViewById(R.id.notes_tv);
        date_tv =itemView.findViewById(R.id.date_tv);
        pin_imgview =itemView.findViewById(R.id.pin_imgview);
    }
}
