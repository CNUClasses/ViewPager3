package com.example.viewpager3;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewPager2_Adapter extends RecyclerView.Adapter {
    private final Context ctx;
    private final LayoutInflater li;
    private int[] image_resources = { R.drawable.p0,R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5 };
    public static final String TAG = "ViewPager2_Adapter";

    class PagerViewHolder extends RecyclerView.ViewHolder {
        private static final int UNINITIALIZED = -1;
        ImageView iv;
        TextView tv;
        int position=UNINITIALIZED;     //start off uninitialized, set it when we are populating
                                        //with a view in onBindViewHolder

        public PagerViewHolder(@NonNull View itemView) {
            super(itemView);
            iv = (ImageView)itemView.findViewById(R.id.imageView);
            tv = (TextView)itemView.findViewById(R.id.tv);
        }
    }

    private class GetImage extends Thread{
        //holds a reference to the hosting activity
        //notice that we cannot easily use a viewmodel
        //since each RowViewHolder has an implicit reference to
        //the parent activity. (From the inflator)
        private final MainActivity act;

        //ref to a viewholder
        private PagerViewHolder myVh;

        //since myVH may be recycled and reused
        //we have to verify that the result we are returning
        //is still what the viewholder wants
        private int original_position;

        public GetImage(PagerViewHolder myVh, MainActivity act) {
            //hold on to a reference to this viewholder
            //note that its contents (specifically iv) may change
            //iff the viewholder is recycled
            this.myVh = myVh;
            //make a copy to compare later, once we have the image
            this.original_position = myVh.position;
            //hold on to the activity
            this.act=act;
        }

        @Override
        public void run() {
            super.run();
            //just sleep for a bit
            try {
                Thread.sleep(2000); //sleep for 2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            act.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //got a result, if the following are NOT equal
                    // then the view has been recycled and is being used by another
                    // number DO NOT MODIFY
                    if (myVh.position == original_position) {
                        //still valid
                        //set the result on the main thread
                        myVh.iv.setImageResource(image_resources[myVh.position]);
                    } else
                        Toast.makeText(ViewPager2_Adapter.this.ctx, "YIKES! Recycler view reused, my result is useless", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

//    private class GetImage extends AsyncTask<Void, Void, Void> {
//        //ref to a viewholder
//        private PagerViewHolder myVh;
//
//        //since myVH may be recycled and reused
//        //we have to verify that the result we are returning
//        //is still what the viewholder wants
//        private int original_position;
//
//        public GetImage(PagerViewHolder myVh) {
//            //hold on to a reference to this viewholder
//            //note that its contents (specifically iv) may change
//            //iff the viewholder is recycled
//            this.myVh = myVh;
//            //make a copy to compare later, once we have the image
//            this.original_position = myVh.position;
//        }
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            //just sleep for a bit
//            try {
//                Thread.sleep(2000); //sleep for 2 seconds
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//        @Override
//        protected void onPostExecute(Void param) {
//             //got a result, if the following are NOT equal
//            // then the view has been recycled and is being used by another
//            // number DO NOT MODIFY
//            if (this.myVh.position == this.original_position){
//                //still valid
//                //set the result on the main thread
//                myVh.iv.setImageResource(image_resources[this.myVh.position ]);
//            }
//            else
//                Toast.makeText(ViewPager2_Adapter.this.ctx,"YIKES! Recycler view reused, my result is useless", Toast.LENGTH_SHORT).show();
//        }
//    }
    
    public ViewPager2_Adapter(Context ctx){
        this.ctx=ctx;

        //will use this to ceate swipe_layouts in onCreateViewHolder
        li=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.i(TAG, "ViewPager2_Adapter: ");
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        //call this when we need to create a brand new PagerViewHolder
        View view = li.inflate(R.layout.swipe_layout, parent, false);
        return new PagerViewHolder(view);   //the new one
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: ");
        //passing in an existing instance, reuse the internal resources
        //pass our data to our ViewHolder.
        PagerViewHolder viewHolder = (PagerViewHolder) holder;

        //set to some default image
        viewHolder.iv.setImageResource(R.drawable.error);
        viewHolder.tv.setText("Image : " + position);
        viewHolder.position=position;       //remember which image this view is bound to

        //launch a thread to 'retreive' the image
        GetImage myTask = new GetImage(viewHolder,(MainActivity) ctx);
        myTask.start();
    }

    @Override
    public int getItemCount() {
        //the size of the collection that contains the items we want to display
        return image_resources.length;
    }
}
