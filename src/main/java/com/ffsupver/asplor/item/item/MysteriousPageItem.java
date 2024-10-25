package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.screen.guideBook.GuideBookScreen;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MysteriousPageItem extends Item {
    public MysteriousPageItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Map<String,String> allChapterData = readChapterData();
        NbtCompound stackNbt = stack.getOrCreateNbt();
        if (stackNbt.contains("chapter",8)){
            String chapter = stackNbt.getString("chapter");
            String description = allChapterData.get(chapter);
            if (description!=null){
                tooltip.add(Text.Serializer.fromJson(description));
            }
        }
    }

    public Map<String,String> readChapterData(){
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        Optional<Resource> getChapterData = resourceManager.getResource(GuideBookScreen.GUIDE_BOOK_CHAPTERS);
        Map<String,String> result = new HashMap<>();
        if (getChapterData.isPresent()){
            try {
                JsonObject allChapterData = JsonParser.parseString(new String(getChapterData.get().getInputStream().readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
                if (allChapterData.has("chapter")){
                    JsonArray allChapter = allChapterData.get("chapter").getAsJsonArray();
                    for (JsonElement element : allChapter){
                        JsonObject chapter = element.getAsJsonObject();
                        result.put(
                                JsonUtils.getString("name",chapter),
                                JsonUtils.getString("description",chapter)
                        );
                    }
                }
            } catch (IOException e){
                Asplor.LOGGER.info(e.getLocalizedMessage());
            }
        }
        return result;
    }
}
