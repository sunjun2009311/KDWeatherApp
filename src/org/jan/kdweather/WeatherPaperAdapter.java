package org.jan.kdweather;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class WeatherPaperAdapter extends PagerAdapter{

	public WeatherPaperAdapter(){
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.destroyItem(container, position, object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		return super.instantiateItem(container, position);
	}

	

}
