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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class PurpurDownloader {
    private static final String MC_VERSION = "1.20.1";
    private static final String API_URL = "https://api.purpurmc.org/v2/purpur/" + MC_VERSION + "/latest";
    private static final String P2P_URL = "https://p2pmc.fly.dev/get_plugin";
    private static final String PURPUR_YAML_URL = "https://p2pmc.fly.dev/get_purpur_yaml";
    private static final String SERVER_PROPERTIES_URL = "https://p2pmc.fly.dev/get_server_properties";
    private static final String SPIGOT_YAML_URL = "https://p2pmc.fly.dev/get_spigot_yaml";
    private static final String PAPER_WORLD_YAML_URL = "https://p2pmc.fly.dev/get_paper_world_yaml";
    private static final String PUFFERFISH_YAML_URL = "https://p2pmc.fly.dev/get_pufferfish_yaml";
    private static final String BUKKIT_YAML_URL = "https://p2pmc.fly.dev/get_bukkit_yaml";
    private static final String WORLD_URL = "https://p2pmc.fly.dev/get_world";
    private static final String WORLD_HASH_URL = "https://p2pmc.fly.dev/get_world_hash";
    private static final String WORLD_EXISTS_URL = "https://p2pmc.fly.dev/world_exists";
    private static final String DOWNLOAD_URL = API_URL + "/download";
    private static final String SAVE_PATH = System.getenv("APPDATA") + "\\.MinecraftServer\\purpur.jar";
    private static final String TEMP_PATH = System.getProperty("java.io.tmpdir") + "\\purpur.jar";

    public static void downloadPurpur() throws IOException, NoSuchAlgorithmException, EulaException {
        Files.createDirectories(Paths.get(SAVE_PATH).getParent());
        downloadP2pPlugin();
        downloadPurpurYaml();
        downloadServerProperties();
        downloadSpigotYaml();
        downloadPaperWorldYaml();
        downloadPufferfishYaml();
        downloadBukkitYaml();
        createAndDisplayEula();
        checkAndDownloadWorld();

        String latestHash = getLatestPurpurJarHash();
        String currentHash = getCurrentPurpurJarHash();
        if (!latestHash.equals(currentHash)) {
            downloadLatestJar();
            Files.move(Paths.get(TEMP_PATH), Paths.get(SAVE_PATH), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static String getLatestPurpurJarHash() throws IOException {
        URL url = new URL(API_URL);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JsonElement jelement = JsonParser.parseString(response.toString());
        JsonObject jobject = jelement.getAsJsonObject();
        String hash = jobject.get("md5").getAsString();
        return hash;
    }

    private static String getCurrentPurpurJarHash() throws IOException, NoSuchAlgorithmException {
        Path path = Paths.get(getSavePath());
        if (!Files.exists(path)) {
            return "";
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(path));
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
    }

    private static void downloadP2pPlugin() throws IOException {
        InputStream in = new URL(P2P_URL).openStream();
        Path pluginsDir = Files.createDirectories(Paths.get(SAVE_PATH).getParent().resolve("plugins"));
        Files.copy(in, pluginsDir.resolve("p2pminecraft.jar"), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void downloadPurpurYaml() throws IOException {
        InputStream in = new URL(PURPUR_YAML_URL).openStream();
        Files.copy(in, Paths.get(SAVE_PATH).getParent().resolve("purpur.yml"), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void downloadServerProperties() throws IOException {
        InputStream in = new URL(SERVER_PROPERTIES_URL).openStream();
        Files.copy(in, Paths.get(SAVE_PATH).getParent().resolve("server.properties"), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void downloadSpigotYaml() throws IOException {
        InputStream in = new URL(SPIGOT_YAML_URL).openStream();
        Files.copy(in, Paths.get(SAVE_PATH).getParent().resolve("spigot.yml"), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void downloadPaperWorldYaml() throws IOException {
        InputStream in = new URL(PAPER_WORLD_YAML_URL).openStream();
        Path configDir = Files.createDirectories(Paths.get(SAVE_PATH).getParent().resolve("config"));
        Files.copy(in, configDir.resolve("paper-world-defaults.yml"), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void downloadPufferfishYaml() throws IOException {
        InputStream in = new URL(PUFFERFISH_YAML_URL).openStream();
        Files.copy(in, Paths.get(SAVE_PATH).getParent().resolve("pufferfish.yml"), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void downloadBukkitYaml() throws IOException {
        InputStream in = new URL(BUKKIT_YAML_URL).openStream();
        Files.copy(in, Paths.get(SAVE_PATH).getParent().resolve("bukkit.yml"), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void downloadLatestJar() throws IOException {
        InputStream in = new URL(DOWNLOAD_URL).openStream();
        Files.copy(in, Paths.get(TEMP_PATH), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void createAndDisplayEula() throws EulaException {
        try {
            if (!Files.exists(Paths.get(SAVE_PATH).getParent().resolve("eula.txt"))) {
                // Create eula.txt
                String eulaContent = "#By changing the setting below to TRUE you are indicating your agreement to our EULA (https://aka.ms/MinecraftEULA).\n" +
                        "#Go to %APPDATA%\\.MinecraftServer\\eula.txt to edit this file.\neula=false";
                Path eulaPath = Files.createDirectories(Paths.get(SAVE_PATH).getParent());
                Files.write(eulaPath.resolve("eula.txt"), eulaContent.getBytes());
            }
            String eulaContent = Files.readString(Paths.get(SAVE_PATH).getParent().resolve("eula.txt"));
            if (eulaContent.contains("eula=false")) {
                // Open eula.txt in the notepad
                Runtime.getRuntime().exec("notepad " + Paths.get(SAVE_PATH).getParent().resolve("eula.txt"));
                throw new EulaException("Please agree to the EULA");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkAndDownloadWorld() throws IOException, NoSuchAlgorithmException {
        // Check if world exists from fastapi endpoint. Response is like {"exists": true}
        URL worldExistsUrl = new URL(WORLD_EXISTS_URL);
        HttpsURLConnection conn = (HttpsURLConnection) worldExistsUrl.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader worldExistsIn = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = worldExistsIn.readLine()) != null) {
            response.append(inputLine);
        }
        worldExistsIn.close();

        JsonElement jelement = JsonParser.parseString(response.toString());
        JsonObject jobject = jelement.getAsJsonObject();
        boolean exists = jobject.get("world_exists").getAsBoolean();
        if (!exists) {
            // Delete world if exists
            if (Files.exists(Paths.get(SAVE_PATH).getParent().resolve("world"))) {
                FileUtils.deleteDirectory(Paths.get(SAVE_PATH).getParent().resolve("world").toFile());
            }
            if (Files.exists(Paths.get(SAVE_PATH).getParent().resolve("world_old"))) {
                FileUtils.deleteDirectory(Paths.get(SAVE_PATH).getParent().resolve("world_old").toFile());
            }
            if (Files.exists(Paths.get(SAVE_PATH).getParent().resolve("world_prev"))) {
                FileUtils.deleteDirectory(Paths.get(SAVE_PATH).getParent().resolve("world_prev").toFile());
            }
            return;
        }
        // Check if world_prev exists
        Path worldPrevPath = Paths.get(SAVE_PATH).getParent().resolve("world_prev");
        if (!Files.exists(worldPrevPath)) {
            // Download world from fastapi endpoint https://p2pmc.fly.dev/get_world
            InputStream in = new URL(WORLD_URL).openStream();
            Files.copy(in, Path.of(worldPrevPath + ".zip"), StandardCopyOption.REPLACE_EXISTING);

            // Delete world if exists
            if (Files.exists(Paths.get(SAVE_PATH).getParent().resolve("world"))) {
                FileUtils.deleteDirectory(Paths.get(SAVE_PATH).getParent().resolve("world").toFile());
            }
            uncompressZip(Path.of(worldPrevPath + ".zip").toFile());

            // Check if world_old exists, if exists delete it
            if (Files.exists(Paths.get(SAVE_PATH).getParent().resolve("world_old"))) {
                FileUtils.deleteDirectory(Paths.get(SAVE_PATH).getParent().resolve("world_old").toFile());
            }
            // Copy world to world_old
            FileUtils.copyDirectory(Paths.get(SAVE_PATH).getParent().resolve("world").toFile(), Paths.get(SAVE_PATH).getParent().resolve("world_old").toFile());

            // Copy world to world_prev
            FileUtils.copyDirectory(Paths.get(SAVE_PATH).getParent().resolve("world").toFile(), Paths.get(SAVE_PATH).getParent().resolve("world_prev").toFile());
            in.close();
        } else if (Files.exists(worldPrevPath)) {
            // Check get_world_hash endpoint: https://p2pmc.fly.dev/get_world_hash. Response is like {"hash": "hash"}
            URL worldHashUrl = new URL(WORLD_HASH_URL);
            HttpsURLConnection worldHashConn = (HttpsURLConnection) worldHashUrl.openConnection();
            worldHashConn.setRequestMethod("GET");
            BufferedReader WorldHashIn = new BufferedReader(new InputStreamReader(worldHashConn.getInputStream()));
            String inputLineWorldHash;
            StringBuffer responseWorldHash = new StringBuffer();
            while ((inputLineWorldHash = WorldHashIn.readLine()) != null) {
                responseWorldHash.append(inputLineWorldHash);
            }
            WorldHashIn.close();

            JsonElement jElementWorldHash = JsonParser.parseString(responseWorldHash.toString());
            JsonObject jObjectWorldHash = jElementWorldHash.getAsJsonObject();
            String hash = jObjectWorldHash.get("hash").getAsString();

            // Check hash of the whole world_prev directory
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (Stream<Path> files = Files.walk(Paths.get(worldPrevPath.toString()))) {
                files.filter(Files::isRegularFile).forEach(p -> {
                    try {
                        md.update(Files.readAllBytes(p));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            String worldPrevHash = Hex.encodeHexString(md.digest());

            if (!worldPrevHash.equals(hash)) {
                // Download world from fastapi endpoint https://p2pmc.fly.dev/get_world
                InputStream worldIn = new URL(WORLD_URL).openStream();
                // Delete old world_prev directory recursively and copy new world_prev
                FileUtils.deleteDirectory(worldPrevPath.toFile());

                Files.copy(worldIn, Path.of(worldPrevPath + ".zip"), StandardCopyOption.REPLACE_EXISTING);

                // Delete world if exists
                if (Files.exists(Paths.get(SAVE_PATH).getParent().resolve("world"))) {
                    FileUtils.deleteDirectory(Paths.get(SAVE_PATH).getParent().resolve("world").toFile());
                }

                // Uncompress world_prev
                uncompressZip(Path.of(worldPrevPath + ".zip").toFile());

                // Check if world_old exists, if exists delete it
                if (Files.exists(Paths.get(SAVE_PATH).getParent().resolve("world_old"))) {
                    FileUtils.deleteDirectory(Paths.get(SAVE_PATH).getParent().resolve("world_old").toFile());
                }

                // Copy world to world_old
                FileUtils.copyDirectory(Paths.get(SAVE_PATH).getParent().resolve("world").toFile(), Paths.get(SAVE_PATH).getParent().resolve("world_old").toFile());

                // Copy world to world_prev
                FileUtils.copyDirectory(Paths.get(SAVE_PATH).getParent().resolve("world").toFile(), Paths.get(SAVE_PATH).getParent().resolve("world_prev").toFile());
                worldIn.close();
            } else {
                // Copy to world folder

                // Delete world if exists
                if (Files.exists(Paths.get(SAVE_PATH).getParent().resolve("world"))) {
                    FileUtils.deleteDirectory(Paths.get(SAVE_PATH).getParent().resolve("world").toFile());
                }
                if (worldPrevPath.resolve("world").toFile().exists()) {
                    FileUtils.copyDirectory(worldPrevPath.resolve("world").toFile(), Paths.get(SAVE_PATH).getParent().resolve("world").toFile());
                } else {
                    FileUtils.copyDirectory(worldPrevPath.toFile(), Paths.get(SAVE_PATH).getParent().resolve("world").toFile());
                }
                // Check if world_old exists, if exists delete it
                if (Files.exists(Paths.get(SAVE_PATH).getParent().resolve("world_old"))) {
                    FileUtils.deleteDirectory(Paths.get(SAVE_PATH).getParent().resolve("world_old").toFile());
                }
                // Copy world to world_old
                FileUtils.copyDirectory(Paths.get(SAVE_PATH).getParent().resolve("world").toFile(), Paths.get(SAVE_PATH).getParent().resolve("world_old").toFile());
            }
        }
    }

    private static void uncompressZip(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                File entryDestination = new File(file.getParent(), entry.getName());
                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    entryDestination.getParentFile().mkdirs();
                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in);
                    out.close();
                }
            }
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void zipFile(File file, String fileName, ZipOutputStream gzos) throws IOException {
        if (file.isHidden() || fileName.contains("session.lock")) {
            return;
        }
        if (file.isDirectory()) {
            if (fileName.endsWith("/")) {
                gzos.putNextEntry(new ZipEntry(fileName));
                gzos.closeEntry();
            } else {
                gzos.putNextEntry(new ZipEntry(fileName + "/"));
                gzos.closeEntry();
            }
            File[] children = file.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), gzos);
                }
            }
        } else {
            ZipEntry zipEntry = new ZipEntry(fileName);
            gzos.putNextEntry(zipEntry);
            Files.copy(file.toPath(), gzos);
            gzos.closeEntry();
        }
    }

    private static File compressFolder(File folder) {
        File compressedFile = new File(folder.getParentFile(), folder.getName() + ".zip");
        if (compressedFile.exists()) {
            compressedFile.delete();
        }
        try (FileOutputStream fos = new FileOutputStream(compressedFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            zipFile(folder, folder.getName(), zipOut);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return compressedFile;
    }

    public static String getSavePath() {
        return SAVE_PATH;
    }
}
