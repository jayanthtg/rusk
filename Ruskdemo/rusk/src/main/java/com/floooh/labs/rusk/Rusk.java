package com.floooh.labs.rusk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author jayanthtg
 * Rusk.java
 * @purpose To inflate a Tooltip on a anchor  view in a specific position on window with or without
 * Pointed arrow and action view.
 */

public class Rusk {

    private View anchor;
    private int background;
    private RuskLayout ruskLayout;
    private ViewGroup root;
    private FrameLayout overlay;
    private Context context;
    private Type type;
    private Handler mHandler = new Handler();

    //duration props
    private int length;
    public static final int LENGTH_SHORT = 3000;
    public static final int LENGTH_LONG = 6000;
    private static final int LENGTH_INDETERMINATE = -1;

    //offset
    private int OFFSET_X = 50;
    private int OFFSET_Y = 50;


    //other props
    private boolean isPointed;
    private Direction direction;

    //listeners


    private Rusk(Context context, Type type) {
        this.context = context;
        this.type = type;
        init();
    }

    //initialise rusk
    private void init() {
        root = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        overlay = new FrameLayout(context);
        length = LENGTH_SHORT;
        background = Color.BLACK;
        ruskLayout = getLayout();
        overlay.addView(ruskLayout);
        ruskLayout.setVisibility(View.INVISIBLE);
        ruskLayout.setAlpha(0f);
        direction = Direction.TOP;
    }

