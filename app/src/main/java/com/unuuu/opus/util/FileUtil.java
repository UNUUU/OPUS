package com.unuuu.opus.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ファイル操作系のUtilityクラス
 */
public final class FileUtil {

    /** 画像の質 */
    private static final int IMAGE_QUALITY = 100;

    private FileUtil() {

    }

    /**
     * 画像をダウンロードフォルダに書き込む
     * @param context コンテキスト
     * @param bitmap ビットマップ
     * @param extension 拡張子
     */
    public static void writeToDownloadDirectory(Context context, Bitmap bitmap, String extension) {
        String bitmapPath = getDownloadFilePath(extension);
        LogUtil.d("画像を保存するパス: " + bitmapPath);
        FileUtil.writeBitmap(bitmap, IMAGE_QUALITY, bitmapPath);
        MediaScannerConnection.scanFile(context, new String[]{bitmapPath}, null, null);
    }

    /**
     * ダウンロード先のファイルのパス
     * @param extension 拡張子
     * @return ファイルのパス
     */
    public static String getDownloadFilePath(String extension) {
        String path = getDownloadDirectoryPath();
        if (path == null) {
            return null;
        }

        String filename = getTimestampFileName();
        StringBuilder builder = new StringBuilder();
        builder.append(path).append(File.separator);
        builder.append(filename);
        if (extension != null) {
            builder.append(".").append(extension);
        }
        return builder.toString();
    }

    /**
     * ダウンロードディレクトリのパスを取得する
     * @return ダウンロードディレクトリのパス
     */
    public static String getDownloadDirectoryPath() {
        File storage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!storage.mkdirs()) {
            LogUtil.e("ダウンロードディレクトリを作れませんでした");
        }
        return storage.getPath();
    }

    public static String getExternalStoragePath(Context context, String dir, boolean useCacheDir) {
        if (useCacheDir) {
            return getCachePath(context, dir);
        }

        File storage = Environment.getExternalStorageDirectory();
        String path = storage.getPath();

        path += File.separator + context.getPackageName();
        if (dir != null) {
            path += File.separator + dir;
        }
        File cacheDir = new File(path);
        if (!cacheDir.exists() && !cacheDir.isDirectory() && !cacheDir.mkdirs()) {
            LogUtil.d("not mkdirs!");
            if (useCacheDir) {
                return getCachePath(context, dir);
            }
            return null;
        }
        return cacheDir.getPath();
    }

    public static File getExternalStorage(Context context, String dir, boolean useCacheDir) {
        String storagePath = getExternalStoragePath(context, dir, useCacheDir);
        if (storagePath == null) {
            return null;
        }
        return new File(storagePath);
    }

    public static String getCachePath(Context context, String dir) {
        File storage = context.getExternalCacheDir();

        if (storage == null) {
            return null;
        }
        LogUtil.d("storage = " + storage);

        if (dir != null) {
            String path = storage.getAbsolutePath() + File.separator + dir;
            storage = new File(path);
        }

        if (!storage.exists() && !storage.isDirectory() && !storage.mkdirs()) {
            LogUtil.d("not mkdirs!");
            return null;
        }
        return storage.getAbsolutePath();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String contentUriToFilePath(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        LogUtil.d("uri = " + uri);

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                LogUtil.d("split[0] = " + split[0] + ", split[1] = " + split[1]);
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // DownloadsProvider
            } else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                LogUtil.d("contentUri = " + contentUri);
                return getDataColumn(context, contentUri, null, null);
                // MediaProvider
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                LogUtil.d("split[0] = " + split[0] + ", split[1] = " + split[1] + ", contentUri = " + contentUri);
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equals(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream, null, options);

                    Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
                    if (options.outMimeType.equals("image/png")) {
                        format = Bitmap.CompressFormat.PNG;
                    }
                    return writeToTempImageAndGetPath(context, format, bmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return getDataColumn(context, uri, null, null);
            }

            if (isGooglePhotosExtraUri(uri)) {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream, null, options);

                    Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
                    if (options.outMimeType.equals("image/png")) {
                        format = Bitmap.CompressFormat.PNG;
                    }
                    return writeToTempImageAndGetPath(context, format, bmp);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            return uri.getPath();
        } else {
            return uri.getPath();
        }
        return null;
    }

    public static String getExternalFilePath(
            Context context,
            String dir,
            String extension,
            boolean useCache) {
        return getExternalFilePath(context,
                dir,
                getTimestampFileName(),
                extension,
                useCache);
    }

    public static String getExternalFilePath(
            Context context,
            String dir,
            String filename,
            String extension,
            boolean useCache) {
        String path = getExternalStoragePath(context, dir, useCache);
        if (path == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(path).append(File.separator);
        builder.append(filename);
        if (extension != null) {
            builder.append(".").append(extension);
        }
        return builder.toString();
    }

    public static String getImageFilePath(Context context, String filename) {
        return getExternalFilePath(
                context,
                "",
                filename,
                "jpg",
                true);
    }

    public static String getImageFilePath(Context context) {
        return getImageFilePath(
                context,
                getTimestampFileName());
    }

    public static String getTimestampFileName() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public static void writeBitmap(Bitmap bitmap, int quality, String path) {
        FileOutputStream fos = null;

        // 書き込むファイルフォーマットをJPEGとPNGで変える
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        File file = new File(path);
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).getPath());
        if (extension.equals("png")) {
            compressFormat = Bitmap.CompressFormat.PNG;
        }

        try {
            fos = new FileOutputStream(new File(path));
            bitmap.compress(compressFormat, quality, fos);
        } catch (Exception ignore) {
            LogUtil.e(ignore);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception ignore) {
                    LogUtil.e(ignore);
                }
            }
        }
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                LogUtil.d("uri = " + uri + ", c.getColumnCount = " + cursor.getColumnCount() + ", c.getColumnIndex = " + index + " : c.getString = " + cursor.getString(index));
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static String writeToTempImageAndGetPath(Context context, Bitmap.CompressFormat format, Bitmap bitmap) {
        String path;
        if (Bitmap.CompressFormat.JPEG == format) {
            path = FileUtil.getExternalFilePath(context, "", "jpg", true);
        } else {
            path = FileUtil.getExternalFilePath(context, "", "png", true);
        }
        writeBitmap(bitmap, IMAGE_QUALITY, path);
        return path;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosExtraUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }
}
