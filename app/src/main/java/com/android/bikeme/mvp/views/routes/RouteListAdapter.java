package com.android.bikeme.mvp.views.routes;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.bikeme.R;
import com.android.bikeme.application.BikeMeApplication;
import com.android.bikeme.databaselocal.models.RouteModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 21/04/2017.
 */
public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.RouteViewHolder> {

    private ArrayList <HashMap<String, Object>> routesToShow;
    private OnRouteClickListener onRouteClickListener;

    public interface OnRouteClickListener
    {
        void onRouteClick(String idRoute);
    }

    public class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView titleRoute;
        public TextView descriptionRoute;
        public TextView ratingRoute;
        public ImageView imageRoute;

        public RouteViewHolder(View view)
        {
            super(view);
            titleRoute = (TextView) view.findViewById(R.id.title_route_card_view);
            descriptionRoute = (TextView)view.findViewById(R.id.description_route_card_view);
            imageRoute = (ImageView) view.findViewById(R.id.route_image_card_view);
            ratingRoute = (TextView)view.findViewById(R.id.rating_route_card_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            onRouteClickListener.onRouteClick(String.valueOf(routesToShow.get(getAdapterPosition()).get("uid")));
        }

    }

    public RouteListAdapter()
    {
        this.routesToShow=new ArrayList<>();
    }

    public void addRoutes(ArrayList <HashMap<String, Object>> routesToShow)
    {
        this.routesToShow.clear();
        if (routesToShow != null)
        {
            this.routesToShow.addAll(routesToShow);
        }
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnRouteClickListener listener)
    {
        this.onRouteClickListener = listener;
    }

    @Override
    public int getItemCount()
    {
        return routesToShow.size();
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bike_me_fragment_routes_list_card, viewGroup, false);
        return new RouteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position)
    {
        holder.titleRoute.setText(String.valueOf(routesToShow.get(position).get(RouteModel.NAME_KEY)));
        holder.descriptionRoute.setText(String.valueOf(routesToShow.get(position).get(RouteModel.DESCRIPTION_KEY)));
        holder.ratingRoute.setText(String.valueOf(routesToShow.get(position).get(RouteModel.AVERAGE_RATINGS_KEY)));
        Bitmap image = BikeMeApplication.getInstance().decodeStringToBitmap(String.valueOf(routesToShow.get(position).get(RouteModel.IMAGE_KEY)));
        holder.imageRoute.setImageBitmap(image);
    }
}