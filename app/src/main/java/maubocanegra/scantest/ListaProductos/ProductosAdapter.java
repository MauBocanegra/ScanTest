package maubocanegra.scantest.ListaProductos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import maubocanegra.scantest.R;

/**
 * Created by maw on 8/17/16.
 */
public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder>{

    OnClickItemListenerProductos mCallback;

    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public ProductosAdapter(Context c){
        context = c;
        sharedpreferences = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        try{
            mCallback = (OnClickItemListenerProductos) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement OnClick");
        }
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleTextView;
        public TextView descripcionTextView;
        public TextView precioTextView;
        public View view;
        public ViewHolder(View v){
            super(v);
            view=v;
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            descripcionTextView = (TextView) v.findViewById(R.id.descripcionTextView);
            precioTextView = (TextView) v.findViewById(R.id.precioTextView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.titleTextView.setText(sharedpreferences.getString("Nombre"+position,""));
        holder.descripcionTextView.setText(sharedpreferences.getString("Desc"+position,""));
        holder.precioTextView.setText("$ "+sharedpreferences.getString("Precio"+position,""));
        holder.view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.OnClickedItem(position);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        int id = sharedpreferences.getInt("numArrays",0);
        return id;
    }



    public interface OnClickItemListenerProductos{
        public void OnClickedItem(int position);
    }
}
