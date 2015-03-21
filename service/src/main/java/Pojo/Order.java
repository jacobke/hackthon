package Pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Created by Administrator on 2015-3-21.
 */
public class Order {
	private int orderid;

	private User user;

	private Date createTime;

	private int expiretime; // in minutes

	private int dealPrice; // 成交价

	// "inprogress", "addprice", "done";
	private String status;

	// viewed list;
	private Map<Integer, Hotel> viewedList = new HashMap<>();

	// bid list:
	private Map<Integer /* hotelid */, List<HotelBidRequest> /* price */> bidMap = new HashMap<>();

	private HotelRequest orderRequest;
	
	private HotelBidRequest winningBid;
	
	private HotelBidRequest bestLosingBid;
	
	// unacked within 1min
	private boolean dead = false;
	
	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public HotelBidRequest getBestLosingBid() {
		return bestLosingBid;
	}

	public void setBestLosingBid(HotelBidRequest bestLosingBid) {
		this.bestLosingBid = bestLosingBid;
	}

	public HotelBidRequest getWinningBid() {
		return winningBid;
	}

	public void setWinningBid(HotelBidRequest winningBid) {
		this.winningBid = winningBid;
	}

	public HotelRequest getHotelRequest() {
		return orderRequest;
	}

	public void setHotelRequest(HotelRequest orderRequest) {
		this.orderRequest = orderRequest;
	}

	public int getOrderid() {
		return orderid;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public int getExpiretime() {
		return expiretime;
	}

	public void setExpiretime(int expiretime) {
		this.expiretime = expiretime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Map<Integer, Hotel> getViewedList() {
		return viewedList;
	}

	public void setViewedList(Map<Integer, Hotel> viewedList) {
		this.viewedList = viewedList;
	}

	public void addHotelBidRequest(HotelBidRequest request) {
		if (!bidMap.containsKey(request.getHotelId())) {
			bidMap.put(request.getHotelId(), new ArrayList<HotelBidRequest>());
		}
		List<HotelBidRequest> list = bidMap.get(request.getHotelId());
		list.add(request);
	}

	public Map<Integer, List<HotelBidRequest>> getBidMap() {
		return bidMap;
	}

	public void setBidMap(Map<Integer, List<HotelBidRequest>> bidMap) {
		this.bidMap = bidMap;
	}

	public int getDealPrice() {
		return dealPrice;
	}

	public void setDealPrice(int dealPrice) {
		this.dealPrice = dealPrice;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public boolean isTimeout() {
		return System.currentTimeMillis() > createTime.getTime() + expiretime * 60L * 1000;
	}

	public boolean isDone() {
		return winningBid != null || bestLosingBid != null;
	}

	public boolean shouldDead() {
		// dead 1min after timeout
	   return System.currentTimeMillis() > createTime.getTime() + expiretime * 60L * 1000 + 60 *1000;
   }

}
