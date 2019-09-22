package com.ijse.ijsestm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;


public class MainActivity extends AppCompatActivity {

    private Button uploadBtn;
    private ImageButton findBtn;
    private TextView txtsName;
    private TextView txtsBatch;
    private TextView txtsNic;
    private EditText studentId;
    private TextView alradyText;
    private ImageButton imageViewButton;
    final Context context = this;
    private ImageView imageView;
    private Dialog dialog;
//"http://35.225.76.194:8081"
    private final String BASEURL = "http://192.168.1.103:8080";

    private byte[] imageByte;
    private String fileContentType=".png";
    private String imageString;

    AlertDialog studentResponseDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMultiplePermissions();



        uploadBtn = (Button) findViewById(R.id.uploadbtn);
        findBtn= findViewById(R.id.findButton);
        studentId=findViewById(R.id.studentID);
        txtsBatch=findViewById(R.id.txtBatch);
        txtsName=findViewById(R.id.txtName);
        txtsNic=findViewById(R.id.txtNic);
        alradyText=findViewById(R.id.txtImageAlready);
        imageViewButton=findViewById(R.id.viewimgBtn);
        imageView=(ImageView)findViewById(R.id.imageView);

        alradyText.setVisibility(View.INVISIBLE);
        imageViewButton.setVisibility(View.INVISIBLE);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(studentId.getText().toString().matches("")){

                     empltyFieldMessage();
               }else{

                   CropImage.activity().start(MainActivity.this);

               }

            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setStudentDetails();

            }
        });

        imageViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Pop.class));
//                Toast.makeText(getApplicationContext(), "Fail to Crop Try Again", Toast.LENGTH_SHORT).show();
//               dialog=new Dialog(MainActivity.this);
//               dialog.setContentView(R.layout.viewimage);
//               dialog.setTitle("Seted");

            }
        });

    }

//    private void showPictureDialog(){
//        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
//        pictureDialog.setTitle("Select Action");
//        String[] pictureDialogItems = {
//                "Select photo from gallery",
//                "Capture photo from camera" };
//        pictureDialog.setItems(pictureDialogItems,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                GetImageFromGallery();
//                               // choosePhotoFromGallary();
//                                break;
//                            case 1:
//                                ClickImageFromCamera();
//                                //takePhotoFromCamera();
//                                break;
//                        }
//                    }
//                });
//        pictureDialog.show();
//    }



//    public void ClickImageFromCamera() {
//
//        CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//        file = new File(Environment.getExternalStorageDirectory(),
//                "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
//        uri = Uri.fromFile(file);
//
//        CamIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
//
//        CamIntent.putExtra("return-data", true);
//
//        startActivityForResult(CamIntent, 0);
//
//    }

//    public void GetImageFromGallery(){
//
//        GalIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), 2);
//
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try{
                    Uri uri = result.getUri();
                    File file = new File(uri.getPath());
                    Bitmap compressedImageBitmap = new Compressor(this).compressToBitmap(file);
                    System.out.println("sizez  "+compressedImageBitmap.getByteCount());
                    //Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    saveImage(compressedImageBitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Fail to Crop Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void saveImage(Bitmap myBitmap) {
        myBitmap = Bitmap.createScaledBitmap(myBitmap, 200, 200, false);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 1, bytes);

        System.out.println("bitmap count  compress    "+myBitmap.getByteCount());
        imageByte=bytes.toByteArray();
        imageString = Base64.encodeToString(imageByte,Base64.DEFAULT);

       // System.out.println(imageString);
           uploadFile();


    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
