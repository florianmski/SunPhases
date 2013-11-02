package com.florianmski.SunPhases.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import com.florianmski.SunPhases.*;
import com.florianmski.SunPhases.utils.Palette;
import com.florianmski.SunPhases.utils.SensorManager;
import com.florianmski.SunPhases.utils.SunPhaseManager;
import com.florianmski.SunPhases.utils.SunPhasesPreferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SunMapFragment extends SupportMapFragment implements SunPhaseManager.OnTimeChangedListener, SensorManager.OnOrientationChangedListener
{
    private Marker markerUser;
    private Marker markerSun;
    private Polyline sunLine;

    private final SunPhaseManager spm = SunPhaseManager.INSTANCE;

    private boolean firstPositionUpdate = true;

    private SeekBar sb;

    public static SunMapFragment newInstance()
    {
        return new SunMapFragment();
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

        getMap().setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener()
        {
            @Override
            public void onMyLocationChange(Location location)
            {
                if(!SunPhasesPreferences.INSTANCE.doWeHaveALocationBitch())
                {
                    updateUserPosition(location.getLatitude(), location.getLongitude());
                    saveUserPosition();
                    updateCameraPosition(location.getLatitude(), location.getLongitude());
                }
            }
        });

        getMap().setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener()
        {
            @Override
            public boolean onMyLocationButtonClick()
            {
                Location location = getMap().getMyLocation();
                if(location != null)
                {
                    updateUserPosition(location.getLatitude(), location.getLongitude());
                    saveUserPosition();
                }
                return false;
            }
        });

        getMap().setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
        {
            @Override
            public void onMarkerDragStart(Marker marker) {}

            @Override
            public void onMarkerDrag(Marker marker)
            {
                if(marker.equals(markerUser))
                    updateSunPosition();
            }

            @Override
            public void onMarkerDragEnd(Marker marker)
            {
                if(marker.equals(markerUser))
                    saveUserPosition();
            }
        });

        getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {
                updateUserPosition(latLng.latitude, latLng.longitude);
                saveUserPosition();
            }
        });

        getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener()
        {
            @Override
            public void onCameraChange(CameraPosition cameraPosition)
            {
                updateSunPosition();
            }
        });

        if(SunPhasesPreferences.INSTANCE.doWeHaveALocationBitch())
        {
            updateUserPosition(spm.getLatitude(), spm.getLongitude());
            updateCameraPosition(spm.getLatitude(), spm.getLongitude());
        }

        getMap().setMyLocationEnabled(SunPhasesPreferences.INSTANCE.isLocationEnabled());
        getMap().setMapType(SunPhasesPreferences.INSTANCE.getMapType());

        sb.setMax((24 * 60 * 60 * 1000)-2); // -2 so we still able to find the last sunphase
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(fromUser)
                {
                    Calendar c = spm.getDate();
                    int millis  = progress % 1000;
                    int seconds = (progress / 1000) % 60;
                    int minutes = (progress / (1000*60)) % 60;
                    int hours   = (progress / (1000*60*60)) % 24;

                    c.set(Calendar.HOUR_OF_DAY, hours);
                    c.set(Calendar.MINUTE, minutes);
                    c.set(Calendar.SECOND, seconds);
                    c.set(Calendar.MILLISECOND, millis);
                    spm.setDate(c);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Warning, this is an awful hacky stuff
        // I did that because I wanted to be sure that the progress bar will be at the left of the location button
        // This can potentially break if the google map lib is updated
        ViewGroup vg1 = (ViewGroup)view; // only one view there
        ViewGroup vg2 = (ViewGroup)vg1.getChildAt(0); // only one view there
        ViewGroup vg3 = (ViewGroup)vg2.getChildAt(0); // two views there

        // second one seems to be what we're looking for: ui controls
        RelativeLayout uiSettings = ((RelativeLayout)vg3.getChildAt(1));

//        uiSettings.getChildAt(0) // location button, id = 2
//        uiSettings.getChildAt(1) // don't know what it is, id = 3
//        uiSettings.getChildAt(2) // zoom buttons, id = 1
        // where's the compass ????

        // stupid seekbar bug http://stackoverflow.com/questions/7404100/how-to-fix-seekbar-bar-thumb-centering-issues
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sb = (SeekBar) inflater.inflate(R.layout.seek_bar, null);

        View btnZoom = uiSettings.getChildAt(2);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.LEFT_OF, btnZoom.getId());
        lp.addRule(RelativeLayout.ALIGN_TOP, btnZoom.getId());
        lp.addRule(RelativeLayout.ALIGN_BOTTOM, btnZoom.getId());

        int btnLocationRightMargin = ((RelativeLayout.LayoutParams)btnZoom.getLayoutParams()).rightMargin;
        lp.setMargins(btnLocationRightMargin, 0, btnLocationRightMargin, 0);
        sb.setLayoutParams(lp);

        uiSettings.addView(sb);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        menu.add(Menu.NONE, R.id.menu_coordinates, Menu.NONE, "Coordinates").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        menu.add(Menu.NONE, R.id.menu_options, Menu.NONE, "Options").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.menu_coordinates:
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View v = inflater.inflate(R.layout.dialog_coordinates, null);
                final EditText edtLatitude = (EditText) v.findViewById(R.id.editTextLatitude);
                final EditText edtLongitude = (EditText) v.findViewById(R.id.editTextLongitude);

                builder.setView(v)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                try
                                {
                                    double latitude = Double.valueOf(edtLatitude.getText().toString());
                                    double longitude = Double.valueOf(edtLongitude.getText().toString());

                                    updateUserPosition(latitude, longitude);
                                    saveUserPosition();
                                    updateCameraPosition(latitude, longitude);
                                }
                                catch(NumberFormatException ignored) {}
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id) {}
                        });

                final AlertDialog d = builder.create();

                edtLongitude.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE)
                            d.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                        return true;
                    }
                });

                d.show();
            }
            return true;
            case R.id.menu_options:
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                LayoutInflater inflater = getActivity().getLayoutInflater();
                View v = inflater.inflate(R.layout.dialog_options, null);
                final Spinner spMapType = (Spinner) v.findViewById(R.id.spinnerMapType);
                final CheckBox cbLocation = (CheckBox) v.findViewById(R.id.checkBoxLocation);

                String[] mapTypes = new String[]{"Normal", "Satellite", "Terrain", "Hybrid"};
                spMapType.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mapTypes));
                spMapType.setSelection(SunPhasesPreferences.INSTANCE.getMapType()-1);
                spMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                    {
                        int mapType = position+1;
                        if(mapType != getMap().getMapType())
                        {
                            SunPhasesPreferences.INSTANCE.putMapType(mapType);
                            getMap().setMapType(mapType);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                cbLocation.setChecked(SunPhasesPreferences.INSTANCE.isLocationEnabled());
                cbLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        SunPhasesPreferences.INSTANCE.putLocationEnabled(isChecked);
                        getMap().setMyLocationEnabled(isChecked);
                    }
                });

                builder.setView(v)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {}
                        });

                builder.create().show();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveUserPosition()
    {
        if(markerUser != null)
            spm.setCoordinates(markerUser.getPosition());
    }

    private void updateUserPosition(double lat, double lon)
    {
        LatLng latLng = new LatLng(lat,lon);

        if(markerUser == null)
        {
            markerUser = getMap().addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                    .anchor(0.5f, 0.875f)
                    .draggable(true));
        }
        else
            markerUser.setPosition(latLng);

        updateSunPosition();
    }

    private void updateSunPosition()
    {
        if(markerUser == null)
            return;

        LatLng latLng = offsetPosition(markerUser.getPosition(), optimalRadius(), spm.getCurrentPosition().getAzimuth());

        if(markerSun == null)
            markerSun = getMap().addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.sun))
                    .anchor(0.5f, 0.5f)
                    .draggable(false));
        else
            markerSun.setPosition(optimalSunPosition(latLng));

        updateSunLine();
        updateProgressBar();
    }

    private void updateSunLine()
    {
        if(sunLine == null)
        {
            sunLine = getMap().addPolyline(new PolylineOptions()
                    .width(7));
        }

        List<LatLng> points = new ArrayList<LatLng>();
        points.add(markerUser.getPosition());
        points.add(markerSun.getPosition());
        sunLine.setPoints(points);
        sunLine.setColor(Palette.d().get(spm.getCurrentPhase().getName()));
    }

    private void updateProgressBar()
    {
        Calendar c = spm.getDate();
        sb.setProgress( c.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000 +
                c.get(Calendar.MINUTE) * 60 * 1000 +
                c.get(Calendar.SECOND) * 1000 +
                c.get(Calendar.MILLISECOND));
    }

    private void updateCameraPosition(double lat, double lon)
    {
        LatLng latLng = new LatLng(lat,lon);

        // for the first markerUser we have using gps or user input, zoomIn
        // then let the user decide
        if(firstPositionUpdate)
        {
            getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            firstPositionUpdate = false;
        }
        else
            getMap().animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    // http://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
    // offset a postion given a position, a distance (in meter) and an angle
    private LatLng offsetPosition(LatLng latLng, double radius, double angle)
    {
        double lat = latLng.latitude;
        double lon = latLng.longitude;

        //Earth’s radius, sphere
        double R = 6378137;

        //offsets in meters
        double dn = radius * Math.cos(angle);
        double de = radius * Math.sin(angle);

        //Coordinate offsets in radians
        double dLat = dn/R;
        double dLon = de/(R*Math.cos(Math.PI * lat / 180f));

        //OffsetPosition, decimal degrees
        double latO = lat + dLat * 180f/Math.PI;
        double lonO = lon + dLon * 180f/Math.PI;

        return new LatLng(latO, lonO);
    }

    // optimal radius with the current projection boundaries
    private float optimalRadius()
    {
        LatLng sw = getMap().getProjection().getVisibleRegion().latLngBounds.southwest;
        LatLng ne = getMap().getProjection().getVisibleRegion().latLngBounds.northeast;

        Location left = new Location("");
        left.setLatitude(sw.latitude);
        left.setLongitude(sw.longitude);

        Location right = new Location("");
        right.setLatitude(ne.latitude);
        right.setLongitude(ne.longitude);

        return left.distanceTo(right)/2;
    }

    // return the optimal relative sun position on the map
    // the sun will be as close as possible of the map edges (minus the chosen padding)
    private LatLng optimalSunPosition(LatLng sunPosition)
    {
        // padding calculation
        LatLng sw = getMap().getProjection().getVisibleRegion().latLngBounds.southwest;
        LatLng ne = getMap().getProjection().getVisibleRegion().latLngBounds.northeast;
        double padding = (sunPosition.longitude < sunPosition.latitude ? ne.longitude - sw.longitude : sw.latitude - ne.latitude) / 10;

        // find the intersection between the map projection boundaries and the sun line
        // The Liang-Barsky line clipping algorithm
        // based on http://www.skytopia.com/project/articles/compsci/clipping.html
        double x = markerUser.getPosition().longitude;
        double y = markerUser.getPosition().latitude;

        double vx = sunPosition.longitude - x;
        double vy = sunPosition.latitude - y;

        LatLngBounds bounds = getMap().getProjection().getVisibleRegion().latLngBounds;
        double left = bounds.southwest.longitude + padding;
        double right = bounds.northeast.longitude - padding;
        double bottom = bounds.southwest.latitude + padding;
        double top = bounds.northeast.latitude - padding;

        double t0 = 0.0;
        double t1 = 1.0;
        double[] p = new double[]{-vx, vx, -vy, vy};
        double[] q = new double[]{x - left, right - x, y - bottom, top - y};

        for (int i = 0; i < 4; i++)
        {
            double r = q[i] / p[i];
            if (p[i] < 0 && r > t0 && r <= t1)
                t0 = r;
            else if (p[i] > 0 && r < t1 && r >= t0)
                t1 = r;
        }

        return new LatLng(y + t1*vy, x + t1*vx);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        spm.addOnTimeChangedListener(this);
        SensorManager.INSTANCE.addOnOrientationChangedListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        spm.removeOnTimeChangedListener(this);
        SensorManager.INSTANCE.removeOnOrientationChangedListener(this);
    }

    @Override
    public void onTimeChanged(Calendar c, boolean sameDay)
    {
        updateSunPosition();
    }

    @Override
    public void onOrientationChangedListener(float[] fusedOrientation)
    {
        float bearing = (float) (fusedOrientation[0] * 180 / Math.PI);
        if(markerUser != null)
            markerUser.setRotation(bearing);
    }
}
