package br.com.zanthus.util;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;


public class ButtonLayout {
	/*Functions to set layout of the buttons*/
    public static void changeWidth(JComponent comp) {
      comp.setAlignmentX(Component.CENTER_ALIGNMENT);
      comp.setAlignmentY(Component.CENTER_ALIGNMENT);
      Dimension dim = comp.getPreferredSize();
      dim.width = Integer.MAX_VALUE;
      comp.setMaximumSize(dim);
    }

    public static void changeHeight(JComponent comp) {
      comp.setAlignmentX(Component.CENTER_ALIGNMENT);
      comp.setAlignmentY(Component.CENTER_ALIGNMENT);
      Dimension dim = comp.getPreferredSize();
      dim.height = Integer.MAX_VALUE;
      comp.setMaximumSize(dim);
    }

    public static void changeBoth(JComponent comp) {
      comp.setAlignmentX(Component.CENTER_ALIGNMENT);
      comp.setAlignmentY(Component.CENTER_ALIGNMENT);
      Dimension dim = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
      comp.setMaximumSize(dim);
    }
}
