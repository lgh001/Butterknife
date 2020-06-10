package cn.lgh.butterknife;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lgh
 * @date 2020/6/9
 */
public class ButterKnife {

    private static final String SUFFIX = "$BindAdapterImp";

    static Map<Class, BindAdapter> mBindCache = new HashMap<>();

    public static void bind(Activity target) {
        BindAdapter bindAdapter = null;
        if (mBindCache.get(target) != null) {
            bindAdapter = mBindCache.get(target);
        } else {
            try {
                String adapterClzName = target.getClass().getName() + SUFFIX;
                Class<?> adapterClz = Class.forName(adapterClzName);
                bindAdapter = (BindAdapter) adapterClz.newInstance();
                mBindCache.put(adapterClz, bindAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bindAdapter != null) {
            bindAdapter.bind(target);
        }
    }

}
