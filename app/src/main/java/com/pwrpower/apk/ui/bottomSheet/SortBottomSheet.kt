package com.pwrpower.apk.ui.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.pwrpower.apk.R
import androidx.core.view.isNotEmpty

class SortBottomSheet : BottomSheetDialogFragment() {

    interface SortListener {
        fun onSortApplied(selectedSortOption: String)
    }

    var listener: SortListener? = null
    private var selectedSortOption: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_sort, container, false)

        val sortOption = arguments?.getString("selectedSortOption")
        selectedSortOption = sortOption ?: "None"

        val sortChipGroup = view.findViewById<ChipGroup>(R.id.cgSort)
        
        loadSelectedFilters(sortChipGroup)

        view.findViewById<View>(R.id.btnApply).setOnClickListener {
            val selectedSortOption = getSelectedChipText(sortChipGroup) ?: "None"

            this.selectedSortOption = selectedSortOption

            listener?.onSortApplied(selectedSortOption)
            dismiss()
        }

        view.findViewById<View>(R.id.btnReset).setOnClickListener{
            resetFilters(sortChipGroup)
        }

        return view
    }

    private fun loadSelectedFilters(sortChipGroup: ChipGroup) {
        selectedSortOption?.let { sortOption ->
            val chip = sortChipGroup.findChipWithText(sortOption)
            chip?.isChecked = true
        } ?: run {
            if (sortChipGroup.isNotEmpty()) {
                val firstChip = sortChipGroup.getChildAt(0) as? Chip
                firstChip?.isChecked = true
                selectedSortOption = firstChip?.text?.toString()
            }
        }
    }

    private fun getSelectedChipText(chipGroup: ChipGroup): String? {
        val selectedChipId = chipGroup.checkedChipId
        if (selectedChipId != View.NO_ID) {
            val chip = chipGroup.findViewById<Chip>(selectedChipId)
            return chip?.text?.toString()
        }
        val firstChip = chipGroup.getChildAt(0) as? Chip
        return firstChip?.text?.toString()
    }

    private fun ChipGroup.findChipWithText(text: String): Chip? {
        for (i in 0 until childCount) {
            val chip = getChildAt(i) as? Chip
            if (chip?.text.toString() == text) {
                return chip
            }
        }
        return null
    }

    private fun resetFilters(sortChipGroup: ChipGroup) {
        if (sortChipGroup.isNotEmpty()) {
            val firstChip = sortChipGroup.getChildAt(0) as? Chip
            firstChip?.isChecked = true
            selectedSortOption = firstChip?.text?.toString()
        }
    }
}