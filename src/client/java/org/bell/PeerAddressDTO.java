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

import java.io.Serializable;

public class PeerAddressDTO implements Serializable {
    private String ip;
    private String port;
    private String last_ack;

    public PeerAddressDTO() {
    }

    public PeerAddressDTO(String ip, String port, String last_ack) {
        this.ip = ip;
        this.port = port;
        this.last_ack = last_ack;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getLast_ack() {
        return last_ack;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setLast_ack(String last_ack) {
        this.last_ack = last_ack;
    }

    @Override
    public String toString() {
        return "PeerAddressDTO{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", last_ack='" + last_ack + '\'' +
                '}';
    }

    public String toAddress() {
        return ip + ":" + port;
    }

    public static PeerAddressDTO fromAddress(String address) {
        String[] split = address.split(":");
        return new PeerAddressDTO(split[0], split[1], null);
    }

    public static PeerAddressDTO fromAddress(String address, String last_ack) {
        String[] split = address.split(":");
        return new PeerAddressDTO(split[0], split[1], last_ack);
    }

    public static PeerAddressDTO fromAddress(String ip, String port, String last_ack) {
        return new PeerAddressDTO(ip, port, last_ack);
    }

}
