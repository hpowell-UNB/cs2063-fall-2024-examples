package mobiledev.unb.ca.roompersistencetest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import mobiledev.unb.ca.roompersistencetest.entity.Item
import mobiledev.unb.ca.roompersistencetest.repository.ItemRepository

/**
 * This ViewModel is a class responsible for preparing and managing the data for the Activity.
 * It also handles the communication between the Activity and the rest of the application.
 */
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepository: ItemRepository = ItemRepository(application)
    val allItems: LiveData<List<Item>> = itemRepository.allItems

    // Insert a new record
    fun insert(name: String?, num: Int) {
        itemRepository.insertRecord(name, num)
    }

    // Remove an existing record
    fun delete(item: Item) {
        itemRepository.deleteRecord(item)
    }
}

