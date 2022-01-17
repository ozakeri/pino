package com.gap.bis_inspection.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.gap.bis_inspection.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by parkjisun on 2017. 3. 15..
 */

public class ImageManager {

    private static final int MINIMUM_HEIGHT = 360;

    public static void adjustCameraParameters(Context context, Camera camera, String pictureSizeStr) {

        Camera.Parameters parameters = camera.getParameters();

        if (pictureSizeStr == null) {
            // 4:3 비율을 가진 해상도의 사진 사이즈만 추려 저장
            List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
            ArrayList<Camera.Size> validPictureSizeList = new ArrayList<>();
            for (Camera.Size size : pictureSizeList) {
                if (size.height > MINIMUM_HEIGHT) {
                    if (size.width / 4 * 3 == size.height) {
                        validPictureSizeList.add(size);
                    }
                }
            }

            // 사진 사이즈 리스트 저장되어 있지 않을 경우에만 저장
            if (Config.getSharedPreferenceString(context, Config.PREF_KEY_PICTURE_SIZE_LIST).isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Camera.Size size : validPictureSizeList) {
                    sb.append(Integer.toString(size.width)).append("x").append(Integer.toString(size.height)).append("|");
                }
                Config.putSharedPreference(context, Config.PREF_KEY_PICTURE_SIZE_LIST, sb.toString());
            }

            // 최초에는 가장 작은 사이즈 설정
            Camera.Size currentSize = parameters.getPictureSize();
            String currentSizeStr = Config.getSharedPreferenceString(context, Config.PREF_KEY_PICTURE_SIZE);
            int newWidth, newHeight;
            if (currentSizeStr.isEmpty()) {
//            Camera.Size newSize = validPictureSizeList.get(validPictureSizeList.size() - 1);
                Camera.Size newSize = validPictureSizeList.get(0); // FIXME jisun-test : 가장 큰 사이즈로 설정
                newWidth = newSize.width;
                newHeight = newSize.height;
            } else {
                String[] temp = currentSizeStr.split("x");
                newWidth = Integer.parseInt(temp[0]);
                newHeight = Integer.parseInt(temp[1]);
            }
            if ((currentSize.width != newWidth) || (currentSize.height != newHeight)) {
                parameters.setPictureSize(newWidth, newHeight);
            }
            Config.putSharedPreference(context, Config.PREF_KEY_PICTURE_SIZE, newWidth + "x" + newHeight);
            Log.e("jisunLog", "new picture size ] " + newWidth + "x" + newHeight);
        } else {
            String[] temp = pictureSizeStr.split("x");
            parameters.setPictureSize(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
            Config.putSharedPreference(context, Config.PREF_KEY_PICTURE_SIZE, pictureSizeStr);
        }

        // 4:3으로 preview 비율 설정
        List<Camera.Size> list = parameters.getSupportedPreviewSizes();
        int targetWidth = 0, targetHeight = 0;
        for (Camera.Size size : list) {
            Log.e("jisunLog", "previewSize " + size.width + "x" + size.height);
            if (size.width / 4 * 3 == size.height) {
                // 해당 비율을 가진 가장 높은 preview 값 찾기
                targetWidth = size.width;
                targetHeight = size.height;
                break;
            }
        }
        Log.e("jisunLog", "target " + targetWidth + "x" + targetHeight);
        parameters.setPreviewSize(targetWidth, targetHeight);

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

        // TODO jisun : 전면 카메라이면 parameter 설정시 죽음
        camera.setParameters(parameters);
    }

//    public void changePictureSize(Camera camera, String newSizeStr) {
//        android.util.Log.e("jisunLog", "newSizeStr " + newSizeStr);
//        Camera.Parameters parameters = camera.getParameters();
//        Camera.Size currentSize = parameters.getPictureSize();
//        if (!getSizeString(currentSize).equals(newSizeStr)) {
//            String[] temp = newSizeStr.split("x");
//            parameters.setPictureSize(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
//
////            List<Camera.Size> list = parameters.getSupportedPreviewSizes();
////            if (!list.isEmpty()) {
////                parameters.setPreviewSize(list.get(0).width, list.get(0).height);
////            }
////            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//
//            try {
//                camera.setParameters(parameters);
//                // parameter 변경 성공하면 shared preferences도 변경
//                Config.putSharedPreference(context, Config.PREF_KEY_PICTURE_SIZE, newSizeStr);
//            } catch (RuntimeException e) {
//                Log.e("jisunLog", "RuntimeException ] " + e.getLocalizedMessage());
//                // setParameter시 죽는 단말기 존재(Nexus5)
//                Toast.makeText(context, "사진 사이즈 변경 실패", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    public static String getSizeString(Camera.Size size) {
        return size.width + "x" + size.height;
    }

    /**
     * Create a path for saving an image
     */
    private static String getMediaFilePath() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "TimestampCamera");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("jisunLog", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
    }

    public static Bitmap saveImageWithTimeStamp(Context context, byte data[], int offset, int length, float textSize,String text) {

        Bitmap src = BitmapFactory.decodeByteArray(data, offset, length);
        Log.e("jisunLog", "src " + src.getWidth() + "x" + src.getHeight());
        src = rotateBitmap(src, 90);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Log.e("jisunLog", "rotated src " + src.getWidth() + "x" + src.getHeight());

        src = drawTextToBitmap(context,src,text);

        SimpleDateFormat sdf = new SimpleDateFormat(Config.TIME_STAMP_FORMAT);
        String dateTime = sdf.format(new Date());
        String str = dateTime + "\n" + "X: " + "35.760640" + "\n" + "Y: " + "51.421878";

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float scaledTextSize = src.getWidth() * textSize / displayMetrics.widthPixels;
        Log.e("jisunLog", "scaletextsize " + scaledTextSize);

        Canvas cs = new Canvas(dest);
        Paint paint = new Paint();
        paint.setTextSize(scaledTextSize);
        paint.setFakeBoldText(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        cs.drawBitmap(src, 0f, 0f, null);

        // 가운데 정렬을 위해 텍스트 시작 시점 계산
        //float height = paint.measureText("yY");
       // float width = paint.measureText(text);
       // float startXPosition = (src.getWidth() - width) / 2;
       // cs.drawText(text, startXPosition, src.getHeight() - height + 15f, paint);
        return dest;
    }

    public static File saveFile(Bitmap bitmap) {
        File file = new File(getMediaFilePath());
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            Log.e(Config.TAG, e.getLocalizedMessage());
            file = null;
        }
        return file;
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap drawTextToBitmap(Context gContext,
                                          Bitmap bitmap,
                                          String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        paint.setTextSize((int) (40 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);


        // draw text to the Canvas center
        Rect bounds = new Rect();
        int noOfLines = 0;
        for (String line: gText.split("\n")) {
            noOfLines++;
        }

        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = 20;
        int y = (bitmap.getHeight() - bounds.height()*noOfLines);

        Paint mPaint = new Paint();
        mPaint.setColor(gContext.getResources().getColor(R.color.transparentBlack));
        int left = bitmap.getWidth();
        int top = (bitmap.getHeight() - bounds.height()*(noOfLines+1));
        int right = 0;
        int bottom = bitmap.getHeight();
        canvas.drawRect(left, top, right, bottom, mPaint);

        for (String line: gText.split("\n")) {
            canvas.drawText(line, x, y, paint);
            y += paint.descent() - paint.ascent();
        }

        return bitmap;
    }
}
