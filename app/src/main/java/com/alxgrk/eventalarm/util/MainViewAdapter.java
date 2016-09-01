/*
 * Created on 22.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alxgrk.eventalarm.R;
import com.alxgrk.eventalarm.info.BandInfo;

import java.util.List;

public class MainViewAdapter extends PagerAdapter {

    private Context context;

    private List<BandInfo> listData;

    public MainViewAdapter(Context context, List<BandInfo> listData) {
        this.context = context;
        this.listData = listData;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        String bandName = this.listData.get(position).getBandName();
        Bitmap bandImage = this.listData.get(position).getBandImage();

        return setUpBandView(collection, bandName, bandImage);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.listData.get(position).getBandName();
    }

    @Override
    public int getCount() {
        return this.listData.size();
    }

    @SuppressLint("InflateParams")
    private View setUpBandView(ViewGroup collection, String bandName, Bitmap bandImage) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.list_group, collection, false);

        ImageView ivBandImage = (ImageView) viewGroup.findViewById(R.id.iv_band_image);
        TextView tvBandName = (TextView) viewGroup.findViewById(R.id.tv_band_name);

        tvBandName.setTypeface(null, Typeface.BOLD);
        tvBandName.setText(bandName);

        ivBandImage.setImageBitmap(bandImage);

        collection.addView(viewGroup);

        return viewGroup;
    }

    @VisibleForTesting
    static class ViewHolder {
        TextView tvBandName;

        ImageView ivBandImage;
    }

    // TODO use this code

    // @VisibleForTesting
    // static class ViewHolderChild {
    // TextView tvLocation;
    //
    // TextView tvDate;
    //
    // TextView tvPrice;
    //
    // TextView tvRemaining;
    // }

    // @Override
    // public View getChildView(int groupPosition, int childPosition, boolean
    // isLastChild,
    // View convertView, ViewGroup parent) {
    // // check if the child view is in NoEventBandGroup
    // if ((groupPosition == listData.size() - 1) && ((InfoObject)
    // getChild(groupPosition,
    // childPosition)).getRuntimeType() == BandInfo.class) {
    // return getBandChildView(groupPosition, childPosition, convertView);
    // } else {
    // return getEventChildView(groupPosition, childPosition, convertView);
    // }
    // }
    //
    // @SuppressLint("InflateParams")
    // private View getEventChildView(int groupPosition, int childPosition, View
    // convertView) {
    // ViewHolderChild holder;
    //
    // final EventDetails event = (EventDetails) getChild(groupPosition,
    // childPosition);
    //
    // final String textLocation = event.getCity() + ", " + event.getVenue();
    // final String textDate = event.getDate().toString();
    // final String textPrice = event.getPrice();
    // final String textRemaining = Integer.toString(event.getAmount());
    //
    // if ((convertView == null) || (convertView.getTag() == null) ||
    // (convertView.getTag()
    // .getClass() != ViewHolderChild.class)) {
    // LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(
    // Context.LAYOUT_INFLATER_SERVICE);
    // convertView = inflater.inflate(R.layout.list_item, null);
    //
    // holder = new ViewHolderChild();
    // holder.tvLocation = (TextView)
    // convertView.findViewById(R.id.tv_location);
    // holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
    // holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
    // holder.tvRemaining = (TextView)
    // convertView.findViewById(R.id.tv_remaining);
    // convertView.setTag(holder);
    // } else {
    // holder = (ViewHolderChild) convertView.getTag();
    // }
    //
    // holder.tvLocation.setText(textLocation);
    //
    // holder.tvDate.setText(textDate);
    //
    // holder.tvPrice.setText(textPrice);
    //
    // holder.tvRemaining.setText(textRemaining);
    //
    // return convertView;
    // }
    //
    // /**
    // * Should only be called by child that belong to the BandsWithoutEvents
    // * group!
    // *
    // * @param groupPosition
    // * the position of the BandsWithoutEvents group
    // * @param childPosition
    // * the position within the BandsWithoutEvents group
    // * @param convertView
    // * from {@link #getChildView(int, int, boolean, View, ViewGroup)}
    // * @return the modified View
    // */
    // private View getBandChildView(int groupPosition, int childPosition, View
    // convertView) {
    // String bandName = ((BandInfo) getChild(groupPosition,
    // childPosition)).getBandName();
    // Bitmap bandImage = ((BandInfo) getChild(groupPosition,
    // childPosition)).getBandImage();
    //
    // return setUpBandView(convertView, bandName, bandImage);
    // }
}
