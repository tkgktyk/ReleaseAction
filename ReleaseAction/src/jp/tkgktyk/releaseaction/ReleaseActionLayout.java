package jp.tkgktyk.releaseaction;

import java.util.ArrayList;
import java.util.List;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

public class ReleaseActionLayout extends FrameLayout {
	private static final String TAG = ReleaseActionLayout.class.getSimpleName();

	private static int DEFAULT_CHILD_GRAVITY = Gravity.TOP | Gravity.START;

	/**
	 * Sentinel value for no current active pointer. Used by
	 * {@link #mActivePointerId}.
	 */
	private static final int INVALID_POINTER = -1;
	/**
	 * ID of the active pointer. This is used to retain consistency during
	 * drags/flings if multiple pointers are used.
	 */
	private int mActivePointerId = INVALID_POINTER;
	private int mTouchSlop;
	/**
	 * True if the user is currently dragging this ScrollView around. This is
	 * not the same as 'is being flinged', which can be checked by
	 * mScroller.isFinished() (flinging begins when the user lifts his finger).
	 */
	private boolean mIsBeingDragged = false;
	/**
	 * Position of the last motion event.
	 */
	private int mLastMotionX;
	private int mLastMotionY;

	private boolean mEnableTouchEventX;
	private boolean mEnableTouchEventY;
	private int mOffsetX;
	private int mOffsetY;

	private Rect mChildRect;

	private Flag mActionFlag;
	public static final int ACTION_LEFT = 0x01;
	public static final int ACTION_TOP = 0x02;
	public static final int ACTION_RIGHT = 0x04;
	public static final int ACTION_BOTTOM = 0x08;

	private void fetchAttribute(Context context, AttributeSet attrs,
			int defStyle) {
	}

	private void initialize() {
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		mChildRect = new Rect();
		mActionFlag = new Flag();
		initializeAroundView();
	}

	public ReleaseActionLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();

