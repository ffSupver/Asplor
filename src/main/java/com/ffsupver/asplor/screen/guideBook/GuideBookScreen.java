package com.ffsupver.asplor.screen.guideBook;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.item.item.GuideBookItem;
import com.ffsupver.asplor.util.ComparatorByMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuideBookScreen extends Screen {
    private static final Pattern SHOULD_TRANSLATE = Pattern.compile("<(.*?)>");
    private int pageIndex;
    private Contents contents;
    public static final Identifier GUIDE_BOOK_TEXTURE = new Identifier(Asplor.MOD_ID,"textures/guide_book/background.png");
    public static final Identifier GUIDE_BOOK_CHAPTERS = new Identifier(Asplor.MOD_ID,"guide_book/chapters.json");
    private List<OrderedText> cachedPage;
    private final List<List<Item>> itemsToRender = new ArrayList<>();
    private Text pageIndexText;

    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;
    private final ArrayList<PageTurnWidget> pageTurnWidgets = new ArrayList<>();
    private final ArrayList<ChapterWidget> chapterWidgets = new ArrayList<>();
    private int cachedPageIndex;
    public GuideBookScreen(Text title,ItemStack book) {
        super(title);
        this.cachedPage = Collections.emptyList();
        this.pageIndexText = ScreenTexts.EMPTY;
        this.contents = getContentsFromItem(book);
        this.cachedPageIndex = -1;

    }

    private Contents getContentsFromItem(ItemStack book){

        ArrayList<String> fileIdentifiers = new ArrayList<>();
        fileIdentifiers.add("introduce");
        fileIdentifiers.add("getting_start");


        MinecraftClient client = MinecraftClient.getInstance();
        ResourceManager resourceManager = client.getResourceManager();

        Pair<Map<ChapterData,Integer>,Map<String,ChapterData>> allChapterDataMaps = readChapterOrder(resourceManager);
        Map<ChapterData, Integer> chapterOrder = allChapterDataMaps.getLeft();
        Map<String, ChapterData> chapterName = allChapterDataMaps.getRight();

        NbtCompound bookNbt = book.getOrCreateNbt();
        if (bookNbt.contains(GuideBookItem.GUIDE_BOOK_DATA_KEY,9)){
            NbtList chapters = bookNbt.getList(GuideBookItem.GUIDE_BOOK_DATA_KEY,8);
           for (NbtElement element : chapters){
               String fileLocation = element.asString();
               fileIdentifiers.add(fileLocation);
           }
        }

        fileIdentifiers.sort(new ComparatorByMap(chapterOrder));


        ArrayList<String> texts = new ArrayList<>();
        ArrayList<Identifier> pictures = new ArrayList<>();
        ArrayList<Integer> picturesOffsetY = new ArrayList<>();
        ArrayList<ChapterData> chapterDatas = new ArrayList<>();

        for (String filePathShort : fileIdentifiers) {
            chapterDatas.add(chapterName.get(filePathShort));

            Identifier fileIdentifier = new Identifier(Asplor.MOD_ID,"guide_book/"+client.getLanguageManager().getLanguage()+"/"+filePathShort+".json");
            Identifier defaultFileIdentifier = new Identifier(Asplor.MOD_ID,"guide_book/en_us/"+filePathShort+".json");

            Optional<Resource> getResource = resourceManager.getResource(fileIdentifier);
            Optional<Resource> getDefaultResource = resourceManager.getResource(defaultFileIdentifier);

            if (getResource.isPresent()) {
                texts.add(readTextFromJson(getResource));
                pictures.add(new Identifier(Asplor.MOD_ID,"textures/guide_book/"+readPicturesFromJson(getResource)));
                picturesOffsetY.add(readPicturesOffsetYFromJson(getResource));
            }else if (getDefaultResource.isPresent()){
                texts.add(readTextFromJson(getDefaultResource));
                pictures.add(new Identifier(Asplor.MOD_ID,"textures/guide_book/"+readPicturesFromJson(getDefaultResource)));
                picturesOffsetY.add(readPicturesOffsetYFromJson(getDefaultResource));
            }
        }

        return new Contents() {
            private final List<String> pages = texts;
            private final List<Identifier> pagePictures = pictures;
            private final List<Integer> pagePictureOffsetY = picturesOffsetY;
            private final List<ChapterData> chapterDataList = chapterDatas;
            @Override
            public int getPageCount() {
                return pages.size();
            }

            @Override
            public Identifier getPicture(int index) {
                return pagePictures.get(index);
            }
            @Override
            public int getPictureOffsetY(int index) {
                return pagePictureOffsetY.get(index);
            }

            @Override
            public ChapterData getChapterData(int index) {
                return chapterDatas.get(index);
            }

            @Override
            public StringVisitable getPageUnchecked(int index) {
                String string = this.pages.get(index);

                try {
                    StringVisitable stringVisitable = Text.Serializer.fromJson(string);
                    if (stringVisitable != null) {
                        return stringVisitable;
                    }
                } catch (Exception var4) {
                }

                return StringVisitable.plain(string);
            }
        };
    }


    private Pair<Map<ChapterData,Integer>,Map<String,ChapterData>> readChapterOrder(ResourceManager resourceManager){
        Optional<Resource> chapterDataResource = resourceManager.getResource(GUIDE_BOOK_CHAPTERS);
        Map<ChapterData,Integer> result = new HashMap<>();
        Map<String,ChapterData> result2 = new HashMap<>();
        if (chapterDataResource.isPresent()){
            try {

                InputStream inputStream = chapterDataResource.get().getInputStream();
                String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject chapterData = JsonParser.parseString(jsonContent).getAsJsonObject();

                if (chapterData.has("chapter")){
                    JsonArray chapterArray = chapterData.getAsJsonArray("chapter");

                    for (JsonElement chapterElement : chapterArray){
                        JsonObject chapter = chapterElement.getAsJsonObject();
                        String name = JsonUtils.getString("name",chapter);
                        Text description = Text.Serializer.fromJson(JsonUtils.getString("description",chapter));
                        ChapterData chapterDataToAdd;
                        if (chapter.has("display_item")){
                            ItemStack displayItem = Registries.ITEM.get(new Identifier(JsonUtils.getString("display_item",chapter))).getDefaultStack();
                            chapterDataToAdd = new ChapterData(name,description,displayItem);
                        }else {
                            chapterDataToAdd = new ChapterData(name,description);
                        }
                        result.put(chapterDataToAdd, JsonUtils.getIntOr("order",chapter,0));
                        result2.put(name,chapterDataToAdd);
                    }
                }
            } catch (IOException e) {
                Asplor.LOGGER.info(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return new Pair<>(result,result2);
    }

    @Nullable
    private String readTextFromJson(Optional<Resource> getResource){
        Resource resource = getResource.get();
        try {
            InputStream inputStream = resource.getInputStream();
            String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

            Text finalText = Text.empty();
            List<Item> itemToRender = new ArrayList<>();
            String text = JsonUtils.getString("text",jsonObject);


            // 定义正则表达式来匹配 <xxx> 格式的字符串
            Matcher matcher = SHOULD_TRANSLATE.matcher(text);

            int lastIndex = 0;
            while (matcher.find()) {
                // 添加普通文本部分（即尖括号之前的文本）
                if (matcher.start() > lastIndex) {
                   finalText = finalText.copy().append(Text.literal(text.substring(lastIndex, matcher.start())));
                }

                // 获取尖括号内容并将其转换为可翻译文本
                String translatableKey = matcher.group(1);
                Text itemText = Text.translatable(translatableKey).formatted(Formatting.AQUA);
                String[] itemId = translatableKey.split("\\.");
                if (itemId.length>=3){
                    Item itemToShow = Registries.ITEM.get(new Identifier(itemId[1], itemId[2]));
                    if (!itemToShow.equals(Items.AIR)){
                        itemText = itemText.copy().setStyle(Style.EMPTY.withFormatting(Formatting.GOLD).withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_ITEM,new HoverEvent.ItemStackContent(itemToShow.getDefaultStack()))
                        ));
                        if (!itemToRender.contains(itemToShow)){
                            itemToRender.add(itemToShow);
                        }
                    }
                }
                finalText = finalText.copy().append(itemText);

                // 更新 lastIndex 以继续处理文本的剩余部分
                lastIndex = matcher.end();
            }

            // 添加最后一段普通文本
            if (lastIndex < text.length()) {
                finalText = finalText.copy().append(Text.literal(text.substring(lastIndex)));
            }


            this.itemsToRender.add(itemToRender);

            return Text.Serializer.toJson(finalText);
        } catch (IOException e) {
            Asplor.LOGGER.info(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return Text.Serializer.toJson(Text.literal("empty in "+resource));
    }

    @Nullable
    private String readPicturesFromJson(Optional<Resource> getResource){
        Resource resource = getResource.get();
        try {
            InputStream inputStream = resource.getInputStream();
            String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

            return JsonUtils.getString("picture",jsonObject);
        } catch (IOException e) {
            Asplor.LOGGER.info(e.getLocalizedMessage()+getResource);
            e.printStackTrace();
        }
        return "default.png";
    }

    private int readPicturesOffsetYFromJson(Optional<Resource> getResource){
        Resource resource = getResource.get();
        try {
            InputStream inputStream = resource.getInputStream();
            String jsonContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();
            return JsonUtils.getIntOr("offset_y",jsonObject,0);
        } catch (IOException e) {
            Asplor.LOGGER.info(e.getLocalizedMessage()+getResource);
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = (this.width - 255) / 2;
        addPageButtons(centerX);
        addChapterButton(centerX);
        addCloseButton();
    }

    protected void addCloseButton() {
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
            this.close();
        }).dimensions(this.width / 2 - 100, 196, 200, 20).build());
    }

    protected void addPageButtons(int centerX) {
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(centerX + 123, 159, true, (button) -> {
            this.goToNextPage();
        }, true));
        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(centerX + 105, 159, false, (button) -> {
            this.goToPreviousPage();
        }, true));
        this.addPageButton(centerX);
        this.updatePageButtons();
    }

    private void addPageButton(int centerX){
        int buttonCount = 4 * 2;
        for (int i = 0; i < buttonCount / 2; i++) {
            int finalI = i + 2;
            PageTurnWidget pageTurnWidgetRight = this.addDrawableChild(new PageTurnWidget((i + 1) * 19 + centerX + 123,159,true, button -> {
                this.jumpToPage(this.pageIndex + finalI);
                updatePageButtons();
            },true));
            PageTurnWidget pageTurnWidgetLeft = this.addDrawableChild(new PageTurnWidget(- (i + 1) * 19 + centerX + 105,159,false, button -> {
                this.jumpToPage(this.pageIndex - finalI);
                updatePageButtons();
            },true));

            pageTurnWidgets.add(pageTurnWidgetLeft);
            pageTurnWidgets.add(pageTurnWidgetRight);
        }
    }

    private void addChapterButton(int centerX){
        for (int i = 0; i < this.contents.getPageCount(); i++) {
            ChapterData chapterData = this.contents.getChapterData(i);
            if (chapterData.isMainChapter){
                int page = i;
                ChapterWidget chapterWidget = this.addDrawableChild(new ChapterWidget(centerX + 230, 10 + chapterWidgets.size() * 20, button -> {
                    this.jumpToPage(page);
                },chapterData.displayItem,chapterData.description,textRenderer));
                chapterWidgets.add(chapterWidget);
            }
        }
    }

    private int getPageCount() {
        return this.contents.getPageCount();
    }
    protected void goToNextPage() {
        if (this.pageIndex < this.getPageCount() - 1) {
            ++this.pageIndex;
        }

        this.updatePageButtons();
    }

    protected void goToPreviousPage() {
        if (this.pageIndex > 0) {
            --this.pageIndex;
        }

        this.updatePageButtons();
    }

    private void updatePageButtons() {
        this.nextPageButton.visible = this.pageIndex < this.getPageCount() - 1;
        this.previousPageButton.visible = this.pageIndex > 0;
        for (int i = 0; i < pageTurnWidgets.size(); i++) {
            int gap = i / 2 + 1;
            boolean left = i % 2 == 0;
            pageTurnWidgets.get(i).visible = left ? this.pageIndex - gap > 0 : this.pageIndex < this.getPageCount() - gap - 1;
        }
    }

    public boolean setPage(int index) {
        int i = MathHelper.clamp(index, 0, this.contents.getPageCount() - 1);
        if (i != this.pageIndex) {
            this.pageIndex = i;
            this.updatePageButtons();
            this.cachedPageIndex = -1;
            return true;
        } else {
            return false;
        }
    }

    protected boolean jumpToPage(int page) {
        return this.setPage(page);
    }


    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        int i = (this.width - 255) / 2;
        context.drawTexture(GUIDE_BOOK_TEXTURE, i, 2, 0, 0, 255, 192);
        context.drawTexture(this.contents.getPicture(pageIndex),i,115 + this.contents.getPictureOffsetY(pageIndex),0,0,255,255);
        if (this.cachedPageIndex != this.pageIndex) {
            StringVisitable stringVisitable = this.contents.getPage(this.pageIndex);
            this.cachedPage = this.textRenderer.wrapLines(stringVisitable, 188);
            this.pageIndexText = Text.translatable("book.pageIndicator", new Object[]{this.pageIndex + 1, Math.max(this.getPageCount(), 1)});
        }

        this.cachedPageIndex = this.pageIndex;
        int k = this.textRenderer.getWidth(this.pageIndexText);
        context.drawText(this.textRenderer, this.pageIndexText, i - k + 255 - 24, 18, 0, false);
        Objects.requireNonNull(this.textRenderer);
        int l = Math.min(128 / 9, this.cachedPage.size());

        for(int m = 0; m < l; ++m) {
            OrderedText orderedText = this.cachedPage.get(m);
            TextRenderer textRenderer1 = this.textRenderer;
            int var10003 = i + 36;
            Objects.requireNonNull(this.textRenderer);
            context.drawText(textRenderer1, orderedText, var10003, 32 + m * 9, 0, false);
        }

        // 渲染物品
        int itemX = i + 36;
        int itemY = 32 + l * 9 + 2; // 在文本下方渲染物品
        for (Item item : itemsToRender.get(pageIndex)) {
            ItemStack itemStackToRender = item.getDefaultStack();
            context.drawItem(itemStackToRender, itemX, itemY);

            if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
                context.drawHoverEvent(this.textRenderer, Style.EMPTY.withHoverEvent(
                                    new HoverEvent(HoverEvent.Action.SHOW_ITEM,new HoverEvent.ItemStackContent(itemStackToRender))
                            ), mouseX,mouseY);
            }

            itemX += 20; // 每个物品之间留出一些空间
        }


        Style style = this.getTextStyleAt(mouseX, mouseY);
        if (style != null) {
            context.drawHoverEvent(this.textRenderer, style, mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Nullable
    public Style getTextStyleAt(double x, double y) {
        if (this.cachedPage.isEmpty()) {
            return null;
        } else {
            int i = MathHelper.floor(x - (double)((this.width - 255) / 2) - 36.0);
            int j = MathHelper.floor(y - 2.0 - 30.0);
            if (i >= 0 && j >= 0) {
                Objects.requireNonNull(this.textRenderer);
                int k = Math.min(128 / 9, this.cachedPage.size());
                if (i <= 114) {
                    Objects.requireNonNull(this.client.textRenderer);
                    if (j < 9 * k + k) {
                        Objects.requireNonNull(this.client.textRenderer);
                        int l = j / 9;
                        if (l >= 0 && l < this.cachedPage.size()) {
                            OrderedText orderedText = this.cachedPage.get(l);
                            return this.client.textRenderer.getTextHandler().getStyleAt(orderedText, i);
                        }

                        return null;
                    }
                }

                return null;
            } else {
                return null;
            }
        }
    }
    @Environment(EnvType.CLIENT)
    public interface Contents {
        int getPageCount();

        StringVisitable getPageUnchecked(int index);

        Identifier getPicture(int index);
        int getPictureOffsetY(int index);

        ChapterData getChapterData(int index);


        default StringVisitable getPage(int index) {
            return index >= 0 && index < this.getPageCount() ? this.getPageUnchecked(index) : StringVisitable.EMPTY;
        }

        static BookScreen.Contents create(ItemStack stack) {
            if (stack.isOf(Items.WRITTEN_BOOK)) {
                return new BookScreen.WrittenBookContents(stack);
            } else {
                return (BookScreen.Contents)(stack.isOf(Items.WRITABLE_BOOK) ? new BookScreen.WritableBookContents(stack) : BookScreen.EMPTY_PROVIDER);
            }
        }
    }

    public class ChapterData{
        public final Text description;
        public final ItemStack displayItem;
        public final boolean isMainChapter;
        public final String name;
        public ChapterData(String name,Text description,ItemStack displayItem,boolean isMainChapter){
            this.description = description;
            this.displayItem = displayItem;
            this.isMainChapter = isMainChapter;
            this.name = name;
        }
        public ChapterData(String name,Text description,ItemStack displayItem){
            this(name,description,displayItem,true);
        }
        public ChapterData(String name,Text description){
            this(name,description,ItemStack.EMPTY,false);
        }

        @Override
        public String toString() {
            return "Chapter :"+this.name+" Description :"+description+" Item :"+displayItem+" "+isMainChapter;
        }
    }
}
