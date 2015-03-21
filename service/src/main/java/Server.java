import Pojo.Order;

import com.alibaba.fastjson.JSON;

import service.HotelService;
import service.BidService;
import service.PriceService;
import service.UserService;

import java.util.Date;

import service.BidService;
import service.HotelService;
import service.PriceService;
import service.UserService;
import Pojo.Hotel;
import Pojo.HotelBidRequest;
import Pojo.Order;
import Pojo.User;
import Pojo.UserBidRequest;

public class Server {
	public static void main(String[] args) {
		spark.Spark.staticFileLocation("/web");

		BidService orderService = BidService.getInstance();
		UserService userService = UserService.getInstance();
		HotelService hotelService = HotelService.getInstance();

		PriceService service = PriceService.getInstance();

		/**
		 * Input: UserID, Price, Star, Place, Type, ExpireTime Output: OrderID or null(means failed)
		 */
		spark.Spark.get("/order/userbid", (req, res) -> {
			int userId = Integer.valueOf(req.queryParams("userid"));
			int price = Integer.valueOf(req.queryParams("price"));
			int star = Integer.valueOf(req.queryParams("star"));
			String place = req.queryParams("place");
			String type = req.queryParams("type");
			int timeout = Integer.valueOf(req.queryParams("timeout"));

			Order order = new Order();
			order.setUser(userService.getUserById(userId));
			UserBidRequest request = new UserBidRequest();
			request.setPrice(price);
			request.setStar(star);
			request.setType(type);
			request.setLocation(place);
			order.setExpiretime(timeout);
			order.setHotelRequest(request);
			order.setCreateTime(new Date());

			// put into userToOrderHistory
			   userService.addUserOrder(userId, order);

			   return orderService.userBid(order);
		   });

		spark.Spark.get("/order/hotelbid", (req, res) -> {
			int hotelId = Integer.valueOf(req.queryParams("hotelid"));
			int orderId = Integer.valueOf(req.queryParams("orderid"));
			int extra = Integer.valueOf(req.queryParams("extra"));
			String comment = req.queryParams("comment");

			HotelBidRequest request = new HotelBidRequest();
			request.setComment(comment);
			request.setExtraPrice(extra);
			request.setCreateDate(new Date());
			request.setHotelId(hotelId);
			Order order = orderService.getOrder(orderId);
			return orderService.hotelBid(request, order);
		});

		// show bid history and 概率
		/**
		 * Input: UserID, Price, Star, Place, Type, ExpireTime Output: Map<Price, Probability>
		 */
		spark.Spark.get("/order/probability", (req, res) -> {
			UserBidRequest request = new UserBidRequest();
			request.setType(req.queryParams("type"));
			request.setStar(Integer.valueOf(req.queryParams("star")));
			request.setLocation(req.queryParams("place"));
			// request.setPrice(Integer.valueOf(req.queryParams("price")));

			   int timeoutMin = Integer.valueOf(req.queryParams("timeout"));

	        double normalPrice = service.getNormalPrice(request);
	        return JSON.toJSON(service.getPricePoint(normalPrice, timeoutMin , new Date()));
		   });

		/**
		 * Input: UserID Output: List<Order>
		 */
		spark.Spark.get("/user/orders", (req, res) -> {
			int userId = Integer.valueOf(req.queryParams("userid"));
			return userService.getUserOrders(userId);
		});

		/**
		 * Input: Orderid Output: Order ： 主要用于竞拍页面，有哪些酒店浏览过，竞拍过。
		 */
		spark.Spark.get("/order/:orderid", (req, res) -> {
			int orderId = Integer.valueOf(req.params(":orderid"));
			return orderService.getOrder(orderId);
		});

		/**
		 * Input: hotelid Output: List<Order>
		 */
		// hotel to check all his bids.
		spark.Spark.get("/hotel/orders", (req, res) -> {
			int hotelId = Integer.valueOf(req.queryParams("hotelid"));

			return orderService.getMatchedOrders(hotelId);
		});

	    spark.Spark.get("/hotel/get", (req, res) -> {
		    int hotelId = Integer.valueOf(req.queryParams("hotelid"));
		    int orderId = Integer.valueOf(req.queryParams("orderid"));
				//todo
		    return null;
	    });

	/*	*//**
		 * Input: orderid Output: Order
		 *//*
        spark.Spark.get("/hotel/add", (req, res) -> {
			int orderId = Integer.valueOf(req.params("orderid"));
			return orderService.getDoneOrder(orderId);
		});*/
		spark.Spark.get("/order/done/:orderid", (req, res) -> {
			int orderId = Integer.valueOf(req.params(":orderid"));
			int hotelBidId = Integer.valueOf(req.params(":hotelBidId"));
			return orderService.confirmOrderBid(orderId, hotelBidId);
		});

		spark.Spark.get("/user/:userid", (req, res) -> {
			int userId = Integer.valueOf(req.params(":userid"));
			User user = userService.getUserById(userId);
			return user;
		});

		spark.Spark.get("/hotel/:hotelid", (req, res) -> {
			int hotelId = Integer.valueOf(req.params(":hotelid"));
			Hotel hotel = hotelService.getHotelById(hotelId);
			return hotel;
		});
	}
}
