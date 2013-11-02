package com.florianmski.SunPhases.utils;

import android.graphics.Color;
import com.florianmski.suncalc.models.SunPhase;

import java.util.HashMap;
import java.util.Map;

public class Palette
{
    public enum Name
    {
        UTOPIA,
        WEEKEND_FUN,
        LAZY_SUNDAY,
        NIGHT_TIME_CLOUD,
        NOSTALGIC_MEMORIES,
        AFRIKA,
        AS_THE_SUN_GOES_DOWN,
        RAY_OF_LIGHT
    }

    private final Map<SunPhase.Name, Integer> colorMap = new HashMap<SunPhase.Name, Integer>();
    private static final Map<SunPhase.Name, Integer> intensityMap = new HashMap<SunPhase.Name, Integer>();
    private static final Map<Palette.Name, Palette> paletteMap = new HashMap<Palette.Name, Palette>();

    static
    {
        init();
    }

    // basic algorithm to sort colors by their saturation
    private Palette(String[] colors)
    {
        float[][] hsv = new float[colors.length][3];
        for(int i = 0; i < colors.length; i++)
        {
            int c = Color.parseColor(colors[i]);
            Color.RGBToHSV(Color.red(c), Color.green(c), Color.blue(c), hsv[i]);
        }

        float[][] hsvSorted = hsv;
        for(int i = 0; i < hsvSorted.length; i++)
        {
            for(int j = 0; j < hsvSorted.length; j++)
            {
//                if((hsvSorted[j][1] + hsvSorted[j][2])/2 < (hsv[i][1] + hsv[i][2])/2)
                if(hsvSorted[j][2] < hsv[i][2])
                {
                    float[] temp = hsv[i];
                    hsvSorted[i] = hsvSorted[j];
                    hsvSorted[j] = temp;
                }
            }
        }

        for(int i = 0; i < hsvSorted.length; i++)
        {
            float[] hsvColor = hsvSorted[i];
            for(Map.Entry<SunPhase.Name, Integer> entry : intensityMap.entrySet())
            {
                if(entry.getValue() == i)
                    colorMap.put(entry.getKey(), Color.HSVToColor(hsvColor));
            }
        }
    }

    private static void init()
    {
        // the higher, the darker
        intensityMap.put(SunPhase.Name.DAYLIGHT, 0);

        intensityMap.put(SunPhase.Name.GOLDEN_HOUR_EVENING, 1);
        intensityMap.put(SunPhase.Name.GOLDEN_HOUR_MORNING, 1);

        intensityMap.put(SunPhase.Name.SUNRISE, 2);
        intensityMap.put(SunPhase.Name.SUNSET, 2);

        intensityMap.put(SunPhase.Name.TWILIGHT_ASTRONOMICAL_EVENING, 3);
        intensityMap.put(SunPhase.Name.TWILIGHT_ASTRONOMICAL_MORNING, 3);
        intensityMap.put(SunPhase.Name.TWILIGHT_CIVIL_EVENING, 3);
        intensityMap.put(SunPhase.Name.TWILIGHT_CIVIL_MORNING, 3);
        intensityMap.put(SunPhase.Name.TWILIGHT_NAUTICAL_EVENING, 3);
        intensityMap.put(SunPhase.Name.TWILIGHT_NAUTICAL_MORNING, 3);

        intensityMap.put(SunPhase.Name.NIGHT_EVENING, 4);
        intensityMap.put(SunPhase.Name.NIGHT_MORNING, 4);

        create(Name.WEEKEND_FUN,          new String[]{"#FFF3A6","#F7E98B","#D9CA68","#756E8A","#4E495C"});
        create(Name.UTOPIA,               new String[]{"#FF4203","#EB7A34","#CDF081","#105B6E","#25383D"});
        create(Name.LAZY_SUNDAY,          new String[]{"#565A96","#6E7196","#D9B368","#D9C08F","#D9CAAD"});
        create(Name.NIGHT_TIME_CLOUD,     new String[]{"#CEC5FE","#8EA5FC","#728EE1","#7675E2","#2E3DB3"});
        create(Name.NOSTALGIC_MEMORIES,   new String[]{"#DDDF96","#B3D3A4","#84B6B9","#6B7B93","#614564"});
        create(Name.AFRIKA,               new String[]{"#C9B849","#C96823","#BE3100","#6F0B00","#241714"});
        create(Name.AS_THE_SUN_GOES_DOWN, new String[]{"#DED286","#F69A71","#F76860","#7B4046","#273540"});
        create(Name.RAY_OF_LIGHT,         new String[]{"#F8A21C","#EC9261","#D5827C","#B08292","#8C849E"});
    }

    private static void create(Palette.Name name, String[] colors)
    {
        paletteMap.put(name, new Palette(colors));
    }

    public static Palette make(Name name)
    {
        return paletteMap.get(name);
    }

    public static Palette d()
    {
        return paletteMap.get(Name.RAY_OF_LIGHT);
    }

    public int get(SunPhase.Name name)
    {
        return colorMap.get(name);
    }
}
