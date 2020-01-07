package nk.bluefrog.rythusevaoffice.activities.bulkbookingtracker;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rajkumar on 8/1/19.
 */

public class TrackerModel implements Serializable {

    /*{commodity,category_id,subcategory_id,category_name,quantity,delivery_address,lat,lng,
            shop_id,shop_name,shop_owner_name,shop_phnumber,shop_image,shop_address,shop_gps,
            shop_discount,shop_mini_order_quantity,shop_category_quantity,shop_categroy_price,booking_id}*/

    private String commodity;
    private String category_id;
    private String subcategory_id;
    private String category_name;
    private String quantity;
    private String delivery_address;
    private String shop_id;
    private String shop_name;
    private String shop_owner_name;
    private String shop_phnumber;
    private String shop_image;
    private String shop_address;
    private String shop_gps;
    private String shop_discount;
    private String shop_mini_order_quantity;
    private String shop_categroy_price;
    private String shop_category_quantity;
    private String booking_id;
    private String lat;
    private String lng;
    private String bookingDate;
    private List<String> row_data;

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public List<String> getRow_data() {
        return row_data;
    }

    public void setRow_data(List<String> row_data) {
        this.row_data = row_data;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_owner_name() {
        return shop_owner_name;
    }

    public void setShop_owner_name(String shop_owner_name) {
        this.shop_owner_name = shop_owner_name;
    }

    public String getShop_phnumber() {
        return shop_phnumber;
    }

    public void setShop_phnumber(String shop_phnumber) {
        this.shop_phnumber = shop_phnumber;
    }

    public String getShop_image() {
        return shop_image;
    }

    public void setShop_image(String shop_image) {
        this.shop_image = shop_image;
    }

    public String getShop_address() {
        return shop_address;
    }

    public void setShop_address(String shop_address) {
        this.shop_address = shop_address;
    }

    public String getShop_gps() {
        return shop_gps;
    }

    public void setShop_gps(String shop_gps) {
        this.shop_gps = shop_gps;
    }

    public String getShop_discount() {
        return shop_discount;
    }

    public void setShop_discount(String shop_discount) {
        this.shop_discount = shop_discount;
    }

    public String getShop_mini_order_quantity() {
        return shop_mini_order_quantity;
    }

    public void setShop_mini_order_quantity(String shop_mini_order_quantity) {
        this.shop_mini_order_quantity = shop_mini_order_quantity;
    }

    public String getShop_categroy_price() {
        return shop_categroy_price;
    }

    public void setShop_categroy_price(String shop_categroy_price) {
        this.shop_categroy_price = shop_categroy_price;
    }

    public String getShop_category_quantity() {
        return shop_category_quantity;
    }

    public void setShop_category_quantity(String shop_category_quantity) {
        this.shop_category_quantity = shop_category_quantity;
    }

    public String getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(String booking_id) {
        this.booking_id = booking_id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
