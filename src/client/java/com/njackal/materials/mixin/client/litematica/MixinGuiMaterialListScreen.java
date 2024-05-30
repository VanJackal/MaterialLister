package com.njackal.materials.mixin.client.litematica;

import fi.dy.masa.litematica.Reference;
import fi.dy.masa.litematica.gui.GuiMaterialList;
import fi.dy.masa.litematica.materials.MaterialListBase;
import fi.dy.masa.litematica.materials.MaterialListEntry;
import fi.dy.masa.litematica.materials.MaterialListSorter;
import fi.dy.masa.malilib.data.DataDump;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.ArrayList;

@Mixin(GuiMaterialList.class)
public class MixinGuiMaterialListScreen extends GuiBase {
    @Inject(method = "initGui", at = @At("RETURN"), remap = false)
    public void initGui(CallbackInfo ci) {
        GuiMaterialList guiMaterialList = (GuiMaterialList) (Object) this;

        int x = 300;
        int y = 4;

        final ButtonGeneric button = new ButtonGeneric(x,y,100,20,"Better Export", "Export to csv using translation strings as names");
        addButton(button, (base, mouse)->{
            // this code is modified from litematica GuiMaterialList ButtonListener
            File dir = new File(FileUtils.getConfigDirectory(), Reference.MOD_ID);
            File file = DataDump.dumpDataToFile(dir, "material_list", ".csv", getMaterialListDump(guiMaterialList.getMaterialList()).getLines());

            if (file != null)
            {
                String key = "litematica.message.material_list_written_to_file";
                this.addMessage(Message.MessageType.SUCCESS, key, file.getName());

                if (this.mc.player != null) {
                    StringUtils.sendOpenFileChatMessage(this.mc.player, key, file);
                }
            }
        });
    }

    private DataDump getMaterialListDump(MaterialListBase materialList) {
        DataDump dump = new DataDump(2,DataDump.Format.CSV);
        int multiplier = materialList.getMultiplier();

        ArrayList<MaterialListEntry> list = new ArrayList<>(materialList.getMaterialsFiltered(false));
        list.sort(new MaterialListSorter(materialList));

        for (MaterialListEntry entry : list){
            int total = entry.getCountTotal() * multiplier;
            dump.addData(String.valueOf(entry.getStack().getItem().getTranslationKey()), String.valueOf(total));
        }
        dump.addTitle("Item", "Count");
        dump.setSort(false);
        return dump;
    }

}
