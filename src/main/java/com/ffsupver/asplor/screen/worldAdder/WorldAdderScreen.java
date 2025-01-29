package com.ffsupver.asplor.screen.worldAdder;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.networking.packet.worldAdder.CreateWorldC2SPacket;
import com.ffsupver.asplor.networking.packet.worldAdder.PlanetCreatingData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorldAdderScreen extends Screen {
    private static final Pattern IS_MINECRAFT_ID = Pattern.compile("minecraft:(\\S+)");

    private TextFieldWidget inputName;
    private ButtonWidget buttonColumn;
    private Column column;

    private ButtonWidget buttonDone;
    private ButtonWidget buttonAdd;
    private ButtonWidget buttonMul;
    private ButtonWidget buttonNoise;
    private ButtonWidget buttonInterpolated;
    private ButtonWidget buttonShiftNoise;
    private ButtonWidget buttonYClampedGradient;
    private ButtonWidget buttonNoiseInRange;
    private ButtonWidget buttonMin;
    private ButtonWidget buttonMax;
    private ButtonWidget buttonStart;
    private ButtonWidget buttonStop;
    private ButtonWidget buttonRemove;
    private ButtonWidget buttonAddList;
    private ArrayList<String> functionArgument;
    private final ArrayList<String> functionList = new ArrayList<>();
    private ButtonWidget buttonAddBlock;
    private ButtonWidget buttonRemoveBlock;
    private final ArrayList<String> biomesIdList = new ArrayList<>();
    private ButtonWidget buttonAddBiome;
    private ButtonWidget buttonRemoveBiome;
    private ButtonWidget buttonOxygen;
    private ButtonWidget buttonTemperature;
    private ButtonWidget buttonGravity;
    private ButtonWidget buttonSolarPower;
    private ButtonWidget buttonTier;
    private final PlanetCreatingData planetData = new PlanetCreatingData();

    private final ArrayList<String> blockList = new ArrayList<>();
    private Text description;


    public WorldAdderScreen( Text title) {
        super(title);
    }


    private ButtonWidget createButton(int xOffset , int yOffset,String function,String description,boolean ignoreInput){
        return createButton(xOffset,yOffset,description,button -> {
            addFunctionToList(function,ignoreInput);
        });
    }
    private ButtonWidget createButton(int xOffset , int yOffset, String description, Consumer<ButtonWidget> onClick){
        return this.addDrawableChild(ButtonWidget.builder(Text.translatable(description), onClick::accept
        ).dimensions(this.width / 2 + xOffset, 40 + yOffset, 50, 20).build());
    }



    @Override
    protected void init() {
        super.init();
        this.column = Column.COLUMNS.get(Column.COLUMNS.size()-1);

        this.buttonDone = this.addDrawableChild(ButtonWidget.builder(Text.translatable("asplor.screen.world_adder.done"),
                button -> {
                    Asplor.LOGGER.info("Creating world with : "+functionList);
                    this.createWorld();
                }
                ).dimensions(this.width / 2 -100, 185, 200, 20).build());
        this.buttonAdd = createButton(-50,0,"add","asplor.screen.world_adder.add",false);
        this.buttonMul = createButton(0,0,"mul","asplor.screen.world_adder.mul",false);
        this.buttonNoise = createButton(50,0,"noise","asplor.screen.world_adder.noise",false);
        this.buttonInterpolated = createButton(50,30,"interpolated","asplor.screen.world_adder.interpolated",false);

        functionArgument = new ArrayList<>();
        this.buttonShiftNoise = createButton(-50,30,"asplor.screen.world_adder.shift_noise",
                buttonWidget -> {
                    addFunctionToList("shift_noise",2);
                }
        );
        this.buttonYClampedGradient = createButton(0,60,"asplor.screen.world_adder.y_clamped_gradient",
                buttonWidget -> {
                    addFunctionToList("y_clamped_gradient",4);
                }
        );
        this.buttonNoiseInRange = createButton(0,30,"asplor.screen.world_adder.noise_in_range",
                buttonWidget -> {
                    addFunctionToList("noise_in_range",5);
                }
        );

        this.buttonMin = createButton(100,0,"min","asplor.screen.world_adder.min",true);
        this.buttonMax = createButton(150,0,"max","asplor.screen.world_adder.max",true);

        this.buttonStart = createButton(100,30,"start","asplor.screen.world_adder.start",true);
        this.buttonStop = createButton(150,30,"stop","asplor.screen.world_adder.stop",true);
        this.buttonRemove = createButton(150,60,"asplor.screen.world_adder.remove",
                buttonWidget -> {
                    if (!this.functionArgument.isEmpty()){
                        this.functionArgument.remove(functionArgument.size() - 1);
                        return;
                    }

                    if (!this.functionList.isEmpty()) {
                        this.functionList.remove(functionList.size() - 1);
                    }
                }
        );
        this.buttonAddList = createButton(100,60,"asplor.screen.world_adder.add_list",
                buttonWidget -> {
                    String[] input = this.inputName.getText().replaceAll("\\s*","").split(",");
                    this.functionList.addAll(Arrays.asList(input));
                }
        );

        this.inputName = new TextFieldWidget(this.textRenderer,
                this.width / 2 - 152, 40, 100, 20,
                Text.translatable( "asplor.screen.world_adder.function_name")
        );


        this.buttonColumn = createButton(150,-20,"asplor.screen.world_adder.column",
                buttonWidget -> updateColumn()
        );
        this.buttonAddBlock = createButton(100,0,"asplor.screen.world_adder.add_block",
                buttonWidget -> {
                    if (this.blockList.size() < 3){
                        String[] input = this.inputName.getText().replaceAll("\\s*", "").split(",");
                        this.blockList.addAll(Arrays.asList(input));
                    }
                }
        );
        this.buttonRemoveBlock = createButton(50,0,"asplor.screen.world_adder.remove_block",
                buttonWidget -> {
                    if (!this.blockList.isEmpty()){
                        this.blockList.remove(this.blockList.size() - 1);
                    }
                }
        );

        this.buttonAddBiome = createButton(100,0,"asplor.screen.world_adder.add_biome",
                buttonWidget -> {
                    String[] input = this.inputName.getText().replaceAll("\\s*", "").split(",");
                    this.biomesIdList.addAll(Arrays.asList(input));
                }
        );
        this.buttonRemoveBiome = createButton(50,0,"asplor.screen.world_adder.remove_biome",
                buttonWidget -> {
                    if (!this.biomesIdList.isEmpty()){
                        this.biomesIdList.remove(this.biomesIdList.size() - 1);
                    }
                }
        );
        this.buttonOxygen = createButton(-50,0,"asplor.screen.world_adder.oxygen",
                buttonWidget -> {
                    planetData.oxygen = Boolean.parseBoolean(inputName.getText());
                }
        );
        this.buttonTemperature = createButton(0,0,"asplor.screen.world_adder.temperature",
                buttonWidget -> {
                    try {
                        planetData.temperature = Short.parseShort(inputName.getText());
                    }catch (NumberFormatException n){
                        Asplor.LOGGER.info("wrong format :"+inputName.getText());
                    }
                }
        );
        this.buttonGravity = createButton(50,0,"asplor.screen.world_adder.gravity",
                buttonWidget -> {
                    try {
                        planetData.gravity = Float.parseFloat(inputName.getText());
                    }catch (NumberFormatException n){
                        Asplor.LOGGER.info("wrong format :"+inputName.getText());
                    }
                }
        );
        this.buttonSolarPower = createButton(100,0,"asplor.screen.world_adder.solar_power",
                buttonWidget -> {
                    try {
                        planetData.solarPower = Integer.parseInt(inputName.getText());
                    }catch (NumberFormatException n){
                        Asplor.LOGGER.info("wrong format :"+inputName.getText());
                    }
                }
        );
        this.buttonTier = createButton(150,0,"asplor.screen.world_adder.tier",
                buttonWidget -> {
                    try {
                        planetData.tier = Integer.parseInt(inputName.getText());
                    }catch (NumberFormatException n){
                        Asplor.LOGGER.info("wrong format :"+inputName.getText());
                    }
                }
        );


        this.inputName.setMaxLength(1024);
        this.inputName.setVisible(true);
        this.inputName.setEditable(true);
        this.addSelectableChild(this.inputName);

        updateColumn();

        this.setInitialFocus(inputName);
    }

    private void setFunctionButton(boolean visible){
        this.buttonAdd.visible = visible;
        this.buttonMul.visible = visible;
        this.buttonMax.visible = visible;
        this.buttonMin.visible = visible;
        this.buttonNoise.visible = visible;
        this.buttonShiftNoise.visible = visible;
        this.buttonNoiseInRange.visible = visible;
        this.buttonStart.visible = visible;
        this.buttonStop.visible = visible;
        this.buttonAddList.visible = visible;
        this.buttonRemove.visible = visible;
        this.buttonInterpolated.visible = visible;
        this.buttonYClampedGradient.visible = visible;
    }

    private void setBlockButton(boolean visible){
        this.buttonRemoveBlock.visible = visible;
        this.buttonAddBlock.visible = visible;
    }

    private void setBiomesButton(boolean visible){
        this.buttonRemoveBiome.visible = visible;
        this.buttonAddBiome.visible = visible;
    }
    private void setPlanetButton(boolean visible){
        this.buttonOxygen.visible = visible;
        this.buttonTemperature.visible = visible;
        this.buttonGravity.visible = visible;
        this.buttonSolarPower.visible = visible;
        this.buttonTier.visible = visible;
    }

    private void updateColumn(){
        this.column = Column.next(this.column);

        switch (this.column){
            case DENSITY_FUNCTION -> {
                this.buttonDone.visible = true;
                setFunctionButton(true);
                setBlockButton(false);
                setBiomesButton(false);
                setPlanetButton(false);
            }
            case BLOCK -> {
                this.buttonDone.visible = true;
                setFunctionButton(false);
                setBlockButton(true);
                setBiomesButton(false);
                setPlanetButton(false);
            }
            case BIOMES -> {
                this.buttonDone.visible = true;
                setFunctionButton(false);
                setBlockButton(false);
                setBiomesButton(true);
                setPlanetButton(false);

            }
            case PLANET -> {
                this.buttonDone.visible = true;
                setFunctionButton(false);
                setBlockButton(false);
                setBiomesButton(false);
                setPlanetButton(true);

            }
        }

        this.description = Text.translatable(
                switch (this.column){
                    case DENSITY_FUNCTION -> "asplor.screen.world_adder.function_name";
                    case BLOCK -> "asplor.screen.world_adder.blocks";
                    case BIOMES -> "asplor.screen.world_adder.biomes";
                    case PLANET -> "asplor.screen.world_adder.planet";
                }
        );
    }



    @Override
    public void tick() {
        super.tick();
        this.inputName.tick();
    }

    private void addFunctionToList(String addType,boolean ignoreInput){
        this.functionList.add(addType);
        if (!ignoreInput && !this.inputName.getText().isEmpty()){
            this.functionList.add(this.inputName.getText());
        }
    }

    private void addFunctionToList(String function,int argumentsCount){
        if (this.functionArgument.size() >= argumentsCount){
            this.functionList.add(function);
            this.functionList.addAll( functionArgument.size() == argumentsCount ? functionArgument : functionArgument.subList(0,argumentsCount));
            this.functionArgument.clear();
        }else {
            this.functionArgument.add(this.inputName.getText());
        }
    }

    private String getRenderString(String orginalString){
        Matcher isMinecraftResult = IS_MINECRAFT_ID.matcher(orginalString);
        if (isMinecraftResult.matches()){
            orginalString = isMinecraftResult.group(1);
        }

        if (orginalString.length() < 8){
            return orginalString;
        }
        return orginalString.substring(0,7);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawTextWithShadow(this.textRenderer, description, this.width / 2 - 153, 30, 10526880);
        this.inputName.render(context, mouseX, mouseY, delta);


        switch (column){
            case DENSITY_FUNCTION -> {
                for (int i = 0; i < functionArgument.size(); i++) {
                    String argumentString = functionArgument.get(i);
                    String displayString = getRenderString(argumentString);
                    Text ArgumentText = Text.literal(displayString)
                            .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID)
                                    .withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.literal(argumentString)))
                            );

                    context.drawText(textRenderer, ArgumentText, this.width / 2 - 210 + (i % 10) * 42, 10 + i / 10 * 20, 0xffffff, true);
                }
                if (!functionList.isEmpty()) {
                    for (int i = 0; i < functionList.size(); i++) {
                        String functionString = functionList.get(i);
                        String displayString = getRenderString(functionString);
                        Text functionText = Text.literal(displayString)
                                .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID)
                                        .withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.literal(functionString)))
                                );


                        context.drawText(textRenderer, functionText, this.width / 2 - 210 + (i % 10) * 42, 100 + i / 10 * 20, 0xffffff, true);


                    }
                }
            }
            case BLOCK -> {
                for (int i = 0; i < blockList.size(); i++) {
                    String blockId = blockList.get(i);
                    Text ArgumentText = Text.literal(blockId)
                            .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID)
                                    .withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.literal(blockId)))
                            );

                    context.drawText(textRenderer, ArgumentText, this.width / 2 - 210 + (i % 3) * 111, 100 + i / 3 * 20, 0xffffff, true);
                }
            }
            case BIOMES -> {
                for (int i = 0; i < biomesIdList.size(); i++) {
                    String blockId = biomesIdList.get(i);
                    Text ArgumentText = Text.literal(blockId)
                            .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID)
                                    .withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.literal(blockId)))
                            );

                    context.drawText(textRenderer, ArgumentText, this.width / 2 - 210 + (i % 3) * 111, 100 + i / 3 * 20, 0xffffff, true);
                }
            }
            case PLANET -> {
                List<String> info = List.of(
                        "oxygen " + planetData.oxygen,"temperature " + planetData.temperature,"gravity " + planetData.gravity,
                        "solarPower " + planetData.solarPower,"tier " + planetData.tier
                );
                for (int i = 0; i < info.size(); i++) {
                    String string = info.get(i);
                    Text ArgumentText = Text.literal(string)
                            .setStyle(Style.EMPTY.withFont(Style.DEFAULT_FONT_ID)
                                    .withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.literal(string)))
                            );

                    context.drawText(textRenderer, ArgumentText, this.width / 2 - 210 + (i % 3) * 111, 100 + i / 3 * 20, 0xffffff, true);
                }
            }
        }
    }
    public void createWorld(){
        CreateWorldC2SPacket.send(functionList,blockList,biomesIdList,planetData);
    }
    private enum Column{
        DENSITY_FUNCTION,
        BLOCK,
        BIOMES,
        PLANET;
        private static final List<Column> COLUMNS = List.of(DENSITY_FUNCTION,BLOCK,BIOMES,PLANET);
        public static Column next(Column column){
            int index = COLUMNS.indexOf(column) + 1;
           return COLUMNS.get(index % COLUMNS.size());
        }
    }


}