		fetchAttribute(context, attrs, defStyle);
	}

	public ReleaseActionLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ReleaseActionLayout(Context context) {
		super(context);
		initialize();
	}

	public void setOffsetX(int offset) {
		mOffsetX = offset;
		requestLayout();
	}

	public int getOffsetX() {
		return mOffsetX;
	}

	public void setOffsetY(int offset) {
		mOffsetY = offset;
		requestLayout();
	}

	public int getOffsetY() {
		return mOffsetY;
	}

	public void setOffset(int x, int y) {
		mOffsetX = x;
		mOffsetY = y;
		requestLayout();
	}

	public boolean canChildScrollHorizontally(int direction) {
		return getTargetView().canScrollHorizontally(direction);
	}

	public boolean canChildScrollVertically(int direction) {
		return getTargetView().canScrollVertically(direction);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		/*
		 * This method JUST determines whether we want to intercept the motion.
		 * If we return true, onMotionEvent will be called and we do the actual
		 * scrolling there.
		 */

		/*
		 * Shortcut the most recurring case: the user is in the dragging state
		 * and he is moving his finger. We want to intercept this motion.
		 */
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && mIsBeingDragged) {
			return true;
		}

		boolean locallyDrag = false;

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE: {
			/*
			 * mIsBeingDragged == false, otherwise the shortcut would have
			 * caught it. Check whether the user has moved far enough from his
			 * original down touch.
			 */

			/*
			 * Locally do absolute value. mLastMotionY is set to the y value of
			 * the down event.
			 */
			final int activePointerId = mActivePointerId;
			if (activePointerId == INVALID_POINTER) {
				// If we don't have a valid id, the touch down wasn't on
				// content.
				break;
			}

			final int pointerIndex = ev.findPointerIndex(activePointerId);
			if (pointerIndex == -1) {
				Log.e(TAG, "Invalid pointerId=" + activePointerId
						+ " in onInterceptTouchEvent");
				break;
			}

			boolean isBeingDraggedX = false;
			boolean isBeingDraggedY = false;
			final int x = (int) ev.getX(pointerIndex);
			final int deltaX = x - mLastMotionX;
			final int y = (int) ev.getY(pointerIndex);
			final int deltaY = y - mLastMotionY;
			if (mEnableTouchEventX && (Math.abs(deltaX) > mTouchSlop)) {
				if (canChildScrollHorizontally(-deltaX)) {
					getTargetView().getParent()
							.requestDisallowInterceptTouchEvent(true);
				} else {
					isBeingDraggedX = true;
				}
			}
			if (mEnableTouchEventY && (Math.abs(deltaY) > mTouchSlop)) {
				if (canChildScrollVertically(-deltaY)) {
					getTargetView().getParent()
							.requestDisallowInterceptTouchEvent(true);
				} else {
					isBeingDraggedY = true;
				}
			}
			if (isBeingDraggedX || isBeingDraggedY) {
				final ViewParent parent = getParent();
				if (parent != null) {
					parent.requestDisallowInterceptTouchEvent(true);
				}
				locallyDrag = true;
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			final int x = (int) ev.getX();
			final int y = (int) ev.getY();
			/*
			 * Remember location of down touch. ACTION_DOWN always refers to
			 * pointer index 0.
			 */
			mLastMotionX = x;
			mLastMotionY = y;
			mActivePointerId = ev.getPointerId(0);
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			// always does not intercept

			/* Release the drag */
			mIsBeingDragged = false;
			mActivePointerId = INVALID_POINTER;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			break;
		}
		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		return mIsBeingDragged || locallyDrag;
	};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			if (getChildCount() <= COUNT_AROUND_VIEWS) {
				return false;
			}

			// Remember where the motion event started
			mLastMotionX = (int) ev.getX();
			mLastMotionY = (int) ev.getY();
			mActivePointerId = ev.getPointerId(0);
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			final int activePointerIndex = ev
					.findPointerIndex(mActivePointerId);
			if (activePointerIndex == -1) {
				Log.e(TAG, "Invalid pointerId=" + mActivePointerId
						+ " in onTouchEvent");
				break;
			}

			final int x = (int) ev.getX(activePointerIndex);
			int deltaX = x - mLastMotionX;
			final int y = (int) ev.getY(activePointerIndex);
			int deltaY = y - mLastMotionY;
			if (!mIsBeingDragged) {
				boolean isBeingDraggedX = false;
				boolean isBeingDraggedY = false;
				if (mEnableTouchEventX && (Math.abs(deltaX) > mTouchSlop)) {
					if (canChildScrollHorizontally(-deltaX)) {
						getTargetView().getParent()
								.requestDisallowInterceptTouchEvent(true);
					} else {
						isBeingDraggedX = true;
					}
				}
				if (mEnableTouchEventY && (Math.abs(deltaY) > mTouchSlop)) {
					if (canChildScrollVertically(-deltaY)) {
						getTargetView().getParent()
								.requestDisallowInterceptTouchEvent(true);
					} else {
						isBeingDraggedY = true;
					}
				}
				if (isBeingDraggedX || isBeingDraggedY) {
					final ViewParent parent = getParent();
					if (parent != null) {
						parent.requestDisallowInterceptTouchEvent(true);
					}
					mIsBeingDragged = true;
				}
			}
			if (mIsBeingDragged) {
				// Scroll to follow the motion event
				move(deltaX, deltaY);
				mLastMotionX = x;
				mLastMotionY = y;
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			if (mIsBeingDragged) {
				onDragFinished();
				mActivePointerId = INVALID_POINTER;
				mIsBeingDragged = false;
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL:
			if (mIsBeingDragged && getChildCount() > COUNT_AROUND_VIEWS) {
				onDragFinished();
				mActivePointerId = INVALID_POINTER;
				mIsBeingDragged = false;
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN: {
			final int index = ev.getActionIndex();
			mLastMotionX = (int) ev.getX(index);
			mLastMotionY = (int) ev.getY(index);
			mActivePointerId = ev.getPointerId(index);
			break;
		}
		case MotionEvent.ACTION_POINTER_UP:
			onSecondaryPointerUp(ev);
			mLastMotionX = (int) ev.getX(ev.findPointerIndex(mActivePointerId));
			mLastMotionY = (int) ev.getY(ev.findPointerIndex(mActivePointerId));
			break;
		}
		return true;
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = (int) ev.getX(newPointerIndex);
			mLastMotionY = (int) ev.getY(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
		}
	}

	private static final int LEFT = 0;
	private static final int TOP = 1;
	private static final int RIGHT = 2;
	private static final int BOTTOM = 3;
	private static final int TARGET = 4;
	private static final int COUNT_AROUND_VIEWS = 4;

	private List<View> mAroundViews;

	private class HiddenView extends View {
		HiddenView(Context context) {
			super(context);
			setVisibility(View.GONE);
		}
	}

	private void initializeAroundView() {
		Context context = getContext();
		mAroundViews = new ArrayList<View>(COUNT_AROUND_VIEWS);
		for (int i = 0; i < COUNT_AROUND_VIEWS; ++i) {
			mAroundViews.add(new HiddenView(context));
			super.addView(mAroundViews.get(i));
		}
	}

	private void setAroundView(int location, View v) {
		if (mAroundViews.get(location) != v) {
			mAroundViews.set(location, v);
			removeViewAt(location);
			addView(v, location);
		}
	}

	public void setLeftView(View v) {
		setAroundView(LEFT, v);
		mEnableTouchEventX = true;
	}

	public View getLeftView() {
		return mAroundViews.get(LEFT);
	}

	public void setTopView(View v) {
		setAroundView(TOP, v);
		mEnableTouchEventY = true;
	}

	public View getTopView() {
		return mAroundViews.get(TOP);
	}

	public void setRightView(View v) {
		setAroundView(RIGHT, v);
		mEnableTouchEventX = true;
	}

	public View getRightView() {
		return mAroundViews.get(RIGHT);
	}

	public void setBottomView(View v) {
		setAroundView(BOTTOM, v);
		mEnableTouchEventY = true;
	}

	public View getBottomView() {
		return mAroundViews.get(BOTTOM);
	}

	public View getTargetView() {
		return getChildAt(TARGET);
	}

	public void setTargetView(FrameLayout v) {
		if (getTargetView() != v) {
			removeViewAt(TARGET);
			addView(v, TARGET);
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		boolean forceLeftGravity = false;

		final int parentLeft = getPaddingLeft();
		final int parentRight = right - left - getPaddingRight();

		final int parentTop = getPaddingTop();
		final int parentBottom = bottom - top - getPaddingBottom();

		final int childCount = getChildCount();
		for (int i = childCount - 1; i >= 0; --i) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final LayoutParams lp = (LayoutParams) child.getLayoutParams();

				final int width = child.getMeasuredWidth();
				final int height = child.getMeasuredHeight();

				int childLeft;
				int childTop;

				int gravity = lp.gravity;
				if (gravity == -1) {
					gravity = DEFAULT_CHILD_GRAVITY;
				}

				final int layoutDirection = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) ? getLayoutDirection()
						: 0;
				final int absoluteGravity = Gravity.getAbsoluteGravity(gravity,
						layoutDirection);
				final int verticalGravity = gravity
						& Gravity.VERTICAL_GRAVITY_MASK;

				switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
				case Gravity.CENTER_HORIZONTAL:
					childLeft = parentLeft + (parentRight - parentLeft - width)
							/ 2 + lp.leftMargin - lp.rightMargin;
					break;
				case Gravity.RIGHT:
					if (!forceLeftGravity) {
						childLeft = parentRight - width - lp.rightMargin;
						break;
					}
				case Gravity.LEFT:
				default:
					childLeft = parentLeft + lp.leftMargin;
				}

				switch (verticalGravity) {
				case Gravity.TOP:
					childTop = parentTop + lp.topMargin;
					break;
				case Gravity.CENTER_VERTICAL:
					childTop = parentTop + (parentBottom - parentTop - height)
							/ 2 + lp.topMargin - lp.bottomMargin;
					break;
				case Gravity.BOTTOM:
					childTop = parentBottom - height - lp.bottomMargin;
					break;
				default:
					childTop = parentTop + lp.topMargin;
				}

				/*
				 * process from TARGET view before around views.
				 */
				mChildRect.set(childLeft, childTop, childLeft + width, childTop
						+ height);
				mChildRect.offset(mOffsetX, mOffsetY);
				switch (i) {
				case TARGET:
					break;
				case LEFT:
					mChildRect.offset(-child.getMeasuredWidth(), 0);
					break;
				case TOP:
					mChildRect.offset(0, -child.getMeasuredHeight());
					break;
				case RIGHT:
					// mChildRect.offset(parentRight, 0);
					// why need offset by padding?
					mChildRect.offset(parentRight - getPaddingRight(), 0);
					break;
				case BOTTOM:
					// mChildRect.offset(0, parentBottom);
					// why need offset by padding?
					mChildRect.offset(0, parentBottom - getPaddingBottom());
					break;
				}
				child.layout(mChildRect.left, mChildRect.top, mChildRect.right,
						mChildRect.bottom);
			}
		}
	}

	private void move(int deltaX, int deltaY) {
		move(deltaX, deltaY, false);
	}

	private boolean valid(int location) {
		View v = mAroundViews.get(location);
		return v.getVisibility() != View.GONE && v.getWidth() > 0;
	}

	private int getAction(int location) {
		int action = 0;
		switch (location) {
		case LEFT:
			action = ACTION_LEFT;
			break;
		case TOP:
			action = ACTION_TOP;
			break;
		case RIGHT:
			action = ACTION_RIGHT;
			break;
		case BOTTOM:
			action = ACTION_BOTTOM;
			break;
		}
		return action;
	}

	private void onActionEnabled(int location) {
		mOnReleaseActionListener.onActionEnabled(this,
				mAroundViews.get(location), getAction(location));
	}

	private void onActionDisabled(int location) {
		mOnReleaseActionListener.onActionDisabled(this,
				mAroundViews.get(location), getAction(location));
	}

	private static final float SPEED = 0.5f;

	private void move(int deltaX, int deltaY, boolean animation) {
		moveWithoutSpeed((int) (deltaX * SPEED), (int) (deltaY * SPEED),
				animation);
	}

	private void moveWithoutSpeed(int deltaX, int deltaY, boolean animation) {
		// horizontal
		int newX = mOffsetX + deltaX;
		// for LEFT
		int maxX = valid(LEFT) ? getLeftView().getWidth() : 0;
		if (newX >= maxX) {
			newX = maxX;
			if (newX != 0) {
				if (!mActionFlag.has(ACTION_LEFT)) {
					mActionFlag.add(ACTION_LEFT);
					onActionEnabled(LEFT);
				}
			}
		} else if (mActionFlag.has(ACTION_LEFT)) {
			mActionFlag.remove(ACTION_LEFT);
			onActionDisabled(LEFT);
		}
		// for RIGHT
		int minX = valid(RIGHT) ? -getRightView().getWidth() : 0;
		if (newX <= minX) {
			newX = minX;
			if (newX != 0) {
				if (!mActionFlag.has(ACTION_RIGHT)) {
					mActionFlag.add(ACTION_RIGHT);
					onActionEnabled(RIGHT);
				}
			}
		} else if (mActionFlag.has(ACTION_RIGHT)) {
			mActionFlag.remove(ACTION_RIGHT);
			onActionDisabled(RIGHT);
		}
		// vertically
		int newY = mOffsetY + deltaY;
		// for TOP
		int maxY = valid(TOP) ? getTopView().getHeight() : 0;
		if (newY >= maxY) {
			newY = maxY;
			if (newY != 0) {
				if (!mActionFlag.has(ACTION_TOP)) {
					mActionFlag.add(ACTION_TOP);
					onActionEnabled(TOP);
				}
			}
		} else if (mActionFlag.has(ACTION_TOP)) {
			mActionFlag.remove(ACTION_TOP);
			onActionDisabled(TOP);
		}
		// for BOTTOM
		int minY = valid(BOTTOM) ? -getBottomView().getHeight() : 0;
		if (newY <= minY) {
			newY = minY;
			if (newY != 0) {
				if (!mActionFlag.has(ACTION_BOTTOM)) {
					mActionFlag.add(ACTION_BOTTOM);
					onActionEnabled(BOTTOM);
				}
			}
		} else if (mActionFlag.has(ACTION_BOTTOM)) {
			mActionFlag.remove(ACTION_BOTTOM);
			onActionDisabled(BOTTOM);
		}
		// move
		if (!animation) {
			setOffset(newX, newY);
		} else {
			Point start = new Point(mOffsetX, mOffsetY);
			Point end = new Point(newX, newY);
			ValueAnimator anim = ValueAnimator.ofObject(
					new TypeEvaluator<Point>() {
						@Override
						public Point evaluate(float fraction, Point startValue,
								Point endValue) {
							return new Point(Math.round(startValue.x
									+ (endValue.x - startValue.x) * fraction),
									Math.round(startValue.y
											+ (endValue.y - startValue.y)
											* fraction));
						}
					}, start, end);
			anim.setDuration(250);
			anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					Point offset = (Point) animation.getAnimatedValue();
					setOffset(offset.x, offset.y);
				}
			});
			anim.start();
		}
	}

	private void goHome(boolean animation) {
		moveWithoutSpeed(-mOffsetX, -mOffsetY, animation);
	}

	public void onDragFinished() {
		mOnReleaseActionListener.onReleaseAction(this);
		// reset
		if (mActionFlag.has(ACTION_LEFT)) {
			onActionDisabled(LEFT);
		}
		if (mActionFlag.has(ACTION_TOP)) {
			onActionDisabled(TOP);
		}
		if (mActionFlag.has(ACTION_RIGHT)) {
			onActionDisabled(RIGHT);
		}
		if (mActionFlag.has(ACTION_BOTTOM)) {
			onActionDisabled(BOTTOM);
		}
		mActionFlag.reset();
		goHome(true);
	}

	public Flag getActionFlag() {
		return mActionFlag;
	}

	public interface OnReleaseActionListener {
		/**
		 * callback when a moving event is finished.
		 * 
		 * @param v
		 */
		public void onReleaseAction(ReleaseActionLayout v);

		public void onActionEnabled(ReleaseActionLayout parent, View v,
				int action);

		public void onActionDisabled(ReleaseActionLayout parent, View v,
				int action);
	}

	public static final OnReleaseActionListener SimpleOnReleaseActionListener = new OnReleaseActionListener() {
		@Override
		public void onReleaseAction(ReleaseActionLayout v) {
		}

		@Override
		public void onActionEnabled(ReleaseActionLayout parent, View v,
				int action) {
		}

		@Override
		public void onActionDisabled(ReleaseActionLayout parent, View v,
				int action) {
		}
	};

	private OnReleaseActionListener mOnReleaseActionListener = SimpleOnReleaseActionListener;

	public void setOnReleaseActionListener(OnReleaseActionListener listener) {
		mOnReleaseActionListener = listener;
	}

	public OnReleaseActionListener getOnReleaseActionListener() {
		return mOnReleaseActionListener;
	}

	public static class Flag {

		public static final int NONE = 0;

		private int mFlags;

		public Flag() {
			mFlags = NONE;
		}

		public Flag(int flags) {
			mFlags = flags;
		}

		public Flag(Flag flag) {
			mFlags = flag.get();
		}

		public int get() {
			return mFlags;
		}

		public Flag reset() {
			reset(NONE);
			return this;
		}

		public Flag reset(int flags) {
			mFlags = flags;
			return this;
		}

		public Flag reset(Flag flag) {
			mFlags = flag.get();
			return this;
		}

		public Flag add(int flags) {
			mFlags |= flags;
			return this;
		}

		public Flag remove(int flags) {
			mFlags &= ~flags;
			return this;
		}

		public boolean has(int flags) {
			return (mFlags & flags) > 0;
		}
	}
}