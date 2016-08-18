package maubocanegra.scantest.AgregarProducto;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import maubocanegra.scantest.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgregarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgregarFragment extends DialogFragment implements OnMapReadyCallback{

    private GoogleApiClient mGoogleApiClient;
    OnAgregarInteractionListener mCallback;

    ImageView imageViewCamera;
    Bitmap bitmap;

    ImageView imageViewScanner;
    TextView scanInfoTextView;
    boolean scanned=false;

    SupportMapFragment mMapFragment;
    GoogleMap mGoogleMap;
    LatLng mLatLng;

    EditText editTextNombre, editTextPrecio, editTextDesc;
    String prodNombre, prodPrecio, prodDescripcion, prodBarcode;
    double[] latlon;

    FloatingActionButton fabGuardar;

    public static final int CAMERA_REQUEST = 987;

    boolean mapIsReady=false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AgregarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgregarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgregarFragment newInstance(String param1, String param2) {
        AgregarFragment fragment = new AgregarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AgregarFragment newInstance(){
        AgregarFragment fragment = new AgregarFragment();
        return  fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (OnAgregarInteractionListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement OnAgregarInteracionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Awareness.API)
                .build();
        mGoogleApiClient.connect();

        try {
        }catch(SecurityException e){
            e.printStackTrace();
        }

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameLayoutMapa, mMapFragment).commit();
        mMapFragment.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragView = inflater.inflate(R.layout.fragment_agregar, container, false);
        imageViewCamera = (ImageView) fragView.findViewById(R.id.imageViewCamera);
        imageViewScanner = (ImageView) fragView.findViewById(R.id.imageViewScanner);
        scanInfoTextView = (TextView) fragView.findViewById(R.id.scanInfo);
        fabGuardar = (FloatingActionButton) fragView.findViewById(R.id.FABGuardar);

        editTextNombre = (EditText) fragView.findViewById(R.id.editTextNombreProducto);
        editTextPrecio = (EditText) fragView.findViewById(R.id.editTextPrecioProducto);
        editTextDesc = (EditText) fragView.findViewById(R.id.editTextDescripcionProducto);

        setOnClickListeners();
        return fragView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void requestPermissionAndStart(String permissionToAsk, int requestCode){
        Log.d("CameraDEB","inRequestProcess");
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                permissionToAsk);

        if(permissionCheck==PackageManager.PERMISSION_GRANTED){
            Log.d("CameraDEB","granted!");
            if(permissionToAsk.equals(Manifest.permission.ACCESS_FINE_LOCATION)){
                initSnapshots();
            } else if(permissionToAsk.equals(Manifest.permission.CAMERA)){
                Log.d("CameraDEB","cameraGranted");
                startCamera();
            }
            return;
        }

        if(ContextCompat.checkSelfPermission(getActivity(),
                permissionToAsk)
                != PackageManager.PERMISSION_GRANTED){

            Log.d("CameraDEB","requestCameraPermission");

            if(permissionToAsk.equals(Manifest.permission.CAMERA)){
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                permissionToAsk,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        }, requestCode);
            }else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{permissionToAsk}, requestCode);
            }
        }
    }

    public void initSnapshots() throws SecurityException{
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e("AwarenessAPI", "Code = "+locationResult.getStatus().getStatusCode());
                            return;
                        }
                        Location location = locationResult.getLocation();
                        Log.i("AwarenessAPI", " *********** Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude()+" *************");
                        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if(mGoogleMap!=null){
                            mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(location.getLatitude(), location.getLongitude())));
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),15));
                            mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
                        }
                    }
                });
    }

    private void setOnClickListeners(){
        if(imageViewCamera!=null){
            imageViewCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent,CAMERA_REQUEST);
                    */
                    Log.d("CameraDEB","willRequestAndStart");
                    requestPermissionAndStart(Manifest.permission.CAMERA, CAMERA_REQUEST);
                }
            });
        }

        if(imageViewScanner!=null){
            imageViewScanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(getActivity());
                    scanIntegrator.initiateScan();
                }
            });
        }

        if(fabGuardar!=null){
            fabGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeProduct();
                }
            });
        }
    }

    public void startCamera(){


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent,CAMERA_REQUEST);
            }
        }


        /*
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
        */

    }

    public String mCurrentPhotoPath;
    public String mStorageDir;
    public String mImageName;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mStorageDir=storageDir.getAbsolutePath();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        mImageName=imageFileName;

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("PhotoDebug",mCurrentPhotoPath);
        return image;
        //
    }

    public interface OnAgregarInteractionListener{
        public void onGrantedResultsIncome(int[] grantedResults);
        public void onProductStored(String barcode);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //requestPermissionsAndInitSnapShots();
        requestPermissionAndStart(Manifest.permission.ACCESS_FINE_LOCATION,123);
        mGoogleMap=googleMap;
    }

    public void setScannerInfo(String st){
        scanInfoTextView.setText(st);
        prodBarcode=new String(st);
        scanned=true;
    }

    public void setImageViewCamera(){

        try {
            Log.d("IMAGEVIEW", "IS IT SETTING? +++++++++++++++++++++++");
            // Get the dimensions of the View
            int targetW = imageViewCamera.getWidth();
            int targetH = imageViewCamera.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            imageViewCamera.setImageBitmap(bitmap);
        }catch(Exception e){
            e.printStackTrace();
        }

        /*
        try {
            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            File directory = cw.getDir("Pictures", Context.MODE_PRIVATE);
            if(directory!=null){Log.i("CamDeb",directory.getAbsolutePath());}
            File f=new File(directory.getAbsolutePath(), mImageName);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageViewCamera.setImageBitmap(b);

            imageViewCamera.setImageBitmap(bitmap);
        }catch(Exception e){
            e.printStackTrace();
        }
        */

    }

    private void storeProduct(){
        prodNombre = editTextNombre.getEditableText().toString();
        prodPrecio = editTextPrecio.getEditableText().toString();
        prodDescripcion = editTextDesc.getEditableText().toString();

        SharedPreferences sharedpreferences;
        sharedpreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        int id = sharedpreferences.getInt("numArrays",0);
        Log.d("WillWorkWith","ID="+id);
        editor.putString("Nombre"+id, prodNombre);
        editor.putString("Precio"+id, prodPrecio);
        editor.putString("Desc"+id, prodDescripcion);
        editor.putString("Barcode"+id, prodBarcode);
        editor.putString("Lat"+id, ""+mLatLng.latitude);
        editor.putString("Lon"+id, ""+mLatLng.longitude);
        editor.commit();
        editor.apply();
        id++;
        editor.putInt("numArrays",id);
        editor.commit();
        editor.apply();

        mCallback.onProductStored(prodBarcode);

        dismiss();
    }
}
