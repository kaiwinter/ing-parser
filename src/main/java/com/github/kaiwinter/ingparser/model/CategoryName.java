package com.github.kaiwinter.ingparser.model;

import java.util.ArrayList;
import java.util.List;

public class CategoryName {

   private final String name;
   private String parentCategoryName;
   private List<CategoryName> subCategories = new ArrayList<>();

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

   public List<CategoryName> getSubCategories() {
      return subCategories;
   }

   public void setSubCategories(List<CategoryName> subCategories) {
      this.subCategories = subCategories;
   }

   @Override
   public String toString() {
      return "CategoryName [name=" + name + ", parentCategoryName=" + parentCategoryName + ", subCategories="
            + subCategories + "]";
   }

}