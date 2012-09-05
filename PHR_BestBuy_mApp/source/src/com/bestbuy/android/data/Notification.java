package com.bestbuy.android.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.bestbuy.android.rewardzone.RZCertificate;


public class Notification {
	
	public static final byte NotificationTypeAlert = 1;
	public static final byte NotificationTypeOffer = 2;
	public static final byte NotificationTypeRZCert = 3;
	public static final byte NotificationTypeDefault = 4;
	public static final byte NotificationRZTypeDefault = 5;
	
	private byte notificationType;
	private Object notification;
	private int hash;
	private int id;
	
	private DateFormat df = new SimpleDateFormat("MM/dd/yy", Locale.US);
	
	public static Notification notificationWithType(byte type, Object object){
		return new Notification(type,object);
		
	}
	
	public static Notification notificationWithType(byte type, Object object,int id){
		return new Notification(type,object,id);
		
	}
	
	private Notification(byte type,Object object){
		this.notification = object;
		setNotificationType(type);
		setHash(object);
	}
	
	private Notification(byte type,Object object,int id){
		this.notification = object;
		this.id = id;
		setNotificationType(type);
		setHash(object);
	}
	
	private void setHash(Object object){
		this.hash = this.hashCode();
	}
	
	public int getHash(){
		return this.hash;
	}
	
	public int getId(){
		return id;
	}
	
	
	private void setNotificationType(byte type){
		this.notificationType = type;
	}
	
	public byte getNotificationType(){
		return notificationType;
	}
	
	public Object getNotificationObject(){
		return notification;
	}
	
	public String getTitle(){
		switch(notificationType){
			case NotificationTypeAlert:
				return ((BestBuyAlert)notification).getTitle();
			case NotificationTypeOffer:
				return ((Offer)notification).getTitle();
			case NotificationTypeRZCert:
				return "Your $"+((RZCertificate)notification).getAmount() +" RewardZone Certificate will expire on: " + df.format(((RZCertificate)notification).getExpiredDate());
		}
		return null;
	}
	
	
	public String getListImageURL(){
		switch(notificationType){
			case NotificationTypeAlert:
				return ((BestBuyAlert)notification).getListImageURL();
			case NotificationTypeOffer:
				return ((Offer)notification).getImageURL();
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notification other = (Notification) obj;
		if (hash != other.hash)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		switch (notificationType) {
		case NotificationTypeAlert:
			return ((BestBuyAlert) notification).getTitle().hashCode();
		case NotificationTypeOffer:
			return ((Offer) notification).getTitle().hashCode();
		case NotificationTypeRZCert:
			return ("Your $" + ((RZCertificate) notification).getAmount()
					+ " RewardZone Certificate will expire on: " + 
					((RZCertificate) notification).getCertificateNumber())
					.hashCode();
			
		}
		return 0;
	}
}
