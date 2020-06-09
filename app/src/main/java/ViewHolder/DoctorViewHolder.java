package ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView doctorAvatar;
    public TextView doctorName, doctorQual;
    public FloatingActionButton book;

    public DoctorViewHolder(@NonNull View itemView) {
        super(itemView);

        doctorAvatar = itemView.findViewById(R.id.doctorAvatar);
        doctorName = itemView.findViewById(R.id.doctorName);
        doctorQual = itemView.findViewById(R.id.doctorQual);
        book = itemView.findViewById(R.id.book);
    }
}
