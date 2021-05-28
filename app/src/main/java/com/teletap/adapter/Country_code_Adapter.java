package com.teletap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.teletap.R;
import com.teletap.model.CountryModel;
import com.teletap.view.CodePicker;

import java.util.List;

public class Country_code_Adapter extends RecyclerView.Adapter<Country_code_Adapter.MyViewHolder> {

    private Context context;
    private List<CountryModel> modelList;
    CodePicker codePicker;



    public Country_code_Adapter(Context context, List<CountryModel> modelList, CodePicker codePicker) {

        this.context = context;
        this.modelList = modelList;
        this.codePicker = codePicker;
    }

    @Override
    public Country_code_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.country_code_layout, parent, false);

        return new Country_code_Adapter.MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(Country_code_Adapter.MyViewHolder holder, final int position) {
        final CountryModel model = modelList.get(position);

        holder.tvCountryNameD.setText(model.getCountryName());
        holder.tvCountryCodeD.setText(model.getCountry_code());

        holder.rltvSelectedD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //codePicker.selectedCountry(modelList.get(position).getCountryName());
                codePicker.selectedCountryCode(modelList.get(position).getCountry_code());
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void filterList(List<CountryModel> customModels) {
        modelList = customModels;
        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCountryNameD, tvCountryCodeD;
        RelativeLayout rltvSelectedD;

        public MyViewHolder(View view) {
            super(view);
            tvCountryNameD = view.findViewById(R.id.tvCountryNameD);
            tvCountryCodeD = view.findViewById(R.id.tvCountryCodeD);
            rltvSelectedD = view.findViewById(R.id.rltvSelectedD);
        }
    }


}