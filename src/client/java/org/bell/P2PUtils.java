package org.bell;

import org.springframework.web.client.RestTemplate;

public class P2PUtils {
    public static PeerAddressDTO[] fetchActiveIPs() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject("https://p2pmc.fly.dev/get_peers", PeerAddressDTO[].class);
    }
}
