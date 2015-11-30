package com.test.customcalendar;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CalendarView extends View{

private final String TAG = CalendarView.class.getSimpleName();
	
	private  float DENSITY = getResources().getDisplayMetrics().density;
	private final float DEFAULT_STROKE_WIDTH = 1F;
	private final float DEFAULT_TEXT_SIZE_YEAR = 16F;
	private final float DEFAULT_TEXT_SIZE_WEEK = 16F;
	private final float DEFAULT_TEXT_SIZE_DAY = 14F;
	
	public String[] weekText = { "日", "一", "二", "三", "四", "五", "六"};
	
	/**年月的背景颜色*/
	private int colorYearBackground = Color.rgb(105, 146, 212);
	/**年月的字体颜色*/
	private int colorTextYear = Color.WHITE;
	
	/**整个日历组件的背景颜色*/
	private int colorBackground = Color.TRANSPARENT;
	/**日历内容的背景颜色*/
	private int colorForeground = Color.WHITE;
	/**边线的颜色*/
	private int colorBorder = Color.rgb(180, 180, 180);
	
	/**星期的字体颜色*/
	private int colorTextWeek = Color.rgb(106, 106, 106);
	/**周六和周日两个label的字体颜色*/
	private int colorTextWeekend = Color.rgb(234, 119, 4);
	
	/**本月日期的字体颜色*/
	private int colorTextDay = Color.rgb(100, 100, 100);
	/**非本月日期的字体颜色*/
	private int colorTextOtherMonthDay = Color.rgb(150, 150, 150);
	
	/**日期方块被选中后的字体颜色*/
	private int colorSelectedText = Color.WHITE;
	/**日期方块被选中后的背景颜色*/
	private int colorSelectedBackground = Color.rgb(124, 209, 176);
	/**日期方块被按压时的背景颜色*/
	private int colorTouchedBackground = Color.rgb(194, 229, 196);
	
	/**今天的日期方块的字体颜色*/
	private int colorTodayText = Color.WHITE;
	/**今天的日期方块的背景颜色*/
	private int colorTodayBackground = Color.rgb(230, 230, 230);
	
	
	private int paddingLeft, paddingTop, paddingRight, paddingBottom;
	
	/**显示日历的有效区域的宽和高*/
	private float rectWidth, rectHeight;
	/**记录显示年月的title的高度*/
	private float titleHeight = 0 * DENSITY;
	/**日历组件内边距，不同于padding*/
	private float space = 16 * DENSITY;
	/**每个方块的宽度*/
	private float perWidth;
	/**每个方块的高度*/
	private float perHeight;
	
	
	/**显示日历的有效区域的矩形*/
	private RectF rectf;
	
	/**缓存需要操作的方块*/
	private RectF rectfCell;
	
	/** 画笔的宽度*/
	private float strokeWidth = DEFAULT_STROKE_WIDTH;
	
	/** 年月字体的大小*/
	private float textSizeYear = DEFAULT_TEXT_SIZE_YEAR;
	/** 星期文字的大小*/
	private float textSizeWeek = DEFAULT_TEXT_SIZE_WEEK;
	/** 日期文字的大小*/
	private float textSizeDay = DEFAULT_TEXT_SIZE_DAY;
	
	/**每条横线的两端的点的坐标*/
	private float[][] rowPoints = new float[8][4];
	/**每条竖线的两端的点的坐标*/
	private float[][] columnPoints = new float[8][4];
	/**顶部显示年月的title区域的位置*/
	private float[] titlePoints = new float[4];
	
	/**用于绘制文字时，缓存当前需要绘制的文字所在的方块的位置*/
	private float[] cellPoints = new float[4];
 	
	private Paint paintBackgroud;
	
	private Paint paintBorder;
	
	private Paint paintText;
	
	/**通过该变量可以方便的取得Paint中的相关文字属性，以便绘制文字的baseLine*/
	private FontMetricsInt fmi;
	/**记录绘制文字时的基线，以便让文字居中绘制；
	 * 计算当文字居中显示时的baseline的公式：baseLine = rectf.top + (rectf.bottom - rectf.top) / 2  + (- fmi.top) - (fmi.bottom - fmi.top) / 2;
	 * rectf.top + (rectf.bottom - rectf.top) / 2算得的值是需要绘制文字的矩形区域的中线；
	 * FontMetrics.top的数值是个负数，其绝对值就是字体绘制边界到baseline的距离，
	 * 所以(- fmi.top) - (fmi.bottom - fmi.top)/2 算得的值是指文字绘制区域的中线到baseLine的距离；
	 * 将这两个值相加就得到了baseLine在需要绘制文字的矩形区域中的位置；
	 */
	private float baseLine;
	
	private Calendar mCalendar;
	
	/** 记录每个日期方块对象的数组*/
	private Cell[] cells = new Cell[42];
	/** 记录今天的日期*/
	private Cell today = null;
	/** 记录当前显示的年月（日可有可无）*/
	private Cell date = null;
	
	/**记录用户上次点击的位置*/
	private float lastX, lastY;
	/**记录被选中的日期方块的序号*/
	private int selectedIndex = -1;
	/**标示当前用户的手指是否松开*/
	private boolean isTouch = false;
	/**
	 * 选中的日期
	 */
	private String selectedDate=null;
	/**
	 * 日期被选中后的回调接口（监听器）
	 */
	private OnDateSelectedListener onDateSelectedListener = null;
	
	public CalendarView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private void init(Context mContext){
		
		mCalendar = Calendar.getInstance();
		initFontSize();
		/*初始化今天的日期*/
		today = new Cell(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
		/*默认将当前显示的日期初始化为今天的日期*/
		date = new Cell(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
		
		rectf = new RectF();
		
		rectfCell = new RectF();
		
		paintBackgroud = new Paint();
		paintBackgroud.setAntiAlias(true);
		paintBackgroud.setColor(colorForeground);
		paintBackgroud.setStyle(Style.FILL);
		paintBackgroud.setStrokeWidth(strokeWidth);
		
		paintBorder = new Paint();
		paintBorder.setAntiAlias(true);
		paintBorder.setColor(colorBorder);
		paintBorder.setStyle(Style.STROKE);
		paintBorder.setStrokeWidth(strokeWidth);
		
		paintText = new Paint();
		paintText.setAntiAlias(true);
		paintText.setTextSize(textSizeDay);
		paintText.setTextAlign(Align.CENTER);
		
	}
	
	/**
	 * 根据不同的设备分辨率初始化字体大小
	 */
	private void initFontSize()
	{ 
		textSizeYear = sp2px(textSizeYear);
		textSizeWeek =sp2px(textSizeWeek);
		textSizeDay = sp2px(textSizeDay);
	}
	
	 /**
	  * 将sp值转换为px值，保证文字大小不变
	  * 
	  * @param spValue
	  * @param fontScale（DisplayMetrics类中属性scaledDensity）
	  * @return
	  */
	 private int sp2px(float spValue) {
		 float scaledDensity =  getResources().getDisplayMetrics().scaledDensity;  
	  return (int) (spValue * scaledDensity + 0.5f);
	 }
	 
	 /**
	  * 将dip或dp值转换为px值，保证尺寸大小不变
	  * 
	  * @param dipValue
	  * @param scale（DisplayMetrics类中属性density）
	  * @return
	  */
	 private int dip2px(float dipValue) {
		 float scaledDensity =  getResources().getDisplayMetrics().scaledDensity;  
	  return (int) (dipValue * scaledDensity + 0.5f);
	 }
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		canvas.drawColor(colorBackground);
		
		paintBackgroud.setColor(colorForeground);
		canvas.drawRect(rectf, paintBackgroud);
		
		/*绘制年月*/
		drawYear(canvas);
		
		/*绘制边线*/
//		drawBorder(canvas);
		
		/*绘制星期，首先要计算文字绘制基线baseLine，因为所有星期都在一行上，所以只需要计算一次*/
		drawWeek(canvas);
		
		/*绘制星期行下方的横线*/
		paintBorder.setColor(colorBorder);
		canvas.drawLine(rowPoints[1][0], rowPoints[1][1], rowPoints[1][2], rowPoints[1][3], paintBorder);
		
		/*计算需要显示的日期*/
		caculateDate(date.getYear(), date.getMonth());
		
		/*绘制日期*/
		drawDate(canvas);
		
		/*绘制被选中的日期*/
		drawSelectedDate(canvas, selectedIndex);
		
		
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		
		paddingLeft = getPaddingLeft();
		paddingTop = getPaddingTop();
		paddingRight = getPaddingRight();
		paddingBottom = getPaddingBottom();
		
		Log.e("getWidth", " --> " + getWidth());
		Log.e("getHeight", " --> " + getHeight());
		
		rectWidth = getWidth() - paddingLeft - paddingRight;
		rectHeight = getHeight() - paddingTop - paddingBottom;
		
		rectWidth = rectHeight = Math.min(rectWidth, rectHeight);
		
		/*初始化顶部显示年月的title区域的位置*/
		titlePoints[0] = paddingLeft;
		titlePoints[1] = paddingTop;
		titlePoints[2] = paddingLeft + rectWidth;
		titlePoints[3] = paddingTop + titleHeight;
		
		/*得到显示日历的矩形区域*/
		rectf.set(paddingLeft, paddingTop + titleHeight, rectWidth + paddingLeft, rectHeight + paddingTop);
		
		/*先对对显示日历的矩形区域缩减出内边距，然后减去8条border的宽度，最后除以7，得到每个日期格子的宽高*/
		perWidth = ((rectWidth - 2 * space) - (strokeWidth * 8))/(float)7;
		perHeight = ((rectHeight - titleHeight - 2 * space) - (strokeWidth * 8))/(float)7;
		
		
		/*计算构筑日历格子的横竖线两端的位置*/
		for(int i = 0; i < 8; i++){
			rowPoints[i][0] = paddingLeft + space;
			rowPoints[i][1] = paddingTop + titleHeight + space + i * (perHeight + strokeWidth);
			rowPoints[i][2] = paddingLeft + rectWidth - space - strokeWidth;
			rowPoints[i][3] = paddingTop + titleHeight + space + i * (perHeight + strokeWidth);
			
			columnPoints[i][0] = paddingLeft + space + i * (perWidth + strokeWidth);
			columnPoints[i][1] = paddingTop + titleHeight + space;
			columnPoints[i][2] = paddingLeft + space + i * (perWidth + strokeWidth);
			columnPoints[i][3] = paddingTop + rectHeight - space - strokeWidth;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		Log.e("getMeasuredWidth", " --> " + getMeasuredWidth());
		Log.e("getMeasuredHeight", " --> " + getMeasuredHeight());
		//将view的宽高强制设置为相同
//		int mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());  
//		setMeasuredDimension(mWidth, mWidth); 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.e(TAG, "ACTION_DOWN");
			lastX = event.getX();
			lastY = event.getY();
			selectedIndex = getCellIndex(lastX, lastY);
			selectedDate=getSelectDate(selectedIndex);
			isTouch = true;
			postInvalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.e(TAG, "ACTION_MOVE");
			break;
		case MotionEvent.ACTION_UP:
			Log.e(TAG, "ACTION_UP");
			if(selectedIndex < 8 || selectedIndex > 49 || selectedIndex != getCellIndex(event.getX(), event.getY())){
				/*按下时的方块和松开手时的方块不同，则放弃选择*/
				selectedIndex = -1;
				selectedDate=null;
			}else{
				/*按下时的方块和松开手时的方块相同，则选择成功*/
				if(null != this.onDateSelectedListener){
					this.onDateSelectedListener.onSelected(cells[selectedIndex - 8]);
				}
			}
			isTouch = false;
			postInvalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.e(TAG, "ACTION_CANCEL");
			/*放弃选择*/
			selectedIndex = -1;
			isTouch = false;
			postInvalidate();
			break;
		default:
			break;
		}
		return true;
//		return super.onTouchEvent(event);
	}
	
	/**
	 * 在第一至七个方块组成的长方形区域，也即第一行上绘制年月
	 * @param canvas
	 */
	private void drawYear(Canvas canvas){
		if(titleHeight <= 0){
			Log.d(TAG, "drawYear-->titleHeight<=0，不再绘制显示年月的title区域");
			return;
		}
		/*将title矩形的位置设置给rectfCell*/
		rectfCell.set(titlePoints[0], titlePoints[1], titlePoints[2], titlePoints[3]);
		
		/*绘制背景*/
		paintBackgroud.setColor(colorYearBackground);
		canvas.drawRect(rectfCell, paintBackgroud);
		/*绘制字体*/
		paintText.setColor(colorTextYear);
		paintText.setTextSize(textSizeYear);
		baseLine = getTextBaseLine(titlePoints[1], titlePoints[3]);
		canvas.drawText(date.getYear() + "年 " + date.getMonth() + "月", rectfCell.centerX(), baseLine, paintText);
	}
	
	/**
	 * 绘制边线
	 * @param canvas
	 */
	private void drawBorder(Canvas canvas){
		paintBorder.setColor(colorBorder);
		for(int i = 0; i < rowPoints.length; i++){
			canvas.drawLine(rowPoints[i][0], rowPoints[i][1], rowPoints[i][2], rowPoints[i][3], paintBorder);
		}
		for(int i = 0; i < columnPoints.length; i++){
			canvas.drawLine(columnPoints[i][0], columnPoints[i][1], columnPoints[i][2], columnPoints[i][3], paintBorder);
		}
	}
	
	/**
	 * 绘制星期
	 * @param canvas
	 */
	private void drawWeek(Canvas canvas){
		/*首先要计算文字绘制基线baseLine，因为所有星期都在一行上，所以只需要计算一次*/
		paintText.setTextSize(textSizeWeek);
		for(int i = 0; i < weekText.length; i++){
			cellPoints = getCellPoints(1 + i);
			if(i == 0){
				baseLine = getTextBaseLine(cellPoints[1], cellPoints[3]);
			}
			rectfCell.set(cellPoints[0], cellPoints[1], cellPoints[2], cellPoints[3]);
			if(i == 0 || i == weekText.length - 1){
				/*是周末，设置一种字体颜色*/
				paintText.setColor(colorTextWeekend);
			}else{
				/*不是周末，设置另一种字体颜色*/
				paintText.setColor(colorTextWeek);
			}
			canvas.drawText(weekText[i], rectfCell.centerX(), baseLine, paintText);
		}
	}
	
	/**
	 * 计算需要显示的日期，并将日期赋值到cells
	 * @param year 指定年份
	 * @param month 指定月份（0~11）
	 */
	private void caculateDate(int year, int month){
		mCalendar.set(year, month, 1);// 设为指定年月的1号 
//		mCalendar.set(Calendar.DATE, 1);
		System.out.println("当前月的第一天：" + mCalendar.get(Calendar.DAY_OF_MONTH));
		/*本月的第一天的星期数*/
		int currentMonthFirstDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
		/*需要显示的上个月的天数*/
		int lastMonthDayShowNum = 0;
		System.out.println("当前月的第一天是星期：" + currentMonthFirstDayOfWeek);
		if(currentMonthFirstDayOfWeek == 1){
			/*如果当月的第一天是星期一，那么向上个月取7天（一周）*/
			lastMonthDayShowNum = 7;
		}else{
			/*如果当月的第一天不是星期一，那么向上个月取到本周的星期一*/
			lastMonthDayShowNum = currentMonthFirstDayOfWeek - 1;
		}
		mCalendar.add(Calendar.DATE, -lastMonthDayShowNum);
		
		for(int i = 0; i < cells.length; i++){
			Cell cell = new Cell(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
			cells[i] = cell;
			mCalendar.add(Calendar.DATE, 1);
		}
		System.out.println("需要显示的上个月的日期：" + cells[0].getYear() + ":" + cells[0].getMonth() + ":" + cells[0].getDay());
		System.out.println("需要显示的下个月的日期：" + cells[cells.length-1].getYear() + ":" + cells[cells.length-1].getMonth() + ":" + cells[cells.length-1].getDay());
	}
	
	/**
	 * 绘制日期
	 * @param canvas
	 */
	private void drawDate(Canvas canvas){
		paintText.setTextSize(textSizeDay);
		for(int i = 0; i < cells.length; i++){
			cellPoints = getCellPoints(8 + i);
			if(i % 7 == 0){
				baseLine = getTextBaseLine(cellPoints[1], cellPoints[3]);
			}
			rectfCell.set(cellPoints[0], cellPoints[1], cellPoints[2], cellPoints[3]);
			if(date.getYear() != cells[i].getYear() || date.getMonth() != cells[i].getMonth()){
				/*不是本月的日期，重新设置一种字体颜色*/
				paintText.setColor(colorTextOtherMonthDay);
			}else{
				/*是本月的日期，再重新设置一种字体颜色*/
				paintText.setColor(colorTextDay);
				
			}
			if(today.getYear() == cells[i].getYear() && today.getMonth() == cells[i].getMonth() && today.getDay() == cells[i].getDay()){
				/*今天日期，设置另一种字体颜色，并重新绘制另一种背景色*/
				paintText.setColor(colorTodayText);
				paintBackgroud.setColor(colorTodayBackground);
//				canvas.drawRect(rectfCell, paintBackgroud);
				canvas.drawRoundRect(rectfCell,dip2px(3),dip2px(3), paintBackgroud);
			}
			canvas.drawText(String.valueOf(cells[i].getDay()), rectfCell.centerX(), baseLine, paintText);
		}
	}
	
	/**
	 * 绘制被选中的日期
	 * @param canvas
	 * @param position
	 */
	private void drawSelectedDate(Canvas canvas, int index){
		if(index < 8 || index > 49){
			Log.d(TAG, "drawSelectedDate-->需要绘制的日期不合法 index = " + index);
			return;
		}
		
		String currentDate=getSelectDate(index);
		if(currentDate==null||!currentDate.equals(selectedDate))
		{
			return;
		}
		cellPoints = getCellPoints(index);
		rectfCell.set(cellPoints[0], cellPoints[1], cellPoints[2], cellPoints[3]);
		/*绘制背景*/
		if(isTouch){
			paintBackgroud.setColor(colorTouchedBackground);
		}else{
			paintBackgroud.setColor(colorSelectedBackground);
		}
		
//		canvas.drawArc(rectfCell, 0, 360, false, paintBackgroud);
		canvas.drawRoundRect(rectfCell,dip2px(3),dip2px(3), paintBackgroud);
		/*绘制字体*/
		paintText.setColor(colorSelectedText);
		paintText.setTextSize(textSizeDay);
		baseLine = getTextBaseLine(cellPoints[1], cellPoints[3]);
		canvas.drawText(String.valueOf(cells[index-8].getDay()), rectfCell.centerX(), baseLine, paintText);
	}
	
	/**
	 * 获取指定方块的位置
	 * @param index 1~49；1~7是第一行，绘制星期；8~49绘制日期
	 * @return 如果找不到对应的方块，则返回points = new float[4];
	 */
	private float[] getCellPoints(int index){
		float[] points = new float[4]; 
		if(index >= 1 && index <= 49){
			int row = index / 7;
			int column = index % 7;
			
			if(column == 0 && row > 0){
				row = row - 1;
			}
			
			if(column == 0){
				column = 7;
			}
			row = row + 1;
			
			points[0] = columnPoints[column - 1][0];
			points[1] = rowPoints[row - 1][1];
			points[2] = columnPoints[column][0];
			points[3] = rowPoints[row][1];
		}
		return points;
	}
	
	/**
	 * 计算居中绘制文字时的基线
	 * @param rectTop 需要绘制文字的矩形区域的top
	 * @param rectBottom 需要绘制文字的矩形区域的bottom
	 * @return
	 */
	private float getTextBaseLine(float rectTop, float rectBottom){
		fmi = paintText.getFontMetricsInt(); 
		return rectTop + (rectBottom - rectTop) / 2  + (- fmi.top) - (fmi.bottom - fmi.top) / 2;
	}
	
	/**
	 * 查找坐标值X,Y对应的日期方块的序号
	 * @param x
	 * @param y
	 * @return 查找不到对应的方块序号，则返回-1
	 */
	private int getCellIndex(float x, float y){
		int index = -1;
		for(int i = 0; i < rowPoints.length - 1; i++){
			if(y > rowPoints[i][1] && y < rowPoints[i+1][1]){
				for(int j = 0; j < columnPoints.length - 1; j++){
					if(x > columnPoints[j][0] && x < columnPoints[j+1][0]){
						index = i * 7 + (j + 1);
						break;
					}
				}
				break;
			}
		}
		return index;
	}
	
	/**
	 * 获取选中的日期
	 * @return
	 */
	private String getSelectDate(int index)
	{
		if(index >= 1 && index <= 41)
		{
		Cell cell=cells[index];
		return cell.getYear()+"-"+(cell.getMonth()+1)+"_"+cell.getDay();
		}
		return selectedDate;
	}
	
	public OnDateSelectedListener getOnDateSelectedListener() {
		return onDateSelectedListener;
	}

	/**
	 * 设置日期被选中后的回调监听器
	 * @param onDateSelectedListener
	 */
	public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
		this.onDateSelectedListener = onDateSelectedListener;
	}

	/**
	 * 获取当前显示的日期（年月必有，日可有可无）
	 * @return
	 */
	public Cell getDate() {
		return date;
	}

	/**
	 * 设置当前要显示的日期（年月必有，日可有可无）
	 * @param date
	 */
	public void setDate(Cell date) {
		this.date = date;
		postInvalidate();
	}
	
	/**
	 * 获取今天的日期
	 * @return
	 */
	public Cell getToday(){
		return today;
	}

	/**
	 * 日期被选中后的回调接口
	 * @author zhangshuo
	 */
	public interface OnDateSelectedListener{
		/**
		 * 日期被选中
		 * @param cell 被选中的日期
		 */
		public void onSelected(Cell cell);
	}
}
