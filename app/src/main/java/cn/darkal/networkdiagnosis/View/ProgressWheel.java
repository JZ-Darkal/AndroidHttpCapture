package cn.darkal.networkdiagnosis.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import cn.darkal.networkdiagnosis.R;

/**
 * An indicator of progress, similar to Android's ProgressBar.
 * Can be used in 'spin mode' or 'increment mode'
 * @author Todd Davies
 *
 * Licensed under the Creative Commons Attribution 3.0 license see:
 * http://creativecommons.org/licenses/by/3.0/
 */
public class ProgressWheel extends View {
	
	//Sizes (with defaults)
	private int fullRadius = 100;
	private int circleRadius = 80;
	private int barLength = 60;
	private int barWidth = 20;      //内部圆弧的宽度
	private int rimWidth = 20;
	private int textSize = 20;
    private int barDegree = 60;
	private float arcR = barWidth/2;  //内部圆弧的半径
	
	//Padding (with defaults)
	private int paddingTop = 5;
	private int paddingBottom = 5;
	private int paddingLeft = 5;
	private int paddingRight = 5;
	
	//Colors (with defaults)
	private int barColor = 0xAA000000;
	private int circleColor = 0x0000ffff;
	private int rimColor = 0xAADDDDDD;
    private int spinRimColor = 0xAADDDDDD;
	private int textColor = 0xFF000000;
	private int spinCircleColor = 0x00000000;

	//Paints
	private Paint barPaint = new Paint();    //圆画笔
	private Paint circlePaint = new Paint();   //内部填充圆画笔
	private Paint barCirclePaint = new Paint();  //圆弧上两端点圆的画笔
	private Paint rimPaint = new Paint();      //底部圈画笔
	private Paint textPaint = new Paint();
    private Paint spinRimPaint = new Paint();   //旋转时，底部圈画笔，主要用于此时改变底部圈颜色
	private Paint spinCirclePaint = new Paint();//旋转时，圆的画笔

	//Rectangles
	@SuppressWarnings("unused")
	private RectF rectBounds = new RectF();
	private RectF circleBounds = new RectF();

	private int startDegree = -90;   //圆弧的起始位置， -90 顶上
	private float startArcX = 0;
	private float startArcY = 0;

