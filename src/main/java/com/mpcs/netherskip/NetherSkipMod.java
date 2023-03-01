package com.mpcs.netherskip;

import com.mpcs.netherskip.mixin.EntityAccessors;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class NetherSkipMod implements ModInitializer, ClientModInitializer {

	public static final String MODID = "netherskip";

	public static KeyBinding keyBinding;
	public static Identifier packetIdentifier = new Identifier(MODID, "keypacket");
	public static boolean autoTeleportDisabled = true;

	@Override
	public void onInitialize() {
		Config config = new Config(MODID);
		if(!config.exists()) {
			config.load();
			config.setBool("disable_auto_teleport", true);
			config.save();
		}

		config.load();
		autoTeleportDisabled = config.getBool("disable_auto_teleport");

		ServerPlayNetworking.registerGlobalReceiver(packetIdentifier, (server, player, handler, buf, responseSender) -> {
			EntityAccessors ea = ((EntityAccessors)player);
			if (ea.getInNetherPortal()) {
				ea.setNetherPortalTime(999999);
			}
		});
	}

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
			if (((EntityAccessors)MinecraftClient.getInstance().player).getInNetherPortal()) {
				matrixStack.push();
				matrixStack.scale(3.0f, 3.0f, 3.0f);
				TextRenderer textRenderer = MinecraftClient.getInstance().inGameHud.getTextRenderer();
				String messageText = Text.translatable("message.netherskip.teleport", keyBinding.getBoundKeyLocalizedText()).getString();
				DrawableHelper.drawCenteredTextWithShadow(matrixStack, textRenderer, OrderedText.styledForwardsVisitedString(messageText, Style.EMPTY), MinecraftClient.getInstance().getWindow().getWidth()/(4*3), 5, 0xA0A0A0);
				matrixStack.pop();
			}
		});

		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.netherskip.skipkey",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_V,
				KeyBinding.GAMEPLAY_CATEGORY
		));

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			while (keyBinding.wasPressed()) {
				if(((EntityAccessors)client.player).getInNetherPortal()) {
					PacketByteBuf buf = PacketByteBufs.create();
					ClientPlayNetworking.send(NetherSkipMod.packetIdentifier, buf);
				}
			}
		});

	}
}
