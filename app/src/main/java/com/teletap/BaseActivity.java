package com.teletap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;


public abstract class BaseActivity extends AppCompatActivity {

//    SpotsDialog mProgressDialog;
    //AlertDialog  mProgressDialog;

    public static final int REQUEST_CAPTURE = 1001;
    public static final int REQUEST_GALLERY = 1002;
    public static final int REQUEST_Doc = 1003;
    public static final int REQUEST_VIDEO = 1003;
    private static Toast toast1;

    public static Toast toast;
    //ProgressDialog pDialog;
    public static Dialog pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mSocket.on("userMessage", OldChatMessages);
//        mSocket.on("adminMessage", NewChatMessage);
        //mSocket.connect();
    }



    @SuppressLint("WrongConstant")
    public static void show_error_toast(final Activity activity, final String message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.popup_error_alert,
                (ViewGroup) ((Activity) activity).findViewById(R.id.llCustom));
        TextView text = (TextView) layout.findViewById(R.id.error);
        text.setText(message);
        toast1 = new Toast(activity);
        toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast1.setDuration(1000);
        toast1.setView(layout);

        toast1.show();

    }
    @SuppressLint("WrongConstant")
    public static void toast(final Activity activity, final String message) {

        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.popup_error_alert,
                (ViewGroup) ((Activity) activity).findViewById(R.id.llCustom));
        TextView text = (TextView) layout.findViewById(R.id.error);
        text.setText(message);
        toast1 = new Toast(activity);
        toast1.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast1.setDuration(1000);
        toast1.setView(layout);

        toast1.show();


    }

    @SuppressLint("WrongConstant")
    public static void successToast(final Activity activity, String message){

        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) ((Activity) activity).findViewById(R.id.custom_toast_container));

        TextView text = (TextView) layout.findViewById(R.id.tvMessage);
        text.setText(message);

        toast = new Toast(activity);
        //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }




    @Override
    protected void onStop() {
        if (toast != null) {
            toast.cancel();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (toast != null) {
            toast.cancel();      // This close toast when stop called
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (toast != null) {
            toast.cancel();      // This close toast when stop called
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private String getUriRealPathAboveKitkat(Context ctx, Uri uri) {
        String ret = "";

        if (ctx != null && uri != null) {

            if (isContentUri(uri)) {
                if (isGooglePhotoDoc(uri.getAuthority())) {
                    ret = uri.getLastPathSegment();
                } else {
                    ret = getImageRealPath(getContentResolver(), uri, null);
                }
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(ctx, uri)) {
                String documentId = DocumentsContract.getDocumentId(uri);
                String uriAuthority = uri.getAuthority();
                if (isMediaDoc(uriAuthority)) {
                    String[] idArr = documentId.split(":");
                    if (idArr.length == 2) {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if ("image".equals(docType)) {
                        } else if ("video".equals(docType)) {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(docType)) {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }
                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(getContentResolver(), mediaContentUri, whereClause);
                    }

                } else if (isDownloadDoc(uriAuthority)) {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.parseLong(documentId));

                    ret = getImageRealPath(getContentResolver(), downloadUriAppendId, null);

                } else if (isExternalStoreDoc(uriAuthority)) {
                    String[] idArr = documentId.split(":");
                    if (idArr.length == 2) {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if ("primary".equalsIgnoreCase(type)) {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether this uri represent a document or not. */
    private boolean isDocumentUri(Context ctx, Uri uri) {
        boolean ret = false;
        if (ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }



    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private boolean isContentUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("content".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private boolean isFileUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("file".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. */
    private boolean isExternalStoreDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.externalstorage.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private boolean isDownloadDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.downloads.documents".equals(uriAuthority)) {
            ret = true;
        }
        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private boolean isMediaDoc(String uriAuthority) {
        boolean ret = false;
        if ("com.android.providers.media.documents".equals(uriAuthority)) {
            ret = true;
        }
        return ret;
    }

    /* Check whether this document is provided by google photos. */
    private boolean isGooglePhotoDoc(String uriAuthority) {
        boolean ret = false;
        if ("com.google.android.apps.photos.content".equals(uriAuthority)) {
            ret = true;
        }
        return ret;
    }

    /* Return uri represented document file real local path.*/
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if (uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA;
                }
                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }


    /**
     * Create a File for saving an image
     */
//    private File getOutputMediaFile(int type) {
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), AppConstants.SAVE_IMAGE_DIRECTORY);
//
//        /**Create the storage directory if it does not exist*/
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                return null;
//            }
//        }
//
//        /**Create a media file name*/
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile;
//        if (type == 1) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
//                    "IMG_" + timeStamp + ".png");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "BookComplaint", null);

        return Uri.parse(path);

    }

    public String getRealPathFromURI(Uri uri) {
        String mCurrentPhotoPath = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            mCurrentPhotoPath = cursor.getString(idx);
            cursor.close();
        }

        return mCurrentPhotoPath;

    }


//    public AlertDialog.Builder getAlertDialogBuilder(String title, String message, boolean cancellable) {
//        return new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
//                .setTitle(title)
//                .setMessage(message)
//                .setCancelable(cancellable);
//    }


    public void openExternalWebView(String url) {

        if (url != null) {
            Intent browserIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent1);
        }
    }


    public String tag() {
        return getClass().getSimpleName();
    }

    public void log(String message) {
        Log.d(tag(), message);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void loadProgressBar(Context context, String message, boolean cancellable) {

        if (pDialog == null) {
            //pDialog = new ProgressDialog(this);
            pDialog = new Dialog(this);
            pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            //pDialog.getWindow().setBackgroundDrawableResource(R.color.black_transparent);
            //pDialog.setMessage(message);
            //Drawable drawable = new ProgressBar(this).getIndeterminateDrawable().mutate();
            //drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            //pDialog.setIndeterminateDrawable(getDrawable(R.drawable.progress_dilog));
            pDialog.setContentView(R.layout.custom_progress_bar_layout);
//            pDialog= new ProgressDialog(context, R.style.MyAlertDialogStyle);
            pDialog.setCancelable(false);
            //            pDialog.setIndeterminate(true);
        }
        if (!pDialog.isShowing())
            pDialog.show();

    }

    public void dismissProgressBar(Context context) {

        if (pDialog != null) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            pDialog = null;
        }
    }

    public AlertDialog.Builder getAlertDialogBuilder(String title, String message, boolean cancellable) {
        return new AlertDialog.Builder(this, R.style.AppTheme_AlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(cancellable);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void enableLoadingBar(Context context, boolean enable, String s) {
        if (enable) {
            loadProgressBar(context, s, false);
        } else {
            dismissProgressBar(context);
        }
    }

    public void onError(String reason) {
        try {
            onError(reason, false);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void onError(String reason, final boolean finishOnOk) {
        if (reason != null && !reason.isEmpty()) {
            getAlertDialogBuilder(null, reason, true).setPositiveButton(getString(R.string.btn_ok), finishOnOk ? new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    } : null).show();
        } else {
            getAlertDialogBuilder(null, getString(R.string.default_error), true)
                    .setPositiveButton(getString(R.string.btn_ok), finishOnOk ? new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    } : null).show();
        }
    }

//    public void onError(String reason, final boolean finishOnOk) {
//        if (reason != null && !reason.isEmpty()) {
//            getAlertDialogBuilder(null, reason, false)
//                    .setPositiveButton(getString(com.b2c.audience.R.string.btn_ok), finishOnOk ? new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            finish();
//                        }
//                    } : null).show();
//        } else {
//            getAlertDialogBuilder(null, getString(com.b2c.audience.R.string.default_error), false)
//                    .setPositiveButton(getString(com.b2c.audience.R.string.btn_ok), finishOnOk ? new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            finish();
//                        }
//                    } : null).show();
//        }
//    }

//    public void UserLogout() {
//
//        SessionManager.clearAll(getApplicationContext());
//
//        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finishAffinity();
//    }
//    public void dialogAccountDeactivate(String msg) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.warning));
//        builder.setMessage(msg);
//        builder.setCancelable(true);
//        builder.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                dialog.cancel();
//                UserLogout();
//            }
//        });
//
//        final AlertDialog alert = builder.create();
//        //2. now setup to change color of the button
//        alert.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface arg0) {
//                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alertDialogButton));
//            }
//        });
//        alert.show();
//    }


}
