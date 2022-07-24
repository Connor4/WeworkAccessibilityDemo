//package com.example.accessibilitydemo;
//
//import android.content.Context;
//import android.content.Intent;
//import android.text.TextUtils;
//import android.view.accessibility.AccessibilityNodeInfo;
//
//import com.iflytek.autofly.access.bean.Action;
//import com.iflytek.autofly.access.bean.ActionFactory;
//import com.iflytek.autofly.access.bean.ActionType;
//import com.iflytek.autofly.access.custom.ICustomAccess;
//import com.iflytek.autofly.access.custom.OnSyncAddActionListener;
//import com.iflytek.autofly.access.presenter.ActionPresenter;
//import com.iflytek.autofly.access.settings.SystemSettings;
//import com.iflytek.autofly.access.utils.AccessibilityConstants;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class ActionModelImpl implements IActionModel, OnSyncAddActionListener {
//    private static final String TAG = "ActionModelImpl";
//    private static final String[] WHITE_WORD_REGEX_LIST = {
//            "[Dd][Jj]",
//            "4[Ss]店",
//            "[Aa][Tt][Mm]机?",
//            "[Kk][Tt][Vv]",
//            "[Rr]&[Bb]",
//            "[Uu][Ss][Bb]",
//            "[Ss][Dd]卡?",
//            "[FfAa][Mm]"
//    };
//    private final ArrayList<ICustomAccess> mAllCustomAccesses = new ArrayList<>();
//
//    //纵向滑动
//    private final String scrollUp = "往上滑";
//    private final String scrollDown = "往下滑";
//    private final String scrollDown2 = "再往下翻";
//    private final String scrollTop = "滑到最顶";
//    private final String scrollBottom = "滑到最底";
////
////    //横向滑动
//    private final String scrollLeft = "往左滑";
//    private final String scrollRight = "往右滑";
//    private final String scrollLeftSide = "翻到头";
//    private final String scrollRightSide = "翻到尾";
//    private final ActionPresenter mPresenter;
//    /**
//     * 根节点是不是scrollview
//     */
//    private boolean isScrollRoot = false;
//    private boolean isViewPager = false;
//    private boolean isRecycle = false;
//
//    public ActionModelImpl(ActionPresenter presenter, int actionViewArrayId, int packageFiliterArrayId) {
//        mPresenter = presenter;
//
//        String[] array = presenter.getBaseContext().getResources()
//                .getStringArray(actionViewArrayId);
//
//        String[] packageName = presenter.getBaseContext().getResources()
//                .getStringArray(packageFiliterArrayId);
//        List<String> mActionPackageFilter = Arrays.asList(packageName);
//        ActionFactory.getInstance().setmActionPackageFilter(mActionPackageFilter);
//    }
//
//    public static boolean isDynamic(String str) {
//        Pattern pattern = Pattern.compile("\\^.+\\^");
//        Matcher isNum = pattern.matcher(str);
//        return isNum.matches();
//    }
//
//    @Override
//    public List<String> getKeywords() {
//        return ActionFactory.getInstance().getKeywords();
//    }
//
//    @Override
//    public List<String> getViewCMD() {
//        return ActionFactory.getInstance().getViewCMD();
//    }
//
//    @Override
//    public boolean isCustomAccessAction(Action action) {
//        if (action == null || action.getRoot() == null) {
//            return false;
//        }
//
//        for (ICustomAccess custom : mAllCustomAccesses) {
//            if (custom.getPackageName().equals(action.getPackageName())
//                    || custom.getPackageName().contentEquals(action.getRoot().getPackageName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public ICustomAccess getCustomAccessByAction(Action action) {
//        if (action == null || action.getRoot() == null) {
//            return null;
//        }
//
//        for (ICustomAccess custom : mAllCustomAccesses) {
//            if (custom.getPackageName().equals(action.getPackageName())
//                    || custom.getPackageName().equals(action.getRoot().getPackageName())) {
//                return custom;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public void startActivity(Intent intent) {
//        mPresenter.startActivity(intent);
//    }
//
//    @Override
//    public Context getBaseContext() {
//        return mPresenter.getBaseContext();
//    }
//
//    @Override
//    public void syncAddAction(int type, ICustomAccess access) {
//        if (ActionFactory.TYPE_USER == type || ActionFactory.TYPE_APP == type) {
//            ActionFactory.getInstance().addUserActionList(access.getActions());
//        }
//        //剔除主动更新keywords逻辑
//    }
//
//    @Override
//    public void updateCurrentWindowAction() {
//        ArrayList<Action> allAction = new ArrayList<>();
//        isScrollRoot = false;
//        isViewPager = false;
//        isRecycle = false;
//        if (SystemSettings.I.getmSetting() != null && SystemSettings.I.getmSetting().isSupportAllVisibleWindow()) {
//            //开启扫描全部可视可交互界面配置后用此api
//            List<AccessibilityNodeInfo> accessibilityNodeInfoList = mPresenter.getCurrentVisibleWindow();
//            if (accessibilityNodeInfoList != null && accessibilityNodeInfoList.size() > 0) {
//                for (int i = 0; i < accessibilityNodeInfoList.size(); i++) {
//                    getAllActionOfNode(accessibilityNodeInfoList.get(i), allAction);
//                }
//            } else {
//                AccessibilityNodeInfo window = mPresenter.getCurrentWindow();
//                getAllActionOfNode(window, allAction);
//            }
//        } else {
//            //关闭扫描全部可视可交互界面配置可以节省算力，按照项目实际情况配置
//            AccessibilityNodeInfo window = mPresenter.getCurrentWindow();
//            getAllActionOfNode(window, allAction);
//        }
////        Logging.d(TAG, "updateCurrentWindowAction allAction: " + allAction);
//
//        if (allAction.equals(ActionFactory.getInstance().getwindowAction())) {
//            return;
//        }
//        ActionFactory.getInstance().cleanWindowActionList();
//        ActionFactory.getInstance().addWindowActionList(allAction);
//    }
//
//    private void getAllActionOfNode(AccessibilityNodeInfo node, ArrayList<Action> list) {
//        if (node == null || !node.isVisibleToUser()) {
//            return;
//        }
//        int N = node.getChildCount();
//        if (N > 0) {
//            String description = TextUtils.isEmpty(node.getContentDescription()) ? "" : node.getContentDescription().toString();
//            if (AccessibilityServices.FILTER_OUT_ACTION.equals(description)) {
//                return;
//            }
//            if (node.isScrollable() && SystemSettings.I.getmSetting() != null && SystemSettings.I.getmSetting().isUseScroll()) {
//                String classname = node.getClassName().toString();
//                if (!TextUtils.isEmpty(classname)) {
//                    if (classname.contains("ScrollView")) {
//                        if (!isScrollRoot && !classname.contains("HorizontalScrollView")) {
//                            getActionOfScroll(node, list, AccessibilityConstants.ACCESS_SCROLL_VERTICAL);
//                            isScrollRoot = true;
//                        }
//                    } else if (classname.contains("ViewPager")) {
//                        if (!isViewPager) {
//                            getActionOfScroll(node, list, AccessibilityConstants.ACCESS_SCROLL_HORIZON);
//                            isViewPager = true;
//                        }
//                    } else {
//                        if (!isScrollRoot) {
//                            //不存在scrollview时才添加recycleview的逻辑
//                            if (!isRecycle) {
//                                getActionOfScroll(node, list, AccessibilityConstants.ACCESS_SCROLL_VERTICAL);
//                            }
//                            isRecycle = true;
//                        }
//                    }
//                }
//            }
//            for (int i = 0; i < N; i++) {
//                getAllActionOfNode(node.getChild(i), list);
//            }
//        } else {
//            getActionOfNodeMulti(node, list);
//        }
//    }
//
//    /**
//     * 支持多个
//     *
//     * @param node 节点
//     * @param list 节点列表
//     */
//    private void getActionOfNodeMulti(AccessibilityNodeInfo node, ArrayList<Action> list) {
//        String description = node.getContentDescription() == null ? "" : node.getContentDescription().toString();
//        String textKeys = TextUtils.isEmpty(node.getText()) ? "" : node.getText().toString();
//        handleActionNode(textKeys, node, list);
////         Logging.d(TAG,"nodeinfo: "+node.toString());
//        if (!"".equals(description)) {
//            if (description.contains("#")) {
//                String[] split = description.split("\\#");
//                for (String s : split) {
//                    if (!s.equals(textKeys)) {
//                        handleActionNode(s, node, list);
//                    }
//                }
//            } else {
//                if (!description.equals(textKeys)) {
//                    handleActionNode(description, node, list);
//                }
//            }
//        }
//    }
//
//    /**
//     * 控件节点text和ContentDescription处理添加到可见即可说响应actionlist
//     * @param keyword 热词文本
//     * @param node 节点
//     * @param list 结果 action list
//     */
//    private void handleActionNode(String keyword, AccessibilityNodeInfo node, ArrayList<Action> list){
//        String key = filterNoNumberOrChineseChars(keyword);
//        if(key.length() >32){
//            key = key.substring(0, 32);
//        }
//        if (!TextUtils.isEmpty(key)) {
//            Action action;
//            if (node.isLongClickable() && SystemSettings.I.getmSetting() != null && SystemSettings.I.getmSetting().isSupportLongClick()) {
//                //需要响应长按，且对应的长按控件只响应长按事件不响应一般的点击
//                action = new Action(ActionType.ACTION_LONG_CLICK, key.trim(), node.getPackageName().toString());
//            } else {
//                //单纯响应点击事件
//                action = new Action(ActionType.ACTION_CLICK, key.trim(), node.getPackageName().toString());
//            }
//            action.setNode(node);
//            list.add(action);
//        }
//    }
//
//    /**
//     * 可滑动控件添加控件信息到action列表中
//     *
//     * @param node       控件节点
//     * @param list       控件列表
//     * @param scrollType 可滑动类型
//     */
//    private void getActionOfScroll(AccessibilityNodeInfo node, ArrayList<Action> list, int scrollType) {
//        if (scrollType == AccessibilityConstants.ACCESS_SCROLL_NOTHING) {
//            return;
//        }
//        if (scrollType == AccessibilityConstants.ACCESS_SCROLL) {
//            createInsetScrollAction(node, list, scrollUp, ActionType.ACTION_SCROLL_VIEW_UP);
//            createInsetScrollAction(node, list, scrollDown, ActionType.ACTION_SCROLL_VIEW_DOWN);
//            createInsetScrollAction(node, list, scrollDown2, ActionType.ACTION_SCROLL_VIEW_DOWN);
//            createInsetScrollAction(node, list, scrollTop, ActionType.ACTION_SCROLL_VIEW_TOP);
//            createInsetScrollAction(node, list, scrollBottom, ActionType.ACTION_SCROLL_VIEW_BOTTOM);
//            createInsetScrollAction(node, list, scrollLeft, ActionType.ACTION_SCROLL_VIEW_LEFT);
//            createInsetScrollAction(node, list, scrollRight, ActionType.ACTION_SCROLL_VIEW_RIGHT);
//            createInsetScrollAction(node, list, scrollLeftSide, ActionType.ACTION_SCROLL_VIEW_SIDE_LEFT);
//            createInsetScrollAction(node, list, scrollRightSide, ActionType.ACTION_SCROLL_VIEW_SIDE_RIGHT);
//        } else if (scrollType == AccessibilityConstants.ACCESS_SCROLL_HORIZON) {
//            createInsetScrollAction(node, list, scrollLeft, ActionType.ACTION_SCROLL_VIEW_LEFT);
//            createInsetScrollAction(node, list, scrollRight, ActionType.ACTION_SCROLL_VIEW_RIGHT);
//            createInsetScrollAction(node, list, scrollLeftSide, ActionType.ACTION_SCROLL_VIEW_SIDE_LEFT);
//            createInsetScrollAction(node, list, scrollRightSide, ActionType.ACTION_SCROLL_VIEW_SIDE_RIGHT);
//        } else if (scrollType == AccessibilityConstants.ACCESS_SCROLL_VERTICAL) {
//            createInsetScrollAction(node, list, scrollUp, ActionType.ACTION_SCROLL_VIEW_UP);
//            createInsetScrollAction(node, list, scrollDown, ActionType.ACTION_SCROLL_VIEW_DOWN);
//            createInsetScrollAction(node, list, scrollDown2, ActionType.ACTION_SCROLL_VIEW_DOWN);
//            createInsetScrollAction(node, list, scrollTop, ActionType.ACTION_SCROLL_VIEW_TOP);
//            createInsetScrollAction(node, list, scrollBottom, ActionType.ACTION_SCROLL_VIEW_BOTTOM);
//        }
//    }
//
//    /**
//     * 创建添加action
//     *
//     * @param node    节点
//     * @param list    action列表
//     * @param keyword 存储文本键值
//     * @param type    action处理类型
//     */
//    private void createInsetScrollAction(AccessibilityNodeInfo node, ArrayList<Action> list, String keyword, int type) {
//        Action action = new Action(type, keyword);
//        action.setNode(node);
//        list.add(action);
//    }
//
//    /**
//     * 过滤掉特殊字符
//     *
//     * @param text 过滤文本
//     * @return 文本
//     */
//    private String onRemoveSpecialChars(String text) {
//        if (null == text) {
//            return text;
//        }
//        String regEx = "[-`~!@#$%^&*()=|{}':;',\\[\\].<>＜＞《》_/ /?~～！@#￥%……&*（）——|{}【】‘；：”“’。，、？]";
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(text);
//        text = m.replaceAll("").trim();
//        return text;
//    }
//
//    @Override
//    public String filterNoNumberOrChineseChars(String text) {
//        if (null == text) {
//            return null;
//        }
//
//        /* 去除空字符 */
//        text = text.replaceAll("\\丨", "");
//        text = text.replaceAll("\\|", "");
//        text = text.replaceAll("\\｜", "");
//        /* 白名单中完全匹配的字符串直接返回，防止被过滤处理 */
//        for (String regex : WHITE_WORD_REGEX_LIST) {
//            if (text.matches(regex)) {
//                return text;
//            }
//        }
//
//        String regEx = "[^\\^.0-9"
//                + "[a-zA-Z]"
//                + "\\u0023"
//                + "\\u0025"
//                + "\\u002B"
//                + "\\u002D"
//                + "\\u00D7"
//                + "\\u00F7"
//                + "\\u003D"
//                + "\\u002A"
//                + "\\u005d"
//                + "\\u0020"
//                + "\\u3000"
//                + "\\u4E00-\\u9FA5"
//                + "\\u9FA6-\\u9FCB"
//                + "\\u3400-\\u4D85"
//                + "\\u2F00-\\u2FD5"
//                + "\\u2E80-\\u2EF3"
//                + "\\uF900-\\uFAD9"
//                + "\\uE815-\\uE86F"
//                + "\\uE400-\\uE5E8"
//                + "\\uE600-\\uE6CF"
//                + "\\u31C0-\\u31E3"
//                + "\\u2FF0-\\u2FFB"
//                + "\\u3105-\\u3120"
//                + "\\u31A0-\\u31BA"
//                + "\\u3007]";
//        Pattern p = Pattern.compile(regEx);
//        Matcher m = p.matcher(text);
//        text = m.replaceAll("").trim();
//        return text;
//    }
//
//}
