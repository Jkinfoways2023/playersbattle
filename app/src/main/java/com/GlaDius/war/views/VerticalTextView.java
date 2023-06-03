package com.GlaDius.war.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class VerticalTextView extends TextView {

    /* renamed from: a */
    final boolean f9348a;

    public VerticalTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int gravity = getGravity();
        if (!Gravity.isVertical(gravity) || (gravity & 112) != 80) {
            this.f9348a = true;
            return;
        }
        setGravity((gravity & 7) | 48);
        this.f9348a = false;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();
        canvas.save();
        if (this.f9348a) {
            canvas.translate(0.0f, (float) getHeight());
            canvas.rotate(-90.0f);
        } else {
            canvas.translate((float) getWidth(), 0.0f);
            canvas.rotate(90.0f);
        }
        canvas.translate((float) getCompoundPaddingLeft(), (float) getExtendedPaddingTop());
        getLayout().draw(canvas);
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i2, i);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }
}
