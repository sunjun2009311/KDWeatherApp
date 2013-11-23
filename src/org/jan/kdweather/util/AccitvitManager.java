package org.jan.kdweather.util;

import java.util.ArrayList;
import java.util.List;

import org.jan.kdweather.location.Location;

import android.app.Activity;
import android.content.Context;

public class AccitvitManager extends Location {
	 private List<Activity> list = new ArrayList<Activity>();
	 
	    private static AccitvitManager ea;

	    private AccitvitManager() {

	    }

	    public static AccitvitManager getInstance() {
	        if (null == ea) {
	            ea = new AccitvitManager();
	        }
	        return ea;
	    }

	   
	    public void addActivity(Activity activity) {
	        list.add(activity);
	    }

	    public void exit(Context context) {
	        for (Activity activity : list) {
	            activity.finish();
	        }
	        System.exit(0);
	    }

}