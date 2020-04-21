package com.otc.keystool.pax.app;

import android.app.Application;
import android.util.Log;

import com.pax.dal.IDAL;
import com.pax.neptunelite.api.NeptuneLiteUser;


public class TradeApplication extends Application {

    private static final String TAG = "TradeApplication";
    private static TradeApplication tradeApplication;
    public final static String APP_VERSION = "V1.00.03_20171027";


    public static final int INDEX_TMK = 2;
    public static final int INDEX_TDK = 1;
    public static final int INDEX_TDK_PIN = 5;
    public static final int INDEX_TPK_PIN = 2;

    private static IDAL dal;
    //public static IGL gl;
    private static IConvert convert;

    private boolean isDalEnabled = true;

    public static IDAL getDal() {
        return dal;
    }

    public static IConvert getConvert() {
        return convert;
    }

    public boolean isDalEnabled() {
        return isDalEnabled;
    }

    public void setDalEnabled(boolean dalEnabled) {
        isDalEnabled = dalEnabled;
    }

    public static TradeApplication getInstance() {
        return tradeApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TradeApplication.tradeApplication = this;
        init();
    }

    private void init() {
        NeptuneLiteUser neptuneLiteUser = NeptuneLiteUser.getInstance();
        try {
            if (dal == null) {
                dal = neptuneLiteUser.getDal(this.getApplicationContext());
                Log.i("FinancialApplication:", "dalProxyClient finished.");
            }
        } catch (Exception e) {
            Log.e("dalProxyClient", e.getMessage());
        }
        convert = new ConverterImp();
    }

    static {
        System.loadLibrary("F_DEVICE_LIB_PayDroid");
        System.loadLibrary("F_PUBLIC_LIB_PayDroid");
        System.loadLibrary("F_EMV_LIB_PayDroid");
        System.loadLibrary("F_ENTRY_LIB_PayDroid");
        System.loadLibrary("F_MC_LIB_PayDroid");
        System.loadLibrary("F_WAVE_LIB_PayDroid");
        System.loadLibrary("F_AE_LIB_PayDroid");
        System.loadLibrary("F_QPBOC_LIB_PayDroid");
        System.loadLibrary("F_DPAS_LIB_PayDroid");
        System.loadLibrary("F_JCB_LIB_PayDroid");
        System.loadLibrary("F_PURE_LIB_PayDroid");
        System.loadLibrary("JNI_EMV_v101");
        System.loadLibrary("JNI_ENTRY_v103");
        System.loadLibrary("JNI_MC_v100");
        System.loadLibrary("JNI_WAVE_v100");
        System.loadLibrary("JNI_AE_v101");
        System.loadLibrary("JNI_QPBOC_v100");
        System.loadLibrary("JNI_DPAS_v100");
        System.loadLibrary("JNI_JCB_v100");
        System.loadLibrary("JNI_PURE_v100");
    }

}
