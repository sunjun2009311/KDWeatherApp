package org.jan.kdweather;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class MyFargemntPagerAdapter extends FragmentStatePagerAdapter{
	private ArrayList<WeatherFragment> flist;
	public MyFargemntPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public MyFargemntPagerAdapter(FragmentManager fm,ArrayList<WeatherFragment> flist){
		super(fm);
		this.flist=flist;
	}
	@Override
	public Fragment getItem(int position) {
		Log.d("debug","Fragment.getItem-->ponsition="+position+",flist.size="+flist.size());
		return flist.get(position);
	}

	@Override
	public int getCount() {
		return flist.size();
	}
 
	

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		super.notifyDataSetChanged();
	}
	
}
