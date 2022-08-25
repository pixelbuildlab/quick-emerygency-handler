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
import com.example.quickemergencyhandler.models.PatientModel;

import java.util.ArrayList;

public class AdapterManagePatientsRecycler extends RecyclerView.Adapter<AdapterManagePatientsRecycler.AdapterManagePatientsViewHolder> {

    private ArrayList<PatientModel> items;
    private Context context;
    private IManagePatientClickListener listener;

    public AdapterManagePatientsRecycler(ArrayList<PatientModel> items, Context context, IManagePatientClickListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdapterManagePatientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_item_manage_patients_recycler, parent, false);
        AdapterManagePatientsViewHolder viewHolder = new AdapterManagePatientsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterManagePatientsViewHolder holder, int position) {
        holder.image.setImageResource(R.drawable.ic_baseline_person_24);
        holder.name.setText(items.get(position).getName().toString());
        holder.phone.setText(items.get(position).getPhoneNo().toString());
        if (items.get(position).getStatus().equals("approved")){
            holder.status.setBackgroundResource(R.drawable.shape_approved_rect);
            holder.status.setText(items.get(position).getStatus());
        }

        else if (items.get(position).getStatus().equals("blocked")){
            holder.status.setBackgroundResource(R.drawable.shape_blocked_rect);
            holder.status.setText(items.get(position).getStatus());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class AdapterManagePatientsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView name, phone, status;
        LinearLayout parentLayout;

        public AdapterManagePatientsViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewManagePatientsSingleItem);
            name = itemView.findViewById(R.id.nameRVManageSingle);
            phone = itemView.findViewById(R.id.phoneRVManageSingle);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            status = itemView.findViewById(R.id.statusRVManageSingle);
            parentLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.parentLayout:
                    listener.onItemClick(getAdapterPosition());
                    break;
            }
        }
    }
}
