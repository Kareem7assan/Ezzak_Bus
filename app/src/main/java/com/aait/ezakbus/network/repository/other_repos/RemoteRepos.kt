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
import com.aait.ezakbus.network.remote_db.RetroWeb
import com.aait.ezakbus.utils.Util
import io.reactivex.Observable

class RemoteRepos : Repos {
    override fun updateUserLocale(token: String): Observable<UserLocaleModel> {
        return RetroWeb.serviceApi.updateUserlocale(token)
    }

    override fun terms(/*token: String*/): Observable<TermsModel> {
        return RetroWeb.serviceApi.terms(/*token*/)
    }

    override fun offers(token: String): Observable<NotificationModel> {
        return RetroWeb.serviceApi.offers(token)
    }

    override fun cancel(token: String): Observable<CancelModel> {
        return RetroWeb.serviceApi.reasons(token)
    }

    override fun laterOdrderDetails(order_id: Int, token: String): Observable<LaterTripModel> {
        return RetroWeb.serviceApi.laterOrderDetails(order_id.toString(),token)
    }

    override fun notificationsNum(token: String): Observable<NotificationNum> {
        return RetroWeb.serviceApi.notification_numbers(token)
    }

    override fun editProfile(country_iso: String?,name: String?, mail:String?, phone: String?, token: String): Observable<RegisterModel> {
        return   RetroWeb.serviceApi.editProfile(name,mail,country_iso,phone,token)
    }

    override fun checkUserSign(social_id: String,name:String?,mail:String?): Observable<RegisterModel> {
        return RetroWeb.serviceApi.checkUserSignIn(social_id,name,mail)
    }

    override fun getCurrentOrder(token: String): Observable<OrderDetailsModel> {
        return RetroWeb.serviceApi.getCurrentOrder(token)
    }

    override fun getCityLines(city_id: String,token: String): Observable<CityLinesModel> {
        return RetroWeb.serviceApi.cityLines(city_id,token)
    }

    override fun trafficLineBranches(
        traffic_line_id: String,
        date: String,
        token: String
    ): Observable<LineBranchesModel> {
        return RetroWeb.serviceApi.trafficLineBranches(traffic_line_id, date, token)
    }

    override fun getPoints(traffic_line_id: String, token: String): Observable<LinePointsModel> {
        return RetroWeb.serviceApi.linePoints(traffic_line_id, token)
    }

    override fun addCoupon(coupon: String, token: String): Observable<ResetModel> {
        return RetroWeb.serviceApi.addCoupon(coupon, token)
    }

    override fun orderBus(
        order_id: String,
        start_traffic_line_point: String,
        end_traffic_line_point: String,
        payment_type: String,
        num_seats: String,
        token: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.orderBus(order_id, start_traffic_line_point, end_traffic_line_point, payment_type,num_seats, token)
    }

    override fun getTicket(
        order_id: String,
        lat: String,
        lng: String,
        token: String
    ): Observable<TicketModel> {
        return RetroWeb.serviceApi.ticket(order_id, lat, lng, token)
    }

    override fun busMenu(token: String): Observable<BusMenuModel> {
        return RetroWeb.serviceApi.busMenu(token)
    }

    override fun calculateTimeDistance(
        order_id: String,
        start_id: String,
        end_id: String,
        num_persons: String?,
        token: String
    ): Observable<TimeDistanceModel> {
        return RetroWeb.serviceApi.calculateTimeDistance(order_id,start_id,end_id,num_persons!!,token)
    }

    override fun changePhone(phone: String, country_iso: String, token: String): Observable<RegisterModel> {
        return RetroWeb.serviceApi.changePhone(phone, country_iso, token)
    }

    override fun activationPhone(code: String, phone: String, country_iso: String, token: String): Observable<RegisterModel> {
        return RetroWeb.serviceApi.activationPhone(code, phone, country_iso, token)
    }
/*

    override fun changePhone(
        phone: String,
        country_iso: String,
        token: String
    ): Observable<RegisterModel> {
        return RetroWeb.serviceApi.changePhone(phone, country_iso, token)
    }

    override fun activationPhone(
        code: String,
        phone: String,
        country_iso: String,
        token: String
    ): Observable<RegisterModel> {
        return RetroWeb.serviceApi.activationPhone(code, phone, country_iso, token)
    }
*/

