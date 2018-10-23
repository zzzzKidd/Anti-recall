/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

public class NodesInfo {

    private static int padding = 10;

    public static void show(AccessibilityNodeInfo node, String TAG) {
        log("v", TAG, "<--------------------------------");
        iter(node, TAG, 0, "v");
        log("v", TAG, "-------------------------------->");
    }

    public static void show(AccessibilityNodeInfo node, String TAG, String level) {
        log(level, TAG, "<--------------------------------");
        log(level, TAG, "      ");
        iter(node, TAG, 0, level);
        log(level, TAG, "  ");
        log(level, TAG, "-------------------------------->");
    }

    private static void iter(AccessibilityNodeInfo node, String TAG, int num, String level) {
        if (node == null) return;
        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            String print = print(node.getChild(i));
            log(level, TAG, "   \t" + addPadding(num) + i + addPadding(num, padding) + print);
            iter(node.getChild(i), TAG, num + 1, level);
        }
    }

    @NonNull
    private static String addPadding(int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++)
            builder.append("\t");
        return builder.toString();
    }

    @NonNull
    private static String addPadding(int num, int amount) {
        if (amount < num)
            return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < amount - num; i++)
            builder.append("\t");
        return builder.toString();
    }

    private static String print(AccessibilityNodeInfo nodeInfo) {

        if (nodeInfo == null)
            return "";

        CharSequence text = nodeInfo.getText();
        CharSequence description = nodeInfo.getContentDescription();
        CharSequence className = nodeInfo.getClassName();
        CharSequence packageName = nodeInfo.getPackageName();
        boolean focusable = nodeInfo.isFocusable();
        boolean clickable = nodeInfo.isClickable();
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        String viewId = nodeInfo.getViewIdResourceName();

        return "| "
                + "text: " + text + " \t"
                + "description: " + description + " \t"
                + String.format("%-40s", "ID: " + viewId) + " \t"
                + String.format("%-40s", "class: " + className) + " \t"
                + String.format("%-30s", "location: " + rect) + " \t"
//                + "focusable: " + focusable + " \t"
//                + "clickable: " + clickable + " \t"
//                + String.format("%-30s", "package: " + packageName) + " \t"
                ;

    }

    private static void log(String level, String TAG, String msg) {
        switch (level) {
            case "v":
                Log.v(TAG, msg);
                break;
            case "d":
                Log.d(TAG, msg);
                break;
            case "i":
                Log.i(TAG, msg);
                break;
            case "w":
                Log.w(TAG, msg);
                break;
            case "e":
                Log.e(TAG, msg);
                break;
            default:
                throw new Error();
        }
    }

}
