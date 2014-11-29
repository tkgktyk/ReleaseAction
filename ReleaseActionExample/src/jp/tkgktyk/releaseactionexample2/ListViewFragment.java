package jp.tkgktyk.releaseactionexample2;

import jp.tkgktyk.releaseaction.ReleaseActionLayout;
import jp.tkgktyk.releaseaction.ReleaseActionLayout.Flag;
import jp.tkgktyk.releaseaction.SimpleReleaseActionLayout;
import jp.tkgktyk.releaseaction.SimpleReleaseActionLayout.SimpleOnReleaseActionListener;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListViewFragment extends Fragment {

	private SimpleReleaseActionLayout mReleaseAction;
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;

	public ListViewFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_view, container,
				false);

		mReleaseAction = (SimpleReleaseActionLayout) view
				.findViewById(R.id.release_action);
		mListView = (ListView) view.findViewById(R.id.list_view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mReleaseAction.setTopTextView(android.R.drawable.ic_menu_delete);
		mReleaseAction.setBottomTextView(android.R.drawable.ic_menu_add);
		mReleaseAction
				.setOnReleaseActionListener(new SimpleOnReleaseActionListener() {
					@Override
					public void onReleaseAction(ReleaseActionLayout v) {
						Flag flag = v.getActionFlag();
						int count = mAdapter.getCount();
						if (flag.has(ReleaseActionLayout.ACTION_TOP)) {
							// delete an item
							if (count == 0) {
								// nodata
							} else {
								mAdapter.remove(mAdapter.getItem(count - 1));
							}
						}
						if (flag.has(ReleaseActionLayout.ACTION_BOTTOM)) {
							// add an item
							mAdapter.add(Integer.toString(count));
						}
					}

					@Override
					public CharSequence getEnabledText(int action) {
						int res = 0;
						switch (action) {
						case ReleaseActionLayout.ACTION_TOP:
							res = R.string.release_delete;
							break;
						case ReleaseActionLayout.ACTION_BOTTOM:
							res = R.string.release_add;
							break;
						}
						if (res == 0) {
							return null;
						}
						return getText(res);
					}
				});

		TextView header = new TextView(getActivity());
		header.setText("Pull down!!");
		mListView.addHeaderView(header);

		TextView footer = new TextView(getActivity());
		footer.setText("Pull up!!");
		mListView.addFooterView(footer);

		mAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1);
		mListView.setAdapter(mAdapter);

		for (Integer i = 0; i < 5; ++i) {
			mAdapter.add(i.toString());
		}
	}
}
