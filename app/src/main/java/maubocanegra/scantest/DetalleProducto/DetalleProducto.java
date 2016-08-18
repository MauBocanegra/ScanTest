package maubocanegra.scantest.DetalleProducto;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import maubocanegra.scantest.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetalleProducto#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetalleProducto extends DialogFragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SupportMapFragment mMapFragment;
    GoogleMap mGoogleMap;
    LatLng mLatLng;
    int posicion=-1;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;


    public DetalleProducto() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetalleProducto.
     */
    // TODO: Rename and change types and number of parameters
    public static DetalleProducto newInstance(String param1, String param2) {
        DetalleProducto fragment = new DetalleProducto();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static DetalleProducto newInstance(int pos){
        DetalleProducto fragment = new DetalleProducto();
        Bundle args = new Bundle();
        args.putInt("posicion",pos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            posicion = getArguments().getInt("posicion");
        }

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutMapa, mMapFragment).commit();
        mMapFragment.getMapAsync(this);

        sharedpreferences = ((Activity)getContext()).getPreferences(Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_detalle_producto, container, false);
        ((TextView)fragView.findViewById(R.id.titleTV)).setText(sharedpreferences.getString("Nombre"+posicion,""));
        ((TextView)fragView.findViewById(R.id.barcodeTV)).setText("("+sharedpreferences.getString("Barcode"+posicion,"")+")");
        ((TextView)fragView.findViewById(R.id.descTV)).setText(sharedpreferences.getString("Desc"+posicion,""));
        ((TextView)fragView.findViewById(R.id.precioTV)).setText("$"+sharedpreferences.getString("Precio"+posicion,""));
        return fragView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("somethingPressed","item="+item.getItemId());
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        double lat = Double.parseDouble(sharedpreferences.getString("Lat"+posicion,""));
        double lon = Double.parseDouble(sharedpreferences.getString("Lon"+posicion,""));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon)));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon),15));
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
    }
}
