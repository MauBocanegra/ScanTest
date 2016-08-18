package maubocanegra.scantest.ListaProductos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import maubocanegra.scantest.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentListaProductos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentListaProductos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    public FragmentListaProductos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentListaProductos.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentListaProductos newInstance(String param1, String param2) {
        FragmentListaProductos fragment = new FragmentListaProductos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentListaProductos newInstance(){
        FragmentListaProductos fragment = new FragmentListaProductos();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_lista_productos, container, false);
        mRecyclerView = (RecyclerView) fragView.findViewById(R.id.recyclerView);

        //perfomance si es fijo el tamanio
        mRecyclerView.setHasFixedSize(true);

        //con linearlayout
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //adapter
        mAdapter = new ProductosAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        return fragView;
    }

    public void updateData(){
        mAdapter = new ProductosAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

}