////
    private void setStudentDetails() {

        System.out.println(studentId.getText().toString());
        if (studentId.getText().toString().matches("")) {

            empltyFieldMessage();
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = BASEURL + "/student/getStudentById";

            waitingMessage("Waiting......","waiting for response");

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            // response
                            try {
                                System.out.println(response);
                                JSONObject responseJson = new JSONObject(response);
                                txtsName.setHint(responseJson.getString("fullName"));
                                txtsBatch.setHint(responseJson.getString("nicNo"));
                                txtsNic.setHint(responseJson.getString("batchName"));

                                downloadAvailableImage();

                                studentResponseDialog.cancel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            studentResponseDialog.cancel();
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    context);

                            // set title
                            alertDialogBuilder.setTitle("Failed");

                            // set dialog message
                            alertDialogBuilder
                                    .setMessage("Student Details Invalid")
                                    .setCancelable(false)
                                    .setPositiveButton("ok",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // if this button is clicked, close
                                            // current activity
                                            dialog.cancel();
                                        }
                                    });


                            // create alert dialog
                            AlertDialog alertDialog = alertDialogBuilder.create();

                            // show it
                            alertDialog.show();
                            System.out.println("sdhfmsdhf dsf,dsfnm,dsf, sfdn,msnf,dsf sd,fndsnf, sdfhdsjfh");
                            downloadAvailableImage();

                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();
                    params.put("id", studentId.getText().toString());

                    return params;
                }
            };
            queue.add(postRequest);
        }
    }

    private  void uploadFile( ){

        RequestQueue queue = Volley.newRequestQueue(this);
        String  url = BASEURL + "/fileUpload/uploadCloud";

        waitingMessage("Image Uploading.....","waiting for response");
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {
                        studentResponseDialog.cancel();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);

                        // set title
                        alertDialogBuilder.setTitle("Success");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Image Upload Success")
                                .setCancelable(false)
                                .setPositiveButton("ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        dialog.cancel();
                                    }
                                });


                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        studentResponseDialog.cancel();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);

                        // set title
                        alertDialogBuilder.setTitle("Failed");

                        // set dialog message
                        alertDialogBuilder
                                .setMessage("Image Upload Failed")
                                .setCancelable(false)
                                .setPositiveButton("ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        dialog.cancel();
                                    }
                                });


                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<>();
                params.put("file", imageString);
                params.put("studentId", studentId.getText().toString());
                params.put("contentType","image/"+fileContentType);

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }
//
    public  void  empltyFieldMessage(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Invalid");

        // set dialog message
        alertDialogBuilder
                .setMessage("Add correct student ID")
                .setCancelable(false)
                .setPositiveButton("ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    public  void  waitingMessage(String title,String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(title);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false);



        // create alert dialog
        studentResponseDialog = alertDialogBuilder.create();

        // show it
        studentResponseDialog.show();
    }

        void downloadAvailableImage(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String  url = BASEURL + "/fileUpload/getFile";


        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {
                        if(response!=null){
                            System.out.println("workkk ");
                            System.out.println(response);
                            alradyText.setVisibility(View.VISIBLE);
                            imageViewButton.setVisibility(View.VISIBLE);

                            imageView=(ImageView) findViewById(R.id.imageView);

                            byte[] decodedString = Base64.decode(response, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0,decodedString.length);
                            System.out.println(decodedByte);

                            InputStream stream = new ByteArrayInputStream(Base64.decode(response.getBytes(), Base64.URL_SAFE));
                            Bitmap image = BitmapFactory.decodeStream(stream);
//                            imageView.setImageBitmap(image);
                           try {
                               File tempDir= Environment.getExternalStorageDirectory();
                               tempDir=new File(tempDir.getAbsolutePath()+"/tempar/");
                               tempDir.mkdir();
                               File tempFile = File.createTempFile("uplodedimage", ".jpg", tempDir);
                               ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                               decodedByte.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                               byte[] bitmapData = bytes.toByteArray();

                               //write the bytes in file
                               FileOutputStream fos = new FileOutputStream(tempFile);
                               fos.write(bitmapData);
                               fos.flush();
                               fos.close();

                               System.out.println(Uri.fromFile(tempFile));
                        //       ImageView imageView = new ImageView(this);
                               imageView.setImageURI(Uri.fromFile(tempFile));
                               startActivity(new Intent(MainActivity.this,Pop.class));
                           }catch (Exception e){

                           }


                        }


                    }

                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("workkkkkk 2");
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<>();
                params.put("studentId",studentId.getText().toString());


                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }

}


