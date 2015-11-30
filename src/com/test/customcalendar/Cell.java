package com.test.customcalendar;

import java.io.Serializable;

/**
 * ÿ�����ڷ������
 * @author zhangshuo
 *
 */
public class Cell implements Serializable{
	private int year;
	/**�·ݣ�0~11*/
	private int month;
	private int day;
	
	/**
	 * ��ʼ��CalendarView�е����ڶ���
	 * @param year �꣬��2015
	 * @param month �·ݣ�0~11
	 * @param day ��
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
	/**�·ݣ�0~11*/
	public int getMonth() {
		return month;
	}
	/**�·ݣ�0~11*/
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