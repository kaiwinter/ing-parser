package com.github.kaiwinter.ingparser.ui.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryModel {

   private final String name;
   private String parentCategoryName;
   private List<CategoryModel> subCategories = new ArrayList<>();

   public CategoryModel(String name) {
      this.name = name;
   }

   public String getParentCategoryName() {
      return parentCategoryName;
   }

   public void setParentCategoryName(String parentCategoryName) {
      this.parentCategoryName = parentCategoryName;
   }

   public String getName() {
      return name;
   }

   public List<CategoryModel> getSubCategories() {
      return subCategories;
   }

   public void setSubCategories(List<CategoryModel> subCategories) {
      this.subCategories = subCategories;
   }

   @Override
   public String toString() {
      return "Category [name=" + name + ", parentCategoryName=" + parentCategoryName + ", subCategories="
            + subCategories + "]";
   }

}