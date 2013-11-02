package com.florianmski.SunPhases.ui.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.florianmski.SunPhases.utils.SunPhaseManager;
import com.florianmski.SunPhases.adapters.ListSunPhasesAdapter;
import com.florianmski.SunPhases.ui.activities.HomeActivity;
import com.florianmski.suncalc.models.SunPhase;

import java.util.Calendar;

public class SunListFragment extends BaseFragment implements SunPhaseManager.OnLocationChangedListener, SunPhaseManager.OnTimeChangedListener
{
    private ListView lv;
    private ListSunPhasesAdapter adapter;

    public static SunListFragment newInstance()
    {
        return new SunListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        lv.setDivider(null);
        lv.setAdapter(adapter = new ListSunPhasesAdapter(getActivity()));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SunPhaseManager.INSTANCE.setDate(((SunPhase) (lv.getAdapter()).getItem(position)).getStartDate());
                ((HomeActivity)getActivity()).getSlidingPaneLayout().closePane();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return lv = new ListView(getActivity());
    }

    @Override
    public void onResume()
    {
        super.onResume();
        SunPhaseManager.INSTANCE.addOnLocationChangedListener(this);
        SunPhaseManager.INSTANCE.addOnTimeChangedListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        SunPhaseManager.INSTANCE.removeOnLocationChangedListener(this);
        SunPhaseManager.INSTANCE.removeOnTimeChangedListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(double latitude, double longitude)
    {
        adapter.refresh();
    }

    @Override
    public void onTimeChanged(Calendar c, boolean sameDay)
    {
        if(!sameDay)
            adapter.refresh();
    }
}
