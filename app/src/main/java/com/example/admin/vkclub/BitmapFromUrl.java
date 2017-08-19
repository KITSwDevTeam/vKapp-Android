package com.example.admin.vkclub;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Romdoul on 7/10/2017.
 */

public class BitmapFromUrl extends AsyncTask<String, Void, Bitmap> {

    ImageView bmImage;
    View uploadingSpinner;
    private Resources mResources;

    DataBaseHelper mDataBaseHelper;
    SharedPreferences prefs;
    String imageBlob;

    public BitmapFromUrl(ImageView bmImage, View uploadingSpinner) {
        this.bmImage = bmImage;
        this.uploadingSpinner = uploadingSpinner;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    protected void onPostExecute(Bitmap result) {
        prefs = PreferenceManager.getDefaultSharedPreferences(Dashboard.getAppContext());
        DbBitmapUtility dbBitmapUtility = new DbBitmapUtility();
        imageBlob = prefs.getString("get_blob", "");
        if (imageBlob.length() == 0){
            if (result != null){
                SharedPreferences.Editor editor = prefs.edit();
                String encodedString = dbBitmapUtility.getString(result);
                editor.putString("get_blob", encodedString);
                editor.commit();
            }else {
                System.out.println("Cannot get bitmap from url" + "Null");
            }
        }

        RoundedBitmapDrawable drawable = createRoundedBitmapDrawableWithBorder(result);
        uploadingSpinner.setVisibility(View.GONE);
        bmImage.setImageAlpha(255);
        bmImage.setImageDrawable(drawable);
    }

    private RoundedBitmapDrawable createRoundedBitmapDrawableWithBorder(Bitmap mBitmap) {
        if (mBitmap != null){
            int bitmapWidth = mBitmap.getWidth();
            int bitmapHeight = mBitmap.getHeight();
            int borderWidthHalf = 10;

            int bitmapRadius = Math.min(bitmapWidth,bitmapHeight)/2;

            int bitmapSquareWidth = Math.min(bitmapWidth,bitmapHeight);

            int newBitmapSquareWidth = bitmapSquareWidth+borderWidthHalf;
            Bitmap roundedBitmap = Bitmap.createBitmap(newBitmapSquareWidth,newBitmapSquareWidth,Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(roundedBitmap);
            canvas.drawColor(Color.BLACK);

            int x = (borderWidthHalf + bitmapSquareWidth - bitmapWidth);
            int y = (borderWidthHalf + bitmapSquareWidth - bitmapHeight);

            canvas.drawBitmap(mBitmap, x, y, null);

            // Initializing a new Paint instance to draw circular border
            Paint borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(borderWidthHalf);
            borderPaint.setColor(Color.GREEN);

            canvas.drawCircle(canvas.getWidth()/2, canvas.getWidth()/2, newBitmapSquareWidth/2, borderPaint);

            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mResources,roundedBitmap);
            roundedBitmapDrawable.setCornerRadius(bitmapRadius);

            roundedBitmapDrawable.setAntiAlias(true);
            // Return the RoundedBitmapDrawable
            return roundedBitmapDrawable;
        }
        return null;
    }
}
