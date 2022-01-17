package com.gap.bis_inspection.widget.DatePicker;

public class ShamsiParseDate {
	public ShamsiParseDate() {
		// TODO Auto-generated constructor stub
	}

	public String GetDate(String date_start) {
		// TODO Auto-generated constructor stub
		String temp_date = "";
		temp_date=date_start;
		String mm="0";

		date_start=date_start.trim();
		String[] start_parts = date_start.split("/");
		int day_start = Integer.parseInt(start_parts[0]);
		String	month_start =new String(start_parts[1]);
		month_start=month_start.trim();
		temp_date=month_start;
		int year_start = Integer.parseInt(start_parts[2]);
		

		if(temp_date.equalsIgnoreCase("فروردین"))
		{
		//	txt_name_month.setText("1");
			mm="01";
		}
		
		else if(temp_date.equalsIgnoreCase("ارديبهشت"))
		{
			//txt_name_month.setText("2");
			mm="02";
		}
		
		else if(temp_date.equalsIgnoreCase("خرداد"))
		{
			//txt_name_month.setText("3");
			mm="03";
		}
		
		
		else if(temp_date.equalsIgnoreCase("تیر"))
		{
			//txt_name_month.setText("4");
			mm="04";
		}
		
		
		else if(temp_date.equalsIgnoreCase("مرداد"))
		{
			//txt_name_month.setText("5");
			mm="05";
		}
		
		
		else if(temp_date.equalsIgnoreCase("شهریور"))
		{
			//txt_name_month.setText("6");
			mm="06";
		}
		
		
		
		else if(temp_date.equalsIgnoreCase("مهر"))
		{
			//txt_name_month.setText("7");
			mm="07";
		}
		
		else if(temp_date.equalsIgnoreCase("آبان"))
		{
			//txt_name_month.setText("8");
			mm="08";
		}
		
		
		else if(temp_date.equalsIgnoreCase("آذر"))
		{
			//txt_name_month.setText("9");
			mm="09";
		}
		
		
		else if(temp_date.equalsIgnoreCase("دی"))
		{
			//txt_name_month.setText("10");
			mm="10";
			
		}
		
		
		else if(temp_date.equalsIgnoreCase("بهمن"))
		{
		//	txt_name_month.setText("11");
			mm="11";
		}
		
		
		else if(temp_date.equalsIgnoreCase("اسفند"))
		{
		//	txt_name_month.setText("12");
			mm="12";
		}

		
		temp_date=year_start+"/"+mm+"/"+day_start;
		//temp_date=year_start+"/"+mm+"/"+day_start;
		return temp_date;
	}
}
