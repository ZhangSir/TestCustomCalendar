package com.test.customcalendar;

import java.util.Calendar;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.test.customcalendar.CalendarView.OnDateSelectedListener;

/**
 * 包含日历的组合日历组件
 * @author zhangshuo
 */
public class CalendarLayout extends LinearLayout implements OnClickListener, OnDateSelectedListener{
	
	private final String TAG = CalendarLayout.class.getSimpleName();
	
	private Context mContext;
	
	private RelativeLayout rlayoutTitle;
	
	private TextView tvTitle;
	
	private ImageButton ibtnPrevious, ibtnNext;
	
	private ViewPager vpCalendar;
	
	private CalendarAdapter<CalendarView> mAdapter;
	
	/**
	 * 日期被选中后的回调接口（监听器）
	 */
	private OnDateSelectedListener onDateSelectedListener = null;
	
	public CalendarLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public CalendarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@SuppressLint("NewApi")
	public CalendarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private void init(Context mContext){
		this.mContext = mContext;
		View contentView = LayoutInflater.from(this.mContext).inflate(
				R.layout.layout_calendar_layout, this);
		rlayoutTitle = (RelativeLayout) contentView
				.findViewById(R.id.rlayout_calendar_layout_title);
		tvTitle = (TextView) rlayoutTitle
				.findViewById(R.id.tv_calendar_layout_title);
		ibtnPrevious = (ImageButton) rlayoutTitle
				.findViewById(R.id.ibtn_calendar_layout_title_previous);
		ibtnNext = (ImageButton) rlayoutTitle
				.findViewById(R.id.ibtn_calendar_layout_title_next);
		vpCalendar = (ViewPager) contentView
				.findViewById(R.id.vp_calendar_layout);
		
		ibtnPrevious.setOnClickListener(this);
		ibtnNext.setOnClickListener(this);
		
		CalendarView[] views = new CalendarView[3];  
        for (int i = 0; i < 3; i++) {  
            views[i] = (CalendarView) LayoutInflater.from(mContext).inflate(R.layout.layout_calendar_layout_item, null);
            views[i].setOnDateSelectedListener(this);
        }  
        mAdapter = new CalendarAdapter<CalendarView>(views);  
		vpCalendar.setAdapter(mAdapter);
		vpCalendar.setOnPageChangeListener(mAdapter);
		vpCalendar.setCurrentItem(mAdapter.getStartPostion());  
	}

	
	class CalendarAdapter<V extends View> extends PagerAdapter implements ViewPager.OnPageChangeListener {

		/** 此Viewpager的Adapter要实现无限循环，需要设置一个起始位置startPosition*/
		private final int startPostion = 1000;
		
		private V[] views;
		
		private Cell date;
		
		private Calendar mCalendar;
		
		/**
		 * 记录当前显示的position
		 */
		private int currentPosition = startPostion;
		
		public CalendarAdapter(V[] views) {
			this.views = views;
			mCalendar = Calendar.getInstance();
			date = new Cell(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
		}

		/**
		 * 通过指定页面的Position，计算对应的日期，并设置给date变量
		 * @param position
		 */
		private void initDateByPosition(int position){
			if(position > currentPosition){
				/*下一个页面*/
				mCalendar.add(Calendar.MONTH, position - currentPosition);
			}else if(position < currentPosition){
				/*上一个页面*/
				mCalendar.add(Calendar.MONTH, position - currentPosition);
			}
			date.setYear(mCalendar.get(Calendar.YEAR));
			date.setMonth(mCalendar.get(Calendar.MONTH));
			date.setDay(mCalendar.get(Calendar.DAY_OF_MONTH));
			currentPosition = position;
		}
		
		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
//			container.removeView((View) object);
			((ViewPager) container).removeView((View) container); 
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			if (((ViewPager) container).getChildCount() == views.length) {  
	            ((ViewPager) container).removeView(views[position % views.length]);  
	        }
			((ViewPager) container).addView(views[position % views.length], 0);  
	        return views[position % views.length]; 
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (View)arg1;
		}

		public V[] getAllItems() {  
		    return views;  
		}  
		 
		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			initDateByPosition(position);
			((CalendarView)views[position % views.length]).setDate(date);
			
			Cell cell = ((CalendarView)views[position % views.length]).getDate();
			if(null != cell){
				System.out.println("onPageSelected-------->" + cell.getYear() + "年" + cell.getMonth() + "月");
				tvTitle.setText(cell.getYear() + "年" + (cell.getMonth() + 1) + "月");
			}else{
				System.out.println("onPageSelected-------->cell为null");
			}
		}

		/**
		 * 获取ViewPager的默认打开位置
		 * @return
		 */
		public int getStartPostion() {
			return startPostion;
		}

	}


	@Override
	public void onSelected(Cell cell) {
		// TODO Auto-generated method stub
		if(null != this.onDateSelectedListener){
			this.onDateSelectedListener.onSelected(cell);
		}
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibtn_calendar_layout_title_previous:
			vpCalendar.setCurrentItem(vpCalendar.getCurrentItem() - 1, true);
			break;
		case R.id.ibtn_calendar_layout_title_next:
			vpCalendar.setCurrentItem(vpCalendar.getCurrentItem() + 1, true);
			break;
		default:
			break;
		}
	}
}
