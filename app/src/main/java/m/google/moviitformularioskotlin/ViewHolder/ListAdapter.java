package m.google.moviitformularioskotlin.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import m.google.moviitformularioskotlin.R;
import m.google.moviitformularioskotlin.model.Model;

import java.text.DateFormat;
import java.util.Calendar;

public class ListAdapter extends FirestoreRecyclerAdapter<Model, ListAdapter.ListHolder> {


    public ListAdapter(@NonNull FirestoreRecyclerOptions<Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ListHolder holder, int position, @NonNull Model model) {
        holder.txtName.setText(model.getNombre());
        holder.txtDate.setText(model.getFecha());
    }

    @NonNull
    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_layout,viewGroup,false);
        return new ListHolder(view);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ListHolder extends RecyclerView.ViewHolder{

        TextView txtName,txtDate;

        ListHolder(@NonNull View itemView) {
            super(itemView);

            txtName= itemView.findViewById(R.id.Id_Name_List);
            txtDate= itemView.findViewById(R.id.Id_Data);
        }
    }
}
