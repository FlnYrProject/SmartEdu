package com.project.smartedu;

/**
 * Created by Shubham Bhasin on 04-Feb-17.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Dell on 10/7/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    String role;
    int widthOfScreen;
    int heightOfScreen;


    public ImageAdapter(Context c,int Screensize,int Screenheight,String role) {
        mContext = c;
        this.role=role;
        widthOfScreen=Screensize;
        heightOfScreen=Screenheight;
    }

    public int getCount() {
        if(role.equals("Teacher")) {
            return mThumbIdsTeacher.length;
        }
        else if(role.equals("Parent"))  {
            return mThumbIdsParent.length;
        }
        else if(role.equals("Student")) {
            return mThumbIdsStudent.length;
        }else if(role.equals("admin"))
        {
            return mThumbIdsAdmin.length;
        }
        return mThumbIdsTeacher.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView imageView;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            //int width = devicewidth/11 ;
            //int height =deviceheight/11 ;
            //using this parameter you have to pass in GridView's Adapter

            //android.widget.AbsListView.LayoutParams parms = new android.widget.AbsListView.LayoutParams(width , height);

            Resources r = Resources.getSystem();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
            // imageView.setLayoutParams(new GridView.LayoutParams((int)mContext.getResources().getDimension(R.dimen.width),(int)mContext.getResources().getDimension(R.dimen.height)));
            imageView.setLayoutParams(new GridView.LayoutParams(widthOfScreen / 2,widthOfScreen / 2));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }



        if(role.equalsIgnoreCase("Teacher")) {
            /*
            imageView.post(new Runnable() {
                @Override
                public void run() {

                    Bitmap bitmap = decodeSampledBitmapFromResource(Resources.getSystem(), mThumbIdsTeacher[position],
                            imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
                    imageView.setImageBitmap(bitmap);

                }
            }); */

            imageView.setImageResource(mThumbIdsTeacher[position]);
            /*
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            } */
        }

        else if(role.equalsIgnoreCase("Parent"))

        {
            imageView.setImageResource(mThumbIdsParent[position]);
        }

        else if(role.equalsIgnoreCase("Student"))

        {
            imageView.setImageResource(mThumbIdsStudent[position]);
        }

        else if(role.equalsIgnoreCase("admin"))

        {
                           /* Bitmap bitmap = decodeSampledBitmapFromResource(imageView.getResources(), mThumbIdsAdmin[position],
                                    widthOfScreen/2, widthOfScreen/2);
                            imageView.setImageBitmap(bitmap); */

            imageView.setImageResource(mThumbIdsAdmin[position]);
        }


        //        imageView.setLayoutParams(new GridView.LayoutParams(360, 360)); //dimension in px
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIdsTeacher = {
            R.drawable.attendance, R.drawable.task_image,
            R.drawable.students, R.drawable.timetable_image,
            R.drawable.marks, R.drawable.uploads,
            R.drawable.messages
    };

    private Integer[] mThumbIdsParent = {
            R.drawable.attendance, R.drawable.task_image,
            R.drawable.messages, R.drawable.marks,

    };

    private Integer[] mThumbIdsStudent = {
            R.drawable.attendance, R.drawable.task_image,
            R.drawable.messages, R.drawable.timetable_image,
            R.drawable.marks, R.drawable.uploads,

    };

    private Integer[] mThumbIdsAdmin = {
            R.drawable.teachers,R.drawable.task_image, R.drawable.classes,
            R.drawable.allotments

    };




    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        //options.inSampleSize=1;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        Log.d("memory grid ",
                "height " + height + " reqd " + reqHeight + " width " + width + " reqd " + reqWidth);

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
