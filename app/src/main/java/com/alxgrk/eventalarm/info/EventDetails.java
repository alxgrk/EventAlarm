/*
 * Created on 25.10.2015
 *
 * author Alex
 */
package com.alxgrk.eventalarm.info;

import android.support.annotation.NonNull;

import com.alxgrk.eventalarm.util.NullChecker;

import java.io.Serializable;
import java.util.Date;

public class EventDetails implements InfoObject, Serializable {

    private static final long serialVersionUID = 1265448984863946108L;

    private EventDate date;

    private String name;

    private String venue;

    private String city;

    private int amount;

    private String price;

    private String swURL;

    public EventDetails(@NonNull String date, @NonNull String name, @NonNull String venue,
            @NonNull String city, int amount, @NonNull String price, @NonNull String swURL)
                    throws NumberFormatException {
        NullChecker.checkNull(date);
        NullChecker.checkNull(name);
        NullChecker.checkNull(venue);
        NullChecker.checkNull(city);
        NullChecker.checkNull(price);
        NullChecker.checkNull(swURL);

        this.date = new EventDate(parseDate(date));
        this.name = name;
        this.venue = venue;
        this.city = city;
        this.amount = amount;
        this.price = price;
        this.swURL = swURL;
    }

    private Date parseDate(String dateString) throws NumberFormatException {
        String dateInMillis = dateString.replaceAll("\\D+", "");
        int length = dateInMillis.length();

        if (length >= 4) {
            dateInMillis = dateInMillis.substring(0, length - 4);
            return new Date(Long.parseLong(dateInMillis));
        } else {
            return new Date();
        }
    }

    @Override
    public String toString() {
        return "EventDetails [date=" + date + ", name=" + name + ", venue=" + venue + ", city="
                + city + ", amount=" + amount + ", price=" + price + ", swURL=" + swURL + "]";
    }

    public EventDate getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getVenue() {
        return venue;
    }

    public String getCity() {
        return city;
    }

    public int getAmount() {
        return amount;
    }

    public String getPrice() {
        return price;
    }

    public String getSwURL() {
        return swURL;
    }

    @Override
    public Class<EventDetails> getRuntimeType() {
        return EventDetails.class;
    }

    @Override
    public int compareTo(@NonNull InfoObject another) {
        NullChecker.checkNull(another);

        if (another.getRuntimeType().equals(getClass())) {
            return getDate().compareTo(((EventDetails) another).getDate());
        } else {
            return 0; // don't care about order
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + amount;
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        result = prime * result + ((swURL == null) ? 0 : swURL.hashCode());
        result = prime * result + ((venue == null) ? 0 : venue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof EventDetails))
            return false;
        EventDetails other = (EventDetails) obj;
        if (amount != other.amount)
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (price == null) {
            if (other.price != null)
                return false;
        } else if (!price.equals(other.price))
            return false;
        if (swURL == null) {
            if (other.swURL != null)
                return false;
        } else if (!swURL.equals(other.swURL))
            return false;
        if (venue == null) {
            if (other.venue != null)
                return false;
        } else if (!venue.equals(other.venue))
            return false;
        return true;
    }
}
