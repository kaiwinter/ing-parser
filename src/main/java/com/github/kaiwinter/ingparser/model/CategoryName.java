package com.github.kaiwinter.ingparser.model;

public class CategoryName {

   private final String name;
   private String parentCategoryName;

   public CategoryName(String name) {
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

   @Override
   public String toString() {
      return "CategoryName [name=" + name + ", parentCategoryName=" + parentCategoryName + "]";
   }

}