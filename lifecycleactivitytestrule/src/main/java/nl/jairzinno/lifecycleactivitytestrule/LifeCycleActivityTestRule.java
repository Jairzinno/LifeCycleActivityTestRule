package nl.jairzinno.lifecycleactivitytestrule;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LifeCycleActivityTestRule<T extends AppCompatActivity> extends ActivityTestRule<T> {

    private Object testCase;

    public LifeCycleActivityTestRule(Object testCase, Class<T> activityClass) {
        super(activityClass);
        this.testCase = testCase;
    }

    public LifeCycleActivityTestRule(Object testCase, Class<T> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode);
        this.testCase = testCase;
    }

    public LifeCycleActivityTestRule(Object testCase, Class<T> activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
        this.testCase = testCase;
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback((activity, stage) -> {
            T sourceActivity = (T) activity;
            for (Method method : testCase.getClass().getDeclaredMethods()) {
                switch (stage) {
                    case PRE_ON_CREATE:
                        invokeMethod(method, sourceActivity, PreOnCreate.class);
                        break;
                    case CREATED:
                        invokeMethod(method, sourceActivity, Created.class);
                        break;
                    case STARTED:
                        invokeMethod(method, sourceActivity, Started.class);
                        break;
                    case RESUMED:
                        invokeMethod(method, sourceActivity, Resumed.class);
                        break;
                    case PAUSED:
                        invokeMethod(method, sourceActivity, Paused.class);
                        break;
                    case STOPPED:
                        invokeMethod(method, sourceActivity, Stopped.class);
                        break;
                    case RESTARTED:
                        invokeMethod(method, sourceActivity, Restarted.class);
                        break;
                    case DESTROYED:
                        invokeMethod(method, sourceActivity, Destroyed.class);
                        break;
                }
            }
        });
    }

    private void invokeMethod(Method method, T sourceActivity, Class stageClass) {
        if (method.isAnnotationPresent(stageClass)) {
            try {
                method.invoke(testCase, sourceActivity);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