    private ViewTreeObserver.OnGlobalLayoutListener observer = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (ruskLayout.getWidth() == 0) return;
            PointF location = focus();
            if (isPointed) {
                ruskLayout.setBackground(new PointedDrawable(background, direction, pointPosition(), (int) location.x));
            } else {
                ruskLayout.setBackgroundColor(background);
            }
            ruskLayout.animate().translationX(location.x).translationY(location.y).withLayer().withEndAction(new Runnable() {
                @Override
                public void run() {
                    ruskLayout.setVisibility(View.VISIBLE);
                    ruskLayout.animate().alphaBy(1f).setDuration(300).start();
                }
            });
        }
    };

    private int pointPosition() {
        return locationOnScreen(anchor)[0] + (anchor.getWidth() / 2);
    }

    //adds the rusk layout to root layout.
    public void show() {
        root.getViewTreeObserver().addOnGlobalLayoutListener(observer);
        if (overlay.getParent() == null) {
            root.addView(overlay);
            if (length != LENGTH_INDETERMINATE)
                mHandler.postDelayed(autoDismissRunnable, length);
        }
    }

    private Runnable autoDismissRunnable = new Runnable() {
        @Override
        public void run() {
            dismiss();
        }
    };

    //creates new rusk object
    public static Rusk bake(Context from, Type withType) {
        return new Rusk(from, withType);
    }

    //gets the appropriate layout from type
    private RuskLayout getLayout() {
        switch (type) {
            case DEFAULT:
                return new RuskLayout(context);
            case CANCELLABLE:
                return new RuskCancellableLayout(context);
            default:
                throw new IllegalArgumentException("Illegal view type for rusk");
        }
    }

    //calculate the positions of anchor and positions rusk layout
    private PointF focus() {
        if (anchor == null) {
            return new PointF((root.getWidth() >> 1) - (ruskLayout.getWidth() >> 1), root.getHeight() - dp2Pixel(85));
        }
        int[] location = locationOnScreen(anchor);
        location[0] = location[0] + (anchor.getWidth() / 2) - (ruskLayout.getWidth() / 2);

//        return clampPoint(new Point(location[0], location[1] + OFFSET_Y), Direction.TOP);
        return clamp(new Point(location[0], location[1] + OFFSET_Y));
    }

    @Deprecated // not using this.
    private PointF clampPoint(Point p, Direction direction) {
        Rect r = new Rect();
        root.getLocalVisibleRect(r);

        if (p.y + anchor.getHeight() + ruskLayout.getHeight() > r.top + r.height()) {
            p.y -= (anchor.getHeight() - ruskLayout.getHeight());
        } else {
            p.y += anchor.getHeight() + ruskLayout.getHeight();
        }
        if ((p.x + ruskLayout.getWidth()) > r.left + r.width()) {
            p.x = p.x - ruskLayout.getWidth();
        }
        if ((p.y + ruskLayout.getHeight()) > r.top + r.height()) {
            p.y = p.y - ruskLayout.getHeight() - OFFSET_Y;
        }
        return new PointF(p);
    }

    //decides direction to clamp.
    private PointF clamp(Point point) {
        switch (direction) {
            case BOTTOM:
                return clampBottom(point);
            default:
                return clampTop(point);
        }

    }

    //clamps the rusk top of the anchor
    private PointF clampTop(Point p) {
        Rect r = new Rect(), q = new Rect();
        root.getLocalVisibleRect(r);
        p.y = Math.max(0, p.y - ruskLayout.getHeight() - OFFSET_Y);
        if (p.x + ruskLayout.getWidth() > r.left + r.width()) {
            int d = (p.x + ruskLayout.getWidth()) - (r.left + r.width());
            p.x -= d;
        }

        return new PointF(p);
    }

    //clamps the rusk bottom of the anchor
    private PointF clampBottom(Point p) {
        Rect r = new Rect(), q = new Rect();
        root.getLocalVisibleRect(r);
        p.y = Math.min(r.height() - ruskLayout.getHeight(), p.y + anchor.getHeight() - OFFSET_Y);
        if (p.x + ruskLayout.getWidth() > r.left + r.width()) {
            int d = (p.x + ruskLayout.getWidth()) - (r.left + r.width());
            p.x -= d;
        }

        return new PointF(p);
    }

    //removes rusk from root
    public synchronized void dismiss() {
        if (root != null) {
            ruskLayout.animate().scaleX(0).setDuration(300).withEndAction(new Runnable() {
                @Override
                public void run() {
                    ruskLayout.animate().scaleX(1);
                    root.removeView(overlay);
                }
            });
        }
    }

    //setters and getters

    public Rusk setPointDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public Rusk setPointed(boolean isPointed) {
        this.isPointed = isPointed;
        return this;
    }

    public boolean isPointed() {
        return this.isPointed;
    }

    public Rusk setBackground(int color) {
        this.background = color;
        this.ruskLayout.setBackgroundColor(background);
        return this;
    }

    public int getBackground() {
        return this.background;
    }

    public Rusk setDuration(int length) {
        this.length = length;
        return this;
    }

    public int getDuration() {
        return this.length;
    }

    public Rusk setType(Type type) {
        this.type = type;
        return this;
    }

    public void setMessage(int resource) {
        ruskLayout.mMessageView.setText(resource);
    }

    public Rusk setMessage(String text) {
        ruskLayout.mMessageView.setText(text);
        return this;
    }

    public Rusk setActionImage(int imageResource) {
        if (ruskLayout instanceof RuskCancellableLayout) {
            ((RuskCancellableLayout) ruskLayout).mActionView.setImageResource(imageResource);
        }
        return this;
    }

    public TextView getMessageView() {
        return this.ruskLayout.mMessageView;
    }

    public ImageView getActionView() {
        if (ruskLayout instanceof RuskCancellableLayout) {
            return ((RuskCancellableLayout) ruskLayout).mActionView;
        }
        return null;
    }

    //sets anchor for the rusk to lay on.
    public Rusk setAnchor(int viewId) {
        anchor = root.findViewById(viewId);
        return this;
    }

    //get rid of offset from statusbar and other system Uis.
    private int[] locationOnScreen(View view) {
        DisplayMetrics matrix = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(matrix);
        int topOffset = matrix.heightPixels - root.getMeasuredHeight();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new int[]{location[0], location[1] - topOffset};

    }

    //converts dp value in to pixels.
    private int dp2Pixel(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private boolean isBound(RectF r, RectF container) {
        return container.contains(r);
    }


    //inner classes

    /**
     * Default layout with only text message.
     */
    private class RuskLayout extends LinearLayout {
        protected TextView mMessageView;

        public RuskLayout(Context context) {
            super(context);
            setPadding(30, 30, 30, 30);
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            init();
        }

        private void init() {
            mMessageView = new TextView(getContext());
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            mMessageView.setLayoutParams(params);
            mMessageView.setPadding(12, 12, 12, 12);
            mMessageView.setGravity(Gravity.CENTER);
            mMessageView.setTextColor(Color.WHITE);
            this.addView(mMessageView);
        }
    }

    /**
     * Extension layout to Default layout has extra imageView for action.
     */
    private class RuskCancellableLayout extends RuskLayout {
        protected ImageView mActionView;

        public RuskCancellableLayout(Context context) {
            super(context);
            init();
        }

        private void init() {
            mActionView = new ImageView(getContext());
            mActionView.setLayoutParams(new LayoutParams(dp2Pixel(30), dp2Pixel(30)));
            mActionView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mActionView.setPadding(12, 12, 12, 12);
            addView(mActionView);
        }

    }

    //Layout types
    public enum Type {
        DEFAULT, CANCELLABLE
    }

    //Directions
    public enum Direction {
        TOP, RIGHT, LEFT, BOTTOM
    }

}
