package jp.tkgktyk.releaseactionexample;

import jp.tkgktyk.releaseaction.ReleaseActionLayout;
import jp.tkgktyk.releaseaction.ReleaseActionLayout.Flag;
import jp.tkgktyk.releaseaction.SimpleReleaseActionLayout;
import jp.tkgktyk.releaseaction.SimpleReleaseActionLayout.SimpleOnReleaseActionListener;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/*
 * section: 0 to 2
 * position: 0 to 2
 */
public class ViewPagerFragment extends Fragment {
	private static final int PAGE_COUNT = 3;
	private static final int MAX_SECTION = 3;

	private SimpleReleaseActionLayout mReleaseAction;
	private ViewPager mViewPager;

	private int mSection;

	public ViewPagerFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_view_pager, container,
				false);

		mReleaseAction = (SimpleReleaseActionLayout) view
				.findViewById(R.id.release_action);
		mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mReleaseAction.setLeftTextView(R.drawable.ic_action_left);
		mReleaseAction.setTopTextView(R.drawable.ic_action_top);
		mReleaseAction.setRightTextView(R.drawable.ic_action_right);
		mReleaseAction.setBottomTextView(R.drawable.ic_action_bottom);
		mReleaseAction
				.setOnReleaseActionListener(new SimpleOnReleaseActionListener() {
					@Override
					public void onReleaseAction(ReleaseActionLayout v) {
						Flag flag = v.getActionFlag();
						if (flag.has(ReleaseActionLayout.ACTION_LEFT)) {
							int next = mViewPager.getCurrentItem() - 1;
							next = numRotation(next, 0, PAGE_COUNT);
							mViewPager.setCurrentItem(next, true);
						}
						if (flag.has(ReleaseActionLayout.ACTION_RIGHT)) {
							int next = mViewPager.getCurrentItem() + 1;
							next = numRotation(next, 0, PAGE_COUNT);
							mViewPager.setCurrentItem(next, true);
						}
						if (flag.has(ReleaseActionLayout.ACTION_TOP)) {
							changeSection(mSection - 1);
						}
						if (flag.has(ReleaseActionLayout.ACTION_BOTTOM)) {
							changeSection(mSection + 1);
						}
					}

					@Override
					public CharSequence getEnabledText(int action) {
						int res = 0;
						switch (action) {
						case ReleaseActionLayout.ACTION_TOP:
							res = R.string.release_previous;
							break;
						case ReleaseActionLayout.ACTION_BOTTOM:
							res = R.string.release_next;
							break;
						}
						if (res == 0) {
							return null;
						}
						return getText(res);
					}
				});

		changeSection(0);
	}

	private int numRotation(int n, int min, int max) {
		if (n < min) {
			return max - 1;
		} else if (n >= max) {
			return min;
		}
		return n;
	}

	private void changeSection(int section) {
		mSection = numRotation(section, 0, MAX_SECTION);
		int cur = mViewPager.getCurrentItem();
		mViewPager.setAdapter(new Adapter(mSection));
		mViewPager.setCurrentItem(cur, true);
	}

	private class Adapter extends PagerAdapter {
		private int mSection;

		public Adapter(int section) {
			mSection = section;
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = getActivity().getLayoutInflater().inflate(
					R.layout.view_page, container, false);
			GridView grid = (GridView) view.findViewById(R.id.grid_view);
			grid.setNumColumns(PAGE_COUNT);
			grid.setAdapter(new GridAdapter(mSection, position));
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return "Section: " + (mSection + 1) + ", Position: "
					+ (position + 1);
		}
	}

	private class GridAdapter extends BaseAdapter {
		private int mSection;
		private int mPosition;

		public GridAdapter(int section, int position) {
			mSection = section;
			mPosition = position;
		}

		@Override
		public int getCount() {
			return PAGE_COUNT * MAX_SECTION;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView == null) {
				tv = new TextView(getActivity());
				tv.setGravity(Gravity.CENTER);
				tv.setLayoutParams(new GridView.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT));
			} else {
				tv = (TextView) convertView;
			}
			tv.setText(Integer.toString(position + 1));
			Log.d("grid adapter", "pos: " + position + ", mSection: "
					+ mSection + ", mPosition: " + mPosition);
			if (position == (mSection * PAGE_COUNT + mPosition)) {
				tv.setBackgroundResource(android.R.color.darker_gray);
			} else {
				tv.setBackgroundResource(R.drawable.boundary);
			}
			return tv;
		}
	}
}
