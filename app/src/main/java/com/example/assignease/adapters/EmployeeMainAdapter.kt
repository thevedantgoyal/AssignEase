package com.example.assignease.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.assignease.R
import com.example.assignease.databinding.EmployeeMainItemViewBinding
import com.example.assignease.models.Works
import com.google.android.material.button.MaterialButton


class EmployeeMainAdapter(
    val onProgressBtnClick: (Works, MaterialButton) -> Unit,
    val onCompleteBtnClick: (Works, MaterialButton) -> Unit
) : RecyclerView.Adapter<EmployeeMainAdapter.EmployeeWorkViewHolder>() {
    class EmployeeWorkViewHolder(val binding: EmployeeMainItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)


    private val diffUtil = object : DiffUtil.ItemCallback<Works>() {
        override fun areItemsTheSame(oldItem: Works, newItem: Works): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Works, newItem: Works): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeWorkViewHolder {
        return EmployeeWorkViewHolder(EmployeeMainItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EmployeeWorkViewHolder, position: Int) {
        val works = differ.currentList[position]
        val isExpanded = works.expanded

        holder.binding.apply {
            mainTitleWork.text = works.workTitle
            mainWorkDescContent.text = works.workDescription
            mainWorkDate.text = works.workLastDate

            when(works.workPriority){
                "1" -> mainWorkOvalimage.setImageResource(R.drawable.green_oval)
                "2" -> mainWorkOvalimage.setImageResource(R.drawable.orange_oval)
                "3" -> mainWorkOvalimage.setImageResource(R.drawable.red_oval)

            }
            when(works.workStatus){
                "1" -> holder.binding.itemmainWorkStatus.text = "Pending"
                "2" -> holder.binding.itemmainWorkStatus.text = "Progress"
                "3" -> holder.binding.itemmainWorkStatus.text = "Completed"
            }

            mainWorkDescContent.visibility = if(isExpanded == true) View.VISIBLE else View.GONE
            mainWorkDescTitle.visibility = if(isExpanded == true) View.VISIBLE else View.GONE
            mainStartingbtn.visibility = if(isExpanded == true) View.VISIBLE else View.GONE
            mainCompletedbtn.visibility = if(isExpanded == true) View.VISIBLE else View.GONE
//
            mainConstraintLayout.setOnClickListener {
                isAnyItemExpanded(position)
                works.expanded = !works.expanded
                notifyItemChanged(position,0)
            }



            if(itemmainWorkStatus.text == "Progress" || itemmainWorkStatus.text == "Completed"){
                Log.d("cc" , "progress")
                mainStartingbtn.text = "In Progress"
                mainStartingbtn.run { setTextColor(ContextCompat.getColor(context,R.color.Beige)) }
                mainStartingbtn.run {  setBackgroundColor(ContextCompat.getColor(context, R.color.darkpink)) }
            }

            if(itemmainWorkStatus.text == "Completed"){
                mainCompletedbtn.text = "WorkDone"
                mainCompletedbtn.run { setTextColor(ContextCompat.getColor(context,R.color.Beige)) }
                mainCompletedbtn.run { setBackgroundColor(ContextCompat.getColor(context,R.color.green)) }
            }


               mainStartingbtn.setOnClickListener { onProgressBtnClick.let {it1->
                   it1(works , mainStartingbtn)
               }}
               mainCompletedbtn.setOnClickListener { onCompleteBtnClick.let {it1->
                   it1(works , mainCompletedbtn)
               } }
        }
    }

    private fun isAnyItemExpanded(position: Int) {
        val expandedItemIndex = differ.currentList.indexOfFirst { it.expanded }
        if(expandedItemIndex >= 0 && expandedItemIndex != position){
            differ.currentList[expandedItemIndex].expanded = false
            notifyItemChanged(expandedItemIndex , 0)

        }
    }

    override fun onBindViewHolder(
        holder: EmployeeWorkViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isNotEmpty() && payloads[0] == 0){
            holder.binding.apply {
                mainWorkDescTitle.visibility = View.GONE
                mainWorkDescContent.visibility = View.GONE
                mainCompletedbtn.visibility = View.GONE
                mainStartingbtn.visibility = View.GONE
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }
}