	//Animation
	//The amount of pixels to move the bar by on each draw
	private int spinSpeed = 2;
	//The number of milliseconds to wait inbetween each draw
	private int delayMillis = 0;
	private Handler spinHandler = new Handler() {
		/**
		 * This is the code that will increment the progress variable
		 * and so spin the wheel
		 */
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
				invalidate();
				if (isSpinning) {
					progress += spinSpeed;
					if (progress > 360) {
						progress = 0;
					}
					spinHandler.sendEmptyMessageDelayed(0, delayMillis);
				}
				break;
				case 1:
					spinHandler.removeMessages(0);
					isSpinning = false;
					invalidate();
				break;
			}
		}
	};
	int progress = 0;
	boolean isSpinning = false;

	//Other
	private String text = "";
	private String[] splitText = {};
	
	/**
	 * The constructor for the ProgressWheel
	 * @param context
	 * @param attrs
	 */
	public ProgressWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

		parseAttributes(context.obtainStyledAttributes(attrs, 
				R.styleable.ProgressWheel));
	}
	
	//----------------------------------
	//Setting up stuff
	//----------------------------------
	
	/**
	 * Now we know the dimensions of the view, setup the bounds and paints
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();

		setupBounds();
		setupPaints();
		invalidate();

	}

	@Override
	protected void onDetachedFromWindow() {
		invalidate();
		super.onDetachedFromWindow();
	}

	/**
	 * Set the properties of the paints we're using to 
	 * draw the progress wheel
	 */
	private void setupPaints() {
		barPaint.setColor(barColor);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Style.STROKE);
        barPaint.setStrokeWidth(barWidth);
        
        rimPaint.setColor(rimColor);
        rimPaint.setAntiAlias(true);
        rimPaint.setStyle(Style.STROKE);
        rimPaint.setStrokeWidth(rimWidth);

        spinRimPaint.setColor(spinRimColor);
        spinRimPaint.setAntiAlias(true);
        spinRimPaint.setStyle(Style.STROKE);
        spinRimPaint.setStrokeWidth(rimWidth);

		spinCirclePaint.setColor(spinCircleColor);
		spinCirclePaint.setAntiAlias(true);
		spinCirclePaint.setStyle(Style.FILL);
		spinCirclePaint.setShadowLayer(1, 2, 2, 0x40000000);
        
        circlePaint.setColor(circleColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Style.FILL);

		barCirclePaint.setColor(barColor);
		barCirclePaint.setAntiAlias(true);
		barCirclePaint.setStyle(Style.FILL);
		barCirclePaint.setStrokeWidth(barWidth);
        
        textPaint.setColor(textColor);
        textPaint.setStyle(Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
	}

	/**
	 * Set the bounds of the component
	 */
	private void setupBounds() {
		paddingTop = this.getPaddingTop();
	    paddingBottom = this.getPaddingBottom();
	    paddingLeft = this.getPaddingLeft();
	    paddingRight = this.getPaddingRight();
		
		rectBounds = new RectF(paddingLeft,
				paddingTop,
                this.getLayoutParams().width - paddingRight,
                this.getLayoutParams().height - paddingBottom);
		
		circleBounds = new RectF(paddingLeft + barWidth,
				paddingTop + barWidth,
                this.getLayoutParams().width - paddingRight - barWidth,
                this.getLayoutParams().height - paddingBottom - barWidth);
		
		fullRadius = (this.getLayoutParams().width - paddingRight - barWidth)/2;
	    circleRadius = (fullRadius - barWidth) + 1;   //内部圆的半径

		arcR = barWidth/2;      //圆弧的半径
		startArcX = (float) (-circleBounds.width()/2* Math.sin( 2* Math.PI/360*(startDegree+270))+this.getLayoutParams().width/2);   //计算时角度要换成弧度
		startArcY = (float) (circleBounds.height()/2* Math.cos(2 * Math.PI / 360 * (startDegree+270)) + this.getLayoutParams().height/2);
	}

	/**
	 * Parse the attributes passed to the view from the XML
	 * @param a the attributes to parse
	 */
	private void parseAttributes(TypedArray a) {
		barWidth = (int) a.getDimension(R.styleable.ProgressWheel_barWidth_progress,
			barWidth);
		
		rimWidth = (int) a.getDimension(R.styleable.ProgressWheel_rimWidth_progress,
			rimWidth);
		
		spinSpeed = (int) a.getInteger(R.styleable.ProgressWheel_spinSpeed_progress,
			spinSpeed);
		
		delayMillis = (int) a.getInteger(R.styleable.ProgressWheel_delayMillis_progress,
			delayMillis);
		if(delayMillis<0) {
			delayMillis = 0;
		}
	    
	    barColor = a.getColor(R.styleable.ProgressWheel_barColor_progress, barColor);
	    
	    barLength = (int) a.getDimension(R.styleable.ProgressWheel_barLength_progress,
	    	barLength);
	    
	    textSize = (int) a.getDimension(R.styleable.ProgressWheel_textSize_progress,
	    	textSize);
	    
	    textColor = (int) a.getColor(R.styleable.ProgressWheel_textColor_progress,
				textColor);
	    
	    setText(a.getString(R.styleable.ProgressWheel_text_progress));
	    
	    rimColor = (int) a.getColor(R.styleable.ProgressWheel_rimColor_progress,
	    	rimColor);

        spinRimColor = (int) a.getColor(R.styleable.ProgressWheel_spinRimColor_progress,
                spinRimColor);
	    
	    circleColor = (int) a.getColor(R.styleable.ProgressWheel_circleColor_progress, circleColor);
		spinCircleColor = (int) a.getColor(R.styleable.ProgressWheel_spinCircleColor_progress, spinCircleColor);

        barDegree = (int) a.getInteger(R.styleable.ProgressWheel_barDegree_progress,-1);
	}

	//----------------------------------
	//Animation stuff
	//----------------------------------
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		//Draw the rim
        if(isSpinning()){
            canvas.drawArc(circleBounds, 360, 360, false, spinRimPaint);
        }else {
            canvas.drawArc(circleBounds, 360, 360, false, rimPaint);
        }

		//Draw the bar
		if(isSpinning) {
            if(barDegree != -1) {    //按度数
                canvas.drawArc(circleBounds, progress - 90, barDegree, false,
                        barPaint);
					//结束度数
					double t = (progress - 90+barDegree + 270);
					//起始点坐标
					float startX = (float) (-circleBounds.width()/2* Math.sin( 2* Math.PI/360*(progress - 90+270))+this.getLayoutParams().width/2);   //计算时角度要换成弧度
					float startY = (float) (circleBounds.height()/2* Math.cos(2 * Math.PI / 360 * (progress - 90 + 270)) + this.getLayoutParams().height/2);
					//结束点坐标
					float endX = (float) (-circleBounds.width() / 2 * Math.sin(2 * Math.PI / 360 * t) + this.getLayoutParams().width / 2);
					float endY = (float) (circleBounds.height() / 2 * Math.cos(2 * Math.PI / 360 * t) + this.getLayoutParams().height / 2);
					//计算两点间距离
					float tmpR = (float) Math.sqrt((endX-startX)*(endX-startX)+(endY-startY)*(endY-startY));
//					canvas.drawCircle(startX, startY, tmpR, barPaint);    //画圆弧起始圆
//					canvas.drawCircle(x, y, tmpR, barPaint);    //画圆弧起始圆
					//确定圆心点
					double tmp = (progress - 90 + barDegree)+(360-barDegree)/2+270;
					float x2 = (float) (-circleBounds.width() / 2 * Math.sin(2 * Math.PI / 360 * tmp) + this.getLayoutParams().width / 2);
					float y2 = (float) (circleBounds.height() / 2 * Math.cos(2 * Math.PI / 360 * tmp) + this.getLayoutParams().height / 2);
					canvas.drawCircle(x2, y2, tmpR, spinCirclePaint);   //画圆
            }else{     //按长度
                canvas.drawArc(circleBounds, progress - 90, barLength, false,
                        barPaint);
				//结束度数
				double t = (progress - 90+barLength + 270);
				//起始点坐标
				float startX = (float) (-circleBounds.width()/2* Math.sin( 2* Math.PI/360*(progress - 90+270))+this.getLayoutParams().width/2);   //计算时角度要换成弧度
				float startY = (float) (circleBounds.height()/2* Math.cos(2 * Math.PI / 360 * (progress - 90 + 270)) + this.getLayoutParams().height/2);
				//结束点坐标
				float endX = (float) (-circleBounds.width() / 2 * Math.sin(2 * Math.PI / 360 * t) + this.getLayoutParams().width / 2);
				float endY = (float) (circleBounds.height() / 2 * Math.cos(2 * Math.PI / 360 * t) + this.getLayoutParams().height / 2);
				//计算两点间距离
				float tmpR = (float) Math.sqrt((endX-startX)*(endX-startX)+(endY-startY)*(endY-startY));
//				canvas.drawCircle(startX, startY, tmpR, barPaint);    //画圆弧起始圆
//				canvas.drawCircle(x, y, tmpR, barPaint);    //画圆弧起始圆
				//确定圆心点
				double tmp = (progress - 90 + barLength)+(360-barLength)/2+270;
				float x2 = (float) (-circleBounds.width() / 2 * Math.sin(2 * Math.PI / 360 * tmp) + this.getLayoutParams().width / 2);
				float y2 = (float) (circleBounds.height() / 2 * Math.cos(2 * Math.PI / 360 * tmp) + this.getLayoutParams().height / 2);
				canvas.drawCircle(x2, y2, tmpR, spinCirclePaint);   //画圆
			}
		} else {
			canvas.drawArc(circleBounds, startDegree, progress, false, barPaint);    // -90 从顶上开始
			double t = progress+startDegree+270;
			if(progress != 0) {
				canvas.drawCircle(startArcX, startArcY, arcR, barCirclePaint);    //画圆弧起始圆

				float x = (float) (-circleBounds.width() / 2 * Math.sin(2 * Math.PI / 360 * t) + this.getLayoutParams().width / 2);
				float y = (float) (circleBounds.height() / 2 * Math.cos(2 * Math.PI / 360 * t) + this.getLayoutParams().height / 2);
				canvas.drawCircle(x, y, arcR, barCirclePaint);   //画圆弧结束圆
			}
		}
		//Draw the inner circle
		canvas.drawCircle((circleBounds.width()/2) + rimWidth + paddingLeft,
				(circleBounds.height()/2) + rimWidth + paddingTop,
				circleRadius,
				circlePaint);
		//Draw the text (attempts to center it horizontally and vertically)
		int offsetNum = 0;
		for(String s : splitText) {
			float offset = textPaint.measureText(s) / 2;
			canvas.drawText(s, this.getWidth() / 2 - offset, 
				this.getHeight() / 2 + (textSize*(offsetNum)) 
				- ((splitText.length-1)*(textSize/2)), textPaint);
			offsetNum++;
		}
	}

	/**
	 * Reset the count (in increment mode)
	 */
	public void resetCount() {
		progress = 0;
		setText("0%");
		invalidate();
	}

	/**
	 * Turn off spin mode
	 */
	public void stopSpinning() {
		spinHandler.sendEmptyMessageDelayed(1,200);
	}
	
	
	/**
	 * Puts the view on spin mode
	 */
	public void spin() {
		isSpinning = true;
		spinHandler.sendEmptyMessage(0);
	}

    public boolean isSpinning(){
        return isSpinning;
    }

	/**
	 * Increment the progress by 1 (of 360)
	 */
	public void incrementProgress() {
		isSpinning = false;
		progress++;
		setText(Math.round(((float)progress/360)*100) + "%");
		spinHandler.sendEmptyMessage(0);
	}

	/**
	 * Set the progress to a specific value
	 */
	public void setProgress(int i) {
	    isSpinning = false;
	    progress=i;
	    spinHandler.sendEmptyMessage(0);
	}
	
	//----------------------------------
	//Getters + setters
	//----------------------------------
	
	/**
	 * Set the text in the progress bar
	 * Doesn't invalidate the view
	 * @param text the text to show ('\n' constitutes a new line)
	 */
	public void setText(String text) {
		this.text = text;
		splitText = this.text.split("\n");
	}
	
	public int getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(int circleRadius) {
		this.circleRadius = circleRadius;
	}

	public int getBarLength() {
		return barLength;
	}

	public void setBarLength(int barLength) {
		this.barLength = barLength;
	}

	public int getBarWidth() {
		return barWidth;
	}

	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}

	public int getBarColor() {
		return barColor;
	}

	public void setBarColor(int barColor) {
		this.barColor = barColor;
	}

	public int getCircleColor() {
		return circleColor;
	}

	public void setCircleColor(int circleColor) {
		this.circleColor = circleColor;
	}

	public int getRimColor() {
		return rimColor;
	}

	public void setRimColor(int rimColor) {
		this.rimColor = rimColor;
	}

    public int getSpinRimColor() {
        return spinRimColor;
    }

    public void setSpinRimColor(int spinRimColor) {
        this.spinRimColor = spinRimColor;
    }

	public Shader getRimShader() {
		return rimPaint.getShader();
	}

	public void setRimShader(Shader shader) {
		this.rimPaint.setShader(shader);
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
	
	public int getSpinSpeed() {
		return spinSpeed;
	}

	public void setSpinSpeed(int spinSpeed) {
		this.spinSpeed = spinSpeed;
	}
	
	public int getRimWidth() {
		return rimWidth;
	}

	public void setRimWidth(int rimWidth) {
		this.rimWidth = rimWidth;
	}
	
	public int getDelayMillis() {
		return delayMillis;
	}

	public void setDelayMillis(int delayMillis) {
		this.delayMillis = delayMillis;
	}
}
