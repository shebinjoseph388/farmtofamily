package com.farmtofamily.ecommerce;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Datta on 8/24/2016.
 */
public class OrderRecyclerAdapter extends RecyclerView.Adapter<OrderRecyclerAdapter.MyViewHolder> {
    private Context mCOntext;
   private ArrayList<MyPojo> prodList = new ArrayList<MyPojo>();


    public OrderRecyclerAdapter(Context mCOntext, ArrayList<MyPojo> prodList){
        this.mCOntext = mCOntext;
        this.prodList = prodList;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
        return new MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageView imageView = holder.prodImg;
        MyPojo myPojo = prodList.get(position);
       // String imgUrl = Constant.AdminPageURL+myPojo.getProd_image();
      //  DownloadImageTask downloadImageTask = new DownloadImageTask(imageView);
        //downloadImageTask.execute(imgUrl);
       // Log.d("mypojo1", "" + myPojo.getProd_image()+" "+myPojo.getProd_name()+" "+myPojo.getProd_price()+" "+myPojo.getQuantity()+" "+myPojo.getStatus_name());
       // Log.d("mypojo",""+myPojo.getProd_image());
     //   imageLoader.DisplayImage("http://192.168.0.128/farmtofamiliez/"+myPojo.getProd_image(),imageView);
        holder.prodid.setText("Order ID: #"+myPojo.getOdid());
        holder.proddate.setText("Order Date: "+myPojo.getDate());
        Log.d("line43","completed");
        holder.prodName.setText("Product Details :"+myPojo.getProd_name());
        Log.d("line45","completed");
     //   holder.prodPrice.setText("ProductPrice. : "+myPojo.getProd_price());
        Log.d("line47","completed");
     //   holder.prodQuant.setText("Quantity :"+myPojo.getQuantity());
        Log.d("line49","completed");
        holder.prodStatus.setText("Status :"+myPojo.getStatus_name());
        Log.d("onbind","completed");
        holder.orderPrice.setText("Order Payment Price. : "+myPojo.getOrder_price());



    }

    @Override
    public int getItemCount() {
        return prodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView prodImg;
        public TextView prodName, prodPrice, prodQuant, prodStatus,proddate,prodid,orderPrice;

        public MyViewHolder(View view){
            super(view);
            prodImg = (ImageView) view.findViewById(R.id.prod_img);
            prodName = (TextView) view.findViewById(R.id.prod_name);
            //prodPrice = (TextView) view.findViewById(R.id.prod_price);
            //prodQuant = (TextView) view.findViewById(R.id.prod_quantity);
            prodStatus = (TextView) view.findViewById(R.id.prod_status);
            proddate = (TextView) view.findViewById(R.id.oddate);
            prodid = (TextView) view.findViewById(R.id.odid);
            orderPrice = (TextView) view.findViewById(R.id.order_price);
        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

        }
    }


}
