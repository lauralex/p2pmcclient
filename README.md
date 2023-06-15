# P2P Minecraft Project - Client side
## Description
Create a P2P Minecraft system using Purpur API (for the server side) and FabricMC (for the client side). FastAPI is used for the backend service.

## Demo
https://github.com/lauralex/p2pmcclient/assets/5181524/2ba73218-eb34-414e-9596-f1c1a3f7d068

# How to Setup the FabricMC Mod
1. **Install Fabric Loader:**
    - Visit the [FabricMC website](https://fabricmc.net/use/).
    - Download the latest version of Fabric Loader that corresponds to your Minecraft version.
    - Run the installer .jar file and choose the "Install" option. Make sure the Minecraft version selected in the installer matches your Minecraft version. This will create a new profile in your Minecraft launcher.

2. **Download and Extract Artifacts.zip:**
    - Download the latest Artifacts.zip file: check the [Latest Artifacts](#latest-artifacts).
    - Locate the Artifacts.zip file in your downloads or wherever you have saved it.
    - Extract the .zip file. You should see a .jar file for the mod named `p2pmcclient-1.0.0.jar`.

4. **Place the Mod in Minecraft's Mods Folder:**
    - Navigate to your Minecraft's `mods` folder. If you can't find it, here's how:
        - For Windows: Press `Win + R` to open the Run dialog, type `%appdata%\.minecraft\mods` and hit Enter.
        - For macOS: Open Finder, then click "Go" from the toolbar at the top of the screen. Hold down the Option key and you should see `Library` appear as an option. Click that, then navigate to `Application Support/minecraft/mods`.
        - For Linux: Navigate to `~/.minecraft/mods`.
    - If the `mods` folder does not exist, create one.
    - Place the .jar file from the Artifacts.zip file into the `mods` folder.

5. **Launch Minecraft with Fabric Profile:**
    - Open the Minecraft Launcher.
    - In the bottom-left corner, switch the profile to the one created by the Fabric Loader.
    - Click "Play" to start Minecraft with the mod installed.

And that's it! If you followed the steps correctly, the FabricMC mod should now be installed on your Minecraft game.

These instructions are subject to change if the process for installing Fabric or the structure of your Artifacts.zip file changes. Always make sure to use the most up-to-date methods for each step.

## Technical details
### Server side (Purpur plugin logic)
Go to the following repo: https://github.com/lauralex/p2pminecraft

### Client side (FabricMC mod logic)
This mod adds a new "P2P" screen to the Multiplayer menu. It allows the user to connect to a P2P server.

If there is no active server, the user can become the server and the other users can connect to it.

The mod automatically downloads the world data from the server and applies the delta patches to the local world data.

### P2P backend service (FastAPI server logic)
Go to the following repo: https://github.com/lauralex/p2pmctracker

## Latest artifacts
- Fabric client mod: [Artifacts.zip](https://nightly.link/lauralex/p2pmcclient/workflows/build/main/Artifacts.zip)
