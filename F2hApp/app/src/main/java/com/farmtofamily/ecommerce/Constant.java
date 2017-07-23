package com.farmtofamily.ecommerce;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constant {
	
	// API URL configuration
	/*static String AdminPageURL = "http://192.168.0.120/f2h/image/";
	static String CategoryAPI = "http://192.168.0.120/f2h/ecommerce/api/get-all-category-data.php";
	static String MenuAPI = "http://192.168.0.120/f2h/ecommerce/api/get-menu-data-by-category-id.php";
	static String TaxCurrencyAPI = "http://192.168.0.120/f2h/ecommerce/api/get-tax-and-currency.php";
	static String MenuDetailAPI = "http://192.168.0.120/f2h/ecommerce/api/get-menu-detail.php";
	static String SendDataAPI = "http://192.168.0.120/f2h/ecommerce/api/add-reservation.php";
	static String RegAPI = "http://192.168.0.120/f2h/ecommerce/api/registration.php";
	static String LoginAPI = "http://192.168.0.120/f2h/ecommerce/api/json_get_data.php";
	static String ResetAPI = "http://192.168.0.120/f2h/ecommerce/api/reset.php";
	static String TrackAPI = "http://192.168.0.120/f2h/ecommerce/api/test_order_status.php";
	static String TimeAPI = "http://www.farmztofamiliez.com/api/get-date_time.php";
	static String UpdateAPI = "http://www.farmztofamiliez.com/api/updateProfile.php";*/
	static String AdminPageURL = "http://farmztofamiliez.com/image/";
	static String CategoryAPI = "http://farmztofamiliez.com/api/get-all-category-data.php";
	static String MenuAPI = "http://farmztofamiliez.com/api/get-menu-data-by-category-id.php";
	static String TaxCurrencyAPI = "http://farmztofamiliez.com/api/get-tax-and-currency.php";
	static String MenuDetailAPI = "http://farmztofamiliez.com/api/get-menu-detail.php";
	static String SendDataAPI = "http://farmztofamiliez.com/api/add-reservation.php";
	static String SendItemDataAPI = "http://farmztofamiliez.com/api/add-reservation-item.php";

	static String RegAPI = "http://farmztofamiliez.com/api/registration.php";
	static String LoginAPI = "http://farmztofamiliez.com/api/json_get_data.php";
	static String ResetAPI = "http://farmztofamiliez.com/api/reset.php";
	static String TrackAPI = "http://farmztofamiliez.com/api/orderHistory.php";
	/*static String AdminPageURL = "http://192.168.0.120/f2h/api/image/";
	static String CategoryAPI = "http://192.168.0.120/f2h/api/get-all-category-data.php";
	static String MenuAPI = "http://192.168.0.120/f2h/api/get-menu-data-by-category-id.php";
	static String TaxCurrencyAPI = "http://192.168.0.120/f2h/api/get-tax-and-currency.php";
	static String MenuDetailAPI = "http://192.168.0.120/f2h/api/get-menu-detail.php";
	static String SendDataAPI = "http://192.168.0.120/f2h/api/add-reservation.php";
	static String RegAPI = "http://192.168.0.120/f2h/api/registration.php";
	static String LoginAPI = "http://192.168.0.120/f2h/api/json_get_data.php";
	static String ResetAPI = "http://192.168.0.120/f2h/api/reset.php";
	static String TrackAPI = "http://192.168.0.120/f2h/api/test_order_status.php";*/
	static String TimeAPI = "http://farmztofamiliez.com/api/get-date_time.php";
	static String CouponAPI = "http://farmztofamiliez.com/api/coupon.php";
	static String WalletAPI = "http://farmztofamiliez.com/api/wallet.php";
	static String UpdateAPI = "http://farmztofamiliez.com/api/updateProfile.php";

	// change this access similar with accesskey in admin panel for security reason
	static String AccessKey = "12345";
	
	// database path configuration
	static String DBPath = "/data/data/com.farmtofamily.ecommerce/databases/";
	
	// method to check internet connection
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// method to handle images from server
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

}
