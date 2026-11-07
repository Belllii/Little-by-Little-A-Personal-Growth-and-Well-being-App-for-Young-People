package com.example.littlebylittle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MoodProgressView extends View {

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint textPercentPaint;
    private Paint textLabelPaint;

    private RectF arcBounds;
    private int currentProgress = 0;
    private int maxProgress = 7; // Weekly goal: 7 check-ins

    public MoodProgressView(Context context) {
        super(context);
        init();
    }

    public MoodProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MoodProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(26f);
        backgroundPaint.setColor(Color.parseColor("#E5DCF7"));

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(26f);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setColor(Color.parseColor("#7655A6"));

        textPercentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPercentPaint.setColor(Color.parseColor("#49356D"));
        textPercentPaint.setTextSize(54f);
        textPercentPaint.setTextAlign(Paint.Align.CENTER);
        textPercentPaint.setFakeBoldText(true);

        textLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textLabelPaint.setColor(Color.parseColor("#777080"));
        textLabelPaint.setTextSize(26f);
        textLabelPaint.setTextAlign(Paint.Align.CENTER);

        arcBounds = new RectF();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float padding = 34f;
        arcBounds.set(padding, padding, w - padding, h - padding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;

        // Draw track ring
        canvas.drawOval(arcBounds, backgroundPaint);

        // Draw progress arc (start from -90 deg: top)
        float sweepAngle = maxProgress > 0
                ? (Math.min(currentProgress, maxProgress) / (float) maxProgress) * 360f
                : 0f;

        if (sweepAngle > 0) {
            canvas.drawArc(arcBounds, -90f, sweepAngle, false, progressPaint);
        }

        // Draw center text
        int percentage = maxProgress > 0 ? (int) ((currentProgress / (float) maxProgress) * 100) : 0;
        String percentText = percentage + "%";
        canvas.drawText(percentText, centerX, centerY + 8f, textPercentPaint);
        canvas.drawText("Weekly Goal", centerX, centerY + 46f, textLabelPaint);
    }

    public void setProgress(int current, int max) {
        this.currentProgress = current;
        this.maxProgress = max > 0 ? max : 1;
        invalidate();
    }
}
