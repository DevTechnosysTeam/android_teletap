package com.teletap.dialogFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.teletap.R;
import com.teletap.databinding.BottomContactsOptionsLayoutBinding;
import com.teletap.databinding.FilterHomeBottomLayoutBinding;

public class FilterHomeDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomOptionsListener listener;
    private FilterHomeBottomLayoutBinding binding;
    private String receiverId;
    //private int listPosition = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.filter_home_bottom_layout, container, false);
        binding.imgCalender.setOnClickListener(this);
        binding.btnApply.setOnClickListener(this);
        binding.imgExpand.setOnClickListener(this);

        /*if (getArguments()!=null) {
            receiverId = getArguments().getString("receiverId", "");
        }*/

        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnEdit) {
            listener.onEdit(view/*, receiverId*/);
        }else if (view.getId() == R.id.btnDelete){
            listener.onDelete(view);
        }
    }

    public interface  BottomOptionsListener{
        void onEdit(View view/*, String receiverId*/);

        void onDelete(View view/*, String receiverId*/);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BottomOptionsListener) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"must implement bottom listener");
        }
    }

}
