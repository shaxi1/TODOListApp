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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileHelper {

    public static final int REQUEST_CODE = 111;
    private final Context context;

    public FileHelper(Context context) {
        this.context = context;
    }

    public void launchFile(String uriString) {
        final int MAX_BUFFER_SIZE = 50 * 1024; // 50 KB
        String temp = "file://" + uriString;
        Uri uri = Uri.parse(temp);

        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            launchFileWithUri(uri);
        } else {
            String mimeType = context.getContentResolver().getType(uri);
            String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

            File cacheDir = context.getCacheDir();
            File tempFile;
            try {
                tempFile = File.createTempFile("temp", "." + extension, cacheDir);
            } catch (IOException e) {
                Toast.makeText(context, "Failed to create temporary file", Toast.LENGTH_SHORT).show();
                return;
            }

            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[MAX_BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                Toast.makeText(context, "Failed to copy file contents, try files under " + MAX_BUFFER_SIZE, Toast.LENGTH_LONG).show();
                tempFile.delete(); // Delete the temporary file in case of failure
                return;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Uri tempFileUri = Uri.fromFile(tempFile);
            launchFileWithUri(tempFileUri);
        }
    }

    private void launchFileWithUri(Uri uri) {
        String mimeType = context.getContentResolver().getType(uri);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            Intent chooser = Intent.createChooser(intent, "Open file with");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(chooser);
        } catch (Exception e) {
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
            DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
            filePath = documentFile != null ? documentFile.getUri().getPath() : null;
        } else {
            filePath = uri.getPath();
        }
        return filePath;
    }

}
