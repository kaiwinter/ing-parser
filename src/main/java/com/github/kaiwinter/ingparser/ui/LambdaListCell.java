package com.github.kaiwinter.ingparser.ui;

import java.util.function.Function;

import javafx.scene.control.ListCell;

public class LambdaListCell<T> extends ListCell<T> {

   private final Function<T, String> valueSupplier;

   public LambdaListCell(Function<T, String> valueSupplier) {
      this.valueSupplier = valueSupplier;
   }

   @Override
   public void updateItem(T object, boolean empty) {
      super.updateItem(object, empty);
      if (empty || object == null) {
         setText(null);
      } else {
         String value = valueSupplier.apply(object);
         if (value == null) {
            setText("");
         } else {
            setText(value);
         }
      }
   }

}
