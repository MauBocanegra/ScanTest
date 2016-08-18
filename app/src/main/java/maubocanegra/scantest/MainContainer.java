package maubocanegra.scantest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import maubocanegra.scantest.AgregarProducto.AgregarFragment;
import maubocanegra.scantest.DetalleProducto.DetalleProducto;
import maubocanegra.scantest.ListaProductos.FragmentListaProductos;
import maubocanegra.scantest.ListaProductos.ProductosAdapter;

public class MainContainer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AgregarFragment.OnAgregarInteractionListener,
        ProductosAdapter.OnClickItemListenerProductos{

    AgregarFragment agregarFragment;
    FragmentListaProductos mFragmentListaProductos;
    FloatingActionButton fab;

    boolean isSearch=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAgregarProducto();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFragmentListaProductos = FragmentListaProductos.newInstance();
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.mainContainer, mFragmentListaProductos);
        tx.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_container, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            isSearch=true;
            IntentIntegrator scanIntegrator = new IntentIntegrator(MainContainer.this);
            scanIntegrator.initiateScan();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_lista) {
            getSupportFragmentManager().popBackStack();
        } else if (id == R.id.nav_agregar) {
            showAgregarProducto();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAgregarProducto(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        agregarFragment = AgregarFragment.newInstance();
        agregarFragment.show(ft,"agregar");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            //LOCATION ON AGREGAR PRODUCTO
            case 123:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED && agregarFragment!=null){
                    agregarFragment.initSnapshots();
                }
                return;
            }

            case AgregarFragment.CAMERA_REQUEST:{
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED && agregarFragment!=null){
                    agregarFragment.startCamera();
                }
            }
        }
    }

    @Override
    public void onGrantedResultsIncome(int[] grantedResults) {

    }

    @Override
    public void onProductStored(String barcode) {
        Snackbar.make(fab, "Agregado "+barcode, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        if(mFragmentListaProductos!=null){
            mFragmentListaProductos.updateData();
        }

        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if(requestCode != agregarFragment.CAMERA_REQUEST) {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                try {
                    if (scanningResult.getContents() != null) {
                        if (isSearch) {
                            searchForScanned(scanningResult.getContents());
                            isSearch = false;
                        } else {
                            agregarFragment.setScannerInfo(scanningResult.getContents());
                        }
                    }
                }catch(Exception e){

                }
            }else{
                Log.d("IMAGEVIEW","SOMETHING? +++++++++++++++++++++++");
                /*
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                */
                agregarFragment.setImageViewCamera();
            }
        //}


        /*
        Log.d("IMAGEVIEW","SOMETHING? +++++++++++++++++++++++");
        if (requestCode == 987 && resultCode == RESULT_OK) {
            Log.d("IMAGEVIEW","IS IT CALLING? +++++++++++++++++++++++");
            agregarFragment.setImageViewCamera();
        }else{
            Log.d("REQUESTDEBUG","reqcode="+requestCode+" result="+resultCode);
        }
        */

    }

    private void searchForScanned(String idScanned){
        SharedPreferences sharedpreferences;
        SharedPreferences.Editor editor;
        sharedpreferences = getPreferences(Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        int count = sharedpreferences.getInt("numArrays",0);
        boolean found=false;
        for(int i=0; i<count; i++){
            if((sharedpreferences.getString("Barcode"+i,"NoExiste")).compareTo(idScanned)==0){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                DetalleProducto mDetalleProducto = DetalleProducto.newInstance(i);
                ft.replace(R.id.mainContainer, mDetalleProducto);
                ft.addToBackStack("Detalle");
                ft.commit();
                found=true;
                break;
            }
        }

        if(!found){
            Snackbar.make(fab, idScanned+" no encontrado!", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void OnClickedItem(int position) {
        Log.d("DEBUGClicked","pos="+position);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DetalleProducto mDetalleProducto = DetalleProducto.newInstance(position);
        ft.replace(R.id.mainContainer, mDetalleProducto);
        ft.addToBackStack("Detalle");
        ft.commit();

    }
}
