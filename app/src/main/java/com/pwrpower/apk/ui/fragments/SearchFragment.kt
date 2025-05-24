package com.pwrpower.apk.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.recyclerview.widget.RecyclerView
import com.pwrpower.apk.R
import com.pwrpower.apk.api.Cars
import com.pwrpower.apk.api.CarsResponse
import com.pwrpower.apk.api.RetrofitInstance
import com.pwrpower.apk.data.CarsAdapter
import com.pwrpower.apk.ui.bottomSheet.SortBottomSheet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rvAdapter: CarsAdapter
    private val cars: MutableList<Cars> = mutableListOf()

    private var sortString: String = "Alphabetically"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val toolbar: Toolbar = view.findViewById(R.id.searchToolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        rvAdapter = CarsAdapter(cars, this::onItemClick)

        val menuHost = requireActivity() as MenuHost
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_cars_search, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

                searchView.queryHint = getString(R.string.searchBar)
                searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean = false

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText.isNullOrEmpty()) {
                            rvAdapter = CarsAdapter(cars, this@SearchFragment::onItemClick)
                            recyclerView.adapter = rvAdapter
                            showCars()
                        }
                        if (newText != null) {
                            val filteredCars = cars.filter { it.name.contains(newText, ignoreCase = true) }
                            Log.d("onQueryTextChange", "Filtered cars: $filteredCars")
                            if (filteredCars.isNotEmpty()) {
                                rvAdapter = CarsAdapter(filteredCars, this@SearchFragment::onItemClick)
                                recyclerView.adapter = rvAdapter
                                showCars()
                            } else {
                                hideCars()
                            }
                        }
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_filter -> {
                        showSortPopup()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        downloadCars(view)

        return view
    }

    private fun downloadCars(view: View) {
        RetrofitInstance.api.getCars().enqueue(object : Callback<CarsResponse> {
            override fun onResponse(call: Call<CarsResponse>, response: Response<CarsResponse>) {
                if (response.isSuccessful) {
                    val carsResponse = response.body()
                    if (carsResponse != null && carsResponse.success) {
                        cars.clear()
                        cars.addAll(carsResponse.cars ?: emptyList())
                        recyclerView = view.findViewById(R.id.recyclerViewCars)
                        rvAdapter = CarsAdapter(cars, this@SearchFragment::onItemClick)
                        recyclerView.adapter = rvAdapter
                        sortReservations()

                    } else {
                        hideCars()
                        Toast.makeText(requireContext(), "Error: ${carsResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    hideCars()
                    Toast.makeText(requireContext(), "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CarsResponse>, t: Throwable) {
                hideCars()
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSortPopup() {
        val sortBottomSheet = SortBottomSheet()

        val bundle = Bundle()
        bundle.putString("selectedSortOption", sortString)

        sortBottomSheet.arguments = bundle

        sortBottomSheet.listener = object : SortBottomSheet.SortListener {
            override fun onSortApplied(selectedSortOption: String) {
                sortString = selectedSortOption
                sortReservations()
            }
        }
        sortBottomSheet.show(parentFragmentManager, "SortBottomSheet")
    }

    private fun sortReservations() {
        when (sortString) {
            getString(R.string.sortAlphabetically) -> {
                cars.sortBy { it.name }
            }
            getString(R.string.sortPriceLow) -> {
                cars.sortBy { it.price }
            }
            getString(R.string.sortPriceHigh) -> {
                cars.sortByDescending { it.price }
            }
            getString(R.string.sortRatingLow) -> {
                cars.sortBy { it.rating }
            }
            getString(R.string.sortPriceHigh) -> {
                cars.sortByDescending { it.rating }
            }
            else -> {
                cars.sortBy { it.name }
            }
        }
        rvAdapter = CarsAdapter(cars, this::onItemClick)
        recyclerView.adapter = rvAdapter
    }

    private fun onItemClick(id: String) {
        val bundle = Bundle()
        bundle.putString("carId", id)
        val fragment = CarInfoFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showCars() {
        recyclerView.visibility = View.VISIBLE
        val noDataText = view?.findViewById<TextView>(R.id.noDataText)
        noDataText?.visibility = View.GONE
    }

    private fun hideCars() {
        recyclerView.visibility = View.GONE
        val noDataText = view?.findViewById<TextView>(R.id.noDataText)
        noDataText?.visibility = View.VISIBLE
    }
}