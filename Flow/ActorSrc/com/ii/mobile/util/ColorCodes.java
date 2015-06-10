/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.util;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

/**
 * 
 * @author kfairchild
 */
public class ColorCodes {

	private static List<Code> codes = new ArrayList<Code>();
	private static boolean inited = false;
	private static String WARNING = "#FF8CFF";

	private synchronized static void init() {
		if (inited) {
			return;
		}
		codes.add(new Code("Available", "#00FF00"));
		codes.add(new Code("Assigned", "#AA0000"));
		codes.add(new Code("Active", "#00FF00"));
		codes.add(new Code("At Lunch", "#FF0000"));
		codes.add(new Code("On Break", "#FF0000"));
		codes.add(new Code("Not In", "#FFFFFF"));
		codes.add(new Code("None", "#FF8C00"));
		codes.add(new Code("Completed", "#FF00FF"));
		codes.add(new Code("Delayed", "#FF8CFF"));
		inited = true;
	}

	public static int getColor(String colorName) {
		// L.out("status: " + status);
		init();
		for (Code code : codes) {
			if (code.getStatus().equals(colorName)) {
				// L.out("code: "+code);
				return Color.parseColor(code.getColorCode());
			}
		}
		L.out("*** ERRROR color not found: " + colorName);
		return Color.parseColor(WARNING);
	}
}

class Code {

	String status;
	String colorCode;

	Code(String status, String colorCode) {
		this.status = status;
		this.colorCode = colorCode;
	}

	public String getColorCode() {
		return colorCode;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "code status: " + status + " code: " + colorCode;
	}
}