    override fun selectBid(bid_id: Int, token: String): Observable<ResetModel> {
        return RetroWeb.serviceApi.selectBid(bid_id,token)
    }

    override fun bids(order_id: Int, token: String): Observable<BidsModel> {
        return RetroWeb.serviceApi.bids(order_id,token)
    }

    override fun rateUser(
        user_id: Int,
        rating: Float,
        comment: String?,
        block_captain: Boolean?,
        token: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.rateUser(user_id,rating,comment,block_captain,token)
    }

    override fun showBill(order_id: Int, token: String): Observable<BillModel> {
        return RetroWeb.serviceApi.showBill(order_id,token)
    }

    override fun captin_details(order_id: Int, token: String): Observable<CaptdeinModel> {
        return RetroWeb.serviceApi.captinDetails(order_id,token)
    }

    override fun replace_charge(token: String): Observable<ResetModel> {
        return RetroWeb.serviceApi.replacePoints(token)
    }

    override fun rideDetails(orderId: Int, token: String): Observable<OrderDetailsModel> {
        return RetroWeb.serviceApi.orderDetials(orderId,token)
    }

    override fun chargeCode(card: String, token: String): Observable<ResetModel> {
        return RetroWeb.serviceApi.chargeCard(card,token)
    }

    override fun addPromoCode(coupon: String, token: String): Observable<ResetModel> {
        return RetroWeb.serviceApi.addPromoCode(coupon,token)
    }

    override fun useBalance(isChecked:Boolean,token: String): Observable<UseBlanceModel> {
        return RetroWeb.serviceApi.useBalance(isChecked,token)
    }

    override fun invitation(token: String): Observable<InvitaionModel> {
        return RetroWeb.serviceApi.invitationData(token)
    }

    override fun updateNotif(isNotify: String, token: String): Observable<ResetModel> {
        return RetroWeb.serviceApi.updateNotif(orders_notify = isNotify,Authorization = token)
    }

    override fun settings(token: String): Observable<DeviceModel> {
        return RetroWeb.serviceApi.settings(Authorization = token)
    }

    override fun contactMSg(msg: String, token: String): Observable<ResetModel> {
        return RetroWeb.serviceApi.sendMsg(msg,token)
    }

    override fun contactUs(token: String): Observable<ContactUsModel> {
        return RetroWeb.serviceApi.contactUs(token)
    }

    override fun getBalance(token: String): Observable<BalanceModel> {
        return RetroWeb.serviceApi.transferBalance(token)
    }

    override fun transferCharge(
        country_id: String,
        phone: String,
        transfer_amount: String,
        token: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.transferMoney(country_id,phone,transfer_amount,token)
    }

    override fun updateClientOrder(
        order_id: String,
        later_order_date: String,
        later_order_time: String,
        notes: String?,
        token: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.updateLaterRide(order_id,later_order_date,later_order_time,notes,token)
    }

    override fun cancelOrder(
        order_id: String,
        reason_id: Int?,
        token: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.cancelOrder(order_id,reason_id,token)
    }

    override fun laterOrders(token: String): Observable<PrevTripModel> {
        return RetroWeb.serviceApi.laterOrders(token)
    }

    override fun changePass(
        oldPass: String,
        newPass: String,
        token: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.changePassword(oldPass,newPass,token)
    }



    override fun profile(
        name: String,
        email: String,
        phone: String,
        token: String
    ): Observable<RegisterModel> {
        return RetroWeb.serviceApi.editProfile(name,email,phone,token)
    }
    override fun srcDistTrips(
        from_lat: String,
        from_lng: String,
        to_lat: String,
        to_lng: String,
        car_type_id: String,
        token: String
    ): Observable<NearestTripModel> {
        return RetroWeb.serviceApi.srcDistTrips(from_lat,from_lng,to_lat,to_lng,car_type_id,token)
    }

