package com.florianmski.SunPhases.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.florianmski.SunPhases.R;
import com.florianmski.SunPhases.ui.views.TimeSeparatorView;
import com.florianmski.SunPhases.utils.Palette;
import com.florianmski.SunPhases.utils.SunPhaseManager;
import com.florianmski.suncalc.models.SunPhase;

import java.util.List;

public class ListSunPhasesAdapter extends BaseAdapter
{
    private final Context context;
    private List<SunPhase> sunPhases;

    public ListSunPhasesAdapter(Context context)
    {
        this.context = context;
        refresh();
    }

    public void refresh()
    {
        this.sunPhases = SunPhaseManager.INSTANCE.getSunPhases();
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return sunPhases.size();
    }

    @Override
    public Object getItem(int i)
    {
        return sunPhases.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        final ViewHolder holder;

        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_sunphase, null);

            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.textView);
            holder.tsvTop = ((TimeSeparatorView) convertView.findViewById(R.id.timeSeparatorViewTop));
            holder.tsvBottom = ((TimeSeparatorView) convertView.findViewById(R.id.timeSeparatorViewBottom));
            holder.vColor = convertView.findViewById(R.id.viewColor);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        SunPhase sp = sunPhases.get(position);
        holder.tv.setText(sp.getName().toString());
        holder.tv.setTextColor(context.getResources().getColor(R.color.black_transparent));

        holder.tsvTop.setTime(sp.getStartDate());
        holder.tsvBottom.setTime(sp.getEndDate());

        holder.tsvTop.setVisibility(View.VISIBLE);
        holder.tsvBottom.setVisibility(View.VISIBLE);

        if(position == 0)
            holder.tsvTop.setVisibility(View.INVISIBLE);
        else if(position == getCount()-1)
            holder.tsvBottom.setVisibility(View.INVISIBLE);

        holder.vColor.setBackgroundColor(Palette.d().get(sp.getName()));

        // Testing animation stuff
//        animate(holder.tsvTop, holder.tsvBottom);

        return convertView;
    }

    private static class ViewHolder
    {
        public TextView tv;
        public TimeSeparatorView tsvTop;
        public TimeSeparatorView tsvBottom;
        public View vColor;
    }

    private void animate(final View tsvTop, final View tsvBottom)
    {
        ObjectAnimator animTranslateYTop = ObjectAnimator.ofFloat(tsvTop, View.TRANSLATION_Y, -tsvTop.getHeight(), 0.0f);
        ObjectAnimator animTranslateYBottom = ObjectAnimator.ofFloat(tsvBottom, View.TRANSLATION_Y, tsvBottom.getHeight(), 0.0f);
        ObjectAnimator animAlphaTop = ObjectAnimator.ofFloat(tsvTop, View.ALPHA, 0.0f, 1.0f);
        ObjectAnimator animAlphaBottom = ObjectAnimator.ofFloat(tsvBottom, View.ALPHA, 0.0f, 1.0f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(animTranslateYTop, animTranslateYBottom, animAlphaTop, animAlphaBottom);
        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                ViewCompat.setHasTransientState(tsvTop, false);
                ViewCompat.setHasTransientState(tsvBottom, false);
            }

            @Override
            public void onAnimationStart(Animator animation)
            {
                ViewCompat.setHasTransientState(tsvTop, true);
                ViewCompat.setHasTransientState(tsvBottom, true);
            }
        });
        set.start();
    }
}
