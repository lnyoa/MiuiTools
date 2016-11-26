package bs.lansys.miuitools.helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import bs.lansys.miuitools.utils.ColorBarUtils;

public class ImageToastView extends RelativeLayout {
	private static int Animation_Toast_resID;
	private static int message_resID;
	private ImageView iv;
	private TextView tv;
	private int mAnimation_Toast;

	public ImageToastView(Context context, TextView txtView) {
		super(context);
		// TODO Auto-generated constructor stub
		tv = txtView;
		if (Animation_Toast_resID == 0) {
			Animation_Toast_resID = context.getResources().getIdentifier("Animation_Toast", "style", "android");
		}
		setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Context mContext = getContext();
		int leftmargin = ColorBarUtils.Dip2Pixl(mContext, 5.0F);
		setPadding(leftmargin, leftmargin, leftmargin, leftmargin);
		RelativeLayout layout = new RelativeLayout(mContext);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(CENTER_IN_PARENT);
		addView(layout, params);
		if (message_resID == 0) {
			message_resID = mContext.getResources().getIdentifier("message", "id", "android");
		}
		if (tv == null) {
			tv = new TextView(mContext);
			tv.setId(message_resID);
		} else {
			tv.setPadding(0, 0, 0, 0);
			tv.setBackground(null);
			tv.setShadowLayer(0.0F, 0.0F, 0.0F, 0);
		}

		tv.setTextSize(14);
		LayoutParams tvParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tvParams.addRule(CENTER_VERTICAL);
		int iconsize = ColorBarUtils.Dip2Pixl(mContext, 32);
		LayoutParams ivParams = new RelativeLayout.LayoutParams(iconsize, iconsize);
		ivParams.addRule(CENTER_VERTICAL);
		iv = new ImageView(mContext);
		int iv_resID = generateViewId();
		iv.setId(iv_resID);
		tvParams.addRule(RIGHT_OF, iv_resID);
		layout.addView(iv, ivParams);

		if (tv.getParent() != null) {
			((ViewGroup) tv.getParent()).removeView(tv);
		}

		layout.addView(tv, tvParams);
		tvParams.leftMargin = leftmargin;
		mAnimation_Toast = Animation_Toast_resID;
	}

	

	public final int getToastAnimation() {
		return mAnimation_Toast;
	}

	public final void setToastAnimation(int animation) {
		mAnimation_Toast = animation;
	}

	public final void setColor(int txtcolor, int bkcolor, float alpha_factor) {
		tv.setTextColor(txtcolor);
		int backcolor = bkcolor;
		if (alpha_factor != 1.0F) {
			backcolor = 0xFFFFFF & bkcolor | (int) (255.0F * alpha_factor) << 24;
		}
		setBackgroundColor(backcolor);
	}

	public final void initToast(Drawable icon, CharSequence txt, int txtColor, int backColor, float alpha_factor) {
		if (iv != null) {
			iv.setImageDrawable(icon);
		}
		if (txt != null) {
			tv.setText(txt);
		}
		setColor(txtColor, backColor, alpha_factor);
	}
}
