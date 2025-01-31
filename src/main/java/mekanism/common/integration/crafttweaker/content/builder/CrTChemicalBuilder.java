package mekanism.common.integration.crafttweaker.content.builder;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import mekanism.api.chemical.attribute.ChemicalAttribute;
import mekanism.common.integration.crafttweaker.CrTConstants;
import mekanism.common.integration.crafttweaker.CrTUtils;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister(loaders = CrTConstants.CONTENT_LOADER)
@ZenCodeType.Name(CrTConstants.CLASS_BUILDER_CHEMICAL)
public abstract class CrTChemicalBuilder<CHEMICAL extends Chemical<CHEMICAL>, BUILDER extends ChemicalBuilder<CHEMICAL, BUILDER>,
      CRT_BUILDER extends CrTChemicalBuilder<CHEMICAL, BUILDER, CRT_BUILDER>> {

    private final BUILDER builder;
    @Nullable
    protected Integer colorRepresentation;

    protected CrTChemicalBuilder(BUILDER builder) {
        this.builder = builder;
    }

    /**
     * Adds an attribute to the set of attributes this chemical has.
     *
     * @param attribute Attribute to add.
     */
    @ZenCodeType.Method
    public CRT_BUILDER with(ChemicalAttribute attribute) {
        getInternal().with(attribute);
        return self();
    }

    /**
     * Sets the tint to apply to this chemical when rendering.
     *
     * @param tint Color in RRGGBB format
     */
    @ZenCodeType.Method
    public CRT_BUILDER tint(int tint) {
        getInternal().tint(tint);
        return self();
    }

    /**
     * Sets the color representation to apply to this chemical when used for things like durability bars. Mostly for use in combination with custom textures that are not
     * tinted.
     *
     * @param color Color in RRGGBB format
     */
    @ZenCodeType.Method
    public CRT_BUILDER colorRepresentation(int color) {
        colorRepresentation = color;
        return self();
    }

    /**
     * Create a chemical from this builder with the given name.
     *
     * @param name Registry name for the chemical.
     */
    @ZenCodeType.Method
    public void build(String name) {
        build(CrTUtils.rl(name));
    }

    /**
     * Create a chemical from this builder with the given name.
     *
     * @param registryName Registry name for the chemical.
     */
    protected abstract void build(ResourceLocation registryName);

    /**
     * Gets the internal {@link ChemicalBuilder}
     */
    protected BUILDER getInternal() {
        return builder;
    }

    @SuppressWarnings("unchecked")
    protected CRT_BUILDER self() {
        return (CRT_BUILDER) this;
    }
}