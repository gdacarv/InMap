package com.contralabs.inmap.utils;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.util.DisplayMetrics;

public class Utils {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <Params, Progress, Result> void executeInParallel(AsyncTask<Params, Progress, Result> task, Params... params){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			task.execute(params);
		else
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	}
	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device density. 
	 * 
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @param resources Resources to get device specific display metrics
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static float convertDpToPixel(float dp, Resources resources){
	    DisplayMetrics metrics = resources.getDisplayMetrics();
	    float px = dp * (metrics.densityDpi / 160f);
	    return px;
	}
}
