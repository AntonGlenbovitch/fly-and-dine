package com.airline.ordering;

import com.airline.ordering.repository.SQLiteConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting Inflight Ordering Application...");
        SQLiteConnection.initializeDatabase();
        logger.info("Database initialized. Application ready.");
        // Further application startup logic will go here
    }
}

