package co.dad.convalesensemanager;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by adrienviolet on 27/01/2017.
 */

public class ConvalesenseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/LakkiReddy-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
