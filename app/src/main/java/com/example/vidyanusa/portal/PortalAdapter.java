package com.example.vidyanusa.portal;
/**
 * Created by wanda on 02/11/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vidyanusa.R;

import java.util.List;

/**
 * Created by putuguna on 21/09/17.
 */

public class PortalAdapter extends RecyclerView.Adapter<PortalAdapter.Holder>{

    private List<Portal> mListData;
    private Context mContext;

    public PortalAdapter(List<Portal> mListData, Context mContext) {
        this.mListData = mListData;
        this.mContext = mContext;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.adapter_item_data, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Portal model = mListData.get(position);

        //set data makanan
        holder.tvNamaMakanan.setText(model.getNama_makanan());
        holder.tvHarga.setText(model.getHarga());
        holder.tvStok.setText(model.getStok_barang());
        holder.tvSisa.setText(model.getSisa_barang());
        holder.tvOwner.setText(model.getOwner());
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {

        public TextView tvNamaMakanan;
        public TextView tvHarga;
        public TextView tvStok;
        public TextView tvSisa;
        public TextView tvOwner;

        public Holder(View itemView) {
            super(itemView);

            tvNamaMakanan = (TextView) itemView.findViewById(R.id.txtNamaMakanan);
            tvHarga = (TextView) itemView.findViewById(R.id.txtHarga);
            tvSisa = (TextView) itemView.findViewById(R.id.txtSisaBarang);
            tvStok = (TextView) itemView.findViewById(R.id.txtStokBarang);
            tvOwner = (TextView) itemView.findViewById(R.id.txtOwner);

        }
    }
}
