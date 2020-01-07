package nk.bluefrog.rythusevaoffice.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import nk.bluefrog.rythusevaoffice.R;
import nk.bluefrog.rythusevaoffice.models.CustomDate;

/**
 ** Created by a7med on 28/06/2015.
 **/
public class CalendarView extends LinearLayout {
	// for logging
	private static final String LOGTAG = "Calendar View";

	// how many days to show, defaults to six weeks, 42 days
	private static final int DAYS_COUNT = 42;

	// default date format
	//private static final String DATE_FORMAT = "MMM yyyy";

    private static final String DATE_FORMAT = "dd/MM/yyyy";

	// date format
	private String dateFormat;

	// current displayed month
	private Calendar currentDate = Calendar.getInstance();

	//event handling
	private EventHandler eventHandler = null;

    private OnEventExist onEventExist = null;

	private HashSet<Date> redDates;

	private ArrayList<Integer>  dayArray =new ArrayList<>(), monthArray=new ArrayList<>();

	int mDay,mMonth,mYear;

	private int[][] count=new int[12][42];

	// internal components
	private LinearLayout header;
	private ImageView btnPrev;
	private ImageView btnNext;
	private TextView txtDate;
	private GridView grid;
    private Context context;
    private DBHelper dbHelper;

	// seasons' rainbow
	int[] rainbow = new int[] {
			R.color.winter/*,
			R.color.fall,
			R.color.winter,
			R.color.spring*/
	};

