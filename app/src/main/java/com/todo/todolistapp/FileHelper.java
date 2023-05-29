package com.todo.todolistapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;

public class FileHelper {

    public static final int REQUEST_CODE = 111;
    private final Context context;

    public FileHelper(Context context) {
        this.context = context;
    }

    public void launchFile(String filePath) {
        File file = new File(filePath);
        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, getMimeType(filePath));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            Intent chooser = Intent.createChooser(intent, "Open file with");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No app found to open the file", Toast.LENGTH_SHORT).show();
        }
    }


    public String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public void chooseFile(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No file manager app found", Toast.LENGTH_SHORT).show();
        }
    }

    public String handleFileSelection(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                return getFilePathFromUri(uri, context);
            }
        }
        return null;
    }

    private String getFilePathFromUri(Uri uri, Context context) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // If the Uri is a Document URI
            DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
            filePath = documentFile != null ? documentFile.getUri().getPath() : null;
        } else {
            // For other Uri schemes, try getting the path directly
            filePath = uri.getPath();
        }
        return filePath;
    }

}
