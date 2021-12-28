package com.intelj.y_ral_gaming.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.intelj.y_ral_gaming.R;
import com.intelj.y_ral_gaming.VolleyMultipartRequest;

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class ClaimNowFragment extends DialogFragment {
    private static final int RESULT_OK = 8;
    private View view;
    private EditText et_datetime, et_duration;
    private Button upload_file;
    private Context context;
    private AlertDialog dialog;
    private static final String ROOT_URL = "http://y-ral-gaming.com/admin/api/upload.php";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageView;
    Bitmap selectedImage;

    public ClaimNowFragment() {
        // Required empty public constructor
    }


    public static ClaimNowFragment newInstance(String param1, String param2) {
        ClaimNowFragment fragment = new ClaimNowFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_claim_now, null);
        builder.setView(view);

        et_datetime = view.findViewById(R.id.et_datetime);
        upload_file = view.findViewById(R.id.upload_file);
        imageView = view.findViewById(R.id.imageview);

        et_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog fromDatePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                newDate.set(year, monthOfYear, dayOfMonth, selectedHour, selectedMinute);
                                et_datetime.setText(new SimpleDateFormat("dd/MMM/yyyy HH:mm", Locale.US).format(newDate.getTime()));
                            }
                        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), true);//Yes 24 hour time
                        //mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                fromDatePickerDialog.show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedImage != null)
                    uploadBitmap(selectedImage);
            }
        });
        upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {
                        showFileChooser();
                    } else {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    Log.e("Else", "Else");
                    showFileChooser();
                }
            }
        });
        dialog = builder.create();
        dialog.show();

        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void showFileChooser() {
        new ImagePicker.Builder(getActivity())
                .mode(ImagePicker.Mode.GALLERY)
                .compressLevel(ImagePicker.ComperesLevel.NONE)
                .directory(ImagePicker.Directory.DEFAULT)
                .allowMultipleImages(false)
                .build();
//        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(pickPhoto , 1);
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            selectedImage = BitmapFactory.decodeFile(mPaths.get(0), opts);
            Glide.with(this).load(selectedImage).into(imageView);
            dialog.show();
        }
    }
//    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//            Uri selectedImages = imageReturnedIntent.getData();
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//            Cursor cursor = getActivity().getContentResolver().query(selectedImages,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            selectedImage = getResizedBitmap(BitmapFactory.decodeFile(picturePath),100);
//            Glide.with(this).load(selectedImage).into(imageView);
    //selectedImage = BitmapFactory.decodeFile(imgDecodableString);
//        try {
//            Uri imageUri = imageReturnedIntent.getData();
//            InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 8;
//            selectedImage = BitmapFactory.decodeStream(imageStream, null, options);
//
//            //selectedImage = BitmapFactory.decodeStream(imageStream);
//
//            selectedImage = getResizedBitmap(selectedImage, 100);// 400 is for example, replace with desired size
//
//            Glide.with(this).load(selectedImage).into(imageView);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
    //   }

    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri picUri = data.getData();
//            Log.i("uri",picUri+"");
//            filePath = getPath(picUri);
//            if (filePath != null) {
//                Uri imageUri = data.getData();
//                InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
//                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//
//                selectedImage = getResizedBitmap(selectedImage, 400);// 400 is for example, replace with desired size
//                try {
//
//                    imageView.setImageBitmap(selectedImage);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            else
//            {
//                Toast.makeText(
//                        context,"no image selected",
//                        Toast.LENGTH_LONG).show();
//            }
//        }
//    }


    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private void uploadBitmap(final Bitmap bitmap) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Uploading file, please wait.");
        dialog.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        try {
                            Log.e("GotError", "sucess");
                            JSONObject obj = new JSONObject(new String(response.data));
                            selectedImage = null;
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Upload Successfully", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("GotError", "" + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
                    }
                }) {

            //            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("tags", tags);
//                return params;
//            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("uploaded_file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                params.put("date_time", new DataPart(et_datetime.getText().toString()));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(context).add(volleyMultipartRequest);
    }
}