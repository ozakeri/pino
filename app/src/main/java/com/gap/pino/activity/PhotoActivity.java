package com.gap.pino.activity;

import static com.shahaabco.satpa.SatpaSDK.anpr_create;
import static com.shahaabco.satpa.SatpaSDK.anpr_recognize_mat;
import static com.shahaabco.satpa.SatpaSDK.anpr_set_params;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gap.pino.R;
import com.shahaabco.satpa.SatpaLicense;
import com.shahaabco.satpa.SatpaSDK;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class PhotoActivity extends AppCompatActivity {

    private static final String TAG = "ANPR_Demo";

    static {
        //ANPRLIB Step 2
        System.loadLibrary("satpa");

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
        }
    }

    private Uri imgUri;
    private Bitmap bmpIn;
    private Mat imIn;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private Button btnSelect, btnDetect, btnRotate;
    private ImageView ivImage, ivPlate;
    private TextView tvConf, tvTime, tvResult[] = new TextView[4];
    private String userChoosenTask;

	/*private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
				case LoaderCallbackInterface.SUCCESS:
					Log.i("OpenCV", "OpenCV loaded successfully");
					break;
				default:
					super.onManagerConnected(status);
				 	break;
			}
		}
	};*/

    private void initialize_satpa() {
        SatpaLicense.anpr_check_lic_file(this); //TODO: 1. First step is to check if a valid license exists

        short result = anpr_create((byte) 0, "www.shahaab-co.com02331099", (byte) 0); //TODO: 2. create a new instance of SATPA library

        if (result < 0)
            Toast.makeText(this, "SATPA Failed to Load", Toast.LENGTH_SHORT).show();

        anpr_set_params((byte) 0, (byte) 8, (byte) 5, (byte) 1, (short) 500); //TODO: 3. Apply Satpa Settings
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File dir = getExternalFilesDir(null);
                File outFile = new File(dir, filename);
                if(!outFile.exists()) {
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                }
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        // NOOP
                    }
                }
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);

        copyAssets();
        initialize_satpa();

        setContentView(R.layout.activity_photo);
        tvResult[0] = (TextView) findViewById(R.id.tvResult1);
        tvResult[1] = (TextView) findViewById(R.id.tvResult2);
        tvResult[2] = (TextView) findViewById(R.id.tvResult3);
        tvResult[3] = (TextView) findViewById(R.id.tvResult4);
        tvConf = (TextView) findViewById(R.id.tvConf);
        tvTime = (TextView) findViewById(R.id.tvTime);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivPlate = (ImageView) findViewById(R.id.ivPlate);
        btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnDetect = (Button) findViewById(R.id.btnDetect);
        btnDetect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                long startTime = System.currentTimeMillis();
                short resize_thresh = 500;
                anpr_set_params((byte) 0, (byte) 8, (byte) 0, (byte) 1, resize_thresh);
                SatpaSDK.SPlate p = anpr_recognize_mat(imIn);
                while ((p.str_fa.length() < 8) && resize_thresh < 1500) {
                    resize_thresh += 500;
                    anpr_set_params((byte) 0, (byte) 8, (byte) 0, (byte) 1, resize_thresh);
                    SatpaSDK.SPlate p2 = anpr_recognize_mat(imIn);
                    if (p2.cnf[0] > p.cnf[0])
                        p = p2;
                }
                ;
                long elapseTime = System.currentTimeMillis() - startTime;
                tvTime.setText(String.format("%d ms", elapseTime));
                for (int i = 0; i < 4; i++)
                    tvResult[i].setText("");

                String s = p.str_fa;

                System.out.println("============sssss==========" + s);
                if (s.length() > 0) {
                    Mat plt = imIn.submat(p.rc);
                    //Imgcodecs.imwrite(anpr.pic_path + "/plt.bmp", plt);
                    Bitmap bmp = Bitmap.createBitmap(plt.width(), plt.height(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(plt, bmp);
                    ivPlate.setImageBitmap(bmp);
                    if (s.length() >= 2)
                        tvResult[0].setText(s.substring(0, Math.min(2, s.length())));
                    if (s.length() >= 3)
                        tvResult[1].setText(s.substring(2, Math.min(3, s.length())));
                    if (s.length() >= 4)
                        tvResult[2].setText(s.substring(3, Math.min(6, s.length())));
                    if (s.length() > 6)
                        tvResult[3].setText(s.substring(6, Math.min(8, s.length())));

                    tvConf.setText("cnf: " + String.format("%.2f", p.cnf[0]));
                } else {
                    tvResult[0].setText("Not Found!");
                    tvConf.setText("cnf: 0");
                }
            }
        });

        btnRotate = (Button) findViewById(R.id.btnRotate);
        btnRotate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bmpIn == null)
                    return;
                bmpIn = rotateImage(bmpIn, 90);
                ivImage.setImageBitmap(bmpIn);
                Utils.bitmapToMat(bmpIn, imIn);
            }
        });

        UpdateButtons(false);
    }


    private void RunIntent() {
        if (userChoosenTask.equals("Take Photo"))
            cameraIntent();
        else if (userChoosenTask.equals("Choose from Library"))
            galleryIntent();
        else if (userChoosenTask.equals("Sample 1"))
            LoadSample(1);
        else if (userChoosenTask.equals("Sample 2"))
            LoadSample(2);
        else if (userChoosenTask.equals("Sample 3"))
            LoadSample(3);
    }

    private void selectImage() {
        final CharSequence[] items = {"از دوربین", "از گالری", "نمونه یک", "نمونه دو", "نمونه سه",
                "لغو"};


        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this);
        builder.setTitle("انتخاب تصویر!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("از دوربین")) {
                    userChoosenTask = "Take Photo";

                } else if (items[item].equals("از گالری")) {
                    userChoosenTask = "Choose from Library";

                } else if (items[item].equals("نمونه یک")) {
                    userChoosenTask = "Sample 1";

                } else if (items[item].equals("نمونه دو")) {
                    userChoosenTask = "Sample 2";

                } else if (items[item].equals("نمونه سه")) {
                    userChoosenTask = "Sample 3";

                } else if (items[item].equals("لغو")) {
                    dialog.dismiss();
                }

                RunIntent();
            }
        });
        builder.show();
    }

    private void LoadSample(int i) {
        File dir = getExternalFilesDir(null);
        String im_path = dir.getPath() + "/sample_1.jpg";
        if (i == 2)
            im_path = dir.getPath() + "/sample_2.jpg";
        else if (i == 3)
            im_path = dir.getPath() + "/sample_3.jpg";

        bmpIn = BitmapFactory.decodeFile(im_path);
        ivImage.setImageBitmap(bmpIn);
        //bmpIn = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), im_path);
        if (imIn == null)
            imIn = new Mat();

        Utils.bitmapToMat(bmpIn, imIn);
        UpdateButtons(true);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Toast.makeText(getApplicationContext(), "رزولوشن دوربین را روی کمترین مقدار مثلا 2 مگاپیکسل تنظیم کنید", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/ANPR/";

        File photo_dir = new File(path);
        if (!photo_dir.exists())
            if (!photo_dir.mkdirs())
                Toast.makeText(getApplicationContext(), "ANPR Dir Failed to be created", Toast.LENGTH_SHORT).show();

        path += "/snapshot.jpg";
        File file = new File(path);

        //https://stackoverflow.com/questions/38200282/android-os-fileuriexposedexception-file-storage-emulated-0-test-txt-exposed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        imgUri = Uri.fromFile(file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);

            bmpIn = correctOrientation(bmpIn, imgUri.getPath());
            ivImage.setImageBitmap(bmpIn);

            if (imIn == null)
                imIn = new Mat();

            Utils.bitmapToMat(bmpIn, imIn);
            UpdateButtons(true);
        }
    }

    private void UpdateButtons(boolean enable) {
        btnRotate.setEnabled(enable);
        btnDetect.setEnabled(enable);
        /*if (enable) {
            btnRotate.setTextColor(Color.parseColor("#770000"));
            btnDetect.setTextColor(Color.parseColor("#770000"));
        } else {
            btnRotate.setTextColor(Color.parseColor("#777777"));
            btnDetect.setTextColor(Color.parseColor("#777777"));
        }*/
    }

    private void onCaptureImageResult(Intent data) {
		/*Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

		File destination = new File(Environment.getExternalStorageDirectory(),
				System.currentTimeMillis() + ".jpg");

		FileOutputStream fo;
		try {
			destination.createNewFile();
			fo = new FileOutputStream(destination);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ivImage.setImageBitmap(thumbnail);*/
        try {
            bmpIn = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), imgUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        if (data != null) {
            try {
                bmpIn = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                imgUri = data.getData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap correctOrientation(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

}
