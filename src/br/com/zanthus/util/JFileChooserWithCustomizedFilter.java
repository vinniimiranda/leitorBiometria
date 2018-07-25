package br.com.zanthus.util;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class JFileChooserWithCustomizedFilter {

  public static void main(String[] a) {
    JFileChooser fileChooser = new JFileChooser(".");
    FileFilter jpegFilter = new ExtensionFileFilter(null, new String[] { "WSQ" });

    fileChooser.addChoosableFileFilter(jpegFilter);
    
    fileChooser.showOpenDialog(null);
  }

}