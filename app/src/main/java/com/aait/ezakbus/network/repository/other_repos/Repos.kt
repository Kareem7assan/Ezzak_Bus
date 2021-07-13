package com.aait.ezakbus.network.repository.other_repos

import com.aait.ezakbus.models.*
import com.aait.ezakbus.models.ads_model.AdsModel
import com.aait.ezakbus.models.balance_model.BalanceModel
import com.aait.ezakbus.models.bids_model.BidsModel
import com.aait.ezakbus.models.bill_model.BillModel
import com.aait.ezakbus.models.bus_menu.BusMenuModel
import com.aait.ezakbus.models.cancel_model.CancelModel
import com.aait.ezakbus.models.captin_model.CaptdeinModel
import com.aait.ezakbus.models.car_filter_model.CarFilterModel
import com.aait.ezakbus.models.categories_model.CategoriesModel
import com.aait.ezakbus.models.cities_model.CitiesModel
import com.aait.ezakbus.models.city_liens_model.CityLinesModel
import com.aait.ezakbus.models.countries_model.CountriesModel
import com.aait.ezakbus.models.device_model.DeviceModel
import com.aait.ezakbus.models.expecting_time_model.ExpectedTimeModel
import com.aait.ezakbus.models.later_trip_model.LaterTripModel
import com.aait.ezakbus.models.line_branch_model.LineBranchesModel
import com.aait.ezakbus.models.line_points.LinePointsModel
import com.aait.ezakbus.models.nearest_trip_model.NearestTripModel
import com.aait.ezakbus.models.notification_model.NotificationModel
import com.aait.ezakbus.models.notifications_model.NotificationNum
import com.aait.ezakbus.models.order_details_model.OrderDetailsModel
import com.aait.ezakbus.models.prev_trip_model.PrevTripModel
import com.aait.ezakbus.models.register_model.RegisterModel
import com.aait.ezakbus.models.route_model.MyRouteModel
import com.aait.ezakbus.models.terms_model.TermsModel
import com.aait.ezakbus.models.ticket_model.TicketModel
import com.aait.ezakbus.models.time_distance_model.TimeDistanceModel
import com.aait.ezakbus.models.use_balance_model.UseBlanceModel
import com.aait.ezakbus.models.user_locale_model.UserLocaleModel
import com.aait.ezakbus.models.wholde_places_model.SearchPlacesModel
import com.aait.ezakbus.models.wholde_places_model.WholePlacesModel
import com.aait.ezakbus.models.years_model.YearsModel
import com.aait.ezakbus.utils.Util
import io.reactivex.Observable
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface Repos {

    fun updateUserLocale(
        token:String
    ):Observable<UserLocaleModel>
    fun getCountries():Observable<CountriesModel>
    fun rideDetails(
        orderId: Int,
        token:String
    ):Observable<OrderDetailsModel>

    fun profile(
        name:String,
        email: String,
        phone:String,
        token:String
    ):Observable<RegisterModel>

    fun srcDistTrips(
        from_lat:String,from_lng: String,to_lat:String,to_lng: String,car_type_id:String,token: String
    ):Observable<NearestTripModel>

    fun getNearestOrder(
        lat:String,lng:String,car_type_id:String,token: String
    ):Observable<NearestTripModel>

    fun getCars(
        token: String
    ):Observable<CarFilterModel>
    fun getRountes(src:String,dist:String,key:String):Observable<MyRouteModel>

    fun getCities(country_id: String):Observable<CitiesModel>

    fun getUser(phone:String,
                country_iso:String,email:String,
                password:String,name:String,
                friend_code:String?=null,
                social_id:String?=null,
                auth:String

    ): Observable<RegisterModel>

    fun verification(
        code:String,
        header:String
    ): Observable<RegisterModel>

    fun sigIn(phone:String,
              key: String,
              social_id: String?=null
              ):Observable<RegisterModel>

    fun forgetPass(phone:String
    ):Observable<RegisterModel>

    fun resendCode(
        header:String
    ):Observable<RegisterModel>

    fun resetPass(
        pass:String,
        header:String
    ):Observable<RegisterModel>

    fun getAds():Observable<AdsModel>
    fun blanced(header:String):Observable<BlanceModel>
    fun points(header:String):Observable<BlanceModel>
    fun years():Observable<YearsModel>
    fun getPreviousTrips(/*year: String,*/header:String):Observable<PrevTripModel>
    fun deleteNotif(id:Int,token:String):Observable<ResetModel>
    fun getExpectedTime(
        from_lat:String,
        from_lng:String,
        car_type_id: Int,
        Authorization:String
    ):Observable<ExpectedTimeModel>
    fun getPlaces(
        from_lat:String,
        from_lng:String,
        Authorization:String
    ):Observable<WholePlacesModel>

    fun searchPlaces(
        lat:String,
        lng:String,
        name:String,
        Authorization:String
    ):Observable<SearchPlacesModel>

    fun savePlace(
        lat: String,
        lng: String,
        name: String?=null,
        address: String,
        place_id:String,
        Authorization: String
    ):Observable<ResetModel>
    fun savePlace(
        lat: String,
        lng: String,
        name: String?=null,
        address: String,
        Authorization: String
    ):Observable<ResetModel>

    fun getCarTypes(
        from_lat:String,
        from_lng:String,
        to_lat:String?=null,
        to_lng:String?=null,
        token:String
        ):Observable<CategoriesModel>

    fun getCarType(
        service_type:String,
        from_lat:String,
        rom_lng:String,
        to_lat:String,
        to_lng:String,
        service_in:String,
        token:String,
        orderId:String?=null,
        car_type_id:String?=null
    ):Observable<CategoriesModel>

    fun createTrip(
        expected_price:String?,
                    tripTime:String,
                    price_id: String,
                   start_address:String,
                   start_lat:String,
                   start_long:String,
                   end_address:String?=null,
                   end_lat:String?=null,
                   end_long:String?=null,
                   car_type_id:String,
                   payment_type:String?="cash",
                   type:String?="now",
                   later_order_date:String?=null,
                   later_order_time:String?=null,
                   coupon:String?=null,
                   notes:String?=null,
                   Authorization:String):Observable<OrderDetailsModel>

    /*fun joinTrip(price_id:String,
                   service_type:String,
                   service_in:String,
                   start_address:String,
                   start_lat:String,
                   start_long:String,
                   end_address:String,
                   end_lat:String,
                   end_long:String,
                   car_type_id:String,
                   expected_distance:String,
                   expected_period:String,
                   expected_price:String,
                   payment_type:String,
                   num_order_persons:String,
                   cheaper_way:String?=null,
                   type:String,
                   later_order_date:String?=null,
                   later_order_time:String?=null,
                   identity_type:String?=null,
                   identity_number:String?=null,
                   coupon:String?=null,
                   notes:String?=null,
                   Authorization:String):Observable<ResetModel>
    fun joinTrip(price_id:String,
                   service_type:String,
                   service_in:String,
                   start_address:String,
                   start_lat:String,
                   start_long:String,
                   end_address:String,
                   end_lat:String,
                   end_long:String,
                   car_type_id:String,
                   expected_distance:String,
                   expected_period:String,
                   expected_price:String,
                   payment_type:String,
                   num_order_persons:String,
                   cheaper_way:String?=null,
                   type:String,
                   later_order_date:String?=null,
                   later_order_time:String?=null,
                   @Part shipment_image: MultipartBody.Part,
                   identity_type:String?=null,
                   identity_number:String?=null,
                   coupon:String?=null,
                   notes:String?=null,
                   Authorization:String):Observable<ResetModel>
*/
    fun changePass(
        oldPass:String,
        newPass:String,
        token:String
    ):Observable<ResetModel>
    fun laterOrders(
        token:String
    ):Observable<PrevTripModel>

    fun cancelOrder(
        order_id:String,
         reason_id:Int?=null,
         token:String
    ):Observable<ResetModel>

    fun updateClientOrder(
        order_id:String,
        later_order_date:String,
        later_order_time:String,
        notes:String?="",
        token:String
    ):Observable<ResetModel>

    fun transferCharge(
        country_id:String,
        phone:String,
        transfer_amount:String,
        token:String
    ):Observable<ResetModel>

    fun getBalance(
        token:String
    ):Observable<BalanceModel>

    fun contactUs(
        token:String
    ):Observable<ContactUsModel>

    fun contactMSg(
        msg:String,
        token:String
    ):Observable<ResetModel>

    fun settings(
        token:String
    ):Observable<DeviceModel>

    fun updateNotif(
        isNotify:String,
        token:String
    ):Observable<ResetModel>

    fun invitation(
        token:String
    ):Observable<InvitaionModel>

    fun useBalance(
        use_balance_first:Boolean,
        token: String
    ):Observable<UseBlanceModel>

    fun addPromoCode(
        coupon:String,
        token: String
    ):Observable<ResetModel>

    fun chargeCode(
        card:String,
        token: String
    ):Observable<ResetModel>

    fun replace_charge(
        token: String
    ):Observable<ResetModel>

    fun captin_details(
        order_id: Int,
        token: String
    ):Observable<CaptdeinModel>
    fun showBill(
        order_id: Int,
        token: String
    ):Observable<BillModel>
    fun rateUser(
        user_id:Int,
        rating:Float,
        comment:String?=null,
        block_captain:Boolean?=false,
        token: String
    ):Observable<ResetModel>

    fun bids(
        order_id: Int,
        token: String
    ):Observable<BidsModel>

    fun selectBid(
        bid_id: Int,
        token: String
    ):Observable<ResetModel>

    fun terms():Observable<TermsModel>
    fun offers(token: String):Observable<NotificationModel>
    fun cancel(
        token: String
    ):Observable<CancelModel>

    fun laterOdrderDetails(
        order_id: Int,
        token: String
    ):Observable<LaterTripModel>

    fun notificationsNum(
        token: String
    ):Observable<NotificationNum>

    fun editProfile(
        country_iso: String?=null,
        name:String?=null,
        email: String?=null,
        phone:String?=null,
        token:String
    ):Observable<RegisterModel>

    fun checkUserSign(
        social_id:String,
        name:String?,
        mail:String?
    ):Observable<RegisterModel>

    fun getCurrentOrder(token: String):Observable<OrderDetailsModel>
    fun getCityLines(city_id: String,token: String):Observable<CityLinesModel>
    fun trafficLineBranches(
        traffic_line_id: String,
        date: String,
        token: String
    ):Observable<LineBranchesModel>

    fun getPoints(
        traffic_line_id: String,
        token: String
    ):Observable<LinePointsModel>

    fun addCoupon(
        coupon: String,
        token: String
    ):Observable<ResetModel>

    fun orderBus(
        order_id: String,
        start_traffic_line_point: String,
        end_traffic_line_point: String,
        payment_type: String,
        numseats:String,
        token: String
    ):Observable<ResetModel>
    fun getTicket(
        order_id: String,
        lat: String,
        lng: String,
        token: String
        ):Observable<TicketModel>
    fun busMenu(
        token: String
    ):Observable<BusMenuModel>
    fun calculateTimeDistance(
         order_id: String,
        start_id: String,
        end_id: String,
        num_persons: String?="1",
        token: String
    ):Observable<TimeDistanceModel>
    fun changePhone(
         phone: String,
         country_iso: String,
        token: String
    ):Observable<RegisterModel>

    fun activationPhone(
        code: String,
        phone: String,
        country_iso: String,
         token: String
    ):Observable<RegisterModel>
}
