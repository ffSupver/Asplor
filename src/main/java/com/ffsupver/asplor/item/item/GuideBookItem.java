package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.networking.packet.OpenGuideBookS2CPacket;
import com.ffsupver.asplor.screen.guideBook.GuideBookScreen;
import com.ffsupver.asplor.util.NbtUtil;
import com.ffsupver.asplor.util.RenderUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GuideBookItem extends Item {
    public static final String GUIDE_BOOK_DATA_KEY = "chapter";
    private static final Text HEAD = Text.literal("-");
    public GuideBookItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!user.getWorld().isClient()){
            OpenGuideBookS2CPacket.send((ServerPlayerEntity) user,hand);
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ArrayList<ChapterData> allChapterData = readChapterData();


        NbtList chapters = getChaptersFromNbt(stack);
        if (chapters != null){
            ArrayList<String> chapterIds = new  ArrayList<>(NbtUtil.readStringListFromNbt(chapters));
            chapterIds.sort((s1,s2)->{
                List<ChapterData> firstChapterDataL = allChapterData.stream().filter(chapterData -> chapterData.name.equals(s1)).toList();
               List<ChapterData> secondChapterDataL = allChapterData.stream().filter(chapterData -> chapterData.name.equals(s2)).toList();
               if (firstChapterDataL.isEmpty() || secondChapterDataL.isEmpty()){
                   return 0;
               }else {
                   ChapterData firstChapter = firstChapterDataL.get(0);
                   ChapterData secondChapter = secondChapterDataL.get(0);
                   return firstChapter.order - secondChapter.order;
               }
            });

            for (String chapterId : chapterIds){
                allChapterData.forEach(chapterData -> {
                    if (chapterData.getName().equals(chapterId)){
                        Text description = Text.Serializer.fromJson(chapterData.getDescription());
                        if (RenderUtil.isShiftPress() || description != null && description.getStyle().getColor().equals(TextColor.fromFormatting(Formatting.GOLD))) {
                            Text toAdd = HEAD.copy().append(description);
                            tooltip.add(toAdd);
                        }
                    }
                });
            }
        }
        super.appendTooltip(stack, world, tooltip, context);

    }

    public ArrayList<ChapterData> readChapterData(){
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        Optional<Resource> getChapterData = resourceManager.getResource(GuideBookScreen.GUIDE_BOOK_CHAPTERS);
        ArrayList<ChapterData> result = new ArrayList<>();
        if (getChapterData.isPresent()){
            try {
               JsonObject allChapterData = JsonParser.parseString(new String(getChapterData.get().getInputStream().readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();
                if (allChapterData.has("chapter")){
                   JsonArray allChapter = allChapterData.get("chapter").getAsJsonArray();
                    for (JsonElement element : allChapter){
                        JsonObject chapter = element.getAsJsonObject();
                        result.add(new ChapterData(
                                JsonUtils.getIntOr("order",chapter,0),
                                JsonUtils.getString("name",chapter),
                                JsonUtils.getString("description",chapter)
                        ));
                    }
                }
            } catch (IOException e){
                Asplor.LOGGER.info(e.getLocalizedMessage());
            }
        }
        return result;
    }

    public static boolean canAdd(ItemStack book,ItemStack page){
        if ((!book.hasNbt() || book.getOrCreateNbt().contains(GuideBookItem.GUIDE_BOOK_DATA_KEY,9)) && page.getOrCreateNbt().contains("chapter",8)){
            NbtList chapterDataList = book.getOrCreateNbt().getList(GUIDE_BOOK_DATA_KEY,8);
            String chapterToAdd = page.getOrCreateNbt().getString("chapter");
            for (NbtElement element : chapterDataList){
                String chapterData = element.asString();
                if (Objects.equals(chapterData, chapterToAdd)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static NbtList getChaptersFromNbt(ItemStack book){
        NbtCompound stackNbt = book.getOrCreateNbt();

        if (stackNbt.contains(GUIDE_BOOK_DATA_KEY,9)) {
            return stackNbt.getList(GUIDE_BOOK_DATA_KEY, 8);
        }
        return null;
    }


    private class ChapterData {
        private final int order;
        private final String name;
        private final String description;
        public ChapterData(int order, String name, String description){

            this.order = order;
            this.name = name;
            this.description = description;
        }

        public int getOrder() {
            return order;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Name: " + this.name + " Description: " + this.description + " Order: " + this.order;
        }
    }
}
