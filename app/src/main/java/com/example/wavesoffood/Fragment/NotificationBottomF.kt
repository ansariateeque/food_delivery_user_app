package com.example.wavesoffood.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.Adapter.NotificationAdapter
import com.example.wavesoffood.Models.NotificationModel
import com.example.wavesoffood.R
import com.example.wavesoffood.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificationBottomF.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationBottomF :BottomSheetDialogFragment() {

lateinit var binding:FragmentNotificationBottomBinding
lateinit var arrayList: ArrayList<NotificationModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrayList= ArrayList()
        arrayList.add(NotificationModel(R.drawable.sademoji,"Your order has been Canceled Successfully"))
        arrayList.add(NotificationModel(R.drawable.car,"Order has been taken by the driver"))
        arrayList.add(NotificationModel(R.drawable.illustration,"Congrats Your Order Placed"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentNotificationBottomBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var adapter=NotificationAdapter(arrayList)
        binding.recycleview.adapter=adapter
        binding.recycleview.layoutManager=LinearLayoutManager(context)
        binding.recycleview.setHasFixedSize(true)
        binding.back.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }
}