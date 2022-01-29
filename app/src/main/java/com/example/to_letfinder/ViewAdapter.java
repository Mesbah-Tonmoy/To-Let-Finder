package com.example.to_letfinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {

    private Context context;
    private List<Data> dataSetList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public ViewAdapter(Context mContext, List<Data> mList){
        context = mContext;
        dataSetList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.recycler_row, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Data newDataSet = dataSetList.get(i);
        viewHolder.address.setText(newDataSet.getDaddress());
        viewHolder.rent.setText(newDataSet.getDrent());
        viewHolder.description.setText(newDataSet.getDdescription());
        Picasso.with(context)
                .load(newDataSet.getImageUrl())
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataSetList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView address, rent, description;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.text1);
            rent = itemView.findViewById(R.id.text2);
            description = itemView.findViewById(R.id.text3);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.OnItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
