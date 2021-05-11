package pucpr.br.cameragalery.adapters;


import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

import pucpr.br.cameragalery.R;
import pucpr.br.cameragalery.model.Foto;
import pucpr.br.cameragalery.repository.GaleryRepository;

public class FotoAdapter extends
    RecyclerView.Adapter<FotoAdapter.ViewHolder>{

    private static ClickListener clickListener;

    public static void setClickListener(ClickListener clickListener) {
        FotoAdapter.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(
                R.layout.recycle_view_item, parent,false
        );
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Foto foto = GaleryRepository.getInstance().getFotos().get(position);

        File file = new File(foto.getPath());

        holder.textViewFoto.setImageURI(Uri.fromFile(file));
    }

    @Override
    public int getItemCount() {
        return GaleryRepository.getInstance().getFotos().size();
    }

    public interface ClickListener {
        void onItemClick(int position,View view);
        boolean onItemLongClick(int position,View view);
    }

    int counter = 0;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView textViewFoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFoto = itemView.findViewById(R.id.imageViewFoto);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener == null)
                        return;
                    clickListener.onItemClick(getAdapterPosition(),view);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(clickListener == null)
                        return false;

                    return clickListener.onItemLongClick(getAdapterPosition(),view);
                }
            });
        }
    }

}
