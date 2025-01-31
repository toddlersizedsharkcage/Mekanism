import mods.mekanism.api.ingredient.ChemicalStackIngredient.PigmentStackIngredient;

//Adds a Painting Recipe that uses 256 mB Red Pigment to convert Clear Sand into Red Sand.

// <recipetype:mekanism:painting>.addRecipe(name as string, itemInput as IIngredientWithAmount, chemicalInput as PigmentStackIngredient, output as IItemStack)

<recipetype:mekanism:painting>.addRecipe("paint_sand", <tag:item:c:sands/colorless>, PigmentStackIngredient.from(<pigment:mekanism:red> * 256), <item:minecraft:red_sand>);
//An alternate implementation of the above recipe are shown commented below. This implementation makes use of implicit casting to allow easier calling:
// <recipetype:mekanism:painting>.addRecipe("paint_sand", <tag:item:c:sands/colorless>, <pigment:mekanism:red> * 256, <item:minecraft:red_sand>);


//Removes the Painting Recipe that allows creating White Dye.

// <recipetype:mekanism:painting>.removeByName(name as string)

<recipetype:mekanism:painting>.removeByName("mekanism:painting/dye/white");