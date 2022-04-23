package com.github.kaiwinter.ingparser.ui;

import com.github.kaiwinter.ingparser.ui.model.CategoryModel;

import javafx.scene.control.ListCell;

public class CategoryModelListCell extends ListCell<CategoryModel> {

   @Override
   public void updateItem(CategoryModel category, boolean empty) {
      super.updateItem(category, empty);
      if (empty || category == null) {
         setText(null);
      } else {
         setText(category.getName());
      }
   }

}
