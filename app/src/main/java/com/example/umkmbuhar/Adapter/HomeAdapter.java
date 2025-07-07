//package com.example.umkmbuhar.Adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.umkmbuhar.Model.User;
//import com.example.umkmbuhar.R;
//import java.util.List;
//
//public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
//    private Context context;
//    private List<User> umkmList;
//    private OnItemClickListener listener;
//
//    public interface OnItemClickListener {
//        void onDetailClick(User user);
//        void onEditClick(User user);
//        void onDeleteClick(User user);
//    }
//
//    public HomeAdapter(Context context, List<User> umkmList, OnItemClickListener listener) {
//        this.context = context;
//        this.umkmList = umkmList;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_umkm_card, parent, false);
//        return new HomeViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
//        User user = umkmList.get(position);
//
//        holder.tvUmkmName.setText(user.getUmkmName());
//        holder.tvUmkmDescription.setText(user.getUmkmAddress());
//        holder.tvUmkmPhone.setText("Telp: " + user.getUmkmTelepon());
//        // Set image if available
//
//        holder.btnDetail.setOnClickListener(v -> listener.onDetailClick(user));
//        holder.ivEdit.setOnClickListener(v -> listener.onEditClick(user));
//        holder.ivDelete.setOnClickListener(v -> listener.onDeleteClick(user));
//    }
//
//    @Override
//    public int getItemCount() {
//        return umkmList.size();
//    }
//
//    static class HomeViewHolder extends RecyclerView.ViewHolder {
//        TextView tvUmkmName, tvUmkmDescription, tvUmkmPhone;
//        ImageView ivEdit, ivDelete;
//        Button btnDetail;
//
//        public HomeViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvUmkmName = itemView.findViewById(R.id.tv_umkm_name);
//            tvUmkmDescription = itemView.findViewById(R.id.tv_umkm_description);
//            tvUmkmPhone = itemView.findViewById(R.id.tv_umkm_phone);
//            ivEdit = itemView.findViewById(R.id.iv_edit);
//            ivDelete = itemView.findViewById(R.id.iv_delete);
//            btnDetail = itemView.findViewById(R.id.btn_detail);
//        }
//    }
//}
//