	// month-season association (northern hemisphere, sorry australia :)
	int[] monthSeason = new int[] {2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

	private int image=0;

	public CalendarView(Context context)
	{
        super(context);
        //super(context);
        this.context=context;
        this.dbHelper = new DBHelper(context);
	}

	public CalendarView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initControl(context, attrs);
		Calendar c = Calendar.getInstance();
		mDay= c.get(Calendar.DATE);
		mMonth= c.get(Calendar.MONTH);
		mYear= c.get(Calendar.YEAR);

	}

	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initControl(context, attrs);
	}

	/**
	 * Load control xml layout
	 */
	private void initControl(Context context, AttributeSet attrs)
	{
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.control_calendar, this);

		loadDateFormat(attrs);
		assignUiElements();
		assignClickHandlers();

		updateCalendar(null);
	}

	private void loadDateFormat(AttributeSet attrs)
	{
		TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

		try
		{
			// try to load provided date format, and fallback to default otherwise
			dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
			if (dateFormat == null)
				dateFormat = DATE_FORMAT;
		}
		finally
		{
			ta.recycle();
		}
	}
	private void assignUiElements()
	{
		// layout is inflated, assign local variables to components
		header = findViewById(R.id.calendar_header);
		btnPrev = findViewById(R.id.calendar_prev_button);
		btnNext = findViewById(R.id.calendar_next_button);
		txtDate = findViewById(R.id.calendar_date_display);
		grid = findViewById(R.id.calendar_grid);

		btnPrev.setVisibility(GONE);
	}

	private void assignClickHandlers()
	{
		// add one month and refresh UI
		btnNext.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mMonth=mMonth+1;
				if(mMonth==12){
					mMonth=0;
					mYear=mYear+1;
				}
				else{

					int j=0;
				}

				currentDate.add(Calendar.MONTH, 1);

                btnPrev.setVisibility(VISIBLE);
                btnNext.setVisibility(GONE);

                eventHandler.onNextClicked();

				//updateCalendar(getDatesFromDB());
			}
		});

		// subtract one month and refresh UI
		btnPrev.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mMonth=mMonth-1;
				if(mMonth==-1){
					mMonth=11;
					mYear=mYear-1;
				}
				currentDate.add(Calendar.MONTH, -1);

                btnPrev.setVisibility(GONE);
                btnNext.setVisibility(VISIBLE);

				eventHandler.onPreviousClicked();
				//updateCalendar(getDatesFromDB());
			}
		});

		// long-pressing a day
		/*grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id)
			{
				// handle long-press
				if (eventHandler == null)
					return false;

				eventHandler.onDayLongPress((Date) view.getItemAtPosition(position));
				return true;
			}
		});*/

		grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                TextView text=view.findViewById(R.id.grid_text);

				SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.UK);

				Calendar calendar = Calendar.getInstance();

				calendar.set(mYear,mMonth,Integer.parseInt(text.getText().toString()));


				eventHandler.onDayPress(format.format(calendar.getTime()),"");



               /* if(dayArray.contains(Integer.parseInt(day_clicked)) && monthArray.contains(mMonth)){
                    int k=0;
                }

                else {
                    if(Integer.parseInt(day_clicked)<currentDate.getTime().getDate()){
                        text.setEnabled(false);
                        text.setFocusable(false);
                    }
                    else{
                        switch (count[mMonth][i]){

                            case 0:

									if(month<10){
										date_clicked=mYear+"-"+"0"+month+"-"+day_clicked;
									}
									else{
										date_clicked=mYear+"-"+month+"-"+day_clicked;
									}

								try {
									Log.d("formatedDate",""+format.parse(date_clicked));
								} catch (ParseException e) {
									e.printStackTrace();
								}


										eventHandler.onDayPress(date_clicked,mMonth+String.valueOf(i));
										count[mMonth][i]++;



								break;


                            case 1:

									date_clicked=mYear+"-"+month+"-"+day_clicked;
									eventHandler.onDayRelease(date_clicked,mMonth+String.valueOf(i));
									count[mMonth][i]=0;
									break;


                        }
                    }

                }*/




				//}

			}
		});
	}

	private HashSet<Date> getDatesFromDB(){

	    dbHelper = new DBHelper(context);

		HashSet<Date> bDatesList  = new HashSet<>();
		ArrayList<String> datesFromDb=new ArrayList<>();

		List<List<String>> datesData = dbHelper.getTableColData(DBTables.ScheduleMeetings.TABLE_NAME
				,DBTables.ScheduleMeetings.MEETING_DATE+","+DBTables.ScheduleMeetings.GP_NAME);

		if(datesData.size()>0){

			for (int i = 0; i <datesData.size() ; i++) {

				try {
					bDatesList.add(new SimpleDateFormat("dd/MM/yyyy", Locale.UK).parse(datesData.get(i).get(0)));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				datesFromDb.add(datesData.get(i).get(0));

			}
		}

		return bDatesList;

	}


    /*
	 * Display dates correctly in grid
	 */
	/*public void updateCalendar()
	{
		updateCalendar(*//*null*//*);
	}*/

	/**
	 * Display dates correctly in grid
	 */
	public void updateCalendar(HashSet<Date> eventDates)
	{

		ArrayList<CustomDate> cells = new ArrayList<>();
		Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		//Log.d("DAY_OF_MONTH",String.valueOf(Calendar.DAY_OF_MONTH));

		int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

		//Log.d("monthBeginningCell",String.valueOf(monthBeginningCell));

		// move calendar backwards to the beginning of the week
		calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

		// fill cells
		while (cells.size() < DAYS_COUNT)
		{
			CustomDate cd=new CustomDate(calendar.get(Calendar.DATE),calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR));
			cd.setDate(calendar.get(Calendar.DATE));
			cd.setMonth(calendar.get(Calendar.MONTH));
			cd.setYear(calendar.get(Calendar.YEAR));
			cells.add(cd);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		// update grid
		grid.setAdapter(new CalendarAdapter(getContext(),cells,image,eventDates));
		// update title
		SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.UK);
		txtDate.setText(sdf.format(currentDate.getTime()));

		// set header color according to current season
		int month = currentDate.get(Calendar.MONTH);
		//int season = monthSeason[month];
		int color = rainbow[0];

		header.setBackgroundColor(getResources().getColor(color));

	}


	private class CalendarAdapter extends ArrayAdapter<CustomDate>
	{
		//days with events
		private HashSet<Date> eventDays;
		private int image;

		//for view inflation
		private LayoutInflater inflater;

		CalendarAdapter(Context context, ArrayList<CustomDate> days, int image,HashSet<Date> eventDays)
		{
			super(context, R.layout.control_calendar_day, days);
			this.image=image;
			this.eventDays = eventDays;
			redDates=eventDays;
			inflater = LayoutInflater.from(context);
		}

		@NonNull
		@Override
		public View getView(int position, View view, @NonNull ViewGroup parent)
		{

			CustomDate date=getItem(position);

			int day = date.getDate();
			int month = date.getMonth();
			int year = date.getYear();

			// inflate item if it does not exist yet
			if (view == null)
				view = inflater.inflate(R.layout.day_single, parent, false);

			TextView pday= view.findViewById(R.id.grid_text);
			TextView tvEvent= view.findViewById(R.id.text_event);
            tvEvent.setVisibility(GONE);

			//ImageView banner= view.findViewById(R.id.grid_image);

			// if this day has an event, specify event image
			pday.setBackgroundResource(0);
            pday.setTypeface(null, Typeface.NORMAL);
            pday.setTextColor(Color.BLACK);

			// clear styling


			if (month != mMonth )
			{
				pday.setVisibility(GONE);

			}else {

                if(day<mDay){
                    pday.setTextColor(getResources().getColor(R.color.greyed_out));
					pday.setText(String.valueOf(date.getDate()));

                }
                else{
                    pday.setVisibility(VISIBLE);
                    pday.setText(String.valueOf(date.getDate()));

                }

			}

			if (day == mDay && month== Calendar.getInstance().get(Calendar.MONTH))
			{

				pday.setTypeface(null, Typeface.BOLD);
				pday.setTextColor(getResources().getColor(R.color.today));


			}

            if (eventDays != null)
            {
                for (Date eventDate : eventDays)
                {

                    if (eventDate.getDate() == day &&
                            eventDate.getMonth() == month )
                    {
                        dayArray.add(eventDate.getDate());
                        monthArray.add(eventDate.getMonth());
                        pday.setBackgroundResource(R.drawable.bookedcolor);
                        pday.setTextColor(Color.parseColor("#ffffff"));
                        tvEvent.setVisibility(VISIBLE);
                        onEventExist.onEvent(eventDate,tvEvent);




                    }

                }
            }


			return view;
			//return null;
		}
	}

	/**
	 * Assign event handler to be passed needed events
	 */
	public void setEventHandler(EventHandler eventHandler,OnEventExist onEventExist)
	{
		this.eventHandler = eventHandler;
        this.onEventExist = onEventExist;
	}

	/**
	 * This interface defines what events to be reported to
	 * the outside world
	 */
	public interface EventHandler
	{
		void onDayPress(String date, String key);
		void onNextClicked();
		void onPreviousClicked();
	}

    public interface OnEventExist
    {
        void onEvent(Date date,TextView event);

    }


}
