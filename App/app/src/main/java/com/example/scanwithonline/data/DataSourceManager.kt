package com.example.scanwithonline.data

/**
 * An enum to define the possible data source types in a clean, type-safe way.
 */
enum class DataSourceType {
    ONLINE,
    OFFLINE
}

/**
 * A singleton object to hold the state of the chosen data source for the current app session.
 * This object can be accessed from anywhere in the app to determine the current mode.
 */
object DataSourceManager {
    // We'll default to ONLINE mode when the app starts.
    // This value will be updated when the user makes a choice on the selection screen.
    var currentSource: DataSourceType = DataSourceType.ONLINE
}