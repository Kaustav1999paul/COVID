package ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView docName, date;
    public CircleImageView docAvatar;
    public FloatingActionButton cancel;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        docAvatar = itemView.findViewById(R.id.docAvatar);
        docName = itemView.findViewById(R.id.docName);
        date = itemView.findViewById(R.id.date);
        cancel = itemView.findViewById(R.id.cancel);

    }
}
