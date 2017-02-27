package org.ethereum;

import org.ethereum.cli.CLIInterface;
import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by human-research on 19/02/2017.
 */
public class Crawler {
    public static void main(String args[]) throws IOException, URISyntaxException, InterruptedException {
        CLIInterface.call(args);

        Ethereum ethereum = EthereumFactory.createEthereum();
        ethereum.startPeerDiscovery();

    }
}
