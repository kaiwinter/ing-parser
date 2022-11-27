package com.github.kaiwinter.ingparser.ui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryModel {

   public static final CategoryModel UNMATCHED_CATEGORY = new CategoryModel("unmatched");
   public static final CategoryModel IGNORE_CATEGORY = new CategoryModel("ignore");

   private final String name;
   private String parentCategoryName;
   private final List<CategoryModel> subCategories = new ArrayList<>();

   public CategoryModel(String name) {
      this.name = name;
   }

   public boolean isIgnoreCategory() {
      return IGNORE_CATEGORY.getName().equals(name);
   }

   public boolean isUnmatchedCategory() {
      return UNMATCHED_CATEGORY.getName().equals(name);
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

   @Override
   public String toString() {
      return "Category [name=" + name + ", parentCategoryName=" + parentCategoryName + ", subCategories="
            + subCategories + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, parentCategoryName, subCategories);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      CategoryModel other = (CategoryModel) obj;
      return Objects.equals(name, other.name) && Objects.equals(parentCategoryName, other.parentCategoryName)
            && Objects.equals(subCategories, other.subCategories);
   }

}