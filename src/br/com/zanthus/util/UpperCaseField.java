package br.com.zanthus.util;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

 public class UpperCaseField extends JTextField {
 
     /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UpperCaseField(int cols) {
         super(cols);
     }
 
     protected Document createDefaultModel() {
         return new UpperCaseDocument();
     }
 
     static class UpperCaseDocument extends PlainDocument {
 
         public void insertString(int offs, String str, AttributeSet a)
             throws BadLocationException {
 
             if (str == null) {
                 return;
             }
             char[] upper = str.toCharArray();
             for (int i = 0; i < upper.length; i++) {
                 upper[i] = Character.toUpperCase(upper[i]);
             }
             super.insertString(offs, new String(upper), a);
         }
     }
 }
