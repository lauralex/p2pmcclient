/*
 * Copyright 2023 Alessandro Bellia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bell;

import com.sun.management.OperatingSystemMXBean;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.security.NoSuchAlgorithmException;

public class P2PScreen extends MultiplayerScreen {
    private ButtonWidget hostServerButton;

    @Override
    public void tick() {
        this.serverListPinger.tick();
    }

    public P2PScreen(Screen parent, Text title) {
        super(parent);
        this.title = title;
    }

    @Override
    protected void init() {
        if (this.initialized) {
            this.serverListWidget.updateSize(this.width, this.height, 32, this.height - 64);
        } else {
            this.initialized = true;
            this.serverList = new ServerList(this.client);
            PeerAddressDTO[] peers = P2PUtils.fetchActiveIPs();
            int count = 0;
            for (PeerAddressDTO peer : peers) {
                this.serverList.add(new ServerInfo(String.valueOf(count), peer.getIp(), false), false);
                count++;
            }
            if (count == 0) {
                hostServerButton = ButtonWidget.builder(Text.literal("Host Server"), button -> {
                    try {
                        PurpurDownloader.downloadPurpur();
                        // ProcessBuilder pb = new ProcessBuilder("java", "-jar", PurpurDownloader.getSavePath());
                        /*

                        "java",
                        "-Xms10240M",
                        "-Xmx10240M",
                        "--add-modules=jdk.incubator.vector",
                        "-XX:+UseG1GC",
                        "-XX:+ParallelRefProcEnabled",
                        "-XX:MaxGCPauseMillis=200",
                        "-XX:+UnlockExperimentalVMOptions",
                        "-XX:+DisableExplicitGC",
                        "-XX:+AlwaysPreTouch",
                        "-XX:G1HeapWastePercent=5",
                        "-XX:G1MixedGCCountTarget=4",
                        "-XX:InitiatingHeapOccupancyPercent=15",
                        "-XX:G1MixedGCLiveThresholdPercent=90",
                        "-XX:G1RSetUpdatingPauseTimePercent=5",
                        "-XX:SurvivorRatio=32",
                        "-XX:+PerfDisableSharedMem",
                        "-XX:MaxTenuringThreshold=1",
                        "-Dusing.aikars.flags=https://mcflags.emc.gs",
                        "-Daikars.new.flags=true",
                        "-XX:G1NewSizePercent=30",
                        "-XX:G1MaxNewSizePercent=40",
                        "-XX:G1HeapRegionSize=8M",
                        "-XX:G1ReservePercent=20",
                        "-jar", "server.jar",
                        "--nogui",
                         */
                        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                        long totalMemorySize = os.getTotalMemorySize() / 1024 / 1024 / 1024;

                        // Assign half of the total memory to the Minecraft server, but no more than 10GB
                        long memoryToAssign = Math.min(totalMemorySize / 2, 10);

                        // Execute the jar file with Aikar's flags

                        ProcessBuilder pb = new ProcessBuilder(
                                "cmd.exe",
                                "/c",
                                "start",
                                "cmd",
                                "/k",
                                "java",
                                // "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005",
                                "-Xms" + memoryToAssign + "G",
                                "-Xmx" + memoryToAssign + "G",
                                "--add-modules=jdk.incubator.vector",
                                "-XX:+UseG1GC",
                                "-XX:+ParallelRefProcEnabled",
                                "-XX:MaxGCPauseMillis=200",
                                "-XX:+UnlockExperimentalVMOptions",
                                "-XX:+DisableExplicitGC",
                                "-XX:+AlwaysPreTouch",
                                "-XX:G1HeapWastePercent=5",
                                "-XX:G1MixedGCCountTarget=4",
                                "-XX:InitiatingHeapOccupancyPercent=15",
                                "-XX:G1MixedGCLiveThresholdPercent=90",
                                "-XX:G1RSetUpdatingPauseTimePercent=5",
                                "-XX:SurvivorRatio=32",
                                "-XX:+PerfDisableSharedMem",
                                "-XX:MaxTenuringThreshold=1",
                                "-Dusing.aikars.flags=https://mcflags.emc.gs",
                                "-Daikars.new.flags=true",
                                "-XX:G1NewSizePercent=30",
                                "-XX:G1MaxNewSizePercent=40",
                                "-XX:G1HeapRegionSize=8M",
                                "-XX:G1ReservePercent=20",
                                "-jar", PurpurDownloader.getSavePath(), "--nogui");
                        pb.directory(new File(PurpurDownloader.getSavePath()).getParentFile());
                        // pb.inheritIO();
                        Process p = pb.start();
                    } catch (IOException | EulaException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }).build();

                this.addDrawableChild(hostServerButton);
            }

            this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36);
            this.serverListWidget.setServers(this.serverList);
            this.addSelectableChild(this.serverListWidget);
            // this.serverListWidget.addEntry(serverListWidget.new ServerEntry(this, new ServerInfo("P2P", "19.81.33.46", false)));
        }
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    //    @Override
//    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//        this.renderBackground(matrices);
//        this.serverListWidget.render(matrices, mouseX, mouseY, delta);
//        drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
//        // super.render(matrices, mouseX, mouseY, delta);
//    }


    @Override
    protected void updateButtonActivationStates() {

    }
}
