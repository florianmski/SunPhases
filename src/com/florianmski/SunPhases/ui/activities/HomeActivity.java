package com.florianmski.SunPhases.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.text.format.DateFormat;
import android.view.*;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import com.florianmski.SunPhases.R;
import com.florianmski.SunPhases.ui.fragments.SunListFragment;
import com.florianmski.SunPhases.ui.fragments.SunMapFragment;
import com.florianmski.SunPhases.ui.views.EdgeSlidingPaneLayout;
import com.florianmski.SunPhases.utils.ActionBarUtils;
import com.florianmski.SunPhases.utils.Palette;
import com.florianmski.SunPhases.utils.SunPhaseManager;
import com.florianmski.suncalc.models.SunPhase;

import java.util.Calendar;

public class HomeActivity extends BaseActivity implements SunPhaseManager.OnTimeChangedListener, SunPhaseManager.OnLocationChangedListener
{
    private final SunPhaseManager spm = SunPhaseManager.INSTANCE;
    private SunPhase.Name latestPhaseName = null;

    private EdgeSlidingPaneLayout slidingPaneLayout;
    private TextView tvTime;
    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        getActionBar().setDisplayShowHomeEnabled(false);

        slidingPaneLayout = (EdgeSlidingPaneLayout) findViewById(R.id.slidingPaneLayout);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_list, SunListFragment.newInstance()).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map, SunMapFragment.newInstance()).commit();

        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener()
        {
            @Override
            public void onPanelSlide(View view, float v) {}

            @Override
            public void onPanelOpened(View view)
            {
                panelOpened();
            }

            @Override
            public void onPanelClosed(View view)
            {
                panelClosed();
            }
        });

        slidingPaneLayout.setParallaxDistance(100);
        slidingPaneLayout.setShadowResource(R.drawable.drawer_shadow);

        slidingPaneLayout.openPane();
        slidingPaneLayout.getViewTreeObserver().addOnGlobalLayoutListener(new FirstLayoutListener());
    }

    public SlidingPaneLayout getSlidingPaneLayout()
    {
        return slidingPaneLayout;
    }

    private void updateActionBarColor()
    {
        SunPhase.Name phaseName = spm.getCurrentPhase().getName();
        if(latestPhaseName == null || !latestPhaseName.equals(phaseName))
        {
            ActionBarUtils.setColor(Palette.d().get(phaseName), getActionBar());
            getActionBar().setTitle(phaseName.toString());
            latestPhaseName = phaseName;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        LinearLayout llTimeDate = (LinearLayout) LayoutInflater.from(getActionBar().getThemedContext()).inflate(R.layout.view_time_date, null);
        tvTime = (TextView) llTimeDate.findViewById(R.id.textViewTime);
        tvDate = (TextView) llTimeDate.findViewById(R.id.textViewDate);
        menu.add(Menu.NONE, R.id.menu_time, Menu.NONE, "Time")
                .setActionView(llTimeDate)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        llTimeDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);

                LinearLayout ll = new LinearLayout(HomeActivity.this);
                ll.setOrientation(LinearLayout.VERTICAL);
                Calendar c = SunPhaseManager.INSTANCE.getDate();
                final DatePicker dp = new DatePicker(HomeActivity.this);
                dp.setCalendarViewShown(false);
                dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
                final TimePicker tp = new TimePicker(HomeActivity.this);
                tp.setIs24HourView(DateFormat.is24HourFormat(HomeActivity.this));
                tp.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                tp.setCurrentMinute(c.get(Calendar.MINUTE));
                ll.addView(dp);
                ll.addView(tp);

                builder.setView(ll)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                Calendar c = Calendar.getInstance();
                                c.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
                                SunPhaseManager.INSTANCE.setDate(c);
                            }
                        })
                        .setNeutralButton("Now", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                SunPhaseManager.INSTANCE.setDate(Calendar.getInstance());
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id) {}
                        });

                final AlertDialog d = builder.create();
                d.show();
            }
        });

        // init the action view
        onTimeChanged(spm.getDate(), false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                if(!slidingPaneLayout.isOpen())
                    slidingPaneLayout.openPane();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        spm.addOnTimeChangedListener(this);
        spm.addOnLocationChangedListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        spm.removeOnTimeChangedListener(this);
        spm.removeOnLocationChangedListener(this);
    }

    @Override
    public void onTimeChanged(Calendar c, boolean sameDay)
    {
        tvTime.setText(DateFormat.getTimeFormat(this).format(c.getTime()));

        if(!sameDay)
            tvDate.setText(DateFormat.getDateFormat(this).format(c.getTime()));

        updateActionBarColor();
    }

    @Override
    public void onLocationChanged(double latitude, double longitude)
    {
        updateActionBarColor();
    }

    private void panelClosed()
    {
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        getSupportFragmentManager().findFragmentById(R.id.fragment_list).setHasOptionsMenu(false);
        getSupportFragmentManager().findFragmentById(R.id.fragment_map).setHasOptionsMenu(true);
    }

    private void panelOpened()
    {
        getActionBar().setHomeButtonEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        getSupportFragmentManager().findFragmentById(R.id.fragment_list).setHasOptionsMenu(true);
        // if both fragment are visible, we want to have both options menu
        getSupportFragmentManager().findFragmentById(R.id.fragment_map).setHasOptionsMenu(!slidingPaneLayout.isSlideable());
    }

    private class FirstLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener
    {
        @SuppressWarnings("deprecation")
        @Override
        public void onGlobalLayout()
        {
            if (slidingPaneLayout.isSlideable() && !slidingPaneLayout.isOpen())
                panelClosed();
            else
                panelOpened();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                slidingPaneLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            else
                slidingPaneLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        }
    }
}