    override fun getNearestOrder(
        lat: String,
        lng: String,
        car_type_id: String,
        token: String
    ): Observable<NearestTripModel> {
        return RetroWeb.serviceApi.nearTrips(lat,lng,car_type_id,token)
    }

    override fun getCars(token: String): Observable<CarFilterModel> {
        return RetroWeb.serviceApi.getCars(token)
    }

    /*override fun createTrip(
        price_id: String,
        service_type: String,
        service_in: String,
        start_address: String,
        start_lat: String,
        start_long: String,
        end_address: String,
        end_lat: String,
        end_long: String,
        car_type_id: String,
        expected_distance: String,
        expected_period: String,
        expected_price: String,
        payment_type: String,
        num_order_persons: String,
        cheaper_way: String?,
        type: String,
        later_order_date: String?,
        later_order_time: String?,
        identity_type: String?,
        identity_number: String?,
        coupon: String?,
        notes: String?,
        Authorization: String
    ): Observable<OrderDetailsModel> {
        return RetroWeb.serviceApi.createTrip(price_id, service_type, service_in, start_address, start_lat, start_long, end_address, end_lat, end_long, car_type_id, expected_distance, expected_period, expected_price, payment_type, num_order_persons, cheaper_way, type, later_order_date, later_order_time, identity_type, identity_number, coupon, notes, Authorization)
    }
*/

    /*override fun joinTrip(
        order_id: String,
        service_type: String,
        service_in: String,
        start_address: String,
        start_lat: String,
        start_long: String,
        end_address: String,
        end_lat: String,
        end_long: String,
        car_type_id: String,
        expected_distance: String,
        expected_period: String,
        expected_price: String,
        payment_type: String,
        num_order_persons: String,
        cheaper_way: String?,
        type: String,
        later_order_date: String?,
        later_order_time: String?,
        shipment_image: MultipartBody.Part,
        identity_type: String?,
        identity_number: String?,
        coupon: String?,
        notes: String?,
        Authorization: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.joinTrip(order_id, start_address, start_lat, start_long, end_address, end_lat, end_long, car_type_id, expected_distance, expected_period, expected_price, payment_type, num_order_persons, cheaper_way, type, later_order_date, later_order_time,shipment_image,identity_type, identity_number, coupon, notes, Authorization)
    }
*/
    /*override fun joinTrip(
        order_id: String,
        service_type: String,
        service_in: String,
        start_address: String,
        start_lat: String,
        start_long: String,
        end_address: String,
        end_lat: String,
        end_long: String,
        car_type_id: String,
        expected_distance: String,
        expected_period: String,
        expected_price: String,
        payment_type: String,
        num_order_persons: String,
        cheaper_way: String?,
        type: String,
        later_order_date: String?,
        later_order_time: String?,
        identity_type: String?,
        identity_number: String?,
        coupon: String?,
        notes: String?,
        Authorization: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.joinTrip(order_id , start_address, start_lat, start_long, end_address, end_lat, end_long, car_type_id, expected_distance, expected_period, expected_price, payment_type, num_order_persons, cheaper_way, type, later_order_date, later_order_time, identity_type, identity_number, coupon, notes, Authorization)
    }*/
    override fun getCarTypes(
        from_lat: String,
        from_lng: String,
        to_lat: String?,
        to_lng: String?,
        token: String
    ) : Observable<CategoriesModel>{
        return RetroWeb.serviceApi.carTypes(
            from_lat,
            from_lng,
            to_lat,
            to_lng,
            token
        )
    }


    override fun getCarType(
        service_type: String,
        from_lat: String,
        from_lng: String,
        to_lat: String,
        to_lng: String,
        service_in: String,
        token: String,
        orderId: String?,
        car_type_id: String?
    ): Observable<CategoriesModel> {
        return RetroWeb.serviceApi.carType(
            service_type,
            from_lat,
            from_lng,
            to_lat,
            to_lng,
            service_in,
            token,
            orderId,
            car_type_id
            )
    }

