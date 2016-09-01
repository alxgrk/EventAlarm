/*
 * Created on 22.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.info.BandInfo;

import java.util.List;

public class SideListAdapter extends BaseAdapter {

    private Context context;

    private List<BandInfo> listData;

    public SideListAdapter(Context context, List<BandInfo> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return this.listData.size();
    }

    @Override
    public Object getItem(int pos) {
        return this.listData.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public boolean hasStableIds() {
        // TODO rethink
        return false;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        String bandName = ((BandInfo) getItem(pos)).getBandName();
        Bitmap bandImage = ((BandInfo) getItem(pos)).getBandImage();

        return setUpBandView(convertView, /* bandName, */ bandImage);
    }

    @SuppressLint("InflateParams")
    private View setUpBandView(View convertView, /* final String bandName, */ Bitmap bandImage) {
        ViewHolder holder;

        if ((convertView == null) || (convertView.getTag().getClass() != ViewHolder.class)) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_no_event, null);

            holder = new ViewHolder();
            holder.ivBandImage = (ImageView) convertView.findViewById(R.id.iv_band_image);
            // holder.tvBandName = (TextView)
            // convertView.findViewById(R.id.tv_band_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // holder.tvBandName.setTypeface(null, Typeface.BOLD);
        // holder.tvBandName.setText(bandName);

        holder.ivBandImage.setImageBitmap(bandImage);

        return convertView;
    }

    @VisibleForTesting
    static class ViewHolder {
        // TextView tvBandName;

        ImageView ivBandImage;
    }
}
