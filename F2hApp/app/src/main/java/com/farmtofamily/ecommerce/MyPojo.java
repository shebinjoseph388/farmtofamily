package com.farmtofamily.ecommerce;

/**
 * Created by Datta on 8/24/2016.
 */
public class MyPojo {

    /**
     * prod_image : catalog/01_Farm2Family/222.png
     * prod_name : MULTI GRAIN BATTER
     * prod_price : 55.0000
     * quantity : 1
     * status_name : Pending
     */

    private String prod_image;
    private String prod_name;
    private String prod_price;
    private String order_price;
    private String quantity;
    private String status_name;
    private String date;
    private String odid;

    public String getOrder_price() {
        return order_price;
    }

    public void setOrder_price(String order_price) {
        this.order_price = order_price;
    }

    public  MyPojo(String prod_image, String prod_name, String prod_price, String quantity, String status_name,String d,String id){
        this.prod_image = prod_image;
        this.prod_name = prod_name;
        this.prod_price = prod_price;
        this.quantity = quantity;
        this.status_name = status_name;
        this.date = d;
        this.odid =id;
    }
    public MyPojo(){

    }

    /*private List<ResponseOrderEnquiryBean> response_order_enquiry;

    public List<ResponseOrderEnquiryBean> getResponse_order_enquiry() {
        return response_order_enquiry;
    }

    public void setResponse_order_enquiry(List<ResponseOrderEnquiryBean> response_order_enquiry) {
        this.response_order_enquiry = response_order_enquiry;
    }*/

//    public static class ResponseOrderEnquiryBean {


        public String getProd_image() {
            return prod_image;
        }

        public void setProd_image(String prod_image) {
            this.prod_image = prod_image;
        }

        public String getProd_name() {
            return prod_name;
        }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOdid() {
        return odid;
    }

    public void setOdid(String odid) {
        this.odid = odid;
    }

    public void setProd_name(String prod_name) {
            this.prod_name = prod_name;
        }

        public String getProd_price() {
            return prod_price;
        }

        public void setProd_price(String prod_price) {
            this.prod_price = prod_price;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getStatus_name() {
            return status_name;
        }

        public void setStatus_name(String status_name) {
            this.status_name = status_name;
        }
    }
//}