    override fun createTrip(
        expected_price:String?,
        tripTime:String,
        priceId:String,
        start_address: String,
        start_lat: String,
        start_long: String,
        end_address: String?,
        end_lat: String?,
        end_long: String?,
        car_type_id: String,
        payment_type: String?,
        type: String?,
        later_order_date: String?,
        later_order_time: String?,
        coupon: String?,
        notes: String?,
        Authorization: String
    ): Observable<OrderDetailsModel> {
        return RetroWeb.serviceApi.createTrip(expected_price,tripTime,priceId,start_address, start_lat, start_long, end_address, end_lat, end_long, car_type_id, type, later_order_date, later_order_time, coupon, notes, payment_type, Authorization)
    }

    override fun getRountes(src: String, dist: String,key:String): Observable<MyRouteModel> {
        return RetroWeb.serviceMapApi.getRouts(src,dist,key)
    }

    override fun savePlace(
        lat: String,
        lng: String,
        name: String?,
        address: String,
        place_id:String,
        header: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.savePlace(lat,lng,name,address,place_id,header)
    }
    override fun savePlace(
        lat: String,
        lng: String,
        name: String?,
        address: String,
        header: String
    ): Observable<ResetModel> {
        return RetroWeb.serviceApi.savePlace(lat,lng,name,address,header)
    }

    override fun searchPlaces(lat: String, lng: String,name:String, header: String): Observable<SearchPlacesModel> {
        return RetroWeb.serviceApi.searchStores(lat/*null*/,lng/*null*/,name,header)
    }

    override fun getPlaces(
        lat: String,
        lng: String,
        token: String
    ): Observable<WholePlacesModel> {
        return RetroWeb.serviceApi.nearstores(lat,lng,token)
    }

    override fun getExpectedTime(
        from_lat: String,
        from_lng: String,
        car_type_id:Int,
        header: String
    ): Observable<ExpectedTimeModel> {
        return RetroWeb.serviceApi.getExpectedTime(from_lat,from_lng,car_type_id,header)
    }

    override fun deleteNotif(id: Int, token: String): Observable<ResetModel> {
        return RetroWeb.serviceApi.delete_notif(id,token)
    }



    override fun getPreviousTrips( header: String): Observable<PrevTripModel> {
        return RetroWeb.serviceApi.getPreviousTrips(header)
    }

    override fun years(): Observable<YearsModel> {
        return RetroWeb.serviceApi.years()
    }

    override fun points(header: String): Observable<BlanceModel> {
        return RetroWeb.serviceApi.points(header)
    }

    override fun blanced(header: String): Observable<BlanceModel> {
        return RetroWeb.serviceApi.balance(header)
    }


    override fun resetPass(pass: String, header: String): Observable<RegisterModel> {
        return RetroWeb.serviceApi.resetPassword(pass,header)
    }

    override fun resendCode(header: String): Observable<RegisterModel> {
        return RetroWeb.serviceApi.resendActivation(header)
    }

    override fun forgetPass(phone: String): Observable<RegisterModel> {
        return RetroWeb.serviceApi.forgetPass(phone)
    }

    override fun sigIn(phone: String, country_iso: String,social_id: String?): Observable<RegisterModel> {
        return RetroWeb.serviceApi.signIn(phone,country_iso,social_id=social_id)
    }

    override fun verification(code: String, header: String): Observable<RegisterModel> {
        return RetroWeb.serviceApi.activation(code,header)
    }


    override fun getCities(country_id: String)
            : Observable<CitiesModel> {
        return RetroWeb.serviceApi.getCities(country_id)
    }



    override fun getUser(phone:String,country_iso: String,
                         email:String,
                         password:String,name:String,friend_code:String?,social_id: String?,auth: String
    ): Observable<RegisterModel> {
       return RetroWeb.serviceApi.register(phone,country_iso,
           email,password,name,friend_code,social_id = social_id,Authorization = auth)
    }


    override fun getAds()
            : Observable<AdsModel> {
        return RetroWeb.serviceApi.getAds()
    }


    override fun getCountries()
            : Observable<CountriesModel> {
        return RetroWeb.serviceApi.getCountries(Util.language)
    }

}

