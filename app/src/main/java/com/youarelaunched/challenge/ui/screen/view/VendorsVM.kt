package com.youarelaunched.challenge.ui.screen.view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youarelaunched.challenge.data.repository.VendorsRepository
import com.youarelaunched.challenge.ui.screen.state.VendorsScreenUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class VendorsVM @Inject constructor(
    private val repository: VendorsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        VendorsScreenUiState(
            searchQuery = null,
            vendors = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        getVendors(null)

        _uiState
            .map { it.searchQuery }
            .distinctUntilChanged()
            .debounce(500)
            .filter { it != null && it.length >= 3 }
            .onEach { getVendors(it!!) }
            .launchIn(viewModelScope)
    }

    fun onSearchChanged(searchQuery: String?) {
        _uiState.update { it.copy(searchQuery = searchQuery) }

        if (searchQuery.isNullOrEmpty()) {
            getVendors(null)
        }
    }

    fun getVendors(searchQuery: String?) {
        viewModelScope.launch {
            runCatching { repository.getVendors(searchQuery) }
                .onSuccess { vendors -> _uiState.update { it.copy(vendors = vendors, error = null) } }
                .onFailure { error -> _uiState.update { it.copy(error = error.message.orEmpty()) } }
        }
    }
}
