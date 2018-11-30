package knowldege.app.tin.com.tinlibrary;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


public class BaseApplicatipon extends Application{

    private static final String TAG = BaseApplicatipon.class.getSimpleName();


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);

    }


}
