package com.emg_soft.businesspuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitContainer;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitItem;
import com.emg_soft.businesspuzzle.workcircuit.circuitdata.CircuitTrack;
import com.emg_soft.businesspuzzle.workcircuit.circuitviews.CircuitTrackView;
import com.emg_soft.businesspuzzle.workcircuit.circuitviews.CircuitViewItem;
import com.emg_soft.businesspuzzle.workcircuit.circuitviews.CircuitViewItemInput;
import com.emg_soft.businesspuzzle.workcircuit.circuitviews.CircuitViewItemOperator;
import com.emg_soft.businesspuzzle.workcircuit.circuitviews.CircuitViewItemResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yura Vyrovoy on 9/6/2017.
 */

public class CircuitLayout extends ViewGroup {

    private static final String TAG = CircuitLayout.class.getSimpleName();

    public static final int STANDARD_BORDER = 50;
    public static final int INPUT_HEIGHT_QUANTUM = 20;
    public static final int TRACK_WIDTH = 5;

    public static final int INPUT_SHIFT = 80;

    public static final int OPERATOR_MAX_SIZE = 320;
    public static final int INPUT_MAX_SIZE = 480;
    public static final int RESULT_MAX_SIZE = 1000;


    private int deviceWidth;
    private int deviceHeight;

    private CircuitContainer mCircuit = null;

    private List<CircuitViewItemInput> lstViewsInputs = new ArrayList<>();
    private List<CircuitViewItemOperator> lstViewsOperators = new ArrayList<>();
    private List<CircuitViewItemResult> lstViewsResults = new ArrayList<>();

    Map<CircuitItem, CircuitViewItem> mapItems = new HashMap<>();
    Map<CircuitTrack, CircuitTrackView> mapTracks = new HashMap<>();

    private int levelNumber;


    public CircuitLayout(Context context) {
        super(context);
        init(this.getContext());
    }

    public CircuitLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(this.getContext());
    }

    public CircuitLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(this.getContext());
    }

    private void init(Context context) {

        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);

        deviceWidth = deviceDisplay.x;
        deviceHeight = deviceDisplay.y;
