package com.inquea.inquea.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inquea.inquea.domain.model.BusinessProfile
import com.inquea.inquea.domain.repository.BusinessRepository
import com.inquea.inquea.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BusinessRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow<Resource<List<BusinessProfile>>>(Resource.Success(emptyList()))
    val searchState: StateFlow<Resource<List<BusinessProfile>>> = _searchState.asStateFlow()

    private var searchJob: Job? = null

    init {
        search("") // Initial load
    }

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            repository.searchBusinesses(query).collect { result ->
                _searchState.value = result
            }
        }
    }
}
