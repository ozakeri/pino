package com.gap.bis_inspection.adapter.advert;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gap.bis_inspection.R;
import com.gap.bis_inspection.db.objectmodel.AttachFile;

import java.util.List;

public class AdvertAddAttachAdapter extends RecyclerView.Adapter<AdvertAddAttachAdapter.CustomViewHolder> {

    private AttachFile attachFile;
    private List<AttachFile> attachFileList = null;
    private List<Bitmap> bitmapList = null;
    private boolean b = false;

    public AdvertAddAttachAdapter(List<AttachFile> attachFileList) {
        this.attachFileList = attachFileList;
    }

    public AdvertAddAttachAdapter(List<Bitmap> bitmapList, boolean b) {
        this.bitmapList = bitmapList;
        this.b = b;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advert_add_attach_items, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        if (attachFileList != null) {
            attachFile = attachFileList.get(position);
            if (attachFile.getAttachFileLocalPath() != null) {
                Bitmap bitmap = resizeBitmap(attachFile.getAttachFileLocalPath(), 100, 100);
                holder.img_attach.setImageBitmap(bitmap);
            }
        } else {
            holder.img_attach.setImageBitmap(bitmapList.get(position));
        }


    }

    @Override
    public int getItemCount() {
        if (attachFileList != null) {
            return attachFileList.size();
        } else {
            return bitmapList.size();
        }
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_attach;
        private RelativeLayout relativeLayout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            img_attach = itemView.findViewById(R.id.img_attach);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
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
}
