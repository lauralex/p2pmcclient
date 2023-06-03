package org.bell.mixin.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.bell.P2PScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MultiPlayerScreenMixin extends Screen {

    @Shadow
    protected abstract void updateButtonActivationStates();

    protected MultiPlayerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addP2PButton(CallbackInfo info) {
        ButtonWidget p2pButton = ButtonWidget.builder(Text.literal("P2P"), button -> this.client.setScreen(new P2PScreen((MultiplayerScreen) (Object) this, Text.literal("P2P")))).build();

        this.addDrawableChild(p2pButton);
        this.updateButtonActivationStates();
    }
}