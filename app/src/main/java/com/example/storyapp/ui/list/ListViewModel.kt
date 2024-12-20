package com.example.storyapp.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagingData
import com.example.storyapp.api.Story
import com.example.storyapp.data.repository.StoryRepository

class ListViewModel(storyRepository: StoryRepository) : ViewModel() {
    val stories: LiveData<PagingData<Story>> = storyRepository.getStories()
}

class ListViewModelFactory(private val repository: StoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}