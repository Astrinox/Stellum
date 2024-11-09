package astrinox.stellum.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import astrinox.stellum.handlers.screenshake.Screenshake;
import astrinox.stellum.handlers.screenshake.ScreenshakeHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class DebugCommand {
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceDispatcher,
            CommandRegistryAccess commandRegistryAccess,
            CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceDispatcher.register(literal("stellum")
                .then(literal("debug")
                        .then(literal("screenshake")
                                .then(argument("intensity", IntegerArgumentType.integer())
                                        .then(argument("durationMs", IntegerArgumentType.integer())
                                                .then(argument("fade", BoolArgumentType.bool())
                                                        .executes(DebugCommand::executeScreenshake)))))));
    }

    public static int executeScreenshake(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        int intensity = context.getArgument("intensity", int.class);
        int durationMs = context.getArgument("durationMs", int.class);
        boolean fade = context.getArgument("fade", boolean.class);
        ScreenshakeHandler.addScreenshake(new Screenshake(intensity, durationMs, fade));
        return 1;
    }
}
