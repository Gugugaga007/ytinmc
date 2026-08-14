/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.cinemamod.mcef.MCEF
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphicsExtractor
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.input.CharacterEvent
 *  net.minecraft.client.input.KeyEvent
 *  net.minecraft.client.input.MouseButtonEvent
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.Identifier
 *  org.cef.browser.CefBrowser
 *  org.cef.browser.CefFrame
 *  org.cef.handler.CefDisplayHandler
 *  org.cef.handler.CefDisplayHandlerAdapter
 */
package com.ytinmc.client;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.ytinmc.client.YtinmcModClient;
import com.ytinmc.network.WatchPartyNetwork;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandler;
import org.cef.handler.CefDisplayHandlerAdapter;

public class YoutubeScreen
extends Screen {
    private MCEFBrowser browser;
    private final Minecraft minecraft = Minecraft.getInstance();
    public static boolean isPipMode = false;
    public static boolean isFullscreen = false;
    public static MCEFBrowser pipBrowser = null;
    private static final int TITLE_BAR_HEIGHT = 34;
    private static final int MARGIN = 18;
    private EditBox urlBar;
    private static boolean hasAddedDisplayHandler = false;

    public static void drawBrowser(int bX, int bY, int bW, int bH, MCEFBrowser b, GuiGraphicsExtractor context) {
        if (b == null || b.getRenderer() == null) {
            return;
        }
        Identifier textureId = b.getRenderer().getTextureId();
        if (textureId != null) {
            context.blit(textureId, bX, bY, bW, bH, 0.0f, 1.0f, 0.0f, 1.0f);
        }
    }

    public YoutubeScreen() {
        super((Component)Component.literal((String)"Browser"));
    }

    protected void init() {
        super.init();
        if (pipBrowser != null) {
            this.browser = pipBrowser;
        } else if (this.browser == null) {
            String url = "https://www.youtube.com";
            pipBrowser = this.browser = MCEF.createBrowser((String)url, false);
        }
        if (!hasAddedDisplayHandler) {
            MCEF.getClient().addDisplayHandler(new CefDisplayHandlerAdapter() {
                @Override
                public void onAddressChange(CefBrowser b, CefFrame frame, String url) {
                    if (frame.isMain() && b == pipBrowser) {
                        b.executeJavaScript("document.documentElement.style.zoom = '80%';", "", 0);
                        Minecraft.getInstance().execute(() -> {
                            Screen patt0$temp = Minecraft.getInstance().screen;
                            if (patt0$temp instanceof YoutubeScreen) {
                                YoutubeScreen ys = (YoutubeScreen)patt0$temp;
                                ys.injectCustomCss();
                                if (ys.urlBar != null && !ys.urlBar.isFocused()) {
                                    ys.urlBar.setValue(url);
                                }
                            }
                        });
                    }
                }
            });
            hasAddedDisplayHandler = true;
        }
        if (this.browser != null) {
            this.browser.setZoomLevel(-1.5);
            this.browser.executeJavaScript("document.documentElement.style.zoom = '80%';", "", 0);
        }
        this.setupUrlBar();
        this.resizeBrowser();
    }

    private void setupUrlBar() {
        this.clearWidgets();
        if (!isFullscreen) {
            int winX = this.getWindowX();
            int winWidth = this.getWindowWidth();
            int headerY = this.getWindowY();
            int reloadX = winX + 60;
            int urlX = reloadX + 30;
            int closeX = winX + winWidth - 34;
            int pipX = closeX - 60;
            int syncX = pipX - 65;
            int urlBarWidth = Math.max(100, syncX - urlX - 8);
            this.urlBar = new EditBox(this.minecraft.font, urlX, headerY + 5, urlBarWidth, 24, (Component)Component.literal((String)"Search or enter URL"));
            this.urlBar.setMaxLength(2048);
            if (this.browser != null && this.browser.getURL() != null) {
                this.urlBar.setValue(this.browser.getURL());
            } else {
                this.urlBar.setValue("https://www.youtube.com");
            }
            this.addRenderableWidget(this.urlBar);
        }
    }

    public void toggleFullscreen() {
        isFullscreen = !isFullscreen;
        this.setupUrlBar();
        this.resizeBrowser();
    }

    public void enablePipAndClose() {
        isPipMode = true;
        if (this.browser != null) {
            double scale = 1.0;
            int pW = (int)((double)YtinmcModClient.pipWidth * scale);
            int pH = (int)((double)YtinmcModClient.pipHeight * scale);
            this.browser.resize(Math.max(100, pW), Math.max(100, pH));
        }
        this.onClose();
    }

    public void terminateSessionAndClose() {
        isPipMode = false;
        if (this.browser != null) {
            try {
                this.browser.loadURL("about:blank");
                this.browser.executeJavaScript("window.stop(); document.querySelectorAll('video, audio').forEach(m => { m.pause(); m.src = ''; });", "", 0);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.browser.close(false);
        }
        pipBrowser = null;
        this.browser = null;
        this.onClose();
    }

    private int getWindowX() {
        return isFullscreen ? 0 : Math.max(10, 18);
    }

    private int getWindowY() {
        return isFullscreen ? 0 : Math.max(10, 18);
    }

    private int getWindowWidth() {
        return isFullscreen ? this.width : Math.max(200, this.width - 36);
    }

    private int getWindowHeight() {
        return isFullscreen ? this.height : Math.max(150, this.height - 36);
    }

    private int getBrowserX() {
        return this.getWindowX();
    }

    private int getBrowserY() {
        return isFullscreen ? 0 : this.getWindowY() + 34;
    }

    private int getBrowserWidth() {
        return this.getWindowWidth();
    }

    private int getBrowserHeight() {
        return isFullscreen ? this.height : Math.max(50, this.getWindowHeight() - 34);
    }

    public void injectCustomCss() {
        if (this.browser != null) {
            String js = "(function() { var s = document.getElementById('ytinmc-style'); if (!s) { s = document.createElement('style'); s.id = 'ytinmc-style'; (document.head || document.documentElement).appendChild(s); } s.textContent = 'html, body, #content, ytd-app { background-color: #0f0f0f !important; min-height: 100% !important; height: 100% !important; } ytd-browse { min-height: 100% !important; }'; if (window.location.href.indexOf('watch') !== -1) { var btn = document.querySelector('button.ytp-size-button'); if (btn && !document.querySelector('ytd-watch-flexy[theater]')) { btn.click(); } } })();";
            this.browser.executeJavaScript(js, "", 0);
        }
    }

    private void resizeBrowser() {
        if (this.browser != null && this.width > 50 && this.height > 50) {
            int bW = this.getBrowserWidth();
            int bH = this.getBrowserHeight();
            this.browser.resize(Math.max(100, bW), Math.max(100, bH));
            this.injectCustomCss();
        }
    }

    public void resize(int w, int h) {
        super.resize(w, h);
        this.setupUrlBar();
        this.resizeBrowser();
    }

    public void onClose() {
        if (this.browser != null) {
            if (!isPipMode) {
                this.browser.executeJavaScript("document.querySelectorAll('video').forEach(v => v.pause());", "", 0);
            } else {
                int pW = YtinmcModClient.pipWidth;
                int pH = YtinmcModClient.pipHeight;
                this.browser.resize(Math.max(100, pW), Math.max(100, pH));
            }
            this.browser.sendMouseRelease(0, 0, 0);
            this.browser.setFocus(false);
        }
        super.onClose();
    }

    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (this.browser != null) {
            if (!isFullscreen) {
                int winX = this.getWindowX();
                int winY = this.getWindowY();
                int winW = this.getWindowWidth();
                int winH = this.getWindowHeight();
                int headerY = this.getWindowY();
                int btnY = headerY + 5;
                int btnH = 24;
                context.fill(winX - 3, winY - 3, winX + winW + 3, winY + winH + 3, -16053490);
                context.fill(winX - 1, winY - 1, winX + winW + 1, winY + winH + 1, -14013896);
                context.fill(winX, winY, winX + winW, winY + winH, -15461352);
                context.fill(winX, winY, winX + winW, winY + 34, -15000798);
                context.fill(winX, winY + 34 - 1, winX + winW, winY + 34, -14145484);
                int backX = winX + 6;
                int backW = 24;
                int fwdX = winX + 33;
                int fwdW = 24;
                int reloadX = winX + 60;
                int reloadW = 24;
                int closeW = 28;
                int closeX = winX + winW - 34;
                int pipW = 55;
                int pipX = closeX - 60;
                int syncW = 60;
                int syncX = pipX - 65;
                boolean isBackHover = mouseX >= backX && mouseX < backX + backW && mouseY >= btnY && mouseY < btnY + btnH;
                context.fill(backX, btnY, backX + backW, btnY + btnH, isBackHover ? -13750724 : -14540244);
                context.text(this.minecraft.font, "<-", backX + 8, btnY + 7, isBackHover ? -16711732 : -5197632, false);
                boolean isFwdHover = mouseX >= fwdX && mouseX < fwdX + fwdW && mouseY >= btnY && mouseY < btnY + btnH;
                context.fill(fwdX, btnY, fwdX + fwdW, btnY + btnH, isFwdHover ? -13750724 : -14540244);
                context.text(this.minecraft.font, "->", fwdX + 8, btnY + 7, isFwdHover ? -16711732 : -5197632, false);
                boolean isReloadHover = mouseX >= reloadX && mouseX < reloadX + reloadW && mouseY >= btnY && mouseY < btnY + btnH;
                context.fill(reloadX, btnY, reloadX + reloadW, btnY + btnH, isReloadHover ? -13750724 : -14540244);
                context.text(this.minecraft.font, "R", reloadX + 7, btnY + 7, isReloadHover ? -16711732 : -5197632, false);
                boolean isSyncHover = mouseX >= syncX && mouseX < syncX + syncW && mouseY >= btnY && mouseY < btnY + btnH;
                context.fill(syncX, btnY, syncX + syncW, btnY + btnH, isSyncHover ? -15681151 : -16411031);
                context.text(this.minecraft.font, "Sync", syncX + 8, btnY + 7, -1, false);
                boolean isPipHover = mouseX >= pipX && mouseX < pipX + pipW && mouseY >= btnY && mouseY < btnY + btnH;
                context.fill(pipX, btnY, pipX + pipW, btnY + btnH, isPipHover ? -10262799 : -11581723);
                context.text(this.minecraft.font, "PiP", pipX + 10, btnY + 7, -1, false);
                boolean isCloseHover = mouseX >= closeX && mouseX < closeX + closeW && mouseY >= btnY && mouseY < btnY + btnH;
                context.fill(closeX, btnY, closeX + closeW, btnY + btnH, isCloseHover ? -1096636 : -2349530);
                context.text(this.minecraft.font, "X", closeX + 10, btnY + 7, -1, false);
            }
            int bX = this.getBrowserX();
            int bY = this.getBrowserY();
            int bW = this.getBrowserWidth();
            int bH = this.getBrowserHeight();
            YoutubeScreen.drawBrowser(bX, bY, bW, bH, this.browser, context);
        }
        super.extractRenderState(context, mouseX, mouseY, delta);
    }

    private boolean isInsideBrowser(double mouseX, double mouseY) {
        int bX = this.getBrowserX();
        int bY = this.getBrowserY();
        int bW = this.getBrowserWidth();
        int bH = this.getBrowserHeight();
        return mouseX >= (double)bX && mouseX < (double)(bX + bW) && mouseY >= (double)bY && mouseY < (double)(bY + bH);
    }

    private int getRelativeBrowserX(double mouseX) {
        return (int)(mouseX - (double)this.getBrowserX());
    }

    private int getRelativeBrowserY(double mouseY) {
        return (int)(mouseY - (double)this.getBrowserY());
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean isFocused) {
        double mX = event.x();
        double mY = event.y();
        int button = event.button();
        if (!isFullscreen) {
            int winX = this.getWindowX();
            int winWidth = this.getWindowWidth();
            int headerY = this.getWindowY();
            int btnY = headerY + 5;
            int btnH = 24;
            int backX = winX + 6;
            int backW = 24;
            int fwdX = winX + 33;
            int fwdW = 24;
            int reloadX = winX + 60;
            int reloadW = 24;
            int closeW = 28;
            int closeX = winX + winWidth - 34;
            int pipW = 55;
            int pipX = closeX - 60;
            int syncW = 60;
            int syncX = pipX - 65;
            if (mY >= (double)btnY && mY < (double)(btnY + btnH)) {
                if (mX >= (double)backX && mX < (double)(backX + backW)) {
                    if (this.browser != null) {
                        this.browser.goBack();
                    }
                    return true;
                }
                if (mX >= (double)fwdX && mX < (double)(fwdX + fwdW)) {
                    if (this.browser != null) {
                        this.browser.goForward();
                    }
                    return true;
                }
                if (mX >= (double)reloadX && mX < (double)(reloadX + reloadW)) {
                    if (this.browser != null) {
                        this.browser.reload();
                    }
                    return true;
                }
                if (mX >= (double)syncX && mX < (double)(syncX + syncW)) {
                    String currentUrl = this.browser != null && this.browser.getURL() != null ? this.browser.getURL() : this.urlBar.getValue();
                    WatchPartyNetwork.sendPlayVideoPacket(currentUrl);
                    return true;
                }
                if (mX >= (double)pipX && mX < (double)(pipX + pipW)) {
                    this.enablePipAndClose();
                    return true;
                }
                if (mX >= (double)closeX && mX < (double)(closeX + closeW)) {
                    this.terminateSessionAndClose();
                    return true;
                }
            }
        }
        if (button == 3 || button == 3) {
            if (this.browser != null) {
                this.browser.goBack();
                return true;
            }
        } else if ((button == 4 || button == 4) && this.browser != null) {
            this.browser.goForward();
            return true;
        }
        if (this.isInsideBrowser(mX, mY) && (this.urlBar == null || !this.urlBar.isMouseOver(mX, mY))) {
            this.browser.sendMousePress(this.getRelativeBrowserX(mX), this.getRelativeBrowserY(mY), button);
            this.browser.setFocus(true);
        }
        return super.mouseClicked(event, isFocused);
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (this.browser != null) {
            this.browser.sendMouseRelease(this.getRelativeBrowserX(mouseX), this.getRelativeBrowserY(mouseY), button);
            this.browser.setFocus(true);
        }
        return super.mouseReleased(event);
    }

    public void mouseMoved(double mouseX, double mouseY) {
        if (this.isInsideBrowser(mouseX, mouseY)) {
            this.browser.sendMouseMove(this.getRelativeBrowserX(mouseX), this.getRelativeBrowserY(mouseY));
        }
        super.mouseMoved(mouseX, mouseY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.isInsideBrowser(mouseX, mouseY)) {
            long win = Minecraft.getInstance().getWindow().handle();
            boolean ctrl = org.lwjgl.glfw.GLFW.glfwGetKey(win, 341) == 1 || org.lwjgl.glfw.GLFW.glfwGetKey(win, 345) == 1;
            int modifiers = ctrl ? 2 : 0;
            this.browser.sendMouseWheel(this.getRelativeBrowserX(mouseX), this.getRelativeBrowserY(mouseY), scrollY, modifiers);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void handleUrlSubmit() {
        if (this.urlBar != null && this.browser != null) {
            Object target = this.urlBar.getValue().trim();
            if (!((String)target).isEmpty()) {
                if (!((String)target).startsWith("http://") && !((String)target).startsWith("https://")) {
                    target = ((String)target).contains(".") && !((String)target).contains(" ") ? "https://" + (String)target : "https://www.google.com/search?q=" + URLEncoder.encode((String)target, StandardCharsets.UTF_8);
                }
                this.browser.loadURL((String)target);
                this.browser.setZoomLevel(-1.5);
                this.browser.executeJavaScript("document.documentElement.style.zoom = '80%';", "", 0);
            }
            this.urlBar.setFocused(false);
        }
    }

    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        if (this.urlBar != null && this.urlBar.isFocused()) {
            if (keyCode == 257 || keyCode == 335) {
                this.handleUrlSubmit();
                return true;
            }
            return super.keyPressed(event);
        }
        if (keyCode == 80) {
            this.enablePipAndClose();
            return true;
        }
        if (keyCode == 300) {
            this.toggleFullscreen();
            return true;
        }
        if (keyCode == 256) {
            if (isFullscreen) {
                this.toggleFullscreen();
            } else {
                this.terminateSessionAndClose();
            }
            return true;
        }
        if (this.browser != null) {
            this.browser.sendKeyPress(keyCode, scanCode, modifiers);
            this.browser.setFocus(true);
        }
        return super.keyPressed(event);
    }

    public boolean keyReleased(KeyEvent event) {
        int keyCode = event.key();
        int scanCode = event.scancode();
        int modifiers = event.modifiers();
        if (this.urlBar != null && this.urlBar.isFocused()) {
            return super.keyReleased(event);
        }
        if (this.browser != null) {
            this.browser.sendKeyRelease(keyCode, scanCode, modifiers);
            this.browser.setFocus(true);
        }
        return super.keyReleased(event);
    }

    public boolean charTyped(CharacterEvent event) {
        char chr = (char)event.codepoint();
        if (this.urlBar != null && this.urlBar.isFocused()) {
            return super.charTyped(event);
        }
        if (chr == '\u0000') {
            return false;
        }
        if (this.browser != null) {
            this.browser.sendKeyTyped(chr, 0);
            this.browser.setFocus(true);
        }
        return super.charTyped(event);
    }
}

