package com.airline.ordering.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for managing SQLite database connections and schema creation.
 */
public class SQLiteConnection {
    
    private static final Logger logger = LoggerFactory.getLogger(SQLiteConnection.class);
    private static final String DB_URL = "jdbc:sqlite:inflight_ordering.db";
    
    private SQLiteConnection() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Establishes a connection to the SQLite database.
     * If the database file does not exist, it will be created.
     * @return A Connection object to the database.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            logger.info("Connected to SQLite database: {}", DB_URL);
        } catch (SQLException e) {
            logger.error("Error connecting to SQLite database: {}", e.getMessage());
            throw e;
        }
        return conn;
    }
    
    /**
     * Initializes the database schema by creating necessary tables if they don't exist.
     * This method should be called once at application startup.
     */
    public static void initializeDatabase() {
        String createPassengersTable = "CREATE TABLE IF NOT EXISTS passengers (\n" +
                                       "    passenger_id TEXT PRIMARY KEY,\n" +
                                       "    first_name TEXT NOT NULL,\n" +
                                       "    last_name TEXT NOT NULL,\n" +
                                       "    email TEXT,\n" +
                                       "    phone_number TEXT,\n" +
                                       "    seat_number TEXT NOT NULL,\n" +
                                       "    type TEXT NOT NULL,\n" +
                                       "    special_requirements TEXT,\n" +
                                       "    created_at TEXT NOT NULL,\n" +
                                       "    updated_at TEXT NOT NULL\n" +
                                       ");";
        
        String createMenuItemsTable = "CREATE TABLE IF NOT EXISTS menu_items (\n" +
                                      "    item_id TEXT PRIMARY KEY,\n" +
                                      "    name TEXT NOT NULL,\n" +
                                      "    description TEXT,\n" +
                                      "    price REAL NOT NULL,\n" +
                                      "    category TEXT NOT NULL,\n" +
                                      "    available_for_types TEXT,\n" +
                                      "    allergens TEXT,\n" +
                                      "    dietary_tags TEXT,\n" +
                                      "    status TEXT NOT NULL,\n" +
                                      "    inventory_count INTEGER NOT NULL,\n" +
                                      "    substitutable_items TEXT,\n" +
                                      "    combo_items TEXT,\n" +
                                      "    created_at TEXT NOT NULL,\n" +
                                      "    updated_at TEXT NOT NULL\n" +
                                      ");";
        
        String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (\n" +
                                   "    order_id TEXT PRIMARY KEY,\n" +
                                   "    passenger_id TEXT NOT NULL,\n" +
                                   "    seat_number TEXT NOT NULL,\n" +
                                   "    total_amount REAL NOT NULL,\n" +
                                   "    status TEXT NOT NULL,\n" +
                                   "    notes TEXT,\n" +
                                   "    requested_delivery_time TEXT,\n" +
                                   "    created_at TEXT NOT NULL,\n" +
                                   "    updated_at TEXT NOT NULL,\n" +
                                   "    confirmed_at TEXT,\n" +
                                   "    delivered_at TEXT,\n" +
                                   "    synced_with_crs INTEGER NOT NULL,\n" +
                                   "    last_sync_attempt TEXT,\n" +
                                   "    crs_order_id TEXT,\n" +
                                   "    sync_version INTEGER NOT NULL\n" +
                                   ");";
        
        String createOrderItemsTable = "CREATE TABLE IF NOT EXISTS order_items (\n" +
                                       "    order_item_id TEXT PRIMARY KEY,\n" +
                                       "    order_id TEXT NOT NULL,\n" +
                                       "    menu_item_id TEXT NOT NULL,\n" +
                                       "    menu_item_name TEXT NOT NULL,\n" +
                                       "    quantity INTEGER NOT NULL,\n" +
                                       "    unit_price REAL NOT NULL,\n" +
                                       "    total_price REAL NOT NULL,\n" +
                                       "    special_instructions TEXT,\n" +
                                       "    substituted_from_item_id TEXT,\n" +
                                       "    status TEXT NOT NULL,\n" +
                                       "    created_at TEXT NOT NULL,\n" +
                                       "    updated_at TEXT NOT NULL,\n" +
                                       "    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE\n" +
                                       ");";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createPassengersTable);
            stmt.execute(createMenuItemsTable);
            stmt.execute(createOrdersTable);
            stmt.execute(createOrderItemsTable);
            logger.info("Database schema initialized successfully.");
        } catch (SQLException e) {
            logger.error("Error initializing database schema: {}", e.getMessage());
        }
    }
    
    /**
     * Closes the given database connection.
     * @param conn The Connection object to close.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                logger.info("SQLite database connection closed.");
            } catch (SQLException e) {
                logger.error("Error closing SQLite database connection: {}", e.getMessage());
            }
        }
    }
}

