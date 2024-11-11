package astrinox.stellum.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.JsonOps;

import astrinox.stellum.Stellum;
import astrinox.stellum.handlers.explosion.BurnMap;
import astrinox.stellum.handlers.explosion.BurnZone;
import astrinox.stellum.handlers.explosion.Burnable;
import astrinox.stellum.handlers.explosion.BurnableIO;
import astrinox.stellum.handlers.explosion.ExplosionHandler;
import astrinox.stellum.handlers.screenshake.Screenshake;
import astrinox.stellum.handlers.screenshake.ScreenshakeHandler;
import astrinox.stellum.registry.BurnMapRegistry;
import astrinox.stellum.util.PerlinNoiseHelper;
import net.minecraft.block.Blocks;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static net.minecraft.server.command.CommandManager.*;

public class StellumDebugCommand {
    public static final SimpleCommandExceptionType NON_PLAYER = new SimpleCommandExceptionType(
            Text.literal("This command can only be used by players"));

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceDispatcher,
            CommandRegistryAccess commandRegistryAccess,
            CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceDispatcher.register(literal("stellum")
                .then(literal("debug")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(literal("screenshake")
                                .then(argument("intensity",
                                        FloatArgumentType.floatArg())
                                        .then(argument("durationMs",
                                                IntegerArgumentType
                                                        .integer())
                                                .then(argument("fade",
                                                        BoolArgumentType.bool())
                                                        .executes(StellumDebugCommand::executeScreenshake)))))
                        .then(literal("noise").then(argument("size",
                                IntegerArgumentType.integer())
                                .then(argument("noiseScale",
                                        DoubleArgumentType.doubleArg())
                                        .then(argument("seed",
                                                IntegerArgumentType
                                                        .integer())
                                                .executes(StellumDebugCommand::executeNoise)))))
                        .then(literal("explosion")
                                .then(argument("size", IntegerArgumentType.integer())
                                        .then(argument("noiseScale",
                                                DoubleArgumentType
                                                        .doubleArg())
                                                .then(argument("noiseMultiplier",
                                                        FloatArgumentType
                                                                .floatArg())
                                                        .then(argument("damage",
                                                                FloatArgumentType
                                                                        .floatArg())
                                                                .then(argument("burnBlocks",
                                                                        BoolArgumentType.bool())
                                                                        .then(argument("burnSize",
                                                                                IntegerArgumentType
                                                                                        .integer())
                                                                                .executes(
                                                                                        StellumDebugCommand::executeExplosion))))))))
                        .then(literal("burnzone")
                                .then(argument("size", IntegerArgumentType.integer())
                                        .then(argument("explosionSize",
                                                IntegerArgumentType
                                                        .integer())
                                                .then(argument("noiseScale",
                                                        DoubleArgumentType
                                                                .doubleArg())
                                                        .then(argument("noiseMultiplier",
                                                                FloatArgumentType
                                                                        .floatArg())
                                                                .executes(
                                                                        StellumDebugCommand::executeBurnzone))))))));
    }

    public static int executeScreenshake(CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        if (!(source.getEntity().isPlayer())) {
            throw NON_PLAYER.create();
        }
        float intensity = context.getArgument("intensity", float.class);
        int durationMs = context.getArgument("durationMs", int.class);
        boolean fade = context.getArgument("fade", boolean.class);
        ScreenshakeHandler.addScreenshake(new Screenshake(intensity, durationMs, fade));
        return 1;
    }

    public static int executeNoise(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        World world = source.getWorld();
        double x = source.getPosition().x;
        double y = source.getPosition().y;
        double z = source.getPosition().z;
        PerlinNoiseHelper noise = new PerlinNoiseHelper(context.getArgument("seed", int.class),
                context.getArgument("noiseScale", double.class));
        int size = context.getArgument("size", int.class);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    if (noise.noise(x + i, y + j, z + k) > 0) {
                        world.setBlockState(new BlockPos((int) x + i, (int) y + j, (int) z + k),
                                Blocks.STONE.getDefaultState());
                    }
                }
            }
        }
        return 1;
    }

    public static int executeExplosion(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        World world = source.getWorld();
        double noiseScale = context.getArgument("noiseScale", double.class);
        float noiseMultiplier = context.getArgument("noiseMultiplier", float.class);

        BurnMap burnMap = new BurnMap()
                .addBurnables(
                        new BurnableIO(
                                new Burnable().addTag(BlockTags.LEAVES),
                                new Burnable().addBlock(Blocks.MANGROVE_ROOTS
                                        .getDefaultState())))
                .addBurnables(
                        new BurnableIO(
                                new Burnable().addTag(BlockTags.BASE_STONE_OVERWORLD),
                                new Burnable().addBlock(
                                        Blocks.COBBLESTONE.getDefaultState())
                                        .addBlock(Blocks.COBBLED_DEEPSLATE
                                                .getDefaultState())
                                        .addBlock(Blocks.MAGMA_BLOCK
                                                .getDefaultState())))
                .addBurnables(
                        new BurnableIO(
                                new Burnable().addTag(BlockTags.LOGS),
                                new Burnable().addBlock(Blocks.POLISHED_BASALT
                                        .getDefaultState())))
                .addBurnables(
                        new BurnableIO(
                                new Burnable().addTag(BlockTags.DIRT),
                                new Burnable().addBlock(Blocks.DEAD_FIRE_CORAL_BLOCK
                                        .getDefaultState())
                                        .addBlock(Blocks.COARSE_DIRT
                                                .getDefaultState())
                                        .addBlock(Blocks.MAGMA_BLOCK
                                                .getDefaultState())))
                .addBurnables(
                        new BurnableIO(
                                new Burnable().addTag(BlockTags.FLOWERS)
                                        .addBlock(Blocks.SHORT_GRASS
                                                .getDefaultState())
                                        .addBlock(Blocks.TALL_GRASS
                                                .getDefaultState()),
                                new Burnable()
                                        .addBlock(Blocks.DEAD_BUBBLE_CORAL
                                                .getDefaultState()
                                                .with(Properties.WATERLOGGED,
                                                        false))
                                        .addBlock(Blocks.DEAD_BUSH
                                                .getDefaultState())));

        ExplosionHandler explosion = new ExplosionHandler()
                .setPos(new BlockPos(
                        (int) source.getPosition().x,
                        (int) source.getPosition().y,
                        (int) source.getPosition().z))
                .setSize(context.getArgument("size", int.class))
                .setNoiseScale(noiseScale)
                .setNoiseMultiplier(noiseMultiplier)
                .setHurtEntities(true)
                .setBreakBlocks(true)
                .setDamage(context.getArgument("damage", float.class))
                .setBurnBlocks(context.getArgument("burnBlocks", boolean.class))
                .setBurnSize(context.getArgument("burnSize", int.class))
                .setBurnMap(burnMap);

        explosion.trigger(world);

        return 1;
    }

    public static int executeBurnzone(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        try {
            World world = source.getWorld();
            double noiseScale = context.getArgument("noiseScale", double.class);
            float noiseMultiplier = context.getArgument("noiseMultiplier", float.class);
            int size = context.getArgument("size", int.class);
            int explosionSize = context.getArgument("explosionSize", int.class);

            BurnMap burnMap = new BurnMap()
                    .addBurnables(
                            new BurnableIO(
                                    new Burnable().addTag(BlockTags.LEAVES),
                                    new Burnable().addBlock(Blocks.MANGROVE_ROOTS
                                            .getDefaultState())))
                    .addBurnables(
                            new BurnableIO(
                                    new Burnable().addTag(BlockTags.BASE_STONE_OVERWORLD),
                                    new Burnable().addBlock(
                                            Blocks.COBBLESTONE.getDefaultState())
                                            .addBlock(Blocks.COBBLED_DEEPSLATE
                                                    .getDefaultState())
                                            .addBlock(Blocks.MAGMA_BLOCK
                                                    .getDefaultState())))
                    .addBurnables(
                            new BurnableIO(
                                    new Burnable().addTag(BlockTags.LOGS),
                                    new Burnable().addBlock(Blocks.POLISHED_BASALT
                                            .getDefaultState())))
                    .addBurnables(
                            new BurnableIO(
                                    new Burnable().addTag(BlockTags.DIRT),
                                    new Burnable().addBlock(Blocks.DEAD_FIRE_CORAL_BLOCK
                                            .getDefaultState())
                                            .addBlock(Blocks.COARSE_DIRT
                                                    .getDefaultState())
                                            .addBlock(Blocks.MAGMA_BLOCK
                                                    .getDefaultState())))
                    .addBurnables(
                            new BurnableIO(
                                    new Burnable().addTag(BlockTags.FLOWERS)
                                            .addBlock(Blocks.SHORT_GRASS
                                                    .getDefaultState())
                                            .addBlock(Blocks.TALL_GRASS
                                                    .getDefaultState()),
                                    new Burnable()
                                            .addBlock(Blocks.DEAD_BUBBLE_CORAL
                                                    .getDefaultState()
                                                    .with(Properties.WATERLOGGED,
                                                            false))
                                            .addBlock(Blocks.DEAD_BUSH
                                                    .getDefaultState())));

            BurnZone burnzone = new BurnZone()
                    .setPos(new BlockPos(
                            (int) source.getPosition().x,
                            (int) source.getPosition().y,
                            (int) source.getPosition().z))
                    .setSize(size)
                    .setExplosionSize(explosionSize)
                    .setNoiseScale(noiseScale)
                    .setNoiseMultiplier(noiseMultiplier)
                    .setBurnMap(burnMap);

            burnzone.trigger(world);
        } catch (Exception e) {
            source.sendError(Text.literal(
                    "An error occurred while executing the burnzone command: " + e.toString()));
        }

        return 1;
    }
}
