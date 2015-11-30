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
	
	public String[] weekText = { "��", "һ", "��", "��", "��", "��", "��"};
	
	/**���µı�����ɫ*/
	private int colorYearBackground = Color.rgb(105, 146, 212);
	/**���µ�������ɫ*/
	private int colorTextYear = Color.WHITE;
	
	/**������������ı�����ɫ*/
	private int colorBackground = Color.TRANSPARENT;
	/**�������ݵı�����ɫ*/
	private int colorForeground = Color.WHITE;
	/**���ߵ���ɫ*/
	private int colorBorder = Color.rgb(180, 180, 180);
	
	/**���ڵ�������ɫ*/
	private int colorTextWeek = Color.rgb(106, 106, 106);
	/**��������������label��������ɫ*/
	private int colorTextWeekend = Color.rgb(234, 119, 4);
	
	/**�������ڵ�������ɫ*/
	private int colorTextDay = Color.rgb(100, 100, 100);
	/**�Ǳ������ڵ�������ɫ*/
	private int colorTextOtherMonthDay = Color.rgb(150, 150, 150);
	
	/**���ڷ��鱻ѡ�к��������ɫ*/
	private int colorSelectedText = Color.WHITE;
	/**���ڷ��鱻ѡ�к�ı�����ɫ*/
	private int colorSelectedBackground = Color.rgb(124, 209, 176);
	/**���ڷ��鱻��ѹʱ�ı�����ɫ*/
	private int colorTouchedBackground = Color.rgb(194, 229, 196);
	
	/**��������ڷ����������ɫ*/
	private int colorTodayText = Color.WHITE;
	/**��������ڷ���ı�����ɫ*/
	private int colorTodayBackground = Color.rgb(230, 230, 230);
	
	
	private int paddingLeft, paddingTop, paddingRight, paddingBottom;
	
	/**��ʾ��������Ч����Ŀ�͸�*/
	private float rectWidth, rectHeight;
	/**��¼��ʾ���µ�title�ĸ߶�*/
	private float titleHeight = 0 * DENSITY;
	/**��������ڱ߾࣬��ͬ��padding*/
	private float space = 16 * DENSITY;
	/**ÿ������Ŀ��*/
	private float perWidth;
	/**ÿ������ĸ߶�*/
	private float perHeight;
	
	
	/**��ʾ��������Ч����ľ���*/
	private RectF rectf;
	
	/**������Ҫ�����ķ���*/
	private RectF rectfCell;
	
	/** ���ʵĿ��*/
	private float strokeWidth = DEFAULT_STROKE_WIDTH;
	
	/** ��������Ĵ�С*/
	private float textSizeYear = DEFAULT_TEXT_SIZE_YEAR;
	/** �������ֵĴ�С*/
	private float textSizeWeek = DEFAULT_TEXT_SIZE_WEEK;
	/** �������ֵĴ�С*/
	private float textSizeDay = DEFAULT_TEXT_SIZE_DAY;
	
	/**ÿ�����ߵ����˵ĵ������*/
	private float[][] rowPoints = new float[8][4];
	/**ÿ�����ߵ����˵ĵ������*/
	private float[][] columnPoints = new float[8][4];
	/**������ʾ���µ�title�����λ��*/
	private float[] titlePoints = new float[4];
	
	/**���ڻ�������ʱ�����浱ǰ��Ҫ���Ƶ��������ڵķ����λ��*/
	private float[] cellPoints = new float[4];
 	
	private Paint paintBackgroud;
	
	private Paint paintBorder;
	
	private Paint paintText;
	
	/**ͨ���ñ������Է����ȡ��Paint�е�����������ԣ��Ա�������ֵ�baseLine*/
	private FontMetricsInt fmi;
	/**��¼��������ʱ�Ļ��ߣ��Ա������־��л��ƣ�
	 * ���㵱���־�����ʾʱ��baseline�Ĺ�ʽ��baseLine = rectf.top + (rectf.bottom - rectf.top) / 2  + (- fmi.top) - (fmi.bottom - fmi.top) / 2;
	 * rectf.top + (rectf.bottom - rectf.top) / 2��õ�ֵ����Ҫ�������ֵľ�����������ߣ�
	 * FontMetrics.top����ֵ�Ǹ������������ֵ����������Ʊ߽絽baseline�ľ��룬
	 * ����(- fmi.top) - (fmi.bottom - fmi.top)/2 ��õ�ֵ��ָ���ֻ�����������ߵ�baseLine�ľ��룻
	 * ��������ֵ��Ӿ͵õ���baseLine����Ҫ�������ֵľ��������е�λ�ã�
	 */
	private float baseLine;
	
	private Calendar mCalendar;
	
	/** ��¼ÿ�����ڷ�����������*/
	private Cell[] cells = new Cell[42];
	/** ��¼���������*/
	private Cell today = null;
	/** ��¼��ǰ��ʾ�����£��տ��п��ޣ�*/
	private Cell date = null;
	
	/**��¼�û��ϴε����λ��*/
	private float lastX, lastY;
	/**��¼��ѡ�е����ڷ�������*/
	private int selectedIndex = -1;
	/**��ʾ��ǰ�û�����ָ�Ƿ��ɿ�*/
	private boolean isTouch = false;
	/**
	 * ѡ�е�����
	 */
	private String selectedDate=null;
	/**
	 * ���ڱ�ѡ�к�Ļص��ӿڣ���������
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
		/*��ʼ�����������*/
		today = new Cell(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
		/*Ĭ�Ͻ���ǰ��ʾ�����ڳ�ʼ��Ϊ���������*/
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
	 * ���ݲ�ͬ���豸�ֱ��ʳ�ʼ�������С
	 */
	private void initFontSize()
	{ 
		textSizeYear = sp2px(textSizeYear);
		textSizeWeek =sp2px(textSizeWeek);
		textSizeDay = sp2px(textSizeDay);
	}
	
	 /**
	  * ��spֵת��Ϊpxֵ����֤���ִ�С����
	  * 
	  * @param spValue
	  * @param fontScale��DisplayMetrics��������scaledDensity��
	  * @return
	  */
	 private int sp2px(float spValue) {
		 float scaledDensity =  getResources().getDisplayMetrics().scaledDensity;  
	  return (int) (spValue * scaledDensity + 0.5f);
	 }
	 
	 /**
	  * ��dip��dpֵת��Ϊpxֵ����֤�ߴ��С����
	  * 
	  * @param dipValue
	  * @param scale��DisplayMetrics��������density��
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
		
		/*��������*/
		drawYear(canvas);
		
		/*���Ʊ���*/
//		drawBorder(canvas);
		
		/*�������ڣ�����Ҫ�������ֻ��ƻ���baseLine����Ϊ�������ڶ���һ���ϣ�����ֻ��Ҫ����һ��*/
		drawWeek(canvas);
		
		/*�����������·��ĺ���*/
		paintBorder.setColor(colorBorder);
		canvas.drawLine(rowPoints[1][0], rowPoints[1][1], rowPoints[1][2], rowPoints[1][3], paintBorder);
		
		/*������Ҫ��ʾ������*/
		caculateDate(date.getYear(), date.getMonth());
		
		/*��������*/
		drawDate(canvas);
		
		/*���Ʊ�ѡ�е�����*/
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
		
		/*��ʼ��������ʾ���µ�title�����λ��*/
		titlePoints[0] = paddingLeft;
		titlePoints[1] = paddingTop;
		titlePoints[2] = paddingLeft + rectWidth;
		titlePoints[3] = paddingTop + titleHeight;
		
		/*�õ���ʾ�����ľ�������*/
		rectf.set(paddingLeft, paddingTop + titleHeight, rectWidth + paddingLeft, rectHeight + paddingTop);
		
		/*�ȶԶ���ʾ�����ľ��������������ڱ߾࣬Ȼ���ȥ8��border�Ŀ�ȣ�������7���õ�ÿ�����ڸ��ӵĿ��*/
		perWidth = ((rectWidth - 2 * space) - (strokeWidth * 8))/(float)7;
		perHeight = ((rectHeight - titleHeight - 2 * space) - (strokeWidth * 8))/(float)7;
		
		
		/*���㹹���������ӵĺ��������˵�λ��*/
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
		//��view�Ŀ��ǿ������Ϊ��ͬ
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
				/*����ʱ�ķ�����ɿ���ʱ�ķ��鲻ͬ�������ѡ��*/
				selectedIndex = -1;
				selectedDate=null;
			}else{
				/*����ʱ�ķ�����ɿ���ʱ�ķ�����ͬ����ѡ��ɹ�*/
				if(null != this.onDateSelectedListener){
					this.onDateSelectedListener.onSelected(cells[selectedIndex - 8]);
				}
			}
			isTouch = false;
			postInvalidate();
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.e(TAG, "ACTION_CANCEL");
			/*����ѡ��*/
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
	 * �ڵ�һ���߸�������ɵĳ���������Ҳ����һ���ϻ�������
	 * @param canvas
	 */
	private void drawYear(Canvas canvas){
		if(titleHeight <= 0){
			Log.d(TAG, "drawYear-->titleHeight<=0�����ٻ�����ʾ���µ�title����");
			return;
		}
		/*��title���ε�λ�����ø�rectfCell*/
		rectfCell.set(titlePoints[0], titlePoints[1], titlePoints[2], titlePoints[3]);
		
		/*���Ʊ���*/
		paintBackgroud.setColor(colorYearBackground);
		canvas.drawRect(rectfCell, paintBackgroud);
		/*��������*/
		paintText.setColor(colorTextYear);
		paintText.setTextSize(textSizeYear);
		baseLine = getTextBaseLine(titlePoints[1], titlePoints[3]);
		canvas.drawText(date.getYear() + "�� " + date.getMonth() + "��", rectfCell.centerX(), baseLine, paintText);
	}
	
	/**
	 * ���Ʊ���
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
	 * ��������
	 * @param canvas
	 */
	private void drawWeek(Canvas canvas){
		/*����Ҫ�������ֻ��ƻ���baseLine����Ϊ�������ڶ���һ���ϣ�����ֻ��Ҫ����һ��*/
		paintText.setTextSize(textSizeWeek);
		for(int i = 0; i < weekText.length; i++){
			cellPoints = getCellPoints(1 + i);
			if(i == 0){
				baseLine = getTextBaseLine(cellPoints[1], cellPoints[3]);
			}
			rectfCell.set(cellPoints[0], cellPoints[1], cellPoints[2], cellPoints[3]);
			if(i == 0 || i == weekText.length - 1){
				/*����ĩ������һ��������ɫ*/
				paintText.setColor(colorTextWeekend);
			}else{
				/*������ĩ��������һ��������ɫ*/
				paintText.setColor(colorTextWeek);
			}
			canvas.drawText(weekText[i], rectfCell.centerX(), baseLine, paintText);
		}
	}
	
	/**
	 * ������Ҫ��ʾ�����ڣ��������ڸ�ֵ��cells
	 * @param year ָ�����
	 * @param month ָ���·ݣ�0~11��
	 */
	private void caculateDate(int year, int month){
		mCalendar.set(year, month, 1);// ��Ϊָ�����µ�1�� 
//		mCalendar.set(Calendar.DATE, 1);
		System.out.println("��ǰ�µĵ�һ�죺" + mCalendar.get(Calendar.DAY_OF_MONTH));
		/*���µĵ�һ���������*/
		int currentMonthFirstDayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
		/*��Ҫ��ʾ���ϸ��µ�����*/
		int lastMonthDayShowNum = 0;
		System.out.println("��ǰ�µĵ�һ�������ڣ�" + currentMonthFirstDayOfWeek);
		if(currentMonthFirstDayOfWeek == 1){
			/*������µĵ�һ��������һ����ô���ϸ���ȡ7�죨һ�ܣ�*/
			lastMonthDayShowNum = 7;
		}else{
			/*������µĵ�һ�첻������һ����ô���ϸ���ȡ�����ܵ�����һ*/
			lastMonthDayShowNum = currentMonthFirstDayOfWeek - 1;
		}
		mCalendar.add(Calendar.DATE, -lastMonthDayShowNum);
		
		for(int i = 0; i < cells.length; i++){
			Cell cell = new Cell(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
			cells[i] = cell;
			mCalendar.add(Calendar.DATE, 1);
		}
		System.out.println("��Ҫ��ʾ���ϸ��µ����ڣ�" + cells[0].getYear() + ":" + cells[0].getMonth() + ":" + cells[0].getDay());
		System.out.println("��Ҫ��ʾ���¸��µ����ڣ�" + cells[cells.length-1].getYear() + ":" + cells[cells.length-1].getMonth() + ":" + cells[cells.length-1].getDay());
	}
	
	/**
	 * ��������
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
				/*���Ǳ��µ����ڣ���������һ��������ɫ*/
				paintText.setColor(colorTextOtherMonthDay);
			}else{
				/*�Ǳ��µ����ڣ�����������һ��������ɫ*/
				paintText.setColor(colorTextDay);
				
			}
			if(today.getYear() == cells[i].getYear() && today.getMonth() == cells[i].getMonth() && today.getDay() == cells[i].getDay()){
				/*�������ڣ�������һ��������ɫ�������»�����һ�ֱ���ɫ*/
				paintText.setColor(colorTodayText);
				paintBackgroud.setColor(colorTodayBackground);
//				canvas.drawRect(rectfCell, paintBackgroud);
				canvas.drawRoundRect(rectfCell,dip2px(3),dip2px(3), paintBackgroud);
			}
			canvas.drawText(String.valueOf(cells[i].getDay()), rectfCell.centerX(), baseLine, paintText);
		}
	}
	
	/**
	 * ���Ʊ�ѡ�е�����
	 * @param canvas
	 * @param position
	 */
	private void drawSelectedDate(Canvas canvas, int index){
		if(index < 8 || index > 49){
			Log.d(TAG, "drawSelectedDate-->��Ҫ���Ƶ����ڲ��Ϸ� index = " + index);
			return;
		}
		
		String currentDate=getSelectDate(index);
		if(currentDate==null||!currentDate.equals(selectedDate))
		{
			return;
		}
		cellPoints = getCellPoints(index);
		rectfCell.set(cellPoints[0], cellPoints[1], cellPoints[2], cellPoints[3]);
		/*���Ʊ���*/
		if(isTouch){
			paintBackgroud.setColor(colorTouchedBackground);
		}else{
			paintBackgroud.setColor(colorSelectedBackground);
		}
		
//		canvas.drawArc(rectfCell, 0, 360, false, paintBackgroud);
		canvas.drawRoundRect(rectfCell,dip2px(3),dip2px(3), paintBackgroud);
		/*��������*/
		paintText.setColor(colorSelectedText);
		paintText.setTextSize(textSizeDay);
		baseLine = getTextBaseLine(cellPoints[1], cellPoints[3]);
		canvas.drawText(String.valueOf(cells[index-8].getDay()), rectfCell.centerX(), baseLine, paintText);
	}
	
	/**
	 * ��ȡָ�������λ��
	 * @param index 1~49��1~7�ǵ�һ�У��������ڣ�8~49��������
	 * @return ����Ҳ�����Ӧ�ķ��飬�򷵻�points = new float[4];
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
	 * ������л�������ʱ�Ļ���
	 * @param rectTop ��Ҫ�������ֵľ��������top
	 * @param rectBottom ��Ҫ�������ֵľ��������bottom
	 * @return
	 */
	private float getTextBaseLine(float rectTop, float rectBottom){
		fmi = paintText.getFontMetricsInt(); 
		return rectTop + (rectBottom - rectTop) / 2  + (- fmi.top) - (fmi.bottom - fmi.top) / 2;
	}
	
	/**
	 * ��������ֵX,Y��Ӧ�����ڷ�������
	 * @param x
	 * @param y
	 * @return ���Ҳ�����Ӧ�ķ�����ţ��򷵻�-1
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
	 * ��ȡѡ�е�����
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
	 * �������ڱ�ѡ�к�Ļص�������
	 * @param onDateSelectedListener
	 */
	public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
		this.onDateSelectedListener = onDateSelectedListener;
	}

	/**
	 * ��ȡ��ǰ��ʾ�����ڣ����±��У��տ��п��ޣ�
	 * @return
	 */
	public Cell getDate() {
		return date;
	}

	/**
	 * ���õ�ǰҪ��ʾ�����ڣ����±��У��տ��п��ޣ�
	 * @param date
	 */
	public void setDate(Cell date) {
		this.date = date;
		postInvalidate();
	}
	
	/**
	 * ��ȡ���������
	 * @return
	 */
	public Cell getToday(){
		return today;
	}

	/**
	 * ���ڱ�ѡ�к�Ļص��ӿ�
	 * @author zhangshuo
	 */
	public interface OnDateSelectedListener{
		/**
		 * ���ڱ�ѡ��
		 * @param cell ��ѡ�е�����
		 */
		public void onSelected(Cell cell);
	}
}
