package com.sportsant.saas.security.blockchain;

import com.sportsant.saas.ai.service.AiAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A lightweight internal blockchain to make audit logs tamper-proof.
 * Each block contains the hash of the previous block.
 */
@Service
public class AuditLedgerService implements AiAware {
    private static final Logger logger = LoggerFactory.getLogger(AuditLedgerService.class);

    private final List<Block> chain = new CopyOnWriteArrayList<>();

    public AuditLedgerService() {
        // Genesis Block
        chain.add(new Block(0, "GENESIS", "0"));
    }

    public synchronized void recordAudit(String data) {
        Block previousBlock = chain.get(chain.size() - 1);
        Block newBlock = new Block(previousBlock.index + 1, data, previousBlock.hash);
        chain.add(newBlock);
        
        logger.info("Blockchain: Recorded Block #{} - Hash: {}", newBlock.index, newBlock.hash);
    }

    public boolean verifyIntegrity() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            
            if (!current.previousHash.equals(previous.hash)) {
                logger.error("Blockchain Integrity Failure at Block #{}", i);
                return false;
            }
            
            if (!current.calculateHash().equals(current.hash)) {
                 logger.error("Blockchain Tamper Evidence at Block #{}", i);
                 return false;
            }
        }
        return true;
    }

    @Override
    public void onAiSuggestion(String suggestionType, Object payload) {
        if ("VERIFY_LEDGER".equals(suggestionType)) {
            verifyIntegrity();
        }
    }

    static class Block {
        public int index;
        public long timestamp;
        public String data;
        public String previousHash;
        public String hash;

        public Block(int index, String data, String previousHash) {
            this.index = index;
            this.timestamp = System.currentTimeMillis();
            this.data = data;
            this.previousHash = previousHash;
            this.hash = calculateHash();
        }

        public String calculateHash() {
            String input = index + timestamp + data + previousHash;
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
