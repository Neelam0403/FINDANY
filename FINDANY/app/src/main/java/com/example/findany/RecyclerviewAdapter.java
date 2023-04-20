package com.example.findany;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {

    private RecyclerviewListner listener;
    private List<ModelClass> userDetails;

    RecyclerviewAdapter(List<ModelClass> userDetails, RecyclerviewListner listener) {
        this.listener = listener;
        this.userDetails = userDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.studentprofileview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int profile = userDetails.get(position).getProfileimage();
        String fullName = userDetails.get(position).getName();
        String year = userDetails.get(position).getYear();
        String branch = userDetails.get(position).getBranch();

        holder.setData(profile, fullName, year, branch);
    }

    @Override
    public int getItemCount() {
        return userDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView profileImage;
        private TextView name;
        private TextView year;
        private TextView branch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileimage);
            name = itemView.findViewById(R.id.fullname);
            year = itemView.findViewById(R.id.year);
            branch = itemView.findViewById(R.id.branch);
            itemView.setOnClickListener(this);
        }

        public void setData(int profile, String fullName, String year, String branch) {
            profileImage.setImageResource(profile);
            name.setText(fullName);
            this.year.setText(year);
            this.branch.setText(branch);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.OnItemListner(pos);
                    Toast.makeText(v.getContext(), "position  "+pos,Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(v.getContext(),RecyclerClickListner.class);
                    intent.putExtra("name",userDetails.get(pos).getName());
                    v.getContext().startActivity(intent);
                }
            }
        }
    }
}
