package com.inmap.views;

import com.inmap.R;
import com.inmap.interfaces.InMapViewController;
import com.inmap.interfaces.MapController;
import com.inmap.interfaces.MapItem;
import com.inmap.interfaces.MapItemsListener;
import com.inmap.interfaces.StoreMapItem;
import com.inmap.views.TranslateHelper.TranslateItem;
import com.inmap.views.ZoomHelper.ZoomItem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class InMapImageView extends ImageView implements InMapViewController, TranslateItem, ZoomItem, MapItemsListener {

	private static final float MAX_MOVE_TO_ALLOW_CLICK = 0;
	private float zoomControl;
	private int xControl, yControl;
	private Drawable mDrawable;
	private int widthLimit, heightLimit, leftMap, topMap;
	private TranslateHelper mTranslateHelper;
	private ZoomHelper mZoomHelper;
	private int level;
	private MapController mMapController;
	private MapItem[] mMapItems;
	private Bitmap mBitmapBallon;
	private Paint mPaintTitle, mPaintSubtext;
	private boolean showingInformationBalloon;
	private StoreMapItem mStoreMapItemShowingInformation;

	private int TRANSLATE_MARGIN;

	public InMapImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public InMapImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public InMapImageView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		setParametersToDefault();
		mBitmapBallon = BitmapFactory.decodeResource(context.getResources(), R.drawable.balloon);
		mPaintTitle = new Paint();
		mPaintTitle.setTextSize(33);
		
		mPaintSubtext = new Paint();
		mPaintSubtext.setTextSize(20);
		//mPaintSubtext.
	
	}

	@Override
	protected void onDraw(Canvas canvas) {
		//super.onDraw(canvas);
		initControls();
		drawMap(canvas);
		drawMapItems(canvas);
		drawInformationBalloon(canvas);
	}

	private void initControls() {
		if(zoomControl == 0){
			zoomControl = getLimitedZoom(0);
			TRANSLATE_MARGIN = getWidth()/10;
		}
	}

	private void drawMap(Canvas canvas) {
		int xCenter = (getWidth()/2),
		yCenter = (getHeight()/2);
		int horizontalOffset = getHorizontalOffset(),
		verticalOffset = getVerticalOffset();
		leftMap = xCenter-horizontalOffset+xControl;
		topMap = yCenter-verticalOffset+yControl;
		int right = xCenter+horizontalOffset+xControl,
		bottom = yCenter+verticalOffset+yControl;
		mDrawable.setBounds(leftMap, topMap, right, bottom);
		mDrawable.draw(canvas);
	}

	private void drawMapItems(Canvas canvas) {
		if(mMapItems != null){
			int width = getWidth(),
			height = getHeight();
			for(MapItem item : mMapItems){
				Bitmap bitmap = item.getMapIconBitmap();
				float x = getScreenX(item.getX()),
				y = getScreenY(item.getY());

				if(x > 0-bitmap.getWidth() && x < width && y > 0-bitmap.getHeight() && y < height)
					canvas.drawBitmap(bitmap, x-bitmap.getWidth()/2, y-bitmap.getHeight()/2, null);
			}
		}
	}

	private void drawInformationBalloon(Canvas canvas) {
		if(showingInformationBalloon){
			float x = getScreenX(mStoreMapItemShowingInformation.getX()),
			y = getScreenY(mStoreMapItemShowingInformation.getY());
			canvas.drawBitmap(mBitmapBallon, x-mBitmapBallon.getWidth()/2, y-mBitmapBallon.getHeight(), null);
			canvas.drawText(mStoreMapItemShowingInformation.getTitle(), x-mBitmapBallon.getWidth()/2+10, 
					y-mBitmapBallon.getHeight()+40, mPaintTitle);
			canvas.drawText(mStoreMapItemShowingInformation.getSubtext(), x-mBitmapBallon.getWidth()/2+10, 
					y-mBitmapBallon.getHeight()/2, mPaintSubtext);
		}
	}

	private float getScreenX(int x) {
		return leftMap + x*zoomControl;
	}

	private float getScreenY(int y) {
		return topMap + y*zoomControl;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = (event.getAction() & MotionEvent.ACTION_MASK);
		switch(action){
		case MotionEvent.ACTION_DOWN:
			mTranslateHelper = new TranslateHelper(this, event.getX(), event.getY());
			break;

		case MotionEvent.ACTION_MOVE:
			if(mZoomHelper != null){
				mZoomHelper.zoom(event);
			} else if(mTranslateHelper != null){
				mTranslateHelper.move(event.getX(), event.getY());
			}
			break;
		case MotionEvent.ACTION_UP:
			if(mTranslateHelper.getTotalMoved() <= MAX_MOVE_TO_ALLOW_CLICK)
				onClick(event.getX(), event.getY());
			mTranslateHelper = null;
			break;
		case MotionEvent.ACTION_POINTER_DOWN: // Two pointers
			mZoomHelper = new ZoomHelper(this,event);
			break;
		case MotionEvent.ACTION_POINTER_UP: // Two pointers
			mZoomHelper = null;
			int index = 1-(event.getAction() & ~MotionEvent.ACTION_MASK)/256;
			mTranslateHelper = new TranslateHelper(this, event.getX(index), event.getY(index));
			break;
		}

		return true;
	}

	private void onClick(float x, float y) {
		Log.d("InMapImageView", "x: " + x + " y: " + y);
		if(showingInformationBalloon){
			showingInformationBalloon = false;
			invalidate();
			return;
		}
		if(mMapItems != null){
			for(MapItem item : mMapItems)
				if(item instanceof StoreMapItem){
					StoreMapItem store = (StoreMapItem) item;
					if(Math.abs(getScreenX(store.getX())-x) + Math.abs(getScreenY(store.getY())-y) <= store.getCircularArea()){
						onClickedStoreMapItem(store);
						break;
					}
				}
		}
	}

	private void onClickedStoreMapItem(StoreMapItem store) {
		showingInformationBalloon = true;
		mStoreMapItemShowingInformation = store;
		invalidate();
	}

	@Override
	public void zoomIn() {
		float zoomAfter = zoomControl * 1.2f;
		zoom(zoomAfter);
	}

	@Override
	public void zoomOut() {
		float zoomAfter = zoomControl * 0.8f;
		zoom(zoomAfter);
	}

	private void zoom(float zoomAfter) {
		int hOffset = getHorizontalOffset(),
		vOffset = getVerticalOffset();
		zoomControl = getLimitedZoom(zoomAfter);
		xControl = getLimitedXControl((float)xControl*(float)getHorizontalOffset()/hOffset);
		yControl = getLimitedYControl((float)yControl*(float)getVerticalOffset()/vOffset);
		invalidate();
	}

	private float getLimitedZoom(float zoomAfter) {
		float width = getWidth(),
		height = getHeight();
		float upLimit = 4,
		bottomLimit = Math.min(width/widthLimit, height/heightLimit);
		return Math.max(Math.min(zoomAfter, upLimit),
				bottomLimit); 
	}

	private int getLimitedXControl(float x) {
		float upLimit, bottomLimit;
		int hOffset = getHorizontalOffset(),
		width = getWidth();
		if(hOffset*2 < width){
			upLimit = (width/2)-hOffset;
			bottomLimit = hOffset-(width/2);
		}else{
			upLimit = hOffset-(width/2);
			bottomLimit = (width/2)-hOffset;
		}
		upLimit += TRANSLATE_MARGIN;
		bottomLimit -= TRANSLATE_MARGIN;
		return (int) Math.max(Math.min(x, upLimit),
				bottomLimit); 
	}

	private int getLimitedYControl(float y) {
		float upLimit, bottomLimit;
		int vOffset = getVerticalOffset(),
		height = getHeight();
		if(vOffset*2 < height){
			upLimit = (height/2)-vOffset;
			bottomLimit = vOffset-(height/2);
		}else{
			upLimit = vOffset-(height/2);
			bottomLimit = (height/2)-vOffset;
		}
		upLimit += TRANSLATE_MARGIN;
		bottomLimit -= TRANSLATE_MARGIN;
		return (int) Math.max(Math.min(y, upLimit),
				bottomLimit); 
	}

	private int getVerticalOffset() {
		return (int) (heightLimit*zoomControl/2);
	}

	private int getHorizontalOffset() {
		return (int) (widthLimit*zoomControl/2);
	}

	@Override
	public void move(float x, float y) {
		xControl = getLimitedXControl(xControl + x);
		yControl = getLimitedYControl(yControl + y);
		invalidate();
	}

	@Override
	public void zoom(float x, float y, float zoom) {
		// TODO zoom in the x,y point
		zoom(zoomControl*zoom);
	}

	@Override
	public void setLevel(int level, int mapResource) {
		setImageResource(mapResource);
		setParametersToDefault();
	}

	private void setParametersToDefault() {
		zoomControl = xControl = yControl = 0;
		mDrawable = getDrawable();
		heightLimit = mDrawable.getMinimumHeight();
		widthLimit = mDrawable.getMinimumWidth();
	}

	@Override
	public void setMapController(MapController controller) {
		mMapController = controller;
		mMapItems = mMapController.getMapItems();
		mMapController.setMapItemsListener(this);
	}

	@Override
	public void refreshMapItemsListener() {
		mMapItems = mMapController.getMapItems();
		showingInformationBalloon = false;
		invalidate();
	}
}
