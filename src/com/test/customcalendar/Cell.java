package com.test.customcalendar;

import java.io.Serializable;

/**
 * 每个日期方块对象
 * @author zhangshuo
 *
 */
public class Cell implements Serializable{
	private int year;
	/**月份，0~11*/
	private int month;
	private int day;
	
	/**
	 * 初始化CalendarView中的日期对象
	 * @param year 年，如2015
	 * @param month 月份，0~11
	 * @param day 日
	 */
	public Cell(int year, int month, int day){
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	/**月份，0~11*/
	public int getMonth() {
		return month;
	}
	/**月份，0~11*/
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return "Cell [year=" + year + ", month=" + month + ", day=" + day
				+ "]";
	}
}