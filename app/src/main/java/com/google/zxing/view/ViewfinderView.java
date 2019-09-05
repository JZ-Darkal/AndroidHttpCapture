/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.google.zxing.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

import cn.darkal.networkdiagnosis.R;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder rectangle and partial
 * transparency outside it, as well as the laser scanner animation and result points.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 30L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;
    /**
     * 四个绿色边角对应的宽度
     */
    private static final int CORNER_WIDTH = 3;
    /**
     * 扫描框中的中间线的宽度
     */
    private static final float MIDDLE_LINE_WIDTH = 3;
    /**
     * 扫描框中的中间线的与扫描框左右的间隙
     */
    private static final float MIDDLE_LINE_PADDING = 5;
    /**
     * 中间那条线每次刷新移动的距离
     */
    private static final float SPEEN_DISTANCE = 4;
    /**
     * 字体大小
     */
    private static final int TEXT_SIZE = 13;
    /**
     * 字体距离扫描框下面的距离
     */
    private static final int TEXT_PADDING_TOP = 24;
    /**
     * 手机的屏幕密度
     */
    private static float density;
    private final int maskColor;
    private final int resultColor;
    private final int resultPointColor;
    boolean isFirst;
    /**
     * 四个绿色边角对应的长度
     */
    private int ScreenRate;
    /**
     * 画笔对象的引用
     */
    private Paint paint;
    /**
     * 中间滑动线的最顶端位置
     */
    private float slideTop;
    /**
     * 中间滑动线的最底端位置
     */
    private int slideBottom;
    private CameraManager cameraManager;
    private Bitmap resultBitmap;
    private int scannerAlpha;
    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        density = context.getResources().getDisplayMetrics().density;
        //将像素转换成dp
        ScreenRate = (int) (20 * density);

        // Initialize these once for performance rather than calling them every time in onDraw().
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        scannerAlpha = 0;
        possibleResultPoints = new ArrayList<ResultPoint>(5);
        lastPossibleResultPoints = null;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }

        Rect frame = cameraManager.getFramingRect();
        Rect previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }

        //初始化中间线滑动的最上边和最下边
        if (!isFirst) {
            isFirst = true;
            slideTop = frame.top;
            slideBottom = frame.bottom;
        }

        //获取屏幕的宽和高
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        //画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(resultBitmap, null, frame, paint);
        } else {

            //画扫描框边上的角，总共8个部分
            paint.setAlpha(0xFF);
            paint.setColor(Color.GREEN);
            canvas.drawRect(frame.left - 1, frame.top - 1, frame.left - 1 + ScreenRate, frame.top - 1 + CORNER_WIDTH, paint);
            canvas.drawRect(frame.left - 1, frame.top - 1, frame.left - 1 + CORNER_WIDTH, frame.top - 1 + ScreenRate, paint);
            canvas.drawRect(frame.right + 2 - ScreenRate, frame.top - 1, frame.right + 2, frame.top - 1 + CORNER_WIDTH, paint);
            canvas.drawRect(frame.right + 2 - CORNER_WIDTH, frame.top - 1, frame.right + 2, frame.top - 1 + ScreenRate, paint);
            canvas.drawRect(frame.left - 1, frame.bottom + 2 - CORNER_WIDTH, frame.left - 1 + ScreenRate, frame.bottom + 2, paint);
            canvas.drawRect(frame.left - 1, frame.bottom + 2 - ScreenRate, frame.left - 1 + CORNER_WIDTH, frame.bottom + 2, paint);
            canvas.drawRect(frame.right + 2 - ScreenRate, frame.bottom + 2 - CORNER_WIDTH, frame.right + 2, frame.bottom + 2, paint);
            canvas.drawRect(frame.right + 2 - CORNER_WIDTH, frame.bottom + 2 - ScreenRate, frame.right + 2, frame.bottom + 2, paint);

            paint.setColor(Color.WHITE);
            canvas.drawLine(frame.left, frame.bottom, frame.left, frame.top, paint);
            canvas.drawLine(frame.left, frame.top, frame.right, frame.top, paint);
            canvas.drawLine(frame.right, frame.top, frame.right, frame.bottom, paint);
            canvas.drawLine(frame.right, frame.bottom, frame.left, frame.bottom, paint);

            //绘制中间的线,每次刷新界面，中间的线往下移动SPEEN_DISTANCE
            slideTop += SPEEN_DISTANCE;
            if (slideTop >= frame.bottom) {
                slideTop = frame.top;
            }
            Shader shaderNew = new LinearGradient(frame.left + MIDDLE_LINE_PADDING, 0, frame.right - MIDDLE_LINE_PADDING, 0,
                    new int[]{0x00FFFFFF, 0xFF00FF00, 0xFF00FF00, 0x00FFFFFF}, null, Shader.TileMode.MIRROR);
            Shader shaderOld = paint.getShader();
            paint.setShader(shaderNew);
            canvas.drawRect(frame.left + MIDDLE_LINE_PADDING, slideTop - MIDDLE_LINE_WIDTH / 2,
                    frame.right - MIDDLE_LINE_PADDING, slideTop + MIDDLE_LINE_WIDTH / 2, paint);

            paint.setShader(shaderOld);

            //画扫描框下面的字
            paint.setColor(Color.WHITE);
            paint.setTextSize(TEXT_SIZE * density);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create("system", Typeface.NORMAL));
            //paint.setTypeface(Typeface.SANS_SERIF);
            canvas.drawText(getResources().getString(R.string.scan_text), frame.centerX(), (float) (frame.bottom + (float) TEXT_PADDING_TOP * density), paint);

            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = possibleResultPoints;
            List<ResultPoint> currentLast = lastPossibleResultPoints;
            int frameLeft = frame.left;
            int frameTop = frame.top;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new ArrayList<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(CURRENT_POINT_OPACITY);
                paint.setColor(resultPointColor);
                synchronized (currentPossible) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                POINT_SIZE, paint);
                    }
                }
            }
            if (currentLast != null) {
                paint.setAlpha(CURRENT_POINT_OPACITY / 2);
                paint.setColor(resultPointColor);
                synchronized (currentLast) {
                    float radius = POINT_SIZE / 2.0f;
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frameLeft + (int) (point.getX() * scaleX),
                                frameTop + (int) (point.getY() * scaleY),
                                radius, paint);
                    }
                }
            }

            //只刷新扫描框的内容，其他地方不刷新
            postInvalidateDelayed(ANIMATION_DELAY,
                    frame.left - POINT_SIZE,
                    frame.top - POINT_SIZE,
                    frame.right + POINT_SIZE,
                    frame.bottom + POINT_SIZE);
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }

}
