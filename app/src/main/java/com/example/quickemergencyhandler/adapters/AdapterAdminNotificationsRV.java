package com.example.quickemergencyhandler.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickemergencyhandler.R;
import com.example.quickemergencyhandler.interfaces.IManagePatientClickListener;
import com.example.quickemergencyhandler.models.NotificationModel;

import java.util.ArrayList;

public class AdapterAdminNotificationsRV extends RecyclerView.Adapter<AdapterAdminNotificationsRV.ViewHolder1> {

    private Context context;
    private ArrayList<NotificationModel> items;
    private IManagePatientClickListener listener;

    public AdapterAdminNotificationsRV(Context context, ArrayList<NotificationModel> items, IManagePatientClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.single_item_admin_notifications_rv, parent, false);
        ViewHolder1 viewHolder = new ViewHolder1(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder1 holder, int position) {
        holder.title.setText(items.get(position).getTitle());
        holder.subTitle.setText(items.get(position).getSubTitle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, subTitle;
        LinearLayout parentLayout;

        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            subTitle = itemView.findViewById(R.id.notificationSubTitle);
            parentLayout = itemView.findViewById(R.id.parentNotificationLayout);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.parentNotificationLayout:
                    listener.onItemClick(getAdapterPosition());
                    break;
            }
        }
    }
}
