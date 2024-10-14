package mobiledev.unb.ca.roompersistencetest.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table") // Represents a SQLite table
class Item {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var name: String? = null
    var num = 0
}