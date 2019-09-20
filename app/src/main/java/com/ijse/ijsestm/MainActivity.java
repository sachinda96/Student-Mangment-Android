package com.ijse.ijsestm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Button uploadBtn;
    private ImageButton findBtn;
    private TextView txtsName;
    private TextView txtsBatch;
    private TextView txtsNic;
    private EditText studentId;
    final Context context = this;

    private final String BASEURL = "http://34.67.151.90:8081";

    private byte[] imageByte;
    private String fileContentType=".jpg";
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
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    saveImage(bitmap);
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
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        imageByte=bytes.toByteArray();
        imageString = Base64.encodeToString(imageByte,Base64.DEFAULT);

        System.out.println(imageString);
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

}


