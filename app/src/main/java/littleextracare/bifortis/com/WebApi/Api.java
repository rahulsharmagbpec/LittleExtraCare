package littleextracare.bifortis.com.WebApi;


public class Api
{
    private static final String baseUrl = "http://119.9.76.86/lec/public";
    public static final String getTokenUrl = baseUrl +"/basic/token";
    public static final String getPhoneOtp = baseUrl +"/phone/getphoneotp";
    public static final String registerUser = baseUrl +"/user/register";
    public static final String verifyOtp = baseUrl +"/phone/verifyotp";
    public static final String location = baseUrl + "/user/location";

    public static final String liveLocation = baseUrl + "/caregiver/locationlive";
    public static final String deleteUser = baseUrl + "/user/destroy";
    public static final String bookRequest = baseUrl + "/caregiver/book";

    public static final String nearestCareGiver = baseUrl + "/caregiver/nearest";
    public static final String storeCareGiverDesc = baseUrl + "/caregiver/store";
    public static final String careGiverPromoCode = baseUrl + "/caregiver/promocode";
    public static final String acceptBookingRequest = baseUrl + "/managework/bookingreply";
    public static final String bookingStatus = baseUrl + "/caregiver/bookingstatus";
    public static final String userBookingStatus = baseUrl + "/user/bookingstatus";

}
