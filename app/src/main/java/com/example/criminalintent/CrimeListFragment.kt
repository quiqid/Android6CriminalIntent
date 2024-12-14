package com.example.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Date
import java.util.UUID

private const val TAG = "CrimeListFragment"

class CrimeListFragment: Fragment() {

    interface Callbacks{
        fun onCrimeSelected(crimeId: UUID)
    }
    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter:CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this)[CrimeListViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list,container,false)

        crimeRecyclerView = view.findViewById<RecyclerView>(R.id.crime_recycler_view)!!
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes ->
                crimes?.let {

                    //Проверка айдишников
//                    it.forEach { crime ->
//                        Log.i(TAG, "Crime ID: ${crime.id}, Title: ${crime.title}, Date: ${crime.date}, Solved: ${crime.isSolved}")
//                    }

                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(crimes: List<Crime>){
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    private inner class CrimeHolder(view: View):RecyclerView.ViewHolder(view), View.OnClickListener{
        private lateinit var crime: Crime

        val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime){
            this.crime = crime
            titleTextView.text = this.crime.title

            val formattedDate = formatDate(this.crime.date) //меняем формат даты
            //dateTextView.text = this.crime.date.toString()
            dateTextView.text = formattedDate

            solvedImageView.visibility =
                if (crime.isSolved){
                    View.VISIBLE
                }else{
                    View.GONE
                }
        }

        override fun onClick(v: View) {
            //Toast.makeText(context,"${crime.title} pressed!", Toast.LENGTH_SHORT).show()
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<CrimeHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount() = crimes.size
    }


    private fun formatDate(date: Date): String {
        //val formatString = "EEEE, MMM dd, yyyy HH:mm"
        val formatString = "EEEE, MMM dd, yyyy"
        val formattedDate = DateFormat.format(formatString, date)
        return formattedDate.toString()
    }


    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}