package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class BottomSheetFragment : BottomSheetDialogFragment(R.layout.bottomsheet_filter_layout) {

    private lateinit var categoryChipGroup: ChipGroup
    private lateinit var regionChipGroup: ChipGroup
    private lateinit var genreChipGroup: ChipGroup
    private lateinit var timeChipGroup: ChipGroup
    private lateinit var sortChipGroup: ChipGroup

    private var onFilterAppliedListener: ((String?, String?, String?, String?) -> Unit)? = null
    private var onResetListener: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryChipGroup = view.findViewById(R.id.filterChipsCategory)
        regionChipGroup = view.findViewById(R.id.filterChipsRegion)
        genreChipGroup = view.findViewById(R.id.filterChipsGenre)
        timeChipGroup = view.findViewById(R.id.filterChipsTime)
        sortChipGroup = view.findViewById(R.id.filterChipsSort)


        val applyButton: Button = view.findViewById(R.id.btnApply)
        val resetButton: Button = view.findViewById(R.id.btnReset)

        applyButton.setOnClickListener {
            val selectedGenre = view.findViewById<Chip>(genreChipGroup.checkedChipId)?.tag as? String
            val selectedRegion = view.findViewById<Chip>(regionChipGroup.checkedChipId)?.tag as? String
            val selectedCategory = view.findViewById<Chip>(categoryChipGroup.checkedChipId)?.tag as? String
            val selectedTime = view.findViewById<Chip>(timeChipGroup.checkedChipId)?.tag as? String
            val selectedSort = view.findViewById<Chip>(sortChipGroup.checkedChipId)?.tag as? String

            onFilterAppliedListener?.invoke(selectedGenre, selectedRegion, selectedTime, selectedSort)
            dismiss()
        }

        resetButton.setOnClickListener {
            categoryChipGroup.check(R.id.chipMovie)
            regionChipGroup.check(R.id.chipAllRegions)
            genreChipGroup.check(R.id.chipAction)
            timeChipGroup.check(R.id.chipAllPeriods)
            sortChipGroup.check(R.id.chipPopularity)
            onResetListener?.invoke()
        }

    }

    fun setOnFilterAppliedListener(listener: (String?, String?, String?, String?) -> Unit) {
        onFilterAppliedListener = listener
    }

    fun setOnResetListener(listener: () -> Unit) {
        onResetListener = listener
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

}