package com.ffsupver.asplor.compat.rei.category.sequencedAssembly;

import com.simibubi.create.compat.emi.EmiSequencedAssemblySubCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.JeiSequencedAssemblySubCategory;
import com.simibubi.create.compat.recipeViewerCommon.SequencedAssemblySubCategoryType;


public class SequencedAssemblySubCategoryTypes {
    public static final SequencedAssemblySubCategoryType ALLOY_PRESSING = new SequencedAssemblySubCategoryType(
            () -> JeiSequencedAssemblySubCategory.AssemblyPressing::new,
            () -> ReiSequencedAssemblySubCategory.AssemblyAlloyPressing::new,
            () -> EmiSequencedAssemblySubCategory.AssemblyPressing::new
    );


}
