package mobiledev.unb.ca.roomcoroutinetest.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import mobiledev.unb.ca.roomcoroutinetest.repository.ItemRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mobiledev.unb.ca.roomcoroutinetest.db.ItemRoomDatabase
import mobiledev.unb.ca.roomcoroutinetest.entity.Item

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val itemRepository: ItemRepository
    val allItems: LiveData<List<Item>>

    init {
        val itemDao = ItemRoomDatabase.getDatabase(application, viewModelScope).itemDao()
        itemRepository = ItemRepository(itemDao)
        allItems = itemRepository.allItems
    }

    fun insert(name: String?, num: Int) = viewModelScope.launch(Dispatchers.IO) {
        itemRepository.insertRecord(name, num)
    }

    fun delete(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        itemRepository.deleteRecord(item)
    }
}
