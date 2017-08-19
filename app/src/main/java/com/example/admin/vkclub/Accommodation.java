package com.example.admin.vkclub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by admin on 7/22/2017.
 */


public class Accommodation extends Fragment {

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "Accommodation";

    private static String[] Title;
    private static String[] Content;
    private static int[] Image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.accommodation, container, false);
        findView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((RecyclerAdapter) mAdapter).setOnItemClickListener(new RecyclerAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }

    private void findView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.accommodation_recycler_view);

        Title = new String[]{"Villa Suite", "Villa Jasmine", "Bungalow ", "Camping", "Khmer Cottage", "Luxury Tent", "Pipe Room"};
        Content = new String[]{
                "This two-bedroom villa with a mezzanine level is suitable for bigger families or group. This modern designed building has" +
                        " a bath and shower room and an open roof deck for BBQ party.",
                "Comparing of a combined bedroom, living, dining, kitchen area and an attached toilet, these versatile homes promises lots of many" +
                        "Pleasant experiences in the cool Kirirom mountain.",
                "Stay in one of our specially designed bungalows and experience the innovating fresh air and peaceful life in the pine forests of Kirirom." +
                        "Relax in our king size or twin-bed bungalows.",
                "Experience a fun and safe camping under the moon light and the twinkling stars in our camping ground or book one of our" +
                        "luxury tents with an en suite bathroom.",
                "Experience a real khmer traditional hut with the fresh and cool breeze. Stay in a khmer farmer's open-style house nicely" +
                        "decorated with ",
                "Luxury Tent-Stay in style and comfort in a high quality tent with the comfort of a hotel room. The tents come with a king" +
                        " size bed and en suite bathroom.",
        "A unique design to Cambodia and other Asian countries, the Pipe Rooms are affordable and cozy options. Ideal for students and couples" +
                " who want to experience a creatively."};
        Image = new int[]{R.drawable.borey_a, R.drawable.borey_r, R.drawable.bungalow, R.drawable.camping, R.drawable.khmercottage,
                R.drawable.luxurytent, R.drawable.piperoom};

        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                String[] links = getResources().getStringArray(R.array.accomodation1);
                Uri uri = Uri.parse(links[position]);
                Log.i("accomodation", links[position]);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();
            }
        }));
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < 7; index++) {
            DataObject obj = new DataObject(Image[index],
                    Title[index], Content[index]);
            results.add(index, obj);
        }
        return results;
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
