package com.thomasezan.segmentedsliderwidget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by thomas on 2/14/15.
 */
public class SliderSelector extends RelativeLayout implements View.OnTouchListener{

    // UI Objects
    private RelativeLayout sliderTray;
    private View sliderView;
    private LinearLayout labelLayout;

    private ArrayList<View> segmentViews = new ArrayList<>();

    // Control
    private int currentPosition = 0;

    private OnSegmentSelectedListener segmentSelectedListener;

    public SliderSelector(Context context) {
        super(context);
        init();
    }

    public SliderSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SliderSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init(){
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.slider_view, this, true);

        sliderTray = (RelativeLayout)findViewById(R.id.tray_layout);
        sliderView = findViewById(R.id.slider_view);
        labelLayout = (LinearLayout)findViewById(R.id.labels_layout);

        sliderTray.setOnTouchListener(this);
    }


    public int getCurrentPosition(){
        return currentPosition;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float touchLocation = event.getX();
        float sliderViewCenterX = sliderView.getWidth()/2;

        // Move the slider along the tray
        if (event.getAction()==MotionEvent.ACTION_MOVE
                && touchLocation - sliderViewCenterX > 0 && touchLocation + sliderViewCenterX < getWidth()
                ){

            sliderView.setX(touchLocation - sliderViewCenterX);
        }

        // Set the final position of the slider on release (ACTION_UP)
        if (event.getAction()==MotionEvent.ACTION_UP){
            float finalPosition = 0;

            int childCount = segmentViews.size();
            for (int i = 0; i<childCount; i++){
                if (touchLocation<((getWidth()*(i+1))/childCount)){
                    View currentView = segmentViews.get(i);
                    finalPosition = currentView.getX();
                    currentPosition = i;
                    if (segmentSelectedListener!=null){
                        segmentSelectedListener.onSegmentSelected(currentPosition);
                    }
                    break;
                }
            }

            // Animate the slider
            ValueAnimator animator = ValueAnimator.ofFloat(sliderView.getX(), finalPosition);


            animator.setDuration(100);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sliderView.setX((Float)animation.getAnimatedValue());
                }
            });

            animator.start();
        }

        return true;
    }

    public void setSegmentViews(ArrayList<View> segmentViews) {
        this.segmentViews = segmentViews;

        if (segmentViews!=null && segmentViews.size()>0){
            for (int i=0; i<segmentViews.size(); i++){
                labelLayout.addView(formatTextView(segmentViews.get(i)));
            }
        }

        sliderView.post(new Runnable() {
            @Override
            public void run() {
                sliderView.setLayoutParams(new RelativeLayout.LayoutParams((labelLayout.getMeasuredWidth()/SliderSelector.this.segmentViews.size()) , RelativeLayout.LayoutParams.WRAP_CONTENT));
            }
        });
    }

    private View formatTextView(View view){
        if (view instanceof TextView){
            ((TextView)view).setGravity(Gravity.CENTER);
        }
        view.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1f));

        return view;
    }

    public void setSegmentSelectedListener(OnSegmentSelectedListener segmentSelectedListener) {
        this.segmentSelectedListener = segmentSelectedListener;
    }
}
