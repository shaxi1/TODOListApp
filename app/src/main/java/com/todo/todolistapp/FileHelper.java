package com.todo.todolistapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {

    public static final int REQUEST_CODE = 111;
    public static final int MAX_FILE_SIZE = 60 * 1024; // 60kB
    private final Context context;

    public FileHelper(Context context) {
        this.context = context;
    }

    public void launchFile(String fileName) {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, fileName);

        Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mimeType = getMimeType(fileUri);
        intent.setDataAndType(fileUri, mimeType);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    public void saveFileToCache(Uri fileUri, String fileName) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(fileUri);
            File cacheDir = context.getCacheDir();
            File file = new File(cacheDir, fileName);
            outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[MAX_FILE_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
    }

    public String handleFileSelection(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String fileName = getFileName(uri);
            saveFileToCache(uri, fileName);
            return fileName;
        }
        return null;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            fileName = getDisplayName(cr, uri);
        }
        if (fileName == null) {
            fileName = uri.getLastPathSegment();
        }
        return fileName;
    }

    public void deleteFileFromCache(String fileName) {
        File cacheDir = context.getCacheDir();
        File file = new File(cacheDir, fileName);

        file.delete();
    }

    @SuppressLint("Range")
    private String getDisplayName(ContentResolver contentResolver, Uri uri) {
        String displayName = null;
        try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return displayName;
    }

}
