package com.contralabs.inmap.controllers;

import android.content.res.Resources;
import android.graphics.BitmapFactory;

import com.contralabs.inmap.interfaces.LevelInformation;
import com.contralabs.inmap.interfaces.MapItem;
import com.contralabs.inmap.interfaces.MapLatLngConverter;
import com.contralabs.inmap.model.Coordinate;
import com.google.android.gms.maps.model.LatLng;

public class PreSettedMapLatLngConverter implements MapLatLngConverter {

	private LevelInformation mLevelInformation;
	private double sin, cos;
	private int[][] mapImageSizes;
	private int[][] mapImageSizesRotated;

	public PreSettedMapLatLngConverter(Resources resources, LevelInformation levelInformation, float bearing) {
		mLevelInformation = levelInformation;
		double rotation = Math.toRadians(bearing);
		cos = Math.cos(rotation);
		sin = Math.sin(rotation);
		calculeImageSizes(resources, levelInformation);
		calculeImagesSizesRotated(bearing);
	}

	private void calculeImagesSizesRotated(float bearing) {
		mapImageSizesRotated = new int[mapImageSizes.length][2];
		float rotationDegrees = (bearing+360)%360;
		int yPivotX, yPivotY, xPivotX, xPivotY;
		for(int i = 0; i < mapImageSizes.length; i++) {
			if(rotationDegrees <= 90 || (rotationDegrees >= 180 && rotationDegrees <= 270)) {
				yPivotX = -mapImageSizes[i][0]/2;
				yPivotY = -mapImageSizes[i][1]/2;
				xPivotX = -yPivotX;
				xPivotY = yPivotY;
			}else {
				xPivotX = -mapImageSizes[i][0]/2;
				xPivotY = -mapImageSizes[i][1]/2;
				yPivotX = -xPivotX;
				yPivotY = xPivotY;
			}
			mapImageSizesRotated[i][0] = (int) (2*Math.abs(xPivotX*cos-xPivotY*sin));
			mapImageSizesRotated[i][1] = (int) (2*Math.abs(yPivotX*sin+yPivotY*cos));
		}
	}

	public void calculeImageSizes(Resources resources,
			LevelInformation levelInformation) {
		mapImageSizes = new int[levelInformation.getLevelsCount()][2];
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		for(int i = 0; i < mapImageSizes.length; i++) {
			BitmapFactory.decodeResource(resources, levelInformation.getMapResource(i), options);
			mapImageSizes[i][1] = options.outHeight;
			mapImageSizes[i][0] = options.outWidth;
		}
	}

	@Override
	public LatLng getLatLng(MapItem item, int level) {
		int width = getMapImageWidth(level),
			height = getMapImageHeight(level);
		int x = item.getX() - width/2,
			y = item.getY() - height/2;
		int nX = (int) (x*cos-y*sin),
			nY = (int) (x*sin+y*cos);
		float pX = getPercentX(nX, level),
			pY = getPercentY(nY, level);
		LatLng northwestBound = getNorthwestBound(level),
			southeastBound = getSoutheastBound(level);
		return new LatLng(northwestBound.latitude - pY*(northwestBound.latitude-southeastBound.latitude), northwestBound.longitude + pX*(southeastBound.longitude-northwestBound.longitude));
	}

	private LatLng getNorthwestBound(int level) {
		return mLevelInformation.getNorthwestBound(level);
	}

	private LatLng getSoutheastBound(int level) {
		return mLevelInformation.getSoutheastBound(level);
	}

	private float getPercentX(int nX, int level) {
		float rotatedWidth = getRotatedWidth(level);
		return (nX+rotatedWidth/2f)/rotatedWidth;
	}

	private float getPercentY(int nY, int level) {
		float rotatedHeight = getRotatedHeight(level);
		return (nY+rotatedHeight/2)/rotatedHeight;
	}

	private int getRotatedWidth(int level) {
		return mapImageSizesRotated[level][0];
	}

	private int getRotatedHeight(int level) {
		return mapImageSizesRotated[level][1];
	}

	private int getMapImageWidth(int level) {
		return mapImageSizes[level][0];
	}

	private int getMapImageHeight(int level) {
		return mapImageSizes[level][1];
	}

	@Override
	public Coordinate getMapCoordinate(LatLng latlng, int level) {
		LatLng northwestBound = getNorthwestBound(level),
			southeastBound = getSoutheastBound(level);
		float pY = (float) ((northwestBound.latitude - latlng.latitude)/(northwestBound.latitude-southeastBound.latitude)),
			pX = (float) ((latlng.longitude - northwestBound.longitude)/(southeastBound.longitude-northwestBound.longitude));
		int nX = (int) (getRotatedWidth(level)*(pX - 0.5f)),
			nY = (int) (getRotatedHeight(level)*(pY - 0.5f));
		int x = (int) (nY*sin + nX*cos),
			y = (int) (-1*nX*sin + nY*cos);
		int halfwidth = getMapImageWidth(level)/2,
			halfheight = getMapImageHeight(level)/2;
		return new Coordinate(x > halfwidth ? -1 : x + halfwidth, y > halfheight ? -1 : y + halfheight);
	}

}
