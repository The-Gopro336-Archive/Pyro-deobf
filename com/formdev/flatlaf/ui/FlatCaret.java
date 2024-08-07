package com.formdev.flatlaf.ui;

import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import javax.swing.JFormattedTextField;
import javax.swing.plaf.UIResource;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

public class FlatCaret extends DefaultCaret implements UIResource {
   private final String selectAllOnFocusPolicy;
   private boolean wasFocused;
   private boolean wasTemporaryLost;
   private boolean isMousePressed;

   public FlatCaret(String selectAllOnFocusPolicy) {
      this.selectAllOnFocusPolicy = selectAllOnFocusPolicy;
   }

   public void install(JTextComponent c) {
      super.install(c);
      Document doc = c.getDocument();
      if (doc != null && this.getDot() == 0 && this.getMark() == 0) {
         int length = doc.getLength();
         if (length > 0) {
            this.setDot(length);
         }
      }

   }

   public void focusGained(FocusEvent e) {
      if (!this.wasTemporaryLost && !this.isMousePressed) {
         this.selectAllOnFocusGained();
      }

      this.wasTemporaryLost = false;
      this.wasFocused = true;
      super.focusGained(e);
   }

   public void focusLost(FocusEvent e) {
      this.wasTemporaryLost = e.isTemporary();
      super.focusLost(e);
   }

   public void mousePressed(MouseEvent e) {
      this.isMousePressed = true;
      super.mousePressed(e);
   }

   public void mouseReleased(MouseEvent e) {
      this.isMousePressed = false;
      super.mouseReleased(e);
   }

   protected void selectAllOnFocusGained() {
      JTextComponent c = this.getComponent();
      Document doc = c.getDocument();
      if (doc != null && c.isEnabled() && c.isEditable()) {
         Object selectAllOnFocusPolicy = c.getClientProperty("JTextField.selectAllOnFocusPolicy");
         if (selectAllOnFocusPolicy == null) {
            selectAllOnFocusPolicy = this.selectAllOnFocusPolicy;
         }

         if (!"never".equals(selectAllOnFocusPolicy)) {
            if (!"always".equals(selectAllOnFocusPolicy)) {
               if (this.wasFocused) {
                  return;
               }

               int dot = this.getDot();
               int mark = this.getMark();
               if (dot != mark || dot != doc.getLength()) {
                  return;
               }
            }

            if (c instanceof JFormattedTextField) {
               EventQueue.invokeLater(() -> {
                  this.setDot(0);
                  this.moveDot(doc.getLength());
               });
            } else {
               this.setDot(0);
               this.moveDot(doc.getLength());
            }

         }
      }
   }
}
