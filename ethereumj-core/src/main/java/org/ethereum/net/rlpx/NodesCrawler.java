package org.ethereum.net.rlpx;

import org.ethereum.net.eth.message.StatusMessage;
import org.ethereum.net.p2p.HelloMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by human-research on 27/02/2017.
 */
public enum NodesCrawler {
    //region Fields
    INSTANCE;
    private Map<NodeAddress, CrawlerNode> nodesMap = new HashMap<>();
    //endregion

    //region Constructors
    //endregion


    //region Getters and Setters

    //endregion


    //region Public Methods
    public void commitHelloMessage(SocketAddress socketAddress, HelloMessage message) {
        InetSocketAddress addr = (InetSocketAddress) socketAddress;
        NodeAddress nodeAddress = new NodeAddress(addr.getAddress(), addr.getPort());
        CrawlerNode nodeToCommit = new CrawlerNode(nodeAddress, message.getPeerId().toCharArray(), message.getClientId());
        nodesMap.put(nodeAddress, nodeToCommit);
    }

    public void commitStatusMessage(SocketAddress socketAddress, StatusMessage msg) {
        InetSocketAddress addr = (InetSocketAddress) socketAddress;
        NodeAddress nodeAddress = new NodeAddress(addr.getAddress(), addr.getPort());
        CrawlerNode node = nodesMap.get(nodeAddress);
        node.setNetworkID(msg.getNetworkId());
    }

    //public void commitPeersMessage()
    //endregion


    //region Private Methods
    public void writeToFile() throws IOException {
        final String path = "nodes.json";
        FileOutputStream fos = new FileOutputStream(new File(path));
        Map<NodeAddress, CrawlerNode> tempMap = new HashMap<>(nodesMap);

        JSONArray jsonArray = new JSONArray();
        tempMap.values().forEach(node -> {
            JSONObject jsonNode = new JSONObject();
            String address = node.getNodeAddress().getInetAddress().toString();
            jsonNode.put("ip", address.contains("/") ? address.substring(1) : address);
            jsonNode.put("port", String.valueOf(node.getNodeAddress().getPort()));
            jsonNode.put("networkID", node.getNetworkID());
            jsonNode.put("hexID", String.valueOf(node.getHexID()));
            jsonNode.put("clientString", node.getClientId());
            jsonArray.add(jsonNode);
        });

        fos.write(jsonArray.toJSONString().getBytes());
        fos.close();
    }
    //endregion



    private class CrawlerNode {
        private NodeAddress nodeAddress;
        private char[] hexID;
        private String clientId;
        private int networkID;

        public CrawlerNode(NodeAddress nodeAddress, char[] hexID, String clientId) {
            this.nodeAddress = nodeAddress;
            this.hexID = hexID;
            this.clientId = clientId;
        }

        public NodeAddress getNodeAddress() {
            return nodeAddress;
        }

        public char[] getHexID() {
            return hexID;
        }

        public String getClientId() {
            return clientId;
        }

        public int getNetworkID() {
            return networkID;
        }

        public void setNetworkID(int networkID) {
            this.networkID = networkID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CrawlerNode that = (CrawlerNode) o;

            return nodeAddress != null ? nodeAddress.equals(that.nodeAddress) : that.nodeAddress == null;
        }

        @Override
        public int hashCode() {
            return nodeAddress != null ? nodeAddress.hashCode() : 0;
        }
    }

    private class NodeAddress {
        private InetAddress inetAddress;
        private int port;

        public NodeAddress(InetAddress inetAddress, int port) {
            this.inetAddress = inetAddress;
            this.port = port;
        }

        public InetAddress getInetAddress() {
            return inetAddress;
        }

        public int getPort() {
            return port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NodeAddress that = (NodeAddress) o;

            if (port != that.port) return false;
            return inetAddress.equals(that.inetAddress);
        }

        @Override
        public int hashCode() {
            int result = inetAddress.hashCode();
            result = 31 * result + port;
            return result;
        }
    }
}
