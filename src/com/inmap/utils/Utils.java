package com.inmap.utils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

public class Utils {

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static <Params, Progress, Result> void executeInParallel(AsyncTask<Params, Progress, Result> task, Params... params){
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			task.execute(params);
		else
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	}
}
