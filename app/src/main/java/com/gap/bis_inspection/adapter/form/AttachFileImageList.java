package com.gap.bis_inspection.adapter.form;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.db.manager.DatabaseManager;
import com.gap.bis_inspection.db.objectmodel.AttachFile;
import com.gap.bis_inspection.service.CoreService;

import java.io.File;
import java.util.List;

/**
 * Created by Mohamad Cheraghi on 9/10/2017.
 */

public class AttachFileImageList extends RecyclerView.Adapter<AttachFileImageList.MyViewHolder> {
    Context context;
    private List<AttachFile> attachFiles;
    private CoreService coreService;
    private DatabaseManager databaseManager;
    private AttachFile attachFile;

    public AttachFileImageList(Context context, List<AttachFile> attachFiles) {
        this.context = context;
        this.attachFiles = attachFiles;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_list_attachfile_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        attachFile = attachFiles.get(position);
        File file = new File(attachFile.getAttachFileLocalPath());
        if (isFileValidImage(file)) {
            Bitmap bitmap = resizeBitmap(attachFile.getAttachFileLocalPath(), 100, 100);
            holder.imageView.setImageBitmap(bitmap);
            System.out.println("attachFile.getAttachFileLocalPath" + attachFile.getAttachFileLocalPath());
        }
        //System.out.println("attachFile.getAttachFileLocalPath" + attachFile.getAttachFileLocalPath());
        //File imgFile = new File("file://" + attachFile.getAttachFileLocalPath());
        //Bitmap bitmap = resizeBitmap(attachFile.getAttachFileLocalPath() , 100 , 100);
        //holder.imageView.setImageBitmap(bitmap);
        databaseManager = new DatabaseManager(context);
        coreService = new CoreService(databaseManager);
    }

    @Override
    public int getItemCount() {
        return attachFiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder  {
        ImageView imageView;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.attachFile_Image);
        }
    }

    private Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

    private boolean isFileValidImage(File file) {
        String[] okFileExtensions = new String[]{"jpg", "png", "gif", "jpeg"};

        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
