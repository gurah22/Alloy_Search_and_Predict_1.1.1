package elements.controllers;

import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ConcurrencyGatekeeper {

    private AtomicReference<ProcessingToken> currentToken = new AtomicReference<>();

    public ProcessingToken getProcessingToken() {
        ProcessingToken processingToken = new ProcessingToken(this);

        boolean successfullySetToken = setTokenIfNoneIsPresent(processingToken);
        if (!successfullySetToken) {
            throw new ValidationException("Server is busy, please try again later");
        }

        return processingToken;
    }

    private boolean setTokenIfNoneIsPresent(ProcessingToken processingToken) {
        return currentToken.compareAndSet(null, processingToken);
    }

    private void removeTokenIfItMatches(ProcessingToken processingToken) {
        currentToken.compareAndSet(processingToken, null);
    }

    public class ProcessingToken implements AutoCloseable {
        private ConcurrencyGatekeeper concurrencyGatekeeper;

        private ProcessingToken(ConcurrencyGatekeeper concurrencyGatekeeper) {
            this.concurrencyGatekeeper = concurrencyGatekeeper;
        }

        @Override
        public void close() throws Exception {
            concurrencyGatekeeper.removeTokenIfItMatches(this);
        }
    }
}
