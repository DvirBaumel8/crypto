package com.att.sane.portal.crypto.listener;

import com.att.sane.portal.crypto.connector.BlockchainConnector;
import com.att.sane.portal.crypto.model.NewPairEventDto;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Block;
import org.springframework.stereotype.Component;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;

import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

@Slf4j
@Component
public class LpListener {
    private static final String NEW_PAIR_CREATED_EVENT_NAME = "PairCreated";
    private static final String FACTORY_ADDRESS = "0x5C69bEe701ef814a2B6a3EDD4B1652CB9cc5aA6f";
    private final BlockchainConnector blockchainConnector;
    private Disposable disposable;

    public LpListener(BlockchainConnector blockchainConnector) {
        this.blockchainConnector = blockchainConnector;
    }

    public void start(Consumer<NewPairEventDto> callback) throws IOException {
        Event newPairCreatedEvent = new Event(NEW_PAIR_CREATED_EVENT_NAME, List.of(TypeReference.create(Address.class)));
        String eventSignature = EventEncoder.encode(newPairCreatedEvent);

        EthFilter filter = new EthFilter(LATEST, LATEST, FACTORY_ADDRESS);
        filter.addSingleTopic(eventSignature);

        Web3j web3j = blockchainConnector.getWeb3j();
        disposable = web3j.ethLogFlowable(filter).subscribe(event -> {
            NewPairEventDto newPairEventDto = createNewPairEventDtoFromEvent(event);
            callback.accept(newPairEventDto);
        });
    }

    public void stop() {
        if(disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private NewPairEventDto createNewPairEventDtoFromEvent(Log event) {
        log.info("New event found, info: {}", event.toString());
        String token0 = event.getTopics().getFirst();
        String token1 = event.getTopics().get(1);
        String pairAddress = event.getTopics().get(2);
        return new NewPairEventDto(token0, token1, pairAddress);
    }

}
