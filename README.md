# P2P Minecraft Project - Client side
## Description
Create a P2P Minecraft system using Purpur API (for the server side) and FabricMC (for the client side). FastAPI is used for the backend service.

## Technical details
### Server side (Purpur plugin logic)
Go to the following repo: https://github.com/lauralex?tab=repositories

### Client side (FabricMC mod logic)
This mod adds a new "P2P" screen to the Multiplayer menu. It allows the user to connect to a P2P server.

If there is no active server, the user can become the server and the other users can connect to it.

The mod automatically downloads the world data from the server and applies the delta patches to the local world data.

### P2P backend service (FastAPI server logic)
Go to the following repo: https://github.com/lauralex/p2pmctracker

## Latest artifacts
- Fabric client mod: [Artifacts.zip](https://nightly.link/lauralex/p2pmcclient/workflows/build/main/Artifacts.zip)
