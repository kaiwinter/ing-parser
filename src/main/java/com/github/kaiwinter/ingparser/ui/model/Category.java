package com.github.kaiwinter.ingparser.ui.model;

import java.util.ArrayList;
import java.util.List;

public class Category {

   private final String name;
   private String parentCategoryName;
   private List<Category> subCategories = new ArrayList<>();

   public Category(String name) {
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

   public List<Category> getSubCategories() {
      return subCategories;
   }

   public void setSubCategories(List<Category> subCategories) {
      this.subCategories = subCategories;
   }

   @Override
   public String toString() {
      return "Category [name=" + name + ", parentCategoryName=" + parentCategoryName + ", subCategories="
            + subCategories + "]";
   }

}