/*
        Bitmap bitmapSource = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmapSource, deviceWidth, deviceHeight, true);
        setBackground(new BitmapDrawable(getResources(), bitmapScaled) );
*/
    }

    private void layoutInputs(int childLeft, int childTop, int childRight, int childBottom){

        int curWidth, curHeight, curLeft, curTop, maxHeight;

        int childWidth = childRight - childLeft;
        int childHeight = childBottom - childTop;

        maxHeight = 0;
        curTop = childTop;

        int itemInputsWidthQuantum = childWidth/ lstViewsInputs.size();

        for (int iInput = 0; iInput < lstViewsInputs.size(); iInput++) {

            CircuitViewItemInput child = lstViewsInputs.get(iInput);

            if (child.getVisibility() == GONE) {
                return;
            }

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));
            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();

            curLeft = itemInputsWidthQuantum/2 + itemInputsWidthQuantum * iInput - curWidth/2;

            //do the layout
            child.layout(curLeft,
                    curTop,
                    curLeft + curWidth,
                    curTop + curHeight);

            //store the max height
            if (maxHeight < curHeight){
                maxHeight = curHeight;
            }

        }

        int inputBottom = curTop + maxHeight;
    }

    private void layoutResults(int childLeft, int childTop, int childRight, int childBottom){

        int curWidth, curHeight, curLeft, curTop;

        int childWidth = childRight - childLeft;
        int childHeight = childBottom - childTop;

        int maxChildHeight = 0;

        for (int iResult = 0; iResult < lstViewsResults.size(); iResult++) {
            CircuitViewItemResult child = lstViewsResults.get(iResult);
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));

            maxChildHeight = Math.max(maxChildHeight,  child.getMeasuredHeight());
        }

        curTop = childBottom - maxChildHeight;

        int itemResultsWidthQuantum = childWidth/ lstViewsResults.size();

        for (int iResult = 0; iResult < lstViewsResults.size(); iResult++) {

            CircuitViewItemResult child = lstViewsResults.get(iResult);

            if (child.getVisibility() == GONE) {
                return;
            }

            //Get the maximum size of the child
            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));

            curWidth = child.getMeasuredWidth();
            curHeight = child.getMeasuredHeight();

            curLeft = itemResultsWidthQuantum/2 + itemResultsWidthQuantum * iResult - curWidth/2;

            //do the layout
            child.layout(curLeft,
                    curTop,
                    curLeft + curWidth,
                    curTop + curHeight);

        }
    }

    private void layoutOperators(int childLeft, int childTop, int childRight, int childBottom){

        int curWidth, curHeight, curLeft, curTop;

        int childWidth = childRight - childLeft;
        int childHeight = childBottom - childTop;


        // create list to divide views by levels
        List<List<CircuitViewItemOperator>> lstLevels = new ArrayList<>();
        for(int i = 0; i < levelNumber; i++){
            lstLevels.add(new ArrayList<CircuitViewItemOperator>());
        }

        for(CircuitViewItemOperator item : lstViewsOperators){
            lstLevels.get(item.getLevel()).add(item);
        }

        int heightOperatorQuantum = childHeight/levelNumber;

        // lets put level by level in layout
        for(int iLevel = 0; iLevel < levelNumber; iLevel++){

            int levelWidthQuantum = childWidth/lstLevels.get(iLevel).size();

            for(int iOperator = 0; iOperator < lstLevels.get(iLevel).size(); iOperator++) {

                CircuitViewItemOperator child = lstLevels.get(iLevel).get(iOperator);

                if (child.getVisibility() == GONE) {
                    return;
                }

                //Get the maximum size of the child
                child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST));

                curWidth = child.getMeasuredWidth();
                curHeight = child.getMeasuredHeight();

                curLeft = levelWidthQuantum/2 + levelWidthQuantum * iOperator - curWidth/2;
                curTop = childTop + heightOperatorQuantum/2 + heightOperatorQuantum * iLevel - curHeight/2;


                //do the layout
                child.layout(curLeft,
                        curTop,
                        curLeft + curWidth,
                        curTop + curHeight);
            }

        }
    }

    private void layoutTracks(int childLeft, int childTop, int childRight, int childBottom){

        List<CircuitTrackView> lstTracksView = new ArrayList<>(mapTracks.values());

        for(CircuitTrackView trackView : lstTracksView){

            View viewStart = (View) trackView.getViewStart();
            int startTop = viewStart.getTop();
            int startLeft = viewStart.getLeft();
            int startBottom = viewStart.getBottom();
            int startRight = viewStart.getRight();

            View viewEnd = (View) trackView.getViewEnd();
            int endTop = viewEnd.getTop();
            int endLeft = viewEnd.getLeft();
            int endBottom = viewEnd.getBottom();
            int endRight = viewEnd.getRight();

            // presume that start is ALWAYS higher than end
            int nTop = startBottom;
            int nBottom = endTop;
            int outX = (startRight + startLeft)/2;


            // check how input is located - left, right or center

            CircuitItem.InputType inputType = ((CircuitViewItem) viewEnd)
                                                    .getCircuitItem()
                                                    .getItemInputNumber(
                                                            ((CircuitViewItem) viewStart)
                                                                                .getCircuitItem());

            // locating track rectangle:
            // we need to have the border to be able to draw track in the center of in or out
            // but having place to draw it with the width greater than 1px

            int inX;

            int inCenter = (endRight + endLeft)/2;
            int inDistance = INPUT_SHIFT;

            if( inputType == CircuitItem.InputType.INPUT_ONE ){
                inX = inCenter - inDistance;
            }
            else if( inputType == CircuitItem.InputType.INPUT_TWO ){
                inX = inCenter + inDistance;
            }
            else if( inputType == CircuitItem.InputType.INPUT_ONLY ){
                inX = inCenter;
            }
            else{
                continue;
            }

            int nLeft;
            int nRight;

            // check how in and out relates
            // who's on the left and who's on the right

            if(outX >= inX){
                nLeft = inX - STANDARD_BORDER;
                nRight = outX + STANDARD_BORDER;
                trackView.setInputLeft(false);
            }
            else{
                nLeft = outX - STANDARD_BORDER;
                nRight = inX + STANDARD_BORDER;
                trackView.setInputLeft(true);
            }


            //do the layout
            trackView.layout(Math.min(nLeft, nRight),
                                nTop,
                                Math.max(nLeft, nRight),
                                nBottom);

        }

        arrangeInputsSublevels();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //get the available size of child view
        final int childLeft = this.getPaddingLeft();
        final int childTop = this.getPaddingTop();
        final int childRight = this.getMeasuredWidth() - this.getPaddingRight();
        final int childBottom = this.getMeasuredHeight() - this.getPaddingBottom();


        // measure max height for each items group

        layoutInputs(childLeft, childTop, childRight, childBottom);

        int maxHeightInput = 0;
        for(CircuitViewItemInput viewItemInput : getLstViewsInputs()){
            int curHeight = viewItemInput.getMeasuredHeight();
            maxHeightInput = Math.max(maxHeightInput, curHeight);
        }

        layoutResults(childLeft, childTop, childRight, childBottom);

        int maxHeightResult = 0;
        for(CircuitViewItemResult viewItemResult : getLstViewsResults()){
            int curHeight = viewItemResult.getMeasuredHeight();
            maxHeightResult = Math.max(maxHeightResult, curHeight);
        }

        int maxHeightOperator = 0;
        for(CircuitViewItemOperator viewItemOperator : getLstViewsOperators()){
            int curHeight = viewItemOperator.getMeasuredHeight();
            maxHeightOperator = Math.max(maxHeightOperator, curHeight);
        }



        //  laying out circuit parts

        int operatorsTop = childTop + maxHeightInput;
        int operatorsBottom = childBottom - maxHeightResult;

        layoutOperators(childLeft, operatorsTop, childRight, operatorsBottom);

        layoutTracks(childLeft, childTop, childRight, childBottom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void createLayoutFromCircuit(CircuitContainer circuit, Context context) {

        mCircuit = circuit;
        setLevelNumber(mCircuit.getLstOperatorLevels().size());

        createAndAddItemsViews(context);
        createAndAddTracksViews(context);
    }

    private void createAndAddItemsViews(Context context){

        int baseID  = 2017;

        // adding results
        for(int iResult = 0; iResult < mCircuit.getLstResults().size(); iResult++){

            CircuitViewItemResult viewResult = new CircuitViewItemResult(context);

            viewResult.setCircuitItem(mCircuit.getLstResults().get(iResult));
            viewResult.setId(baseID++);

            super.addView(viewResult);
            mapItems.put(viewResult.getCircuitItem(), viewResult);

            lstViewsResults.add(viewResult);
        }

        // adding inputs
        for(int iInput = 0; iInput < mCircuit.getLstInputs().size(); iInput++){

            CircuitViewItemInput cbxInput = new CircuitViewItemInput(context);
            cbxInput.setCircuitItem(mCircuit.getLstInputs().get(iInput));
            cbxInput.setId(baseID++);

            cbxInput.getCircuitItem().addChangeListener(new CircuitItem.CircuitItemChangeListener() {
                @Override
                public void onChange(boolean bOutputTrue) {
                    recalcResult();
                }
            });

            super.addView(cbxInput);
            mapItems.put(cbxInput.getCircuitItem(), cbxInput);

            lstViewsInputs.add(cbxInput);
        }

        // adding operators
        for(int iOperator = 0; iOperator < mCircuit.getLstOperators().size(); iOperator++){

            CircuitViewItemOperator viewOperator = new CircuitViewItemOperator(context);
            viewOperator.setCircuitItem(mCircuit.getLstOperators().get(iOperator));
            viewOperator.setId(baseID++);

            super.addView(viewOperator);
            mapItems.put(viewOperator.getCircuitItem(), viewOperator);

            lstViewsOperators.add(viewOperator);
        }
    }

    public void recalcResult(){

        for(CircuitViewItemResult item : lstViewsResults){
            item.calcValue();
        }

        List<CircuitTrackView> lstTracksViews = getTracksViewsList();
        for(CircuitTrackView viewTrack : lstTracksViews) {
            viewTrack.updateState();
        }
    }

    public void createAndAddTracksViews(Context context) {

        List<CircuitTrack> lstTracks = mCircuit.getLstTracks();

        for(CircuitTrack track : lstTracks){

            CircuitViewItem viewStart = getView(track.getItemStart());
            CircuitViewItem viewEnd = getView(track.getItemEnd());

            CircuitTrackView trackView = new CircuitTrackView(viewStart, viewEnd, context);


            mapTracks.put(track, trackView);
            super.addView(trackView);
        }

        // take inputs, result and operators to the top
        for(CircuitViewItem viewItem : lstViewsOperators){
            bringChildToFront((View)viewItem);
        }

        for(CircuitViewItem viewItem : lstViewsInputs){
            bringChildToFront((View)viewItem);
        }

        for(CircuitViewItem viewItem : lstViewsResults){
            bringChildToFront((View)viewItem);
        }

    }

    private void arrangeInputsSublevels(){

        // !!!! counting level from RESULT TO INPUT

        int NLevels = mCircuit.getLevelNumber() + 1;

        // prepare list of every level list of track view
        List<List<CircuitTrackView>> lstTracksLevels = new ArrayList<>();
        for(int iLevel = 0; iLevel < NLevels; iLevel++){
            lstTracksLevels.add(new ArrayList<CircuitTrackView>());
        }

        // create tracks list
        List<CircuitTrackView> lstTracksViews = new ArrayList<>(mapTracks.values());

        // disperse tracks by levels
        for(CircuitTrackView viewTrack : lstTracksViews){
            int nL = viewTrack.getLevel();

            // if this track is linked to result
            if(nL == -1){
                nL = NLevels - 1;
            }

            lstTracksLevels.get(nL).add(viewTrack);
        }


        // process each list separately
        for(List<CircuitTrackView> lstLevel : lstTracksLevels){
            arrangeTracksOneLevel(lstLevel);
        }

    }

    private void arrangeTracksOneLevel(List<CircuitTrackView> lstLevel){

        // sorting track views by width. acceding

        Collections.sort(lstLevel, new Comparator<CircuitTrackView>() {

            @Override
            public int compare(CircuitTrackView circuitTrackView, CircuitTrackView t1) {

            return circuitTrackView.getWidth()- t1.getWidth();
            }
        });

        List<CircuitTrackView> lstTmp = new ArrayList<>(lstLevel);

        Log.i(TAG, "Sort:");
        for(CircuitTrackView t : lstTmp){
            Log.i(TAG, "w = " + t.getWidth());
        }



        for(int iCurrent = 0; iCurrent < lstLevel.size(); iCurrent++){

            int subLevel = lstLevel.get(iCurrent).getSubLevel();

            for(int iNext = iCurrent+1; iNext < lstLevel.size(); iNext++){

                if(lstLevel.get(iCurrent).isViewCrossed(lstLevel.get(iNext)) == true)
                {
                    lstLevel.get(iNext).setSubLevel(subLevel + 1);
                }

            }
        }


    }

    @Nullable
    private CircuitViewItem getView(CircuitItem item){

        for(int iChild = 0; iChild < getChildCount(); iChild++){

            CircuitViewItem view = null;
            try {
                view = (CircuitViewItem)getChildAt(iChild);
            }catch (ClassCastException ex){
                view = null;
                continue;
            }

            if(view.getCircuitItem() == item){
                return view;
            }
        }

        return null;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {

        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());

        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }


    // setters and getters
    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public List<CircuitViewItemInput> getLstViewsInputs() {
        return lstViewsInputs;
    }

    public List<CircuitViewItemOperator> getLstViewsOperators() {
        return lstViewsOperators;
    }

    public List<CircuitViewItemResult> getLstViewsResults() {
        return lstViewsResults;
    }

    public CircuitContainer getmCircuit() {
        return mCircuit;
    }

    public List<CircuitTrackView> getTracksViewsList(){
        return new ArrayList<>(mapTracks.values());
    }









}

