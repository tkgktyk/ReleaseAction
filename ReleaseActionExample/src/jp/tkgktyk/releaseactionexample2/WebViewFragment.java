package jp.tkgktyk.releaseactionexample2;

import java.util.ArrayList;
import java.util.List;

import jp.tkgktyk.releaseaction.ReleaseActionLayout;
import jp.tkgktyk.releaseaction.ReleaseActionLayout.Flag;
import jp.tkgktyk.releaseaction.SimpleReleaseActionLayout;
import jp.tkgktyk.releaseaction.SimpleReleaseActionLayout.SimpleOnReleaseActionListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewFragment extends Fragment {
	private static final String TAG = WebViewFragment.class.getSimpleName();
	public static final String ARG_URL = TAG + ".url";

	private SimpleReleaseActionLayout mReleaseAction;
	private WebView mWebView;
	private boolean mIsLoading;

	public WebViewFragment() {
	}

	public static Fragment newInstance(String url) {
		Fragment fragment = new WebViewFragment();
		if (!TextUtils.isEmpty(url)) {
			Bundle args = new Bundle();
			args.putString(ARG_URL, url);
			fragment.setArguments(args);
		}
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_web_view, container,
				false);

		mReleaseAction = (SimpleReleaseActionLayout) view
				.findViewById(R.id.release_action);
		mWebView = (WebView) view.findViewById(R.id.web_view);

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				mIsLoading = true;
				if (isAdded()) {
					getActivity().setProgressBarIndeterminateVisibility(true);
				}
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (isAdded()) {
					getActivity().setProgressBarIndeterminateVisibility(false);
				}
				load_jQuery();
				mIsLoading = false;
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient() {
		});

		if (savedInstanceState == null) {
			// at first time
			String url = "http://www.google.com/";
			if (getArguments() != null) {
				url = getArguments().getString(ARG_URL, url);
			}
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.loadUrl(url);
		} else {
			mWebView.restoreState(savedInstanceState);
		}

		mReleaseAction.setLeftTextView(R.drawable.ic_action_right);
		mReleaseAction.setTopTextView(R.drawable.ic_action_bottom);
		mReleaseAction.setRightTextView(R.drawable.ic_action_left);
		mReleaseAction.setBottomTextView(R.drawable.ic_action_top);
		mReleaseAction
				.setOnReleaseActionListener(new SimpleOnReleaseActionListener() {
					@TargetApi(Build.VERSION_CODES.KITKAT)
					@Override
					public void onReleaseAction(ReleaseActionLayout v) {
						if (mIsLoading) {
							Toast.makeText(getActivity(),
									"Wait for page to finish loading.",
									Toast.LENGTH_SHORT).show();
						}
						scroll(v);
					}

					private void scroll(ReleaseActionLayout v) {
						Flag flag = v.getActionFlag();
						List<String> scrolls = new ArrayList<String>();
						if (flag.has(ReleaseActionLayout.ACTION_LEFT)) {
							scrolls.add("scrollLeft: $(document).width()");
						}
						if (flag.has(ReleaseActionLayout.ACTION_TOP)) {
							scrolls.add("scrollTop: $(document).height()");
						}
						if (flag.has(ReleaseActionLayout.ACTION_RIGHT)) {
							scrolls.add("scrollLeft: 0");
						}
						if (flag.has(ReleaseActionLayout.ACTION_BOTTOM)) {
							scrolls.add("scrollTop: 0");
						}
						if (scrolls.size() > 0) {
							String script = "(function($){"
									+ "$('html, body').animate({"
									+ TextUtils.join(",", scrolls)
									+ "}, 'normal')" + "})(jQuery)";
							javascript(script);
						}
					}

					@Override
					public CharSequence getEnabledText(int action) {
						int res = 0;
						switch (action) {
						case ReleaseActionLayout.ACTION_TOP:
							res = R.string.release_bottom;
							break;
						case ReleaseActionLayout.ACTION_BOTTOM:
							res = R.string.release_top;
							break;
						}
						if (res == 0) {
							return null;
						}
						return getText(res);
					}
				});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mWebView.saveState(outState);
	}

	private void load_jQuery() {
		String script = "(function(){"
				+ "if (typeof jQuery == 'undefined') {"
				+ "var script = document.createElement('script');"
				+ "script.src = 'https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js';"
				+ "script.language='javascript';"
				+ "script.type='text/javascript';"
				+ "(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(script);}"
				+ "}" + ")()";
		javascript(script);
	}

	@SuppressLint("NewApi")
	private void javascript(String script) {
		Log.d(TAG, "javascript: " + script);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mWebView.evaluateJavascript(script, null);
		} else {
			mWebView.loadUrl("javascript:" + script);
		}
	}
}
