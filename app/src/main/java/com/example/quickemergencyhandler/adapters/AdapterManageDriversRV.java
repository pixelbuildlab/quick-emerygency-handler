package com.example.quickemergencyhandler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.interfaces.IManagePatientClickListener;
import com.example.quickemergencyhandler.models.DriverModel;

import java.util.ArrayList;

public class AdapterManageDriversRV extends RecyclerView.Adapter<AdapterManageDriversRV.MyViewHolder> {

    private ArrayList<DriverModel> items;
    private Context context;
    private IManagePatientClickListener listener;

    public AdapterManageDriversRV(ArrayList<DriverModel> items, Context context, IManagePatientClickListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_item_manage_drivers, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.image.setImageResource(R.drawable.ic_baseline_person_24);
        holder.name.setText(items.get(position).getName().toString());
        holder.phoneNo.setText(items.get(position).getPhoneNo().toString());
        if (items.get(position).getStatus().equals("approved")) {
            holder.status.setBackgroundResource(R.drawable.shape_approved_rect);
            holder.status.setText(items.get(position).getStatus());
        } else if (items.get(position).getStatus().equals("blocked")) {
            holder.status.setBackgroundResource(R.drawable.shape_blocked_rect);
            holder.status.setText(items.get(position).getStatus());
        } else {
            holder.status.setBackgroundResource(R.drawable.shape_pending_rect);
            holder.status.setText(items.get(position).getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name, phoneNo, status;
        LinearLayout parentLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewManageDriversSingleItem);
            name = itemView.findViewById(R.id.nameRVManageSingleD);
            phoneNo = itemView.findViewById(R.id.phoneRVManageSingleD);
            status = itemView.findViewById(R.id.statusRVManageSingleD);
            parentLayout = itemView.findViewById(R.id.parentLayoutD);
            parentLayout.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.parentLayoutD:
                    listener.onItemClick(getAdapterPosition());
                    break;
            }
        }
    }
}
