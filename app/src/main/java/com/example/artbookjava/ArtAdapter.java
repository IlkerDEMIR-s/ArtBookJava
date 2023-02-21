package com.example.artbookjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artbookjava.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {

    ArrayList<Art> artArrayList;

    public ArtAdapter(ArrayList<Art> artArrayList){ // this is the constructor

        this.artArrayList = artArrayList;
    }


    @NonNull
    @Override
    public ArtAdapter.ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ArtHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtAdapter.ArtHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.artNameRecyclerText.setText(artArrayList.get(position).name);

        holder.binding.imageRecyclerView.setImageBitmap(artArrayList.get(position).image);

        holder.binding.artistNameRecyclerText.setText(artArrayList.get(position).artist);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(holder.itemView.getContext(), ArtActivity2nd.class);
                intent.putExtra("artId", artArrayList.get(position).id);
                intent.putExtra("info", "old");
                holder.itemView.getContext().startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {

        return artArrayList.size();
    }


    public class ArtHolder extends RecyclerView.ViewHolder {

        private RecyclerRowBinding binding;

        public ArtHolder(RecyclerRowBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
