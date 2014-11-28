package jp.tkgktyk.releaseaction;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class SimpleReleaseActionLayout extends ReleaseActionLayout {
	private static final String TAG = SimpleReleaseActionLayout.class
			.getSimpleName();

	public SimpleReleaseActionLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public SimpleReleaseActionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SimpleReleaseActionLayout(Context context) {
		super(context);
	}

	public void setLeftTextView() {
		setLeftTextView(0);
	}

	public void setLeftTextView(int drawable) {
		TextView tv = new TextView(getContext());
		tv.setGravity(Gravity.CENTER_VERTICAL);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
		tv.setLayoutParams(lp);
		tv.setCompoundDrawablesWithIntrinsicBounds(0, drawable, 0, 0);
		tv.setText(" "); // if text isn't set, position of drawable is strange.
		tv.setVisibility(View.INVISIBLE);
		setLeftView(tv);
	}

	public void setTopTextView(int drawable) {
		TextView tv = new TextView(getContext());
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
		tv.setLayoutParams(lp);
		tv.setCompoundDrawablesWithIntrinsicBounds(0, drawable, 0, 0);
		tv.setText(R.string.keep_on_pulling);
		setTopView(tv);
	}

	public void setRightTextView(int drawable) {
		TextView tv = new TextView(getContext());
		tv.setGravity(Gravity.CENTER_VERTICAL);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
		tv.setLayoutParams(lp);
		tv.setCompoundDrawablesWithIntrinsicBounds(0, drawable, 0, 0);
		tv.setText(" "); // if text isn't set, position of drawable is strange.
		tv.setVisibility(View.INVISIBLE);
		setRightView(tv);
	}

	public void setBottomTextView(int drawable) {
		TextView tv = new TextView(getContext());
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
		tv.setLayoutParams(lp);
		tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, drawable);
		tv.setText(R.string.keep_on_pulling);
		setBottomView(tv);
	}

	public static abstract class SimpleOnReleaseActionListener implements
			OnReleaseActionListener {
		@Override
		public final void onActionEnabled(ReleaseActionLayout parent, View v,
				int action) {
			TextView tv = (TextView) v;
			switch (action) {
			case ACTION_LEFT:
			case ACTION_RIGHT:
				tv.setVisibility(View.VISIBLE);
				break;
			case ACTION_TOP:
			case ACTION_BOTTOM:
				tv.setText(getEnabledText(action));
				break;
			}
		}

		public abstract CharSequence getEnabledText(int action);

		@Override
		public final void onActionDisabled(ReleaseActionLayout parent, View v,
				int action) {
			TextView tv = (TextView) v;
			switch (action) {
			case ACTION_LEFT:
			case ACTION_RIGHT:
				tv.setVisibility(View.INVISIBLE);
				break;
			case ACTION_TOP:
			case ACTION_BOTTOM:
				tv.setText(R.string.keep_on_pulling);
				break;
			}
		}

	}
}