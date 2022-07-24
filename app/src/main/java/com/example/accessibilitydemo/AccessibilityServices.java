//package com.example.accessibilitydemo;
//
//import android.accessibilityservice.AccessibilityService;
//import android.accessibilityservice.AccessibilityServiceInfo;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.accessibility.AccessibilityEvent;
//import android.view.accessibility.AccessibilityNodeInfo;
//import android.view.accessibility.AccessibilityWindowInfo;
//
//import com.iflytek.autofly.access.bean.ActionFactory;
//import com.iflytek.autofly.access.presenter.ActionPresenter;
//import com.iflytek.autofly.access.view.IActionView;
//import com.iflytek.autofly.util.log.Logging;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class AccessibilityServices extends AccessibilityService implements IActionView {
//    private static final String TAG = "AccessibilityServices";
//
//    private final static int MSG_CONTENT_CHANGE = 1;
//
//    private final static int MSG_STATE_CHANGE = 2;
//
//    private final static int MSG_WINDOW_CHANGE = 3;
//
//    public static final String FILTER_OUT_ACTION = "NEED_FILTER_NOT_SET_VIEW_CMD";
//
//    /**
//     * Flag if the infrastructure is initialized.
//     */
//    private boolean isInfrastructureInitialized;
//
//    private Handler mHandler;
//
//    private int stateChangeTime = 60;
//    private int contentChangeTime = 500;
//    private int windowChangeTime = 60;
//
//    private final List<AccessibilityNodeInfo> accessibilityNodeInfoList = new ArrayList<>();
//
//
//    @Override
//    public AccessibilityNodeInfo getCurrentRootNodeInfo() {
//        AccessibilityNodeInfo rootInActiveWindow = null;
//        try {
//            rootInActiveWindow = getRootInActiveWindow();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return rootInActiveWindow;
//    }
//
//    @Override
//    public List<AccessibilityNodeInfo> getVisibleNodeInfo() {
//        accessibilityNodeInfoList.clear();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            List<AccessibilityWindowInfo> accessibilityWindowInfoList = getWindows();
//            for (int i = 0; i < accessibilityWindowInfoList.size(); i++) {
//                accessibilityNodeInfoList.add(accessibilityWindowInfoList.get(i).getRoot());
//            }
//            return accessibilityNodeInfoList;
//        }
//        return null;
//    }
//
//    @Override
//    public AccessibilityService getAccessibilityService() {
//        return this;
//    }
//
//    @Override
//    public void setAccessbilityHandlerTime(int stateChangeTime, int contentChangeTime, int windowChangeTime) {
//        setStateChangeTime(stateChangeTime);
//        setContentChangeTime(stateChangeTime);
//        setWindowChangeTime(stateChangeTime);
//    }
//
//
//    @Override
//    protected void onServiceConnected() {
//        Logging.d(TAG, "onServiceConnected() called");
//        if (isInfrastructureInitialized) {
//            return;
//        }
//
//        ActionFactory.getInstance().init(this);
//
//        //TODO 第三参数需要确认数据来源
//        ActionPresenter.getInstance().init(this, this);
//
//        setServiceInfo();
//
//        // We are in an initialized state now.
//        isInfrastructureInitialized = true;
//        HandlerThread handlerThread = new HandlerThread("access_service");
//        handlerThread.start();
//        mHandler = new Handler(handlerThread.getLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if(ActionPresenter.getInstance().getmActionModel() == null){
//                    //无障碍服务sdk未初始化直接退出
//                    return;
//                }
//                switch (msg.what) {
//                    case MSG_CONTENT_CHANGE:
//                        ActionPresenter.getInstance().updateWindow();
//                        ActionPresenter.getInstance().onWindowStateChanged(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP);
//                        break;
//                    case MSG_STATE_CHANGE:
//                    case MSG_WINDOW_CHANGE:
//                        Bundle stateChangeBuild = msg.getData();
//                        if(stateChangeBuild != null){
//                            String pkg = (String) msg.getData().getCharSequence("pkg");
//                            String clazz = (String) msg.getData().getCharSequence("clazz");
//                            ActionPresenter.getInstance().updateBasicWindow(pkg,clazz);
//                        }
//                        ActionPresenter.getInstance().updateWindow();
//                        ActionPresenter.getInstance().onWindowStateChanged(true);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//    }
//
//    private void setServiceInfo() {
//        AccessibilityServiceInfo info = getServiceInfo();
//        if (info == null) {
//            info = new AccessibilityServiceInfo();
//        }
//        // We are interested in all types of accessibility events.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
//                    | AccessibilityEvent.TYPE_WINDOWS_CHANGED;
//        } else {
//            info.eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
//        }
////		info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        // We want to provide specific type of feedback.
////        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_HAPTIC
////				| AccessibilityServiceInfo.FEEDBACK_SPOKEN
////				| AccessibilityServiceInfo.FEEDBACK_AUDIBLE;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
//        // We want to receive events in a certain interval.
//        info.notificationTimeout = 600;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
//        }
//        // We want to receive accessibility events only from certain packages.
////        info.packageNames = new String[]{};
//        setServiceInfo(info);
//        Logging.d(TAG, "setServiceInfo");
//
//    }
//
//
//    @Override
//    public void onDestroy() {
//        Logging.d(TAG, "onDestroy");
//        super.onDestroy();
//    }
//
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        if (isInfrastructureInitialized) {
//
//            // We are not in an initialized state anymore.
//            isInfrastructureInitialized = false;
//        }
//        return false;
//    }
//
//    @Override
//    public void onAccessibilityEvent(final AccessibilityEvent event) {
//        //Logging.d(TAG, "onAccessibilityEvent event:" + event.toString());
//        int type = event.getEventType();
//        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == type
//                || AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == type
//                || AccessibilityEvent.TYPE_WINDOWS_CHANGED == type) {
//            mHandler.removeMessages(MSG_CONTENT_CHANGE);
//            if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == type) {
//                mHandler.removeMessages(MSG_STATE_CHANGE);
//                mHandler.removeMessages(MSG_WINDOW_CHANGE);
//                CharSequence pkg = event.getPackageName();
//                CharSequence clazz = event.getClassName();
//                Message message = Message.obtain();
//                Bundle bundle = new Bundle();
//                bundle.putCharSequence("pkg",pkg);
//                bundle.putCharSequence("clazz",clazz);
//                message.setData(bundle);
//                message.what = MSG_STATE_CHANGE;
//                mHandler.sendMessageDelayed(message,stateChangeTime); //增加60ms延迟，降低winddow和state连续触发率
//            } else if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == type) {
//                mHandler.sendEmptyMessageDelayed(MSG_CONTENT_CHANGE,contentChangeTime); //500 延迟500降低触发率
//            } else {
//                mHandler.removeMessages(MSG_STATE_CHANGE);
//                mHandler.removeMessages(MSG_WINDOW_CHANGE);
//                AccessibilityNodeInfo info = getRootInActiveWindow();
//                Message message = Message.obtain();
//                Bundle bundle = null;
//                if (info != null){
//                    CharSequence pkg = info.getPackageName();
//                    if (!TextUtils.isEmpty(pkg)){
//                        bundle = new Bundle();
//                        bundle.putCharSequence("pkg",pkg);
//                        bundle.putCharSequence("clazz","");
//                    }
//                }
//                message.setData(bundle);
//                message.what = MSG_WINDOW_CHANGE;
//                mHandler.sendMessageDelayed(message, windowChangeTime); //增加60ms延迟，降低winddow和state连续触发率
//            }
//        }
//    }
//
//    public void setStateChangeTime(int stateChangeTime) {
//        this.stateChangeTime = stateChangeTime;
//    }
//
//    public void setContentChangeTime(int contentChangeTime) {
//        this.contentChangeTime = contentChangeTime;
//    }
//
//    public void setWindowChangeTime(int windowChangeTime) {
//        this.windowChangeTime = windowChangeTime;
//    }
//
//    @Override
//    public void onInterrupt() {
//        Logging.d(TAG, "onInterrupt");
//    }
//
//}
